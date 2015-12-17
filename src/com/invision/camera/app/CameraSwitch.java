package com.invision.camera.app;

import java.util.ArrayList;
import java.util.List;

import com.android.camera.CameraActivity;
import com.android.gallery3d.app.Log;
import com.android.givallery3d.R;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.AnimationSet;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class CameraSwitch extends LinearLayout implements GestureTarget {

	public CameraSwitch(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public CameraSwitch(Context context, AttributeSet attrs) {
		this(context, attrs, -1);
		// TODO Auto-generated constructor stub
	}

	public CameraSwitch(Context context) {
		this(context, null);
		// TODO Auto-generated constructor stub
	}
	
	private CameraActivity mCameraActivity;
	public void setup(CameraActivity cameraActivity){
		mCameraActivity = cameraActivity;
	}

	private List<ImageView> mTargets = new ArrayList<ImageView>();
	private int mCurrentSelectIndex = 2;
	private ImageView mCurrentSelectImage = null;
	private int mTargetsCou = 0;
	@Override
	protected void onFinishInflate() {
		// TODO Auto-generated method stub
		super.onFinishInflate();
		mTargets.add((ImageView) findViewById(R.id.photo));
		mTargets.add((ImageView) findViewById(R.id.video));
		mTargets.add((ImageView) findViewById(R.id.shutter_button));
		mTargetsCou = mTargets.size();
		updateSelectedTarget();
	}
	private void updateSelectedTarget(){
		ImageView temp = mTargets.get(mCurrentSelectIndex);
		if(mCurrentSelectImage == null){
			mCurrentSelectImage = temp;
//			temp.setScaleX(1.5f);
//			temp.setScaleY(1.5f);
			targetSelectedAnim(temp);
		}else if(!mCurrentSelectImage.equals(temp)){
//			mCurrentSelectImage.setScaleX(1.0f);
//			mCurrentSelectImage.setScaleY(1.0f);
			targetUnselectedAnim(mCurrentSelectImage);
//			temp.setScaleX(1.5f);
//			temp.setScaleY(1.5f);
			targetSelectedAnim(temp);
			mCurrentSelectImage = temp;
		}
	}
	private void targetSelectedAnim(final ImageView target){
		
		ValueAnimator animation = ValueAnimator.ofFloat(1f, 1.4f);
		animation.setDuration(500);
		animation.addUpdateListener(new AnimatorUpdateListener() {
		    public void onAnimationUpdate(ValueAnimator animation) {
		    	float value = (Float) animation.getAnimatedValue();
		    	target.setScaleX(value);
		    	target.setScaleY(value);
		    }
		});
		animation.start();
	}
	private void targetUnselectedAnim(final ImageView target){
		ValueAnimator animation = ValueAnimator.ofFloat(1.4f, 1f);
		animation.setDuration(500);
		animation.addUpdateListener(new AnimatorUpdateListener() {
		    public void onAnimationUpdate(ValueAnimator animation) {
		    	float value = (Float) animation.getAnimatedValue();
		    	target.setScaleX(value);
		    	target.setScaleY(value);
		    }
		});
		animation.start();
	}
	private final int WAIT_TIMES = 2;
	private int preCou = 0;
	private int nextCou = 0;
	@Override
	public void onPreTarget() {
		// TODO Auto-generated method stub
		nextCou = 0;
		preCou++;
		preCou %= WAIT_TIMES;
		if(preCou != 0) return;
		if(mCurrentSelectIndex>0){
			mCurrentSelectIndex--;
		}
		updateSelectedTarget();
	}

	@Override
	public void onNextTarget() {
		// TODO Auto-generated method stub
		preCou = 0;
		nextCou++;
		nextCou %= WAIT_TIMES;
		if(nextCou != 0) return;
		if(mCurrentSelectIndex<(mTargetsCou-1)){
			mCurrentSelectIndex++;
		}
		updateSelectedTarget();
	}

	@Override
	public void onFlingDown() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDoAction() {
		// TODO Auto-generated method stub
		if(mCurrentSelectIndex == 0){
			mCameraActivity.onCameraSelected(CameraActivity.PHOTO_MODULE_INDEX);
		}else if(mCurrentSelectIndex == 1){
			mCameraActivity.onCameraSelected(CameraActivity.VIDEO_MODULE_INDEX);
		}else if(mCurrentSelectIndex == 2){
			mCameraActivity.shutterPerformClick();
		}
	}

	@Override
	public void onRelease() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onInit() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTpMoveDelta(int deltaX, int deltaY) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTpMoveFling(float velocityX, float velocityY) {
		// TODO Auto-generated method stub
		
	}

}
