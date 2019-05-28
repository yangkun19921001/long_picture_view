package com.yk.big_picture_library;

public interface LoadNetImageCallBack {
    void onStart();

    void onLoadSucceed();

    void onLoadFail(Exception e);

    void onLoadProgress(int progress);
}
