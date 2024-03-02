package com.siflusso.ui.more;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

import static com.siflusso.util.Constants.SUBSCRIPTIONS;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.ListFragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.facebook.login.LoginManager;
import com.siflusso.R;
import com.siflusso.data.model.auth.UserAuthInfo;
import com.siflusso.data.remote.ErrorHandling;
import com.siflusso.data.repository.AuthRepository;
import com.siflusso.databinding.FragmentMoreBinding;
import com.siflusso.di.Injectable;
import com.siflusso.ui.base.BaseActivity;
import com.siflusso.ui.downloadmanager.core.utils.Utils;
import com.siflusso.ui.login.LoginActivity;
import com.siflusso.ui.manager.AuthManager;
import com.siflusso.ui.manager.SettingsManager;
import com.siflusso.ui.manager.TokenManager;
import com.siflusso.ui.plans.PlansAdapter;
import com.siflusso.ui.profile.EditProfileActivity;
import com.siflusso.ui.settings.SettingsActivity;
import com.siflusso.ui.splash.SplashActivity;
import com.siflusso.ui.viewmodels.HomeViewModel;
import com.siflusso.ui.viewmodels.LoginViewModel;
import com.siflusso.ui.viewmodels.SettingsViewModel;
import com.siflusso.util.DialogHelper;
import com.siflusso.util.GlideApp;
import com.siflusso.util.NetworkUtils;
import com.siflusso.util.SpacingItemDecoration;
import com.siflusso.util.Tools;

import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import me.drakeet.support.toast.ToastCompat;
import timber.log.Timber;


public class MoreFragment extends Fragment implements Injectable{

    FragmentMoreBinding binding;
    @Inject
    TokenManager tokenManager;
    private LoginViewModel loginViewModel;
    @Inject
    ViewModelProvider.Factory viewModelFactory;
    @Inject
    AuthRepository authRepository;
    private HomeViewModel homeViewModel;

    private SettingsViewModel settingsViewModel;

    @Inject
    SettingsManager settingsManager;
    @Inject
    AuthManager authManager;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMoreBinding.inflate(getLayoutInflater());
        AndroidInjection.inject(requireActivity());
        settingsViewModel = new ViewModelProvider(this, viewModelFactory).get(SettingsViewModel.class);
        loginViewModel = new ViewModelProvider(this, viewModelFactory).get(LoginViewModel.class);
        homeViewModel = new ViewModelProvider(this, viewModelFactory).get(HomeViewModel.class);

        settingsViewModel.getSettingsDetails();

        settingsViewModel.getPlans();
        loginViewModel.getAuthDetails();
        onCheckAuthenticatedUser();
        binding.btnLogin.setOnClickListener(v -> startActivity(new Intent(requireActivity(), LoginActivity.class)));
        binding.userProfileEdit.setOnClickListener(v -> startActivity(new Intent(requireActivity(), EditProfileActivity.class)));
        loginViewModel.authDetailMutableLiveData.observe(getViewLifecycleOwner(), auth -> {

            if (auth !=null) {

                authManager.saveSettings(auth);
                binding.layoutLogOff.setVisibility(View.GONE);


                if (auth.getPremuim() == 0) {

//                    binding.btnSubscribe.setVisibility(VISIBLE);
                    binding.btnSubscribe.setVisibility(GONE);

                } else {

                    binding.btnSubscribe.setVisibility(GONE);

                }

            } else {

                binding.btnSubscribe.setVisibility(GONE);
                binding.layoutLogOff.setVisibility(VISIBLE);


            }

        });
        binding.btnMyList.setOnClickListener(v -> ((BaseActivity) requireActivity()).changeFragment(new ListFragment(),ListFragment.class.getSimpleName()));

