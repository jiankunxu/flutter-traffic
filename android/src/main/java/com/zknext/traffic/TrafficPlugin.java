package com.zknext.traffic;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;

import com.hdgq.locationlib.listener.OnResultListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;
import java.util.Set;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;

/** TrafficPlugin */
public class TrafficPlugin implements FlutterPlugin, MethodCallHandler, ActivityAware {
  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  Context vContext;

  private MethodChannel channel;

  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
//    vContext=flutterPluginBinding.getApplicationContext();
    channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "traffic");
    channel.setMethodCallHandler(this);
  }
  @Override
  public void onMethodCall(@NonNull MethodCall call, @NonNull final Result result) {
    Log.e("result",call.method);
    switch (call.method) {
      case "getPlatformVersion":
        result.success("Android " + android.os.Build.VERSION.RELEASE);
        break;
      case "initSDK":
        String appId =call.argument("appId");
        String appSecurity =call.argument("appSecurity");
        String enterpriseSenderCode = call.argument("enterpriseSenderCode");
        String type=call.argument("type");
        JiaoTongJu.InitSDK(vContext, appId, appSecurity, enterpriseSenderCode,type, new OnResultListener() {
          @Override
          public void onSuccess() {
            result.success("success");
            return;
          }
          @Override
          public void onFailure(String s, String s1) {
            result.success(s);
            return;
          }
        });
        break;
      case "startSDK":
        JSONObject argumentObj=new JSONObject();
        try {
          argumentObj.put("billNum",call.argument("billNum"));
          argumentObj.put("loadingAreaCode",call.argument("loadingAreaCode"));
          argumentObj.put("unloadingAreaCode",call.argument("unloadingAreaCode"));
        } catch (JSONException e) {
          e.printStackTrace();
        }
        JiaoTongJu.startSDK(vContext, argumentObj.toString(), new OnResultListener() {
          @Override
          public void onSuccess() {
            result.success("success");
            return;
          }

          @Override
          public void onFailure(String s, String s1) {
            result.success(s);
            return;
          }
        });
        break;
      case "stopSDK":
        JSONObject argumentObj2=new JSONObject();
        try {
          argumentObj2.put("billNum",call.argument("billNum"));
          argumentObj2.put("loadingAreaCode",call.argument("loadingAreaCode"));
          argumentObj2.put("unloadingAreaCode",call.argument("unloadingAreaCode"));
        } catch (JSONException e) {
          e.printStackTrace();
        }
        JiaoTongJu.stopSDK(vContext, argumentObj2.toString(), new OnResultListener() {
          @Override
          public void onSuccess() {
            result.success("success");
            return;
          }

          @Override
          public void onFailure(String s, String s1) {
            result.success(s);
            return;
          }
        });
        break;
      default:
        result.notImplemented();
        break;
    }


//
//    try {
//
//
//
//      if (call.method.equals("getPlatformVersion")) {
//
//      } if(call.method.equals("")){
//
//      }if(call.method.equals("startSDK")){
//
//      }if(call.method.equals("")){
//      }else {
//        result.notImplemented();
//      }
//    } catch (Exception e) {
////      result.error("Exception encountered", call.method, e);
//    }
  }

  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    channel.setMethodCallHandler(null);
  }

  @Override
  public void onAttachedToActivity(@NonNull ActivityPluginBinding binding) {
    vContext=binding.getActivity();
  }

  @Override
  public void onDetachedFromActivityForConfigChanges() {

  }

  @Override
  public void onReattachedToActivityForConfigChanges(@NonNull ActivityPluginBinding binding) {

  }

  @Override
  public void onDetachedFromActivity() {
    vContext=null;
  }
}
