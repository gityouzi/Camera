/*
 * Copyright (C) 2010 The Android Open Source Project
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

package com.android.gallery3d.ui;

import com.android.givallery3d.R;
import com.android.gallery3d.app.AbstractGalleryActivity;
import com.android.gallery3d.app.AlbumSetDataLoader;
import com.android.gallery3d.data.MediaObject;
import com.android.gallery3d.data.Path;
import com.android.gallery3d.ui.AlbumSetSlidingWindow.AlbumSetEntry;

public class AlbumSetSlotRenderer extends AbstractSlotRenderer {
    @SuppressWarnings("unused")
    private static final String TAG = "AlbumSetView";
    private static final int CACHE_SIZE = 96;
    private final int mPlaceholderColor;

    private final ColorTexture mWaitLoadingTexture;
    private final ResourceTexture mCameraOverlay;
    //jiangxd add
    private final ResourceTexture mSlotBgOverlay;
    //end
    private final AbstractGalleryActivity mActivity;
    private final SelectionManager mSelectionManager;
    protected final LabelSpec mLabelSpec;

    protected AlbumSetSlidingWindow mDataWindow;
    private SlotView mSlotView;

    private int mPressedIndex = -1;
    private boolean mAnimatePressedUp;
    private Path mHighlightItemPath = null;
    private boolean mInSelectionMode;

    public static class LabelSpec {
        public int labelBackgroundHeight;
        public int titleOffset;
        public int countOffset;
        public int titleFontSize;
        public int countFontSize;
        public int leftMargin;
        public int iconSize;
        public int titleRightMargin;
        public int backgroundColor;
        public int titleColor;
        public int countColor;
        public int borderSize;
    }

    public AlbumSetSlotRenderer(AbstractGalleryActivity activity,
            SelectionManager selectionManager,
            SlotView slotView, LabelSpec labelSpec, int placeholderColor) {
        super (activity);
        mActivity = activity;
        mSelectionManager = selectionManager;
        mSlotView = slotView;
        mLabelSpec = labelSpec;
        mPlaceholderColor = placeholderColor;

        mWaitLoadingTexture = new ColorTexture(mPlaceholderColor);
        mWaitLoadingTexture.setSize(1, 1);
        mCameraOverlay = new ResourceTexture(activity,
                R.drawable.ic_cameraalbum_overlay);
        //jiangxd add
        mSlotBgOverlay = new ResourceTexture(activity,
                R.drawable.ic_invision_bum_overlay);
    }

    public void setPressedIndex(int index) {
        if (mPressedIndex == index) return;
        mPressedIndex = index;
        mSlotView.invalidate();
    }

    public void setPressedUp() {
        if (mPressedIndex == -1) return;
        mAnimatePressedUp = true;
        mSlotView.invalidate();
    }

    public void setHighlightItemPath(Path path) {
        if (mHighlightItemPath == path) return;
        mHighlightItemPath = path;
        mSlotView.invalidate();
    }

    public void setModel(AlbumSetDataLoader model) {
        if (mDataWindow != null) {
            mDataWindow.setListener(null);
            mDataWindow = null;
            mSlotView.setSlotCount(0);
        }
        if (model != null) {
            mDataWindow = new AlbumSetSlidingWindow(
                    mActivity, model, mLabelSpec, CACHE_SIZE);
            mDataWindow.setListener(new MyCacheListener());
            mSlotView.setSlotCount(mDataWindow.size());
        }
    }

    private static Texture checkLabelTexture(Texture texture) {
        return ((texture instanceof UploadedTexture)
                && ((UploadedTexture) texture).isUploading())
                ? null
                : texture;
    }

    private static Texture checkContentTexture(Texture texture) {
        return ((texture instanceof TiledTexture)
                && !((TiledTexture) texture).isReady())
                ? null
                : texture;
    }

    @Override
    public int renderSlot(GLCanvas canvas, int index, int pass, int width, int height) {
        AlbumSetEntry entry = mDataWindow.get(index);
        int renderRequestFlags = 0;
        //jiangxd add SlotView BackGround
        renderRequestFlags |= renderSlotBg(canvas, index, entry, width, height);
        //jiangxd add end
        renderRequestFlags |= renderContent(canvas, entry, width, height);
        renderRequestFlags |= renderLabel(canvas, entry, width, height);
        renderRequestFlags |= renderOverlay(canvas, index, entry, width, height);
        return renderRequestFlags;
    }
    //jiangxd add
    protected int renderSlotBg(
            GLCanvas canvas, int index, AlbumSetEntry entry, int width, int height){
    	int renderRequestFlags = 0;
    	if(entry.album!=null){
    		mSlotBgOverlay.draw(canvas, 0, 0,width,height);
    	}
    	return renderRequestFlags;
    }
    //end
    protected int renderOverlay(
            GLCanvas canvas, int index, AlbumSetEntry entry, int width, int height) {
        int renderRequestFlags = 0;
        /* jiangxd  not to render cameraOverLay
        if (entry.album != null && entry.album.isCameraRoll()) {
            int uncoveredHeight = height - mLabelSpec.labelBackgroundHeight;
            int dim = uncoveredHeight / 2;
            mCameraOverlay.draw(canvas, (width - dim) / 2,
                    (uncoveredHeight - dim) / 2, dim, dim);
        }
        */
        if (mPressedIndex == index) {
            if (mAnimatePressedUp) {
                drawPressedUpFrame(canvas, width, height);
                renderRequestFlags |= SlotView.RENDER_MORE_FRAME;
                if (isPressedUpFrameFinished()) {
                    mAnimatePressedUp = false;
                    mPressedIndex = -1;
                }
            } else {
                drawPressedFrame(canvas, width, height);
            }
        } else if ((mHighlightItemPath != null) && (mHighlightItemPath == entry.setPath)) {
            drawSelectedFrame(canvas, width, height);
        } else if (mInSelectionMode && mSelectionManager.isItemSelected(entry.setPath)) {
            drawSelectedFrame(canvas, width, height);
        }
        return renderRequestFlags;
    }

    protected int renderContent(
            GLCanvas canvas, AlbumSetEntry entry, int width, int height) {
        int mContentGap = 10;
        int mPaddingLeft = 10;
        int mPaddingTop = 40;
        int mContentWidth = (width-mPaddingLeft*2-mContentGap)/2;
        int mContentHeight = (height-mPaddingTop-mContentGap-mContentGap)/2;
        int renderRequestFlags = 0;
        /* first bitmap on slot */
        Texture content0 = checkContentTexture(entry.content);
        if (content0 == null) {
            content0 = mWaitLoadingTexture;
            entry.isWaitLoadingDisplayed = true;
        } else if (entry.isWaitLoadingDisplayed) {
            entry.isWaitLoadingDisplayed = false;
            content0 = new FadeInTexture(mPlaceholderColor, entry.bitmapTexture);
            entry.content = content0;
        }
        drawContent(canvas, content0, mPaddingLeft,mPaddingTop,mContentWidth, mContentHeight, entry.rotation);
        if ((content0 instanceof FadeInTexture) &&
                ((FadeInTexture) content0).isAnimating()) {
            renderRequestFlags |= SlotView.RENDER_MORE_FRAME;
        }
        
        /* second bitmap on slot */
        Texture content1 = checkContentTexture(entry.contentAdd[0]);
        if(content1 == null){
        	content1 = mWaitLoadingTexture;
        	entry.isWaitLoadingDisplayed2 = true;
        }else if(entry.isWaitLoadingDisplayed2){
        	entry.isWaitLoadingDisplayed2 = false;
        	content1 = new FadeInTexture(mPlaceholderColor, entry.bitmapAddTexture[0]);
        	entry.contentAdd[0] = content1;
        }
        drawContent(canvas, content1, mPaddingLeft+mContentWidth+mContentGap,mPaddingTop,mContentWidth, mContentHeight, entry.rotation);

        if ((content1 instanceof FadeInTexture) &&
                ((FadeInTexture) content1).isAnimating()) {
            renderRequestFlags |= SlotView.RENDER_MORE_FRAME;
        }
        /* third bitmap on slot */
        Texture content2 = checkContentTexture(entry.contentAdd[1]);
        if(content2 == null){
        	content2 = mWaitLoadingTexture;
        	entry.isWaitLoadingDisplayed3 = true;
        }else if(entry.isWaitLoadingDisplayed3){
        	entry.isWaitLoadingDisplayed3 = false;
        	content2 = new FadeInTexture(mPlaceholderColor, entry.bitmapAddTexture[1]);
        	entry.contentAdd[1] = content2;
        }
        drawContent(canvas, content2, mPaddingLeft,mPaddingTop+mContentHeight+mContentGap,mContentWidth, mContentHeight, entry.rotation);
        if ((content2 instanceof FadeInTexture) &&
                ((FadeInTexture) content2).isAnimating()) {
            renderRequestFlags |= SlotView.RENDER_MORE_FRAME;
        }
        
        /* fourth bitmap on slot */
        Texture content3 = checkContentTexture(entry.contentAdd[2]);
        if(content3 == null){
        	content3 = mWaitLoadingTexture;
        	entry.isWaitLoadingDisplayed4 = true;
        }else if(entry.isWaitLoadingDisplayed4){
        	entry.isWaitLoadingDisplayed4 = false;
        	content3 = new FadeInTexture(mPlaceholderColor, entry.bitmapAddTexture[2]);
        	entry.contentAdd[2] = content3;
        }
        drawContent(canvas, content3, mPaddingLeft+mContentWidth+mContentGap,mPaddingTop+mContentHeight+mContentGap,mContentWidth, mContentHeight, entry.rotation);
        if ((content3 instanceof FadeInTexture) &&
                ((FadeInTexture) content3).isAnimating()) {
            renderRequestFlags |= SlotView.RENDER_MORE_FRAME;
        }
        
        return renderRequestFlags;
    }

    protected int renderLabel(
            GLCanvas canvas, AlbumSetEntry entry, int width, int height) {
        Texture content = checkLabelTexture(entry.labelTexture);
        if (content == null) {
            content = mWaitLoadingTexture;
        }
        int b = AlbumLabelMaker.getBorderSize();
        int h = mLabelSpec.labelBackgroundHeight;
        //content.draw(canvas, -b, height - h + b, width + b + b, h); jiangxd modify
        content.draw(canvas, 3, 0, width -3*2 + b, h);
        return 0;
    }

    @Override
    public void prepareDrawing() {
        mInSelectionMode = mSelectionManager.inSelectionMode();
    }

    private class MyCacheListener implements AlbumSetSlidingWindow.Listener {

        @Override
        public void onSizeChanged(int size) {
            mSlotView.setSlotCount(size);
        }

        @Override
        public void onContentChanged() {
            mSlotView.invalidate();
        }
    }

    public void pause() {
        mDataWindow.pause();
    }

    public void resume() {
        mDataWindow.resume();
    }

    @Override
    public void onVisibleRangeChanged(int visibleStart, int visibleEnd) {
        if (mDataWindow != null) {
            mDataWindow.setActiveWindow(visibleStart, visibleEnd);
        }
    }

    @Override
    public void onSlotSizeChanged(int width, int height) {
        if (mDataWindow != null) {
            mDataWindow.onSlotSizeChanged(width, height);
        }
    }
}
