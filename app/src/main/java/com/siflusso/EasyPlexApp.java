package com.siflusso;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.multidex.MultiDexApplication;

import com.applovin.sdk.AppLovinSdk;
import com.siflusso.di.AppInjector;
import com.siflusso.ui.downloadmanager.core.DownloadNotifier;
import com.siflusso.ui.manager.SettingsManager;
import com.siflusso.util.Tools;
import com.facebook.FacebookSdk;
import com.facebook.ads.AudienceNetworkAds;
import com.google.android.gms.ads.MobileAds;
import com.jaredrummler.android.device.DeviceName;
import com.stringcare.library.SC;
import com.unity3d.ads.IUnityAdsInitializationListener;
import com.unity3d.ads.UnityAds;
import com.vungle.warren.InitCallback;
import com.vungle.warren.Vungle;
import com.vungle.warren.error.VungleException;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasAndroidInjector;
import timber.log.Timber;

/**
 * Application level class.
 *
 * @author Yobex.
 */
public class EasyPlexApp extends MultiDexApplication implements HasAndroidInjector {


    @Inject
    SharedPreferences sharedPreferences;

    @Inject
    DispatchingAndroidInjector<Object> androidInjector;

    @Inject
    SettingsManager settingsManager;


    @SuppressLint("StaticFieldLeak")
    private static Context context;


    @Override
    public void onCreate() {
        SC.init(this);
        super.onCreate();

        AppInjector.init(this);
        DeviceName.init(this);

        if (BuildConfig.DEBUG) { Timber.plant(new Timber.DebugTree());}

        Timber.i("Creating EasyPlex Application");

        EasyPlexApp.context = getApplicationContext();
        DownloadNotifier downloadNotifier = DownloadNotifier.getInstance(this);
        downloadNotifier.makeNotifyChans();
        downloadNotifier.startUpdate();

        // Initialize the Mobile Ads SDK.
        MobileAds.initialize(context, initializationStatus -> {});

        // Initialize the Audience Network SDK
        AudienceNetworkAds.initialize(context);

        // Initialize Vungle Network SDK
        Vungle.init(settingsManager.getSettings().getVungleAppid(), context, new InitCallback() {
            @Override
            public void onSuccess() {
                //
            }

            @Override
            public void onError(VungleException exception) {
                //

            }

            @Override
            public void onAutoCacheAdAvailable(String placementId) {
                // Ad has become available to play for a cache optimized placement
            }
        });

        // Initialize the Unity Ads  Network SDK
        if (settingsManager.getSettings().getUnityGameId() !=null) {

            UnityAds.initialize(context, settingsManager.getSettings().getUnityGameId(), false, new IUnityAdsInitializationListener() {
                @Override
                public void onInitializationComplete() {
                    //
                }

                @Override
                public void onInitializationFailed(UnityAds.UnityAdsInitializationError error, String message) {
                    //
                }
            });
        }
        AppLovinSdk.getInstance(context).setMediationProvider( "max" );
        AppLovinSdk.initializeSdk( context, configuration -> {
            //AppLovin SDK is initialized, start loading ads
        });


    }


    public static boolean hasNetwork() {
        return Tools.checkIfHasNetwork(context);
    }



    public static Context getContext() {
        return context;
    }

    @Override
    public AndroidInjector<Object> androidInjector() {
        AppInjector.init(this);
        return androidInjector;
    }



}

/*
 * Application has activities that is why we implement HasActivityInjector interface.
 * Activities have fragments so we have to implement HasFragmentInjector/HasSupportFragmentInjector
 * in our activities.
 * No child fragment and don’t inject anything in your fragments, no need to implement
 * HasSupportFragmentInjector.
 */
