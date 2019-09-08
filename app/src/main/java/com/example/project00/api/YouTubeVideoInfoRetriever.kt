package com.example.project00.api

import java.io.BufferedInputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.io.UnsupportedEncodingException
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLDecoder
import java.nio.charset.Charset
import java.util.TreeMap

/**
 * Represents youtube video information retriever.
 */
public class YouTubeVideoInfoRetriever
{
    private val URL_YOUTUBE_GET_VIDEO_INFO = "http://www.youtube.com/get_video_info?&video_id="

    val KEY_DASH_VIDEO = "dashmpd"
    val KEY_HLS_VIDEO = "hlsvp"

    private val kvpList = TreeMap<String, String>()

    @Throws(IOException::class)
    fun retrieve(videoId: String) {
        val targetUrl = "$URL_YOUTUBE_GET_VIDEO_INFO$videoId&el=info&ps=default&eurl=&gl=US&hl=en"
        val client = SimpleHttpClient()
        val output =
            client.execute(targetUrl, SimpleHttpClient.HTTP_GET, SimpleHttpClient.DEFAULT_TIMEOUT)
        parse(output)
    }

    fun printAll() {
        println("TOTAL VARIABLES=" + kvpList.size)

        for (entry in kvpList.entries) {
            print("" + entry.key + "=")
            println("" + entry.value + "")
        }
    }

    @Throws(UnsupportedEncodingException::class)
    private fun parse(data: String) {
        val splits = data.split("&".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        var kvpStr = ""

        if (splits.size < 1) {
            return
        }

        kvpList.clear()

        for (i in splits.indices) {
            kvpStr = splits[i]

            try {
                // Data is encoded multiple times
                kvpStr = URLDecoder.decode(kvpStr, SimpleHttpClient.ENCODING_UTF_8)
                kvpStr = URLDecoder.decode(kvpStr, SimpleHttpClient.ENCODING_UTF_8)

                val kvpSplits = kvpStr.split("=".toRegex(), 2).toTypedArray()

                if (kvpSplits.size == 2) {
                    kvpList.put(kvpSplits[0], kvpSplits[1])
                } else if (kvpSplits.size == 1) {
                    kvpList.put(kvpSplits[0], "")
                }
            } catch (ex: UnsupportedEncodingException) {
                throw ex
            }

        }
    }

    class SimpleHttpClient {
        val ENCODING_UTF_8 : Charset = Charsets.UTF_8
        val DEFAULT_TIMEOUT = 10000

        val HTTP_GET = "GET"

        @Throws(IOException::class)
        fun execute(urlStr: String, httpMethod: String, timeout: Int): String {
            var url: URL? = null
            var conn: HttpURLConnection? = null
            var inStream: InputStream? = null
            val outStream: OutputStream? = null
            var response: String = ""

            try {
                url = URL(urlStr)
                conn = url.openConnection() as HttpURLConnection
                conn.connectTimeout = timeout
                conn.requestMethod = httpMethod

                inStream = BufferedInputStream(conn.inputStream)
                response = getInput(inStream)
            } finally {
                if (conn != null && conn.errorStream != null) {
                    var errorResponse = " : "
                    errorResponse = errorResponse + getInput(conn.errorStream)
                    response = response!! + errorResponse
                }

                conn?.disconnect()
            }

            return response
        }

        @Throws(IOException::class)
        private fun getInput(`in`: InputStream): String {
            val sb = StringBuilder(8192)
            val b = ByteArray(1024)
            var bytesRead = 0

            while (true) {
                bytesRead = `in`.read(b)
                if (bytesRead < 0) {
                    break
                }
                val s = String(b, 0, bytesRead, ENCODING_UTF_8)
                sb.append(s)
            }

            return sb.toString()
        }
    }
}
