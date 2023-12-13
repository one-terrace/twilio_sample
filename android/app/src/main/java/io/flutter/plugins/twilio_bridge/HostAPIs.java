// Autogenerated from Pigeon (v14.0.0), do not edit directly.
// See also: https://pub.dev/packages/pigeon

import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import io.flutter.plugin.common.BasicMessageChannel;
import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.MessageCodec;
import io.flutter.plugin.common.StandardMessageCodec;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Generated class from Pigeon. */
@SuppressWarnings({"unused", "unchecked", "CodeBlock2Expr", "RedundantSuppression", "serial"})
public class HostAPIs {

  /** Error class for passing custom error details to Flutter via a thrown PlatformException. */
  public static class FlutterError extends RuntimeException {

    /** The error code. */
    public final String code;

    /** The error details. Must be a datatype supported by the api codec. */
    public final Object details;

    public FlutterError(@NonNull String code, @Nullable String message, @Nullable Object details) 
    {
      super(message);
      this.code = code;
      this.details = details;
    }
  }

  @NonNull
  protected static ArrayList<Object> wrapError(@NonNull Throwable exception) {
    ArrayList<Object> errorList = new ArrayList<Object>(3);
    if (exception instanceof FlutterError) {
      FlutterError error = (FlutterError) exception;
      errorList.add(error.code);
      errorList.add(error.getMessage());
      errorList.add(error.details);
    } else {
      errorList.add(exception.toString());
      errorList.add(exception.getClass().getSimpleName());
      errorList.add(
        "Cause: " + exception.getCause() + ", Stacktrace: " + Log.getStackTraceString(exception));
    }
    return errorList;
  }

  public enum MakeCallStatus {
    SUCCESS(0),
    ANOTHER_CALL(1),
    FAIL(2);

    final int index;

    private MakeCallStatus(final int index) {
      this.index = index;
    }
  }

  /** Asynchronous error handling return type for non-nullable API method returns. */
  public interface Result<T> {
    /** Success case callback method for handling returns. */
    void success(@NonNull T result);

    /** Failure case callback method for handling errors. */
    void error(@NonNull Throwable error);
  }
  /** Asynchronous error handling return type for nullable API method returns. */
  public interface NullableResult<T> {
    /** Success case callback method for handling returns. */
    void success(@Nullable T result);

    /** Failure case callback method for handling errors. */
    void error(@NonNull Throwable error);
  }
  /** Generated interface from Pigeon that represents a handler of messages from Flutter. */
  public interface TwilioBridgeHostApi {

    @NonNull 
    String getLanguage();

    void sendFromNative(@NonNull Result<Boolean> result);

    void initialize(@NonNull Result<Void> result);

    void deinitialize(@NonNull Result<Void> result);

    void toggleAudioRoute(@NonNull Boolean toSpeaker, @NonNull Result<Boolean> result);

    void makeCall(@NonNull Result<MakeCallStatus> result);

