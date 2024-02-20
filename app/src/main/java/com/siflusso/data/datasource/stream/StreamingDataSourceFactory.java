package com.siflusso.data.datasource.stream;

import androidx.annotation.NonNull;
import androidx.paging.DataSource;
import com.siflusso.data.local.entity.Media;
import com.siflusso.ui.manager.SettingsManager;


public class StreamingDataSourceFactory extends DataSource.Factory<Integer, Media> {

    private final String query;
    private final SettingsManager settingsManager;

    public StreamingDataSourceFactory(String query, SettingsManager settingsManager) {
        this.settingsManager = settingsManager;
        this.query = query;
    }

    @NonNull
    @Override
    public DataSource<Integer, Media> create() {
        return new StreamDataSource( query, settingsManager);
    }
}
