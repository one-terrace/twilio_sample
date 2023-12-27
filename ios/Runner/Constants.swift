//
//  Consts.swift
//  Runner
//
//  Created by ZeroGravity on 12/10/23.
//

import Foundation

struct Constants {
    static let twilio = TwilioConstants()
}

struct TwilioConstants {
    let accessToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCIsImN0eSI6InR3aWxpby1mcGE7dj0xIn0.eyJqdGkiOiJTSzZlYmUwYTI3MGE2ODI5YmI1NmRiNmRlYTM2ZmU1N2RhLTE3MDM2NDY1NTEiLCJncmFudHMiOnsiaWRlbnRpdHkiOiJLaW5kbHlEYW5ueVF1YW50aWNvIiwidm9pY2UiOnsiaW5jb21pbmciOnsiYWxsb3ciOnRydWV9LCJvdXRnb2luZyI6eyJhcHBsaWNhdGlvbl9zaWQiOiJBUDQ2YTU5N2YzNThmODMzNzUyMTgyYTBlOWJhMTM4ODA5In19fSwiaWF0IjoxNzAzNjQ2NTUxLCJleHAiOjE3MDM2NTAxNTEsImlzcyI6IlNLNmViZTBhMjcwYTY4MjliYjU2ZGI2ZGVhMzZmZTU3ZGEiLCJzdWIiOiJBQzM4YmZmM2Q5NWQzNDI2ZGUyMjllMzZmOTQ2OTNjNzEzIn0.GpDw09UZc4mn6VVeaJ3z-6d1hlFdqzg-M9H50O3FvQU"
    
    let twimlParamTo = "to"

    let kRegistrationTTLInDays = 365

    let kCachedDeviceToken = "CachedDeviceToken"
    let kCachedBindingDate = "CachedBindingDate"
}
