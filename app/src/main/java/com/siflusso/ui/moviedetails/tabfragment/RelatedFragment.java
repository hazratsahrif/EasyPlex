package com.siflusso.ui.moviedetails.tabfragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.siflusso.R;
import com.siflusso.data.local.entity.Media;
import com.siflusso.databinding.FragmentRelatedBinding;

public class RelatedFragment extends Fragment {
      FragmentRelatedBinding binding;
    private Media media;
    public static RelatedFragment newInstance(Media media) {
        RelatedFragment fragment = new RelatedFragment();
        Bundle args = new Bundle();
        args.putParcelable("relatedObject",  media); // assuming MyObject implements Parcelable
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentRelatedBinding.inflate(inflater,container,false);
        if (getArguments() != null) {
            media = getArguments().getParcelable("relatedObject");
            Toast.makeText(getContext(),"Cast objec"+ media.getTitle(),Toast.LENGTH_SHORT).show();
            onLoadRelated(media);
        }

        return  binding.getRoot();
    }

    private void onLoadRelated(Media media) {

    }
}