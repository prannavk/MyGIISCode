package com.giis.mobileappproto1.ui.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.appcompat.widget.AppCompatTextView
import com.giis.mobileappproto1.R

abstract class MySpinnerAdapters : BaseAdapter() {

}

class LabelsAdapter(
    private var stringsList: List<String>,
    private val context: Context,
    private val affectOtherViewsOnChange : (() -> Unit)?
) : MySpinnerAdapters(),
    OnTouchListener//, OnFocusChangeListener // AdapterView.OnItemSelectedListener {
{

    private var pleaseSelectPresentInList: Boolean
    private var faintLineVisible: Boolean = true

    init {
        addPleaseSelect()
        pleaseSelectPresentInList = true
        Log.e("ADAPTER", "init")
    }

    override fun getCount(): Int = stringsList.size
    override fun getItem(position: Int): Any = stringsList[position]
    override fun getItemId(position: Int): Long = position.toLong()
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view: View
        var itemViewHolder: ItemViewHolder
        convertView?.let {
            if (pleaseSelectPresentInList) {
                removePleaseSelect()
                pleaseSelectPresentInList = false
            }
            // notifyDataSetChanged() -> Uncommenting this may lead to infinite loop or App Freezing
            view = it
            itemViewHolder = view.tag as ItemViewHolder
            if(faintLineVisible){
                hideFaintLine(view)
                faintLineVisible = false
            }
            return view
        }
            ?: kotlin.run {
                val inflater = LayoutInflater.from(context)
                view = inflater.inflate(R.layout.simple_spinner_layout, parent, false)
                itemViewHolder = ItemViewHolder()
                itemViewHolder.itemTextView = view.findViewById(R.id.item_textView)
                view.tag = itemViewHolder
                itemViewHolder.itemTextView.text = stringsList[position]
                if(faintLineVisible){
                    hideFaintLine(view)
                    faintLineVisible = false
                }
                return view
            }
    }

    private class ItemViewHolder {
        lateinit var itemTextView: AppCompatTextView
    }

    private fun addPleaseSelect() {
        val mutableBuffer = mutableListOf<String>()
        mutableBuffer.add(0, "Please Select")
        mutableBuffer.addAll(stringsList)
        stringsList = mutableBuffer.toList()
    }

    private fun removePleaseSelect() {
        stringsList = stringsList.filter { it != "Please Select" }
    }

    private fun hideFaintLine(view: View) {
        view.findViewById<View>(R.id.faint_spinner_item_line).visibility =
            View.GONE
    }

    private fun showFaintLine(view: View) {
        view.findViewById<View>(R.id.faint_spinner_item_line).visibility =
            View.VISIBLE
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        if (pleaseSelectPresentInList) {
            notifyDataSetChanged()
        }
        affectOtherViewsOnChange?.invoke()
        v?.performClick()
        v?.let {
            if(!faintLineVisible){
                showFaintLine(it)
                faintLineVisible = true
            }
        }
        return false
    }

}