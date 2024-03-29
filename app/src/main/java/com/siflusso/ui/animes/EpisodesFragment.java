package com.siflusso.ui.animes;

import static com.siflusso.util.Constants.SPECIALS;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.siflusso.R;
import com.siflusso.data.local.entity.Media;
import com.siflusso.data.model.genres.Genre;
import com.siflusso.data.model.serie.Season;
import com.siflusso.data.repository.MediaRepository;
import com.siflusso.databinding.LayoutEpisodesFragmentBinding;
import com.siflusso.di.Injectable;
import com.siflusso.ui.manager.AuthManager;
import com.siflusso.ui.manager.SettingsManager;
import com.siflusso.ui.manager.TokenManager;
import com.siflusso.ui.viewmodels.AnimeViewModel;
import com.siflusso.util.ItemAnimation;

import java.util.Iterator;

import javax.inject.Inject;


public class EpisodesFragment extends Fragment implements Injectable {


    private String mediaGenre;

    LayoutEpisodesFragmentBinding binding;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private AnimeViewModel animeViewModel;

    @Inject
    TokenManager tokenManager;

    @Inject
    MediaRepository mediaRepository;

    @Inject
    SharedPreferences sharedPreferences;

    @Inject
    AuthManager authManager;

    @Inject
    SettingsManager settingsManager;

    EpisodeAnimeAdapter episodeAnimeAdapter;


    private static final int ANIMATION_TYPE = ItemAnimation.FADE_IN;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.layout_episodes_fragment, container, false);

        animeViewModel = new ViewModelProvider(this, viewModelFactory).get(AnimeViewModel.class);

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
        binding.recyclerView.setItemViewCacheSize(4);

        Media serieDetail = requireArguments().getParcelable("serieDetail");

        for (Genre genre : serieDetail.getGenres()) {
            mediaGenre = genre.getName();
        }



        if (serieDetail.getSeasons() !=null && !serieDetail.getSeasons().isEmpty()) {

            for(Iterator<Season> iterator = serieDetail.getSeasons().iterator(); iterator.hasNext(); ) {
                if(iterator.next().getName().equals(SPECIALS))
                    iterator.remove();
            }
            binding.planetsSpinner.setItem(serieDetail.getSeasons());
            binding.planetsSpinner.setSelection(0);
            binding.planetsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {


                    Season season = (Season) adapterView.getItemAtPosition(position);
                    String episodeId = String.valueOf(season.getId());
                    String currentSeason = season.getName();
                    String seasonNumber = season.getSeasonNumber();

                    animeViewModel.searchQuery.setValue(episodeId);

                    // Episodes RecycleView
                    episodeAnimeAdapter = new EpisodeAnimeAdapter(serieDetail.getId(),
                            seasonNumber,episodeId,currentSeason,
                            sharedPreferences,authManager,settingsManager,mediaRepository
                            , serieDetail.getName(), serieDetail.getPremuim()
                            ,tokenManager,requireActivity()
                            , serieDetail.getPosterPath()
                            , serieDetail,mediaGenre,
                            serieDetail.getImdbExternalId()
                    ,ANIMATION_TYPE);

                    animeViewModel.getAnimeSeasons().observe(getViewLifecycleOwner(), animesLists -> episodeAnimeAdapter.submitList(animesLists));
                    binding.recyclerView.setAdapter(episodeAnimeAdapter);

                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                    // do nothing if no season selected

                }
            });


        }


        return binding.getRoot();


    }



    // On Fragment Detach clear binding views & adapters to avoid memory leak
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding.recyclerView.setAdapter(null);
        binding.constraintLayout.removeAllViews();
        binding = null;
    }

}
