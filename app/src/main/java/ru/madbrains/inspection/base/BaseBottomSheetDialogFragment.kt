package ru.madbrains.inspection.base

import android.app.Dialog
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.LayoutRes
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import ru.madbrains.inspection.extensions.ContextAware

abstract class BaseBottomSheetDialogFragment(@LayoutRes val layout: Int) :
    BottomSheetDialogFragment(), ContextAware {

    // expand layout when create
    open val isExpanded: Boolean = false

    override fun getContext(): Context = super.requireActivity()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(layout, container, false)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val bottomSheetDialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        bottomSheetDialog.setOnShowListener {
            val dialog = it as BottomSheetDialog
            val layout =
                dialog.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)
            val isLandscape = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
            val state =
                if (isExpanded || isLandscape) BottomSheetBehavior.STATE_EXPANDED else BottomSheetBehavior.STATE_COLLAPSED
            val bottomSheetBehavior = BottomSheetBehavior.from<FrameLayout?>(layout!!)

            bottomSheetBehavior.state = state
            bottomSheetBehavior.addBottomSheetCallback(object :
                BottomSheetBehavior.BottomSheetCallback() {

                override fun onSlide(bottomSheet: View, slideOffset: Float) {}

                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    if (newState == BottomSheetBehavior.STATE_COLLAPSED && isExpanded) {
                        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                    }
                }
            })
        }
        return bottomSheetDialog
    }

    fun expandLayout() {
        dialog?.let {
            val layout =
                it.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)
            BottomSheetBehavior.from<FrameLayout?>(layout).state =
                BottomSheetBehavior.STATE_EXPANDED
        }
    }
}