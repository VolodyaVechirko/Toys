package com.vvechirko.toys;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.design.widget.TextInputEditText;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class CompoundEditText extends TextInputEditText {

    private Rect bounds = new Rect();
    private DrawableClickListener clickListener;

    public CompoundEditText(Context context) {
        super(context);
        init();
    }

    public CompoundEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CompoundEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        setActive(false);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Drawable dEnd = getCompoundDrawablesRelative()[2];
        if (event.getAction() == MotionEvent.ACTION_UP && dEnd != null && clickListener != null) {
            bounds = dEnd.getBounds();
            final float x = event.getX(), y = event.getY();

            //check to make sure the touch event was within the bounds of the drawable
            if (x >= getRight() - bounds.width() && x <= getRight() - getPaddingRight()
                    && y >= getPaddingTop() && y <= getHeight() - getPaddingBottom()) {

                clickListener.onClick(DrawableClickListener.DrawablePosition.END);
                //use this to prevent the keyboard from coming up
                event.setAction(MotionEvent.ACTION_CANCEL);
                return true;
            }
        }

        return super.onTouchEvent(event);
    }

    public void setActive(boolean active) {
        setClickable(active);
        setLongClickable(active);
        setFocusable(active);
        setFocusableInTouchMode(active);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Drawable dEnd = getCompoundDrawablesRelative()[2];
        if (dEnd != null) {
            String hint = "Text";
            Paint.FontMetricsInt fontMetricsInt = getPaint().getFontMetricsInt();
            getPaint().getTextBounds(hint, 0, hint.length(), bounds);
            int textVerticalSpace = bounds.top - fontMetricsInt.top;
            int offset = (getHeight() - dEnd.getIntrinsicHeight()) / 2 - textVerticalSpace - getPaddingTop() / 2;
            dEnd.setBounds(0, -offset, dEnd.getIntrinsicWidth(), dEnd.getIntrinsicHeight() - offset);
        }
        super.onDraw(canvas);
    }

    public void setDrawableClickListener(DrawableClickListener listener) {
        this.clickListener = listener;
    }

    public interface DrawableClickListener {

        enum DrawablePosition {TOP, BOTTOM, START, END}

        void onClick(DrawablePosition target);
    }
}