package com.example.chrock.util;

import android.view.MotionEvent;
import android.view.View;

public class Animacao {

    public static void efeitoApertar(View v) {
        v.setOnTouchListener((view, evento) -> {
            switch (evento.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    view.animate().cancel();
                    view.animate()
                            .scaleX(0.95f).scaleY(0.95f)
                            .setStartDelay(0)
                            .setDuration(80)
                            .start();
                    break;

                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    view.animate()
                            .scaleX(1f).scaleY(1f)
                            .setStartDelay(60)
                            .setDuration(150)
                            .start();
                    break;
            }
            return false;
        });
    }
}
