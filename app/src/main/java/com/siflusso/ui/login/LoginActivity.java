package com.siflusso.ui.login;

import static android.view.View.GONE;
import static com.basgeekball.awesomevalidation.ValidationStyle.TEXT_INPUT_LAYOUT;
import static com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions.withCrossFade;
import static com.siflusso.util.Constants.APP_PASSWORD;
import static com.siflusso.util.Constants.ARG_MOVIE;
import static com.siflusso.util.Constants.DEVICE_LIMIT;
import static com.siflusso.util.Constants.FIRST_PASSWORD_CHECK;
import static com.siflusso.util.Constants.FIRST_TIME_APP_RUN;
import static com.siflusso.util.Constants.GOOGLE_CLIENT_ID;
import static com.siflusso.util.Constants.SERVER_BASE_URL;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.siflusso.R;
import com.siflusso.data.model.auth.Login;
import com.siflusso.data.model.auth.UserAuthInfo;
import com.siflusso.data.remote.ErrorHandling;
import com.siflusso.data.repository.AuthRepository;
import com.siflusso.databinding.ActivityLoginBinding;
import com.siflusso.di.Injectable;
import com.siflusso.ui.base.BaseActivity;
import com.siflusso.ui.devices.UserDevicesManagement;
import com.siflusso.ui.manager.SettingsManager;
import com.siflusso.ui.manager.TokenManager;
import com.siflusso.ui.register.RegisterActivity;
import com.siflusso.ui.seriedetails.SerieDetailsActivity;
import com.siflusso.ui.users.PhoneAuthActivity;
import com.siflusso.ui.users.UserProfiles;
import com.siflusso.ui.viewmodels.LoginViewModel;
import com.siflusso.ui.viewmodels.SettingsViewModel;
import com.siflusso.util.DialogHelper;
import com.siflusso.util.GlideApp;
import com.siflusso.util.NetworkUtils;
import com.siflusso.util.Tools;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.jaredrummler.android.device.DeviceName;
import com.stringcare.library.SC;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Objects;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import timber.log.Timber;


/**
 * EasyPlex - Android Movie Portal App
 * @package EasyPlex - Android Movie Portal App
 * @author      @Y0bEX
 * @copyright Copyright (c) 2023 Y0bEX,
 * @license     <a href="http://codecanyon.net/wiki/support/legal-terms/licensing-terms/">...</a>
 * @<a href="profile">https://codecanyon.net/us</a>er/yobex
 * @link yobexd@gmail.com
 * @skype yobexd@gmail.com
 **/



public class LoginActivity extends AppCompatActivity implements Injectable {
    ActivityLoginBinding binding;
    com.facebook.AccessTokenTracker accessTokenTracker;
    @Inject
    SharedPreferences sharedPreferences;
    @Inject
    SharedPreferences.Editor sharedPreferencesEditor;
    @Inject
    TokenManager tokenManager;
    @Inject
    SettingsManager settingsManager;
    @Inject
    AuthRepository authRepository;
    @Inject
    ViewModelProvider.Factory viewModelFactory;
    private LoginViewModel loginViewModel;
    private SettingsViewModel settingsViewModel;
    AwesomeValidation validator;
    private static final String EMAIL = "email";
    private static final String USER_POSTS = "user_posts";
    private static final String AUTH_TYPE = "rerequest";
    private CallbackManager mCallbackManager;
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_GET_TOKEN = 9002;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        binding.constraintLayout.setVisibility(GONE);
        loginViewModel = new ViewModelProvider(this, viewModelFactory).get(LoginViewModel.class);
        settingsViewModel = new ViewModelProvider(this, viewModelFactory).get(SettingsViewModel.class);
        mCallbackManager = CallbackManager.Factory.create();

        if(sharedPreferences.getBoolean(FIRST_TIME_APP_RUN,false)){
            binding.loginLayout.setVisibility(View.VISIBLE);
            binding.backgroundLayout.setVisibility(GONE);
        }
        else {
            binding.loginLayout.setVisibility(GONE);
            binding.backgroundLayout.setVisibility(View.VISIBLE);

        }
        Tools.hideSystemPlayerUi(this, true, 0);
        Tools.setSystemBarTransparent(this);
        onLoadAppLogo();
        onLoadSplashImage();
        onLoadValitator();
        onSetupRules();
        onLoadGoogleOneTapSigning();
        if (!sharedPreferences.getBoolean(FIRST_PASSWORD_CHECK, false)) {
            sharedPreferencesEditor = sharedPreferences.edit();
            sharedPreferencesEditor.putBoolean(FIRST_PASSWORD_CHECK, Boolean.TRUE);
            sharedPreferencesEditor.apply();
        }
        binding.btnLoginLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sharedPreferencesEditor = sharedPreferences.edit();
                sharedPreferencesEditor.putBoolean(FIRST_TIME_APP_RUN,Boolean.TRUE);
                sharedPreferencesEditor.apply();

