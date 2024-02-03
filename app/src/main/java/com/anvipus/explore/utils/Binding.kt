package com.anvipus.explore.utils

import android.widget.ImageView
import androidx.databinding.BindingAdapter

@BindingAdapter("app:load")
fun load(img: ImageView, url: String?) {
    if (url?.isNotEmpty() == true) img.load(url)
}