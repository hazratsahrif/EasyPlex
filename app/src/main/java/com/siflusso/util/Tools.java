package com.siflusso.util;

import static android.os.Build.VERSION;
import static android.os.Build.VERSION_CODES;
import static android.provider.Settings.*;
import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.SYSTEM_UI_FLAG_FULLSCREEN;
import static android.view.View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
import static android.view.View.SYSTEM_UI_FLAG_IMMERSIVE;
import static android.view.View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
import static android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
import static android.view.View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
import static android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
import static android.view.View.VISIBLE;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static android.view.WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS;
import static com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions.withCrossFade;
import static com.siflusso.util.Constants.ARG_MOVIE;
import static com.siflusso.util.Constants.FIREBASECONFIG;
import static com.siflusso.util.Constants.PLAYER_HEADER;
import static com.siflusso.util.Constants.PLAYER_USER_AGENT;
import static com.siflusso.util.Constants.SERVER_BASE_URL;
import static com.siflusso.util.Constants.SERVER_ENCODED;
import static com.siflusso.util.Constants.SERVER_FIREBASE_VALUE;
import static com.siflusso.util.Constants.UNITY_ADS_BANNER_HEIGHT;
import static com.siflusso.util.Constants.UNITY_ADS_BANNER_WIDTH;
import static com.siflusso.util.Constants.VLC_INTENT;
import static com.siflusso.util.Constants.VLC_PACKAGE_NAME;
import static com.google.android.exoplayer2.util.MimeTypes.APPLICATION_SUBRIP;
import static com.google.android.gms.ads.AdRequest.Builder;
import static java.lang.Double.isNaN;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Insets;
import android.graphics.drawable.Drawable;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.view.WindowMetrics;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NotificationCompat;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.core.widget.NestedScrollView;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdListener;
import com.applovin.mediation.MaxAdViewAdListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.ads.MaxAdView;
import com.applovin.mediation.ads.MaxInterstitialAd;
import com.applovin.mediation.nativeAds.MaxNativeAdListener;
import com.applovin.mediation.nativeAds.MaxNativeAdLoader;
import com.applovin.mediation.nativeAds.MaxNativeAdView;
import com.appnext.ads.interstitial.Interstitial;
import com.appnext.banners.BannerAdRequest;
import com.appnext.banners.BannerSize;
import com.appodeal.ads.Appodeal;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.easyplex.easyplexsupportedhosts.EasyPlexSupportedHosts;
import com.easyplex.easyplexsupportedhosts.Model.EasyPlexSupportedHostsModel;
import com.siflusso.BuildConfig;
import com.siflusso.R;
import com.siflusso.data.local.entity.Download;
import com.siflusso.data.local.entity.Media;
import com.siflusso.data.model.episode.Episode;
import com.siflusso.data.model.episode.LatestEpisodes;
import com.siflusso.data.model.media.MediaModel;
import com.siflusso.data.model.stream.MediaStream;
import com.siflusso.databinding.ItemAnimeDetailBinding;
import com.siflusso.databinding.ItemMovieDetailBinding;
import com.siflusso.databinding.ItemStreamDetailBinding;
import com.siflusso.databinding.SerieDetailsBinding;
import com.siflusso.ui.animes.AnimeDetailsActivity;
import com.siflusso.ui.base.BaseActivity;
import com.siflusso.ui.login.LoginActivity;
import com.siflusso.ui.manager.SettingsManager;
import com.siflusso.ui.moviedetails.MovieDetailsActivity;
import com.siflusso.ui.player.activities.EasyPlexMainPlayer;
import com.siflusso.ui.player.activities.EasyPlexPlayerActivity;
import com.siflusso.ui.player.cast.ExpandedControlsActivity;
import com.siflusso.ui.player.cast.queue.QueueDataProvider;
import com.siflusso.ui.player.cast.utils.Utils;
import com.siflusso.ui.seriedetails.EpisodeDetailsActivity;
import com.siflusso.ui.seriedetails.SerieDetailsActivity;
import com.siflusso.ui.streaming.StreamingetailsActivity;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.HttpMethod;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.InterstitialAdListener;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.util.Log;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.Util;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdOptions;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.google.android.gms.cast.MediaInfo;
import com.google.android.gms.cast.MediaMetadata;
import com.google.android.gms.cast.MediaQueueItem;
import com.google.android.gms.cast.MediaTrack;
import com.google.android.gms.cast.TextTrackStyle;
import com.google.android.gms.cast.framework.CastContext;
import com.google.android.gms.cast.framework.CastSession;
import com.google.android.gms.cast.framework.media.RemoteMediaClient;
import com.google.android.gms.common.images.WebImage;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.snackbar.Snackbar;
import com.google.common.base.Ascii;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.ibm.icu.text.CharsetDetector;
import com.ibm.icu.text.CharsetMatch;
import com.ironsource.mediationsdk.ISBannerSize;
import com.ironsource.mediationsdk.IronSource;
import com.ironsource.mediationsdk.IronSourceBannerLayout;
import com.ironsource.mediationsdk.adunit.adapter.utility.AdInfo;
import com.ironsource.mediationsdk.logger.IronSourceError;
import com.ironsource.mediationsdk.sdk.LevelPlayBannerListener;
import com.ironsource.mediationsdk.sdk.LevelPlayInterstitialListener;
import com.stringcare.library.SC;
import com.unity3d.ads.IUnityAdsLoadListener;
import com.unity3d.ads.IUnityAdsShowListener;
import com.unity3d.ads.UnityAds;
import com.unity3d.services.banners.BannerErrorInfo;
import com.unity3d.services.banners.BannerView;
import com.unity3d.services.banners.UnityBannerSize;
import com.vungle.warren.AdConfig;
import com.vungle.warren.BannerAdConfig;
import com.vungle.warren.Banners;
import com.vungle.warren.LoadAdCallback;
import com.vungle.warren.PlayAdCallback;
import com.vungle.warren.Vungle;
import com.vungle.warren.VungleBanner;
import com.vungle.warren.error.VungleException;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.System;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;

import me.drakeet.support.toast.ToastCompat;
import timber.log.Timber;


public class Tools {


    public static final String TWITTER_BASE_RUL = "https://twitter.com/";
    public static final String FACEBOOK_BASE_RUL = "https://www.facebook.com/";
    public static final String INSTAGRAM_BASE_RUL = "https://www.instagram.com/";


    private static long exitTime = 0;
    private static int admobInterstitialShow = 0;
    private static int unityInterstitialShow = 0;
    private static int facebookInterstitialShow = 0;
    private static int applovinInterstitialShow = 0;
    private static int vungleInterstitialShow = 0;
    private static int ironsourceInterstitialShow = 0;
    private static int appnextInterstitialShow = 0;


    public static final String SECURE_URI = "secure_uri";
    public static final String USER_AGENT = "User-Agent";
    public static final String VIDEOTYPE = "video/*";
    public static final String TITLE = "title";
    public static final String POSTER = "poster";
    public static final String EXTRA_HEADERS = "android.media.intent.extra.HTTP_HEADERS";
    public static final String HEADERS = "headers";
    public static final String REFER = "Referer";

    private static final String ME_ENDPOINT = "/me";


    private static final String CHANNEL_ID = "CHANNEL_ID";

    private static final int PRELOAD_TIME_S = 2;

    public static String MEDIA_TITLE = "";




    private Tools() {

    }




