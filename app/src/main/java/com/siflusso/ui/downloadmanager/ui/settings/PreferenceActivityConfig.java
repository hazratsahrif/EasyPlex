/* * EasyPlex - Movies - Live Streaming - TV Series, Anime * * @author @Y0bEX * @package EasyPlex - Movies - Live Streaming - TV Series, Anime * @copyright Copyright (c) 2021 Y0bEX, * @license http://codecanyon.net/wiki/support/legal-terms/licensing-terms/ * @profile https://codecanyon.net/user/yobex * @link yobexd@gmail.com * @skype yobexd@gmail.com **/

package com.siflusso.ui.downloadmanager.ui.settings;

import android.os.Parcel;
import android.os.Parcelable;

/*
 * Specifies the toolbar title and fragment (by class name). Part of PreferenceActivity.
 */

public class PreferenceActivityConfig implements Parcelable
{
    private String fragment;
    private String title;

    public PreferenceActivityConfig(String fragment, String title)
    {
        this.fragment = fragment;
        this.title = title;
    }

    public PreferenceActivityConfig(Parcel source)
    {
        fragment = source.readString();
        title = source.readString();
    }

    public void setFragment(String fragment)
    {
        this.fragment = fragment;
    }

    public String getFragment()
    {
        return fragment;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getTitle()
    {
        return title;
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(fragment);
        dest.writeString(title);
    }

    public static final Creator<PreferenceActivityConfig> CREATOR =
            new Creator<PreferenceActivityConfig>()
            {
                @Override
                public PreferenceActivityConfig createFromParcel(Parcel source)
                {
                    return new PreferenceActivityConfig(source);
                }

                @Override
                public PreferenceActivityConfig[] newArray(int size)
                {
                    return new PreferenceActivityConfig[size];
                }
            };
}
