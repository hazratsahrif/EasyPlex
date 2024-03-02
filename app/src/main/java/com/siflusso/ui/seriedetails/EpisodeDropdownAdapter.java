package com.siflusso.ui.seriedetails;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.siflusso.data.model.serie.Season;
import com.siflusso.databinding.SeasonsDropdownBinding;

import java.util.List;


public class EpisodeDropdownAdapter extends RecyclerView.Adapter<EpisodeDropdownAdapter.EpisodeViewHolder> {
    final List<Season> season;
    AdapterView.OnItemSelectedListener onItemSelectedListener;

    public EpisodeDropdownAdapter(List<Season> season) {
        this.season = season;
    }

    public void setOnItemSelectedListener(AdapterView.OnItemSelectedListener onItemSelectedListener) {
        this.onItemSelectedListener = onItemSelectedListener;
    }


    @NonNull
    @Override
    public EpisodeDropdownAdapter.EpisodeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        SeasonsDropdownBinding binding = SeasonsDropdownBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        binding.planetsSpinner.setOnItemSelectedListener(onItemSelectedListener);
        return new EpisodeViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull EpisodeDropdownAdapter.EpisodeViewHolder holder, int position) {
        SeasonsDropdownBinding binding = holder.binding;
        binding.planetsSpinner.setItem(season);
    }

    @Override
    public int getItemCount() {
        return 1;
    }

    public static class EpisodeViewHolder extends RecyclerView.ViewHolder {
        final SeasonsDropdownBinding binding;

        public EpisodeViewHolder(@NonNull SeasonsDropdownBinding itemView) {
            super(itemView.getRoot());
            this.binding = itemView;
        }
    }
}