        onClick();
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        loginViewModel.getAuthDetails();
        super.onResume();
    }

    private void onClick() {
        binding.cvSuggest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSuggestionDialog();
            }
        });
        binding.privacyPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPrivacyDialog();

            }
        });
        binding.settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                requireActivity().startActivity(new Intent(requireActivity(), SettingsActivity.class));
            }
        });
        binding.aboutus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showAboutUsDialog();
            }
        });

    }

    private void showAboutUsDialog() {
        final Dialog aboutusDialog = new Dialog(requireActivity());
        aboutusDialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        aboutusDialog.setContentView(R.layout.dialog_about);
        aboutusDialog.setCancelable(true);
        aboutusDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        ImageView imageView = aboutusDialog.findViewById(R.id.logo_aboutus);
        TextView textView = aboutusDialog.findViewById(R.id.app_version);

        if (settingsManager.getSettings().getLatestVersion() !=null && !settingsManager.getSettings().getLatestVersion().isEmpty()){
            textView.setText(getString(R.string.app_versions) + settingsManager.getSettings().getLatestVersion());
        }else {

            String versionName = Utils.getAppVersionName(requireActivity());
            textView.setText(getString(R.string.app_versions) + versionName);
        }

        Tools.loadMainLogo(requireActivity(), imageView);
        WindowManager.LayoutParams layoutParams2 = new WindowManager.LayoutParams();
        layoutParams2.copyFrom(aboutusDialog.getWindow().getAttributes());
        layoutParams2.width = WRAP_CONTENT;
        layoutParams2.height = WRAP_CONTENT;

        aboutusDialog.findViewById(R.id.bt_getcode).setOnClickListener(v15 -> {
            if (settingsManager.getSettings().getAppUrlAndroid().isEmpty()) {

                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.yobex))));

            } else {

                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(settingsManager.getSettings().getAppUrlAndroid())));

            }

        });

        aboutusDialog.findViewById(R.id.bt_close).setOnClickListener(v14 -> aboutusDialog.dismiss());

        aboutusDialog.findViewById(R.id.app_url).setOnClickListener(v13 -> {


            if (settingsManager.getSettings().getAppUrlAndroid() != null && !settingsManager.getSettings().getAppUrlAndroid().trim().isEmpty()) {

                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(settingsManager.getSettings().getAppUrlAndroid())));


            } else {

                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.yobex))));

            }

        });

        aboutusDialog.show();
        aboutusDialog.getWindow().setAttributes(layoutParams2);
    }

    private void showPrivacyDialog() {
        final Dialog navdialog = new Dialog(requireActivity());
        navdialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        navdialog.setContentView(R.layout.dialog_gdpr_basic);
        navdialog.setCancelable(true);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(navdialog.getWindow().getAttributes());
        layoutParams.width = MATCH_PARENT;
        layoutParams.height = WRAP_CONTENT;

        TextView reportMovieName = navdialog.findViewById(R.id.tv_content);
        reportMovieName.setText(settingsManager.getSettings().getPrivacyPolicy());

        navdialog.findViewById(R.id.bt_accept).setOnClickListener(v1 -> navdialog.dismiss());

        navdialog.findViewById(R.id.bt_decline).setOnClickListener(v12 -> navdialog.dismiss());


        navdialog.show();
        navdialog.getWindow().setAttributes(layoutParams);
    }

    private void showSuggestionDialog() {
        if (settingsManager.getSettings().getSuggestAuth() == 1) {

            if (tokenManager.getToken().getAccessToken() != null) {


                final Dialog suggestion = new Dialog(requireActivity());
                suggestion.requestWindowFeature(Window.FEATURE_NO_TITLE);
                suggestion.setContentView(R.layout.dialog_suggest);
                suggestion.setCancelable(false);
                suggestion.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                WindowManager.LayoutParams lps = new WindowManager.LayoutParams();
                lps.copyFrom(suggestion.getWindow().getAttributes());

                lps.gravity = Gravity.BOTTOM;
                lps.width = MATCH_PARENT;
                lps.height = MATCH_PARENT;

                suggestion.show();
                suggestion.getWindow().setAttributes(lps);

                EditText editTextMessage = suggestion.findViewById(R.id.et_post);

                suggestion.findViewById(R.id.view_report).setOnClickListener(v -> {

                    editTextMessage.getText();

                    if (editTextMessage.getText() != null) {

                        String name = authManager.getUserInfo().getName();
                        String email = authManager.getUserInfo().getEmail();

                        if (!TextUtils.isEmpty(name)) {

                            homeViewModel.sendSuggestion(name, editTextMessage.getText().toString());

                        } else if (!TextUtils.isEmpty(email)){

                            homeViewModel.sendSuggestion(email, editTextMessage.getText().toString());

                        }else {

                            homeViewModel.sendSuggestion("User", editTextMessage.getText().toString());
                        }


                        homeViewModel.suggestMutableLiveData.observe(requireActivity(), report -> {

                            if (report != null) {

                                suggestion.dismiss();

                                Tools.ToastHelper(requireActivity(),requireActivity().getString(R.string.suggest_success));


                            }


                        });

                    }


                });

                suggestion.findViewById(R.id.bt_close).setOnClickListener(x ->
                        suggestion.dismiss());
                suggestion.show();
                suggestion.getWindow().setAttributes(lps);

            } else {

                DialogHelper.showSuggestWarning(requireActivity());

            }

        }
        else {

            final Dialog suggestion = new Dialog(requireActivity());
            suggestion.requestWindowFeature(Window.FEATURE_NO_TITLE);
            suggestion.setContentView(R.layout.dialog_suggest);
            suggestion.setCancelable(false);
            suggestion.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            WindowManager.LayoutParams lps = new WindowManager.LayoutParams();
            lps.copyFrom(suggestion.getWindow().getAttributes());

            lps.gravity = Gravity.BOTTOM;
            lps.width = MATCH_PARENT;
            lps.height = MATCH_PARENT;

            suggestion.show();
            suggestion.getWindow().setAttributes(lps);

            EditText editTextMessage = suggestion.findViewById(R.id.et_post);

            suggestion.findViewById(R.id.view_report).setOnClickListener(v -> {


                editTextMessage.getText();


                if (editTextMessage.getText() != null) {

                    String suggestTitlte = authManager.getUserInfo().getEmail();

                    if (!TextUtils.isEmpty(suggestTitlte)) {

                        homeViewModel.sendSuggestion(suggestTitlte, editTextMessage.getText().toString());
                    } else {

                        homeViewModel.sendSuggestion("User", editTextMessage.getText().toString());
                    }


                    homeViewModel.suggestMutableLiveData.observe(requireActivity(), report -> {


                        if (report != null) {


                            suggestion.dismiss();

                            Tools.ToastHelper(requireActivity(),requireActivity().getString(R.string.suggest_success));


                        }


                    });

                }


            });

            suggestion.findViewById(R.id.bt_close).setOnClickListener(x ->

                    suggestion.dismiss());


            suggestion.show();
            suggestion.getWindow().setAttributes(lps);
        }
    }

    private void onCheckAuthenticatedUser() {


        if (tokenManager.getToken().getAccessToken() == null) {


            binding.btnSubscribe.setVisibility(GONE);
            binding.layoutLogOff.setVisibility(VISIBLE);
           binding.userProfileName.setVisibility(View.GONE);
           binding.userProfileEmail.setVisibility(View.GONE);
           binding.userProfileEmail.setVisibility(View.GONE);
           binding.userProfileEdit.setVisibility(View.GONE);
           binding.userAvatar.setVisibility(GONE);
           binding.userProfileName.setText("");
           binding.userProfileName.setText("");
           binding.verifiedEmail.setVisibility(GONE);
           binding.logout.setVisibility(GONE);
           binding.layoutLogin.setVisibility(GONE);

            binding.logout.setOnClickListener(v -> {
//                mGoogleSignInClient.signOut()
//                        .addOnCompleteListener(requireActivity(), task -> {
//                        });


                authRepository.getUserLogout()
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<>() {
                            @Override
                            public void onSubscribe(@NotNull Disposable d) {

                                //

                            }

                            @SuppressLint("NotifyDataSetChanged")
                            @Override
                            public void onNext(@NotNull UserAuthInfo userAuthInfo) {



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

                LoginManager.getInstance().logOut();
                tokenManager.deleteToken();
                authManager.deleteAuth();
                authManager.deleteSettingsProfile();
                settingsManager.deleteSettings();
                startActivity(new Intent(requireActivity(), SplashActivity.class));
                requireActivity().finish();
            });

        }
        else {
            
        }


        binding.btnSubscribe.setOnClickListener(v -> {

            if (tokenManager.getToken().getAccessToken() == null) {

                Tools.ToastHelper(requireActivity(),requireActivity().getString(R.string.login_to_subscribe));


            }else {


                binding.btnSubscribe.setOnClickListener(x -> settingsViewModel.plansMutableLiveData.observe(getViewLifecycleOwner(), plans -> {

                    final Dialog dialog = new Dialog(requireActivity());
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.dialog_plans_display);
                    dialog.setCancelable(true);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                    WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                    lp.copyFrom(dialog.getWindow().getAttributes());

                    lp.gravity = Gravity.BOTTOM;
                    lp.width = MATCH_PARENT;
                    lp.height = MATCH_PARENT;

                    RecyclerView recyclerViewPlans = dialog.findViewById(R.id.recycler_view_plans);
                    PlansAdapter plansAdapter = new PlansAdapter();
                    recyclerViewPlans.setLayoutManager(new LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false));
                    recyclerViewPlans.addItemDecoration(new SpacingItemDecoration(1, Tools.dpToPx(requireActivity(), 0), true));
                    recyclerViewPlans.setAdapter(plansAdapter);
                    plansAdapter.addCasts(plans.getPlans(), settingsManager);

                    dialog.findViewById(R.id.bt_close).setOnClickListener(y ->
                            dialog.dismiss());

                    dialog.show();
                    dialog.getWindow().setAttributes(lp);


                }));

            }
        });



        authRepository.getAuth()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<>() {
                    @Override
                    public void onSubscribe(@NotNull Disposable d) {

                        //

                    }

                    @Override
                    public void onNext(@io.reactivex.rxjava3.annotations.NonNull UserAuthInfo auth) {


                        if (auth.getPremuim() == 1) {

                            onCancelSubscription(auth);
//                            binding.userSubscribedBtn.setVisibility(VISIBLE);
                            binding.userSubscribedBtn.setVisibility(GONE);


                        }



                        if (!auth.getProfiles().isEmpty()){



                            Tools.loadUserAvatar(requireActivity(), binding.userAvatar, authManager.getSettingsProfile().getAvatar());


                        }else {


                            Tools.loadUserAvatar(requireActivity(), binding.userAvatar, auth.getAvatar());

                        }


                        authManager.saveSettings(auth);
                        binding.btnLogin.setVisibility(View.GONE);
                        binding.userProfileName.setVisibility(VISIBLE);
                        binding.userProfileEmail.setVisibility(VISIBLE);
                        binding.userProfileEmail.setVisibility(VISIBLE);
                        binding.userProfileEdit.setVisibility(VISIBLE);
                        binding.logout.setVisibility(VISIBLE);


                        if (!auth.getProfiles().isEmpty()){
                            binding.userProfileName.setText(authManager.getSettingsProfile().getName());
                        }else {
                            binding.userProfileName.setText(auth.getName());
                        }

                        binding.userProfileEmail.setText(auth.getEmail());




                        if (auth.getEmailVerifiedAt() == null) {

                            binding.verifiedEmail.setText(R.string.non_verified);
                            binding.verifiedEmail.setTextColor(ContextCompat.getColor(requireActivity(), R.color.red_A700));

                        } else {

                            binding.verifiedEmail.setText(R.string.verified);
                            binding.verifiedEmail.setTextColor(ContextCompat.getColor(requireActivity(), R.color.light_green_400));
                        }


                        if (settingsManager.getSettings().getMantenanceMode() != 1 && settingsManager.getSettings().getEmailVerify() == 1 && auth.getEmailVerifiedAt() == null) {


                            final Dialog dialog = new Dialog(requireActivity());
                            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            dialog.setContentView(R.layout.dialog_email_verify_notice);
                            dialog.setCancelable(false);
                            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

                            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                            lp.copyFrom(dialog.getWindow().getAttributes());

                            lp.gravity = Gravity.BOTTOM;
                            lp.width = MATCH_PARENT;
                            lp.height = MATCH_PARENT;


                            TextView mailTitle = dialog.findViewById(R.id.mailTitle);
                            LinearLayout buttonResendToken = dialog.findViewById(R.id.resendTokenButton);
                            ImageButton btclose = dialog.findViewById(R.id.bt_close);
                            Button btnRestart = dialog.findViewById(R.id.btnRestart);

                            buttonResendToken.setOnClickListener(v -> {

                                loginViewModel.getVerifyEmail().observe(getViewLifecycleOwner(), login -> {

                                    if (login.status == ErrorHandling.Status.SUCCESS) {

                                        btnRestart.setVisibility(VISIBLE);
                                        buttonResendToken.setVisibility(View.GONE);
                                        btclose.setVisibility(VISIBLE);

                                        Tools.ToastHelper(requireActivity(),requireActivity().getString(R.string.rest_confirmation_mail) + authManager.getUserInfo().getEmail());


                                    } else {

                                        Tools.ToastHelper(requireActivity(),ErrorHandling.Status.ERROR.toString());


                                    }

                                });

                                dialog.findViewById(R.id.bt_close).setOnClickListener(x -> {
                                    mailTitle.setVisibility(VISIBLE);
                                    btnRestart.setVisibility(GONE);
                                    btclose.setVisibility(GONE);
                                });

                                dialog.show();
                                dialog.getWindow().setAttributes(lp);

                            });

                            btnRestart.setOnClickListener(v -> {
                                Intent intent = new Intent(requireActivity(), SplashActivity.class);
                                startActivity(intent);
                                requireActivity().finish();
                            });


                            dialog.show();
                            dialog.getWindow().setAttributes(lp);
                            dialog.findViewById(R.id.bt_close).setOnClickListener(x ->
                                    dialog.dismiss());
                            dialog.show();
                            dialog.getWindow().setAttributes(lp);


                        }

                        if (auth.getPremuim() == 0) {

//                            binding.btnSubscribe.setVisibility(VISIBLE);
                            binding.btnSubscribe.setVisibility(GONE);
                            binding.userSubscribedBtn.setVisibility(GONE);

                        } else {

                            binding.btnSubscribe.setVisibility(GONE);
//                            binding.userSubscribedBtn.setVisibility(VISIBLE);
                            binding.userSubscribedBtn.setVisibility(GONE);


                        }





                        binding.userSubscribedBtn.setOnClickListener(v -> {

                            final Dialog dialog = new Dialog(requireActivity());
                            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            dialog.setContentView(R.layout.dialog_confirm_cancel_subscription);
                            dialog.setCancelable(true);
                            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                            lp.copyFrom(dialog.getWindow().getAttributes());
                            lp.width = ViewGroup.LayoutParams.WRAP_CONTENT;
                            lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;


                            dialog.findViewById(R.id.bt_getcode).setOnClickListener(x -> {

                                if (auth.getType() != null && !auth.getType().isEmpty() && auth.getType().equals("paypal")) {

                                    authRepository.cancelAuthSubcriptionPaypal().subscribeOn(Schedulers.io())
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribe(new Observer<>() {
                                                @Override
                                                public void onSubscribe(@NotNull Disposable d) {

                                                    //

                                                }

                                                @Override
                                                public void onNext(@NotNull UserAuthInfo userAuthInfo) {

                                                    Tools.ToastHelper(requireActivity(),SUBSCRIPTIONS);

                                                    startActivity(new Intent(requireActivity(), SplashActivity.class));
                                                    requireActivity().finish();
                                                }

                                                @SuppressLint("ClickableViewAccessibility")
                                                @Override
                                                public void onError(@NotNull Throwable e) {

                                                    //
                                                }

                                                @Override
                                                public void onComplete() {

                                                    //

                                                }
                                            });

                                } else if (auth.getType() != null && !auth.getType().isEmpty() && auth.getType().equals("stripe")) {

                                    authRepository.cancelAuthSubcription().subscribeOn(Schedulers.io())
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribe(new Observer<>() {
                                                @Override
                                                public void onSubscribe(@NotNull Disposable d) {

                                                    //

                                                }

                                                @Override
                                                public void onNext(@NotNull UserAuthInfo userAuthInfo) {

                                                    Tools.ToastHelper(requireActivity(),SUBSCRIPTIONS);

                                                    startActivity(new Intent(requireActivity(), SplashActivity.class));
                                                    requireActivity().finish();
                                                }

                                                @SuppressLint("ClickableViewAccessibility")
                                                @Override
                                                public void onError(@NotNull Throwable e) {

                                                    //
                                                }

                                                @Override
                                                public void onComplete() {

                                                    //

                                                }
                                            });

                                } else {

                                    authRepository.cancelAuthSubcriptionPaypal().subscribeOn(Schedulers.io())
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribe(new Observer<>() {
                                                @Override
                                                public void onSubscribe(@NotNull Disposable d) {

                                                    //

                                                }

                                                @Override
                                                public void onNext(@NotNull UserAuthInfo userAuthInfo) {

                                                    Tools.ToastHelper(requireActivity(),SUBSCRIPTIONS);

                                                    startActivity(new Intent(requireActivity(), SplashActivity.class));
                                                    requireActivity().finish();
                                                }

                                                @SuppressLint("ClickableViewAccessibility")
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


                                dialog.dismiss();

                            });

                            dialog.findViewById(R.id.bt_close).setOnClickListener(x -> dialog.dismiss());
                            dialog.show();
                            dialog.getWindow().setAttributes(lp);
                        });


                        binding.logout.setOnClickListener(v -> {
//
//                            mGoogleSignInClient.signOut()
//                                    .addOnCompleteListener(requireActivity(), task -> {
//                                    });
                            authRepository.getUserLogout()
                                    .subscribeOn(Schedulers.newThread())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new Observer<>() {
                                        @Override
                                        public void onSubscribe(@NotNull Disposable d) {

                                            //

                                        }

                                        @SuppressLint("NotifyDataSetChanged")
                                        @Override
                                        public void onNext(@NotNull UserAuthInfo userAuthInfo) {



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

                            LoginManager.getInstance().logOut();
                            tokenManager.deleteToken();
                            authManager.deleteAuth();
                            settingsManager.deleteSettings();
                            startActivity(new Intent(requireActivity(), SplashActivity.class));
                            requireActivity().finish();
                        });


                    }


                    @Override
                    public void onError(@NotNull Throwable e) {

                        
                        binding.btnSubscribe.setVisibility(GONE);
                        binding.btnLogin.setVisibility(VISIBLE);
                        binding.userProfileName.setVisibility(View.GONE);
                        binding.userProfileEmail.setVisibility(View.GONE);
                        binding.userProfileEmail.setVisibility(View.GONE);
                        binding.userProfileEdit.setVisibility(View.GONE);
                        binding.userAvatar.setVisibility(GONE);
                        binding.userProfileName.setText("");
                        binding.userProfileName.setText("");
                        binding.verifiedEmail.setVisibility(GONE);
                        binding.logout.setVisibility(GONE);

                    }

                    @Override
                    public void onComplete() {

                        //

                    }
                });



        binding.logout.setOnClickListener(v -> {
//            binding.mGoogleSignInClient.signOut()
//                    .addOnCompleteListener(requireActivity(), task -> {
//                    });

            LoginManager.getInstance().logOut();
            tokenManager.deleteToken();
            authManager.deleteAuth();
            settingsManager.deleteSettings();
            startActivity(new Intent(requireActivity(), SplashActivity.class));
            requireActivity().finish();
        });
    }

    private void onCancelSubscription(UserAuthInfo auth) {

        if (auth.getType() !=null && !auth.getType().isEmpty()) {

            if ("paypal".equals(auth.getType())) {

                loginViewModel.getExpirationStatusDetails();

                loginViewModel.expiredMutableLiveData.observe(getViewLifecycleOwner(), authx -> {


                    if (authx.getSubscription().equals("expired")) {

                        loginViewModel.cancelAuthSubscriptionPaypal();
                        loginViewModel.authCancelPaypalMutableLiveData.observe(getViewLifecycleOwner(), cancelsubs -> {

                            Tools.ToastHelper(requireActivity(),SUBSCRIPTIONS);
                            startActivity(new Intent(requireActivity(), SplashActivity.class));
                            requireActivity().finish();

                        });

                    }

                });


            } else if ("stripe".equals(auth.getType())) {

                loginViewModel.getAuthDetails();
                loginViewModel.getStripeSubStatusDetails();

                loginViewModel.stripeStatusDetailMutableLiveData.observe(getViewLifecycleOwner(), authx -> {

                    if (authx.getActive() <= 0) {

                        loginViewModel.cancelAuthSubscription();
                        loginViewModel.authCancelPlanMutableLiveData.observe(getViewLifecycleOwner(), cancelsubs -> {

                            if (cancelsubs != null) {

                                Tools.ToastHelper(requireActivity(),SUBSCRIPTIONS);
                                startActivity(new Intent(requireActivity(), SplashActivity.class));
                                requireActivity().finish();
                            }

                        });

                    }

                });
            }else {

                loginViewModel.getExpirationStatusDetails();

                loginViewModel.expiredMutableLiveData.observe(getViewLifecycleOwner(), authx -> {


                    if (authx.getSubscription().equals("expired")) {

                        loginViewModel.cancelAuthSubscriptionPaypal();
                        loginViewModel.authCancelPaypalMutableLiveData.observe(getViewLifecycleOwner(), cancelsubs -> {


                            if (android.os.Build.VERSION.SDK_INT == 25) {
                                ToastCompat.makeText(requireActivity(), SUBSCRIPTIONS, Toast.LENGTH_SHORT)
                                        .setBadTokenListener(toast -> Timber.e("Failed to toast")).show();
                            } else {
                                Toast.makeText(requireActivity(), SUBSCRIPTIONS, Toast.LENGTH_SHORT).show();
                            }
                            startActivity(new Intent(requireActivity(), SplashActivity.class));
                            requireActivity().finish();

                        });

                    }

                });
            }

        }else {

            loginViewModel.getExpirationStatusDetails();

            loginViewModel.expiredMutableLiveData.observe(getViewLifecycleOwner(), authx -> {


                if (authx.getSubscription().equals("expired")) {

                    loginViewModel.cancelAuthSubscriptionPaypal();
                    loginViewModel.authCancelPaypalMutableLiveData.observe(getViewLifecycleOwner(), cancelsubs -> {


                        if (android.os.Build.VERSION.SDK_INT == 25) {
                            ToastCompat.makeText(requireActivity(), SUBSCRIPTIONS, Toast.LENGTH_SHORT)
                                    .setBadTokenListener(toast -> Timber.e("Failed to toast")).show();
                        } else {
                            Toast.makeText(requireActivity(), SUBSCRIPTIONS, Toast.LENGTH_SHORT).show();
                        }
                        startActivity(new Intent(requireActivity(), SplashActivity.class));
                        requireActivity().finish();

                    });

                }

            });
        }

    }
}