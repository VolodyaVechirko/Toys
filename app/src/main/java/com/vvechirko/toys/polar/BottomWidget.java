package com.vvechirko.toys.polar;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

import com.vvechirko.toys.R;

import java.util.HashSet;
import java.util.Set;

import static com.vvechirko.toys.polar.ViewAnimations.goneAll;
import static com.vvechirko.toys.polar.ViewAnimations.scaleAll;
import static java.util.Collections.emptySet;

@CoordinatorLayout.DefaultBehavior(BottomWidget.Behavior.class)
public class BottomWidget extends TransformingPolarLayout {

    public static final String TAG = BottomWidget.class.getSimpleName();
    static final int DEFAULT_DURATION = 500; // in ms
    static final int MODE_BACKGROUND = 0;
    public static final int MODE_COLLAPSED = 1;
    public static final int MODE_3_BUTTONS = 3;
    static final int MODE_3_SECONDARY_ITEMS = 4;
    public static final int MODE_5_BUTTONS = 5;
    static final int MODE_5_SECONDARY_ITEMS = 6;

    Integer mode = MODE_3_BUTTONS;

    int duration = DEFAULT_DURATION;
    ValueAnimator animator;
    SparseArray<Set<View>> views = new SparseArray<>(6);
    private ContentBottomAnimatorListener animatorListener;
    private boolean hideBackgroundOnCollapse;

    //region Layout methods
    public BottomWidget(Context context) {
        super(context);
    }

