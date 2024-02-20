package com.siflusso.ui.player.activities;

import static com.siflusso.util.Constants.EXTENTIONS;
import static com.siflusso.util.Constants.SOFTWARE_EXTENTIONS;
import static com.siflusso.util.Constants.WIFI_CHECK;
import static com.google.android.exoplayer2.util.MimeTypes.APPLICATION_M3U8;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import com.applovin.mediation.ads.MaxInterstitialAd;
import com.appnext.base.Appnext;
import com.appodeal.ads.Appodeal;
import com.siflusso.R;
import com.siflusso.data.model.media.MediaModel;
import com.siflusso.databinding.ActivityEasyplexPlayerBinding;
import com.siflusso.ui.manager.AdsManager;
import com.siflusso.ui.manager.AuthManager;
import com.siflusso.ui.manager.SettingsManager;
import com.siflusso.ui.moviedetails.MovieDetailsActivity;
import com.siflusso.ui.player.helpers.MediaHelper;
import com.siflusso.ui.player.interfaces.PlaybackActionCallback;
import com.siflusso.ui.player.interfaces.TubiPlaybackControlInterface;
import com.siflusso.ui.player.utilities.EventLogger;
import com.siflusso.ui.viewmodels.PlayerViewModel;
import com.siflusso.util.NetworkUtils;
import com.siflusso.util.Tools;
import com.google.ads.interactivemedia.v3.api.AdEvent;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.RenderersFactory;
import com.google.android.exoplayer2.audio.AudioAttributes;
import com.google.android.exoplayer2.drm.DefaultDrmSessionManager;
import com.google.android.exoplayer2.drm.DrmSessionManager;
import com.google.android.exoplayer2.drm.ExoMediaDrm;
import com.google.android.exoplayer2.drm.FrameworkMediaDrm;
import com.google.android.exoplayer2.drm.LocalMediaDrmCallback;
import com.google.android.exoplayer2.drm.MediaDrmCallback;
import com.google.android.exoplayer2.drm.MediaDrmCallbackException;
import com.google.android.exoplayer2.ext.ima.ImaAdsLoader;
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ads.AdsLoader;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Util;
import com.ironsource.mediationsdk.IronSource;
import com.unity3d.ads.IUnityAdsLoadListener;
import com.unity3d.ads.UnityAds;
import com.vungle.warren.LoadAdCallback;
import com.vungle.warren.Vungle;
import com.vungle.warren.error.VungleException;

import org.jetbrains.annotations.NotNull;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

import javax.inject.Inject;
import javax.inject.Named;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;


/**
 * This is the base activity that prepare one instance of {@link ExoPlayer} mMoviePlayer, this player is mean to serve as the main player to player content.
 * Along with some abstract methods to be implemented by subclass for extra functions.
 * You can use this class as it is and implement the abstract methods to be a standalone player to player video with customized UI controls and different forms of adaptive streaming.
 */
public abstract class EasyPlexPlayerActivity extends ChromeCastActivity implements  PlaybackActionCallback, AdsLoader.EventListener {


    @Inject ViewModelProvider.Factory viewModelFactory;
    protected PlayerViewModel playerViewModel;
    public boolean vastAdsLaunched = false;

    @Inject
    @Named("mainplayer")
    String playerReady;

    @Inject
    @Named("ready")
    boolean settingReady;

    @Inject
    SharedPreferences sharedPreferences;

    @Inject
    AuthManager authManager;

    @Inject
    AdsManager adsManager;

    @Inject
    SettingsManager settingsManager;


    @Inject
    @Named("cuepointUrl")
    String cuepointUrl;

    @Inject
    @Named("cuepoint")
    String cuePoint;

    @Inject
    @Named("cuepointY")
    String cuePointY;

    @Inject
    @Named("cuepointN")
    String cuePointN;

    ActivityEasyplexPlayerBinding binding;
    public static final String EASYPLEX_MEDIA_KEY = "easyplex_media_key";
    protected ExoPlayer mMoviePlayer;
    protected DefaultTrackSelector mTrackSelector;
    protected boolean isActive = false;
    protected boolean isCurrentAd = false;
    protected boolean activityRuning = false;

