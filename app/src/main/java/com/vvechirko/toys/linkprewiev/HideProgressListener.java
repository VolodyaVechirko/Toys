package com.vvechirko.toys.linkprewiev;

import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.view.View;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

public class HideProgressListener implements RequestListener<Drawable> {

    View progress;

    public HideProgressListener(View progress) {
        this.progress = progress;
        if (progress != null) {
            progress.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onLoadFailed(@Nullable GlideException e, Object o, Target target, boolean b) {
        if (progress != null)
            progress.setVisibility(View.GONE);
        return false;
    }

    @Override
    public boolean onResourceReady(Drawable drawable, Object o, Target<Drawable> target, DataSource dataSource, boolean b) {
        if (progress != null)
            progress.setVisibility(View.GONE);
        return false;
    }
}