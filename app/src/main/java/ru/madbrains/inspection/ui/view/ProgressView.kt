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
}