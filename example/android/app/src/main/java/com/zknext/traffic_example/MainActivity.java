package com.zknext.traffic_example;

import android.content.Context;

import io.flutter.embedding.android.FlutterActivity;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.plugins.GeneratedPluginRegistrant;

/***
 * 插件源码
 */
public class MainActivity extends FlutterActivity {
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        GeneratedPluginRegistrant.registerWith(this,this);
////        new TrafficPlugin(context);
//    }

    @Override
    public void configureFlutterEngine(FlutterEngine flutterEngine){
        Context context=this;
        GeneratedPluginRegistrant.registerWith(flutterEngine);
    }

//    private static final String CHANNEL = "com.zknext.traffic/authority";
//    private MethodChannel methodChannel;
//    @Override
//    public void configureFlutterEngine(FlutterEngine flutterEngine){
//        GeneratedPluginRegistrant.registerWith(flutterEngine);
//        methodChannel = new MethodChannel(flutterEngine.getDartExecutor(), CHANNEL);
//        methodChannel.setMethodCallHandler(
//                new MethodChannel.MethodCallHandler() {
//                    @Override
//                    public void onMethodCall(MethodCall call, MethodChannel.Result result) {
//                        if (call.method.equals("")) {
//                            //do something
//                        } else {
//                            //没有对应方法
//                            result.notImplemented();
//                        }
//                    }
//                }
//        );
//    }
}
