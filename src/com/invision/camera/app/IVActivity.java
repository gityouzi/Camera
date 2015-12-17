package com.invision.camera.app;

import java.util.Stack;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;

@SuppressLint("NewApi") public abstract class IVActivity extends Activity {
	public IVApplication mIVApplication;
	private GestureTarget mCurrentGestureTarget = null;
    private Stack<GestureTarget> mGestureStack = new Stack<GestureTarget>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
        mIVApplication = (IVApplication) getApplication();
		super.onCreate(savedInstanceState);
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		if(isSupportActCycle()){
			mIVApplication.onResume();
			mIVApplication.setGestureTarget(mCurrentGestureTarget);
		}
		super.onResume();
	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		if(isSupportActCycle()){
			mIVApplication.onPause();
			mIVApplication.setGestureTarget(null);
		}
		super.onPause();
	}
	public void openGesture(){
		mIVApplication.openGesture();
	}
	public void closeGesture(){
		mIVApplication.closeGesture();
	}
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		if(snapNextStackGestureTarget()) return;
		super.onBackPressed();
	}
	public void setGestureTarget(GestureTarget gestureTarget){
		if(mCurrentGestureTarget != null && !mCurrentGestureTarget.equals(gestureTarget)){
			mGestureStack.push(mCurrentGestureTarget);
		}
		mCurrentGestureTarget = gestureTarget;
		mIVApplication.setGestureTarget(mCurrentGestureTarget);
	};
	public boolean snapNextStackGestureTarget(){
		if(mGestureStack.size()==0) return false;
		mCurrentGestureTarget = mGestureStack.pop();
		mIVApplication.setGestureTarget(mCurrentGestureTarget);
		return true;
	}
	protected boolean isSupportActCycle(){
		return true;
	}
}
