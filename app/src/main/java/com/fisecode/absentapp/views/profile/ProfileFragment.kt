package com.fisecode.absentapp.views.profile

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.fisecode.absentapp.BuildConfig
import com.fisecode.absentapp.R
import com.fisecode.absentapp.databinding.FragmentProfileBinding
import com.fisecode.absentapp.dialog.MyDialog
import com.fisecode.absentapp.hawkstorage.HawkStorage
import com.fisecode.absentapp.model.SignOutResponse
import com.fisecode.absentapp.networking.ApiServices
import com.fisecode.absentapp.views.absentspot.AbsentSpotActivity
import com.fisecode.absentapp.views.changepassword.ChangePasswordActivity
import com.fisecode.absentapp.views.employeedetail.EmployeeDetailActivity
import com.fisecode.absentapp.views.main.MainActivity
import com.fisecode.absentapp.views.signin.SignInActivity
import org.jetbrains.anko.startActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileFragment : Fragment() {

    private var binding: FragmentProfileBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onClick()
        updateView()
    }

    private fun updateView() {
        val user = HawkStorage.instance(context).getUser()
        val imageUrl = BuildConfig.BASE_IMAGE_URL + user.photo
        Glide.with(requireContext()).load(imageUrl).placeholder(R.drawable.employee_photo).into(binding!!.imgProfilePicture)
        binding?.tvNameProfile?.text = user.name
        binding?.tvEmailProfile?.text = user.email
    }

    private fun onClick() {
        binding?.btnChangePassword?.setOnClickListener {
            context?.startActivity<ChangePasswordActivity>()
        }
        binding?.btnSignOut?.setOnClickListener {
            AlertDialog.Builder(context)
                .setTitle(getString(R.string.sign_out))
                .setMessage(getString(R.string.are_you_sure))
                .setPositiveButton(getString(R.string.yes)){ dialog, _ ->
                    signOutRequest(dialog)
                }
                .setNegativeButton(getString(R.string.no)){dialog, _->
                    dialog.dismiss()
                }
                .show()
        }

        binding?.btnEmployeeDetails?.setOnClickListener {
            context?.startActivity<EmployeeDetailActivity>()
        }
        binding?.btnAbsentSpot?.setOnClickListener {
            context?.startActivity<AbsentSpotActivity>()
        }
    }

    private fun signOutRequest(dialog: DialogInterface?) {
        val token = HawkStorage.instance(context).getToken()
        ApiServices.getAbsentServices()
            .signOutRequest("Bearer $token")
            .enqueue(object : Callback<SignOutResponse>{
                override fun onResponse(
                    call: Call<SignOutResponse>,
                    response: Response<SignOutResponse>
                ) {
                    dialog?.dismiss()
                    if (response.isSuccessful){
                        HawkStorage.instance(context).deleteAll()
                        (activity as MainActivity).finishAffinity()
                        context?.startActivity<SignInActivity>()
                    }else{
                        MyDialog.dynamicDialog(context, getString(R.string.alert), getString(R.string.something_wrong))
                    }
                }

                override fun onFailure(call: Call<SignOutResponse>, t: Throwable) {
                    dialog?.dismiss()
                    MyDialog.dynamicDialog(context, getString(R.string.alert), "Error ${t.message}")
                }
            })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun onResume() {
        super.onResume()
        updateView()
    }

}

