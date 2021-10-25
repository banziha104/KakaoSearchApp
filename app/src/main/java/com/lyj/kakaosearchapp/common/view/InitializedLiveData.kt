package com.lyj.kakaosearchapp.common.view

import androidx.lifecycle.*


/**
 * 초기화를 필수로 하는 LiveData 상속 클래스
 * 초기화 되었기 때문에 value 호출시 NotNull로 받아올 수 있음
 *
 * @param initialValue 초기화 값
 * @param T 초기화 데이터의 타입
 */
open class InitializedLiveData<T : Any>(initialValue : T) : LiveData<T>(initialValue){
    override fun getValue(): T {
        return super.getValue() as T
    }
}

class InitializedMutableLiveData<T : Any>(initialValue : T) : InitializedLiveData<T>(initialValue) {

    public override fun setValue(value: T) {
        super.setValue(value)
    }

    public override fun postValue(value: T) {
        super.postValue(value)
    }
}