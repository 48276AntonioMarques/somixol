package com.github.antoniomarques.somixol

typealias Credential = String

class Room(val credential: Credential) {
    companion object {
        fun create(): Room {
            println("Creating room...")
            return Room("super_secret_credential")
        }
    }
}