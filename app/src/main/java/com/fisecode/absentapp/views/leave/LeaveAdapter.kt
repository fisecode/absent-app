package com.fisecode.absentapp.views.leave

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fisecode.absentapp.R
import com.fisecode.absentapp.databinding.FragmentLeaveBinding
import com.fisecode.absentapp.model.dummy.LeaveModel

class LeaveAdapter (
    private val lisData : List<LeaveModel>,
    private val itemAdapterCallback : ItemAdapterCallback,
    ) : RecyclerView.Adapter<HomeAdapter.ViewHolder>(){

}