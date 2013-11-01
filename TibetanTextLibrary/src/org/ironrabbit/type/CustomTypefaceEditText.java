package org.ironrabbit.type;


import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.EditText;

public class CustomTypefaceEditText extends EditText {

    private boolean mInit = false;
    
    public CustomTypefaceEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
      

       init();
    }

    public CustomTypefaceEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		

	       init();
	}



	public CustomTypefaceEditText(Context context) {
		super(context);
		

	       init();
	}
	
	


	private void init() {
        
    	mInit = true;
    	
        setTypeface(CustomTypefaceManager.getCurrentTypeface(getContext()));
    	 
    	
        if (CustomTypefaceManager.precomposeRequired())
    	{
    	//	addTextChangedListener(mTibetanTextWatcher);
    	}
    }
    
	/*
    TextWatcher mTibetanTextWatcher = new TextWatcher()
	{
    	
    	
		@Override
		public void afterTextChanged(Editable s) {
			
			String newText = s.toString();
			
			if (CustomTypefaceManager.precomposeRequired() && newText.endsWith("\u0f0b"))
			{
				newText = CustomTypefaceManager.handlePrecompose(newText).trim();
			
				//now remove our watcher, set the value, then re-add our watcher
				removeTextChangedListener(mTibetanTextWatcher);
				setText(newText);
				addTextChangedListener(mTibetanTextWatcher);
			
				//move the cursor to the end
				setSelection(newText.length());
			}
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start,
				int count, int after) {
			
			
		}

		@Override
		public void onTextChanged(CharSequence s, int start,
				int before, int count) {
			
			
		}
		
	};
    */

}