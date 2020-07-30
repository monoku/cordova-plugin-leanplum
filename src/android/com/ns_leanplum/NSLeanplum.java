package com.monoku.leanplum;

import android.content.Context;
import android.util.Log;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.leanplum.Leanplum;
import com.leanplum.Var;
import com.leanplum.callbacks.StartCallback;
import com.leanplum.callbacks.VariableCallback;
import com.leanplum.callbacks.VariablesChangedCallback;
import com.leanplum.internal.Constants;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class NSLeanplum extends CordovaPlugin {
    public static Map<String, Object> variables = new HashMap<String, Object>();
    public static JSONObject handlers = new JSONObject();

    public static String CLASS_TAG = "NSLeanplum";

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("start")) {
            Context self = this.cordova.getActivity().getApplicationContext();
            Leanplum.setApplicationContext(self);
            this.start();
            return true;
        }
        if (action.equals("forceContentUpdate")) {
            this.forceContentUpdate();
            return true;
        }
        if (action.equals("setAppIdForDevelopmentMode")) {
            String appId = args.getString(0);
            String accessKey = args.getString(1);

            this.setAppIdForDevelopmentMode(appId, accessKey);
            return true;
        }

        if (action.equals("setAppIdForProductionMode")) {
            String appId = args.getString(0);
            String accessKey = args.getString(1);

            this.setAppIdForProductionMode(appId, accessKey);
            return true;
        }

        if (action.equals("setDeviceId")) {
            String deviceID = args.getString(0);
            this.setDeviceId(deviceID);
            return true;
        }

        if (action.equals("setUserId")) {
            String userID = args.getString(0);
            this.setUserId(userID);
            return true;
        }

        if (action.equals("track")) {
            String event = args.getString(0);
            JSONObject properties = args.getJSONObject(1);

            this.track(event, properties);
            return true;
        }

        if (action.equals("setVariables")) {
            JSONObject object = args.getJSONObject(0);

            this.setVariables(object);
            return true;
        }

        if (action.equals(("getVariable"))) {
            String name = args.getString(0);

            this.getVariable(name, callbackContext);
            return true;
        }

        if (action.equals(("getVariables"))) {
            this.getVariables(callbackContext);
            return true;
        }

        // Callbacks
        if (action.equals("onStartResponse")) {
            this.onStartResponse(callbackContext);
            return true;
        }
        if (action.equals("onValueChanged")) {
            String name = args.getString(0);

            this.onValueChanged(name, callbackContext);
            return true;
        }
        if (action.equals("onVariablesChanged")) {
            this.onVariablesChanged(callbackContext);
            return true;
        }

        return false;
    }

    private void setAppIdForDevelopmentMode(String appId, String accessKey) {
        Leanplum.setAppIdForDevelopmentMode(appId, accessKey);
    }

    private void setAppIdForProductionMode(String appId, String accessKey) {
        Leanplum.setAppIdForProductionMode(appId, accessKey);
    }

    private void setDeviceId(String id) {
        Leanplum.setDeviceId(id);
    }

    private void setUserId(String id) {
        Leanplum.setUserId(id);
    }

    private void start() {
        Leanplum.start(Leanplum.getContext());
    }

    private void forceContentUpdate() {
        Leanplum.forceContentUpdate();
    }

    private void track(String event, JSONObject properties) throws JSONException {
        Leanplum.track(event, jsonToMap(properties));
    }

    private void setVariables(JSONObject object) throws JSONException {
        Log.d(CLASS_TAG, "setVariables");
        for (Map.Entry<String, Object> entry : jsonToMap(object).entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            variables.put(key, Var.define(key, value));
        }
    }

    private void getVariable(String name, CallbackContext callbackContext) {
        if (variables.containsKey(name)) {
            Object variableValue = getVariableValue(name);

            try {
                JSONObject data = new JSONObject();

                data.put("value", variableValue);
                callbackContext.success(data);
            } catch (JSONException e) {
                e.printStackTrace();
                callbackContext.error("There was an error reading the variable: " + name);
            }

        } else {
            callbackContext.error("Variable " + name + " not found, please sure that you create it before.");
        }
    }

    private void getVariables(CallbackContext callbackContext) {
        try {
            callbackContext.success(getVariablesValues());
        } catch (JSONException e) {
            e.printStackTrace();
            callbackContext.error("There was an error reading the variables");
        }
    }

    private void onStartResponse(final CallbackContext callbackContext) {
        Leanplum.addStartResponseHandler(new StartCallback() {
            @Override
            public void onResponse(boolean success) {
                Log.d(CLASS_TAG, "onStartResponse");
                JSONObject data = new JSONObject();
                try {
                    data.put("started", success);
                    callbackContext.success(data);
                } catch (JSONException e) {
                    e.printStackTrace();
                    callbackContext.error("There was an error (onStartResponse) callback");
                }
            }
        });
    }

    private void onValueChanged(final String name, final CallbackContext callbackContext) {
        Var<Object> var = (Var<Object>) variables.get(name);

        var.addValueChangedHandler(new VariableCallback<Object>() {
            @Override
            public void handle(Var<Object> var) {
                try {
                    JSONObject data = new JSONObject();
                    Log.d(CLASS_TAG, "onValueChanged: " + name);

                    data.put("value", getVariableValue(name));
                    PluginResult pluginResultOk = new PluginResult(PluginResult.Status.OK, data);
                    pluginResultOk.setKeepCallback(true);
                    callbackContext.sendPluginResult(pluginResultOk);
                } catch (JSONException e) {
                    e.printStackTrace();
                    PluginResult pluginResultError = new PluginResult(
                            PluginResult.Status.ERROR,
                            "There was an error handling variable: " + name + "change"
                    );
                    pluginResultError.setKeepCallback(true);
                    callbackContext.sendPluginResult(pluginResultError);

                }

            }
        });
    }

    private void onVariablesChanged(final CallbackContext callbackContext) {
        Leanplum.addVariablesChangedHandler(new VariablesChangedCallback() {
            @Override
            public void variablesChanged() {
                try {
                    PluginResult pluginResultOk = new PluginResult(
                            PluginResult.Status.OK,
                            getVariablesValues()
                    );
                    pluginResultOk.setKeepCallback(true);
                    callbackContext.sendPluginResult(pluginResultOk);
                } catch (JSONException e) {
                    e.printStackTrace();
                    PluginResult pluginResultError = new PluginResult(
                            PluginResult.Status.ERROR,
                            "There was an error handling variables changes"
                    );
                    pluginResultError.setKeepCallback(true);
                    callbackContext.sendPluginResult(pluginResultError);

                }
            }
        });
    }

    private Object getVariableValue(String name) {
        Log.d(CLASS_TAG, "getVariableValue: " + name);
        if (variables.containsKey(name)) {
            Var<?> variable = (Var<?>) variables.get(name);
            Object variableValue = variable.value();
            String kind = variable.kind();

            if (Constants.Kinds.DICTIONARY.equals(kind)) {
                return new JSONObject((Map) variableValue);
            } else if (Constants.Kinds.ARRAY.equals(kind)) {
                return new JSONArray((Collection) variableValue);
            } else {
                return variableValue;
            }
        } else {
            return new JSONObject();
        }
    }

    private JSONObject getVariablesValues() throws JSONException {
        JSONObject data = new JSONObject();

        for (Map.Entry<String, Object> entry : variables.entrySet()) {
            String key = entry.getKey();
            Var<?> variable = (Var<?>) entry.getValue();
            String variableKind = variable.kind();

            if (variableKind == Constants.Kinds.FILE) {
                continue;
            } else if (Constants.Kinds.DICTIONARY.equals(variableKind)) {
                JSONObject valueToAdd = new JSONObject((Map) variable.value());
                data.put(key, valueToAdd);
            } else if (Constants.Kinds.ARRAY.equals(variableKind)) {
                JSONArray valueToAdd = new JSONArray((Collection) variable.value());
                data.put(key, valueToAdd);
            } else {
                data.put(key, variable.value());
            }
        }
        return data;
    }

    public Map<String, Object> jsonToMap(JSONObject json) throws JSONException {
        Map<String, Object> retMap = new HashMap<String, Object>();

        if(json != JSONObject.NULL) {
            retMap = toMap(json);
        }
        return retMap;
    }

    public Map<String, Object> toMap(JSONObject object) throws JSONException {
        Map<String, Object> map = new HashMap<String, Object>();

        @SuppressWarnings("unchecked")
        Iterator<String> keysItr = object.keys();
        while(keysItr.hasNext()) {
            String key = keysItr.next();
            Object value = object.get(key);
            if(value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }
            map.put(key, value);
        }
        return map;
    }
}
