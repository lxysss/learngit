package com.example.asus.lxymediaplayer;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ColorSpace;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.litepal.LitePal;
import org.litepal.crud.DataSupport;
import org.litepal.tablemanager.Connector;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class LxyMainActivity extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;
    private List<Media> medias = new ArrayList<>();
    //   private SwipeRefreshLayout swipeRefresh;
    private LxyMediaAdapter adapter;
    private boolean isExit;
    private String filePath;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {        //toolbar
        switch (item.getItemId()) {
            case R.id.main_toolbar_refresh:
                refreshMediaList();
                Toast.makeText(LxyMainActivity.this, "刷新成功！", Toast.LENGTH_SHORT).show();
                break;
            case R.id.main_toolbar_regard:
                Intent intent2 = new Intent(LxyMainActivity.this, LxyRegardActivity.class);
                startActivity(intent2);
                //Toast.makeText(LxyMainActivity.this,"Created!",Toast.LENGTH_SHORT).show();
                break;
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lxy_main);
        Toolbar toolbar = findViewById(R.id.main_toolbar);
        ActivityCollector.addActivity(this);           //添加到活动容器中
        setSupportActionBar(toolbar);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.main_drawer_layout);
        NavigationView naviView = (NavigationView) findViewById(R.id.nav_view);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.touxiang11);
        }
        naviView.setCheckedItem(R.id.nav_file);                   //NavView
        naviView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_file:
                        if (ContextCompat.checkSelfPermission(LxyMainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(LxyMainActivity.this,
                                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                        } else {
                            openFileManager();
                        }
                        mDrawerLayout.closeDrawers();
                        break;
                    case R.id.nav_regard:
                        Intent intent2 = new Intent(LxyMainActivity.this, LxyRegardActivity.class);
                        startActivity(intent2);
                        mDrawerLayout.closeDrawers();
                        break;
                    case R.id.nav_exit:
                        AlertDialog.Builder dialog = new AlertDialog.Builder(LxyMainActivity.this);
                        dialog.setTitle("请确定~~");
                        dialog.setMessage("是否退出Lxy视频播放器");
                        dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCollector.finishAll();
                            }
                        });
                        dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                        dialog.show();
                        mDrawerLayout.closeDrawers();
                        break;
                    case R.id.nav_internet:
                        Intent intent4=new Intent(LxyMainActivity.this,LxyInternetActivity.class);
                        startActivity(intent4);
                        mDrawerLayout.closeDrawers();
                        break;
                    case R.id.nav_camera:
                        // 激活系统的照相机进行录像
                        Intent intent5 = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                        startActivityForResult(intent5, 2);
                        mDrawerLayout.closeDrawers();
                        break;
                    default:
                        break;
                }
                return true;
            }
        });

        UpdateMediaList();
        adapter = new LxyMediaAdapter(LxyMainActivity.this, R.layout.lxy_media_item, medias); //显示列表
        ListView listview = (ListView) findViewById(R.id.list_view);
        listview.setAdapter(adapter);


        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Media item = medias.get(position);
                Intent intent = new Intent(LxyMainActivity.this, LxyVerticalPlayerActivity.class);
                intent.setData(Uri.parse(item.getMediaPath()));
                startActivity(intent);
            }
        });

/*         swipeRefresh=(SwipeRefreshLayout)findViewById(R.id.swipe_refresh);
         swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
         swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
             @Override
             public void onRefresh() {
                 refreshMediaList();
             }
         });*/

    }


    public void UpdateMediaList() {
        Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;        //获取视频信息
        String[] searchMedia = new String[]{
                //MediaStore.Video.Media.DISPLAY_NAME,  //视频在sd卡中的名称
                MediaStore.Video.Media.DATA,  //视频存放位置
                MediaStore.Video.Media.TITLE,  //视频标题
                MediaStore.Video.Media.SIZE,  //视频大小
                MediaStore.Video.Media.DURATION,  //视频时长
                MediaStore.Video.Media.DATE_ADDED  //视频添加时间
        };

        //  String where = MediaStore.Video.Media.DATA + " like \"%" + "/Video" + "%\"";  //查询Video目录
        String sort = MediaStore.Video.Media.DEFAULT_SORT_ORDER;     //排序
        ContentResolver resolver = (ContentResolver) getContentResolver();
        Cursor cursor = resolver.query(
                uri,
                searchMedia,
                null,                //查询全部视频
                null,
                sort);

        //Connector.getDatabase();                       //创建数据库
        //LitePal.getDatabase();

        while (cursor.moveToNext()) {                 //遍历Cursor
            String MediaPath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA));
            String MediaTitle = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE));
            String MediaSize = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE));
            String MediaDuration = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION));
            String MediaAdded = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_ADDED));

            Media media = new Media(MediaPath, MediaTitle, MediaSize, MediaDuration, MediaAdded);
            //media.save();
            //Toast.makeText(LxyMainActivity.this,MediaDuration,Toast.LENGTH_SHORT).show();

            medias.add(media);

        }
        cursor.close();
    }

    private void refreshMediaList() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        medias.clear();
                        UpdateMediaList();
                        adapter.notifyDataSetChanged();
                        //             swipeRefresh.setRefreshing(false);
                    }
                });
            }
        }).start();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit();
            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }


    public void exit() {
        if (!isExit) {
            isExit = true;
            Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
            mHandler.sendEmptyMessageDelayed(0, 2000);
        } else {
            ActivityCollector.finishAll();
        }
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            isExit = false;
        }

    };

    private void openFileManager() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("*/*");
        startActivityForResult(intent, 1);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case 1:
            if (resultCode == RESULT_OK) {
                if (Build.VERSION.SDK_INT >= 19) {
                    handleFileOnKitKat(data);
                } else {
                    handleFileBeforeKitKat(data);
                }
            }break;
            case 2:
                break;
                default:break;
        }

    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void handleFileOnKitKat(Intent data) {
        Uri uri = data.getData();
        if (DocumentsContract.isDocumentUri(this, uri)) {
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1];
                String selectionVideo = MediaStore.Video.Media._ID + "=" + id;
                //String selection= MediaStore.Images.Media._ID+"="+id;
                if (getFilePath(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, selectionVideo) != null) {
                    filePath = getFilePath(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, selectionVideo);
                }
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                filePath = getFilePath(contentUri, null);
            } else if ("content".equalsIgnoreCase(uri.getScheme())) {
                filePath = getFilePath(uri, null);
            } else if ("file".equalsIgnoreCase(uri.getScheme())) {
                filePath = uri.getPath();
            }

        }
        int lastDot = filePath.lastIndexOf(".");
        String extention = filePath.substring(lastDot + 1).toUpperCase();
        //跳转
        Intent intent3 = new Intent(LxyMainActivity.this, LxyVerticalPlayerActivity.class);
        intent3.setData(Uri.parse(filePath));
        startActivity(intent3);

        Toast.makeText(this, filePath, Toast.LENGTH_SHORT).show();
    }

    private void handleFileBeforeKitKat(Intent data) {
        Uri uri = data.getData();
        String filePath = getFilePath(uri, null);
        int lastDot = filePath.lastIndexOf(".");
        String extention = filePath.substring(lastDot + 1).toUpperCase();
        Toast.makeText(this, extention, Toast.LENGTH_SHORT).show();
        Toast.makeText(this, filePath, Toast.LENGTH_SHORT).show();
    }

    private String getFilePath(Uri uri, String selection) {
        String path = null;
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openFileManager();
                } else {
                    Toast.makeText(this, "You denied the permission!", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }


}


