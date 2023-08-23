package ru.eugenehash.eugenetwink.bluetooth

import android.bluetooth.BluetoothDevice
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IntDef
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.eugenehash.eugenetwink.databinding.RecyclerItemBinding

class RecyclerAdapter(private val listener: (device: Device) -> Unit) :
    ListAdapter<RecyclerAdapter.Device, RecyclerAdapter.Holder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        Holder(RecyclerItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.itemView.setOnClickListener { listener(currentList[position]) }
        holder.state.visibility = currentList[position].visibility
        holder.address.text = currentList[position].address
        holder.name.text = currentList[position].name
    }

    private class DiffCallback : DiffUtil.ItemCallback<Device>() {

        override fun areContentsTheSame(oldItem: Device, newItem: Device) =
            oldItem == newItem && oldItem.bondState == newItem.bondState

        override fun areItemsTheSame(oldItem: Device, newItem: Device) = oldItem.address == newItem.address
    }

    class Holder(binding: RecyclerItemBinding) : RecyclerView.ViewHolder(binding.root) {

        val name = binding.name
        val state = binding.state
        val address = binding.address
    }

    data class Device(val name: String, val address: String, @State val bondState: Int) {

        val visibility get() = if (bondState == BOND_BONDED) View.GONE else View.VISIBLE

        companion object {

            @Retention(AnnotationRetention.SOURCE)
            @IntDef(BOND_NONE, BOND_BONDING, BOND_BONDED)
            annotation class State

            const val BOND_NONE = BluetoothDevice.BOND_NONE
            const val BOND_BONDING = BluetoothDevice.BOND_BONDING
            const val BOND_BONDED = BluetoothDevice.BOND_BONDED
        }

        override fun equals(other: Any?): Boolean {
            return if (other !is Device) false
            else if (this.name != other.name) false
            else this.address == other.address
        }

        override fun hashCode() = name.hashCode() * address.hashCode()
    }
}