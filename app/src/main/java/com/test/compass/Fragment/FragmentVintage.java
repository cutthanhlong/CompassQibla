package com.test.compass.Fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
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
import com.test.compass.databinding.FragmentCompassBinding;

import java.util.List;


public class FragmentVintage extends Fragment implements SensorEventListener, LocationListener {
    FragmentCompassBinding binding;

    private SensorManager mSensorManager;

    private Sensor sensor;
    private SensorManager sensorManager;
    LocationManager locationManager;


    float[] G_area_f3098y;
    float[] G_area_f3099z;
    private float val;
    private float G_area_f3084C = 0.0f;
    Float f3087n = Float.valueOf(0.0f);
    boolean f3095v = true;
    float f3096w = 0.0f;
    private float[] G_area_mGeoManegic = new float[3];
    private float[] G_area_mGravity = new float[3];

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = FragmentCompassBinding.inflate(getLayoutInflater());
        checkLocation();

        this.mSensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        if (ContextCompat.checkSelfPermission(getActivity(), "android.permission.ACCESS_FINE_LOCATION") != 0) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{"android.permission.ACCESS_FINE_LOCATION", "android.permission.ACCESS_COARSE_LOCATION"}, 100);
        } else {
            intialization();
        }
//        checkLocationisEnableorNot();
        getLocation();


        return binding.getRoot();
    }

    private void intialization() {
        SensorManager sensorManager2 = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        this.sensorManager = sensorManager2;
        this.sensor = sensorManager2.getDefaultSensor(2);

        this.binding.imgCompassNiddle.setImageResource(R.drawable.tam_vintage);
        this.binding.relCompassBorder.setBackgroundResource(R.drawable.bg_vintage);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!isLocationEnabled()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setMessage("Your GPS seems to be disabled, do you want to enable it?").setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    FragmentVintage.this.startActivity(new Intent("android.settings.LOCATION_SOURCE_SETTINGS"));
                }
            }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                }
            });
            builder.create().show();
            intialization();
        }
        SensorManager sensorManager2 = this.sensorManager;
        if (sensorManager2 != null) {
            sensorManager2.registerListener(this, sensorManager2.getDefaultSensor(2), 1);
            SensorManager sensorManager3 = this.sensorManager;
            sensorManager3.registerListener(this, sensorManager3.getDefaultSensor(1), 1);
            if (this.sensor == null) {
                Log.e("sensor", "Not Supported");
            }
        }
    }

    public void onPause() {
        super.onPause();
        SensorManager sensorManager2 = this.sensorManager;
        if (sensorManager2 != null) {
            sensorManager2.unregisterListener(this);
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
            float[] fArr = this.G_area_mGravity;
            fArr[0] = (fArr[0] * 0.97f) + (sensorEvent.values[0] * 0.029999971f);
            float[] fArr2 = this.G_area_mGravity;
            fArr2[1] = (fArr2[1] * 0.97f) + (sensorEvent.values[1] * 0.029999971f);
            float[] fArr3 = this.G_area_mGravity;
            fArr3[2] = (fArr3[2] * 0.97f) + (sensorEvent.values[2] * 0.029999971f);
        } else if (type == 2) {
            float[] fArr4 = this.G_area_mGeoManegic;
            fArr4[0] = (fArr4[0] * 0.97f) + (sensorEvent.values[0] * 0.029999971f);
            float[] fArr5 = this.G_area_mGeoManegic;
            fArr5[1] = (fArr5[1] * 0.97f) + (sensorEvent.values[1] * 0.029999971f);
            float[] fArr6 = this.G_area_mGeoManegic;
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
        if (SensorManager.getRotationMatrix(fArr7, new float[9], this.G_area_mGravity, this.G_area_mGeoManegic)) {
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
                this.binding.relCompassBorder.startAnimation(rotateAnimation);
            } else {
                this.binding.relCompassBorder.clearAnimation();
            }
            this.G_area_f3084C = -this.f3096w;
        }
    }

    public float m4806a(float f, float f2) {
        float f3 = f - f2;
        return Math.abs(f3) > 150.0f ? f > 0.0f ? f2 - (((f2 + 180.0f) + (180.0f - f)) * 0.05f) : f2 + ((f + 180.0f + (180.0f - f2)) * 0.05f) : f2 + (f3 * 0.05f);
    }

    public void onRequestPermissionsResult(int i, String[] strArr, int[] iArr) {
        super.onRequestPermissionsResult(i, strArr, iArr);
        if (i != 100) {
            return;
        }
        if (iArr.length <= 0 || iArr[0] != 0) {
            getActivity().finish();
            Toast.makeText(getContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(getContext(), "Permission Granted", Toast.LENGTH_SHORT).show();
        intialization();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
//        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
//        try {
//            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
//            binding.tvAddress.setText(addresses.get(0).getCountryName());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    @Override
    public void onLocationChanged(@NonNull List<Location> locations) {
        LocationListener.super.onLocationChanged(locations);
    }

    @Override
    public void onFlushComplete(int requestCode) {
        LocationListener.super.onFlushComplete(requestCode);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        LocationListener.super.onStatusChanged(provider, status, extras);
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {
        LocationListener.super.onProviderEnabled(provider);
    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
        LocationListener.super.onProviderDisabled(provider);
    }

    private void checkLocation() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
        }
    }

    private void checkLocationisEnableorNot() {
        @SuppressLint("ServiceCast") LocationManager lm = (LocationManager) getActivity().getSystemService(Context.LOCALE_SERVICE);
        boolean gpsEnabled = false;
        boolean netWorkEnabled = false;
        try {
            gpsEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            netWorkEnabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!gpsEnabled && !netWorkEnabled) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setMessage("Your GPS seems to be disabled, do you want to enable it?").setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    FragmentVintage.this.startActivity(new Intent("android.settings.LOCATION_SOURCE_SETTINGS"));
                }
            }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                }
            });
            builder.create().show();
        }
    }


    private void getLocation() {
        try {
            locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 500, 5, (LocationListener) this);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
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
}
