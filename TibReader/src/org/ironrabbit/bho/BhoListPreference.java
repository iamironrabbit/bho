package org.ironrabbit.bho;

import org.geometerplus.zlibrary.ui.android.R;
import org.ironrabbit.bho.BhoRadioButtonListAdapter.OnBhoSelectedListener;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.preference.ListPreference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class BhoListPreference extends ListPreference implements OnBhoSelectedListener {
	Context context;
	List<BhoOptions> options;
	int selectedItem;

	CharSequence title, summary;
	BhoTextView title_, summary_;

	public BhoListPreference(Context context) {
		super(context);
		this.context = context;
		setLayoutResource(R.layout.bho_preference);
	}

	public BhoListPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		setLayoutResource(R.layout.bho_preference);
	}

	public void getOptions() {

		options = new ArrayList<BhoOptions>();
		CharSequence[] entryValues = getEntryValues();

		String value = PreferenceManager.getDefaultSharedPreferences(context).getString(this.getKey(), "");

		int i = 0;
		for(CharSequence cs : getEntries()) {
			try {
				if(value.equals(entryValues[i]))
					options.add(new BhoOptions(cs.toString(), true));
				else
					options.add(new BhoOptions(cs.toString(), false));
			} catch(NullPointerException e) {
				options.add(new BhoOptions(cs.toString(), false));
			}
			i++;
		}
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
	protected void onBindView(View view) {
		title_ = (BhoTextView) view.findViewById(android.R.id.title);
		summary_ = (BhoTextView) view.findViewById(android.R.id.summary);

		title_.setText(title);
		summary_.setText(summary);
	}

	@Override
	protected void onPrepareDialogBuilder(Builder builder) {
		getOptions();
		BhoRadioButtonListAdapter adapter = new BhoRadioButtonListAdapter(this, context, options);
		builder.setAdapter(adapter, null);
		super.onPrepareDialogBuilder(builder);
	}

	@Override
	public void onItemSelected(int which) {
		Log.d(BhoTyper.BHOTAG, "selected " + which + " but value is: " + getValue());
		setValueIndex(which);
		setValue(this.getEntryValues()[which].toString());
		PreferenceManager.getDefaultSharedPreferences(context).edit().putString(getKey(), getValue()).commit();
		
		this.getDialog().dismiss();
		getOptions();
	}

}
