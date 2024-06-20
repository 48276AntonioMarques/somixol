package com.github.antoniomarques.somixol.network

import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.net.SocketTimeoutException
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

private enum class OutputType {
    SEND,
    BROADCAST
}


object Network {
    private val broadcast_address = InetAddress.getByName("255.255.255.255")

    private fun output(port: Int, message: ByteArray, type: OutputType, dstAddress: InetAddress? = null) {
        val packet = DatagramPacket(message, message.size, dstAddress ?: broadcast_address, port)
        val socket = DatagramSocket()
        socket.use {
            when(type) {
                OutputType.SEND -> socket.send(packet)
                OutputType.BROADCAST -> {
                    socket.broadcast = true
                    socket.send(packet)
                }
            }
        }
    }

    fun send(address: InetAddress, port: Int, message: ByteArray) = output(port, message, OutputType.SEND, address)

    fun broadcast(port: Int, message: ByteArray) = output(port, message, OutputType.BROADCAST)

    fun receive(port: Int, bufferSize: Int, timeout: Duration): DatagramPacket {
        val socket = DatagramSocket(port)
        val buffer = ByteArray(bufferSize)
        val packet = DatagramPacket(buffer, buffer.size)
        socket.soTimeout = timeout.inWholeMilliseconds.toInt()
        socket.use {
            socket.receive(packet)
        }
        return packet
    }

    private fun outputWithResponse(
        port: Int,
        bufferSize: Int,
        timeout: Duration,
        output: () -> Unit
    ): DatagramPacket? {
        val start = System.currentTimeMillis()
        val endTime = start + timeout.inWholeMilliseconds
        while(endTime > System.currentTimeMillis()) {
            output()
            try {
                val dutyCycle = 5.toDuration(DurationUnit.SECONDS)
                return receive(port, bufferSize, dutyCycle)
            }
            catch (_: SocketTimeoutException) { }
        }
        return null
    }

    fun sendWithResponse(
        srcPort: Int,
        bufferSize: Int,
        dstAddress:InetAddress,
        dstPort: Int,
        message: ByteArray,
        timeout: Duration
    ): DatagramPacket? =
        outputWithResponse(srcPort, bufferSize, timeout) {
            send(dstAddress, dstPort, message)
        }

    fun broadcastWithResponse(
        srcPort: Int,
        dstPort: Int,
        bufferSize: Int,
        message: ByteArray,
        timeout: Duration
    ): DatagramPacket? =
        outputWithResponse(dstPort, bufferSize, timeout) {
            broadcast(srcPort, message)
        }
}