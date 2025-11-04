package com.cookies

import android.os.Build
import android.util.Log
import android.webkit.CookieManager
import android.webkit.ValueCallback
import com.facebook.react.bridge.Arguments.createMap
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReadableMap
import com.facebook.react.bridge.WritableMap
import java.io.IOException
import java.net.HttpCookie
import java.net.URISyntaxException
import java.net.URL
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import kotlin.text.substring

class CookieManagerService {
  @get:Throws(Exception::class)
  private val cookieManager: CookieManager
    get() {
      try {
        val cookieManager =
          android.webkit.CookieManager.getInstance()
        cookieManager.setAcceptCookie(true)
        return cookieManager
      } catch (e: Exception) {
        throw Exception(e)
      }
    }

  fun set(url: String?, cookie: ReadableMap, useWebKit: Boolean?, promise: Promise) {
    var cookieString: String? = null
    try {
      cookieString = toRFC6265string(makeHTTPCookieObject(url, cookie))
    } catch (e: Exception) {
      promise.reject(e)
      return
    }

    addCookies(url, cookieString, promise)
  }

  fun setFromResponse(url: String?, cookie: String?, promise: Promise) {
    if (cookie == null) {
      promise.reject(Exception(INVALID_COOKIE_VALUES))
      return
    }

    addCookies(url, cookie, promise)
  }

  fun flush(promise: Promise) {
    try {
      this.cookieManager.flush()
      promise.resolve(true)
    } catch (e: Exception) {
      promise.reject(e)
    }
  }

  fun removeSessionCookies(promise: Promise) {
    try {
      this.cookieManager.removeSessionCookies { data -> promise.resolve(data) }
    } catch (e: Exception) {
      promise.reject(e)
    }
  }

  @Throws(URISyntaxException::class, IOException::class)
  fun getFromResponse(url: String?, promise: Promise) {
    promise.resolve(url)
  }

  fun getAll(useWebKit: Boolean?, promise: Promise) {
    promise.reject(Exception(GET_ALL_NOT_SUPPORTED))
  }

  fun get(url: String?, useWebKit: Boolean?, promise: Promise) {
    if (isEmpty(url)) {
      promise.reject(Exception(INVALID_URL_MISSING_HTTP))
      return
    }
    try {
      val cookiesString = this.cookieManager.getCookie(url)

      val cookieMap = createCookieList(cookiesString)
      promise.resolve(cookieMap)
    } catch (e: Exception) {
      promise.reject(e)
    }
  }

  fun clearByName(url: String?, name: String?, useWebKit: Boolean?, promise: Promise) {
    promise.reject(Exception(CLEAR_BY_NAME_NOT_SUPPORTED))
  }

  fun clearAll(useWebKit: Boolean?, promise: Promise) {
    try {
      val cookieManager = this.cookieManager
      if (USES_LEGACY_STORE) {
        cookieManager.removeAllCookie()
        cookieManager.removeSessionCookie()
        promise.resolve(true)
      } else {
        cookieManager.removeAllCookies { value -> promise.resolve(value) }
        cookieManager.flush()
      }
    } catch (e: Exception) {
      promise.reject(e)
    }
  }

  private fun addCookies(url: String?, cookieString: String?, promise: Promise) {
    try {
      val cookieManager = this.cookieManager
      if (USES_LEGACY_STORE) {
        cookieManager.setCookie(url, cookieString)
        promise.resolve(true)
      } else {
        cookieManager.setCookie(url, cookieString, object : ValueCallback<Boolean?> {
          override fun onReceiveValue(value: Boolean?) {
            promise.resolve(value)
          }
        })
        cookieManager.flush()
      }
    } catch (e: Exception) {
      promise.reject(e)
    }
  }

