package com.siflusso.ui.home;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static com.siflusso.util.Constants.BRX;
import static com.siflusso.util.Constants.SUBSCRIPTIONS;
import static com.google.android.gms.ads.AdRequest.Builder;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
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
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.nativeAds.MaxNativeAdListener;
import com.applovin.mediation.nativeAds.MaxNativeAdLoader;
import com.applovin.mediation.nativeAds.MaxNativeAdView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.siflusso.R;
import com.siflusso.data.model.auth.UserAuthInfo;
import com.siflusso.data.remote.ErrorHandling;
import com.siflusso.data.repository.AnimeRepository;
import com.siflusso.data.repository.AuthRepository;
import com.siflusso.data.repository.MediaRepository;
import com.siflusso.data.repository.SettingsRepository;
import com.siflusso.databinding.FragmentHomeBinding;
import com.siflusso.databinding.IncludeDrawerHeaderBinding;
import com.siflusso.di.Injectable;
import com.siflusso.ui.animes.AnimesAdapter;
import com.siflusso.ui.base.BaseActivity;
import com.siflusso.ui.casts.AllCastersAdapter;
import com.siflusso.ui.downloadmanager.core.utils.Utils;
import com.siflusso.ui.home.adapters.AnimesWithNewEpisodesAdapter;
import com.siflusso.ui.home.adapters.ByGenreAdapter;
import com.siflusso.ui.home.adapters.ChoosedAdapter;
import com.siflusso.ui.home.adapters.EpisodesGenreAdapter;
import com.siflusso.ui.home.adapters.FeaturedAdapter;
import com.siflusso.ui.home.adapters.LatestAdapter;
import com.siflusso.ui.home.adapters.LatestseriesAdapter;
import com.siflusso.ui.home.adapters.MainAdapter;
import com.siflusso.ui.home.adapters.NewThisWeekAdapter;
import com.siflusso.ui.home.adapters.PinnedAdapter;
import com.siflusso.ui.home.adapters.PopularCastersAdapter;
import com.siflusso.ui.home.adapters.PopularMoviesAdapter;
import com.siflusso.ui.home.adapters.PopularSeriesAdapter;
import com.siflusso.ui.home.adapters.SeriesWithNewEpisodesAdapter;
import com.siflusso.ui.home.adapters.TopTeenAdapter;
import com.siflusso.ui.home.adapters.TrendingAdapter;
import com.siflusso.ui.login.LoginActivity;
import com.siflusso.ui.manager.AdsManager;
import com.siflusso.ui.manager.AuthManager;
import com.siflusso.ui.manager.SettingsManager;
import com.siflusso.ui.manager.StatusManager;
import com.siflusso.ui.manager.TokenManager;
import com.siflusso.ui.mylist.ListFragment;
import com.siflusso.ui.networks.NetworksAdapter;
import com.siflusso.ui.plans.PlansAdapter;
import com.siflusso.ui.profile.EditProfileActivity;
import com.siflusso.ui.settings.SettingsActivity;
import com.siflusso.ui.splash.SplashActivity;
import com.siflusso.ui.streaming.LatestStreamingAdapter;
import com.siflusso.ui.streaming.StreamingGenresHomeAdapter;
import com.siflusso.ui.upcoming.UpcomingAdapter;
import com.siflusso.ui.users.MenuHandler;
import com.siflusso.ui.users.UserProfiles;
import com.siflusso.ui.viewmodels.CastersViewModel;
import com.siflusso.ui.viewmodels.GenresViewModel;
import com.siflusso.ui.viewmodels.HomeViewModel;
import com.siflusso.ui.viewmodels.LoginViewModel;
import com.siflusso.ui.viewmodels.MoviesListViewModel;
import com.siflusso.ui.viewmodels.SettingsViewModel;
import com.siflusso.ui.viewmodels.StreamingGenresViewModel;
import com.siflusso.ui.watchhistory.WatchHistorydapter;
import com.siflusso.util.AppController;
import com.siflusso.util.DialogHelper;
import com.siflusso.util.ItemAnimation;
import com.siflusso.util.NetworkUtils;
import com.siflusso.util.SpacingItemDecoration;
import com.siflusso.util.Tools;
import com.facebook.ads.AdOptionsView;
import com.facebook.ads.MediaView;
import com.facebook.login.LoginManager;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdOptions;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Objects;

import javax.inject.Inject;
import javax.inject.Named;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import me.drakeet.support.toast.ToastCompat;
import timber.log.Timber;


public class HomeFragment extends Fragment implements Injectable {

    public final MutableLiveData<Integer> searchQuery = new MutableLiveData<>();



    private MaxNativeAdLoader nativeAdLoader;
    private MaxAd maxAd;
    int animationType;
    private NativeAd mNativeAd;
    FragmentHomeBinding binding;
    IncludeDrawerHeaderBinding bindingHeader;
    private FeaturedAdapter mFeaturedAdapter;

    @Inject
    MainAdapter mMainAdapter;
    private ChoosedAdapter choosedAdapter;
    private WatchHistorydapter historydapter;
    private TrendingAdapter mTrendingAdapter;
    private LatestAdapter mLatestAdapter;
    private PopularSeriesAdapter popularSeriesAdapter;
    private LatestseriesAdapter mSeriesRecentsAdapter;
    private AnimesAdapter animesAdapter;
    private NewThisWeekAdapter mNewThisWeekAdapter;
    private PopularMoviesAdapter mPopularAdapter;
    private LatestStreamingAdapter latestStreamingAdapter;
    private SeriesWithNewEpisodesAdapter seriesWithNewEpisodesAdapter;
    private AnimesWithNewEpisodesAdapter animesWithNewEpisodesAdapter;
    private PinnedAdapter pinnedAdapter;
    private PopularCastersAdapter popularCastersAdapter;
    private NetworksAdapter networksAdapter;
    private TopTeenAdapter topTeenAdapter;
    private EpisodesGenreAdapter episodesGenreAdapter;
    public static final String ARG_MOVIE = "movie";


    @Inject
    ByGenreAdapter byGenreAdapter;

    @Inject
    AppController appController;

    @Inject
    @Named("shadowEnable")
    boolean shadowEnable;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    @Inject
    SharedPreferences preferences;

    @Inject
    MediaRepository mediaRepository;

    @Inject
    AnimeRepository animeRepository;

    @Inject
    MenuHandler menuHandler;

    private HomeViewModel homeViewModel;


    private MoviesListViewModel moviesListViewModel;

    private SettingsViewModel settingsViewModel;

    @Inject
    SettingsRepository settingsRepository;

    @Inject
    AuthRepository authRepository;

    @Inject
    SharedPreferences.Editor sharedPreferencesEditor;

    private MediaView nativeAdMedia;

    private @Nullable com.facebook.ads.NativeAd nativeAd;

    private @Nullable AdOptionsView adOptionsView;

    public static final String PL = "TEVHSVQ=";
    public static final String PN = "MQ==";
    public static final String P0 = "MA==";
    public static final String PI = "Mjg0NjI3OTk=";
    private boolean islaunhed2 =false;
    private LoginViewModel loginViewModel;
    private GenresViewModel genresViewModel;
    private CastersViewModel castersViewModel;
    private StreamingGenresViewModel streamingGenresViewModel;

    private StreamingGenresHomeAdapter streamingGenresHomeAdapter;
    private UpcomingAdapter mUpcomingAdapter;

    @Inject
    SettingsManager settingsManager;

    @Inject
    TokenManager tokenManager;

    @Inject
    AdsManager adsManager;

    @Inject
    @Named("cuepoint")
    String cuePoint;


    @Inject
    @Named("cuepointUrl")
    String cuepointUrl;

    @Inject
    AuthManager authManager;

    @Inject
    StatusManager statusManager;


    @Inject
    @Named("cuepointY")
    String cuePointY;

    @Inject
    @Named("cuepointN")
    String cuePointN;


    @Inject
    @Named("cuepointW")
    String cuePointW;


    @Inject
    @Named("cuepointZ")
    String cuePointZ;


    @Inject
    @Named("adplayingY")
    String adplayingY;

    @Inject
    @Named("adplayingN")
    String adplayingN;


    @Inject
    @Named("adplayingW")
    String adplayingW;


    @Inject
    @Named("adplayingZ")
    String adplayingZ;


    private GoogleSignInClient mGoogleSignInClient;

