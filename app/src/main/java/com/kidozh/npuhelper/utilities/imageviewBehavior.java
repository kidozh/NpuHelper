package com.kidozh.npuhelper.utilities;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.google.android.material.appbar.AppBarLayout;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

public class imageviewBehavior extends CoordinatorLayout.Behavior<ImageView> {
    public imageviewBehavior(){}
    public imageviewBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    @Override
    public boolean layoutDependsOn(@NonNull CoordinatorLayout parent, @NonNull ImageView child, @NonNull View dependency) {
        return super.layoutDependsOn(parent, child, dependency);

    }

    @Override
    public boolean onDependentViewChanged(@NonNull CoordinatorLayout parent, @NonNull ImageView child, @NonNull View dependency) {
        if(dependency instanceof AppBarLayout){
            if (child.getVisibility() == View.GONE || child.getVisibility() == View.INVISIBLE){
            }
            else {
                child.setVisibility(View.GONE);
            }

        }
        return super.onDependentViewChanged(parent, child, dependency);
    }
}
