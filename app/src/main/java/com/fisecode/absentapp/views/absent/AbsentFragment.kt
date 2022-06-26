package com.fisecode.absentapp.views.absent

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.fisecode.absentapp.BuildConfig
import com.fisecode.absentapp.R
import com.fisecode.absentapp.databinding.FragmentAbsentBinding
import com.fisecode.absentapp.hawkstorage.HawkStorage
import com.fisecode.absentapp.views.changepassword.ChangePasswordActivity
import com.fisecode.absentapp.views.history.HistoryActivity
import org.jetbrains.anko.startActivity

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
        onClick()
        updateView()
    }

    private fun updateView() {
        val user = HawkStorage.instance(context).getUser()
        val employee = HawkStorage.instance(context).getEmployee()
        val imageUrl = BuildConfig.BASE_IMAGE_URL + user.photo
        val employeeId = employee.employeeId
        val employeeIdFormat = String.format("%05d", employeeId?.toInt())
        Glide.with(requireContext()).load(imageUrl).placeholder(R.drawable.employee_photo).into(binding!!.ivEmployeePhoto)
        binding?.tvNameEmployee?.text = user.name
        binding?.tvNumberIdEmployee?.text = "LMS00$employeeIdFormat"
    }

    private fun onClick() {
        binding?.btnHistory?.setOnClickListener {
            context?.startActivity<HistoryActivity>()
        }
    }

}