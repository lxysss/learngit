package com.example.asus.lxymediaplayer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

public class LxyInternetActivity extends AppCompatActivity implements View.OnClickListener{
    private ImageButton youkuView,bilibiliView,aiqiyiView,souhuView;
    private Button button;
    private Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lxy_internet);
        youkuView=findViewById(R.id.youku);
        youkuView.setOnClickListener(this);

        bilibiliView=findViewById(R.id.bilibili);
        bilibiliView.setOnClickListener(this);

        aiqiyiView=findViewById(R.id.aiqiyi);
        aiqiyiView.setOnClickListener(this);

        souhuView=findViewById(R.id.sougou);
        souhuView.setOnClickListener(this);

        button=findViewById(R.id.internet_button);
        button.setOnClickListener(this);

        intent=new Intent(LxyInternetActivity.this,LxyWebActivity.class);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.youku:
                intent.putExtra("url","http://www.youku.com/");
                startActivity(intent);
                break;
            case R.id.bilibili:
                intent.putExtra("url","https://www.bilibili.com/");
                startActivity(intent);
                break;
            case R.id.aiqiyi:
                intent.putExtra("url","http://www.iqiyi.com/");
                startActivity(intent);
                break;
            case R.id.sougou:
                intent.putExtra("url","https://tv.sohu.com/");
                startActivity(intent);
                break;
            case R.id.internet_button:
                intent.putExtra("url","https://www.baidu.com/");
                startActivity(intent);
                break;
            default:
                break;
        }
    }
}
