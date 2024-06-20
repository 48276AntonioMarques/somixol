package com.github.antoniomarques.somixol

import com.github.antoniomarques.somixol.network.Doorman
import com.github.antoniomarques.somixol.network.Stranger
import com.github.antoniomarques.somixol.network.checksum
import com.github.antoniomarques.somixol.network.toPort
import kotlin.time.DurationUnit
import kotlin.time.toDuration

fun main(args: Array<String>) {
    val mode = if (args.isEmpty()) readln() else args[0]
    when (mode) {
        "1" -> hireDoorman()
        "2" -> joinRoom()
    }
    // Start the APP
    println("--END--")
}

fun hireDoorman() {
    val room = Room.create()
    val invite = "local"
    val checkString = String(invite.checksum(), Charsets.UTF_8)
    print("Watching for invite(port: ${invite.toPort()}, checksum: ${checkString})")
    val timeout = 15.toDuration(DurationUnit.SECONDS)
    // val timeout = 1.toDuration(DurationUnit.MINUTES)
    Doorman.watch(invite, room.credential, timeout)
}

fun joinRoom() {
    val invite = "local"
    val checkString = String(invite.checksum(), Charsets.UTF_8)
    println("Searching for room(port: ${invite.toPort()}, checksum: ${checkString})...")
    val timeout = 15.toDuration(DurationUnit.SECONDS)
    // val timeout = 1.toDuration(DurationUnit.MINUTES)
    Stranger.join(invite, timeout)
}
