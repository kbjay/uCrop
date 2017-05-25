package com.yalantis.ucrop.custom;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;

import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.UCropActivity;

import java.io.File;

import static com.yalantis.ucrop.UCrop.REQUEST_CROP;

public class CropAct extends AppCompatActivity {

    private static final int CALL_CAMERA = 100;
    private static final int CALL_PIC_REPO = 101;
    private static final String ANSWER_REPO = Environment.getExternalStorageDirectory() + "/" + "zyb_answer_pic";

    private Uri mSource;
    private AlertDialog alertDialog;
    private int mType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        mType = intent.getIntExtra(CustomCrop.CROP_TYPE,CustomCrop.USE_IN_616);
    }

    @Override
    protected void onResume() {
        super.onResume();
        String[] items = {"拍照", "图库", "取消"};
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        callCamera();
                        alertDialog.dismiss();
                        break;
                    case 1:
                        callPicRes();
                        alertDialog.dismiss();
                        break;
                    default:
                        if ( alertDialog!= null) {
                            alertDialog.dismiss();
                            CropAct.this.finish();
                        }
                        break;
                }
            }
        }).setCancelable(false);

        alertDialog = builder.create();
        alertDialog.show();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case CALL_CAMERA:
                mSource = Uri.fromFile(new File(ANSWER_REPO));
                startCrop();
                break;
            case CALL_PIC_REPO:
                if (data == null) {
                    break;
                }
                Uri uri = data.getData();
                Cursor cursor = getContentResolver().query(uri, null, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
                    mSource = Uri.fromFile(new File(path));
                    startCrop();
                }
                break;
            case REQUEST_CROP:
                handleDestPic(data);
                break;
            default:
                break;
        }

    }

    private void handleDestPic(Intent data) {

        Uri output = UCrop.getOutput(data);
        clearPicCache(output.getPath());
        setResult(RESULT_OK, new Intent().putExtra(CustomCrop.DEST_URI, output));
        finish();
    }

    private void clearPicCache(String path) {
        File file = new File(getCacheDir() + "/dest");
        if(file.isDirectory()){
            File[] files = file.listFiles();
            for (int i = 0; i < files.length; i++) {
                if(path.equals(files[i].getPath())){
                    continue;
                }
                files[i].delete();
            }
        }
    }

    private void startCrop() {
        File file = new File(getCacheDir() + "/dest");
        if (!file.exists() && !file.isDirectory()){
            file.mkdir();
        }

        String destPath = (Math.random()+10000)+".png";

        Uri destUri = Uri.fromFile(new File(getCacheDir() + "/dest", destPath));
        UCrop mUcrop = getUCrop(destUri);
        mUcrop.start(this);
    }

    private UCrop getUCrop(Uri destUri) {
        if(mSource==null){
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


    private void callPicRes() {
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                "image/*");
        startActivityForResult(intent, CALL_PIC_REPO);
    }

    private void callCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri
                .fromFile(new File(ANSWER_REPO)));
        startActivityForResult(intent, CALL_CAMERA);
    }

    @Override
    protected void onDestroy() {
        if (alertDialog!=null){
            alertDialog.dismiss();
        }
        super.onDestroy();
    }
}
