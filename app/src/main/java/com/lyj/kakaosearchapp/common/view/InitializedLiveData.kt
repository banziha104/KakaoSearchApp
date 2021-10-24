package com.lyj.kakaosearchapp.common.view

import androidx.lifecycle.*

class InitializedMutableLiveData<T : Any>(initialValue : T) : InitializedLiveData<T>(initialValue) {

    public override fun setValue(value: T) {
        super.setValue(value)
    }

    public override fun postValue(value: T) {
        super.postValue(value)
    }
}

open class InitializedLiveData<T : Any>(initialValue : T) : LiveData<T>(initialValue){
    override fun getValue(): T {
        return super.getValue() as T
    }

    inline fun <R : Any> map(crossinline mapper : (T) -> R) : InitializedLiveData<R>{
        return InitializedLiveData(mapper(value))
    }
}