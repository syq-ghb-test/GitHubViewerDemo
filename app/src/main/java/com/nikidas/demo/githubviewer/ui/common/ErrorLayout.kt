package com.nikidas.demo.githubviewer.ui.common

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

class ErrorLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {
    private val errorText: TextView
    private val retryButton: Button

    init {
        orientation = VERTICAL
        gravity = Gravity.CENTER
        errorText = TextView(context).apply {
            setTextColor(Color.RED)
            textSize = 16f
            gravity = Gravity.CENTER
        }
        retryButton = Button(context).apply {
            text = "重试"
            setBackgroundColor(Color.parseColor("#FF9800"))
            setTextColor(Color.WHITE)
            layoutParams = LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT
            ).apply { topMargin = 32 }
        }
        addView(errorText)
        addView(retryButton)
    }

    fun setErrorText(msg: String) {
        errorText.text = msg
    }

    fun setOnRetryClickListener(listener: OnClickListener) {
        retryButton.setOnClickListener(listener)
    }
} 