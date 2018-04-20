package com.usiellau.conferenceol.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.google.gson.Gson;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.usiellau.conferenceol.R;
import com.usiellau.conferenceol.network.ConfSvMethods;
import com.usiellau.conferenceol.network.HttpResult;
import com.usiellau.conferenceol.network.entity.FileDescription;
import com.usiellau.conferenceol.util.Utils;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

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
    @BindView(R.id.btn_logout)
    Button btnLogout;

    
    ProgressDialog progressDialog;


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
        initData();
    }
    private void initData(){
        String nickName=PreferenceManager.getDefaultSharedPreferences(this).getString("nickname","null");
        tvNickname.setText(nickName);
        String imagePath=PreferenceManager.getDefaultSharedPreferences(this).getString("imagePath","");
        String fileName=imagePath.substring(imagePath.lastIndexOf("\\")+1);
        String localPath=Utils.getDefaultFileSavePath(this)+ File.separator+fileName;
        ivHead.setImageURI(Uri.fromFile(new File(localPath)));
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

    @OnClick(R.id.nickname_layout)
    void onClickNicknameLayout(){
        Intent intent=new Intent(this,NicknameActivity.class);
        startActivityForResult(intent,11);
    }

    @OnClick(R.id.btn_logout)
    void onClickBtnLogout(){
        Intent intent=new Intent(this,LoginActivity.class);
        startActivity(intent);
        finish();
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
                    uploadImage(new File(path));
                    break;
                case 11:
                    String nickname=PreferenceManager.getDefaultSharedPreferences(this).getString("nickname","null");
                    tvNickname.setText(nickname);
                    break;
                    default:
                        break;
            }
        }
    }
    
    private void uploadImage(final File file){
        FileDescription description=new FileDescription(FileDescription.TYPE_USER_HEAD_IMAGE, 
                PreferenceManager.getDefaultSharedPreferences(this).getString("username",""));
        Gson gson=new Gson();
        ConfSvMethods.getInstance().uploadFile(new Observer<HttpResult<String>>() {
            @Override
            public void onSubscribe(Disposable d) {
                showProgressDialog();
            }

            @Override
            public void onNext(HttpResult<String> stringHttpResult) {
                if(stringHttpResult.getCode()==0){
                    Toast.makeText(UserInfoActivity.this, "上传成功", Toast.LENGTH_SHORT).show();
                    ivHead.setImageURI(Uri.fromFile(file));
                    String suffix=file.getPath().substring(file.getPath().lastIndexOf("."));
                    Utils.copySdcardFile(file.getPath(),
                            Utils.getDefaultFileSavePath(UserInfoActivity.this)+File.separator+"headImage"+suffix);
                    Utils.updateLocalUserInfo(UserInfoActivity.this);
                }else{
                    Toast.makeText(UserInfoActivity.this, "上传失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Throwable e) {
                Toast.makeText(UserInfoActivity.this, "error", Toast.LENGTH_SHORT).show();
                closeProgressDialog();
            }

            @Override
            public void onComplete() {
                closeProgressDialog();
            }
        },gson.toJson(description),file);
    }

    private void showProgressDialog(){
        if(progressDialog==null){
            progressDialog=new ProgressDialog(this);
            progressDialog.setMessage("上传头像中...");
        }
        progressDialog.show();
    }

    private void closeProgressDialog(){
        progressDialog.cancel();
    }
}
