package ru.eugenehash.eugenetwink.command

class Switch private constructor() {

    companion object {

        const val NONE: Byte = 0b00000000
        const val POWER: Byte = 0b00000001
        const val RANDOM: Byte = 0b00000010
        const val AUTOPLAY: Byte = 0b00000100
    }
}