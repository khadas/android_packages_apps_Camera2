package com.android.camera;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import com.android.camera.debug.Log;

import android.app.Instrumentation;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

public class CameraImageActivity extends Activity {
    private static final Log.Tag TAG = new Log.Tag("CameraImageActivity");

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Intent intent = new Intent(getIntent());
        boolean take_photo_shot = false;
        if (isVoiceInteractionRoot()) {
            if (intent.hasExtra("com.google.assistant.extra.CAMERA_OPEN_ONLY")) {
                take_photo_shot = false;
            } else if (intent.hasExtra("android.intent.extra.CAMERA_OPEN_ONLY")) {
                take_photo_shot = false;
            } else {
                take_photo_shot = true;
            }
            Log.d(TAG,"start voice camera, take_photo:" + take_photo_shot);

        }
        if (take_photo_shot) {
            intent.setClass(this, CameraVoiceActivity.class);
        } else {
            intent.setClass(this, CameraActivity.class);
        }
        startActivity(intent);
     }
}