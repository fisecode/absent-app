package com.fisecode.absentapp.model

import com.google.gson.annotations.SerializedName

data class LeaveHistory(

    @field:SerializedName("end_date")
    val endDate: String? = null,

    @field:SerializedName("updated_at")
    val updatedAt: String? = null,

    @field:SerializedName("applied_on")
    val appliedOn: String? = null,

    @field:SerializedName("leave_type")
    val leaveType: LeaveType? = null,

    @field:SerializedName("employee_id")
    val employeeId: Int? = null,

    @field:SerializedName("created_at")
    val createdAt: String? = null,

    @field:SerializedName("total_leave_days")
    val totalLeaveDays: String? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("leave_reason")
    val leaveReason: String? = null,

    @field:SerializedName("leave_type_id")
    val leaveTypeId: Int? = null,

    @field:SerializedName("start_date")
    val startDate: String? = null,

    @field:SerializedName("status")
    val status: String? = null
) {
    var expand: Boolean = false
}
