package com.siflusso.ui.library;

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
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;

import com.siflusso.R;
import com.siflusso.data.model.genres.Genre;
import com.siflusso.databinding.LayoutGenresBinding;
import com.siflusso.di.Injectable;
import com.siflusso.ui.manager.SettingsManager;
import com.siflusso.ui.viewmodels.GenresViewModel;
import com.siflusso.util.ItemAnimation;
import com.siflusso.util.SpacingItemDecoration;
import com.siflusso.util.Tools;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;



public class AnimesFragment extends Fragment implements Injectable {

    LayoutGenresBinding binding;

    @Inject
    ViewModelProvider.Factory viewModelFactory;
    private GenresViewModel genresViewModel;
    ItemAdapter adapter;
    private List<String> provinceList;
    private static final int ANIMATION_TYPE = ItemAnimation.FADE_IN;


    @Inject
    SettingsManager settingsManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.layout_genres, container, false);

        genresViewModel = new ViewModelProvider(this, viewModelFactory).get(GenresViewModel.class);

        adapter = new ItemAdapter(requireActivity(), ANIMATION_TYPE,settingsManager);

        onLoadAllGenres();

        onLoadGenres();

        if (Tools.isRTL(Locale.getDefault())){

            binding.filterBtn.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            binding.filterBtn.setBackgroundResource(R.drawable.bg_episodes_rtl);
            binding.filterBtnAllgenres.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            binding.filterBtnAllgenres.setBackgroundResource(R.drawable.bg_episodes);
        }


        binding.recyclerView.setLayoutManager(new GridLayoutManager(requireActivity(), 3));
        binding.recyclerView.addItemDecoration(new SpacingItemDecoration(3, Tools.dpToPx(requireActivity(), 0), true));
        binding.recyclerView.setAdapter(adapter);

        return binding.getRoot();

    }



    private void onLoadGenres() {

        binding.filterBtn.setOnClickListener(v -> binding.planetsSpinner.performClick());

        genresViewModel.getMoviesGenresList();
        genresViewModel.movieDetailMutableLiveData.observe(getViewLifecycleOwner(), movieDetail -> {

            if (!movieDetail.getGenresPlayer().isEmpty()) {

                binding.noMoviesFound.setVisibility(View.GONE);

                binding.planetsSpinner.setItem(movieDetail.getGenresPlayer());
                binding.planetsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {

                        binding.planetsSpinner.setVisibility(View.GONE);
                        binding.filterBtn.setVisibility(View.VISIBLE);
                        Genre genre = (Genre) adapterView.getItemAtPosition(position);
                        int genreId = genre.getId();
                        String genreName = genre.getName();

                        binding.selectedGenre.setText(genreName);


                        genresViewModel.searchQuery.setValue(String.valueOf(genreId));
                        genresViewModel.getAnimesGenresitemPagedList().observe(getViewLifecycleOwner(), genresList -> {

                            if (genresList !=null) {
                                adapter.submitList(genresList);


                            }


                        });

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                        // Invoked when a network exception occurred talking to the server or when an unexpected exception occurred creating the request or processing the response.

                    }
                });


            }else {

                binding.noMoviesFound.setVisibility(View.VISIBLE);

            }


        });


    }



    private void onLoadAllGenres() {

        provinceList = new ArrayList<>();
        provinceList.add(getString(R.string.all_genres));
        provinceList.add(getString(R.string.latest_added));
        provinceList.add(getString(R.string.by_rating));
        provinceList.add(getString(R.string.by_year));
        provinceList.add(getString(R.string.by_views));

        binding.filterBtnAllgenres.setOnClickListener(v -> binding.planetsSpinnerSort.performClick());

        binding.noMoviesFound.setVisibility(View.GONE);

        binding.planetsSpinnerSort.setItem(provinceList);
        binding.planetsSpinnerSort.setSelection(0);
        binding.planetsSpinnerSort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {



                switch (position) {

                    case 0:
                        String genreName = provinceList.get(0);
                        binding.selectedGenreLeft.setText(genreName);
                        genresViewModel.animePagedList.observe(getViewLifecycleOwner(), favoriteMovies -> adapter.submitList(favoriteMovies));

                        break;
                    case 1:

                        String latest = provinceList.get(1);
                        binding.selectedGenreLeft.setText(latest);
                        genresViewModel.animeLatestPagedList.observe(getViewLifecycleOwner(), favoriteMovies -> adapter.submitList(favoriteMovies));

                        break;
                    case 2:

                        String rating = provinceList.get(2);
                        binding.selectedGenreLeft.setText(rating);
                        genresViewModel.animeRatingPagedList.observe(getViewLifecycleOwner(), favoriteMovies -> adapter.submitList(favoriteMovies));

                        break;
                    case 3:


                        String year = provinceList.get(3);
                        binding.selectedGenreLeft.setText(year);
                        genresViewModel.animeYearPagedList.observe(getViewLifecycleOwner(), favoriteMovies -> adapter.submitList(favoriteMovies));

                        break;
                    case 4:


                        String serieview = provinceList.get(4);
                        binding.selectedGenreLeft.setText(serieview);

                        genresViewModel.animeViewsgPagedList.observe(getViewLifecycleOwner(), favoriteMovies -> adapter.submitList(favoriteMovies));

                        break;
                    default:
                        break;
                }


            }


            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

                // Nothting to refresh when no Item Selected

            }
        });

    }




    // On Fragment Detach clear binding views &  adapters to avoid memory leak
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding.recyclerView.setAdapter(null);
        binding.constraintLayout.removeAllViews();
        binding.planetsSpinner.clearSelection();
        binding = null;


    }

}
