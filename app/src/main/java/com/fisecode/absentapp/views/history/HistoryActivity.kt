package com.fisecode.absentapp.views.history

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.applandeo.materialcalendarview.EventDay
import com.applandeo.materialcalendarview.listeners.OnCalendarPageChangeListener
import com.applandeo.materialcalendarview.listeners.OnDayClickListener
import com.fisecode.absentapp.R
import com.fisecode.absentapp.databinding.ActivityHistoryBinding
import com.fisecode.absentapp.dialog.MyDialog
import com.fisecode.absentapp.hawkstorage.HawkStorage
import com.fisecode.absentapp.model.AbsentHistory
import com.fisecode.absentapp.model.AbsentHistoryResponse
import com.fisecode.absentapp.model.Wrapper
import com.fisecode.absentapp.networking.ApiServices
import com.fisecode.absentapp.utils.Helpers.formatTo
import com.fisecode.absentapp.utils.Helpers.toCalendar
import com.fisecode.absentapp.utils.Helpers.toDate
import com.fisecode.absentapp.utils.Helpers.toDateCalendar
import com.fisecode.absentapp.utils.Helpers.toTime
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class HistoryActivity : AppCompatActivity() {

    private companion object{
        private val TAG: String = HistoryActivity::class.java.simpleName
    }

    private var binding: ActivityHistoryBinding? = null
    private val events = mutableListOf<EventDay>()
    private var dataHistories: List<AbsentHistory>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        init()
    }

    private fun init() {
        setSupportActionBar(binding?.tbHistory)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        //Request Data History
        requestDataHistory()

        //Setup Calendar Swipe
        setupCalendar()

        //OnClick
        onClick()
    }

    private fun requestDataHistory() {
        val calendar = binding?.calendarViewHistory?.currentPageDate
        val lastDay = calendar?.getActualMaximum(Calendar.DAY_OF_MONTH)
        val month = calendar?.get(Calendar.MONTH)?.plus(1)
        val year = calendar?.get(Calendar.YEAR)

        val fromDate = "$year-$month-01"
        val toDate = "$year-$month-$lastDay"
        getDataHistory(fromDate, toDate)
    }

    private fun getDataHistory(fromDate: String, toDate: String) {
        val token = HawkStorage.instance(this).getToken()
        binding?.pbHistory?.visibility = View.VISIBLE
        ApiServices.getAbsentServices()
            .getHistoryAbsent("Bearer $token", fromDate, toDate)
            .enqueue(object : Callback<Wrapper<AbsentHistoryResponse>>{
                override fun onResponse(
                    call: Call<Wrapper<AbsentHistoryResponse>>,
                    response: Response<Wrapper<AbsentHistoryResponse>>
                ) {
                    binding?.pbHistory?.visibility = View.GONE
                    if (response.isSuccessful){
                        dataHistories = response.body()?.data?.absent
                        if (dataHistories != null){
                            for (dataHistory in dataHistories!!){
                                val status = dataHistory.status
                                val checkInTime: String
                                val checkOutTime: String
                                val checkInDate: String
                                val calendarHistoryCheckIn: Calendar?
                                val calendarHistoryCheckOut: Calendar?
                                val currentDate = Calendar.getInstance()

                                if (status == "Present"){
                                    checkInDate = dataHistory.date.toString()
                                    checkInTime = dataHistory.checkIn.toString()
                                    checkOutTime = dataHistory.checkOut.toString()

                                    calendarHistoryCheckOut = checkInDate.toDate()?.toCalendar()

                                    if (calendarHistoryCheckOut != null){
                                        events.add(EventDay(calendarHistoryCheckOut, R.drawable.ic_baseline_check_circle_primary_24))
                                    }

                                    if (currentDate.get(Calendar.DAY_OF_MONTH) == calendarHistoryCheckOut?.get(Calendar.DAY_OF_MONTH)){
                                        binding?.tvCurrentDate?.text = checkInDate.toDate()?.formatTo("dd")
                                        binding?.tvCurrentMonth?.text = checkInDate.toDate()?.formatTo("MMMM")
                                        binding?.tvTimeCheckIn?.text = checkInTime.toTime()?.formatTo("HH:mm")
                                        binding?.tvTimeCheckOut?.text = checkOutTime.toTime()?.formatTo("HH:mm")
                                    }
                                }else{
                                    checkInDate = dataHistory.date.toString()
                                    checkInTime = dataHistory.checkIn.toString()
                                    calendarHistoryCheckIn = checkInDate.toDate()?.toCalendar()

                                    if (calendarHistoryCheckIn != null){
                                        events.add(EventDay(calendarHistoryCheckIn, R.drawable.ic_baseline_check_circle_yellow_light_24))
                                    }

                                    if (currentDate.get(Calendar.DAY_OF_MONTH) == calendarHistoryCheckIn?.get(Calendar.DAY_OF_MONTH)){
                                        binding?.tvCurrentDate?.text = checkInDate.toDate()?.formatTo("dd")
                                        binding?.tvCurrentMonth?.text = checkInDate.toDate()?.formatTo("MMMM")
                                        binding?.tvTimeCheckIn?.text = checkInTime.toTime()?.formatTo("HH:mm")
                                    }
                                }
                            }
                        }
                        binding?.calendarViewHistory?.setEvents(events)
                    }else{
                        MyDialog.dynamicDialog(this@HistoryActivity, getString(R.string.alert), getString(R.string.something_wrong))
                    }
                }

                override fun onFailure(call: Call<Wrapper<AbsentHistoryResponse>>, t: Throwable) {
                    binding?.pbHistory?.visibility = View.GONE
                    MyDialog.dynamicDialog(this@HistoryActivity, getString(R.string.alert), "${t.message}")
                    Log.e(TAG, "Error: ${t.message}")
                }

            })
    }

    private fun setupCalendar() {
        binding?.calendarViewHistory?.setOnPreviousPageChangeListener(object :
            OnCalendarPageChangeListener {
            override fun onChange() {
                requestDataHistory()
            }

        })

        binding?.calendarViewHistory?.setOnForwardPageChangeListener(object :
            OnCalendarPageChangeListener {
            override fun onChange() {
                requestDataHistory()
            }

        })
    }

    private fun onClick() {
        binding?.tbHistory?.setNavigationOnClickListener {
            finish()
        }
        binding?.calendarViewHistory?.setOnDayClickListener(object : OnDayClickListener {
            override fun onDayClick(eventDay: EventDay) {
                val clickedDayCalendar = eventDay.calendar
                binding?.tvCurrentDate?.text = clickedDayCalendar.toDateCalendar().formatTo("dd")
                binding?.tvCurrentMonth?.text = clickedDayCalendar.toDateCalendar().formatTo("MMMM")

                if (dataHistories != null){
                    for (dataHistory in dataHistories!!){
                        val checkInTime: String
                        val checkOutTime: String
                        val date = dataHistory.date
                        val calendarDate = date?.toDate()?.toCalendar()
                        if (clickedDayCalendar.get(Calendar.DAY_OF_MONTH) == calendarDate?.get(Calendar.DAY_OF_MONTH)){
                            if (dataHistory.status == "Present"){
                                checkInTime = dataHistory.checkIn.toString()
                                checkOutTime = dataHistory.checkOut.toString()

                                binding?.tvTimeCheckIn?.text = checkInTime.toTime()?.formatTo("HH:mm")
                                binding?.tvTimeCheckOut?.text = checkOutTime.toTime()?.formatTo("HH:mm")
                                break
                            }else{
                                checkInTime = dataHistory.checkIn.toString()
                                binding?.tvTimeCheckIn?.text = checkInTime.toTime()?.formatTo("HH:mm")
                                break
                            }
                        }else{
                            binding?.tvTimeCheckIn?.text = getString(R.string.default_text)
                            binding?.tvTimeCheckOut?.text = getString(R.string.default_text)
                        }
                    }
                }
            }

        })
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}