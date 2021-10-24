package com.lyj.kakaosearchapp.common.extension.android

import android.view.inputmethod.EditorInfo
import android.widget.EditText
import io.reactivex.rxjava3.core.Observable

fun EditText.searchButtonActionObserver(): Observable<Unit> =
    Observable.create<Unit> { emiiter ->
        setOnEditorActionListener { textView, actionId, keyEvent ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                emiiter.onNext(Unit)
                true
            } else {
                false
            }
        }
    }.doOnDispose { setOnEditorActionListener(null) }

