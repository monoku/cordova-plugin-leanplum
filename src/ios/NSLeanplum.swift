import Foundation
import Leanplum

var variables = [String: LPVar]()

@objc(NSLeanplum) class NSLeanplum : CDVPlugin {
    func createVar(key: String, value: Any) -> LPVar? {
        var lpVar: LPVar;
        switch value.self {
        case is Int, is Double, is Float:
            lpVar = LPVar.define(key, with: value as? Double ?? 0.0)
        case is Bool:
            lpVar = LPVar.define(key, with: value as? Bool ?? false)
        case is String:
            lpVar = LPVar.define(key, with: value as? String)
        case is Array<Any>:
            lpVar = LPVar.define(key, with: value as? Array)
        case is Dictionary<String, Any>:
            lpVar = LPVar.define(key, with: value as? Dictionary)
        default:
            return nil
        }
        return lpVar;
    }
    
    func getAllVariables() -> Dictionary<String, Any> {
        var allVariables = [String: Any]()
        for (key, value) in variables {
            if(value.kind == "file") {
                continue
            }
            allVariables[key] = value.value
        }
        return allVariables
    }
    
    @objc(start:) func start(command: CDVInvokedUrlCommand) {
        Leanplum.start()
    }
    @objc(setAppIdForDevelopmentMode:) func setAppIdForDevelopmentMode(command: CDVInvokedUrlCommand) {
        let appId = command.argument(at: 0) as? String ?? ""
        let accessKey = command.argument(at: 1) as? String ?? ""
        Leanplum.setAppId(appId, withDevelopmentKey: accessKey)
    }

    @objc(setAppIdForProductionMode:) func setAppIdForProductionMode(command: CDVInvokedUrlCommand) {
        let appId = command.argument(at: 0) as? String ?? ""
        let accessKey = command.argument(at: 1) as? String ?? ""
        Leanplum.setAppId(appId, withDevelopmentKey: accessKey)
    }
    
    @objc(track:) func track(command: CDVInvokedUrlCommand) {
        let event = command.argument(at: 0) as? String ?? ""
        guard let parametersDict = command.argument(at: 1) as? Dictionary<String, Any> else {
          return
        }
        Leanplum.track(event, withParameters: parametersDict)
    }
    
    @objc(setUserId:) func setUserId(command: CDVInvokedUrlCommand) {
        let id = command.argument(at: 0) as? String ?? ""
        Leanplum.setUserId(id)
    }
    @objc(setVariables:) func setVariables(command: CDVInvokedUrlCommand) {
        let newVariables = command.argument(at: 0)
        guard let variablesDict = newVariables as? Dictionary<String, Any> else {
            return
        }

        for (key, value) in variablesDict {
            if let lpVar = self.createVar(key: key, value: value) {
                variables[key] = lpVar
            }
        }
    }
    
    @objc(getVariable:) func getVariable(command: CDVInvokedUrlCommand) {
        let variableName = command.argument(at: 0) as? String ?? ""
        
        if let lpVar = variables[variableName] {
            
            var resultData = [String:Any]()
            resultData["value"] = lpVar.value
            
            let result = CDVPluginResult(
                status: CDVCommandStatus_OK,
                messageAs: resultData
            )
            
            self.commandDelegate.send(result, callbackId: command.callbackId)
        } else {
            let result = CDVPluginResult(
                status: CDVCommandStatus_ERROR,
                messageAs: "We couldn't find the variable"
            )
            self.commandDelegate.send(result, callbackId: command.callbackId)
        }
    }
    
    @objc(getVariables:) func getVariables(command: CDVInvokedUrlCommand) {
        let allVariables = self.getAllVariables()
        
        let result = CDVPluginResult(
            status: CDVCommandStatus_OK,
            messageAs: allVariables
        )
        self.commandDelegate.send(result, callbackId: command.callbackId)
    }
    
    @objc(onStartResponse:) func onStartResponse(command: CDVInvokedUrlCommand) {
        Leanplum.onStartResponse { (success:Bool) in
            var resultData = [String:Any]()
            resultData["started"] = success
            
            let result = CDVPluginResult(
                status: CDVCommandStatus_OK,
                messageAs: resultData
            )
            self.commandDelegate.send(result, callbackId: command.callbackId)
        }
    }
    
    @objc(onValueChanged:) func onValueChanged(command: CDVInvokedUrlCommand) {
        let variableName = command.argument(at: 0) as? String ?? ""
        if let lpVar = variables[variableName] {
            lpVar.onValueChanged { [weak self] in
                var resultData = [String:Any]()
                resultData["value"] = lpVar.value
                
                let result = CDVPluginResult(
                  status: CDVCommandStatus_OK,
                  messageAs: resultData
              )
                result?.setKeepCallbackAs(true)
                self?.commandDelegate.send(result, callbackId: command.callbackId)
            }
        }
    }
    @objc(onVariablesChanged:) func onVariablesChanged(command: CDVInvokedUrlCommand) {
        Leanplum.onVariablesChanged { [weak self] in
            let allVariables = self?.getAllVariables()
            let result = CDVPluginResult(
                status: CDVCommandStatus_OK,
                messageAs: allVariables
            )
            result?.setKeepCallbackAs(true)
            self?.commandDelegate.send(result, callbackId: command.callbackId)
        }
    }
    
    @objc(forceContentUpdate:) func forceContentUpdate(command: CDVInvokedUrlCommand) {
        Leanplum.forceContentUpdate();
    }
    
    @objc(setUserAttributes:) func setUserAttributes(command: CDVInvokedUrlCommand) {
        guard let attributesDict = command.argument(at: 0) as? Dictionary<String, Any> else {
            return
        }
        DispatchQueue.main.async {
            Leanplum.setUserAttributes(attributesDict)
        }
    }

    @objc(setAppVersion:) func setAppVersion(command: CDVInvokedUrlCommand) {
        let versionNumber = command.argument(at: 0) as? String ?? "";
        Leanplum.setAppVersion(versionNumber);
    }
}


