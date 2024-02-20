package com.siflusso.ui.moviedetails.tabfragment;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.siflusso.R;
import com.siflusso.data.local.entity.Media;
import com.siflusso.data.repository.AuthRepository;
import com.siflusso.data.repository.MediaRepository;
import com.siflusso.databinding.FragmentCastBinding;
import com.siflusso.databinding.FragmentOverviewBinding;
import com.siflusso.ui.manager.SettingsManager;
import com.siflusso.ui.moviedetails.adapters.CastTabAdapter;
import com.siflusso.ui.viewmodels.MovieDetailViewModel;

import javax.inject.Inject;

public class CastFragment extends Fragment {
    FragmentCastBinding binding;
    private SettingsManager settingsManager;
    private RecyclerView recyclerView;
    @Inject
    ViewModelProvider.Factory viewModelFactory;
    @Inject
    MediaRepository mediaRepository;
    @Inject
    AuthRepository authRepository;
    private MovieDetailViewModel movieDetailViewModel;
    private CastTabAdapter mCastAdapter;



    private Media media;
    public static CastFragment newInstance(Media media) {
        CastFragment fragment = new CastFragment();
        Bundle args = new Bundle();
        args.putParcelable("myCastObject",  media); // assuming MyObject implements Parcelable
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCastBinding.inflate(inflater,container,false);
        if (getArguments() != null) {
            media = getArguments().getParcelable("myCastObject");
            onLoadCast(media);
        }
        return  binding.getRoot();
    }

    private void onLoadCast(Media movieDetail) {
        Toast.makeText(getContext(),"Cast Object "+ media.getTitle(),Toast.LENGTH_SHORT).show();
        mCastAdapter = new CastTabAdapter(getContext(), movieDetail.getCast());
        binding.RvCast.setHasFixedSize(true);
        binding.RvCast.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        binding.RvCast.setAdapter(mCastAdapter);
    }
}