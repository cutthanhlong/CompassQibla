package com.test.compass.Fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.RotateAnimation;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.test.compass.R;
import com.test.compass.activity.ActivityOptions;
import com.test.compass.adapter.CountryAdapter;
import com.test.compass.databinding.FragmentHomeBinding;
import com.test.compass.language.Model.CountryItem;
import com.test.compass.util.Constant;
import com.test.compass.util.IsNetWork;
import com.test.compass.util.SharePreferencesController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;


public class FragmentHome extends Fragment implements SensorEventListener, LocationListener {
    FragmentHomeBinding binding;

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
    private final float[] G_area_mGeoManegic = new float[3];
    private final float[] G_area_mGravity = new float[3];

    private final int REQUEST_CODE_LOCATION_PERMISSION = 125;
    private final int REQUEST_CODE_CAMERA_PERMISSION = 126;
    RelativeLayout rl_read, rl_camera, rl_gps;
    ImageView img_read, img_camera, img_gps;
    private Dialog dialog;
    private ActivityResultLauncher<String> requestCameraPermissionLauncher;
    private ActivityResultLauncher<String> requestLocationPermissionLauncher;
    private int locationPermissionDeniedCountNoti = 0;
    private int locationPermissionDeniedCount = 0;
    private int locationPermissionDeniedCountCamera = 0;
    boolean isQibla, isVibrate;
    boolean isVibrating = true;
    Vibrator vibrator;
    String TAG = "FragmentHome";
    int themeSelect = 0;

    //search country
    List<CountryItem> listCountry;
    CountryAdapter countryAdapter;
    double lLatitude, lLongitude;
    String txtGetLocation = "";


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(getLayoutInflater());

