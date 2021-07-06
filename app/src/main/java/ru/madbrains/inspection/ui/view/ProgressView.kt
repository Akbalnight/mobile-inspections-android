package ru.madbrains.inspection.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import kotlinx.android.synthetic.main.view_progress.view.*
import ru.madbrains.inspection.R

class ProgressView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    init {
        LayoutInflater.from(context)
            .inflate(R.layout.view_progress, this, true)
    }

    fun changeVisibility(visibility: Boolean) {
        viewLockContent.isVisible = visibility
        progress.isVisible = visibility
    }

    fun changeTextVisibility(visibility: Boolean) {
        progressText.isVisible = visibility
    }

    fun setTextButton(text: String, call: () -> Unit) {
        progressText.text = text
        progressText.setOnClickListener { call() }
    }

    fun changeVisibility(visibility: Boolean, resId: Int?) {
        viewLockContent.isVisible = visibility
        progress.isVisible = visibility
        if (resId != null) {
            progressText.setText(resId)
            progressText.isVisible = true
        } else {
            progressText.isVisible = false
        }
    }
}