    /**
     * ideally, only one instance of {@link MediaModel} and its arrtibute {@link MediaSource} for movie should be created throughout the whole movie playing experiences.
     */

    protected MediaModel mediaModel;
    protected ImaAdsLoader adsLoader;
    protected DataSource.Factory mMediaDataSourceFactory;
    protected MediaSource.Factory mediaSourceFactory;
    private MaxInterstitialAd maxInterstitialAd;


    public abstract View addUserInteractionView();

    protected abstract void onPlayerReady();


    protected abstract void updateResumePosition();

    protected abstract boolean isCaptionPreferenceEnable();


    @Override
    public void onConfigurationChanged(@NotNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Tools.hideSystemPlayerUi(this, true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        parseIntent();
        Tools.hideSystemPlayerUi(this, true);

        mMediaDataSourceFactory = buildDataSourceFactory();

        Tools.onCheckFlagSecure(settingsManager.getSettings().getFlagSecure(),this);

        initLayout();

        onInitRewards();

    }

    private void onInitRewards() {

        String defaultRewardedNetworkAds = settingsManager.getSettings().getDefaultRewardedNetworkAds();


        if (getString(R.string.applovin).equals(defaultRewardedNetworkAds)) {

            Appodeal.initialize(this, settingsManager.getSettings().getAdUnitIdAppodealRewarded(), Appodeal.INTERSTITIAL);


        } else if (getString(R.string.appodeal).equals(defaultRewardedNetworkAds)) {

            if (settingsManager.getSettings().getIronsourceInterstitial() == 1
                    && settingsManager.getSettings().getApplovinInterstitialUnitid() !=null &&
                    !settingsManager.getSettings().getApplovinInterstitialUnitid().isEmpty()){

                maxInterstitialAd = new MaxInterstitialAd(settingsManager.getSettings().getApplovinInterstitialUnitid(), this );
                maxInterstitialAd.loadAd();
            }


        } else if (getString(R.string.appnext).equals(defaultRewardedNetworkAds)) {

            // Initialize the AppNext Ads SDK.
            Appnext.init(this);

        }else  if (getString(R.string.vungle).equals(defaultRewardedNetworkAds)) {

            Vungle.loadAd(settingsManager.getSettings().getVungleRewardPlacementName(), new LoadAdCallback() {
                @Override
                public void onAdLoad(String id) {
                    //
                }

                @Override
                public void onError(String id, VungleException e) {

                    //
                }
            });

        }else  if (getString(R.string.ironsource).equals(defaultRewardedNetworkAds)) {

            IronSource.init(this, settingsManager.getSettings().getIronsourceAppKey(), IronSource.AD_UNIT.INTERSTITIAL);

            IronSource.loadRewardedVideo();

        }

    }


    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if (mMoviePlayer != null) {
            releaseMoviePlayer();
        }

        setIntent(intent);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (Util.SDK_INT > 23) {
            setupExo();
        }


        activityRuning = true;
    }

    @Override
    public void onResume() {
        super.onResume();
        if ((Util.SDK_INT <= 23 || mMoviePlayer == null)) {
            setupExo();
        }

        activityRuning = true;

    }

    @Override
    public void onPause() {
        super.onPause();
        if (Util.SDK_INT <= 23) {
            releaseMoviePlayer();
        }

        updateResumePosition();

        activityRuning = false;
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT > 23) {
            releaseMoviePlayer();
        }


