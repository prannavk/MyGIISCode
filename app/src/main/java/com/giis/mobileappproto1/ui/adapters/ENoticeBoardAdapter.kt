package com.giis.mobileappproto1.ui.adapters

import android.content.Context
import android.content.res.ColorStateList
import android.os.Build
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.giis.mobileappproto1.R
import com.giis.mobileappproto1.data.models.NoticeDTO
import com.giis.mobileappproto1.databinding.EnoticeCardLayoutBinding
import com.giis.mobileappproto1.utils.globalTag

class ENoticeBoardAdapter(
    private val noticeList: List<NoticeDTO>,
    private val parentContext: Context
) :
    RecyclerView.Adapter<ENoticeBoardAdapter.NoticeViewHolder>() {

    private val colorForegroundList =
        listOf<Int>(R.color.light_cyan_blue, R.color.deep_pink, R.color.lime_green)
    private val colorBackgroundList =
        listOf<Int>(R.color.alice_blue, R.color.light_pink, R.color.greenYellow)

    inner class NoticeViewHolder(private val binding: EnoticeCardLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: NoticeDTO, position: Int) {
            binding.noticeItem = item

            // setting this here since I did not set the data binding attribute for this field
            binding.noticeDescTv.text = item.noticeDescription

            // I set the colors of the eNotice Cards here and used the array approach instead of
            val f = position % colorForegroundList.size
            val b = position % colorBackgroundList.size
            binding.noticeTypeCard.backgroundTintList =
                ColorStateList.valueOf(parentContext.resources.getColor(colorBackgroundList[b]))
            binding.borderBoxColor.backgroundTintList =
                ColorStateList.valueOf(parentContext.resources.getColor(colorForegroundList[f]))
            binding.noticeTypeCard.setTextColor(parentContext.resources.getColor(colorForegroundList[f]))

            binding.noticeDescTv.viewTreeObserver.addOnGlobalLayoutListener(object :
                ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    val textStringLength = item.noticeDescription.length
                    val textStringPaintTextSize =
                        binding.noticeDescTv.paint.measureText(item.noticeDescription)
                    val textViewWidth = binding.noticeDescTv.width
                    val textViewMaxLines = binding.noticeDescTv.maxLines
                    val referenceCharSize = (textStringPaintTextSize / textStringLength).toInt()
                    val maxNumberOfChars =
                        ((textViewWidth / referenceCharSize) * textViewMaxLines)
                    val numberOfCharsOfThisText =
                        (textStringPaintTextSize) / (referenceCharSize * textViewMaxLines)
                    if ((maxNumberOfChars / 2) <= numberOfCharsOfThisText) {
                        val truncatedString =
                            item.noticeDescription.substring(0, maxNumberOfChars - 13)
                        val finalTruncatedString =
                            truncatedString.substring(0, truncatedString.lastIndexOf(" "))
                                .plus("...")
                        binding.noticeDescTv.text = finalTruncatedString
                        Log.e(globalTag, "Truncated String for $position = $finalTruncatedString")
                    }
                    Log.e(
                        globalTag,
                        "max chars for $position is $maxNumberOfChars , $textStringPaintTextSize - textPaintSize, $referenceCharSize - refCharSize, $textStringLength - textStringLength"
                    )
                    Log.e(
                        globalTag,
                        "for $position -> $maxNumberOfChars and ${numberOfCharsOfThisText}, maxLines -> $textViewMaxLines"
                    )
                    binding.noticeDescTv.viewTreeObserver.removeOnGlobalLayoutListener(this)
                }
            })


            binding.noticeTitleTv.viewTreeObserver.addOnGlobalLayoutListener(object :
                ViewTreeObserver.OnGlobalLayoutListener {
                @RequiresApi(Build.VERSION_CODES.R)
                override fun onGlobalLayout() {
                    val titleLength = item.noticeTitle.length
                    val refTitleTextSize = binding.noticeTitleTv.paint.measureText(item.noticeTitle)
                    val charSize = (refTitleTextSize / titleLength).toInt()
                    val textViewSize = binding.noticeDescTv.width
                    val maxCharsInThisTextView = textViewSize / charSize
                    Log.e(globalTag, "sizes --====-- $refTitleTextSize ?c $textViewSize")
                    if (refTitleTextSize > textViewSize) {
                        var presentInferredFontSizeInSp = 16.0f
                        var measuredText = 0.0f
                        do {
                            presentInferredFontSizeInSp -= 1.0f
                            binding.noticeTitleTv.setTextSize(
                                TypedValue.COMPLEX_UNIT_SP,
                                presentInferredFontSizeInSp
                            )
                            measuredText = binding.noticeTitleTv.paint.measureText(item.noticeTitle)
                        } while (measuredText > textViewSize && presentInferredFontSizeInSp > 10.0f)
                        Log.e(globalTag, "finally measured text is: $measuredText")
                        if (presentInferredFontSizeInSp <= 10.0f) {
                            val truncatedNoticeTitle =
                                "${item.noticeTitle.substring(0, maxCharsInThisTextView - 6)}..."
                            binding.noticeTitleTv.setTextSize(
                                TypedValue.COMPLEX_UNIT_SP,
                                15.0f
                            )
                            Log.e(globalTag, "Truncated text set is: $truncatedNoticeTitle")
                            binding.noticeTitleTv.text = truncatedNoticeTitle
                        }
                    }
                    binding.noticeTitleTv.viewTreeObserver.removeOnGlobalLayoutListener(this)
                }
            })

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoticeViewHolder {
        val inflater = LayoutInflater.from(parentContext)
        val listItemBinding = EnoticeCardLayoutBinding.inflate(inflater, parent, false)
        return NoticeViewHolder(listItemBinding)
    }

    override fun getItemCount(): Int = noticeList.size

    override fun onBindViewHolder(holder: NoticeViewHolder, position: Int) {
        holder.bind(noticeList[position], position)
    }
}