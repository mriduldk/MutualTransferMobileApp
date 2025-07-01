package com.codingstudio.mutualtransfer.model.search

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.UUID

@Parcelize
data class ModelSearch (
    var searchId : String = UUID.randomUUID().toString(),

    var searchStateText : String ?= null,
    var searchStateId : String ?= null,

    var searchDistrictText : String ?= null,
    var searchDistrictId : String ?= null,
    var searchBlockText : String ?= null,
    var searchBlockId : String ?= null,
    var searchSchoolText : String ?= null,
    var searchSchoolId : String ?= null,


    var searchDepartmentText : String ?= null,
    var searchDepartmentId : String ?= null,

    var searchCurrentRoleText : String ?= null,
    var searchCurrentRoleId : String ?= null,

    var userDistrictText : String ?= null,
    var userDistrictId : String ?= null,
    var userBlockText : String ?= null,
    var userBlockId : String ?= null,
    var userSchoolText : String ?= null,
    var userSchoolId : String ?= null,

    var userSchoolType : String ?= null,
    var userPostType : String ?= null,
    var userSubject : String ?= null,

) : Parcelable

