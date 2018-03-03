package com.example.asus.lxymediaplayer;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
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
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.nio.file.Path;

public class LxyVerticalPlayerActivity extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;
    private Media media;
    private VideoView mVideoView;
    private int mLastPlayedTime;
    private boolean isExit;
    private final String LAST_PLAYED_TIME = "LAST_TIME";
    private String path,filePath;
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {        //toolbar
        switch (item.getItemId()) {
            case R.id.main_toolbar_regard:
                Intent intent2=new Intent(LxyVerticalPlayerActivity.this,LxyRegardActivity.class);
                startActivity(intent2);
              //  Toast.makeText(LxyVerticalPlayerActivity.this, "Created!", Toast.LENGTH_SHORT).show();
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
        setContentView(R.layout.activity_lxy_vertical_player);
        ActivityCollector.addActivity(this);           //添加到活动容器中
        Toolbar toolbar = findViewById(R.id.main_toolbar);
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
                        if (ContextCompat.checkSelfPermission(LxyVerticalPlayerActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(LxyVerticalPlayerActivity.this,
                                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                        } else {
                            openFileManager();
                        }
                        mDrawerLayout.closeDrawers();
                        break;
                    case R.id.nav_regard:
                        Intent intent2=new Intent(LxyVerticalPlayerActivity.this,LxyRegardActivity.class);
                        startActivity(intent2);
                        mDrawerLayout.closeDrawers();
                        break;
                    case R.id.nav_exit:
                        AlertDialog.Builder dialog = new AlertDialog.Builder(LxyVerticalPlayerActivity.this);
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
                    case R.id.nav_camera:
                        // 激活系统的照相机进行录像
                        Intent intent5 = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                        startActivityForResult(intent5, 2);
                        mDrawerLayout.closeDrawers();
                        break;
                    case R.id.nav_internet:
                        Intent intent4=new Intent(LxyVerticalPlayerActivity.this,LxyInternetActivity.class);
                        startActivity(intent4);
                        break;
                    default:
                        break;
                }
                return true;
            }
        });

        // replaceFragment(new VerticalPlayerFragment());
        Uri uri = getIntent().getData();
        path = uri.getPath();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        String[] searchvideo = new String[]{
                MediaStore.Video.Media.DATA,  //视频存放位置
                MediaStore.Video.Media.TITLE,  //视频标题
                MediaStore.Video.Media.SIZE,  //视频大小
                MediaStore.Video.Media.DURATION,  //视频时长
                MediaStore.Video.Media.DATE_ADDED  //视频添加时间
        };

        String where = MediaStore.Video.Media.DATA + " = '" + path + "'";  //查询指定位置视频
        //String[] keywords = null;
        String sort = MediaStore.Video.Media.DEFAULT_SORT_ORDER;     //排序
        Cursor cursor = getContentResolver().
                query(
                        MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                        searchvideo,
                        where,
                        null,
                        sort);

        while (cursor.moveToNext()) {                 //遍历Cursor
            String MediaPath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA));
            String MediaTitle = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE));
            String MediaSize = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE));
            String MediaDuration = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION));
            String MediaAdded = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_ADDED));

            media = new Media(MediaPath, MediaTitle, MediaSize, MediaDuration, MediaAdded);
        }
        cursor.close();

        TextView videotitle = (TextView) findViewById(R.id.media_title);
        TextView videosize = (TextView) findViewById(R.id.media_size);
        TextView videoduration = (TextView) findViewById(R.id.media_duration);
        TextView videoadddate = (TextView) findViewById(R.id.media_create_time);
        TextView videopath = (TextView) findViewById(R.id.media_path);


        videotitle.setText(media.getMediaTitle());
        videosize.setText(String.valueOf(media.getMediaSize() + "M"));
        videoduration.setText(media.getMediaDuration());
        videoadddate.setText(media.getMediaAdded());
        videopath.setText(media.getMediaPath());

        mVideoView = (VideoView) findViewById(R.id.video_view1);
        mVideoView.setVideoPath(path);

        MediaController controller = new MediaController(this);
        mVideoView.setMediaController(controller);



   /*   if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Intent intent = new Intent(LxyVerticalPlayerActivity.this, LxyLandscapePlayerActivity.class);
            intent.setData(Uri.parse(path));
            intent.putExtra("mLastPlayedTime", mLastPlayedTime);
            // super.onDestroy();
            // ActivityCollector.removeActivity(this);
            startActivity(intent);
            //   replaceFragment(new LandscapePlayerFragment());
            //mVideoView= (VideoView) findViewById(R.id.video_view1);
            //mVideoView.setVideoPath(path);
            //MediaController controller = new MediaController(this);
            // mVideoView.setMediaController(controller);
        }*/
    }

    protected void onPause() {
        super.onPause();
        mVideoView.pause();
        mLastPlayedTime = mVideoView.getCurrentPosition();
    }

    @Override
    protected void onResume() {
        super.onResume();

        mVideoView.start();
        if (mLastPlayedTime > 0) {
            mVideoView.seekTo(mLastPlayedTime);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(LAST_PLAYED_TIME, mVideoView.getCurrentPosition());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        mLastPlayedTime = savedInstanceState.getInt(LAST_PLAYED_TIME);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {//横屏
            Intent intent = new Intent(LxyVerticalPlayerActivity.this, LxyLandscapePlayerActivity.class);
            intent.setData(Uri.parse(path));
            intent.putExtra("mLastPlayedTime", mLastPlayedTime);
            startActivity(intent);
        }
        else if (this.getResources().getConfiguration().orientation== Configuration.ORIENTATION_PORTRAIT){//竖屏

        }
    }

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

        mVideoView = (VideoView) findViewById(R.id.video_view1);
        path=filePath;
        mVideoView.setVideoPath(path);
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