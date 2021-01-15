package ru.madbrains.inspection.base

import android.content.Context
import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleObserver
import ru.madbrains.inspection.extensions.ContextAware

abstract class BaseFragment(
    @LayoutRes layout: Int
) : Fragment(layout), ContextAware, LifecycleObserver {

    override fun getContext(): Context = super.requireActivity()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        lifecycle.addObserver(this)
    }
}