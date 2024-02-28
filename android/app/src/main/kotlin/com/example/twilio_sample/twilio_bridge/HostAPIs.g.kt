// Autogenerated from Pigeon (v14.0.0), do not edit directly.
// See also: https://pub.dev/packages/pigeon


import android.util.Log
import io.flutter.plugin.common.BasicMessageChannel
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.MessageCodec
import io.flutter.plugin.common.StandardMessageCodec
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer

private fun wrapResult(result: Any?): List<Any?> {
  return listOf(result)
}

private fun wrapError(exception: Throwable): List<Any?> {
  if (exception is HostAPIError) {
    return listOf(
      exception.code,
      exception.message,
      exception.details
    )
  } else {
    return listOf(
      exception.javaClass.simpleName,
      exception.toString(),
      "Cause: " + exception.cause + ", Stacktrace: " + Log.getStackTraceString(exception)
    )
  }
}

/**
 * Error class for passing custom error details to Flutter via a thrown PlatformException.
 * @property code The error code.
 * @property message The error message.
 * @property details The error details. Must be a datatype supported by the api codec.
 */
class HostAPIError (
  val code: String,
  override val message: String? = null,
  val details: Any? = null
) : Throwable()

enum class MakeCallStatus(val raw: Int) {
  SUCCESS(0),
  ANOTHERCALL(1),
  FAIL(2);

  companion object {
    fun ofRaw(raw: Int): MakeCallStatus? {
      return values().firstOrNull { it.raw == raw }
    }
  }
}

/** Generated interface from Pigeon that represents a handler of messages from Flutter. */
interface TwilioBridgeHostApi {
  fun getLanguage(): String
  fun sendFromNative(callback: (Result<Boolean>) -> Unit)
  fun initialize(callback: (Result<Unit>) -> Unit)
  fun deinitialize(callback: (Result<Unit>) -> Unit)
  fun toggleAudioRoute(toSpeaker: Boolean, callback: (Result<Boolean>) -> Unit)
  fun makeCall(token: String?, callback: (Result<MakeCallStatus>) -> Unit)
  fun hangUp(callback: (Result<Unit>) -> Unit)
  fun muteUnmute(callback: (Result<Unit>) -> Unit)
  fun changeAudioOutput(callback: (Result<Unit>) -> Unit)

