package com.android.camera;

import android.os.Bundle;
import com.android.camera.debug.Log;

import android.view.KeyEvent;

public class CameraVoiceActivity extends CameraActivity {

    private static final Log.Tag TAG = new Log.Tag("CameraVoiceActivity");
    Thread takePictureThread = new Thread(new Runnable() {
        @Override
        public void run() {
            try {
                Thread.sleep(6000);
            } catch (InterruptedException e) {
                Log.e(TAG, "sleep error");
            }
            Log.d(TAG, "mCurrentModule " + mCurrentModule);
            mCurrentModule.onShutterButtonClick();
        }
    });
    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        takePictureThread.start();
    }
}
