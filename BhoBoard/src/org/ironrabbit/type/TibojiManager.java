package org.ironrabbit.type;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.StringTokenizer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class TibojiManager {

	Context mContext;
	HashMap<String,Tiboji> hmTiboji;
	
	private static TibojiManager mInstance = null;
	
	private TibojiManager ()
	{
		
	}
	
	public static TibojiManager getInstance (Context context)
	{
		if (mInstance == null)
		{
			mInstance = new TibojiManager();
			mInstance.init(context);
		}
		
		return mInstance;
	}
	
	private void init (Context context)
	{
		mContext = context;
		hmTiboji = new HashMap<String,Tiboji>();
		
		try {
			String[] tibojiFiles = mContext.getAssets().list("tiboji");
			
			for (int i = 0; i < tibojiFiles.length; i++)
			{
				InputStream is = mContext.getAssets().open("tiboji/" + tibojiFiles[i]);
				
				Tiboji t = new Tiboji();
				
				t.symbol =  new File(tibojiFiles[i]).getName();
				t.symbol = t.symbol.substring(0,t.symbol.indexOf('.'));
				
				t.bitmap = BitmapFactory.decodeStream(is);
				
				hmTiboji.put(t.symbol,t);
			}
			
		} catch (IOException e) {
			
			Log.e("Tiboji","error loading",e);
		}
	}
	
	public Iterator<Entry<String,Tiboji>> getTiboji ()
	{
		return hmTiboji.entrySet().iterator();
		
	}
	public Tiboji getTiboji (String symbol)
	{
		return hmTiboji.get(symbol);
	}
	
	public Iterator<Tiboji> findTiboji (String message)
	{
		
		StringTokenizer st = new StringTokenizer (message," ");
		String token = null;
		ArrayList<Tiboji> result = new ArrayList<Tiboji>();
				
		while(st.hasMoreTokens())
		{
			token = st.nextToken();
			if (token.startsWith("("))
			{
				token = token.substring(1,token.length()-1);
				if (hmTiboji.containsKey(token))
					result.add(hmTiboji.get(token));
			
			}
		}
		
		
		return result.iterator();
	}
	
	public class Tiboji
	{
		public String symbol;
		public Bitmap bitmap;
	}
}
