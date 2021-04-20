/**
 * Copyright 2021 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.firebase.example.fireeats;

import android.graphics.Bitmap;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.signature.ObjectKey;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

/**
 * An example full-screen activity that shows and hides the system UI (i.e. status bar and
 * navigation/system bar) with user interaction.
 */
public class SplashScreenActivity extends AppCompatActivity {

    private static final String TAG = "SplashScreenActivity";
    private static final String SEASONAL_IMAGE_URL_RC_FLAG = "seasonal_image_url";

    // TODO: Initialize splash_screen_trace

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final FirebaseRemoteConfig mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
            // We only set this value low for this codelab example.
            // See https://firebase.google.com/docs/remote-config/get-started?platform=android#throttling
            .setMinimumFetchIntervalInSeconds(1)
            .build();
        mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings);
        mFirebaseRemoteConfig.fetchAndActivate()
            .addOnCompleteListener(this, new OnCompleteListener<Boolean>() {
                @Override
                public void onComplete(@NonNull Task<Boolean> task) {
                    if (task.isSuccessful()) {
                        boolean updated = task.getResult();
                        Log.d(TAG, "Config params updated: " + updated);
                    } else {
                        Log.e(TAG, "Failed to fetch RC config");
                    }
                    executeTasksBasedOnRC(mFirebaseRemoteConfig);
                }
            });
    }

    private void executeTasksBasedOnRC(FirebaseRemoteConfig rcConfig) {
        String seasonalImageUrl = rcConfig.getString(SEASONAL_IMAGE_URL_RC_FLAG);
        Log.d(TAG, SEASONAL_IMAGE_URL_RC_FLAG + ": " + seasonalImageUrl);

        // TODO: Add a custom attribute "seasonal_image_url_attribute" to splash_screen_trace

        if (!seasonalImageUrl.isEmpty()) {
            // TODO: Start the splash_seasonal_image_processing here

            // TODO: Add a custom attribute "seasonal_image_url_attribute" to splash_seasonal_image_processing

            Glide.with(SplashScreenActivity.this.getApplicationContext())
                .asBitmap()
                .load(seasonalImageUrl)
                .signature(new ObjectKey(Utils.getCacheUUID()))
                .listener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(
                        @Nullable GlideException e,
                        Object model, Target<Bitmap> target,
                        boolean isFirstResource) {
                        // TODO: Stop the splash_seasonal_image_processing here

                        launchMainActivity();
                        return true;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, Object model,
                        Target<Bitmap> target, DataSource dataSource,
                        boolean isFirstResource) {
                        // TODO: Stop the splash_seasonal_image_processing here

                        launchMainActivity();
                        return true;
                    }
                })
                .preload();
        } else {
            launchMainActivity();
        }
    }

    private void launchMainActivity() {
        startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // TODO: Stop the splash_screen_trace here
    }
}
