/**
 * EasyPlex - Movies - Live Streaming - TV Series, Anime
 *
 * @author @Y0bEX
 * @package  EasyPlex - Movies - Live Streaming - TV Series, Anime
 * @copyright Copyright (c) 2021 Y0bEX,
 * @license http://codecanyon.net/wiki/support/legal-terms/licensing-terms/
 * @profile https://codecanyon.net/user/yobex
 * @link yobexd@gmail.com
 * @skype yobexd@gmail.com
 **/

package com.siflusso.ui.downloadmanager.ui.details;

import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.Formatter;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import com.siflusso.R;
import com.siflusso.databinding.DialogDownloadDetailsBinding;
import com.siflusso.ui.downloadmanager.core.exception.FileAlreadyExistsException;
import com.siflusso.ui.downloadmanager.core.exception.FreeSpaceException;
import com.siflusso.ui.downloadmanager.core.model.data.entity.DownloadInfo;
import com.siflusso.ui.downloadmanager.core.system.FileSystemContracts;
import com.siflusso.ui.downloadmanager.core.utils.Utils;
import com.siflusso.ui.downloadmanager.ui.BaseAlertDialog;
import com.siflusso.ui.downloadmanager.ui.ClipboardDialog;
import com.siflusso.ui.downloadmanager.ui.adddownload.AddDownloadActivity;
import com.siflusso.ui.downloadmanager.ui.adddownload.AddInitParams;

