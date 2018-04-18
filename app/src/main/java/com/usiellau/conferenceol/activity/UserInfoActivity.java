package com.usiellau.conferenceol.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.usiellau.conferenceol.R;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by UsielLau on 2018/4/18 0018 16:22.
 */
public class UserInfoActivity extends AppCompatActivity {

    String title="个人信息";

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.head_image_layout)
    RelativeLayout headImageLayout;
    @BindView(R.id.iv_head)
    ImageView ivHead;
    @BindView(R.id.nickname_layout)
    RelativeLayout nicknameLayout;
    @BindView(R.id.tv_nickname)
    TextView tvNickname;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        ButterKnife.bind(this);
        toolbar.setTitle(title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
    private void initData(){

    }

    @OnClick(R.id.head_image_layout)
    void onClickHeadImageLayout(){
        PictureSelector.create(this)
                .openGallery(PictureMimeType.ofImage())
                .maxSelectNum(1)
                .imageSpanCount(4)
                .previewImage(true)
                .enableCrop(true)
                .withAspectRatio(1,1)
                .forResult(PictureConfig.CHOOSE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST:
                    // 图片、视频、音频选择结果回调
                    List<LocalMedia> selectList = PictureSelector.obtainMultipleResult(data);
                    // 例如 LocalMedia 里面返回三种path
                    // 1.media.getPath(); 为原图path
                    // 2.media.getCutPath();为裁剪后path，需判断media.isCut();是否为true  注意：音视频除外
                    // 3.media.getCompressPath();为压缩后path，需判断media.isCompressed();是否为true  注意：音视频除外
                    // 如果裁剪并压缩了，以取压缩路径为准，因为是先裁剪后压缩的
                    String path=null;
                    LocalMedia media=selectList.get(0);
                    if(media.isCut()){
                        path=media.getCutPath();
                    }else{
                        path=media.getPath();
                    }

                    ivHead.setImageURI(Uri.fromFile(new File(path)));
                    break;
            }
        }
    }
}