  companion object {
    /** The codec used by TwilioBridgeHostApi. */
    val codec: MessageCodec<Any?> by lazy {
      StandardMessageCodec()
    }
    /** Sets up an instance of `TwilioBridgeHostApi` to handle messages through the `binaryMessenger`. */
    @Suppress("UNCHECKED_CAST")
    fun setUp(binaryMessenger: BinaryMessenger, api: TwilioBridgeHostApi?) {
      run {
        val channel = BasicMessageChannel<Any?>(binaryMessenger, "dev.flutter.pigeon.twilio_sample.TwilioBridgeHostApi.getLanguage", codec)
        if (api != null) {
          channel.setMessageHandler { _, reply ->
            var wrapped: List<Any?>
            try {
              wrapped = listOf<Any?>(api.getLanguage())
            } catch (exception: Throwable) {
              wrapped = wrapError(exception)
            }
            reply.reply(wrapped)
          }
        } else {
          channel.setMessageHandler(null)
        }
      }
      run {
        val channel = BasicMessageChannel<Any?>(binaryMessenger, "dev.flutter.pigeon.twilio_sample.TwilioBridgeHostApi.sendFromNative", codec)
        if (api != null) {
          channel.setMessageHandler { _, reply ->
            api.sendFromNative() { result: Result<Boolean> ->
              val error = result.exceptionOrNull()
              if (error != null) {
                reply.reply(wrapError(error))
              } else {
                val data = result.getOrNull()
                reply.reply(wrapResult(data))
              }
            }
          }
        } else {
          channel.setMessageHandler(null)
        }
      }
      run {
        val channel = BasicMessageChannel<Any?>(binaryMessenger, "dev.flutter.pigeon.twilio_sample.TwilioBridgeHostApi.initialize", codec)
        if (api != null) {
          channel.setMessageHandler { _, reply ->
            api.initialize() { result: Result<Unit> ->
              val error = result.exceptionOrNull()
              if (error != null) {
                reply.reply(wrapError(error))
              } else {
                reply.reply(wrapResult(null))
              }
            }
          }
        } else {
          channel.setMessageHandler(null)
        }
      }
      run {
        val channel = BasicMessageChannel<Any?>(binaryMessenger, "dev.flutter.pigeon.twilio_sample.TwilioBridgeHostApi.deinitialize", codec)
        if (api != null) {
          channel.setMessageHandler { _, reply ->
            api.deinitialize() { result: Result<Unit> ->
              val error = result.exceptionOrNull()
              if (error != null) {
                reply.reply(wrapError(error))
              } else {
                reply.reply(wrapResult(null))
              }
            }
          }
        } else {
          channel.setMessageHandler(null)
        }
      }
      run {
        val channel = BasicMessageChannel<Any?>(binaryMessenger, "dev.flutter.pigeon.twilio_sample.TwilioBridgeHostApi.toggleAudioRoute", codec)
        if (api != null) {
          channel.setMessageHandler { message, reply ->
            val args = message as List<Any?>
            val toSpeakerArg = args[0] as Boolean
            api.toggleAudioRoute(toSpeakerArg) { result: Result<Boolean> ->
              val error = result.exceptionOrNull()
              if (error != null) {
                reply.reply(wrapError(error))
              } else {
                val data = result.getOrNull()
                reply.reply(wrapResult(data))
              }
            }
          }
        } else {
          channel.setMessageHandler(null)
        }
      }
      run {
        val channel = BasicMessageChannel<Any?>(binaryMessenger, "dev.flutter.pigeon.twilio_sample.TwilioBridgeHostApi.makeCall", codec)
        if (api != null) {
          channel.setMessageHandler { message, reply ->
            val args = message as List<Any?>
            val tokenArg = args[0] as String?
            api.makeCall(tokenArg) { result: Result<MakeCallStatus> ->
              val error = result.exceptionOrNull()
              if (error != null) {
                reply.reply(wrapError(error))
              } else {
                val data = result.getOrNull()
                reply.reply(wrapResult(data!!.raw))
              }
            }
          }
        } else {
          channel.setMessageHandler(null)
        }
      }
      run {
        val channel = BasicMessageChannel<Any?>(binaryMessenger, "dev.flutter.pigeon.twilio_sample.TwilioBridgeHostApi.hangUp", codec)
        if (api != null) {
          channel.setMessageHandler { _, reply ->
            api.hangUp() { result: Result<Unit> ->
              val error = result.exceptionOrNull()
              if (error != null) {
                reply.reply(wrapError(error))
              } else {
                reply.reply(wrapResult(null))
              }
            }
          }
        } else {
          channel.setMessageHandler(null)
        }
      }
      run {
        val channel = BasicMessageChannel<Any?>(binaryMessenger, "dev.flutter.pigeon.twilio_sample.TwilioBridgeHostApi.muteUnmute", codec)
        if (api != null) {
          channel.setMessageHandler { _, reply ->
            api.muteUnmute() { result: Result<Unit> ->
              val error = result.exceptionOrNull()
              if (error != null) {
                reply.reply(wrapError(error))
              } else {
                reply.reply(wrapResult(null))
              }
            }
          }
        } else {
          channel.setMessageHandler(null)
        }
      }
      run {
        val channel = BasicMessageChannel<Any?>(binaryMessenger, "dev.flutter.pigeon.twilio_sample.TwilioBridgeHostApi.changeAudioOutput", codec)
        if (api != null) {
          channel.setMessageHandler { _, reply ->
            api.changeAudioOutput() { result: Result<Unit> ->
              val error = result.exceptionOrNull()
              if (error != null) {
                reply.reply(wrapError(error))
              } else {
                reply.reply(wrapResult(null))
              }
            }
          }
        } else {
          channel.setMessageHandler(null)
        }
      }
    }
  }
}
