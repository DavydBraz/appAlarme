package com.example.appalarm.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.lifecycle.lifecycleScope
import com.example.appalarm.databinding.FragmentCronometroBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class CronometroFragment : Fragment() {

    private val binding by lazy {
        FragmentCronometroBinding.inflate(layoutInflater)
    }

    private var job: Job?=null
    private lateinit var tempoS: String
    private var seg: Int = 0

    private var min: Int= 0
    private var hora: Int= 0

    override fun onStart() {
        super.onStart()
        binding.BtnIniciar.setOnClickListener {
            "00 : 00 : 00".also { binding.TextTime.text = it }
            binding.BtnIniciar.isGone=true
            binding.BtnParar.isGone=false
            job= lifecycleScope.launch(Dispatchers.IO){
                while (true){
                    delay(1000L)
                    seg++
                    if(seg==60){
                        min++
                        seg=0
                    }
                    if (min==60){
                        hora++
                        min=0
                    }
                    tempoS = String.format("%02d : %02d : %02d", hora, min, seg)
                    withContext(Dispatchers.Main){
                        binding.TextTime.text=tempoS
                    }
                }
                hora=0
            }
        }
        binding.BtnParar.setOnClickListener {
            tempoS="00 : 00 : 00"
            seg= 0
            min= 0
            hora= 0
            job?.cancel()
            binding.BtnParar.isGone=true
            binding.BtnIniciar.isGone=false
        }


    }

    override fun onStop() {
        super.onStop()
        job?.cancel()
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        FragmentCronometroBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }
}