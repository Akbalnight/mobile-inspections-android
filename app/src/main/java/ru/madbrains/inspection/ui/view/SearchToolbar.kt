package ru.madbrains.inspection.ui.view

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.widget.Toolbar
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.toolbar_with_search.view.*
import ru.madbrains.inspection.R
import ru.madbrains.inspection.extensions.drawables
import ru.madbrains.inspection.extensions.hideKeyboard
import ru.madbrains.inspection.extensions.showKeyboard

class SearchToolbar : Toolbar {
    private var currentState = State.DEFAULT
    lateinit var onSearchInput: (searchString: String) -> Unit

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    fun setupSearch(
        leadingItem: Drawable,
        onSearchInput: (searchString: String) -> Unit,
        initialState: State = State.DEFAULT
    ) {
        btnLeading.setImageDrawable(leadingItem)
        this.onSearchInput = onSearchInput

        btnAction.setOnClickListener {
            changeState(if (currentState == State.DEFAULT) State.SEARCH else State.DEFAULT)
        }

        etSearch.doAfterTextChanged {
            it?.let { searchText ->
                onSearchInput(searchText.toString())
            }
        }

        if (initialState != State.DEFAULT) {
            changeState(initialState)
        }
    }

    private fun changeState(state: State) {
        currentState = state

        etSearch.isVisible = state == State.SEARCH
        tvTitle.isVisible = state == State.DEFAULT

        when (state) {
            State.DEFAULT -> {
                onSearchInput("")
                hideKeyboard()
                btnAction.setImageDrawable(context.drawables[R.drawable.ic_search])
            }
            State.SEARCH -> {
                etSearch.text.clear()
                showKeyboard(etSearch)
                btnAction.setImageDrawable(context.drawables[R.drawable.ic_close])
            }
        }
    }

    fun onNavigationBack(fragment: Fragment) {
        when (currentState) {
            State.DEFAULT -> {
                hideKeyboard()
                fragment.findNavController().popBackStack()
            }
            State.SEARCH -> {
                changeState(State.DEFAULT)
            }
        }
    }
}

enum class State {
    DEFAULT,
    SEARCH
}