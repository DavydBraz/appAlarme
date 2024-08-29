package com.example.appalarm.fragments

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isGone
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.appalarm.model.Alarme
import com.example.appalarm.adapter.ListaAlarmeAdapter
import com.example.appalarm.classes.AlarmeConfigs
import com.example.appalarm.database.AlarmeDAO
import com.example.appalarm.databinding.FragmentListaAtividadesBinding
import com.example.appalarm.telas.NotificacaoSiActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar

class ListaAtividadesFragment : Fragment() {
    private var listaAdapter: ListaAlarmeAdapter? = null
    private val binding by lazy {
        FragmentListaAtividadesBinding.inflate(layoutInflater)
    }
    private var listaAlarmes = emptyList<Alarme>()
    private val alarmeConfigs= AlarmeConfigs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        listaAdapter = ListaAlarmeAdapter(
            {id -> confirmarExclusao(id)},
            {alarme -> editar(alarme)},
            {alarme, novoStatus -> mudarStatusAlarm(alarme, novoStatus) }
        )
        binding.RvAlarmes.adapter = listaAdapter
        binding.RvAlarmes.layoutManager=LinearLayoutManager(requireContext())
    }

    private fun editar(alarme: Alarme) {
        val notifIntent= Intent(requireContext(), NotificacaoSiActivity::class.java)
        notifIntent.putExtra("alarme", alarme)
        startActivity(notifIntent)
    }

    private fun confirmarExclusao(id: Int) {
        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val alertBuilder= AlertDialog.Builder(requireContext())
        alertBuilder.setTitle("Confirmar excluir item")
        alertBuilder.setMessage("Deseja realmente excluir o item selecionado?")
        alertBuilder.setPositiveButton("Sim"){
            _,_ ->
            val alarmeDAO=AlarmeDAO(requireContext())
            if (alarmeDAO.remover(id)){
                atualizarListaAlarmes()
                Toast.makeText(requireContext(),
                    "Sucesso ao remover item",
                    Toast.LENGTH_LONG
                ).show()
            }else{
                Toast.makeText(requireContext(),
                    "Erro ao remover item",
                    Toast.LENGTH_LONG).show()
            }
            alarmeConfigs.cancelarAlarme(id, requireContext(), alarmManager)
        }
        alertBuilder.setNegativeButton("Não"){
            _,_ ->
        }
        alertBuilder.create().show()
    }

    private fun atualizarListaAlarmes(){
        val alarmeDAO = AlarmeDAO(requireContext())
        listaAlarmes=alarmeDAO.listar()
        listaAdapter?.adicionarLista(listaAlarmes)
    }

    override fun onStart() {
        super.onStart()
        atualizarListaAlarmes()
        binding.AdicionarAlarme.setOnClickListener{
            binding.CardSelectionOpcoes.isGone=!binding.CardSelectionOpcoes.isGone
            binding.BtnSelectionNotificacao.setOnClickListener {
                startActivity(Intent(requireContext(), NotificacaoSiActivity::class.java))
            }
            binding.BtnSelectionCancel.setOnClickListener {
                binding.CardSelectionOpcoes.isGone=true
            }
        }
    }

    override fun onStop() {
        super.onStop()
        binding.CardSelectionOpcoes.isGone=true
    }

    @SuppressLint("ScheduleExactAlarm")
    private fun mudarStatusAlarm(alarme: Alarme, novoStatus: Boolean) {
//        Log.i("alarme", "$novoStatus")
        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager

        lifecycleScope.launch(Dispatchers.IO) {
            alarme.status = novoStatus
            val alarmeDAO = AlarmeDAO(requireContext())
            alarmeDAO.atualizar(alarme)
//            Log.i("alarmeDAO", "${alarmeDAO.listar()}")

            withContext(Dispatchers.Main) {
                if (!alarme.status) {
                    alarmeConfigs.cancelarAlarme(alarme.id, requireContext(), alarmManager)
//                    Log.i("Executou", "Alarme cancelado com ID ${alarme.id}")
                } else {
                    val calendario=alarmeConfigs.calendarioAlarme(alarme)

                    val agora = Calendar.getInstance()
                    if (calendario.before(agora)) {
                        alarme.status=false
                        alarmeDAO.atualizar(alarme)
                        atualizarListaAlarmes()
                        Toast.makeText(
                            requireContext(),
                            "Tempo já passou, informe pelo menos 1 minuto depois",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@withContext
                    }
                    alarmeConfigs.usarAlarme(requireContext(), alarme, calendario, alarmManager)
                    atualizarListaAlarmes()
                }
            }
        }
    }
}