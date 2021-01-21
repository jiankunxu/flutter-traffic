package com.baidu;

import android.graphics.Bitmap;

/**
 * Created by kongfanqun on 2019/5/20.
 */

public class MyCamataBack {
    private static MyCamataBack myCamataBack = null;
    public interface OnMyCamataBack{
        public void back(Bitmap data, String path);
    }
    MyCamataBack.OnMyCamataBack onMyCamataBack;

    public MyCamataBack.OnMyCamataBack getOnMyCamataBack() {
        return onMyCamataBack;
    }

    public void setOnMyCamataBack(MyCamataBack.OnMyCamataBack onMyCamataBack) {
        this.onMyCamataBack = onMyCamataBack;
    }

    public static MyCamataBack getInstance(){
        if(myCamataBack==null){
            myCamataBack = new MyCamataBack();
        }
        return myCamataBack;
    }
}
