package com.example.videodownloadingline.utils

import android.app.Activity
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.videodownloadingline.model.downloadlink.WebViewDownloadUrl
import com.google.common.net.InternetDomainName
import org.json.JSONObject
import org.jsoup.Jsoup
import java.net.HttpURLConnection
import java.net.URL


fun websiteScrapperPart1(data: String?): WebViewDownloadUrl? {
    val videoItem = WebViewDownloadUrl()
    return if (data != null) {
        val document = Jsoup.parse(data)
        try {
            videoItem.videotitle =
                document.select("meta[property=taboola:title]").first()!!.attr("content")
            videoItem.videotype =
                document.select("meta[property=og:type]").first()!!.attr("content")

            videoItem.previewlink =
                document.select("meta[property=og:image]").first()!!.attr("content")

            videoItem.sdurl = document.select("meta[name=contentUrl]").first()!!.attr("content")

        } catch (e: Exception) {
            Log.d(TAG, "scrapper 1 $e")
        }
        getWebViewDownloadObj(videoItem)
    } else {
        null
    }
}


fun websiteScrapperPart2(data: String?): WebViewDownloadUrl? {
    val videoItem = WebViewDownloadUrl()
    return if (data != null) {
        val document = Jsoup.parse(data)
        try {
            videoItem.videotitle =
                document.select("meta[property=og:title]").first()!!.attr("content")
            videoItem.videotype =
                document.select("meta[property=og:type]").first()!!.attr("content")

            videoItem.previewlink =
                document.select("meta[property=og:image]").first()!!.attr("content")

            val element = document.select("div[id=video-player-bg]").select("script")
            for (element1 in element) {
                if (element1.data().contains("html5player") && element1.data()
                        .contains("setVideoUrlLow")
                ) {
                    val videolow = element1.html().split("setVideoUrlLow\\('").toTypedArray()
                    val videolow1 = videolow[1].split("'\\)").toTypedArray()
                    videoItem.sdurl = videolow1[0]
                    val videohd = element1.html().split("setVideoUrlHigh\\('").toTypedArray()
                    val videohd1 = videohd[1].split("'\\)").toTypedArray()
                    videoItem.sdurl = videohd1[0]
                }
            }

        } catch (e: Exception) {
            Log.d(TAG, "scrapper 1 $e")
        }
        getWebViewDownloadObj(videoItem)
    } else {
        null
    }
}


fun websiteScrapperPart3(data: String?): WebViewDownloadUrl? {
    val videoItem = WebViewDownloadUrl()
    return if (data != null) {
        val document = Jsoup.parse(data)
        try {
            try {
                videoItem.videotitle =
                    document.select("meta[property=taboola:title]").first()!!.attr("content")

                videoItem.videotype =
                    document.select("meta[property=og:type]").first()!!.attr("content")

                videoItem.previewlink =
                    document.select("meta[property=og:image]").first()!!.attr("content")

            } catch (e: java.lang.Exception) {
                Log.i(TAG, "websiteScrapperPart3: From Inner Exp-> ${e.message}")
            }
            val elements = document.select("script[type=application/ld+json]")
            for (element in elements) {
                if (element.html().contains("contentUrl") && element.html()
                        .contains(".mp4")
                ) {
                    try {
                        val jsonObject = JSONObject(element.html())
                        videoItem.sdurl = jsonObject.getString("contentUrl")
                        Log.d("testurlscrapper3", jsonObject.getString("contentUrl"))
                    } catch (e: java.lang.Exception) {
                        Log.d("testloop", "failed $e")
                    }
                }
            }
        } catch (e: Exception) {
            Log.d(TAG, "scrapper 1 $e")
        }
        getWebViewDownloadObj(videoItem)
    } else {
        null
    }
}


fun websiteScrapperPart4(data: String?): WebViewDownloadUrl? {
    val videoItem = WebViewDownloadUrl()
    return if (data != null) {
        val document = Jsoup.parse(data)
        try {
            try {
                videoItem.videotitle =
                    document.select("meta[property=taboola:title]").first()!!.attr("content")

                videoItem.videotype =
                    document.select("meta[property=og:type]").first()!!.attr("content")

                videoItem.previewlink =
                    document.select("meta[property=og:image]").first()!!.attr("content")

            } catch (e: java.lang.Exception) {
                Log.i(TAG, "websiteScrapperPart4: ${e.message}")
            }
            val elements = document.select("script")
            for (element in elements) {
                if (element.html()
                        .contains(".mp4") && element.hasAttr("defer") && element.html()
                        .contains("__html5playerdata")
                ) {
                    Log.d("testhtml", "found script")
                    val videolow = element.html().split("media_mp4\":\"").toTypedArray()
                    val videolow1 = videolow[1].split("\"").toTypedArray()
                    videoItem.sdurl = videolow1[0]
                    Log.d("testurl", videoItem.sdurl.toString())
                }
            }
        } catch (e: Exception) {
            Log.d(TAG, "scrapper 1 $e")
        }
        getWebViewDownloadObj(videoItem)
    } else {
        null
    }
}


fun genericDownloader(data: String?): WebViewDownloadUrl? {
    val webView = WebViewDownloadUrl()
    return if (data != null) {
        try {
            val document = Jsoup.parse(data)
            //String url= document.select("div[class=fp-player]").select("video").attr("src");
            var url = document.select("video").attr("src")
            if (url.isNullOrEmpty() || url.trim { it <= ' ' }.isEmpty()) {
                url = document.select("video").first()!!.select("source").attr("src")
            }
            Log.d(TAG, "testurl $url")
            webView.sdurl = url
        } catch (e: java.lang.Exception) {
            webView.sdurl = null
            Log.d(TAG, e.toString())
        }
        getWebViewDownloadObj(webView)
    } else {
        null
    }


    //thread.start()
    /*while (thread.isAlive) {
    }*/
    /**/
}

private fun getWebViewDownloadObj(fbValue: WebViewDownloadUrl): WebViewDownloadUrl? {
    return if (fbValue.sdurl == null || fbValue.sdurl!!.trim().isEmpty())
        if (fbValue.othervideourl == null || fbValue.othervideourl!!.trim().isEmpty())
            null
        else {
            fbValue.sdurl = fbValue.othervideourl
            fbValue
        } else {
        fbValue
    }
}


@RequiresApi(Build.VERSION_CODES.N)
fun getVideoFileSize(url: String): Int {
    val size = try {
        val link = URL(url)
        val urlConnection: HttpURLConnection = link.openConnection() as HttpURLConnection
        urlConnection.requestMethod = "HEAD"
        urlConnection.connect()
        urlConnection.inputStream
        val op = urlConnection.contentLengthLong
        Log.i(TAG, "getVideoFileSize: $op")
        if (op.toInt() <= 0) 10 else DownloadProgressLiveData.getMb(op)
    } catch (e: Exception) {
        Log.i(TAG, "getVideoFileSize: ${e.message}")
        0
    }
    return size
}


fun Activity.isValidUrl(url: String?): Boolean {
    return try {
        URL(url).toURI()
        true
    } catch (e: java.lang.Exception) {
        Toast.makeText(this, "Please Enter valid Url", Toast.LENGTH_SHORT).show()
        false
    }
}


fun getHostDomainName(host: String): String {
    return InternetDomainName.from(host).topPrivateDomain().toString()
}