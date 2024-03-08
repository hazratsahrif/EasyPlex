package com.siflusso.ui.seriedetails;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.applovin.mediation.ads.MaxRewardedAd;
import com.appnext.ads.fullscreen.RewardedVideo;
import com.easyplex.easyplexsupportedhosts.EasyPlexSupportedHosts;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.siflusso.data.local.entity.Download;
import com.siflusso.data.local.entity.History;
import com.siflusso.data.local.entity.Media;
import com.siflusso.data.model.episode.Episode;
import com.siflusso.data.model.serie.Season;
import com.siflusso.data.repository.MediaRepository;
import com.siflusso.databinding.SpinnerLayoutBinding;
import com.siflusso.ui.comments.CommentsAdapter;
import com.siflusso.ui.downloadmanager.ui.adddownload.AddDownloadDialog;
import com.siflusso.ui.manager.AuthManager;
import com.siflusso.ui.manager.SettingsManager;
import com.siflusso.ui.manager.TokenManager;

import java.util.List;

import io.reactivex.rxjava3.disposables.CompositeDisposable;

public class SpinnerAdapter extends RecyclerView.Adapter<SpinnerAdapter.ViewHolder> {
    SpinnerLayoutBinding binding;

    private static final String TAG_DOWNLOAD_DIALOG = "add_download_dialog";
    private List<Episode> episodeList;
    private final String externalId;
    private final String currentSerieId;
    private final String currentSeasons;
    private Download download;
    private final Media media;
    String seasonId;
    private boolean adsLaunched = false;
    private final String currentSeasonsNumber;
    private final String currentTvShowName;
    private final int premuim;
    private final String serieCover;
    private final SharedPreferences preferences;
    private final AuthManager authManager;
    private final SettingsManager settingsManager;
    private final Context context;
    private final MediaRepository mediaRepository;
    private final TokenManager tokenManager;
    private final String mediaGenre;
    private Media seriesDetail;
    EpisodeAdapter mEpisodesSerieAdapter;
    List<Season> season;

    public SpinnerAdapter(String serieid, String seasonsid, String seasonsidpostion, String currentseason, SharedPreferences preferences, AuthManager authManager

            , SettingsManager settingsManager, MediaRepository mediaRepository, String currentTvShowName, int
                                  premuim, TokenManager tokenManager, Context context, String serieCover, Media media, String mediaGenre,
                          String externalId,Media seriesDetail,
                          List<Season> season) {
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
        this.seriesDetail = seriesDetail;
        this.season = season;

    }

    @NonNull
    @Override
    public SpinnerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = SpinnerLayoutBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull SpinnerAdapter.ViewHolder holder, int position) {
        binding.planetsSpinner.setItem(season);
        binding.planetsSpinner.setSelection(0);

        binding.recyclerView.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(@NonNull View view) {

            }

            @Override
            public void onChildViewDetachedFromWindow(@NonNull View view) {

            }
        });
        binding.recyclerView.setNestedScrollingEnabled(true);

        binding.planetsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                Season season = (Season) adapterView.getItemAtPosition(position);
                String episodeId = String.valueOf(season.getId());
                String currentSeason = season.getName();
                String seasonNumber = season.getSeasonNumber();
                mEpisodesSerieAdapter = new EpisodeAdapter(seriesDetail.getId(),seasonNumber,episodeId,currentSeason,preferences,authManager,settingsManager,mediaRepository
                        ,seriesDetail.getName(),seriesDetail.getPremuim(),tokenManager,context,seriesDetail.getPosterPath(),seriesDetail,mediaGenre,externalId,seriesDetail);
                binding.recyclerView.setLayoutManager(new LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false));
                binding.recyclerView.setHasFixedSize(true);
                binding.recyclerView.setHovered(true);
                binding.recyclerView.setNestedScrollingEnabled(true);
                Toast.makeText(context, "Size : "+season.getEpisodes().size(), Toast.LENGTH_SHORT).show();
                mEpisodesSerieAdapter.addSeasons(season.getEpisodes());
                binding.recyclerView.setAdapter(mEpisodesSerieAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

                // do nothing if no season selected

            }
        });

    }

    @Override
    public int getItemCount() {
        return 1;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        SpinnerLayoutBinding binding;

        public ViewHolder(@NonNull SpinnerLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
