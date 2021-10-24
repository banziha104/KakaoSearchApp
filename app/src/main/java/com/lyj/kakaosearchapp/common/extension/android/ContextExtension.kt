
package com.lyj.kakaosearchapp.common.extension.android

import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes

fun Context.longToast(
    @StringRes res : Int
) = Toast.makeText(this,res, Toast.LENGTH_LONG).show()


fun Context.longToast(
    message: String
) = Toast.makeText(this,message,Toast.LENGTH_LONG).show()


fun Context.shortToast(
    @StringRes res : Int
) = Toast.makeText(this,res,Toast.LENGTH_SHORT).show()


fun Context.shortToast(
    message : String
) = Toast.makeText(this,message,Toast.LENGTH_SHORT).show()


fun Context.getStringFormat(
    @StringRes formatRes: Int,
    vararg data : Any?
) : String = String.format(getString(formatRes),*data)
