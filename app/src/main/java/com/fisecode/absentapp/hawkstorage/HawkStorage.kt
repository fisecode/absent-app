package com.fisecode.absentapp.hawkstorage

import android.content.Context
import com.fisecode.absentapp.model.Employee
import com.fisecode.absentapp.model.LeaveHistory
import com.fisecode.absentapp.model.LeaveType
import com.fisecode.absentapp.model.User
import com.orhanobut.hawk.Hawk

class HawkStorage {
    companion object{
        private const val USER_KEY = "user_key"
        private const val TOKEN_KEY = "token_key"
        private const val EMPLOYEE_KEY = "employee_key"
        private const val LEAVETYPE_KEY = "leavetype_key"
        private const val LEAVEHISTORY_KEY = "leavehistory_key"
        private val hawkStorage = HawkStorage()

        fun instance(context: Context?): HawkStorage{
            Hawk.init(context).build()
            return hawkStorage
        }
    }

    fun setUser(user: User){
        Hawk.put(USER_KEY, user)
    }

    fun getUser(): User{
        return Hawk.get(USER_KEY)
    }

    fun setEmployee(employee: Employee){
        Hawk.put(EMPLOYEE_KEY, employee)
    }

    fun getEmployee(): Employee{
        return Hawk.get(EMPLOYEE_KEY)
    }

    fun setLeaveType(leaveType: List<LeaveType>?){
        Hawk.put(LEAVETYPE_KEY, leaveType)
    }

    fun getLeaveType(): List<LeaveType>?{
        return Hawk.get(LEAVETYPE_KEY)
    }

    fun setLeaveHistory(leaveHistory: List<LeaveHistory>?){
        Hawk.put(LEAVEHISTORY_KEY, leaveHistory)
    }

    fun getLeaveHistory(): List<LeaveHistory>?{
        return Hawk.get(LEAVEHISTORY_KEY)
    }


    fun setToken(accessToken: String){
        Hawk.put(TOKEN_KEY, accessToken)
    }

    fun getToken(): String{
        val rawToken = Hawk.get<String>(TOKEN_KEY)
        val token = rawToken.split("|")
        return token[1]
    }

    fun isSignIn(): Boolean{
        if (Hawk.contains(USER_KEY)){
            return true
        }
        return false
    }

    fun deleteAll(){
        Hawk.deleteAll()
    }
}