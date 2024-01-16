import UIKit
import Flutter
import PushKit

@UIApplicationMain
@objc class AppDelegate: FlutterAppDelegate {
    var twilioBridgeApi: TwilioBridgeImplementation?
    private let voipQueue = DispatchQueue(label: "voip_queue", qos: .userInteractive)
    
    override func application(
    _ application: UIApplication,
    didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?
    ) -> Bool {
        GeneratedPluginRegistrant.register(with: self)
        self.initialize()
      
        return super.application(application, didFinishLaunchingWithOptions: launchOptions)
    }

    override func application(_ application: UIApplication, didRegisterForRemoteNotificationsWithDeviceToken deviceToken: Data) {
        let token = deviceToken.map { String(format: "%02.2hhx", $0) }.joined()
        
        print(token, "ggwp")
    }
    
    private func initialize() {
        self.initializeBridge()
        self.voipRegistration()
    }
    
    private func initializeBridge() {
        guard twilioBridgeApi == nil else { return }
        
        let controller = window?.rootViewController as! FlutterViewController
        twilioBridgeApi = TwilioBridgeImplementation(binaryMessenger: controller.binaryMessenger)
        TwilioBridgeHostApiSetup.setUp(binaryMessenger: controller.binaryMessenger, api: self.twilioBridgeApi)
    }

    private func voipRegistration() {
        let voipRegistry = PKPushRegistry(queue: voipQueue)
        voipRegistry.delegate = self
        voipRegistry.desiredPushTypes = [PKPushType.voIP]
    }
}

//MARK: - PKPushRegistryDelegate
extension AppDelegate : PKPushRegistryDelegate {
    // Handle updated push credentials
    func pushRegistry(_ registry: PKPushRegistry, didUpdate credentials: PKPushCredentials, for type: PKPushType) {
        print(credentials.token)
        let deviceToken = credentials.token.map { String(format: "%02.2hhx", $0) }.joined()
        print("pushRegistry -> deviceToken :\(deviceToken)")
    }
        
    func pushRegistry(_ registry: PKPushRegistry, didInvalidatePushTokenFor type: PKPushType) {
        print("pushRegistry:didInvalidatePushTokenForType:")
    }
    
    func pushRegistry(_ registry: PKPushRegistry, didReceiveIncomingPushWith payload: PKPushPayload, for type: PKPushType) {
        handleReceiveIncomingPush(registry, didReceiveIncomingPushWith: payload, for: type)
    }
    
    // Handle incoming pushes
    func pushRegistry(_ registry: PKPushRegistry, didReceiveIncomingPushWith payload: PKPushPayload, for type: PKPushType, completion: @escaping () -> Void) {
        handleReceiveIncomingPush(registry, didReceiveIncomingPushWith: payload, for: type)
        
        completion()
    }
    
    private func handleReceiveIncomingPush(_ registry: PKPushRegistry, didReceiveIncomingPushWith payload: PKPushPayload, for type: PKPushType) {
        self.initialize()
        
        guard type == .voIP, self.twilioBridgeApi != nil else { return }
        
        self.twilioBridgeApi!.accessToken = payload.dictionaryPayload["token"] as? String
        
        if self.twilioBridgeApi?.isInitialized != true {
            self.twilioBridgeApi?.initialize {[weak self] _ in
                self?.reportCall(from: "dog")
            }
        }
        
        self.reportCall(from: "dog")
    }
    
    private func reportCall(from: String) {
        DispatchQueue.main.sync {
            self.twilioBridgeApi?.reportIncomingCall(from: from, uuid: UUID())
        }
    }
}
