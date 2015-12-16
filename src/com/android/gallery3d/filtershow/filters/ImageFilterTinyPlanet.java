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

package com.android.gallery3d.filtershow.filters;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;

import com.android.gallery3d.app.Log;
import com.android.gallery3d.filtershow.presets.ImagePreset;

/**
 * An image filter which creates a tiny planet projection.
 */
public class ImageFilterTinyPlanet extends ImageFilter {
    private float mAngle = 0;

    private static final String TAG = ImageFilterTinyPlanet.class.getSimpleName();
    public static final String GOOGLE_PANO_NAMESPACE = "http://ns.google.com/photos/1.0/panorama/";

    public static final String CROPPED_AREA_IMAGE_WIDTH_PIXELS =
            "CroppedAreaImageWidthPixels";
    public static final String CROPPED_AREA_IMAGE_HEIGHT_PIXELS =
            "CroppedAreaImageHeightPixels";
    public static final String CROPPED_AREA_FULL_PANO_WIDTH_PIXELS =
            "FullPanoWidthPixels";
    public static final String CROPPED_AREA_FULL_PANO_HEIGHT_PIXELS =
            "FullPanoHeightPixels";
    public static final String CROPPED_AREA_LEFT =
            "CroppedAreaLeftPixels";
    public static final String CROPPED_AREA_TOP =
            "CroppedAreaTopPixels";

    public ImageFilterTinyPlanet() {
        setFilterType(TYPE_TINYPLANET);
        mName = "TinyPlanet";

        mMinParameter = 10;
        mMaxParameter = 60;
        mDefaultParameter = 20;
        mPreviewParameter = 20;
        mParameter = 20;
        mAngle = 0;
    }

    public void setAngle(float angle) {
        mAngle = angle;
    }

    public float getAngle() {
        return mAngle;
    }

    public boolean isNil() {
        // TinyPlanet always has an effect
        return false;
    }

    native protected void nativeApplyFilter(
            Bitmap bitmapIn, int width, int height, Bitmap bitmapOut, int outSize, float scale,
            float angle);

    @Override
    public Bitmap apply(Bitmap bitmapIn, float scaleFactor, boolean highQuality) {
        int w = bitmapIn.getWidth();
        int h = bitmapIn.getHeight();
        int outputSize = (int) (w / 2f);
        ImagePreset preset = getImagePreset();

        if (preset != null) {
//            XMPMeta xmp = preset.getImageLoader().getXmpObject();
//            // Do nothing, just use bitmapIn as is if we don't have XMP.
//            if(xmp != null) {
//              bitmapIn = applyXmp(bitmapIn, xmp, w);
//            }
        }

        Bitmap mBitmapOut = null;
        while (mBitmapOut == null) {
            try {
                mBitmapOut = Bitmap.createBitmap(
                        outputSize, outputSize, Bitmap.Config.ARGB_8888);
            } catch (java.lang.OutOfMemoryError e) {
                System.gc();
                outputSize /= 2;
                Log.v(TAG, "No memory to create Full Tiny Planet create half");
            }
        }
        nativeApplyFilter(bitmapIn, bitmapIn.getWidth(), bitmapIn.getHeight(), mBitmapOut,
                outputSize, mParameter / 100f, mAngle);
        return mBitmapOut;
    }
}
