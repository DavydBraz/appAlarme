package com.example.appalarm.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.appalarm.R
import com.example.appalarm.telas.NotificacaoSiActivity
import com.example.appalarm.databinding.FragmentMenuBinding

class MenuFragment : Fragment(){
    private val binding by lazy{
        FragmentMenuBinding.inflate(layoutInflater)
    }
    private fun menu(){
        binding.BtnNotificacaoS.setOnClickListener{
            startActivity(Intent(requireContext(), NotificacaoSiActivity::class.java))
        }
        binding.BtnCronometro.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.FragmentMainTela, CronometroFragment())
                .commit()
        }
        binding.BtnTemporizador.setOnClickListener {
            parentFragmentManager
                .beginTransaction()
                .replace(R.id.FragmentMainTela, TemporizadorFragment())
                .commit()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        /*
        FragmentMenuBinding.inflate(
            inflater,
            container,
            false
        )
         */
        menu()
        return binding.root
    }

}