  @Throws(Exception::class)
  private fun createCookieList(allCookies: String?): WritableMap {
    val allCookiesMap = createMap()

    if (!isEmpty(allCookies)) {
      val cookieHeaders: Array<String?> =
        allCookies!!.split(";".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
      for (singleCookie in cookieHeaders) {
        val cookies = HttpCookie.parse(singleCookie)
        for (cookie in cookies) {
          if (cookie != null) {
            val name = cookie.getName()
            val value = cookie.getValue()
            if (!isEmpty(name) && !isEmpty(value)) {
              val cookieMap = createCookieData(cookie)
              allCookiesMap.putMap(name!!, cookieMap)
            }
          }
        }
      }
    }

    return allCookiesMap
  }

  @Throws(Exception::class)
  private fun makeHTTPCookieObject(url: String?, cookie: ReadableMap): HttpCookie {
    var parsedUrl: URL? = null
    try {
      parsedUrl = URL(url)
    } catch (e: Exception) {
      throw Exception(INVALID_URL_MISSING_HTTP)
    }

    val topLevelDomain = parsedUrl.host

    if (isEmpty(topLevelDomain)) {
      // assume something went terribly wrong here and no cookie can be created
      throw Exception(INVALID_URL_MISSING_HTTP)
    }

    val cookieBuilder = HttpCookie(cookie.getString("name"), cookie.getString("value"))

    if (cookie.hasKey("domain") && !isEmpty(cookie.getString("domain"))) {
      var domain = cookie.getString("domain")
      // strip the leading . as Android doesn't take it
      // but will include subdomains by default
      if (domain!!.startsWith(".")) {
        domain = domain.substring(1)
      }

      if (!topLevelDomain!!.contains(domain) && topLevelDomain != domain) {
        throw Exception(String.format(INVALID_DOMAINS, topLevelDomain, domain))
      }

      cookieBuilder.domain = domain
    } else {
      cookieBuilder.domain = topLevelDomain
    }

    // unlike iOS, Android will handle no path gracefully and assume "/""
    if (cookie.hasKey("path") && !isEmpty(cookie.getString("path"))) {
      cookieBuilder.path = cookie.getString("path")
    }

    if (cookie.hasKey("expires") && !isEmpty(cookie.getString("expires"))) {
      val date = parseDate(cookie.getString("expires")!!)
      if (date != null) {
        cookieBuilder.maxAge = date.time
      }
    }

    if (cookie.hasKey("secure") && cookie.getBoolean("secure")) {
      cookieBuilder.secure = true
    }

    if (HTTP_ONLY_SUPPORTED) {
      if (cookie.hasKey("httpOnly") && cookie.getBoolean("httpOnly")) {
        cookieBuilder.isHttpOnly = true
      }
    }

    return cookieBuilder
  }

  private fun createCookieData(cookie: HttpCookie): WritableMap {
    val cookieMap = createMap()
    cookieMap.putString("name", cookie.name)
    cookieMap.putString("value", cookie.value)
    cookieMap.putString("domain", cookie.domain)
    cookieMap.putString("path", cookie.path)
    cookieMap.putBoolean("secure", cookie.secure)
    if (HTTP_ONLY_SUPPORTED) {
      cookieMap.putBoolean("httpOnly", cookie.isHttpOnly)
    }

    // if persistent the max Age will be -1
    val expires = cookie.maxAge
    if (expires > 0) {
      val expiry = formatDate(Date(expires))
      if (!isEmpty(expiry)) {
        cookieMap.putString("expires", expiry)
      }
    }
    return cookieMap
  }

  /**
   * As HttpCookie is designed specifically for headers, it only gives us 2 formats on toString
   * dependent on the cookie version: 0 = Netscape; 1 = RFC 2965/2109, both without leading "Cookie:" token.
   * For our purposes RFC 6265 is the right way to go.
   * This is a convenience method to give us the right formatting.
   */
  private fun toRFC6265string(cookie: HttpCookie): String {
    val builder = StringBuilder()

    builder.append(cookie.name).append('=').append(cookie.value)

    if (!cookie.hasExpired()) {
      val expiresAt = cookie.maxAge
      if (expiresAt > 0) {
        val dateString = formatDate(Date(expiresAt), true)
        if (!isEmpty(dateString)) {
          builder.append("; expires=").append(dateString)
        }
      }
    }

    if (!isEmpty(cookie.domain)) {
      builder.append("; domain=").append(cookie.domain)
    }

    if (!isEmpty(cookie.path)) {
      builder.append("; path=").append(cookie.path)
    }

    if (cookie.secure) {
      builder.append("; secure")
    }

    if (HTTP_ONLY_SUPPORTED && cookie.isHttpOnly) {
      builder.append("; httponly")
    }

    return builder.toString()
  }

  private fun isEmpty(value: String?): Boolean {
    return value == null || value.isEmpty()
  }

  /**
   * Used for pushing cookies expiry time in and out of the library.
   *
   * @return simple date formatter
   */
  private fun dateFormatter(): DateFormat {
    // suggested formatting -> return DateTimeFormatter.RFC_1123_DATE_TIME;
    // matching ios format as yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ
    val df: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ", Locale.US)
    df.timeZone = TimeZone.getTimeZone("GMT")
    return df
  }

  /**
   * Used building the correctly formatted date for a cookie string in RFC_1123_DATE_TIME format
   *
   * @return simple date formatter
   */
  private fun RFC1123dateFormatter(): DateFormat {
    val df: DateFormat = SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US)
    df.timeZone = TimeZone.getTimeZone("GMT")
    return df
  }

  private fun parseDate(dateString: String): Date? {
    return parseDate(dateString, false)
  }

  private fun parseDate(dateString: String, rfc1123: Boolean): Date? {
    var date: Date? = null
    try {
      date = (if (rfc1123) RFC1123dateFormatter() else dateFormatter()).parse(dateString)
    } catch (e: Exception) {
      val message = e.message
      Log.i("Cookies", message ?: "Unable to parse date")
    }
    return date
  }

  private fun formatDate(date: Date): String? {
    return formatDate(date, false)
  }

  private fun formatDate(date: Date, rfc1123: Boolean): String? {
    var dateString: String? = null
    try {
      dateString = (if (rfc1123) RFC1123dateFormatter() else dateFormatter()).format(date)
    } catch (e: Exception) {
      val message = e.message
      Log.i("Cookies", message ?: "Unable to format date")
    }
    return dateString
  }

  companion object {
    private val USES_LEGACY_STORE = Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP
    private val HTTP_ONLY_SUPPORTED = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N

    private const val INVALID_URL_MISSING_HTTP =
      "Invalid URL: It may be missing a protocol (ex. http:// or https://)."
    private const val INVALID_COOKIE_VALUES = "Unable to add cookie - invalid values"
    private const val GET_ALL_NOT_SUPPORTED =
      "Get all cookies not supported for Android (iOS only)"
    private const val CLEAR_BY_NAME_NOT_SUPPORTED =
      "Cannot remove a single cookie by name on Android"
    private const val INVALID_DOMAINS =
      "Cookie URL host %s and domain %s mismatched. The cookie won't set correctly."
  }
}

