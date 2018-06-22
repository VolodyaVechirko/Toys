package com.vvechirko.toys.linkprewiev;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.vvechirko.toys.R;

public class LinkPreview extends RelativeLayout {

    AppCompatTextView tvTitle, tvLink;
    RoundedImageView ivPreview;
    ProgressBar progressBar;

    LinkGenerator linkGenerator;
    Drawable noImage;
    String link;

    public LinkPreview(Context context) {
        super(context);
        initialize(context);
    }

    public LinkPreview(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context);
    }

    public LinkPreview(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initialize(context);
    }

    private void initialize(Context context) {
        inflate(context, R.layout.link_preview_layout, this);
        ivPreview = findViewById(R.id.ivPreview);
        tvTitle = findViewById(R.id.tvTitle);
        tvLink = findViewById(R.id.tvLink);
        progressBar = findViewById(R.id.progressBar);

        linkGenerator = new LinkGenerator();
        noImage = new ColorDrawable(Color.parseColor("#898AA2"));
    }

    public void setLink(String url) {
        link = url;
        linkGenerator.generatePreview(link, new LinkGenerator.PreviewGeneratedListener() {
            @Override
            public void onPreviewCached(LinkPreviewModel preview) {
                showPreview(preview);
            }

            @Override
            public void onPreviewStartGenerate() {
                clear();
                showLoading(true);
            }

            @Override
            public void onPreviewGenerated(@Nullable LinkPreviewModel preview) {
                showLoading(false);
                if (preview != null) {
                    showPreview(preview);
                } else {
                    clear();
                }
            }

            @Override
            public void onPreviewGenerateError(Throwable throwable) {
                throwable.printStackTrace();
            }
        });
    }

    public void setError(String error) {
        clear();
        tvLink.setText(error);
    }

    public String getLink() {
        return link;
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? VISIBLE : GONE);
    }

    private void showPreview(LinkPreviewModel lp) {
        tvTitle.setText(lp.getTitle());
        tvLink.setText(lp.getUrl());

        if (lp.getPreviewUrl() != null) {
            Glide.with(getContext().getApplicationContext())
                    .load(lp.getPreviewUrl())
                    .listener(new HideProgressListener(progressBar))
                    .apply(RequestOptions.placeholderOf(noImage)
                            .error(noImage)
                    ).into(ivPreview);
        } else {
            ivPreview.setImageDrawable(noImage);
        }
    }

    private void clear() {
        ivPreview.setImageDrawable(null);

        tvLink.setText("");
        tvTitle.setText("");
        showLoading(false);
    }
}