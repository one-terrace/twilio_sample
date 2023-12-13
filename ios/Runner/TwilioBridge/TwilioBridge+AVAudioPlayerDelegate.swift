//
//  TwilioBridge+AVAudioPlayerDelegate.swift
//  Runner
//
//  Created by ZeroGravity on 12/11/23.
//

import Foundation
import AVFAudio

extension TwilioBridgeImplementation: AVAudioPlayerDelegate {
    func audioPlayerDidFinishPlaying(_ player: AVAudioPlayer, successfully flag: Bool) {
        if flag {
            NSLog("Audio player finished playing successfully");
        } else {
            NSLog("Audio player finished playing with some error");
        }
    }
    
    func audioPlayerDecodeErrorDidOccur(_ player: AVAudioPlayer, error: Error?) {
        if let error = error {
            NSLog("Decode error occurred: \(error.localizedDescription)")
        }
    }
}
