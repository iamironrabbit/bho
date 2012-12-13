package org.ironrabbit.bho;

import org.geometerplus.zlibrary.ui.android.R;

import android.content.Context;
import android.preference.Preference;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class BhoPreference extends Preference {
	CharSequence title_text, summary_text;
	
	public BhoPreference(Context context) {
		super(context);
		
		setLayoutResource(R.layout.bho_preference);
	}
	
	public BhoPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		setLayoutResource(R.layout.bho_preference);
	}
	
	@Override
	public void setTitle(CharSequence title_text) {
		this.title_text = title_text;
	}
	
	@Override
	public void setSummary(CharSequence summary_text) {
		this.summary_text = summary_text;
	}
	
	@Override
	protected void onBindView(View view) {
		Log.d(BhoTyper.BHOTAG, "HELLO BIND VIEW?");
		BhoTextView title = (BhoTextView) view.findViewById(android.R.id.title);
		BhoTextView summary = (BhoTextView) view.findViewById(android.R.id.summary);
		
		title.setText(title_text);
		summary.setText(summary_text);
	}

}
