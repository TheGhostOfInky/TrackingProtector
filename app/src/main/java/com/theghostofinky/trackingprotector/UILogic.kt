package com.theghostofinky.trackingprotector

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import kotlinx.coroutines.flow.first

enum class Actions {
    NONE,
    SHARE,
    COPY,
    OPEN;

    companion object {
        @Suppress("unused")
        fun fromString(value: String): Actions = Actions.values().firstOrNull {
            it.name == value
        } ?: let {
            NONE
        }

        fun fromIndex(index: Int): Actions = Actions.values().firstOrNull {
            it.ordinal == index
        } ?: let {
            NONE
        }
    }
}

suspend fun handleShare(sharedUrl: String) {
    try {
        val settings = PreferencesDatabase(MainActivity.appContext)

        val actionIndex = settings
            .getPreference(PrefKeys.SHARE_ACTION, Actions.SHARE.ordinal)
            .first()

        val action = Actions.fromIndex(actionIndex)

        handleActions(sharedUrl, action, settings)

    } catch (e: Exception) {
        e.message?.let {
            sendToast(it)
        }
    }
}

suspend fun handleOpen(openUrl: Uri?) {
    if (openUrl == null) {
        sendToast("No URL provided")
        return
    }
    try {
        val settings = PreferencesDatabase(MainActivity.appContext)

        val actionIndex = settings
            .getPreference(PrefKeys.OPEN_ACTION, Actions.SHARE.ordinal)
            .first()

        val action = Actions.fromIndex(actionIndex)

        handleActions(openUrl.toString(), action, settings)

    } catch (e: Exception) {
        e.message?.let {
            sendToast(it)
        }
    }
}

suspend fun buttonHandle(inputUrl: String, buttonType: Actions) {
    try {
        val settings = PreferencesDatabase(MainActivity.appContext)

        handleActions(inputUrl, buttonType, settings)

    } catch (e: Exception) {
        e.message?.let {
            sendToast(it)
        }
    }
}

suspend fun handleActions(inputURL: String, action: Actions, settings: PreferencesDatabase) {
    val baseHost = settings
        .getPreference(PrefKeys.INSTANCE, "www.reddit.com")
        .first()
    val context = settings
        .getPreference(PrefKeys.CONTEXT_COUNT, 1)
        .first()

    val url = untrackURL(inputURL)
    val finalUrl = url.getCleanUrl(baseHost, context)

    when (action) {
        Actions.SHARE -> shareURL(finalUrl)
        Actions.COPY -> copyURL(finalUrl)
        Actions.OPEN -> openURL(finalUrl)
        Actions.NONE -> Unit
    }
}

fun shareURL(url: String) {
    val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, url)
        type = "text/plain"
    }

    val shareIntent = Intent.createChooser(sendIntent, null)
    shareIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
    MainActivity.appContext.startActivity(shareIntent)
}

fun openURL(url: String) {
    val browserIntent = Intent().apply {
        action = Intent.ACTION_VIEW
        data = Uri.parse(url)
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
    }
    MainActivity.appContext.startActivity(browserIntent)
}

fun copyURL(url: String) {
    val clipboard = getSystemService(MainActivity.appContext, ClipboardManager::class.java)
    val clip = ClipData.newPlainText("clean URL", url)
    clipboard?.setPrimaryClip(clip)
    sendToast("Copied URL to clipboard")
}

fun sendToast(toastText: String, time: Int = Toast.LENGTH_SHORT) {
    Handler(Looper.getMainLooper()).post {
        val ctx = MainActivity.appContext
        val toast = Toast.makeText(ctx, toastText, time)
        toast.show()
    }
}