                binding.loginLayout.setVisibility(View.VISIBLE);
                binding.backgroundLayout.setVisibility(GONE);
            }
        });
        binding.btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                binding.loginLayout.setVisibility(GONE);
                binding.backgroundLayout.setVisibility(View.VISIBLE);
            }
        });



        if (settingsManager.getSettings().getForce_password_access() == 1) {


            String savedPassword = sharedPreferences.getString(APP_PASSWORD,null);

            binding.loader.setVisibility(View.GONE);
            binding.codeAccessEnable.setVisibility(View.VISIBLE);
            binding.formContainer.setVisibility(View.GONE);


            settingsViewModel.getAppPasswordCheck(savedPassword);
            settingsViewModel.appPasswordMutableLiveData.observe(this, passwordcheck -> {

                if (passwordcheck !=null && passwordcheck.getPassword().equals("match")){



                    if (tokenManager.getToken().getAccessToken() != null) {

                        binding.codeAccessEnable.setVisibility(View.GONE);

                        onRedirect();

                    }else {

                        savePassword(savedPassword);
                        binding.codeAccessEnable.setVisibility(View.GONE);
                        binding.formContainer.setVisibility(View.VISIBLE);
                    }



                }else {

                    binding.loader.setVisibility(View.GONE);
                    binding.codeAccessEnable.setVisibility(View.VISIBLE);
                    binding.formContainer.setVisibility(View.GONE);
                }
            });


        }else if (tokenManager.getToken().getAccessToken() != null) {

            onRedirect();

        }else {


            binding.codeAccessEnable.setVisibility(View.GONE);
            binding.formContainer.setVisibility(View.VISIBLE);

        }

        binding.btnEnterPasswordAccess.setOnClickListener(v -> {

            String passwordMatch = binding.tilPasswordCode.getEditText().getText().toString();


            settingsViewModel.getAppPasswordCheck(passwordMatch);
            settingsViewModel.appPasswordMutableLiveData.observe(this, passwordcheck -> {

                if (passwordcheck !=null && passwordcheck.getPassword().equals("match")){

                    savePassword(passwordMatch);
                    binding.codeAccessEnable.setVisibility(View.GONE);
                    binding.formContainer.setVisibility(View.VISIBLE);

                }else {

                    Toast.makeText(LoginActivity.this, R.string.access_code, Toast.LENGTH_SHORT).show();
                }
            });

        });

        binding.textGetCode.setOnClickListener(v -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(settingsManager.getSettings().getInstagramUrl()))));

        if (settingsManager.getSettings().getForceLogin() == 1){
            binding.btnSkip.setVisibility(View.GONE);
        }


        binding.btnFacebook.setOnClickListener(v -> binding.loginButton.performClick());

        // Set the initial permissions to request from the user while logging in
        binding.loginButton.setPermissions(Arrays.asList(EMAIL, USER_POSTS));

        binding.loginButton.setAuthType(AUTH_TYPE);

        binding.loginButton.registerCallback(mCallbackManager, new FacebookCallback<>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                onLoadAuthFromFacebook(loginResult);
            }

            @Override
            public void onCancel() {
                Timber.i("Login attempt canceled.");

            }

            @Override
            public void onError(@NonNull FacebookException e) {
                Timber.i("Login attempt failed.");
            }
        });


        binding.btnGoogle.setOnClickListener(v -> signIn());
        binding.signInButton.setOnClickListener(v -> signIn());
        binding.btnSkip.setOnClickListener(v -> skip());
        binding.btnLogin.setOnClickListener(v -> login());

        binding.goToRegister.setOnClickListener(view -> goToRegister());
        binding.forgetPassword.setOnClickListener(view -> goToForgetPassword());

    }

    private void signIn() {

        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_GET_TOKEN);
    }

    private void onLoadGoogleOneTapSigning() {

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(SC.reveal(GOOGLE_CLIENT_ID))
                .requestEmail()
                .requestServerAuthCode(SC.reveal(GOOGLE_CLIENT_ID))
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

    }


    private void onLoadAuthFromFacebook(LoginResult loginResult) {

        loginViewModel.getLoginFromFacebook(loginResult.getAccessToken().getToken()).observe(LoginActivity.this, login -> {

            hideKeyboard();
            binding.formContainer.setVisibility(View.GONE);
            binding.loader.setVisibility(View.VISIBLE);

            if (login.status == ErrorHandling.Status.SUCCESS) {
                assert login.data != null;
                tokenManager.saveToken(login.data);
                Timber.i(login.data.getAccessToken());
                onRedirect();

            } else {

                binding.formContainer.setVisibility(View.VISIBLE);
                binding.loader.setVisibility(View.GONE);
                DialogHelper.erroLogin(this);


            }

        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode , resultCode , data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_GET_TOKEN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }


    private void handleSignInResult(@NonNull Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            onLoadAuthFromGoogle(account);
        } catch (ApiException e) {
            Timber.tag("TAG").w(e, "handleSignInResult:error");


        }
    }

    private void onLoadAuthFromGoogle(GoogleSignInAccount account) {

        loginViewModel.getLoginFromGoogle(account.getServerAuthCode()).observe(LoginActivity.this, login -> {

            hideKeyboard();
            binding.formContainer.setVisibility(View.GONE);
            binding.loader.setVisibility(View.VISIBLE);

            if (login.status == ErrorHandling.Status.SUCCESS) {
                assert login.data != null;
                tokenManager.saveToken(login.data);
                Timber.i(login.data.getAccessToken());
                onRedirect();

            } else {

                binding.formContainer.setVisibility(View.VISIBLE);
                binding.loader.setVisibility(View.GONE);
                DialogHelper.erroLogin(this);


            }

        });
    }

    private void onLoadSplashImage() {

        GlideApp.with(getApplicationContext()).asBitmap().load(settingsManager.getSettings().getSplashImage())
                .fitCenter()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .transition(withCrossFade())
                .skipMemoryCache(true)
                .into(binding.splashImage);

    }


    void skip(){

        binding.skipProgressIndicator.setVisibility(View.VISIBLE);
        sharedPreferencesEditor = sharedPreferences.edit();
        sharedPreferencesEditor.putBoolean(FIRST_TIME_APP_RUN,Boolean.TRUE);
        sharedPreferencesEditor.apply();




        startActivity(new Intent(LoginActivity.this, BaseActivity.class));
        finish();
        binding.skipProgressIndicator.setVisibility(GONE);


    }

    private void onRedirect() {

        binding.textViewCheckingAuth.setVisibility(View.VISIBLE);
        binding.loader.setVisibility(View.VISIBLE);
        binding.formContainer.setVisibility(GONE);

        authRepository.getAuth()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<>() {
                    @Override
                    public void onSubscribe(@NotNull Disposable d) {

                        //

                    }

                    @Override
                    public void onNext(@io.reactivex.rxjava3.annotations.NonNull UserAuthInfo userAuthInfo) {

                        binding.loader.setVisibility(GONE);
                        binding.textViewCheckingAuth.setVisibility(GONE);

                        if (settingsManager.getSettings().getDeviceManagement() == 1){

                            NetworkUtils.getMacAdress(LoginActivity.this);
                            if (NetworkUtils.getMacAdress(LoginActivity.this).equals("null")){
                                return;
                            }

                            DeviceName.with(LoginActivity.this).request((info, error) -> {

                                String name = info.getName();
                                String model = info.model;

                                authRepository.addDevice(NetworkUtils.getMacAdress(LoginActivity.this),model,name)
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


                        if (settingsManager.getSettings().getPhoneVerification() == 1){




                            if (userAuthInfo.getVerified() != 1) {


                                startActivity(new Intent(LoginActivity.this, PhoneAuthActivity.class));
                                finish();


                            }else {


                                if (settingsManager.getSettings().getDeviceManagement() == 1 && userAuthInfo.getDeviceList().size() > DEVICE_LIMIT){

                                    Toast.makeText(LoginActivity.this, R.string.max_devices, Toast.LENGTH_SHORT).show();

                                    Intent intent = new Intent(LoginActivity.this, UserDevicesManagement.class);
                                    intent.putExtra("isDeviceLimitReached", "isDeviceLimitReached");
                                    startActivity(intent);
                                    finish();


                                }else if (settingsManager.getSettings().getProfileSelection() == 1 ){

                                    if (!userAuthInfo.getProfiles().isEmpty()) {

                                        startActivity(new Intent(LoginActivity.this, UserProfiles.class));
                                        finish();


                                    }else {
                                        binding.loader.setVisibility(GONE);
                                        binding.textViewCheckingAuth.setVisibility(GONE);
                                        startActivity(new Intent(LoginActivity.this, BaseActivity.class));
                                        finish();


                                    }

                                }else {


                                    binding.loader.setVisibility(GONE);
                                    binding.textViewCheckingAuth.setVisibility(GONE);

                                    startActivity(new Intent(LoginActivity.this, BaseActivity.class));
                                    finish();
                                }


                            }



                        }else {


                            if (settingsManager.getSettings().getDeviceManagement() == 1 && userAuthInfo.getDeviceList().size() > DEVICE_LIMIT) {


                                Toast.makeText(LoginActivity.this, R.string.max_devices, Toast.LENGTH_SHORT).show();


                                Intent intent = new Intent(LoginActivity.this, UserDevicesManagement.class);
                                intent.putExtra("isDeviceLimitReached", "isDeviceLimitReached");
                                startActivity(intent);
                                finish();

                            } else
                                if (settingsManager.getSettings().getProfileSelection() == 1 ){

                                if (!userAuthInfo.getProfiles().isEmpty()) {

                                    startActivity(new Intent(LoginActivity.this, UserProfiles.class));
                                    finish();


                                }else {


                                    binding.loader.setVisibility(GONE);
                                    binding.textViewCheckingAuth.setVisibility(GONE);
                                    startActivity(new Intent(LoginActivity.this, BaseActivity.class));
                                    finish();


                                }

                            }
                            else {


                                binding.loader.setVisibility(GONE);
                                binding.textViewCheckingAuth.setVisibility(GONE);
                                startActivity(new Intent(LoginActivity.this, BaseActivity.class));
                                finish();
                            }
                        }


                    }


                    @Override
                    public void onError(@NotNull Throwable e) {

                        binding.formContainer.setVisibility(View.VISIBLE);
                        binding.loader.setVisibility(View.GONE);
                        binding.textViewCheckingAuth.setVisibility(GONE);

                    }

                    @Override
                    public void onComplete() {

                        //

                    }
                });



    }


    void login() {

        String email = binding.tilEmail.getEditText().getText().toString();
        String password = binding.tilPassword.getEditText().getText().toString();
        binding.tilEmail.setError(null);
        binding.tilPassword.setError(null);
        if (validator.validate()) {
            hideKeyboard();
            binding.formContainer.setVisibility(View.GONE);
            binding.linearProgressIndicator.setVisibility(View.VISIBLE);

            authRepository.getLogin(email, password)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<>() {
                        @Override
                        public void onSubscribe(@NotNull Disposable d) {

                            //

                        }

                        @Override
                        public void onNext(@NotNull Login login) {


                            tokenManager.saveToken(login);
                            onRedirect();

                        }

                        @SuppressLint("ClickableViewAccessibility")
                        @Override
                        public void onError(@NotNull Throwable e) {
                            binding.formContainer.setVisibility(View.VISIBLE);
                            binding.linearProgressIndicator.setVisibility(View.GONE);
                            DialogHelper.erroLogin(LoginActivity.this);

                        }

                        @Override
                        public void onComplete() {

                            //

                        }
                    });
        }


    }



    private void onLoadValitator() {

        validator = new AwesomeValidation(TEXT_INPUT_LAYOUT);
        validator.setTextInputLayoutErrorTextAppearance(R.style.TextInputLayoutErrorStyle);
    }


    // Display Main Logo
    private void onLoadAppLogo() {

        Glide.with(getApplicationContext()).asBitmap().load(SERVER_BASE_URL +"image/minilogo")
                .fitCenter()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .transition(withCrossFade())
                .skipMemoryCache(true)
                .into(binding.logoImageTop);

    }


    // Register Button

    void goToRegister() {
        startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        Animatoo.animateFade(this);
    }


    @SuppressLint("NonConstantResourceId")

    void goToForgetPassword() {
        startActivity(new Intent(LoginActivity.this, PasswordForget.class));
        Animatoo.animateFade(this);
    }



    // Hide Keyboard on Submit
    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


    // Input Email & Password Validation
    public void onSetupRules() {
        validator.addValidation(this, R.id.til_email, Patterns.EMAIL_ADDRESS, R.string.err_email);
        validator.addValidation(this, R.id.til_password, RegexTemplate.NOT_EMPTY, R.string.err_password);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (accessTokenTracker !=null){
            accessTokenTracker.stopTracking();
            accessTokenTracker = null;
        }

        binding.loginButton.unregisterCallback(mCallbackManager);

        binding = null;


    }

    private void savePassword(String password){

        sharedPreferencesEditor = sharedPreferences.edit();
        sharedPreferencesEditor.putString(APP_PASSWORD,password);
        sharedPreferencesEditor.apply();
    }



}
