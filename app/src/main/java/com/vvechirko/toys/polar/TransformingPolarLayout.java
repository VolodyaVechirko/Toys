package com.vvechirko.toys.polar;

import android.content.Context;
import android.support.annotation.FloatRange;
import android.util.AttributeSet;
import android.util.SparseArray;

public class TransformingPolarLayout extends PolarLayout {
    public static final int ANGLE = 1;
    public static final int RADIUS = 2;
    public static final int X = 4;
    public static final int Y = 8;
    public static final int XY = X + Y;
    public static final int ALL = -1;

    protected final SparseArray<Float> scaleInit = new SparseArray<>(2);
    protected final SparseArray<Float> shiftInit = new SparseArray<>(4);

    public TransformingPolarLayout(Context context) {
        super(context);
    }

    public TransformingPolarLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TransformingPolarLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * Save initial scale multipliers to SparseArray
     */
    protected float init(SparseArray<Float> holder, int property, float value) {
        if (holder.get(property) == null) {
            holder.put(property, value);
            return value;
        } else {
            return holder.get(property);
        }
    }

    public void scale(int property, float factor) {
        scale(this, property, factor, 1);
    }

    public void scale(int property, float factor, @FloatRange(from = 0.0, to = 1.0) float t) {
        scale(this, property, factor, t);
    }

    public void shift(int property, float value) {
        shift(this, property, value, 1);
    }

    public void shift(int property, float value, @FloatRange(from = 0.0, to = 1.0) float t) {
        shift(this, property, value, t);
    }

    public static <T extends TransformingPolarLayout> void scale(
            T l, int property, float factor, @FloatRange(from = 0.0, to = 1.0) float t) {
        if ((property & ANGLE) != 0x00) {
            Float initFactor = l.init(l.scaleInit, ANGLE, l.angleMultiplier);
            l.angleMultiplier = initFactor * (1 + (factor - 1) * t);
        }
        if ((property & RADIUS) != 0x00) {
            Float initFactor = l.init(l.scaleInit, RADIUS, l.radiusMultiplier);
            l.radiusMultiplier = initFactor * (1 + (factor - 1) * t);
        }
        if (property != 0x00) l.requestLayout();
    }

    public static <T extends TransformingPolarLayout> void shift(
            T l, int property, float value, @FloatRange(from = 0.0, to = 1.0) float t) {

        if ((property & ANGLE) != 0x00) {
            Float initValue = l.init(l.shiftInit, ANGLE, l.offsetAngle);
            l.offsetAngle = initValue + value * t;
        }
        if ((property & RADIUS) != 0x00) {
            Float initValue = l.init(l.shiftInit, RADIUS, l.offsetRadius);
            l.offsetRadius = initValue + value * t;
        }
        if ((property & X) != 0x00) {
            Float initValue = l.init(l.shiftInit, X, l.offsetX);
            l.offsetX = initValue + value * t;
        }
        if ((property & Y) != 0x00) {
            Float initValue = l.init(l.shiftInit, Y, l.offsetY);
            l.offsetY = initValue + value * t;
        }
        if (property != 0x00) l.requestLayout();
    }
}