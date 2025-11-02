package com.mediainfo

import androidx.exifinterface.media.ExifInterface
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.ReadableMap
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import androidx.core.net.toUri
import com.facebook.react.bridge.ReadableType

private const val ERROR_TAG = "IMAGE_METADATA_ERROR"

class ImageUtility(private val context: ReactApplicationContext) {

  fun extract(uri: String, promise: Promise) {
    try {
      val photoUri = uri.toUri()
      context.contentResolver.openInputStream(photoUri)?.use { inputStream ->
        val exif = ExifInterface(inputStream)
        val tags = formatTags(exif)
        promise.resolve(tags)
      } ?: run {
        promise.reject("E_FILE_NOT_FOUND", "Unable to open input stream for URI: $uri")
      }
    } catch (e: Exception) {
      e.printStackTrace()
      promise.reject("E_EXTRACT_FAILED", "Failed to extract metadata: ${e.message}")
    }
  }

  fun inject(uri: String, tags: ReadableMap, promise: Promise) {
    val photoUri = uri.toUri()
    val params = Arguments.createMap()

    try {
      context.contentResolver.openFileDescriptor(photoUri, "rw", null)?.use { parcelDescriptor ->
        val exif = ExifInterface(parcelDescriptor.fileDescriptor)

        for ((valType, tag) in IMAGE_TAGS) {
          if (!tags.hasKey(tag)) continue

          val type = tags.getType(tag)

          when (type) {
            ReadableType.Boolean -> exif.setAttribute(tag, tags.getBoolean(tag).toString())
            ReadableType.Number ->
              when (valType) {
                "double" -> exif.setAttribute(tag, tags.getDouble(tag).toBigDecimal().toPlainString())
                else -> exif.setAttribute(tag, tags.getDouble(tag).toInt().toString())
              }
            ReadableType.String -> exif.setAttribute(tag, tags.getString(tag))
            ReadableType.Array -> exif.setAttribute(tag, tags.getArray(tag).toString())
            else -> exif.setAttribute(tag, tags.getString(tag))
          }
        }

        if (
          tags.hasKey(ExifInterface.TAG_GPS_LATITUDE) &&
          tags.hasKey(ExifInterface.TAG_GPS_LONGITUDE)
        ) {
          exif.setLatLong(
            tags.getDouble(ExifInterface.TAG_GPS_LATITUDE),
            tags.getDouble(ExifInterface.TAG_GPS_LONGITUDE)
          )
        }

        if (tags.hasKey(ExifInterface.TAG_GPS_ALTITUDE)) {
          exif.setAltitude(tags.getDouble(ExifInterface.TAG_GPS_ALTITUDE))
        }

        params.putString("uri", uri)
        params.putMap("tags", formatTags(exif))

        exif.saveAttributes()
        promise.resolve(params)
      }

    } catch (e: Exception) {
      promise.reject(ERROR_TAG, e.message, e)
      e.printStackTrace()
    }
  }

  fun formatTags(exif: ExifInterface): ReadableMap {
    val tags = Arguments.createMap()

    for ((type, tag) in IMAGE_TAGS) {
      val attribute = exif.getAttribute(tag)
      if (!attribute.isNullOrEmpty()) {
        when (type) {
          "string" -> tags.putString(tag, attribute)
          "int" -> tags.putInt(tag, exif.getAttributeInt(tag, 0))
          "double" -> tags.putDouble(tag, exif.getAttributeDouble(tag, 0.0))
          "array" -> {
            val array = Arguments.createArray()
            exif.getAttributeRange(tag)?.forEach { value ->
              array.pushDouble(value.toDouble())
            }
            if (array.size() > 0) tags.putArray(tag, array)
          }
        }
      }
    }

    // GPS (lat, long, altitude)
    exif.latLong?.let {
      tags.putDouble(ExifInterface.TAG_GPS_ALTITUDE, exif.getAltitude(0.0))
      tags.putDouble(ExifInterface.TAG_GPS_LATITUDE, it[0])
      tags.putDouble(ExifInterface.TAG_GPS_LONGITUDE, it[1])
    }

    return tags
  }
}

