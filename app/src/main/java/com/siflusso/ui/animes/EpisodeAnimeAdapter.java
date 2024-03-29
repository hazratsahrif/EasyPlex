package com.siflusso.ui.animes;


import static com.siflusso.util.Constants.E;
import static com.siflusso.util.Constants.PLAYER_HEADER;
import static com.siflusso.util.Constants.PLAYER_USER_AGENT;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatRatingBar;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.paging.PagedListAdapter;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.MaxReward;
import com.applovin.mediation.MaxRewardedAdListener;
import com.applovin.mediation.ads.MaxRewardedAd;
import com.appnext.ads.fullscreen.RewardedVideo;
import com.appnext.base.Appnext;
import com.appodeal.ads.Appodeal;
import com.appodeal.ads.RewardedVideoCallbacks;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.siflusso.R;
import com.siflusso.data.local.entity.Download;
import com.siflusso.data.local.entity.History;
import com.siflusso.data.local.entity.Media;
import com.siflusso.data.model.MovieResponse;
import com.siflusso.data.model.comments.Comment;
import com.siflusso.data.model.episode.Episode;
import com.siflusso.data.model.episode.EpisodeStream;
import com.siflusso.data.model.media.MediaModel;
import com.siflusso.data.model.media.Resume;
import com.siflusso.data.model.report.Report;
import com.siflusso.data.repository.MediaRepository;
import com.siflusso.databinding.RowSeasons2Binding;
import com.siflusso.ui.comments.CommentsAdapter;
import com.siflusso.ui.downloadmanager.core.RepositoryHelper;
import com.siflusso.ui.downloadmanager.core.model.data.entity.DownloadInfo;
import com.siflusso.ui.downloadmanager.core.settings.SettingsRepository;
import com.siflusso.ui.downloadmanager.ui.adddownload.AddDownloadActivity;
import com.siflusso.ui.downloadmanager.ui.adddownload.AddDownloadDialog;
import com.siflusso.ui.downloadmanager.ui.adddownload.AddInitParams;
import com.siflusso.ui.manager.AuthManager;
import com.siflusso.ui.manager.SettingsManager;
import com.siflusso.ui.manager.TokenManager;
import com.siflusso.ui.player.activities.EasyPlexMainPlayer;
import com.siflusso.ui.player.activities.EasyPlexPlayerActivity;
import com.siflusso.ui.player.activities.EmbedActivity;
import com.siflusso.ui.player.cast.ExpandedControlsActivity;
import com.siflusso.ui.player.cast.queue.QueueDataProvider;
import com.siflusso.ui.player.cast.utils.Utils;
import com.siflusso.ui.player.fsm.state_machine.FsmPlayerApi;
import com.siflusso.ui.seriedetails.SerieDetailsActivity;
import com.siflusso.ui.settings.SettingsActivity;
import com.siflusso.util.Constants;
import com.siflusso.util.DialogHelper;
import com.siflusso.util.GlideApp;
import com.siflusso.util.ItemAnimation;
import com.siflusso.util.SpacingItemDecoration;
import com.siflusso.util.Tools;
import com.easyplex.easyplexsupportedhosts.EasyPlexSupportedHosts;
import com.easyplex.easyplexsupportedhosts.Model.EasyPlexSupportedHostsModel;
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
import com.google.android.gms.cast.MediaTrack;
import com.google.android.gms.cast.framework.CastContext;
import com.google.android.gms.cast.framework.CastSession;
import com.google.android.gms.cast.framework.media.RemoteMediaClient;
import com.google.android.gms.common.images.WebImage;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.ironsource.mediationsdk.IronSource;
import com.ironsource.mediationsdk.adunit.adapter.utility.AdInfo;
import com.ironsource.mediationsdk.logger.IronSourceError;
import com.ironsource.mediationsdk.model.Placement;
import com.ironsource.mediationsdk.sdk.LevelPlayRewardedVideoListener;
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

import static android.view.View.GONE;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static com.siflusso.util.Constants.ARG_MOVIE;
import static com.siflusso.util.Constants.DEFAULT_WEBVIEW_ADS_RUNNING;
import static com.siflusso.util.Constants.S0;
import static com.siflusso.util.Constants.SERVER_BASE_URL;
import static com.google.android.gms.cast.MediaStatus.REPEAT_MODE_REPEAT_OFF;


/**
 * Adapter for Next Movie.
 *
 * @author Yobex.
 */
public class EpisodeAnimeAdapter extends PagedListAdapter<Episode, EpisodeAnimeAdapter.NextPlayMoviesViewHolder> {


    private RewardedVideo mAppNextAdsVideoRewarded;
    private MaxRewardedAd maxRewardedAd;
    private static final String TAG_DOWNLOAD_DIALOG = "add_download_dialog";
    private AddDownloadDialog addDownloadDialog;
    private CountDownTimer mCountDownTimer;
    private boolean webViewLauched = false;
    private final String externalId;
    private final String currentSerieId;
    private final String currentSeasons;
    private Download download;
    private final Media media;
    final String seasonId;
    private boolean adsLaunched = false;
    private final String currentSeasonsNumber;
    private final String currentTvShowName;
    private final int premuim;
    private final String serieCover;
    private final SharedPreferences preferences;
    private final AuthManager authManager;
    private final SettingsManager settingsManager;
    private final Context context;
    private RewardedAd mRewardedAd;
    boolean isLoading;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private final MediaRepository mediaRepository;
    private final TokenManager tokenManager;
    private History history;
    private static final int PRELOAD_TIME_S = 2;
    private EasyPlexSupportedHosts easyPlexSupportedHosts;
    private final String mediaGenre;
    private final static String MEDIA_TYPE = "anime";
    private final int animationType;
    private CommentsAdapter commentsAdapter;

    public EpisodeAnimeAdapter(String serieid, String seasonsid, String seasonsidpostion, String currentseason, SharedPreferences preferences, AuthManager authManager

            , SettingsManager settingsManager, MediaRepository mediaRepository, String currentTvShowName, int
                                       premuim, TokenManager tokenManager, Context context, String serieCover, Media media, String mediaGenre, String externalId, int animationType) {
        super(ITEM_CALLBACK);
        this.currentSerieId = serieid;
        this.currentSeasons = seasonsid;
        this.seasonId = seasonsidpostion;
        this.preferences = preferences;
        this.authManager = authManager;
        this.settingsManager = settingsManager;
        this.currentSeasonsNumber = currentseason;
        this.currentTvShowName = currentTvShowName;
        this.premuim = premuim;
        this.tokenManager = tokenManager;
        this.mediaRepository = mediaRepository;
        this.serieCover = serieCover;
        this.context = context;
        this.media = media;
        this.mediaGenre = mediaGenre;
        this.externalId = externalId;
        this.animationType = animationType;

    }

    @NonNull
    @Override
    public NextPlayMoviesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        RowSeasons2Binding binding = RowSeasons2Binding.inflate(LayoutInflater.from(parent.getContext()), parent, false);

