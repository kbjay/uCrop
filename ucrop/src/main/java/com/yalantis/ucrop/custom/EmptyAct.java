package com.yalantis.ucrop.custom;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.UCropActivity;

import java.io.File;

/**
 * 创建时间：2017/7/10
 * 作者：kb_jay
 * 功能描述：
 */

public class EmptyAct extends AppCompatActivity {
    private final int CALL_CAMERA = 130;
    private static final String ANSWER_REPO = Environment.getExternalStorageDirectory() + "/" + "zyb_camera_pic";
    private Uri mSource;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        callCamera();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void callCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri
                .fromFile(new File(ANSWER_REPO)));
        startActivityForResult(intent, CALL_CAMERA);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("kb_jay", requestCode + ""+resultCode);
        if (resultCode == RESULT_CANCELED) {
            finish();
            return ;
        }
        if (requestCode == CALL_CAMERA) {
            startCrop();
        } else if (requestCode == UCrop.REQUEST_CROP) {
            handleDestPic(data);
        }
    }

    private void handleDestPic(Intent data) {

        Uri output = UCrop.getOutput(data);
        clearPicCache(output.getPath());
        setResult(RESULT_OK, new Intent().putExtra(CustomCarameCrop.DEST_URI, output));
        finish();
    }

    private void clearPicCache(String path) {
        File file = new File(getCacheDir() + "/dest");
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (int i = 0; i < files.length; i++) {
                if (path.equals(files[i].getPath())) {
                    continue;
                }
                files[i].delete();
            }
        }
    }

    private void startCrop() {
        mSource = Uri.fromFile(new File(ANSWER_REPO));
        File file = new File(getCacheDir() + "/dest");
        if (!file.exists() && !file.isDirectory()) {
            file.mkdir();
        }

        String destPath = (Math.random() + 10000) + ".png";

        Uri destUri = Uri.fromFile(new File(getCacheDir() + "/dest", destPath));
        UCrop mUcrop = getUCrop(destUri);
        mUcrop.start(this);
    }

    private UCrop getUCrop(Uri destUri) {
        if (mSource == null) {
            return null;
        }
        int width = getWindow().getWindowManager().getDefaultDisplay().getWidth();
        // TODO: 2017/5/25  这里客制化  (根据type调整)
        UCrop.Options options = new UCrop.Options();
        options.setCompressionFormat(Bitmap.CompressFormat.PNG);

        options.setToolbarColor(Color.RED);
        options.setAllowedGestures(UCropActivity.SCALE, UCropActivity.ROTATE, UCropActivity.ALL);

        options.setHideBottomControls(false);
        options.setFreeStyleCropEnabled(true);

        return UCrop.of(mSource, destUri).withAspectRatio(width, 200).withOptions(options);
    }
}
