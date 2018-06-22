package com.vvechirko.toys.linkprewiev;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

import com.vvechirko.toys.R;

public class RoundedImageView extends AppCompatImageView {

    Path mClipPath;
    RectF mRect;
    float radius;

    public RoundedImageView(Context context) {
        super(context);
        init(context, null);
    }

    public RoundedImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public RoundedImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.RoundedImageView);
            radius = array.getDimension(R.styleable.RoundedImageView_cornerRadius, 0);
            array.recycle();
        }

        mClipPath = new Path();
        mRect = new RectF(0, 0, getWidth(), getHeight());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mRect.left = 0;
        mRect.top = 0;
        mRect.right = getWidth();
        mRect.bottom = getHeight();

        mClipPath.addRoundRect(mRect, radius, radius, Path.Direction.CW);
        canvas.clipPath(mClipPath);
        super.onDraw(canvas);
    }
}