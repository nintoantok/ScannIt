package com.hoffenglobal.scannit.Utils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.hoffenglobal.scannit.R;


public class MyDialog extends Dialog {
    public MyDialog(Context context) {
        super(context);
    }

    public MyDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    protected MyDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.layout_progress_dialog);

        setCanceledOnTouchOutside(false);
        setCancelable(false);
        Window window = getWindow();
        window.setDimAmount(0.7f);
        window.setLayout(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        window.setBackgroundDrawable(new ColorDrawable());

        //This is the one who throws Surface Out Of Resources Exception
        //But it's basically more of a version clash. FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS is a field
        //Implemented from API Level 21. Lower end phones usually Ignores it
        //But at times, rare phones - Mostly Samsung Low End phones throws an Exception
        //Moving this statement to build checks
//        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            //Moved in due to Exception
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

            window.setStatusBarColor(getContext().getResources().getColor(R.color.colorPrimary));
        }

        ImageView tvLoader = (ImageView) findViewById(R.id.tvLoader);
        Animation rotation = AnimationUtils.loadAnimation(getContext(), R.anim.rotate_animation);
        rotation.setRepeatCount(Animation.INFINITE);
        tvLoader.startAnimation(rotation);

    }

}
