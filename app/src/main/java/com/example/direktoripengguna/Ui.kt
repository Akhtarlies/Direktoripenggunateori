package com.example.direktoripengguna

import android.graphics.Color
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding

/** Edge-to-edge: header biru menempel sampai status bar, konten aman dari navigation bar. */
@Suppress("DEPRECATION")
fun AppCompatActivity.setupHeader(root: View, header: View) {
    supportActionBar?.hide()
    WindowCompat.setDecorFitsSystemWindows(window, false)
    window.statusBarColor = Color.TRANSPARENT
    WindowCompat.getInsetsController(window, root).isAppearanceLightStatusBars = false

    val headerTop = header.paddingTop
    ViewCompat.setOnApplyWindowInsetsListener(root) { _, insets ->
        val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
        header.updatePadding(top = headerTop + bars.top)
        root.updatePadding(bottom = bars.bottom)
        insets
    }
}
