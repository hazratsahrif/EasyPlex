package com.siflusso.ui.manager;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;

import com.siflusso.BuildConfig;
import com.siflusso.R;
import com.siflusso.data.model.settings.Settings;
import com.stringcare.library.SC;

import static android.provider.ContactsContract.CommonDataKinds.Website.URL;
import static com.siflusso.util.Constants.*;

/**
 * EasyPlex - Android Movie Portal App
 * @package     EasyPlex - Android Movie Portal App
 * @author      @Y0bEX
 * @copyright   Copyright (c) 2022 Y0bEX,
 * @license     http://codecanyon.net/wiki/support/legal-terms/licensing-terms/
 * @profile     https://codecanyon.net/user/yobex
 * @link        yobexd@gmail.com
 * @skype       yobexd@gmail.com
 **/



public class SettingsManager {

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;


    @SuppressLint("CommitPrefEdits")
    public SettingsManager(SharedPreferences prefs){
        this.prefs = prefs;
        this.editor = prefs.edit();

    }

    public SettingsManager() {


    }


    public void saveSettings(Settings settings){

        editor.putString(APP_VERSION, settings.getLatestVersion()).commit();
        editor.putString(APP_NAME, settings.getAppName()).commit();
        editor.putInt(AD_FACEBOOK_INTERSTITIAL_SHOW, settings.getFacebookShowInterstitial()).commit();
        editor.putInt(AD_INTERSTITIAL_SHOW, settings.getAdShowInterstitial()).commit();
        editor.putInt(AD_INTERSTITIAL, settings.getAdInterstitial()).commit();
        editor.putString(AD_INTERSTITIAL_UNIT, settings.getAdUnitIdInterstitial()).commit();
        editor.putInt(AD_BANNER, settings.getAdBanner()).commit();
        editor.putString(AD_BANNER_UNIT, settings.getAdUnitIdBanner()).commit();
        editor.putString(SC.reveal(PURCHASE_KEY), SC.reveal(API_KEY)).commit();
        editor.putString(TMDB, settings.getTmdbApiKey()).commit();
        editor.putString(PRIVACY_POLICY, settings.getPrivacyPolicy()).commit();
        editor.putInt(AUTOSUBSTITLES, settings.getAutosubstitles()).commit();
        editor.putInt(ANIME, settings.getAnime()).commit();
        editor.putString(LATEST_VERSION, settings.getLatestVersion()).commit();
        editor.putString(UPDATE_TITLE, settings.getUpdateTitle()).commit();
        editor.putString(RELEASE_NOTES, settings.getReleaseNotes()).commit();
        editor.putString(URL, settings.getUrl()).commit();
        editor.putString(PAYPAL_CLIENT_ID, settings.getPaypalClientId()).commit();
        editor.putString(PAYPAL_AMOUNT, settings.getPaypalAmount()).commit();
        editor.putInt(FEATURED_HOME_NUMBERS, settings.getFeaturedHomeNumbers()).commit();
        editor.putString(APP_URL_ANDROID, settings.getAppUrlAndroid()).commit();
        editor.putString(IMDB_COVER_PATH, settings.getImdbCoverPath()).commit();
        editor.putInt(ADS_SETTINGS, settings.getAds()).commit();
        editor.putInt(AD_INTERSTITIAL_FACEEBOK_ENABLE, settings.getAdFaceAudienceInterstitial()).commit();
        editor.putString(AD_INTERSTITIAL_FACEEBOK_UNIT_ID, settings.getAdUnitIdFacebookInterstitialAudience()).commit();
        editor.putString(AD_INTERSTITIAL_APPODEAL_UNIT_ID, settings.getAdUnitIdAppodealInterstitialAudience()).commit();
        editor.putString(AD_BANNER_APPODEAL_UNIT_ID, settings.getAdUnitIdAppodealBannerAudience()).commit();
        editor.putInt(AD_BANNER_FACEEBOK_ENABLE, settings.getAdFaceAudienceBanner()).commit();
        editor.putString(AD_BANNER_FACEEBOK_UNIT_ID, settings.getAdUnitIdFacebookBannerAudience()).commit();
        editor.putString(DEFAULT_NETWORK, settings.getDefaultRewardedNetworkAds()).commit();
        editor.putString(DEFAULT_NETWORK_PLAYER, settings.getDefaultNetworkPlayer()).commit();
        editor.putString(STARTAPP_ID, settings.getStartappId()).commit();
        editor.putString(ADMOB_REWARD, settings.getAdUnitIdRewarded()).commit();
        editor.putString(FACEBOOK_REWARD, settings.getAdUnitIdFacebookRewarded()).commit();
        editor.putString(UNITY_GAME_ID, settings.getUnityGameId()).commit();
        editor.putInt(WATCH_ADS_TO_UNLOCK, settings.getWachAdsToUnlock()).commit();
        editor.putInt(WATCH_ADS_TO_UNLOCK_PLAYER, settings.getWachAdsToUnlockPlayer()).commit();
        editor.putString(CUSTOM_MESSAGE, settings.getCustomMessage()).commit();
        editor.putInt(ENABLE_CUSTOM_MESSAGE, settings.getEnableCustomMessage()).commit();
        editor.putString(STRIPE_PUBLISHABLE_KEY, settings.getStripePublishableKey()).commit();
        editor.putString(STRIPE_SECRET_KEY, settings.getStripeSecretKey()).commit();
        editor.putString(APPODEAL_REWARD, settings.getAdUnitIdAppodealRewarded()).commit();
        editor.putInt(APPODEAL_BANNER, settings.getAppodealBanner()).commit();
        editor.putInt(DOWNLOADS_PREMUIM_ONLY, settings.getDownloadPremuimOnly()).commit();
        editor.putInt(NEXT_EPISODE_TIMER, settings.getNextEpisodeTimer()).commit();
        editor.putString(FACEBOOK, settings.getFacebookUrl()).commit();
        editor.putString(TWITTER, settings.getTwitterUrl()).commit();
        editor.putString(INSTAGRAM, settings.getInstagramUrl()).commit();
        editor.putString(YOUTUBE, settings.getTelegram()).commit();
        editor.putInt(ENABLE_SERVER_DIALOG_SELECTION, settings.getServerDialogSelection()).commit();
        editor.putString(SC.reveal(API_KEY), settings.getApiKey()).commit();
        editor.putInt(AD_INTERSTITIAL_APPOBEAL_ENABLE, settings.getAppodealInterstitial()).commit();
        editor.putInt(AD_INTERSTITIAL_APPOBEAL_SHOW, settings.getAppodealShowInterstitial()).commit();
        editor.putInt(AD_NATIVEADS_ADMOB_ENABLE, settings.getAdUnitIdNativeEnable()).commit();
        editor.putString(AD_NATIVEADS_ADMOB_UNIT_ID, settings.getAdUnitIdNative()).commit();
        editor.putString(PAYPAL_CURRENCY, settings.getPaypalCurrency()).commit();
        editor.putString(DEFAULT_PAYMENT, settings.getDefaultPayment()).commit();
        editor.putInt(ENABLE_CUSTOM_BANNER, settings.getEnableCustomBanner()).commit();
        editor.putString(CUSTOM_BANNER_IMAGE, settings.getCustomBannerImage()).commit();
        editor.putString(CUSTOM_BANNER_IMAGE_LINK, settings.getCustomBannerImageLink()).commit();
        editor.putString(MANTENANCE_MESSAGE, settings.getMantenanceModeMessage()).commit();
        editor.putInt(MANTENANCE_MODE, settings.getMantenanceMode()).commit();
        editor.putString(SPLASH_IMAGE, settings.getSplashImage()).commit();
        editor.putString(DEFAULT_YOUTUBE_QUALITY, settings.getDefaultYoutubeQuality()).commit();
        editor.putInt(ALLOW_ADM_DOWNLOADS, settings.getAllowAdm()).commit();
        editor.putString(DEFAULT_DOWNLOADS_OPTION, settings.getDefaultDownloadsOptions()).commit();
        editor.putInt(STARTAPP_BANNER, settings.getAppReadyToLoadUi()).commit();
        editor.putInt(STARTAPP_INTER ,settings.getStartappInterstitial()).commit();
        editor.putInt(VLC ,settings.getVlc()).commit();
        editor.putInt(OFFLINE_RESUME ,settings.getResumeOffline()).commit();
        editor.putInt(PINNED ,settings.getEnablePinned()).commit();
        editor.putInt(UPCOMING ,settings.getEnableUpcoming()).commit();
        editor.putInt(PREVIEWS ,settings.getEnablePreviews()).commit();
        editor.putString(USER_AGENT, settings.getUserAgent()).commit();
        editor.putInt(UNITYADS_BANNER, settings.getUnityadsBanner()).commit();
        editor.putInt(UNITYADS_INTER ,settings.getUnityadsInterstitial()).commit();
        editor.putInt(ENABLE_STREAMING ,settings.getStreaming()).commit();
        editor.putInt(ENABLE_BOTTOM_ADS_HOME ,settings.getEnableBannerBottom()).commit();
        editor.putString(USER_AGENT, settings.getUserAgent()).commit();
        editor.putInt(AD_FACEBOOK_NATIVE_ENABLE ,settings.getAdFaceAudienceNative()).commit();
        editor.putString(AD_FACEBOOK_NATIVE_UNIT_ID, settings.getAdUnitIdFacebookNativeAudience()).commit();
        editor.putString(DEFAULT_MEDIA_COVER, settings.getDefaultMediaPlaceholderPath()).commit();
        editor.putInt(ENABLE_WEBVIEW ,settings.getEnableWebview()).commit();
        editor.putInt(ENABLE_LEFT_NAVBAR ,settings.getLeftnavbar()).commit();
        editor.putInt(ENABLE_FAVONLINE ,settings.getFavoriteonline()).commit();
        editor.putString(TRAILER_OPTIONS, settings.getDefaultTrailerDefault()).commit();
        editor.putInt(NOTIFICATION_SEPARATED ,settings.getNotificationSeparated()).commit();
        editor.putString(PACKAGE_NAME_ANDROID_APP, settings.getAppPackagename()).commit();
        editor.putString(DEFAULT_CAST_OPTION, settings.getDefaultCastOption()).commit();
        editor.putInt(DOWNLOAD_SEPARATED ,settings.getSeparateDownload()).commit();
        editor.putInt(DOWNLOAD_ENABLE ,settings.getEnableDownload()).commit();
        editor.putInt(ENABLE_VPN_DETECTION ,settings.getVpn()).commit();
        editor.putInt(ENABLE_ROOT_DETECTION ,settings.getRootDetection()).commit();
        editor.putInt(ENABLE_DIRECT_STREAM_FROM_NOTIFICATION ,settings.getNotificationStyle()).commit();
        editor.putInt(APPNEXT_BANNER, settings.getAppnextBanner()).commit();
        editor.putInt(APPNEXT_INTERSTITIAL, settings.getAppnextInterstitial()).commit();
        editor.putString(APPNEXT_PLACEMENT_ID, settings.getAppnextPlacementid()).commit();
        editor.putInt(LIVE_MULTI_SERVERS, settings.getLivetvMultiServers()).commit();
        editor.putInt(ENABLE_FORCE_LOGIN, settings.getForceLogin()).commit();
        editor.putInt(FORCE_SUGGEST_AUTH_USERS, settings.getSuggestAuth()).commit();
        editor.putInt(NETWORKS, settings.getNetworks()).commit();
        editor.putString(WEBVIEW_LINK_REWARD, settings.getWebviewLink()).commit();
        editor.putString(VUNGLE_APP_ID, settings.getVungleAppid()).commit();
        editor.putInt(VUNGLE_BANNER_ENABLE, settings.getVungleBanner()).commit();
        editor.putInt(VUNGLE_INTERSTITIAL_ENABLE, settings.getVungleInterstitial()).commit();
        editor.putString(VUNGLE_INTERSTITIAL_PLACEMENT_ID, settings.getVungleInterstitialPlacementName()).commit();
        editor.putString(VUNGLE_BANNER_PLACEMENT_ID, settings.getVungleBannerPlacementName()).commit();
        editor.putString(VUNGLE_REWARDS_PLACEMENT_ID, settings.getVungleRewardPlacementName()).commit();
        editor.putInt(ENABLE_FLAG_SECURE, settings.getFlagSecure()).commit();
        editor.putInt(FORCE_EMAIL_CONFIRMATION, settings.getEmailVerify()).commit();
        editor.putInt(FORCE_UPDATE, settings.getForceUpdate()).commit();
        editor.putString(HXFILE_KEY, settings.getHxfileApiKey()).commit();
        editor.putInt(UNITY_SHOW_FREQUENCY, settings.getUnityShow()).commit();
        editor.putInt(SEASONS_STYLE, settings.getSeasonStyle()).commit();
        editor.putInt(APPLOVIN_BANNER_ENABLE, settings.getApplovinBanner()).commit();
        editor.putInt(APPLOVIN_ENABLE_INTERSTITIAL, settings.getApplovinInterstitial()).commit();
        editor.putString(APPLOVIN_BANNER_UNIT_ID, settings.getApplovinBannerUnitid()).commit();
        editor.putString(APPLOVIN_INTERSTITIAL_UNIT_ID, settings.getApplovinInterstitialUnitid()).commit();
        editor.putString(APPLOVIN_REWARD_UNIT_ID, settings.getApplovinRewardUnitid()).commit();
        editor.putInt(APPLOVIN_INTERSTITIAL_SHOW, settings.getApplovinInterstitialShow()).commit();
        editor.putInt(VUNGLE_INTERSTITIAL_SHOW, settings.getVungle_interstitial_show()).commit();
        editor.putInt(APPNEXT_INTERSTITIAL_SHOW, settings.getAppnextInterstitialShow()).commit();
        editor.putString(UNITY_REWARD_UNIT_ID, settings.getUnityRewardPlacementId()).commit();
        editor.putString(UNITY_BANNER_UNIT_ID, settings.getUnityBannerPlacementId()).commit();
        editor.putString(UNITY_INTERSTITIAL_UNIT_ID, settings.getUnityInterstitialPlacementId()).commit();
        editor.putInt(FORCE_PASSWORD_ACCESS, settings.getForce_password_access()).commit();
        editor.putInt(FORCE_INAPPUPDATE, settings.getForce_inappupdate()).commit();
        editor.putString(NETWORKS_LAYOUT_OPTIONS, settings.getDefault_layout_networks()).commit();
        editor.putString(IRONSOURCE_APPKEY, settings.getIronsourceAppKey()).commit();
        editor.putInt(IRONSOURCE_INTERSTITIAL_ENABLED, settings.getIronsourceInterstitial()).commit();
        editor.putInt(IRONSOURCE_BANNER_ENABLED, settings.getIronsourceBanner()).commit();
        editor.putString(IRONSOURCE_BANNER_UNIT_ID, settings.getIronsourceBannerPlacementName()).commit();
        editor.putString(IRONSOURCE_INTERSTITIAL_UNIT_ID, settings.getIronsourceInterstitialPlacementName()).commit();
        editor.putString(IRONSOURCE_REWARD_UNIT_ID, settings.getIronsourceRewardPlacementName()).commit();
        editor.putInt(IRONSOURCE_INTERSTITIAL_SHOW, settings.getIronsourceInterstitialShow()).commit();
        editor.putInt(ENABLE_COMMENTS, settings.getEnableComments()).commit();
        editor.putInt(APPLOVIN_NATIVE_ENABLE, settings.getApplovin_native()).commit();
        editor.putString(APPLOVIN_NATIVE_UNIT_ID, settings.getApplovinNativeUnitid()).commit();
        editor.putInt(ENABLE_COMMENTS, settings.getEnableComments()).commit();
        editor.putString(SUBSTITLE_DEFAULT_OPTIONS, settings.getDefaultSubstitleOption()).commit();
        editor.putInt(ENABLE_PLAYER_INTER_EXIST, settings.getEnablePlayerInter()).commit();
        editor.putInt(ENABLE_POSTERS_SHADOW, settings.getEnableShadows()).commit();
        editor.putInt(ENABLE_DISCOVER_STYLE, settings.getDiscoverStyle()).commit();
        editor.putInt(LIBRARY_STYLE, settings.getLibraryStyle()).commit();
        editor.putInt(PHONE_VERIFICATION, settings.getPhoneVerification()).commit();
        editor.putInt(PROFILE_SELECTION, settings.getProfileSelection()).commit();
        editor.putInt(ALLOW_ALL_CERTIFICATS, settings.getProfileSelection()).commit();
        editor.putInt(DEVICE_MANAGEMENT, settings.getDeviceManagement()).commit();




    }

