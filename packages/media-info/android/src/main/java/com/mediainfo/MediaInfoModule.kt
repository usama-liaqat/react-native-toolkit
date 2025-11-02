package com.mediainfo

import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.module.annotations.ReactModule
import com.facebook.react.bridge.Promise


@ReactModule(name = MediaInfoModule.NAME)
class MediaInfoModule(reactContext: ReactApplicationContext) :
  NativeMediaInfoSpec(reactContext) {

  private val imageUtility = ImageUtility(reactContext)


  override fun getName(): String {
    return NAME
  }

  // Example method
  // See https://reactnative.dev/docs/native-modules-android
  override fun multiply(a: Double, b: Double): Double {
    return a * b
  }


  override fun extract(uri: String, promise: Promise) {
    imageUtility.extract(uri, promise)
  }

  companion object {
    const val NAME = "MediaInfo"
  }
}
