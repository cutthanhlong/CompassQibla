package com.test.compass.Fragment;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
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
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;
import com.test.compass.R;
import com.test.compass.Service.PrayerService;
import com.test.compass.activity.MainActivity;
import com.test.compass.activity.PrayerSettingActivity;
import com.test.compass.activity.SettingPrayer;
import com.test.compass.adapter.prayer.PrayerAdapter;
import com.test.compass.adapter.prayer.PrayerModel;
import com.test.compass.call_api_prayer.AladhanApi;
import com.test.compass.call_api_prayer.PrayerDateData;
import com.test.compass.call_api_prayer.PrayerDateResponse;
import com.test.compass.databinding.FragmentPrayerBinding;
import com.test.compass.util.Constant;
import com.test.compass.util.IsNetWork;
import com.test.compass.util.SharePreferencesController;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;


public class FragmentPrayer extends Fragment {

    FragmentPrayerBinding binding;
    LinearLayoutManager linearLayoutManager;
    public static List<PrayerModel> listPrayer;
    public static PrayerAdapter prayerAdapter;
    Disposable disposable;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private double currentLatitude;
    private double currentLongitude;
    private String current_date;
    private Calendar calendar;
    public static TextView time_service;
    private int date = 0;
    private int method = 0;
    private int school = 0;
    public static TextView tv_next_step;

    RelativeLayout rl_read, rl_camera, rl_gps;
    ImageView img_read, img_camera, img_gps;
    private Dialog dialog;

    private ActivityResultLauncher<String> requestCameraPermissionLauncher;
    private ActivityResultLauncher<String> requestLocationPermissionLauncher;


    private int locationPermissionDeniedCount = 0;
    private int locationPermissionDeniedCountCamera = 0;

