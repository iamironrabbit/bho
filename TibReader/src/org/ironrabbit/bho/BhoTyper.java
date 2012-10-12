package org.ironrabbit.bho;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import org.ironrabbit.TibConvert;

import android.content.Context;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.LinearLayout;

public class BhoTyper {
	
	public static String BHOTAG = "******************* LANG SERVICE **************";
	public static String FONT = "monlambodyig.ttf";
	
	Typeface bho;
	ArrayList<TextView> textViews = new ArrayList<TextView>();
	Context c;
	View root;
	
	public BhoTyper(Context c, View root) {
		this.c = c;
		this.root = root;
		bho = Typeface.createFromAsset(this.c.getAssets(), FONT);
	    
		refreshBho();
	}
	
	public static void parseForBhoViews(Context c, ViewGroup viewGroup) {
	    Typeface t = Typeface.createFromAsset(c.getAssets(), FONT);
	    Log.d(BhoTyper.BHOTAG, "view group with " + viewGroup.getChildCount() + " children");
	    for(int i=0; i<viewGroup.getChildCount(); i++) {
	        View v = viewGroup.getChildAt(i);
	        if(v instanceof LinearLayout)
	            parseForBhoViews(c, (ViewGroup) v);
	        else {
	            if(v instanceof TextView) {
	                ((TextView) v).setTypeface(t);
	            } else if(v instanceof EditText) {
	                ((EditText) v).setTypeface(t);
	            }
	        }
	    }
	}
	
	public void refreshBho() {
		parseForTextViews(this.root);

	    for(View v : textViews) {
	    	if(!(v instanceof android.widget.EditText)) {
	    		
	    		String oldText = ((TextView) v).getText().toString();
	    		String bhoText = TibConvert.convertUnicodeToPrecomposedTibetan(oldText);
	    		
	    		((TextView) v).setTypeface(bho, TextUtils.CAP_MODE_CHARACTERS);
	    		((TextView) v).setText(bhoText);
	    	} else {
	    		String oldHint = ((EditText) v).getHint().toString();
	    		String bhoHint = TibConvert.convertUnicodeToPrecomposedTibetan(oldHint);
	    		
	    		((EditText) v).setTypeface(bho, TextUtils.CAP_MODE_CHARACTERS);
	    		((EditText) v).setHint(bhoHint);
	    	}
	    }
	}
	
	public static int getIntValueFromContextualMenu(Map<Integer, String> opts, int which) {
		int item = -1;
	    int match = 0;
	    
	    Iterator<Integer> i = opts.keySet().iterator();
	    while(i.hasNext()) {
	    	int opt = i.next();
	    	if(match == which)
	    		item = opt;
	    	
	    	match++;
	    }
	    
	    return item;
	}
	
	private void parseForTextViews(View view) {
		try {
			if(view instanceof android.widget.TextView)
				textViews.add((TextView) view);
			else {
				ViewGroup vg = (ViewGroup) view;
				for(int v=0; v< vg.getChildCount(); v++)
					parseForTextViews((View) vg.getChildAt(v));
			}
		} catch(ClassCastException e) {}
	}

	public static void checkForFont(String fontDirName, Context c) {		
		boolean fontIsFound = false;
		File fontDir = new File(fontDirName);
		
		if(!fontDir.exists())
			fontDir.mkdir();
		else {		
			for(File f : fontDir.listFiles(new FileFilter() {

				@Override
				public boolean accept(File file) {
					if (file.getName().startsWith(".")) {
						return false;
					}
					final String lcName = file.getName().toLowerCase();
					return lcName.endsWith(".ttf") || lcName.endsWith(".otf");
				}

			})) {
				if(f.getName().equals(FONT)) {

					try {
						String thisHash = hash(f);
						String trueHash = hash(c.getAssets().open(FONT));
						if(thisHash.equals(trueHash)) {
							fontIsFound = true;
							break;
						}
					} catch (NoSuchAlgorithmException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}


				}
			}
		}
		
		if(!fontIsFound) {
			try {
				File bhoFont = new File(fontDir, FONT); 
				FileOutputStream fos = new FileOutputStream(bhoFont);
				
				InputStream is = c.getAssets().open(FONT);
				byte[] bhoFontBytes = new byte[is.available()];
				is.read(bhoFontBytes);
				is.close();
				
				fos.write(bhoFontBytes);
				fos.flush();
				fos.close();

			} catch (IOException e) {
				
				e.printStackTrace();
			}
		}
		
		// TODO: set to default
		
		
	}
	
	public static String hash(File f) throws IOException, NoSuchAlgorithmException {
		return hash(new FileInputStream(f));
	}
	
	public static String hash (InputStream is) throws IOException, NoSuchAlgorithmException
	{
		MessageDigest digester;
		
		digester = MessageDigest.getInstance("SHA-1");
	
		  byte[] bytes = new byte[8192];
		  int byteCount;
		  while ((byteCount = is.read(bytes)) > 0) {
		    digester.update(bytes, 0, byteCount);
		  }
		  
		  byte[] messageDigest = digester.digest();
		  
		// Create Hex String
	        StringBuffer hexString = new StringBuffer();
	        for (int i=0; i<messageDigest.length; i++)
	            hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
	        return hexString.toString();
	
	}
}
