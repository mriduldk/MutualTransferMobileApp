package com.codingstudio.mutualtransfer.ui.payment

import android.app.AlertDialog
import android.content.Context


class AlertConfirmationDialog private constructor(
    private val context: Context,
    private val title: String?,
    private val message: String?,
    private val positiveButtonText: String?,
    private val hasNegativeButton: Boolean,
    private val cancelable: Boolean,
    private val negativeButtonText: String?,
    private val onConfirm: (() -> Unit)?,
    private val onCancel: (() -> Unit)?
) {

    fun show() {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(title)
        builder.setMessage(message)

        builder.setPositiveButton(positiveButtonText ?: "Ok") { dialog, _ ->
            onConfirm?.invoke()
            dialog.dismiss()
        }

        if (hasNegativeButton){
            builder.setNegativeButton(negativeButtonText ?: "Cancel") { dialog, _ ->
                onCancel?.invoke()
                dialog.dismiss()
            }
        }

        builder.setCancelable(cancelable)

        val dialog = builder.create()
        dialog.show()
    }

    class Builder(private val context: Context) {
        private var title: String? = null
        private var message: String? = null
        private var positiveButtonText: String? = null
        private var hasNegativeButton: Boolean = true
        private var cancelable: Boolean = true
        private var negativeButtonText: String? = null
        private var onConfirm: (() -> Unit)? = null
        private var onCancel: (() -> Unit)? = null

        fun setTitle(title: String) = apply { this.title = title }
        fun setMessage(message: String) = apply { this.message = message }
        fun setPositiveButtonText(text: String) = apply { this.positiveButtonText = text }
        fun setHasNegativeButton(has: Boolean) = apply { this.hasNegativeButton = has }
        fun setCancelable(cancelable: Boolean) = apply { this.cancelable = cancelable }
        fun setNegativeButtonText(text: String) = apply { this.negativeButtonText = text }
        fun onConfirm(action: () -> Unit) = apply { this.onConfirm = action }
        fun onCancel(action: () -> Unit) = apply { this.onCancel = action }

        fun build(): AlertConfirmationDialog {
            return AlertConfirmationDialog(
                context = context,
                title = title,
                message = message,
                positiveButtonText = positiveButtonText,
                hasNegativeButton = hasNegativeButton,
                cancelable = cancelable,
                negativeButtonText = negativeButtonText,
                onConfirm = onConfirm,
                onCancel = onCancel
            )
        }
    }
}