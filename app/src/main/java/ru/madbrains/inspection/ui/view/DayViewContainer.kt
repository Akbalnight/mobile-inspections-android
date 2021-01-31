package ru.madbrains.inspection.ui.view

import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.kizitonwose.calendarview.ui.ViewContainer
import ru.madbrains.inspection.R

class DayViewContainer(view: View) : ViewContainer(view) {
    val tvDay: TextView = view.findViewById(R.id.tvDay)
    val haveRoutesDot: View = view.findViewById(R.id.haveRoutesDot)
    val clDate: ConstraintLayout = view.findViewById(R.id.clDate)
}