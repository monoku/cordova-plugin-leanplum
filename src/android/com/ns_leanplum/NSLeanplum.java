package com.monoku.leanplum;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import org.json.JSONArray;

import android.util.Log;

public class NSLeanplum extends CordovaPlugin {
  private static final String TAG = "NSLeanplum";

  @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        Log.d(TAG, "execute: " + action);
        return false;
    }
}