import java.util.UUID;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class DownloadDetailsDialog extends DialogFragment
{
    @SuppressWarnings("unused")
    private static final String TAG = DownloadDetailsDialog.class.getSimpleName();

    private static final String TAG_OPEN_DIR_ERROR_DIALOG = "open_dir_error_dialog";
    private static final String TAG_REPLACE_FILE_DIALOG = "replace_file_dialog";
    private static final String TAG_ID = "id";
    private static final String TAG_URL_CLIPBOARD_DIALOG = "url_clipboard_dialog";
    private static final String TAG_CHECKSUM_CLIPBOARD_DIALOG = "checksum_clipboard_dialog";
    private static final String TAG_CUR_CLIPBOARD_TAG = "cur_clipboard_tag";

    private AlertDialog alert;
    private AppCompatActivity activity;
    private DialogDownloadDetailsBinding binding;
    private DownloadDetailsViewModel viewModel;
    private BaseAlertDialog.SharedViewModel dialogViewModel;
    private final CompositeDisposable disposables = new CompositeDisposable();
    private ClipboardDialog clipboardDialog;
    private ClipboardDialog.SharedViewModel clipboardViewModel;
    private String curClipboardTag;

    public static DownloadDetailsDialog newInstance(UUID downloadId)
    {
        DownloadDetailsDialog frag = new DownloadDetailsDialog();

        Bundle args = new Bundle();
        args.putSerializable(TAG_ID, downloadId);
        frag.setArguments(args);

        return frag;
    }

    @Override
    public void onAttach(@NonNull Context context)
    {
        super.onAttach(context);

        if (context instanceof AppCompatActivity)
            activity = (AppCompatActivity)context;
    }

    @Override
    public void onResume()
    {
        super.onResume();

        /* Back button handle */
        getDialog().setOnKeyListener((DialogInterface dialog, int keyCode, KeyEvent event) -> {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                if (event.getAction() != KeyEvent.ACTION_DOWN) {
                    return true;
                } else {
                    onBackPressed();
                    return true;
                }
            } else {
                return false;
            }
        });
    }

    @Override
    public void onStop()
    {
        super.onStop();

        unsubscribeClipboardManager();
        disposables.clear();
    }

    @Override
    public void onStart()
    {
        super.onStart();

        observeDownload();
        subscribeAlertDialog();
        subscribeClipboardManager();
    }

    private void observeDownload()
    {
        UUID id = (UUID)getArguments().getSerializable(TAG_ID);

        disposables.add(viewModel.observeInfoAndPieces(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((infoAndPieces) -> {
                            if (infoAndPieces == null) {
                                finish();
                                return;
                            }
                            viewModel.updateInfo(infoAndPieces);
                        },
                        (Throwable t) -> {
                            Timber.e("Getting info " + id + " error: " +
                                    Log.getStackTraceString(t));
                        }));
    }

    private void subscribeAlertDialog()
    {
        Disposable d = dialogViewModel.observeEvents().subscribe(this::handleAlertDialogEvent);
        disposables.add(d);
        d = clipboardViewModel.observeSelectedItem().subscribe((item) -> {
            switch (item.dialogTag) {
                case TAG_URL_CLIPBOARD_DIALOG:
                    handleUrlClipItem(item.str);
                    break;
                case TAG_CHECKSUM_CLIPBOARD_DIALOG:
                    handleChecksumClipItem(item.str);
                    break;
            }
        });
        disposables.add(d);
    }
    private void subscribeClipboardManager() {
        ClipboardManager clipboard = (ClipboardManager)activity.getSystemService(Activity.CLIPBOARD_SERVICE);
        clipboard.addPrimaryClipChangedListener(clipListener);
    }

    private void unsubscribeClipboardManager() {
        ClipboardManager clipboard = (ClipboardManager)activity.getSystemService(Activity.CLIPBOARD_SERVICE);
        clipboard.removePrimaryClipChangedListener(clipListener);
    }

    private final ClipboardManager.OnPrimaryClipChangedListener clipListener = this::switchClipboardButton;

    private void switchClipboardButton()
    {
        ClipData clip = Utils.getClipData(activity.getApplicationContext());
        viewModel.showClipboardButton.set(clip != null);
    }

    private final ViewTreeObserver.OnWindowFocusChangeListener onFocusChanged =
            (__) -> switchClipboardButton();

    private void handleAlertDialogEvent(BaseAlertDialog.Event event)
    {
        if (event.dialogTag == null || !event.dialogTag.equals(TAG_REPLACE_FILE_DIALOG))
            return;
        if (event.type == BaseAlertDialog.EventType.POSITIVE_BUTTON_CLICKED)
            applyChangedParams(true);
    }

    private void handleUrlClipItem(String item)
    {
        if (TextUtils.isEmpty(item))
            return;

        viewModel.mutableParams.setUrl(item);
    }

    private void handleChecksumClipItem(String item)
    {
        if (TextUtils.isEmpty(item))
            return;

        viewModel.mutableParams.setChecksum(item);
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        viewModel = new ViewModelProvider(this).get(DownloadDetailsViewModel.class);
        ViewModelProvider provider = new ViewModelProvider(activity);
        dialogViewModel = provider.get(BaseAlertDialog.SharedViewModel.class);
        clipboardViewModel = provider.get(ClipboardDialog.SharedViewModel.class);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        if (activity == null)
            activity = (AppCompatActivity)getActivity();

        if (savedInstanceState != null)
            curClipboardTag = savedInstanceState.getString(TAG_CUR_CLIPBOARD_TAG);

        FragmentManager fm = getChildFragmentManager();
        clipboardDialog = (ClipboardDialog)fm.findFragmentByTag(curClipboardTag);

        LayoutInflater i = LayoutInflater.from(activity);
        binding = DataBindingUtil.inflate(i, R.layout.dialog_download_details, null, false);
        binding.setViewModel(viewModel);

        initLayoutView();

        binding.getRoot().getViewTreeObserver().addOnWindowFocusChangeListener(onFocusChanged);

        return alert;
    }

    @Override
    public void onDestroyView()
    {
        binding.getRoot().getViewTreeObserver().removeOnWindowFocusChangeListener(onFocusChanged);

        super.onDestroyView();
    }

    private void initLayoutView()
    {
        binding.link.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s)
            {
                binding.layoutLink.setErrorEnabled(false);
                binding.layoutLink.setError(null);
            }
        });
        binding.name.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s)
            {
                binding.layoutName.setErrorEnabled(false);
                binding.layoutName.setError(null);
            }
        });
        binding.checksum.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s)
            {
                checkChecksumField(s);
            }
        });

        binding.folderChooserButton.setOnClickListener((v) ->
                chooseDir.launch(viewModel.mutableParams.getDirPath())
        );
        binding.urlClipboardButton.setOnClickListener((v) ->
                showClipboardDialog(TAG_URL_CLIPBOARD_DIALOG));
        binding.checksumClipboardButton.setOnClickListener((v) ->
                showClipboardDialog(TAG_CHECKSUM_CLIPBOARD_DIALOG));

        switchClipboardButton();

        initAlertDialog(binding.getRoot());
    }

    private void initAlertDialog(View view)
    {
        alert = new AlertDialog.Builder(activity)
                .setTitle(R.string.download_details)
                .setPositiveButton(R.string.close, null)
                .setNegativeButton(R.string.apply, null)
                .setNeutralButton(R.string.redownload, null)
                .setView(view)
                .create();

        alert.setOnShowListener((DialogInterface dialog) -> {
            Button closeButton = alert.getButton(AlertDialog.BUTTON_POSITIVE);
            Button applyButton = alert.getButton(AlertDialog.BUTTON_NEGATIVE);
            Button redownloadButton = alert.getButton(AlertDialog.BUTTON_NEUTRAL);

            closeButton.setOnClickListener((v) -> finish());
            applyButton.setOnClickListener((v) -> applyChangedParams(false));
            redownloadButton.setOnClickListener((v) -> showAddDownloadDialog());
        });
    }

    private void showAddDownloadDialog()
    {
        DownloadInfo downloadInfo = viewModel.info.getDownloadInfo();
        if (downloadInfo == null)
            return;

        AddInitParams initParams = new AddInitParams();
        initParams.url = downloadInfo.url;
        initParams.dirPath = downloadInfo.dirPath;
        initParams.fileName = downloadInfo.fileName;
        initParams.description = downloadInfo.description;
        initParams.userAgent = downloadInfo.userAgent;
        initParams.refer = downloadInfo.userAgent;
        initParams.unmeteredConnectionsOnly = downloadInfo.unmeteredConnectionsOnly;
        initParams.retry = downloadInfo.retry;
        initParams.replaceFile = true;

        Intent i = new Intent(activity, AddDownloadActivity.class);
        i.putExtra(AddDownloadActivity.TAG_INIT_PARAMS, initParams);
        startActivity(i);

        finish();
    }

    private void applyChangedParams(boolean checkFileExists)
    {
        if (!checkUrlField(binding.link.getText()))
            return;
        if (!checkNameField(binding.name.getText()))
            return;

        try {
            if (!viewModel.applyChangedParams(checkFileExists))
                return;

        } catch (FreeSpaceException e) {
            showFreeSpaceErrorToast();
            return;
        } catch (FileAlreadyExistsException e) {
            showReplaceFileDialog();
            return;
        }

        finish();
    }

    private boolean checkUrlField(Editable s)
    {
        if (s == null)
            return false;

        if (TextUtils.isEmpty(s)) {
            binding.layoutLink.setErrorEnabled(true);
            binding.layoutLink.setError(getString(R.string.download_error_empty_link));
            binding.layoutLink.requestFocus();

            return false;
        }

        binding.layoutLink.setErrorEnabled(false);
        binding.layoutLink.setError(null);

        return true;
    }

    private boolean checkNameField(Editable s)
    {
        if (s == null)
            return false;

        if (TextUtils.isEmpty(s)) {
            binding.layoutName.setErrorEnabled(true);
            binding.layoutName.setError(getString(R.string.download_error_empty_name));
            binding.layoutName.requestFocus();

            return false;
        }
        if (!viewModel.fs.isValidFatFilename(s.toString())) {
            binding.layoutName.setErrorEnabled(true);
            binding.layoutName.setError(getString(R.string.download_error_name_is_not_correct,
                    viewModel.fs.buildValidFatFilename(s.toString())));
            binding.layoutName.requestFocus();

            return false;
        }

        binding.layoutName.setErrorEnabled(false);
        binding.layoutName.setError(null);

        return true;
    }
    private void checkChecksumField(Editable s)
    {
        if (!TextUtils.isEmpty(s) && !viewModel.isChecksumValid(s.toString())) {
            binding.layoutChecksum.setErrorEnabled(true);
            binding.layoutChecksum.setError(getString(R.string.error_invalid_checksum));
            binding.layoutChecksum.requestFocus();

            return;
        }

        binding.layoutChecksum.setErrorEnabled(false);
        binding.layoutChecksum.setError(null);
    }


    private void showFreeSpaceErrorToast()
    {
        DownloadInfo downloadInfo = viewModel.info.getDownloadInfo();
        if (downloadInfo == null)
            return;

        String totalSizeStr = Formatter.formatFileSize(activity, downloadInfo.totalBytes);
        String availSizeStr = Formatter.formatFileSize(activity, viewModel.info.getStorageFreeSpace());

        Toast.makeText(activity.getApplicationContext(),
                activity.getString(R.string.download_error_no_enough_free_space, availSizeStr, totalSizeStr),
                Toast.LENGTH_LONG)
                .show();
    }

    private void showClipboardDialog(String tag)
    {
        if (!isAdded())
            return;

        FragmentManager fm = getChildFragmentManager();
        if (fm.findFragmentByTag(tag) == null) {
            curClipboardTag = tag;
            clipboardDialog = ClipboardDialog.newInstance();
            clipboardDialog.show(fm, tag);
        }
    }

    final ActivityResultLauncher<Uri> chooseDir = registerForActivityResult(
            new FileSystemContracts.OpenDirectory(),
            uri -> {
                if (uri == null) {
                    return;
                }
                try {
                    viewModel.fs.takePermissions(uri);
                    viewModel.mutableParams.setDirPath(uri);
                } catch (Exception e) {
                    Timber.e("Unable to open directory: %s", Log.getStackTraceString(e));
                    showOpenDirErrorDialog();
                }
            }
    );

    private void showOpenDirErrorDialog()
    {
        if (!isAdded())
            return;

        FragmentManager fm = getChildFragmentManager();
        if (fm.findFragmentByTag(TAG_OPEN_DIR_ERROR_DIALOG) == null) {
            BaseAlertDialog openDirErrorDialog = BaseAlertDialog.newInstance(
                    getString(R.string.error),
                    getString(R.string.unable_to_open_folder),
                    0,
                    getString(R.string.ok),
                    null,
                    null,
                    true);

            openDirErrorDialog.show(fm, TAG_OPEN_DIR_ERROR_DIALOG);
        }
    }

    private void showReplaceFileDialog()
    {
        if (!isAdded())
            return;

        FragmentManager fm = getChildFragmentManager();
        if (fm.findFragmentByTag(TAG_REPLACE_FILE_DIALOG) == null) {
            BaseAlertDialog replaceFileDialog = BaseAlertDialog.newInstance(
                    getString(R.string.replace_file),
                    getString(R.string.error_file_exists),
                    0,
                    getString(R.string.yes),
                    getString(R.string.no),
                    null,
                    true);

            replaceFileDialog.show(fm, TAG_REPLACE_FILE_DIALOG);
        }
    }

    private void onBackPressed()
    {
        finish();
    }

    private void finish()
    {
        alert.dismiss();
    }
}
