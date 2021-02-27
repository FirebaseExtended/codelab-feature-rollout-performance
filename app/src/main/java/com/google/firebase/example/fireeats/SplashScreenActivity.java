package com.google.firebase.example.fireeats;

import android.annotation.SuppressLint;

import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * An example full-screen activity that shows and hides the system UI (i.e. status bar and
 * navigation/system bar) with user interaction.
 */
public class SplashScreenActivity extends AppCompatActivity {

    private static final String TAG = "SplashScreenActivity";
    private static final String SIGN_IN_IMAGE_URL_RC_FLAG = "sign_in_image_url";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final FirebaseRemoteConfig mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
            .setMinimumFetchIntervalInSeconds(30)
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

                    String signInImageUrl = mFirebaseRemoteConfig
                        .getString(SIGN_IN_IMAGE_URL_RC_FLAG);
                    Log.d(TAG, SIGN_IN_IMAGE_URL_RC_FLAG + ": " + signInImageUrl);

                    if (!signInImageUrl.isEmpty()) {
                        Glide.with(SplashScreenActivity.this.getApplicationContext())
                            .load(signInImageUrl)
                            .listener(new RequestListener<Drawable>() {
                                @Override
                                public boolean onLoadFailed(
                                    @Nullable GlideException e,
                                    Object model, Target<Drawable> target,
                                    boolean isFirstResource) {
                                    startActivity(
                                        new Intent(SplashScreenActivity.this, MainActivity.class));
                                    finish();
                                    return true;
                                }

                                @Override
                                public boolean onResourceReady(Drawable resource, Object model,
                                    Target<Drawable> target, DataSource dataSource,
                                    boolean isFirstResource) {
                                    startActivity(
                                        new Intent(SplashScreenActivity.this, MainActivity.class));
                                    finish();
                                    return true;
                                }
                            })
                            .preload();
                    } else {
                        startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
                        finish();
                    }
                }
            });
    }
}