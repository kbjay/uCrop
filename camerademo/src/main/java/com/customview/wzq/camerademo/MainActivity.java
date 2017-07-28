package com.customview.wzq.camerademo;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.yalantis.ucrop.custom.CustomCarameCrop;
/**
*@date  2017/7/10
*@author  kb_jay
*@Desctiption  定义一个空的act作为传送信息的门
*/
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void start(View view) {
        CustomCarameCrop.startCrop(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK){
            if(requestCode==CustomCarameCrop.REQUEST_CODE){
                Uri parcelableExtra = (Uri) data.getParcelableExtra(CustomCarameCrop.DEST_URI);
                Log.d("kb_jay",parcelableExtra.toString());
            }
        }
    }
}
