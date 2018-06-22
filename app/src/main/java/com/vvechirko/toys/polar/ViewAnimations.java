package com.vvechirko.toys.polar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.graphics.Point;
import android.os.Build;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateDecelerateInterpolator;

public class ViewAnimations {
    public static int X = 4;
    public static int Y = 8;


    public static void scale(View v, int props, float factor, float t) {
        float scale = 1f + (factor - 1) * t;
        if ((props & X) != 0x00) v.setScaleX(scale);
        if ((props & Y) != 0x00) v.setScaleY(scale);
    }

    public static void scaleAll(Iterable<View> views, int props, float factor, float t) {
        for (View v : views) scale(v, props, factor, t);
    }

    public static void clickableAll(Iterable<View> views, boolean enabled) {
        for (View v : views) v.setClickable(enabled);
    }

    public static void goneAll(Iterable<View> views, boolean gone) {
        for (View v : views) v.setVisibility(gone ? View.GONE : View.VISIBLE);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void hideWithReveal(View view) {
        int cx = (view.getRight() - view.getLeft()) / 2;
        int cy = view.getBottom();
        hideWithReveal(view, new Point(cx, cy));

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void hideWithReveal(View view, Point point) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // get the initial radius for the clipping circle
            int initialRadius = (int) Math.hypot(view.getWidth(), view.getHeight());

            // create the animation (the final radius is zero)
            Animator animator = ViewAnimationUtils.createCircularReveal(view, point.x, point.y, initialRadius, 0);

            // make the view invisible when the animation is done
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    view.setVisibility(View.GONE);
                }
            });

            animator.setInterpolator(new AccelerateDecelerateInterpolator());
            animator.setDuration(500);
            animator.start();
        } else {
            view.setVisibility(View.GONE);
        }
    }
}