/*
 * Copyright (C) 2007-2008 Esmertec AG. Copyright (C) 2007-2008 The Android Open
 * Source Project
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.ironrabbit.tbchat.app.im.app;

import org.ironrabbit.tbchat.R;
import org.ironrabbit.tbchat.app.im.provider.Imps;
import org.ironrabbit.tbchat.app.im.provider.Imps.ProviderSettings;
import org.ironrabbit.tbchat.app.lang.BhoListPreference;
import org.ironrabbit.tbchat.app.lang.BhoTyper;

import android.content.ContentResolver;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.util.Log;

public class SettingActivity extends android.preference.PreferenceActivity implements
        OnSharedPreferenceChangeListener {
    BhoListPreference mOtrMode;
    CheckBoxPreference mHideOfflineContacts;
    CheckBoxPreference mEnableNotification;
    CheckBoxPreference mNotificationVibrate;
    CheckBoxPreference mNotificationSound;
    CheckBoxPreference mForegroundService;

    private void setInitialValues() {
        ContentResolver cr = getContentResolver();
        Imps.ProviderSettings.QueryMap settings = new Imps.ProviderSettings.QueryMap(cr,
                false /* keep updated */, null /* no handler */);
        mOtrMode.setValue(settings.getOtrMode());
        mHideOfflineContacts.setChecked(settings.getHideOfflineContacts());
        mEnableNotification.setChecked(settings.getEnableNotification());
        mNotificationVibrate.setChecked(settings.getVibrate());
        mNotificationSound.setChecked(settings.getRingtoneURI() != null);
        mForegroundService.setChecked(settings.getUseForegroundPriority());

        settings.close();
    }

    /* save the preferences in Imps so they are accessible everywhere */
    @Override
    public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
        final Imps.ProviderSettings.QueryMap settings = new Imps.ProviderSettings.QueryMap(
                getContentResolver(), false /* don't keep updated */, null /* no handler */);
        
        if (key.equals(getString(R.string.pref_security_otr_mode))) {
            settings.setOtrMode(prefs.getString(key, "auto"));
        } else if (key.equals(getString(R.string.pref_hide_offline_contacts))) {
            settings.setHideOfflineContacts(prefs.getBoolean(key, false));
        } else if (key.equals(getString(R.string.pref_enable_notification))) {
            settings.setEnableNotification(prefs.getBoolean(key, true));
        } else if (key.equals(getString(R.string.pref_notification_vibrate))) {
            settings.setVibrate(prefs.getBoolean(key, true));
        } else if (key.equals(getString(R.string.pref_notification_sound))) {
            // TODO sort out notification sound pref
            if (prefs.getBoolean(key, false)) {
                settings.setRingtoneURI(ProviderSettings.RINGTONE_DEFAULT);
            } else {
                settings.setRingtoneURI(null);
            }
        } else if (key.equals(getString(R.string.pref_foreground_service))) {
            settings.setUseForegroundPriority(prefs.getBoolean(key, false));
        }
        settings.close();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        mHideOfflineContacts = (CheckBoxPreference) findPreference(getString(R.string.pref_hide_offline_contacts));
        mOtrMode = (BhoListPreference) findPreference(getString(R.string.pref_security_otr_mode));
        mEnableNotification = (CheckBoxPreference) findPreference(getString(R.string.pref_enable_notification));
        mNotificationVibrate = (CheckBoxPreference) findPreference(getString(R.string.pref_notification_vibrate));
        mNotificationSound = (CheckBoxPreference) findPreference(getString(R.string.pref_notification_sound));
        // TODO re-enable Ringtone preference
        //mNotificationRingtone = (CheckBoxPreference) findPreference(getString(R.string.pref_notification_ringtone));
        mForegroundService = (CheckBoxPreference) findPreference(getString(R.string.pref_foreground_service));
    }

    @Override
    protected void onResume() {
        super.onResume();

        setInitialValues();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(
                this);
    }

}
