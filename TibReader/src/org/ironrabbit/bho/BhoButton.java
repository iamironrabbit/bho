package org.ironrabbit.bho;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;

public class BhoButton extends Button {
	Context c;
	private static Typeface t;
	
	public BhoButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.c = context;
		
		if(t == null)
			t = org.ironrabbit.type.CustomTypefaceManager.getCurrentTypeface(context);
		
		setTypeface(t);
	
	}
	
	public BhoButton(Context context) {
		super(context);
		this.c = context;
		
		if(t == null)
		    t = org.ironrabbit.type.CustomTypefaceManager.getCurrentTypeface(context);
		
		setTypeface(t);
	}

	@Override
	public void setTypeface(Typeface typeface) {
		super.setTypeface(typeface);
	}

}
