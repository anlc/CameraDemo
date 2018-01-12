package com.demo.camerademo;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.TextureView;
import android.view.TextureView.SurfaceTextureListener;

/**
 * Created by Administrator on 2018/1/12.
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class CameraView2 extends TextureView implements SurfaceTextureListener {

    Activity activity;
    CameraManager cameraManager;
    int cameraId = 0;

    public CameraView2(Context context, Activity activity) {
        super(context);
        this.activity = activity;
        setSurfaceTextureListener(this);
    }

    private void init(int width, int height) {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
        }
        try {

            cameraManager = (CameraManager) activity.getSystemService(Context.CAMERA_SERVICE);
            for (String cameraId : cameraManager.getCameraIdList()) {
                CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(cameraId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        Log.e("tag", "onSurfaceTextureAvailable    width:" + width + "  height:" + height);
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        Log.e("tag", "onSurfaceTextureSizeChanged    width:" + width + "  height:" + height);
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    private final CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {

        }

        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {

        }

        @Override
        public void onError(@NonNull CameraDevice camera, int error) {

        }
    };
}
