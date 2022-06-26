package com.fisecode.absentapp.views.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.fisecode.absentapp.R
import com.fisecode.absentapp.databinding.FragmentProfileBinding
import com.fisecode.absentapp.views.changepassword.ChangePasswordActivity
import com.fisecode.absentapp.views.employeedetail.EmployeeDetailActivity
import com.fisecode.absentapp.views.main.MainActivity
import com.fisecode.absentapp.views.signin.SignInActivity
import org.jetbrains.anko.startActivity

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
    }

    private fun onClick() {
        binding?.btnChangePassword?.setOnClickListener {
            context?.startActivity<ChangePasswordActivity>()
        }
        binding?.btnSignOut?.setOnClickListener {
            context?.startActivity<SignInActivity>()
            (activity as MainActivity).finishAffinity()
        }
        binding?.btnEmployeeDetails?.setOnClickListener {
            context?.startActivity<EmployeeDetailActivity>()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

}