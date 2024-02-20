package com.siflusso.ui.devices;

import static com.siflusso.util.Constants.DEVICE_LIMIT;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;

import com.siflusso.R;
import com.siflusso.data.model.auth.UserAuthInfo;
import com.siflusso.data.repository.AuthRepository;
import com.siflusso.databinding.ActivityDevicesManagementBinding;
import com.siflusso.di.Injectable;
import com.siflusso.ui.base.BaseActivity;
import com.siflusso.ui.manager.AuthManager;
import com.siflusso.ui.manager.SettingsManager;
import com.siflusso.ui.splash.SplashActivity;
import com.siflusso.ui.users.MenuHandler;
import com.siflusso.util.NetworkUtils;
import com.siflusso.util.SpacingItemDecoration;
import com.siflusso.util.Tools;
import com.jaredrummler.android.device.DeviceName;

import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class UserDevicesManagement extends AppCompatActivity implements Injectable {


    ActivityDevicesManagementBinding binding;

    @Inject
    AuthRepository authRepository;

    @Inject
    SettingsManager settingsManager;

    @Inject
    AuthManager authManager;

    @Inject
    MenuHandler menuHandler;

    @Inject
    DevicesManagementAdapter devicesManagementAdapter;

    private String isDeviceLimitReached;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        AndroidInjection.inject(this);

        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_devices_management);

        binding.deviceMaxAllowed.setText(getString(R.string.maximum_devices_allowed_is)+ DEVICE_LIMIT);

        binding.setController(menuHandler);

        Intent intent = getIntent();

        isDeviceLimitReached = intent.getStringExtra("isDeviceLimitReached");

        menuHandler.isDevicesLimitReached.set(isDeviceLimitReached != null && intent.getStringExtra("isDeviceLimitReached").equals("isDeviceLimitReached"));

        onLoadAppLogo();

        binding.rvProfiles.setHasFixedSize(true);
        binding.rvProfiles.setLayoutManager(new GridLayoutManager(this, 1));
        binding.rvProfiles.addItemDecoration(new SpacingItemDecoration(3, Tools.dpToPx(this, 0), true));
        binding.rvProfiles.setAdapter(devicesManagementAdapter);

        binding.rvProfiles.setEmptyView(binding.emptyViewDownloadList);

        onLoadDeviceLists();

        binding.btnCoutinue.setOnClickListener(view -> startActivity(new Intent(UserDevicesManagement.this, BaseActivity.class)));

        devicesManagementAdapter.setonDeleteDeviceListner(isDeleted -> {
            if (isDeleted) onLoadDeviceLists();
        });

    }

    private void onLoadDeviceLists() {

        authRepository.getAuth()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<>() {
                    @Override
                    public void onSubscribe(@NotNull Disposable d) {

                        //

                    }

                    @Override
                    public void onNext(@NonNull UserAuthInfo userAuthInfo) {

                        if (settingsManager.getSettings().getDeviceManagement() == 1){

                            NetworkUtils.getMacAdress(UserDevicesManagement.this);
                            if (NetworkUtils.getMacAdress(UserDevicesManagement.this).equals("null")){
                                return;
                            }

                            DeviceName.with(UserDevicesManagement.this).request((info, error) -> {

                                String name = info.getName();
                                String model = info.model;

                                authRepository.addDevice(NetworkUtils.getMacAdress(UserDevicesManagement.this),model,name)
                                        .subscribeOn(Schedulers.newThread())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(new Observer<>() {
                                            @Override
                                            public void onSubscribe(@NotNull Disposable d) {

                                                //

                                            }

                                            @Override
                                            public void onNext(@io.reactivex.rxjava3.annotations.NonNull UserAuthInfo userAuthInfo) {


                                                //

                                            }


                                            @Override
                                            public void onError(@NotNull Throwable e) {


                                                //

                                            }

                                            @Override
                                            public void onComplete() {

                                                //

                                            }
                                        });

                            });

                        }


                        menuHandler.isDevicesLimitRevoked.set(userAuthInfo.getDeviceList().size() <= DEVICE_LIMIT);

                        devicesManagementAdapter.addMain(userAuthInfo.getDeviceList(), UserDevicesManagement.this, authManager, authRepository);

                    }


                    @Override
                    public void onError(@NotNull Throwable e) {


                        //

                    }

                    @Override
                    public void onComplete() {

                        //

                    }
                });
    }


    // Load App Logo
    private void onLoadAppLogo() {

        Tools.loadMiniLogo(this, binding.toolbar.logoImageTop);

    }


    @Override
    public void onBackPressed() {

        if (isDeviceLimitReached == null) {
            super.onBackPressed();

        } else  if (Boolean.TRUE.equals(menuHandler.isDevicesLimitRevoked.get())){

            startActivity(new Intent(UserDevicesManagement.this, BaseActivity.class));
            finish();

        }else if (isDeviceLimitReached.equals("isDeviceLimitReached")){

            startActivity(new Intent(UserDevicesManagement.this, SplashActivity.class));
            finish();


        }else {
            Toast.makeText(this, getString(R.string.delete_more_devices_to_coutinue), Toast.LENGTH_SHORT).show();

        }

    }
}
