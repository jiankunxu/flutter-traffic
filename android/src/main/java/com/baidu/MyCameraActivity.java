package com.baidu;


import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import androidx.core.app.ActivityCompat;
import android.view.Surface;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.baidu.idcardquality.IDcardQualityProcess;
import com.baidu.ocr.ui.R;
import com.baidu.ocr.ui.camera.CameraThreadPool;
import com.baidu.ocr.ui.camera.ICameraControl;
import com.baidu.ocr.ui.camera.MaskView;
import com.baidu.ocr.ui.camera.OCRCameraLayout;
import com.baidu.ocr.ui.camera.PermissionCallback;
import com.baidu.ocr.ui.crop.CropView;
import com.baidu.ocr.ui.crop.FrameOverlayView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


/**
 * 拍照界面 (正方形)
 */
public class MyCameraActivity extends Activity {
    public static final String KEY_OUTPUT_FILE_PATH = "outputFilePath";
    public static final String KEY_CONTENT_TYPE = "contentType";

    public static final String CONTENT_TYPE_GENERAL = "general";

    public static final String CONTENT_TYPE_ID_CARD_FRONT_MANUAL = "IDCardFront_MANUAL"; // 身份证
    // 正面 手动
    public static final String CONTENT_TYPE_ID_CARD_BACK_MANUAL = "IDCardBack_MANUAL"; //身份证 反面 手动


    public static final String CONTENT_TYPE_NONE_RECTANGLE = "rectangle"; //长方形
    public static final String CONTENT_TYPE_NONE_SQUARE = "square"; //正方形 (拍照后手动裁切 )


    public final static String TYPE_FRONT = "1";  //前置
    public final static String TYPE_BACK = "0";  //后置
    public static String TYPE = MyCameraActivity.TYPE_FRONT;// 默认前置
    public static String ISSHOWTYPE = "1";// 默认 显示翻转按钮


    private static final int REQUEST_CODE_PICK_IMAGE = 100;
    private static final int PERMISSIONS_REQUEST_CAMERA = 800;
    private static final int PERMISSIONS_EXTERNAL_STORAGE = 801;

