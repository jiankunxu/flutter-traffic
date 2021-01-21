package com.baidu;

import android.app.Application;

import com.baidu.ocr.sdk.OCR;
import com.baidu.ocr.sdk.OnResultListener;
import com.baidu.ocr.sdk.exception.OCRError;
import com.baidu.ocr.sdk.model.AccessToken;
import com.zkwl.base.manage.BaseManage;

/**
 * Created by kongfanqun on 2019/12/18.
 */

public class BaiduOCR_Manage {
    /*初始化数据*/
    //百度识别
    public static void initBaiduOrc(Application application, String api_key, String secret_key) {
        OCR.getInstance(application).initAccessTokenWithAkSk(new OnResultListener<AccessToken>() {
            @Override
            public void onResult(AccessToken result) {
                String token = result.getAccessToken();
                BaseManage.hasGotToken = true;
            }

            @Override
            public void onError(OCRError error) {
                error.printStackTrace();
                BaseManage.hasGotToken = false;
            }
        }, application, api_key, secret_key);
    }
}
