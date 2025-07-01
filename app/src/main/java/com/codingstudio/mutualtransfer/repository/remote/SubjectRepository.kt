package com.codingstudio.mutualtransfer.repository.remote

import com.codingstudio.mutualtransfer.api.RetrofitInstance

class SubjectRepository {

    suspend fun getAllHighSecondarySubject() = RetrofitInstance.subjectAPI.getAllHighSecondarySubject()

}