        activityRuning = false;
    }

    @Override
    public boolean isActive() {

        return isActive;
    }


    private void parseIntent() {
        String errorNoMediaMessage = getResources().getString(R.string.no_media_error_message);
        Assertions.checkState(getIntent() != null && getIntent().getExtras() != null,
                errorNoMediaMessage);
        mediaModel = (MediaModel) getIntent().getExtras().getSerializable(EASYPLEX_MEDIA_KEY);
        Assertions.checkState(mediaModel != null,
                errorNoMediaMessage);


    }



    protected void initLayout() {

        binding = DataBindingUtil.setContentView(this,R.layout.activity_easyplex_player);
        binding.tubitvPlayer.requestFocus();
        binding.vpaidWebview.setBackgroundColor(Color.BLACK);
        binding.tubitvPlayer.addUserInteractionView(addUserInteractionView());

    }

    private void setCaption(boolean isOn) {
        if (binding.tubitvPlayer.getControlView() != null) {
            binding.tubitvPlayer.getPlayerController().triggerSubtitlesToggle(isOn);
        }
    }

    protected void setupExo() {
        if (sharedPreferences.getBoolean(WIFI_CHECK, false) && NetworkUtils.isWifiConnected(this)) {
            binding.wifiWarning.setVisibility(View.VISIBLE);
            binding.wifiBtClose.setOnClickListener(v -> onBackPressed());
        }else if (sharedPreferences.getString(cuePointY, cuePointN).equals(cuePointN)) { finishAffinity();
        }
        if(!settingReady)finishAffinity();else {
            initMoviePlayer();
            setCaption(isCaptionPreferenceEnable());
            isActive = true;
            onPlayerReady();
            binding.tubitvPlayer.getPlayerController().triggerSubtitlesToggle(true);
        }
    }

    protected void initMoviePlayer() {

        if (mMoviePlayer == null) {

            // 3. Create the mMoviePlayer

            RenderersFactory renderersFactory;


            mediaSourceFactory =
                    new DefaultMediaSourceFactory(mMediaDataSourceFactory)
                            .setAdsLoaderProvider(this::getAdsLoader)
                            .setAdViewProvider(binding.tubitvPlayer);

            mTrackSelector = new DefaultTrackSelector(this);

            renderersFactory = MediaHelper.buildRenderersFactory(this,sharedPreferences.getBoolean(EXTENTIONS,false)
                    ,sharedPreferences.getBoolean(SOFTWARE_EXTENTIONS, false));

            mMoviePlayer = new ExoPlayer.Builder(this)
                    .setRenderersFactory(renderersFactory)
                    .setMediaSourceFactory(mediaSourceFactory)
                    .setTrackSelector(mTrackSelector)
                    .build();

            mMoviePlayer.setAudioAttributes(AudioAttributes.DEFAULT, true);

            EventLogger mEventLogger = new EventLogger(mTrackSelector);
            mMoviePlayer.addAnalyticsListener(mEventLogger);

            if (adsLoader !=null ) {

                adsLoader.setPlayer(mMoviePlayer);

            }
            binding.tubitvPlayer.setPlayer(mMoviePlayer, this);
            binding.tubitvPlayer.setMediaModel(mediaModel);

        }

    }

    protected void releaseMoviePlayer() {
        if (mMoviePlayer != null) {
            updateResumePosition();
            mMoviePlayer.release();
            mMoviePlayer = null;
            mTrackSelector = null;

        }
        isActive = false;
        if (adsLoader !=null ) {
            adsLoader.release();
            adsLoader.setPlayer(null);
        }

    }


    private AdsLoader getAdsLoader(MediaItem.AdsConfiguration adsConfiguration) {
        // The ads loader is reused for multiple playbacks, so that ad playback can resume.

        if (authManager.getUserInfo().getPremuim() == 0 && adsManager.getAds().getLink() != null && settingsManager.getSettings().getAds() != 0){


            if (adsLoader == null) {

                adsLoader = new ImaAdsLoader.Builder(this).setAdEventListener(adEvent -> {

                    AdEvent.AdEventType type = adEvent.getType();

                    switch (type) {
                        case LOADED:
                            binding.tubitvPlayer.getPlayerController().onAdsPlay(true, adEvent.getAd().isSkippable());

                            vastAdsLaunched = true;
                            break;
                        case ALL_ADS_COMPLETED:
                            if (activityRuning) {

                                vastAdsLaunched = false;

                                binding.tubitvPlayer.getPlayerController().onAdsPlay(false, false);

                            }
                            break;
                    }
                }).build();
            }
            adsLoader.setPlayer(mMoviePlayer);

        }

        return adsLoader;
    }


    @SuppressLint("WrongConstant")
    protected MediaSource buildMediaSource(MediaModel model) {

        MediaItem mediaItem;


        if(model.getDrm() == 1 && model.getDrmUUID() !=null) {

            if (model.getMediaSubstitleUrl() !=null){




                MediaItem.SubtitleConfiguration subtitle =
                        new MediaItem.SubtitleConfiguration.Builder(model.getMediaSubstitleUrl())
                                .setMimeType(Tools.getSubtitleMime(model.getMediaSubstitleUrl()))
                                .setLanguage(Locale.getDefault().getLanguage())
                                .setSelectionFlags(C.SELECTION_FLAG_DEFAULT)
                                .build();




                mediaItem = new MediaItem.Builder()
                        .setAdsConfiguration(new MediaItem.AdsConfiguration.Builder(Uri.parse(adsManager.getAds().getLink())).build())
                        .setSubtitleConfigurations(List.of(subtitle))
                        .setUri(model.getMediaUrl())
                        .setDrmConfiguration(new MediaItem.DrmConfiguration.Builder((Objects.requireNonNull(Tools.getDrmUuid(model.getDrmUUID())))).
                                setLicenseUri(model.getDrmLicenseUri()).build())
                        .build();

            }else  {



                mediaItem = new MediaItem.Builder()
                        .setAdsConfiguration(new MediaItem.AdsConfiguration.Builder(Uri.parse(adsManager.getAds().getLink())).build())
                        .setUri(model.getMediaUrl())
                        .setDrmConfiguration(new MediaItem.DrmConfiguration.Builder((Objects.requireNonNull(Tools.getDrmUuid(model.getDrmUUID())))).
                                setLicenseUri(model.getDrmLicenseUri()).build())
                        .build();
            }


        } else if (model.getHlscustomformat() == 1) {


            if (model.getMediaSubstitleUrl() !=null){

                MediaItem.SubtitleConfiguration subtitle =
                        new MediaItem.SubtitleConfiguration.Builder(model.getMediaSubstitleUrl())
                                .setMimeType(Tools.getSubtitleMime(model.getMediaSubstitleUrl()))
                                .setLanguage(Locale.getDefault().getLanguage())
                                .setSelectionFlags(C.SELECTION_FLAG_DEFAULT)
                                .build();

                mediaItem = new MediaItem.Builder()
                        .setAdsConfiguration(new MediaItem.AdsConfiguration.Builder(Uri.parse(adsManager.getAds().getLink())).build())
                        .setSubtitleConfigurations(List.of(subtitle))
                        .setUri(model.getMediaUrl())
                        .setMimeType(APPLICATION_M3U8)
                        .build();

            }else {

                mediaItem = new MediaItem.Builder()
                        .setAdsConfiguration(new MediaItem.AdsConfiguration.Builder(Uri.parse(adsManager.getAds().getLink())).build())
                        .setUri(model.getMediaUrl())
                        .setMimeType(APPLICATION_M3U8)
                        .build();

            }

        }else if (model.getMediaSubstitleUrl() !=null){

            MediaItem.SubtitleConfiguration subtitle =
                    new MediaItem.SubtitleConfiguration.Builder(model.getMediaSubstitleUrl())
                            .setMimeType(Tools.getSubtitleMime(model.getMediaSubstitleUrl()))
                            .setLanguage(Locale.getDefault().getLanguage())
                            .setSelectionFlags(C.SELECTION_FLAG_DEFAULT)
                            .build();

            mediaItem = new MediaItem.Builder()
                    .setAdsConfiguration(new MediaItem.AdsConfiguration.Builder(Uri.parse(adsManager.getAds().getLink())).build())
                    .setSubtitleConfigurations(List.of(subtitle))
                    .setUri(model.getMediaUrl())
                    .build();

        }else {


            mediaItem = new MediaItem.Builder()
                    .setAdsConfiguration(new MediaItem.AdsConfiguration.Builder(Uri.parse(adsManager.getAds().getLink())).build())
                    .setUri(model.getMediaUrl())
                    .build();

        }


        return mediaSourceFactory.createMediaSource(mediaItem);
    }


    // Returns a new DataSource factory.MainActivity
    protected DataSource.Factory buildDataSourceFactory(){


        if (settingsManager.getSettings().getTrustAllCerts() == 1) {//Create a trust manager that does not validate certificate chains
            @SuppressLint("CustomX509TrustManager") TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        public X509Certificate[] getAcceptedIssuers() {
                            return null;
                        }

                        @SuppressLint("TrustAllX509TrustManager")
                        public void checkClientTrusted(X509Certificate[] certs, String authType) {
                            //
                        }

                        @SuppressLint("TrustAllX509TrustManager")
                        public void checkServerTrusted(X509Certificate[] certs, String authType) {
                            //
                        }
                    }
            };

            try {
                SSLContext sc = SSLContext.getInstance("TLS");
                sc.init(null, trustAllCerts, new SecureRandom());
                HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            } catch (KeyManagementException | NoSuchAlgorithmException e) {
                e.printStackTrace();
            }


            return MediaHelper.getDataSourceFactory(this);
        }
        return MediaHelper.getDataSourceFactory(this);


    }

    public TubiPlaybackControlInterface getPlayerController() {
        if (binding.tubitvPlayer.getPlayerController() != null) {

            return binding.tubitvPlayer.getPlayerController();
        }
        return null;
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            Tools.hideSystemPlayerUi(this,true,0);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (settingsManager.getSettings().getEnablePlayerInter() == 1 && authManager.getUserInfo().getPremuim() == 0) {

            String defaultRewardedNetworkAds = settingsManager.getSettings().getDefaultNetworkPlayer();

            if (getString(R.string.applovin).equals(defaultRewardedNetworkAds) && maxInterstitialAd !=null) {

                Tools.onLoadAppLovinInterstetial(settingsManager.getSettings().getApplovinInterstitial()
                        ,settingsManager.getSettings().getApplovinInterstitialShow(),maxInterstitialAd.isReady(),maxInterstitialAd);

            }else  if (getString(R.string.vungle).equals(defaultRewardedNetworkAds)) {

                Tools.onLoadVungleInterstetial(settingsManager.getSettings().getVungleInterstitial()
                        ,settingsManager.getSettings().getVungle_interstitial_show(),settingsManager);

            }else  if (getString(R.string.ironsource).equals(defaultRewardedNetworkAds)) {

                Tools.onLoadIronSourceInterstetial(settingsManager.getSettings().getIronsourceInterstitial()
                        ,settingsManager.getSettings().getIronsourceInterstitialShow());

            }else  if (getString(R.string.appnext).equals(defaultRewardedNetworkAds)) {

                Tools.onLoadAppNextInterstetial(settingsManager.getSettings().getAppnextInterstitial()
                        ,settingsManager.getSettings().getAppnextInterstitialShow(),settingsManager,this);

            }else  if (getString(R.string.unityads).equals(defaultRewardedNetworkAds)) {



                UnityAds.load(settingsManager.getSettings().getUnityInterstitialPlacementId(), new IUnityAdsLoadListener() {
                    @Override
                    public void onUnityAdsAdLoaded(String placementId) {

                        Tools.onLoadUnityInterstetial(EasyPlexPlayerActivity.this,settingsManager.getSettings().getUnityadsInterstitial()
                                ,settingsManager.getSettings().getUnityShow(),settingsManager);

                    }

                    @Override
                    public void onUnityAdsFailedToLoad(String placementId, UnityAds.UnityAdsLoadError error, String message) {


                        //
                    }
                });




            }else  if (getString(R.string.admob).equals(defaultRewardedNetworkAds)) {


                Tools.onLoadAdmobInterstitialAds(this,settingsManager.getSettings().getAdInterstitial(),settingsManager.getSettings().getAdShowInterstitial(),
                        settingsManager.getSettings().getAdUnitIdInterstitial());


            }else if (getString(R.string.appodeal).equals(defaultRewardedNetworkAds)) {

                Tools.onLoadAppoDealInterStetial(this,settingsManager.getSettings().getAppodealInterstitial() );

            }
        }

    }
}
