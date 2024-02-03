package com.anvipus.library.util

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.core.os.bundleOf
import com.bumptech.glide.Glide
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.anvipus.library.R
import com.anvipus.library.databinding.LayoutBaseProgressDialogBinding

class ProgressDialog : DialogFragment() {

    companion object {
        private const val EXTRA_IS_CANCELABLE: String = "isCancelable"
        private const val EXTRA_HAS_NAV_CONTROLLER: String = "hasNavController"
        private const val EXTRA_IS_TRANSACTION: String = "isTransaction"

        const val TAG: String = "ProgressDialog"

        fun newInstance(
            isCancelable: Boolean,
            hasNavController: Boolean,
            isTransaction: Boolean) : ProgressDialog {
            val progressDialog = ProgressDialog()
            return progressDialog.apply {
                arguments = bundleOf().apply {
                    putBoolean(EXTRA_IS_CANCELABLE, isCancelable)
                    putBoolean(EXTRA_HAS_NAV_CONTROLLER, hasNavController)
                    putBoolean(EXTRA_IS_TRANSACTION, isTransaction)
                }
            }
        }
    }

    private lateinit var binding: LayoutBaseProgressDialogBinding

    private var mIsCancelable: Boolean = true
    private var hasNavController: Boolean = true
    private var isTransaction: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            isTransaction = it.getBoolean(EXTRA_IS_TRANSACTION)
            hasNavController = it.getBoolean(EXTRA_HAS_NAV_CONTROLLER)
            mIsCancelable = it.getBoolean(EXTRA_IS_CANCELABLE)
        }
        if (isTransaction) {
            setStyle(STYLE_NO_FRAME, R.style.FullScreenDialog)
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return object : Dialog(requireContext(), theme) {
            override fun onBackPressed() {
                if (mIsCancelable) {
                    if (hasNavController) {
                        findNavController().navigateUp()
                    } else {
                        requireActivity().finish()
                    }
                }
            }
        }.apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.layout_base_progress_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = LayoutBaseProgressDialogBinding.bind(view)
        showContent(isTransaction)
    }

    override fun onStart() {
        super.onStart()
        initView()
    }

    private fun initView() {
        val window = dialog?.window!!
        if (isTransaction) {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            val decorView = window.decorView
            window.setLayout(width, height)
            window.statusBarColor = resColor(R.color.colorWhite)
            val wic = WindowInsetsControllerCompat(window, decorView)
            wic.isAppearanceLightStatusBars = true
        } else {
            window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }

    private fun showContent(isTransaction: Boolean) {
        with(binding) {
            if (isTransaction) {
                commonProgressDialog.layoutParentCommonProgressDialog.visibility = View.GONE
                transactionProgressDialog.layoutParentProgressDialogTransaction.visibility = View.VISIBLE
                Glide.with(requireContext())
                    .load(R.raw.motionplay_flying_loading)
                    .into(transactionProgressDialog.ivLoading)
            } else {
                commonProgressDialog.layoutParentCommonProgressDialog.visibility = View.VISIBLE
                transactionProgressDialog.layoutParentProgressDialogTransaction.visibility = View.GONE
            }
        }
    }

}