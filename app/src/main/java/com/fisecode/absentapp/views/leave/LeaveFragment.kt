package com.fisecode.absentapp.views.leave

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fisecode.absentapp.databinding.FragmentLeaveBinding
import com.fisecode.absentapp.model.dummy.LeaveModel
import org.jetbrains.anko.startActivity

class LeaveFragment : Fragment() {

    private var binding: FragmentLeaveBinding? = null
    private var leaveList : ArrayList<LeaveModel> = ArrayList()
    private lateinit var leaveAdapter: LeaveAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLeaveBinding.inflate(layoutInflater)
        return binding?.root
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initDataDummy()
        onClick()
        val adapter = LeaveAdapter(leaveList)
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding?.rcListLeave?.layoutManager = layoutManager
        binding?.rcListLeave?.adapter = adapter
        adapter.notifyDataSetChanged()
    }

    private fun onClick() {
        binding?.fabLeaveRequest?.setOnClickListener {
            context?.startActivity<LeaveRequestActivity>()
        }
    }

    private fun initDataDummy() {
        leaveList = ArrayList()
        leaveList.add(LeaveModel("Pending", "20 Jun 2022", "20 Jul 2022", 1, "Sick Leave", "Sakit", false))
        leaveList.add(LeaveModel("Reject", "27 Jun 2022", "30 Jun 2022", 3, "Personal", "Ada Keperluan Keluarga", false))
        leaveList.add(LeaveModel("Approved", "30 Jun 2022", "4 Jul 2022", 3, "Personal", "Liburan", false))
        leaveList.add(LeaveModel("Approved", "30 Jun 2022", "4 Jul 2022", 3, "Annual Leave", "Liburan", false))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}