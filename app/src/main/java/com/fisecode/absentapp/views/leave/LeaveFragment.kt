package com.fisecode.absentapp.views.leave

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fisecode.absentapp.R
import com.fisecode.absentapp.databinding.FragmentLeaveBinding
import com.fisecode.absentapp.databinding.FragmentProfileBinding
import com.fisecode.absentapp.model.dummy.LeaveModel

class LeaveFragment : Fragment() {

    private var _binding: FragmentLeaveBinding? = null
    private val binding get() = _binding!!
    private var leaveList : ArrayList<LeaveModel> = ArrayList()
    private lateinit var leaveAdapter: LeaveAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLeaveBinding.inflate(layoutInflater)
        return _binding?.root
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initDataDummy()
        var adapter = LeaveAdapter(leaveList)
        var layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        _binding?.rcListLeave?.layoutManager = layoutManager
        _binding?.rcListLeave?.adapter = adapter
        adapter.notifyDataSetChanged()
    }

    fun initDataDummy() {
        leaveList = ArrayList()
        leaveList.add(LeaveModel("20 Juni 2022", "Pending", "Senin, 20 Juni 2022", "Senin, 20 Juli 2022", 1, "Sick Leave", "Sakit", false))
        leaveList.add(LeaveModel("24 Juni 2022", "Reject", "Senin, 27 Juni 2022", "Rabu, 30 Juni 2022", 3, "Personal", "Ada Keperluan Keluarga", false))
        leaveList.add(LeaveModel("27 Juni 2022", "Approved", "Kamis, 30 Juni 2022", "Senin, 4 Juli 2022", 3, "Personal", "Liburan", false))
        leaveList.add(LeaveModel("27 Juni 2022", "Approved", "Kamis, 30 Juni 2022", "Senin, 4 Juli 2022", 3, "Personal", "Liburan", false))
    }
}