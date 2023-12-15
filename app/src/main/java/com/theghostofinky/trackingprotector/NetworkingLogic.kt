package com.theghostofinky.trackingprotector

import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import java.net.URL
import java.util.regex.Pattern

@Suppress("unused")
class RedditURL(srcUrl: String) {
    private var innerUrl: URL
    private var subreddit: String
    private var postId: String
    private var commentID: String? = null

    init {
        val parsedUrl = URL(srcUrl)
        if (!parsedUrl.host.endsWith("reddit.com")) {
            throw Exception("Invalid URL: not reddit.com")
        }
        val chunks = parsedUrl.path.split("/").filter {
            it.isNotEmpty()
        }
        if (chunks.size < 3 || chunks[0] != "r" || chunks[2] != "comments") {
            throw Exception("Invalid URL: 'r' and 'comments' fragments not found")
        }
        when (chunks.size) {
            6 -> {
                subreddit = chunks[1]
                postId = chunks[3]
                commentID = chunks[5]
            }

            5 -> {
                subreddit = chunks[1]
                postId = chunks[3]
            }

            else -> {
                throw Exception("Invalid URL: unsupported number of chunks")
            }
        }
        innerUrl = parsedUrl
    }

    fun isCommentLink(): Boolean {
        return commentID != null
    }

    fun getCleanUrl(baseHost: String = "www.reddit.com", context: Int? = null): String {
        var finalUrl = "https://$baseHost/r/$subreddit/comments/$postId/"
        commentID?.let { cID ->
            finalUrl += "-/$cID/"

            context?.let {
                finalUrl += "?context=$it"
            }
        }
        return finalUrl
    }
}


suspend fun untrackURL(url: String): RedditURL {
    var mutUrl = url.trim()

    if (mutUrl.isEmpty()) {
        throw Exception("Empty URL string provided")
    }

    if (!mutUrl.startsWith("http://", true) && !mutUrl.startsWith("https://", true)) {
        mutUrl = "https://$url"
    }

    val trackerPattern = Pattern.compile(
        "^https?://(?:\\w+\\.)?reddit\\.com/r/[\\w_]+/s/\\w+\$",
        Pattern.CASE_INSENSITIVE
    )

    val inputMatcher = trackerPattern.matcher(mutUrl)

    if (!inputMatcher.find()) {
        throw Exception("Invalid URL")
    }

    val loc = getRedirect(mutUrl)

    if (trackerPattern.matcher(loc).find()) {
        return untrackURL(loc)
    }

    return RedditURL(loc)
}

suspend fun getRedirect(url: String): String {
    val client = HttpClient(Android) {
        followRedirects = false
    }

    val response: HttpResponse = client.get(url)

    val status = response.status.value
    val location = response.headers["location"]

    if (status == 301 && location != null) {
        return location
    } else {
        throw Exception("URL provided is not a redirect")
    }
}