package com.example.appalarm.telas

import AlarmeChannel
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.appalarm.R
import com.example.appalarm.databinding.ActivityMenuPrincipalBinding
import com.example.appalarm.fragments.ListaAtividadesFragment
import com.example.appalarm.fragments.MenuFragment

class MenuPrincipalActivity : AppCompatActivity() {

    private val binding by lazy{
        ActivityMenuPrincipalBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val alarmeChannel = AlarmeChannel(this)
        alarmeChannel.createNotificationChannel()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        supportFragmentManager.
        beginTransaction().
        add(R.id.FragmentMainTela, MenuFragment()).
        commit()
    }

    override fun onStart() {
        super.onStart()
        binding.BottomNV.setOnItemSelectedListener { item->
            when(item.itemId){
                R.id.ItemSair->{
                    AlertDialog.Builder(this)
                        .setTitle("Sair")
                        .setMessage("Deseja realmente sair do aplicativo?")
                        .setPositiveButton("Sim") { _, _ ->
                            finishAffinity()
                        }
                        .setNegativeButton("Não") { _, _ ->
                            println("Ação de saída cancelada pelo usuário.")
                        }
                        .create()
                        .show()
                    true
                }
                R.id.ItemMenu->{
                    supportFragmentManager.
                    beginTransaction().
                    replace(R.id.FragmentMainTela, MenuFragment()).
                    commit()
                    true
                }
                R.id.itemListaAlarmes->{
                    supportFragmentManager.
                    beginTransaction().
                    replace(R.id.FragmentMainTela, ListaAtividadesFragment()).
                    commit()
                    true
                }
                else ->{
                    false
                }
            }

        }
    }
}