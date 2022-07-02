package com.fisecode.absentapp.views.leave

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fisecode.absentapp.R
import com.fisecode.absentapp.databinding.FragmentLeaveBinding
import com.fisecode.absentapp.dialog.MyDialog
import com.fisecode.absentapp.hawkstorage.HawkStorage
import com.fisecode.absentapp.model.LeaveHistoryResponse
import com.fisecode.absentapp.model.Wrapper
import com.fisecode.absentapp.networking.ApiServices
import com.fisecode.absentapp.networking.RetrofitClient
import okhttp3.ResponseBody
import org.jetbrains.anko.startActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Converter
import retrofit2.Response
import java.io.IOException

class LeaveFragment : Fragment() {

    private var binding: FragmentLeaveBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLeaveBinding.inflate(layoutInflater)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()

    }

    override fun onResume() {
        super.onResume()
        getLeaveHistory()
    }

    private fun init() {
        onClick()
        val leaveHistory = HawkStorage.instance(context).getLeaveHistory()
        if (leaveHistory?.isNotEmpty() == true){
            initLeaveHistory()
        }
    }

    private fun onClick() {
        binding?.fabLeaveRequest?.setOnClickListener {
            context?.startActivity<LeaveRequestActivity>()
        }
    }

    private fun initLeaveHistory(){
        val leaveHistory = HawkStorage.instance(context).getLeaveHistory()
        val adapter = LeaveAdapter(leaveHistory)
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding?.rcListLeave?.layoutManager = layoutManager
        binding?.rcListLeave?.adapter = adapter
        adapter.notifyDataSetChanged()
    }
    private fun getLeaveHistory(){
        val token = HawkStorage.instance(context).getToken()
        MyDialog.showProgressDialog(context)
        ApiServices.getAbsentServices()
            .leaveHistory("Bearer $token")
            .enqueue(object : Callback<Wrapper<LeaveHistoryResponse>>{
                override fun onResponse(
                    call: Call<Wrapper<LeaveHistoryResponse>>,
                    response: Response<Wrapper<LeaveHistoryResponse>>
                ) {
                    MyDialog.hideDialog()
                    if (response.isSuccessful){
                        val leaveHistory = response.body()?.data?.leaveHistory
                        if (leaveHistory != null){
                            HawkStorage.instance(context).setLeaveHistory(leaveHistory)
                            initLeaveHistory()
                        }
                    }else {
                        val errorConverter: Converter<ResponseBody, Wrapper<LeaveHistoryResponse>> =
                            RetrofitClient
                                .getClient()
                                .responseBodyConverter(
                                    LeaveHistoryResponse::class.java,
                                    arrayOfNulls<Annotation>(0)
                                )
                        val errorResponse: Wrapper<LeaveHistoryResponse>?
                        try {
                            response.errorBody()?.let {
                                errorResponse = errorConverter.convert(it)
                                MyDialog.dynamicDialog(
                                    context,
                                    getString(R.string.failed),
                                    errorResponse?.meta?.message.toString()
                                )
                            }
                        } catch (e: IOException) {
                            e.printStackTrace()
                            Log.e(TAG, "Error: ${e.message}")
                        }
                    }
                }

                override fun onFailure(call: Call<Wrapper<LeaveHistoryResponse>>, t: Throwable) {
                    MyDialog.hideDialog()
                    Log.e(TAG, "Error: ${t.message}")
                }

            })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    companion object{
        private val TAG = LeaveFragment::class.java.simpleName
    }
}