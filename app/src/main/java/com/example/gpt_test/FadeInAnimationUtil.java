package com.example.gpt_test;

import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.TextView;

public class FadeInAnimationUtil {

    public static void fadeInTextView(TextView textView, int duration) {
        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setDuration(duration);
        textView.startAnimation(fadeIn);
    }
}
