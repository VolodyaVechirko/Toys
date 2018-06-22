package com.vvechirko.toys.polar;

import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.view.animation.LinearInterpolator;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.signum;

public class AnimatorBuilder {
    protected ValueAnimator main;
    protected List<AnimatorBlock> animations;
    protected final TimeInterpolator LI = new LinearInterpolator();

    public AnimatorBuilder(float... range) {
        this.main = ValueAnimator.ofFloat(range);
        this.animations = new ArrayList<>();
    }

    public AnimatorBuilder duration(long duration) {
        this.main.setDuration(duration);
        return this;
    }

    public AnimatorBuilder reverse(boolean reverse) {
        if (reverse) main.setInterpolator(t -> 1f - t);
        return this;
    }

    public AnimatorBuilder animate(Action<Float> action) {
        return this.animate(action, 0.0f, 1.0f, LI);
    }

    public AnimatorBuilder animate(Action<Float> action, boolean trim) {
        return this.animate(action, 0.0f, 1.0f, trim, LI);
    }

    public AnimatorBuilder animate(Action<Float> action, float from, float to) {
        return this.animate(action, from, to, LI);
    }

    public AnimatorBuilder animate(Action<Float> action, float from, float to, boolean trim) {
        return this.animate(action, from, to, trim, LI);
    }

    public AnimatorBuilder animate(Action<Float> action, float from, float to, TimeInterpolator i) {
        return this.animate(action, from, to, false, i);
    }

    public AnimatorBuilder animate(Action<Float> action, float from, float to, boolean trim, TimeInterpolator i) {
        animations.add(new AnimatorBlock(action, from, to, trim ? new Trimmer<>(-1f) : null, i));
        return this;
    }

    public ValueAnimator build() {
        main.addUpdateListener(l -> {
            Float t = (Float) l.getAnimatedValue();
            for (AnimatorBlock a : animations) {
                float x = normalizeRange(a.from, a.to, t);
                if (a.trim != null) x = a.trim.trim(x);
                if (x != -1f) a.action.call(a.i.getInterpolation(x));
            }
        });
        return main;
    }

    protected float normalizeRange(float a, float b, float x) {
        return (noneg(x - a) - noneg(x - b)) * (x - a) / (b - a) + noneg(x - b);
    }

    protected float noneg(float x) {
        return signum(signum(x) + 1f);
    }

    protected static class AnimatorBlock {
        Action<Float> action;
        Float from, to;
        TimeInterpolator i;
        Trimmer<Float> trim;

        public AnimatorBlock(Action<Float> action, Float from, Float to, Trimmer<Float> trim, TimeInterpolator i) {
            this.action = action;
            this.from = from;
            this.to = to;
            this.trim = trim;
            this.i = i;
        }
    }

    protected static class Trimmer<T extends Number> {
        T left, center, right;
        T trimValue;

        public Trimmer(T trimValue) {
            this.trimValue = trimValue;
        }

        public T trim(T value) {
            // init
            if (center == null) left = center = right = value;

            // shift
            left = center;
            center = right;
            right = value;

            if (left.equals(center) && center.equals(right))
                return trimValue;
            else
                return center;

        }
    }

    public interface Action<T> {
        void call(T t);
    }
}