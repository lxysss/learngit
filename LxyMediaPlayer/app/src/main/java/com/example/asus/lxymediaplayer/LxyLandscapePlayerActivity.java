package com.example.asus.lxymediaplayer;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.VideoView;


public class LxyLandscapePlayerActivity extends AppCompatActivity {
    private VideoView mVideoView;
    private  int mLastPlayedTime;
    private final String LAST_PLAYED_TIME = "LAST_TIME";
    private View mVolumeBrightnessLayout;
    private ImageView mOperationBg;
    private ImageView mOperationPercent;
    private AudioManager mAudioManager;

    private int mMaxVolume;
    private int mVolume = -1;
    private float mBrightness = -1f;

    private GestureDetector mGestureDetector;
    private MediaController mMediaController;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lxy_landscape_player);
        ActivityCollector.addActivity(this);           //添加到活动容器中

        Uri uri=getIntent().getData();

        String path=uri.getPath();

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
      //  getSupportActionBar().hide();           //隐藏Actionbar


        mVideoView= (VideoView) findViewById(R.id.video_view2);
        mVolumeBrightnessLayout = findViewById(R.id.operation_volume_brightness);
        mOperationBg = (ImageView) findViewById(R.id.operation_bg);
        mOperationPercent = (ImageView) findViewById(R.id.operation_percent);

        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mMaxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

        mVideoView.requestFocus();

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.FILL_PARENT,
                RelativeLayout.LayoutParams.FILL_PARENT);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);



        mVideoView.setLayoutParams(layoutParams);
        mVideoView.setVideoPath(path);
       // mVideoView.start();
       // mVideoView.seekTo(getIntent().getIntExtra("mLastPlayedTime",0));

        if(this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            finish();
        }

        mMediaController = new MediaController(this);
        mVideoView.setMediaController(mMediaController);

        mVideoView.requestFocus();

        mGestureDetector = new GestureDetector(this, new MyGestureListener());

    }

    protected void onPause() {
        super.onPause();
        mVideoView.pause();
        mLastPlayedTime = mVideoView.getCurrentPosition();
    }

    @Override
    protected  void onResume() {
        super.onResume();
        mVideoView.start();
        if(mLastPlayedTime > 0) {
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
    public boolean onTouchEvent(MotionEvent event) {
        if (mGestureDetector.onTouchEvent(event))
            return true;

        // 处理手势结束
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_UP:
                endGesture();
                break;
        }

        return super.onTouchEvent(event);
    }

    /** 手势结束 */
    private void endGesture() {
        mVolume = -1;
        mBrightness = -1f;

        // 隐藏
        mDismissHandler.removeMessages(0);
        mDismissHandler.sendEmptyMessageDelayed(0, 500);
    }

    private class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
    //滑动
    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2,
                            float distanceX, float distanceY) {
        float mOldX = e1.getX(), mOldY = e1.getY();
        int y = (int) e2.getRawY();
        Display disp = getWindowManager().getDefaultDisplay();
        int windowWidth = disp.getWidth();
        int windowHeight = disp.getHeight();

        if (mOldX > windowWidth * 4.0 / 5)// 右边滑动
            onVolumeSlide((mOldY - y) / windowHeight);
        else if (mOldX < windowWidth / 5.0)// 左边滑动
            onBrightnessSlide((mOldY - y) / windowHeight);

        return super.onScroll(e1, e2, distanceX, distanceY);
    }
}



    // 定时隐藏
    private Handler mDismissHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mVolumeBrightnessLayout.setVisibility(View.GONE);
        }
    };


     // 滑动改变声音大小

     // @param percent

    private void onVolumeSlide(float percent) {
        if (mVolume == -1) {
            mVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            if (mVolume < 0)
                mVolume = 0;

            // 显示
            mOperationBg.setImageResource(R.drawable.ic_volume_down_black_24dp);
            mVolumeBrightnessLayout.setVisibility(View.VISIBLE);
        }

        int index = (int) (percent * mMaxVolume) + mVolume;
        if (index > mMaxVolume)
            index = mMaxVolume;
        else if (index < 0)
            index = 0;

        // 变更声音
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, index, 0);

        // 变更进度条
        ViewGroup.LayoutParams lp = mOperationPercent.getLayoutParams();
      //  lp.width = findViewById(R.id.operation_full).getLayoutParams().width* index / mMaxVolume;
        mOperationPercent.setLayoutParams(lp);
    }


     // 滑动改变亮度

     //@param percent

    private void onBrightnessSlide(float percent) {
        if (mBrightness < 0) {
            mBrightness = getWindow().getAttributes().screenBrightness;
            if (mBrightness <= 0.00f)
                mBrightness = 0.50f;
            if (mBrightness < 0.01f)
                mBrightness = 0.01f;

            // 显示
            mOperationBg.setImageResource(R.drawable.ic_brightness_6_black_24dp);
            mVolumeBrightnessLayout.setVisibility(View.VISIBLE);
        }
        WindowManager.LayoutParams lpa = getWindow().getAttributes();
        lpa.screenBrightness = mBrightness + percent;
        if (lpa.screenBrightness > 1.0f)
            lpa.screenBrightness = 1.0f;
        else if (lpa.screenBrightness < 0.01f)
            lpa.screenBrightness = 0.01f;
        getWindow().setAttributes(lpa);

        ViewGroup.LayoutParams lp = mOperationPercent.getLayoutParams();
     //  lp.width = (int) (findViewById(R.id.operation_full).getLayoutParams().width * lpa.screenBrightness);
        mOperationPercent.setLayoutParams(lp);
    }


}
