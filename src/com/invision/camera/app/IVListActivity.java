package com.invision.camera.app;


import android.app.Activity;
import android.app.ListActivity;
import android.os.Bundle;

public abstract class IVListActivity extends ListActivity {
	public IVApplication mIVApplication;
	private GestureTarget mCurrentGestureTarget = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
        mIVApplication = (IVApplication) getApplication();
		super.onCreate(savedInstanceState);
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		mIVApplication.onResume();
		mIVApplication.setGestureTarget(mCurrentGestureTarget);
		super.onResume();
	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		mIVApplication.onPause();
		mIVApplication.setGestureTarget(null);
		super.onPause();
	}
	public void setGestureTarget(GestureTarget gestureTarget){
		mCurrentGestureTarget = gestureTarget;
		mIVApplication.setGestureTarget(mCurrentGestureTarget);
	};
}