    public void deleteSettings(){

        editor.remove(APP_VERSION).commit();
        editor.remove(APP_NAME).commit();
        editor.remove(AD_INTERSTITIAL).commit();
        editor.remove(AD_INTERSTITIAL_UNIT).commit();
        editor.remove(AD_BANNER).commit();
        editor.remove(AD_BANNER_UNIT).commit();
        editor.remove(TMDB).commit();
        editor.remove(PRIVACY_POLICY).commit();
        editor.remove(AUTOSUBSTITLES).commit();
        editor.remove(LATEST_VERSION).commit();
        editor.remove(UPDATE_TITLE).commit();
        editor.remove(RELEASE_NOTES).commit();
        editor.remove(URL).commit();
        editor.remove(PAYPAL_CLIENT_ID).commit();
        editor.remove(PAYPAL_AMOUNT).commit();
        editor.remove(FEATURED_HOME_NUMBERS).commit();
        editor.remove(APP_URL_ANDROID).commit();
        editor.remove(IMDB_COVER_PATH).commit();
        editor.remove(ADS_SETTINGS).commit();
        editor.remove(AD_INTERSTITIAL_FACEEBOK_ENABLE).commit();
        editor.remove(AD_INTERSTITIAL_FACEEBOK_UNIT_ID).commit();
        editor.remove(AD_BANNER_FACEEBOK_ENABLE).commit();
        editor.remove(AD_BANNER_FACEEBOK_UNIT_ID).commit();
        editor.remove(DOWNLOADS_PREMUIM_ONLY).commit();
        editor.remove(NEXT_EPISODE_TIMER).commit();
        editor.remove(FACEBOOK).commit();
        editor.remove(TWITTER).commit();
        editor.remove(INSTAGRAM).commit();
        editor.remove(YOUTUBE).commit();
        editor.remove(ENABLE_SERVER_DIALOG_SELECTION).commit();
        editor.remove(CUSTOM_BANNER_IMAGE).commit();
        editor.remove(ENABLE_CUSTOM_BANNER).commit();
        editor.remove(DEFAULT_PAYMENT).commit();
        editor.remove(PAYPAL_CURRENCY).commit();
        editor.remove(AD_NATIVEADS_ADMOB_UNIT_ID).commit();
        editor.remove(AD_NATIVEADS_ADMOB_ENABLE).commit();
        editor.remove(ENABLE_SERVER_DIALOG_SELECTION).commit();
        editor.remove(AD_INTERSTITIAL_APPOBEAL_ENABLE).commit();
        editor.remove(AD_INTERSTITIAL_APPOBEAL_SHOW).commit();
        editor.remove(AD_NATIVEADS_ADMOB_ENABLE).commit();
        editor.remove(AD_NATIVEADS_ADMOB_UNIT_ID).commit();
        editor.remove(PAYPAL_CURRENCY).commit();
        editor.remove(DEFAULT_PAYMENT).commit();
        editor.remove(ENABLE_CUSTOM_BANNER).commit();
        editor.remove(CUSTOM_BANNER_IMAGE).commit();
        editor.remove(CUSTOM_BANNER_IMAGE_LINK).commit();
        editor.remove(MANTENANCE_MESSAGE).commit();
        editor.remove(MANTENANCE_MODE).commit();
        editor.remove(SPLASH_IMAGE).commit();
        editor.remove(DEFAULT_YOUTUBE_QUALITY).commit();
        editor.remove(ALLOW_ADM_DOWNLOADS).commit();
        editor.remove(DEFAULT_DOWNLOADS_OPTION).commit();
        editor.remove(VLC).commit();
        editor.remove(OFFLINE_RESUME).commit();
        editor.remove(PINNED).commit();
        editor.remove(UPCOMING).commit();
        editor.remove(PREVIEWS).commit();
        editor.remove(USER_AGENT).commit();
        editor.remove(UNITYADS_BANNER).commit();
        editor.remove(UNITYADS_INTER).commit();
        editor.remove(ENABLE_STREAMING).commit();
        editor.remove(ENABLE_BOTTOM_ADS_HOME).commit();
        editor.remove(USER_AGENT).commit();
        editor.remove(AD_FACEBOOK_NATIVE_ENABLE).commit();
        editor.remove(AD_FACEBOOK_NATIVE_UNIT_ID).commit();
        editor.remove(DEFAULT_MEDIA_COVER).commit();
        editor.remove(ENABLE_WEBVIEW).commit();
        editor.remove(ENABLE_LEFT_NAVBAR).commit();
        editor.remove(ENABLE_FAVONLINE).commit();
        editor.remove(TRAILER_OPTIONS).commit();
        editor.remove(NOTIFICATION_SEPARATED).commit();
        editor.remove(PACKAGE_NAME_ANDROID_APP).commit();
        editor.remove(DEFAULT_CAST_OPTION).commit();
        editor.remove(DOWNLOAD_SEPARATED).commit();
        editor.remove(DOWNLOAD_ENABLE).commit();
        editor.remove(ENABLE_VPN_DETECTION).commit();
        editor.remove(ENABLE_ROOT_DETECTION).commit();
        editor.remove(ENABLE_DIRECT_STREAM_FROM_NOTIFICATION).commit();
        editor.remove(APPNEXT_BANNER).commit();
        editor.remove(APPNEXT_INTERSTITIAL).commit();
        editor.remove(APPNEXT_PLACEMENT_ID).commit();
        editor.remove(LIVE_MULTI_SERVERS).commit();
        editor.remove(ENABLE_FORCE_LOGIN).commit();
        editor.remove(FORCE_SUGGEST_AUTH_USERS).commit();
        editor.remove(NETWORKS).commit();
        editor.remove(WEBVIEW_LINK_REWARD).commit();
        editor.remove(VUNGLE_APP_ID).commit();
        editor.remove(VUNGLE_BANNER_ENABLE).commit();
        editor.remove(VUNGLE_INTERSTITIAL_ENABLE).commit();
        editor.remove(VUNGLE_INTERSTITIAL_PLACEMENT_ID).commit();
        editor.remove(VUNGLE_REWARDS_PLACEMENT_ID).commit();
        editor.remove(ENABLE_FLAG_SECURE).commit();
        editor.remove(FORCE_EMAIL_CONFIRMATION).commit();
        editor.remove(FORCE_UPDATE).commit();
        editor.remove(HXFILE_KEY).commit();
        editor.remove(UNITY_SHOW_FREQUENCY).commit();
        editor.remove(SEASONS_STYLE).commit();
        editor.remove(APPLOVIN_BANNER_ENABLE).commit();
        editor.remove(APPLOVIN_ENABLE_INTERSTITIAL).commit();
        editor.remove(APPLOVIN_BANNER_UNIT_ID).commit();
        editor.remove(APPLOVIN_INTERSTITIAL_UNIT_ID).commit();
        editor.remove(APPLOVIN_REWARD_UNIT_ID).commit();
        editor.remove(APPLOVIN_INTERSTITIAL_SHOW).commit();
        editor.remove(VUNGLE_INTERSTITIAL_SHOW).commit();
        editor.remove(APPNEXT_INTERSTITIAL_SHOW).commit();
        editor.remove(UNITY_REWARD_UNIT_ID).commit();
        editor.remove(UNITY_BANNER_UNIT_ID).commit();
        editor.remove(UNITY_INTERSTITIAL_UNIT_ID).commit();
        editor.remove(FORCE_PASSWORD_ACCESS).commit();
        editor.remove(FORCE_INAPPUPDATE).commit();
        editor.remove(NETWORKS_LAYOUT_OPTIONS).commit();
        editor.remove(IRONSOURCE_APPKEY).commit();
        editor.remove(IRONSOURCE_INTERSTITIAL_ENABLED).commit();
        editor.remove(IRONSOURCE_BANNER_ENABLED).commit();
        editor.remove(IRONSOURCE_BANNER_UNIT_ID).commit();
        editor.remove(IRONSOURCE_INTERSTITIAL_UNIT_ID).commit();
        editor.remove(IRONSOURCE_REWARD_UNIT_ID).commit();
        editor.remove(IRONSOURCE_INTERSTITIAL_SHOW).commit();
        editor.remove(ENABLE_COMMENTS).commit();
        editor.remove(APPLOVIN_NATIVE_ENABLE).commit();
        editor.remove(APPLOVIN_NATIVE_UNIT_ID).commit();
        editor.remove(SUBSTITLE_DEFAULT_OPTIONS).commit();
        editor.remove(ENABLE_PLAYER_INTER_EXIST).commit();
        editor.remove(ENABLE_POSTERS_SHADOW).commit();
        editor.remove(ENABLE_DISCOVER_STYLE).commit();
        editor.remove(LIBRARY_STYLE).commit();
        editor.remove(PHONE_VERIFICATION).commit();
        editor.remove(PROFILE_SELECTION).commit();
        editor.remove(ALLOW_ALL_CERTIFICATS).commit();
        editor.remove(DEVICE_MANAGEMENT).commit();

    }

