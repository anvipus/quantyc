package com.anvipus.explore.utils

import android.app.Activity
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat

fun Activity.closeKeyboard() {
    var focus = currentFocus
    if (focus == null) {
        focus = View(this)
    }

    val imm = ContextCompat.getSystemService(this, InputMethodManager::class.java)
    imm?.hideSoftInputFromWindow(focus.windowToken, 0)
}
fun View.show() {
    this.visibility = View.VISIBLE
}

fun View.hide(type: Int = 1) {
    this.visibility = if (type == 1) View.GONE else View.INVISIBLE
}
fun View.isShowing(): Boolean = visibility == View.VISIBLE

fun View.showIf(show: Boolean?, type: Int = 1) {
    if (show == true) {
        show()
    } else {
        hide(type)
    }
}