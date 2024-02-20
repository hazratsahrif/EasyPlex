package com.siflusso.ui.moviedetails.tabfragment;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.siflusso.data.repository.AuthRepository;
import com.siflusso.data.repository.MediaRepository;
import com.siflusso.ui.manager.SettingsManager;
import com.siflusso.ui.viewmodels.MovieDetailViewModel;

public class MyViewModelFactory implements ViewModelProvider.Factory  {
    private final MediaRepository mediaRepository;
    private final SettingsManager settingsManager;
    private final AuthRepository authRepository;

    public MyViewModelFactory(MediaRepository mediaRepository, SettingsManager settingsManager, AuthRepository authRepository) {
        this.mediaRepository = mediaRepository;
        this.settingsManager = settingsManager;
        this.authRepository = authRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(MovieDetailViewModel.class)) {
            return (T) new MovieDetailViewModel(mediaRepository, settingsManager, authRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
