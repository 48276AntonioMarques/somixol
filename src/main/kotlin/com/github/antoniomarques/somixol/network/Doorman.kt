package com.github.antoniomarques.somixol.network

import com.github.antoniomarques.somixol.Credential
import kotlin.time.Duration

// For debug purposes doorman -> stranger happens at port
//               and stranger -> doorman happens at port + 1
object Doorman {
    fun watch(invite: Invite, credential: Credential, time: Duration) {
        val port = invite.toPort()
        val checksum = invite.checksum()
        // Broadcasts the checksum
        // Receives a response with the key and auth number
        val keyPacket = Network.broadcastWithResponse(port, port + 1, KEY_SIZE + 2, checksum,time)

        // Validate key
        val key = invite.toKey()
        if (keyPacket == null) {
            println("Room not found.")
            return
        }
        if(keyPacket.data.size != KEY_SIZE + 2) {
            println("Invalid key size.")
            return
        }

        // Test key
        val keyCandidate = keyPacket.data.sliceArray(0 until KEY_SIZE)
        println("          Key: '${String(key, Charsets.UTF_8)}'")
        println("Key candidate: '${String(keyCandidate, Charsets.UTF_8)}'")
        if (!keyCandidate.contentEquals(key)) {
            println("Invalid key.")
            return
        }

        // Extract auth number
        val authNumber = keyPacket.data.sliceArray(KEY_SIZE until KEY_SIZE + 2).toString(Charsets.UTF_8)

        // Asks for user approval
        println("New user wants to join ($authNumber). Do you approve? (y/n)> ")
        if (readln() != "y") {
            println("User denied.")
            return
        }

        // Sends the credential
        // Receives a response with some signed data
        val credentialArray = credential.toByteArray()
        val dataPacket =
            Network.sendWithResponse(keyPacket.port, 11, keyPacket.address, port, credentialArray, time)
        if (dataPacket == null) {
            println("Data not received.")
            return
        }
        val data = dataPacket.data.toString(Charsets.UTF_8)
        if(data != "signed_data") {
            println("Invalid data.")
            return
        }
        println("Data received: $data")
    }
}
