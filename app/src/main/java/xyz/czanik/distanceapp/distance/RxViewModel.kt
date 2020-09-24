package xyz.czanik.distanceapp.distance

import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.kotlin.addTo

abstract class RxViewModel : ViewModel() {

    private val disposables = CompositeDisposable()

    override fun onCleared() = disposables.dispose()

    protected fun handleError(error: Throwable) = println("PrintingError $error")

    protected fun Disposable.manage() = addTo(disposables)
}