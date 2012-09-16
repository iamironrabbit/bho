package org.ironrabbit.bhoboard;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.inputmethodservice.Keyboard.Key;

public class DynaDrawable extends Drawable {

	Paint mPaint;
	Typeface mTypeface;
	String mText;
	
	Key mKey;

	public DynaDrawable (Context context, Key key, Typeface typeface, String text, int textColor)
	{
		mTypeface = typeface;
		mText = text;
		mKey = key;
		
		mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setTextSize((int)(key.height/2.5));
        mPaint.setColor(textColor);
        mPaint.setTypeface(mTypeface);
	        
	}
	
	@Override
	public void draw(Canvas canvas) {

		
		canvas.drawText(mText, -5, 5, mPaint);
		
	}


	@Override
	public int getOpacity() {
		return PixelFormat.OPAQUE;
	}

	@Override
	public void setAlpha(int alpha) {
		
	}

	@Override
	public void setColorFilter(ColorFilter cf) {
		
	}

}
