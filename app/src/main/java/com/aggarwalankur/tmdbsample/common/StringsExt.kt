package com.aggarwalankur.tmdbsample.common

import androidx.annotation.StringRes
import com.aggarwalankur.tmdbsample.TMDBSampleApp

object Strings {
    fun get(@StringRes stringRes: Int, vararg formatArgs: Any = emptyArray()): String {
        return TMDBSampleApp.instance.getString(stringRes, *formatArgs)
    }
}