    /** The codec used by TwilioBridgeHostApi. */
    static @NonNull MessageCodec<Object> getCodec() {
      return new StandardMessageCodec();
    }
    /**Sets up an instance of `TwilioBridgeHostApi` to handle messages through the `binaryMessenger`. */
    static void setUp(@NonNull BinaryMessenger binaryMessenger, @Nullable TwilioBridgeHostApi api) {
      {
        BasicMessageChannel<Object> channel =
            new BasicMessageChannel<>(
                binaryMessenger, "dev.flutter.pigeon.twilio_sample.TwilioBridgeHostApi.getLanguage", getCodec());
        if (api != null) {
          channel.setMessageHandler(
              (message, reply) -> {
                ArrayList<Object> wrapped = new ArrayList<Object>();
                try {
                  String output = api.getLanguage();
                  wrapped.add(0, output);
                }
 catch (Throwable exception) {
                  ArrayList<Object> wrappedError = wrapError(exception);
                  wrapped = wrappedError;
                }
                reply.reply(wrapped);
              });
        } else {
          channel.setMessageHandler(null);
        }
      }
      {
        BasicMessageChannel<Object> channel =
            new BasicMessageChannel<>(
                binaryMessenger, "dev.flutter.pigeon.twilio_sample.TwilioBridgeHostApi.sendFromNative", getCodec());
        if (api != null) {
          channel.setMessageHandler(
              (message, reply) -> {
                ArrayList<Object> wrapped = new ArrayList<Object>();
                Result<Boolean> resultCallback =
                    new Result<Boolean>() {
                      public void success(Boolean result) {
                        wrapped.add(0, result);
                        reply.reply(wrapped);
                      }

                      public void error(Throwable error) {
                        ArrayList<Object> wrappedError = wrapError(error);
                        reply.reply(wrappedError);
                      }
                    };

                api.sendFromNative(resultCallback);
              });
        } else {
          channel.setMessageHandler(null);
        }
      }
      {
        BasicMessageChannel<Object> channel =
            new BasicMessageChannel<>(
                binaryMessenger, "dev.flutter.pigeon.twilio_sample.TwilioBridgeHostApi.initialize", getCodec());
        if (api != null) {
          channel.setMessageHandler(
              (message, reply) -> {
                ArrayList<Object> wrapped = new ArrayList<Object>();
                Result<Void> resultCallback =
                    new Result<Void>() {
                      public void success(Void result) {
                        wrapped.add(0, null);
                        reply.reply(wrapped);
                      }

                      public void error(Throwable error) {
                        ArrayList<Object> wrappedError = wrapError(error);
                        reply.reply(wrappedError);
                      }
                    };

                api.initialize(resultCallback);
              });
        } else {
          channel.setMessageHandler(null);
        }
      }
      {
        BasicMessageChannel<Object> channel =
            new BasicMessageChannel<>(
                binaryMessenger, "dev.flutter.pigeon.twilio_sample.TwilioBridgeHostApi.deinitialize", getCodec());
        if (api != null) {
          channel.setMessageHandler(
              (message, reply) -> {
                ArrayList<Object> wrapped = new ArrayList<Object>();
                Result<Void> resultCallback =
                    new Result<Void>() {
                      public void success(Void result) {
                        wrapped.add(0, null);
                        reply.reply(wrapped);
                      }

                      public void error(Throwable error) {
                        ArrayList<Object> wrappedError = wrapError(error);
                        reply.reply(wrappedError);
                      }
                    };

                api.deinitialize(resultCallback);
              });
        } else {
          channel.setMessageHandler(null);
        }
      }
      {
        BasicMessageChannel<Object> channel =
            new BasicMessageChannel<>(
                binaryMessenger, "dev.flutter.pigeon.twilio_sample.TwilioBridgeHostApi.toggleAudioRoute", getCodec());
        if (api != null) {
          channel.setMessageHandler(
              (message, reply) -> {
                ArrayList<Object> wrapped = new ArrayList<Object>();
                ArrayList<Object> args = (ArrayList<Object>) message;
                Boolean toSpeakerArg = (Boolean) args.get(0);
                Result<Boolean> resultCallback =
                    new Result<Boolean>() {
                      public void success(Boolean result) {
                        wrapped.add(0, result);
                        reply.reply(wrapped);
                      }

                      public void error(Throwable error) {
                        ArrayList<Object> wrappedError = wrapError(error);
                        reply.reply(wrappedError);
                      }
                    };

                api.toggleAudioRoute(toSpeakerArg, resultCallback);
              });
        } else {
          channel.setMessageHandler(null);
        }
      }
      {
        BasicMessageChannel<Object> channel =
            new BasicMessageChannel<>(
                binaryMessenger, "dev.flutter.pigeon.twilio_sample.TwilioBridgeHostApi.makeCall", getCodec());
        if (api != null) {
          channel.setMessageHandler(
              (message, reply) -> {
                ArrayList<Object> wrapped = new ArrayList<Object>();
                Result<MakeCallStatus> resultCallback =
                    new Result<MakeCallStatus>() {
                      public void success(MakeCallStatus result) {
                        wrapped.add(0, result.index);
                        reply.reply(wrapped);
                      }

                      public void error(Throwable error) {
                        ArrayList<Object> wrappedError = wrapError(error);
                        reply.reply(wrappedError);
                      }
                    };

                api.makeCall(resultCallback);
              });
        } else {
          channel.setMessageHandler(null);
        }
      }
    }
  }
}
