package com.vvechirko.toys;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.support.annotation.AttrRes;
import android.support.annotation.ColorInt;
import android.support.annotation.MenuRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.LinearLayout;

import java.lang.reflect.Constructor;

public class BottomBar extends LinearLayout {

    private final float DIMEN_88_DP = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 88, getResources().getDisplayMetrics());
    private final float DIMEN_12_DP = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12, getResources().getDisplayMetrics());

    private OnItemSelectedListener onItemSelectedListener;

    private Menu mMenu;

    private Paint mPaint;
    private RectF mRect;

    public BottomBar(@NonNull Context context) {
        this(context, null);
    }

    public BottomBar(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BottomBar(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setOrientation(HORIZONTAL);
        setGravity(Gravity.BOTTOM);
        setWillNotDraw(false);

        @MenuRes int menuRes = 0;
        @ColorInt int bgColor = Color.BLACK;

        if (attrs != null) {
            TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.BottomBar);
            menuRes = array.getResourceId(R.styleable.BottomBar_barMenu, 0);
            bgColor = array.getColor(R.styleable.BottomBar_barColor, 0);
            array.recycle();
        }

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(bgColor);
        mPaint.setStyle(Paint.Style.FILL);
        mRect = new RectF();

        setBarItems(menuRes);
    }

    public void inflateMenu(@MenuRes int resId) {
        mMenu = newMenuInstance(getContext());
        new MenuInflater(getContext()).inflate(resId, mMenu);

        removeAllViews();
        float weight = 1.0f / mMenu.size();

        for (int i = 0; i < mMenu.size(); i++) {
            MenuItem menuItem = mMenu.getItem(i);
            Item item = new Item(getContext(), menuItem);
            LayoutParams lp = new LayoutParams(0, LayoutParams.MATCH_PARENT, weight);
            lp.setMargins(0, (int) DIMEN_12_DP, 0, 0);
            addView(item, lp);

            item.setOnClickListener(v -> selectBarItem(indexOfChild(v)));
        }
    }

    public void setBarItems(@MenuRes int resId) {
        inflateMenu(resId);
        selectBarItem(0);
    }

    private Menu newMenuInstance(Context context) {
        try {
            Constructor<MenuBuilder> constructor = MenuBuilder.class.getDeclaredConstructor(Context.class);
            return constructor.newInstance(context);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public void setCounter(int position, int count) {
        if (position < getChildCount()) {
            Item item = (Item) getChildAt(position);
            item.setCount(count);
        }
    }

    public int getCounter(int position) {
        if (position < getChildCount()) {
            Item item = (Item) getChildAt(position);
            return item.getCount();
        }
        return 0;
    }

    public void incBy(int position, int i) {
        if (position < getChildCount()) {
            Item item = (Item) getChildAt(position);
            item.setCount(item.getCount() + i);
        }

    }

    public void selectBarItem(int position) {
        for (int i = 0; i < getChildCount(); i++) {
            Item item = (Item) getChildAt(i);
            item.setActive(position == i);
        }

        if (onItemSelectedListener != null) {
            onItemSelectedListener.onItemSelected(mMenu.getItem(position));
        }
    }

    public void setOnItemSelectedListener(OnItemSelectedListener onItemSelectedListener) {
        this.onItemSelectedListener = onItemSelectedListener;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // draw bg oval
        mRect.left = (getWidth() - DIMEN_88_DP) / 2;
        mRect.top = 0;
        mRect.right = (getWidth() + DIMEN_88_DP) / 2;
        mRect.bottom = getHeight();
        canvas.drawOval(mRect, mPaint);

        // draw bg rect
        mRect.left = 0;
        mRect.top = DIMEN_12_DP;
        mRect.right = getWidth();
        mRect.bottom = getHeight();
        canvas.drawRect(mRect, mPaint);
    }

    private static class Item extends LinearLayout {

        private AppCompatImageView itemIcon;
        private AppCompatTextView itemText, itemCount;
        private int colorAccent, colorSecondary;

        private boolean isActive = true;

        private Item(Context context, MenuItem menuItem) {
            super(context);
            initializeViews(context, menuItem);
        }

        private void initializeViews(Context context, MenuItem menuItem) {
            inflate(context, R.layout.item_bottom_bar, this);
            colorAccent = ContextCompat.getColor(getContext(), R.color.colorAccent);
            colorSecondary = ContextCompat.getColor(getContext(), R.color.whiteSemiTransparent);

            itemIcon = findViewById(R.id.itemIcon);
            itemText = findViewById(R.id.itemText);
            itemCount = findViewById(R.id.noteItemCount);

            if (menuItem != null) {
                itemIcon.setImageDrawable(menuItem.getIcon());
                itemText.setText(menuItem.getTitle());
                stateChanged();
            }
        }

        private void stateChanged() {
            itemIcon.setColorFilter(isActive ? colorAccent : colorSecondary, PorterDuff.Mode.SRC_IN);
            itemText.setTextColor(isActive ? colorAccent : colorSecondary);
//            itemText.setVisibility(isActive ? VISIBLE : GONE);
        }

        private boolean isActive() {
            return isActive;
        }

        private void setActive(boolean active) {
            isActive = active;
            stateChanged();
        }

        private void setCount(int count) {
            if (count <= 0) {
                clearCount();
            } else {
                itemCount.setText(String.valueOf(count));
                itemCount.setVisibility(VISIBLE);
            }
        }

        private void clearCount() {
            itemCount.setText("");
            itemCount.setVisibility(GONE);
        }

        private int getCount() {
            return itemCount.getText().length() == 0 ? 0 : Integer.valueOf(itemCount.getText().toString());
        }
    }

    public interface OnItemSelectedListener {
        void onItemSelected(@NonNull MenuItem item);
    }
}