        if (!hasLocationPermission()) {
            CardView ok;
            dialog = new Dialog(getContext());
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
                    intialization();
                }
                locationPermissionDeniedCount = 0;
            } else {
                locationPermissionDeniedCount++;
                img_read.setImageResource(R.drawable.switch_off);
            }
        });
        checkSensor();
        this.mSensorManager = (SensorManager) requireActivity().getSystemService(Context.SENSOR_SERVICE);

        updateQibla();

        binding.icOptions.setOnClickListener(view -> {
            startActivity(new Intent(requireActivity(), ActivityOptions.class));
        });

        return binding.getRoot();
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
        ok.setOnClickListener(view -> dialog.dismiss());

        dialog.show();
    }

    public void updateQibla() {
        binding.tvLocationNow.setSelected(true);

        addDataCountry();

        vibrator = (Vibrator) requireActivity().getSystemService(requireActivity().VIBRATOR_SERVICE);
        isQibla = SharePreferencesController.getInstance(requireActivity()).getBoolean(Constant.IS_QIBLA, true);
        isVibrate = SharePreferencesController.getInstance(requireActivity()).getBoolean(Constant.IS_VIBRATE, true);

        if (!isQibla) {
            binding.ivBtnVibrate.setVisibility(View.GONE);
            binding.lnLocation.setVisibility(View.INVISIBLE);
            binding.ivBtnQibla.setImageResource(R.drawable.ic_qibla_sn);

            setUnVisibleView();

            rotate(0);
        } else {
            binding.ivBtnVibrate.setVisibility(View.VISIBLE);
            binding.lnLocation.setVisibility(View.VISIBLE);
            binding.ivBtnQibla.setImageResource(R.drawable.ic_qibla_s);

            setVisibleView();

            updateQiblaAngle();
        }

        if (!isVibrate) {
            binding.ivBtnVibrate.setImageResource(R.drawable.ic_vibrate_sn);
        } else {
            binding.ivBtnVibrate.setImageResource(R.drawable.ic_vibrate_s);
        }

        try {
            themeSelect = SharePreferencesController.getInstance(requireActivity()).getInt(Constant.THEME_HOME_SELECT, 0);
        } catch (Exception e) {
            themeSelect = 0;
        }
        binding.viewFlipper.setDisplayedChild(themeSelect);
        if (themeSelect == 0 || themeSelect == 2 || themeSelect == 5 || themeSelect == 6) {
            binding.tvDegree.setVisibility(View.VISIBLE);
            if (themeSelect == 5) {
                binding.tvDegree.setTextColor(ContextCompat.getColor(requireActivity(), R.color.color_0068B2));
            } else {
                binding.tvDegree.setTextColor(ContextCompat.getColor(requireActivity(), R.color.white));
            }
        } else {
            binding.tvDegree.setVisibility(View.GONE);
        }


        binding.ivBtnQibla.setOnClickListener(view -> {
            if (!hasLocationPermission()) {
                showDialogPermission();
            } else {
                if (isQibla) {
                    binding.ivBtnVibrate.setVisibility(View.GONE);
                    binding.lnLocation.setVisibility(View.INVISIBLE);
                    binding.ivBtnQibla.setImageResource(R.drawable.ic_qibla_sn);

                    setUnVisibleView();

                    rotate(0);
                } else {
                    binding.ivBtnVibrate.setVisibility(View.VISIBLE);
                    binding.lnLocation.setVisibility(View.VISIBLE);
                    binding.ivBtnQibla.setImageResource(R.drawable.ic_qibla_s);

                    setVisibleView();

                    updateQiblaAngle();
                }
                isQibla = !isQibla;
                SharePreferencesController.getInstance(requireActivity()).putBoolean(Constant.IS_QIBLA, isQibla);
            }
        });

        binding.ivBtnVibrate.setOnClickListener(view -> {
            if (isVibrate) {
                binding.ivBtnVibrate.setImageResource(R.drawable.ic_vibrate_sn);
            } else {
                binding.ivBtnVibrate.setImageResource(R.drawable.ic_vibrate_s);
            }

            isVibrate = !isVibrate;
            SharePreferencesController.getInstance(requireActivity()).putBoolean(Constant.IS_QIBLA, isVibrate);
        });

        binding.ivThemeNext.setOnClickListener(view -> {
            if (!hasLocationPermission()) {
                showDialogPermission();
            }
            setNewTheme(true);
        });

        binding.ivThemeBack.setOnClickListener(view -> {
            if (!hasLocationPermission()) {
                showDialogPermission();
            }
            setNewTheme(false);
        });

        binding.lnLocation.setOnClickListener(view -> {
            if (hasLocationPermission()) {
                showDialogSearchLocation();
            } else {
                showDialogPermission();
            }
        });
    }

    public void addDataCountry() {
        listCountry = new ArrayList<>();
        listCountry.add(new CountryItem("Afghanistan"));
        listCountry.add(new CountryItem("Albania"));
        listCountry.add(new CountryItem("Algeria"));
        listCountry.add(new CountryItem("Andorra"));
        listCountry.add(new CountryItem("Angola"));
        listCountry.add(new CountryItem("Antigua and Barbuda"));
        listCountry.add(new CountryItem("Argentina"));
        listCountry.add(new CountryItem("Armenia"));
        listCountry.add(new CountryItem("Australia"));
        listCountry.add(new CountryItem("Austria"));
        listCountry.add(new CountryItem("Azerbaijan"));
        listCountry.add(new CountryItem("Bahamas"));
        listCountry.add(new CountryItem("Bahrain"));
        listCountry.add(new CountryItem("Bangladesh"));
        listCountry.add(new CountryItem("Barbados"));
        listCountry.add(new CountryItem("Belarus"));
        listCountry.add(new CountryItem("Belgium"));
        listCountry.add(new CountryItem("Belize"));
        listCountry.add(new CountryItem("Benin"));
        listCountry.add(new CountryItem("Bhutan"));
        listCountry.add(new CountryItem("Bolivia"));
        listCountry.add(new CountryItem("Bosnia and Herzegovina"));
        listCountry.add(new CountryItem("Botswana"));
        listCountry.add(new CountryItem("Brazil"));
        listCountry.add(new CountryItem("Brunei"));
        listCountry.add(new CountryItem("Bulgaria"));
        listCountry.add(new CountryItem("Burkina Faso"));
        listCountry.add(new CountryItem("Burundi"));
        listCountry.add(new CountryItem("Cabo Verde"));
        listCountry.add(new CountryItem("Cambodia"));
        listCountry.add(new CountryItem("Cameroon"));
        listCountry.add(new CountryItem("Canada"));
        listCountry.add(new CountryItem("Central African Republic"));
        listCountry.add(new CountryItem("Chad"));
        listCountry.add(new CountryItem("Chile"));
        listCountry.add(new CountryItem("China"));
        listCountry.add(new CountryItem("Colombia"));
        listCountry.add(new CountryItem("Comoros"));
        listCountry.add(new CountryItem("Congo"));
        listCountry.add(new CountryItem("Costa Rica"));
        listCountry.add(new CountryItem("Côte d'Ivoire"));
        listCountry.add(new CountryItem("Croatia"));
        listCountry.add(new CountryItem("Cuba"));
        listCountry.add(new CountryItem("Cyprus"));
        listCountry.add(new CountryItem("Czech Republic (Czechia)"));
        listCountry.add(new CountryItem("Denmark"));
        listCountry.add(new CountryItem("Djibouti"));
        listCountry.add(new CountryItem("Dominica"));
        listCountry.add(new CountryItem("Dominican Republic"));
        listCountry.add(new CountryItem("DR Congo"));
        listCountry.add(new CountryItem("Ecuador"));
        listCountry.add(new CountryItem("Egypt"));
        listCountry.add(new CountryItem("El Salvador"));
        listCountry.add(new CountryItem("Equatorial Guinea"));
        listCountry.add(new CountryItem("Eritrea"));
        listCountry.add(new CountryItem("Estonia"));
        listCountry.add(new CountryItem("Eswatini"));
        listCountry.add(new CountryItem("Ethiopia"));
        listCountry.add(new CountryItem("Fiji"));
        listCountry.add(new CountryItem("Finland"));
        listCountry.add(new CountryItem("France"));
        listCountry.add(new CountryItem("Gabon"));
        listCountry.add(new CountryItem("Gambia"));
        listCountry.add(new CountryItem("Georgia"));
        listCountry.add(new CountryItem("Germany"));
        listCountry.add(new CountryItem("Ghana"));
        listCountry.add(new CountryItem("Greece"));
        listCountry.add(new CountryItem("Grenada"));
        listCountry.add(new CountryItem("Guatemala"));
        listCountry.add(new CountryItem("Guinea"));
        listCountry.add(new CountryItem("Guinea-Bissau"));
        listCountry.add(new CountryItem("Guyana"));
        listCountry.add(new CountryItem("Haiti"));
        listCountry.add(new CountryItem("Holy See"));
        listCountry.add(new CountryItem("Honduras"));
        listCountry.add(new CountryItem("Hungary"));
        listCountry.add(new CountryItem("Iceland"));
        listCountry.add(new CountryItem("India"));
        listCountry.add(new CountryItem("Indonesia"));
        listCountry.add(new CountryItem("Iran"));
        listCountry.add(new CountryItem("Iraq"));
        listCountry.add(new CountryItem("Ireland"));
        listCountry.add(new CountryItem("Israel"));
        listCountry.add(new CountryItem("Italy"));
        listCountry.add(new CountryItem("Jamaica"));
        listCountry.add(new CountryItem("Japan"));
        listCountry.add(new CountryItem("Jordan"));
        listCountry.add(new CountryItem("Kazakhstan"));
        listCountry.add(new CountryItem("Kenya"));
        listCountry.add(new CountryItem("Kiribati"));
        listCountry.add(new CountryItem("Kuwait"));
        listCountry.add(new CountryItem("Kyrgyzstan"));
        listCountry.add(new CountryItem("Laos"));
        listCountry.add(new CountryItem("Latvia"));
        listCountry.add(new CountryItem("Lebanon"));
        listCountry.add(new CountryItem("Lesotho"));
        listCountry.add(new CountryItem("Liberia"));
        listCountry.add(new CountryItem("Libya"));
        listCountry.add(new CountryItem("Liechtenstein"));
        listCountry.add(new CountryItem("Lithuania"));
        listCountry.add(new CountryItem("Luxembourg"));
        listCountry.add(new CountryItem("Madagascar"));
        listCountry.add(new CountryItem("Malawi"));
        listCountry.add(new CountryItem("Malaysia"));
        listCountry.add(new CountryItem("Maldives"));
        listCountry.add(new CountryItem("Mali"));
        listCountry.add(new CountryItem("Malta"));
        listCountry.add(new CountryItem("Marshall Islands"));
        listCountry.add(new CountryItem("Mauritania"));
        listCountry.add(new CountryItem("Mauritius"));
        listCountry.add(new CountryItem("Mexico"));
        listCountry.add(new CountryItem("Micronesia"));
        listCountry.add(new CountryItem("Moldova"));
        listCountry.add(new CountryItem("Monaco"));
        listCountry.add(new CountryItem("Mongolia"));
        listCountry.add(new CountryItem("Montenegro"));
        listCountry.add(new CountryItem("Morocco"));
        listCountry.add(new CountryItem("Mozambique"));
        listCountry.add(new CountryItem("Myanmar"));
        listCountry.add(new CountryItem("Namibia"));
        listCountry.add(new CountryItem("Nauru"));
        listCountry.add(new CountryItem("Nepal"));
        listCountry.add(new CountryItem("Netherlands"));
        listCountry.add(new CountryItem("New Zealand"));
        listCountry.add(new CountryItem("Nicaragua"));
        listCountry.add(new CountryItem("Niger"));
        listCountry.add(new CountryItem("Nigeria"));
        listCountry.add(new CountryItem("North Korea"));
        listCountry.add(new CountryItem("North Macedonia"));
        listCountry.add(new CountryItem("Norway"));
        listCountry.add(new CountryItem("Oman"));
        listCountry.add(new CountryItem("Pakistan"));
        listCountry.add(new CountryItem("Palau"));
        listCountry.add(new CountryItem("Panama"));
        listCountry.add(new CountryItem("Papua New Guinea"));
        listCountry.add(new CountryItem("Paraguay"));
        listCountry.add(new CountryItem("Peru"));
        listCountry.add(new CountryItem("Philippines"));
        listCountry.add(new CountryItem("Poland"));
        listCountry.add(new CountryItem("Portugal"));
        listCountry.add(new CountryItem("Qatar"));
        listCountry.add(new CountryItem("Romania"));
        listCountry.add(new CountryItem("Russia"));
        listCountry.add(new CountryItem("Rwanda"));
        listCountry.add(new CountryItem("Saint Kitts & Nevis"));
        listCountry.add(new CountryItem("Saint Lucia"));
        listCountry.add(new CountryItem("Samoa"));
        listCountry.add(new CountryItem("San Marino"));
        listCountry.add(new CountryItem("Sao Tome & Principe"));
        listCountry.add(new CountryItem("Saudi Arabia"));
        listCountry.add(new CountryItem("Senegal"));
        listCountry.add(new CountryItem("Serbia"));
        listCountry.add(new CountryItem("Seychelles"));
        listCountry.add(new CountryItem("Sierra Leone"));
        listCountry.add(new CountryItem("Singapore"));
        listCountry.add(new CountryItem("Slovakia"));
        listCountry.add(new CountryItem("Slovenia"));
        listCountry.add(new CountryItem("Solomon Islands"));
        listCountry.add(new CountryItem("Somalia"));
        listCountry.add(new CountryItem("South Africa"));
        listCountry.add(new CountryItem("South Korea"));
        listCountry.add(new CountryItem("South Sudan"));
        listCountry.add(new CountryItem("Spain"));
        listCountry.add(new CountryItem("Sri Lanka"));
        listCountry.add(new CountryItem("St. Vincent & Grenadines"));
        listCountry.add(new CountryItem("State of Palestine"));
        listCountry.add(new CountryItem("Sudan"));
        listCountry.add(new CountryItem("Suriname"));
        listCountry.add(new CountryItem("Sweden"));
        listCountry.add(new CountryItem("Switzerland"));
        listCountry.add(new CountryItem("Syria"));
        listCountry.add(new CountryItem("Tajikistan"));
        listCountry.add(new CountryItem("Tanzania"));
        listCountry.add(new CountryItem("Thailand"));
        listCountry.add(new CountryItem("Timor-Leste"));
        listCountry.add(new CountryItem("Togo"));
        listCountry.add(new CountryItem("Tonga"));
        listCountry.add(new CountryItem("Trinidad and Tobago"));
        listCountry.add(new CountryItem("Tunisia"));
        listCountry.add(new CountryItem("Turkey"));
        listCountry.add(new CountryItem("Turkmenistan"));
        listCountry.add(new CountryItem("Tuvalu"));
        listCountry.add(new CountryItem("Uganda"));
        listCountry.add(new CountryItem("Ukraine"));
        listCountry.add(new CountryItem("United Arab Emirates"));
        listCountry.add(new CountryItem("United Kingdom"));
        listCountry.add(new CountryItem("United States"));
        listCountry.add(new CountryItem("Uruguay"));
        listCountry.add(new CountryItem("Uzbekistan"));
        listCountry.add(new CountryItem("Vanuatu"));
        listCountry.add(new CountryItem("Venezuela"));
        listCountry.add(new CountryItem("Vietnam"));
        listCountry.add(new CountryItem("Yemen"));
        listCountry.add(new CountryItem("Zambia"));
        listCountry.add(new CountryItem("Zimbabwe"));
    }

    public void setVisibleView() {
        binding.ivQibla.setVisibility(View.VISIBLE);
        binding.ivQibla1.setVisibility(View.VISIBLE);
        binding.ivQibla2.setVisibility(View.VISIBLE);
        binding.ivQibla3.setVisibility(View.VISIBLE);
        binding.ivQibla4.setVisibility(View.VISIBLE);
        binding.ivQibla5.setVisibility(View.VISIBLE);
        binding.ivQibla6.setVisibility(View.VISIBLE);
    }

    public void setUnVisibleView() {
        binding.ivQibla.setVisibility(View.GONE);
        binding.ivQibla1.setVisibility(View.GONE);
        binding.ivQibla2.setVisibility(View.GONE);
        binding.ivQibla3.setVisibility(View.GONE);
        binding.ivQibla4.setVisibility(View.GONE);
        binding.ivQibla5.setVisibility(View.GONE);
        binding.ivQibla6.setVisibility(View.GONE);
    }

    public void setNewTheme(boolean isCheck) {
        //isCheck = true -> next
        //isCheck = false -> back

        if (isCheck) {
            themeSelect += 1;
        } else {
            themeSelect -= 1;
        }

        if (themeSelect == -1) {
            themeSelect = 6;
        } else if (themeSelect == 7) {
            themeSelect = 0;
        }

        SharePreferencesController.getInstance(requireActivity()).putInt(Constant.THEME_HOME_SELECT, themeSelect);
        binding.viewFlipper.setDisplayedChild(themeSelect);

        if (themeSelect == 0 || themeSelect == 2 || themeSelect == 5 || themeSelect == 6) {
            binding.tvDegree.setVisibility(View.VISIBLE);
            if (themeSelect == 5) {
                binding.tvDegree.setTextColor(ContextCompat.getColor(requireActivity(), R.color.color_0077CC));
            } else {
                binding.tvDegree.setTextColor(ContextCompat.getColor(requireActivity(), R.color.white));
            }
        } else {
            binding.tvDegree.setVisibility(View.GONE);
        }
    }

    private void intialization() {
        SensorManager sensorManager2 = (SensorManager) requireActivity().getSystemService(Context.SENSOR_SERVICE);
        this.sensorManager = sensorManager2;
        this.sensor = sensorManager2.getDefaultSensor(2);
    }

    private void checkSensor() {
        PackageManager packageManager = requireActivity().getPackageManager();
        if (packageManager.hasSystemFeature(PackageManager.FEATURE_SENSOR_COMPASS)) {
            intialization();
        } else {
            CardView ok;
            final Dialog dialog = new Dialog(getContext());
            dialog.setContentView(R.layout.dialog_poup_device);
            dialog.getWindow().setGravity(Gravity.CENTER);
            dialog.getWindow().setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setCancelable(false);
            ok = dialog.findViewById(R.id.ok);

            ok.setOnClickListener(view -> dialog.dismiss());
            dialog.show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!hasLocationPermission()) {
            binding.ivBtnVibrate.setVisibility(View.GONE);
            binding.lnLocation.setVisibility(View.INVISIBLE);
            binding.ivBtnQibla.setImageResource(R.drawable.ic_qibla_sn);

            setUnVisibleView();

            rotate(0);
        } else {
            binding.ivBtnVibrate.setVisibility(View.VISIBLE);
            binding.lnLocation.setVisibility(View.VISIBLE);
            binding.ivBtnQibla.setImageResource(R.drawable.ic_qibla_s);

            setVisibleView();

            updateQiblaAngle();
        }
        Log.d("aaa", "home");
        if (dialog != null && dialog.isShowing()) {
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

    float bearingTo;
    int startPosition, endPosition;
    FusedLocationProviderClient fusedLocationProviderClient;

    @SuppressLint("MissingPermission")
    private void updateQiblaAngle() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(requireActivity(), location -> {

        });
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(requireActivity(), location -> {
            try {
                if (location != null) {
                    lLatitude = location.getLatitude();
                    lLongitude = location.getLongitude();
                    setLocationQibla(lLatitude, lLongitude);

                    Geocoder geocoder = new Geocoder(requireActivity(), Locale.getDefault());
                    try {
                        List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                        txtGetLocation = addresses.get(0).getAddressLine(0);
                        binding.tvLocationNow.setText(txtGetLocation);
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(requireActivity(), requireActivity().getString(R.string.location_fail), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (fusedLocationProviderClient == null) {
                        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity());
                    } else {
                        if (countReturn < 3) {
                            updateQiblaAngle();
                        } else {
                            Toast.makeText(requireActivity(), requireActivity().getString(R.string.location_fail), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            } catch (Exception e) {
                Toast.makeText(requireActivity(), requireActivity().getString(R.string.location_fail), Toast.LENGTH_SHORT).show();
            }
        });
    }

    int countReturn = 0;

    private void showDialogSearchLocation() {
        TextView btnCancel, btnOk;
        AutoCompleteTextView mapSearch;
        ImageView ivGetLocation;

        Dialog dialog1 = new Dialog(requireActivity());
        dialog1.setContentView(R.layout.dialog_search_location);
        dialog1.getWindow().setGravity(Gravity.CENTER);
        dialog1.getWindow().setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        dialog1.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog1.setCancelable(false);

        btnCancel = dialog1.findViewById(R.id.btn_cancel);
        btnOk = dialog1.findViewById(R.id.btn_ok);
        ivGetLocation = dialog1.findViewById(R.id.iv_get_location);
        mapSearch = dialog1.findViewById(R.id.map_search);

        mapSearch.setThreshold(1);

        countryAdapter = new CountryAdapter(requireActivity(), listCountry);
        mapSearch.setAdapter(countryAdapter);

        btnCancel.setOnClickListener(view -> dialog1.dismiss());

        btnOk.setOnClickListener(view -> {
            if (mapSearch.getText().equals("")) {
                Toast.makeText(requireContext(), getString(R.string.please_add_text), Toast.LENGTH_SHORT).show();
            } else {
                if (IsNetWork.haveNetworkConnectionSplash(requireContext())) {
                    try {
                        String location = mapSearch.getText().toString();
                        List<Address> addressList;
                        Geocoder geocoder = new Geocoder(requireActivity());
                        addressList = geocoder.getFromLocationName(location, 1);
                        Address address = addressList.get(0);
                        Log.e(TAG, "list: " + addressList.size());
                        Log.e(TAG, "country: " + address.getCountryName());
                        Log.e(TAG, "latitude: " + address.getLatitude());
                        Log.e(TAG, "longitude: " + address.getLongitude());
                        lLatitude = address.getLatitude();
                        lLongitude = address.getLongitude();
                        setLocationQibla(lLatitude, lLongitude);
                        binding.tvLocationNow.setText(location);
                    } catch (Exception e) {
                        Toast.makeText(requireActivity(), requireActivity().getString(R.string.fail_to_search), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(requireActivity(), requireActivity().getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                }
                dialog1.dismiss();
            }
        });

        ivGetLocation.setOnClickListener(view -> {
            updateQiblaAngle();
            dialog1.dismiss();
        });

        dialog1.setOnDismissListener(dialogInterface -> addDataCountry());

        dialog1.show();

    }

    public void setLocationQibla(double currentLatitude, double currentLongitude) {
        Location locationEnd = new Location("makkah");
        locationEnd.setLatitude(21.3891d);
        locationEnd.setLongitude(39.8579d);

        Location locationStart = new Location("Currentlatlong");
        locationStart.setLatitude(currentLatitude);
        locationStart.setLongitude(currentLongitude);
        bearingTo = locationStart.bearingTo(locationEnd);
        if (bearingTo <= 0.0f) {
            bearingTo += 360.0f;
        }
        if (isQibla) {
            binding.tvDegree.setText((int) bearingTo + "°");
            rotate(bearingTo);
        }
        startPosition = (int) (bearingTo) - 1;
        endPosition = (int) (bearingTo) + 1;
        Log.e(TAG, "vĩ độ: " + currentLatitude);
        Log.e(TAG, "kinh độ: " + currentLongitude);
        Log.e(TAG, "startPosition: " + startPosition);
        Log.e(TAG, "endPosition: " + endPosition);
        Log.e(TAG, "Qibla Angle: " + String.format("%.02f", bearingTo) + "°");
    }

    private void rotate(float f) {
        RotateAnimation rotateAnimation = new RotateAnimation(0.0f, f, 1, 0.5f, 1, 0.5f);
        rotateAnimation.setDuration(1000);
        rotateAnimation.setFillAfter(true);
        binding.imgCompassNiddle.startAnimation(rotateAnimation);
        binding.imgCompassNiddle1.startAnimation(rotateAnimation);
        binding.imgCompassNiddle2.startAnimation(rotateAnimation);
        binding.imgCompassNiddle3.startAnimation(rotateAnimation);
        binding.imgCompassNiddle4.startAnimation(rotateAnimation);
        binding.imgCompassNiddle5.startAnimation(rotateAnimation);
        binding.imgCompassNiddle6.startAnimation(rotateAnimation);
    }

    private void openAppSettings() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", requireActivity().getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }

    public void onPause() {
        super.onPause();
        SensorManager sensorManager2 = this.sensorManager;
        if (sensorManager2 != null) {
            sensorManager2.unregisterListener(this);
        }
    }

    @SuppressLint("SetTextI18n")
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
            if (f >= startPosition && endPosition >= f && isQibla) {
                if (isVibrate && isVibrating) {
                    isVibrating = false;
                    vibrator.vibrate(150);
                }
                binding.ivHomeNen.setBackground(ContextCompat.getDrawable(requireActivity(), R.drawable.home_nen_s));
            } else {
                isVibrating = true;
                binding.ivHomeNen.setBackground(ContextCompat.getDrawable(requireActivity(), R.drawable.home_nen_sn));
            }
            this.binding.tvAddress.setText(textNumber((int) f));
            if (!isQibla) {
                binding.tvDegree.setText(f + "°");
            }
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
            float valueOf = fArr8[0];
            this.f3087n = valueOf;
            float m4806a = m4806a((valueOf * 360.0f) / 6.28318f, this.f3096w);
            this.f3096w = m4806a;
            int f = Math.round(m4806a + 360.0f) % 360;
            if (f >= startPosition && endPosition >= f && isQibla) {
                if (isVibrate && isVibrating) {
                    isVibrating = false;
                    vibrator.vibrate(150);
                }
                binding.ivHomeNen.setBackground(ContextCompat.getDrawable(requireActivity(), R.drawable.home_nen_s));
            } else {
                isVibrating = true;
                binding.ivHomeNen.setBackground(ContextCompat.getDrawable(requireActivity(), R.drawable.home_nen_sn));
            }
            this.binding.tvAddress.setText(textNumber(f));
            if (!isQibla) {
                binding.tvDegree.setText(f + "°");
            }
            RotateAnimation rotateAnimation = new RotateAnimation(this.G_area_f3084C, -this.f3096w, 1, 0.5f, 1, 0.5f);
            rotateAnimation.setFillAfter(true);
            if (this.f3095v) {
                binding.viewFlipper.startAnimation(rotateAnimation);
            } else {
                binding.viewFlipper.clearAnimation();
            }
            this.G_area_f3084C = -this.f3096w;
        }
    }

    public float m4806a(float f, float f2) {
        float f3 = f - f2;
        return Math.abs(f3) > 150.0f ? f > 0.0f ? f2 - (((f2 + 180.0f) + (180.0f - f)) * 0.05f) : f2 + ((f + 180.0f + (180.0f - f2)) * 0.05f) : f2 + (f3 * 0.05f);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
//        Geocoder geocoder = new Geocoder(requireActivity(), Locale.getDefault());
//        try {
//            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
//            binding.tvLocationNow.setText(addresses.get(0).getCountryName());
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

    private boolean hasLocationPermission() {
        boolean cameraPermission = checkCameraPermission();
        boolean gpsPermission = checkGPSActivation();
        boolean hasCoarsePermission = checkLocationFind();
        return hasCoarsePermission && cameraPermission && gpsPermission;
    }

    private boolean checkLocationFind() {
        return ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private boolean checkCameraPermission() {
        return ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    public boolean checkGPSActivation() {
        Object systemService = requireActivity().getSystemService(Context.LOCATION_SERVICE);
        Objects.requireNonNull(systemService);
        return ((LocationManager) systemService).isProviderEnabled("gps");
    }

    private void getLocation() {
        try {
            locationManager = (LocationManager) requireActivity().getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 500, 5, this);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    public String textNumber(int i) {
        StringBuilder sb = new StringBuilder();
        sb.append(i);
        sb.append("°");
        if (i <= 15 || i >= 345) {

            return sb + "N";
        } else if (i <= 75) {

            return sb + "NE";
        } else if (i <= 105) {

            return sb + "E";
        } else if (i <= 165) {

            return sb + "SE";
        } else if (i <= 195) {

            return sb + "S";
        } else if (i <= 255) {

            return sb + "SW";
        } else if (i <= 285) {

            return sb + "W";
        } else {
            return sb + "NW";
        }
    }


}
