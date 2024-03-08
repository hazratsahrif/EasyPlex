package com.siflusso.ui.splash;

import static android.view.View.GONE;
import static com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions.withCrossFade;
import static com.siflusso.util.Constants.DEVICE_LIMIT;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.jaredrummler.android.device.DeviceName;
import com.siflusso.R;
import com.siflusso.data.model.ads.Ads;
import com.siflusso.data.model.auth.UserAuthInfo;
import com.siflusso.data.model.settings.Settings;
import com.siflusso.data.repository.AuthRepository;
import com.siflusso.data.repository.SettingsRepository;
import com.siflusso.databinding.ActivitySplashBinding;
import com.siflusso.di.Injectable;
import com.siflusso.ui.base.BaseActivity;
import com.siflusso.ui.devices.UserDevicesManagement;
import com.siflusso.ui.login.LoginActivity;
import com.siflusso.ui.manager.AdsManager;
import com.siflusso.ui.manager.SettingsManager;
import com.siflusso.ui.manager.StatusManager;
import com.siflusso.ui.users.PhoneAuthActivity;
import com.siflusso.ui.users.UserProfiles;
import com.siflusso.util.Constants;
import com.siflusso.util.DialogHelper;
import com.siflusso.util.GlideApp;
import com.siflusso.util.NetworkUtils;
import com.siflusso.util.Tools;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import dagger.android.AndroidInjection;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import timber.log.Timber;


@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity implements Injectable {

    ActivitySplashBinding binding;
    @Inject
    AuthRepository authRepository;

    @Inject
    SettingsRepository settingsRepository;

    @Inject
    SettingsManager settingsManager;


    @Inject
    AdsManager adsManager;


    @Inject
    StatusManager statusManager;


    @Inject
    ViewModelProvider.Factory viewModelFactory;


    @Inject
    @Named("sniffer")
    @Nullable
    ApplicationInfo provideApplicationInfo;

    @Inject
    @Named("package_name")
    String packageName;



    @Inject
    @Named("vpn")
    boolean checkVpn;


    @Inject
    @Singleton
    @Named("firebaseRemoteUrl")
    FirebaseRemoteConfig provideFirebaseRemoteConfig;

    @SuppressLint("TimberArgCount")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this,R.layout.activity_splash);

        provideFirebaseRemoteConfig.fetchAndActivate().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                if (task.isSuccessful()){
                    Timber.tag("TAG").d("onActivate: %s", task.getResult());
                }else {
                    Timber.tag("TAG").d(task.getException(), "onActivationError: %s");
                }
            }else {
                Timber.tag("TAG").e(task.getException(), "onError: %s");
            }


        });


        onHideTaskBar();
        onLoadLogo();
        onLoadSplashImage();

        if (provideApplicationInfo != null){

            DialogHelper.snifferAppDetectorDialog(this,provideApplicationInfo.loadLabel(this.getPackageManager()).toString());

        }else if (settingsManager.getSettings().getVpn() ==1 && checkVpn) {


            finishAffinity();

            Toast.makeText(this, R.string.vpn_message, Toast.LENGTH_SHORT).show();

        } else {


            final Handler handler = new Handler();

            long delayMillis = Constants.FIREBASECONFIG ? 3000 : 500;

            handler.postDelayed(() -> settingsRepository.getSettings()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<>() {
                        @Override
                        public void onSubscribe(@NotNull Disposable d) {
                            // Subscription logic
                        }

                        @Override
                        public void onNext(@NonNull Settings settings) {


                            settingsManager.saveSettings(settings);

                            settingsRepository.getAdsSettings()
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new Observer<>() {
                                        @Override
                                        public void onSubscribe(@NotNull Disposable d) {
                                            // Subscription logic
                                        }

                                        @Override
                                        public void onNext(@NonNull Ads ads) {
                                            adsManager.saveSettings(ads);
                                        }

                                        @Override
                                        public void onError(@NotNull Throwable e) {
                                            // Error handling for ads settings
                                        }

                                        @Override
                                        public void onComplete() {
                                            // Completion logic for ads settings
                                        }
                                    });
                            new Handler(Looper.getMainLooper()).postDelayed(() -> {
//                                checkIfUserIsLogedIn();
                                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                                finish();
                            }, 4500);
                        }

                        @Override
                        public void onError(@NotNull Throwable e) {
                            Toast.makeText(SplashActivity.this, getErrorMessage(), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onComplete() {
                            // Completion logic for settings
                        }

                        private String getErrorMessage() {
                            return Constants.FIREBASECONFIG
                                    ? "Error Loading App Settings. Make sure you are using the right API Address."
                                    : "Error Loading API. Please check your correct API address!";
                        }
                    }), delayMillis);



        }

    }




    private void onLoadSplashImage() {

        GlideApp.with(getApplicationContext()).asBitmap().load(settingsManager.getSettings().getSplashImage())
                .fitCenter()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .transition(withCrossFade())
                .skipMemoryCache(true)
                .into(binding.splashImage);

    }


    // Hide TaskBar
    private void onHideTaskBar() {

        Tools.hideSystemPlayerUi(this,true,0);
    }


    // Load Logo
    private void onLoadLogo() {

        Tools.loadMiniLogo(this,binding.logoImageTop);


    }
}
