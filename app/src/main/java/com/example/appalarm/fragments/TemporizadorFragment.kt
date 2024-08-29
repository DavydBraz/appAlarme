package com.example.appalarm.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isGone
import androidx.lifecycle.lifecycleScope
import com.example.appalarm.classes.SomAlarme
import com.example.appalarm.databinding.FragmentTemporizadorBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
//resolvi fazer mais manual, pra me divertir pensando na logica mesmo
//embora com uso de biblioteca ficaria bem mais fácil de fazer :).
class TemporizadorFragment : Fragment() {
    private var job: Job?=null
    private var seg: Int = 0
    private var min: Int = 0
    private var hora: Int = 0
    private var somTocar:SomAlarme? = null

    private val binding by lazy{
        FragmentTemporizadorBinding.inflate(layoutInflater)
    }

    private fun verificarNumeros(seg: Int, min: Int, hora: Int): Triple<Int, Int, Int> {
        val segCorrigido = verificarRangeNumero(seg)
        val minCorrigido = verificarRangeNumero(min)
        val horaCorrigido = verificarRangeNumero(hora)

        return Triple(segCorrigido, minCorrigido, horaCorrigido)
    }

    private fun tempoEditavel(editavel: Boolean){
        binding.apply {
            EditSeg.isEnabled=editavel
            EditMin.isEnabled=editavel
            EditHora.isEnabled=editavel
        }
    }

    private fun verificarRangeNumero(valor: Int): Int{
        var valorCorrigido=valor
        if (valor<0 || valor>60){
            valorCorrigido=0
        }
        return valorCorrigido
    }

    private fun setarTodosCampos(segText: String,minText: String , horaText: String){
        binding.apply {
            EditSeg.setText(segText)
            EditMin.setText(minText)
            EditHora.setText(horaText)
        }
    }

    private fun inicializarTemporizador(){
        seg=binding.EditSeg.text.toString().toInt()
        min=binding.EditMin.text.toString().toInt()
        hora=binding.EditHora.text.toString().toInt()

        val (segCorrigido, minCorrigido, horaCorrigido) = verificarNumeros(seg, min, hora)
        seg = segCorrigido
        min = minCorrigido
        hora = horaCorrigido

        job=lifecycleScope.launch(Dispatchers.IO){
            while (true) {
                withContext(Dispatchers.Main) {
                    setarTodosCampos(String.format("%02d", seg),String.format("%02d", min), String.format("%02d", hora))
              }
                delay(1000L)
                if (seg != 0) {
                    seg--
                    if (hora == 0 && min == 0 && seg == 1) {
                        withContext(Dispatchers.Main) {
                            binding.EditSeg.setText("01")
                            delay(1000)
                            seg = 0
                            binding.EditSeg.setText("00")
                        }
                    }
                }
                if (seg == 0 && min != 0) {
                    min--
                    seg = 59
                }
                if (hora != 0 && min == 0 && seg == 0) {
                    hora--
                    min = 59
                    seg = 59
                    if (hora == 1 && min == 0 && seg == 0) {
                        min = 59
                        seg = 59
                        hora--
                        withContext(Dispatchers.Main) {
                            binding.apply {
                                EditHora.setText(String.format("%02d", hora))
                                EditMin.setText(String.format("%02d", min))
                                EditSeg.setText(String.format("%02d", seg))
                            }
                        }
                    }

                }
                if (hora==0 && min==0 && seg==0){
                    somTocar?.startAlarm()
                    withContext(Dispatchers.Main){
                        Toast.makeText(
                        requireContext(), "Acabou o tempo", Toast.LENGTH_SHORT
                    ).show()
                        binding.apply {
                            EditHora.setText(String.format("%02d", hora))
                            EditMin.setText(String.format("%02d", min))
                            EditSeg.setText(String.format("%02d", seg))
                            BtnParar.isGone=true
                            BtnIniciarTemp.isGone=false
                        }
                        tempoEditavel(true)
                        binding.CardTempPararMusic.isGone=false
                    }
                    job?.cancel()
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        somTocar = SomAlarme(requireContext())
        binding.BtnIniciarTemp.setOnClickListener {
            try{
                if(verificarRangeNumero(binding.EditSeg.text.toString().toInt())+verificarRangeNumero(binding.EditMin.text.toString().toInt())+verificarRangeNumero(binding.EditHora.text.toString().toInt())!=0){
                    tempoEditavel(false)
                    inicializarTemporizador()
                    binding.apply {
                        BtnIniciarTemp.isGone = true
                        BtnParar.isGone = false
                    }
                }else{
                    setarTodosCampos("00", "00",  "00")
                    Toast.makeText(
                        requireContext(), "Informe um Tempo valido, diferente de zero", Toast.LENGTH_SHORT
                    ).show()
                    job?.cancel()
                }
            }catch (e: Exception){
                setarTodosCampos("00", "00",  "00")
                Toast.makeText(
                    requireContext(), "Informe um Tempo valido, apenas numeros", Toast.LENGTH_SHORT
                ).show()
            }

        }

        binding.StopMusic.setOnClickListener {
            somTocar?.stopAlarm()
            binding.CardTempPararMusic.isGone=true
        }

        binding.BtnParar.setOnClickListener {
            job?.cancel()
            binding.apply {
                BtnParar.isGone=true
                BtnResetar.isGone=false
                BtnRetomar.isGone=false
            }

        }
        binding.BtnRetomar.setOnClickListener {
            job?.cancel()
            try{
                if(verificarRangeNumero(binding.EditSeg.text.toString().toInt())+verificarRangeNumero(binding.EditMin.text.toString().toInt())+verificarRangeNumero(binding.EditHora.text.toString().toInt())!=0){
                    inicializarTemporizador()
                    binding.apply {
                        BtnIniciarTemp.isGone = true
                        BtnParar.isGone = false
                    }

                }else{
                    setarTodosCampos("00", "00",  "00")
                    Toast.makeText(
                        requireContext(), "Informe um Tempo valido, diferente de zero", Toast.LENGTH_SHORT
                    ).show()
                    job?.cancel()
                }
                if(verificarRangeNumero(binding.EditSeg.text.toString().toInt())+verificarRangeNumero(binding.EditMin.text.toString().toInt())+verificarRangeNumero(binding.EditHora.text.toString().toInt())!=0){
                    binding.apply {
                        BtnParar.isGone=false
                        BtnResetar.isGone=true
                        BtnRetomar.isGone=true
                    }
                }

            }catch (e: Exception){
                setarTodosCampos("00", "00",  "00")
                job?.cancel()
            }
        }

        binding.BtnResetar.setOnClickListener {
            tempoEditavel(true)
            job?.cancel()
            setarTodosCampos("00", "00",  "00")
            binding.apply {
                BtnIniciarTemp.isGone=false
                BtnParar.isGone=true
                BtnResetar.isGone=true
                BtnRetomar.isGone=true
            }
        }
    }

    override fun onStop() {
        somTocar?.stopAlarm()
        super.onStop()
        tempoEditavel(true)
        job?.cancel()
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }
}