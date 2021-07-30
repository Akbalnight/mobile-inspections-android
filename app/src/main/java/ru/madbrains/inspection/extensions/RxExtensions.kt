package ru.madbrains.inspection.extensions

import androidx.lifecycle.MutableLiveData
import io.reactivex.Completable
import io.reactivex.Single
import ru.madbrains.inspection.base.Event
import ru.madbrains.inspection.base.ProgressState

fun <T> Single<T>.changeProgressWith(mutableLiveData: MutableLiveData<Event<ProgressState>>): Single<T> {
    return doOnSubscribe { mutableLiveData.postValue(Event(ProgressState.PROGRESS)) }
        .doOnError { mutableLiveData.postValue(Event(ProgressState.FAILED)) }
        .doOnSuccess { mutableLiveData.postValue(Event(ProgressState.DONE)) }
}

fun Completable.changeProgressWith(mutableLiveData: MutableLiveData<Boolean>): Completable {
    return doOnSubscribe { mutableLiveData.postValue(true) }
        .doAfterTerminate { mutableLiveData.postValue(false) }
}

fun <T> Single<T>.changeProgressWithB(mutableLiveData: MutableLiveData<Boolean>): Single<T> {
    return doOnSubscribe { mutableLiveData.postValue(true) }
        .doAfterTerminate { mutableLiveData.postValue(false) }
}
