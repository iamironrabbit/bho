package org.ironrabbit;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;

import org.ironrabbit.type.TibConvert;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;

public class TibetanPadActivity extends SherlockActivity implements Runnable {
	
	public final static String TAG = "Bho";

	private EditText ev;
	private String mFetchUrl;
	private String mResult;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_edit);
    	ev=(EditText)findViewById(R.id.editor);
    	
        if (getIntent() != null && getIntent().getExtras() != null)
        {
        	
	        String text = parseExtras (getIntent().getExtras());

	        if (text != null)
	        {
	        	
	        	if (text.toLowerCase().startsWith("http"))
	        	{
	        		mFetchUrl = text;
	        		Toast.makeText(this, "Loading: " + text, Toast.LENGTH_LONG).show();
	        		new Thread (this).start();
	        		
	        	}
	        	else
	        	{
	        
	                String tibText = TibConvert.convertUnicodeToPrecomposedTibetan(text);
	                ev.setText(tibText);
	        	}
	        	
	        	return;
	        }
        }
       

    	
    	setupEditor();
        
    }
    
    public void run ()
    {
		try {
			mResult = fetchUrlPlaintext(mFetchUrl);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			mResult = "error fetching page";
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

			mResult = "error fetching page";
		}
		
		mHandler.sendEmptyMessage(1);
    }
    
    private String fetchUrlPlaintext (String url) throws MalformedURLException, IOException
    {
    	//get the page, show the plaintext, fix the tibetan
    	
    	return ExtractText.extract(url);
    	
    	
    }
    
    public String parseExtras (Bundle extras)   
    {
    	if (extras.containsKey(Intent.EXTRA_STREAM))
        {
            try
            {
                // Get resource path from intent callee
                Uri uri = (Uri) extras.getParcelable(Intent.EXTRA_STREAM);

                ContentResolver cr = getContentResolver();
                InputStream is = cr.openInputStream(uri);
                // Get binary bytes for encode
                byte[] data = getBytesFromFile(is);

                return new String(data);
            } catch (Exception e)
            {
                Log.e(this.getClass().getName(), e.toString());
            }

        } 
    	else if (extras.containsKey(Intent.EXTRA_TEXT))
        {
        	
            return extras.getString(Intent.EXTRA_TEXT);
        }
     
       	return null;
    }
    
    public static byte[] getBytesFromFile(InputStream is)
	{
		try
		{
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();

			int nRead;
			byte[] data = new byte[16384];

			while ((nRead = is.read(data, 0, data.length)) != -1)
			{
				buffer.write(data, 0, nRead);
			}

			buffer.flush();

			return buffer.toByteArray();
		} catch (IOException e)
		{
			Log.e("com.eggie5.post_to_eggie5", e.toString());
			return null;
		}
	}
    
    
    public void setupEditor ()
    {
 	    }

	@Override
	protected void onResume() {
		super.onResume();
		
	}
    
	private Handler mHandler = new Handler ()
	{

		@Override
		public void handleMessage(Message msg) {
			

			if (msg.what == 1)
				ev.setText(TibConvert.convertUnicodeToPrecomposedTibetan(mResult));
			
			
			super.handleMessage(msg);
		}
		
	};
    
}