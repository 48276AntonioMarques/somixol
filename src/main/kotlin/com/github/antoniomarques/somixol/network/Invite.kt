package com.github.antoniomarques.somixol.network

typealias Invite = String

const val CHECKSUM_SIZE = 9
const val KEY_SIZE = 10

fun Invite.toPort(): Int {
    // Module by big prime number
    val offset = this.map { it.code }.sum() % 7919
    return 1024 + offset
}

fun Invite.checksum(): ByteArray {
    // Hash this string
    return this.hashCode().toString().toByteArray()
}

fun Invite.toKey(): ByteArray {
    return "invitation".toByteArray()
}
