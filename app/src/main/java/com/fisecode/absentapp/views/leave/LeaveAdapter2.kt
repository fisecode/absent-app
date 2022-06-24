package com.fisecode.absentapp.views.leave

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fisecode.absentapp.R
import com.fisecode.absentapp.databinding.FragmentLeaveBinding
import com.fisecode.absentapp.model.dummy.LeaveModel
import kotlinx.android.synthetic.main.item_leave.view.*

class LeaveAdapter2 (
    private val listData : List<LeaveModel>,
    private val itemAdapterCallback : ItemAdapterCallback,
    ) : RecyclerView.Adapter<LeaveAdapter2.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.item_leave, parent, false)
        return ViewHolder(view)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listData[position], itemAdapterCallback)
        notifyItemChanged(position)
    }

    override fun getItemCount(): Int {
        return listData.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(data: LeaveModel, itemAdapterCallback: ItemAdapterCallback) {
            itemView.apply {
                tv_applied_on.text = data.appliedOn
                tv_status_leave.text = data.status
                tv_date_start.text = data.dateStart
                tv_date_end.text = data.dateEnd
                tv_total_days.text = data.totalDays.toString()
                tv_leave_type.text = data.leaveType
                tv_reason.text = data.reason
                if (data.status == "Reject") {
                    tv_status_leave.setTextColor(Color.parseColor("#FF0000"));
                } else if (data.status == "Pending") {
                    tv_status_leave.setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.color_primary
                        )
                    );
                }
                val isVisible : Boolean = data.expand
                cl_expand.visibility = if (isVisible) View.VISIBLE else View.GONE
                cl_leave.setOnClickListener {

                }
                itemView.setOnClickListener { itemAdapterCallback.onClick(it, data) }
            }
        }
    }

    interface ItemAdapterCallback {
        fun onClick(v: View, data: LeaveModel)
    }
}
