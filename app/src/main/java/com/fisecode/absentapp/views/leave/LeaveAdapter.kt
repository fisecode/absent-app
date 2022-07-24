package com.fisecode.absentapp.views.leave

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fisecode.absentapp.R
import com.fisecode.absentapp.databinding.ItemLeaveBinding
import com.fisecode.absentapp.model.LeaveHistory
import com.fisecode.absentapp.utils.Helpers.formatTo
import com.fisecode.absentapp.utils.Helpers.toDate

class LeaveAdapter(
    private val listData: List<LeaveHistory>?,
    ) : RecyclerView.Adapter<LeaveAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemLeaveBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemLeaveBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder){
            with(listData!![position]){
                val startDate = this.startDate.toString()
                val endDate = this.endDate.toString()
                binding.tvStartDateHeader.text = startDate.toDate()?.formatTo("dd MMM yyyy")
                binding.tvEndDateHeader.text = endDate.toDate()?.formatTo("dd MMM yyyy")
                binding.tvStatusHeader.text = this.status
                binding.tvStartDate.text = startDate.toDate()?.formatTo("dd MMM yyyy")
                binding.tvEndDate.text = endDate.toDate()?.formatTo("dd MMM yyyy")
                binding.tvTotalDays.text = this.totalLeaveDays.toString()
                binding.tvLeaveType.text = this.leaveType?.name
                binding.tvLeaveTypeHeader.text = this.leaveType?.name
                binding.tvReason.text = this.leaveReason
                when (this.status) {
                    "Reject" -> {
                        binding.tvStatusHeader.setTextColor(Color.parseColor("#F53558"))
                    }
                    "Approved" -> {
                        binding.tvStatusHeader.setTextColor(Color.parseColor("#2FD686"))
                    }
                    else -> {
                        binding.tvStatusHeader.setTextColor(Color.parseColor("#FFB900"))
                    }
                }
                binding.clExpand.visibility = if (this.expand) View.VISIBLE else View.GONE
                binding.clLeave.setOnClickListener {
                    this.expand = !this.expand
                    notifyItemChanged(position)
                }
                if (this.expand) {
                    binding.ivArrow.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24)
                }else {
                    binding.ivArrow.setImageResource(R.drawable.ic_baseline_keyboard_arrow_right_24)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return listData!!.size
    }
}
