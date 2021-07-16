package com.twofortyfouram

object MavenName {
    fun remap(oldName: String, newNamePrefix: String) = when {
        oldName.contains("metadata") -> {
            "$newNamePrefix-metadata"
        }
        oldName.contains("jvm") -> {
            "$newNamePrefix-jvm"
        }
        else -> {
            "$newNamePrefix"
        }
    }
}