package com.zknext.traffic;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;

import com.baidu.BaiduOCR_Manage;
import com.baidu.ocr.ui.camera.CameraActivity;
import com.hdgq.locationlib.listener.OnResultListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
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
  private int REQUEST_HEAD = 100;   //头像
  private int REQUEST_CAR = 101;   //车辆照片
  private int REQUEST_CODE_CAMERA = 102;   //身份证
  private int REQUEST_CODE_DRIVING_LICENSE = 103; //驾驶证
  private int REQUEST_CODE_VEHICLE_LICENSE = 104; //行驶证
  private int REQUEST_CODE_VEHICLE_LICENSE_Trailer = 105; //挂车 行驶证
  private int REQUEST_CODE_SERVICE_LICENSE = 106; //从业资格证
  private int REQUEST_CODE_TRANSPORT_LICENSE = 107; //运输证号

  private String headImg = "headImg";
  private String carImg = "carImg";
  private String id_licenceImg = "id_licenceImg";
  private String drive_licenceImg = "drive_licenceImg";
  private String travel_licenceImg = "travel_licenceImg";
  private String travel_licence_TrailerImg = "travel_licence_TrailerImg";
  private String service_Img = "service_Img";
  private String transport_Img = "transport_Img";
  private String imageEnd = ".jpg";
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
      case "initBaiduOrc":
          BaiduOCR_Manage.initBaiduOrc((Application) vContext.getApplicationContext(), "XU0nBdCQY5EYhUhCUNLjNeFG", "46FXm2zdHwsLVMdNSqoNG7HMErPQGorU");
        break;
      case "ocrDrivingLicense":
          File file = new File(vContext.getFilesDir(), drive_licenceImg + imageEnd);
          Intent intent = new Intent(vContext, CameraActivity.class);
          intent.putExtra(CameraActivity.KEY_OUTPUT_FILE_PATH, file.getAbsolutePath());
          intent.putExtra(CameraActivity.KEY_CONTENT_TYPE, CameraActivity.CONTENT_TYPE_NONE_RECTANGLE);
          Activity activity = (Activity) vContext;
          activity.startActivityForResult(intent, REQUEST_CODE_DRIVING_LICENSE);
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
