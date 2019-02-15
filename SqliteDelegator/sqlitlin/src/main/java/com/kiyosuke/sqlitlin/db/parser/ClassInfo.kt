package com.kiyosuke.sqlitlin.db.parser

import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.primaryConstructor

class ClassInfoCache {
    private val cacheData = mutableMapOf<KClass<*>, ClassInfo<*>>()

    @Suppress("UNCHECKED_CAST")
    operator fun <T : Any> get(cls: KClass<T>): ClassInfo<T> =
        cacheData.getOrPut(cls) { ClassInfo(cls) } as ClassInfo<T>
}

class ClassInfo<T : Any>(cls: KClass<T>) {
    private val constructor = cls.primaryConstructor!!

    private val columnNameToParamMap: MutableMap<String, KParameter> = hashMapOf()

    init {
        constructor.parameters.forEach {
            cacheDataForParameter(cls, it)
        }
    }

    private fun cacheDataForParameter(cls: KClass<*>, param: KParameter) {
        val paramName = param.name!!
        val property = cls.declaredMemberProperties.find { it.name == paramName } ?: return
        val name = property.findAnnotation<ColumnName>()?.name ?: paramName
        columnNameToParamMap[name] = param
    }

    fun getConstructorParameter(propName: String): KParameter = columnNameToParamMap[propName]!!

    fun parseConstructorArgument(param: KParameter, value: Any?): Any? {
        return value
    }

    fun createInstance(arguments: Map<KParameter, Any?>): T {
        return constructor.callBy(arguments)
    }
}