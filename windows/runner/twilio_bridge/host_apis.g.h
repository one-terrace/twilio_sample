// Autogenerated from Pigeon (v14.0.0), do not edit directly.
// See also: https://pub.dev/packages/pigeon

#ifndef PIGEON_HOST_APIS_G_H_
#define PIGEON_HOST_APIS_G_H_
#include <flutter/basic_message_channel.h>
#include <flutter/binary_messenger.h>
#include <flutter/encodable_value.h>
#include <flutter/standard_message_codec.h>

#include <map>
#include <optional>
#include <string>

namespace twilio_sample {


// Generated class from Pigeon.

class FlutterError {
 public:
  explicit FlutterError(const std::string& code)
    : code_(code) {}
  explicit FlutterError(const std::string& code, const std::string& message)
    : code_(code), message_(message) {}
  explicit FlutterError(const std::string& code, const std::string& message, const flutter::EncodableValue& details)
    : code_(code), message_(message), details_(details) {}

  const std::string& code() const { return code_; }
  const std::string& message() const { return message_; }
  const flutter::EncodableValue& details() const { return details_; }

 private:
  std::string code_;
  std::string message_;
  flutter::EncodableValue details_;
};

template<class T> class ErrorOr {
 public:
  ErrorOr(const T& rhs) : v_(rhs) {}
  ErrorOr(const T&& rhs) : v_(std::move(rhs)) {}
  ErrorOr(const FlutterError& rhs) : v_(rhs) {}
  ErrorOr(const FlutterError&& rhs) : v_(std::move(rhs)) {}

  bool has_error() const { return std::holds_alternative<FlutterError>(v_); }
  const T& value() const { return std::get<T>(v_); };
  const FlutterError& error() const { return std::get<FlutterError>(v_); };

 private:
  friend class TwilioBridgeHostApi;
  ErrorOr() = default;
  T TakeValue() && { return std::get<T>(std::move(v_)); }

  std::variant<T, FlutterError> v_;
};


enum class MakeCallStatus {
  success = 0,
  anotherCall = 1,
  fail = 2
};
// Generated interface from Pigeon that represents a handler of messages from Flutter.
class TwilioBridgeHostApi {
 public:
  TwilioBridgeHostApi(const TwilioBridgeHostApi&) = delete;
  TwilioBridgeHostApi& operator=(const TwilioBridgeHostApi&) = delete;
  virtual ~TwilioBridgeHostApi() {}
  virtual ErrorOr<std::string> GetLanguage() = 0;
  virtual void SendFromNative(std::function<void(ErrorOr<bool> reply)> result) = 0;
  virtual void Initialize(std::function<void(std::optional<FlutterError> reply)> result) = 0;
  virtual void Deinitialize(std::function<void(std::optional<FlutterError> reply)> result) = 0;
  virtual void ToggleAudioRoute(
    bool to_speaker,
    std::function<void(ErrorOr<bool> reply)> result) = 0;
  virtual void MakeCall(
    const std::string* token,
    std::function<void(ErrorOr<MakeCallStatus> reply)> result) = 0;
  virtual void HangUp(std::function<void(std::optional<FlutterError> reply)> result) = 0;
  virtual void MuteUnmute(std::function<void(std::optional<FlutterError> reply)> result) = 0;
  virtual void ChangeAudioOutput(std::function<void(std::optional<FlutterError> reply)> result) = 0;

  // The codec used by TwilioBridgeHostApi.
  static const flutter::StandardMessageCodec& GetCodec();
  // Sets up an instance of `TwilioBridgeHostApi` to handle messages through the `binary_messenger`.
  static void SetUp(
    flutter::BinaryMessenger* binary_messenger,
    TwilioBridgeHostApi* api);
  static flutter::EncodableValue WrapError(std::string_view error_message);
  static flutter::EncodableValue WrapError(const FlutterError& error);

 protected:
  TwilioBridgeHostApi() = default;

};
}  // namespace twilio_sample
#endif  // PIGEON_HOST_APIS_G_H_
