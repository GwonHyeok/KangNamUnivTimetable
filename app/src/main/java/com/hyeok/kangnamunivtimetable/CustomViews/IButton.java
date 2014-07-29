package com.hyeok.kangnamunivtimetable.CustomViews;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hyeok.kangnamunivtimetable.R;

@SuppressWarnings({"UnusedDeclaration", "FieldCanBeLocal"})
public class IButton extends RelativeLayout {

    private RelativeLayout layout;
    private ImageView image;
    private TextView text;

    public IButton(Context context) {
        this(context, null);
    }

    public IButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public IButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.ibutton, this, true);

        layout = (RelativeLayout) view.findViewById(R.id.btn_layout);

        image = (ImageView) view.findViewById(R.id.btn_icon);
        text = (TextView) view.findViewById(R.id.btn_text);

        if (attrs != null) {
            TypedArray attributes = context.obtainStyledAttributes(attrs,
                    R.styleable.IButtonStyle);

            Drawable drawable = attributes
                    .getDrawable(R.styleable.IButtonStyle_button_icon);
            if (drawable != null) {
                image.setImageDrawable(drawable);
            }

            String str = attributes
                    .getString(R.styleable.IButtonStyle_button_text);
            text.setText(str);

            int color = attributes
                    .getColor(R.styleable.IButtonStyle_button_text_color, -1);
            text.setTextColor(color);

            boolean istextBold = attributes.getBoolean(R.styleable.IButtonStyle_button_text_bold, false);
            if (istextBold) {
                text.setTypeface(null, Typeface.BOLD);
            }

            boolean iscenter = attributes.getBoolean(R.styleable.IButtonStyle_button_center, false);
            if (iscenter) {
                layout.setGravity(Gravity.CENTER);
            }
            attributes.recycle();
        }

    }

    @Override
    public void setOnClickListener(final OnClickListener l) {
        super.setOnClickListener(l);
        layout.setOnClickListener(l);
    }

    public void setImageResource(int id) {
        image.setImageResource(id);
    }

    public void setDrawable(int resId) {
        image.setImageResource(resId);
    }
}