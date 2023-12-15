package com.theghostofinky.trackingprotector

import org.junit.Assert
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun reddit_wrapper() {
        val srcUrl =
            "https://www.reddit.com/r/EnoughMuskSpam/comments/17m5izv/twitter_is_reportedly_losing_1_billionyear/?share_id=VkCRF5YtnJBfUJy4oMbH8&utm_content=1&utm_medium=android_app&utm_name=androidcss&utm_source=share&utm_term=14"
        val destUrl = "https://www.reddit.com/r/EnoughMuskSpam/comments/17m5izv/"
        val urlObj = RedditURL(srcUrl)
        Assert.assertEquals(destUrl, urlObj.getCleanUrl())
    }

    @Test
    fun reddit_wrapper_comment() {
        val srcUrl =
            "https://www.reddit.com/r/europe/comments/17mdeqr/german_industry_can_the_backbone_of_the_economy/k7le3cc?share_id=v11_v04VWhnJslWeot72e&utm_content=2&utm_medium=android_app&utm_name=androidcss&utm_source=share&utm_term=14"
        val destUrl = "https://www.reddit.com/r/europe/comments/17mdeqr/-/k7le3cc"
        val urlObj = RedditURL(srcUrl)
        Assert.assertEquals(destUrl, urlObj.getCleanUrl())
    }
}