package com.siflusso.ui.comments;

import static com.siflusso.util.Constants.ARG_COMMENT;
import static com.siflusso.util.Constants.SERVER_BASE_URL;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.siflusso.R;
import com.siflusso.data.model.auth.UserAuthInfo;
import com.siflusso.data.model.comments.Comment;
import com.siflusso.data.model.media.StatusFav;
import com.siflusso.data.repository.MediaRepository;
import com.siflusso.databinding.ItemCommentBinding;
import com.siflusso.ui.manager.AuthManager;
import com.siflusso.util.GlideApp;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * Adapter for Movies - Series - Animes - Streaming Comments.
 *
 * @author Yobex.
 */
public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.MainViewHolder> {

    private List<Comment> castList;
    private Context context;
    private AuthManager authManager;
    private MediaRepository authRepository;

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }


    @SuppressLint("NotifyDataSetChanged")
    public void addToContent(List<Comment> castList , Context context,AuthManager authManager,MediaRepository authRepository) {
        this.castList = castList;
        this.context = context;
        this.authManager = authManager;
        this.authRepository = authRepository;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CommentsAdapter.MainViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        ItemCommentBinding binding = ItemCommentBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);

        return new CommentsAdapter.MainViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentsAdapter.MainViewHolder holder, int position) {
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



        private final ItemCommentBinding binding;

        MainViewHolder(@NonNull ItemCommentBinding binding)
        {
            super(binding.getRoot());

            this.binding = binding;
        }


        void onBind(final int position) {

            final Comment comment = castList.get(position);

            UserAuthInfo userAuthInfo = new UserAuthInfo();

            userAuthInfo.setId(comment.getUserId());



            binding.textViewNameItemComment.setText(comment.getUserName());
            binding.textViewContentItemComment.setText(comment.getComment());

            GlideApp.with(context).asDrawable().load(comment.getUser_image())
                    .fitCenter()
                    .placeholder(R.drawable.mainlogo)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(binding.userImg);


            binding.textViewTimeItemComment.setText(comment.getTime());


            binding.moreCommentOptions.setOnClickListener((v) -> {
                PopupMenu popup = new PopupMenu(v.getContext(), v);
                popup.inflate(R.menu.comment_item_popup);

                popup.getMenu().findItem(R.id.delete_menu).setVisible(authManager.getUserInfo().getId().equals(comment.getUserId()));
                popup.setOnMenuItemClickListener(item -> onDeleteComment(item,comment));
                popup.show();
            });



        }
    }

    private boolean onDeleteComment(MenuItem item, Comment comment) {

        if (item.getItemId() == R.id.delete_menu) {

            authRepository.deleteComment(String.valueOf(comment.getId()))
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<>() {
                        @Override
                        public void onSubscribe(@NotNull Disposable d) {

                            //

                        }

                        @SuppressLint("NotifyDataSetChanged")
                        @Override
                        public void onNext(@NotNull StatusFav statusFav) {

                            Toast.makeText(context, R.string.comment_deleted, Toast.LENGTH_SHORT).show();

                            if (onItemClickListener != null) {
                                onItemClickListener.onItemClick(true);
                            }

                        }

                        @Override
                        public void onError(@NotNull Throwable e) {

                            Toast.makeText(context, R.string.comment_not_deleted, Toast.LENGTH_SHORT).show();

                        }

                        @Override
                        public void onComplete() {

                            //

                        }
                    });
        }

        return true;
    }


    public interface OnItemClickListener {
        void onItemClick(boolean deleted);
    }
}