    ActivityResultLauncher<Intent> resultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == RESULT_OK) {
            if (result.getData() != null) {
                PrayerModel updatedPrayer = (PrayerModel) result.getData().getSerializableExtra("updatedPrayer");
                updateAdapterWithUpdatedPrayer(updatedPrayer);
            }
        }
    });


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentPrayerBinding.inflate(getLayoutInflater());
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        method = SharePreferencesController.getInstance(requireActivity()).getInt("method", 3);
        school = SharePreferencesController.getInstance(requireActivity()).getInt("school", 0);
        calendar = Calendar.getInstance();
        updateDate();
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
            rl_read.setOnClickListener(view -> {
                if (!checkLocationPermission()) {
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
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(requireActivity(), new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        currentLatitude = location.getLatitude();
                        currentLongitude = location.getLongitude();
                        callDate();
                        Log.d("call_data", currentLatitude + " &&& " + currentLongitude);
                        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
                        try {
                            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                            if (addresses != null && addresses.size() > 0) {
                                Address address = addresses.get(0);
                                String city = address.getAdminArea();
                                String country = address.getCountryName();
                                binding.tvCountry.setText(city + ", " + country);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });


        }
        requestLocationPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
            if (isGranted) {
                if (dialog != null && dialog.isShowing()) {
                    img_read.setImageResource(R.drawable.switch_on);
                }
                locationPermissionDeniedCount = 0;
            } else {
                locationPermissionDeniedCount++;
                img_read.setImageResource(R.drawable.switch_off);
            }
        });
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

        ((MainActivity) requireActivity()).addMainVPListener(new MainActivity.MainVPListenerPrayer() {
            @Override
            public void onCall_API() {
                if (checkLocationPermission()) {
                    callDate();
                }
            }
        });
        initView();
        bindView();
        time_service = binding.tvCountDown;
        tv_next_step = binding.tvNextStep;
        Log.e("aaa", "onCreate");
        return binding.getRoot();
    }

    private static String displayTimeZone(TimeZone tz) {

        long hours = TimeUnit.MILLISECONDS.toHours(tz.getRawOffset());
        String result = "";
        if (hours > 0) {
            result = String.format("GMT (+%d)", hours);
        } else {
            result = String.format("GMT (%d)", hours);
        }

        return result;

    }

    public void initView() {

        addData();

        binding.lnNoInternet.setVisibility(View.GONE);
        binding.lnInternet.setVisibility(View.VISIBLE);

        linearLayoutManager = new LinearLayoutManager(requireActivity());
        binding.rcvPrayer.setLayoutManager(linearLayoutManager);

        prayerAdapter = new PrayerAdapter(requireActivity(), listPrayer, time -> {

        }, pos -> {
            if (listPrayer.get(pos).isAlarm()) {
                listPrayer.get(pos).cancelAlarm(requireActivity());
                PrayerModel prayer = listPrayer.get(pos);
                Intent intent = new Intent(requireActivity(), PrayerSettingActivity.class);
                intent.putExtra("prayer", prayer);
                resultLauncher.launch(intent);
            } else {
                PrayerModel prayer = listPrayer.get(pos);
                Intent intent = new Intent(requireActivity(), PrayerSettingActivity.class);
                intent.putExtra("prayer", prayer);
                resultLauncher.launch(intent);
            }
        });
        prayerAdapter.setCheck(listPrayer.get(0).getName());
        binding.rcvPrayer.setAdapter(prayerAdapter);
    }

    public void bindView() {

        binding.ivMoreP.setOnClickListener(view -> {
            startActivity(new Intent(requireActivity(), SettingPrayer.class));
        });
        binding.ivLeftP.setOnClickListener(view -> {
            updateDateByOffset(-1);
            callDateSetting();
        });
        binding.ivRightP.setOnClickListener(view -> {
            updateDateByOffset(1);
            callDateSetting();
        });

        TimeZone currentTimeZone = TimeZone.getDefault();

        String timeZoneInfo = displayTimeZone(currentTimeZone);
        binding.tvGmt.setText(timeZoneInfo);
    }

    public void addData() {
        listPrayer = new ArrayList<>();
        listPrayer.add(new PrayerModel(1, "Fajar", "05:00", "05:00"));
        listPrayer.add(new PrayerModel(2, "Sunrise", "06:00", "06:00"));
        listPrayer.add(new PrayerModel(3, "Dhuhr", "11:30", "11:30"));
        listPrayer.add(new PrayerModel(4, "Asr", "15:00", "15:00"));
        listPrayer.add(new PrayerModel(5, "Maghrib", "17:00", "17:00"));
        listPrayer.add(new PrayerModel(6, "Isha", "18:50", "18:50"));
    }

    private boolean checkLocationPermission() {
        return ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    public boolean checkGPSActivation() {
        Object systemService = getActivity().getSystemService(Context.LOCATION_SERVICE);
        Objects.requireNonNull(systemService);
        return ((LocationManager) systemService).isProviderEnabled("gps");
    }

    private boolean checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            return false;
        } else {
            return true;
        }
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
        boolean hasCoarsePermission = checkLocationPermission();
        return hasCoarsePermission && cameraPermission && gpsPermission;
    }


    @Override
    public void onResume() {
        super.onResume();

        Log.e("aaa", "onResume");
        date = SharePreferencesController.getInstance(requireActivity()).getInt("date", 0);
        method = SharePreferencesController.getInstance(requireActivity()).getInt("method", 3);
        school = SharePreferencesController.getInstance(requireActivity()).getInt("school", 0);
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
        if (checkLocationPermission()) {
            binding.lnCheckPermission.setVisibility(View.GONE);
            binding.lnInternet.setVisibility(View.VISIBLE);
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(requireActivity(), new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        currentLatitude = location.getLatitude();
                        currentLongitude = location.getLongitude();
                        callDate();
                        callDateSetting();
                        if (date == -2) {
                            Calendar calendar1 = Calendar.getInstance();
                            calendar1.add(Calendar.DAY_OF_MONTH, -2);
                            String newDate = formatDate(calendar1.getTime());
                            callAPIbyHijiDate(newDate);
                        } else if (date == -1) {
                            Calendar calendar1 = Calendar.getInstance();
                            calendar1.add(Calendar.DAY_OF_MONTH, -1);
                            String newDate = formatDate(calendar1.getTime());
                            callAPIbyHijiDate(newDate);
                        } else if (date == 0) {
                            callAPIbyHijiDate(Constant.getCurrentDateString());
                        } else if (date == 1) {
                            Calendar calendar1 = Calendar.getInstance();
                            calendar1.add(Calendar.DAY_OF_MONTH, 1);
                            String newDate = formatDate(calendar1.getTime());
                            callAPIbyHijiDate(newDate);
                        } else {
                            Calendar calendar1 = Calendar.getInstance();
                            calendar1.add(Calendar.DAY_OF_MONTH, 2);
                            String newDate = formatDate(calendar1.getTime());
                            callAPIbyHijiDate(newDate);
                        }
                        Log.d("call_data", currentLatitude + " &&& " + currentLongitude);
                        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
                        try {
                            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                            if (addresses != null && addresses.size() > 0) {
                                Address address = addresses.get(0);
                                String city = address.getAdminArea();
                                String country = address.getCountryName();
                                binding.tvCountry.setText(city + ", " + country);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        } else {
            binding.lnInternet.setVisibility(View.GONE);
            binding.lnCheckPermission.setVisibility(View.VISIBLE);
        }
        binding.tvReload.setOnClickListener(view -> {
            showDialogPermission();
        });


        Log.e("aaa", "method" + method);
        Log.e("aaa", "school" + school);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (disposable != null) {
            disposable.dispose();
        }
    }


    private void callDate() {
        Log.e("aaa", "callDate");
        if (IsNetWork.haveNetworkConnectionSplash(requireActivity())) {
            binding.lnNoInternet.setVisibility(View.GONE);
            binding.lnInternet.setVisibility(View.VISIBLE);
            binding.loading.setVisibility(View.VISIBLE);
            binding.loading.playAnimation();
            AladhanApi.call_api.getPrayerTimings(Constant.getCurrentDateString(), currentLatitude, currentLongitude, method, school).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<PrayerDateResponse>() {
                        @Override
                        public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {
                            disposable = d;
                        }

                        @Override
                        public void onNext(@io.reactivex.rxjava3.annotations.NonNull PrayerDateResponse prayerTimesResponse) {
                            if (prayerTimesResponse != null && prayerTimesResponse.getData() != null) {
                                PrayerDateData prayerDateData = prayerTimesResponse.getData();
                                SimpleDateFormat inputDateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                                try {
                                    Date date = inputDateFormat.parse(prayerDateData.getDateInfo().getGregorian().getDate());
                                    SimpleDateFormat outputDateFormat = new SimpleDateFormat("EEEE, MMMM dd, yyyy", Locale.ENGLISH);
                                    binding.tvDateTime.setText(outputDateFormat.format(Objects.requireNonNull(date)));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                binding.tvTimeWord.setText(prayerDateData.getDateInfo().getHijri().getDay() + ", " + prayerDateData.getDateInfo().getHijri().getMonth().getEn() + ", " + prayerDateData.getDateInfo().getHijri().getYear() + " " + prayerDateData.getDateInfo().getHijri().getDesignation().getAbbreviated());
                                listPrayer.get(0).setTimer(prayerDateData.getPrayerTimings().getFajr());
                                listPrayer.get(1).setTimer(prayerDateData.getPrayerTimings().getSunrise());
                                listPrayer.get(2).setTimer(prayerDateData.getPrayerTimings().getDhuhr());
                                listPrayer.get(3).setTimer(prayerDateData.getPrayerTimings().getAsr());
                                listPrayer.get(4).setTimer(prayerDateData.getPrayerTimings().getMaghrib());
                                listPrayer.get(5).setTimer(prayerDateData.getPrayerTimings().getIsha());

                                listPrayer.get(0).setName("Fajr");
                                listPrayer.get(1).setName("Sunrise");
                                listPrayer.get(2).setName("Dhuhr");
                                listPrayer.get(3).setName("Asr");
                                listPrayer.get(4).setName("Maghrib");
                                listPrayer.get(5).setName("Isha");

                                if (readTimeAlarm(listPrayer.get(0).getId()) == null && readTimeAlarm(listPrayer.get(1).getId()) == null && readTimeAlarm(listPrayer.get(2).getId()) == null && readTimeAlarm(listPrayer.get(3).getId()) == null && readTimeAlarm(listPrayer.get(4).getId()) == null && readTimeAlarm(listPrayer.get(5).getId()) == null) {
                                    listPrayer.get(0).setTime_alarm(prayerDateData.getPrayerTimings().getFajr());
                                    listPrayer.get(1).setTime_alarm(prayerDateData.getPrayerTimings().getSunrise());
                                    listPrayer.get(2).setTime_alarm(prayerDateData.getPrayerTimings().getDhuhr());
                                    listPrayer.get(3).setTime_alarm(prayerDateData.getPrayerTimings().getAsr());
                                    listPrayer.get(4).setTime_alarm(prayerDateData.getPrayerTimings().getMaghrib());
                                    listPrayer.get(5).setTime_alarm(prayerDateData.getPrayerTimings().getIsha());
                                    saveTimeAlarm(listPrayer.get(0).getId(), prayerDateData.getPrayerTimings().getFajr());
                                    saveTimeAlarm(listPrayer.get(1).getId(), prayerDateData.getPrayerTimings().getSunrise());
                                    saveTimeAlarm(listPrayer.get(2).getId(), prayerDateData.getPrayerTimings().getDhuhr());
                                    saveTimeAlarm(listPrayer.get(3).getId(), prayerDateData.getPrayerTimings().getAsr());
                                    saveTimeAlarm(listPrayer.get(4).getId(), prayerDateData.getPrayerTimings().getMaghrib());
                                    saveTimeAlarm(listPrayer.get(5).getId(), prayerDateData.getPrayerTimings().getIsha());
                                }


                                Intent serviceIntent = new Intent(requireActivity(), PrayerService.class);
                                serviceIntent.putExtra("Fajr", prayerDateData.getPrayerTimings().getFajr());
                                serviceIntent.putExtra("Sunrise", prayerDateData.getPrayerTimings().getSunrise());
                                serviceIntent.putExtra("Dhuhr", prayerDateData.getPrayerTimings().getDhuhr());
                                serviceIntent.putExtra("Asr", prayerDateData.getPrayerTimings().getAsr());
                                serviceIntent.putExtra("Maghrib", prayerDateData.getPrayerTimings().getMaghrib());
                                serviceIntent.putExtra("Isha", prayerDateData.getPrayerTimings().getIsha());

                                if (!isServiceRunning(PrayerService.class)) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                        requireActivity().startForegroundService(serviceIntent);
                                    } else {
                                        requireActivity().startService(serviceIntent);
                                    }
                                }
                                binding.rcvPrayer.setAdapter(prayerAdapter);
                                prayerAdapter.notifyDataSetChanged();
                                Log.d("call_data", new Gson().toJson(prayerTimesResponse));
                            }
                        }

                        @Override
                        public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                            Log.e("call_data", e.getMessage() + "Lỗi");
                        }

                        @Override
                        public void onComplete() {
                            binding.loading.setVisibility(View.GONE);
                        }
                    });
        } else {
            binding.lnNoInternet.setVisibility(View.VISIBLE);
            binding.lnInternet.setVisibility(View.GONE);
        }
    }

    private void callDateSetting() {
        Log.e("aaa", "callDatebySeting");
        if (IsNetWork.haveNetworkConnectionSplash(requireActivity())) {
            binding.lnNoInternet.setVisibility(View.GONE);
            binding.lnInternet.setVisibility(View.VISIBLE);
            binding.loading.setVisibility(View.VISIBLE);
            binding.loading.playAnimation();
            AladhanApi.call_api.getPrayerTimings(current_date, currentLatitude, currentLongitude, method, school).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<PrayerDateResponse>() {
                        @Override
                        public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {
                            disposable = d;
                        }

                        @Override
                        public void onNext(@io.reactivex.rxjava3.annotations.NonNull PrayerDateResponse prayerTimesResponse) {
                            if (prayerTimesResponse != null && prayerTimesResponse.getData() != null) {
                                PrayerDateData prayerDateData = prayerTimesResponse.getData();
                                binding.tvTimeWord.setText(prayerDateData.getDateInfo().getHijri().getDay() + ", " + prayerDateData.getDateInfo().getHijri().getMonth().getEn() + ", " + prayerDateData.getDateInfo().getHijri().getYear() + " " + prayerDateData.getDateInfo().getHijri().getDesignation().getAbbreviated());
                                SimpleDateFormat inputDateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                                try {
                                    Date date = inputDateFormat.parse(prayerDateData.getDateInfo().getGregorian().getDate());
                                    SimpleDateFormat outputDateFormat = new SimpleDateFormat("EEEE, MMMM dd, yyyy", Locale.ENGLISH);
                                    binding.tvDateTime.setText(outputDateFormat.format(Objects.requireNonNull(date)));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

                                if (Constant.getCurrentDateString().equals(prayerDateData.getDateInfo().getGregorian().getDate())) {
                                    binding.tvCountDown.setVisibility(View.VISIBLE);
                                    binding.tvNextStep.setVisibility(View.VISIBLE);
                                } else {
                                    binding.tvNextStep.setVisibility(View.INVISIBLE);
                                    binding.tvCountDown.setVisibility(View.INVISIBLE);
                                }

                                listPrayer.get(0).setTimer(prayerDateData.getPrayerTimings().getFajr());
                                listPrayer.get(1).setTimer(prayerDateData.getPrayerTimings().getSunrise());
                                listPrayer.get(2).setTimer(prayerDateData.getPrayerTimings().getDhuhr());
                                listPrayer.get(3).setTimer(prayerDateData.getPrayerTimings().getAsr());
                                listPrayer.get(4).setTimer(prayerDateData.getPrayerTimings().getMaghrib());
                                listPrayer.get(5).setTimer(prayerDateData.getPrayerTimings().getIsha());

                                if (readTimeAlarm(listPrayer.get(0).getId()) == null && readTimeAlarm(listPrayer.get(1).getId()) == null && readTimeAlarm(listPrayer.get(2).getId()) == null && readTimeAlarm(listPrayer.get(3).getId()) == null && readTimeAlarm(listPrayer.get(4).getId()) == null && readTimeAlarm(listPrayer.get(5).getId()) == null) {
                                    listPrayer.get(0).setTime_alarm(prayerDateData.getPrayerTimings().getFajr());
                                    listPrayer.get(1).setTime_alarm(prayerDateData.getPrayerTimings().getSunrise());
                                    listPrayer.get(2).setTime_alarm(prayerDateData.getPrayerTimings().getDhuhr());
                                    listPrayer.get(3).setTime_alarm(prayerDateData.getPrayerTimings().getAsr());
                                    listPrayer.get(4).setTime_alarm(prayerDateData.getPrayerTimings().getMaghrib());
                                    listPrayer.get(5).setTime_alarm(prayerDateData.getPrayerTimings().getIsha());
                                }


                                listPrayer.get(0).setName("Fajr");
                                listPrayer.get(1).setName("Sunrise");
                                listPrayer.get(2).setName("Dhuhr");
                                listPrayer.get(3).setName("Asr");
                                listPrayer.get(4).setName("Maghrib");
                                listPrayer.get(5).setName("Isha");

                                binding.rcvPrayer.setAdapter(prayerAdapter);
                                prayerAdapter.notifyDataSetChanged();
                            }
                        }

                        @Override
                        public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                            Log.e("call_data", e.getMessage());
                        }

                        @Override
                        public void onComplete() {
                            binding.loading.setVisibility(View.GONE);
                        }
                    });
        } else {
            binding.lnNoInternet.setVisibility(View.VISIBLE);
            binding.lnInternet.setVisibility(View.GONE);
        }
    }

    private void callAPIbyHijiDate(String date) {
        Log.e("aaa", "callAPIbyHijiDate");

        if (IsNetWork.haveNetworkConnectionSplash(requireActivity())) {
            binding.lnNoInternet.setVisibility(View.GONE);
            binding.lnInternet.setVisibility(View.VISIBLE);
            binding.loading.setVisibility(View.VISIBLE);
            binding.loading.playAnimation();
            AladhanApi.call_api.getPrayerTimings(date, currentLatitude, currentLongitude, method, school).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<PrayerDateResponse>() {
                        @Override
                        public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {
                            disposable = d;
                        }

                        @Override
                        public void onNext(@io.reactivex.rxjava3.annotations.NonNull PrayerDateResponse prayerTimesResponse) {
                            if (prayerTimesResponse != null && prayerTimesResponse.getData() != null) {
                                PrayerDateData prayerDateData = prayerTimesResponse.getData();
                                binding.tvTimeWord.setText(prayerDateData.getDateInfo().getHijri().getDay() + ", " + prayerDateData.getDateInfo().getHijri().getMonth().getEn() + ", " + prayerDateData.getDateInfo().getHijri().getYear() + " " + prayerDateData.getDateInfo().getHijri().getDesignation().getAbbreviated());
                            }
                        }

                        @Override
                        public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                            Log.e("call_data", e.getMessage() + "Lỗi");
                        }

                        @Override
                        public void onComplete() {
                            binding.loading.setVisibility(View.GONE);
                        }
                    });
        } else {
            binding.lnNoInternet.setVisibility(View.VISIBLE);
            binding.lnInternet.setVisibility(View.GONE);

        }
    }

    private void updateDate() {
        String currentDate = formatDate(calendar.getTime());
        current_date = currentDate;
        SharePreferencesController.getInstance(requireActivity()).putString("date_now", currentDate);
    }

    private void updateDateByOffset(int offset) {
        calendar.add(Calendar.DAY_OF_MONTH, offset);
        String newDate = formatDate(calendar.getTime());
        current_date = newDate;
    }

    private String formatDate(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        return dateFormat.format(date);
    }

    private boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) requireActivity().getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> runningServices = manager.getRunningServices(Integer.MAX_VALUE);
        for (ActivityManager.RunningServiceInfo service : runningServices) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void updateAdapterWithUpdatedPrayer(PrayerModel updatedPrayer) {
        int position = findPositionOfPrayer(updatedPrayer);
        if (position != -1) {
            listPrayer.set(position, updatedPrayer);
            prayerAdapter.notifyDataSetChanged();
        }
    }

    private int findPositionOfPrayer(PrayerModel prayerToFind) {
        for (int i = 0; i < listPrayer.size(); i++) {
            if (listPrayer.get(i).getName().equals(prayerToFind.getName())) {
                return i;
            }
        }
        return -1;
    }

    private void saveTimeAlarm(int prayerId, String timeAlarm) {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("prayer_prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("time_alarm_" + prayerId, timeAlarm);
        editor.apply();
    }

    private String readTimeAlarm(int prayerId) {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("prayer_prefs", Context.MODE_PRIVATE);
        return sharedPreferences.getString("time_alarm_" + prayerId, null);
    }

    public void showDialogPermission() {
        CardView ok;
        dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.dialog_poup_permission);
        Objects.requireNonNull(dialog.getWindow()).setGravity(Gravity.CENTER);
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
        rl_read.setOnClickListener(view -> {
            if (!checkLocationPermission()) {
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
        ok.setOnClickListener(view -> dialog.dismiss());

        dialog.show();
    }


}
