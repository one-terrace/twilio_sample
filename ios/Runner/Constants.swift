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
    let accessToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCIsImN0eSI6InR3aWxpby1mcGE7dj0xIn0.eyJqdGkiOiJTSzVhMDQyNzc4N2FlY2I2MzEzODEwNDYwYmUwZDcyODUwLTE3MDI0NzA2MjciLCJncmFudHMiOnsiaWRlbnRpdHkiOiJhbGljZSIsInZvaWNlIjp7ImluY29taW5nIjp7ImFsbG93Ijp0cnVlfSwib3V0Z29pbmciOnsiYXBwbGljYXRpb25fc2lkIjoiQVAxM2NkMzYxNTU5Njk4YWJmNDQ1OTEzNjM4YTUyODZlZCJ9fX0sImlhdCI6MTcwMjQ3MDYyNywiZXhwIjoxNzAyNDc0MjI3LCJpc3MiOiJTSzVhMDQyNzc4N2FlY2I2MzEzODEwNDYwYmUwZDcyODUwIiwic3ViIjoiQUNmZjYzMWUxY2ZkMzc0NmQxMzJlMDI5NzIwNGRhZjU0MSJ9.ny1GCEf2DySJ0vEvz-2TtW1vL4jYvcfK9uzdhMJKZwc"
    
    let twimlParamTo = "to"

    let kRegistrationTTLInDays = 365

    let kCachedDeviceToken = "CachedDeviceToken"
    let kCachedBindingDate = "CachedBindingDate"
}
