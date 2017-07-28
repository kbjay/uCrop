package com.yalantis.ucrop.custom;

import android.app.Activity;
import android.content.Intent;

/**
 * 创建时间：2017/7/10
 * 作者：kb_jay
 * 功能描述：crop  的入口出
 */

public class CustomCarameCrop {

    public static final String DEST_URI = "dest_uri";
    public static final int REQUEST_CODE = 120;

    public static void startCrop(Activity act) {
        Intent intent = new Intent(act, EmptyAct.class);
        act.startActivityForResult(intent,CustomCarameCrop.REQUEST_CODE);
    }
}
