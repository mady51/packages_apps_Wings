/*
 *  Copyright (C) 2017 Cardinal-AOSP
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
*/
package com.cardinal.settings.fragments;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.UserHandle;
import android.os.PowerManager;
import android.os.IPowerManager;
import android.os.ServiceManager;
import android.support.v7.preference.CheckBoxPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceScreen;
import android.support.v14.preference.SwitchPreference;

import android.provider.SearchIndexableResource;
import android.provider.Settings;

import android.view.View;
import android.util.Log;
import android.app.AlertDialog;
import android.text.TextWatcher;
import android.text.Editable;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.search.Indexable;

import com.android.internal.logging.nano.MetricsProto.MetricsEvent;
import com.android.internal.util.custom.CustomUtils;

import com.cardinal.settings.preference.CustomSeekBarPreference;
import com.cardinal.settings.preference.SystemSettingSwitchPreference;

import java.util.ArrayList;
import java.util.List;

public class NavigationBarSettings extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener, Indexable {
    private static final String TAG = "NavigationBar";

    private static final String KEYS_SHOW_NAVBAR_KEY = "navigation_bar_show";

    private static final String KEY_BUTTON_ANBI = "button_anbi";

    private Handler mHandler;

    private SwitchPreference mAnbiPreference;
    private SwitchPreference mEnableNavBar;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.cardinal_settings_navigationbar);

        mHandler = new Handler();

        final Resources res = getActivity().getResources();
        PreferenceScreen prefSet = getPreferenceScreen();
        ContentResolver resolver = getActivity().getContentResolver();

        mEnableNavBar = (SwitchPreference) prefSet.findPreference(
                KEYS_SHOW_NAVBAR_KEY);

        boolean showNavBarDefault = CustomUtils.deviceSupportNavigationBar(getActivity());
        boolean showNavBar = Settings.System.getInt(resolver,
                    Settings.System.NAVIGATION_BAR_SHOW, showNavBarDefault ? 1:0) == 1;
        mEnableNavBar.setChecked(showNavBar);

        mAnbiPreference = (SwitchPreference) findPreference(KEY_BUTTON_ANBI);
	    
        if (mAnbiPreference != null) {
            mAnbiPreference.setChecked((Settings.System.getInt(resolver,
            		Settings.System.ANBI_ENABLED, 0) == 1));
            mAnbiPreference.setOnPreferenceChangeListener(this);
        }
		
    @Override
    public boolean onPreferenceTreeClick(Preference preference) {
        if (preference == mEnableNavBar) {
            boolean checked = ((SwitchPreference)preference).isChecked();
            Settings.System.putInt(getContentResolver(),
                    Settings.System.NAVIGATION_BAR_SHOW, checked ? 1:0);
            return true;
        }
        // If we didn't handle it, let preferences handle it.
        return super.onPreferenceTreeClick(preference);
    }

    @Override
    public int getMetricsCategory() {
        return MetricsEvent.WINGS;
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        ContentResolver resolver = getActivity().getContentResolver();
        if (preference == mAnbiPreference) {
           boolean value = (Boolean) newValue;
            Settings.System.putInt(getActivity().getContentResolver(),
                      Settings.System.ANBI_ENABLED, value ? 1 : 0);
            return true;
        }
        return false;
    }

    public static final Indexable.SearchIndexProvider SEARCH_INDEX_DATA_PROVIDER =
            new BaseSearchIndexProvider() {
                @Override
                public List<SearchIndexableResource> getXmlResourcesToIndex(Context context,
                        boolean enabled) {
                    ArrayList<SearchIndexableResource> result =
                            new ArrayList<SearchIndexableResource>();

                    SearchIndexableResource sir = new SearchIndexableResource(context);
                    sir.xmlResId = R.xml.cardinal_settings_navigationbar;
                    result.add(sir);
                    return result;
                }

                @Override
                public List<String> getNonIndexableKeys(Context context) {
                    ArrayList<String> result = new ArrayList<String>();
                    return result;
                }
            };
}
