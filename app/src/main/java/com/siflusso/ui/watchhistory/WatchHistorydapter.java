package com.siflusso.ui.watchhistory;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static com.siflusso.util.Constants.ARG_MOVIE;
import static com.siflusso.util.Constants.ARG_MOVIE_HISTORY;
import static com.siflusso.util.Constants.DEFAULT_WEBVIEW_ADS_RUNNING;
import static com.siflusso.util.Constants.PLAYER_HEADER;
import static com.siflusso.util.Constants.PLAYER_USER_AGENT;
import static com.siflusso.util.Constants.SERVER_BASE_URL;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.MaxReward;
import com.applovin.mediation.MaxRewardedAdListener;
import com.applovin.mediation.ads.MaxRewardedAd;
import com.appnext.ads.fullscreen.RewardedVideo;
import com.appodeal.ads.Appodeal;
import com.appodeal.ads.RewardedVideoCallbacks;
import com.easyplex.easyplexsupportedhosts.EasyPlexSupportedHosts;
import com.easyplex.easyplexsupportedhosts.Model.EasyPlexSupportedHostsModel;
import com.siflusso.R;
import com.siflusso.data.local.entity.History;
import com.siflusso.data.local.entity.Media;
import com.siflusso.data.model.MovieResponse;
import com.siflusso.data.model.media.MediaModel;
import com.siflusso.data.model.media.Resume;
import com.siflusso.data.repository.AnimeRepository;
import com.siflusso.data.repository.MediaRepository;
import com.siflusso.databinding.ItemHistoryBinding;
import com.siflusso.ui.animes.AnimeDetailsActivity;
import com.siflusso.ui.base.BaseActivity;
import com.siflusso.ui.manager.AuthManager;
import com.siflusso.ui.manager.SettingsManager;
import com.siflusso.ui.manager.TokenManager;
import com.siflusso.ui.moviedetails.MovieDetailsActivity;
import com.siflusso.ui.player.activities.EasyPlexMainPlayer;
import com.siflusso.ui.player.activities.EasyPlexPlayerActivity;
import com.siflusso.ui.player.activities.EmbedActivity;
import com.siflusso.ui.player.cast.ExpandedControlsActivity;
import com.siflusso.ui.player.cast.queue.QueueDataProvider;
import com.siflusso.ui.player.cast.utils.Utils;
import com.siflusso.ui.seriedetails.SerieDetailsActivity;
import com.siflusso.ui.settings.SettingsActivity;
import com.siflusso.util.Constants;
import com.siflusso.util.DialogHelper;
import com.siflusso.util.Tools;
import com.facebook.ads.AdError;
import com.facebook.ads.InterstitialAdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.gms.cast.MediaInfo;
import com.google.android.gms.cast.MediaMetadata;
import com.google.android.gms.cast.MediaQueueItem;
import com.google.android.gms.cast.MediaStatus;
import com.google.android.gms.cast.MediaTrack;
import com.google.android.gms.cast.framework.CastContext;
import com.google.android.gms.cast.framework.CastSession;
import com.google.android.gms.cast.framework.media.RemoteMediaClient;
import com.google.android.gms.common.images.WebImage;
import com.ironsource.mediationsdk.IronSource;
import com.ironsource.mediationsdk.logger.IronSourceError;
import com.ironsource.mediationsdk.model.Placement;
import com.ironsource.mediationsdk.sdk.RewardedVideoListener;
import com.unity3d.ads.IUnityAdsLoadListener;
import com.unity3d.ads.IUnityAdsShowListener;
import com.unity3d.ads.UnityAds;
import com.vungle.warren.AdConfig;
import com.vungle.warren.LoadAdCallback;
import com.vungle.warren.PlayAdCallback;
import com.vungle.warren.Vungle;
import com.vungle.warren.error.VungleException;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Adapter for Media Watch History.
 *
 * @author Yobex.
 */
public class WatchHistorydapter extends RecyclerView.Adapter<WatchHistorydapter.MainViewHolder> {


    private RewardedVideo mAppNextAdsVideoRewarded;
    private MaxRewardedAd maxRewardedAd;
    private CountDownTimer mCountDownTimer;
    private boolean webViewLauched = false;
    private EasyPlexSupportedHosts easyPlexSupportedHosts;
    private List<History> castList;
    private final MediaRepository mediaRepository;
    private final AnimeRepository animeRepository;
    private final AuthManager authManager;
    private final Context context;
    private final SettingsManager settingsManager;
    private final TokenManager tokenManager;
    private RewardedAd mRewardedAd;
    boolean isLoading;
    private boolean adsLaunched = false;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private static final int PRELOAD_TIME_S = 2;
    private static final String TAG = "WatchingHistoryAdapter";


    public WatchHistorydapter(MediaRepository mediaRepository,
                              AuthManager authManager, SettingsManager
                                      settingsManager, TokenManager tokenManager,Context context,AnimeRepository animeRepository) {
        this.mediaRepository = mediaRepository;
        this.authManager = authManager;
        this.settingsManager = settingsManager;
        this.tokenManager = tokenManager;
        this.context = context;
        this.animeRepository = animeRepository;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void addToContent(List<History> castList) {
        this.castList = castList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public WatchHistorydapter.MainViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        ItemHistoryBinding binding = ItemHistoryBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);

        return new WatchHistorydapter.MainViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull WatchHistorydapter.MainViewHolder holder, int position) {
        holder.onBind(position);
    }

    @Override
    public int getItemCount() {
        if (castList != null) {
            return castList.size();
        } else {
            return 0;
        }
    }

    class MainViewHolder extends RecyclerView.ViewHolder {

        private final ItemHistoryBinding binding;


        MainViewHolder(@NonNull ItemHistoryBinding binding)
        {
            super(binding.getRoot());

            this.binding = binding;
        }

