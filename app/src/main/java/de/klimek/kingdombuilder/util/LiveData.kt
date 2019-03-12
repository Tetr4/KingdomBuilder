package de.klimek.kingdombuilder.util

import androidx.lifecycle.*

fun <T : Any> mutableLiveDataOf(initialValue: T) = MutableLiveData<T>().apply { setValue(initialValue) }

fun <T, R> LiveData<T>.map(func: (T) -> R) = Transformations.map(this, func)

fun <T> LiveData<T>.distinct() = MediatorLiveData<T>().apply {
    addSource(this@distinct) {
        if (it != this.value) {
            value = it
        }
    }
}

fun <A, B, R> LiveData<A>.combineWith(other: LiveData<B>, combine: (A, B) -> R) = MediatorLiveData<R>().apply {
    val onChanged = Observer<Any?> {
        val a: A? = this@combineWith.value
        val b: B? = other.value
        if (a != null && b != null) {
            value = combine(a, b)
        }
    }
    addSource(this@combineWith, onChanged)
    addSource(other, onChanged)
}

fun <T> LiveData<T>.observe(owner: LifecycleOwner, observer: (T) -> Unit) = observe(owner, Observer(observer))

fun <T> LiveData<T>.observeOnce(observer: (T) -> Unit) {
    observeForever(object : Observer<T> {
        override fun onChanged(t: T) {
            observer(t)
            removeObserver(this)
        }
    })
}