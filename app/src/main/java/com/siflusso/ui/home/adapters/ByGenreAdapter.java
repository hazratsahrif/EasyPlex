package com.siflusso.ui.home.adapters;

import static com.siflusso.util.Constants.ARG_MOVIE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.siflusso.R;
import com.siflusso.data.local.entity.Media;
import com.siflusso.ui.animes.AnimeDetailsActivity;
import com.siflusso.ui.manager.SettingsManager;
import com.siflusso.ui.moviedetails.MovieDetailsActivity;
import com.siflusso.ui.seriedetails.SerieDetailsActivity;
import com.siflusso.util.ItemAnimation;
import com.siflusso.util.Tools;

import org.jetbrains.annotations.NotNull;

public class ByGenreAdapter extends PagedListAdapter<Media, ByGenreAdapter.ItemViewHolder> {

    private final Context context;
    private final int animationType;

    private  final SettingsManager settingsManager;

    public ByGenreAdapter(Context context, int animationType,SettingsManager settingsManager) {
        super(DIFF_CALLBACK);
        this.context = context;
        this.animationType = animationType;
        this.settingsManager = settingsManager;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_genre, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {

        Media media = getItem(position);


        if (media != null) {


            Tools.onLoadMediaCoverAdapters(context,holder.imageView,media.getPosterPath());


            setAnimation(holder.itemView, position);


            if (media.getName() !=null){

                holder.title.setText(media.getName());

            }else  if (media.getTitle() !=null){


                holder.title.setText(media.getTitle());

            }


            if (settingsManager.getSettings().getLibraryStyle() == 1){


                holder.type.setText(media.getGenreName());

                holder.title.setText(media.getName());


                if ("anime".equals(media.getType())) {
                    holder.type.setText("Anime");
                } else if ("serie".equals(media.getType())) {
                    holder.type.setText("Serie");
                } else if ("movie".equals(media.getType())) {
                    holder.type.setText("Movie");
                }


            }else {


                if ("anime".equals(media.getType())) {
                    holder.type.setText("Anime");
                    holder.title.setText(media.getName());
                } else if ("serie".equals(media.getType())) {
                    holder.type.setText("Serie");
                    holder.title.setText(media.getName());
                } else if ("movie".equals(media.getType())) {
                    holder.type.setText("Movie");
                    holder.title.setText(media.getTitle());
                }


            }





            if (media.getSubtitle() !=null) {

                holder.substitle.setText(media.getSubtitle());

            }else {

                holder.substitle.setVisibility(View.GONE);
            }

            holder.movietype.setOnClickListener(v -> {

                if ("anime".equals(media.getType())) {
                    Intent intent = new Intent(context, AnimeDetailsActivity.class);
                    intent.putExtra(ARG_MOVIE, media);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                } else if ("serie".equals(media.getType())) {
                    Intent intent = new Intent(context, SerieDetailsActivity.class);
                    intent.putExtra(ARG_MOVIE, media);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                } else if ("movie".equals(media.getType())) {
                    Intent intent = new Intent(context, MovieDetailsActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra(ARG_MOVIE, media);
                    context.startActivity(intent);
                }

            });


        }

        }


    private static final DiffUtil.ItemCallback<Media> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<>() {
                @Override
                public boolean areItemsTheSame(Media oldItem, Media newItem) {
                    return oldItem.getId().equals(newItem.getId());
                }

                @SuppressLint("DiffUtilEquals")
                @Override
                public boolean areContentsTheSame(Media oldItem, @NotNull Media newItem) {
                    return oldItem.equals(newItem);
                }
            };


    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NotNull RecyclerView recyclerView, int newState) {
                onAttach = false;
                super.onScrollStateChanged(recyclerView, newState);
            }
        });
        super.onAttachedToRecyclerView(recyclerView);
    }



    private int lastPosition = -1;
    private boolean onAttach = true;


    private void setAnimation(View view, int position) {
        if (position > lastPosition) {
            ItemAnimation.animate(view, onAttach ? position : -1, animationType);
            lastPosition = position;
        }
    }


    static class ItemViewHolder extends RecyclerView.ViewHolder {

        final ImageView imageView;
        final LinearLayout movietype;
        final TextView mgenres;

        final TextView substitle;

        final TextView type;

        final TextView title;

        public ItemViewHolder(View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.item_movie_image);
            movietype = itemView.findViewById(R.id.rootLayout);
            mgenres = itemView.findViewById(R.id.mgenres);
            substitle = itemView.findViewById(R.id.substitle);
            type = itemView.findViewById(R.id.type);

            title = itemView.findViewById(R.id.movietitle);
        }
    }
}
