// You have generated a new plugin project without
// specifying the `--platforms` flag. A plugin project supports no platforms is generated.
// To add platforms, run `flutter create -t plugin --platforms <platforms> .` under the same
// directory. You can also find a detailed instruction on how to add platforms in the `pubspec.yaml` at https://flutter.dev/docs/development/packages-and-plugins/developing-packages#plugin-platforms.

import 'dart:async';

import 'package:flutter/services.dart';

class Traffic {
  static const MethodChannel _channel =
      const MethodChannel('traffic');

  static Future<String> get platformVersion async {
    final String version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }
  static Future<String> initSDK(String appId,String appSecurity,String enterpriseSenderCode,String type) async {
    Map argument = {
      'appId': appId,
      "appSecurity":appSecurity,
      "enterpriseSenderCode":enterpriseSenderCode,
      "type":type
    };
    final String version = await _channel.invokeMethod('initSDK',argument);
    return version;
  }
  static Future<String> startSDK(String billNum,String loadingAreaCode,String unloadingAreaCode) async {
    Map argument = {
      'billNum': billNum,
      "loadingAreaCode":loadingAreaCode,
      "unloadingAreaCode":unloadingAreaCode,
    };
    final String version = await _channel.invokeMethod('startSDK',argument);
    return version;
  }
  static Future<String> stopSDK(String billNum,String loadingAreaCode,String unloadingAreaCode) async {
    Map argument = {
      'billNum': billNum,
      "loadingAreaCode":loadingAreaCode,
      "unloadingAreaCode":unloadingAreaCode,
    };
    final String version = await _channel.invokeMethod('stopSDK',argument);
    return version;
  }
  static Future<void> initBaiduOrc(String apiKey,String secretKey) async {
    Map argument = {
      'apiKey': apiKey,
      "secretKey":secretKey,
    };
    await _channel.invokeMethod('initBaiduOrc',argument);
  }
  static Future<String> ocrDrivingLicense(String billNum,String loadingAreaCode,String unloadingAreaCode) async {
    Map argument = {
    };
    await _channel.invokeMethod('ocrDrivingLicense',argument);
    return "";
  }
}
