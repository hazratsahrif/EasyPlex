/* * EasyPlex - Movies - Live Streaming - TV Series, Anime * * @author @Y0bEX * @package EasyPlex - Movies - Live Streaming - TV Series, Anime * @copyright Copyright (c) 2021 Y0bEX, * @license http://codecanyon.net/wiki/support/legal-terms/licensing-terms/ * @profile https://codecanyon.net/user/yobex * @link yobexd@gmail.com * @skype yobexd@gmail.com **/

package com.siflusso.ui.downloadmanager.core.system;

import android.annotation.TargetApi;
import android.os.Build;
import android.system.Os;
import android.system.OsConstants;
import android.system.StructStatVfs;

import androidx.annotation.NonNull;

import java.io.FileDescriptor;
import java.io.IOException;

class SysCallImpl implements SysCall
{
    @Override
    public void lseek(@NonNull FileDescriptor fd, long offset) throws IOException, UnsupportedOperationException
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            try {
                Os.lseek(fd, offset, OsConstants.SEEK_SET);

            } catch (Exception e) {
                throw new IOException(e);
            }

        } else {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    @TargetApi(21)
    public void fallocate(@NonNull FileDescriptor fd, long length) throws IOException
    {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
            return;

        try {
            long curSize = Os.fstat(fd).st_size;
            long newBytes = length - curSize;
            long availBytes = availableBytes(fd);
            if (availBytes < newBytes)
                throw new IOException("Not enough free space; " + newBytes + " requested, " +
                        availBytes + " available");

            Os.posix_fallocate(fd, 0, length);

        } catch (Exception e) {
            try {
                Os.ftruncate(fd, length);

            } catch (Exception ex) {
                throw new IOException(ex);
            }
        }
    }

    /*
     * Return the number of bytes that are free on the file system
     * backing the given FileDescriptor
     *
     * TODO: maybe there is analog for KitKat?
     */

    @Override
    @TargetApi(21)
    public long availableBytes(@NonNull FileDescriptor fd) throws IOException
    {
        try {
            StructStatVfs stat = Os.fstatvfs(fd);

            return stat.f_bavail * stat.f_bsize;
        } catch (Exception e) {
            throw new IOException(e);
        }
    }
}
