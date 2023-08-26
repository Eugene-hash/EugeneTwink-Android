package ru.eugenehash.eugenetwink.command

class Camera private constructor() {

    companion object {

        const val OPEN: Byte = 0b00010000
        const val CLOSE: Byte = 0b00100000
        const val SCAN_STOP: Byte = 0b00000001
        const val SCAN_START: Byte = 0b01000000
    }
}