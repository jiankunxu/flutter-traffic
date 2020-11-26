package com.zknext.traffic;

import android.content.Context;

import com.hdgq.locationlib.LocationOpenApi;
import com.hdgq.locationlib.constant.Constants;
import com.hdgq.locationlib.entity.ShippingNoteInfo;
import com.hdgq.locationlib.listener.OnResultListener;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by kongfanqun on 2020/3/20.
 */

public class JiaoTongJu {
    public static void startSDK(Context context, String data,OnResultListener listener) {
        try {
            JSONObject jsonObject = new JSONObject(data);
            ShippingNoteInfo shippingNoteInfo = new ShippingNoteInfo();
            shippingNoteInfo.setShippingNoteNumber(jsonObject.optString("billNum"));
            shippingNoteInfo.setSerialNumber("0000");
            shippingNoteInfo.setStartCountrySubdivisionCode(jsonObject.optString("loadingAreaCode"));
            shippingNoteInfo.setEndCountrySubdivisionCode(jsonObject.optString("unloadingAreaCode"));
            ShippingNoteInfo[] shippingNoteInfos = new ShippingNoteInfo[1];
            shippingNoteInfos[0] = shippingNoteInfo;
            /*开始*/
            LocationOpenApi.start(context, shippingNoteInfos, listener);
        }catch (JSONException e){}
    }

    public static void stopSDK(Context context, String data,OnResultListener listener) {
        try {
            JSONObject jsonObject = new JSONObject(data);
            ShippingNoteInfo shippingNoteInfo = new ShippingNoteInfo();
            shippingNoteInfo.setShippingNoteNumber(jsonObject.optString("billNum"));
            shippingNoteInfo.setSerialNumber("0000");
            shippingNoteInfo.setStartCountrySubdivisionCode(jsonObject.optString("loadingAreaCode"));
            shippingNoteInfo.setEndCountrySubdivisionCode(jsonObject.optString("unloadingAreaCode"));
            ShippingNoteInfo[] shippingNoteInfos = new ShippingNoteInfo[1];
            shippingNoteInfos[0] = shippingNoteInfo ;
            LocationOpenApi.stop(context, shippingNoteInfos, listener);
        }catch (JSONException e){}

    }
    public static void InitSDK(Context context,String appId,String appSecurity,String enterpriseSenderCode,String type,OnResultListener listener) {
        try {
            LocationOpenApi.init(context, appId, appSecurity, enterpriseSenderCode, type, listener);
        }catch (Exception e){

        }
    }
}
