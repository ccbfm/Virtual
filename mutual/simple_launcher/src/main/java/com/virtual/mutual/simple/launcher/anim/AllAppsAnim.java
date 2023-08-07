package com.virtual.mutual.simple.launcher.anim;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

public class AllAppsAnim {

    private final View target, start, end;
    private int centerX, centerY;
    private float radius;
    private boolean isShow = false;

    public AllAppsAnim(final View target, final View start, final View end) {
        this.target = target;
        this.start = start;
        this.end = end;
        target.post(new Runnable() {
            @Override
            public void run() {
                int[] screen = new int[2];
                target.getLocationOnScreen(screen);
                int w = target.getWidth();
                int h = target.getHeight();
                centerX = screen[0] + (w >> 1);
                centerY = screen[1] + (h >> 1);
            }
        });
        start.post(new Runnable() {
            @Override
            public void run() {
                int w = start.getWidth();
                int h = start.getHeight();
                radius = (float) Math.hypot(w, h);
            }
        });
    }

    public void showAllApps() {
        if (isShow) {
            return;
        }
        hide(this.start, new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                AllAppsAnim.this.start.setVisibility(View.GONE);
                show(AllAppsAnim.this.end, new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        AllAppsAnim.this.end.setVisibility(View.VISIBLE);
                        isShow = true;
                    }
                });
            }
        });
    }

    public void hideAllApps() {
        if (!isShow) {
            return;
        }
        hide(this.end, new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                AllAppsAnim.this.end.setVisibility(View.GONE);
                show(AllAppsAnim.this.start, new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        AllAppsAnim.this.start.setVisibility(View.VISIBLE);
                        isShow = false;
                    }
                });
            }
        });
    }


    public void show(final View view, AnimatorListenerAdapter adapter) {
        //Log.d("AllAppsAnim", "showAllApps " + centerX + " " + centerY + " " + radius);
        Animator circularReveal = ViewAnimationUtils.createCircularReveal(view,
                centerX, centerY, 0f, radius);
        circularReveal.setDuration(300);
        circularReveal.setInterpolator(new DecelerateInterpolator());
        circularReveal.addListener(adapter);
        circularReveal.start();
    }


    public void hide(final View view, AnimatorListenerAdapter adapter) {
        Animator circularReveal = ViewAnimationUtils.createCircularReveal(view,
                centerX, centerY, radius, 0f);
        circularReveal.setDuration(200);
        circularReveal.setInterpolator(new AccelerateInterpolator());
        circularReveal.addListener(adapter);
        circularReveal.start();
    }
}
