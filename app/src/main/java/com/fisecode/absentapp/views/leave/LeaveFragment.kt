package com.fisecode.absentapp.views.leave

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.fisecode.absentapp.R
import com.fisecode.absentapp.databinding.FragmentLeaveBinding
import com.fisecode.absentapp.databinding.FragmentProfileBinding

class LeaveFragment : Fragment() {

    private var binding: FragmentLeaveBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLeaveBinding.inflate(inflater, container, false)
        return binding?.root
    }

}