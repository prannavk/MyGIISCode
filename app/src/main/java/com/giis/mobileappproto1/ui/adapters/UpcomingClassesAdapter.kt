package com.giis.mobileappproto1.ui.adapters

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.giis.mobileappproto1.R
import com.giis.mobileappproto1.data.models.ClassDetails1DTO
import com.giis.mobileappproto1.databinding.UpcomingClasscardLayoutBinding

class UpcomingClassesAdapter(
    private val classesList: List<ClassDetails1DTO>,
    private val parentContext: Context
) : RecyclerView.Adapter<UpcomingClassesAdapter.ClassViewHolder>() {

    inner class ClassViewHolder(private val binding: UpcomingClasscardLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ClassDetails1DTO, position: Int) {
            binding.classDetails = item
            when (item.classStatusDetail) {
                "Upcoming" -> binding.statusCard.backgroundTintList = ColorStateList.valueOf(
                    parentContext.resources.getColor(
                        R.color.lemon_chiffon
                    )
                )

                "Delayed" -> binding.statusCard.backgroundTintList = ColorStateList.valueOf(
                    parentContext.resources.getColor(
                        R.color.delayed
                    )
                )

                "Starting Soon" -> binding.statusCard.backgroundTintList = ColorStateList.valueOf(
                    parentContext.resources.getColor(
                        R.color.starting_soon
                    )
                )

                else -> binding.statusCard.backgroundTintList = ColorStateList.valueOf(
                    parentContext.resources.getColor(
                        R.color.indian_red
                    )
                )
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClassViewHolder {
        val inflater = LayoutInflater.from(parentContext)
        val listItemBinding = UpcomingClasscardLayoutBinding.inflate(inflater, parent, false)
        return ClassViewHolder(listItemBinding)
    }

    override fun getItemCount(): Int = classesList.size

    override fun onBindViewHolder(holder: ClassViewHolder, position: Int) {
        holder.bind(classesList[position], position)
    }
}