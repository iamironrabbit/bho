package org.ironrabbit.bho;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class BhoTextView extends TextView {
	Context c;
	private static Typeface t;
	private boolean isSet = false;
	
	public BhoTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.c = context;
		
		doType();
		
	}
	
	private void doType ()
	{
		if (!isSet)
		{
			if(t == null)
			  t = org.ironrabbit.type.CustomTypefaceManager.getCurrentTypeface(getContext());
			
			setTypeface(t);
			isSet = true;
			
		}
	}
	
	public BhoTextView(Context context) {
		super(context);
		this.c = context;
		
		doType();
	}
	
	@Override
	public void setTypeface(Typeface typeface) {
		super.setTypeface(typeface);
	}
	
	
}