    private File outputFile;
    private String contentType;
    private Handler handler = new Handler();
    private OCRCameraLayout takePictureContainer;
    private OCRCameraLayout cropContainer;
    private OCRCameraLayout confirmResultContainer;
    private ImageView lightButton;  //灯 开关按钮
    private CameraView_bd cameraView;
    private ImageView displayImageView;
    private CropView cropView; //裁切视图图片层
    private FrameOverlayView overlayView;// 裁剪 框
    private MyMaskView cropMaskView; //裁剪视图 父类
    private ImageView takePhotoBtn; //拍照 按钮
    private PermissionCallback permissionCallback = new PermissionCallback() {
        @Override
        public boolean onRequestPermission() {
            ActivityCompat.requestPermissions(MyCameraActivity.this,
                    new String[]{Manifest.permission.CAMERA},
                    PERMISSIONS_REQUEST_CAMERA);
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_camera_activity);
        View view = findViewById(R.id.back);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        cameraView = findViewById(R.id.camera_view);
        cameraView.getCameraControl().setPermissionCallback(permissionCallback);
        lightButton = findViewById(R.id.light_button);
        lightButton.setOnClickListener(lightButtonOnClickListener);
        takePhotoBtn = findViewById(R.id.take_photo_button);
        takePhotoBtn.setOnClickListener(takeButtonOnClickListener);

        displayImageView = findViewById(R.id.display_image_view);

        findViewById(R.id.album_button).setOnClickListener(albumButtonOnClickListener);
        findViewById(R.id.rotate_button).setOnClickListener(rotateButtonOnClickListener); //旋转图片

        takePictureContainer = findViewById(R.id.take_picture_container);
        confirmResultContainer = findViewById(R.id.confirm_result_container);
        confirmResultContainer.findViewById(R.id.confirm_button).setOnClickListener(confirmButtonOnClickListener); //确定裁剪
        confirmResultContainer.findViewById(R.id.cancel_button).setOnClickListener(confirmCancelButtonOnClickListener); //取消裁剪

        cropView = findViewById(R.id.crop_view);
        overlayView = findViewById(R.id.overlay_view); //裁剪框

        cropContainer = findViewById(R.id.crop_container);
        cropContainer.findViewById(R.id.confirm_button).setOnClickListener(cropConfirmButtonListener);
        cropContainer.findViewById(R.id.cancel_button).setOnClickListener(cropCancelButtonListener);
        cropMaskView = cropContainer.findViewById(R.id.crop_mask_view);

        setOrientation(getResources().getConfiguration());
        initParams();
        cameraView.setAutoPictureCallback(autoTakePictureCallback);

        //点击翻转 摄像头
        cameraView.hintView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MyCameraActivity.TYPE.equals(MyCameraActivity.TYPE_BACK)) {
                    MyCameraActivity.TYPE = MyCameraActivity.TYPE_FRONT;
                } else {
                    MyCameraActivity.TYPE = MyCameraActivity.TYPE_BACK;
                }
                setShow(); //显示隐藏  闪光灯  按钮
                cameraView.stop();
                cameraView.start();
            }
        });
        setShow(); //显示隐藏  闪光灯  按钮
    }

    /*设置闪光灯*/
    public void setShow() {
        if (TYPE.equals(TYPE_BACK)) {
            lightButton.setVisibility(View.VISIBLE);
        } else {
            lightButton.setVisibility(View.INVISIBLE);
        }
    }

    private void initParams() {
        String outputPath = getIntent().getStringExtra(KEY_OUTPUT_FILE_PATH);
        if (outputPath != null) {
            outputFile = new File(outputPath);
        }

        TYPE = getIntent().getStringExtra(TYPE);
        ISSHOWTYPE = getIntent().getStringExtra(ISSHOWTYPE);
        contentType = getIntent().getStringExtra(KEY_CONTENT_TYPE);
        if (contentType == null) {
            contentType = CONTENT_TYPE_GENERAL;
        }
        int maskType;
        switch (contentType) {

            case CONTENT_TYPE_ID_CARD_FRONT_MANUAL:
                maskType = MaskView.MASK_TYPE_ID_CARD_FRONT_MANUAL;
                overlayView.setVisibility(View.INVISIBLE);
                break;
            case CONTENT_TYPE_ID_CARD_BACK_MANUAL:
                maskType = MaskView.MASK_TYPE_ID_CARD_BACK_MANUAL;
                overlayView.setVisibility(View.INVISIBLE);
                break;

            case CONTENT_TYPE_NONE_RECTANGLE: //长方形
                maskType = MaskView.MASK_TYPE_NONE_RECTANGLE;
                overlayView.setVisibility(View.INVISIBLE);
                break;
            case CONTENT_TYPE_NONE_SQUARE:  //正方形
                maskType = MaskView.MASK_TYPE_NONE_SQUARE;
                overlayView.setVisibility(View.INVISIBLE);
                break;
            case CONTENT_TYPE_GENERAL:
            default:
                maskType = MaskView.MASK_TYPE_NONE;
                cropMaskView.setVisibility(View.INVISIBLE);
                break;
        }

        cameraView.setMaskType(maskType, this);
        cropMaskView.setMaskType(maskType);
    }

    /*显示 照片*/
    private void showTakePicture() {
        cameraView.getCameraControl().resume();
        updateFlashMode();
        takePictureContainer.setVisibility(View.VISIBLE);
        confirmResultContainer.setVisibility(View.INVISIBLE);
        cropContainer.setVisibility(View.INVISIBLE);
    }

    /*显示裁剪*/
    private void showCrop() {
        cameraView.getCameraControl().pause();
        updateFlashMode();
        takePictureContainer.setVisibility(View.INVISIBLE);
        confirmResultContainer.setVisibility(View.INVISIBLE);
        cropContainer.setVisibility(View.VISIBLE);
    }

    /*显示结果确认*/
    private void showResultConfirm() {
        cameraView.getCameraControl().pause(); //暂停
        updateFlashMode();
        takePictureContainer.setVisibility(View.INVISIBLE);
        confirmResultContainer.setVisibility(View.VISIBLE);
        cropContainer.setVisibility(View.INVISIBLE);
    }

    // take photo;
    private void updateFlashMode() {
        int flashMode = cameraView.getCameraControl().getFlashMode();
        if (flashMode == ICameraControl.FLASH_MODE_TORCH) {
            lightButton.setImageResource(R.drawable.bd_ocr_light_on);
        } else {
            lightButton.setImageResource(R.drawable.bd_ocr_light_off);
        }
    }

    /*图库选择*/
    private View.OnClickListener albumButtonOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    ActivityCompat.requestPermissions(MyCameraActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSIONS_EXTERNAL_STORAGE);
                    return;
                }
            }
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE);
        }
    };
    /*灯光*/
    private View.OnClickListener lightButtonOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (cameraView.getCameraControl().getFlashMode() == ICameraControl.FLASH_MODE_OFF) {
                cameraView.getCameraControl().setFlashMode(ICameraControl.FLASH_MODE_TORCH);
            } else {
                cameraView.getCameraControl().setFlashMode(ICameraControl.FLASH_MODE_OFF);
            }
            updateFlashMode();
        }
    };
    /*拍照按钮*/
    private View.OnClickListener takeButtonOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            cameraView.takePicture(outputFile, takePictureCallback);
        }
    };

    private CameraView_bd.OnTakePictureCallback autoTakePictureCallback = new CameraView_bd
            .OnTakePictureCallback() {
        @Override
        public void onPictureTaken(final Bitmap bitmap) {
            CameraThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        FileOutputStream fileOutputStream = new FileOutputStream(outputFile);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
                        bitmap.recycle();
                        fileOutputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Intent intent = new Intent();
                    intent.putExtra(MyCameraActivity.KEY_CONTENT_TYPE, contentType);
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                }
            });
        }
    };

    private CameraView_bd.OnTakePictureCallback takePictureCallback = new CameraView_bd
            .OnTakePictureCallback() {
        @Override
        public void onPictureTaken(final Bitmap bitmap) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    takePictureContainer.setVisibility(View.INVISIBLE);
                    if (cropMaskView.getMaskType() == MaskView.MASK_TYPE_NONE) {
                        cropView.setFilePath(outputFile.getAbsolutePath());
                        showCrop();
                    } else if (cropMaskView.getMaskType() == MaskView.MASK_TYPE_BANK_CARD) {
                        cropView.setFilePath(outputFile.getAbsolutePath());
                        cropMaskView.setVisibility(View.INVISIBLE);
                        overlayView.setVisibility(View.VISIBLE);
                        overlayView.setTypeWide();
                        showCrop();
                    } else {
                        displayImageView.setImageBitmap(bitmap);
                        showResultConfirm();
                    }
                }
            });
        }
    };

    private View.OnClickListener cropCancelButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // 释放 cropView中的bitmap;
            cropView.setFilePath(null);
            showTakePicture();
        }
    };

    /*设置裁剪大小*/
    private View.OnClickListener cropConfirmButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int maskType = cropMaskView.getMaskType();
            Rect rect;
            switch (maskType) {
                case MaskView.MASK_TYPE_BANK_CARD:
                case MaskView.MASK_TYPE_ID_CARD_BACK:
                case MaskView.MASK_TYPE_ID_CARD_FRONT:
                    rect = cropMaskView.getFrameRect();
                    break;
                case MaskView.MASK_TYPE_ID_CARD_BACK_MANUAL:
                    rect = cropMaskView.getFrameRect();
                    break;
                case MaskView.MASK_TYPE_ID_CARD_FRONT_MANUAL:
                    rect = cropMaskView.getFrameRect();
                    break;
                case MaskView.MASK_TYPE_NONE_RECTANGLE:
                    rect = cropMaskView.getFrameRect();
                    break;
                case MaskView.MASK_TYPE_NONE_SQUARE:
                    rect = cropMaskView.getFrameRect();
                    break;
                case MaskView.MASK_TYPE_NONE:
                default:
                    rect = overlayView.getFrameRect();
                    break;
            }
            Bitmap cropped = cropView.crop(rect);
            displayImageView.setImageBitmap(cropped);
            cropAndConfirm();
        }
    };

    private void cropAndConfirm() {
        cameraView.getCameraControl().pause();
        updateFlashMode();
        doConfirmResult();
    }

    private void doConfirmResult() {
        CameraThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    FileOutputStream fileOutputStream = new FileOutputStream(outputFile);
                    Bitmap bitmap = ((BitmapDrawable) displayImageView.getDrawable()).getBitmap();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent();
                intent.putExtra(MyCameraActivity.KEY_CONTENT_TYPE, contentType);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });
    }

    /*确定裁剪*/
    private View.OnClickListener confirmButtonOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            doConfirmResult();
        }
    };

    private View.OnClickListener confirmCancelButtonOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            displayImageView.setImageBitmap(null);
            showTakePicture();
        }
    };

    private View.OnClickListener rotateButtonOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            cropView.rotate(90);
        }
    };

    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = null;
        try {
            cursor = getContentResolver().query(contentURI, null, null, null, null);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        if (cursor == null) {
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }


    private void setOrientation(Configuration newConfig) {
        int rotation = getWindowManager().getDefaultDisplay().getRotation();
        int orientation;
        int cameraViewOrientation = CameraView_bd.ORIENTATION_PORTRAIT;
        switch (newConfig.orientation) {
            case Configuration.ORIENTATION_PORTRAIT:
                cameraViewOrientation = CameraView_bd.ORIENTATION_PORTRAIT;
                orientation = OCRCameraLayout.ORIENTATION_PORTRAIT;
                break;
            case Configuration.ORIENTATION_LANDSCAPE:
                orientation = OCRCameraLayout.ORIENTATION_HORIZONTAL;
                if (rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_90) {
                    cameraViewOrientation = CameraView_bd.ORIENTATION_HORIZONTAL;
                } else {
                    cameraViewOrientation = CameraView_bd.ORIENTATION_INVERT;
                }
                break;
            default:
                orientation = OCRCameraLayout.ORIENTATION_PORTRAIT;
                cameraView.setOrientation(CameraView_bd.ORIENTATION_PORTRAIT);
                break;
        }
        takePictureContainer.setOrientation(orientation);
        cameraView.setOrientation(cameraViewOrientation);
        cropContainer.setOrientation(orientation);
        confirmResultContainer.setOrientation(orientation);
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setOrientation(newConfig);
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onPause() {
        super.onPause();
        cameraView.stop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        cameraView.start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PICK_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                Uri uri = data.getData();
                cropView.setFilePath(getRealPathFromURI(uri));
                showCrop();
            } else {
                cameraView.getCameraControl().resume();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CAMERA: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager
                        .PERMISSION_GRANTED) {
                    cameraView.getCameraControl().refreshPermission();
                } else {
                    Toast.makeText(getApplicationContext(), R.string.camera_permission_required,
                            Toast.LENGTH_LONG)
                            .show();
                }
                break;
            }
            case PERMISSIONS_EXTERNAL_STORAGE:
            default:
                break;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        CameraThreadPool.cancelAutoFocusTimer();
        IDcardQualityProcess.getInstance().releaseModel();
    }
}



