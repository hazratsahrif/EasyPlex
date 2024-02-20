package com.siflusso.ui.manager;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import com.siflusso.data.model.ads.Ads;
import static com.siflusso.util.Constants.ADS_CLICKTHROUGHURL;
import static com.siflusso.util.Constants.ADS_LINK;


/**
 * EasyPlex - Android Movie Portal App
 * @package     EasyPlex - Android Movie Portal App
 * @author      @Y0bEX
 * @copyright   Copyright (c) 2022 Y0bEX,
 * @license     http://codecanyon.net/wiki/support/legal-terms/licensing-terms/
 * @profile     https://codecanyon.net/user/yobex
 * @link        yobexd@gmail.com
 * @skype       yobexd@gmail.com
 **/




public class AdsManager {



    private final SharedPreferences prefs;
    private final SharedPreferences.Editor editor;

    @SuppressLint("CommitPrefEdits")
    public AdsManager(SharedPreferences prefs){
        this.prefs = prefs;
        this.editor = prefs.edit();
    }

    public void saveSettings(Ads ads){
        editor.putString(ADS_LINK, ads.getLink()).commit();
        editor.putString(ADS_CLICKTHROUGHURL, ads.getClickThroughUrl()).commit();
        editor.apply();
    }

    public void deleteAds(){
        editor.remove(ADS_LINK).commit();
        editor.remove(ADS_CLICKTHROUGHURL).commit();
    }

    public Ads getAds(){
        Ads ads = new Ads();
        ads.setLink(prefs.getString(ADS_LINK,
                "https://pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=/124319096/external/single_ad_samples&ciu_szs=300x250&impl=s&gdfp_req=1&env=vp&output=vast&unviewed_position_start=1&cust_params=deployment%3Ddevsite%26sample_ct%3Dlinear&correlator="));
        ads.setClickThroughUrl(prefs.getString(ADS_CLICKTHROUGHURL, null));
        return ads;
    }




}
