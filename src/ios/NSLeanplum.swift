import Foundation
import Leanplum

@objc(NSLeanplum) class NSLeanplum : CDVPlugin {
  @objc(start:) func start(command: CDVInvokedUrlCommand) {
    print("start method from NSLeanplum")   
  }
}


