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

	Key mKey;

	int mXOffset;
	int mYOffset;
	
	private boolean mHasMult = false;
	private int mTextSize = -1;
	
	public DynaDrawable (Context context, Key key, Typeface typeface, int textColor, int xOffset, int yOffset, float heightMod, boolean hasMult)
	{
		mTypeface = typeface;
		
		mKey = key;
		mTextSize = (int)(key.height/heightMod);
		
		mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setTextSize(mTextSize);
        mPaint.setColor(textColor);
        mPaint.setTypeface(mTypeface);
	      
        mXOffset = xOffset;
        mYOffset = yOffset;
        mHasMult = hasMult;
     
	}
	
	@Override
	public void draw(Canvas canvas) {

		for (int i = 0; i < mKey.codes.length; i++)
		{
			int xOffset = mXOffset;
			
			if (i > 0 && (mKey.codes[i]!=3993))
				xOffset = 10;
			
			canvas.drawText(((char)mKey.codes[i])+"", xOffset, mYOffset, mPaint);
		}
		
		if (mHasMult)
		{
			mPaint.setTextSize((int)(mTextSize/1.5));
			canvas.drawText("...", mXOffset, mYOffset+(mYOffset/2), mPaint);
			mPaint.setTextSize(mTextSize);
		}
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
