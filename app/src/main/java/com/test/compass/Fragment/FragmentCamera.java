package com.test.compass.Fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.RotateAnimation;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.test.compass.R;
import com.test.compass.databinding.FragmentCameraCompassBinding;

import java.util.ArrayList;

public class FragmentCamera extends Fragment implements SensorEventListener, SurfaceHolder.Callback, Handler.Callback {
    FragmentCameraCompassBinding binding;
    private static final int MSG_CAMERA_OPENED = 1;
    private static final int MSG_SURFACE_READY = 2;
    static final int MY_PERMISSIONS_REQUEST_CAMERA = 1242;
    public static final int REQUEST_CODE_LOCATION_PERMISSON = 100;
    private float azimuth = 0.0f;
    private float currentDegree = 0.0f;
    private float currentazimuth = 0.0f;
    CameraDevice mCameraDevice;
    String[] mCameraIDsList;
    CameraManager mCameraManager;
    CameraDevice.StateCallback mCameraStateCB;

    public Surface mCameraSurface = null;
    CameraCaptureSession mCaptureSession;
    private float[] mGeomegnatic = new float[3];
    private float[] mGravity = new float[3];

    public final Handler mHandler = new Handler(this);
    boolean mIsCameraConfigured = false;
    private SensorManager mSensorManager;
    boolean mSurfaceCreated = true;
    SurfaceHolder mSurfaceHolder;
    SurfaceView mSurfaceView;
    double magAbsVal;
    Sensor sensor;
    private float val;
    float[] G_area_f3098y;
    float[] G_area_f3099z;
    private float G_area_f3084C = 0.0f;
    Float f3087n = Float.valueOf(0.0f);
    boolean f3095v = true;
    float f3096w = 0.0f;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCameraCompassBinding.inflate(getLayoutInflater());

