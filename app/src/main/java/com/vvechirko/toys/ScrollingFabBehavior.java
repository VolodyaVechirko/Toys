package com.vvechirko.toys;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

public class ScrollingFabBehavior<V extends View> extends CoordinatorLayout.Behavior<V> {

    public ScrollingFabBehavior() {

    }

    public ScrollingFabBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onStartNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull V child,
                                       @NonNull View directTargetChild, @NonNull View target, int axes, int type) {
        return axes == ViewCompat.SCROLL_AXIS_VERTICAL
                || super.onStartNestedScroll(coordinatorLayout, child, directTargetChild, target, axes, type);
    }

    @Override
    public void onNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull V child, @NonNull View target,
                               int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type) {
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type);

        applyHideBehavior(target, child, dyConsumed);
    }

    private void applyHideBehavior(View target, View child, int dyConsumed) {
        int bottomMargin = ((CoordinatorLayout.LayoutParams) child.getLayoutParams()).bottomMargin;
        int topY = 0, bottomY = child.getHeight() + bottomMargin;

        if (canScrollVertically(target)) {
            // we can scroll
            if (dyConsumed > 0 && child.getTranslationY() == topY) { // hide fab
                child.animate()
                        .translationY(bottomY)
                        .setInterpolator(new AccelerateInterpolator())
                        .start();
            } else if (dyConsumed < 0 && child.getTranslationY() == bottomY) { // show fab
                child.animate()
                        .translationY(topY)
                        .setInterpolator(new DecelerateInterpolator())
                        .start();
            }
        } else {
            // we cannot scroll, so show fab
            if (dyConsumed == 0 && child.getTranslationY() == bottomY) {
                child.animate()
                        .translationY(topY)
                        .setInterpolator(new DecelerateInterpolator())
                        .start();
            }
        }
    }

    @SuppressLint("RestrictedApi")
    private boolean canScrollVertically(View target) {
        if (target instanceof RecyclerView) {
            RecyclerView rv = (RecyclerView) target;
            return rv.computeVerticalScrollRange() > rv.getHeight();
        } else if (target instanceof NestedScrollView) {
            NestedScrollView nsv = (NestedScrollView) target;
            return nsv.computeVerticalScrollRange() > nsv.getHeight();
        } else {
            return false;
        }
    }
}