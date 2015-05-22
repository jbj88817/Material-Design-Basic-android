package com.bojie.materialtest.anim;

import android.support.v7.widget.RecyclerView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

/**
 * Created by bojiejiang on 5/22/15.
 */
public class AnimationUtils {

    public static void animate(RecyclerView.ViewHolder holder, boolean goDown) {


        YoYo.with(Techniques.RubberBand)
                .duration(1000)
                .playOn(holder.itemView);

//        AnimatorSet animatorSet = new AnimatorSet();
//        ObjectAnimator animatorY = ObjectAnimator.ofFloat(holder.itemView, "translationY",
//                goDown ? 200 : -200, 0);
//        ObjectAnimator animatorX = ObjectAnimator.ofFloat(holder.itemView, "translationX",
//                -50, 50, -30, 30, -20, 20, -5, 5, 0);
//
//        animatorX.setDuration(1000);
//        animatorY.setDuration(1000);
//        animatorSet.playTogether(animatorX, animatorY);
//        animatorSet.start();
    }

}
