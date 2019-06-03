package com.yk.big_picture;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.yk.big_picture_library.BigView;
import com.yk.big_picture_library.LoadNetImageCallBack;

public class MainActivity extends AppCompatActivity {

    /**
     * 测试长图
     */
    private final String BIG_IMAGE_PAHT = Environment.getExternalStorageDirectory() + "/big_img.png";

    /**
     * 网络图片地址
     */
    private String URL = "https://file.digitaling.com/eImg/uimages/20170104/1483513576654699.jpg";
    private String URL_2 = "http://77fkxu.com1.z0.glb.clouddn.com/20180131/1517367951_59939.png";


    private String TAG = this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //如果测试过程中发生崩溃，请把日志告诉我，谢谢！
        CrashHandler.getInstance().init(getApplicationContext());
        setContentView(R.layout.activity_main);
        BigView bigView=findViewById(R.id.bv_img);
        //本地文件
/*        try {
            bigView.setImage(BIG_IMAGE_PAHT);
        } catch (FileNotFoundException e) {
            Log.e(TAG,e.getMessage());
        }*/
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("加载中....");
        progressDialog.setMax(100);
        //网络地址
        bigView.setNetUrl(URL_2, new LoadNetImageCallBack() {
            @Override
            public void onStart() {
                progressDialog.show();
            }

            @Override
            public void onLoadSucceed() {
                progressDialog.cancel();
            }

            @Override
            public void onLoadFail(Exception e) {
                Log.i(TAG,e.getMessage());
            }

            @Override
            public void onLoadProgress(int progress) {
                progressDialog.setProgress(progress);
            }
        });
    }
}
