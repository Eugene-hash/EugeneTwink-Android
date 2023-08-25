package ru.eugenehash.eugenetwink.ui.modes

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.eugenehash.eugenetwink.databinding.ModeItemBinding

class RecyclerAdapter(
    private val items: Array<String>,
    private val isEnabled: Boolean?,
    private val listener: (position: Int) -> Unit,
) : RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(ModeItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.setOnClickListener { listener(position) }
        viewHolder.itemView.isEnabled = isEnabled ?: false
        viewHolder.title.text = items[position]
    }

    override fun getItemCount() = items.size

    class ViewHolder(binding: ModeItemBinding) : RecyclerView.ViewHolder(binding.root) {

        val title = binding.title
    }
}