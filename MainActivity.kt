package com.linkgo.app

import android.app.Activity
import android.os.Bundle
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.net.Uri
import android.graphics.Color
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView

class MainActivity : Activity() {

    private lateinit var linkInput: EditText
    private lateinit var statusText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(36, 48, 36, 32)
            setBackgroundColor(Color.rgb(15, 23, 42))
        }

        fun addText(text: String, size: Float, color: Int): TextView {
            return TextView(this).apply {
                this.text = text
                textSize = size
                setTextColor(color)
                root.addView(
                    this,
                    ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                )
            }
        }

        addText("LinkGo", 30f, Color.WHITE)
        addText("Universal Link Resolver", 15f, Color.LTGRAY)

        linkInput = EditText(this).apply {
            hint = "Tempel link di sini..."
            textSize = 16f
            setTextColor(Color.BLACK)
            setHintTextColor(Color.GRAY)
            setPadding(18, 12, 18, 12)
            setSingleLine(false)
            minLines = 2
            maxLines = 4
            setBackgroundColor(Color.WHITE)
        }

        val inputParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply {
            topMargin = 32
        }
        root.addView(linkInput, inputParams)

        root.addView(Button(this).apply {
            text = "Tempel Link"
            setOnClickListener { pasteFromClipboard() }
        })

        root.addView(Button(this).apply {
            text = "Periksa Link"
            setOnClickListener { validateLink() }
        })

        statusText = addText(
            "Siap. Masukkan link untuk memulai.",
            15f,
            Color.WHITE
        )

        (statusText.layoutParams as? LinearLayout.LayoutParams)?.let {
            it.topMargin = 24
            statusText.layoutParams = it
        }

        setContentView(root)
    }

    private fun pasteFromClipboard() {
        val clipboard = getSystemService(
            Context.CLIPBOARD_SERVICE
        ) as ClipboardManager

        val item: ClipData.Item? = clipboard.primaryClip?.getItemAt(0)
        val text = item?.coerceToText(this)?.toString()

        if (!text.isNullOrBlank()) {
            linkInput.setText(text.trim())
            statusText.text = "Link berhasil ditempel."
        } else {
            statusText.text = "Clipboard belum berisi teks."
        }
    }

    private fun validateLink() {
        val raw = linkInput.text.toString().trim()
        val uri = runCatching { Uri.parse(raw) }.getOrNull()

        if (
            uri == null ||
            uri.scheme !in listOf("https", "http") ||
            uri.host.isNullOrBlank() ||
            uri.userInfo != null
        ) {
            statusText.text =
                "Link tidak valid. Gunakan URL HTTP atau HTTPS."
            return
        }

        statusText.text =
            "Format URL valid. Resolver tujuan belum diaktifkan."
    }
}
