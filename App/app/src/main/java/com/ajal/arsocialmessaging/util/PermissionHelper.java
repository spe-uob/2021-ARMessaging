/*
 * Copyright 2017 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ajal.arsocialmessaging.util;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

/** Helper to ask camera permission.
 * Needs to be a singleton so that it doesn't close the app if it is asking for permissions for the first time */
public final class PermissionHelper {

  private static final int PERMISSIONS_CODE = 0;
  private static final String REQUIRED_PERMISSIONS[] = {
          Manifest.permission.WRITE_EXTERNAL_STORAGE,
          Manifest.permission.CAMERA,
          Manifest.permission.ACCESS_FINE_LOCATION,
          Manifest.permission.ACCESS_COARSE_LOCATION,
          Manifest.permission.INTERNET
  };
  private boolean permissionsRequested = false;


  /** Check to see we have the necessary permissions for this app. */
  public static boolean hasPermissions(Activity activity) {
    for (String p : REQUIRED_PERMISSIONS) {
      if (ContextCompat.checkSelfPermission(activity, p) != PackageManager.PERMISSION_GRANTED) {
        return false;
      }
    }
    return true;
  }

  /** Check to see we have the necessary permissions for this app, and ask for them if we don't. */
  public static void requestPermissions(Activity activity) {
    ActivityCompat.requestPermissions(activity, REQUIRED_PERMISSIONS,
            PERMISSIONS_CODE);
  }

  /** Check to see if we need to show the rationale for this permission. */
  public static boolean shouldShowRequestPermissionRationale(Activity activity) {
    for (String p : REQUIRED_PERMISSIONS) {
      if (ActivityCompat.shouldShowRequestPermissionRationale(activity, p)) {
        return true;
      }
    }
    return false;
  }

  public static void requestPermissionsIfDenied(Activity activity){
    if (shouldShowRequestPermissionRationale(activity)) {
      launchPermissionSettings(activity);
      return;
    }
  }

  /** Launch Application Setting to grant permission. */
  public static void launchPermissionSettings(Activity activity) {
    Intent intent = new Intent();
    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
    intent.setData(Uri.fromParts("package", activity.getPackageName(), null));
    activity.startActivity(intent);
  }
}
