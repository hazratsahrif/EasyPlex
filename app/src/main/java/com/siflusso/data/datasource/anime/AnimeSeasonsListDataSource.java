package com.siflusso.data.datasource.anime;

import androidx.annotation.NonNull;
import androidx.paging.PageKeyedDataSource;
import com.siflusso.data.model.episode.Episode;
import com.siflusso.data.remote.ApiInterface;
import com.siflusso.data.remote.ServiceGenerator;
import com.siflusso.ui.manager.SettingsManager;
import org.jetbrains.annotations.NotNull;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class AnimeSeasonsListDataSource extends PageKeyedDataSource<Integer, Episode> {

    private final String genreId;

    private final SettingsManager settingsManager;

    public AnimeSeasonsListDataSource(String genreId, SettingsManager settingsManager){

        this.settingsManager = settingsManager;
        this.genreId = genreId;

    }

    public static final int PAGE_SIZE = 12;
    private static final int FIRST_PAGE = 1;



    @Override
    public void loadInitial(@NonNull LoadInitialParams<Integer> params, @NonNull LoadInitialCallback<Integer, Episode> callback) {

        ApiInterface apiInterface = ServiceGenerator.createService(ApiInterface.class);
        Call<Episode> call = apiInterface.getAnimeSeasonsPaginate(genreId,settingsManager.getSettings().getApiKey(),FIRST_PAGE);
        call.enqueue(new Callback<>() {

            @Override
            public void onResponse(@NotNull Call<Episode> call, @NotNull Response<Episode> response) {


                if (response.isSuccessful()) {

                    callback.onResult(response.body().getGlobaldata(), null, FIRST_PAGE + 1);


                }
            }

            @Override
            public void onFailure(@NotNull Call<Episode> call, @NotNull Throwable t) {

                //
            }
        });

    }

    public void loadBefore(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Integer, Episode> callback) {


        ApiInterface apiInterface = ServiceGenerator.createService(ApiInterface.class);
        Call<Episode> call = apiInterface.getAnimeSeasonsPaginate(genreId,settingsManager.getSettings().getApiKey(),params.key);
        call.enqueue(new Callback<>() {

            @Override
            public void onResponse(@NotNull Call<Episode> call, @NotNull Response<Episode> response) {


                if (response.isSuccessful()) {

                    Integer key = (params.key > 1) ? params.key - 1 : null;
                    callback.onResult(response.body().getGlobaldata(), key);


                }
            }

            @Override
            public void onFailure(@NotNull Call<Episode> call, @NotNull Throwable t) {
                //
            }
        });


    }

    @Override
    public void loadAfter(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Integer, Episode> callback) {

        ApiInterface apiInterface = ServiceGenerator.createService(ApiInterface.class);

        Call<Episode> call = apiInterface.getAnimeSeasonsPaginate(genreId,settingsManager.getSettings().getApiKey(),params.key);
        call.enqueue(new Callback<Episode>() {

            @Override
            public void onResponse(@NotNull Call<Episode> call, @NotNull Response<Episode> response) {


                if (response.isSuccessful()) {

                    callback.onResult(response.body().getGlobaldata(), params.key + 1);


                }
            }

            @Override
            public void onFailure(@NotNull Call<Episode> call, @NotNull Throwable t) {
                //
            }
        });


    }

}