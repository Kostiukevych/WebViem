package com.example.minibrowser8.model

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Message
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.GeolocationPermissions
import android.webkit.PermissionRequest
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class BrowserSlotState(
    val id: Int,
    val initialUrl: String = ""
) {
    var inputUrl by mutableStateOf(initialUrl)
    var currentUrl by mutableStateOf(initialUrl)
    var title by mutableStateOf("Slot #${id + 1}")
    var progress by mutableIntStateOf(0)
    var isLoading by mutableStateOf(false)
    var canGoBack by mutableStateOf(false)
    var canGoForward by mutableStateOf(false)
    var isDesktopMode by mutableStateOf(false)
    
    // HTML5 Video custom full-screen view (e.g. YouTube player fullscreen)
    var customView by mutableStateOf<View?>(null)
    private var customViewCallback: WebChromeClient.CustomViewCallback? = null

    var webViewInstance: WebView? = null
        private set

    // Clean Mobile Chrome User-Agent (WITHOUT "Version/4.0" or "; wv" tokens which cause Instagram/Discord to block mobile browsers)
    private val mobileChromeUserAgent =
        "Mozilla/5.0 (Linux; Android 14; Pixel 8) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/130.0.0.0 Mobile Safari/537.36"
    private val desktopChromeUserAgent =
        "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/130.0.0.0 Safari/537.36"

    @SuppressLint("SetJavaScriptEnabled", "ClickableViewAccessibility")
    fun getOrCreateWebView(context: Context): WebView {
        if (webViewInstance != null) {
            val existing = webViewInstance!!
            (existing.parent as? ViewGroup)?.removeView(existing)
            return existing
        }

        val wv = WebView(context).apply {
            isFocusable = true
            isFocusableInTouchMode = true

            val s = settings
            s.javaScriptEnabled = true
            s.domStorageEnabled = true
            s.databaseEnabled = true
            s.useWideViewPort = true
            s.loadWithOverviewMode = true
            s.setSupportZoom(true)
            s.builtInZoomControls = true
            s.displayZoomControls = false
            s.layoutAlgorithm = WebSettings.LayoutAlgorithm.NORMAL
            s.textZoom = 100
            
            // Media, Audio & WebRTC Playback settings
            s.mediaPlaybackRequiresUserGesture = false
            s.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            s.allowFileAccess = true
            s.allowContentAccess = true
            s.javaScriptCanOpenWindowsAutomatically = true
            s.setSupportMultipleWindows(true)
            
            s.cacheMode = WebSettings.LOAD_DEFAULT
            s.setGeolocationEnabled(true)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                s.safeBrowsingEnabled = true
            }

            // Set genuine mobile/desktop Chrome user-agent
            s.userAgentString = if (isDesktopMode) desktopChromeUserAgent else mobileChromeUserAgent

            CookieManager.getInstance().apply {
                setAcceptCookie(true)
            }

            isVerticalScrollBarEnabled = true
            isHorizontalScrollBarEnabled = false

            // Touch listener to handle internal scrolling smoothly without parent stealing
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
                this@BrowserSlotState.isLoading = true
                this@BrowserSlotState.progress = 10
                url?.let {
                    this@BrowserSlotState.currentUrl = it
                    this@BrowserSlotState.inputUrl = it
                }
                this@BrowserSlotState.canGoBack = view?.canGoBack() == true
                this@BrowserSlotState.canGoForward = view?.canGoForward() == true
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                this@BrowserSlotState.isLoading = false
                this@BrowserSlotState.progress = 100
                url?.let {
                    this@BrowserSlotState.currentUrl = it
                    this@BrowserSlotState.inputUrl = it
                }
                view?.title?.let {
                    if (it.isNotBlank()) this@BrowserSlotState.title = it
                }
                this@BrowserSlotState.canGoBack = view?.canGoBack() == true
                this@BrowserSlotState.canGoForward = view?.canGoForward() == true
                CookieManager.getInstance().flush()
            }

            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                val uri = request?.url ?: return false
                val urlString = uri.toString()

                // Standard web protocols continue inside this WebView slot
                if (urlString.startsWith("http://") || urlString.startsWith("https://")) {
                    return false
                }

                // Handle intent://, instagram://, discord://, mailto:, tel: without crash/error screen
                return try {
                    val intent = Intent.parseUri(urlString, Intent.URI_INTENT_SCHEME)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(intent)
                    true
                } catch (_: Exception) {
                    true
                }
            }
        }

        wv.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                this@BrowserSlotState.progress = newProgress
                if (newProgress == 100) {
                    this@BrowserSlotState.isLoading = false
                }
                this@BrowserSlotState.canGoBack = view?.canGoBack() == true
                this@BrowserSlotState.canGoForward = view?.canGoForward() == true
            }

            override fun onReceivedTitle(view: WebView?, newTitle: String?) {
                super.onReceivedTitle(view, newTitle)
                if (!newTitle.isNullOrBlank()) {
                    this@BrowserSlotState.title = newTitle
                }
            }

            // WebRTC / Microphone / Camera permissions for Discord & Instagram voice/video
            override fun onPermissionRequest(request: PermissionRequest?) {
                try {
                    request?.grant(request.resources)
                } catch (_: Exception) {}
            }

            override fun onGeolocationPermissionsShowPrompt(
                origin: String?,
                callback: GeolocationPermissions.Callback?
            ) {
                callback?.invoke(origin, true, false)
            }

            // Handle popups / OAuth windows (e.g. login with Google in Discord/Instagram)
            override fun onCreateWindow(
                view: WebView?,
                isDialog: Boolean,
                isUserGesture: Boolean,
                resultMsg: Message?
            ): Boolean {
                val newWebView = WebView(context).apply {
                    settings.javaScriptEnabled = true
                    settings.userAgentString = wv.settings.userAgentString
                    webViewClient = object : WebViewClient() {
                        override fun shouldOverrideUrlLoading(
                            v: WebView?,
                            req: WebResourceRequest?
                        ): Boolean {
                            req?.url?.let { targetUri ->
                                wv.loadUrl(targetUri.toString())
                            }
                            return true
                        }
                    }
                }
                val transport = resultMsg?.obj as? WebView.WebViewTransport
                transport?.webView = newWebView
                resultMsg?.sendToTarget()
                return true
            }

            // HTML5 Video Fullscreen callbacks (e.g. YouTube player fullscreen)
            override fun onShowCustomView(view: View?, callback: CustomViewCallback?) {
                super.onShowCustomView(view, callback)
                this@BrowserSlotState.customView = view
                this@BrowserSlotState.customViewCallback = callback
            }

            override fun onHideCustomView() {
                super.onHideCustomView()
                this@BrowserSlotState.customViewCallback?.onCustomViewHidden()
                this@BrowserSlotState.customView = null
                this@BrowserSlotState.customViewCallback = null
            }
        }

        if (initialUrl.isNotBlank()) {
            wv.loadUrl(formatUrl(initialUrl))
        }

        webViewInstance = wv
        return wv
    }

    fun load(rawUrl: String) {
        val formatted = formatUrl(rawUrl)
        if (formatted.isNotBlank()) {
            inputUrl = formatted
            webViewInstance?.loadUrl(formatted)
        }
    }

    fun reload() {
        webViewInstance?.reload()
    }

    fun stop() {
        webViewInstance?.stopLoading()
        isLoading = false
    }

    fun goBack(): Boolean {
        if (customView != null) {
            customViewCallback?.onCustomViewHidden()
            customView = null
            customViewCallback = null
            return true
        }
        val wv = webViewInstance
        if (wv?.canGoBack() == true) {
            wv.goBack()
            canGoBack = wv.canGoBack()
            canGoForward = wv.canGoForward()
            return true
        }
        return false
    }

    fun goForward(): Boolean {
        val wv = webViewInstance
        if (wv?.canGoForward() == true) {
            wv.goForward()
            canGoBack = wv.canGoBack()
            canGoForward = wv.canGoForward()
            return true
        }
        return false
    }

    fun toggleDesktopMode() {
        val wv = webViewInstance ?: return
        isDesktopMode = !isDesktopMode
        wv.settings.userAgentString = if (isDesktopMode) desktopChromeUserAgent else mobileChromeUserAgent
        wv.reload()
    }

    fun hideCustomVideoView() {
        customViewCallback?.onCustomViewHidden()
        customView = null
        customViewCallback = null
    }

    fun destroy() {
        hideCustomVideoView()
        webViewInstance?.let { wv ->
            wv.stopLoading()
            wv.loadUrl("about:blank")
            wv.clearHistory()
            wv.destroy()
        }
        webViewInstance = null
    }

    companion object {
        fun formatUrl(input: String): String {
            val trimmed = input.trim()
            if (trimmed.isEmpty()) return ""
            if (trimmed.startsWith("http://") || trimmed.startsWith("https://")) {
                return trimmed
            }
            return if (trimmed.contains(".") && !trimmed.contains(" ")) {
                "https://$trimmed"
            } else {
                val query = Uri.encode(trimmed)
                "https://www.google.com/search?q=$query"
            }
        }
    }
}
