package org.ironrabbit.bhoboard;

import org.ironrabbit.type.CustomTypefaceManager;

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
	
	private final static int[] EXCLUDE_OFFSET_KEYS = {3963,3962,3954,3964,3965,3966};
	
	@Override
	public void draw(Canvas canvas) {

		for (int i = 0; i < mKey.codes.length; i++)
		{
			int xOffset = mXOffset;
			
			if (i > 0 && (mKey.codes[i]!=3993) && CustomTypefaceManager.precomposeRequired())
				xOffset = 10;
			
			for (int n = 0; n < EXCLUDE_OFFSET_KEYS.length; n++)
				if (EXCLUDE_OFFSET_KEYS[n]==mKey.codes[i])
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
