package com.demo.camerademo;

import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private CameraView cameraView;
    private Button button_take_video;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (checkCameraHardware()) {

//            cameraView = new CameraView(this, getWindowManager());

            FrameLayout preview = findViewById(R.id.camera_preview);
//            preview.addView(cameraView);
            if (Build.VERSION.SDK_INT > 21) {
                preview.addView(new CameraView2(this, this));
            }

//            Camera.Size cameraSize = cameraView.getCameraSize();
//            preview.getLayoutParams().width = cameraSize.height;
//            preview.getLayoutParams().height = cameraSize.width;
//            button_take_video = findViewById(R.id.button_take_video);
        } else {
            showMsg();
        }
    }

    private boolean videoing = false;

    public void onClick(View view) {
        if (!checkCameraHardware()) {
            showMsg();
        }
        switch (view.getId()) {
            case R.id.button_tran://转换摄像头
                cameraView.switchCamera();
                break;
            case R.id.button_take_pic://拍照
                cameraView.takePicture();
                break;
            case R.id.button_take_video://录像
                if (videoing) {
                    videoing = cameraView.stopVideo();
                    button_take_video.setText("开始");
                } else {
                    videoing = cameraView.startVideo();
                    button_take_video.setText(videoing ? "停止" : "开始");
                }
                break;
        }
    }

    private boolean checkCameraHardware() {
        if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            return true;
        }
        return false;
    }

    private void showMsg() {
        Toast.makeText(this, "打开摄像头失败", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraView.release();
    }

    private void tackPicture() {

    }
}
