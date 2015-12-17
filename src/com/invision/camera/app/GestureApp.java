package com.invision.camera.app;

import android.app.Application;

import com.invision.service.IYCServiceCallback;
import com.invision.service.IYCServiceCallbackListener;
import com.invision.service.YCServerManager;

public class GestureApp extends Application {
	private GestureTarget mCurrentGestureTarget = null;
	private YCServerManager mYCServiceClient;
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		mYCServiceClient = YCServerManager.getServerManager(this);
	}
	public void onResume(){
		mYCServiceClient.registerYCServiceCallback(mGestureListener);
	}
	public void onPause(){
		mYCServiceClient.setYcControlMode(YCServerManager.YC_CONTROL_MODE_TP);
		
		mYCServiceClient.unregisterYCServiceCallback(mGestureListener);
	}
	public void openGesture(){
		mYCServiceClient.registerYCServiceCallback(mGestureListener);
	}
	public void closeGesture(){
		mYCServiceClient.setYcControlMode(YCServerManager.YC_CONTROL_MODE_TP);
		
		mYCServiceClient.unregisterYCServiceCallback(mGestureListener);
	}
	private IYCServiceCallback mGestureListener=new IYCServiceCallbackListener(){
		public void	onServiceConnected(){
			mYCServiceClient.setYcControlMode(YCServerManager.YC_CONTROL_MODE_GESTURE);
		};
		public void	onServiceReconnected(){
			mYCServiceClient.setYcControlMode(YCServerManager.YC_CONTROL_MODE_GESTURE);
		};
		public void	onTouchScroll(int action, float deltaX, float deltaY){
			tpMoveDelta((int)deltaX, (int)deltaY);
		}
		public void onTouchFling(float velocityX, float velocityY){
			tpMoveFling(velocityX, velocityY);
		};
		public void onGestureEvent(int mGestureAction) {
			switch (mGestureAction) {
			case MOVE_PREV:// left
				onPreTarget();
				break;
			case MOVE_NEXT:// right
				onNextTarget();
				break;
			case FLING_DOWN:// fling down
				onFlingDown();
				break;
			case 5:// click
				doAction();
				break;

			default:
				break;
			}
		};
	};

	public void onFlingDown() {
    	if(mCurrentGestureTarget != null){
    		mCurrentGestureTarget.onFlingDown();
    	}
	}
	public void onPreTarget() {
		if(mCurrentGestureTarget != null){
			mCurrentGestureTarget.onPreTarget();
		}
	}
	public void onNextTarget() {
		if(mCurrentGestureTarget != null){
			mCurrentGestureTarget.onNextTarget();
		}
	}
	public void doAction() {
    	if(mCurrentGestureTarget != null){
    		mCurrentGestureTarget.onDoAction();
    	}
	}
	public void tpMoveFling(float velocityX, float velocityY){
		if(mCurrentGestureTarget != null){
			mCurrentGestureTarget.onTpMoveFling(velocityX, velocityY);
		}
	}
	public void tpMoveDelta(int deltaX, int deltaY){
		if(mCurrentGestureTarget != null){
			mCurrentGestureTarget.onTpMoveDelta(deltaX, deltaY);
		}
	}
	public void setGestureTarget(GestureTarget gestureTarget){
		if(gestureTarget == null){
			if(mCurrentGestureTarget != null){
				mCurrentGestureTarget.onRelease();
				mCurrentGestureTarget = gestureTarget;
			}
		}else if(mCurrentGestureTarget != null && mCurrentGestureTarget != gestureTarget){
			mCurrentGestureTarget.onRelease();
			if(mYCServiceClient.getYcControlMode()==YCServerManager.YC_CONTROL_MODE_GESTURE){
				gestureTarget.onInit();
			}
			mCurrentGestureTarget = gestureTarget;
		}else{
			if(mYCServiceClient.getYcControlMode()==YCServerManager.YC_CONTROL_MODE_GESTURE){
				gestureTarget.onInit();
			}
			mCurrentGestureTarget = gestureTarget;
		}
	}
}