        return new EpisodeAnimeAdapter.NextPlayMoviesViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull NextPlayMoviesViewHolder holder, int position) {

        Episode episode = getItem(position);

        holder.onBind(episode,position);

    }


    class NextPlayMoviesViewHolder extends RecyclerView.ViewHolder {


        private final RowSeasons2Binding binding;

        NextPlayMoviesViewHolder (@NonNull RowSeasons2Binding binding)
        {
            super(binding.getRoot());

            this.binding = binding;
        }

        @SuppressLint("SetTextI18n")
        void onBind(final Episode episode, int position) {

            Tools.onLoadMediaCoverEpisode(context,binding.epcover,episode.getStillPath());

            binding.eptitle.setText(episode.getEpisodeNumber() +" - " +episode.getName());
            binding.epoverview.setText(episode.getOverview());



            if (!adsLaunched) {

                createAndLoadRewardedAd();

                initLoadRewardedAd();

            }



            if (settingsManager.getSettings().getResumeOffline() == 1) {


                onLoadEpisodeOffline(episode);


            }else {

                onLoadEpisodeOnline(episode);
            }


            binding.epLayout.setOnClickListener(v -> {

                if (episode.getEnableStream() !=1) {
                    Toast.makeText(context, context.getString(R.string.stream_is_currently_not_available_for_this_media), Toast.LENGTH_SHORT).show();
                    return;
                }

                onClickMoreOptionsIcons(episode, position);
            });


            if (settingsManager.getSettings().getEnableDownload() == 0) {

                binding.downloadEpisode.setImageResource(R.drawable.ic_notavailable);
            }

            binding.downloadEpisode.setOnClickListener(v -> {


                if (settingsManager.getSettings().getEnableDownload() == 0) {

                    DialogHelper.showNoDownloadAvailable(context,context.getString(R.string.download_disabled));

                }else    if (episode.getEnableMediaDownload() == 0) {

                    Toast.makeText(context, R.string.download_is_currently_not_available_for_this_media, Toast.LENGTH_SHORT).show();

                }else  onLoadEpisodeDownloadInfo(episode, position);


            });

            binding.miniPlay.setOnClickListener(v -> {

                PopupMenu popup = new PopupMenu(v.getContext(), v);
                popup.inflate(R.menu.episode_item_popup);
                popup.getMenu().findItem(R.id.episode_comments).setVisible(settingsManager.getSettings().getEnableComments() != 0);
                popup.setForceShowIcon(true);
                popup.setOnMenuItemClickListener(item -> episodeMiniMenuClicked(item,episode, position));
                popup.show();
            });

        }

        @SuppressLint("NonConstantResourceId")
        private boolean episodeMiniMenuClicked(MenuItem item, Episode episode, int position) {
            switch (item.getItemId()) {
                case R.id.play_menu:
                    onClickMoreOptionsIconsDot(episode, position);
                    break;
                case R.id.report_menu:

                    final Dialog dialog = new Dialog(context);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.dialog_report);
                    dialog.setCancelable(false);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

                    WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                    lp.copyFrom(dialog.getWindow().getAttributes());

                    lp.gravity = Gravity.BOTTOM;
                    lp.width = MATCH_PARENT;
                    lp.height = MATCH_PARENT;


                    EditText editTextMessage = dialog.findViewById(R.id.et_post);
                    TextView reportMovieName = dialog.findViewById(R.id.movietitle);
                    ImageView imageView = dialog.findViewById(R.id.image_movie_poster);


                    String name = currentTvShowName + " : " + S0 + currentSeasons + E + episode.getEpisodeNumber() + " : " + episode.getName();

                    reportMovieName.setText(name);

                    Tools.onLoadMediaCover(context,imageView,episode.getStillPath());


                    dialog.findViewById(R.id.bt_close).setOnClickListener(v -> dialog.dismiss());
                    dialog.findViewById(R.id.view_report).setOnClickListener(v -> {


                        editTextMessage.getText();

                        if (editTextMessage.getText() !=null) {


                            mediaRepository.getReport(settingsManager.getSettings().getApiKey(),name,editTextMessage.getText().toString())
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new Observer<>() {
                                        @Override
                                        public void onSubscribe(@NotNull Disposable d) {

                                            //

                                        }

                                        @Override
                                        public void onNext(@NotNull Report report) {

                                            dialog.dismiss();


                                            Toast.makeText(context, context.getString(R.string.report_sent), Toast.LENGTH_SHORT).show();
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


                    });

                    dialog.show();
                    dialog.getWindow().setAttributes(lp);

                    dialog.findViewById(R.id.bt_close).setOnClickListener(x ->

                            dialog.dismiss());


                    dialog.show();
                    dialog.getWindow().setAttributes(lp);

                    break;
                case R.id.episode_comments:

                    if (tokenManager.getToken().getAccessToken() != null) {
                        onLoadSerieComments(episode.getId());
                    }else {
                        Toast.makeText(context, "You need to login to able to add a comment !", Toast.LENGTH_SHORT).show();
                    }

                    break;
            }
            return true;
        }



        private void onLoadSerieComments(Integer id) {

            commentsAdapter = new CommentsAdapter();

            final Dialog dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_comments);
            dialog.setCancelable(false);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(dialog.getWindow().getAttributes());

            lp.width = MATCH_PARENT;
            lp.height = MATCH_PARENT;

            lp.gravity = Gravity.BOTTOM;

            commentsAdapter.setOnItemClickListener(clicked -> {
                if (clicked){
                    mediaRepository.getAnimesEpisodesComments(id,settingsManager.getSettings().getApiKey())
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Observer<>() {
                                @Override
                                public void onSubscribe(@NotNull Disposable d) {

                                    //

                                }

                                @SuppressLint("NotifyDataSetChanged")
                                @Override
                                public void onNext(@io.reactivex.rxjava3.annotations.NonNull MovieResponse movieResponse) {

                                    commentsAdapter.addToContent(movieResponse.getComments(),context,authManager,mediaRepository);
                                    commentsAdapter.notifyDataSetChanged();

                                }

                                @Override
                                public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {

                                }

                                @Override
                                public void onComplete() {

                                }

                            });

                }
            });

            RecyclerView rvComments = dialog.findViewById(R.id.rv_comments);

            rvComments.setHasFixedSize(true);
            rvComments.setNestedScrollingEnabled(false);
            rvComments.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
            rvComments.addItemDecoration(new SpacingItemDecoration(1, Tools.dpToPx(context, 0), true));


            TextView commentTotal = dialog.findViewById(R.id.comment_total);

            FloatingActionButton add_comment_btn = dialog.findViewById(R.id.add_comment_btn);

            EditText editTextComment = dialog.findViewById(R.id.comment_message);

            LinearLayout noCommentFound = dialog.findViewById(R.id.no_comment_found);

            mediaRepository.getAnimesEpisodesComments(id,settingsManager.getSettings().getApiKey())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<>() {
                        @Override
                        public void onSubscribe(@NotNull Disposable d) {

                            //

                        }

                        @Override
                        public void onNext(@io.reactivex.rxjava3.annotations.NonNull MovieResponse movieResponse) {

                            commentsAdapter.addToContent(movieResponse.getComments(),context,authManager,mediaRepository);
                            rvComments.setAdapter(commentsAdapter);


                            if (commentsAdapter.getItemCount() == 0) {
                                noCommentFound.setVisibility(View.VISIBLE);

                            }else {
                                noCommentFound.setVisibility(GONE);
                            }

                            commentTotal.setText(movieResponse.getComments().size()+" Comments");


                            add_comment_btn.setOnClickListener(v -> {

                                if (editTextComment.getText().toString() !=null){

                                    mediaRepository.addCommentEpisodeAnime(editTextComment.getText().toString(),String.valueOf(id))
                                            .subscribeOn(Schedulers.io())
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribe(new Observer<>() {
                                                @Override
                                                public void onSubscribe(@NotNull Disposable d) {

                                                    //

                                                }

                                                @Override
                                                public void onNext(@io.reactivex.rxjava3.annotations.NonNull Comment comment) {

                                                    Toast.makeText(context, "Comment added successfully", Toast.LENGTH_SHORT).show();
                                                    editTextComment.setText("");


                                                    mediaRepository.getAnimesEpisodesComments(id,settingsManager.getSettings().getApiKey())
                                                            .subscribeOn(Schedulers.io())
                                                            .observeOn(AndroidSchedulers.mainThread())
                                                            .subscribe(new Observer<>() {
                                                                @Override
                                                                public void onSubscribe(@NotNull Disposable d) {

                                                                    //

                                                                }

                                                                @Override
                                                                public void onNext(@io.reactivex.rxjava3.annotations.NonNull MovieResponse movieResponse) {

                                                                    commentsAdapter.addToContent(movieResponse.getComments(),context,authManager,mediaRepository);
                                                                    rvComments.scrollToPosition(rvComments.getAdapter().getItemCount()-1);
                                                                    commentsAdapter.notifyDataSetChanged();
                                                                }

                                                                @Override
                                                                public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {

                                                                }

                                                                @Override
                                                                public void onComplete() {

                                                                }

                                                            });

                                                }

                                                @Override
                                                public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {

                                                }

                                                @Override
                                                public void onComplete() {

                                                }

                                            });


                                }else {

                                    Toast.makeText(context, "Type a word to able to comment !", Toast.LENGTH_SHORT).show();
                                }

                            });
                        }

                        @Override
                        public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {

                        }

                        @Override
                        public void onComplete() {

                        }

                    });



            dialog.show();
            dialog.getWindow().setAttributes(lp);

            dialog.findViewById(R.id.bt_close).setOnClickListener(x ->

                    dialog.dismiss());

            dialog.show();
            dialog.getWindow().setAttributes(lp);
        }


        private void onLoadEpisodeDownloadInfo(Episode episode, int position) {



            download = new Download(String.valueOf(episode.getId()),String.valueOf(episode.getId()),episode.getStillPath(),currentTvShowName + " : " + "S0" +
                    currentSeasons + "E" + episode.getEpisodeNumber() +
                    " : " + episode.getName(),episode.getLink());


            String defaultDownloadsOptions = settingsManager.getSettings().getDefaultDownloadsOptions();
            if ("Free".equals(defaultDownloadsOptions)) {
                onLoadDownloadsList(episode);
            } else if ("PremuimOnly".equals(defaultDownloadsOptions)) {

                if (premuim == 1 && authManager.getUserInfo().getPremuim() == 1 && tokenManager.getToken() != null) {

                    onLoadDownloadsList(episode);

                } else if (premuim == 0 && authManager.getUserInfo().getPremuim() == 1 && tokenManager.getToken() != null) {

                    onLoadDownloadsList(episode);

                }else {

                    DialogHelper.showPremuimWarning(context);
                }
            } else if ("WithAdsUnlock".equals(defaultDownloadsOptions)) {


                if (premuim == 1 && authManager.getUserInfo().getPremuim() == 1 && tokenManager.getToken() != null) {

                    onLoadDownloadsList(episode);

                } else if (premuim == 0 && authManager.getUserInfo().getPremuim() == 1 && tokenManager.getToken() != null) {

                    onLoadDownloadsList(episode);

                }else {

                    onLoadSubscribeDialog(episode,position,false);

                }
            }
        }

        private void onClickMoreOptionsIconsDot(Episode episode, int position) {

            final Dialog dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_mini_play);
            dialog.setCancelable(false);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(dialog.getWindow().getAttributes());

            lp.gravity = Gravity.BOTTOM;
            lp.width = MATCH_PARENT;
            lp.height = MATCH_PARENT;

            TextView movieName = dialog.findViewById(R.id.text_view_video_next_release_date);
            TextView movieoverview = dialog.findViewById(R.id.text_overview_label);
            AppCompatRatingBar appCompatRatingBar = dialog.findViewById(R.id.rating_bar);
            TextView viewMovieRating = dialog.findViewById(R.id.view_movie_rating);
            ImageView imageView = dialog.findViewById(R.id.next_cover_media);
            ProgressBar progressBar = dialog.findViewById(R.id.resume_progress_bar);
            TextView epResumeTitle = dialog.findViewById(R.id.epResumeTitle);
            TextView timeRemaning = dialog.findViewById(R.id.timeRemaning);
            LinearLayout linearLayouttimeRemaning = dialog.findViewById(R.id.resumePlayProgress);
            LinearLayout linearResume = dialog.findViewById(R.id.resumeLinear);
            Button playButtonIcon = dialog.findViewById(R.id.PlayButtonIcon);
            ImageView episodeDownload = dialog.findViewById(R.id.episodeDownload);
            episodeDownload.setOnClickListener(v -> onLoadEpisodeDownloadInfo(episode,position));

            playButtonIcon.setOnClickListener(v -> {


                if (episode.getEnableStream() !=1) {

                    Toast.makeText(context, "Stream is currently not available for this Media", Toast.LENGTH_SHORT).show();
                    return;
                }
                onClickMoreOptionsIcons(episode,position);
                dialog.dismiss();
            });


            mediaRepository.hasResume(episode.getId()).observe((AnimeDetailsActivity) context, resumeInfo -> {

                if (resumeInfo != null) {

                    if (resumeInfo.getTmdb() != null && resumeInfo.getResumePosition() !=null

                            && resumeInfo.getTmdb().equals(String.valueOf(episode.getId())) && Tools.id(context).equals(resumeInfo.getDeviceId())) {

                        double d = resumeInfo.getResumePosition();

                        double moveProgress = d * 100 / resumeInfo.getMovieDuration();

                        progressBar.setVisibility(View.VISIBLE);
                        linearLayouttimeRemaning.setVisibility(View.VISIBLE);
                        progressBar.setProgress((int) moveProgress);
                        timeRemaning.setText(Tools.getProgressTime((resumeInfo.getMovieDuration() - resumeInfo.getResumePosition()), true));
                        timeRemaning.setVisibility(View.VISIBLE);
                        linearResume.setVisibility(View.VISIBLE);



                    } else {

                        progressBar.setProgress(0);
                        progressBar.setVisibility(GONE);
                        timeRemaning.setVisibility(GONE);
                        linearLayouttimeRemaning.setVisibility(GONE);
                        linearResume.setVisibility(GONE);

                    }

                }else {

                    progressBar.setProgress(0);
                    progressBar.setVisibility(GONE);
                    linearLayouttimeRemaning.setVisibility(GONE);
                    timeRemaning.setVisibility(GONE);
                    linearResume.setVisibility(GONE);
                }

            });


            movieName.setText(episode.getName());
            appCompatRatingBar.setRating(Float.parseFloat(episode.getVoteAverage()) / 2);
            viewMovieRating.setText(String.valueOf(episode.getVoteAverage()));
            epResumeTitle.setText(episode.getName());

            movieName.setText(episode.getName());
            movieoverview.setText(episode.getOverview());

            GlideApp.with(context).load(episode.getStillPath())
                    .fitCenter()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imageView);

            dialog.findViewById(R.id.bt_close).setOnClickListener(x ->
                    dialog.dismiss());

            dialog.show();
            dialog.getWindow().setAttributes(lp);
        }

        private void onClickMoreOptionsIcons(Episode episode, int position) {


            download = new Download(String.valueOf(episode.getId()),String.valueOf(episode.getId()),episode.getStillPath(),currentTvShowName + " : " + "S0" +
                    currentSeasons + "E" + episode.getEpisodeNumber() +
                    " : " + episode.getName(),episode.getLink());

            if (!episode.getVideos().isEmpty() && episode.getVideos() !=null) {



               if (premuim == 1 && authManager.getUserInfo().getPremuim() == 1 && tokenManager.getToken() != null) {

                    onStartEpisode(episode,position);

               }else if (episode.getEnableAdsUnlock() ==1 ){


                   if (premuim == 1 && authManager.getUserInfo().getPremuim() == 1 && tokenManager.getToken() != null){

                       onStartEpisode(episode,position);


                   }else {

                       onLoadSubscribeDialog(episode,position,true);
                   }



               }else if (settingsManager.getSettings().getWachAdsToUnlock() == 1 && premuim != 1 && authManager.getUserInfo().getPremuim() == 0) {

                    if (settingsManager.getSettings().getEnableWebview() == 1) {

                        final Dialog dialog = new Dialog(context);
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.setContentView(R.layout.episode_webview);
                        dialog.setCancelable(false);
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                        lp.copyFrom(dialog.getWindow().getAttributes());

                        lp.gravity = Gravity.BOTTOM;
                        lp.width = MATCH_PARENT;
                        lp.height = MATCH_PARENT;


                        mCountDownTimer = new CountDownTimer(DEFAULT_WEBVIEW_ADS_RUNNING, 1000) {
                            @SuppressLint({"SetTextI18n", "SetJavaScriptEnabled"})
                            @Override
                            public void onTick(long millisUntilFinished) {

                                if (!webViewLauched) {

                                    WebView webView = dialog.findViewById(R.id.webViewVideoBeforeAds);
                                    webView.getSettings().setJavaScriptEnabled(true);
                                    webView.setWebViewClient(new WebViewClient());
                                    WebSettings webSettings = webView.getSettings();
                                    webSettings.setSupportMultipleWindows(false);
                                    webSettings.setJavaScriptCanOpenWindowsAutomatically(false);
                                    if (settingsManager.getSettings().getWebviewLink() !=null && !settingsManager.getSettings().getWebviewLink().isEmpty()) {

                                        webView.loadUrl(settingsManager.getSettings().getWebviewLink());
                                    }else {

                                        webView.loadUrl(SERVER_BASE_URL+"webview");
                                    }

                                    webViewLauched = true;
                                }

                            }

                            @Override
                            public void onFinish() {

                                dialog.dismiss();
                                onStartEpisode(episode,position);
                                webViewLauched = false;

                                if (mCountDownTimer != null) {

                                    mCountDownTimer.cancel();
                                    mCountDownTimer = null;

                                }
                            }

                        }.start();

                        dialog.show();
                        dialog.getWindow().setAttributes(lp);


                    }else {

                        onLoadSubscribeDialog(episode,position,true);
                    }

                } else if (settingsManager.getSettings().getWachAdsToUnlock() == 0 && premuim == 0) {


                    onStartEpisode(episode,position);


                } else if (authManager.getUserInfo().getPremuim() == 1 && premuim == 0) {


                    onStartEpisode(episode,position);


                } else {

                    DialogHelper.showPremuimWarning(context);

                }

            }else {


                DialogHelper.showNoStreamEpisode(context);

            }
        }

        private void onLoadEpisodeOnline(Episode episode) {

            mediaRepository.getResumeById(String.valueOf(episode.getId()),settingsManager.getSettings().getApiKey())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<>() {
                        @Override
                        public void onSubscribe(@NotNull Disposable d) {

                            //

                        }

                        @SuppressLint({"TimberArgCount", "SetTextI18n"})
                        @Override
                        public void onNext(@NotNull Resume resume) {


                            if (resume.getTmdb() != null && resume.getResumePosition() != null

                                    && resume.getTmdb().equals(String.valueOf(episode.getId())) && Tools.id(context).equals(resume.getDeviceId())) {


                                double d = resume.getResumePosition();

                                double moveProgress = d * 100 / resume.getMovieDuration();


                                binding.resumeProgressBar.setVisibility(View.VISIBLE);
                                binding.resumeProgressBar.setProgress((int) moveProgress);

                                binding.timeRemaning.setText(Tools.getProgressTime((resume.getMovieDuration() - resume.getResumePosition()), true));


                            } else {


                                binding.resumeProgressBar.setProgress(0);
                                binding.resumeProgressBar.setVisibility(GONE);
                                binding.timeRemaning.setVisibility(GONE);

                            }
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


        @SuppressLint("StaticFieldLeak")
        private void onStartEpisode(Episode episode, int position) {

            CastSession castSession = CastContext.getSharedInstance(context).getSessionManager()
                    .getCurrentCastSession();

            if (settingsManager.getSettings().getServerDialogSelection() == 1) {

                String[] charSequence = new String[episode.getVideos().size()];
                for (int i = 0; i<episode.getVideos().size(); i++) {
                    charSequence[i] = String.valueOf(episode.getVideos().get(i).getServer());

                }

                final AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.MyAlertDialogTheme);
                builder.setTitle(R.string.source_quality);
                builder.setCancelable(true);
                builder.setItems(charSequence, (dialogInterface, wich) -> {


                    if (episode.getVideos().get(wich).getHeader() !=null && !episode.getVideos().get(wich).getHeader().isEmpty()) {

                        PLAYER_HEADER = episode.getVideos().get(wich).getHeader();
                    }


                    if (episode.getVideos().get(wich).getUseragent() !=null && !episode.getVideos().get(wich).getUseragent().isEmpty()) {

                        PLAYER_USER_AGENT = episode.getVideos().get(wich).getUseragent();
                    }



                    if (episode.getVideos().get(wich).getEmbed() == 1) {

                        Intent intent = new Intent(context, EmbedActivity.class);
                        intent.putExtra(Constants.MOVIE_LINK, episode.getVideos().get(wich).getLink());
                        context.startActivity(intent);


                    }else if (episode.getVideos().get(wich).getSupportedHosts() == 1) {

                        startSupportedHostsStream(episode,wich);

                    }else {


                        if (castSession != null && castSession.isConnected()) {

                            onLoadChromcast(episode, castSession, episode.getVideos().get(wich).getLink());


                        } else if (settingsManager.getSettings().getVlc() == 1) {


                            final Dialog dialog = new Dialog(context);
                            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            dialog.setContentView(R.layout.dialog_bottom_stream);
                            dialog.setCancelable(false);
                            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

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
                                Tools.streamEpisodeFromVlc(context,episode.getVideos().get(wich).getLink(),episode,settingsManager);
                                dialog.hide();
                            });

                            mxPlayer.setOnClickListener(v12 -> {
                                Tools.streamEpisodeFromMxPlayer(context,episode.getVideos().get(wich).getLink(),episode,settingsManager);
                                dialog.hide();

                            });

                            webcast.setOnClickListener(v12 -> {

                                Tools.streamEpisodeFromMxWebcast(context,episode.getVideos().get(wich).getLink(),episode,settingsManager);
                                dialog.hide();

                            });


                            easyplexPlayer.setOnClickListener(v12 -> {
                                onLoadMainPlayerStream(episode,position, episode.getVideos().get(wich).getLink(),episode.getVideos().get(wich));
                                dialog.hide();
                            });

                            dialog.show();
                            dialog.getWindow().setAttributes(lp);

                            dialog.findViewById(R.id.bt_close).setOnClickListener(x ->

                                    dialog.dismiss());


                            dialog.show();
                            dialog.getWindow().setAttributes(lp);


                        } else {

                            onLoadMainPlayerStream(episode,position, episode.getVideos().get(wich).getLink(), episode.getVideos().get(wich));

                        }

                    }



                });


                builder.show();

            } else {


                if (episode.getVideos().get(0).getHeader() !=null && !episode.getVideos().get(0).getHeader().isEmpty()) {

                    PLAYER_HEADER = episode.getVideos().get(0).getHeader();
                }


                if (episode.getVideos().get(0).getUseragent() !=null && !episode.getVideos().get(0).getUseragent().isEmpty()) {

                    PLAYER_USER_AGENT = episode.getVideos().get(0).getUseragent();
                }


                if (episode.getVideos().get(0).getEmbed() == 1) {


                    Intent intent = new Intent(context, EmbedActivity.class);
                    intent.putExtra(Constants.MOVIE_LINK, episode.getVideos().get(0).getLink());
                    context.startActivity(intent);


                }else if (episode.getVideos().get(0).getSupportedHosts() == 1){


                    easyPlexSupportedHosts = new EasyPlexSupportedHosts(context);


                    if (settingsManager.getSettings().getHxfileApiKey() !=null && !settingsManager.getSettings().getHxfileApiKey().isEmpty())  {

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

                                            onLoadChromcast(episode, castSession, vidURL.get(i).getUrl());

                                        }else {

                                            if (settingsManager.getSettings().getVlc() == 1) {

                                                final Dialog dialog = new Dialog(context);
                                                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                                dialog.setContentView(R.layout.dialog_bottom_stream);
                                                dialog.setCancelable(false);
                                                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

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
                                                    Tools.streamEpisodeFromVlc(context,vidURL.get(i).getUrl(),episode,settingsManager);
                                                    dialog.hide();
                                                });

                                                mxPlayer.setOnClickListener(v12 -> {
                                                    Tools.streamEpisodeFromMxPlayer(context,vidURL.get(i).getUrl(),episode,settingsManager);
                                                    dialog.hide();

                                                });

                                                webcast.setOnClickListener(v12 -> {

                                                    Tools.streamEpisodeFromMxWebcast(context,vidURL.get(i).getUrl(),episode,settingsManager);
                                                    dialog.hide();

                                                });

                                                easyplexPlayer.setOnClickListener(v12 -> {

                                                    onLoadMainPlayerStream(episode,position, vidURL.get(i).getUrl(), episode.getVideos().get(0));
                                                    dialog.hide();


                                                });

                                                dialog.show();
                                                dialog.getWindow().setAttributes(lp);

                                                dialog.findViewById(R.id.bt_close).setOnClickListener(x ->

                                                        dialog.dismiss());


                                                dialog.show();
                                                dialog.getWindow().setAttributes(lp);


                                            } else {

                                                onLoadMainPlayerStream(episode,position, vidURL.get(i).getUrl(), episode.getVideos().get(0));


                                            }

                                        }

                                    });

                                    builder.show();


                                } else Toast.makeText(context, "NULL", Toast.LENGTH_SHORT).show();

                            } else {


                                onLoadMainPlayerStream(episode,position, vidURL.get(0).getUrl(), episode.getVideos().get(0));

                            }

                        }

                        @Override
                        public void onError() {

                            Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
                        }
                    });

                    easyPlexSupportedHosts.find(episode.getVideos().get(0).getLink());


                } else {


                    if (castSession != null && castSession.isConnected()) {

                        onLoadChromcast(episode, castSession, episode.getVideos().get(0).getLink());

                    }else {

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
                                Tools.streamEpisodeFromVlc(context,episode.getVideos().get(0).getLink(),episode,settingsManager);
                                dialog.hide();
                            });

                            mxPlayer.setOnClickListener(v12 -> {
                                Tools.streamEpisodeFromMxPlayer(context,episode.getVideos().get(0).getLink(),episode,settingsManager);
                                dialog.hide();

                            });

                            webcast.setOnClickListener(v12 -> {

                                Tools.streamEpisodeFromMxWebcast(context,episode.getVideos().get(0).getLink(),episode,settingsManager);
                                dialog.hide();

                            });


                            easyplexPlayer.setOnClickListener(v12 -> {
                                onLoadMainPlayerStream(episode,position, episode.getVideos().get(0).getLink(),episode.getVideos().get(0));
                                dialog.hide();
                            });

                            dialog.show();
                            dialog.getWindow().setAttributes(lp);

                            dialog.findViewById(R.id.bt_close).setOnClickListener(x ->

                                    dialog.dismiss());


                            dialog.show();
                            dialog.getWindow().setAttributes(lp);


                        } else {

                            onLoadMainPlayerStream(episode,position, episode.getVideos().get(0).getLink(), episode.getVideos().get(0));

                        }
                    }

                }



            }

        }

        private void onLoadChromcast(Episode episode, CastSession castSession, String link) {

            String currentepname = episode.getName();
            String artwork = episode.getStillPath();
            String name = currentTvShowName + " : " +"S0" + currentSeasons + "E" + episode.getEpisodeNumber() + " : " + episode.getName();

            MediaMetadata movieMetadata = new MediaMetadata(MediaMetadata.MEDIA_TYPE_MOVIE);
            movieMetadata.putString(MediaMetadata.KEY_TITLE, name);
            movieMetadata.putString(MediaMetadata.KEY_SUBTITLE, currentepname);

            movieMetadata.addImage(new WebImage(Uri.parse(artwork)));
            List<MediaTrack> tracks = new ArrayList<>();


            MediaInfo mediaInfo = new MediaInfo.Builder(link)
                    .setStreamType(MediaInfo.STREAM_TYPE_BUFFERED)
                    .setMetadata(movieMetadata)
                    .setMediaTracks(tracks)
                    .build();

            final RemoteMediaClient remoteMediaClient = castSession.getRemoteMediaClient();
            if (remoteMediaClient == null) {
                Timber.tag("TAG").w("showQueuePopup(): null RemoteMediaClient");
                return;
            }
            final QueueDataProvider provider = QueueDataProvider.getInstance(context);
            PopupMenu popup = new PopupMenu(context, binding.cardView);
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
                                REPEAT_MODE_REPEAT_OFF, null);
                    } else {
                        return false;
                    }
                } else {
                    if (provider1.getCount() == 0) {
                        remoteMediaClient.queueLoad(newItemArray, 0,
                                REPEAT_MODE_REPEAT_OFF, null);
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

        private void startSupportedHostsStream(Episode episode, int wich) {


            easyPlexSupportedHosts = new EasyPlexSupportedHosts(context);

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


                                CastSession castSession = CastContext.getSharedInstance(context).getSessionManager()
                                        .getCurrentCastSession();

                                if (castSession != null && castSession.isConnected()) {

                                    onLoadChromcast(episode, castSession, vidURL.get(wich).getUrl());


                                }else {

                                    if (settingsManager.getSettings().getVlc() == 1) {


                                        final Dialog dialog = new Dialog(context);
                                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                        dialog.setContentView(R.layout.dialog_bottom_stream);
                                        dialog.setCancelable(false);
                                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

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
                                            Tools.streamEpisodeFromVlc(context,vidURL.get(i).getUrl(),episode,settingsManager);
                                            dialog.hide();
                                        });

                                        mxPlayer.setOnClickListener(v12 -> {
                                            Tools.streamEpisodeFromMxPlayer(context,vidURL.get(i).getUrl(),episode,settingsManager);
                                            dialog.hide();

                                        });

                                        webcast.setOnClickListener(v12 -> {

                                            Tools.streamEpisodeFromMxWebcast(context,vidURL.get(i).getUrl(),episode,settingsManager);
                                            dialog.hide();

                                        });

                                        easyplexPlayer.setOnClickListener(v12 -> {

                                            onLoadMainPlayerStream(episode,wich, vidURL.get(i).getUrl(), episode.getVideos().get(wich));
                                            dialog.hide();


                                        });

                                        dialog.show();
                                        dialog.getWindow().setAttributes(lp);

                                        dialog.findViewById(R.id.bt_close).setOnClickListener(x ->

                                                dialog.dismiss());


                                        dialog.show();
                                        dialog.getWindow().setAttributes(lp);


                                    } else {

                                        onLoadMainPlayerStream(episode,wich, vidURL.get(i).getUrl(), episode.getVideos().get(wich));
                                    }
                                }


                            });

                            builder.show();


                        }else  Toast.makeText(context, "NULL", Toast.LENGTH_SHORT).show();

                    }else {

                        CastSession castSession = CastContext.getSharedInstance(context).getSessionManager()
                                .getCurrentCastSession();

                        if (castSession != null && castSession.isConnected()) {

                            onLoadChromcast(episode, castSession, vidURL.get(wich).getUrl());


                        }else {

                            if (settingsManager.getSettings().getVlc() == 1) {


                                final Dialog dialog = new Dialog(context);
                                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                dialog.setContentView(R.layout.dialog_bottom_stream);
                                dialog.setCancelable(false);
                                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

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
                                    Tools.streamEpisodeFromVlc(context,vidURL.get(0).getUrl(),episode,settingsManager);
                                    dialog.hide();
                                });

                                mxPlayer.setOnClickListener(v12 -> {
                                    Tools.streamEpisodeFromMxPlayer(context,vidURL.get(0).getUrl(),episode,settingsManager);
                                    dialog.hide();

                                });

                                webcast.setOnClickListener(v12 -> {

                                    Tools.streamEpisodeFromMxWebcast(context,vidURL.get(0).getUrl(),episode,settingsManager);
                                    dialog.hide();

                                });

                                easyplexPlayer.setOnClickListener(v12 -> {

                                    onLoadMainPlayerStream(episode,wich, vidURL.get(0).getUrl(), episode.getVideos().get(wich));
                                    dialog.hide();


                                });

                                dialog.show();
                                dialog.getWindow().setAttributes(lp);

                                dialog.findViewById(R.id.bt_close).setOnClickListener(x ->

                                        dialog.dismiss());


                                dialog.show();
                                dialog.getWindow().setAttributes(lp);


                            } else {

                                onLoadMainPlayerStream(episode,wich, vidURL.get(0).getUrl(), episode.getVideos().get(wich));
                            }
                        }
                    }

                }

                @Override
                public void onError() {

                    Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
                }
            });

            easyPlexSupportedHosts.find(episode.getVideos().get(wich).getLink());


        }

        private void onLoadSubscribeDialog(Episode media, int position, boolean stream) {


            download = new Download(String.valueOf(media.getId()),String.valueOf(media.getId()),media.getStillPath(),currentTvShowName + " : " + "S0" +
                    currentSeasons + "E" + media.getEpisodeNumber() +
                    " : " + media.getName(),media.getLink());

            final Dialog dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_subscribe);
            dialog.setCancelable(false);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(dialog.getWindow().getAttributes());

            lp.gravity = Gravity.BOTTOM;
            lp.width = MATCH_PARENT;
            lp.height = MATCH_PARENT;

            dialog.findViewById(R.id.view_watch_ads_to_play).setOnClickListener(v -> {

                String defaultRewardedNetworkAds = settingsManager.getSettings().getDefaultRewardedNetworkAds();

                if (context.getString(R.string.applovin).equals(defaultRewardedNetworkAds)) {

                    maxRewardedAd = MaxRewardedAd.getInstance( settingsManager.getSettings().getApplovinRewardUnitid(), (AnimeDetailsActivity) context );
                    maxRewardedAd.loadAd();

                    onLoadApplovinAds(media,position,stream);

                } else  if (context.getString(R.string.vungle).equals(defaultRewardedNetworkAds)) {

                    onLoadVungleAds(media,position,stream);

                } else  if (context.getString(R.string.appnext).equals(defaultRewardedNetworkAds)) {

                    onLoadAppNextAds(media,position,stream);

                } else  if (context.getString(R.string.ironsource).equals(defaultRewardedNetworkAds)) {

                    onLoadIronSourceAds(media,position,stream);

                }else if (context.getString(R.string.unityads).equals(defaultRewardedNetworkAds)) {

                    onLoadUnityAds(media,position,stream);


                } else if (context.getString(R.string.admob).equals(defaultRewardedNetworkAds)) {

                    onLoadAdmobRewardAds(media,position,stream);


                }else if (context.getString(R.string.appodeal).equals(defaultRewardedNetworkAds)) {

                    onLoadAppOdealRewardAds(media,position,stream);

                } else if (context.getString(R.string.facebook).equals(defaultRewardedNetworkAds)) {

                    onLoadFaceBookRewardAds(media,position,stream);

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

        private void onLoadApplovinAds(Episode episode, int position, boolean stream) {


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

                    if (stream) {

                        onStartEpisode(episode,position);

                    }else {

                        onLoadDownloadsList(episode);
                    }

                    maxRewardedAd.loadAd();

                }

                @Override
                public void onUserRewarded(MaxAd ad, MaxReward reward) {
                    //
                }
            });
        }


        private void onLoadVungleAds(Episode episode, int position, boolean stream) {

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

                    if (stream) {

                        onStartEpisode(episode,position);

                    }else {

                        onLoadDownloadsList(episode);
                    }


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

        private void onLoadAppNextAds(Episode episode, int position, boolean stream) {

            mAppNextAdsVideoRewarded.showAd();

            // Get callback for ad loaded
            mAppNextAdsVideoRewarded.setOnAdLoadedCallback((s, appnextAdCreativeType) -> {

            });

            mAppNextAdsVideoRewarded.setOnAdOpenedCallback(() -> {

            });
            mAppNextAdsVideoRewarded.setOnAdClickedCallback(() -> {

            });

            mAppNextAdsVideoRewarded.setOnAdClosedCallback(() -> {
                if (stream) {

                    onStartEpisode(episode,position);

                }else {

                    onLoadDownloadsList(episode);
                }
            });

            mAppNextAdsVideoRewarded.setOnAdErrorCallback(error -> {

            });

            // Get callback when the user saw the video until the end (video ended)
            mAppNextAdsVideoRewarded.setOnVideoEndedCallback(() -> {


            });


        }

        private void onLoadIronSourceAds(Episode episode, int position, boolean stream) {

            IronSource.showRewardedVideo(settingsManager.getSettings().getIronsourceRewardPlacementName());

            IronSource.setLevelPlayRewardedVideoListener(new LevelPlayRewardedVideoListener() {
                @Override
                public void onAdOpened(AdInfo adInfo) {

                    //
                }

                @Override
                public void onAdShowFailed(IronSourceError ironSourceError, AdInfo adInfo) {
                    //
                }

                @Override
                public void onAdClicked(Placement placement, AdInfo adInfo) {
                    //
                }

                @Override
                public void onAdRewarded(Placement placement, AdInfo adInfo) {

                    if (stream) {

                        onStartEpisode(episode,position);

                    }else {

                        onLoadDownloadsList(episode);
                    }
                }

                @Override
                public void onAdClosed(AdInfo adInfo) {
                    //
                }

                @Override
                public void onAdAvailable(AdInfo adInfo) {
                    //
                }

                @Override
                public void onAdUnavailable() {
                    //
                }

            });

        }

        private void onLoadAppOdealRewardAds(Episode episode, int position, boolean stream) {

            Appodeal.show((AnimeDetailsActivity) context, Appodeal.REWARDED_VIDEO);

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

                    if (stream) {

                        onStartEpisode(episode,position);

                    }else {

                        onLoadDownloadsList(episode);
                    }

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



        private void onLoadFaceBookRewardAds(Episode episode, int position, boolean stream) {


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

                    if (stream) {

                        onStartEpisode(episode,position);

                    }else {

                        onLoadDownloadsList(episode);
                    }

                }


            };


            facebookInterstitialAd.loadAd(
                    facebookInterstitialAd.buildLoadAdConfig()
                            .withAdListener(interstitialAdListener)
                            .build());

        }

        private void onLoadAdmobRewardAds(Episode episode, int position, boolean stream) {

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
            mRewardedAd.show((AnimeDetailsActivity) context, rewardItem -> {
                if (stream) {

                    onStartEpisode(episode,position);

                }else {

                    onLoadDownloadsList(episode);
                }
            });


        }

        private void onLoadUnityAds(Episode episode, int position, boolean stream) {


            UnityAds.load(settingsManager.getSettings().getUnityRewardPlacementId(), new IUnityAdsLoadListener() {
                @Override
                public void onUnityAdsAdLoaded(String placementId) {

                    UnityAds.show ((AnimeDetailsActivity) context, settingsManager.getSettings().getUnityRewardPlacementId(), new IUnityAdsShowListener() {
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

                            if (stream) {

                                onStartEpisode(episode,position);

                            }else {

                                onLoadDownloadsList(episode);
                            }
                        }
                    });

                }

                @Override
                public void onUnityAdsFailedToLoad(String placementId, UnityAds.UnityAdsLoadError error, String message) {


                    //
                }
            });


        }



        private void onLoadEpisodeOffline(Episode episode) {

            mediaRepository.hasResume(episode.getId()).observe((AnimeDetailsActivity) context, resumeInfo -> {

                if (resumeInfo != null) {

                    if (resumeInfo.getTmdb() != null && resumeInfo.getResumePosition() !=null

                            && resumeInfo.getTmdb().equals(String.valueOf(episode.getId())) && Tools.id(context).equals(resumeInfo.getDeviceId())) {


                        double d = resumeInfo.getResumePosition();

                        double moveProgress = d * 100 / resumeInfo.getMovieDuration();


                        binding.resumeProgressBar.setVisibility(View.VISIBLE);
                        binding.resumeProgressBar.setProgress((int) moveProgress);

                        binding.timeRemaning.setText(Tools.getProgressTime((resumeInfo.getMovieDuration() - resumeInfo.getResumePosition()), true));



                    } else {

                        binding.resumeProgressBar.setProgress(0);
                        binding.resumeProgressBar.setVisibility(GONE);
                        binding.timeRemaning.setVisibility(GONE);

                    }

                }else {


                    binding.resumeProgressBar.setProgress(0);
                    binding.resumeProgressBar.setVisibility(GONE);
                    binding.timeRemaning.setVisibility(GONE);

                }

            });
        }


        private void onLoadMainPlayerStream(Episode episode, int position, String url, EpisodeStream episodeStream) {


            if (episodeStream.getHeader() !=null && !episodeStream.getHeader().isEmpty()) {

                settingsManager.getSettings().setHeader(episodeStream.getHeader());
            }


            if (episodeStream.getUseragent() !=null && !episodeStream.getUseragent().isEmpty()) {

                settingsManager.getSettings().setUserAgent(episodeStream.getUseragent());
            }

            String tvseasonid = seasonId;
            Integer currentep = Integer.parseInt(episode.getEpisodeNumber());
            String currentepname = episode.getName();
            String currenteptmdbnumber = String.valueOf(episode.getId());
            String currentepimdb = String.valueOf(episode.getId());
            String artwork = episode.getStillPath();
            String type = "anime";
            String name = "S0" + currentSeasons + "E" + episode.getEpisodeNumber() + " : " + episode.getName();

            Intent intent = new Intent(context, EasyPlexMainPlayer.class);
            intent.putExtra(EasyPlexPlayerActivity.EASYPLEX_MEDIA_KEY,
                    MediaModel.media(currentSerieId,
                            null,
                            null, type, name, url, artwork,
                            null, currentep
                            , currentSeasons, currentepimdb, tvseasonid,
                            currentepname,
                            currentSeasonsNumber, position,
                            currenteptmdbnumber, premuim,episodeStream.getHls(),
                            null,
                            externalId,serieCover,
                            episode.getHasrecap(),
                            episode.getSkiprecapStartIn()
                            ,mediaGenre,currentTvShowName,Float.parseFloat(episode.getVoteAverage()),
                            episodeStream.getDrmuuid(),episodeStream.getDrmlicenceuri(),episodeStream.getDrm()));
            intent.putExtra(ARG_MOVIE, media);
            context.startActivity(intent);

            history = new History(currentSerieId,currentSerieId,serieCover,name,"","");

            if (authManager.getSettingsProfile().getId() !=null) {

                history.setUserProfile(String.valueOf(authManager.getSettingsProfile().getId()));

            }


            history.setVoteAverage(Float.parseFloat(episode.getVoteAverage()));
            history.setSerieName(currentTvShowName);
            history.setPosterPath(serieCover);
            history.setTitle(name);
            history.setBackdropPath(episode.getStillPath());
            history.setEpisodeNmber(episode.getEpisodeNumber());
            history.setSeasonsId(tvseasonid);
            history.setType("anime");
            history.setTmdbId(currentSerieId);
            history.setPosition(position);
            history.setEpisodeId(String.valueOf(episode.getId()));
            history.setEpisodeName(episode.getName());
            history.setEpisodeTmdb(String.valueOf(episode.getId()));
            history.setSerieId(currentSerieId);
            history.setCurrentSeasons(currentSeasons);
            history.setSeasonsNumber(currentSeasonsNumber);
            history.setImdbExternalId(externalId);
            history.setPremuim(premuim);
            compositeDisposable.add(Completable.fromAction(() -> mediaRepository.addhistory(history))
                    .subscribeOn(Schedulers.io())
                    .subscribe());
        }


        private void createAndLoadRewardedAd() {

            String defaultRewardedNetworkAds = settingsManager.getSettings().getDefaultRewardedNetworkAds();

            if (context.getString(R.string.appnext).equals(defaultRewardedNetworkAds)) {

                // Initialize the AppNext Ads SDK.
                Appnext.init(context);

                mAppNextAdsVideoRewarded = new RewardedVideo(context, settingsManager.getSettings().getAppnextPlacementid());
                mAppNextAdsVideoRewarded.loadAd();

            } else  if (context.getString(R.string.vungle).equals(defaultRewardedNetworkAds)) {

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

            } else if (context.getString(R.string.applovin).equals(defaultRewardedNetworkAds)) {

                maxRewardedAd = MaxRewardedAd.getInstance(settingsManager.getSettings().getApplovinRewardUnitid(), (AnimeDetailsActivity) context);
                maxRewardedAd.loadAd();

            }else if (context.getString(R.string.appodeal).equals(settingsManager.getSettings().getDefaultRewardedNetworkAds())) {

                if (settingsManager.getSettings().getAdUnitIdAppodealRewarded() !=null) {

                    Appodeal.initialize((AnimeDetailsActivity) context, settingsManager.getSettings().getAdUnitIdAppodealRewarded(),Appodeal.REWARDED_VIDEO);

                }

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
            if (preferences.getString(
                    FsmPlayerApi.decodeServerMainApi2(), FsmPlayerApi.decodeServerMainApi4()).equals(FsmPlayerApi.decodeServerMainApi4())) { ((AnimeDetailsActivity)context).finish(); }
        }

    }




    @SuppressLint("StaticFieldLeak")
    private void onLoadDownloadsList(Episode episode) {

        if (settingsManager.getSettings().getSeparateDownload() == 1) {

            if (episode.getDownloads() !=null && !episode.getDownloads().isEmpty()) {

                onLoadEpisodeDownloadInfo(episode, episode.getDownloads());

            }else {

                DialogHelper.showNoDownloadAvailable(context,context.getString(R.string.about_no_stream_download));
            }

        }else if (episode.getVideos() !=null && !episode.getVideos().isEmpty()) {

            onLoadEpisodeDownloadInfo(episode, episode.getVideos());

        }else {

            DialogHelper.showNoDownloadAvailable(context,context.getString(R.string.about_no_stream_download));
        }

    }

    @SuppressLint("StaticFieldLeak")
    private void onLoadEpisodeDownloadInfo(Episode episode, List<EpisodeStream> downloads) {

        String[] charSequence = new String[downloads.size()];
        for (int i = 0; i<downloads.size(); i++) {
            charSequence[i] = String.valueOf(downloads.get(i).getServer());

        }


        final AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.MyAlertDialogTheme);
        builder.setTitle(R.string.select_quality);
        builder.setCancelable(true);
        builder.setItems(charSequence, (dialogInterface, wich) -> {

            if (downloads.get(wich).getEmbed() !=1) {

                if (settingsManager.getSettings().getAllowAdm() == 1) {


                    if (downloads.get(wich).getExternal()  == 1) {

                        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(downloads.get(wich).getLink())));

                    } else   if (downloads.get(wich).getSupportedHosts() == 1){


                        easyPlexSupportedHosts = new EasyPlexSupportedHosts(context);

                        if (settingsManager.getSettings().getHxfileApiKey() !=null && !settingsManager.getSettings().getHxfileApiKey().isEmpty())  {

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
                                        builder.setItems(name, (dialogInterface, i) -> onLoadDonwloadFromDialogs(episode,vidURL.get(i).getUrl(),downloads.get(wich)));

                                        builder.show();


                                    } else
                                        Toast.makeText(context, "NULL", Toast.LENGTH_SHORT).show();

                                } else {

                                    onLoadDonwloadFromDialogs(episode,vidURL.get(0).getUrl(), downloads.get(wich));
                                }

                            }

                            @Override
                            public void onError() {

                                Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
                            }
                        });

                        easyPlexSupportedHosts.find(downloads.get(wich).getLink());


                    }else {

                        onLoadDonwloadFromDialogs(episode,downloads.get(wich).getLink(), downloads.get(wich));

                    }



                } else {

                    if (downloads.get(wich).getExternal()  == 1) {

                        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(downloads.get(wich).getLink())));

                    }else   if (downloads.get(wich).getSupportedHosts() == 1){

                        easyPlexSupportedHosts = new EasyPlexSupportedHosts(context);


                        if (settingsManager.getSettings().getHxfileApiKey() !=null && !settingsManager.getSettings().getHxfileApiKey().isEmpty())  {

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
                                        builder.setItems(name, (dialogInterface, i) -> onLoadDownloadLink(episode, vidURL.get(i).getUrl(), downloads.get(wich)));

                                        builder.show();


                                    } else
                                        Toast.makeText(context, "NULL", Toast.LENGTH_SHORT).show();

                                } else {


                                    onLoadDownloadLink(episode, vidURL.get(0).getUrl(), downloads.get(wich));
                                }

                            }

                            @Override
                            public void onError() {

                                Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
                            }
                        });

                        easyPlexSupportedHosts.find(downloads.get(wich).getLink());


                    }else {

                        onLoadDownloadLink(episode, downloads.get(wich).getLink(), downloads.get(wich));

                    }

                }


            }else {

                DialogHelper.showNoDownloadAvailable(context,context.getString(R.string.about_no_stream_download));
            }



        });

        builder.show();
    }

    private void onLoadDownloadLink(Episode episode, String url, EpisodeStream downloads) {

        String name = "S0" + currentSeasons + "E" + episode.getEpisodeNumber() + " : " + episode.getName();

        FragmentManager fm = ((FragmentActivity)context).getSupportFragmentManager();
        addDownloadDialog = (AddDownloadDialog)fm.findFragmentByTag(TAG_DOWNLOAD_DIALOG);
        if (addDownloadDialog == null) {
            AddInitParams initParams = null;
            Intent i = ((FragmentActivity)context).getIntent();
            if (i != null)
                initParams = i.getParcelableExtra(AddDownloadActivity.TAG_INIT_PARAMS);
            if (initParams == null) {
                initParams = new AddInitParams();
            }
            fillInitParams(initParams, episode, url,downloads);
            addDownloadDialog = AddDownloadDialog.newInstance(initParams);
            addDownloadDialog.show(fm, TAG_DOWNLOAD_DIALOG);
        }



        download = new Download(String.valueOf(episode.getId()),String.valueOf(episode.getId()),episode.getStillPath(),name,"");

        download.setId(String.valueOf(episode.getId()));
        download.setPosterPath(serieCover);
        download.setTitle(name);
        download.setName(name);
        download.setBackdropPath(episode.getStillPath());
        download.setEpisodeNmber(episode.getEpisodeNumber());
        download.setSeasonsId(seasonId);
        download.setPosition(0);
        download.setType(MEDIA_TYPE);
        download.setTmdbId(currentSerieId);
        download.setEpisodeId(String.valueOf(episode.getId()));
        download.setEpisodeName(episode.getName());
        download.setEpisodeTmdb(String.valueOf(episode.getId()));
        download.setSerieId(currentSerieId);
        download.setSerieName(currentTvShowName);
        download.setOverview(episode.getOverview());
        download.setCurrentSeasons(currentSeasons);
        download.setSeasonsId(seasonId);
        download.setSeasonsNumber(currentSeasonsNumber);
        download.setImdbExternalId(externalId);
        download.setPremuim(premuim);
        download.setHls(episode.getHls());
        download.setHasrecap(episode.getHasrecap());
        download.setSkiprecapStartIn(episode.getSkiprecapStartIn());
        download.setMediaGenre(mediaGenre);
        download.setOverview(media.getOverview());

        compositeDisposable.add(Completable.fromAction(() -> mediaRepository.addMovie(download))
                .subscribeOn(Schedulers.io())
                .subscribe());
    }



    private void fillInitParams(AddInitParams params, Episode episode, String downloadUrl, EpisodeStream downloads)
    {

        String ePname = "S0" + currentSeasons + "E" + episode.getEpisodeNumber() + " : " + episode.getName();

        String name = "S0" + currentSeasons + "E" + episode.getEpisodeNumber() + "_" + episode.getName();

        SettingsRepository pref = RepositoryHelper.getSettingsRepository(context);
        SharedPreferences localPref = PreferenceManager.getDefaultSharedPreferences(context);

        if (params.url == null) {
            params.url = downloadUrl;
        }

        if (params.type == null) {
            params.type = MEDIA_TYPE;
        }


        if (params.fileName == null) {
            params.fileName = name.replaceAll("[^a-zA-Z0-9_-]", "");

        }

        if (downloads.getUseragent() !=null && !downloads.getUseragent().isEmpty()){

            if (params.userAgent == null) {
                params.userAgent = downloads.getUseragent();
            }
        }


        if (downloads.getHeader() !=null && !downloads.getHeader().isEmpty()){

            if (params.refer == null) {
                params.refer = downloads.getHeader();
            }
        }


        if (params.mediaId == null) {
            params.mediaId = String.valueOf(episode.getId());
        }


        if (params.mediaName == null) {
            params.mediaName = media.getName() + " : " + ePname;
        }


        if (params.mediabackdrop == null) {
            params.mediabackdrop = episode.getStillPath();
        }



        if (params.dirPath == null) {
            params.dirPath = Uri.parse(pref.saveDownloadsIn());
        }
        if (params.retry == null) {
            params.retry = localPref.getBoolean(
                    context.getString(R.string.add_download_retry_flag),
                    true
            );
        }
        if (params.replaceFile == null) {
            params.replaceFile = localPref.getBoolean(
                    context.getString(R.string.add_download_replace_file_flag),
                    false
            );
        }
        if (params.unmeteredConnectionsOnly == null) {
            params.unmeteredConnectionsOnly = localPref.getBoolean(
                    context.getString(R.string.add_download_unmetered_only_flag),
                    false
            );
        }
        if (params.numPieces == null) {
            params.numPieces = localPref.getInt(
                    context.getString(R.string.add_download_num_pieces),
                    DownloadInfo.MIN_PIECES
            );
        }
    }



    private void onLoadDonwloadFromDialogs(Episode episode, String url, EpisodeStream downloads) {

        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_download_options);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());

        lp.gravity = Gravity.BOTTOM;
        lp.width = MATCH_PARENT;
        lp.height = MATCH_PARENT;


        LinearLayout withAdm = dialog.findViewById(R.id.withAdm);
        LinearLayout withApp = dialog.findViewById(R.id.withApp);
        LinearLayout with1App = dialog.findViewById(R.id.with1DM);

        withAdm.setOnClickListener(v12 -> {
            Tools.downloadFromAdm(context,url,true,media,settingsManager, episode, true);
            dialog.dismiss();

        });

        with1App.setOnClickListener(v12 -> {
            Tools.downloadFrom1dm(context, url, true, media, settingsManager, episode, true);
            dialog.dismiss();
        });

        withApp.setOnClickListener(v12 -> {
            onLoadDownloadLink(episode,url, downloads);
            dialog.dismiss();
        });



        dialog.show();
        dialog.getWindow().setAttributes(lp);

        dialog.findViewById(R.id.bt_close).setOnClickListener(x ->

                dialog.dismiss());


        dialog.show();
        dialog.getWindow().setAttributes(lp);
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

    private static final DiffUtil.ItemCallback<Episode> ITEM_CALLBACK =
            new DiffUtil.ItemCallback<>() {
                @Override
                public boolean areItemsTheSame(Episode oldItem, Episode newItem) {
                    return oldItem.getId().equals(newItem.getId());
                }

                @SuppressLint("DiffUtilEquals")
                @Override
                public boolean areContentsTheSame(Episode oldItem, @NotNull Episode newItem) {
                    return oldItem.equals(newItem);
                }
            };

    private int lastPosition = -1;
    private final boolean onAttach = true;

    private void setAnimation(View view, int position) {
        if (position > lastPosition) {
            ItemAnimation.animate(view, onAttach ? position : -1, animationType);
            lastPosition = position;
        }
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        compositeDisposable.clear();
        adsLaunched = false;
    }


    @Override
    public void onViewDetachedFromWindow(@NonNull NextPlayMoviesViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        compositeDisposable.clear();
        adsLaunched = false;
    }
}
