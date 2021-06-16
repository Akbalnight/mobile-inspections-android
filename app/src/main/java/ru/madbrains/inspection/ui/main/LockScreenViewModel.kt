package ru.madbrains.inspection.ui.main

import android.webkit.CookieManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import retrofit2.HttpException
import ru.madbrains.data.prefs.PreferenceStorage
import ru.madbrains.domain.interactor.AuthInteractor
import ru.madbrains.domain.interactor.DetoursInteractor
import ru.madbrains.inspection.base.BaseViewModel
import ru.madbrains.inspection.base.Event
import timber.log.Timber

class LockScreenViewModel(
    private val preferenceStorage: PreferenceStorage,
    private val authInteractor: AuthInteractor
) : BaseViewModel() {

}