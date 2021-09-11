package de.adschmidt.songskipper.backend

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.reflect.full.companionObject

interface Loggable {}

fun <T : Loggable> getClassForLogging(javaClass: Class<T>): Class<*> {
    return javaClass.enclosingClass?.takeIf {
        it.kotlin.companionObject?.java == javaClass
    } ?: javaClass
}

inline fun <reified T : Loggable> T.logger(): Logger =
    LoggerFactory.getLogger(getClassForLogging(T::class.java).name)