package com.fisecode.absentapp.views.absent

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.fisecode.absentapp.BuildConfig
import com.fisecode.absentapp.R
import com.fisecode.absentapp.databinding.FragmentAbsentBinding
import com.fisecode.absentapp.dialog.MyDialog
import com.fisecode.absentapp.hawkstorage.HawkStorage
import com.fisecode.absentapp.model.GetUserResponse
import com.fisecode.absentapp.model.Wrapper
import com.fisecode.absentapp.networking.ApiServices
import com.fisecode.absentapp.networking.RetrofitClient
import com.fisecode.absentapp.utils.Helpers
import com.fisecode.absentapp.views.history.HistoryActivity
import okhttp3.ResponseBody
import org.jetbrains.anko.startActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Converter
import retrofit2.Response
import java.io.IOException

class AbsentFragment : Fragment() {

    private var binding: FragmentAbsentBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAbsentBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        onClick()
    }

    private fun init() {
        getUserData()
        updateView()
    }

    private fun getUserData() {
        val token = HawkStorage.instance(context).getToken()
        MyDialog.showProgressDialog(context)
        ApiServices.getAbsentServices()
            .getUser("Bearer $token")
            .enqueue(object : Callback<Wrapper<GetUserResponse>>{
                override fun onResponse(
                    call: Call<Wrapper<GetUserResponse>>,
                    response: Response<Wrapper<GetUserResponse>>
                ) {
                    MyDialog.hideDialog()
                    if (response.isSuccessful){
                        val user = response.body()?.data?.employee?.get(0)?.user
                        val employee = response.body()?.data?.employee?.first()
                        if (user != null && employee != null){
                            HawkStorage.instance(context).setUser(user)
                            HawkStorage.instance(context).setEmployee(employee)
                        }
                    }else{
                        val errorConverter: Converter<ResponseBody, Wrapper<GetUserResponse>> =
                            RetrofitClient
                                .getClient()
                                .responseBodyConverter(
                                    GetUserResponse::class.java,
                                    arrayOfNulls<Annotation>(0)
                                )
                        val errorResponse: Wrapper<GetUserResponse>?
                        try {
                            response.errorBody()?.let {
                                errorResponse = errorConverter.convert(it)
                                MyDialog.dynamicDialog(context,
                                    getString(R.string.failed),
                                    errorResponse?.meta?.message.toString())
                            }
                        }catch (e: IOException){
                            e.printStackTrace()
                            Log.e(TAG, "Error: ${e.message}")
                        }
                    }
                }

                override fun onFailure(call: Call<Wrapper<GetUserResponse>>, t: Throwable) {
                    MyDialog.hideDialog()
                    Log.e(TAG, "Error: ${t.message}")
                }

            })
    }

    private fun updateView() {
        val user = HawkStorage.instance(context).getUser()
        val employee = HawkStorage.instance(context).getEmployee()
        val imageUrl = BuildConfig.BASE_IMAGE_URL + user.photo
        Glide.with(requireContext()).load(imageUrl).placeholder(R.drawable.employee_photo).into(binding!!.ivEmployeePhoto)
        binding?.tvNameEmployee?.text = user.name
        binding?.tvNumberIdEmployee?.text = Helpers.employeeIdFormat(employee.employeeId)
    }

    private fun onClick() {
        binding?.btnHistory?.setOnClickListener {
            context?.startActivity<HistoryActivity>()
        }
    }

    companion object{
        private val TAG = AbsentFragment::class.java.simpleName
    }


}