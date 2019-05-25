package com.yk.big_picture;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.yk.big_picture_library.BigView;

import java.io.FileNotFoundException;

public class MainActivity extends AppCompatActivity {

    /**
     * 测试长图
     */
    private final String BIG_IMAGE_PAHT = Environment.getExternalStorageDirectory() + "/big_img.png";

    private String TAG = this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BigView bigView=findViewById(R.id.bv_img);

        try {
            bigView.setImage(BIG_IMAGE_PAHT);
        } catch (FileNotFoundException e) {
            Log.e(TAG,e.getMessage());
        }
    }
}
