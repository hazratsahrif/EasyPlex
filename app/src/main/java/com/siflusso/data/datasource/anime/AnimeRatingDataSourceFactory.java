package com.siflusso.data.datasource.anime;

import androidx.lifecycle.MutableLiveData;
import androidx.paging.DataSource;
import androidx.paging.PageKeyedDataSource;

import com.siflusso.data.local.entity.Media;
import com.siflusso.data.remote.ApiInterface;
import com.siflusso.ui.manager.SettingsManager;

import javax.inject.Inject;

public class AnimeRatingDataSourceFactory extends DataSource.Factory {

    private final MutableLiveData<PageKeyedDataSource<Integer, Media>> serieLiveDataSource = new MutableLiveData<>();

    private final ApiInterface requestInterface;
    private final SettingsManager settingsManager;

    @Inject
    public AnimeRatingDataSourceFactory(ApiInterface requestInterface,SettingsManager settingsManager) {
        this.requestInterface = requestInterface;
        this.settingsManager = settingsManager;
    }

    @Override
    public DataSource create() {

        AnimeRatingDataSource serieDataSource = new AnimeRatingDataSource(requestInterface,settingsManager);
        serieLiveDataSource.postValue(serieDataSource);

        return serieDataSource;

    }

    public MutableLiveData<PageKeyedDataSource<Integer, Media>> getItemLiveDataSource() {
        return serieLiveDataSource;
    }

}
