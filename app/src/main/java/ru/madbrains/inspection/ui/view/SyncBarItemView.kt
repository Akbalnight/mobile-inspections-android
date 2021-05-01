package ru.madbrains.inspection.ui.view

import android.content.Context
import android.util.AttributeSet
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.item_sync_panel_layout.view.*
import ru.madbrains.inspection.R
import ru.madbrains.inspection.base.ProgressState

class SyncBarItemView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    init {
        inflate(getContext(), R.layout.item_sync_panel_layout, this)
        setupAttrs(context, attrs)
    }

    private fun setupAttrs(context: Context, attrs: AttributeSet?) {
        attrs?.let {
            val typedArray = context.theme.obtainStyledAttributes(
                attrs,
                R.styleable.syncBarItemView,
                0, 0
            )
            setIcon(typedArray.getResourceId(R.styleable.syncBarItemView_iconValue, 0))
            setText(typedArray.getResourceId(R.styleable.syncBarItemView_textValue, 0))
            typedArray.recycle()
        }
    }

    private fun setIcon(@DrawableRes id: Int) {
        item_icon.setImageDrawable(
            if (id == 0) null else
                ContextCompat.getDrawable(context, id)
        )
    }


    private fun setText(@StringRes text: Int) {
        item_text.setText(text)
    }

    fun setState(state: ProgressState) {
        val icon = when (state) {
            ProgressState.PROGRESS -> R.drawable.ic_sync_progress
            ProgressState.FAILED -> R.drawable.ic_sync_failed
            ProgressState.DONE -> R.drawable.ic_sync_done
            ProgressState.NONE -> 0
        }
        setIcon(icon)
    }
}