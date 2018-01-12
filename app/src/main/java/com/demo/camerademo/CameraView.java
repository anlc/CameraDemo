package com.demo.camerademo;

import android.content.Context;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Created by Administrator on 2018/1/8.
 */

public class CameraView extends SurfaceView implements SurfaceHolder.Callback {

    private static final SparseIntArray DEFAULT_ORIENTATIONS = new SparseIntArray();
    private static final SparseIntArray INVERSE_ORIENTATIONS = new SparseIntArray();

    private SurfaceHolder holder;
    private Camera camera;
    private Camera.Parameters parameters;
    private Camera.Size size;

    private WindowManager windowManager;
    private DisplayMetrics displayMetrics;
    private int cameraId = 0;

    static {
        DEFAULT_ORIENTATIONS.append(Surface.ROTATION_0, 90);
        DEFAULT_ORIENTATIONS.append(Surface.ROTATION_90, 0);
        DEFAULT_ORIENTATIONS.append(Surface.ROTATION_180, 270);
        DEFAULT_ORIENTATIONS.append(Surface.ROTATION_270, 180);

        INVERSE_ORIENTATIONS.append(Surface.ROTATION_0, 270);
        INVERSE_ORIENTATIONS.append(Surface.ROTATION_90, 180);
        INVERSE_ORIENTATIONS.append(Surface.ROTATION_180, 90);
        INVERSE_ORIENTATIONS.append(Surface.ROTATION_270, 0);
    }

    public CameraView(Context context, WindowManager windowManager) {
        super(context);
        displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        this.windowManager = windowManager;
        this.camera = getCamera();
        holder = getHolder();
        holder.addCallback(this);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        initParams();
    }

    private Camera getCamera() {
        Camera camera = null;
        try {
            camera = Camera.open();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return camera;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            camera.setPreviewDisplay(holder);
            camera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (this.holder.getSurface() == null) {
            return;
        }
        try {
            camera.stopPreview();

            Camera.Size cameraSize = getCameraSize();
            camera.getParameters().setPreviewSize(cameraSize.width, cameraSize.height);
            startCamera();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initParams() {
        parameters = camera.getParameters();
        Camera.Size cameraSize = getCameraSize();
        parameters.setPictureSize(cameraSize.width, cameraSize.height);
        int rotation = windowManager.getDefaultDisplay().getRotation();
        parameters.setRotation(DEFAULT_ORIENTATIONS.get(rotation));
        boolean autoFocus = parameters.getSupportedFocusModes().contains(Camera.Parameters.FOCUS_MODE_AUTO);
        final List<String> modes = parameters.getSupportedFocusModes();
        if (autoFocus && modes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);//1连续对焦
        } else if (modes.contains(Camera.Parameters.FOCUS_MODE_FIXED)) {
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_FIXED);
        } else if (modes.contains(Camera.Parameters.FOCUS_MODE_INFINITY)) {
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_INFINITY);
        } else {
            parameters.setFocusMode(modes.get(0));
        }
        camera.setParameters(parameters);
    }

    //获取适合屏幕的宽高
    public Camera.Size getCameraSize() {
        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;
        float windowRatio = width / height;
        float minRatio = Integer.MAX_VALUE;

        List<Camera.Size> sizeList = camera.getParameters().getSupportedPreviewSizes();
        Camera.Size result = sizeList.get(sizeList.size() - 1);
        for (int i = 0; i < sizeList.size(); i++) {
            if (width == sizeList.get(i).width || width == sizeList.get(i).height) {
                return size = sizeList.get(i);
            }
            float ratio = sizeList.get(i).width / height;
            if (minRatio > Math.abs(windowRatio - ratio)) {
                result = sizeList.get(i);
            }
        }
        return size = result;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    //切换摄像头
    public void switchCamera() {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            cameraId = 0;
        } else {
            cameraId = 1;
        }
        try {
            camera.stopPreview();
            camera.release();
            camera = Camera.open(cameraId);
            startCamera();
            initParams();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startCamera() throws IOException {
        camera.setPreviewDisplay(holder);
        camera.startPreview();
        camera.setDisplayOrientation(90);
    }

    public void release() {
        camera.stopPreview();
        camera.release();
    }

    public void takePicture() {
        camera.takePicture(null, null, pictureCallback);
    }

    private Camera.PictureCallback pictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            File pictureFile = new File(getOutputPicFilePath());
            FileOutputStream outputStream = null;
            try {
                outputStream = new FileOutputStream(pictureFile);
                outputStream.write(data);
                outputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (outputStream != null) {
                    try {
                        outputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            camera.cancelAutoFocus();
            camera.startPreview();
        }
    };

    private String getOutputPicFilePath() {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath() + "/camera/picture.jpg";
    }

    private String getOutputVideoFilePath(){
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES).getAbsolutePath() + "/test.mp4";
    }

    public boolean startVideo(){
        if(prepareVideoRecorder()){
            mediaRecorder.start();
            return true;
        }else{
            return false;
        }
    }

    public boolean stopVideo(){
        mediaRecorder.stop();
        releaseMediaRecorder();
        camera.lock();
        return false;
    }

    private void releaseMediaRecorder(){
        if (mediaRecorder != null) {
            mediaRecorder.reset();   // clear recorder configuration
            mediaRecorder.release(); // release the recorder object
            mediaRecorder = null;
            camera.lock();           // lock camera for later use
        }
    }

    private MediaRecorder mediaRecorder;
    private boolean prepareVideoRecorder(){

        mediaRecorder = new MediaRecorder();

        camera.unlock();
        mediaRecorder.setCamera(camera);

        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);

        CamcorderProfile profile = CamcorderProfile.get(CamcorderProfile.QUALITY_720P);
        mediaRecorder.setOutputFile(getOutputVideoFilePath());
        mediaRecorder.setVideoEncodingBitRate(profile.videoBitRate);
        mediaRecorder.setVideoFrameRate(profile.videoFrameRate);
        mediaRecorder.setVideoSize(size.width, size.height);
        mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.DEFAULT);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);

        int rotation = windowManager.getDefaultDisplay().getRotation();
        mediaRecorder.setOrientationHint(DEFAULT_ORIENTATIONS.get(rotation));
        mediaRecorder.setPreviewDisplay(holder.getSurface());

        try {
            mediaRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
