package com.yalantis.ucrop.custom;

import android.app.Activity;
import android.content.Intent;

/**
 * Created by Administrator on 2017/5/25.
 */

public class CustomCrop {
    public static final String DEST_URI = "dest_uri";
    public static final int CROP_REQUESE = 300;
    public static final String CROP_TYPE = "crop_type";
    public static final int USE_IN_ACOUNT =1;
    public static final int USE_IN_616=2;


    public static void crop(Activity context,int type) {
        Intent intent= new Intent(context, CropAct.class);
        intent.putExtra(CROP_TYPE,type);
        context.startActivityForResult(intent,CROP_REQUESE);
    }
}
