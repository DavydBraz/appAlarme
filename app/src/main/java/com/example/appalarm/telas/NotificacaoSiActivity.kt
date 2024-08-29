package com.example.appalarm.telas

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.format.DateFormat
import android.util.Log
import android.widget.TimePicker
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.appalarm.R
import com.example.appalarm.classes.AlarmeConfigs
import com.example.appalarm.database.AlarmeDAO
import com.example.appalarm.databinding.ActivityNotificacaoSiBinding
import com.example.appalarm.model.Alarme
import java.util.Calendar

class NotificacaoSiActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityNotificacaoSiBinding.inflate(layoutInflater)
    }

    private val alarmeConfigs=AlarmeConfigs()

    private val requestPermissionLauncher: ActivityResultLauncher<String> =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (!isGranted) {
                Toast.makeText(this, "Permissão de notificação negada", Toast.LENGTH_SHORT).show()
                finish()
            }else{
                Toast.makeText(this, "Permissão de notificacao concedida", Toast.LENGTH_SHORT)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setupPermission()

        val bundle = intent.extras
        var notificacao: Alarme? = null
        if (bundle != null) {
            notificacao = bundle.getSerializable("alarme") as Alarme
            binding.apply {
                EditTextNomeNotif.setText(notificacao.nomeNotificacao)
                EditTextData.setText(notificacao.data)
                EditTextHora.setText(notificacao.hora)
                TextImagem.setText(notificacao.imagem)
                BtnTodoDia.isChecked = notificacao.todoDia
                BtnVibracao.isChecked = notificacao.vibrar
            }
        }
        inicializarToolbar(notificacao)
    }

    private fun setupPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (!getSystemService(AlarmManager::class.java).canScheduleExactAlarms()) {
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                startActivity(intent)
                requestPermissionLauncher.launch(Manifest.permission.SET_ALARM)
                finish()
            }
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {            // Verifica se a permissão foi negada permanentemente
                if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                    Toast.makeText(this, "Habilite a permissão de notificação nas configurações do aplicativo.", Toast.LENGTH_LONG).show()
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.fromParts("package", packageName, null)
                    }
                    startActivity(intent)
                    finish()
                } else {
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            } else {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        } else {
            Log.i("NotificacaoSiActivity", "Permissões de agendamento exato e de notificações não aplicáveis para esta versão do Android")
        }
    }

    private fun editar(notificacao: Alarme) {
        val notificacaoAtualizada = Alarme(
            notificacao.id,
            binding.EditTextNomeNotif.text.toString(),
            binding.EditTextHora.text.toString(),
            binding.EditTextData.text.toString(),
            notificacao.status,
            binding.TextImagem.text.toString(),
            null,
            binding.BtnVibracao.isChecked,
            binding.BtnTodoDia.isChecked
        )
        val calendario=alarmeConfigs.calendarioAlarme(notificacaoAtualizada)
        val agora = Calendar.getInstance()
        Log.i("Agora", "$agora")
        if (calendario.before(agora)) {
            Toast.makeText(this, "Tempo já passou, informe pelo menos 1 minuto depois", Toast.LENGTH_SHORT).show()
            return
        }else{
            notificacaoAtualizada.status=true
            val alarmeDAO = AlarmeDAO(this)
            if (alarmeDAO.atualizar(notificacaoAtualizada)) {
                if (binding.BtnTodoDia.isChecked){
                    Toast.makeText(this, "Sera notificado todo dia por volta do horário especificado.", Toast.LENGTH_SHORT).show()
                }
                scheduleAlarm(notificacaoAtualizada)
                Log.i("Executou", "${notificacaoAtualizada.id}")
                Toast.makeText(this, "Alarme atualizado com sucesso", Toast.LENGTH_LONG).show()
                finish()
            } else {
                notificacaoAtualizada.status=false
                Toast.makeText(this, "Erro ao atualizar o alarme", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun salvar() {
        val alarme = Alarme(
            null,
            binding.EditTextNomeNotif.text.toString(),
            binding.EditTextHora.text.toString(),
            binding.EditTextData.text.toString(),
            true,
            binding.TextImagem.text.toString(),
            null,
            binding.BtnVibracao.isChecked,
            binding.BtnTodoDia.isChecked
        )
        val calendario=alarmeConfigs.calendarioAlarme(alarme)
        val agora = Calendar.getInstance()
        Log.i("Agora", "$agora")
        if (calendario.before(agora)) {
            Toast.makeText(this, "Tempo já passou, informe pelo menos 1 minuto depois", Toast.LENGTH_SHORT).show()
            return
        } else {
            val alarmeDAO = AlarmeDAO(this)
            val novoId = alarmeDAO.salvar(alarme)
            if (novoId != null) {
                if (binding.BtnTodoDia.isChecked){
                    Toast.makeText(this, "O alarme vai notificar todo dia entorno do horário especificado.", Toast.LENGTH_SHORT).show()
                }
                alarme.id = novoId
                scheduleAlarm(alarme)
                Toast.makeText(this, "Notificação Salva", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Erro ao salvar notificação", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @SuppressLint("ScheduleExactAlarm")
    private fun scheduleAlarm(alarme: Alarme) {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val calendario=alarmeConfigs.calendarioAlarme(alarme)

        val agora = Calendar.getInstance()
        Log.i("Agora", "$agora")
        if (calendario.before(agora)) {
            Toast.makeText(this, "Tempo já passou, informe pelo menos 1 minuto depois", Toast.LENGTH_SHORT).show()
            return
        }
        alarmeConfigs.usarAlarme(this, alarme, calendario, alarmManager)
    }

    private fun inicializarToolbar(notificacao: Alarme?) {
        binding.apply {
            EditTextData.setOnClickListener {
                showDatePickerDialog()
            }
            BtnCancel.setOnClickListener {
                finish()
            }
            EditTextHora.setOnClickListener {
                showTimePickerDialog()
            }
            BtnConfirm.setOnClickListener {
                if (EditTextHora.text.isNotEmpty() &&
                    EditTextData.text.isNotEmpty() &&
                    EditTextNomeNotif.text.isNotEmpty()
                ) {
                    if (notificacao != null) {
                        editar(notificacao)
                    } else {
                        salvar()
                    }
                } else {
                    Toast.makeText(applicationContext, "Preencha os campos corretamente!", Toast.LENGTH_SHORT).show()
                }
            }
            BtnAdicionarImagem.setOnClickListener {
                Toast.makeText(applicationContext, "Ainda não foi configurado :)", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showTimePickerDialog() {
        val tempo = Calendar.getInstance()
        val hora = tempo.get(Calendar.HOUR_OF_DAY)
        val minuto = tempo.get(Calendar.MINUTE)
        val formato24h = DateFormat.is24HourFormat(this)

        TimePickerDialog(
            this,
            { _: TimePicker, horaSelecionada: Int, minutoSelecionado: Int ->
                val horaFormatada = if (formato24h) {
                    String.format("%02d:%02d", horaSelecionada, minutoSelecionado)
                } else {
                    val amPm = if (horaSelecionada < 12) "AM" else "PM"
                    val horaFormatada = if (horaSelecionada % 12 == 0) 12 else horaSelecionada % 12
                    String.format("%02d:%02d %s", horaFormatada, minutoSelecionado, amPm)
                }
                binding.EditTextHora.setText(horaFormatada)
            },
            hora,
            minuto,
            formato24h
        ).show()
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val ano = calendar.get(Calendar.YEAR)
        val mes = calendar.get(Calendar.MONTH)
        val dia = calendar.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(
            this,
            { _, anoSelecionado, mesSelecionado, diaSelecionado ->
                val dataSelecionada = "$diaSelecionado/${mesSelecionado + 1}/$anoSelecionado"
                binding.EditTextData.setText(dataSelecionada)
            },
            ano,
            mes,
            dia
        ).apply {
            datePicker.minDate = calendar.timeInMillis
            show()
        }
    }
}
