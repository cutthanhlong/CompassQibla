package com.test.compass.Fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.test.compass.R;
import com.test.compass.databinding.FragmentWeatherBinding;
import com.test.compass.util.BoardCastNetwork;
import com.test.compass.util.IsNetWork;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;


public class FragmentWeather extends Fragment implements LocationListener {
    FragmentWeatherBinding binding;

    String areaName;
    private double currentLatitude;
    private double currentLongitude;
    String humidity;

    String pressure;
    String sunRiseTime;
    String sunSetTime;
    String temp;
    String visibility;
    String weatherDate;
    String weatherIcon;
    String wind;
    String cloudiness;
    BoardCastNetwork boardCastNetwork;

    public static NestedScrollView view;
    public static ImageView imageView;

    SwipeRefreshLayout swipeRefreshLayout;
    RelativeLayout rl_read, rl_camera, rl_gps;
    ImageView img_read, img_camera, img_gps;
    private Dialog dialog;
    private ActivityResultLauncher<String> requestCameraPermissionLauncher;
    private ActivityResultLauncher<String> requestLocationPermissionLauncher;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private int locationPermissionDeniedCount = 0;
    private int locationPermissionDeniedCountCamera = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentWeatherBinding.inflate(getLayoutInflater());
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        if (!hasLocationPermission()) {
            CardView ok;
            dialog = new Dialog(getContext());
            dialog.setContentView(R.layout.dialog_poup_permission);
            dialog.getWindow().setGravity(Gravity.CENTER);
            dialog.getWindow().setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setCancelable(false);
            ok = dialog.findViewById(R.id.ok);
            rl_read = dialog.findViewById(R.id.rl_read);
            rl_camera = dialog.findViewById(R.id.rl_camera);
            rl_gps = dialog.findViewById(R.id.rl_gps);
            img_read = dialog.findViewById(R.id.img_read);
            img_camera = dialog.findViewById(R.id.img_camera);
            img_gps = dialog.findViewById(R.id.img_gps);
            if (checkLocationFind()) {
                img_read.setImageResource(R.drawable.switch_on);
            }
            if (checkCameraPermission()) {
                img_camera.setImageResource(R.drawable.switch_on);
            }
            if (checkGPSActivation()) {
                img_gps.setImageResource(R.drawable.switch_on);
            } else {
                img_gps.setImageResource(R.drawable.switch_off);
            }
            rl_read.setOnClickListener(view -> {
                if (!checkLocationFind()) {
                    if (locationPermissionDeniedCount >= 2 && !shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                        openAppSettings();
                    } else {
                        locationPermissionDeniedCount++;
                        requestLocationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
                    }
                }
            });
            rl_camera.setOnClickListener(view -> {
                try {
                    if (!checkCameraPermission()) {
                        if (locationPermissionDeniedCountCamera >= 2 && !shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                            openAppSettings();
                        } else {
                            locationPermissionDeniedCountCamera++;
                            requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA);

                        }
                    }
                } catch (Exception e) {

                }
            });
            rl_gps.setOnClickListener(view -> {
                if (!checkGPSActivation()) {
                    try {
                        startActivity(new Intent("android.settings.LOCATION_SOURCE_SETTINGS"));
                    } catch (ActivityNotFoundException unused) {
                    }
                }
            });
            ok.setOnClickListener(view -> {
                dialog.dismiss();
            });
            dialog.show();
        } else {
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        currentLatitude = location.getLatitude();
                        currentLongitude = location.getLongitude();
                        Log.e("FragmentHome", "weather currentLatitude: " + currentLatitude);
                        Log.e("FragmentHome", "weather currentLongitude: " + currentLongitude);
                        getCurrentWeatherData(currentLatitude, currentLongitude);
                        getCurrentUvIndex(currentLatitude, currentLongitude);
                    }
                }
            });
        }
        requestCameraPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
            if (isGranted) {
                if (dialog != null && dialog.isShowing()) {
                    img_camera.setImageResource(R.drawable.switch_on);
                }
                locationPermissionDeniedCountCamera = 0;
            } else {
                if (dialog.isShowing()) {
                    img_camera.setImageResource(R.drawable.switch_off);
                }
                locationPermissionDeniedCountCamera++;
            }
        });
        requestLocationPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
            if (isGranted) {
                if (dialog != null && dialog.isShowing()) {
                    img_read.setImageResource(R.drawable.switch_on);
                }
                locationPermissionDeniedCount = 0;
            } else {
                img_read.setImageResource(R.drawable.switch_off);
                locationPermissionDeniedCount++;
            }
        });

        binding.toolbar.icStart.setVisibility(View.GONE);
        boardCastNetwork = new BoardCastNetwork();
        binding.toolbar.tvToolbar.setText(getString(R.string.live_weather));
        view = binding.viewWeather;
        imageView = binding.imgNoInternet;
        swipeRefreshLayout = binding.swipe;
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                        if (IsNetWork.haveNetworkConnectionSplash(getContext())) {
                            FragmentWeather.view.setVisibility(View.VISIBLE);
                            FragmentWeather.imageView.setVisibility(View.GONE);
                            if (checkLocationPermission()) {
                                fusedLocationProviderClient.getLastLocation().addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                                    @Override
                                    public void onSuccess(Location location) {
                                        if (location != null) {
                                            currentLatitude = location.getLatitude();
                                            currentLongitude = location.getLongitude();
                                            getCurrentWeatherData(currentLatitude, currentLongitude);
                                            getCurrentUvIndex(currentLatitude, currentLongitude);
                                        }
                                    }
                                });
                            }
                        } else {
                            FragmentWeather.view.setVisibility(View.GONE);
                            FragmentWeather.imageView.setVisibility(View.VISIBLE);
                        }
                    }
                }, 2000);
            }
        });
        swipeRefreshLayout.setColorSchemeColors(
                getResources().getColor(android.R.color.holo_blue_bright),
                getResources().getColor(android.R.color.holo_orange_dark),
                getResources().getColor(android.R.color.holo_green_dark),
                getResources().getColor(android.R.color.holo_red_dark)
        );

        return binding.getRoot();
    }

    private void openAppSettings() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }

    private boolean hasLocationPermission() {
        boolean cameraPermission = checkCameraPermission();
        boolean gpsPermission = checkGPSActivation();
        boolean hasCoarsePermission = checkLocationFind();
        return hasCoarsePermission && cameraPermission && gpsPermission;
    }

    private boolean checkLocationFind() {
        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return false;
        } else {
            return true;
        }
    }

    private boolean checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            return false;
        } else {
            return true;
        }
    }

    public boolean checkGPSActivation() {
        Object systemService = getActivity().getSystemService(Context.LOCATION_SERVICE);
        Objects.requireNonNull(systemService);
        return ((LocationManager) systemService).isProviderEnabled("gps");
    }

    @Override
    public void onStart() {
        super.onStart();
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        getActivity().registerReceiver(boardCastNetwork, intentFilter);

    }

    @Override
    public void onStop() {
        super.onStop();
        getActivity().unregisterReceiver(boardCastNetwork);
    }

    private boolean checkLocationPermission() {
        return ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }


    public void onResume() {
        super.onResume();
        Log.d("aaa", "weather");
        if (checkLocationPermission()) {
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        currentLatitude = location.getLatitude();
                        currentLongitude = location.getLongitude();
                        getCurrentWeatherData(currentLatitude, currentLongitude);
                        getCurrentUvIndex(currentLatitude, currentLongitude);

                        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
                        try {
                            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                            if (addresses != null && addresses.size() > 0) {
                                Address address = addresses.get(0);
                                String city = address.getAdminArea();
                                String country = address.getCountryName();
                                binding.tvNameCity.setText(city + ", " + country);

                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
        if (dialog != null && dialog.isShowing()) {
            if (checkLocationPermission()) {
                img_read.setImageResource(R.drawable.switch_on);
            }
            if (checkCameraPermission()) {
                img_camera.setImageResource(R.drawable.switch_on);
            }
            if (checkGPSActivation()) {
                img_gps.setImageResource(R.drawable.switch_on);
            } else {
                img_gps.setImageResource(R.drawable.switch_off);
            }
        }
    }


    public void onPause() {
        super.onPause();
    }


    @Override
    public void onLocationChanged(@NonNull Location location) {
        this.currentLatitude = location.getLatitude();
        double longitude = location.getLongitude();
        this.currentLongitude = longitude;
        getCurrentWeatherData(this.currentLatitude, longitude);
        getCurrentUvIndex(this.currentLatitude, this.currentLongitude);
    }

    private void getCurrentWeatherData(double d, double d2) {
        Volley.newRequestQueue(getContext()).add(new JsonObjectRequest(0, "https://api.openweathermap.org/data/2.5/weather?lat=" + d + "&lon=" + d2 + "&appid=cb1f36d2d48e66ab98ba96def331f7cf&units=metric", (JSONObject) null, new Response.Listener<JSONObject>() {
            public void onResponse(JSONObject jSONObject) {
                FragmentWeather.this.parseJsonResponse(jSONObject);
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
            }
        }));
    }

    private void getCurrentUvIndex(double d, double d2) {
        Volley.newRequestQueue(getContext()).add(new JsonObjectRequest(0, "https://api.openweathermap.org/data/2.5/uvi?lat=" + d + "&lon=" + d2 + "&appid=cb1f36d2d48e66ab98ba96def331f7cf", (JSONObject) null, new Response.Listener<JSONObject>() {
            public void onResponse(JSONObject jSONObject) {
                FragmentWeather.this.parseUvIndexResponse(jSONObject);
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
            }
        }));
    }

    public void parseUvIndexResponse(JSONObject jSONObject) {
        try {
            binding.tvUv.setText(jSONObject.getString("value"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void parseJsonResponse(JSONObject jSONObject) {
        JSONObject jSONObject2 = jSONObject;
        try {
            JSONObject jSONObject3 = jSONObject2.getJSONObject("main");
            this.areaName = jSONObject2.getString("name");
            this.temp = jSONObject3.getString("temp");
            this.humidity = jSONObject3.getString("humidity");
            this.pressure = jSONObject3.getString("pressure");
            this.weatherDate = jSONObject2.getString("dt");
            this.visibility = jSONObject2.getString("visibility");
            JSONObject jSONObject4 = jSONObject2.getJSONObject(NotificationCompat.CATEGORY_SYSTEM);
            this.sunRiseTime = jSONObject4.getString("sunrise") + "000";
            this.sunSetTime = jSONObject4.getString("sunset") + "000";
            long parseLong = Long.parseLong(this.sunRiseTime);
            long parseLong2 = Long.parseLong(this.sunSetTime);
            String string = jSONObject2.getJSONArray("weather").getJSONObject(0).getString("id");
            this.weatherIcon = string;
            String name_weather = jSONObject2.getJSONArray("weather").getJSONObject(0).getString("main");
            if (name_weather.equals("Clouds")) {
                binding.imgWeather.setImageResource(R.drawable.ic_cloud);
            } else if (name_weather.equals("Clear")) {
                binding.imgWeather.setImageResource(R.drawable.ic_sun);
            } else if (name_weather.equals("Rain")) {
                binding.imgWeather.setImageResource(R.drawable.ic_rain);
            } else if (name_weather.equals("Thunderstorm")) {
                binding.imgWeather.setImageResource(R.drawable.ic_rain_sam);
            } else {
                binding.imgWeather.setImageResource(R.drawable.ic_sun_clound);
            }
            cloudiness = jSONObject2.getJSONObject("clouds").getString("all");
            binding.tvCloudiness.setText(cloudiness);

//            this.binding.tvNameCity.setText(this.areaName);
            TextView textView = this.binding.tvTemp;
            double a = Double.parseDouble(temp);
            int b = (int) a;
            textView.setText(b + " °C");
            TextView textView2 = this.binding.tvHumidity;
            textView2.setText(this.humidity + "%");
            String formattedDateNow = getCurrentDateString();
            SimpleDateFormat inputFormat1 = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
            SimpleDateFormat outputFormat1 = new SimpleDateFormat("EEEE, dd MMM, yyyy", Locale.US);

            try {
                // Chuyển chuỗi thành đối tượng ngày
                Date date = inputFormat1.parse(formattedDateNow);

                // Định dạng lại ngày tháng theo yêu cầu
                String formattedDate2 = outputFormat1.format(date);

                binding.tvDay.setText(formattedDate2);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            this.visibility = String.valueOf(Double.parseDouble(this.visibility) / 1000.0d);
            TextView textView3 = binding.tvVisibility;
            textView3.setText(this.visibility + " km");
            TextView textView4 = this.binding.tvHpa;
            textView4.setText(this.pressure + " hpa");
            this.wind = jSONObject2.getJSONObject("wind").getString("speed");
            TextView textView5 = this.binding.tvWind;
            textView5.setText(this.wind + "km/h");
            this.binding.sunRise.setText(getDate(parseLong, "HH:mm"));
            this.binding.sunSet.setText(getDate(parseLong2, "HH:mm"));
//            this.activityLiveWeatherDetailsBinding.progress.setVisibility(8);
        } catch (JSONException e) {
            e.printStackTrace();
//            this.activityLiveWeatherDetailsBinding.progress.setVisibility(8);
        }
    }

    private static String getDate(long j, String str) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(str);
        Calendar instance = Calendar.getInstance();
        instance.setTimeInMillis(j);
        return simpleDateFormat.format(instance.getTime());
    }

    public static String getCurrentDateString() {
        Calendar calendar = Calendar.getInstance();
        Date currentDate = calendar.getTime();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        return dateFormat.format(currentDate);
    }

}
