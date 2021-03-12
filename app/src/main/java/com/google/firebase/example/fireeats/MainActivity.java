/**
 * Copyright 2017 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.google.firebase.example.fireeats;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.ColorUtils;
import androidx.lifecycle.ViewModelProvider;
import androidx.palette.graphics.Palette;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.example.fireeats.viewmodel.MainActivityViewModel;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

public class MainActivity extends AppCompatActivity implements
    View.OnClickListener,
    FilterDialogFragment.FilterListener {

    private static final String TAG = "MainActivity";

    private static final int RC_SIGN_IN = 9001;

    private static final int LIMIT = 50;

    private static final String SEASONAL_IMAGE_URL_RC_FLAG = "seasonal_image_url";

    private Toolbar mToolbar;
    private TextView mCurrentSearchView;
    private TextView mCurrentSortByView;
    private ViewGroup mDefaultEmptyView;
    private ViewGroup mSeasonalEmptyView;
    private TextView mSeasonalEmptyTextView;


    private FirebaseRemoteConfig mFirebaseRemoteConfig;

    private FilterDialogFragment mFilterDialog;

    private MainActivityViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        mCurrentSearchView = findViewById(R.id.text_current_search);
        mCurrentSortByView = findViewById(R.id.text_current_sort_by);
        mDefaultEmptyView = findViewById(R.id.default_empty_view);
        mSeasonalEmptyView = findViewById(R.id.seasonal_empty_view);
        mSeasonalEmptyTextView = findViewById(R.id.seasonal_empty_text_view);

        findViewById(R.id.filter_bar).setOnClickListener(this);
        findViewById(R.id.button_clear_filter).setOnClickListener(this);

        // View model
        mViewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);

        loadEmptyImage();

        // Filter Dialog
        mFilterDialog = new FilterDialogFragment();
    }

    private void loadEmptyImage() {
        // Load fetched sign in image url
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        String seasonalImageUrl = mFirebaseRemoteConfig.getString(SEASONAL_IMAGE_URL_RC_FLAG);
        Log.d(TAG, SEASONAL_IMAGE_URL_RC_FLAG + ": " + seasonalImageUrl);

        if (!seasonalImageUrl.isEmpty()) {
            mDefaultEmptyView.setVisibility(View.GONE);
            mSeasonalEmptyView.setVisibility(View.VISIBLE);

            ImageView imageView = findViewById(R.id.seasonal_empty_image_view);
            Glide.with(getApplicationContext())
                .asBitmap()
                .load(seasonalImageUrl)
                .listener(new SeasonalImageListener())
                .into(imageView);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        // Apply filters
        onFilter(mViewModel.getFilters());
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private void onAddItemsClicked() {
    }

    @Override
    public void onFilter(Filters filters) {
        // Set header
        mCurrentSearchView.setText(Html.fromHtml(filters.getSearchDescription(this)));
        mCurrentSortByView.setText(filters.getOrderDescription(this));

        // Save filters
        mViewModel.setFilters(filters);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add_items:
                onAddItemsClicked();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.filter_bar:
                onFilterClicked();
                break;
            case R.id.button_clear_filter:
                onClearFilterClicked();
        }
    }

    public void onFilterClicked() {
        // Show the dialog containing filter options
        mFilterDialog.show(getSupportFragmentManager(), FilterDialogFragment.TAG);
    }

    public void onClearFilterClicked() {
        mFilterDialog.resetFilters();

        onFilter(Filters.getDefault());
    }

    class SeasonalImageListener implements RequestListener<Bitmap> {
        @Override
        public boolean onLoadFailed(
            @Nullable GlideException e,
            Object model, Target<Bitmap> target, boolean isFirstResource) {
            return false;
        }

        @Override
        public boolean onResourceReady(Bitmap resource, Object model,
            Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
            if (resource != null) {
                Palette p = Palette.from(resource).generate();
                GradientDrawable drawable = (GradientDrawable) mSeasonalEmptyTextView.getBackground();
                drawable.setColor(
                    ColorUtils.setAlphaComponent(p.getDarkMutedSwatch().getRgb(), 128));
                mSeasonalEmptyTextView.setTextColor(p.getDarkMutedSwatch().getBodyTextColor());
            }
            return false;
        }
    }
}
