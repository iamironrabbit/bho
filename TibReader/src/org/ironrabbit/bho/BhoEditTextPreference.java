package org.ironrabbit.bho;

import org.ironrabbit.reader.R;


import android.content.Context;
import android.graphics.Typeface;
import android.preference.EditTextPreference;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class BhoEditTextPreference extends EditTextPreference {
    Context c;
    private static Typeface t;
    CharSequence title, summary;
        
    public BhoEditTextPreference(Context context) {
        super(context);
        
        this.c = context;
        initLayout();
    }
    
    public BhoEditTextPreference(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.editTextPreferenceStyle);
        
        this.c = context;
        initLayout();
    }
    
    public BhoEditTextPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        this.c = context;
        initLayout();
    }
    
    private void initLayout() {
    	setLayoutResource(R.layout.bho_preference);
    	
    	if(t == null)
            t = org.ironrabbit.type.CustomTypefaceManager.getCurrentTypeface(c);
        
        this.getEditText().setTypeface(t);
    }
    
    @Override
    protected void onAddEditTextToDialogView(View dialogView, EditText editText) {
        super.onAddEditTextToDialogView(dialogView, editText);
        
        BhoTyper.parseForBhoViews(c, (ViewGroup) dialogView);
    }
}
