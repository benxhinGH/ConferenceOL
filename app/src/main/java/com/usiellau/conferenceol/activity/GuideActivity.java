package com.usiellau.conferenceol.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.usiellau.conferenceol.R;
import com.usiellau.conferenceol.tools.ImageLoader;
import com.usiellau.conferenceol.util.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by UsielLau on 2018/1/19 0019 3:55.
 */

public class GuideActivity extends AppCompatActivity {

    @BindView(R.id.image)
    ImageView imageView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        //隐藏状态栏
        //定义全屏参数
        int flag= WindowManager.LayoutParams.FLAG_FULLSCREEN;
        //设置当前窗体为全屏显示
        getWindow().setFlags(flag, flag);
        ButterKnife.bind(this);
        int[] screenSize= Utils.getAndroiodScreenProperty(this);
        Bitmap bitmap=ImageLoader.decodeSampledBitmapFromResource(getResources(),
                R.drawable.pic_guide_girl,screenSize[0],screenSize[1]);
        imageView.setImageBitmap(bitmap);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doSkip();
            }
        },2000);
    }


    private void doSkip(){
        Intent intent=new Intent(this,LoginActivity.class);
        startActivity(intent);
        finish();
    }

}
