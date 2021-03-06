package com.kiyosuke.sqlitlin.db.parser

import com.kiyosuke.sqlitlin.db.ColumnMap
import kotlin.reflect.KClass
import kotlin.reflect.KParameter

@Deprecated("This function is used reflection, so takes a long time to complete.")
inline fun <reified T : Any> ColumnMap.parse(): T {
    return parse(T::class)
}

@Deprecated("This function is used reflection, so takes a long time to complete.")
fun <T : Any> ColumnMap.parse(targetClass: KClass<T>): T {
    val seed = ColumnSeed(targetClass, ClassInfoCache())
    this.forEach { (column, value) ->
        seed.setColumnProperty(column.name, value)
    }
    return seed.spawn()
}

internal class ColumnSeed<out T : Any>(
    targetClass: KClass<T>,
    classInfoCache: ClassInfoCache
) {
    private val classInfo: ClassInfo<T> = classInfoCache[targetClass]

    private val arguments = mutableMapOf<KParameter, Any?>()

    fun setColumnProperty(propertyName: String, value: Any?) {
        val param = classInfo.getConstructorParameter(propertyName)
        arguments[param] = classInfo.parseConstructorArgument(param, value)
    }

    fun spawn(): T = classInfo.createInstance(arguments)
}