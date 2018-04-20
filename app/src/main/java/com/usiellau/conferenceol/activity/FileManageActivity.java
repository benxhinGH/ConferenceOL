package com.usiellau.conferenceol.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.usiellau.conferenceol.R;
import com.usiellau.conferenceol.adapter.FileRvAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import jp.wasabeef.recyclerview.animators.LandingAnimator;


/**
 * Created by UsielLau on 2018/3/23 0023 10:06.
 */

public class FileManageActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.rv_file)
    RecyclerView rvFile;

    private List<File> files;

    private FileRvAdapter adapter;

    MaterialDialog deleteAlertDialog;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_manage);
        ButterKnife.bind(this);
        initView();
        initDatas();
        setListener();
    }

    private void initView(){
        toolbar.setTitle("我的文件");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        adapter=new FileRvAdapter(this);
        rvFile.setLayoutManager(new LinearLayoutManager(this));
        rvFile.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        rvFile.setItemAnimator(new LandingAnimator());
        rvFile.setAdapter(adapter);
    }

    private void initDatas(){
        Observable<List<File>> observable=Observable.create(new ObservableOnSubscribe<List<File>>() {
            @Override
            public void subscribe(ObservableEmitter<List<File>> emitter) throws Exception {
                File folder=getExternalFilesDir(null);
                File[] temp=folder.listFiles();
                files=new ArrayList<>();
                for(File file:temp){
                    if(file.getName().contains(".pdf"))files.add(file);
                }
                emitter.onNext(files);
            }
        });
        observable
                .observeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .map(new Function<List<File>, List<String>>() {
            @Override
            public List<String> apply(List<File> files) throws Exception {
                List<String> list=new ArrayList<>();
                for(File file:files){
                    list.add(file.getName());
                }
                return list;
            }
        }).subscribe(new Observer<List<String>>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(List<String> strings) {
                adapter.setDatas(strings);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    private void setListener(){
        adapter.setOnItemClickListener(new FileRvAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent=new Intent(FileManageActivity.this,PdfViewActivity.class);
                intent.putExtra("fileName",files.get(position).getName());
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(int position) {
                showDeleteAlertDialog(position);
            }
        });
    }

    private void showDeleteAlertDialog(final int position){
        if(deleteAlertDialog==null){
            deleteAlertDialog=new MaterialDialog.Builder(this)
                    .title("提示")
                    .iconRes(R.drawable.ic_delete_black_24dp)
                    .content("确定删除文件？")
                    .positiveText("确定")
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            if(files.get(position).delete()){
                                files.remove(position);
                                adapter.removeData(position);
                                Toast.makeText(FileManageActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(FileManageActivity.this, "删除失败", Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
                    .negativeText("取消")
                    .build();
        }
        deleteAlertDialog.show();
    }



}