        if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), "android.permission.ACCESS_FINE_LOCATION") + ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), "android.permission.CAMERA") != 0) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{"android.permission.ACCESS_FINE_LOCATION", "android.permission.CAMERA"}, 100);
        } else {
            inti();
        }
        return binding.getRoot();
    }

    private void inti() {
        SensorManager sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        this.mSensorManager = sensorManager;
        this.sensor = sensorManager.getDefaultSensor(2);
        this.binding.imgCompassNiddle.setImageResource(R.drawable.tam_camera);
        this.binding.rlControl.setBackgroundResource(R.drawable.camera_compass);
        SurfaceHolder holder = this.binding.cameraSurfaceView.getHolder();
        this.mSurfaceHolder = holder;
        holder.addCallback(this);
        this.mCameraManager = (CameraManager) getActivity().getSystemService(Context.CAMERA_SERVICE);
        this.mSensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        try {
            String[] cameraIdList = this.mCameraManager.getCameraIdList();
            this.mCameraIDsList = cameraIdList;
            for (String str : cameraIdList) {
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        this.mCameraStateCB = new CameraDevice.StateCallback() {
            public void onDisconnected(CameraDevice cameraDevice) {
            }

            public void onError(CameraDevice cameraDevice, int i) {
            }

            public void onOpened(CameraDevice cameraDevice) {
                mCameraDevice = cameraDevice;
                mHandler.sendEmptyMessage(1);
            }
        };

    }

    @Override
    public void onStart() {
        super.onStart();
        if (ContextCompat.checkSelfPermission(getActivity(), "android.permission.CAMERA") == 0) {
            try {
                this.mCameraManager.openCamera(this.mCameraIDsList[1], this.mCameraStateCB, new Handler());
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        } else if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), "android.permission.CAMERA")) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{"android.permission.CAMERA"}, MY_PERMISSIONS_REQUEST_CAMERA);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!isLocationEnabled()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("Your GPS seems to be disabled, do you want to enable it?").setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    startActivity(new Intent("android.settings.LOCATION_SOURCE_SETTINGS"));
                }
            }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                }
            });
            builder.create().show();
        }
        SensorManager sensorManager = this.mSensorManager;
        if (sensorManager != null) {
            sensorManager.registerListener(this, sensorManager.getDefaultSensor(2), 1);
            SensorManager sensorManager2 = this.mSensorManager;
            sensorManager2.registerListener(this, sensorManager2.getDefaultSensor(1), 1);
            if (this.sensor == null) {
                Log.e("sensor", "Not Supported");
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        SensorManager sensorManager = this.mSensorManager;
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled("gps") || locationManager.isProviderEnabled("network");
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        int type = sensorEvent.sensor.getType();
        if (type == 1) {
            float[] fArr = this.mGravity;
            fArr[0] = (fArr[0] * 0.97f) + (sensorEvent.values[0] * 0.029999971f);
            float[] fArr2 = this.mGravity;
            fArr2[1] = (fArr2[1] * 0.97f) + (sensorEvent.values[1] * 0.029999971f);
            float[] fArr3 = this.mGravity;
            fArr3[2] = (fArr3[2] * 0.97f) + (sensorEvent.values[2] * 0.029999971f);
        } else if (type == 2) {
            float[] fArr4 = this.mGeomegnatic;
            fArr4[0] = (fArr4[0] * 0.97f) + (sensorEvent.values[0] * 0.029999971f);
            float[] fArr5 = this.mGeomegnatic;
            fArr5[1] = (fArr5[1] * 0.97f) + (sensorEvent.values[1] * 0.029999971f);
            float[] fArr6 = this.mGeomegnatic;
            fArr6[2] = (fArr6[2] * 0.97f) + (sensorEvent.values[2] * 0.029999971f);
            double d = sensorEvent.values[0];
            double d2 = sensorEvent.values[1];
            double d3 = sensorEvent.values[2];
            Double.isNaN(d);
            Double.isNaN(d);
            Double.isNaN(d);
            Double.isNaN(d);
            Double.isNaN(d2);
            Double.isNaN(d2);
            Double.isNaN(d2);
            Double.isNaN(d2);
            Double.isNaN(d3);
            Double.isNaN(d3);
            Double.isNaN(d3);
            Double.isNaN(d3);
            binding.txtUT.setText(Math.round(Math.sqrt((d * d) + (d2 * d2) + (d3 * d3))) + "μT");
        } else if (type != 13) {
            float f = sensorEvent.values[0];
            this.val = f;
            this.binding.tvAddress.setText(textNumber((int) f));
        }
        if (sensorEvent.sensor.getType() == 1) {
            this.G_area_f3098y = sensorEvent.values;
        }
        if (sensorEvent.sensor.getType() == 2) {
            this.G_area_f3099z = sensorEvent.values;
        }
        if (this.G_area_f3098y == null || this.G_area_f3099z == null) {
            return;
        }
        float[] fArr7 = new float[9];
        if (SensorManager.getRotationMatrix(fArr7, new float[9], this.mGravity, this.mGeomegnatic)) {
            float[] fArr8 = new float[3];
            SensorManager.getOrientation(fArr7, fArr8);
            Float valueOf = Float.valueOf(fArr8[0]);
            this.f3087n = valueOf;
            float m4806a = m4806a((valueOf.floatValue() * 360.0f) / 6.28318f, this.f3096w);
            this.f3096w = m4806a;
            this.binding.tvAddress.setText(textNumber(Math.round(m4806a + 360.0f) % 360));
            RotateAnimation rotateAnimation = new RotateAnimation(this.G_area_f3084C, -this.f3096w, 1, 0.5f, 1, 0.5f);
            rotateAnimation.setFillAfter(true);
            if (this.f3095v) {
                this.binding.rlControl.startAnimation(rotateAnimation);
            } else {
                this.binding.rlControl.clearAnimation();
            }
            this.G_area_f3084C = -this.f3096w;
        }
    }

    public float m4806a(float f, float f2) {
        float f3 = f - f2;
        return Math.abs(f3) > 150.0f ? f > 0.0f ? f2 - (((f2 + 180.0f) + (180.0f - f)) * 0.05f) : f2 + ((f + 180.0f + (180.0f - f2)) * 0.05f) : f2 + (f3 * 0.05f);
    }

    public String textNumber(int i) {
        StringBuilder sb = new StringBuilder();
        sb.append(i);
        sb.append("°");
        if (i <= 15 || i >= 345) {

            return ((Object) sb) + getString(R.string.north);
        } else if (i <= 75) {

            return ((Object) sb) + getString(R.string.northeast);
        } else if (i <= 105) {

            return ((Object) sb) + getString(R.string.east);
        } else if (i <= 165) {

            return ((Object) sb) + getString(R.string.southeast);
        } else if (i <= 195) {

            return ((Object) sb) + getString(R.string.south);
        } else if (i <= 255) {

            return ((Object) sb) + getString(R.string.southwest);
        } else if (i <= 285) {

            return ((Object) sb) + getString(R.string.west);
        } else {
            return ((Object) sb) + getString(R.string.northwest);
        }
    }

    @Override
    public void onStop() {
        CameraDevice cameraDevice;
        super.onStop();
        try {
            CameraCaptureSession cameraCaptureSession = this.mCaptureSession;
            if (cameraCaptureSession != null) {
                cameraCaptureSession.stopRepeating();
                this.mCaptureSession.close();
                this.mCaptureSession = null;
            }
            this.mIsCameraConfigured = false;
            cameraDevice = this.mCameraDevice;
            if (cameraDevice == null) {
                return;
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
            cameraDevice = this.mCameraDevice;
            if (cameraDevice == null) {
                return;
            }
        } catch (IllegalStateException e2) {
            e2.printStackTrace();
            cameraDevice = this.mCameraDevice;
            if (cameraDevice == null) {
                return;
            }
        } catch (Throwable th) {
            CameraDevice cameraDevice2 = this.mCameraDevice;
            if (cameraDevice2 != null) {
                cameraDevice2.close();
                this.mCameraDevice = null;
                this.mCaptureSession = null;
            }
            throw th;
        }
        cameraDevice.close();
        this.mCameraDevice = null;
        this.mCaptureSession = null;
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    public boolean handleMessage(@NonNull Message message) {
        int i = message.what;
        if ((i == 1 || i == 2) && this.mSurfaceCreated && this.mCameraDevice != null && !this.mIsCameraConfigured) {
            configureCamera();
        }
        return true;
    }

    private void configureCamera() {
        try {
            ArrayList arrayList = new ArrayList();
            arrayList.add(this.mCameraSurface);
            this.mCameraDevice.createCaptureSession(arrayList, new CaptureSessionListener(), (Handler) null);
            this.mIsCameraConfigured = true;
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {
        this.mCameraSurface = surfaceHolder.getSurface();
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        this.mCameraSurface = surfaceHolder.getSurface();
        this.mSurfaceCreated = true;
        this.mHandler.sendEmptyMessage(2);
    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {
        this.mSurfaceCreated = false;
    }

    private class CaptureSessionListener extends CameraCaptureSession.StateCallback {
        public void onConfigureFailed(CameraCaptureSession cameraCaptureSession) {
        }

        private CaptureSessionListener() {
        }

        public void onConfigured(CameraCaptureSession cameraCaptureSession) {
            try {
                mCaptureSession = cameraCaptureSession;
                CaptureRequest.Builder createCaptureRequest = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
                createCaptureRequest.addTarget(mCameraSurface);
                mCaptureSession.setRepeatingRequest(createCaptureRequest.build(), (CameraCaptureSession.CaptureCallback) null, (Handler) null);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == MY_PERMISSIONS_REQUEST_CAMERA) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                try {
                    if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    mCameraManager.openCamera(mCameraIDsList[1], mCameraStateCB, new Handler());
                } catch (CameraAccessException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(getActivity(), "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == 100) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getActivity(), "Permission Granted", Toast.LENGTH_SHORT).show();
                inti();
            } else {
                Toast.makeText(getActivity(), "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