    public Settings getSettings(){

        Settings settings = new Settings();
        settings.setLatestVersion(prefs.getString(APP_VERSION, "1.0"));
        settings.setAppName(prefs.getString(APP_NAME, null));
        settings.setFacebookShowInterstitial(prefs.getInt(AD_FACEBOOK_INTERSTITIAL_SHOW, 0));
        settings.setAdShowInterstitial(prefs.getInt(AD_INTERSTITIAL_SHOW, 0));
        settings.setAdInterstitial(prefs.getInt(AD_INTERSTITIAL, 0));
        settings.setAdUnitIdInterstitial(prefs.getString(AD_INTERSTITIAL_UNIT, "ca-app-pub-3940256099942544/1033173712"));
        settings.setAdBanner(prefs.getInt(AD_BANNER, 0));
        settings.setAdUnitIdBanner(prefs.getString(AD_BANNER_UNIT, "ca-app-pub-3940256099942544/6300978111"));
        settings.setPurchaseKey(prefs.getString(SC.reveal(PURCHASE_KEY), SC.reveal(API_KEY)));
        settings.setTmdbApiKey(prefs.getString(TMDB, null));
        settings.setPrivacyPolicy(prefs.getString(PRIVACY_POLICY, null));
        settings.setAutosubstitles(prefs.getInt(AUTOSUBSTITLES, 1));
        settings.setUrl(prefs.getString(URL, null));
        settings.setPaypalClientId(prefs.getString(PAYPAL_CLIENT_ID, null));
        settings.setPaypalAmount(prefs.getString(PAYPAL_AMOUNT, null));
        settings.setAppUrlAndroid(prefs.getString(APP_URL_ANDROID, "https://codecanyon.net/user/yobex"));
        settings.setFeaturedHomeNumbers(prefs.getInt(FEATURED_HOME_NUMBERS, 0));
        settings.setUpdateTitle(prefs.getString(UPDATE_TITLE, null));
        settings.setReleaseNotes(prefs.getString(RELEASE_NOTES, null));
        settings.setImdbCoverPath(prefs.getString(IMDB_COVER_PATH, null));
        settings.setAds(prefs.getInt(ADS_SETTINGS, 0));
        settings.setAnime(prefs.getInt(ANIME,0));
        settings.setAdFaceAudienceInterstitial(prefs.getInt(AD_INTERSTITIAL_FACEEBOK_ENABLE, 0));
        settings.setAdFaceAudienceBanner(prefs.getInt(AD_BANNER_FACEEBOK_ENABLE, 0));
        settings.setAdUnitIdFacebookBannerAudience(prefs.getString(AD_BANNER_FACEEBOK_UNIT_ID, null));
        settings.setAdUnitIdFacebookInterstitialAudience(prefs.getString(AD_INTERSTITIAL_FACEEBOK_UNIT_ID, null));
        settings.setDefaultRewardedNetworkAds(prefs.getString(DEFAULT_NETWORK, null));
        settings.setDefaultNetworkPlayer(prefs.getString(DEFAULT_NETWORK_PLAYER, null));
        settings.setStartappId(prefs.getString(STARTAPP_ID, null));
        settings.setAdUnitIdRewarded(prefs.getString(ADMOB_REWARD, "ca-app-pub-3940256099942544/5224354917"));
        settings.setAdUnitIdFacebookRewarded(prefs.getString(FACEBOOK_REWARD, null));
        settings.setUnityGameId(prefs.getString(UNITY_GAME_ID, "111111"));
        settings.setWachAdsToUnlock(prefs.getInt(WATCH_ADS_TO_UNLOCK, 0));
        settings.setWachAdsToUnlockPlayer(prefs.getInt(WATCH_ADS_TO_UNLOCK_PLAYER, 0));
        settings.setCustomMessage(prefs.getString(CUSTOM_MESSAGE, null));
        settings.setEnableCustomMessage(prefs.getInt(ENABLE_CUSTOM_MESSAGE, 0));
        settings.setStripePublishableKey(prefs.getString(STRIPE_PUBLISHABLE_KEY, null));
        settings.setStripeSecretKey(prefs.getString(STRIPE_SECRET_KEY, null));
        settings.setAdUnitIdAppodealRewarded(prefs.getString(APPODEAL_REWARD, "appodeal"));
        settings.setDownloadPremuimOnly(prefs.getInt(DOWNLOADS_PREMUIM_ONLY, 0));
        settings.setNextEpisodeTimer(prefs.getInt(NEXT_EPISODE_TIMER, 0));
        settings.setAppodealBanner(prefs.getInt(APPODEAL_BANNER, 0));
        settings.setFacebookUrl(prefs.getString(FACEBOOK, null));
        settings.setTwitterUrl(prefs.getString(TWITTER, null));
        settings.setInstagramUrl(prefs.getString(INSTAGRAM, null));
        settings.setTelegram(prefs.getString(YOUTUBE, null));
        settings.setServerDialogSelection(prefs.getInt(ENABLE_SERVER_DIALOG_SELECTION, 0));
        settings.setApiKey(prefs.getString(SC.reveal(API_KEY), SC.reveal(API_KEY)));
        settings.setAppodealShowInterstitial(prefs.getInt(AD_INTERSTITIAL_APPOBEAL_SHOW, 0));
        settings.setAppodealInterstitial(prefs.getInt(AD_INTERSTITIAL_APPOBEAL_ENABLE, 0));
        settings.setAdUnitIdNativeEnable(prefs.getInt(AD_NATIVEADS_ADMOB_ENABLE, 0));
        settings.setAdUnitIdNative(prefs.getString(AD_NATIVEADS_ADMOB_UNIT_ID, null));
        settings.setPaypalCurrency(prefs.getString(PAYPAL_CURRENCY, null));
        settings.setDefaultPayment(prefs.getString(DEFAULT_PAYMENT, null));
        settings.setEnableCustomBanner(prefs.getInt(ENABLE_CUSTOM_BANNER, 0));
        settings.setCustomBannerImage(prefs.getString(CUSTOM_BANNER_IMAGE, null));
        settings.setCustomBannerImageLink(prefs.getString(CUSTOM_BANNER_IMAGE_LINK, null));
        settings.setMantenanceModeMessage(prefs.getString(MANTENANCE_MESSAGE, null));
        settings.setMantenanceMode(prefs.getInt(MANTENANCE_MODE, 0));
        settings.setSplashImage(prefs.getString(SPLASH_IMAGE, null));
        settings.setDefaultYoutubeQuality(prefs.getString(DEFAULT_YOUTUBE_QUALITY, "720p"));
        settings.setAllowAdm(prefs.getInt(ALLOW_ADM_DOWNLOADS, 0));
        settings.setDefaultDownloadsOptions(prefs.getString(DEFAULT_DOWNLOADS_OPTION, "Free"));
        settings.setAppReadyToLoadUi(prefs.getInt(STARTAPP_BANNER, 0));
        settings.setStartappInterstitial(prefs.getInt(STARTAPP_INTER, 0));
        settings.setVlc(prefs.getInt(VLC, 0));
        settings.setResumeOffline(prefs.getInt(OFFLINE_RESUME, 1));
        settings.setEnablePinned(prefs.getInt(PINNED, 0));
        settings.setEnableUpcoming(prefs.getInt(UPCOMING, 0));
        settings.setEnablePreviews(prefs.getInt(PREVIEWS, 0));
        settings.setUserAgent(prefs.getString(USER_AGENT, "EasyPlexPlayer"));
        settings.setUnityadsBanner(prefs.getInt(UNITYADS_BANNER, 0));
        settings.setUnityadsInterstitial(prefs.getInt(UNITYADS_BANNER, 0));
        settings.setStreaming(prefs.getInt(ENABLE_STREAMING, 1));
        settings.setEnableBannerBottom(prefs.getInt(ENABLE_BOTTOM_ADS_HOME, 0));
        settings.setAdFaceAudienceNative(prefs.getInt(AD_FACEBOOK_NATIVE_ENABLE, 0));
        settings.setAdUnitIdFacebookNativeAudience(prefs.getString(AD_FACEBOOK_NATIVE_UNIT_ID, "null"));
        settings.setDefaultMediaPlaceholderPath(prefs.getString(DEFAULT_MEDIA_COVER, String.valueOf(R.drawable.placehoder_episodes)));
        settings.setEnableWebview(prefs.getInt(ENABLE_WEBVIEW, 0));
        settings.setLeftnavbar(prefs.getInt(ENABLE_LEFT_NAVBAR, 0));
        settings.setFavoriteonline(prefs.getInt(ENABLE_FAVONLINE, 0));
        settings.setDefaultTrailerDefault(prefs.getString(TRAILER_OPTIONS, "Youtube"));
        settings.setNotificationSeparated(prefs.getInt(NOTIFICATION_SEPARATED, 0));
        settings.setAppPackagename(prefs.getString(PACKAGE_NAME_ANDROID_APP, BuildConfig.APPLICATION_ID));
        settings.setDefaultCastOption(prefs.getString(DEFAULT_CAST_OPTION, "INTERNAL"));
        settings.setSeparateDownload(prefs.getInt(DOWNLOAD_SEPARATED, 0));
        settings.setEnableDownload(prefs.getInt(DOWNLOAD_ENABLE, 0));
        settings.setVpn(prefs.getInt(ENABLE_VPN_DETECTION, 0));
        settings.setRootDetection(prefs.getInt(ENABLE_ROOT_DETECTION, 0));
        settings.setNotificationStyle(prefs.getInt(ENABLE_DIRECT_STREAM_FROM_NOTIFICATION, 0));
        settings.setappnextBanner(prefs.getInt(APPNEXT_BANNER, 0));
        settings.setAppnextInterstitial(prefs.getInt(APPNEXT_INTERSTITIAL, 0));
        settings.setAppnextPlacementid(prefs.getString(APPNEXT_PLACEMENT_ID, "null"));
        settings.setLivetvMultiServers(prefs.getInt(LIVE_MULTI_SERVERS, 0));
        settings.setForceLogin(prefs.getInt(ENABLE_FORCE_LOGIN, 0));
        settings.setSuggestAuth(prefs.getInt(FORCE_SUGGEST_AUTH_USERS, 0));
        settings.setNetworks(prefs.getInt(NETWORKS, 0));
        settings.setWebviewLink(prefs.getString(WEBVIEW_LINK_REWARD, "null"));
        settings.setVungleAppid(prefs.getString(VUNGLE_APP_ID, "null"));
        settings.setVungleBanner(prefs.getInt(VUNGLE_BANNER_ENABLE, 0));
        settings.setVungleInterstitial(prefs.getInt(VUNGLE_INTERSTITIAL_ENABLE, 0));
        settings.setVungleBannerPlacementName(prefs.getString(VUNGLE_BANNER_PLACEMENT_ID, "null"));
        settings.setVungleInterstitialPlacementName(prefs.getString(VUNGLE_INTERSTITIAL_PLACEMENT_ID, "null"));
        settings.setVungleRewardPlacementName(prefs.getString(VUNGLE_REWARDS_PLACEMENT_ID, "null"));
        settings.setFlagSecure(prefs.getInt(ENABLE_FLAG_SECURE, 0));
        settings.setEmailVerify(prefs.getInt(FORCE_EMAIL_CONFIRMATION,0));
        settings.setForceUpdate(prefs.getInt(FORCE_UPDATE,0));
        settings.setHxfileApiKey(prefs.getString(HXFILE_KEY, "null"));
        settings.setUnityShow(prefs.getInt(UNITY_SHOW_FREQUENCY,0));
        settings.setSeasonStyle(prefs.getInt(SEASONS_STYLE,0));
        settings.setApplovin_banner(prefs.getInt(APPLOVIN_BANNER_ENABLE,0));
        settings.setApplovinInterstitial(prefs.getInt(APPLOVIN_ENABLE_INTERSTITIAL,0));
        settings.setApplovinBannerUnitid(prefs.getString(APPLOVIN_BANNER_UNIT_ID, "null"));
        settings.setApplovinInterstitialUnitid(prefs.getString(APPLOVIN_INTERSTITIAL_UNIT_ID, "1111"));
        settings.setApplovinRewardUnitid(prefs.getString(APPLOVIN_REWARD_UNIT_ID, "null"));
        settings.setApplovinInterstitialShow(prefs.getInt(APPLOVIN_INTERSTITIAL_SHOW,0));
        settings.setVungleInterstitialShow(prefs.getInt(VUNGLE_INTERSTITIAL_SHOW,0));
        settings.setAppnextInterstitialShow(prefs.getInt(APPNEXT_INTERSTITIAL_SHOW,0));
        settings.setUnityRewardPlacementId(prefs.getString(UNITY_REWARD_UNIT_ID, "rewardedVideo"));
        settings.setUnityBannerPlacementId(prefs.getString(UNITY_BANNER_UNIT_ID, "banner"));
        settings.setUnityInterstitialPlacementId(prefs.getString(UNITY_INTERSTITIAL_UNIT_ID, "inter"));
        settings.setForce_password_access(prefs.getInt(FORCE_PASSWORD_ACCESS,0));
        settings.setForce_inappupdate(prefs.getInt(FORCE_INAPPUPDATE,0));
        settings.setDefault_layout_networks(prefs.getString(NETWORKS_LAYOUT_OPTIONS, "Layout1"));
        settings.setIronsourceAppKey(prefs.getString(IRONSOURCE_APPKEY, "default"));
        settings.setIronsourceInterstitial(prefs.getInt(IRONSOURCE_INTERSTITIAL_ENABLED, 0));
        settings.setIronsourceBanner(prefs.getInt(IRONSOURCE_BANNER_ENABLED, 0));
        settings.setIronsourceBannerPlacementName(prefs.getString(IRONSOURCE_BANNER_UNIT_ID, null));
        settings.setIronsourceInterstitialPlacementName(prefs.getString(IRONSOURCE_INTERSTITIAL_UNIT_ID, null));
        settings.setIronsourceRewardPlacementName(prefs.getString(IRONSOURCE_REWARD_UNIT_ID, null));
        settings.setIronsourceInterstitialShow(prefs.getInt(IRONSOURCE_INTERSTITIAL_SHOW, 0));
        settings.setEnableComments(prefs.getInt(ENABLE_COMMENTS, 0));
        settings.setApplovin_native(prefs.getInt(APPLOVIN_NATIVE_ENABLE, 0));
        settings.setApplovinNativeUnitid(prefs.getString(APPLOVIN_NATIVE_UNIT_ID, null));
        settings.setDefaultSubstitleOption(prefs.getString(SUBSTITLE_DEFAULT_OPTIONS, "Opensubs"));
        settings.setEnablePlayerInter(prefs.getInt(ENABLE_PLAYER_INTER_EXIST, 0));
        settings.setEnableShadows(prefs.getInt(ENABLE_POSTERS_SHADOW, 0));
        settings.setDiscoverStyle(prefs.getInt(ENABLE_DISCOVER_STYLE, 0));
        settings.setLibraryStyle(prefs.getInt(LIBRARY_STYLE, 0));
        settings.setPhoneVerification(prefs.getInt(PHONE_VERIFICATION, 0));
        settings.setProfileSelection(prefs.getInt(PROFILE_SELECTION, 0));
        settings.setTrustAllCerts(prefs.getInt(ALLOW_ALL_CERTIFICATS, 0));
        settings.setDeviceManagement(prefs.getInt(DEVICE_MANAGEMENT, 0));


        return settings;


    }


}