    public BottomWidget(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BottomWidget(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void enableAnimationListener(ContentBottomAnimatorListener listener) {
        animatorListener = listener;
    }

    public void disableAnimationListener() {
        animatorListener = null;
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        super.addView(child, index, params);

        int childMode = child.getLayoutParams() instanceof LayoutParams
                ? ((LayoutParams) child.getLayoutParams()).mode
                : 0;
        Set<View> modeChildren = views.get(childMode, new HashSet<>());
        modeChildren.add(child);
        views.put(childMode, modeChildren);
    }
    //endregion

    private boolean isAnimating() {
        return animator != null && animator.isRunning();
    }

    public void initCollapsed() {
        this.mode = MODE_COLLAPSED;
        // hide all menu buttons
        hide(MODE_5_BUTTONS, 1f);
        hide(MODE_5_SECONDARY_ITEMS, 1f);
        hide(MODE_3_BUTTONS, 1f);
        hide(MODE_3_SECONDARY_ITEMS, 1f);
        // show FAB
        hide(MODE_COLLAPSED, 0.0f);
        // collapse layout
        collapse(1);
        // scale down background ellipse
        scaleAll(getAll(MODE_BACKGROUND), X, 92f / 440f, 1f);
        scaleAll(getAll(MODE_BACKGROUND), Y, 92f / 390f, 1f);
    }

    //region Helpers
    protected Iterable<View> getAll(int mode) {
        return views.get(mode, emptySet());
    }

    protected void hide(int mode, float t) {
        scaleAll(getAll(mode), X | Y, 0, t);
        goneAll(getAll(mode), t > .9f);
    }

    public void collapse(float t) {
        scale(RADIUS | ANGLE, 0, t);
        shift(Y, -.4f, t);
        scaleAll(getAll(MODE_BACKGROUND), X, 92f / 440f, t);
        scaleAll(getAll(MODE_BACKGROUND), Y, 92f / 390f, t);

        if (hideBackgroundOnCollapse) {
            goneAll(getAll(MODE_BACKGROUND), t > .9f);
        }
    }
    //endregion

    public void setMode(int newMode, boolean animate) {
        // skip same mode
        if (newMode == this.mode) {
            Log.d(TAG, "setMode: same");
            return;
        }
        // skip if current animator in progress
        if (animate && isAnimating()) return;

        switch (newMode) {
            case MODE_COLLAPSED:  // collapsed fab only
                if (this.mode == MODE_3_BUTTONS)
                    collapse3ToFab(animate, false);
                if (this.mode == MODE_5_BUTTONS)
                    collapse5ToFab(animate, false);
                break;
            case MODE_3_BUTTONS: // 3 buttons
                if (this.mode == MODE_COLLAPSED)
                    collapse3ToFab(animate, true);
                if (this.mode == MODE_5_BUTTONS)
                    change3To5(animate, true);
                break;
            case MODE_5_BUTTONS: // 5 buttons
                if (this.mode == MODE_COLLAPSED)
                    collapse5ToFab(animate, true);
                if (this.mode == MODE_3_BUTTONS)
                    change3To5(animate, false);
                break;
        }

    }

    public void collapse3ToFab(boolean animate, boolean reverse) {
        Log.d(TAG, "collapse3ToFab: " + (reverse ? "reversed" : "natural"));
        animator = new AnimatorBuilder(0, 1)
                .duration(animate ? duration : 1)
                .reverse(reverse)
                // hide 5 buttons and their descriptions immediately
                .animate(t -> {
                    hide(MODE_5_BUTTONS, 1);
                    hide(MODE_5_SECONDARY_ITEMS, 1);
                }, .0f, .01f)
                // set start mode
                .animate(t -> {
                    this.mode = MODE_3_BUTTONS;
                    Log.d(TAG, "collapse3ToFab: " + t);
                }, .0f, .01f, true)
                // hide 3 buttons description with animation
                .animate(t -> hide(MODE_3_SECONDARY_ITEMS, t), .0f, .2f)
                // collapse layout
                .animate(this::collapse, .0f, .9f, new AccelerateInterpolator())
                // resize 3 buttons from 72dp to 54dp fab size
                .animate(t -> scaleAll(getAll(MODE_3_BUTTONS), X | Y, 56f / 72f, t), .0f, .9f, true)
                // hide buttons
                .animate(t -> hide(MODE_3_BUTTONS, t), .9f, 1f, true)
                .animate(t -> hide(MODE_COLLAPSED, 1 - t), .9f, 1f)
                // set end mode
                .animate(t -> {
                    this.mode = MODE_COLLAPSED;
                    Log.d(TAG, "collapse3ToFab: collapsed " + t);
                }, .99f, 1f, true)
                .build();
        animator.start();
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if (animatorListener != null) {
                    animatorListener.onModeCompleted();
                }
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
    }

    public void collapse5ToFab(boolean animate, boolean reverse) {
        Log.d(TAG, "collapse5ToFab: " + (reverse ? "reversed" : "natural"));
        animator = new AnimatorBuilder(0, 1)
                .duration(animate ? duration : 1)
                .reverse(reverse)
                // hide 5 buttons and their descriptions immediately
                .animate(t -> {
                    hide(MODE_3_BUTTONS, 1);
                    hide(MODE_3_SECONDARY_ITEMS, 1);
                }, .0f, .01f)
                // set start mode
                .animate(t -> this.mode = MODE_5_BUTTONS, .0f, .01f, true)
                // hide 3 buttons description with animation
                .animate(t -> hide(MODE_5_SECONDARY_ITEMS, t), .0f, .2f)
                // collapse layout
                .animate(this::collapse, .0f, .9f, new AccelerateInterpolator())
                // hide buttons
                .animate(t -> hide(MODE_5_BUTTONS, t), .9f, 1f, true)
                .animate(t -> hide(MODE_COLLAPSED, 1 - t), .9f, 1f)
                // set end mode
                .animate(t -> this.mode = MODE_COLLAPSED, .99f, 1f, true)
                .build();
        animator.start();
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if (animatorListener != null) {
                    animatorListener.onModeCompleted();
                }
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
    }

    public void change3To5(boolean animate, boolean reverse) {
        Log.d(TAG, "change3To5: " + (reverse ? "reversed" : "natural"));
        animator = new AnimatorBuilder(0, 1)
                .duration(animate ? duration * 3 / 2 : 1)
                .reverse(reverse)
                // set start mode
                .animate(t -> this.mode = MODE_3_BUTTONS, .0f, .01f, true)
                // hide 3 buttons description with animation
                .animate(t -> hide(MODE_3_SECONDARY_ITEMS, t), .0f, .2f)
                // collapse layout by angle
                .animate(t -> scale(ANGLE, 0, t), .01f, .49f, true, new DecelerateInterpolator())
                // resize 3 buttons from 72dp to 54dp fab size
                .animate(t -> scaleAll(getAll(MODE_3_BUTTONS), X | Y, 56f / 72f, t), .01f, .45f, true)
                // hide buttons
                .animate(t -> hide(MODE_3_BUTTONS, t), .49f, .51f, true)    // hide old ones

//--------------------------- Animation symmetry ----------------------------

                .animate(t -> hide(MODE_5_BUTTONS, 1 - t), .49f, .51f)  // show new ones
                // expand layout by angle
                .animate(t -> scale(ANGLE, 0, 1 - t), .51f, .99f, true, new DecelerateInterpolator())
                // show 5 buttons description with animation
                .animate(t -> hide(MODE_5_SECONDARY_ITEMS, 1 - t), .9f, 1f)
                // set new mode
                .animate(t -> this.mode = MODE_5_BUTTONS, .99f, 1f, true)
                .build();
        animator.start();
    }


    //region LayoutParams
    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }

    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams lp) {
        if (lp instanceof LayoutParams) {
            return new LayoutParams((LayoutParams) lp);
        } else if (lp instanceof PolarLayout.LayoutParams) {
            return new LayoutParams((PolarLayout.LayoutParams) lp);
        } else if (lp instanceof MarginLayoutParams) {
            return new LayoutParams((MarginLayoutParams) lp);
        } else
            return new LayoutParams(lp);
    }

