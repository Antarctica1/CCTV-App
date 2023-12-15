package com.example.raspberrypi_project;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import java.io.IOException;

public class cctvActivity extends AppCompatActivity {
    WebView webView;
    WebSettings webSettings;
    Button  call;
    
    Thread thread;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cctv_activity);

        Intent intent = getIntent();
        String ip = intent.getStringExtra("ip");


        call = (Button) findViewById(R.id.callText);


        webView = (WebView)findViewById(R.id.cctvWeb);
        webView.setWebViewClient(new WebViewClient());

        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);

        webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        thread = new Thread();

        //webView.loadUrl("https://youtube.com"); //url 기능

        //카메라 기능 (여기에 코딩)
       webView.loadData("<html><head><style type='text/css'>body{margin:auto auto;text-align:center;} " +
                        "img{width:100%25;} div{overflow: hidden;} </style></head>" +
                        "<body><div><img src='http://"+ip+":8091/></div></body></html>",
                "text/html", "UTF-8");



        webView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    webView.reload();
                }
                return true;
            }
        }); // WebView 터치 시 새로고침



        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(cctvActivity.this);
                builder.setTitle("신고");
                builder.setMessage("신고하시겠습니까?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:112"));
                        startActivity(intent);
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        }); // 신고하기 버튼 클릭 => 112 전화 걸기

    }

    @Override
    public void onBackPressed() { // 뒤로 가기 버튼을 누를 시 cctvActivity -> MainActivity로 화면 전환
        Intent intent = new Intent(cctvActivity.this, MainActivity.class);
        startActivity(intent);
        super.onBackPressed();
    }
}
