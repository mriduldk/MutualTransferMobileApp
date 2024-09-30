package com.codingstudio.mutualtransfer.utils

open class EventWrapper<out T>(private val content: T?) {

    var hasBeenHandled = false
        private set

    fun getContentIfNotHandled() : T? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            content
        }
    }

    fun peekContent() : T? = content

}