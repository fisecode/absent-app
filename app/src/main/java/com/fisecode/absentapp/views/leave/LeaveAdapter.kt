package com.fisecode.absentapp.views.leave

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.fisecode.absentapp.R
import com.fisecode.absentapp.databinding.ItemLeaveBinding
import com.fisecode.absentapp.model.dummy.LeaveModel

class LeaveAdapter (
    private val listData : List<LeaveModel>,
    ) : RecyclerView.Adapter<LeaveAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemLeaveBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemLeaveBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder){
            with(listData[position]){
                binding.tvStartDateHeader.text = this.dateStart
                binding.tvEndDateHeader.text = this.dateEnd
                binding.tvStatusHeader.text = this.status
                binding.tvDateStart.text = this.dateStart
                binding.tvDateEnd.text = this.dateEnd
                binding.tvTotalDays.text = this.totalDays.toString()
                binding.tvLeaveType.text = this.leaveType
                binding.tvLeaveTypeHeader.text = this.leaveType
                binding.tvReason.text = this.reason
                if (this.status == "Reject") {
                    binding.tvStatusHeader.setTextColor(Color.parseColor("#F53558"));
                } else if (this.status == "Approved") {
                    binding.tvStatusHeader.setTextColor(Color.parseColor("#2FD686"));
                } else {
                    binding.tvStatusHeader.setTextColor(Color.parseColor("#FFB931"));
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
        return listData.size
    }
}
