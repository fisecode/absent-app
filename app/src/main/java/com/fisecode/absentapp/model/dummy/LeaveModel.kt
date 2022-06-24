package com.fisecode.absentapp.model.dummy

class LeaveModel (appliedOn:String, status:String, dateStart:String, dateEnd:String, totalDays:Int, leaveType:String, reason:String){

    var appliedOn = ""
    var status = ""
    var dateStart = ""
    var dateEnd = ""
    var totalDays = 0
    var leaveType = ""
    var reason = ""

    init {
        this.appliedOn = appliedOn
        this.status = status
        this.dateStart = dateStart
        this.dateEnd = dateEnd
        this.totalDays = totalDays
        this.leaveType = leaveType
        this.reason = reason
    }
}