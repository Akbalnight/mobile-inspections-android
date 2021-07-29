package ru.madbrains.inspection.ui.main.routes.routecalendar

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isInvisible
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.kizitonwose.calendarview.model.CalendarDay
import com.kizitonwose.calendarview.model.DayOwner
import com.kizitonwose.calendarview.ui.DayBinder
import com.kizitonwose.calendarview.utils.next
import com.kizitonwose.calendarview.utils.previous
import kotlinx.android.synthetic.main.fragment_route_calendar.*
import kotlinx.android.synthetic.main.view_calendar_month_header.view.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.threeten.bp.YearMonth
import org.threeten.bp.temporal.WeekFields
import ru.madbrains.data.extensions.toYYYYMMDD
import ru.madbrains.inspection.R
import ru.madbrains.inspection.base.BaseFragment
import ru.madbrains.inspection.base.EventObserver
import ru.madbrains.inspection.extensions.colors
import ru.madbrains.inspection.extensions.drawables
import ru.madbrains.inspection.extensions.stringArrays
import ru.madbrains.inspection.ui.delegates.DetourUiModel
import ru.madbrains.inspection.ui.main.routes.DetoursViewModel
import ru.madbrains.inspection.ui.main.routes.dateroutelist.DateRouteListFragment
import ru.madbrains.inspection.ui.view.DayViewContainer
import java.util.*

@SuppressLint("SetTextI18n", "DefaultLocale")
class RouteCalendarFragment : BaseFragment(R.layout.fragment_route_calendar) {

    private val routeCalendarViewModel: RouteCalendarViewModel by viewModel()
    private val detoursViewModel: DetoursViewModel by sharedViewModel()

    private var nextMonth: YearMonth? = null
    private var previousMonth: YearMonth? = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setupCalendar()

        detoursViewModel.detours.observe(viewLifecycleOwner, Observer {
            routeCalendarViewModel.updateRouteDates(it.filterIsInstance<DetourUiModel>())
            calendarView.notifyCalendarChanged()
        })
        routeCalendarViewModel.navigateToDateRouteList.observe(
            viewLifecycleOwner,
            EventObserver { date ->
                openDateRouteList(date)
            })
    }

    private fun setupCalendar() {
        calendarView.dayBinder = object : DayBinder<DayViewContainer> {
            override fun create(view: View) = DayViewContainer(view)

            override fun bind(container: DayViewContainer, day: CalendarDay) {
                container.view.isInvisible = day.owner != DayOwner.THIS_MONTH
                container.tvDay.text = day.date.dayOfMonth.toString()
                container.haveRoutesDot.isInvisible =
                    !routeCalendarViewModel.routeDates.contains(day.date)

                val timeNow = Calendar.getInstance().time
                val dateNow: String = timeNow.toYYYYMMDD()

                if (day.date.toString() == dateNow) {
                    container.clDate.background = drawables[R.drawable.light_blue_circle]
                    container.tvDay.setTextColor(colors[R.color.textWhite])
                } else {
                    container.clDate.background = null
                    container.tvDay.setTextColor(colors[R.color.textMain])
                }

                container.view.setOnClickListener {
                    routeCalendarViewModel.dateClick(day.date)
                }
            }
        }

        calendarView.monthScrollListener = { month ->
            nextMonth = month.yearMonth.next
            previousMonth = month.yearMonth.previous
            val months = stringArrays[R.array.months]
            header.tvMonth.text = "${months[month.month - 1]} ${month.year}"
        }

        header.btnMonthLeft.setOnClickListener {
            calendarView.smoothScrollToMonth(previousMonth!!)
        }

        header.btnMonthRight.setOnClickListener {
            calendarView.smoothScrollToMonth(nextMonth!!)
        }

        val currentMonth = YearMonth.now()
        val firstMonth = currentMonth.minusMonths(5)
        val lastMonth = currentMonth.plusMonths(5)
        val firstDayOfWeek = WeekFields.of(Locale.getDefault()).firstDayOfWeek
        calendarView.setup(firstMonth, lastMonth, firstDayOfWeek)
        calendarView.scrollToMonth(currentMonth)
    }

    private fun openDateRouteList(date: Date) {
        val args = bundleOf(
            DateRouteListFragment.KEY_TOOLBAR_ARG to date
        )
        findNavController().navigate(R.id.action_DetoursFragment_to_dateRouteListFragment, args)
    }
}