package com.example.minibrowser8

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Bundle
import android.text.InputType
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.webkit.CookieManager
import android.webkit.GeolocationPermissions
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.GridLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.widget.NestedScrollView

/**
 * One browser "slot": its WebView plus the small header wrapped around it
 * (url field, reload / open-externally / fullscreen buttons).
 */
class BrowserSlot(val index: Int) {
    lateinit var card: LinearLayout
    lateinit var webView: WebView
    lateinit var urlInput: EditText
    lateinit var fullscreenButton: Button
}

class MainActivity : Activity() {

    private val slots = mutableListOf<BrowserSlot>()
    private var fullscreenSlot: BrowserSlot? = null

    private lateinit var rootFrame: FrameLayout
    private lateinit var mainContent: LinearLayout
    private lateinit var fullscreenContainer: FrameLayout
    private lateinit var grid: GridLayout

    // Height of each card when shown inside the scrollable grid.
    // Big enough to actually browse a feed, not just a thin strip.
    private val cardHeightDp = 460

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        CookieManager.getInstance().setAcceptCookie(true)
        buildUi()
    }

    // ---------------------------------------------------------------------
    // UI construction (built entirely in code)
    // ---------------------------------------------------------------------

    @SuppressLint("SetJavaScriptEnabled")
    private fun buildUi() {
        rootFrame = FrameLayout(this).apply {
            setBackgroundColor(Color.rgb(8, 8, 8))
        }

        mainContent = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        }

        mainContent.addView(buildTopBar())

        val scrollView = NestedScrollView(this).apply {
            isFillViewport = true
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
        }

        grid = GridLayout(this).apply {
            columnCount = 2
            setPadding(dp(4), dp(4), dp(4), dp(4))
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            )
        }
        scrollView.addView(grid)
        mainContent.addView(scrollView)

        for (i in 0 until 8) {
            val slot = BrowserSlot(i)
            slot.card = createCard(slot)
            slots.add(slot)
            addCardToGrid(slot)
        }

        rootFrame.addView(mainContent)

        fullscreenContainer = FrameLayout(this).apply {
            visibility = View.GONE
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
            setBackgroundColor(Color.BLACK)
        }
        rootFrame.addView(fullscreenContainer)

        setContentView(rootFrame)
    }

    private fun buildTopBar(): LinearLayout {
        val topBar = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(14), dp(8), dp(10), dp(8))
            setBackgroundColor(Color.rgb(18, 18, 18))
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }
        val title = TextView(this).apply {
            text = "⚡ 8 Mini Browsers"
            setTextColor(Color.WHITE)
            textSize = 15f
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }
        topBar.addView(title)

        topBar.addView(textButton("↻ ВСЕ") {
            for (slot in slots) slot.webView.reload()
        })

        return topBar
    }

    private fun addCardToGrid(slot: BrowserSlot) {
        val params = GridLayout.LayoutParams().apply {
            width = 0
            height = dp(cardHeightDp)
            rowSpec = GridLayout.spec(GridLayout.UNDEFINED)
            columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
            setMargins(dp(5), dp(5), dp(5), dp(5))
        }
        grid.addView(slot.card, slot.index, params)
    }

    @SuppressLint("SetJavaScriptEnabled", "ClickableViewAccessibility")
    private fun createCard(slot: BrowserSlot): LinearLayout {
        val card = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            background = GradientDrawable().apply {
                setColor(Color.rgb(20, 20, 20))
                cornerRadius = dp(12).toFloat()
            }
            clipToOutline = true
        }

        // --- Header: url/title field + reload + open-externally + fullscreen ---
        val header = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(10), dp(6), dp(4), dp(6))
            background = GradientDrawable().apply {
                setColor(Color.rgb(30, 30, 30))
                cornerRadii = floatArrayOf(
                    dp(12).toFloat(), dp(12).toFloat(),
                    dp(12).toFloat(), dp(12).toFloat(),
                    0f, 0f, 0f, 0f
                )
            }
        }

        val urlInput = EditText(this).apply {
            hint = "🌐 Введите ссылку…"
            setHintTextColor(Color.rgb(140, 140, 140))
            setTextColor(Color.WHITE)
            textSize = 12f
            setSingleLine(true)
            inputType = InputType.TYPE_TEXT_VARIATION_URI
            imeOptions = EditorInfo.IME_ACTION_GO
            background = null
            setPadding(dp(2), 0, dp(2), 0)
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }
        slot.urlInput = urlInput
        urlInput.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_GO ||
                (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER)
            ) {
                loadUrlFromInput(slot)
                true
            } else {
                false
            }
        }
        header.addView(urlInput)

        header.addView(iconButton("↻") { slot.webView.reload() })
        header.addView(iconButton("↗") { openExternally(slot) })
        val fullscreenButton = iconButton("⛶") { toggleFullscreen(slot) }
        slot.fullscreenButton = fullscreenButton
        header.addView(fullscreenButton)

        card.addView(
            header,
            LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dp(40))
        )

        // --- WebView body ---
        val webView = WebView(this).apply {
            configureWebView(this)
        }
        // Let the WebView keep vertical drag gestures for itself so its own
        // content (feeds, pages) scrolls smoothly instead of the outer list
        // stealing the touch.
        webView.setOnTouchListener { v, _ ->
            v.parent.requestDisallowInterceptTouchEvent(true)
            false
        }
        webView.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            0,
            1f
        )
        slot.webView = webView
        card.addView(webView)

        return card
    }

    private fun textButton(label: String, onClick: () -> Unit): Button {
        return Button(this).apply {
            text = label
            textSize = 12f
            setTextColor(Color.WHITE)
            setBackgroundColor(Color.TRANSPARENT)
            setPadding(dp(8), 0, dp(4), 0)
            setOnClickListener { onClick() }
        }
    }

    private fun iconButton(label: String, onClick: () -> Unit): Button {
        return Button(this).apply {
            text = label
            textSize = 13f
            setTextColor(Color.WHITE)
            setBackgroundColor(Color.TRANSPARENT)
            minWidth = dp(34)
            minimumWidth = dp(34)
            minHeight = dp(34)
            minimumHeight = dp(34)
            setPadding(0, 0, 0, 0)
            setOnClickListener { onClick() }
        }
    }

    private fun loadUrlFromInput(slot: BrowserSlot) {
        var text = slot.urlInput.text.toString().trim()
        if (text.isEmpty()) return
        if (!text.startsWith("http://") && !text.startsWith("https://")) {
            text = "https://$text"
        }
        slot.urlInput.setText(text)
        slot.webView.loadUrl(text)
        currentFocus?.let {
            val imm = getSystemService(INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
            imm.hideSoftInputFromWindow(it.windowToken, 0)
        }
    }

    private fun openExternally(slot: BrowserSlot) {
        val url = slot.webView.url ?: slot.urlInput.text.toString().trim()
        if (url.isEmpty() || url == "about:blank") return
        try {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
        } catch (_: Exception) {
            // No app can handle it; ignore.
        }
    }

    // ---------------------------------------------------------------------
    // Fullscreen toggle
    // ---------------------------------------------------------------------

    private fun toggleFullscreen(slot: BrowserSlot) {
        if (fullscreenSlot == slot) {
            exitFullscreen()
        } else if (fullscreenSlot == null) {
            enterFullscreen(slot)
        }
    }

    private fun enterFullscreen(slot: BrowserSlot) {
        grid.removeView(slot.card)
        fullscreenContainer.addView(
            slot.card,
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        )
        mainContent.visibility = View.GONE
        fullscreenContainer.visibility = View.VISIBLE
        slot.fullscreenButton.text = "🗗"
        fullscreenSlot = slot
    }

    private fun exitFullscreen() {
        val slot = fullscreenSlot ?: return
        fullscreenContainer.removeView(slot.card)
        addCardToGrid(slot)
        fullscreenContainer.visibility = View.GONE
        mainContent.visibility = View.VISIBLE
        slot.fullscreenButton.text = "⛶"
        fullscreenSlot = null
    }

    // ---------------------------------------------------------------------
    // WebView configuration
    // ---------------------------------------------------------------------

    @SuppressLint("SetJavaScriptEnabled")
    private fun configureWebView(webView: WebView) {
        val settings = webView.settings

        settings.javaScriptEnabled = true
        settings.domStorageEnabled = true
        settings.databaseEnabled = true

        CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true)

        settings.useWideViewPort = true
        settings.loadWithOverviewMode = true

        settings.setSupportZoom(true)
        settings.builtInZoomControls = false
        settings.displayZoomControls = false

        settings.mediaPlaybackRequiresUserGesture = false
        settings.cacheMode = WebSettings.LOAD_DEFAULT
        settings.setGeolocationEnabled(true)

        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView,
                request: WebResourceRequest
            ): Boolean = false
        }

        webView.webChromeClient = object : WebChromeClient() {
            override fun onGeolocationPermissionsShowPrompt(
                origin: String?,
                callback: GeolocationPermissions.Callback?
            ) {
                callback?.invoke(origin, true, false)
            }
        }

        settings.userAgentString = WebSettings.getDefaultUserAgent(this)

        webView.isVerticalScrollBarEnabled = true
        webView.isHorizontalScrollBarEnabled = false
        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null)
    }

    private fun dp(value: Int): Int {
        val density = resources.displayMetrics.density
        return (value * density).toInt()
    }

    // ---------------------------------------------------------------------
    // Back button handling
    // ---------------------------------------------------------------------

    override fun onBackPressed() {
        val fs = fullscreenSlot
        if (fs != null) {
            if (fs.webView.canGoBack()) {
                fs.webView.goBack()
            } else {
                exitFullscreen()
            }
            return
        }
        for (slot in slots.reversed()) {
            if (slot.webView.canGoBack()) {
                slot.webView.goBack()
                return
            }
        }
        super.onBackPressed()
    }

    override fun onDestroy() {
        for (slot in slots) {
            slot.webView.stopLoading()
            slot.webView.loadUrl("about:blank")
            slot.webView.clearHistory()
            slot.webView.destroy()
        }
        slots.clear()
        super.onDestroy()
    }
}
