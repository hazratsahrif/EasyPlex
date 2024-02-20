package com.siflusso.ui.mylist;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import com.siflusso.R;
import com.siflusso.data.repository.AuthRepository;
import com.siflusso.data.repository.MediaRepository;
import com.siflusso.databinding.FragmentFavouriteMoviesBinding;
import com.siflusso.di.Injectable;
import com.siflusso.ui.manager.SettingsManager;
import com.siflusso.ui.manager.TokenManager;
import com.siflusso.ui.viewmodels.LoginViewModel;
import com.siflusso.ui.viewmodels.MoviesListViewModel;
import com.siflusso.util.SpacingItemDecoration;
import com.siflusso.util.Tools;

import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;


public class SeriesListFragment extends Fragment implements Injectable , DeleteFavoriteDetectListner {


    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private SeriesMyListdapter seriesMyListdapter;
    private SeriesOnlineMyListdapter seriesOnlineMyListdapter;

    private MoviesListViewModel moviesListViewModel;


    FragmentFavouriteMoviesBinding binding;

    @Inject
    MediaRepository mediaRepository;

    @Inject
    AuthRepository authRepository;

    @Inject
    TokenManager tokenManager;

    @Inject
    SettingsManager settingsManager;

    private LoginViewModel loginViewModel;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    @Nullable
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_favourite_movies, container, false);

        // ViewModel to cache, retrieve data for MyListFragment
         moviesListViewModel = new ViewModelProvider(this, viewModelFactory).get(MoviesListViewModel.class);

        loginViewModel = new ViewModelProvider(this, viewModelFactory).get(LoginViewModel.class);

        seriesMyListdapter = new SeriesMyListdapter(mediaRepository);

        seriesOnlineMyListdapter = new SeriesOnlineMyListdapter(mediaRepository,authRepository,this);

        onLoadListData();

        binding.rvMylist.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        binding.rvMylist.addItemDecoration(new SpacingItemDecoration(3, Tools.dpToPx(requireActivity(), 0), true));
        binding.rvMylist.setHasFixedSize(true);

        return  binding.getRoot();

    }


    private void onLoadListData() {

        if (settingsManager.getSettings().getFavoriteonline() == 1 && tokenManager.getToken().getAccessToken() !=null) {

            onLoadSeriesListOnline();

        } else {

            onLoadSeriesListOffline();
        }
    }

    private void onLoadSeriesListOnline() {

        loginViewModel.getAuthDetails();
        loginViewModel.authDetailMutableLiveData.observe(getViewLifecycleOwner(), auth -> {

            seriesOnlineMyListdapter.addToContent(auth.getFavoritesSeries(),requireActivity());
            binding.rvMylist.setAdapter(seriesOnlineMyListdapter);
            binding.rvMylist.setEmptyView(binding.noResults);

        });

    }

    private void onLoadSeriesListOffline() {

        moviesListViewModel.getSeriesFavorites().observe(getViewLifecycleOwner(), favoriteMovies -> {

                seriesMyListdapter.addToContent(favoriteMovies,requireActivity());
                binding.rvMylist.setAdapter(seriesMyListdapter);
                binding.rvMylist.setEmptyView(binding.noResults);

        });
    }




    @Override
    public void onResume() {
        super.onResume();
        onLoadListData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding.rvMylist.setAdapter(null);
        binding =null;


    }


    @Override
    public void onMediaDeletedSuccess(boolean clicked) {
        if (clicked) {
            onLoadListData();
        }
    }
}
