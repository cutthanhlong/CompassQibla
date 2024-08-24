package com.test.compass.call_api_prayer;

import java.util.concurrent.TimeUnit;

import hu.akarnokd.rxjava3.retrofit.RxJava3CallAdapterFactory;
import io.reactivex.rxjava3.core.Observable;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface AladhanApi {
    String BASE_URL = "https://api.aladhan.com/";
    HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY);
    OkHttpClient.Builder okbuilder = new OkHttpClient.Builder()
            .readTimeout(60, TimeUnit.SECONDS)
            .connectTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .addInterceptor(loggingInterceptor);

    AladhanApi call_api = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .client(okbuilder.build())
            .build()
            .create(AladhanApi.class);

    @GET("v1/methods")
    Observable<PrayerTimesResponse> getPrayerTimes();

    @GET("v1/timings/{date}")
    Observable<PrayerDateResponse> getPrayerTimings(
            @Path("date") String date,
            @Query("latitude") double latitude,
            @Query("longitude") double longitude,
            @Query("method") int method,
            @Query("school") int school
    );


    //getTime by method PrayerTimeData https://api.aladhan.com/v1/timingsByAddress/16-12-2023?address=H%C3%A0%20N%E1%BB%99i&method=3
    // ngày hiện tại, vị trí  hiện tại + method là id

    //getTime by method PrayerTimeData https://api.aladhan.com/v1/timings/16-12-2023?latitude=21.029611812828943&longitude=105.82236655339233&method=3
    // ngày hiện tại + lat + long + method id

    //get List month  by method PrayerTimeData https://api.aladhan.com/v1/calendar/2023?latitude=21.029611812828943&longitude=105.82236655339233&method=3
    // năm hiện tại + lat + long + method id

    //Calculation Asr Time get  https://api.aladhan.com/v1/calendar/2023?latitude=21.029611812828943&longitude=105.82236655339233&method=3&school=0
    // năm hiện tại + lat +long + method id + school = 0: Shafi or school = 1 Hanafi

    // FormatTime Setting 12h or 24h sẽ thay đổi time prayer đang chạy

}