    @Override
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    }

    public void setHideBackgroundOnCollapse(boolean hideBackgroundOnCollapse) {
        this.hideBackgroundOnCollapse = hideBackgroundOnCollapse;
    }

    public static class LayoutParams extends PolarLayout.LayoutParams {
        int mode = MODE_BACKGROUND;

        public LayoutParams(@NonNull Context c, @Nullable AttributeSet attrs) {
            super(c, attrs);

            final TypedArray a = c.obtainStyledAttributes(attrs, R.styleable.BottomWidget_Layout);
            mode = a.getInt(R.styleable.BottomWidget_Layout_layout_mode, MODE_BACKGROUND);
            a.recycle();
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(@NonNull ViewGroup.LayoutParams source) {
            super(source);
        }

        public LayoutParams(@NonNull MarginLayoutParams source) {
            super(source);
        }

        public LayoutParams(@NonNull PolarLayout.LayoutParams source) {
            super(source);
        }

        /**
         * Copy constructor
         */
        public LayoutParams(@NonNull LayoutParams source) {
            super(source);
            this.mode = source.mode;
        }

    }
    //endregion

    public interface ContentBottomAnimatorListener {
        void onModeCompleted();
    }

    // specific ContentBottomWidget behavior
    public static class Behavior extends CoordinatorLayout.Behavior<BottomWidget> {

        public Behavior() {
        }

        public Behavior(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        @Override
        public boolean onStartNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull BottomWidget child,
                                           @NonNull View directTargetChild, @NonNull View target, int axes, int type) {
            return axes == ViewCompat.SCROLL_AXIS_VERTICAL
                    || super.onStartNestedScroll(coordinatorLayout, child, directTargetChild, target, axes, type);
        }

        @Override
        public void onNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull BottomWidget child,
                                   @NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type) {
            super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type);
            applyHideBehavior(target, child, dyConsumed);
        }

        private void applyHideBehavior(View target, BottomWidget child, int dyConsumed) {
            int bottomMargin = ((CoordinatorLayout.LayoutParams) child.getLayoutParams()).bottomMargin;
            int topY = 0, bottomY = child.getHeight() + bottomMargin;

            if (canScrollVertically(target)) {
                // we can scroll
                if (dyConsumed > 0 && child.getTranslationY() == topY) {
                    // collapse fab
                    child.setMode(BottomWidget.MODE_COLLAPSED, true);
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
}