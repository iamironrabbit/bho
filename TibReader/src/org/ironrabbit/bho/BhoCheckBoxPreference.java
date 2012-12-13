package org.ironrabbit.bho;

import org.geometerplus.zlibrary.ui.android.R;

import android.content.Context;
import android.preference.CheckBoxPreference;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;

public class BhoCheckBoxPreference extends CheckBoxPreference {
	Context context;
	CharSequence title, summary;
	BhoTextView title_, summary_;
	
	public BhoCheckBoxPreference(Context context) {
		super(context);
		this.context = context;
		setLayoutResource(R.layout.bho_checkbox_preference);
	}
	
	public BhoCheckBoxPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		setLayoutResource(R.layout.bho_checkbox_preference);
	}
	
	public BhoCheckBoxPreference(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
		setLayoutResource(R.layout.bho_checkbox_preference);
	}
	
	@Override
	public void setTitle(CharSequence title) {
		this.title = title;
		
		try {
			title_.setText(title);
		} catch(NullPointerException e) {};
	}
	
	@Override
	public void setSummary(CharSequence summary) {
		this.summary = summary;
		
		try {
			summary_.setText(summary);
		} catch(NullPointerException e) {}
	}
	
	@Override
	public void setSummaryOn(CharSequence summary) {
		setSummary(summary);
	}
	
	@Override
	public void setSummaryOff(CharSequence summary) {
		setSummary(summary);
	}
	
	@Override
	public void onBindView(View view) {
		title_ = (BhoTextView) view.findViewById(android.R.id.title);
		summary_ = (BhoTextView) view.findViewById(android.R.id.summary);
		
		title_.setText(title);
		summary_.setText(summary);
		
		LinearLayout widget_frame = (LinearLayout) view.findViewById(android.R.id.widget_frame);
		CheckBox cb = (CheckBox) widget_frame.getChildAt(0);
		cb.setChecked(isChecked());
	}

}
