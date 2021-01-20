package org.tahoe.lafs.network.base

import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import java.util.concurrent.atomic.AtomicBoolean

class SingleLiveEvent<T> : MutableLiveData<T>() {

    private val mPending = AtomicBoolean(false)
    private val observersOwnerPair = mutableSetOf<Pair<LifecycleOwner, Observer<in T>>>()

    private val internalObserver = Observer<T> { t ->
        if (mPending.compareAndSet(true, false)) {
            observersOwnerPair.forEach { observerOwnerPair ->
                if (observerOwnerPair.first.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED))
                    observerOwnerPair.second.onChanged(t)
            }
        }
    }

    @MainThread
    override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
        if (observersOwnerPair.isEmpty()) {
            super.observeForever(internalObserver)
        }
        observersOwnerPair.add(Pair(owner, observer))
    }

    override fun removeObservers(owner: LifecycleOwner) {
        observersOwnerPair.clear()
        super.removeObservers(owner)
        super.removeObserver(internalObserver)
    }

    override fun removeObserver(observer: Observer<in T>) {
        observersOwnerPair.remove(observersOwnerPair.find { it.second == observer })
        if (observersOwnerPair.isEmpty())
            super.removeObserver(internalObserver)
    }

    @MainThread
    override fun setValue(t: T?) {
        mPending.set(true)
        super.setValue(t)
    }

    @WorkerThread
    override fun postValue(value: T) {
        mPending.set(true)
        super.postValue(value)
    }

    /**
     * Used for cases where T is Void, to make calls cleaner.
     */
    @MainThread
    fun call() {
        value = null
    }

    companion object {

        private val TAG = "SingleLiveEvent"
    }
}