package ru.eugenehash.eugenetwink.ui.modes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import ru.eugenehash.eugenetwink.R
import ru.eugenehash.eugenetwink.bluetooth.BluetoothSerial
import ru.eugenehash.eugenetwink.command.Main
import ru.eugenehash.eugenetwink.databinding.FragmentModesBinding
import ru.eugenehash.eugenetwink.setting.SettingModel

class ModesFragment : Fragment() {

    private val modes get() = binding!!.modes
    private lateinit var settingModel: SettingModel
    private var binding: FragmentModesBinding? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, instanceState: Bundle?): View {
        settingModel = ViewModelProvider(requireActivity())[SettingModel::class.java]
        binding = FragmentModesBinding.inflate(inflater, container, false)

        modes.adapter = RecyclerAdapter(
            resources.getStringArray(R.array.modes),
            settingModel.power.value, this::onItemClickListener,
        ); modes.layoutManager = LinearLayoutManager(context)

        return binding!!.root
    }

    private fun onItemClickListener(position: Int) {
        BluetoothSerial.instance.write(Main.MODE, position.toByte())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}