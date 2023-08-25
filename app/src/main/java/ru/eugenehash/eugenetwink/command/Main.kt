package ru.eugenehash.eugenetwink.command

class Main private constructor() {

    companion object {

        const val MODE: Byte = 0b00000001
        const val BRIGHT: Byte = 0b00000010
        const val SWITCH: Byte = 0b00000100
        const val SETTING: Byte = 0b00000000
    }
}