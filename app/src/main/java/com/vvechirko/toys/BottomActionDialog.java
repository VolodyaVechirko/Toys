package com.vvechirko.toys;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.annotation.StyleRes;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;

public class BottomActionDialog extends BottomSheetDialog {

    private AppCompatTextView tvTitle, tvActionText1, tvActionText2;
    private AppCompatImageView ivActionIcon1, ivActionIcon2;
    private View actionContainer1, actionContainer2;

    public BottomActionDialog(@NonNull Context context) {
        super(context);
        init();
    }

    public BottomActionDialog(@NonNull Context context, @StyleRes int theme) {
        super(context, theme);
        init();
    }

    public BottomActionDialog titleText(@StringRes int text) {
        tvTitle.setText(text);
        return this;
    }

    public BottomActionDialog action1(@DrawableRes int icon, @StringRes int text, View.OnClickListener listener) {
        ivActionIcon1.setImageResource(icon);
        tvActionText1.setText(text);

        actionContainer1.setVisibility(View.VISIBLE);
        actionContainer1.setOnClickListener(v -> {
            listener.onClick(v);
            dismiss();
        });
        return this;
    }

    public BottomActionDialog action2(@DrawableRes int icon, @StringRes int text, View.OnClickListener listener) {
        ivActionIcon2.setImageResource(icon);
        tvActionText2.setText(text);

        actionContainer2.setVisibility(View.VISIBLE);
        actionContainer2.setOnClickListener(v -> {
            listener.onClick(v);
            dismiss();
        });
        return this;
    }

    private void init() {
        setContentView(new View(getContext()));
        setContentView(R.layout.bottom_action_dialog);
        findViewsById();

        actionContainer1.setVisibility(View.GONE);
        actionContainer2.setVisibility(View.GONE);
    }

    private void findViewsById() {
        tvTitle = findViewById(R.id.tvTitle);
        actionContainer1 = findViewById(R.id.actionContainer1);
        actionContainer2 = findViewById(R.id.actionContainer2);
        tvActionText1 = findViewById(R.id.tvActionText1);
        tvActionText2 = findViewById(R.id.tvActionText2);
        ivActionIcon1 = findViewById(R.id.ivActionIcon1);
        ivActionIcon2 = findViewById(R.id.ivActionIcon2);
    }
}
