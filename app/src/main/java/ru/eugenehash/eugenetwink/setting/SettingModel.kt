package ru.eugenehash.eugenetwink.setting

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

@Suppress("MemberVisibilityCanBePrivate")
class SettingModel : ViewModel() {

    lateinit var bright: MutableLiveData<Int>
    lateinit var power: MutableLiveData<Boolean>
    lateinit var random: MutableLiveData<Boolean>
    lateinit var autoplay: MutableLiveData<Boolean>

    fun setSetting(setting: Setting) {
        power = MutableLiveData(setting.power)
        bright = MutableLiveData(setting.bright)
        random = MutableLiveData(setting.random)
        autoplay = MutableLiveData(setting.autoplay)
    }
}