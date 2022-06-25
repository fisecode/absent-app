package com.fisecode.absentapp.model.dummy

class LeaveModel (status:String, dateStart:String, dateEnd:String, totalDays:Int, leaveType:String, reason:String, expand:Boolean){

    var status = ""
    var dateStart = ""
    var dateEnd = ""
    var totalDays = 0
    var leaveType = ""
    var reason = ""
    var expand = false

    init {
        this.status = status
        this.dateStart = dateStart
        this.dateEnd = dateEnd
        this.totalDays = totalDays
        this.leaveType = leaveType
        this.reason = reason
        this.expand = expand
    }
}