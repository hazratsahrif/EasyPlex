package com.siflusso.ui.library;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.siflusso.R;
import com.siflusso.data.datasource.genreslist.ByGenreListDataSource;
import com.siflusso.data.datasource.networks.NetworksListDataSourceFactory;
import com.siflusso.data.local.entity.Media;
import com.siflusso.data.model.networks.Network;
import com.siflusso.data.repository.MediaRepository;
import com.siflusso.databinding.ItemNetworkBinding;
import com.siflusso.databinding.RowItemCategoryBinding;
import com.siflusso.ui.base.BaseActivity;
import com.siflusso.ui.home.adapters.ByGenreAdapter;
import com.siflusso.util.ItemAnimation;
import com.siflusso.util.SpacingItemDecoration;
import com.siflusso.util.Tools;

import java.util.List;

/**
 * Adapter for Movie.
 *
 * @author Yobex.
 */
public class NetworkssAdapter extends RecyclerView.Adapter<NetworkssAdapter.MainViewHolder> {

    private List<Network> castList;
    private Context context;
    public final MutableLiveData<String> searchQuery = new MutableLiveData<>();
    private MediaRepository mediaRepository;
    private ByGenreAdapter byGenreAdapter;


    public void addMain(List<Network> castList, Context context,MediaRepository mediaRepository,ByGenreAdapter byGenreAdapter) {
        this.castList = castList;
        this.context = context;
        this.mediaRepository = mediaRepository;
        this.byGenreAdapter = byGenreAdapter;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public NetworkssAdapter.MainViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        RowItemCategoryBinding binding = RowItemCategoryBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);


        return new NetworkssAdapter.MainViewHolder(binding);

    }

    @Override
    public void onBindViewHolder(@NonNull MainViewHolder holder, int position) {

        holder.onBind(position);

    }



    @Override
    public int getItemCount() {
        if (castList != null) {
            return castList.size();
        } else {
            return 0;
        }
    }

    class MainViewHolder extends RecyclerView.ViewHolder {

        private final RowItemCategoryBinding binding;

        MainViewHolder(@NonNull RowItemCategoryBinding binding)
        {
            super(binding.getRoot());

            this.binding = binding;
        }


        void onBind(final int position) {

            final Network network = castList.get(position);

            Tools.onLoadMediaCoverAdapters(context,binding.image, network.getLogoPath());

            binding.lytParent.setOnClickListener(v -> {


                final Dialog dialog = new Dialog(context);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_movies_by_genres);
                dialog.setCancelable(false);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(dialog.getWindow().getAttributes());

                lp.gravity = Gravity.BOTTOM;
                lp.width = MATCH_PARENT;
                lp.height = MATCH_PARENT;

                RecyclerView recyclerView = dialog.findViewById(R.id.rv_movies_genres);
                TextView mGenreType = dialog.findViewById(R.id.movietitle);

                mGenreType.setText(network.getName());

                searchQuery.setValue(String.valueOf(network.getId()));
                getByGenresitemPagedList().observe(((BaseActivity)context), genresList -> {

                    recyclerView.setLayoutManager(new GridLayoutManager(context, 3));
                    recyclerView.addItemDecoration(new SpacingItemDecoration(3, Tools.dpToPx(context, 0), true));
                    byGenreAdapter.submitList(genresList);
                    recyclerView.setAdapter(byGenreAdapter);


                });



                dialog.show();
                dialog.getWindow().setAttributes(lp);

                dialog.findViewById(R.id.bt_close).setOnClickListener(x ->

                        dialog.dismiss());


                dialog.show();
                dialog.getWindow().setAttributes(lp);

            });

        }


    }


    PagedList.Config config =
            (new PagedList.Config.Builder())
                    .setEnablePlaceholders(false)
                    .setPageSize(ByGenreListDataSource.PAGE_SIZE)
                    .setPrefetchDistance(ByGenreListDataSource.PAGE_SIZE)
                    .setInitialLoadSizeHint(ByGenreListDataSource.PAGE_SIZE)
                    .build();

    public LiveData<PagedList<Media>> getByGenresitemPagedList() {
        return Transformations.switchMap(searchQuery, query -> {
            NetworksListDataSourceFactory factory = mediaRepository.networksListDataSourceFactory(query);
            return new LivePagedListBuilder<>(factory, config).build();
        });
    }



}
