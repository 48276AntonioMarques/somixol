package com.github.antoniomarques.somixol.network

import java.net.DatagramPacket
import java.net.InetAddress
import java.net.PortUnreachableException
import java.net.SocketTimeoutException
import java.nio.charset.Charset
import kotlin.time.Duration

object Stranger {
    fun join(invite: Invite, time: Duration) {
        val port = invite.toPort()
        val checksum = invite.checksum()

        // Receives the checksum
        val discoveryPacket: DatagramPacket? = try {
            Network.receive(port, CHECKSUM_SIZE, time)
        } catch (ste: SocketTimeoutException) {
            println("Room not found.")
            null
        }
        catch (pue: PortUnreachableException) {
            println("Port unreachable.")
            null
        }
        if (discoveryPacket == null) return
        if (!discoveryPacket.data.contentEquals(checksum)) {
            println("Room mismatch.")
            return
        }

        val key = invite.toKey()
        val authNumber = (10..99).random().toString()
        val message = key + authNumber.toByteArray()
        // Sends the key and auth number
        // Receives a response with the credential
        println("Showing invite(${String(message, Charset.defaultCharset())}})...")
        val credentialPacket =
            Network.sendWithResponse(port, 23, discoveryPacket.address, port + 1, message, time)

        if (credentialPacket == null) {
            println("Credential not received.")
            return
        }
        val credential = credentialPacket.data.toString(Charsets.UTF_8)
        println("Credential received: $credential")
        // Sends some signed constant
        val data = ""
        val signedData = "signed_data".toByteArray()
        Network.send(discoveryPacket.address, port + 1, signedData)
    }

}