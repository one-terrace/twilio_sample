import AVFoundation
import UIKit
import TwilioVoice
import CallKit

extension FlutterError: Error {}

class TwilioBridgeImplementation: NSObject {
    let audioDevice = DefaultAudioDevice()
    var callKitProvider: CXProvider?
    let callKitCallController = CXCallController()
    var callKitCompletionCallback: ((Bool) -> Void)? = nil
    var activeCallInvites: [String: CallInvite]! = [:]
    var activeCalls: [String: Call]! = [:]
    var outgoingCallId: String?
    var activeCall: Call? = nil
    var playCustomRingback = false
    var userInitiatedDisconnect: Bool = false
    var ringtonePlayer: AVAudioPlayer? = nil
    var incomingPushCompletionCallback: (() -> Void)?
    var isInitialized: Bool = false
    
    var flutterApis: TwilioBridgeFlutterApi?
    
    init(binaryMessenger: FlutterBinaryMessenger) {
        flutterApis = TwilioBridgeFlutterApi(binaryMessenger: binaryMessenger)
    }
}

// MARK: - TwilioBridgeHostApi

extension TwilioBridgeImplementation: TwilioBridgeHostApi {
    func getLanguage() throws -> String {
        return "iOS"
    }

    func sendFromNative(completion: @escaping (Result<Bool, Error>) -> Void) {
        completion(.success(true))
    }
    
    func initialize(completion: @escaping (Result<Void, Error>) -> Void) {
        let configuration:CXProviderConfiguration
        
        if #available(iOS 14.0, *) {
            configuration = CXProviderConfiguration()
        } else {
            configuration = CXProviderConfiguration(localizedName: "Twilio sample")
        }
        
        configuration.maximumCallGroups = 1
        configuration.maximumCallsPerCallGroup = 1
        self.callKitProvider = CXProvider(configuration: configuration)
        
        if let provider = callKitProvider {
            provider.setDelegate(self, queue: nil)
        }
        
        TwilioVoiceSDK.audioDevice = audioDevice
        
        let defaultLogger = TwilioVoiceSDK.logger
        
        if let params = LogParameters.init(module:TwilioVoiceSDK.LogModule.platform , logLevel: TwilioVoiceSDK.LogLevel.debug, message: "The default logger is used for app logs") {
            defaultLogger.log(params: params)
        }
        
        self.isInitialized = true
        
        completion(.success(()))
    }
    
    func deinitialize(completion: @escaping (Result<Void, Error>) -> Void) {
        if let provider = callKitProvider {
            provider.invalidate()
        }
        
        self.isInitialized = false
        
        completion(.success(()))
    }
    
    func checkRecordPermission(completion: @escaping (_ permissionGranted: Bool) -> Void) {
        if #available(iOS 17.0, *) {
            switch AVAudioApplication.shared.recordPermission {
            case .granted:
                // Record permission already granted.
                completion(true)
            case .denied:
                // Record permission denied.
                completion(false)
            case .undetermined:
                // Requesting record permission.
                // Optional: pop up app dialog to let the users know if they want to request.
                AVAudioSession.sharedInstance().requestRecordPermission { granted in completion(granted) }
            default:
                completion(false)
            }
        } else {
            switch AVAudioSession.sharedInstance().recordPermission {
            case .granted:
                // Record permission already granted.
                completion(true)
            case .denied:
                // Record permission denied.
                completion(false)
            case .undetermined:
                // Requesting record permission.
                // Optional: pop up app dialog to let the users know if they want to request.
                AVAudioSession.sharedInstance().requestRecordPermission { granted in completion(granted) }
            default:
                completion(false)
            }
        }
    }
    
    func toggleAudioRoute(toSpeaker: Bool, completion: @escaping (Result<Bool, Error>) -> Void) {
        // The mode set by the Voice SDK is "VoiceChat" so the default audio route is the built-in receiver. Use port override to switch the route.
        audioDevice.block = {
            do {
                if toSpeaker {
                    try AVAudioSession.sharedInstance().overrideOutputAudioPort(.speaker)
                } else {
                    try AVAudioSession.sharedInstance().overrideOutputAudioPort(.none)
                }
                completion(.success(true))
            } catch {
                NSLog(error.localizedDescription)
                completion(.success(false))
            }
        }
        
        audioDevice.block()
    }
    
    func makeCall(completion: @escaping (Result<MakeCallStatus, Error>) -> Void) {
        guard activeCall == nil else {
            userInitiatedDisconnect = true
            completion(.success(.anotherCall))
            return
        }
        
        checkRecordPermission { [weak self] permissionGranted in
            let uuid = UUID()
            let handle = "Twilio sample"
            
            guard !permissionGranted else {
                self?.performStartCallAction(uuid: uuid, handle: handle) {result in
                    switch result {
                    case .success:
                        completion(.success(.success))
                    case .failure(let error):
                        completion(.failure(error))
                    }
                }
                return
            }
            
            // MARK: Request microphone access
        
            self?.performStartCallAction(uuid: uuid, handle: handle) { result in
                switch result {
                case .success:
                    completion(.success(.success))
                case .failure(let error):
                    completion(.failure(error))
                }
            }
        }
    }
}
