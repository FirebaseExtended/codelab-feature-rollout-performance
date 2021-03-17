package com.google.firebase.example.fireeats;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final FirebaseRemoteConfig mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
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

        if (!seasonalImageUrl.isEmpty()) {
            Glide.with(SplashScreenActivity.this.getApplicationContext())
                .asBitmap()
                .load(seasonalImageUrl)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .listener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(
                        @Nullable GlideException e,
                        Object model, Target<Bitmap> target,
                        boolean isFirstResource) {
                        goToMainPage();
                        return true;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, Object model,
                        Target<Bitmap> target, DataSource dataSource,
                        boolean isFirstResource) {
                        goToMainPage();
                        return true;
                    }
                })
                .preload();
        } else {
            goToMainPage();
        }
    }

    private void goToMainPage() {
        startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
        finish();
    }
}