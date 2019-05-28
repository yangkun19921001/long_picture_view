package com.yk.big_picture_library;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.SystemClock;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import static android.content.ContentValues.TAG;

/**
 * 获取服务器图片工具类
 */
public class HttpBitmapUtils {

    private static DownCallListener downCallListener;
    private static DownLoadAsyncTask mDownLoadAsyncTask;
    private static final String ALBUM_PATH = Environment.getExternalStorageDirectory() + "/BigView/";


    public Bitmap downBitmap(String urlPath) {
        Bitmap bitmap = null;
        try {
            URL url = new URL(urlPath);
            HttpURLConnection httpc = (HttpURLConnection) url.openConnection();
            httpc.setConnectTimeout(60 * 1000);
            httpc.setReadTimeout(60 * 1000);
            if (httpc.getResponseCode() == 200) {
                InputStream in = httpc.getInputStream();
                //BitmapFactory->不同方式读取图片进入程序中
                bitmap = BitmapFactory.decodeStream(in);
            }
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return bitmap;
    }


    public static void loadImage(String url, DownCallListener down) {
        downCallListener = down;
        mDownLoadAsyncTask = new DownLoadAsyncTask();
        mDownLoadAsyncTask.execute(url);
    }

    public void cancel() {
        if (mDownLoadAsyncTask != null && mDownLoadAsyncTask.getStatus() == AsyncTask.Status.RUNNING) {
            mDownLoadAsyncTask.cancel(true);
        }
    }


    /**
     * 使用 异步任务下载图片 并显示进度
     * 参数1 String  就是 doInbackground() 的参数类型 我们的代码就在这里写 系统默认调用
     * 参数2 Integer      onProgressUpdate() 的参数类型 系统不会自动调用此方法 手动调用：publishProgress()
     * 参数3 Bitmap       doInbackground() 的返回值类型 也是 onPostExecute() 的参数类型
     */
   static class DownLoadAsyncTask extends AsyncTask<String, Integer, String> {


        /**
         * 在 doInbackground() 执行前，系统自动调用 在主线程运行
         */
        @Override
        protected void onPreExecute() {
            Log.i(TAG, "开始下载----");
            if (downCallListener != null) {
                downCallListener.onPreExecute();
            }
        }

        /**
         * 不在主线程 执行
         *
         * @param strUrl url
         * @return 位图
         */
        @Override
        protected String doInBackground(String... strUrl) {
            HttpURLConnection connection = null;
            try {
                URL url = new URL(strUrl[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.setConnectTimeout(20000);
                int code = connection.getResponseCode();
                if (code == 200) {
                    //为了显示进度条这里使用 字节数组输出流
                    InputStream is = connection.getInputStream();
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    int length = -1;
                    int progress = 0;    //进度
                    int count = connection.getContentLength();  //获取内容产固定
                    byte[] bs = new byte[1024];
                    while ((length = is.read(bs)) != -1) {
                        progress += length;    //进度累加
                        if (count == 0) {
                            publishProgress(-1);
                        } else {
                            //进度值改变通知
                            publishProgress((int) ((float) progress / count * 100));
                        }

                        Log.d("Tag", "=任务是否取消：" + isCancelled() + "=======任务进度：" + (int) ((float) progress / count * 100) + "%");
                        if (isCancelled()) {//如果取消了任务 就不执行
                            return null;
                        }

                        bos.write(bs, 0, length);
                    }
                    Log.d("Tag", "=========任务完成");
                    return saveFile(BitmapFactory.decodeByteArray(bos.toByteArray(), 0, bos.size()));


                }
            } catch (Exception e) {
                e.printStackTrace();
                if (downCallListener != null) {
                    downCallListener.onLoadError(e);
                }
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
            return null;
        }

        /**
         * 在 doInbackground() 执行后 系统自动调用  在主线程运行
         *
         * @param filePathName 位图
         */
        @Override
        protected void onPostExecute(String filePathName) {
            Log.d("Tag", "===============任务是否取消：" + isCancelled());
            if (downCallListener != null) {
                downCallListener.onPostExecute(filePathName);
            }
        }

        /**
         * 系统不会自动调用 使用 publishProgress() 调用
         * 在主线程执行
         *
         * @param values
         */
        @Override
        protected void onProgressUpdate(Integer... values) {
            int progress = values[0];       //进度值
            if (progress != -1) {
                Log.d(TAG, "下载的进度--》" + values + "");
                if (downCallListener != null) {
                    downCallListener.onProgressUpdate(progress);
                }
            }
        }
    }

    /**
     * 保存文件
     * @param bm
     * @throws IOException
     */
    public static String saveFile(Bitmap bm) throws IOException {
        File dirFile = new File(ALBUM_PATH);
        if(!dirFile.exists()){
            dirFile.mkdir();
        }
        File myCaptureFile = new File(ALBUM_PATH + SystemClock.currentThreadTimeMillis() +".png");
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
        bm.compress(Bitmap.CompressFormat.JPEG, 80, bos);
        bos.flush();
        bos.close();
        return myCaptureFile.getAbsolutePath();
    }


    public interface DownCallListener {

        /**
         * 开始下载
         */
        void onPreExecute();

        /**
         * 下载进度
         */
        void onProgressUpdate(int progress);

        /**
         * 下载完成
         */

        void onPostExecute(String bitmap);

        /**
         * 加载失败
         * @param e
         */
        void onLoadError(Exception e);
    }

}

