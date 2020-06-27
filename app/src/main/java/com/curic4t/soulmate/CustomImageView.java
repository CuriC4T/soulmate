package com.curic4t.soulmate;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.appcompat.widget.AppCompatImageView;

public class CustomImageView extends AppCompatImageView {
    public CustomImageView(Context context){
        super(context);
        init();
    }
    public CustomImageView(Context context, AttributeSet attrs){
        super(context,attrs);
        init();
    }


    public void init(){
        setClickable(true);

    }
    @Override
    public boolean performClick() {
        return super.performClick();
    }

}
