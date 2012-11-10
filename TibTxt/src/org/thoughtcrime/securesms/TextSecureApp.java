package org.thoughtcrime.securesms;

import java.util.Locale;
import org.ironrabbit.tbtxt.R;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.preference.PreferenceManager;

public class TextSecureApp extends Application {

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public void onCreate() {
		super.onCreate();
		
		 checkLocale ();
	}

	 public boolean checkLocale ()
	    {
	        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);

	        Configuration config = getResources().getConfiguration();	       
	        Locale locale;
	       
	        String oldLocale = config.locale.getLanguage();
	        
	        boolean doTibetanStyle = settings.getBoolean("pref_locale_bo", false);
	        String tibetanLocale = "bo";
	        
	        if (doTibetanStyle)
	        {
               	locale = new Locale(tibetanLocale);            
	            config.locale = locale;
	            getResources().updateConfiguration(config, getResources().getDisplayMetrics());
	        }
	        else
	        {
	        	  config.locale = Locale.getDefault();
		          getResources().updateConfiguration(config, getResources().getDisplayMetrics());
	        }
	        
	        return oldLocale.equals(config.locale.getLanguage());
	    }
}
