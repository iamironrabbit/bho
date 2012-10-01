package org.ironrabbit.bho;

import org.ironrabbit.TibConvert;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.EditText;

public class BhoEditText extends EditText {
	Context c;
	private static Typeface t;
	private String lastChange = "";
	
	public BhoEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.c = context;
		
		if(t == null)
			t = Typeface.createFromAsset(this.c.getAssets(), BhoTyper.FONT);
		
		setTypeface(t);
		
		addTextChangedListener(new TextWatcher() { 
            public void  afterTextChanged (Editable s){
            	
            	String newText = s.toString();
            	
            	if (!lastChange.equals(newText))
            	{
            		if (Math.abs(newText.length()-lastChange.length())>2)
            		{
    	                newText = TibConvert.convertUnicodeToPrecomposedTibetan(newText);

            		}
            		
            		lastChange = newText;
            		setText(lastChange);
            		
            		setSelection(lastChange.length());
            		
            	}
            	
            } 
            public void  beforeTextChanged  (CharSequence s, int start, int count, int after){} 
            public void  onTextChanged  (CharSequence s, int start, int before,  int count) {}
    	});
		
	}
	
	public BhoEditText(Context context) {
		super(context);
		this.c = context;
		
		if(t == null)
			t = Typeface.createFromAsset(this.c.getAssets(), BhoTyper.FONT);
		
		setTypeface(t);
		
		addTextChangedListener(new TextWatcher() { 
            public void  afterTextChanged (Editable s){
            	
            	String newText = s.toString();
            	
            	if (!lastChange.equals(newText))
            	{
            		if (Math.abs(newText.length()-lastChange.length())>2)
            		{
    	                newText = TibConvert.convertUnicodeToPrecomposedTibetan(newText);

            		}
            		
            		lastChange = newText;
            		setText(lastChange);
            		
            		setSelection(lastChange.length());
            		
            	}
            	
            } 
            public void  beforeTextChanged  (CharSequence s, int start, int count, int after){} 
            public void  onTextChanged  (CharSequence s, int start, int before,  int count) {}
    	});
	}

	@Override
	public void setTypeface(Typeface typeface) {
		super.setTypeface(typeface);
	}

}
