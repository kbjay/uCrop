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
import android.util.Log;

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
        mType = intent.getIntExtra(CustomCrop.CROP_TYPE, CustomCrop.USE_IN_616);
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        // finish();
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
                        if (alertDialog != null) {
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
        Log.d("kb_jay:", resultCode + "");
        if (resultCode == RESULT_CANCELED) {
            finish();
        }
        if (resultCode == RESULT_OK) {
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

    }

    private void handleDestPic(Intent data) {

        Uri output = UCrop.getOutput(data);
        clearPicCache(output.getPath());
        setResult(RESULT_OK, new Intent().putExtra(CustomCrop.DEST_URI, output));
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

/*        //初始化UCrop配置
        UCrop.Options options = new UCrop.Options();
        //设置裁剪图片可操作的手势
        options.setAllowedGestures(UCropActivity.SCALE, UCropActivity.ROTATE, UCropActivity.ALL);
        //是否隐藏底部容器，默认显示
        options.setHideBottomControls(true);
        //设置toolbar颜色
        options.setToolbarColor(ActivityCompat.getColor(activity, R.color.colorPrimary));
        //设置状态栏颜色
        options.setStatusBarColor(ActivityCompat.getColor(activity, R.color.colorPrimary));
        //是否能调整裁剪框
        options.setFreeStyleCropEnabled(true);
        //UCrop配置
        uCrop.withOptions(options);
        //设置裁剪图片的宽高比，比如16：9
        uCrop.withAspectRatio(aspectRatioX, aspectRatioY);
        //uCrop.useSourceImageAspectRatio();*/
/*
//设置Toolbar标题
        void setToolbarTitle (@Nullable String text)
//设置裁剪的图片格式
        void setCompressionFormat (@NonNull Bitmap.CompressFormat format)
//设置裁剪的图片质量，取值0-100
        void setCompressionQuality ( @IntRange(from = 0) int compressQuality)
//设置最多缩放的比例尺
        void setMaxScaleMultiplier (
        @FloatRange(from = 1.0, fromInclusive = false) float maxScaleMultiplier)
//动画时间
        void setImageToCropBoundsAnimDuration ( @IntRange(from = 100) int durationMillis)
//设置图片压缩最大值
        void setMaxBitmapSize ( @IntRange(from = 100) int maxBitmapSize)
//是否显示椭圆裁剪框阴影
        void setOvalDimmedLayer ( boolean isOval)
//设置椭圆裁剪框阴影颜色
        void setDimmedLayerColor ( @ColorInt int color)
//是否显示裁剪框
        void setShowCropFrame ( boolean show)
//设置裁剪框边的宽度
        void setCropFrameStrokeWidth ( @IntRange(from = 0) int width)
//是否显示裁剪框网格
        void setShowCropGrid ( boolean show)
//设置裁剪框网格颜色
        void setCropGridColor ( @ColorInt int color)
//设置裁剪框网格宽
        void setCropGridStrokeWidth ( @IntRange(from = 0) int width)*/

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
        if (alertDialog != null) {
            alertDialog.dismiss();
        }
        super.onDestroy();
    }
}
