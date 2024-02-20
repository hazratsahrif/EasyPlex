package com.siflusso.data.datasource.medialibrary;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.DataSource;
import androidx.paging.PageKeyedDataSource;

import com.siflusso.data.datasource.genreslist.MoviesGenreListDataSource;
import com.siflusso.data.datasource.movie.MovieDataSource;
import com.siflusso.data.local.entity.Media;
import com.siflusso.data.remote.ApiInterface;
import com.siflusso.ui.manager.SettingsManager;

import javax.inject.Inject;

public class MediaLibraryDataSourceFactory extends DataSource.Factory {

    private final String query;

    private final SettingsManager settingsManager;

    public MediaLibraryDataSourceFactory(String query,SettingsManager settingsManager) {
        this.settingsManager = settingsManager;
        this.query = query;

    }

    @NonNull
    @Override
    public DataSource<Integer, Media> create() {
        return new MoviesGenreListDataSource(query, settingsManager);
    }

}
