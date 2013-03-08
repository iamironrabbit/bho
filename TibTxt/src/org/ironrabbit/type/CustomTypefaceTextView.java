package org.ironrabbit.type;


import android.content.Context;
import android.graphics.Typeface;
import android.text.ClipboardManager;
import android.text.Spannable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewDebug.CapturedViewProperty;
import android.widget.TextView;
import android.widget.Toast;

public class CustomTypefaceTextView extends TextView {

    Context mContext;
    private boolean mDidInit = false;
    
    public CustomTypefaceTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;

       init();
    }

    
    
    public CustomTypefaceTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		  mContext = context;

	       init();
	}



	public CustomTypefaceTextView(Context context) {
		super(context);
		  mContext = context;

	       init();
	}



	private void init() {
    	mDidInit = true;
    	

    	if (mContext == null)
    		mContext = getContext();
    	
    
        setTypeface(CustomTypefaceManager.getCurrentTypeface(getContext()));
        
        
    }
    


	@Override
	public void setText(CharSequence text, BufferType type) {
		if (!mDidInit)
        	init();
		String newText = text.toString().trim();		
		newText = TibConvert.convertUnicodeToPrecomposedTibetan(text.toString());
		super.setText(newText,  TextView.BufferType.SPANNABLE);
	}


	

}