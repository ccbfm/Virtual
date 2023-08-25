package com.virtual.generic.camera;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.OutputConfiguration;
import android.hardware.camera2.params.SessionConfiguration;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.text.TextUtils;
import android.util.Log;
import android.util.Size;
import android.view.Surface;

import androidx.annotation.NonNull;

import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class Camera2Manager {
    private static final String TAG = "Camera2Manager";
    private final CameraManager mCameraManager;
    private final ImageCallback mImageCallback;
    private CameraDevice mCameraDevice;
    private final Semaphore mCameraOpenCloseLock = new Semaphore(1);
    private CameraParam mCameraParam;
    private HandlerThread mCameraThread, mImageThread;
    private ImageReader mImageReader;
    private List<SurfaceTexture> mSurfaceTextures;

    public Camera2Manager(Context context, ImageCallback imageCallback) {
        mCameraManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
        mImageCallback = imageCallback;
    }

    public void setSurfaceTextures(List<SurfaceTexture> surfaceTextures) {
        mSurfaceTextures = surfaceTextures;
    }

    public void setSurfaceTextures(SurfaceTexture... surfaceTextures) {
        mSurfaceTextures = Arrays.asList(surfaceTextures);
    }

    public void startPreview(@Facing int facing, Size needSize) {
        CameraParam cameraParam = chooseCameraId(facing, needSize);
        if (cameraParam == null) {
            return;
        }
        mCameraParam = cameraParam;
        startPreview();
    }

    private void startPreview() {
        if (mCameraParam != null) {
            Log.d(TAG, "startPreview");
            CameraParam cameraParam = mCameraParam;
            mCameraThread = new HandlerThread("CameraThread");
            mCameraThread.start();
            Handler cameraHandler = new Handler(mCameraThread.getLooper());

            mImageThread = new HandlerThread("ImageThread");
            mImageThread.start();
            Handler imageHandler = new Handler(mImageThread.getLooper());

            prepareImageReader(cameraParam.size, imageHandler);
            openCamera(cameraParam.cameraId, cameraHandler);
        }
    }

    public void stopPreview() {
        Log.d(TAG, "stopPreview");
        closeCamera();
        if (mCameraThread != null) {
            mCameraThread.quitSafely();
            mCameraThread = null;
        }

        if (mImageThread != null) {
            mImageThread.quitSafely();
            mImageThread = null;
        }
    }

    private void resetPreview() {
        stopPreview();
        startPreview();
    }

    private CameraParam chooseCameraId(int facing, Size needSize) {
        try {
            String[] ids = mCameraManager.getCameraIdList();
            if (ids.length == 0) {
                Log.e(TAG, "No available camera.");
                return null;
            }
            for (String cameraId : ids) {
                CameraCharacteristics characteristics = mCameraManager.getCameraCharacteristics(cameraId);
                Integer internal = characteristics.get(CameraCharacteristics.LENS_FACING);
                if (internal == null) {
                    continue;
                }
                if (internal == facing) {
                    StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                    if (map == null) {
                        continue;
                    }
                    Size[] supportedUtilSizes = map.getOutputSizes(ImageFormat.YUV_420_888);
                    if (supportedUtilSizes == null) {
                        continue;
                    }
                    Size supportedSize = getRightSupportedSize(supportedUtilSizes, needSize);
                    if (supportedSize == null) {
                        continue;
                    }

                    Integer level = characteristics.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL);
                    if (level == null || level == CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LEGACY) {
                        continue;
                    }
                    return new CameraParam(cameraId, characteristics, supportedSize);
                }
            }
        } catch (Throwable throwable) {
            Log.e(TAG, "chooseCameraId Throwable ", throwable);
        }
        return null;
    }

    private void prepareImageReader(Size size, Handler imageHandler) {
        if (mImageReader != null) {
            mImageReader.close();
        }
        Log.d(TAG, "prepareImageReader size: " + size);
        mImageReader = ImageReader.newInstance(size.getWidth(), size.getHeight(), ImageFormat.YUV_420_888, 1);
        mImageReader.setOnImageAvailableListener(new ImageReader.OnImageAvailableListener() {
            @Override
            public void onImageAvailable(ImageReader reader) {
                Log.d(TAG, "onImageAvailable reader: " + reader + " " + Thread.currentThread());
                if (mImageCallback != null) {
                    mImageCallback.onImage(reader.acquireNextImage());
                }
            }
        }, imageHandler);
    }

    @SuppressLint("MissingPermission")
    private void openCamera(final String cameraId, final Handler cameraHandler) {
        if (TextUtils.isEmpty(cameraId)) {
            Log.e(TAG, "Open camera failed. No camera available");
            return;
        }
        try {
            if (!mCameraOpenCloseLock.tryAcquire(2500, TimeUnit.MILLISECONDS)) {
                throw new RuntimeException("Time out waiting to lock camera opening.");
            }
            mCameraManager.openCamera(cameraId, new CameraDevice.StateCallback() {
                @Override
                public void onOpened(@NonNull CameraDevice camera) {
                    Log.d(TAG, "Camera onOpened " + camera + " " + Thread.currentThread());
                    mCameraOpenCloseLock.release();
                    mCameraDevice = camera;
                    startCaptureSession(camera, cameraHandler);
                }

                @Override
                public void onDisconnected(@NonNull CameraDevice camera) {
                    Log.d(TAG, "Camera onDisconnected " + camera);
                    mCameraOpenCloseLock.release();
                    camera.close();
                    mCameraDevice = null;
                }


                @Override
                public void onError(@NonNull CameraDevice camera, int error) {
                    Log.d(TAG, "Camera onError " + error + " " + camera);
                    mCameraOpenCloseLock.release();
                    camera.close();
                    mCameraDevice = null;
                    //resetPreview();
                }
            }, cameraHandler);
        } catch (Throwable throwable) {
            Log.e(TAG, "Open camera Throwable.", throwable);
        }
    }

    private void closeCamera() {
        try {
            mCameraOpenCloseLock.acquire();
            if (mCameraDevice != null) {
                mCameraDevice.close();
                mCameraDevice = null;
            }
            if (mImageReader != null) {
                mImageReader.close();
                mImageReader = null;
            }
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted while trying to lock camera closing.", e);
        } finally {
            mCameraOpenCloseLock.release();
        }
    }

    private void startCaptureSession(CameraDevice cameraDevice, Handler cameraHandler) {
        try {
            if (mImageReader == null) {
                return;
            }
            CaptureRequest.Builder builder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            builder.addTarget(mImageReader.getSurface());

            CameraCaptureSession.StateCallback stateCallback = new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession session) {
                    Log.e(TAG, "onConfigured session " + session);
                    try {
                        builder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_OFF);
                        session.setRepeatingRequest(builder.build(), null, null);
                    } catch (Throwable throwable) {
                        Log.e(TAG, "onConfigured Throwable.", throwable);
                    }
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                    Log.e(TAG, "Failed to configure capture session");
                }

                @Override
                public void onClosed(@NonNull CameraCaptureSession session) {
                    super.onClosed(session);
                    Log.e(TAG, "onClosed session.");
                }
            };

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                final LinkedList<OutputConfiguration> outputConfigurations = new LinkedList<>();
                outputConfigurations.add(new OutputConfiguration(mImageReader.getSurface()));
                if (mSurfaceTextures != null && mSurfaceTextures.size() > 0) {
                    for (SurfaceTexture texture : mSurfaceTextures) {
                        Surface surface = new Surface(texture);
                        builder.addTarget(surface);
                        outputConfigurations.add(new OutputConfiguration(surface));
                    }
                }
                cameraDevice.createCaptureSession(new SessionConfiguration(SessionConfiguration.SESSION_REGULAR,
                        outputConfigurations, new HandlerExecutor(cameraHandler), stateCallback));
            } else {
                final LinkedList<Surface> surfaceList = new LinkedList<>();
                surfaceList.add(mImageReader.getSurface());
                if (mSurfaceTextures != null && mSurfaceTextures.size() > 0) {
                    for (SurfaceTexture texture : mSurfaceTextures) {
                        Surface surface = new Surface(texture);
                        builder.addTarget(surface);
                        surfaceList.add(surface);
                    }
                }
                cameraDevice.createCaptureSession(surfaceList, stateCallback, cameraHandler);
            }

        } catch (IllegalStateException e) {
            Log.e(TAG, "startCaptureSession IllegalStateException.");
            //resetPreview();
        } catch (Throwable throwable) {
            Log.e(TAG, "startCaptureSession Throwable.", throwable);
        }
    }

    private static Size getRightSupportedSize(Size[] supportedUtilSizes, Size size) {
        if (supportedUtilSizes == null) {
            return null;
        }
        Arrays.sort(supportedUtilSizes, new SizeComparator());
        int w = size.getWidth();
        int h = size.getHeight();
        for (Size ss : supportedUtilSizes) {
            if (ss.getWidth() >= w && ss.getHeight() >= h) {
                return ss;
            }
        }
        return null;
    }

    private static final class SizeComparator implements Comparator<Size> {
        @Override
        public int compare(Size o1, Size o2) {
            int ow1 = o1.getWidth();
            int ow2 = o2.getWidth();
            if (ow1 == ow2) {
                int oh1 = o1.getHeight();
                int oh2 = o2.getHeight();
                return oh1 - oh2;
            } else {
                return ow1 - ow2;
            }
        }
    }

    private static final class CameraParam {
        String cameraId;
        CameraCharacteristics characteristics;
        Size size;

        public CameraParam(String cameraId, CameraCharacteristics characteristics, Size size) {
            this.cameraId = cameraId;
            this.characteristics = characteristics;
            this.size = size;
        }
    }

    public interface ImageCallback {
        void onImage(Image image);
    }

    public @interface Facing {
        int FRONT = CameraCharacteristics.LENS_FACING_FRONT;
        int BACK = CameraCharacteristics.LENS_FACING_BACK;
        int EXTERNAL = CameraCharacteristics.LENS_FACING_EXTERNAL;
    }
}
