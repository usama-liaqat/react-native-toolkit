package com.cookies

import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReadableMap
import com.facebook.react.module.annotations.ReactModule

@ReactModule(name = CookiesModule.NAME)
class CookiesModule(reactContext: ReactApplicationContext) :
  NativeCookiesSpec(reactContext) {

  val cookieManagerService = CookieManagerService()

  override fun getName(): String {
    return NAME
  }

  override fun setValue(url: String?, cookie: ReadableMap, useWebKit: Boolean?, promise: Promise) {
    cookieManagerService.set(url, cookie, useWebKit, promise)
  }

  override fun setFromResponse(url: String?, cookie: String?, promise: Promise) {
    cookieManagerService.setFromResponse(url, cookie, promise)
  }

  override fun getValue(url: String?, useWebKit: Boolean?, promise: Promise) {
    cookieManagerService.get(url, useWebKit, promise)
  }

  override fun getFromResponse(url: String?, promise: Promise) {
    cookieManagerService.getFromResponse(url, promise)

  }

  override fun clearAll(useWebKit: Boolean?, promise: Promise) {
    cookieManagerService.clearAll(useWebKit, promise)
  }

  override fun flush(promise: Promise) {
    cookieManagerService.flush(promise)
  }

  override fun removeSessionCookies(promise: Promise) {
    cookieManagerService.removeSessionCookies(promise)
  }

  override fun getAll(useWebKit: Boolean?, promise: Promise) {
    promise.resolve(emptyMap<String, Any>())
  }

  override fun clearByName(url: String?, name: String?, useWebKit: Boolean?, promise: Promise) {
    promise.resolve(false)
  }

    companion object {
    const val NAME = "Cookies"
  }
}
