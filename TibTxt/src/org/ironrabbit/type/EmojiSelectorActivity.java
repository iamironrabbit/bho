package org.ironrabbit.type;

import org.ironrabbit.tbtxt.R;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
/*
public class EmojiSelectorActivity extends Activity {
 public static final String TAG = "EmojiSelection";

 GridView gridView;
 CustomEmojis customEmojis;

 @Override
 protected void onCreate(Bundle savedInstanceState) {
  super.onCreate(savedInstanceState);
  setContentView(R.layout.emojis);

  initUIElement();

  customEmojis = new CustomEmojis(this);
  gridView.setAdapter(customEmojis);

  gridView.setOnItemClickListener(new OnItemClickListener() {

   @Override
   public void onItemClick(AdapterView<?> arg0, View arg1,
     int position, long arg3) {

    Log.i(TAG, "U are in OnItemSelected");
    SharedPreferences preferences = EmojiSelection.this
      .getSharedPreferences("pref", MODE_WORLD_READABLE);
    SharedPreferences.Editor editor = preferences.edit();

    editor.putInt("smiley", position);
    System.out.println("Selected emojis ---> " + position);

    // dont forgot to commit preference
    editor.commit();

    finish();
   }
  });
 }

 private void initUIElement() {
 // gridView = (GridView) findViewById(R.id.gridview1);

 }
 
 class CustomEmojis extends BaseAdapter {
	 private Activity activity;
	 private static LayoutInflater inflater = null;

	 public final int[] images = new int[] { };

	 public CustomEmojis(Activity act) {
	  activity = act;
	  inflater = (LayoutInflater) activity
	    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	 }

	 @Override
	 public int getCount() {
	  return images.length;
	 }

	 @Override
	 public Object getItem(int position) {
	  return position;
	 }

	 @Override
	 public long getItemId(int position) {
	  return position;
	 }

	 @Override
	 public View getView(int position, View convertView, ViewGroup parent) {
	  ViewHolder holder;
	  if (convertView == null) {
	   holder = new ViewHolder();
	   convertView = inflater.inflate(R.layout.grid_row, null);
	   holder.imageView = (ImageView) convertView
	     .findViewById(R.id.imageView1);
	   convertView.setTag(holder);
	  } else {
	   holder = (ViewHolder) convertView.getTag();
	  }
	  holder.imageView.setImageResource(images[position]);
	  return convertView;
	 }

	 public static class ViewHolder {
	  public ImageView imageView;
	 }

	}

}*/
