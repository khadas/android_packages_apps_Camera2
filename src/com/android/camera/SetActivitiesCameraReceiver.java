/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.camera;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera.CameraInfo;
import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;

import com.android.camera.debug.Log;

// Enable or disable camera-related activities based on "camera hal" in every boot up.
// This receiver runs when BOOT_COMPLETED intent is received.
public class SetActivitiesCameraReceiver extends BroadcastReceiver {
    private static final Log.Tag TAG = new Log.Tag("SetActivitiesCameraReceiver");
    private static final boolean CHECK_BACK_CAMERA_ONLY = false;
    private static final boolean DEBUG = false;
    private static final String ACTIVITIES[] = {
        "com.android.camera.CameraLauncher",
    };

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive intent=" + intent);
        String action = intent.getAction();
        // Disable camera-related activities if there is no camera.
        int component_state = (CHECK_BACK_CAMERA_ONLY
            ? hasBackCamera(context) : hasCamera(context))
            ? PackageManager.COMPONENT_ENABLED_STATE_ENABLED
            : PackageManager.COMPONENT_ENABLED_STATE_DISABLED;

        Log.i(TAG, "component state is " + component_state);

        if (Intent.ACTION_BOOT_COMPLETED.equals(action)) {
            for (int i = 0; i < ACTIVITIES.length; i++) {
                setComponent(context, ACTIVITIES[i],
                    component_state);
            }
        }

        if(UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)){
            UsbDevice device = (UsbDevice)intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
            if (isUsbCamera(device)) {
                Log.i(TAG, "usb camera plug in, enable all camera activities!");
                for (int i = 0; i < ACTIVITIES.length; i++) {
                    //enableComponent
                    setComponent(context, ACTIVITIES[i],
                        PackageManager.COMPONENT_ENABLED_STATE_ENABLED);
                }
            }
        } else if(UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)){
            UsbDevice device = (UsbDevice)intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
            if (isUsbCamera(device)) {
               Log.i(TAG, "usb camera plug out, disable all camera activities!");
                for (int i = 0; i < ACTIVITIES.length; i++) {
                    //disableComponent
                    setComponent(context, ACTIVITIES[i],
                        PackageManager.COMPONENT_ENABLED_STATE_DISABLED);
                }
             }
         }
    }

    private boolean hasCamera(Context context) {
        PackageManager pm = context.getPackageManager();
        return pm.hasSystemFeature(PackageManager.FEATURE_CAMERA)
            || pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT)
            || pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_EXTERNAL);
    }

    private boolean hasBackCamera(Context context) {
        PackageManager pm = context.getPackageManager();
        return pm.hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    private void setComponent(Context context, String klass, final int enabledState) {
        ComponentName name = new ComponentName(context, klass);
        PackageManager pm = context.getPackageManager();

        // We need the DONT_KILL_APP flag, otherwise we will be killed
        // immediately because we are in the same app.
        pm.setComponentEnabledSetting(name,
            enabledState,
            PackageManager.DONT_KILL_APP);
    }

    public boolean isUsbCamera(UsbDevice device) {
        int count = device.getInterfaceCount();
        if (DEBUG) {
            for (int i = 0; i < count; i++) {
                UsbInterface intf = device.getInterface(i);
                Log.i(TAG, "isCamera UsbInterface:" + intf);
            }
        }

        for (int i = 0; i < count; i++) {
            UsbInterface intf = device.getInterface(i);
            if (intf.getInterfaceClass() == UsbConstants.USB_CLASS_VIDEO) {
                return true;
            }
        }
        return false;
    }
}
