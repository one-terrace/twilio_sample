extension FlutterError: Error {}

class TwilioBridgeHostApiImplementation: TwilioBridgeHostApi {
  func getLanguage() throws -> String {
    return "iOS"
  }

  func sendFromNative(completion: @escaping (Result<Bool, Error>) -> Void) {
    completion(.success(true))
  }
}