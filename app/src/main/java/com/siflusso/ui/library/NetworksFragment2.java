package com.siflusso.ui.library;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import com.siflusso.R;
import com.siflusso.data.repository.MediaRepository;
import com.siflusso.databinding.LayoutNetworksBinding;
import com.siflusso.di.Injectable;
import com.siflusso.ui.home.adapters.ByGenreAdapter;
import com.siflusso.ui.viewmodels.NetworksViewModel;
import com.siflusso.util.SpacingItemDecoration;
import com.siflusso.util.Tools;

import javax.inject.Inject;


public class NetworksFragment2 extends Fragment implements Injectable {

    LayoutNetworksBinding binding;

    @Inject
    ViewModelProvider.Factory viewModelFactory;
    private NetworksViewModel networksViewModel;
    NetworkssAdapter adapter;

    @Inject
    ByGenreAdapter byGenreAdapter;


    @Inject
    MediaRepository mediaRepository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.layout_networks, container, false);

        networksViewModel = new ViewModelProvider(this, viewModelFactory).get(NetworksViewModel.class);

        adapter = new NetworkssAdapter();

        onLoadGenres();

        return binding.getRoot();


    }



    // Load Genres
    private void onLoadGenres() {

        networksViewModel.getNetworksLib();
        networksViewModel.networkLibMutableLiveData.observe(getViewLifecycleOwner(), movieDetail -> {

            if (!movieDetail.getNetworks().isEmpty()) {

                adapter.addMain(movieDetail.getNetworks(),requireActivity(),mediaRepository,byGenreAdapter);

                binding.recyclerView.setLayoutManager(new GridLayoutManager(requireActivity(), 4));
                binding.recyclerView.addItemDecoration(new SpacingItemDecoration(1, Tools.dpToPx(requireActivity(), 0), true));
                binding.recyclerView.setHasFixedSize(true);
                binding.recyclerView.setAdapter(adapter);



            }else {


                binding.noMoviesFound.setVisibility(View.VISIBLE);

            }


        });



    }


    // On Fragment Detach clear binding views & adapters to avoid memory leak
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding.recyclerView.setAdapter(null);
        binding.constraintLayout.removeAllViews();
        binding = null;
    }

}
