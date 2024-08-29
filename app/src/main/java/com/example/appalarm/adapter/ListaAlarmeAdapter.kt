package com.example.appalarm.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.appalarm.databinding.ItemListaBinding
import com.example.appalarm.model.Alarme

class ListaAlarmeAdapter(
    val onClickExcluir: (Int) -> Unit,
    val onClickEditar: (Alarme) -> Unit,
    private val onStatusChange: (Alarme, Boolean) -> Unit
): RecyclerView.Adapter<ListaAlarmeAdapter.ListaAlarmeViewHolder>() {
    private var listaAlarmes: List<Alarme> = emptyList()

    fun adicionarLista(lista: List<Alarme>){
        this.listaAlarmes=lista
        notifyDataSetChanged()
    }

    inner class ListaAlarmeViewHolder(itemBinding: ItemListaBinding):
        RecyclerView.ViewHolder(itemBinding.root) {

        private val binding: ItemListaBinding

        init {
            binding = itemBinding
        }

        fun bind(alarme: Alarme) {
            binding.apply {
                NomeNotificacao.text=alarme.nomeNotificacao
                DiasAlarme.text=alarme.data
                HoraDoAlarme.text=alarme.hora
                StatusAlarme.isChecked=alarme.status
                BtnExcluir.setOnClickListener{
                    alarme.id?.let { it1 -> onClickExcluir(it1.toInt()) }
                }
                BtnEditar.setOnClickListener{
                    onClickEditar(alarme)
                }
                StatusAlarme.setOnClickListener {
                    val novoStatus = !alarme.status
                    onStatusChange(alarme, novoStatus)
                }
            }
        }
    }

    //criando a visualizacao
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListaAlarmeViewHolder {
        val itemListaBinding=ItemListaBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ListaAlarmeViewHolder(itemListaBinding)
    }

    //realizando a vinculacao da view holder com os dados
    override fun onBindViewHolder(holder: ListaAlarmeViewHolder, position: Int) {
        val alarme=listaAlarmes[position]
        holder.bind(alarme)
    }

    //informando a quantidade de itens pro view holder
    override fun getItemCount(): Int {
        return listaAlarmes.size
    }
}
