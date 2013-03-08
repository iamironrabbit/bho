package org.ironrabbit.type;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ironrabbit.tbtxt.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Spannable;
import android.text.style.ImageSpan;
import android.util.Log;

public class TibojiManager {

	Context mContext;
	
	HashMap<String,Tiboji> mTiboji;
	HashMap<String,Tiboji> mEmoji;
	
	
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
		mTiboji = new HashMap<String,Tiboji>();
	
		try {
			String[] tibojiFiles = mContext.getAssets().list("tiboji");
			
			for (int i = 0; i < tibojiFiles.length; i++)
			{
				InputStream is = mContext.getAssets().open("tiboji/" + tibojiFiles[i]);
				
				Tiboji t = new Tiboji();
				
				t.symbol =  new File(tibojiFiles[i]).getName();
				t.symbol = t.symbol.substring(0,t.symbol.indexOf('.'));
				
				t.bitmap = BitmapFactory.decodeStream(is);
				
				mTiboji.put(t.symbol,t);
				

				addPattern(emoticons, '(' + t.symbol + ')', t.bitmap);
				
				
			}
			
		} catch (IOException e) {
			
			Log.e("Tiboji","error loading",e);
		}
		
		mEmoji = new HashMap<String,Tiboji>();
		
		try {
			String[] emojiFiles = mContext.getAssets().list("emoji");
			
			for (int i = 0; i < emojiFiles.length; i++)
			{
				InputStream is = mContext.getAssets().open("emoji/" + emojiFiles[i]);
				
				Tiboji t = new Tiboji();
				
				t.symbol =  new File(emojiFiles[i]).getName();
				t.symbol = t.symbol.substring(0,t.symbol.indexOf('.'));
				
				t.bitmap = BitmapFactory.decodeStream(is);
				
				mEmoji.put(t.symbol,t);
			}
			
		} catch (IOException e) {
			
			Log.e("Emoji","error loading",e);
		}
	}
	
	public Iterator<Entry<String,Tiboji>> getTiboji ()
	{
		return mTiboji.entrySet().iterator();
		
	}
	public Tiboji getTiboji (String symbol)
	{
		return mTiboji.get(symbol);
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
				if (mTiboji.containsKey(token))
					result.add(mTiboji.get(token));
			
			}
		}
		
		
		return result.iterator();
	}
	
	public Iterator<Entry<String,Tiboji>> getEmoji ()
	{
		return mEmoji.entrySet().iterator();
		
	}
	public Tiboji getEmoji (String symbol)
	{
		return mEmoji.get(symbol);
	}
	
	public final static String[][] EMOJI_ASCII_MAP = {
			{"O:)","angel"},
			{":)","smile"},
			{"8)","cool"},
			{";)","wink"},
			{"<3","heart"},
			{":|","plain"},
			{":(","sad"},
			{":/","smirk"},
			{">O<","sunny"},
			{"~]","candle"},
			{"}8)","yak"},
			{"}:)","yak"},
			{"(O)","shabalep"},
			{"/\\","hands"}
	};
	
	public Iterator<Tiboji> findEmoji (String message)
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
				if (mEmoji.containsKey(token))
					result.add(mEmoji.get(token));
			
			}
		}
		
		
		return result.iterator();
	}
	
	public class Tiboji
	{
		public String symbol;
		public Bitmap bitmap;
	}
	
	private final Map<Pattern, Bitmap> emoticons = new HashMap<Pattern, Bitmap>();


	
	private void addPattern(Map<Pattern, Bitmap> map, String smile,
	Bitmap resource) {
	map.put(Pattern.compile(Pattern.quote(smile)), resource);
	}

	public boolean addSmiles(Context context, Spannable spannable) {
	boolean hasChanges = false;
	for (Entry<Pattern, Bitmap> entry : emoticons.entrySet()) {
	Matcher matcher = entry.getKey().matcher(spannable);
	while (matcher.find()) {
	boolean set = true;
	for (ImageSpan span : spannable.getSpans(matcher.start(),
	        matcher.end(), ImageSpan.class))
	    if (spannable.getSpanStart(span) >= matcher.start()
	            && spannable.getSpanEnd(span) <= matcher.end())
	        spannable.removeSpan(span);
	    else {
	        set = false;
	        break;
	    }
	if (set) {
	    hasChanges = true;
	    spannable.setSpan(new ImageSpan(context, entry.getValue()),
	            matcher.start(), matcher.end(),
	            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
	}
	}
	}
	return hasChanges;
	}
}