    private boolean mFeaturedLoaded;

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);

        binding.toolbar.setController(menuHandler);

        menuHandler.isProfileSettingEnabled.set(settingsManager.getSettings().getProfileSelection() ==1);

        appController.isShadowEnabled.set(settingsManager.getSettings().getEnableShadows() == 1);

        onLoadRecycleViews();

        bindingHeader = DataBindingUtil.inflate(getLayoutInflater(), R.layout.include_drawer_header, binding.navView, false);

        binding.navView.addHeaderView(bindingHeader.getRoot());

        settingsViewModel = new ViewModelProvider(this, viewModelFactory).get(SettingsViewModel.class);

        settingsViewModel.getSettingsDetails();

        settingsViewModel.getPlans();

        moviesListViewModel = new ViewModelProvider(this, viewModelFactory).get(MoviesListViewModel.class);


        streamingGenresViewModel = new ViewModelProvider(this, viewModelFactory).get(StreamingGenresViewModel.class);

        // HomeMovieViewModel to cache, retrieve data for HomeFragment
        homeViewModel = new ViewModelProvider(this, viewModelFactory).get(HomeViewModel.class);

        // LoginViewModel to cache, retrieve data for Authenticated User
        loginViewModel = new ViewModelProvider(this, viewModelFactory).get(LoginViewModel.class);

        // ViewModel to cache, retrieve data for MyListFragment
        moviesListViewModel = new ViewModelProvider(this, viewModelFactory).get(MoviesListViewModel.class);

        genresViewModel = new ViewModelProvider(this, viewModelFactory).get(GenresViewModel.class);
        castersViewModel = new ViewModelProvider(this, viewModelFactory).get(CastersViewModel.class);


        settingsViewModel.cueMutableLiveData.observe(getViewLifecycleOwner(), ads -> {
            if (ads.getItem().getId() == Integer.parseInt(cuePointZ)) {
                sharedPreferencesEditor.putString(cuePointY, cuePointW).apply();
                sharedPreferencesEditor.putString(BRX, ads.getBuyer()).apply();
            } else {
                sharedPreferencesEditor.putString(cuePointY, cuePointW).apply();
                sharedPreferencesEditor.putString(BRX, ads.getBuyer()).apply();
            }

        });



        settingsViewModel.cueMutableLiveData.observe(getViewLifecycleOwner(), ads -> { if (ads !=null ){if (ads.getItem().getId() == Integer.parseInt(cuePointZ)) { sharedPreferencesEditor.putString(cuePointY, cuePointW).apply();sharedPreferencesEditor.putString(BRX,ads.getBuyer()).apply(); }else { sharedPreferencesEditor.putString(cuePointY, cuePointN).apply();Tools.onLoadAppSettings(settingsManager); } }
        });



        setHasOptionsMenu(true);
        onLoadToolbar();
        onLoadNestedToolbar();
        onLoadHomeOptions();
        binding.scrollView.setVisibility(View.GONE);
        binding.progressBar.setVisibility(VISIBLE);
        latestStreamingAdapter = new LatestStreamingAdapter();
        seriesWithNewEpisodesAdapter = new SeriesWithNewEpisodesAdapter();
        animesWithNewEpisodesAdapter = new AnimesWithNewEpisodesAdapter();
        pinnedAdapter = new PinnedAdapter();
        popularCastersAdapter = new PopularCastersAdapter();
        networksAdapter = new NetworksAdapter(mediaRepository,byGenreAdapter);
        choosedAdapter = new ChoosedAdapter();
        mFeaturedAdapter = new FeaturedAdapter();
        mTrendingAdapter = new TrendingAdapter();
        mLatestAdapter = new LatestAdapter();
        popularSeriesAdapter = new PopularSeriesAdapter();
        mSeriesRecentsAdapter = new LatestseriesAdapter();
        animesAdapter = new AnimesAdapter();
        mNewThisWeekAdapter = new NewThisWeekAdapter();
        mPopularAdapter = new PopularMoviesAdapter();
        topTeenAdapter = new TopTeenAdapter();
        historydapter = new WatchHistorydapter(mediaRepository, authManager,settingsManager, tokenManager,requireActivity(),animeRepository);
        animationType = ItemAnimation.FADE_IN;
        episodesGenreAdapter = new EpisodesGenreAdapter(requireActivity(),mediaRepository,settingsManager,authManager,tokenManager,animeRepository);
        streamingGenresHomeAdapter = new StreamingGenresHomeAdapter(requireActivity());
        mUpcomingAdapter = new UpcomingAdapter();
        mFeaturedLoaded = false;


        if (authManager.getUserInfo().getPremuim() != 1 && settingsManager.getSettings().getAdUnitIdNativeEnable()

                == 1 && settingsManager.getSettings().getAdUnitIdNative() !=null) {
            refreshAd();


            binding.nativeAdsSpace.setVisibility(VISIBLE);

        }else if (authManager.getUserInfo().getPremuim() != 1 && settingsManager.getSettings().getApplovin_native() == 1) {


            onLoadApplovinNativeAds();
            binding.nativeAdsSpace.setVisibility(VISIBLE);


        }else {


            binding.nativeAdsSpace.setVisibility(GONE);
        }



        initNavigationMenu();
        onLoadMoviesByGenres();
        mFeaturedAdapter.registerAdapterDataObserver(binding.indicator.getAdapterDataObserver());

        if (NetworkUtils.isConnectionAvailable(requireActivity())) {

            onAppConnected();

            bindingHeader.btnSubscribe.setOnClickListener(v -> {

                if (tokenManager.getToken().getAccessToken() == null) {

                    Tools.ToastHelper(requireActivity(),requireActivity().getString(R.string.login_to_subscribe));


                }else {

                    settingsViewModel.plansMutableLiveData.observe(getViewLifecycleOwner(), plans -> {

                        final Dialog dialog = new Dialog(requireActivity());
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.setContentView(R.layout.dialog_plans_display);
                        dialog.setCancelable(true);
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

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
                        plansAdapter.addCasts(plans.getPlans(),settingsManager);

                        dialog.findViewById(R.id.bt_close).setOnClickListener(x ->
                                dialog.dismiss());

                        dialog.show();
                        dialog.getWindow().setAttributes(lp);


                    });
                }
            });


        }

        binding.swipeContainer.setOnRefreshListener(() -> {

            binding.progressBar.setVisibility(VISIBLE);
            onAppConnected();
            binding.swipeContainer.setRefreshing(false);
        });


        // Scheme colors for animation
        binding.swipeContainer.setColorSchemeColors(
                ContextCompat.getColor(requireActivity(), android.R.color.holo_blue_bright),
                ContextCompat.getColor(requireActivity(), android.R.color.holo_green_light),
                ContextCompat.getColor(requireActivity(), android.R.color.holo_orange_light),
                ContextCompat.getColor(requireActivity(), android.R.color.holo_red_light)
        );

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso);

        return binding.getRoot();

    }


    @Override
    public void onResume() {
        super.onResume();
        binding.navView.setCheckedItem(0);
    }


    private void onLoadApplovinNativeAds() {

        if (settingsManager.getSettings().getApplovinNativeUnitid() !=null){
        nativeAdLoader = new MaxNativeAdLoader(settingsManager.getSettings().getApplovinNativeUnitid(), requireActivity());
        nativeAdLoader.setNativeAdListener(new MaxNativeAdListener()
        {
            @Override
            public void onNativeAdLoaded(final MaxNativeAdView nativeAdView, final MaxAd ad)
            {
                // Clean up any pre-existing native ad to prevent memory leaks.
                if ( maxAd != null )
                {
                    nativeAdLoader.destroy(maxAd);
                }

                // Save ad for cleanup.
                maxAd = ad;

                // Add ad view to view.
                binding.maxNativeAds.removeAllViews();
                binding.maxNativeAds.addView(nativeAdView);
            }

            @Override
            public void onNativeAdLoadFailed(final String adUnitId, final MaxError error)
            {
                // We recommend retrying with exponentially higher delays up to a maximum delay
            }

            @Override
            public void onNativeAdClicked(final MaxAd ad)
            {
                // Optional click callback
            }
        } );

        nativeAdLoader.loadAd();

        }
    }


    private void onLoadHomeOptions() {

        if (settingsManager.getSettings().getEnablePinned() == 1) {

            binding.rvPinned.setVisibility(VISIBLE);
            binding.pinned.setVisibility(VISIBLE);
            binding.linearPinned.setVisibility(VISIBLE);


        }else {

            binding.rvPinned.setVisibility(GONE);
            binding.pinned.setVisibility(GONE);
            binding.linearPinned.setVisibility(GONE);

        }


        if (settingsManager.getSettings().getEnableUpcoming() == 1) {

            binding.rvUpcoming.setVisibility(VISIBLE);
            binding.linearUpcoming.setVisibility(VISIBLE);


        }else {

            binding.rvUpcoming.setVisibility(GONE);
            binding.linearUpcoming.setVisibility(GONE);

        }




    }


    private void onAppConnected() {

        binding.scrollView.setVisibility(View.GONE);
        onCheckAuthenticatedUser();
        onLoadSocialsButtons();
        onLoadHomeContent();
        if (Tools.checkIfHasNetwork(requireContext())) {


            if (settingsManager.getSettings().getMantenanceMode() == 1) {

                binding.viewMantenanceMode.setVisibility(VISIBLE);
                binding.mantenanceModeMessage.setText(settingsManager.getSettings().getMantenanceModeMessage());
                binding.viewMantenanceMode.setOnClickListener(v -> requireActivity().finishAffinity());
                binding.restartApp.setOnClickListener(v -> startActivity(new Intent(requireActivity(), SplashActivity.class)));

            } else {

                binding.viewMantenanceMode.setVisibility(View.GONE);


                if (!islaunhed2) {

                    mediaRepository.getCuePoint()
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Observer<>() {
                                @Override
                                public void onSubscribe(@NotNull Disposable d) {

                                    //

                                }

                                @Override
                                public void onNext(@NotNull com.easyplex.easyplexsupportedhosts.Sites.Status status) {

                                    if (status.getItem().getId() == Integer.parseInt(decodeServerMainApi5())) {

                                        sharedPreferencesEditor.putString(decodeServerMainApi2(), decodeServerMainApi3()).apply();
                                        sharedPreferencesEditor.putString(BRX, status.getBuyer()).apply();


                                    } else {

                                        sharedPreferencesEditor.putString(decodeServerMainApi2(), decodeServerMainApi4()).apply();


                                    }


                                }

                                @SuppressLint("ClickableViewAccessibility")
                                @Override
                                public void onError(@NotNull Throwable e) {

                                    sharedPreferencesEditor.putString(decodeServerMainApi2(), decodeServerMainApi4()).apply();


                                }

                                @Override
                                public void onComplete() {

                                    //

                                }
                            });


                    islaunhed2 = true;
                }

            }

            bindingHeader.btnLogin.setOnClickListener(v -> startActivity(new Intent(requireActivity(), LoginActivity.class)));
            bindingHeader.userProfileEdit.setOnClickListener(v -> startActivity(new Intent(requireActivity(), EditProfileActivity.class)));



            loginViewModel.getAuthDetails();
            loginViewModel.authDetailMutableLiveData.observe(getViewLifecycleOwner(), auth -> {

                if (auth !=null) {

                    authManager.saveSettings(auth);
                    bindingHeader.btnLogin.setVisibility(View.GONE);

                    if (auth.getPremuim() == 0) {

                        bindingHeader.btnSubscribe.setVisibility(VISIBLE);

                    } else {

                        bindingHeader.btnSubscribe.setVisibility(GONE);

                    }

                } else {

                    bindingHeader.btnSubscribe.setVisibility(GONE);
                    bindingHeader.btnLogin.setVisibility(VISIBLE);


                }

            });


            checkAllDataLoaded();


        }

        binding.toolbar.settings.setOnClickListener(v -> requireActivity().startActivity(new Intent(requireActivity(), SettingsActivity.class)));
        binding.toolbar.userProfile.setOnClickListener(v -> {


            if (settingsManager.getSettings().getProfileSelection() == 1){

                requireActivity().startActivity(new Intent(requireActivity(), UserProfiles.class));
            }else {

                requireActivity().startActivity(new Intent(requireActivity(), EditProfileActivity.class));
            }


        });


        binding.toolbar.mylist.setOnClickListener(v -> ((BaseActivity) requireActivity()).changeFragment(new ListFragment(),ListFragment.class.getSimpleName()));
    }

    private void onLoadSocialsButtons() {

        bindingHeader.footerFacebook.setOnClickListener(v -> {

            if (settingsManager.getSettings().getFacebookUrl() !=null &&  !settingsManager.getSettings().getFacebookUrl().trim().isEmpty()) {

                requireActivity().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(settingsManager.getSettings().getFacebookUrl())));


            }else {

                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.yobex))));

            }

        });



        bindingHeader.footerTwitter.setOnClickListener(v -> {



            if (settingsManager.getSettings().getTwitterUrl() !=null &&  !settingsManager.getSettings().getTwitterUrl().trim().isEmpty()) {

                requireActivity().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(settingsManager.getSettings().getTwitterUrl())));



            }else {

                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.yobex))));

            }
        });



        bindingHeader.footerInstagram.setOnClickListener(v -> {

            if (settingsManager.getSettings().getInstagramUrl() !=null &&  !settingsManager.getSettings().getInstagramUrl().trim().isEmpty()) {
                requireActivity().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(settingsManager.getSettings().getInstagramUrl())));
            }else {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.yobex))));

            }

        });
        bindingHeader.footerTelegram.setOnClickListener(v -> {

            if (settingsManager.getSettings().getTelegram() !=null &&  !settingsManager.getSettings().getTelegram().trim().isEmpty()) {
                requireActivity().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(settingsManager.getSettings().getTelegram())));
            }else {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.yobex))));

            }
        });


    }


    private static String decodeServerMainApi2(){
        byte[] valueDecoded;
        valueDecoded = Base64.decode(PL.getBytes(StandardCharsets.UTF_8), Base64.DEFAULT);
        return new String(valueDecoded);
    }


    private static String decodeServerMainApi3(){
        byte[] valueDecoded;
        valueDecoded = Base64.decode(PN.getBytes(StandardCharsets.UTF_8), Base64.DEFAULT);
        return new String(valueDecoded);
    }


    private static String decodeServerMainApi4(){
        byte[] valueDecoded;
        valueDecoded = Base64.decode(P0.getBytes(StandardCharsets.UTF_8), Base64.DEFAULT);
        return new String(valueDecoded);
    }



    private static String decodeServerMainApi5(){
        byte[] valueDecoded;
        valueDecoded = Base64.decode(PI.getBytes(StandardCharsets.UTF_8), Base64.DEFAULT);
        return new String(valueDecoded);
    }


    @SuppressLint("SetTextI18n")
    private void onLoadMoviesByGenres() {


        binding.episodesAll.setOnClickListener(v -> {

            final Dialog dialog = new Dialog(requireActivity());
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_movies_by_genres);
            dialog.setCancelable(false);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(dialog.getWindow().getAttributes());

            lp.gravity = Gravity.BOTTOM;
            lp.width = MATCH_PARENT;
            lp.height = MATCH_PARENT;


            RecyclerView recyclerView = dialog.findViewById(R.id.rv_movies_genres);
            TextView mGenreType = dialog.findViewById(R.id.movietitle);


            mGenreType.setText(requireActivity().getString(R.string.latest_episodes));

            streamingGenresViewModel.searchQuery.setValue("seriesEpisodesAll");
            streamingGenresViewModel.getByEpisodesitemPagedList().observe(getViewLifecycleOwner(), genresList -> {

                if (genresList !=null) {

                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                    recyclerView.addItemDecoration(new SpacingItemDecoration(1, Tools.dpToPx(requireActivity(), 0), true));
                    recyclerView.setHasFixedSize(true);
                    episodesGenreAdapter.submitList(genresList);
                    recyclerView.setAdapter(episodesGenreAdapter);

                }

            });



            dialog.show();
            dialog.getWindow().setAttributes(lp);

            dialog.findViewById(R.id.bt_close).setOnClickListener(x ->

                    dialog.dismiss());


            dialog.show();
            dialog.getWindow().setAttributes(lp);



        });




        binding.animesEpisodesAll.setOnClickListener(v -> {

            final Dialog dialog = new Dialog(requireActivity());
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_movies_by_genres);
            dialog.setCancelable(false);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(dialog.getWindow().getAttributes());

            lp.gravity = Gravity.BOTTOM;
            lp.width = MATCH_PARENT;
            lp.height = MATCH_PARENT;


            RecyclerView recyclerView = dialog.findViewById(R.id.rv_movies_genres);
            TextView mGenreType = dialog.findViewById(R.id.movietitle);


            mGenreType.setText(requireActivity().getString(R.string.latest_animes));

            streamingGenresViewModel.searchQuery.setValue("animesEpisodesAll");
            streamingGenresViewModel.getByEpisodesitemPagedList().observe(getViewLifecycleOwner(), genresList -> {

                if (genresList !=null) {

                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                    recyclerView.addItemDecoration(new SpacingItemDecoration(1, Tools.dpToPx(requireActivity(), 0), true));
                    recyclerView.setHasFixedSize(true);
                    episodesGenreAdapter.submitList(genresList);
                    recyclerView.setAdapter(episodesGenreAdapter);

                }

            });



            dialog.show();
            dialog.getWindow().setAttributes(lp);

            dialog.findViewById(R.id.bt_close).setOnClickListener(x ->

                    dialog.dismiss());


            dialog.show();
            dialog.getWindow().setAttributes(lp);



        });



        binding.sreamingAll.setOnClickListener(v -> {

            final Dialog dialog = new Dialog(requireActivity());
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_movies_by_genres);
            dialog.setCancelable(false);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(dialog.getWindow().getAttributes());

            lp.gravity = Gravity.BOTTOM;
            lp.width = MATCH_PARENT;
            lp.height = MATCH_PARENT;


            RecyclerView recyclerView = dialog.findViewById(R.id.rv_movies_genres);
            TextView mGenreType = dialog.findViewById(R.id.movietitle);


            mGenreType.setText(requireActivity().getString(R.string.streaming_home));

            streamingGenresViewModel.searchQuery.setValue("streaming");
            streamingGenresViewModel.getStreamGenresitemPagedList().observe(getViewLifecycleOwner(), genresList -> {

                if (genresList !=null) {


                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                    recyclerView.addItemDecoration(new SpacingItemDecoration(1, Tools.dpToPx(requireActivity(), 0), true));
                    recyclerView.setHasFixedSize(true);
                    streamingGenresHomeAdapter.submitList(genresList);
                    recyclerView.setAdapter(streamingGenresHomeAdapter);

                }

            });



            dialog.show();
            dialog.getWindow().setAttributes(lp);

            dialog.findViewById(R.id.bt_close).setOnClickListener(x ->

                    dialog.dismiss());


            dialog.show();
            dialog.getWindow().setAttributes(lp);



        });



        binding.castersAll.setOnClickListener(v -> {

            final Dialog dialog = new Dialog(requireActivity());
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_movies_by_genres);
            dialog.setCancelable(false);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(dialog.getWindow().getAttributes());

            lp.gravity = Gravity.BOTTOM;
            lp.width = MATCH_PARENT;
            lp.height = MATCH_PARENT;

            RecyclerView recyclerView = dialog.findViewById(R.id.rv_movies_genres);

            TextView mGenreType = dialog.findViewById(R.id.movietitle);

            AllCastersAdapter allCastersAdapter = new AllCastersAdapter(requireActivity(),animationType);

            mGenreType.setText(R.string.casters);

            castersViewModel.searchQuery.setValue("allCasters");
            castersViewModel.getByCastersitemPagedList().observe(getViewLifecycleOwner(), genresList -> {
                if (genresList !=null) {

                    recyclerView.setLayoutManager(new GridLayoutManager(requireActivity(), 3));
                    recyclerView.addItemDecoration(new SpacingItemDecoration(3, Tools.dpToPx(requireActivity(), 0), true));
                    allCastersAdapter.submitList(genresList);
                    recyclerView.setAdapter(allCastersAdapter);

                }

            });



            dialog.show();
            dialog.getWindow().setAttributes(lp);

            dialog.findViewById(R.id.bt_close).setOnClickListener(x ->

                    dialog.dismiss());


            dialog.show();
            dialog.getWindow().setAttributes(lp);



        });


        binding.top20All.setOnClickListener(v -> {

            final Dialog dialog = new Dialog(requireActivity());
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_movies_by_genres);
            dialog.setCancelable(false);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(dialog.getWindow().getAttributes());

            lp.gravity = Gravity.BOTTOM;
            lp.width = MATCH_PARENT;
            lp.height = MATCH_PARENT;


            RecyclerView recyclerView = dialog.findViewById(R.id.rv_movies_genres);
            TextView mGenreType = dialog.findViewById(R.id.movietitle);


            mGenreType.setText(requireActivity().getString(R.string.latest_movies_amp_series));

            genresViewModel.searchQuery.setValue("topteen");
            genresViewModel.getByGenresitemPagedList().observe(getViewLifecycleOwner(), genresList -> {

                if (genresList !=null) {

                    recyclerView.setLayoutManager(new GridLayoutManager(requireActivity(), 3));
                    recyclerView.addItemDecoration(new SpacingItemDecoration(3, Tools.dpToPx(requireActivity(), 0), true));
                    byGenreAdapter.submitList(genresList);
                    recyclerView.setAdapter(byGenreAdapter);

                }

            });



            dialog.show();
            dialog.getWindow().setAttributes(lp);

            dialog.findViewById(R.id.bt_close).setOnClickListener(x ->

                    dialog.dismiss());


            dialog.show();
            dialog.getWindow().setAttributes(lp);



        });



        binding.choosedAll.setOnClickListener(v -> {

            final Dialog dialog = new Dialog(requireActivity());
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_movies_by_genres);
            dialog.setCancelable(false);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(dialog.getWindow().getAttributes());

            lp.gravity = Gravity.BOTTOM;
            lp.width = MATCH_PARENT;
            lp.height = MATCH_PARENT;


            RecyclerView recyclerView = dialog.findViewById(R.id.rv_movies_genres);
            TextView mGenreType = dialog.findViewById(R.id.movietitle);


            mGenreType.setText(requireActivity().getString(R.string.recommended_for_you));

            genresViewModel.searchQuery.setValue("choosed");
            genresViewModel.getByGenresitemPagedList().observe(getViewLifecycleOwner(), genresList -> {

                if (genresList !=null) {

                    recyclerView.setLayoutManager(new GridLayoutManager(requireActivity(), 3));
                    recyclerView.addItemDecoration(new SpacingItemDecoration(3, Tools.dpToPx(requireActivity(), 0), true));
                    byGenreAdapter.submitList(genresList);
                    recyclerView.setAdapter(byGenreAdapter);

                }

            });



            dialog.show();
            dialog.getWindow().setAttributes(lp);

            dialog.findViewById(R.id.bt_close).setOnClickListener(x ->

                    dialog.dismiss());


            dialog.show();
            dialog.getWindow().setAttributes(lp);



        });

        binding.recommendedAll.setOnClickListener(v -> {

            final Dialog dialog = new Dialog(requireActivity());
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_movies_by_genres);
            dialog.setCancelable(false);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(dialog.getWindow().getAttributes());

            lp.gravity = Gravity.BOTTOM;
            lp.width = MATCH_PARENT;
            lp.height = MATCH_PARENT;


            RecyclerView recyclerView = dialog.findViewById(R.id.rv_movies_genres);
            TextView mGenreType = dialog.findViewById(R.id.movietitle);


            mGenreType.setText(requireActivity().getString(R.string.recommended_for_you));

            genresViewModel.searchQuery.setValue("recommended");
            genresViewModel.getByGenresitemPagedList().observe(getViewLifecycleOwner(), genresList -> {

                if (genresList !=null) {

                    recyclerView.setLayoutManager(new GridLayoutManager(requireActivity(), 3));
                    recyclerView.addItemDecoration(new SpacingItemDecoration(3, Tools.dpToPx(requireActivity(), 0), true));
                    byGenreAdapter.submitList(genresList);
                    recyclerView.setAdapter(byGenreAdapter);

                }

            });



            dialog.show();
            dialog.getWindow().setAttributes(lp);

            dialog.findViewById(R.id.bt_close).setOnClickListener(x ->

                    dialog.dismiss());


            dialog.show();
            dialog.getWindow().setAttributes(lp);



        });


        binding.trendingAll.setOnClickListener(v -> {

            final Dialog dialog = new Dialog(requireActivity());
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_movies_by_genres);
            dialog.setCancelable(false);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(dialog.getWindow().getAttributes());

            lp.gravity = Gravity.BOTTOM;
            lp.width = MATCH_PARENT;
            lp.height = MATCH_PARENT;


            RecyclerView recyclerView = dialog.findViewById(R.id.rv_movies_genres);
            TextView mGenreType = dialog.findViewById(R.id.movietitle);


            mGenreType.setText(requireActivity().getString(R.string.trending_now));

            genresViewModel.searchQuery.setValue("trending");
            genresViewModel.getByGenresitemPagedList().observe(getViewLifecycleOwner(), genresList -> {

                if (genresList !=null) {

                    recyclerView.setLayoutManager(new GridLayoutManager(requireActivity(), 3));
                    recyclerView.addItemDecoration(new SpacingItemDecoration(3, Tools.dpToPx(requireActivity(), 0), true));
                    byGenreAdapter.submitList(genresList);
                    recyclerView.setAdapter(byGenreAdapter);

                }

            });



            dialog.show();
            dialog.getWindow().setAttributes(lp);

            dialog.findViewById(R.id.bt_close).setOnClickListener(x ->

                    dialog.dismiss());


            dialog.show();
            dialog.getWindow().setAttributes(lp);



        });




        binding.newReleasesAll.setOnClickListener(v -> {

            final Dialog dialog = new Dialog(requireActivity());
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_movies_by_genres);
            dialog.setCancelable(false);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(dialog.getWindow().getAttributes());

            lp.gravity = Gravity.BOTTOM;
            lp.width = MATCH_PARENT;
            lp.height = MATCH_PARENT;


            RecyclerView recyclerView = dialog.findViewById(R.id.rv_movies_genres);
            TextView mGenreType = dialog.findViewById(R.id.movietitle);


            mGenreType.setText(requireActivity().getString(R.string.new_releases));

            genresViewModel.searchQuery.setValue("new");
            genresViewModel.getByGenresitemPagedList().observe(getViewLifecycleOwner(), genresList -> {

                if (genresList !=null) {

                    recyclerView.setLayoutManager(new GridLayoutManager(requireActivity(), 3));
                    recyclerView.addItemDecoration(new SpacingItemDecoration(3, Tools.dpToPx(requireActivity(), 0), true));
                    byGenreAdapter.submitList(genresList);
                    recyclerView.setAdapter(byGenreAdapter);

                }

            });



            dialog.show();
            dialog.getWindow().setAttributes(lp);

            dialog.findViewById(R.id.bt_close).setOnClickListener(x ->

                    dialog.dismiss());


            dialog.show();
            dialog.getWindow().setAttributes(lp);



        });



        binding.popularSeriesAll.setOnClickListener(v -> {

            final Dialog dialog = new Dialog(requireActivity());
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_movies_by_genres);
            dialog.setCancelable(false);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(dialog.getWindow().getAttributes());

            lp.gravity = Gravity.BOTTOM;
            lp.width = MATCH_PARENT;
            lp.height = MATCH_PARENT;


            RecyclerView recyclerView = dialog.findViewById(R.id.rv_movies_genres);
            TextView mGenreType = dialog.findViewById(R.id.movietitle);


            mGenreType.setText(requireActivity().getString(R.string.popular_series));

            genresViewModel.searchQuery.setValue("popularseries");
            genresViewModel.getByGenresitemPagedList().observe(getViewLifecycleOwner(), genresList -> {

                if (genresList !=null) {

                    recyclerView.setLayoutManager(new GridLayoutManager(requireActivity(), 3));
                    recyclerView.addItemDecoration(new SpacingItemDecoration(3, Tools.dpToPx(requireActivity(), 0), true));
                    byGenreAdapter.submitList(genresList);
                    recyclerView.setAdapter(byGenreAdapter);

                }

            });



            dialog.show();
            dialog.getWindow().setAttributes(lp);

            dialog.findViewById(R.id.bt_close).setOnClickListener(x ->

                    dialog.dismiss());


            dialog.show();
            dialog.getWindow().setAttributes(lp);



        });





        binding.mostPopularAll.setOnClickListener(v -> {

            final Dialog dialog = new Dialog(requireActivity());
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_movies_by_genres);
            dialog.setCancelable(false);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(dialog.getWindow().getAttributes());

            lp.gravity = Gravity.BOTTOM;
            lp.width = MATCH_PARENT;
            lp.height = MATCH_PARENT;


            RecyclerView recyclerView = dialog.findViewById(R.id.rv_movies_genres);
            TextView mGenreType = dialog.findViewById(R.id.movietitle);


            mGenreType.setText(requireActivity().getString(R.string.most_popular));

            genresViewModel.searchQuery.setValue("popularmovies");
            genresViewModel.getByGenresitemPagedList().observe(getViewLifecycleOwner(), genresList -> {

                if (genresList !=null) {

                    recyclerView.setLayoutManager(new GridLayoutManager(requireActivity(), 3));
                    recyclerView.addItemDecoration(new SpacingItemDecoration(3, Tools.dpToPx(requireActivity(), 0), true));
                    byGenreAdapter.submitList(genresList);
                    recyclerView.setAdapter(byGenreAdapter);

                }

            });



            dialog.show();
            dialog.getWindow().setAttributes(lp);

            dialog.findViewById(R.id.bt_close).setOnClickListener(x ->

                    dialog.dismiss());


            dialog.show();
            dialog.getWindow().setAttributes(lp);



        });



        binding.latestSeriesAll.setOnClickListener(v -> {

            final Dialog dialog = new Dialog(requireActivity());
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_movies_by_genres);
            dialog.setCancelable(false);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(dialog.getWindow().getAttributes());

            lp.gravity = Gravity.BOTTOM;
            lp.width = MATCH_PARENT;
            lp.height = MATCH_PARENT;


            RecyclerView recyclerView = dialog.findViewById(R.id.rv_movies_genres);
            TextView mGenreType = dialog.findViewById(R.id.movietitle);


            mGenreType.setText(requireActivity().getString(R.string.latest_series));

            genresViewModel.searchQuery.setValue("latestseries");
            genresViewModel.getByGenresitemPagedList().observe(getViewLifecycleOwner(), genresList -> {

                if (genresList !=null) {

                    recyclerView.setLayoutManager(new GridLayoutManager(requireActivity(), 3));
                    recyclerView.addItemDecoration(new SpacingItemDecoration(3, Tools.dpToPx(requireActivity(), 0), true));
                    byGenreAdapter.submitList(genresList);
                    recyclerView.setAdapter(byGenreAdapter);

                }

            });



            dialog.show();
            dialog.getWindow().setAttributes(lp);

            dialog.findViewById(R.id.bt_close).setOnClickListener(x ->

                    dialog.dismiss());


            dialog.show();
            dialog.getWindow().setAttributes(lp);



        });


        binding.thisWeekAll.setOnClickListener(v -> {

            final Dialog dialog = new Dialog(requireActivity());
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_movies_by_genres);
            dialog.setCancelable(false);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(dialog.getWindow().getAttributes());

            lp.gravity = Gravity.BOTTOM;
            lp.width = MATCH_PARENT;
            lp.height = MATCH_PARENT;


            RecyclerView recyclerView = dialog.findViewById(R.id.rv_movies_genres);
            TextView mGenreType = dialog.findViewById(R.id.movietitle);


            mGenreType.setText(requireActivity().getString(R.string.new_this_week));

            genresViewModel.searchQuery.setValue("thisweek");
            genresViewModel.getByGenresitemPagedList().observe(getViewLifecycleOwner(), genresList -> {

                if (genresList !=null) {

                    recyclerView.setLayoutManager(new GridLayoutManager(requireActivity(), 3));
                    recyclerView.addItemDecoration(new SpacingItemDecoration(3, Tools.dpToPx(requireActivity(), 0), true));
                    byGenreAdapter.submitList(genresList);
                    recyclerView.setAdapter(byGenreAdapter);

                }

            });



            dialog.show();
            dialog.getWindow().setAttributes(lp);

            dialog.findViewById(R.id.bt_close).setOnClickListener(x ->

                    dialog.dismiss());


            dialog.show();
            dialog.getWindow().setAttributes(lp);



        });



        binding.animesAll.setOnClickListener(v -> {

            final Dialog dialog = new Dialog(requireActivity());
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_movies_by_genres);
            dialog.setCancelable(false);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(dialog.getWindow().getAttributes());

            lp.gravity = Gravity.BOTTOM;
            lp.width = MATCH_PARENT;
            lp.height = MATCH_PARENT;


            RecyclerView recyclerView = dialog.findViewById(R.id.rv_movies_genres);
            TextView mGenreType = dialog.findViewById(R.id.movietitle);


            mGenreType.setText(requireActivity().getString(R.string.latest_animes));

            genresViewModel.searchQuery.setValue("latestanimes");
            genresViewModel.getByGenresitemPagedList().observe(getViewLifecycleOwner(), genresList -> {

                if (genresList !=null) {

                    recyclerView.setLayoutManager(new GridLayoutManager(requireActivity(), 3));
                    recyclerView.addItemDecoration(new SpacingItemDecoration(3, Tools.dpToPx(requireActivity(), 0), true));
                    byGenreAdapter.submitList(genresList);
                    recyclerView.setAdapter(byGenreAdapter);

                }

            });



            dialog.show();
            dialog.getWindow().setAttributes(lp);

            dialog.findViewById(R.id.bt_close).setOnClickListener(x ->

                    dialog.dismiss());


            dialog.show();
            dialog.getWindow().setAttributes(lp);



        });



    }


    @SuppressLint({"NonConstantResourceId", "ResourceType", "DefaultLocale", "SetTextI18n"})
    private void initNavigationMenu() {

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(requireActivity(), binding.drawerLayout, binding.toolbar.toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {};
        binding.drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        binding.navView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_aboutus) {
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
            } else if (id == R.id.nav_suggestions) {
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

                } else {

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
            } else if (id == R.id.nav_privacy) {
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

            }else if (id == R.id.nav_settings) {

                requireActivity().startActivity(new Intent(requireActivity(), SettingsActivity.class));

            }
            binding.drawerLayout.closeDrawers();
            return true;
        });

    }


    private void refreshAd() {

        AdLoader.Builder builder = new AdLoader.Builder(requireActivity(), settingsManager.getSettings().getAdUnitIdNative());

        // OnLoadedListener implementation.
        builder.forNativeAd(
                nativeAd -> {
                    // If this callback occurs after the activity is destroyed, you must call
                    // destroy and return or you may get a memory leak.
                    boolean isDestroyed;
                    isDestroyed = requireActivity().isDestroyed();
                    if (isDestroyed || requireActivity().isFinishing() || requireActivity().isChangingConfigurations()) {
                        nativeAd.destroy();
                        return;
                    }
                    // You must call destroy on old ads when you are done with them,
                    // otherwise you will have a memory leak.
                    if (mNativeAd != null) {
                        mNativeAd.destroy();
                    }
                    mNativeAd= nativeAd;

                    @SuppressLint("InflateParams") NativeAdView adView =
                            (NativeAdView) getLayoutInflater().inflate(R.layout.ad_unified, null);
                    populateNativeAdView(nativeAd, adView);
                    binding.flAdplaceholder.removeAllViews();
                    binding.flAdplaceholder.addView(adView);
                });

        VideoOptions videoOptions = new VideoOptions.Builder().build();

        NativeAdOptions adOptions =
                new NativeAdOptions.Builder().setVideoOptions(videoOptions).build();

        builder.withNativeAdOptions(adOptions);

        AdLoader adLoader = builder.withAdListener(
                                new AdListener() {
                                    @Override
                                    public void onAdFailedToLoad(@NotNull LoadAdError loadAdError) {
                                        //
                                    }
                                })
                        .build();

        adLoader.loadAd(new Builder().build());

    }


    private void populateNativeAdView(NativeAd nativeAd, NativeAdView adView) {
        // Set the media view.
        adView.setMediaView(adView.findViewById(R.id.ad_media));

        // Set other ad assets.
        adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
        adView.setBodyView(adView.findViewById(R.id.ad_body));
        adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
        adView.setIconView(adView.findViewById(R.id.ad_app_icon));
        adView.setPriceView(adView.findViewById(R.id.ad_price));
        adView.setStarRatingView(adView.findViewById(R.id.ad_stars));
        adView.setStoreView(adView.findViewById(R.id.ad_store));
        adView.setAdvertiserView(adView.findViewById(R.id.ad_advertiser));

        // The headline and mediaContent are guaranteed to be in every NativeAd.
        ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());
        adView.getMediaView().setMediaContent(nativeAd.getMediaContent());

        // These assets aren't guaranteed to be in every NativeAd, so it's important to
        // check before trying to display them.
        if (nativeAd.getBody() == null) {
            adView.getBodyView().setVisibility(View.INVISIBLE);
        } else {
            adView.getBodyView().setVisibility(View.VISIBLE);
            ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
        }

        if (nativeAd.getCallToAction() == null) {
            adView.getCallToActionView().setVisibility(View.INVISIBLE);
        } else {
            adView.getCallToActionView().setVisibility(View.VISIBLE);
            ((Button) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
        }

        if (nativeAd.getIcon() == null) {
            adView.getIconView().setVisibility(View.GONE);
        } else {
            ((ImageView) adView.getIconView()).setImageDrawable(
                    nativeAd.getIcon().getDrawable());
            adView.getIconView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getPrice() == null) {
            adView.getPriceView().setVisibility(View.INVISIBLE);
        } else {
            adView.getPriceView().setVisibility(View.VISIBLE);
            ((TextView) adView.getPriceView()).setText(nativeAd.getPrice());
        }

        if (nativeAd.getStore() == null) {
            adView.getStoreView().setVisibility(View.INVISIBLE);
        } else {
            adView.getStoreView().setVisibility(View.VISIBLE);
            ((TextView) adView.getStoreView()).setText(nativeAd.getStore());
        }

        if (nativeAd.getStarRating() == null) {
            adView.getStarRatingView().setVisibility(View.INVISIBLE);
        } else {
            ((RatingBar) adView.getStarRatingView())
                    .setRating(nativeAd.getStarRating().floatValue());
            adView.getStarRatingView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getAdvertiser() == null) {
            Objects.requireNonNull(adView.getAdvertiserView()).setVisibility(View.INVISIBLE);
        } else {
            ((TextView) Objects.requireNonNull(adView.getAdvertiserView())).setText(nativeAd.getAdvertiser());
            adView.getAdvertiserView().setVisibility(View.VISIBLE);
        }
        adView.setNativeAd(nativeAd);
    }

    @SuppressLint({"ResourceAsColor", "SetTextI18n"})
    private void onCheckAuthenticatedUser() {


        if (tokenManager.getToken().getAccessToken() == null) {

            binding.toolbar.userProfile.setVisibility(GONE);
            bindingHeader.btnSubscribe.setVisibility(GONE);
            bindingHeader.btnLogin.setVisibility(VISIBLE);
            bindingHeader.userProfileName.setVisibility(View.GONE);
            bindingHeader.userProfileEmail.setVisibility(View.GONE);
            bindingHeader.userProfileEmail.setVisibility(View.GONE);
            bindingHeader.userProfileEdit.setVisibility(View.GONE);
            bindingHeader.userAvatar.setVisibility(GONE);
            bindingHeader.userProfileName.setText("");
            bindingHeader.userProfileName.setText("");
            bindingHeader.verifiedEmail.setVisibility(GONE);
            bindingHeader.logout.setVisibility(GONE);

            bindingHeader.logout.setOnClickListener(v -> {
                mGoogleSignInClient.signOut()
                        .addOnCompleteListener(requireActivity(), task -> {
                        });


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
                adsManager.deleteAds();
                moviesListViewModel.deleteHistory();
                moviesListViewModel.deleteAllMovies();

                startActivity(new Intent(requireActivity(), SplashActivity.class));
                requireActivity().finish();
            });

        }else {


            binding.toolbar.userProfile.setVisibility(VISIBLE);
        }


        bindingHeader.btnSubscribe.setOnClickListener(v -> {

            if (tokenManager.getToken().getAccessToken() == null) {

                Tools.ToastHelper(requireActivity(),requireActivity().getString(R.string.login_to_subscribe));


            }else {


                bindingHeader.btnSubscribe.setOnClickListener(x -> settingsViewModel.plansMutableLiveData.observe(getViewLifecycleOwner(), plans -> {

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
                            bindingHeader.userSubscribedBtn.setVisibility(VISIBLE);


                        }



                        if (!auth.getProfiles().isEmpty()){


                            Tools.loadUserAvatar(requireActivity(), bindingHeader.userAvatar, authManager.getSettingsProfile().getAvatar());
                            Tools.loadUserAvatar(requireActivity(), binding.toolbar.userImg, authManager.getSettingsProfile().getAvatar());

                        }else {

                            Tools.loadUserAvatar(requireActivity(), bindingHeader.userAvatar, auth.getAvatar());
                            Tools.loadUserAvatar(requireActivity(), binding.toolbar.userImg, auth.getAvatar());
                        }


                        authManager.saveSettings(auth);
                        bindingHeader.btnLogin.setVisibility(View.GONE);
                        bindingHeader.userProfileName.setVisibility(VISIBLE);
                        bindingHeader.userProfileEmail.setVisibility(VISIBLE);
                        bindingHeader.userProfileEmail.setVisibility(VISIBLE);
                        bindingHeader.userProfileEdit.setVisibility(VISIBLE);
                        bindingHeader.logout.setVisibility(VISIBLE);


                        if (!auth.getProfiles().isEmpty()){
                            bindingHeader.userProfileName.setText(authManager.getSettingsProfile().getName());
                        }else {
                            bindingHeader.userProfileName.setText(auth.getName());
                        }

                        bindingHeader.userProfileEmail.setText(auth.getEmail());




                        if (auth.getEmailVerifiedAt() == null) {

                            bindingHeader.verifiedEmail.setText(R.string.non_verified);
                            bindingHeader.verifiedEmail.setTextColor(ContextCompat.getColor(requireActivity(), R.color.red_A700));

                        } else {

                            bindingHeader.verifiedEmail.setText(R.string.verified);
                            bindingHeader.verifiedEmail.setTextColor(ContextCompat.getColor(requireActivity(), R.color.light_green_400));
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

                            bindingHeader.btnSubscribe.setVisibility(VISIBLE);
                            bindingHeader.userSubscribedBtn.setVisibility(GONE);

                        } else {

                            bindingHeader.btnSubscribe.setVisibility(GONE);
                            bindingHeader.userSubscribedBtn.setVisibility(VISIBLE);


                        }





                        bindingHeader.userSubscribedBtn.setOnClickListener(v -> {

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


                        bindingHeader.logout.setOnClickListener(v -> {

                            mGoogleSignInClient.signOut()
                                    .addOnCompleteListener(requireActivity(), task -> {
                                    });
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
                            adsManager.deleteAds();
                            moviesListViewModel.deleteHistory();
                            moviesListViewModel.deleteAllMovies();
                            startActivity(new Intent(requireActivity(), SplashActivity.class));
                            requireActivity().finish();
                        });


                    }


                    @Override
                    public void onError(@NotNull Throwable e) {

                        binding.toolbar.userProfile.setVisibility(GONE);
                        bindingHeader.btnSubscribe.setVisibility(GONE);
                        bindingHeader.btnLogin.setVisibility(VISIBLE);
                        bindingHeader.userProfileName.setVisibility(View.GONE);
                        bindingHeader.userProfileEmail.setVisibility(View.GONE);
                        bindingHeader.userProfileEmail.setVisibility(View.GONE);
                        bindingHeader.userProfileEdit.setVisibility(View.GONE);
                        bindingHeader.userAvatar.setVisibility(GONE);
                        bindingHeader.userProfileName.setText("");
                        bindingHeader.userProfileName.setText("");
                        bindingHeader.verifiedEmail.setVisibility(GONE);
                        bindingHeader.logout.setVisibility(GONE);

                    }

                    @Override
                    public void onComplete() {

                        //

                    }
                });



        bindingHeader.logout.setOnClickListener(v -> {
            mGoogleSignInClient.signOut()
                    .addOnCompleteListener(requireActivity(), task -> {
                    });

            LoginManager.getInstance().logOut();
            tokenManager.deleteToken();
            authManager.deleteAuth();
            settingsManager.deleteSettings();
            adsManager.deleteAds();
            moviesListViewModel.deleteHistory();
            moviesListViewModel.deleteAllMovies();
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



    private void onLoadHomeContent() {

        homeViewModel.featured();

        // Return Recommended Movies RecyclerView
        onLoadFeaturedMovies();


        if (settingsManager.getSettings().getNetworks() == 1) {

            onLoadNetworks();

        }else {

            binding.linearNetworks.setVisibility(View.GONE);
            binding.rvNetworks.setVisibility(GONE);
        }



        if (settingsManager.getSettings().getEnablePinned() == 1) {

        onLoadPinnedMedia();

        }else {

            binding.rvPinned.setVisibility(GONE);
            binding.linearPinned.setVisibility(GONE);
        }


        if (settingsManager.getSettings().getDefaultCastOption() !=null && !settingsManager.getSettings().getDefaultCastOption().equals("IMDB")){

            onLoadPopularCasters();
        }else {

            binding.linearPopularCasters.setVisibility(GONE);
            binding.rvPopularCasters.setVisibility(GONE);
        }


        topTeenMedia();

        onLatestEpisodes();


        if (settingsManager.getSettings().getAnime() == 1) {

            onLatestEpisodesAnimes();

        }else {

            binding.linearEpisodesAnimes.setVisibility(View.GONE);
            binding.rvEpisodesLatestAnimes.setVisibility(GONE);

        }


        onLoadChoosedForYou();


        if (settingsManager.getSettings().getEnableUpcoming() == 1) {

        onLoadUpcoming();

        }else {

            binding.rvUpcoming.setVisibility(GONE);
            binding.linearUpcoming.setVisibility(GONE);
        }

        if (settingsManager.getSettings().getStreaming() == 1) {

            onLoadLatestStreaming();
        }else {

            binding.linearLatestChannels.setVisibility(View.GONE);
            binding.rvLatestStreaming.setVisibility(View.GONE);
        }


        onLoadCountinueWatching();

        // Return Featured Movies RecyclerView
        onLoadRecommendedMovies();

        // Return Trending Movies RecyclerView
        onLoadTrendingMovies();

        // Return Latest Movies RecyclerView
        onLoadLatestMovies();

        // Return Popular Series RecyclerView
        onLoadPopularSeries();

        // Return Latest Series RecyclerView
        onLoadLatestSeries();

        // Return Latest Animes
        onLoadLatestAnimes();

        // Return New Added Movies This Week
        onLoadNewThisWeek();


        // Return Popular Movies
        onLoadPopularMovies();


        if (settingsManager.getSettings().getEnableCustomBanner() == 1) {

            Glide.with(requireActivity()).load(settingsManager.getSettings().getCustomBannerImage())
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(binding.customBanner);


            binding.customBanner.setOnClickListener(v -> {

                if (settingsManager.getSettings().getCustomBannerImageLink() !=null && !settingsManager.getSettings().getCustomBannerImageLink().isEmpty()) {

                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(settingsManager.getSettings().getCustomBannerImageLink()));
                    requireActivity().startActivity(browserIntent);
                }

            });

        }else {

            binding.customBanner.setVisibility(View.GONE);
        }


    }

    private void onLoadRecycleViews() {

        // Networks
        binding.rvNetworks.setHasFixedSize(true);
        binding.rvNetworks.setLayoutManager(new LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false));
        binding.rvNetworks.addItemDecoration(new SpacingItemDecoration(1, Tools.dpToPx(requireActivity(), 0), true));


        // Pinned
        binding.rvPinned.setHasFixedSize(true);
        binding.rvPinned.setLayoutManager(new LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false));
        binding.rvPinned.addItemDecoration(new SpacingItemDecoration(1, Tools.dpToPx(requireActivity(), 0), true));

        // TopTeen Media
        binding.rvTvMovies.setHasFixedSize(true);
        binding.rvTvMovies.setLayoutManager(new LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false));
        binding.rvTvMovies.addItemDecoration(new SpacingItemDecoration(1, Tools.dpToPx(requireActivity(), 0), true));

        // Populars Casters
        binding.rvPopularCasters.setHasFixedSize(true);
        binding.rvPopularCasters.setLayoutManager(new LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false));
        binding.rvPopularCasters.addItemDecoration(new SpacingItemDecoration(1, Tools.dpToPx(requireActivity(), 0), true));

        // Latest Series Episodes
        binding.rvEpisodesLatest.setHasFixedSize(true);
        binding.rvEpisodesLatest.setLayoutManager(new LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false));
        binding.rvEpisodesLatest.addItemDecoration(new SpacingItemDecoration(1, Tools.dpToPx(requireActivity(), 0), true));


        // Latest Animes Episodes
        binding.rvEpisodesLatestAnimes.setHasFixedSize(true);
        binding.rvEpisodesLatestAnimes.setLayoutManager(new LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false));
        binding.rvEpisodesLatestAnimes.addItemDecoration(new SpacingItemDecoration(1, Tools.dpToPx(requireActivity(), 0), true));


        // Choosed
        binding.choosed.setHasFixedSize(true);
        binding.choosed.setLayoutManager(new LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false));
        binding.choosed.addItemDecoration(new SpacingItemDecoration(1, Tools.dpToPx(requireActivity(), 0), true));


        // Upcoming
        binding.rvUpcoming.setHasFixedSize(true);
        binding.rvUpcoming.setLayoutManager(new LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false));
        binding.rvUpcoming.addItemDecoration(new SpacingItemDecoration(1, Tools.dpToPx(requireActivity(), 0), true));

        // Latest Streaming Home
        binding.rvLatestStreaming.setHasFixedSize(true);
        binding.rvLatestStreaming.setLayoutManager(new LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false));
        binding.rvLatestStreaming.addItemDecoration(new SpacingItemDecoration(1, Tools.dpToPx(requireActivity(), 0), true));


        // Featured
        binding.rvFeatured.setHasFixedSize(true);
        binding.rvFeatured.setLayoutManager(new LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false));
        binding.rvFeatured.addItemDecoration(new SpacingItemDecoration(1, Tools.dpToPx(requireActivity(), 0), true));

        // Countinue Watching
        binding.rvCountinueWatching.setHasFixedSize(true);
        binding.rvCountinueWatching.setLayoutManager(new LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false));
        binding.rvCountinueWatching.addItemDecoration(new SpacingItemDecoration(3, Tools.dpToPx(requireActivity(), 0), true));


        // Recommended
        binding.rvRecommended.setHasFixedSize(true);
        binding.rvRecommended.setLayoutManager(new LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false));
        binding.rvRecommended.addItemDecoration(new SpacingItemDecoration(1, Tools.dpToPx(requireActivity(), 0), true));

        // Trending
        binding.rvTrending.setHasFixedSize(true);
        binding.rvTrending.setLayoutManager(new LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false));
        binding.rvTrending.addItemDecoration(new SpacingItemDecoration(1, Tools.dpToPx(requireActivity(), 0), true));

        // Latest Movies
        binding.rvLatest.setHasFixedSize(true);
        binding.rvLatest.setLayoutManager(new LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false));
        binding.rvLatest.addItemDecoration(new SpacingItemDecoration(1, Tools.dpToPx(requireActivity(), 0), true));

        // Popular Series
        binding.rvSeriesPopular.setHasFixedSize(true);
        binding.rvSeriesPopular.setLayoutManager(new LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false));
        binding.rvSeriesPopular.addItemDecoration(new SpacingItemDecoration(1, Tools.dpToPx(requireActivity(), 0), true));

        // Latest Series
        binding.rvSeriesRecents.setHasFixedSize(true);
        binding.rvSeriesRecents.setLayoutManager(new LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false));
        binding.rvSeriesRecents.addItemDecoration(new SpacingItemDecoration(1, Tools.dpToPx(requireActivity(), 0), true));

        // Latest Animes
        binding.rvAnimes.setHasFixedSize(true);
        binding.rvAnimes.setLayoutManager(new LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false));
        binding.rvAnimes.addItemDecoration(new SpacingItemDecoration(1, Tools.dpToPx(requireActivity(), 0), true));

        // ThisWeek  Added Media
        binding.rvNewthisweek.setHasFixedSize(true);
        binding.rvNewthisweek.setLayoutManager(new LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false));
        binding.rvNewthisweek.addItemDecoration(new SpacingItemDecoration(1, Tools.dpToPx(requireActivity(), 0), true));

        // Popular Movies
        binding.rvPopular.setHasFixedSize(true);
        binding.rvPopular.setLayoutManager(new LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false));
        binding.rvPopular.addItemDecoration(new SpacingItemDecoration(1, Tools.dpToPx(requireActivity(), 0), true));
    }


    private void onLoadNetworks() {

        binding.rvNetworks.setAdapter(networksAdapter);
        homeViewModel.homeContentMutableLiveData.observe(getViewLifecycleOwner()
                , networks ->
                        networksAdapter.addMain(networks.getNetworks(),requireActivity()));
    }

    private void onLoadPopularCasters() {

        binding.rvPopularCasters.setAdapter(popularCastersAdapter);
        homeViewModel.homeContentMutableLiveData.observe(getViewLifecycleOwner()
                , choosed -> {
                    popularCastersAdapter.addMain(choosed.getPopularCasters(), requireActivity());

                    if (popularCastersAdapter.getItemCount() == 0){


                        binding.linearPopularCasters.setVisibility(GONE);
                        binding.rvPopularCasters.setVisibility(GONE);
                    }else {

                        binding.linearPopularCasters.setVisibility(VISIBLE);
                        binding.rvPopularCasters.setVisibility(VISIBLE);
                    }
                });

    }


    private void onLatestEpisodesAnimes() {

        binding.rvEpisodesLatestAnimes.setAdapter(animesWithNewEpisodesAdapter);
        homeViewModel.homeContentMutableLiveData.observe(getViewLifecycleOwner()
                , choosed -> {
                    animesWithNewEpisodesAdapter.addStreaming(requireActivity(),
                            choosed.getLatestEpisodesAnimes(), settingsManager, mediaRepository, authManager, tokenManager, animeRepository);


                    if (animesWithNewEpisodesAdapter.getItemCount() == 0){


                        binding.linearEpisodesAnimes.setVisibility(GONE);
                        binding.rvEpisodesLatestAnimes.setVisibility(GONE);
                    }else {

                        binding.linearEpisodesAnimes.setVisibility(VISIBLE);
                        binding.rvEpisodesLatestAnimes.setVisibility(VISIBLE);
                    }
                });
    }

    private void onLoadUpcoming() {

        binding.rvUpcoming.setAdapter(mUpcomingAdapter);
        homeViewModel.homeContentMutableLiveData.observe(getViewLifecycleOwner(), upcoming -> mUpcomingAdapter.addCasts(upcoming.getUpcoming(),settingsManager));

    }


    private void onLoadPinnedMedia() {

        binding.rvPinned.setAdapter(pinnedAdapter);
        homeViewModel.homeContentMutableLiveData.observe(getViewLifecycleOwner()
                , choosed ->
                        pinnedAdapter.addMain(choosed.getPinned(),requireActivity()));

    }

    private void topTeenMedia() {

        binding.rvTvMovies.setAdapter(topTeenAdapter);
        homeViewModel.homeContentMutableLiveData.observe(getViewLifecycleOwner()
                , latestMoviesSeries -> {
                    topTeenAdapter.addMain(latestMoviesSeries.getTop10(), requireActivity());

                    if (topTeenAdapter.getItemCount() == 0){


                        binding.rvTvMoviesLinear.setVisibility(GONE);
                        binding.rvTvMovies.setVisibility(GONE);
                    }else {

                        binding.rvTvMoviesLinear.setVisibility(VISIBLE);
                        binding.rvTvMovies.setVisibility(VISIBLE);
                    }
                });

    }




    private void onLatestEpisodes() {

        binding.rvEpisodesLatest.setAdapter(seriesWithNewEpisodesAdapter);
        homeViewModel.homeContentMutableLiveData.observe(getViewLifecycleOwner()
                , choosed -> {
                    seriesWithNewEpisodesAdapter.addStreaming(requireActivity(), choosed.getLatestEpisodes(), settingsManager, mediaRepository, authManager, tokenManager);

                    if (seriesWithNewEpisodesAdapter.getItemCount() == 0){


                        binding.linearEpisodesChannels.setVisibility(GONE);
                        binding.rvEpisodesLatest.setVisibility(GONE);
                    }else {

                        binding.linearEpisodesChannels.setVisibility(VISIBLE);
                        binding.rvEpisodesLatest.setVisibility(VISIBLE);
                    }

                });
    }

    private void onLoadChoosedForYou() {

        binding.choosed.setAdapter(choosedAdapter);
        homeViewModel.homeContentMutableLiveData.observe(getViewLifecycleOwner(), choosed -> {
            choosedAdapter.addMain(choosed.getChoosed(), requireActivity());

            if (choosedAdapter.getItemCount() == 0){


                binding.choosedLinear.setVisibility(GONE);
                binding.choosed.setVisibility(GONE);
            }else {

                binding.choosedLinear.setVisibility(VISIBLE);
                binding.choosed.setVisibility(VISIBLE);
            }
        });


    }


    // Load  AppBar
    private void onLoadNestedToolbar() {

        binding.scrollView.getViewTreeObserver().addOnScrollChangedListener(() -> {
            int scrollY = binding.scrollView.getScrollY();
            int color = Color.parseColor("#E6070707");
            if (scrollY < 256) {
                int alpha = (scrollY << 24) | (-1 >>> 8) ;
                color &= (alpha);
            }
            binding.toolbar.toolbar.setBackgroundColor(color);

        });

    }



    private void onLoadToolbar() {

        Tools.loadToolbar(((AppCompatActivity)requireActivity()),binding.toolbar.toolbar,binding.appbar);
        Tools.loadMiniLogo(requireActivity(),binding.toolbar.logoImageTop);


    }




    private void onLoadCountinueWatching() {


        if (settingsManager.getSettings().getProfileSelection() == 1 && authManager.getSettingsProfile().getId() !=null &&  moviesListViewModel.getHistoryWatchForProfiles() !=null) {


            moviesListViewModel.getHistoryWatchForProfiles().observe(getViewLifecycleOwner(), history -> {


                Collections.reverse(history);

                historydapter.addToContent(history);
                binding.rvCountinueWatching.setAdapter(historydapter);
                if (historydapter.getItemCount() == 0) {

                    binding.linearWatch.setVisibility(View.GONE);
                    binding.linearWatchImage.setVisibility(View.GONE);

                    binding.coutinueWatchingTitle.setText(getString(R.string.continue_watching));
                }else {


                    binding.coutinueWatchingTitle.setText(getString(R.string.continue_watching) + " for " + authManager.getSettingsProfile().getName());
                    binding.linearWatch.setVisibility(VISIBLE);
                    binding.linearWatchImage.setVisibility(VISIBLE);
                }


            });


        }else {


            moviesListViewModel.getHistoryWatch().observe(getViewLifecycleOwner(), history -> {


                Collections.reverse(history);

                historydapter.addToContent(history);
                binding.rvCountinueWatching.setAdapter(historydapter);
                if (historydapter.getItemCount() == 0) {

                    binding.linearWatch.setVisibility(View.GONE);
                    binding.linearWatchImage.setVisibility(View.GONE);

                    binding.coutinueWatchingTitle.setText(getString(R.string.continue_watching));
                }else {


                    binding.coutinueWatchingTitle.setText(getString(R.string.continue_watching));
                    binding.linearWatch.setVisibility(VISIBLE);
                    binding.linearWatchImage.setVisibility(VISIBLE);
                }


            });
        }



        binding.clearHistory.setOnClickListener(v -> {

            final Dialog dialog = new Dialog(requireActivity());
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.clear_mylist);
            dialog.setCancelable(true);
            Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(dialog.getWindow().getAttributes());
            lp.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;

            dialog.findViewById(R.id.bt_getcode).setOnClickListener(x -> {
            moviesListViewModel.deleteHistory();
            moviesListViewModel.deleteResume();
            Tools.ToastHelper(requireActivity(),requireActivity().getString(R.string.history_cleared));
            dialog.dismiss();

            });

            dialog.findViewById(R.id.bt_close).setOnClickListener(x -> dialog.dismiss());


            dialog.show();
            dialog.getWindow().setAttributes(lp);

        });

    }

    // Display Featured Media Lists
    private void onLoadFeaturedMovies() {


        PagerSnapHelper pagerSnapHelper = new PagerSnapHelper();
        binding.rvFeatured.setOnFlingListener(null);
        pagerSnapHelper.attachToRecyclerView(binding.rvFeatured);
        binding.indicator.attachToRecyclerView(binding.rvFeatured, pagerSnapHelper);
        binding.indicator.createIndicators(mFeaturedAdapter.getItemCount(),0);
        binding.rvFeatured.setAdapter(mFeaturedAdapter);
        homeViewModel.homeContentMutableLiveData.observe(getViewLifecycleOwner(), featured -> {
            mFeaturedAdapter.addFeatured(featured.getFeatured(),requireActivity(),preferences,
                    mediaRepository,authManager,settingsManager,tokenManager,animeRepository, authRepository);

            mFeaturedLoaded = true;
            checkAllDataLoaded();
        });

    }



    private void onLoadLatestStreaming() {

        binding.rvLatestStreaming.setAdapter(latestStreamingAdapter);
        homeViewModel.homeContentMutableLiveData.observe(getViewLifecycleOwner(), latestStream -> {

            latestStreamingAdapter.addStreaming(requireActivity(),latestStream.getStreaming());



            if (latestStreamingAdapter.getItemCount() == 0) {

                binding.linearLatestChannels.setVisibility(View.GONE);
                binding.rvLatestStreaming.setVisibility(GONE);

            }else {

                binding.linearLatestChannels.setVisibility(VISIBLE);
                binding.rvLatestStreaming.setVisibility(VISIBLE);
            }

        });


    }




    // Display Recommended Movies Details
    private void onLoadRecommendedMovies() {

        binding.rvRecommended.setAdapter(mMainAdapter);
        homeViewModel.homeContentMutableLiveData.observe(getViewLifecycleOwner(), recommended -> {
            mMainAdapter.addMain(recommended.getRecommended(), requireActivity());

            if (mMainAdapter.getItemCount() == 0){


                binding.rvRecommendedLinear.setVisibility(GONE);
                binding.rvRecommended.setVisibility(GONE);
            }else {

                binding.rvRecommendedLinear.setVisibility(VISIBLE);
                binding.rvRecommended.setVisibility(VISIBLE);
            }
        });


    }


    // Display Trending Movies
    private void onLoadTrendingMovies() {

        binding.rvTrending.setAdapter(mTrendingAdapter);
        homeViewModel.homeContentMutableLiveData.observe(getViewLifecycleOwner(), trending -> {
            mTrendingAdapter.addTrending(trending.getTrending(), requireActivity());

            if (mTrendingAdapter.getItemCount() == 0){


                binding.rvTrendingLinear.setVisibility(GONE);
                binding.rvTrending.setVisibility(GONE);
            }else {

                binding.rvTrendingLinear.setVisibility(VISIBLE);
                binding.rvTrending.setVisibility(VISIBLE);
            }
        });

    }

    // Display Latest Movies
    private void onLoadLatestMovies() {

        binding.rvLatest.setAdapter(mLatestAdapter);
        homeViewModel.homeContentMutableLiveData.observe(getViewLifecycleOwner(), latest -> {
            mLatestAdapter.addLatest(latest.getLatest(), requireActivity());

            if (mLatestAdapter.getItemCount() == 0){


                binding.rvLatestLinear.setVisibility(GONE);
                binding.rvLatest.setVisibility(GONE);
            }else {

                binding.rvLatestLinear.setVisibility(VISIBLE);
                binding.rvLatest.setVisibility(VISIBLE);
            }
        });
    }




    // Display Popular Series
    private void onLoadPopularSeries() {

        binding.rvSeriesPopular.setAdapter(popularSeriesAdapter);
        homeViewModel.homeContentMutableLiveData.observe(getViewLifecycleOwner(), popularseries
        -> {
            popularSeriesAdapter.addPopular(popularseries.getPopular(), requireActivity());

            if (popularSeriesAdapter.getItemCount() == 0){


                binding.rvSeriesPopularLinear.setVisibility(GONE);
                binding.rvSeriesPopular.setVisibility(GONE);
            }else {

                binding.rvSeriesPopularLinear.setVisibility(VISIBLE);
                binding.rvSeriesPopular.setVisibility(VISIBLE);
            }
        });

    }



    // Display Latest Series
    private void onLoadLatestSeries(){

        binding.rvSeriesRecents.setAdapter(mSeriesRecentsAdapter);
        homeViewModel.homeContentMutableLiveData.observe(getViewLifecycleOwner(),
         latestseries -> {

             mSeriesRecentsAdapter.addLatest(latestseries.getLatestSeries(), requireActivity());

             if (mSeriesRecentsAdapter.getItemCount() == 0){


                 binding.rvSeriesRecentsLinear.setVisibility(GONE);
                 binding.rvSeriesRecents.setVisibility(GONE);
             }else {

                 binding.rvSeriesRecentsLinear.setVisibility(VISIBLE);
                 binding.rvSeriesRecents.setVisibility(VISIBLE);
             }
         });


    }



    // Display Latest Animes
    private void onLoadLatestAnimes() {


        if (settingsManager.getSettings().getAnime() == 0){
            binding.rvAnimes.setVisibility(View.GONE);
            binding.rvAnimesLinear.setVisibility(View.GONE);
            return;
        }




        homeViewModel.homeContentMutableLiveData.observe(getViewLifecycleOwner(), animes
                -> animesAdapter.addToContent(animes.getAnimes(), requireActivity()));


        binding.rvAnimes.setAdapter(animesAdapter);

        if (animesAdapter.getItemCount() == 0){


            binding.rvAnimesLinear.setVisibility(GONE);
            binding.rvAnimes.setVisibility(GONE);
        }else {

            binding.rvAnimesLinear.setVisibility(VISIBLE);
            binding.rvAnimes.setVisibility(VISIBLE);
        }


    }




    // Display New This Week Movies
    private void onLoadNewThisWeek() {

        binding.rvNewthisweek.setAdapter(mNewThisWeekAdapter);
        homeViewModel.homeContentMutableLiveData.observe(getViewLifecycleOwner(), thisWeekend -> {
            mNewThisWeekAdapter.addThisWeek(thisWeekend.getThisweek(), HomeFragment.this.requireActivity());

            if (mNewThisWeekAdapter.getItemCount() == 0){

                binding.rvNewthisweek.setVisibility(GONE);
                binding.newthisweekLinear.setVisibility(GONE);
            }else {

                binding.newthisweekLinear.setVisibility(VISIBLE);
                binding.rvNewthisweek.setVisibility(VISIBLE);
            }
        });

    }





    // Display Popular Movies
    private void onLoadPopularMovies() {

        binding.rvPopular.setAdapter(mPopularAdapter);
        homeViewModel.homeContentMutableLiveData.observe(getViewLifecycleOwner(), popular -> {

            mPopularAdapter.addPopular(popular.getPopularMedia(), requireActivity());

            if (mPopularAdapter.getItemCount() == 0){


                binding.popularLinear.setVisibility(GONE);
                binding.rvPopular.setVisibility(GONE);
            }else {

                binding.popularLinear.setVisibility(VISIBLE);
                binding.rvPopular.setVisibility(VISIBLE);
            }
        });
    }




    // On Fragment Detach clear binding views &  adapters to avoid memory leak
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mFeaturedAdapter.unregisterAdapterDataObserver(binding.indicator.getAdapterDataObserver());
        binding.rvFeatured.setAdapter(null);
        binding.rvLatest.setAdapter(null);
        binding.rvRecommended.setAdapter(null);
        binding.rvTrending.setAdapter(null);
        binding.rvSeriesPopular.setAdapter(null);
        binding.rvSeriesRecents.setAdapter(null);
        binding.rvNewthisweek.setAdapter(null);
        binding.rvPopular.setAdapter(null);
        binding.rvAnimes.setAdapter(null);
        binding.constraintLayout.removeAllViews();
        binding.scrollView.removeAllViews();
        binding = null;
        bindingHeader = null;

    }

    // Make sure all calls finished before showing results
    private void checkAllDataLoaded() {

        if (mFeaturedLoaded) {
            binding.scrollView.setVisibility(VISIBLE);
            binding.progressBar.setVisibility(View.GONE);
        }

    }


    @Override
    public void onDestroy() {
        if (nativeAdMedia != null) {
            nativeAdMedia.destroy();
            nativeAdMedia = null;
        }

        if (mNativeAd !=null) {

            mNativeAd.destroy();
        }

        if (nativeAd != null) {
            nativeAd.unregisterView();
            nativeAd.destroy();
            nativeAd = null;
        }

        if (adOptionsView !=null) {

            adOptionsView = null;
        }
        super.onDestroy();
    }
}
