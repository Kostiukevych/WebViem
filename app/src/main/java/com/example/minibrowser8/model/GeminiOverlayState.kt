package com.example.minibrowser8.model

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.view.MotionEvent
import android.view.View
import android.webkit.CookieManager
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class GeminiOverlayState {
    var isVisible by mutableStateOf(false)
    var isMinimized by mutableStateOf(false)
    var isLoading by mutableStateOf(false)
    var progress by mutableIntStateOf(0)
    var title by mutableStateOf("Gemini AI")

    var webViewInstance: WebView? = null
        private set

    private val geminiUrl = "https://gemini.google.com"

    private val mobileChromeUserAgent =
        "Mozilla/5.0 (Linux; Android 14; Pixel 8) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/130.0.0.0 Mobile Safari/537.36"

    fun open() {
        isVisible = true
        isMinimized = false
    }

    fun close() {
        isVisible = false
    }

    fun minimize() {
        isMinimized = true
    }

    fun restore() {
        isVisible = true
        isMinimized = false
    }

    @SuppressLint("SetJavaScriptEnabled", "ClickableViewAccessibility")
    fun getOrCreateWebView(context: Context): WebView {
        if (webViewInstance != null) {
            return webViewInstance!!
        }

        val wv = WebView(context.applicationContext).apply {
            val s = settings
            s.javaScriptEnabled = true
            s.domStorageEnabled = true
            s.databaseEnabled = true
            s.useWideViewPort = true
            s.loadWithOverviewMode = true
            s.setSupportZoom(true)
            s.builtInZoomControls = false
            s.displayZoomControls = false
            s.layoutAlgorithm = WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING
            s.mediaPlaybackRequiresUserGesture = false
            s.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            s.allowFileAccess = true
            s.allowContentAccess = true
            s.javaScriptCanOpenWindowsAutomatically = true
            s.setSupportMultipleWindows(true)
            s.userAgentString = mobileChromeUserAgent

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                s.safeBrowsingEnabled = true
            }

            CookieManager.getInstance().apply {
                setAcceptCookie(true)
            }

            setOnTouchListener { v, event ->
                if (event.action == MotionEvent.ACTION_DOWN || event.action == MotionEvent.ACTION_MOVE) {
                    v.parent?.requestDisallowInterceptTouchEvent(true)
                }
                false
            }
        }

        CookieManager.getInstance().setAcceptThirdPartyCookies(wv, true)

        wv.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                this@GeminiOverlayState.isLoading = true
                this@GeminiOverlayState.progress = 15
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                this@GeminiOverlayState.isLoading = false
                this@GeminiOverlayState.progress = 100
                view?.title?.let {
                    if (it.isNotBlank()) this@GeminiOverlayState.title = it
                }
                CookieManager.getInstance().flush()
            }
        }

        wv.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                this@GeminiOverlayState.progress = newProgress
                if (newProgress == 100) {
                    this@GeminiOverlayState.isLoading = false
                }
            }
        }

        wv.loadUrl(geminiUrl)
        webViewInstance = wv
        return wv
    }

    fun reload() {
        webViewInstance?.reload()
    }

    fun destroy() {
        webViewInstance?.let { wv ->
            wv.stopLoading()
            wv.loadUrl("about:blank")
            wv.clearHistory()
            wv.destroy()
        }
        webViewInstance = null
    }
}
