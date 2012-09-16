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

	int mXOffset = -5;
	int mYOffset = 5;
	
	public DynaDrawable (Context context, Key key, Typeface typeface, String text, int textColor, int xOffset, int yOffset)
	{
		mTypeface = typeface;
		mText = text;
		mKey = key;
		
		mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setTextSize((int)(key.height/2.5));
        mPaint.setColor(textColor);
        mPaint.setTypeface(mTypeface);
	      
        mXOffset = xOffset;
        mYOffset = yOffset;
        
	}
	
	@Override
	public void draw(Canvas canvas) {

		
		canvas.drawText(mText, mXOffset, mYOffset, mPaint);
		
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
