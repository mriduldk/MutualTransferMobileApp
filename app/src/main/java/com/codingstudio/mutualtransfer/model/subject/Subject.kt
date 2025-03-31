package com.codingstudio.mutualtransfer.model.subject

data class Subject (
    val id: Int,
    val subject_id: String ?= null,
    val subject_name: String ?= null,
    val type: String ?= null,
    val is_delete: Int = 0,
    val created_at: String ?= null,
    val updated_at: String ?= null
)