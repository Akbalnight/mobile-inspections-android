package ru.madbrains.inspection.ui.main.routes.points

import android.content.Context
import android.content.Intent
import ru.madbrains.domain.model.DetourModel
import ru.madbrains.inspection.R
import ru.madbrains.inspection.base.BaseActivity
import ru.madbrains.inspection.ui.main.routes.points.RoutePointsFragment.Companion.KEY_DETOUR

class RoutePointsActivity : BaseActivity(R.layout.activity_route_points) {
    companion object {
        fun start(context: Context, route: DetourModel) {
            val intent = Intent(context, RoutePointsActivity::class.java)
            intent.putExtra(KEY_DETOUR, route)
            context.startActivity(intent)
        }
    }
}