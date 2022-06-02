package com.anvipus.explore.base

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.view.*
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.anvipus.explore.MainActivity
import com.anvipus.explore.R
import com.anvipus.library.util.resColor
import com.scottyab.rootbeer.RootBeer

abstract class BaseFragment: Fragment() {


    protected abstract val layoutResource: Int

    protected open val showBottomNav: Boolean = false
    protected open val showToolbar: Boolean = true
    protected open val headTitle: Int = 0
    protected open val statusBarColor: Int? = null
    protected open val showToolbarLogo: Boolean = false

    protected lateinit var defaultLifecycleObserver: DefaultLifecycleObserver

    var rootView: View? = null

    private val main: MainActivity? by lazy {
        requireActivity() as? MainActivity
    }

    override fun onStart() {
        super.onStart()
        initStatusBar(statusBarColor)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (rootView != null) {
//            rootView.removeSelf()
        }
        rootView = inflater.inflate(layoutResource, container, false)
        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)



    }

    override fun onResume() {
        super.onResume()
        initStatusBar(statusBarColor)
    }





    protected fun setupSpannable(
        fullText: String,
        actionText: String,
        color: Int? = null,
        fontId: Int? = null,
        action: () -> Unit = {}
    ): SpannableStringBuilder {
        val spannableStringBuilder = SpannableStringBuilder(fullText)
        val stringAction = object : ClickableSpan() {
            override fun onClick(widget: View) {
                action.invoke()
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
                ds.typeface = ResourcesCompat.getFont(requireContext(), fontId ?: R.font.inter_heavy)
                ds.color = resColor(color ?: R.color.colorDimGrayAlt)
            }
        }
        val endText = fullText.indexOf(actionText) + actionText.length
        spannableStringBuilder.setSpan(
            stringAction,
            fullText.indexOf(actionText),
            endText,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        return spannableStringBuilder
    }

    @SuppressLint("InlinedApi")
    @Suppress("DEPRECATION")
    protected fun initStatusBar(color: Int?) {
        if (Build.VERSION.SDK_INT >= 21) {
            val window = requireActivity().window
            if (showToolbar.not()) {
                setWindowFlag()
                window.addFlags(
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
                )
            } else {
                window.run {
                    addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                    clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
                }
            }
            window.statusBarColor = if (color != null) {
                setStatusBarAppearance(window, isLightStatusBar = true)
                requireContext().resColor(color)
            } else {
                setStatusBarAppearance(window, isLightStatusBar = false)
                Color.TRANSPARENT
            }
        }
    }

    @Suppress("DEPRECATION")
    private fun setWindowFlag(
        bits: Int = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
        on: Boolean = false
    ) {
        val win = activity?.window
        val winParams = win?.attributes
        if (on) {
            winParams?.flags = winParams!!.flags or bits
        } else {
            winParams?.flags = winParams!!.flags and bits.inv()
        }
        win.attributes = winParams
    }




    private fun setStatusBarAppearance(window: Window, isLightStatusBar: Boolean) {
        val wic = WindowInsetsControllerCompat(window, window.decorView)
        wic.isAppearanceLightStatusBars = isLightStatusBar
    }

    protected fun navigate(destination: NavDirections) = lifecycleScope.launchWhenResumed {
        with(navController()) {
            currentDestination?.getAction(destination.actionId)?.let {
                navigate(destination)
            }
        }
    }

    fun navController() = findNavController()
}