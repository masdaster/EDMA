package com.github.masdaster.edma.models

data class ProxyResult<T>(val data: T?, val error: Throwable? = null)
