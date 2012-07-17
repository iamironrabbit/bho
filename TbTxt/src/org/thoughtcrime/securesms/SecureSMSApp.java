package org.thoughtcrime.securesms;

import java.util.Locale;
import org.ironrabbit.tbtxt.R;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.preference.PreferenceManager;

public class SecureSMSApp extends Application
{

	private Locale locale;
	public final static String DEFAULT_LOCALE = "bo";
	private SharedPreferences settings;
	
	public final static String PREF_DEFAULT_LOCALE = "dl";
	
	@Override
    public void onCreate() {
        super.onCreate();
        
        settings = PreferenceManager.getDefaultSharedPreferences(this);

        Configuration config = getResources().getConfiguration();

        String lang = settings.getString(PREF_DEFAULT_LOCALE, DEFAULT_LOCALE);
        
        if (! "".equals(lang) && ! config.locale.getLanguage().equals(lang))
        {
        	if (lang.equals("xx"))
            {
            	locale = Locale.getDefault();
            
            }
            else
            	locale = new Locale(lang);
        	
            Locale.setDefault(locale);
            config.locale = locale;
            getResources().updateConfiguration(config, getResources().getDisplayMetrics());
        }
        
    }
	
    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);

        String lang = settings.getString(PREF_DEFAULT_LOCALE, DEFAULT_LOCALE);

        if (! "".equals(lang) && ! newConfig.locale.getLanguage().equals(lang))
        {
            locale = new Locale(lang);
            newConfig.locale = locale;
            Locale.setDefault(locale);
            getResources().updateConfiguration(newConfig, getResources().getDisplayMetrics());
        }
    }
}