        @SuppressLint("SetTextI18n")
        void onBind(final int position) {

            final History history = castList.get(position);

            if (!adsLaunched) {

                createAndLoadRewardedAd();

            }

            binding.infoHistory.setOnClickListener(v -> {
                String type = history.getType();
                if ("0".equals(type)) {
                    mediaRepository.getMovie(history.getId(), settingsManager.getSettings().getApiKey())
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Observer<>() {
                                @Override
                                public void onSubscribe(@NotNull Disposable d) {

                                    //

                                }

                                @Override
                                public void onNext(@NotNull Media movieDetail) {

                                    Intent intent = new Intent(context, MovieDetailsActivity.class);
                                    intent.putExtra(ARG_MOVIE, movieDetail);
                                    context.startActivity(intent);

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
                } else if ("1".equals(type)) {
                    mediaRepository.getSerie(history.getId())
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Observer<>() {
                                @Override
                                public void onSubscribe(@NotNull Disposable d) {

                                    //

                                }

                                @Override
                                public void onNext(@NotNull Media movieDetail) {

                                    Intent intent = new Intent(context, SerieDetailsActivity.class);
                                    intent.putExtra(ARG_MOVIE, movieDetail);
                                    context.startActivity(intent);

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
                } else if ("anime".equals(type)) {
                    animeRepository.getAnimeDetails(history.getId())
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Observer<>() {
                                @Override
                                public void onSubscribe(@NotNull Disposable d) {

                                    //

                                }

                                @Override
                                public void onNext(@NotNull Media movieDetail) {

                                    Intent intent = new Intent(context, AnimeDetailsActivity.class);
                                    intent.putExtra(ARG_MOVIE, movieDetail);
                                    context.startActivity(intent);

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
            });


            binding.deleteFromHistory.setOnClickListener(v -> {

                final Dialog dialog = new Dialog(context);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_remove_movie_from_history);
                dialog.setCancelable(false);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(dialog.getWindow().getAttributes());

                lp.gravity = Gravity.BOTTOM;
                lp.width = MATCH_PARENT;
                lp.height = MATCH_PARENT;


                TextView movieName = dialog.findViewById(R.id.movietitle);
                TextView movieoverview = dialog.findViewById(R.id.text_overview_label);


                switch (history.getType()) {
                    case "1":
                    case "anime":
                        movieName.setText(history.getSerieName() + " : " + history.getTitle());
                        break;
                    default:

                        movieName.setText(history.getTitle());
                        break;
                }

                movieoverview.setText(context.getString(R.string.are_you_sure_to_delete_from_your_watching_history)+" "+history.getTitle()+context.getString(R.string.from_history));

                dialog.findViewById(R.id.view_delete_from_history).setOnClickListener(v12 -> {

                    compositeDisposable.add(Completable.fromAction(() -> mediaRepository.removeHistory(history))
                            .subscribeOn(Schedulers.io())
                            .subscribe());
                    dialog.dismiss();

                });


                dialog.findViewById(R.id.text_view_cancel).setOnClickListener(v1 -> dialog.dismiss());

                dialog.show();
                dialog.getWindow().setAttributes(lp);

                dialog.findViewById(R.id.bt_close).setOnClickListener(x ->

                        dialog.dismiss());


                dialog.show();
                dialog.getWindow().setAttributes(lp);


            });



            binding.rootLayout.setOnLongClickListener(v -> {

                Toast.makeText(context, ""+history.getId(), Toast.LENGTH_SHORT).show();
                return false;
            });


            binding.rootLayout.setOnClickListener(view -> {


                if (history.getPremuim() == 1 && authManager.getUserInfo().getPremuim() == 1 && tokenManager.getToken() != null) {


                    onLoadStream(history);


                } else  if (settingsManager.getSettings().getEnableWebview() == 1) {

                final Dialog dialog = new Dialog(context);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.episode_webview);
                dialog.setCancelable(false);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(dialog.getWindow().getAttributes());

                lp.gravity = Gravity.BOTTOM;
                lp.width = MATCH_PARENT;
                lp.height = MATCH_PARENT;


                mCountDownTimer = new CountDownTimer(DEFAULT_WEBVIEW_ADS_RUNNING, 1000) {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onTick(long millisUntilFinished) {

                        if (!webViewLauched) {

                            WebView webView = dialog.findViewById(R.id.webViewVideoBeforeAds);
                            webView.getSettings().setJavaScriptEnabled(true);
                            webView.setWebViewClient(new WebViewClient());
                            WebSettings webSettings = webView.getSettings();
                            webSettings.setSupportMultipleWindows(false);
                            webSettings.setJavaScriptCanOpenWindowsAutomatically(false);
                            webView.loadUrl(settingsManager.getSettings().getWebviewLink());
                            webViewLauched = true;
                        }

                    }

                    @Override
                    public void onFinish() {

                        dialog.dismiss();
                        onLoadStream(history);
                        webViewLauched = false;

                        if (mCountDownTimer != null) {

                            mCountDownTimer.cancel();
                            mCountDownTimer = null;

                        }
                    }

                }.start();

                dialog.show();
                dialog.getWindow().setAttributes(lp);


            }  else if (settingsManager.getSettings().getWachAdsToUnlock() == 1 && history.getPremuim() != 1 && authManager.getUserInfo().getPremuim() == 0) {


                    onLoadSubscribeDialog(history);

                } else if (settingsManager.getSettings().getWachAdsToUnlock() == 0 && history.getPremuim() == 0) {


                    onLoadStream(history);


                } else if (authManager.getUserInfo().getPremuim() == 1 && history.getPremuim() == 0) {


                    onLoadStream(history);


                } else {

                    DialogHelper.showPremuimWarning(context);

                }

            });




            if (history.getType().equals("0")) {

             binding.movietitle.setText(history.getTitle());

            }else {


            binding.movietitle.setText(history.getSerieName()+ " : " + history.getTitle());


            }





            if (settingsManager.getSettings().getResumeOffline() == 1) {

                if ("0".equals(history.getType())) {
                    mediaRepository.hasResume(Integer.parseInt(history.getTmdbId())).observe((BaseActivity) context, resumeInfo -> {

                        if (resumeInfo !=null && resumeInfo.getResumePosition() != null && authManager.getUserInfo().getId()
                                != null && authManager.getUserInfo().getId() == resumeInfo.getUserResumeId()

                                && resumeInfo.getDeviceId().equals(Tools.id(context))) {

                            double d = resumeInfo.getResumePosition();

                            double moveProgress = d * 100 / resumeInfo.getMovieDuration();

                            binding.lineaTime.setVisibility(View.VISIBLE);
                            binding.resumeProgressBar.setProgress((int) moveProgress);
                            binding.timeRemaning.setText(Tools.getProgressTime((resumeInfo.getMovieDuration() - resumeInfo.getResumePosition()), true));

                        }else {

                            binding.timeRemaning.setVisibility(View.GONE);
                        }

                    });
                } else if ("1".equals(history.getType())) {
                    mediaRepository.hasResume(Integer.parseInt(history.getEpisodeId())).observe((BaseActivity) context, resumeInfo -> {

                        if (resumeInfo !=null && resumeInfo.getResumePosition() != null && authManager.getUserInfo().getId() != null


                            && authManager.getUserInfo().getId() == resumeInfo.getUserResumeId() && resumeInfo.getDeviceId().equals(Tools.id(context))) {

                            double d = resumeInfo.getResumePosition();

                            double moveProgress = d * 100 / resumeInfo.getMovieDuration();

                            binding.lineaTime.setVisibility(View.VISIBLE);
                            binding.resumeProgressBar.setProgress((int) moveProgress);
                            binding.timeRemaning.setText(Tools.getProgressTime((resumeInfo.getMovieDuration() - resumeInfo.getResumePosition()), true));

                        }else {

                            binding.timeRemaning.setVisibility(View.GONE);
                        }

                    });
                } else if ("anime".equals(history.getType())) {
                    mediaRepository.hasResume(Integer.parseInt(history.getEpisodeTmdb())).observe((BaseActivity) context, resumeInfo -> {

                        if (resumeInfo !=null && resumeInfo.getResumePosition() != null && authManager.getUserInfo().getId() != null


                                && authManager.getUserInfo().getId() == resumeInfo.getUserResumeId() && resumeInfo.getDeviceId().equals(Tools.id(context))) {


                            double d = resumeInfo.getResumePosition();

                            double moveProgress = d * 100 / resumeInfo.getMovieDuration();

                            binding.lineaTime.setVisibility(View.VISIBLE);
                            binding.resumeProgressBar.setProgress((int) moveProgress);
                            binding.timeRemaning.setText(Tools.getProgressTime((resumeInfo.getMovieDuration() - resumeInfo.getResumePosition()), true));

                        } else {


                            binding.lineaTime.setVisibility(View.GONE);
                        }

                    });
                }

            }else {

                if ("0".equals(history.getType())) {
                    mediaRepository.getResumeById(history.getTmdbId(), settingsManager.getSettings().getApiKey())
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Observer<>() {
                                @Override
                                public void onSubscribe(@NotNull Disposable d) {

                                    //

                                }

                                @SuppressLint("TimberArgCount")
                                @Override
                                public void onNext(@NotNull Resume resume) {

                                    if (resume.getResumePosition() != null && authManager.getUserInfo().getId() != null


                                            && authManager.getUserInfo().getId() == resume.getUserResumeId() && resume.getDeviceId().equals(Tools.id(context))) {


                                        double d = resume.getResumePosition();

                                        double moveProgress = d * 100 / resume.getMovieDuration();

                                        binding.lineaTime.setVisibility(View.VISIBLE);
                                        binding.resumeProgressBar.setProgress((int) moveProgress);
                                        binding.timeRemaning.setText(Tools.getProgressTime((resume.getMovieDuration() - resume.getResumePosition()), true));

                                    }

                                }

                                @SuppressLint("ClickableViewAccessibility")
                                @Override
                                public void onError(@NotNull Throwable e) {


                                    binding.timeRemaning.setVisibility(View.GONE);

                                }

                                @Override
                                public void onComplete() {

                                    //

                                }
                            });
                } else if ("1".equals(history.getType())) {
                    mediaRepository.getResumeById(history.getEpisodeId(), settingsManager.getSettings().getApiKey())
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Observer<>() {
                                @Override
                                public void onSubscribe(@NotNull Disposable d) {

                                    //

                                }

                                @SuppressLint("TimberArgCount")
                                @Override
                                public void onNext(@NotNull Resume resume) {

                                    if (resume.getResumePosition() != null && authManager.getUserInfo().getId() != null


                                            && authManager.getUserInfo().getId() == resume.getUserResumeId() && resume.getDeviceId().equals(Tools.id(context))) {


                                        double d = resume.getResumePosition();

                                        double moveProgress = d * 100 / resume.getMovieDuration();

                                        binding.lineaTime.setVisibility(View.VISIBLE);
                                        binding.resumeProgressBar.setProgress((int) moveProgress);
                                        binding.timeRemaning.setText(Tools.getProgressTime((resume.getMovieDuration() - resume.getResumePosition()), true));

                                    }

                                }

                                @SuppressLint("ClickableViewAccessibility")
                                @Override
                                public void onError(@NotNull Throwable e) {


                                    binding.timeRemaning.setVisibility(View.GONE);


                                }

                                @Override
                                public void onComplete() {

                                    //

                                }
                            });
                } else if ("anime".equals(history.getType())) {
                    mediaRepository.getResumeById(history.getEpisodeTmdb(), settingsManager.getSettings().getApiKey())
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Observer<>() {
                                @Override
                                public void onSubscribe(@NotNull Disposable d) {

                                    //

                                }

                                @SuppressLint("TimberArgCount")
                                @Override
                                public void onNext(@NotNull Resume resume) {

                                    if (resume.getResumePosition() != null && authManager.getUserInfo().getId() != null


                                            && authManager.getUserInfo().getId() == resume.getUserResumeId() && resume.getDeviceId().equals(Tools.id(context))) {


                                        double d = resume.getResumePosition();

                                        double moveProgress = d * 100 / resume.getMovieDuration();

                                        binding.lineaTime.setVisibility(View.VISIBLE);
                                        binding.resumeProgressBar.setProgress((int) moveProgress);
                                        binding.timeRemaning.setText(Tools.getProgressTime((resume.getMovieDuration() - resume.getResumePosition()), true));

                                    }

                                }

                                @SuppressLint("ClickableViewAccessibility")
                                @Override
                                public void onError(@NotNull Throwable e) {


                                    binding.timeRemaning.setVisibility(View.GONE);


                                }

                                @Override
                                public void onComplete() {

                                    //

                                }
                            });
                }

            }


            Tools.onLoadMediaCover(context,binding.itemMovieImage, history.getBackdropPath());

            binding.ratingBar.setRating(history.getVoteAverage() / 2);
            binding.viewMovieRating.setText(String.valueOf(history.getVoteAverage()));

        }

        private void createAndLoadRewardedAd() {

            if (settingsManager.getSettings().getDefaultRewardedNetworkAds() !=null && "Admob".equals
                    (settingsManager.getSettings().getDefaultRewardedNetworkAds()) && mRewardedAd == null) {

                isLoading = true;
                AdRequest adRequest = new AdRequest.Builder().build();
                RewardedAd.load(
                        context,
                        settingsManager.getSettings().getAdUnitIdRewarded(),
                        adRequest,
                        new RewardedAdLoadCallback() {
                            @Override
                            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {

                                mRewardedAd = null;

                                isLoading = false;

                            }

                            @Override
                            public void onAdLoaded(@NonNull RewardedAd rewardedAd) {

                                isLoading = false;
                                mRewardedAd = rewardedAd;
                            }
                        });
            }else if (context.getString(R.string.appodeal).equals(settingsManager.getSettings().getDefaultRewardedNetworkAds())) {


                Appodeal.initialize((BaseActivity) context, settingsManager.getSettings().getAdUnitIdAppodealRewarded(),Appodeal.REWARDED_VIDEO, list -> {


                });

            } else if (context.getString(R.string.applovin).equals(settingsManager.getSettings().getDefaultRewardedNetworkAds())) {

                maxRewardedAd = MaxRewardedAd.getInstance( settingsManager.getSettings().getApplovinRewardUnitid(), (BaseActivity) context);
                maxRewardedAd.loadAd();


            } else if (context.getString(R.string.appnext).equals(settingsManager.getSettings().getDefaultRewardedNetworkAds())) {


                mAppNextAdsVideoRewarded = new RewardedVideo(context, settingsManager.getSettings().getAppnextPlacementid());
                mAppNextAdsVideoRewarded.loadAd();


            }else if (context.getString(R.string.unityads).equals(settingsManager.getSettings().getDefaultRewardedNetworkAds())){

                UnityAds.load(settingsManager.getSettings().getUnityInterstitialPlacementId(), new IUnityAdsLoadListener() {
                    @Override
                    public void onUnityAdsAdLoaded(String placementId) {

                       //

                    }

                    @Override
                    public void onUnityAdsFailedToLoad(String placementId, UnityAds.UnityAdsLoadError error, String message) {

                        //

                    }
                });



            }
            adsLaunched = true;
        }



        private void onLoadSubscribeDialog(History media) {


            final Dialog dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_subscribe);
            dialog.setCancelable(false);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(dialog.getWindow().getAttributes());

            lp.gravity = Gravity.BOTTOM;
            lp.width = MATCH_PARENT;
            lp.height = MATCH_PARENT;


            dialog.findViewById(R.id.view_watch_ads_to_play).setOnClickListener(v -> {

                String defaultRewardedNetworkAds = settingsManager.getSettings().getDefaultRewardedNetworkAds();


                if (context.getString(R.string.applovin).equals(defaultRewardedNetworkAds)) {

                    onLoadApplovinAds(media);

                }else  if (context.getString(R.string.vungle).equals(defaultRewardedNetworkAds)) {

                    onLoadVungleAds(media);

                }else  if (context.getString(R.string.ironsource).equals(defaultRewardedNetworkAds)) {

                    onLoadIronsourceAds(media);

                }else  if (context.getString(R.string.appnext).equals(defaultRewardedNetworkAds)) {

                    onLoadAppNextAds(media);

                }else  if (context.getString(R.string.unityads).equals(defaultRewardedNetworkAds)) {

                    onLoadUnityAds(media);


                }else  if (context.getString(R.string.admob).equals(defaultRewardedNetworkAds)) {


                    onLoadAdmobRewardAds(media);


                }else  if (context.getString(R.string.facebook).equals(defaultRewardedNetworkAds)) {

                    onLoadFaceBookRewardAds(media);

                }else if (context.getString(R.string.appodeal).equals(defaultRewardedNetworkAds)) {

                    onLoadAppOdealRewardAds(media);

                }

                dialog.dismiss();

            });



            dialog.findViewById(R.id.text_view_go_pro).setOnClickListener(v -> {

                context.startActivity(new Intent(context, SettingsActivity.class));

                dialog.dismiss();


            });




            dialog.findViewById(R.id.bt_close).setOnClickListener(v ->

            dialog.dismiss());
            dialog.show();
            dialog.getWindow().setAttributes(lp);
        }

        private void onLoadApplovinAds(History media) {


            if (maxRewardedAd.isReady()) {

                maxRewardedAd.showAd();
            }

            maxRewardedAd.setListener(new MaxRewardedAdListener() {
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
                public void onRewardedVideoStarted(MaxAd ad) {
                    //
                }

                @Override
                public void onRewardedVideoCompleted(MaxAd ad) {

                    onLoadStream(media);
                    maxRewardedAd.loadAd();

                }

                @Override
                public void onUserRewarded(MaxAd ad, MaxReward reward) {
                    //
                }
            });
        }

        private void onLoadAppNextAds(History media) {

            mAppNextAdsVideoRewarded.showAd();

            // Get callback for ad loaded
            mAppNextAdsVideoRewarded.setOnAdLoadedCallback((s, appnextAdCreativeType) -> {
                //
            });

            mAppNextAdsVideoRewarded.setOnAdOpenedCallback(() -> {
                //
            });
            mAppNextAdsVideoRewarded.setOnAdClickedCallback(() -> {

            });

            mAppNextAdsVideoRewarded.setOnAdClosedCallback(() -> onLoadStream(media));

            mAppNextAdsVideoRewarded.setOnAdErrorCallback(error -> {
               //
            });

            // Get callback when the user saw the video until the end (video ended)
            mAppNextAdsVideoRewarded.setOnVideoEndedCallback(() -> {


            });
        }

        private void onLoadIronsourceAds(History media) {

            IronSource.showRewardedVideo(settingsManager.getSettings().getIronsourceRewardPlacementName());

            IronSource.setRewardedVideoListener(new RewardedVideoListener() {
                /**
                 * Invoked when the RewardedVideo ad view has opened.
                 * Your Activity will lose focus. Please avoid performing heavy
                 * tasks till the video ad will be closed.
                 */
                @Override
                public void onRewardedVideoAdOpened() {

                    //
                }
                /*Invoked when the RewardedVideo ad view is about to be closed.
                Your activity will now regain its focus.*/
                @Override
                public void onRewardedVideoAdClosed() {

                    //
                }

                @Override
                public void onRewardedVideoAvailabilityChanged(boolean available) {
                    //Change the in-app 'Traffic Driver' state according to availability.
                }

                @Override
                public void onRewardedVideoAdRewarded(Placement placement) {

                    onLoadStream(media);
                }

                @Override
                public void onRewardedVideoAdShowFailed(IronSourceError error) {

                    //

                }
                /*Invoked when the end user clicked on the RewardedVideo ad
                 */
                @Override
                public void onRewardedVideoAdClicked(Placement placement) {
                    //

                }

                @Override
                public void onRewardedVideoAdStarted(){
                    //
                }
                /* Invoked when the video ad finishes plating. */
                @Override
                public void onRewardedVideoAdEnded(){

                    //
                }
            });
        }

        private void onLoadVungleAds(History media) {

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



            Vungle.playAd(settingsManager.getSettings().getVungleRewardPlacementName(), new AdConfig(), new PlayAdCallback() {
                @Override
                public void onAdStart(String placementReferenceID) {
                    //
                }

                @Override
                public void onAdViewed(String placementReferenceID) {
                    //
                }



                @Override
                public void onAdEnd(String id, boolean completed, boolean isCTAClicked) {

                    onLoadStream(media);
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


        private void onLoadFaceBookRewardAds(History media) {
            com.facebook.ads.InterstitialAd facebookInterstitialAd = new com.facebook.ads.InterstitialAd(context,settingsManager.getSettings().getAdUnitIdFacebookInterstitialAudience());

            InterstitialAdListener interstitialAdListener = new InterstitialAdListener() {

                @Override
                public void onError(com.facebook.ads.Ad ad, AdError adError) {

                    //

                }

                @Override
                public void onAdLoaded(com.facebook.ads.Ad ad) {

                    facebookInterstitialAd.show();

                }

                @Override
                public void onAdClicked(com.facebook.ads.Ad ad) {

                    //

                }

                @Override
                public void onLoggingImpression(com.facebook.ads.Ad ad) {


                    //vvvvvv
                }

                @Override
                public void onInterstitialDisplayed(com.facebook.ads.Ad ad) {

                    //

                }

                @Override
                public void onInterstitialDismissed(com.facebook.ads.Ad ad) {

                    onLoadStream(media);

                }


            };


            facebookInterstitialAd.loadAd(
                    facebookInterstitialAd.buildLoadAdConfig()
                            .withAdListener(interstitialAdListener)
                            .build());


        }


        private void onLoadAppOdealRewardAds(History media) {

            Appodeal.show((BaseActivity) context, Appodeal.REWARDED_VIDEO);

            Appodeal.setRewardedVideoCallbacks(new RewardedVideoCallbacks() {
                @Override
                public void onRewardedVideoLoaded(boolean isPrecache) {

                    //

                }

                @Override
                public void onRewardedVideoFailedToLoad() {

                    //


                }

                @Override
                public void onRewardedVideoShown() {


                    //


                }

                @Override
                public void onRewardedVideoShowFailed() {

                    //

                }

                @Override
                public void onRewardedVideoClicked() {
                    //


                }

                @Override
                public void onRewardedVideoFinished(double amount, String name) {

                    onLoadStream(media);

                }

                @Override
                public void onRewardedVideoClosed(boolean finished) {

                    //

                }

                @Override
                public void onRewardedVideoExpired() {


                    //


                }

            });

        }


        private void onLoadAdmobRewardAds(History media) {

            if (mRewardedAd == null) {
                Toast.makeText(context, "The rewarded ad wasn't ready yet", Toast.LENGTH_SHORT).show();
                return;
            }

            mRewardedAd.setFullScreenContentCallback(
                    new FullScreenContentCallback() {
                        @Override
                        public void onAdShowedFullScreenContent() {
                            //
                        }

                        @Override
                        public void onAdFailedToShowFullScreenContent(com.google.android.gms.ads.@NotNull AdError adError) {
                            super.onAdFailedToShowFullScreenContent(adError);
                            mRewardedAd = null;
                        }

                        @Override
                        public void onAdDismissedFullScreenContent() {
                            mRewardedAd = null;
                            // Preload the next rewarded ad.
                            initLoadRewardedAd();
                        }
                    });
            mRewardedAd.show((BaseActivity) context, rewardItem -> onLoadStream(media));


        }


        public void initLoadRewardedAd() {


            if (mRewardedAd == null) {
                isLoading = true;
                AdRequest adRequest = new AdRequest.Builder().build();
                RewardedAd.load(
                        context,
                        settingsManager.getSettings().getAdUnitIdRewarded(),
                        adRequest,
                        new RewardedAdLoadCallback() {
                            @Override
                            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {

                                mRewardedAd = null;

                                isLoading = false;

                            }

                            @Override
                            public void onAdLoaded(@NonNull RewardedAd rewardedAd) {

                                isLoading = false;
                                mRewardedAd = rewardedAd;
                            }
                        });
            }

        }

        private void onLoadUnityAds(History media) {


            UnityAds.load(settingsManager.getSettings().getUnityRewardPlacementId(), new IUnityAdsLoadListener() {
                @Override
                public void onUnityAdsAdLoaded(String placementId) {

                    UnityAds.show ((BaseActivity) context, settingsManager.getSettings().getUnityRewardPlacementId(), new IUnityAdsShowListener() {
                        @Override
                        public void onUnityAdsShowFailure(String placementId, UnityAds.UnityAdsShowError error, String message) {
                            //
                        }

                        @Override
                        public void onUnityAdsShowStart(String placementId) {
                            //
                        }

                        @Override
                        public void onUnityAdsShowClick(String placementId) {
                            //
                        }

                        @Override
                        public void onUnityAdsShowComplete(String placementId, UnityAds.UnityAdsShowCompletionState state) {

                            onLoadStream(media);
                        }
                    });

                }

                @Override
                public void onUnityAdsFailedToLoad(String placementId, UnityAds.UnityAdsLoadError error, String message) {

                    //

                }
            });


        }

        private void onLoadStream(History history) {

            if (history.getType().equals("0")) {

                mediaRepository.getMovie(history.getId(),settingsManager.getSettings().getApiKey())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<>() {
                            @Override
                            public void onSubscribe(@NotNull Disposable d) {

                                //

                            }

                            @SuppressLint("StaticFieldLeak")
                            @Override
                            public void onNext(@NotNull Media media) {


                                if (media.getEnableStream() !=1) {
                                    Toast.makeText(context, context.getString(R.string.stream_is_currently_not_available_for_this_media), Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                CastSession castSession = CastContext.getSharedInstance(context).getSessionManager()
                                        .getCurrentCastSession();

                                if (settingsManager.getSettings().getServerDialogSelection() == 1) {

                                    String[] charSequence = new String[media.getVideos().size()];
                                    for (int i = 0; i < media.getVideos().size(); i++) {
                                        charSequence[i] = String.valueOf(media.getVideos().get(i).getServer());

                                    }

                                    final AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.MyAlertDialogTheme);
                                    builder.setTitle(R.string.select_quality);

                                    builder.setCancelable(true);

                                    builder.setItems(charSequence, (dialogInterface, wich) -> {



                                        if (media.getVideos().get(wich).getHeader() !=null && !media.getVideos().get(wich).getHeader().isEmpty()) {

                                            PLAYER_HEADER = media.getVideos().get(wich).getHeader();
                                        }


                                        if (media.getVideos().get(wich).getUseragent() !=null && !media.getVideos().get(wich).getUseragent().isEmpty()) {

                                            PLAYER_USER_AGENT = media.getVideos().get(wich).getUseragent();
                                        }





                                        if (media.getVideos().get(wich).getEmbed() == 1) {


                                                Intent intent = new Intent(context, EmbedActivity.class);
                                                intent.putExtra(Constants.MOVIE_LINK, media.getVideos().get(0).getLink());
                                                context.startActivity(intent);


                                            } else if (media.getVideos().get(wich).getSupportedHosts() == 1) {

                                                easyPlexSupportedHosts = new EasyPlexSupportedHosts(context);

                                                if (settingsManager.getSettings().getHxfileApiKey() != null && !settingsManager.getSettings().getHxfileApiKey().isEmpty()) {

                                                    easyPlexSupportedHosts.setApikey(settingsManager.getSettings().getHxfileApiKey());
                                                }

                                                easyPlexSupportedHosts.setMainApiServer(SERVER_BASE_URL);

                                                easyPlexSupportedHosts.onFinish(new EasyPlexSupportedHosts.OnTaskCompleted() {

                                                    @Override
                                                    public void onTaskCompleted(ArrayList<EasyPlexSupportedHostsModel> vidURL, boolean multipleQuality) {

                                                        if (multipleQuality) {
                                                            if (vidURL != null) {

                                                                CharSequence[] name = new CharSequence[vidURL.size()];

                                                                for (int i = 0; i < vidURL.size(); i++) {
                                                                    name[i] = vidURL.get(i).getQuality();
                                                                }


                                                                final AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.MyAlertDialogTheme);
                                                                builder.setTitle(context.getString(R.string.select_qualities));
                                                                builder.setCancelable(true);
                                                                builder.setItems(name, (dialogInterface1, i) -> {

                                                                    if (castSession !=null && castSession.isConnected()) {

                                                                        onLoadChromCastMovies(castSession,vidURL.get(i).getUrl(), media);

                                                                    } else   if (settingsManager.getSettings().getVlc() == 1) {



                                                                        final Dialog dialog = new Dialog(context);
                                                                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                                                        dialog.setContentView(R.layout.dialog_bottom_stream);
                                                                        dialog.setCancelable(false);
                                                                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

                                                                        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                                                                        lp.copyFrom(dialog.getWindow().getAttributes());

                                                                        lp.gravity = Gravity.BOTTOM;
                                                                        lp.width = MATCH_PARENT;
                                                                        lp.height = MATCH_PARENT;


                                                                        LinearLayout vlc = dialog.findViewById(R.id.vlc);
                                                                        LinearLayout mxPlayer = dialog.findViewById(R.id.mxPlayer);
                                                                        LinearLayout easyplexPlayer = dialog.findViewById(R.id.easyplexPlayer);
                                                                        LinearLayout webcast = dialog.findViewById(R.id.webCast);
                                                                        

                                                                        vlc.setOnClickListener(v12 -> {
                                                                            Tools.streamMediaFromVlc(context,vidURL.get(i).getUrl(),media,settingsManager, media.getVideos().get(wich));
                                                                            dialog.hide();
                                                                        });

                                                                        mxPlayer.setOnClickListener(v12 -> {
                                                                            Tools.streamMediaFromMxPlayer(context,vidURL.get(i).getUrl(),media,settingsManager,media.getVideos().get(wich));
                                                                            dialog.hide();

                                                                        });

                                                                        webcast.setOnClickListener(v12 -> {
                                                                            Tools.streamMediaFromMxWebcast(context,vidURL.get(i).getUrl(),media,settingsManager);
                                                                            dialog.hide();

                                                                        });
                                                                        

                                                                        easyplexPlayer.setOnClickListener(v12 -> {

                                                                            onLoadMainPlayerStreamMovie(media, wich, history, vidURL.get(i).getUrl());

                                                                            dialog.hide();


                                                                        });

                                                                        dialog.show();
                                                                        dialog.getWindow().setAttributes(lp);

                                                                        dialog.findViewById(R.id.bt_close).setOnClickListener(x ->

                                                                        dialog.dismiss());

                                                                        dialog.show();
                                                                        dialog.getWindow().setAttributes(lp);



                                                                    }else {

                                                                        onLoadMainPlayerStreamMovie(media, wich, history, vidURL.get(i).getUrl());

                                                                    }
                                                                });

                                                                builder.show();


                                                            } else
                                                                Toast.makeText(context, "NULL", Toast.LENGTH_SHORT).show();

                                                        } else {


                                                            if (castSession !=null && castSession.isConnected()) {

                                                                onLoadChromCastMovies(castSession,vidURL.get(0).getUrl(), media);

                                                            } else   if (settingsManager.getSettings().getVlc() == 1) {



                                                                final Dialog dialog = new Dialog(context);
                                                                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                                                dialog.setContentView(R.layout.dialog_bottom_stream);
                                                                dialog.setCancelable(false);
                                                                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

                                                                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                                                                lp.copyFrom(dialog.getWindow().getAttributes());

                                                                lp.gravity = Gravity.BOTTOM;
                                                                lp.width = MATCH_PARENT;
                                                                lp.height = MATCH_PARENT;


                                                                LinearLayout vlc = dialog.findViewById(R.id.vlc);
                                                                LinearLayout mxPlayer = dialog.findViewById(R.id.mxPlayer);
                                                                LinearLayout easyplexPlayer = dialog.findViewById(R.id.easyplexPlayer);
                                                                LinearLayout webcast = dialog.findViewById(R.id.webCast);



                                                                vlc.setOnClickListener(v12 -> {
                                                                    Tools.streamMediaFromVlc(context,vidURL.get(0).getUrl(),media,settingsManager, media.getVideos().get(wich));
                                                                    dialog.hide();
                                                                });

                                                                mxPlayer.setOnClickListener(v12 -> {
                                                                    Tools.streamMediaFromMxPlayer(context,vidURL.get(0).getUrl(),media,settingsManager, media.getVideos().get(wich));
                                                                    dialog.hide();

                                                                });

                                                                webcast.setOnClickListener(v12 -> {
                                                                    Tools.streamMediaFromMxWebcast(context,vidURL.get(0).getUrl(),media,settingsManager);
                                                                    dialog.hide();

                                                                });

                                                                easyplexPlayer.setOnClickListener(v12 -> {

                                                                    onLoadMainPlayerStreamMovie(media, wich, history, vidURL.get(0).getUrl());


                                                                    dialog.hide();


                                                                });

                                                                dialog.show();
                                                                dialog.getWindow().setAttributes(lp);
                                                                dialog.findViewById(R.id.bt_close).setOnClickListener(x ->
                                                                dialog.dismiss());
                                                                dialog.show();
                                                                dialog.getWindow().setAttributes(lp);



                                                            }else {

                                                                onLoadMainPlayerStreamMovie(media, wich, history, vidURL.get(0).getUrl());

                                                            }


                                                        }

                                                    }

                                                    @Override
                                                    public void onError() {

                                                        Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
                                                    }
                                                });

                                                easyPlexSupportedHosts.find(media.getVideos().get(wich).getLink());

                                            } else {


                                            if (castSession !=null && castSession.isConnected()) {

                                                onLoadChromCastMovies(castSession,media.getVideos().get(wich).getLink(), media);

                                            } else   if (settingsManager.getSettings().getVlc() == 1) {


                                                final Dialog dialog = new Dialog(context);
                                                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                                dialog.setContentView(R.layout.dialog_bottom_stream);
                                                dialog.setCancelable(false);
                                                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

                                                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                                                lp.copyFrom(dialog.getWindow().getAttributes());

                                                lp.gravity = Gravity.BOTTOM;
                                                lp.width = MATCH_PARENT;
                                                lp.height = MATCH_PARENT;


                                                LinearLayout vlc = dialog.findViewById(R.id.vlc);
                                                LinearLayout mxPlayer = dialog.findViewById(R.id.mxPlayer);
                                                LinearLayout easyplexPlayer = dialog.findViewById(R.id.easyplexPlayer);
                                                LinearLayout webcast = dialog.findViewById(R.id.webCast);


                                                vlc.setOnClickListener(v12 -> {
                                                    Tools.streamMediaFromVlc(context,media.getVideos().get(wich).getLink(),media,settingsManager, media.getVideos().get(wich));
                                                    dialog.hide();
                                                });

                                                mxPlayer.setOnClickListener(v12 -> {
                                                    Tools.streamMediaFromMxPlayer(context,media.getVideos().get(wich).getLink(),media,settingsManager,media.getVideos().get(wich));
                                                    dialog.hide();

                                                });

                                                webcast.setOnClickListener(v12 -> {
                                                    Tools.streamMediaFromMxWebcast(context,media.getVideos().get(wich).getLink(),media,settingsManager);
                                                    dialog.hide();

                                                });


                                                easyplexPlayer.setOnClickListener(v12 -> {

                                                 onLoadMainPlayerStreamMovie(media, wich, history, media.getVideos().get(wich).getLink());

                                                dialog.hide();


                                                });

                                                dialog.show();
                                                dialog.getWindow().setAttributes(lp);

                                                dialog.findViewById(R.id.bt_close).setOnClickListener(x ->

                                                        dialog.dismiss());

                                                dialog.show();
                                                dialog.getWindow().setAttributes(lp);



                                            }else {

                                                onLoadMainPlayerStreamMovie(media, wich, history, media.getVideos().get(wich).getLink());
                                            }
                                        }

                                        dialogInterface.dismiss();

                                    });

                                    builder.show();

                                } else {



                                    if (media.getVideos().get(0).getHeader() !=null && !media.getVideos().get(0).getHeader().isEmpty()) {

                                        PLAYER_HEADER = media.getVideos().get(0).getHeader();
                                    }


                                    if (media.getVideos().get(0).getUseragent() !=null && !media.getVideos().get(0).getUseragent().isEmpty()) {

                                        PLAYER_USER_AGENT = media.getVideos().get(0).getUseragent();
                                    }


                                    if (media.getVideos().get(0).getEmbed() == 1) {


                                        Intent intent = new Intent(context, EmbedActivity.class);
                                        intent.putExtra(Constants.MOVIE_LINK, media.getVideos().get(0).getLink());
                                        context.startActivity(intent);


                                    } else if (media.getVideos().get(0).getSupportedHosts() == 1) {

                                        easyPlexSupportedHosts = new EasyPlexSupportedHosts(context);

                                        if (settingsManager.getSettings().getHxfileApiKey() != null && !settingsManager.getSettings().getHxfileApiKey().isEmpty()) {

                                            easyPlexSupportedHosts.setApikey(settingsManager.getSettings().getHxfileApiKey());
                                        }

                                        easyPlexSupportedHosts.setMainApiServer(SERVER_BASE_URL);

                                        easyPlexSupportedHosts.onFinish(new EasyPlexSupportedHosts.OnTaskCompleted() {

                                            @Override
                                            public void onTaskCompleted(ArrayList<EasyPlexSupportedHostsModel> vidURL, boolean multipleQuality) {

                                                if (multipleQuality) {
                                                    if (vidURL != null) {

                                                        CharSequence[] name = new CharSequence[vidURL.size()];

                                                        for (int i = 0; i < vidURL.size(); i++) {
                                                            name[i] = vidURL.get(i).getQuality();
                                                        }


                                                        final AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.MyAlertDialogTheme);
                                                        builder.setTitle(context.getString(R.string.select_qualities));
                                                        builder.setCancelable(true);
                                                        builder.setItems(name, (dialogInterface, i) -> {


                                                            if (castSession !=null && castSession.isConnected()) {

                                                                onLoadChromCastMovies(castSession,vidURL.get(i).getUrl(), media);

                                                            } else   if (settingsManager.getSettings().getVlc() == 1) {



                                                                final Dialog dialog = new Dialog(context);
                                                                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                                                dialog.setContentView(R.layout.dialog_bottom_stream);
                                                                dialog.setCancelable(false);
                                                                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

                                                                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                                                                lp.copyFrom(dialog.getWindow().getAttributes());

                                                                lp.gravity = Gravity.BOTTOM;
                                                                lp.width = MATCH_PARENT;
                                                                lp.height = MATCH_PARENT;


                                                                LinearLayout vlc = dialog.findViewById(R.id.vlc);
                                                                LinearLayout mxPlayer = dialog.findViewById(R.id.mxPlayer);
                                                                LinearLayout easyplexPlayer = dialog.findViewById(R.id.easyplexPlayer);
                                                                LinearLayout webcast = dialog.findViewById(R.id.webCast);


                                                                vlc.setOnClickListener(v12 -> {
                                                                    Tools.streamMediaFromVlc(context,vidURL.get(i).getUrl(),media,settingsManager, media.getVideos().get(0));
                                                                    dialog.hide();
                                                                });

                                                                mxPlayer.setOnClickListener(v12 -> {
                                                                    Tools.streamMediaFromMxPlayer(context,vidURL.get(i).getUrl(),media,settingsManager,media.getVideos().get(0));
                                                                    dialog.hide();

                                                                });

                                                                webcast.setOnClickListener(v12 -> {
                                                                    Tools.streamMediaFromMxWebcast(context,vidURL.get(i).getUrl(),media,settingsManager);
                                                                    dialog.hide();

                                                                });


                                                                easyplexPlayer.setOnClickListener(v12 -> {

                                                                    onLoadMainPlayerStreamMovie(media, 0, history, vidURL.get(i).getUrl());

                                                                    dialog.hide();


                                                                });

                                                                dialog.show();
                                                                dialog.getWindow().setAttributes(lp);

                                                                dialog.findViewById(R.id.bt_close).setOnClickListener(x ->

                                                                        dialog.dismiss());

                                                                dialog.show();
                                                                dialog.getWindow().setAttributes(lp);



                                                            }else {

                                                                onLoadMainPlayerStreamMovie(media, 0, history, vidURL.get(i).getUrl());

                                                            }
                                                        });

                                                        builder.show();


                                                    } else
                                                        Toast.makeText(context, "NULL", Toast.LENGTH_SHORT).show();

                                                } else {


                                                    if (castSession !=null && castSession.isConnected()) {

                                                        onLoadChromCastMovies(castSession,vidURL.get(0).getUrl(), media);

                                                    } else   if (settingsManager.getSettings().getVlc() == 1) {



                                                        final Dialog dialog = new Dialog(context);
                                                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                                        dialog.setContentView(R.layout.dialog_bottom_stream);
                                                        dialog.setCancelable(false);
                                                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

                                                        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                                                        lp.copyFrom(dialog.getWindow().getAttributes());

                                                        lp.gravity = Gravity.BOTTOM;
                                                        lp.width = MATCH_PARENT;
                                                        lp.height = MATCH_PARENT;


                                                        LinearLayout vlc = dialog.findViewById(R.id.vlc);
                                                        LinearLayout mxPlayer = dialog.findViewById(R.id.mxPlayer);
                                                        LinearLayout easyplexPlayer = dialog.findViewById(R.id.easyplexPlayer);
                                                        LinearLayout webcast = dialog.findViewById(R.id.webCast);


                                                        vlc.setOnClickListener(v12 -> {
                                                            Tools.streamMediaFromVlc(context,vidURL.get(0).getUrl(),media,settingsManager, media.getVideos().get(0));
                                                            dialog.hide();
                                                        });

                                                        mxPlayer.setOnClickListener(v12 -> {
                                                            Tools.streamMediaFromMxPlayer(context,vidURL.get(0).getUrl(),media,settingsManager,media.getVideos().get(0));
                                                            dialog.hide();

                                                        });

                                                        webcast.setOnClickListener(v12 -> {
                                                            Tools.streamMediaFromMxWebcast(context,vidURL.get(0).getUrl(),media,settingsManager);
                                                            dialog.hide();

                                                        });


                                                        easyplexPlayer.setOnClickListener(v12 -> {

                                                            onLoadMainPlayerStreamMovie(media, 0, history, vidURL.get(0).getUrl());

                                                            dialog.hide();


                                                        });

                                                        dialog.show();
                                                        dialog.getWindow().setAttributes(lp);

                                                        dialog.findViewById(R.id.bt_close).setOnClickListener(x ->

                                                                dialog.dismiss());

                                                        dialog.show();
                                                        dialog.getWindow().setAttributes(lp);



                                                    }else {

                                                        onLoadMainPlayerStreamMovie(media, 0, history, vidURL.get(0).getUrl());

                                                    }
                                                }

                                            }

                                            @Override
                                            public void onError() {

                                                Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
                                            }
                                        });

                                        easyPlexSupportedHosts.find(media.getVideos().get(0).getLink());


                                    } else if (settingsManager.getSettings().getVlc() == 1) {


                                        final Dialog dialog = new Dialog(context);
                                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                        dialog.setContentView(R.layout.dialog_bottom_stream);
                                        dialog.setCancelable(false);
                                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

                                        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                                        lp.copyFrom(dialog.getWindow().getAttributes());

                                        lp.gravity = Gravity.BOTTOM;
                                        lp.width = MATCH_PARENT;
                                        lp.height = MATCH_PARENT;

                                        LinearLayout vlc = dialog.findViewById(R.id.vlc);
                                        LinearLayout mxPlayer = dialog.findViewById(R.id.mxPlayer);
                                        LinearLayout easyplexPlayer = dialog.findViewById(R.id.easyplexPlayer);
                                        LinearLayout webcast = dialog.findViewById(R.id.webCast);


                                        vlc.setOnClickListener(v12 -> {
                                            Tools.streamMediaFromVlc(context,media.getVideos().get(0).getLink(),media,settingsManager, media.getVideos().get(0));
                                            dialog.hide();
                                        });

                                        mxPlayer.setOnClickListener(v12 -> {
                                            Tools.streamMediaFromMxPlayer(context,media.getVideos().get(0).getLink(),media,settingsManager, media.getVideos().get(0));
                                            dialog.hide();

                                        });

                                        webcast.setOnClickListener(v12 -> {
                                            Tools.streamMediaFromMxWebcast(context,media.getVideos().get(0).getLink(),media,settingsManager);
                                            dialog.hide();

                                        });

                                        easyplexPlayer.setOnClickListener(v12 -> {


                                            onLoadMainPlayerStreamMovie(media, 0, history, media.getVideos().get(0).getLink());
                                            dialog.hide();


                                        });

                                        dialog.show();
                                        dialog.getWindow().setAttributes(lp);

                                        dialog.findViewById(R.id.bt_close).setOnClickListener(x ->

                                                dialog.dismiss());


                                        dialog.show();
                                        dialog.getWindow().setAttributes(lp);


                                    } else {

                                        onLoadMainPlayerStreamMovie(media, 0, history, media.getVideos().get(0).getLink());

                                    }

                                }


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


            }else if (history.getType().equals("1")){

                mediaRepository.getSerieEpisodeDetails(history.getEpisodeId(),settingsManager.getSettings().getApiKey())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .cache()
                        .subscribe(new Observer<>() {
                            @Override
                            public void onSubscribe(@NotNull Disposable d) {

                                //

                            }

                            @Override
                            public void onNext(@NotNull MovieResponse movieResponse) {


                                if (movieResponse.getEpisodes().get(0).getEnableStream() !=1) {
                                    Toast.makeText(context, context.getString(R.string.stream_is_currently_not_available_for_this_media), Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                if (movieResponse.getEpisodes().get(0).getVideos() !=null && !movieResponse.getEpisodes().get(0).getVideos().isEmpty()) {


                                    if (settingsManager.getSettings().getServerDialogSelection() == 1) {


                                    String[] charSequence = new String[movieResponse.getEpisodes().get(0).getVideos().size()];
                                    for (int i = 0; i < movieResponse.getEpisodes().get(0).getVideos().size(); i++) {
                                        charSequence[i] = String.valueOf(movieResponse.getEpisodes().get(0).getVideos().get(i).getServer());

                                    }

                                    final AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.MyAlertDialogTheme);
                                    builder.setTitle(R.string.source_quality);
                                    builder.setCancelable(true);
                                    builder.setItems(charSequence, (dialogInterface, wich) -> {


                                        if (movieResponse.getEpisodes().get(0).getVideos().get(wich).getHeader() !=null &&
                                                !movieResponse.getEpisodes().get(0).getVideos().get(wich).getHeader().isEmpty()) {

                                            PLAYER_HEADER = movieResponse.getEpisodes().get(0).getVideos().get(wich).getHeader();
                                        }


                                        if (movieResponse.getEpisodes().get(0).getVideos().get(wich).getUseragent() !=null
                                                && !movieResponse.getEpisodes().get(0).getVideos().get(wich).getUseragent().isEmpty()) {

                                            PLAYER_USER_AGENT = movieResponse.getEpisodes().get(0).getVideos().get(wich).getUseragent();
                                        }


                                          if (movieResponse.getEpisodes().get(0).getVideos().get(wich).getEmbed() == 1) {


                                                onLoadMainPlayerStreamEmbed(movieResponse.getEpisodes().get(0).getVideos().get(wich).getLink());


                                            } else if (movieResponse.getEpisodes().get(0).getVideos().get(wich).getSupportedHosts() == 1) {

                                                easyPlexSupportedHosts = new EasyPlexSupportedHosts(context);

                                                if (settingsManager.getSettings().getHxfileApiKey() != null && !settingsManager.getSettings().getHxfileApiKey().isEmpty()) {

                                                    easyPlexSupportedHosts.setApikey(settingsManager.getSettings().getHxfileApiKey());
                                                }

                                                easyPlexSupportedHosts.setMainApiServer(SERVER_BASE_URL);

                                                easyPlexSupportedHosts.onFinish(new EasyPlexSupportedHosts.OnTaskCompleted() {

                                                    @Override
                                                    public void onTaskCompleted(ArrayList<EasyPlexSupportedHostsModel> vidURL, boolean multipleQuality) {

                                                        if (multipleQuality) {
                                                            if (vidURL != null) {

                                                                CharSequence[] name = new CharSequence[vidURL.size()];

                                                                for (int i = 0; i < vidURL.size(); i++) {
                                                                    name[i] = vidURL.get(i).getQuality();
                                                                }

                                                                final AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.MyAlertDialogTheme);
                                                                builder.setTitle(context.getString(R.string.select_qualities));
                                                                builder.setCancelable(true);
                                                                builder.setItems(name, (dialogInterface, i) -> {


                                                                    CastSession castSession = CastContext.getSharedInstance(context).getSessionManager()
                                                                            .getCurrentCastSession();
                                                                    if (castSession != null && castSession.isConnected()) {

                                                                       onLoadChomecastHistory(movieResponse,vidURL.get(i).getUrl(),castSession,binding.itemMovieImage,history);


                                                                    } else if (settingsManager.getSettings().getVlc() == 1) {


                                                                        final Dialog dialog = new Dialog(context);
                                                                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                                                        dialog.setContentView(R.layout.dialog_bottom_stream);
                                                                        dialog.setCancelable(false);
                                                                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

                                                                        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                                                                        lp.copyFrom(dialog.getWindow().getAttributes());

                                                                        lp.gravity = Gravity.BOTTOM;
                                                                        lp.width = MATCH_PARENT;
                                                                        lp.height = MATCH_PARENT;


                                                                        LinearLayout vlc = dialog.findViewById(R.id.vlc);
                                                                        LinearLayout mxPlayer = dialog.findViewById(R.id.mxPlayer);
                                                                        LinearLayout easyplexPlayer = dialog.findViewById(R.id.easyplexPlayer);
                                                                        LinearLayout webcast = dialog.findViewById(R.id.webCast);


                                                                        vlc.setOnClickListener(v12 -> {
                                                                            Tools.streamEpisodeFromVlc(context,vidURL.get(i).getUrl(),movieResponse.getEpisodes().get(0),settingsManager);
                                                                            dialog.hide();
                                                                        });

                                                                        mxPlayer.setOnClickListener(v12 -> {
                                                                            Tools.streamEpisodeFromMxPlayer(context,vidURL.get(i).getUrl(),movieResponse.getEpisodes().get(0),settingsManager);
                                                                            dialog.hide();

                                                                        });

                                                                        webcast.setOnClickListener(v12 -> {

                                                                            Tools.streamEpisodeFromMxWebcast(context,vidURL.get(i).getUrl(),movieResponse.getEpisodes().get(0),settingsManager);
                                                                            dialog.hide();

                                                                        });


                                                                        easyplexPlayer.setOnClickListener(v12 -> {

                                                                            onLoadMainPlayerStreamSeries(movieResponse, history, wich, vidURL.get(i).getUrl());
                                                                            dialog.hide();


                                                                        });

                                                                        dialog.show();
                                                                        dialog.getWindow().setAttributes(lp);

                                                                        dialog.findViewById(R.id.bt_close).setOnClickListener(x ->

                                                                                dialog.dismiss());


                                                                        dialog.show();
                                                                        dialog.getWindow().setAttributes(lp);


                                                                    } else {

                                                                        onLoadMainPlayerStreamSeries(movieResponse, history, wich, vidURL.get(i).getUrl());
                                                                    }

                                                                });

                                                                builder.show();


                                                            } else
                                                                Toast.makeText(context, "NULL", Toast.LENGTH_SHORT).show();

                                                        } else {

                                                            CastSession castSession = CastContext.getSharedInstance(context).getSessionManager()
                                                                    .getCurrentCastSession();
                                                            if (castSession != null && castSession.isConnected()) {

                                                                onLoadChomecastHistory(movieResponse,vidURL.get(0).getUrl(),castSession,binding.itemMovieImage,history);


                                                            } else if (settingsManager.getSettings().getVlc() == 1) {

                                                                final Dialog dialog = new Dialog(context);
                                                                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                                                dialog.setContentView(R.layout.dialog_bottom_stream);
                                                                dialog.setCancelable(false);
                                                                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

                                                                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                                                                lp.copyFrom(dialog.getWindow().getAttributes());

                                                                lp.gravity = Gravity.BOTTOM;
                                                                lp.width = MATCH_PARENT;
                                                                lp.height = MATCH_PARENT;


                                                                LinearLayout vlc = dialog.findViewById(R.id.vlc);
                                                                LinearLayout mxPlayer = dialog.findViewById(R.id.mxPlayer);
                                                                LinearLayout easyplexPlayer = dialog.findViewById(R.id.easyplexPlayer);
                                                                LinearLayout webcast = dialog.findViewById(R.id.webCast);


                                                                vlc.setOnClickListener(v12 -> {
                                                                    Tools.streamEpisodeFromVlc(context,vidURL.get(0).getUrl(),movieResponse.getEpisodes().get(0),settingsManager);
                                                                    dialog.hide();
                                                                });

                                                                mxPlayer.setOnClickListener(v12 -> {
                                                                    Tools.streamEpisodeFromMxPlayer(context,vidURL.get(0).getUrl(),movieResponse.getEpisodes().get(0),settingsManager);
                                                                    dialog.hide();

                                                                });

                                                                webcast.setOnClickListener(v12 -> {

                                                                    Tools.streamEpisodeFromMxWebcast(context,vidURL.get(0).getUrl(),movieResponse.getEpisodes().get(0),settingsManager);
                                                                    dialog.hide();

                                                                });


                                                                easyplexPlayer.setOnClickListener(v12 -> {

                                                                    onLoadMainPlayerStreamSeries(movieResponse, history, wich, vidURL.get(0).getUrl());
                                                                    dialog.hide();


                                                                });

                                                                dialog.show();
                                                                dialog.getWindow().setAttributes(lp);

                                                                dialog.findViewById(R.id.bt_close).setOnClickListener(x ->

                                                                dialog.dismiss());


                                                                dialog.show();
                                                                dialog.getWindow().setAttributes(lp);


                                                            } else {

                                                                onLoadMainPlayerStreamSeries(movieResponse, history, wich, vidURL.get(0).getUrl());
                                                            }

                                                        }

                                                    }

                                                    @Override
                                                    public void onError() {

                                                        Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
                                                    }
                                                });

                                                easyPlexSupportedHosts.find(movieResponse.getEpisodes().get(0).getVideos().get(wich).getLink());


                                            } else {


                                                if (settingsManager.getSettings().getVlc() == 1) {


                                                    final Dialog dialog = new Dialog(context);
                                                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                                    dialog.setContentView(R.layout.dialog_bottom_stream);
                                                    dialog.setCancelable(false);
                                                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

                                                    WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                                                    lp.copyFrom(dialog.getWindow().getAttributes());

                                                    lp.gravity = Gravity.BOTTOM;
                                                    lp.width = MATCH_PARENT;
                                                    lp.height = MATCH_PARENT;


                                                    LinearLayout vlc = dialog.findViewById(R.id.vlc);
                                                    LinearLayout mxPlayer = dialog.findViewById(R.id.mxPlayer);
                                                    LinearLayout easyplexPlayer = dialog.findViewById(R.id.easyplexPlayer);
                                                    LinearLayout webcast = dialog.findViewById(R.id.webCast);


                                                    vlc.setOnClickListener(v12 -> {
                                                        Tools.streamEpisodeFromVlc(context,movieResponse.getEpisodes().get(0).getVideos().get(wich).getLink()
                                                                ,movieResponse.getEpisodes().get(0),settingsManager);
                                                        dialog.hide();
                                                    });

                                                    mxPlayer.setOnClickListener(v12 -> {
                                                        Tools.streamEpisodeFromMxPlayer(context,
                                                                movieResponse.getEpisodes().get(0).getVideos().get(wich).getLink(),movieResponse.getEpisodes().get(0),settingsManager);
                                                        dialog.hide();

                                                    });

                                                    webcast.setOnClickListener(v12 -> {

                                                        Tools.streamEpisodeFromMxWebcast(context,
                                                                movieResponse.getEpisodes().get(0).getVideos().get(wich).getLink(),movieResponse.getEpisodes().get(0),settingsManager);
                                                        dialog.hide();

                                                    });


                                                    easyplexPlayer.setOnClickListener(v12 -> {

                                                        onLoadMainPlayerStreamSeries(movieResponse, history, wich, movieResponse.getEpisodes().get(0).getVideos().get(wich).getLink());

                                                        dialog.hide();


                                                    });

                                                    dialog.show();
                                                    dialog.getWindow().setAttributes(lp);

                                                    dialog.findViewById(R.id.bt_close).setOnClickListener(x ->

                                                            dialog.dismiss());


                                                    dialog.show();
                                                    dialog.getWindow().setAttributes(lp);


                                                } else {

                                                    onLoadMainPlayerStreamSeries(movieResponse, history, wich, movieResponse.getEpisodes().get(0).getVideos().get(wich).getLink());

                                                }

                                            }

                                    });

                                    builder.show();

                                } else {


                                    if (movieResponse.getEpisodes().get(0).getVideos().get(0).getHeader() !=null &&
                                            !movieResponse.getEpisodes().get(0).getVideos().get(0).getHeader().isEmpty()) {

                                        PLAYER_HEADER = movieResponse.getEpisodes().get(0).getVideos().get(0).getHeader();
                                    }


                                    if (movieResponse.getEpisodes().get(0).getVideos().get(0).getUseragent() !=null
                                            && !movieResponse.getEpisodes().get(0).getVideos().get(0).getUseragent().isEmpty()) {

                                        PLAYER_USER_AGENT = movieResponse.getEpisodes().get(0).getVideos().get(0).getUseragent();
                                    }

                                    CastSession castSession = CastContext.getSharedInstance(context).getSessionManager()
                                            .getCurrentCastSession();
                                    if (castSession != null && castSession.isConnected()) {

                                        MediaMetadata movieMetadata = new MediaMetadata(MediaMetadata.MEDIA_TYPE_MOVIE);
                                        movieMetadata.putString(MediaMetadata.KEY_TITLE, movieResponse.getEpisodes().get(0).getName());
                                        movieMetadata.putString(MediaMetadata.KEY_SUBTITLE, movieResponse.getEpisodes().get(0).getName());

                                        movieMetadata.addImage(new WebImage(Uri.parse(movieResponse.getEpisodes().get(0).getStillPath())));
                                        List<MediaTrack> tracks = new ArrayList<>();


                                        MediaInfo mediaInfo = new MediaInfo.Builder(movieResponse.getEpisodes().get(0).getVideos().get(0).getLink())
                                                .setStreamType(MediaInfo.STREAM_TYPE_BUFFERED)
                                                .setMetadata(movieMetadata)
                                                .setMediaTracks(tracks)
                                                .build();

                                        final RemoteMediaClient remoteMediaClient = castSession.getRemoteMediaClient();
                                        if (remoteMediaClient == null) {
                                            Timber.tag(TAG).w("showQueuePopup(): null RemoteMediaClient");
                                            return;
                                        }
                                        final QueueDataProvider provider = QueueDataProvider.getInstance(context);
                                        PopupMenu popup = new PopupMenu(context, binding.itemMovieImage);
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
                                                            MediaStatus.REPEAT_MODE_REPEAT_OFF, null);
                                                } else {
                                                    return false;
                                                }
                                            } else {
                                                if (provider1.getCount() == 0) {
                                                    remoteMediaClient.queueLoad(newItemArray, 0,
                                                            MediaStatus.REPEAT_MODE_REPEAT_OFF, null);
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
                                            if (!TextUtils.isEmpty(toastMessage)) {
                                                Toast.makeText(context, toastMessage, Toast.LENGTH_SHORT).show();
                                            }
                                            return true;
                                        };
                                        popup.setOnMenuItemClickListener(clickListener);
                                        popup.show();

                                    } else {

                                        if (movieResponse.getEpisodes().get(0).getVideos().get(0).getEmbed() == 1) {


                                            onLoadMainPlayerStreamEmbed(movieResponse.getEpisodes().get(0).getVideos().get(0).getLink());


                                        } else if (movieResponse.getEpisodes().get(0).getVideos().get(0).getSupportedHosts() == 1) {

                                            easyPlexSupportedHosts = new EasyPlexSupportedHosts(context);

                                            if (settingsManager.getSettings().getHxfileApiKey() != null && !settingsManager.getSettings().getHxfileApiKey().isEmpty()) {

                                                easyPlexSupportedHosts.setApikey(settingsManager.getSettings().getHxfileApiKey());
                                            }

                                            easyPlexSupportedHosts.setMainApiServer(SERVER_BASE_URL);

                                            easyPlexSupportedHosts.onFinish(new EasyPlexSupportedHosts.OnTaskCompleted() {

                                                @Override
                                                public void onTaskCompleted(ArrayList<EasyPlexSupportedHostsModel> vidURL, boolean multipleQuality) {

                                                    if (multipleQuality) {
                                                        if (vidURL != null) {

                                                            CharSequence[] name = new CharSequence[vidURL.size()];

                                                            for (int i = 0; i < vidURL.size(); i++) {
                                                                name[i] = vidURL.get(i).getQuality();
                                                            }

                                                            final AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.MyAlertDialogTheme);
                                                            builder.setTitle(context.getString(R.string.select_qualities));
                                                            builder.setCancelable(true);
                                                            builder.setItems(name, (dialogInterface, i) -> {


                                                                if (settingsManager.getSettings().getVlc() == 1) {


                                                                    final Dialog dialog = new Dialog(context);
                                                                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                                                    dialog.setContentView(R.layout.dialog_bottom_stream);
                                                                    dialog.setCancelable(false);
                                                                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

                                                                    WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                                                                    lp.copyFrom(dialog.getWindow().getAttributes());

                                                                    lp.gravity = Gravity.BOTTOM;
                                                                    lp.width = MATCH_PARENT;
                                                                    lp.height = MATCH_PARENT;


                                                                    LinearLayout vlc = dialog.findViewById(R.id.vlc);
                                                                    LinearLayout mxPlayer = dialog.findViewById(R.id.mxPlayer);
                                                                    LinearLayout easyplexPlayer = dialog.findViewById(R.id.easyplexPlayer);
                                                                    LinearLayout webcast = dialog.findViewById(R.id.webCast);


                                                                    vlc.setOnClickListener(v12 -> {
                                                                        Tools.streamEpisodeFromVlc(context,vidURL.get(i).getUrl(),movieResponse.getEpisodes().get(0),settingsManager);
                                                                        dialog.hide();
                                                                    });

                                                                    mxPlayer.setOnClickListener(v12 -> {
                                                                        Tools.streamEpisodeFromMxPlayer(context,vidURL.get(i).getUrl(),movieResponse.getEpisodes().get(0),settingsManager);
                                                                        dialog.hide();

                                                                    });

                                                                    webcast.setOnClickListener(v12 -> {

                                                                        Tools.streamEpisodeFromMxWebcast(context,vidURL.get(i).getUrl(),movieResponse.getEpisodes().get(0),settingsManager);
                                                                        dialog.hide();

                                                                    });

                                                                    easyplexPlayer.setOnClickListener(v12 -> {

                                                                        onLoadMainPlayerStreamSeries(movieResponse, history, 0, vidURL.get(i).getUrl());
                                                                        dialog.hide();


                                                                    });

                                                                    dialog.show();
                                                                    dialog.getWindow().setAttributes(lp);
                                                                    dialog.findViewById(R.id.bt_close).setOnClickListener(x ->
                                                                    dialog.dismiss());

                                                                    dialog.show();
                                                                    dialog.getWindow().setAttributes(lp);


                                                                } else {

                                                                    onLoadMainPlayerStreamSeries(movieResponse, history, 0, vidURL.get(i).getUrl());
                                                                }

                                                            });

                                                            builder.show();


                                                        } else
                                                            Toast.makeText(context, "NULL", Toast.LENGTH_SHORT).show();

                                                    } else {

                                                        onLoadMainPlayerStreamSeries(movieResponse, history, 0, vidURL.get(0).getUrl());

                                                        Timber.i("URL IS :%s", vidURL.get(0).getUrl());
                                                    }

                                                }

                                                @Override
                                                public void onError() {

                                                    Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
                                                }
                                            });

                                            easyPlexSupportedHosts.find(movieResponse.getEpisodes().get(0).getVideos().get(0).getLink());


                                        } else {

                                            if (settingsManager.getSettings().getVlc() == 1) {


                                                final Dialog dialog = new Dialog(context);
                                                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                                dialog.setContentView(R.layout.dialog_bottom_stream);
                                                dialog.setCancelable(false);
                                                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

                                                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                                                lp.copyFrom(dialog.getWindow().getAttributes());

                                                lp.gravity = Gravity.BOTTOM;
                                                lp.width = MATCH_PARENT;
                                                lp.height = MATCH_PARENT;


                                                LinearLayout vlc = dialog.findViewById(R.id.vlc);
                                                LinearLayout mxPlayer = dialog.findViewById(R.id.mxPlayer);
                                                LinearLayout easyplexPlayer = dialog.findViewById(R.id.easyplexPlayer);
                                                LinearLayout webcast = dialog.findViewById(R.id.webCast);



                                                vlc.setOnClickListener(v12 -> {
                                                    Tools.streamEpisodeFromVlc(context,
                                                            movieResponse.getEpisodes().get(0).getVideos().get(0).getLink(),movieResponse.getEpisodes().get(0),settingsManager);
                                                    dialog.hide();
                                                });

                                                mxPlayer.setOnClickListener(v12 -> {
                                                    Tools.streamEpisodeFromMxPlayer(context,movieResponse.getEpisodes().get(0).getVideos().get(0).getLink(),movieResponse.getEpisodes().get(0),settingsManager);
                                                    dialog.hide();

                                                });

                                                webcast.setOnClickListener(v12 -> {

                                                    Tools.streamEpisodeFromMxWebcast(context,
                                                            movieResponse.getEpisodes().get(0).getVideos().get(0).getLink(),movieResponse.getEpisodes().get(0),settingsManager);
                                                    dialog.hide();

                                                });


                                                easyplexPlayer.setOnClickListener(v12 -> {

                                                    onLoadMainPlayerStreamSeries(movieResponse, history, 0, movieResponse.getEpisodes().get(0).getVideos().get(0).getLink());

                                                    dialog.hide();


                                                });

                                                dialog.show();
                                                dialog.getWindow().setAttributes(lp);

                                                dialog.findViewById(R.id.bt_close).setOnClickListener(x ->

                                                        dialog.dismiss());


                                                dialog.show();
                                                dialog.getWindow().setAttributes(lp);


                                            } else {


                                                onLoadMainPlayerStreamSeries(movieResponse, history, 0, movieResponse.getEpisodes().get(0).getVideos().get(0).getLink());


                                            }

                                        }


                                    }

                                }

                                }else {

                                    DialogHelper.showNoStreamAvailable(context);
                                }

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


            }else {

                mediaRepository.getAnimeEpisodeDetails(history.getEpisodeTmdb(),settingsManager.getSettings().getApiKey())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .cache()
                        .subscribe(new Observer<>() {
                            @Override
                            public void onSubscribe(@NotNull Disposable d) {

                                //

                            }

                            @SuppressLint("StaticFieldLeak")
                            @Override
                            public void onNext(@NotNull MovieResponse movieResponse) {


                                if (movieResponse.getEpisodes().get(0).getEnableStream() !=1) {
                                    Toast.makeText(context, context.getString(R.string.stream_is_currently_not_available_for_this_media), Toast.LENGTH_SHORT).show();
                                    return;
                                }


                                if (settingsManager.getSettings().getServerDialogSelection() == 1) {


                                    String[] charSequence = new String[movieResponse.getEpisodes().get(0).getVideos().size()];
                                    for (int i = 0; i < movieResponse.getEpisodes().get(0).getVideos().size(); i++) {
                                        charSequence[i] = String.valueOf(movieResponse.getEpisodes().get(0).getVideos().get(i).getServer());

                                    }


                                    final AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.MyAlertDialogTheme);
                                    builder.setTitle(R.string.source_quality);
                                    builder.setCancelable(true);
                                    builder.setItems(charSequence, (dialogInterface, wich) -> {


                                        if (movieResponse.getEpisodes().get(0).getVideos().get(wich).getHeader() !=null &&
                                                !movieResponse.getEpisodes().get(0).getVideos().get(wich).getHeader().isEmpty()) {

                                            PLAYER_HEADER = movieResponse.getEpisodes().get(0).getVideos().get(wich).getHeader();
                                        }


                                        if (movieResponse.getEpisodes().get(0).getVideos().get(wich).getUseragent() !=null
                                                && !movieResponse.getEpisodes().get(0).getVideos().get(wich).getUseragent().isEmpty()) {

                                            PLAYER_USER_AGENT = movieResponse.getEpisodes().get(0).getVideos().get(wich).getUseragent();
                                        }


                                        if (movieResponse.getEpisodes().get(0).getVideos().get(wich).getEmbed() == 1) {


                                            Intent intent = new Intent(context, EmbedActivity.class);
                                            intent.putExtra(Constants.MOVIE_LINK, movieResponse.getEpisodes().get(0).getVideos().get(wich).getLink());
                                            context.startActivity(intent);


                                        } else if (movieResponse.getEpisodes().get(0).getVideos().get(wich).getSupportedHosts() == 1) {


                                            easyPlexSupportedHosts = new EasyPlexSupportedHosts(context);

                                            if (settingsManager.getSettings().getHxfileApiKey() != null && !settingsManager.getSettings().getHxfileApiKey().isEmpty()) {

                                                easyPlexSupportedHosts.setApikey(settingsManager.getSettings().getHxfileApiKey());
                                            }

                                            easyPlexSupportedHosts.setMainApiServer(SERVER_BASE_URL);

                                            easyPlexSupportedHosts.onFinish(new EasyPlexSupportedHosts.OnTaskCompleted() {

                                                @Override
                                                public void onTaskCompleted(ArrayList<EasyPlexSupportedHostsModel> vidURL, boolean multipleQuality) {

                                                    if (multipleQuality) {
                                                        if (vidURL != null) {
                                                            CharSequence[] name = new CharSequence[vidURL.size()];

                                                            for (int i = 0; i < vidURL.size(); i++) {
                                                                name[i] = vidURL.get(i).getQuality();
                                                            }

                                                            final AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.MyAlertDialogTheme);
                                                            builder.setTitle(context.getString(R.string.select_qualities));
                                                            builder.setCancelable(true);
                                                            builder.setItems(name, (dialogInterface, i) -> {


                                                                if (settingsManager.getSettings().getVlc() == 1) {


                                                                    final Dialog dialog = new Dialog(context);
                                                                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                                                    dialog.setContentView(R.layout.dialog_bottom_stream);
                                                                    dialog.setCancelable(false);
                                                                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

                                                                    WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                                                                    lp.copyFrom(dialog.getWindow().getAttributes());

                                                                    lp.gravity = Gravity.BOTTOM;
                                                                    lp.width = MATCH_PARENT;
                                                                    lp.height = MATCH_PARENT;


                                                                    LinearLayout vlc = dialog.findViewById(R.id.vlc);
                                                                    LinearLayout mxPlayer = dialog.findViewById(R.id.mxPlayer);
                                                                    LinearLayout easyplexPlayer = dialog.findViewById(R.id.easyplexPlayer);
                                                                    LinearLayout webcast = dialog.findViewById(R.id.webCast);


                                                                    vlc.setOnClickListener(v12 -> {
                                                                        Tools.streamEpisodeFromVlc(context,vidURL.get(i).getUrl(),movieResponse.getEpisodes().get(0),settingsManager);
                                                                        dialog.hide();
                                                                    });

                                                                    mxPlayer.setOnClickListener(v12 -> {
                                                                        Tools.streamEpisodeFromMxPlayer(context,vidURL.get(i).getUrl(),movieResponse.getEpisodes().get(0),settingsManager);
                                                                        dialog.hide();

                                                                    });

                                                                    webcast.setOnClickListener(v12 -> {

                                                                        Tools.streamEpisodeFromMxWebcast(context,vidURL.get(i).getUrl(),movieResponse.getEpisodes().get(0),settingsManager);
                                                                        dialog.hide();

                                                                    });

                                                                    easyplexPlayer.setOnClickListener(v12 -> {

                                                                        onLoadMainPlayerStreamAnimes(movieResponse, history, wich, vidURL.get(i).getUrl());
                                                                        dialog.hide();


                                                                    });

                                                                    dialog.show();
                                                                    dialog.getWindow().setAttributes(lp);

                                                                    dialog.findViewById(R.id.bt_close).setOnClickListener(x ->

                                                                            dialog.dismiss());


                                                                    dialog.show();
                                                                    dialog.getWindow().setAttributes(lp);


                                                                } else {

                                                                    onLoadMainPlayerStreamAnimes(movieResponse, history, wich, vidURL.get(i).getUrl());
                                                                }

                                                            });

                                                            builder.show();


                                                        } else
                                                            Toast.makeText(context, "NULL", Toast.LENGTH_SHORT).show();

                                                    } else {


                                                        if (settingsManager.getSettings().getVlc() == 1) {


                                                            final Dialog dialog = new Dialog(context);
                                                            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                                            dialog.setContentView(R.layout.dialog_bottom_stream);
                                                            dialog.setCancelable(false);
                                                            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

                                                            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                                                            lp.copyFrom(dialog.getWindow().getAttributes());

                                                            lp.gravity = Gravity.BOTTOM;
                                                            lp.width = MATCH_PARENT;
                                                            lp.height = MATCH_PARENT;


                                                            LinearLayout vlc = dialog.findViewById(R.id.vlc);
                                                            LinearLayout mxPlayer = dialog.findViewById(R.id.mxPlayer);
                                                            LinearLayout easyplexPlayer = dialog.findViewById(R.id.easyplexPlayer);
                                                            LinearLayout webcast = dialog.findViewById(R.id.webCast);


                                                            vlc.setOnClickListener(v12 -> {
                                                                Tools.streamEpisodeFromVlc(context,vidURL.get(0).getUrl(),movieResponse.getEpisodes().get(0),settingsManager);
                                                                dialog.hide();
                                                            });

                                                            mxPlayer.setOnClickListener(v12 -> {
                                                                Tools.streamEpisodeFromMxPlayer(context,vidURL.get(0).getUrl(),movieResponse.getEpisodes().get(0),settingsManager);
                                                                dialog.hide();

                                                            });

                                                            webcast.setOnClickListener(v12 -> {

                                                                Tools.streamEpisodeFromMxWebcast(context,vidURL.get(0).getUrl(),movieResponse.getEpisodes().get(0),settingsManager);
                                                                dialog.hide();

                                                            });

                                                            easyplexPlayer.setOnClickListener(v12 -> {

                                                                onLoadMainPlayerStreamAnimes(movieResponse, history, wich, vidURL.get(0).getUrl());
                                                                dialog.hide();


                                                            });

                                                            dialog.show();
                                                            dialog.getWindow().setAttributes(lp);

                                                            dialog.findViewById(R.id.bt_close).setOnClickListener(x ->

                                                                    dialog.dismiss());


                                                            dialog.show();
                                                            dialog.getWindow().setAttributes(lp);


                                                        } else {

                                                            onLoadMainPlayerStreamAnimes(movieResponse, history, wich, vidURL.get(0).getUrl());
                                                        }


                                                    }

                                                }

                                                @Override
                                                public void onError() {

                                                    Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
                                                }
                                            });

                                            easyPlexSupportedHosts.find(movieResponse.getEpisodes().get(0).getVideos().get(wich).getLink());


                                        } else {


                                            if (settingsManager.getSettings().getVlc() == 1) {


                                                final Dialog dialog = new Dialog(context);
                                                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                                dialog.setContentView(R.layout.dialog_bottom_stream);
                                                dialog.setCancelable(false);
                                                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

                                                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                                                lp.copyFrom(dialog.getWindow().getAttributes());

                                                lp.gravity = Gravity.BOTTOM;
                                                lp.width = MATCH_PARENT;
                                                lp.height = MATCH_PARENT;


                                                LinearLayout vlc = dialog.findViewById(R.id.vlc);
                                                LinearLayout mxPlayer = dialog.findViewById(R.id.mxPlayer);
                                                LinearLayout easyplexPlayer = dialog.findViewById(R.id.easyplexPlayer);
                                                LinearLayout webcast = dialog.findViewById(R.id.webCast);




                                                vlc.setOnClickListener(v12 -> {
                                                    Tools.streamEpisodeFromVlc(context,movieResponse.getEpisodes().get(0).getVideos().get(wich).getLink()
                                                            ,movieResponse.getEpisodes().get(0),settingsManager);
                                                    dialog.hide();
                                                });

                                                mxPlayer.setOnClickListener(v12 -> {
                                                    Tools.streamEpisodeFromMxPlayer(context,movieResponse.getEpisodes().get(0).getVideos().get(wich).getLink()
                                                            ,movieResponse.getEpisodes().get(0),settingsManager);
                                                    dialog.hide();

                                                });

                                                webcast.setOnClickListener(v12 -> {

                                                    Tools.streamEpisodeFromMxWebcast(context,movieResponse.getEpisodes().get(0).getVideos().get(wich).getLink(),
                                                            movieResponse.getEpisodes().get(0),settingsManager);
                                                    dialog.hide();

                                                });


                                                easyplexPlayer.setOnClickListener(v12 -> {

                                                    onLoadMainPlayerStreamAnimes(movieResponse, history, wich, movieResponse.getEpisodes().get(0).getVideos().get(wich).getLink());
                                                    dialog.hide();


                                                });

                                                dialog.show();
                                                dialog.getWindow().setAttributes(lp);

                                                dialog.findViewById(R.id.bt_close).setOnClickListener(x ->

                                                        dialog.dismiss());


                                                dialog.show();
                                                dialog.getWindow().setAttributes(lp);


                                            } else {

                                                onLoadMainPlayerStreamAnimes(movieResponse, history, wich, movieResponse.getEpisodes().get(0).getVideos().get(wich).getLink());


                                            }


                                        }


                                    });

                                    builder.show();

                                } else {



                                    if (movieResponse.getEpisodes().get(0).getVideos().get(0).getHeader() !=null &&
                                            !movieResponse.getEpisodes().get(0).getVideos().get(0).getHeader().isEmpty()) {

                                        PLAYER_HEADER = movieResponse.getEpisodes().get(0).getVideos().get(0).getHeader();
                                    }


                                    if (movieResponse.getEpisodes().get(0).getVideos().get(0).getUseragent() !=null
                                            && !movieResponse.getEpisodes().get(0).getVideos().get(0).getUseragent().isEmpty()) {

                                        PLAYER_USER_AGENT = movieResponse.getEpisodes().get(0).getVideos().get(0).getUseragent();
                                    }

                                    if (movieResponse.getEpisodes().get(0).getVideos().get(0).getEmbed() == 1) {


                                        Intent intent = new Intent(context, EmbedActivity.class);
                                        intent.putExtra(Constants.MOVIE_LINK, movieResponse.getEpisodes().get(0).getVideos().get(0).getLink());
                                        context.startActivity(intent);


                                    }  else if (movieResponse.getEpisodes().get(0).getVideos().get(0).getSupportedHosts() == 1) {


                                        easyPlexSupportedHosts = new EasyPlexSupportedHosts(context);

                                        if (settingsManager.getSettings().getHxfileApiKey() != null && !settingsManager.getSettings().getHxfileApiKey().isEmpty()) {

                                            easyPlexSupportedHosts.setApikey(settingsManager.getSettings().getHxfileApiKey());
                                        }

                                        easyPlexSupportedHosts.setMainApiServer(SERVER_BASE_URL);

                                        easyPlexSupportedHosts.onFinish(new EasyPlexSupportedHosts.OnTaskCompleted() {

                                            @Override
                                            public void onTaskCompleted(ArrayList<EasyPlexSupportedHostsModel> vidURL, boolean multipleQuality) {

                                                if (multipleQuality) {
                                                    if (vidURL != null) {
                                                        CharSequence[] name = new CharSequence[vidURL.size()];

                                                        for (int i = 0; i < vidURL.size(); i++) {
                                                            name[i] = vidURL.get(i).getQuality();
                                                        }

                                                        final AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.MyAlertDialogTheme);
                                                        builder.setTitle(context.getString(R.string.select_qualities));
                                                        builder.setCancelable(true);
                                                        builder.setItems(name, (dialogInterface, i) -> {


                                                            if (settingsManager.getSettings().getVlc() == 1) {


                                                                final Dialog dialog = new Dialog(context);
                                                                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                                                dialog.setContentView(R.layout.dialog_bottom_stream);
                                                                dialog.setCancelable(false);
                                                                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

                                                                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                                                                lp.copyFrom(dialog.getWindow().getAttributes());

                                                                lp.gravity = Gravity.BOTTOM;
                                                                lp.width = MATCH_PARENT;
                                                                lp.height = MATCH_PARENT;


                                                                LinearLayout vlc = dialog.findViewById(R.id.vlc);
                                                                LinearLayout mxPlayer = dialog.findViewById(R.id.mxPlayer);
                                                                LinearLayout easyplexPlayer = dialog.findViewById(R.id.easyplexPlayer);
                                                                LinearLayout webcast = dialog.findViewById(R.id.webCast);



                                                                vlc.setOnClickListener(v12 -> {
                                                                    Tools.streamEpisodeFromVlc(context,vidURL.get(i).getUrl(),movieResponse.getEpisodes().get(0),settingsManager);
                                                                    dialog.hide();
                                                                });

                                                                mxPlayer.setOnClickListener(v12 -> {
                                                                    Tools.streamEpisodeFromMxPlayer(context,vidURL.get(i).getUrl(),movieResponse.getEpisodes().get(0),settingsManager);
                                                                    dialog.hide();

                                                                });

                                                                webcast.setOnClickListener(v12 -> {

                                                                    Tools.streamEpisodeFromMxWebcast(context,vidURL.get(i).getUrl(),movieResponse.getEpisodes().get(0),settingsManager);
                                                                    dialog.hide();

                                                                });

                                                                easyplexPlayer.setOnClickListener(v12 -> {

                                                                    onLoadMainPlayerStreamAnimes(movieResponse, history, 0, vidURL.get(i).getUrl());
                                                                    dialog.hide();


                                                                });

                                                                dialog.show();
                                                                dialog.getWindow().setAttributes(lp);

                                                                dialog.findViewById(R.id.bt_close).setOnClickListener(x ->

                                                                        dialog.dismiss());


                                                                dialog.show();
                                                                dialog.getWindow().setAttributes(lp);


                                                            } else {

                                                                onLoadMainPlayerStreamAnimes(movieResponse, history, 0, vidURL.get(i).getUrl());
                                                            }

                                                        });

                                                        builder.show();


                                                    } else
                                                        Toast.makeText(context, "NULL", Toast.LENGTH_SHORT).show();

                                                } else {


                                                    if (settingsManager.getSettings().getVlc() == 1) {


                                                        final Dialog dialog = new Dialog(context);
                                                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                                        dialog.setContentView(R.layout.dialog_bottom_stream);
                                                        dialog.setCancelable(false);
                                                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

                                                        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                                                        lp.copyFrom(dialog.getWindow().getAttributes());

                                                        lp.gravity = Gravity.BOTTOM;
                                                        lp.width = MATCH_PARENT;
                                                        lp.height = MATCH_PARENT;


                                                        LinearLayout vlc = dialog.findViewById(R.id.vlc);
                                                        LinearLayout mxPlayer = dialog.findViewById(R.id.mxPlayer);
                                                        LinearLayout easyplexPlayer = dialog.findViewById(R.id.easyplexPlayer);
                                                        LinearLayout webcast = dialog.findViewById(R.id.webCast);



                                                        vlc.setOnClickListener(v12 -> {
                                                            Tools.streamEpisodeFromVlc(context,vidURL.get(0).getUrl(),movieResponse.getEpisodes().get(0),settingsManager);
                                                            dialog.hide();
                                                        });

                                                        mxPlayer.setOnClickListener(v12 -> {
                                                            Tools.streamEpisodeFromMxPlayer(context,vidURL.get(0).getUrl(),movieResponse.getEpisodes().get(0),settingsManager);
                                                            dialog.hide();

                                                        });

                                                        webcast.setOnClickListener(v12 -> {

                                                            Tools.streamEpisodeFromMxWebcast(context,vidURL.get(0).getUrl(),movieResponse.getEpisodes().get(0),settingsManager);
                                                            dialog.hide();

                                                        });

                                                        easyplexPlayer.setOnClickListener(v12 -> {

                                                            onLoadMainPlayerStreamAnimes(movieResponse, history, 0, vidURL.get(0).getUrl());
                                                            dialog.hide();


                                                        });

                                                        dialog.show();
                                                        dialog.getWindow().setAttributes(lp);

                                                        dialog.findViewById(R.id.bt_close).setOnClickListener(x ->

                                                                dialog.dismiss());


                                                        dialog.show();
                                                        dialog.getWindow().setAttributes(lp);


                                                    } else {

                                                        onLoadMainPlayerStreamAnimes(movieResponse, history, 0, vidURL.get(0).getUrl());
                                                    }



                                                }

                                            }

                                            @Override
                                            public void onError() {

                                                Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
                                            }
                                        });

                                        easyPlexSupportedHosts.find(movieResponse.getEpisodes().get(0).getVideos().get(0).getLink());


                                    } else {

                                        onLoadMainPlayerStreamAnimes(movieResponse, history, 0, movieResponse.getEpisodes().get(0).getVideos().get(0).getLink());


                                    }

                                }


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

        }

        private void onLoadChromCastMovies(CastSession castSession, String downloadUrl, @NotNull Media media) {

            MediaMetadata movieMetadata = new MediaMetadata(MediaMetadata.MEDIA_TYPE_MOVIE);
            movieMetadata.putString(MediaMetadata.KEY_TITLE, media.getTitle());
            movieMetadata.putString(MediaMetadata.KEY_SUBTITLE, media.getTitle());
            movieMetadata.addImage(new WebImage(Uri.parse(media.getPosterPath())));

            List<MediaTrack> tracks = new ArrayList<>();

            for (int i = 0; i < media.getSubstitles().size(); i++) {

                tracks.add(Tools.buildTrack(i+1,media.getSubstitles().get(i).getLink(),media.getSubstitles().get(i).getLang()));

            }

            MediaInfo mediaInfo = new MediaInfo.Builder(downloadUrl)
                    .setStreamType(MediaInfo.STREAM_TYPE_BUFFERED)
                    .setMetadata(movieMetadata)
                    .setMediaTracks(tracks)
                    .build();

            final RemoteMediaClient remoteMediaClient = castSession.getRemoteMediaClient();
            if (remoteMediaClient == null) {
                Timber.tag(TAG).w("showQueuePopup(): null RemoteMediaClient");
                return;
            }




            final QueueDataProvider provider = QueueDataProvider.getInstance(context);
            PopupMenu popup = new PopupMenu(context, binding.itemMovieImage);
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
                                MediaStatus.REPEAT_MODE_REPEAT_OFF, null);
                    } else {
                        return false;
                    }
                } else {
                    if (provider1.getCount() == 0) {
                        remoteMediaClient.queueLoad(newItemArray, 0,
                                MediaStatus.REPEAT_MODE_REPEAT_OFF, null);
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

                    Tools.streamMediaFromMxWebcast(context,downloadUrl,media,settingsManager);
                }
                if (!TextUtils.isEmpty(toastMessage)) {
                    Toast.makeText(context, toastMessage, Toast.LENGTH_SHORT).show();
                }
                return true;
            };
            popup.setOnMenuItemClickListener(clickListener);
            popup.show();
        }

        private void onLoadMainPlayerStreamAnimes(MovieResponse movieResponse, History history, int wich, String link) {


            String name = history.getTitle();
            Integer currentep = Integer.parseInt(movieResponse.getEpisodes().get(0).getEpisodeNumber());
            String currentepname = movieResponse.getEpisodes().get(0).getName();
            String currenteptmdbnumber = String.valueOf(movieResponse.getEpisodes().get(0).getId());
            String currentseasons = history.getCurrentSeasons();
            String currentseasonsNumber = history.getSeasonsNumber();
            String currentepimdb = String.valueOf(movieResponse.getEpisodes().get(0).getId());
            String artwork = movieResponse.getEpisodes().get(0).getStillPath();
            String type = "anime";
            String currentquality = movieResponse.getEpisodes().get(0).getVideos().get(wich).getServer();
            int seasondbId = Integer.parseInt(movieResponse.getEpisodes().get(0).getanimeSeasonId());
            float voteAverage = Float.parseFloat(movieResponse.getEpisodes().get(0).getVoteAverage());


            Intent intent = new Intent(context, EasyPlexMainPlayer.class);

            intent.putExtra(EasyPlexPlayerActivity.EASYPLEX_MEDIA_KEY,
                    MediaModel.media(history.getSerieId(), null,
                            currentquality, type, name, link, artwork, null, currentep
                            , currentseasons,
                            currentepimdb,
                            String.valueOf(seasondbId), currentepname,
                            currentseasonsNumber, history.getPosition(),
                            currenteptmdbnumber,
                            history.getPremuim(),movieResponse.getEpisodes().get(0).getVideos().get(wich).getHls(),null,history.getImdbExternalId(),history.getPosterPath(),
                            movieResponse.getEpisodes().get(0).getHasrecap()
                            ,movieResponse.getEpisodes().get(0).getSkiprecapStartIn(),history.getMediaGenre(),history.getSerieName(),voteAverage
                    ,movieResponse.getEpisodes().get(0).getVideos().get(wich).getDrmuuid(),
                            movieResponse.getEpisodes().get(0).getVideos().get(wich).getDrmlicenceuri(),
                            movieResponse.getEpisodes().get(0).getVideos().get(wich).getDrm()));
            intent.putExtra(ARG_MOVIE_HISTORY, history);
            context.startActivity(intent);
        }

        private void onLoadMainPlayerStreamSeries(MovieResponse movieResponse, History history, int wich, String link) {


            String name = history.getTitle();
            Integer currentep = Integer.parseInt(movieResponse.getEpisodes().get(0).getEpisodeNumber());
            String currentepname = movieResponse.getEpisodes().get(0).getName();
            String currenteptmdbnumber = String.valueOf(movieResponse.getEpisodes().get(0).getId());
            String currentseasons = history.getCurrentSeasons();
            String currentseasonsNumber = history.getSeasonsNumber();
            String currentepimdb = String.valueOf(movieResponse.getEpisodes().get(0).getId());
            String artwork = movieResponse.getEpisodes().get(0).getStillPath();
            String type = "1";
            String currentquality = movieResponse.getEpisodes().get(0).getVideos().get(wich).getServer();
            int seasondbId = Integer.parseInt(movieResponse.getEpisodes().get(0).getSeasonId());
            float voteAverage = Float.parseFloat(movieResponse.getEpisodes().get(0).getVoteAverage());


            Intent intent = new Intent(context, EasyPlexMainPlayer.class);

            intent.putExtra(EasyPlexPlayerActivity.EASYPLEX_MEDIA_KEY,
                    MediaModel.media(history.getSerieId(), null,
                            currentquality, type, name, link, artwork, null, currentep
                            , currentseasons,
                            currentepimdb,
                            String.valueOf(seasondbId), currentepname,
                            currentseasonsNumber, history.getPosition(),
                            currenteptmdbnumber,
                            history.getPremuim(),movieResponse.getEpisodes().get(0).getVideos().get(wich).getHls(),null,history.getImdbExternalId(),history.getPosterPath(),
                            movieResponse.getEpisodes().get(0).getHasrecap()
                            ,movieResponse.getEpisodes().get(0).getSkiprecapStartIn(),
                            history.getMediaGenre(),history.getSerieName(),voteAverage,
                            movieResponse.getEpisodes().get(0).getVideos().get(wich).getDrmuuid(),
                            movieResponse.getEpisodes().get(0).getVideos().get(wich).getDrmlicenceuri(),
                            movieResponse.getEpisodes().get(0).getVideos().get(wich).getDrm()));
            intent.putExtra(ARG_MOVIE_HISTORY, history);
            context.startActivity(intent);


        }

        private void onLoadMainPlayerStreamEmbed(String link) {

            Intent intent = new Intent(context, EmbedActivity.class);
            intent.putExtra(Constants.MOVIE_LINK, link);
            context.startActivity(intent);

        }

        private void onLoadMainPlayerStreamMovie(Media media, int wich, History history, String url) {

            Intent intent = new Intent(context, EasyPlexMainPlayer.class);
            intent.putExtra(EasyPlexPlayerActivity.EASYPLEX_MEDIA_KEY, MediaModel.media(history.getTmdbId(), null
                    , media.getVideos().get(wich).getServer(), "0", media.getTitle(),
                    url, media.getBackdropPath(),
                    null, null
                    , null, null,
                    null, null,
                    null,
                    null,
                    null, media.getPremuim(), media.getVideos().get(wich).getHls(), null, media.getImdbExternalId()
                    , media.getPosterPath(),
                    media.getHasrecap(), media.getSkiprecapStartIn(), history.getMediaGenre(),
                    history.getSerieName(), media.getVoteAverage(),media.getVideos().get(wich).getDrmuuid(),
                    media.getVideos().get(wich).getDrmlicenceuri(),
                    media.getVideos().get(wich).getDrm()));
            context.startActivity(intent);
        }
    }

    private void onLoadChomecastHistory(MovieResponse movieResponse, String url, CastSession castSession, ImageView itemMovieImage, History history) {

        MediaMetadata movieMetadata = new MediaMetadata(MediaMetadata.MEDIA_TYPE_MOVIE);
        movieMetadata.putString(MediaMetadata.KEY_TITLE, history.getSerieName());
        movieMetadata.putString(MediaMetadata.KEY_SUBTITLE, history.getName());

        movieMetadata.addImage(new WebImage(Uri.parse(movieResponse.getEpisodes().get(0).getStillPath())));
        List<MediaTrack> tracks = new ArrayList<>();


        MediaInfo mediaInfo = new MediaInfo.Builder(url)
                .setStreamType(MediaInfo.STREAM_TYPE_BUFFERED)
                .setMetadata(movieMetadata)
                .setMediaTracks(tracks)
                .build();

        final RemoteMediaClient remoteMediaClient = castSession.getRemoteMediaClient();
        if (remoteMediaClient == null) {
            Timber.tag(TAG).w("showQueuePopup(): null RemoteMediaClient");
            return;
        }
        final QueueDataProvider provider = QueueDataProvider.getInstance(context);
        PopupMenu popup = new PopupMenu(context, itemMovieImage);
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
                            MediaStatus.REPEAT_MODE_REPEAT_OFF, null);
                } else {
                    return false;
                }
            } else {
                if (provider1.getCount() == 0) {
                    remoteMediaClient.queueLoad(newItemArray, 0,
                            MediaStatus.REPEAT_MODE_REPEAT_OFF, null);
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
            if (!TextUtils.isEmpty(toastMessage)) {
                Toast.makeText(context, toastMessage, Toast.LENGTH_SHORT).show();
            }
            return true;
        };
        popup.setOnMenuItemClickListener(clickListener);
        popup.show();
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        adsLaunched = false;
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull MainViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        adsLaunched = false;
    }
}
