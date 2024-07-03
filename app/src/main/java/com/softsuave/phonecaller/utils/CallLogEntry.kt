package com.softsuave.phonecaller.utils

data class CallLogEntry(val number: String, val type: CALL_TYPE, val date: Long, val duration: String, val name: String)