package ru.eugenehash.eugenetwink.ui.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import ru.eugenehash.eugenetwink.R
import ru.eugenehash.eugenetwink.bluetooth.BluetoothSerial
import ru.eugenehash.eugenetwink.command.Main
import ru.eugenehash.eugenetwink.command.Switch
import ru.eugenehash.eugenetwink.databinding.FragmentSettingBinding
import ru.eugenehash.eugenetwink.setting.SettingModel

class SettingFragment : Fragment() {

    private val binding get() = _binding!!
    private lateinit var settingModel: SettingModel
    private var _binding: FragmentSettingBinding? = null
    private val bluetoothSerial = BluetoothSerial.instance

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, instanceState: Bundle?): View {
        _binding = FragmentSettingBinding.inflate(inflater, container, false)
        settingModel = ViewModelProvider(requireActivity())[SettingModel::class.java]

        binding.power.setOnCheckedChangeListener(this::onCheckedChangeListener)
        binding.power.isChecked = settingModel.power.value!!

        binding.bright.setOnSeekBarChangeListener(OnSeekBarChangeListener())
        binding.bright.progress = settingModel.bright.value!!

        binding.random.setOnCheckedChangeListener(this::onCheckedChangeListener)
        binding.random.isChecked = settingModel.random.value!!

        binding.autoplay.setOnCheckedChangeListener(this::onCheckedChangeListener)
        binding.autoplay.isChecked = settingModel.autoplay.value!!

        return binding.root
    }

    private fun onCheckedChangeListener(button: CompoundButton, isChecked: Boolean) {
        binding.random.isEnabled = binding.autoplay.isChecked && binding.power.isChecked
        binding.autoplay.isEnabled = binding.power.isChecked
        binding.bright.isEnabled = binding.power.isChecked

        when (button.id) {
            R.id.power -> settingModel.power.value = isChecked
            R.id.autoplay -> settingModel.autoplay.value = isChecked
            R.id.random -> settingModel.random.value = isChecked
        }

        bluetoothSerial.write(
            Main.SWITCH, when (button.id) {
                R.id.power -> Switch.POWER
                R.id.autoplay -> Switch.AUTOPLAY
                R.id.random -> Switch.RANDOM
                else -> Switch.NONE
            }, if (isChecked) 1 else 0
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private inner class OnSeekBarChangeListener: SeekBar.OnSeekBarChangeListener {

        override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
            bluetoothSerial.write(Main.BRIGHT, progress.toByte())
            settingModel.bright.value = progress
        }

        override fun onStartTrackingTouch(seekBar: SeekBar?) = Unit

        override fun onStopTrackingTouch(seekBar: SeekBar?) = Unit
    }
}