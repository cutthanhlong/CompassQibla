package com.test.compass.Fragment;

import android.Manifest;
import android.content.Context;
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
import com.test.compass.databinding.FragmentMapCompassBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;

public class FragmentSatellite extends Fragment implements SensorEventListener {

    FragmentMapCompassBinding binding;
    private GoogleMap googleMap;
    private Location location = null;
    public LocationManager locationManager;
    private float[] mGeomegnatic = new float[3];
    private float[] mGravity = new float[3];
    private SensorManager mSensorManager;

    private MapView mapView;
    Sensor sensor;
    private float val;
    float[] G_area_f3098y;
    float[] G_area_f3099z;
    private float G_area_f3084C = 0.0f;
    Float f3087n = Float.valueOf(0.0f);
    boolean f3095v = true;
    float f3096w = 0.0f;
    private FusedLocationProviderClient fusedLocationProviderClient;

    public LocationListener locationListener = new LocationListener() {
        public void onProviderDisabled(String str) {
        }

        public void onProviderEnabled(String str) {
        }

        public void onStatusChanged(String str, int i, Bundle bundle) {
        }

        public void onLocationChanged(Location location) {
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMapCompassBinding.inflate(getLayoutInflater());
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        this.locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        this.mSensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        initView(savedInstanceState);
        if (ContextCompat.checkSelfPermission(getActivity(), "android.permission.ACCESS_FINE_LOCATION") != 0) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{"android.permission.ACCESS_FINE_LOCATION", "android.permission.ACCESS_COARSE_LOCATION"}, 100);
        } else {
            inti();
        }

        return binding.getRoot();
    }

    private void inti() {
        SensorManager sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        this.mSensorManager = sensorManager;
        this.sensor = sensorManager.getDefaultSensor(2);
    }

    private void initView(Bundle bundle) {
        MapView mapView2 = (MapView) binding.mapView;
        this.mapView = mapView2;
        mapView2.onCreate(bundle);
        this.mapView.getMapAsync(new OnMapReadyCallback() {
            public void onMapReady(GoogleMap googleMap) {
                FragmentSatellite.this.mapView_onMapReady(googleMap);
            }
        });
    }

    public void onDestroy() {
        super.onDestroy();
        this.mapView.onDestroy();
    }

    private void initMap() {
        if (ContextCompat.checkSelfPermission(getActivity(), "android.permission.ACCESS_FINE_LOCATION") != 0 && ContextCompat.checkSelfPermission(getActivity(), "android.permission.ACCESS_COARSE_LOCATION") != 0) {
            if (ContextCompat.checkSelfPermission(getActivity(), "android.permission.ACCESS_FINE_LOCATION") != 0) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{"android.permission.ACCESS_FINE_LOCATION"}, 12);
            }
            if (ContextCompat.checkSelfPermission(getActivity(), "android.permission.ACCESS_COARSE_LOCATION") != 0) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{"android.permission.ACCESS_COARSE_LOCATION"}, 13);
            }
        } else if (this.googleMap != null) {

            this.googleMap.setMapType(2);
            this.binding.imgCompassBg.setBackgroundResource(R.drawable.bg_map1);
            this.binding.imgNiddleBg.setImageResource(R.drawable.tam_map1);

        }
        this.googleMap.setMyLocationEnabled(true);
        this.googleMap.getUiSettings().setMyLocationButtonEnabled(false);
    }

    private void getCurrentLocation() {
        if (ContextCompat.checkSelfPermission(getActivity(), "android.permission.ACCESS_FINE_LOCATION") == 0 || ContextCompat.checkSelfPermission(getActivity(), "android.permission.ACCESS_COARSE_LOCATION") == 0) {
            boolean isProviderEnabled = this.locationManager.isProviderEnabled("gps");
            boolean isProviderEnabled2 = this.locationManager.isProviderEnabled("network");
            if (isProviderEnabled || isProviderEnabled2) {
                this.location = null;
                if (isProviderEnabled) {
                    this.locationManager.requestLocationUpdates("gps", 10, 1000.0f, this.locationListener);
                    this.location = this.locationManager.getLastKnownLocation("gps");
                }
                if (isProviderEnabled2) {
                    this.locationManager.requestLocationUpdates("network", 10, 1000.0f, this.locationListener);
                    this.location = this.locationManager.getLastKnownLocation("network");
                    return;
                }
                return;
            }
            return;
        }
        if (ContextCompat.checkSelfPermission(getActivity(), "android.permission.ACCESS_FINE_LOCATION") != 0) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{"android.permission.ACCESS_FINE_LOCATION"}, 12);
        }
        if (ContextCompat.checkSelfPermission(getActivity(), "android.permission.ACCESS_COARSE_LOCATION") != 0) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{"android.permission.ACCESS_COARSE_LOCATION"}, 13);
        }
    }

    public void mapView_onMapReady(GoogleMap googleMap2) {
        this.googleMap = googleMap2;
        initMap();
        getCurrentLocation();
    }


    public void onResume() {
        super.onResume();
        SensorManager sensorManager = this.mSensorManager;
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(3), 1);
        this.mapView.onResume();
        getCurrentLocation();
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        try {
                            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12f));
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
        SensorManager sensorManager2 = this.mSensorManager;
        if (sensorManager2 != null) {
            sensorManager2.registerListener(this, sensorManager2.getDefaultSensor(2), 1);
            SensorManager sensorManager3 = this.mSensorManager;
            sensorManager3.registerListener(this, sensorManager3.getDefaultSensor(1), 1);
            if (this.sensor == null) {
                Log.e("sensor", "Not Supported");
            }
        }
    }

    public void onPause() {
        super.onPause();
        SensorManager sensorManager = this.mSensorManager;
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }

    public void onRequestPermissionsResult(int i, String[] strArr, int[] iArr) {
        super.onRequestPermissionsResult(i, strArr, iArr);
        if (i != 100) {
            return;
        }
        if (iArr.length <= 0 || iArr[0] != 0) {
            getActivity().finish();
            Toast.makeText(getActivity(), "Permission Denied", Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(getActivity(), "Permission Granted", Toast.LENGTH_SHORT).show();
        inti();
    }

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
                this.binding.imgCompassBg.startAnimation(rotateAnimation);
            } else {
                this.binding.imgCompassBg.clearAnimation();
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
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
