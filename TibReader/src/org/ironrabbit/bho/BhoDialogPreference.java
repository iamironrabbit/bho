package org.ironrabbit.bho;


import org.ironrabbit.reader.R;

import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

public class BhoDialogPreference extends DialogPreference {
	Context context;
	BhoButton btn1, btn2, btn3;

	CharSequence title, dialogTitle;
	int dialogLayoutResId;
	
	public interface BhoDialogPreferenceListener {
		public void onDialogLayoutSet(View view);
	}

	public BhoDialogPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;

		initLayout();
	}

	public BhoDialogPreference(Context context, AttributeSet attrs, int defStype) {
		super(context, attrs, defStype);
		this.context = context;

		initLayout();
	}

	private void initLayout() {
		setLayoutResource(R.layout.bho_alert_dialog_title);
	}

	@Override
	public void setTitle(CharSequence title) {
		this.title = title;
	}

	@Override
	public void setDialogTitle(CharSequence dialogTitle) {
		this.dialogTitle = dialogTitle;
	}

	@Override
	public void setDialogLayoutResource(int dialogLayoutResId) {
		this.dialogLayoutResId = dialogLayoutResId;
		super.setDialogLayoutResource(R.layout.bho_dialog_preference);
	}

	@Override
	protected void onBindDialogView(View view) {
		LinearLayout dialogLayoutHolder = (LinearLayout) view.findViewById(R.id.bho_dialog_message_holder);
		
		View child_view = LayoutInflater.from(context).inflate(dialogLayoutResId, null);
		dialogLayoutHolder.removeAllViews();
		try {
			((LinearLayout) child_view.getParent()).removeView(child_view);
		} catch(NullPointerException e) {}

		dialogLayoutHolder.addView(child_view);
		((BhoDialogPreferenceListener) this).onDialogLayoutSet(child_view); 
		
		/*
		if(this.dialogLayoutResId != R.layout.bho_dialog_preference) {
			Log.d(BhoTyper.BHOTAG, "HERE IS WHERE I WOULD SET A NEW CHILD VIEW");

			
			
		} else {
			
		}
		*/
		
		BhoTextView dialogTitleView = (BhoTextView) view.findViewById(R.id.bho_dialog_title);
		try {
			dialogTitleView.setText(dialogTitle);
		} catch(NullPointerException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onBindView(View view) {
		BhoTextView titleView = (BhoTextView) view.findViewById(R.id.bho_alert_title);
		try {
			titleView.setText(title);
		} catch(NullPointerException e) {
			e.printStackTrace();
		}
	}

}
