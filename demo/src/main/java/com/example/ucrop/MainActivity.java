package com.example.ucrop;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.yalantis.ucrop.custom.CropAct;
import com.yalantis.ucrop.custom.CustomCrop;

public class MainActivity extends AppCompatActivity {

    private ImageView ivTest;

    //1:重写onActivityResult用于接受切好的图的uri
    //2:manifest 中声明act  两个
    //3:调用custom的CustomCrop.crop()  开始切图
    //
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ivTest = (ImageView) this.findViewById(R.id.iv_test);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==CustomCrop.CROP_REQUESE&&resultCode==RESULT_OK){
            Uri outputUri = (Uri) data.getParcelableExtra(CustomCrop.DEST_URI);
            handleUri(outputUri);
        }
    }

    private void handleUri(Uri destUri) {

        System.out.println(destUri.getPath());
        Picasso.with(this).load(destUri).into(ivTest);
    }

    public void choosePic(View view) {
        CustomCrop.crop(this,CustomCrop.USE_IN_616);
    }
}
