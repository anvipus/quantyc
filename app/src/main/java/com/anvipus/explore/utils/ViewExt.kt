package com.anvipus.explore.utils

import android.app.Activity
import android.graphics.drawable.Drawable
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.anvipus.explore.R
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.bumptech.glide.signature.ObjectKey
import com.google.android.material.imageview.ShapeableImageView

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

fun RecyclerView.linear() {
    this.layoutManager = LinearLayoutManager(context)
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

fun ShapeableImageView.load(url: String?, placeholder: Int? = null, success: ((Boolean) -> Unit)? = null) {
    GlideApp.with(context).load(url)
        .placeholder(placeholder ?: R.drawable.ic_placeholder)
        .error(placeholder ?: R.drawable.ic_placeholder)
        .listener(object : RequestListener<Drawable> {
            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<Drawable>?,
                isFirstResource: Boolean
            ): Boolean {
                success?.invoke(false)
                return false
            }

            override fun onResourceReady(
                resource: Drawable?,
                model: Any?,
                target: Target<Drawable>?,
                dataSource: DataSource?,
                isFirstResource: Boolean
            ): Boolean {

                success?.invoke(true)
                return false
            }
        }).into(this)
}

fun ShapeableImageView.loadV2(url: String?, success: ((Boolean) -> Unit)? = null) {
    GlideApp.with(context).load(url)
        .placeholder(R.drawable.ic_placeholder)
        .error(R.drawable.ic_placeholder)
        .listener(object : RequestListener<Drawable> {
            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<Drawable>?,
                isFirstResource: Boolean
            ): Boolean {
                success?.invoke(false)
                return false
            }

            override fun onResourceReady(
                resource: Drawable?,
                model: Any?,
                target: Target<Drawable>?,
                dataSource: DataSource?,
                isFirstResource: Boolean
            ): Boolean {

                success?.invoke(true)
                return false
            }
        }).into(this)
}

fun ShapeableImageView.loadImageFromAsset(
    url: Int?,
    placeholder: Int? = null,
    success: ((Boolean) -> Unit)? = null
) {
    GlideApp.with(context).load(url)
        .placeholder(placeholder ?: R.drawable.ic_placeholder)
        .error(placeholder ?: R.drawable.ic_placeholder)
        .listener(object : RequestListener<Drawable> {
            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<Drawable>?,
                isFirstResource: Boolean
            ): Boolean {
                success?.invoke(false)
                return false
            }

            override fun onResourceReady(
                resource: Drawable?,
                model: Any?,
                target: Target<Drawable>?,
                dataSource: DataSource?,
                isFirstResource: Boolean
            ): Boolean {

                success?.invoke(true)
                return false
            }
        }).into(this)
}