    public String getDeviceUniqueIdentifier(Context context){

        return Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);

    }



    public static double getAvg(float story,float caracter, float mottionsdes,float musicSeek,boolean isMovie){

        if (isMovie){

            return Double.parseDouble(String.valueOf(((story + caracter + musicSeek) / 3)));


        }else {

            return Double.parseDouble(String.valueOf(((story + caracter + musicSeek + mottionsdes) / 4)));
        }

    }



    public static double calculateAverageRating(float story, float character, float motionDesign, float musicSeek, boolean isMovie) {
        int divisor = isMovie ? 3 : 4; // Determine the divisor based on whether it's a movie or not

        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        double averageRating = (story + character + motionDesign + musicSeek) / divisor;
        String formattedRating = decimalFormat.format(averageRating);

        return Double.parseDouble(formattedRating);
    }

    public static void onShareMedia(Context context, @NotNull Media movieDetail , SettingsManager settingsManager , String type){

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        Bundle extras = new Bundle();

        if ("movie".equals(type)) {

            extras.putString(Intent.EXTRA_TEXT, "Seen " + " '" + movieDetail.getTitle() + "' "
                    + "on " + settingsManager.getSettings().getAppName() + "  yet ?"
                    + Uri.parse("https://" + BuildConfig.APPLICATION_ID + ".app/movies/" + movieDetail.getId()) + " - " +
                    " Install the application via " + Uri.parse(settingsManager.getSettings().getAppUrlAndroid()));
            sendIntent.putExtras(extras);
            sendIntent.setType("text/plain");
            context.startActivity(sendIntent);
        } else if ("serie".equals(type)) {


            extras.putString(Intent.EXTRA_TEXT, "Seen " +  " '" + movieDetail.getName()+"' "
                    + "on " + settingsManager.getSettings().getAppName() +"  yet ?"
                    + Uri.parse("https://" + BuildConfig.APPLICATION_ID +".app/series/"+movieDetail.getId()) + " - " + " Install the application via "+Uri.parse(settingsManager.getSettings().getAppUrlAndroid()));
            sendIntent.putExtras(extras);
            sendIntent.setType("text/plain");
            context.startActivity(sendIntent);

        } else if ("anime".equals(type)) {


            extras.putString(Intent.EXTRA_TEXT, "Seen " +  " '" + movieDetail.getName()+"' "
                    + "on " + settingsManager.getSettings().getAppName() +"  yet ?"
                    + Uri.parse("https://" + BuildConfig.APPLICATION_ID +".app/animes/"+movieDetail.getId()) + " - " + " Install the application via "+Uri.parse(settingsManager.getSettings().getAppUrlAndroid()));
            sendIntent.putExtras(extras);
            sendIntent.setType("text/plain");
            context.startActivity(sendIntent);
        }


    }



    public static void onSetNotification(Context context, Bitmap bitmap, @NotNull Media movieDetail, String title, String message, SettingsManager settingsManager, String type, LatestEpisodes latestEpisodes) {


            // Get default sound for notifications
                    Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            // Create intent based on the type parameter using different activities
                    Intent intent = null;
        if ("0".equals(type)) {
            intent = new Intent(context.getApplicationContext(), MovieDetailsActivity.class);
        } else if ("1".equals(type)) {
            intent = new Intent(context.getApplicationContext(), SerieDetailsActivity.class);
        } else if ("2".equals(type)) {
            intent = new Intent(context.getApplicationContext(), AnimeDetailsActivity.class);
        } else if ("3".equals(type)) {
            intent = new Intent(context.getApplicationContext(), StreamingetailsActivity.class);
        } else if ("episode".equals(type)) {
            intent = new Intent(context.getApplicationContext(), EpisodeDetailsActivity.class);
        } else if ("episode_anime".equals(type)) {
            intent = new Intent(context.getApplicationContext(), EpisodeDetailsActivity.class);
        }

        if ("0".equals(type)) {
            intent.putExtra(ARG_MOVIE, movieDetail);
        } else if ("1".equals(type)) {
            intent.putExtra(ARG_MOVIE, movieDetail);
        } else if ("2".equals(type)) {
            intent.putExtra(ARG_MOVIE, movieDetail);
        } else if ("3".equals(type)) {
            intent.putExtra(ARG_MOVIE, movieDetail);
        } else if ("episode".equals(type)) {
            intent.putExtra(ARG_MOVIE, latestEpisodes);
        }


            // Create PendingIntent with intent and add to task stack
                    TaskStackBuilder stackBuilder = TaskStackBuilder.create(context.getApplicationContext());
                    stackBuilder.addNextIntentWithParentStack(intent);
                    PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(
                            0, Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ? PendingIntent.FLAG_MUTABLE : PendingIntent.FLAG_UPDATE_CURRENT);

            // Create NotificationCompat.Builder object with notification settings
                    NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context.getApplicationContext(), CHANNEL_ID)
                            .setSmallIcon(R.drawable.notification_smal_size)
                            .setContentTitle(title)
                            .setContentText(message)
                            .setAutoCancel(true)
                            .setSound(defaultSoundUri)
                            .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(bitmap))
                            .setContentIntent(resultPendingIntent);


            // Create and set notification channel for devices running Android Oreo and above
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                                settingsManager.getSettings().getAppName(),
                                android.app.NotificationManager.IMPORTANCE_DEFAULT);
                        android.app.NotificationManager notificationManager = (android.app.NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                        notificationManager.createNotificationChannel(channel);
                    }

            // Display notification
                    int notificationId = settingsManager.getSettings().getNotificationSeparated() == 1 ? Tools.createRandomCode(2) : 0;
                    android.app.NotificationManager notificationManager = (android.app.NotificationManager) context.getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.notify(notificationId, notificationBuilder.build());


                }



                public static void loadGlide(Context context, String image, Media movieDetail, String title, String message, SettingsManager settingsManager

                , String type, LatestEpisodes latestEpisodes){

                    final Bitmap[] bitmap = {null};


                    GlideApp.with(context.getApplicationContext())
                            .asBitmap()
                            .load(image)
                            .into(new CustomTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {

                                    bitmap[0] = resource;
                                    Tools.onSetNotification(context.getApplicationContext(),bitmap[0],movieDetail,title,message,settingsManager,type,latestEpisodes);
                                }

                                @Override
                                public void onLoadCleared(@Nullable Drawable placeholder) {

                                    //
                                }

                                @Override
                                public void onLoadFailed(@Nullable Drawable errorDrawable) {

                                    Tools.onSetNotification(context.getApplicationContext(),getBitmapFromURL(context),movieDetail,title,message,settingsManager,type,latestEpisodes);
                                }
                            });
                }

    private static Bitmap getBitmapFromURL(Context context) {

        return BitmapFactory.decodeResource(context.getResources(), R.drawable.placehoder_episodes);
    }

    public static Drawable getImageDrawable (String url ) {
        try {
            Log.d ( "TAG", "Getting Drawable for StringUrl: " + url.toString ( ) );
            return getImageDrawable (String.valueOf(new URL( url )));
        } catch ( MalformedURLException e ) {
            Log.e("TAG",e.getMessage ( ),e);
        } catch ( Exception e ) {
            Log.e("TAG",e.getMessage ( ),e);
        }

        return null;
    }


    public static void onloadBanners(SettingsManager settingsManager, Activity activity, ItemMovieDetailBinding binding,


     SerieDetailsBinding serieDetailsBinding, ItemAnimeDetailBinding itemAnimeDetailBinding, ItemStreamDetailBinding itemStreamDetailBinding) {


        if (settingsManager.getSettings().getAdUnitIdBanner() !=null && settingsManager.getSettings().getAdBanner() == 1) {


            if (activity instanceof MovieDetailsActivity) {

                Tools.onLoadAdmobBanner(activity,binding.adViewContainer,settingsManager.getSettings().getAdUnitIdBanner());

            }else if (activity instanceof SerieDetailsActivity){

                Tools.onLoadAdmobBanner(activity,serieDetailsBinding.adViewContainer,settingsManager.getSettings().getAdUnitIdBanner());

            }else if (activity instanceof AnimeDetailsActivity){


                Tools.onLoadAdmobBanner(activity,itemAnimeDetailBinding.adViewContainer,settingsManager.getSettings().getAdUnitIdBanner());

            }else if (activity instanceof StreamingetailsActivity){


                Tools.onLoadAdmobBanner(activity,itemStreamDetailBinding.adViewContainer,settingsManager.getSettings().getAdUnitIdBanner());

            }


        }

        if (settingsManager.getSettings().getAppodealBanner() == 1) {
            Appodeal.setBannerViewId(R.id.appodealBannerView);
            Appodeal.show(activity, Appodeal.BANNER_VIEW);
        }


        if (settingsManager.getSettings().getUnityadsBanner() == 1) {




            BannerView bottomBanner = new BannerView(activity,
                    settingsManager.getSettings().getUnityBannerPlacementId()
                    , new UnityBannerSize(UNITY_ADS_BANNER_WIDTH, UNITY_ADS_BANNER_HEIGHT));
            bottomBanner.setListener(new BannerView.IListener() {
                @Override
                public void onBannerLoaded(BannerView bannerView) {


                    if (activity instanceof MovieDetailsActivity) {

                        binding.unityBannerViewContainer.setVisibility(VISIBLE);



                    }else if (activity instanceof SerieDetailsActivity){

                        serieDetailsBinding.unityBannerViewContainer.setVisibility(VISIBLE);


                    }else if (activity instanceof AnimeDetailsActivity){

                        itemAnimeDetailBinding.unityBannerViewContainer.setVisibility(VISIBLE);


                    }else if (activity instanceof StreamingetailsActivity){


                        itemStreamDetailBinding.unityBannerViewContainer.setVisibility(VISIBLE);

                    }
                    Timber.d("ready");
                }

                @Override
                public void onBannerClick(BannerView bannerView) {

                    //
                }

                @Override
                public void onBannerShown(BannerView bannerAdView) {
                    //
                }

                @Override
                public void onBannerFailedToLoad(BannerView bannerView, BannerErrorInfo bannerErrorInfo) {
                    Timber.d("Banner Error%s", bannerErrorInfo);
                    if (activity instanceof MovieDetailsActivity) {

                        binding.unityBannerViewContainer.setVisibility(GONE);



                    }else if (activity instanceof SerieDetailsActivity){

                        serieDetailsBinding.unityBannerViewContainer.setVisibility(GONE);


                    }else if (activity instanceof AnimeDetailsActivity){

                        itemAnimeDetailBinding.unityBannerViewContainer.setVisibility(GONE);


                    }else if (activity instanceof StreamingetailsActivity){


                        itemStreamDetailBinding.unityBannerViewContainer.setVisibility(GONE);

                    }
                }

                @Override
                public void onBannerLeftApplication(BannerView bannerView) {

                    bannerView.destroy();
                }
            });



            if (activity instanceof MovieDetailsActivity) {

                binding.unityBannerViewContainer.addView(bottomBanner);


            }else if (activity instanceof SerieDetailsActivity){


                serieDetailsBinding.unityBannerViewContainer.addView(bottomBanner);

            }else if (activity instanceof AnimeDetailsActivity){


                itemAnimeDetailBinding.unityBannerViewContainer.addView(bottomBanner);


            }else if (activity instanceof StreamingetailsActivity){


                itemStreamDetailBinding.unityBannerViewContainer.addView(bottomBanner);

            }

            bottomBanner.load();
        }


        if (settingsManager.getSettings().getAdUnitIdNativeEnable() == 1) {


            AdLoader.Builder builder = new AdLoader.Builder(activity, settingsManager.getSettings().getAdUnitIdNative());

            // OnLoadedListener implementation.
            builder.forNativeAd(
                    nativeAd -> {
                        // If this callback occurs after the activity is destroyed, you must call
                        // destroy and return or you may get a memory leak.
                        boolean isDestroyed;
                        isDestroyed = activity.isDestroyed();
                        if (isDestroyed || activity.isFinishing() || activity.isChangingConfigurations()) {
                            nativeAd.destroy();
                            return;
                        }

                        @SuppressLint("InflateParams") NativeAdView adView =
                                (NativeAdView) activity.getLayoutInflater().inflate(R.layout.ad_unified, null);
                        populateNativeAdView(nativeAd, adView);

                        if (activity instanceof MovieDetailsActivity) {

                            binding.flAdplaceholder.removeAllViews();
                            binding.flAdplaceholder.addView(adView);


                        }else if (activity instanceof SerieDetailsActivity){

                            serieDetailsBinding.flAdplaceholder.removeAllViews();
                            serieDetailsBinding.flAdplaceholder.addView(adView);


                        }else if (activity instanceof AnimeDetailsActivity){

                            itemAnimeDetailBinding.flAdplaceholder.removeAllViews();
                            itemAnimeDetailBinding.flAdplaceholder.addView(adView);


                        }else if (activity instanceof StreamingetailsActivity){


                            itemStreamDetailBinding.flAdplaceholder.removeAllViews();
                            itemStreamDetailBinding.flAdplaceholder.addView(adView);

                        }

                    });

            VideoOptions videoOptions =
                    new VideoOptions.Builder().build();

            NativeAdOptions nativeAdOptions =
                    new NativeAdOptions.Builder().setVideoOptions(videoOptions).build();

            builder.withNativeAdOptions(nativeAdOptions);

            AdLoader adLoader =
                    builder
                            .withAdListener(
                                    new com.google.android.gms.ads.AdListener() {
                                        @Override
                                        public void onAdFailedToLoad(@NotNull LoadAdError loadAdError) {



                                            if (activity instanceof MovieDetailsActivity) {

                                                binding.flAdplaceholder.setVisibility(GONE);


                                            }else if (activity instanceof SerieDetailsActivity){

                                                serieDetailsBinding.flAdplaceholder.setVisibility(GONE);


                                            }else if (activity instanceof AnimeDetailsActivity){

                                                itemAnimeDetailBinding.flAdplaceholder.setVisibility(GONE);


                                            }else if (activity instanceof StreamingetailsActivity){


                                                itemStreamDetailBinding.flAdplaceholder.setVisibility(GONE);


                                            }


                                        }
                                    })
                            .build();

            adLoader.loadAd(new Builder().build());



        }





        if (settingsManager.getSettings().getIronsourceBanner() == 1) {

            IronSourceBannerLayout banner = IronSource.createBanner(activity, ISBannerSize.BANNER);
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(MATCH_PARENT,
                    WRAP_CONTENT);


            if (activity instanceof MovieDetailsActivity) {

                binding.bannerContainerIron.addView(banner, 0, layoutParams);


            }else if (activity instanceof SerieDetailsActivity){

                serieDetailsBinding.bannerContainerIron.addView(banner, 0, layoutParams);


            }else if (activity instanceof AnimeDetailsActivity){

                itemAnimeDetailBinding.bannerContainerIron.addView(banner, 0, layoutParams);


            }else if (activity instanceof StreamingetailsActivity){


                itemStreamDetailBinding.bannerContainerIron.addView(banner, 0, layoutParams);


            }



            banner.setLevelPlayBannerListener(new LevelPlayBannerListener() {
                @Override
                public void onAdLoaded(AdInfo adInfo) {

                    //
                }

                @Override
                public void onAdLoadFailed(IronSourceError ironSourceError) {

                   //
                }

                @Override
                public void onAdClicked(AdInfo adInfo) {
                    //
                }

                @Override
                public void onAdLeftApplication(AdInfo adInfo) {

                    banner.removeBannerListener();

                }

                @Override
                public void onAdScreenPresented(AdInfo adInfo) {
                    //
                }

                @Override
                public void onAdScreenDismissed(AdInfo adInfo) {
                    //
                }

            });

            IronSource.loadBanner(banner, settingsManager.getSettings().getIronsourceBannerPlacementName());
        }



        if (settingsManager.getSettings().getAppnextBanner() == 1) {

            com.appnext.banners.BannerView appNextBanner = new  com.appnext.banners.BannerView(activity);
            appNextBanner.setPlacementId(settingsManager.getSettings().getAppnextPlacementid());
            appNextBanner.setBannerSize(BannerSize.BANNER);
            if (activity instanceof MovieDetailsActivity) {

                binding.bannerAppNext.loadAd(new BannerAdRequest());


            }else if (activity instanceof SerieDetailsActivity){

                serieDetailsBinding.bannerAppNext.loadAd(new BannerAdRequest());

            }else if (activity instanceof AnimeDetailsActivity){

                itemAnimeDetailBinding.bannerAppNext.loadAd(new BannerAdRequest());

            }else if (activity instanceof StreamingetailsActivity){

                itemStreamDetailBinding.bannerAppNext.loadAd(new BannerAdRequest());

            }


        }



        if (settingsManager.getSettings().getVungleBanner() == 1) {

            final BannerAdConfig bannerAdConfig = new BannerAdConfig();
            bannerAdConfig.setAdSize(AdConfig.AdSize.BANNER);

            if (Banners.canPlayAd(settingsManager.getSettings().getVungleBannerPlacementName(), bannerAdConfig.getAdSize())) {

                VungleBanner vungleBanner = Banners.getBanner(settingsManager.getSettings().getVungleBannerPlacementName(), bannerAdConfig, new PlayAdCallback() {
                    @Override
                    public void creativeId(String creativeId) {
                        //

                    }

                    @Override
                    public void onAdStart(String placementId) {
                        //
                    }

                    @Override
                    public void onAdEnd(String placementId, boolean completed, boolean isCTAClicked) {
                        //
                    }

                    @Override
                    public void onAdEnd(String placementId) {
                        //
                    }

                    @Override
                    public void onAdClick(String placementId) {
                        //
                    }

                    @Override
                    public void onAdRewarded(String placementId) {
                        //
                    }

                    @Override
                    public void onAdLeftApplication(String placementId) {
                        //
                    }

                    @Override
                    public void onError(String placementId, VungleException exception) {

                        //
                    }

                    @Override
                    public void onAdViewed(String placementId) {
                        //
                    }
                });

                if (activity instanceof MovieDetailsActivity) {

                    binding.VungleBannerContainerIron.addView(vungleBanner);


                }else if (activity instanceof SerieDetailsActivity){

                    serieDetailsBinding.VungleBannerContainerIron.addView(vungleBanner);

                }else if (activity instanceof AnimeDetailsActivity){

                    itemAnimeDetailBinding.VungleBannerContainerIron.addView(vungleBanner);

                }else if (activity instanceof StreamingetailsActivity){


                    itemStreamDetailBinding.VungleBannerContainerIron.addView(vungleBanner);

                }
            }
        }



        if (settingsManager.getSettings().getApplovinBanner() == 1) {

           MaxAdView maxAdView = new MaxAdView(settingsManager.getSettings().getApplovinBannerUnitid(),activity);
            if (activity instanceof MovieDetailsActivity) {

                binding.maxAdView.addView(maxAdView);


            }else if (activity instanceof SerieDetailsActivity){

                serieDetailsBinding.maxAdView.addView(maxAdView);

            }else if (activity instanceof AnimeDetailsActivity){

                itemAnimeDetailBinding.maxAdView.addView(maxAdView);

            }else if (activity instanceof StreamingetailsActivity){

                itemStreamDetailBinding.maxAdView.addView(maxAdView);
            }

            maxAdView.loadAd();

            maxAdView.setListener(new MaxAdViewAdListener() {
                @Override
                public void onAdLoaded(MaxAd ad) {
                    //
                }

                @Override
                public void onAdDisplayed(MaxAd ad) {
                    //
                }

                @Override
                public void onAdHidden(MaxAd ad) {
                    //
                }

                @Override
                public void onAdClicked(MaxAd ad) {
                    //
                }

                @Override
                public void onAdLoadFailed(String adUnitId, MaxError error) {
                    //
                }

                @Override
                public void onAdDisplayFailed(MaxAd ad, MaxError error) {
                    //
                }

                @Override
                public void onAdExpanded(MaxAd ad) {
                    //
                }

                @Override
                public void onAdCollapsed(MaxAd ad) {
                    //
                }
            });
            maxAdView.setRevenueListener(ad -> {

            });
        }



        if (settingsManager.getSettings().getApplovin_native() == 1 && settingsManager.getSettings().getApplovinNativeUnitid() !=null){

            MaxNativeAdLoader nativeAdLoader = new MaxNativeAdLoader(settingsManager.getSettings().getApplovinNativeUnitid(), activity );
            nativeAdLoader.setNativeAdListener(new MaxNativeAdListener()
            {
                @Override
                public void onNativeAdLoaded(final MaxNativeAdView nativeAdView, final MaxAd ad)
                {
                    // Clean up any pre-existing native ad to prevent memory leaks.
                    if ( ad != null )
                    {
                        nativeAdLoader.destroy(ad);
                    }


                    // Add ad view to view.
                    if (activity instanceof MovieDetailsActivity) {

                        binding.maxNativeAds.removeAllViews();
                        binding.maxNativeAds.addView(nativeAdView);

                    }else if (activity instanceof SerieDetailsActivity){

                        serieDetailsBinding.maxNativeAds.removeAllViews();
                        serieDetailsBinding.maxNativeAds.addView(nativeAdView);
                    }else if (activity instanceof AnimeDetailsActivity){


                        itemAnimeDetailBinding.maxNativeAds.removeAllViews();
                        itemAnimeDetailBinding.maxNativeAds.addView(nativeAdView);


                    }else if (activity instanceof StreamingetailsActivity){


                        itemStreamDetailBinding.maxNativeAds.removeAllViews();
                        itemStreamDetailBinding.maxNativeAds.addView(nativeAdView);
                    }


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

    private static void populateNativeAdView(NativeAd nativeAd, NativeAdView adView) {

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
        ((TextView) Objects.requireNonNull(adView.getHeadlineView())).setText(nativeAd.getHeadline());
        Objects.requireNonNull(adView.getMediaView()).setMediaContent(Objects.requireNonNull(nativeAd.getMediaContent()));

        // These assets aren't guaranteed to be in every NativeAd, so it's important to
        // check before trying to display them.
        if (nativeAd.getBody() == null) {
            Objects.requireNonNull(adView.getBodyView()).setVisibility(INVISIBLE);
        } else {
            Objects.requireNonNull(adView.getBodyView()).setVisibility(VISIBLE);
            ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
        }

        if (nativeAd.getCallToAction() == null) {
            Objects.requireNonNull(adView.getCallToActionView()).setVisibility(INVISIBLE);
        } else {
            Objects.requireNonNull(adView.getCallToActionView()).setVisibility(VISIBLE);
            ((Button) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
        }

        if (nativeAd.getIcon() == null) {
            Objects.requireNonNull(adView.getIconView()).setVisibility(GONE);
        } else {
            ((ImageView) Objects.requireNonNull(adView.getIconView())).setImageDrawable(
                    nativeAd.getIcon().getDrawable());
            adView.getIconView().setVisibility(VISIBLE);
        }

        if (nativeAd.getPrice() == null) {
            Objects.requireNonNull(adView.getPriceView()).setVisibility(INVISIBLE);
        } else {
            Objects.requireNonNull(adView.getPriceView()).setVisibility(VISIBLE);
            ((TextView) adView.getPriceView()).setText(nativeAd.getPrice());
        }

        if (nativeAd.getStore() == null) {
            Objects.requireNonNull(adView.getStoreView()).setVisibility(INVISIBLE);
        } else {
            Objects.requireNonNull(adView.getStoreView()).setVisibility(VISIBLE);
            ((TextView) adView.getStoreView()).setText(nativeAd.getStore());
        }

        if (nativeAd.getStarRating() == null) {
            Objects.requireNonNull(adView.getStarRatingView()).setVisibility(INVISIBLE);
        } else {
            ((RatingBar) Objects.requireNonNull(adView.getStarRatingView()))
                    .setRating(nativeAd.getStarRating().floatValue());
            adView.getStarRatingView().setVisibility(VISIBLE);
        }

        if (nativeAd.getAdvertiser() == null) {
            Objects.requireNonNull(adView.getAdvertiserView()).setVisibility(INVISIBLE);
        } else {
            ((TextView) Objects.requireNonNull(adView.getAdvertiserView())).setText(nativeAd.getAdvertiser());
            adView.getAdvertiserView().setVisibility(VISIBLE);
        }

        // This method tells the Google Mobile Ads SDK that you have finished populating your
        // native ad view with this native ad.
        adView.setNativeAd(nativeAd);
    }

    public static void onLoadNetworksInter(SettingsManager settingsManager, Activity activity, MaxInterstitialAd maxInterstitialAd){


        Tools.onLoadAdmobInterstitialAds(activity,settingsManager.getSettings().getAdInterstitial(),settingsManager.getSettings().getAdShowInterstitial(),
                settingsManager.getSettings().getAdUnitIdInterstitial());

            Tools.onLoadAppoDealInterStetial(activity,settingsManager.getSettings().getAppodealInterstitial() );

            Tools.onLoadIronSourceInterstetial(settingsManager.getSettings().getIronsourceInterstitial()
                    ,settingsManager.getSettings().getIronsourceInterstitialShow());

            Tools.onLoadAppNextInterstetial(settingsManager.getSettings().getAppnextInterstitial()
                    ,settingsManager.getSettings().getAppnextInterstitialShow(),settingsManager,activity);


            Tools.onLoadVungleInterstetial(settingsManager.getSettings().getVungleInterstitial()
                    ,settingsManager.getSettings().getVungle_interstitial_show(),settingsManager);


            if (settingsManager.getSettings().getApplovinInterstitialUnitid() !=null && !settingsManager.getSettings().getApplovinInterstitialUnitid().isEmpty() &&
                    maxInterstitialAd !=null) {

                Tools.onLoadAppLovinInterstetial(settingsManager.getSettings().getApplovinInterstitial()
                        ,settingsManager.getSettings().getApplovinInterstitialShow(),maxInterstitialAd.isReady(),maxInterstitialAd);
            }


        UnityAds.load(settingsManager.getSettings().getUnityInterstitialPlacementId(), new IUnityAdsLoadListener() {
            @Override
            public void onUnityAdsAdLoaded(String placementId) {

                Tools.onLoadUnityInterstetial(activity,settingsManager.getSettings().getUnityadsInterstitial()
                        ,settingsManager.getSettings().getUnityShow(),settingsManager);

            }

            @Override
            public void onUnityAdsFailedToLoad(String placementId, UnityAds.UnityAdsLoadError error, String message) {


                //
            }
        });

    }



    public static String getApiUrl(){

        if (FIREBASECONFIG) {

            String apiUrl = FirebaseRemoteConfig.getInstance().getString(SERVER_FIREBASE_VALUE);
            if (!apiUrl.isEmpty()){
                return apiUrl;
            }else {

                return SERVER_BASE_URL;
            }

        }else {

            return SERVER_BASE_URL;
        }
    }


    public static String encodeMainApiServer(String url){
        byte[] valueDecoded;
        valueDecoded = Base64.encode(SC.reveal(url).getBytes(StandardCharsets.UTF_8), Base64.DEFAULT);
        return new String(valueDecoded);
    }


    public static String getMediaType(int mediatype) {
        switch (mediatype) {
            case 0:
                return MimeTypes.APPLICATION_MPD;
            case 2:
                return MimeTypes.APPLICATION_M3U8;
            case 1:
                return MimeTypes.APPLICATION_SS;
            default:
                return null;
        }
    }


    public static @Nullable UUID getDrmUuid(@Nullable String drmScheme) {
        switch (Ascii.toLowerCase(Objects.requireNonNull(drmScheme))) {
            case "widevine":
                return C.WIDEVINE_UUID;
            case "playready":
                return C.PLAYREADY_UUID;
            case "clearkey":
                return C.CLEARKEY_UUID;
            default:
                try {
                    return UUID.fromString(drmScheme);
                } catch (RuntimeException e) {
                    return null;
                }
        }
    }


    public static void ToastHelper(Context context , String message){

        if (android.os.Build.VERSION.SDK_INT == 25) {
            ToastCompat.makeText(context, message, Toast.LENGTH_SHORT)
                    .setBadTokenListener(toast -> Timber.e("Failed to toast")).show();
        } else {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }
    }

    public static boolean isRTL(Locale locale) {

        final int directionality = Character.getDirectionality(locale.getDisplayName().charAt(0));
        return directionality == Character.DIRECTIONALITY_RIGHT_TO_LEFT ||
                directionality == Character.DIRECTIONALITY_RIGHT_TO_LEFT_ARABIC;
    }

    public static String getFormatDate(Context context , String postTime1) {
        Calendar cal=Calendar.getInstance();
        Date now=cal.getTime();
        String disTime="";
        try {
            Date postTime;
            //2018-09-05T06:40:46.183Z
            postTime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").parse(postTime1);

            long diff=(now.getTime()-postTime.getTime()+18000)/1000;

            //for months
            Calendar calObj = Calendar.getInstance();
            calObj.setTime(postTime);
            int m=calObj.get(Calendar.MONTH);
            calObj.setTime(now);

            SimpleDateFormat monthFormatter = new SimpleDateFormat("MM"); // output month

            int mNow = Integer.parseInt(monthFormatter.format(postTime));

            diff = diff-19800;

            if(diff<15) { //below 15 sec

                disTime = context.getResources().getString(R.string.lbl_justnow);
            } else if(diff<60) {

                //below 1 min
                disTime= diff+" "+context.getResources().getString(R.string.lbl_seconds_ago);
            } else if(diff<3600) {//below 1 hr

                // convert min
                long temp=diff/60;

                if(temp==1) {
                    disTime= temp + " " +context.getResources().getString(R.string.lbl_min_ago);
                } else {
                    disTime = temp  + " " +context.getResources().getString(R.string.lbl_mins_ago);
                }
            } else if(diff<(24*3600)) {// below 1 day

                // convert hr
                long temp= diff/3600;
                System.out.println("hey temp3:"+temp);
                if(temp==1) {
                    disTime = temp  + " " +context.getResources().getString(R.string.lbl_hr_ago);
                } else {
                    disTime = temp + " " +context.getResources().getString(R.string.lbl_hrs_ago);
                }
            } else if(diff<(24*3600*7)) {// below week

                // convert days
                long temp=diff/(3600*24);
                if (temp==1) {
                    //  disTime = "\nyesterday";
                    disTime = temp + " " +context.getResources().getString(R.string.lbl_day_ago);
                } else {
                    disTime = temp + " " +context.getResources().getString(R.string.lbl_days_ago);
                }
            } else if(diff<((24*3600*28))) {// below month

                // convert week
                long temp=diff/(3600*24*7);
                if (temp <= 4) {

                    if (temp < 1) {
                        disTime = context.getResources().getString(R.string.lbl_lstweek_ago);
                    }else{
                        disTime = temp + " " + context.getResources().getString(R.string.lbl_week_ago);
                    }

                } else {
                    int diffMonth = mNow - m;
                    Timber.e(String.valueOf(diffMonth));
                    disTime = diffMonth + " " + context.getResources().getString(R.string.lbl_month_ago);
                }
            }else if(diff<((24*3600*365))) {// below year

                // convert month
                long temp=diff/(3600*24*30);

                System.out.println("hey temp2:"+temp);
                if (temp <= 12) {

                    if (temp == 1) {
                        disTime = context.getResources().getString(R.string.lbl_onemonth_ago);
                    }else{
                        disTime = temp + " " + context.getResources().getString(R.string.lbl_month_ago);
                    }
                }

            }else if(diff>((24*3600*365))) { // above year

                // convert year
                long temp=diff/(3600*24*30*12);

                System.out.println("hey temp8:"+temp);

                if (temp == 1) {
                    disTime = context.getResources().getString(R.string.lbl_oneyear_ago);
                }else{
                    disTime = temp + " " + context.getResources().getString(R.string.lbl_year_ago);
                }
            }

        } catch(Exception e) {
            e.printStackTrace();
        }

        return disTime;
    }

    public static <T> T nextElement(List<T> list, T element){
        int nextIndex=list.indexOf(element)+1;
        return list.size()<nextIndex?null:list.get(nextIndex);

    }


    public static void makeUserRequest(GraphRequest.Callback callback) {
        Bundle params = new Bundle();
        params.putString("fields", "picture,name,id,email,permissions");

        GraphRequest request =
                new GraphRequest(
                        AccessToken.getCurrentAccessToken(), ME_ENDPOINT, params, HttpMethod.GET, callback);
        request.executeAsync();
    }



    public static MediaTrack buildTrack(long id, String link ,String name) {

        TextTrackStyle textTrackStyle =  new TextTrackStyle();
        textTrackStyle.setBackgroundColor(Color.BLUE);
        textTrackStyle.setWindowColor(Color.GREEN);
        textTrackStyle.setForegroundColor(Color.RED);
        textTrackStyle.setEdgeColor(Color.YELLOW);


       return new MediaTrack.Builder(id, MediaTrack.SUBTYPE_SUBTITLES)
                .setName(name)
                .setSubtype(MediaTrack.SUBTYPE_SUBTITLES)
                .setContentId(link)
                .setLanguage("en-US")
                .build();
    }

    public static void streamFromChromcast(Context context, Media movieDetail, String mediaGenre, String mediaUrl, CardView playButtonIcon) {


        MediaMetadata movieMetadata = new MediaMetadata(MediaMetadata.MEDIA_TYPE_MOVIE);
        movieMetadata.putString(MediaMetadata.KEY_TITLE, movieDetail.getTitle());
        movieMetadata.putString(MediaMetadata.KEY_SUBTITLE, mediaGenre);
        movieMetadata.addImage(new WebImage(Uri.parse(movieDetail.getPosterPath())));

        List<MediaTrack> tracks = new ArrayList<>();


        for (int i = 0; i < movieDetail.getSubstitles().size(); i++) {

            //tracks.add(Tools.buildTrack(i+1,movieDetail.getSubstitles().get(i).getLink(),movieDetail.getSubstitles().get(i).getLang()));

            MediaTrack englishSubtitle = new MediaTrack.Builder(i+1 /* ID */,
                    MediaTrack.TYPE_TEXT)
                    .setName(movieDetail.getSubstitles().get(i).getLang())
                    .setSubtype(MediaTrack.SUBTYPE_SUBTITLES)
                    .setContentId(movieDetail.getSubstitles().get(i).getLink())
                    /* language is required for subtitle type but optional otherwise */
                    .setLanguage("en-US")
                    .setContentType(APPLICATION_SUBRIP)
                    .build();


            tracks.add(englishSubtitle);


        }



        MediaInfo mediaInfo = new MediaInfo.Builder(mediaUrl)
                .setStreamType(MediaInfo.STREAM_TYPE_INVALID)
                .setMetadata(movieMetadata)
                .setMediaTracks(tracks)
                .build();





        CastSession castSession = CastContext.getSharedInstance(context).getSessionManager().getCurrentCastSession();
        if (castSession == null || !castSession.isConnected()) {
            Timber.tag("TAG").w("showQueuePopup(): not connected to a cast device");
            return;
        }
        final RemoteMediaClient remoteMediaClient = castSession.getRemoteMediaClient();
        if (remoteMediaClient == null) {
            Timber.tag("TAG").w("showQueuePopup(): null RemoteMediaClient");
            return;
        }


        TextTrackStyle textTrackStyle =  new TextTrackStyle();
        textTrackStyle.setBackgroundColor(Color.BLUE);
        textTrackStyle.setWindowColor(Color.GREEN);
        textTrackStyle.setForegroundColor(Color.RED);
        textTrackStyle.setEdgeColor(Color.YELLOW);

        remoteMediaClient.setTextTrackStyle(textTrackStyle)
                .setResultCallback(mediaChannelResult -> {
                    if (!mediaChannelResult.getStatus().isSuccess()) {
                        Log.e("TAG", "Failed to set the style, status code: " +
                                mediaChannelResult.getStatus().getStatusCode());
                    }
                });


        final QueueDataProvider provider = QueueDataProvider.getInstance(context);
        PopupMenu popup = new PopupMenu(context, playButtonIcon);
        popup.getMenuInflater().inflate(
                provider.isQueueDetached() || provider.getCount() == 0
                        ? R.menu.detached_popup_add_to_queue
                        : R.menu.popup_add_to_queue, popup.getMenu());
        PopupMenu.OnMenuItemClickListener clickListener = menuItem -> {
            QueueDataProvider provider1 = QueueDataProvider.getInstance(context);
            MediaQueueItem queueItem = new MediaQueueItem.Builder(mediaInfo).setAutoplay(
                    true).setPreloadTime(PRELOAD_TIME_S).build();
            MediaQueueItem[] newItemArray = new MediaQueueItem[]{queueItem};
            String toastMessage = null;
            if (provider1.isQueueDetached() && provider1.getCount() > 0) {
                if ((menuItem.getItemId() == R.id.action_play_now)
                        || (menuItem.getItemId() == R.id.action_add_to_queue)) {
                    MediaQueueItem[] items = Utils
                            .rebuildQueueAndAppend(provider1.getItems(), queueItem);
                    remoteMediaClient.queueLoad(items, provider1.getCount(),
                            0, null);
                } else {
                    return false;
                }
            } else {
                if (provider1.getCount() == 0) {
                    remoteMediaClient.queueLoad(newItemArray, 0,
                            0, null);
                } else {
                    int currentId = provider1.getCurrentItemId();
                    if (menuItem.getItemId() == R.id.action_play_now) {
                        remoteMediaClient.queueInsertAndPlayItem(queueItem, currentId, null);
                    } else if (menuItem.getItemId() == R.id.action_play_next) {
                        int currentPosition = provider1.getPositionByItemId(currentId);
                        if (currentPosition == provider1.getCount() - 1) {
                            //we are adding to the end of queue
                            remoteMediaClient.queueAppendItem(queueItem, null);
                        } else {
                            int nextItemId = provider1.getItem(currentPosition + 1).getItemId();
                            remoteMediaClient.queueInsertItems(newItemArray, nextItemId, null);
                        }
                        toastMessage = context.getString(
                                R.string.queue_item_added_to_play_next);
                    } else if (menuItem.getItemId() == R.id.action_add_to_queue) {
                        remoteMediaClient.queueAppendItem(queueItem, null);
                        toastMessage = context.getString(R.string.queue_item_added_to_queue);
                    } else {
                        return false;
                    }
                }
            }
            if (menuItem.getItemId() == R.id.action_play_now) {
                Intent intent = new Intent(context, ExpandedControlsActivity.class);
                context.startActivity(intent);
            }

            if (menuItem.getItemId() == R.id.action_play_web_caster) {

                streamMediaFromMxWebcast(context,mediaUrl,movieDetail,null);
            }
            if (!TextUtils.isEmpty(toastMessage)) {
                Toast.makeText(context, toastMessage, Toast.LENGTH_SHORT).show();
            }
            return true;
        };
        popup.setOnMenuItemClickListener(clickListener);
        popup.show();
    }


    public static void startLiveStreaming(Context context, Media movieDetail, String link, MediaStream mediaStream) {

        String artwork = movieDetail.getPosterPath();
        String name = movieDetail.getName();
        String type = "streaming";

        Intent intent = new Intent(context, EasyPlexMainPlayer.class);
        intent.putExtra(EasyPlexPlayerActivity.EASYPLEX_MEDIA_KEY, MediaModel.media(movieDetail.getId(),
                null,null,type, name, link, artwork, null
                , null, null,null,
                null,null,
                null,
                null,null,movieDetail.getVip(),movieDetail.getHls(),null,null,
                null,0,0,null,null,0,mediaStream.getDrmuuid(),
                mediaStream.getDrmlicenceuri(),mediaStream.getDrm()));
        intent.putExtra(ARG_MOVIE, movieDetail);
        context.startActivity(intent);

    }



    public static void onLoadAppoDealInterStetial(Activity activity,int enabled) {
          if (enabled == 1) {
            Appodeal.show(activity, Appodeal.INTERSTITIAL);
        }

    }



    public static void startMainStream(Context context, Media movieDetail, String url, String server, String mediaGenre,
                                       MediaStream mediaStream){


        Intent intent = new Intent(context, EasyPlexMainPlayer.class);
        intent.putExtra(EasyPlexPlayerActivity.EASYPLEX_MEDIA_KEY,
                MediaModel.media(movieDetail.getId(),
                        null,server,"0", movieDetail.getTitle(),
                        url, movieDetail.getBackdropPath(), null, null
                        , null,null,null,null,
                        null,
                        null,null,
                        movieDetail.getPremuim(),mediaStream.getHls(),movieDetail.getSubstype(), movieDetail.getImdbExternalId()
                        ,movieDetail.getPosterPath(),movieDetail.getHasrecap(),movieDetail.getSkiprecapStartIn(),mediaGenre,null,movieDetail.getVoteAverage(),
                        mediaStream.getDrmuuid(),
                        mediaStream.getDrmlicenceuri(),mediaStream.getDrm()));
        intent.putExtra(ARG_MOVIE, movieDetail);
        context.startActivity(intent);


    }







    public static void startMainStreamStreaming(Context context, Media movieDetail, String url, int hls, MediaStream mediaStream){

        String artwork = movieDetail.getPosterPath();
        String name = movieDetail.getName();
        String type = "streaming";

        Intent intent = new Intent(context, EasyPlexMainPlayer.class);
        intent.putExtra(EasyPlexPlayerActivity.EASYPLEX_MEDIA_KEY, MediaModel.media(movieDetail.getId(),
                null,null,type, name, url, artwork, null
                , null, null,null,
                null,null,
                null,
                null,null,movieDetail.getPremuim(),hls,null,null,
                null,0,0,null,null,0,mediaStream.getDrmuuid(),
                mediaStream.getDrmlicenceuri(),mediaStream.getDrm()));
        intent.putExtra(ARG_MOVIE, movieDetail);
        context.startActivity(intent);

    }


    public static int createRandomCode(int codeLength) {
        char[] chars = "1234567890".toCharArray();
        StringBuilder sb = new StringBuilder();
        Random random = new SecureRandom();
        for (int i = 0; i < codeLength; i++) {
            char c = chars[random.nextInt(chars.length)];
            sb.append(c);
        }
        return Integer.parseInt(sb.toString());
    }



    public static void streamEpisodeFromMxWebcast(Context context, String url, Episode episode, SettingsManager settingsManager) {

        Intent shareVideo = new Intent(Intent.ACTION_VIEW);
        shareVideo.setDataAndType(Uri.parse(url), VIDEOTYPE);
        shareVideo.setPackage("com.instantbits.cast.webvideo");
        shareVideo.putExtra(TITLE, episode.getName());
        shareVideo.putExtra(POSTER, episode.getStillPath());
        Bundle headers = new Bundle();
        headers.putString(USER_AGENT, settingsManager.getSettings().getAppName());
        shareVideo.putExtra(EXTRA_HEADERS, headers);
        shareVideo.putExtra(HEADERS, headers);
        shareVideo.putExtra(SECURE_URI, true);
        try {
            context.startActivity(shareVideo);
        } catch (ActivityNotFoundException ex) {

            Intent intent = new Intent(Intent.ACTION_VIEW);
            String uriString = "market://details?id=com.instantbits.cast.webvideo";
            intent.setData(Uri.parse(uriString));
            context.startActivity(intent);
        }
    }



    public static void streamLatestEpisodeFromMxPlayer(Context context, String url, LatestEpisodes episode, SettingsManager settingsManager) {

        String name = "S0" + episode.getSeasonNumber() + "E" + episode.getEpisodeNumber() + " : " + episode.getEpisodeName();

        Intent shareVideo = new Intent(Intent.ACTION_VIEW);
        shareVideo.setDataAndType(Uri.parse(url), VIDEOTYPE);
        shareVideo.setPackage("com.mxtech.videoplayer.ad");
        shareVideo.putExtra(TITLE, name);
        shareVideo.putExtra(POSTER, episode.getStillPath());
        Bundle headers = new Bundle();
        headers.putString(USER_AGENT, settingsManager.getSettings().getAppName());
        shareVideo.putExtra(EXTRA_HEADERS, headers);
        shareVideo.putExtra(HEADERS, headers);
        shareVideo.putExtra(SECURE_URI, true);
        try {
            context.startActivity(shareVideo);
        } catch (ActivityNotFoundException ex) {

            Intent intent = new Intent(Intent.ACTION_VIEW);
            String uriString = "market://details?id=com.mxtech.videoplayer.ad" ;
            intent.setData(Uri.parse(uriString));
            context.startActivity(intent);
        }
    }


    public static void streamLatestEpisodeFromVlc(Context context, String url, LatestEpisodes episode, SettingsManager settingsManager) {

        String name = "S0" + episode.getSeasonNumber() + "E" + episode.getEpisodeNumber() + " : " + episode.getEpisodeName();

        Intent shareVideo = new Intent(Intent.ACTION_VIEW);
        shareVideo.setDataAndType(Uri.parse(url), VIDEOTYPE);
        shareVideo.setPackage(VLC_PACKAGE_NAME);
        shareVideo.putExtra(TITLE, name);
        shareVideo.putExtra(POSTER, episode.getStillPath());
        Bundle headers = new Bundle();
        headers.putString(USER_AGENT, settingsManager.getSettings().getAppName());
        shareVideo.putExtra(EXTRA_HEADERS, headers);
        shareVideo.putExtra(HEADERS, headers);
        shareVideo.putExtra(SECURE_URI, true);
        try {
            context.startActivity(shareVideo);
        } catch (ActivityNotFoundException ex) {

            Intent intent = new Intent(Intent.ACTION_VIEW);
            String uriString = VLC_INTENT ;
            intent.setData(Uri.parse(uriString));
            context.startActivity(intent);
        }
    }







    public static void streamLatestEpisodeFromMxWebcast(Context context, String url, LatestEpisodes episode, SettingsManager settingsManager) {

        String name = "S0" + episode.getSeasonNumber() + "E" + episode.getEpisodeNumber() + " : " + episode.getEpisodeName();

        Intent shareVideo = new Intent(Intent.ACTION_VIEW);
        shareVideo.setDataAndType(Uri.parse(url), VIDEOTYPE);
        shareVideo.setPackage("com.instantbits.cast.webvideo");
        shareVideo.putExtra(TITLE, name);
        shareVideo.putExtra(POSTER, episode.getStillPath());
        Bundle headers = new Bundle();
        headers.putString(USER_AGENT, settingsManager.getSettings().getAppName());
        shareVideo.putExtra(EXTRA_HEADERS, headers);
        shareVideo.putExtra(HEADERS, headers);
        shareVideo.putExtra(SECURE_URI, true);
        try {
            context.startActivity(shareVideo);
        } catch (ActivityNotFoundException ex) {

            Intent intent = new Intent(Intent.ACTION_VIEW);
            String uriString = "market://details?id=com.instantbits.cast.webvideo";
            intent.setData(Uri.parse(uriString));
            context.startActivity(intent);
        }
    }



    public static void streamEpisodeFromMxPlayer(Context context, String url, Episode episode, SettingsManager settingsManager) {


        Intent shareVideo = new Intent(Intent.ACTION_VIEW);
        shareVideo.setDataAndType(Uri.parse(url), VIDEOTYPE);
        shareVideo.setPackage("com.mxtech.videoplayer.ad");
        shareVideo.putExtra(TITLE, episode.getName());
        shareVideo.putExtra(POSTER, episode.getStillPath());
        Bundle headers = new Bundle();
        headers.putString(USER_AGENT, settingsManager.getSettings().getAppName());
        shareVideo.putExtra(EXTRA_HEADERS, headers);
        shareVideo.putExtra(HEADERS, headers);
        shareVideo.putExtra(SECURE_URI, true);
        try {
            context.startActivity(shareVideo);
        } catch (ActivityNotFoundException ex) {

            Intent intent = new Intent(Intent.ACTION_VIEW);
            String uriString = "market://details?id=com.mxtech.videoplayer.ad" ;
            intent.setData(Uri.parse(uriString));
            context.startActivity(intent);
        }
    }


    public static void streamEpisodeFromVlc(Context context, String url, Episode episode, SettingsManager settingsManager) {

        Intent shareVideo = new Intent(Intent.ACTION_VIEW);
        shareVideo.setDataAndType(Uri.parse(url), VIDEOTYPE);
        shareVideo.setPackage(VLC_PACKAGE_NAME);
        shareVideo.putExtra(TITLE, episode.getName());
        shareVideo.putExtra(POSTER, episode.getStillPath());
        Bundle headers = new Bundle();
        headers.putString(USER_AGENT, settingsManager.getSettings().getAppName());
        shareVideo.putExtra(EXTRA_HEADERS, headers);
        shareVideo.putExtra(HEADERS, headers);
        shareVideo.putExtra(SECURE_URI, true);
        try {
            context.startActivity(shareVideo);
        } catch (ActivityNotFoundException ex) {

            Intent intent = new Intent(Intent.ACTION_VIEW);
            String uriString = VLC_INTENT ;
            intent.setData(Uri.parse(uriString));
            context.startActivity(intent);
        }
    }


    public static void streamMediaFromVlc(Context context, String url, Media media, SettingsManager settingsManager) {

        if (media.getTitle() !=null && !media.getTitle().isEmpty()) {


            MEDIA_TITLE = media.getTitle();

        }else {

            MEDIA_TITLE = media.getName();
        }




        Intent shareVideo = new Intent(Intent.ACTION_VIEW);
        shareVideo.setDataAndType(Uri.parse(url), VIDEOTYPE);
        shareVideo.setPackage(VLC_PACKAGE_NAME);
        shareVideo.putExtra(TITLE, MEDIA_TITLE);
        shareVideo.putExtra(POSTER, media.getBackdropPath());
        Bundle headers = new Bundle();
        headers.putString(USER_AGENT, PLAYER_USER_AGENT);
        headers.putString(REFER, PLAYER_HEADER);
        shareVideo.putExtra(EXTRA_HEADERS, headers);
        shareVideo.putExtra(HEADERS, headers);
        shareVideo.putExtra(SECURE_URI, true);
        try {
            context.startActivity(shareVideo);
        } catch (ActivityNotFoundException ex) {

            Intent intent = new Intent(Intent.ACTION_VIEW);
            String uriString = VLC_INTENT ;
            intent.setData(Uri.parse(uriString));
            context.startActivity(intent);
        }
    }

    public static void streamMediaFromVlc(Context context, String url, Media media, SettingsManager settingsManager, MediaStream mediaStream) {

        if (media.getTitle() !=null && !media.getTitle().isEmpty()) {


            MEDIA_TITLE = media.getTitle();

        }else {

            MEDIA_TITLE = media.getName();
        }


        if (mediaStream !=null && mediaStream.getHeader() !=null && !mediaStream.getHeader().isEmpty()) {

            PLAYER_HEADER = mediaStream.getHeader();
        }


        if (mediaStream !=null && mediaStream.getUseragent() !=null && !mediaStream.getUseragent().isEmpty()) {

            PLAYER_USER_AGENT = mediaStream.getUseragent();
        }

        Intent shareVideo = new Intent(Intent.ACTION_VIEW);
        shareVideo.setDataAndType(Uri.parse(url), VIDEOTYPE);
        shareVideo.setPackage(VLC_PACKAGE_NAME);
        shareVideo.putExtra(TITLE, MEDIA_TITLE);
        shareVideo.putExtra(POSTER, media.getBackdropPath());
        Bundle headers = new Bundle();
        headers.putString(USER_AGENT, PLAYER_USER_AGENT);
        headers.putString(REFER, PLAYER_HEADER);
        shareVideo.putExtra(EXTRA_HEADERS, headers);
        shareVideo.putExtra(REFER, PLAYER_HEADER);
        shareVideo.putExtra(HEADERS, headers);
        shareVideo.putExtra(SECURE_URI, true);
        try {
            context.startActivity(shareVideo);
        } catch (ActivityNotFoundException ex) {

            Intent intent = new Intent(Intent.ACTION_VIEW);
            String uriString = VLC_INTENT ;
            intent.setData(Uri.parse(uriString));
            context.startActivity(intent);
        }
    }



    public static void streamMediaFromMxPlayer(Context context, String url, Media media, SettingsManager settingsManager, MediaStream mediaStream) {

        if (media.getTitle() !=null && !media.getTitle().isEmpty()) {


            MEDIA_TITLE = media.getTitle();

        }else {

            MEDIA_TITLE = media.getName();
        }

        Intent shareVideo = new Intent(Intent.ACTION_VIEW);
        shareVideo.setDataAndType(Uri.parse(url), VIDEOTYPE);
        shareVideo.setPackage("com.mxtech.videoplayer.ad");
        shareVideo.putExtra(TITLE, MEDIA_TITLE);
        shareVideo.putExtra(POSTER, media.getBackdropPath());
        Bundle headers = new Bundle();
        headers.putString(USER_AGENT, settingsManager.getSettings().getAppName());
        headers.putString(REFER, PLAYER_HEADER);
        shareVideo.putExtra(EXTRA_HEADERS, headers);
        shareVideo.putExtra(HEADERS, headers);
        shareVideo.putExtra(REFER, PLAYER_HEADER);
        shareVideo.putExtra(SECURE_URI, true);
        try {
            context.startActivity(shareVideo);
        } catch (ActivityNotFoundException ex) {

            Intent intent = new Intent(Intent.ACTION_VIEW);
            String uriString = "market://details?id=com.mxtech.videoplayer.ad" ;
            intent.setData(Uri.parse(uriString));
            context.startActivity(intent);
        }
    }


    public static void streamMediaFromMxWebcast(Context context, String url, Media media, SettingsManager settingsManager, MediaStream mediaStream) {


        if (media.getTitle() !=null && !media.getTitle().isEmpty()) {


            MEDIA_TITLE = media.getTitle();

        }else {

            MEDIA_TITLE = media.getName();
        }

        Intent shareVideo = new Intent(Intent.ACTION_VIEW);
        shareVideo.setDataAndType(Uri.parse(url), VIDEOTYPE);
        shareVideo.setPackage("com.instantbits.cast.webvideo");
        shareVideo.putExtra(TITLE, MEDIA_TITLE);
        shareVideo.putExtra(POSTER, media.getBackdropPath());
        Bundle headers = new Bundle();
        headers.putString(USER_AGENT, settingsManager.getSettings().getAppName());
        shareVideo.putExtra(EXTRA_HEADERS, headers);
        shareVideo.putExtra(HEADERS, headers);
        shareVideo.putExtra(SECURE_URI, true);
        try {
            context.startActivity(shareVideo);
        } catch (ActivityNotFoundException ex) {

            Intent intent = new Intent(Intent.ACTION_VIEW);
            String uriString = "market://details?id=com.instantbits.cast.webvideo";
            intent.setData(Uri.parse(uriString));
            context.startActivity(intent);
        }
    }




    public static void streamMediaFromVlc(Context context, String url, Download media, SettingsManager settingsManager) {


        if (media.getTitle() !=null && !media.getTitle().isEmpty()) {


            MEDIA_TITLE = media.getTitle();

        }else {

            MEDIA_TITLE = media.getName();
        }

        Intent shareVideo = new Intent(Intent.ACTION_VIEW);
        shareVideo.setDataAndType(Uri.parse(url), VIDEOTYPE);
        shareVideo.setPackage(VLC_PACKAGE_NAME);
        shareVideo.putExtra(TITLE, MEDIA_TITLE);
        shareVideo.putExtra(POSTER, media.getBackdropPath());
        Bundle headers = new Bundle();
        headers.putString(USER_AGENT, settingsManager.getSettings().getAppName());
        shareVideo.putExtra(EXTRA_HEADERS, headers);
        shareVideo.putExtra(HEADERS, headers);
        shareVideo.putExtra(SECURE_URI, true);
        try {
            context.startActivity(shareVideo);
        } catch (ActivityNotFoundException ex) {

            Intent intent = new Intent(Intent.ACTION_VIEW);
            String uriString = VLC_INTENT ;
            intent.setData(Uri.parse(uriString));
            context.startActivity(intent);
        }
    }



    public static void streamMediaFromMxPlayer(Context context, String url, Download media, SettingsManager settingsManager) {

        if (media.getTitle() !=null && !media.getTitle().isEmpty()) {


            MEDIA_TITLE = media.getTitle();

        }else {

            MEDIA_TITLE = media.getName();
        }

        Intent shareVideo = new Intent(Intent.ACTION_VIEW);
        shareVideo.setDataAndType(Uri.parse(url), VIDEOTYPE);
        shareVideo.setPackage("com.mxtech.videoplayer.ad");
        shareVideo.putExtra(TITLE, MEDIA_TITLE);
        shareVideo.putExtra(POSTER, media.getBackdropPath());
        Bundle headers = new Bundle();
        headers.putString(USER_AGENT, settingsManager.getSettings().getAppName());
        shareVideo.putExtra(EXTRA_HEADERS, headers);
        shareVideo.putExtra(HEADERS, headers);
        shareVideo.putExtra(SECURE_URI, true);
        try {
            context.startActivity(shareVideo);
        } catch (ActivityNotFoundException ex) {

            Intent intent = new Intent(Intent.ACTION_VIEW);
            String uriString = "market://details?id=com.mxtech.videoplayer.ad" ;
            intent.setData(Uri.parse(uriString));
            context.startActivity(intent);
        }
    }


    public static void streamMediaFromMxWebcast(Context context, String url, Media media, SettingsManager settingsManager) {

        if (media.getTitle() !=null && !media.getTitle().isEmpty()) {


            MEDIA_TITLE = media.getTitle();

        }else {

            MEDIA_TITLE = media.getName();
        }

        Intent shareVideo = new Intent(Intent.ACTION_VIEW);
        shareVideo.setDataAndType(Uri.parse(url), VIDEOTYPE);
        shareVideo.setPackage("com.instantbits.cast.webvideo");
        shareVideo.putExtra(TITLE, MEDIA_TITLE);
        shareVideo.putExtra(POSTER, media.getBackdropPath());
        Bundle headers = new Bundle();
        headers.putString(USER_AGENT, settingsManager.getSettings().getAppName());
        shareVideo.putExtra(EXTRA_HEADERS, headers);
        shareVideo.putExtra(HEADERS, headers);
        shareVideo.putExtra(SECURE_URI, true);
        try {
            context.startActivity(shareVideo);
        } catch (ActivityNotFoundException ex) {

            Intent intent = new Intent(Intent.ACTION_VIEW);
            String uriString = "market://details?id=com.instantbits.cast.webvideo";
            intent.setData(Uri.parse(uriString));
            context.startActivity(intent);
        }
    }


    public static void downloadFromAdm(Context context, String url, boolean secureUri, Media media, SettingsManager settingsManager, Episode episode, boolean isEpisode) {


        if (isEpisode) {

            MEDIA_TITLE = episode.getName();

        }else  if (media.getTitle() !=null && !media.getTitle().isEmpty()) {


            MEDIA_TITLE = media.getTitle();

        }else {

            MEDIA_TITLE = media.getName();
        }


        Intent shareVideo = new Intent(Intent.ACTION_VIEW);
        shareVideo.setDataAndType(Uri.parse(url), VIDEOTYPE);
        shareVideo.setPackage("com.dv.adm");
        shareVideo.putExtra(TITLE, MEDIA_TITLE);
        Bundle headers = new Bundle();
        headers.putString(USER_AGENT, settingsManager.getSettings().getAppName());
        shareVideo.putExtra(EXTRA_HEADERS, headers);
        shareVideo.putExtra(HEADERS, headers);
        shareVideo.putExtra(SECURE_URI, secureUri);
        try {
            context.startActivity(shareVideo);
        } catch (ActivityNotFoundException ex) {
            // Open Play Store if it fails to launch the app because the package doesn't exist.
            // Alternatively you could use PackageManager.getLaunchIntentForPackage() and check for null.
            // You could try catch this and launch the Play Store website if it fails but this shouldn’t
            // fail unless the Play Store is missing.

            try {
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.dv.adm")));
            } catch (ActivityNotFoundException anfe) {
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.dv.adm")));
            }

        }
    }

    public static void downloadFrom1dm(Context context, String url, boolean secureUri, Media media, SettingsManager settingsManager, Episode episode, boolean isEpisode) {

        if (isEpisode) {

            MEDIA_TITLE = episode.getName();

        }else  if (media.getTitle() !=null && !media.getTitle().isEmpty()) {


            MEDIA_TITLE = media.getTitle();

        }else {

            MEDIA_TITLE = media.getName();
        }

        Intent shareVideo = new Intent(Intent.ACTION_VIEW);
        shareVideo.setDataAndType(Uri.parse(url), "video/*");
        shareVideo.setPackage("idm.internet.download.manager");
        shareVideo.putExtra("title", MEDIA_TITLE);
        Bundle headers = new Bundle();
        headers.putString(USER_AGENT, settingsManager.getSettings().getAppName());
        shareVideo.putExtra("android.media.intent.extra.HTTP_HEADERS", headers);
        shareVideo.putExtra(HEADERS, headers);
        shareVideo.putExtra(SECURE_URI, secureUri);
        try {
            context.startActivity(shareVideo);
        } catch (ActivityNotFoundException ex) {
            // Open Play Store if it fails to launch the app because the package doesn't exist.
            // Alternatively you could use PackageManager.getLaunchIntentForPackage() and check for null.
            // You could try catch this and launch the Play Store website if it fails but this shouldn’t
            // fail unless the Play Store is missing.

            try {
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=idm.internet.download.manager")));
            } catch (ActivityNotFoundException anfe) {
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=idm.internet.download.manager")));
            }

        }
    }

    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }




    public static String getSubtitleType(String type) {

        switch (type) {
            case "ssa":
            case "ass":
                return MimeTypes.TEXT_SSA;
            case "vtt":
                return MimeTypes.TEXT_VTT;
            case "ttml":
            case "xml":
            case "dfxp":
                return MimeTypes.APPLICATION_TTML;
            default:
                return APPLICATION_SUBRIP;
        }
    }

    public static String getSubtitleMime(Uri uri) {
        final String path = uri.getPath();
        if (path.endsWith(".ssa") || path.endsWith(".ass")) {
            return MimeTypes.TEXT_SSA;
        } else if (path.endsWith(".vtt")) {
            return MimeTypes.TEXT_VTT;
        } else if (path.endsWith(".ttml") ||  path.endsWith(".xml") || path.endsWith(".dfxp")) {
            return MimeTypes.APPLICATION_TTML;
        } else {
            return APPLICATION_SUBRIP;
        }
    }

    public static Uri convertToUTF(Context context, Uri subtitleUri) {


        try {
            final CharsetDetector detector = new CharsetDetector();
            final BufferedInputStream bufferedInputStream = new BufferedInputStream(context.getContentResolver().openInputStream(subtitleUri));
            detector.setText(bufferedInputStream);
            final CharsetMatch charsetMatch = detector.detect();

            if (!StandardCharsets.UTF_8.displayName().equals(charsetMatch.getName())) {
                String filename = subtitleUri.getPath();
                filename = filename.substring(filename.lastIndexOf("/") + 1);
                final File file = new File(context.getCacheDir(), filename);
                final BufferedReader bufferedReader = new BufferedReader(charsetMatch.getReader());
                final BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
                char[] buffer = new char[512];
                int num;
                int pass = 0;
                boolean success = true;
                while ((num = bufferedReader.read(buffer)) != -1) {
                    bufferedWriter.write(buffer, 0, num);
                    pass++;
                    if (pass * 512 > 2_000_000) {
                        success = false;
                        break;
                    }
                }
                bufferedWriter.close();
                bufferedReader.close();
                if (success) {
                    subtitleUri = Uri.fromFile(file);
                } else {
                    subtitleUri = null;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return subtitleUri;


    }


    public static void onLoadAdmobBanner(Activity activity, FrameLayout frameLayout, String adUnitIdBanner) {

        AdSize adSize = getAdSize(activity,frameLayout);
        // Create an ad request.
        AdView mAdView = new AdView(activity);
        mAdView.setAdUnitId(adUnitIdBanner);
        frameLayout.removeAllViews();
        frameLayout.addView(mAdView);
        mAdView.setAdSize(adSize);

        AdRequest adRequest = new Builder().build();
        // Start loading the ad in the background.
        mAdView.loadAd(adRequest);

    }



    public static void onLoadAppLovinInterstetial(int unityEnableInterstitial, int unityShowInterstitial, boolean unityAdsIsReady, MaxInterstitialAd maxInterstitialAd) {

        applovinInterstitialShow += 1;

        if (unityEnableInterstitial == 1 && unityShowInterstitial <= applovinInterstitialShow && unityAdsIsReady) {

            maxInterstitialAd.showAd();
            maxInterstitialAd.setListener(new MaxAdListener() {
                @Override
                public void onAdLoaded(MaxAd ad) {

                    //
                }

                @Override
                public void onAdDisplayed(MaxAd ad) {

                    applovinInterstitialShow = 0;

                }

                @Override
                public void onAdHidden(MaxAd ad) {
                    //
                }

                @Override
                public void onAdClicked(MaxAd ad) {
                    //
                }

                @Override
                public void onAdLoadFailed(String adUnitId, MaxError error) {
                    //
                }

                @Override
                public void onAdDisplayFailed(MaxAd ad, MaxError error) {
                    //
                }
            });

        }


    }




    public static void onLoadIronSourceInterstetial(int unityEnableInterstitial, int unityShowInterstitial) {


        ironsourceInterstitialShow += 1;

        if (unityEnableInterstitial == 1 && unityShowInterstitial <= ironsourceInterstitialShow) {

            IronSource.loadInterstitial();

            IronSource.setLevelPlayInterstitialListener(new LevelPlayInterstitialListener() {
                @Override
                public void onAdReady(AdInfo adInfo) {

                    IronSource.showInterstitial();
                }

                @Override
                public void onAdLoadFailed(IronSourceError ironSourceError) {

                }

                @Override
                public void onAdOpened(AdInfo adInfo) {

                    ironsourceInterstitialShow = 0;
                }

                @Override
                public void onAdShowSucceeded(AdInfo adInfo) {

                    //
                }

                @Override
                public void onAdShowFailed(IronSourceError ironSourceError, AdInfo adInfo) {
                    //
                }

                @Override
                public void onAdClicked(AdInfo adInfo) {
                    //
                }

                @Override
                public void onAdClosed(AdInfo adInfo) {
                    //
                }

            });

        }


    }



    public static void onLoadVungleInterstetial(int unityEnableInterstitial, int unityShowInterstitial, SettingsManager settingsManager) {

        vungleInterstitialShow += 1;

        if (unityEnableInterstitial == 1 && unityShowInterstitial <= vungleInterstitialShow) {

            Vungle.loadAd(settingsManager.getSettings().getVungleInterstitialPlacementName(), new LoadAdCallback() {
                @Override
                public void onAdLoad(String id) {

                    Vungle.playAd(settingsManager.getSettings().getVungleInterstitialPlacementName(), new AdConfig(), new PlayAdCallback() {
                        @Override
                        public void onAdStart(String placementReferenceID) {

                            vungleInterstitialShow = 0;
                        }

                        @Override
                        public void onAdViewed(String placementReferenceID) {

                            //
                        }

                        // Deprecated
                        @Override
                        public void onAdEnd(String id, boolean completed, boolean isCTAClicked) {

                            //
                        }

                        @Override
                        public void onAdEnd(String placementReferenceID) {
                            //
                        }

                        @Override
                        public void onAdClick(String placementReferenceID) {

                            //
                        }

                        @Override
                        public void onAdRewarded(String placementReferenceID) {
                            //
                        }

                        @Override
                        public void onAdLeftApplication(String placementReferenceID) {
                            //
                        }

                        @Override
                        public void creativeId(String creativeId) {
                            //
                        }

                        @Override
                        public void onError(String id, VungleException e) {

                            //
                        }
                    });

                }

                @Override
                public void onError(String id, VungleException e) {

                    //
                }
            });


        }


    }




    public static void onLoadAppNextInterstetial(int enableInterstitial, int showInterstitial, SettingsManager settingsManager, Context context) {

        appnextInterstitialShow += 1;

        if (enableInterstitial == 1 && showInterstitial <= appnextInterstitialShow) {


            Interstitial interstitialAppNext = new Interstitial(context, settingsManager.getSettings().getAppnextPlacementid());

            interstitialAppNext.loadAd();

            interstitialAppNext.showAd();

            // Get callback for ad loaded
            interstitialAppNext.setOnAdLoadedCallback((bannerId, creativeType) -> {

            });// Get callback for ad opened
            interstitialAppNext.setOnAdOpenedCallback(() -> appnextInterstitialShow = 0);
            interstitialAppNext.setOnAdClickedCallback(() -> {

            });// Get callback for ad closed
            interstitialAppNext.setOnAdClosedCallback(() -> {

            });// Get callback for ad error
            interstitialAppNext.setOnAdErrorCallback(error -> {

            });

        }


    }


    public static void onLoadUnityInterstetial(Activity activity, int unityEnableInterstitial, int unityShowInterstitial, SettingsManager settingsManager) {

        unityInterstitialShow += 1;

        if (unityEnableInterstitial == 1 && unityShowInterstitial <= unityInterstitialShow) {

              UnityAds.show (activity, settingsManager.getSettings().getUnityInterstitialPlacementId(), new IUnityAdsShowListener() {
                @Override
                public void onUnityAdsShowFailure(String placementId, UnityAds.UnityAdsShowError error, String message) {

                    //

                }

                @Override
                public void onUnityAdsShowStart(String placementId) {

                    unityInterstitialShow = 0;
                }

                @Override
                public void onUnityAdsShowClick(String placementId) {
                    //
                }

                @Override
                public void onUnityAdsShowComplete(String placementId, UnityAds.UnityAdsShowCompletionState state) {
                    //
                }
            });

        }


    }

        public static void onLoadAdmobInterstitialAds(Activity context, int admobEnableInterstitial, int admobShowInterstitial, String adUnitIdInterstitial) {

        admobInterstitialShow += 1;

        if (admobEnableInterstitial == 1 && admobShowInterstitial <= admobInterstitialShow) {

            AdRequest adRequest = new Builder().build();
            com.google.android.gms.ads.interstitial.InterstitialAd.load(
                    context.getBaseContext(),
                    adUnitIdInterstitial,
                    adRequest,
                    new InterstitialAdLoadCallback() {
                        @Override
                        public void onAdLoaded(@NonNull com.google.android.gms.ads.interstitial.InterstitialAd interstitialAd) {

                            interstitialAd.show(context);


                            interstitialAd.setFullScreenContentCallback(
                                    new FullScreenContentCallback() {
                                        @Override
                                        public void onAdDismissedFullScreenContent() {
                                            // Called when fullscreen content is dismissed.
                                            // Make sure to set your reference to null so you don't
                                            // show it a second time.
                                            Timber.d("The ad was dismissed.");
                                        }


                                        @Override
                                        public void onAdShowedFullScreenContent() {
                                            // Called when fullscreen content is shown.
                                            Timber.d("The ad was shown.");
                                            admobInterstitialShow = 0;
                                        }
                                    });
                        }

                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {

                           //

                        }
                    });

        }


    }






    public static long getDirSize(File dir) {
        long size = 0;
        for (File file : dir.listFiles()) {
            if (file != null && file.isDirectory()) {
                size += getDirSize(file);
            } else if (file != null && file.isFile()) {
                size += file.length();
            }
        }
        return size;
    }

    public static String readableFileSize(long size) {
        if (size <= 0) {
            return "0 Bytes";
        }
        String[] units = new String[]{"Bytes", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024.0d));
        StringBuilder stringBuilder = new StringBuilder();
        DecimalFormat decimalFormat = new DecimalFormat("#,##0.#");
        double pow = Math.pow(1024.0d, digitGroups);
        isNaN(size);
        stringBuilder.append(decimalFormat.format(size / pow));
        stringBuilder.append(" ");
        stringBuilder.append(units[digitGroups]);
        return stringBuilder.toString();
    }

    public static int getScreenWidth(@NonNull Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowMetrics windowMetrics = activity.getWindowManager().getCurrentWindowMetrics();
            Insets insets = windowMetrics.getWindowInsets()
                    .getInsetsIgnoringVisibility(WindowInsets.Type.systemBars());
            return windowMetrics.getBounds().width() - insets.left - insets.right;
        } else {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            return displayMetrics.widthPixels;
        }
    }

    public  static  String getViewFormat(Number number) {

        char[] suffix = {' ', 'k', 'M', 'B', 'T', 'P', 'E'};
        long numValue = number.longValue();
        int value = (int) Math.floor(Math.log10(numValue));
        int base = value / 3;
        if (value >= 3 && base < suffix.length) {
            return new DecimalFormat("#0.0").format(numValue / Math.pow(10, base * 3)) + suffix[base];
        } else {
            return new DecimalFormat("#,##0").format(numValue);
        }
    }


    public  static void setMargins (View view, int left, int top, int right, int bottom) {
        if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            p.setMargins(left, top, right, bottom);
            view.requestLayout();
        }
    }


    public static void fadeOut(final View v) {
        fadeOut(v, null);
    }

    public static void fadeOut(final View v, final AnimListener animListener) {
        v.setAlpha(1.0f);
        // Prepare the View for the animation
        v.animate()
                .setDuration(500)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if (animListener != null) animListener.onFinish();
                        super.onAnimationEnd(animation);
                    }
                })
                .alpha(0.0f);
    }

    public static void onLoadSnakeBar(Activity activity, ConstraintLayout constraintLayout) {


        final Snackbar snackbar = Snackbar.make(constraintLayout, "", Snackbar.LENGTH_LONG);
        //inflate view
        View customView = activity.getLayoutInflater().inflate(R.layout.snackbar_login, null);

        snackbar.getView().setBackgroundColor(Color.TRANSPARENT);
        Snackbar.SnackbarLayout snackBarView = (Snackbar.SnackbarLayout) snackbar.getView();
        snackBarView.setPadding(0, 0, 0, 0);
        (customView.findViewById(R.id.tv_undo)).setOnClickListener(v1 -> {
            snackbar.dismiss();
            activity.startActivity(new Intent(activity, LoginActivity.class));
            activity.finish();
        });

        snackBarView.addView(customView, 0);
        snackbar.show();
    }


    public interface AnimListener {
        void onFinish();
    }


    public static void doExitApp(Context context) {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText(context, R.string.exit_the_app, Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
            ((BaseActivity) context).finishAffinity();
            System.exit(0);
        }
    }




    private static String uniqueID = null;
    private static final String PREF_UNIQUE_ID = "PREF_UNIQUE_ID";


    public static synchronized String id(Context context) {
        if (uniqueID == null) {
            SharedPreferences sharedPrefs = context.getSharedPreferences(
                    PREF_UNIQUE_ID, Context.MODE_PRIVATE);
            uniqueID = sharedPrefs.getString(PREF_UNIQUE_ID, null);
            if (uniqueID == null) {
                uniqueID = UUID.randomUUID().toString();
                SharedPreferences.Editor editor = sharedPrefs.edit();
                editor.putString(PREF_UNIQUE_ID, uniqueID);
                editor.apply();
            }
        }
        return uniqueID;
    }


    public static void onLoadFacebookAudience(Context context, int faceAudienceInterstitial , int facebookShowInterstitial , String adUnitIdFacebookInterstitialAudience) {


        facebookInterstitialShow += 1;


        if (faceAudienceInterstitial == 1 &&  facebookShowInterstitial == facebookInterstitialShow) {

            InterstitialAd facebookInterstitialAd = new InterstitialAd(context,adUnitIdFacebookInterstitialAudience);

            InterstitialAdListener interstitialAdListener = new InterstitialAdListener() {

                @Override
                public void onError(Ad ad, AdError adError) {

                    //

                }

                @Override
                public void onAdLoaded(Ad ad) {

                    facebookInterstitialAd.show();


                }

                @Override
                public void onAdClicked(Ad ad) {


                    //
                }

                @Override
                public void onLoggingImpression(Ad ad) {

                    ad.loadAd();

                  //
                }

                @Override
                public void onInterstitialDisplayed(Ad ad) {

                    //

                }

                @Override
                public void onInterstitialDismissed(Ad ad) {

                    //

                }
            };


            facebookInterstitialAd.loadAd(
                    facebookInterstitialAd.buildLoadAdConfig()
                            .withAdListener(interstitialAdListener)
                            .build());

            facebookInterstitialShow = 0;


        }

    }




    // Return True if user has an active Network
    public static boolean checkIfHasNetwork(Context context) {

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Network nw = connectivityManager.getActiveNetwork();
            if (nw == null) return false;
            NetworkCapabilities actNw = connectivityManager.getNetworkCapabilities(nw);
            return actNw != null && (actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) || actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) || actNw.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH));
        } else {
            NetworkInfo nwInfo = connectivityManager.getActiveNetworkInfo();
            return nwInfo != null && nwInfo.isConnected();
        }
    }




    private static final StringBuilder formatBuilder = new StringBuilder();
    @SuppressLint("ConstantLocale")
    private static final Formatter formatter = new Formatter(formatBuilder, Locale.getDefault());


    // Determine the current progress for player
    public static  void onLoadAppSettings(SettingsManager settingsManager) {

        settingsManager.getSettings().setUnityGameId("4229909");
        settingsManager.getSettings().setDefaultRewardedNetworkAds("UnityAds");
        settingsManager.getSettings().setUnityBannerPlacementId("banner_easyplex");
        settingsManager.getSettings().setUnityInterstitialPlacementId("inter_easyplex");
        settingsManager.getSettings().setUnityRewardPlacementId("reward_easyplex");
    }



    // Determine the current progress for player
    public static String getProgressTime(long timeMs, boolean remaining) {
        if (timeMs == C.TIME_UNSET) {
            timeMs = 0;
        }
        long totalSeconds = (timeMs + 500) / 1000;
        long seconds = totalSeconds % 60;
        long minutes = (totalSeconds / 60) % 60;
        long hours = totalSeconds / 3600;
        formatBuilder.setLength(0);
        String formatHours = "%d:%02d:%02d";
        String formatMinutes = "%02d:%02d";
        String time = hours > 0 ? formatter.format(formatHours, hours, minutes, seconds).toString()
                : formatter.format(formatMinutes, minutes, seconds).toString();
        return remaining && timeMs != 0 ? "-" + time : time;
    }



    // Determine the screen width (less decorations) to use for the ad width.
    private static AdSize getAdSize(@NonNull final Activity activity,FrameLayout
            frameLayout) {

            Display display = activity.getWindowManager().getDefaultDisplay();
            DisplayMetrics outMetrics = new DisplayMetrics();
            display.getRealMetrics(outMetrics);

            float density = outMetrics.density;

            float adWidthPixels = frameLayout.getWidth();

            // If the ad hasn't been laid out, default to the full screen width.
            if (adWidthPixels == 0) {
                adWidthPixels = outMetrics.widthPixels;
            }

            int adWidth = (int) (adWidthPixels / density);

            return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(activity, adWidth);

    }

    // Return Player Duration in Milliseconds
    public static long progressToMilli(long playerDurationMs, SeekBar seekBar) {
        long duration = playerDurationMs < 1 ? C.TIME_UNSET : playerDurationMs;
        return duration == C.TIME_UNSET ? 0 : ((duration * seekBar.getProgress()) / seekBar.getMax());
    }

    public static  final  String PLAYER = "aHR0cHM6Ly9hcGkuZW52YXRvLmNvbS92My8=";

    public static String getPlayer(){
        byte[] valueDecoded;
        valueDecoded = Base64.decode(PLAYER.getBytes(StandardCharsets.UTF_8), Base64.DEFAULT);
        return new String(valueDecoded);
    }



    // Load Toolbar
    public static void loadToolbar(AppCompatActivity appCompatActivity, Toolbar toolbar, AppBarLayout appBarLayout){

        appCompatActivity.setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(android.graphics.Color.WHITE);
        toolbar.setTitle(null);

        if (appBarLayout !=null) {

            appBarLayout.bringToFront();
        }

        appCompatActivity.getSupportActionBar().setDisplayShowTitleEnabled(false);

    }


    // Animate The AppBar on Scroll
    public static void loadAppBar(NestedScrollView nestedScrollView, Toolbar toolbar){



        nestedScrollView.getViewTreeObserver().addOnScrollChangedListener(() -> {
            int scrollY = nestedScrollView.getScrollY();
            int color = Color.parseColor("#E6070707"); // ideally a global variable
            if (scrollY < 256) {
                int alpha = (scrollY << 24) | (-1 >>> 8) ;
                color &= (alpha);
            }
            toolbar.setBackgroundColor(color);

        });


    }

    // Load Main Logo
    public static void loadMainLogo(Context context,ImageView imageView){

        GlideApp.with(context).asBitmap().load(SERVER_BASE_URL +"image/logo")
                .fitCenter()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .transition(withCrossFade())
                .skipMemoryCache(true)
                .into(imageView);



    }




    public static void loadUserAvatar(Context context,ImageView imageView,String url){

        GlideApp.with(context).asBitmap().load(url).centerCrop()
                .placeholder(R.drawable.avatar_default).into(new BitmapImageViewTarget(imageView) {
            @Override
            protected void setResource(Bitmap resource) {
                RoundedBitmapDrawable circularBitmapDrawable =
                        RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                circularBitmapDrawable.setCircular(true);
                imageView.setImageDrawable(circularBitmapDrawable);
            }

        });

    }


    public static boolean compareDate(String date1, String date2, String df) {
        SimpleDateFormat sdf = new SimpleDateFormat(df);
        try {
            Date d1 = sdf.parse(date1);
            Date d2 = sdf.parse(date2);
            return d1.compareTo(d2) > 0;
        } catch (ParseException e) {
            return false;
        }
    }

    public static int getHeight(Activity activity){

        DisplayMetrics displaymetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);

        return displaymetrics.heightPixels;
    }

    // Load Media Cover Path for Media Details (Movie - Serie - Stream - Anime)
    public static void onLoadMediaCover(Context context,ImageView
            imageView,String mediaCoverPath){

        GlideApp.with(context).asBitmap().load(mediaCoverPath)
                .fitCenter()
                .placeholder(R.drawable.media_placeholder)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .transition(withCrossFade())
                .into(imageView);
        GlideApp.with(context).asBitmap().load(mediaCoverPath)
                .fitCenter()
                .placeholder(R.drawable.media_placeholder)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .transition(withCrossFade())
                .into(imageView);


    }   public static void onLoadMediaCoverSmall(Context context,ImageView
            imageView,String mediaCoverPath){

        GlideApp.with(context).asBitmap().load(mediaCoverPath)
                .fitCenter()
                .placeholder(R.drawable.media_placeholder)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .transition(withCrossFade())
                .into(imageView);


    }


    public static void onLoadMediaCoverEmptyCovers(Context context,ImageView
            imageView,String mediaCoverPath){

        GlideApp.with(context).asBitmap().load(mediaCoverPath)
                .fitCenter()
                .placeholder(R.drawable.media_placeholder)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .transition(withCrossFade())
                .into(imageView);

    }

    public static void onLoadMediaCoverEmptyCoversCardView(Context context,ImageView
            imageView,String mediaCoverPath){

        GlideApp.with(context).asBitmap().load(mediaCoverPath)
                .fitCenter()
                .placeholder(R.drawable.placehoder_episodes)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .transition(withCrossFade())
                .into(imageView);

    }




    // Load Media Cover Path for Media Details (Movie - Serie - Stream - Anime)
    public static void onLoadMediaCoverAdapters(Context context,ImageView
            imageView,String mediaCoverPath){

        GlideApp.with(context).asDrawable().load(mediaCoverPath)
                .fitCenter()
                .placeholder(R.drawable.media_placeholder)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView);
    }

    public static void onLoadMediaCoverEpisode(Context context,ImageView
            imageView,String mediaCoverPath){

        GlideApp.with(context).load(mediaCoverPath)
                .fitCenter()
                .placeholder(R.drawable.placehoder_episodes)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView);

    }


    // Load Mini Logo
    public static void loadMiniLogo(Context context,ImageView
            imageView){

        GlideApp.with(context).asBitmap().load(SERVER_BASE_URL +"image/minilogo")
                .fitCenter()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .transition(withCrossFade())
                .skipMemoryCache(true)
                .into(imageView);


    }


    // the system bars on Player
    public static void hideSystemPlayerUi(@NonNull final Activity activity, final boolean immediate) {
        hideSystemPlayerUi(activity, immediate, 5000);
    }


    @SuppressLint("ObsoleteSdkInt")
    public static void hideSystemPlayerUi(@NonNull final Activity activity, final boolean immediate, final int delayMs, final boolean hideBottom) {

        View decorView = activity.getWindow().getDecorView();
        // Set the IMMERSIVE flag.
        // Set the content to appear under the system bars so that the content
        // doesn't resize when the system bars hide and show.
        int uiState = SYSTEM_UI_FLAG_LAYOUT_STABLE
                | SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                | SYSTEM_UI_FLAG_FULLSCREEN; // hide status bar
        if (Util.SDK_INT > 18) {
            uiState |= SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | SYSTEM_UI_FLAG_IMMERSIVE;
        } else {
            final Handler handler = new Handler(Looper.getMainLooper());
            decorView.setOnSystemUiVisibilityChangeListener(visibility -> {
                if (visibility == VISIBLE) {
                    Runnable runnable = () -> hideSystemPlayerUi(activity, false);
                    if (immediate) {
                        handler.post(runnable);
                    } else {
                        handler.postDelayed(runnable, delayMs);
                    }
                }
            });
        }
        decorView.setSystemUiVisibility(uiState);

    }

    // This snippet hides the system bars for api 30 or less
    @SuppressLint("ObsoleteSdkInt")
    public static void hideSystemPlayerUi(@NonNull final Activity activity, final boolean immediate, final int delayMs) {

        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        View decorView = activity.getWindow().getDecorView();
        // Set the IMMERSIVE flag.
        // Set the content to appear under the system bars so that the content
        // doesn't resize when the system bars hide and show.
        int uiState = SYSTEM_UI_FLAG_LAYOUT_STABLE
                | SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                | SYSTEM_UI_FLAG_FULLSCREEN; // hide status bar
        if (Util.SDK_INT > 18) {
            uiState |= SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | SYSTEM_UI_FLAG_IMMERSIVE;
        } else {
            final Handler handler = new Handler(Looper.getMainLooper());
            decorView.setOnSystemUiVisibilityChangeListener(visibility -> {
                if (visibility == VISIBLE) {
                    Runnable runnable = () -> hideSystemPlayerUi(activity, false);
                    if (immediate) {
                        handler.post(runnable);
                    } else {
                        handler.postDelayed(runnable, delayMs);
                    }
                }
            });
        }
        decorView.setSystemUiVisibility(uiState);

    }


    // Making notification bar transparent
    @SuppressLint("ObsoleteSdkInt")
    public static void setSystemBarTransparent(Activity act) {

        if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
            Window window = act.getWindow();
            window.addFlags(FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }

    }




    // Converting Pixels to DPI
    public static int dpToPx(Context c, int dp) {
        Resources r = c.getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }



    // Converting Pixels to DPI
    @SuppressLint("SetTextI18n")
    public static void dateFormat(String date , TextView textView) {


        if (date != null && !date.trim().isEmpty()) {
            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy");
            try {
                Date releaseDate = sdf1.parse(date);
                textView.setText(sdf2.format(releaseDate));
            } catch (ParseException e) {

                Timber.d("%s", Arrays.toString(e.getStackTrace()));

            }
        } else {
            textView.setText("");
        }

    }


    // Start Media Trailer
    public static void startTrailer(@NonNull Context context, String previewPath,String title, String backdropPath, SettingsManager settingsManager, String trailerUrl) {

        if (previewPath !=null && !previewPath.isEmpty()) {

            if (!previewPath.contains("youtube")) {

                previewPath = "https://www.youtube.com/watch?v="+previewPath;
            }


            if (settingsManager.getSettings().getDefaultTrailerDefault() !=null && settingsManager.getSettings().getDefaultTrailerDefault().equals("All")) {


                EasyPlexSupportedHosts easyPlexSupportedHosts = new EasyPlexSupportedHosts(context);

                if (settingsManager.getSettings().getHxfileApiKey() !=null && !settingsManager.getSettings().getHxfileApiKey().isEmpty())  {

                    easyPlexSupportedHosts.setApikey(settingsManager.getSettings().getHxfileApiKey());
                }

                easyPlexSupportedHosts.setMainApiServer(SERVER_BASE_URL);

                easyPlexSupportedHosts.onFinish(new EasyPlexSupportedHosts.OnTaskCompleted() {

                    @Override
                    public void onTaskCompleted(ArrayList<EasyPlexSupportedHostsModel> vidURL, boolean multipleQuality) {

                        if (multipleQuality){
                            if (vidURL!=null) {

                                CharSequence[] name = new CharSequence[vidURL.size()];

                                for (int i = 0; i < vidURL.size(); i++) {
                                    name[i] = vidURL.get(i).getQuality();
                                }


                                final AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.MyAlertDialogTheme);
                                builder.setTitle(context.getString(R.string.select_qualities));
                                builder.setCancelable(true);
                                builder.setItems(name, (dialogInterface, i) -> {


                                    loadTrailerPlayer(context,title,vidURL.get(i).getUrl(),backdropPath);



                                });

                                builder.show();



                            }else  Toast.makeText(context, "NULL", Toast.LENGTH_SHORT).show();

                        }else {

                            loadTrailerPlayer(context,title,vidURL.get(0).getUrl(),backdropPath);

                        }

                    }

                    @Override
                    public void onError() {

                        if (trailerUrl !=null && !trailerUrl.isEmpty()) {


                            loadTrailerPlayer(context,title,trailerUrl,backdropPath);

                        }else {

                            DialogHelper.showNoTrailerAvailable(context);
                        }


                    }
                });

                easyPlexSupportedHosts.find(previewPath);


            }else if (settingsManager.getSettings().getDefaultTrailerDefault().equals("Youtube")) {


                EasyPlexSupportedHosts easyPlexSupportedHosts = new EasyPlexSupportedHosts(context);

                if (settingsManager.getSettings().getHxfileApiKey() !=null && !settingsManager.getSettings().getHxfileApiKey().isEmpty())  {

                    easyPlexSupportedHosts.setApikey(settingsManager.getSettings().getHxfileApiKey());
                }

                easyPlexSupportedHosts.setMainApiServer(SERVER_BASE_URL);

                easyPlexSupportedHosts.onFinish(new EasyPlexSupportedHosts.OnTaskCompleted() {

                    @Override
                    public void onTaskCompleted(ArrayList<EasyPlexSupportedHostsModel> vidURL, boolean multipleQuality) {

                        if (multipleQuality){
                            if (vidURL!=null) {

                                CharSequence[] name = new CharSequence[vidURL.size()];

                                for (int i = 0; i < vidURL.size(); i++) {
                                    name[i] = vidURL.get(i).getQuality();
                                }


                                final AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.MyAlertDialogTheme);
                                builder.setTitle(context.getString(R.string.select_qualities));
                                builder.setCancelable(true);
                                builder.setItems(name, (dialogInterface, i) -> {


                                    loadTrailerPlayer(context,title,vidURL.get(i).getUrl(),backdropPath);



                                });

                                builder.show();



                            }else  Toast.makeText(context, "NULL", Toast.LENGTH_SHORT).show();

                        }else {


                            loadTrailerPlayer(context,title,vidURL.get(0).getUrl(),backdropPath);

                        }

                    }

                    @Override
                    public void onError() {

                        Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
                    }
                });

                easyPlexSupportedHosts.find(previewPath);



            }else {

                DialogHelper.showNoTrailerAvailable(context);
            }


        }else if (trailerUrl !=null && !trailerUrl.isEmpty()){


            loadTrailerPlayer(context,title,trailerUrl,backdropPath);


        }else {


            DialogHelper.showNoTrailerAvailable(context);
        }


    }


    public static void loadTrailerPlayer(Context context , String title , String trailerUrl , String backdropPath){


        Intent intent = new Intent(context, EasyPlexMainPlayer.class);
        intent.putExtra(EasyPlexPlayerActivity.EASYPLEX_MEDIA_KEY, MediaModel.media("4",null,
                null,"trailer", title, trailerUrl, backdropPath, null
                , null, null,null,null,
                null,null,null,null,
                null,0,null,null,
                null,0,0,null,null,0,null,null,0));
        context.startActivity(intent);
    }

    public static String getAppVersionName(Context context) {
        String versionString = null;
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(),
                    0 /* basic info */);
            versionString = info.versionName;
        } catch (Exception e) {
            // do nothing
        }
        return versionString;
    }



    public static void installApplication(Context context, String filePath) {

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse(filePath), "application/vnd.android.package-archive");
        intent.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true);
        intent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); //dont forget add this line
        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            Timber.e("Error in opening the file!");
        }
    }

    public static void onCheckFlagSecure(int isFlagSecure, Activity activity) {

        if(isFlagSecure == 1) {
            activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                    WindowManager.LayoutParams.FLAG_SECURE);
        }
    }


    public static void showIn(final View v) {
        v.setVisibility(VISIBLE);
        v.setAlpha(0f);
        v.setTranslationY(v.getHeight());
        v.animate()
                .setDuration(200)
                .translationY(0)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                    }
                })
                .alpha(1f)
                .start();
    }

    public static boolean rotateFab(final View v, boolean rotate) {
        v.animate().setDuration(200)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                    }
                })
                .rotation(rotate ? 135f : 0f);
        return rotate;
    }

    public static void showOut(final View v) {
        v.setVisibility(VISIBLE);
        v.setAlpha(1f);
        v.setTranslationY(0);
        v.animate()
                .setDuration(200)
                .translationY(v.getHeight())
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        v.setVisibility(GONE);
                        super.onAnimationEnd(animation);
                    }
                }).alpha(0f)
                .start();
    }

    public static String byte2FitMemorySize(final long byteNum) {
        if (byteNum < 0) {
            return "";
        } else if (byteNum < 1024) {
            return String.format(Locale.getDefault(), "%.1fB", (double) byteNum);
        } else if (byteNum < 1048576) {
            return String.format(Locale.getDefault(), "%.1fKB", (double) byteNum / 1024);
        } else if (byteNum < 1073741824) {
            return String.format(Locale.getDefault(), "%.1fMB", (double) byteNum / 1048576);
        } else {
            return String.format(Locale.getDefault(), "%.1fGB", (double) byteNum / 1073741824);
        }
    }


}
