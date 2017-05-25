	1：manifest中添加代码如下
 
        <activity
            android:name="com.yalantis.ucrop.UCropActivity"
            android:screenOrientation="portrait"
            android:theme="@style/Theme.AppCompat.NoActionBar" />

        <activity
            android:name="com.yalantis.ucrop.custom.CropAct"
            android:screenOrientation="portrait"
            android:theme="@style/ucrop_GetSourcePicStyle" />
	    
	 2：重写onActivityResult   请求码为CustomCrop.CROP_REQUESE  
	 3：获取的是切好的的图的uri
	  Uri outputUri = (Uri) data.getParcelableExtra(CustomCrop.DEST_URI);
	 4：使用时调用
	  CustomCrop.crop(this,CustomCrop.USE_IN_616);
