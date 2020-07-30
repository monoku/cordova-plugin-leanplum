var exec = require('cordova/exec');

exports.start = function (data, success, error) {
    const options = [];
    exec(success, error, "NSLeanplum", "start", options);
};

exports.setAppIdForDevelopmentMode = function (appId, accessKey, success, error) {
    const options = [appId, accessKey];
    exec(success, error, "NSLeanplum", "setAppIdForDevelopmentMode", options);
};

exports.setAppIdForProductionMode = function (appId, accessKey, success, error) {
    const options = [appId, accessKey];
    exec(success, error, "NSLeanplum", "setAppIdForProductionMode", options);
};

exports.track = function (event, parametersDict = {}, success, error) {
    const options = [event, parametersDict];
    exec(success, error, "NSLeanplum", "track", options);
};

exports.setUserId = function (id, success, error) {
    const options = [id];
    exec(success, error, "NSLeanplum", "setUserId", options);
};

exports.setVariables = function (variables, success, error) {
    const options = [variables];
    exec(success, error, "NSLeanplum", "setVariables", options);
};

exports.getVariable = function (name, success, error) {
    const options = [name];
    exec(success, error, "NSLeanplum", "getVariable", options);
};

exports.getVariables = function (success, error) {
    const options = [];
    exec(success, error, "NSLeanplum", "getVariables", options);
};

exports.onStartResponse = function (success, error) {
    const options = [];
    exec(success, error, "NSLeanplum", "onStartResponse", options);
};

exports.onValueChanged = function (name, success, error) {
    const options = [name];
    exec(success, error, "NSLeanplum", "onValueChanged", options);
};

exports.forceContentUpdate = function (success, error) {
    const options = [];
    exec(success, error, "NSLeanplum", "forceContentUpdate", options);
};

exports.onVariablesChanged = function (success, error) {
    const options = [];
    exec(success, error, "NSLeanplum", "onVariablesChanged", options);
};
