package com.invision.camera.app;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.view.View;

@SuppressLint("NewApi")
public class DispatchGesture implements GestureTarget {
	private ArrayList<View> mTargetViews = new ArrayList<View>();
	private View mCurrentTargetView = null;
	private final static boolean CYCLE_SUPPORT = false;
	
	public void addTargetView(View target){
		if(!mTargetViews.contains(target)){
			mTargetViews.add(target);
		}
	}
	public void setCurrentTargetView(View target){
		if(target == null) return;
		if(mCurrentTargetView != null){
			mCurrentTargetView.setSelected(false);
//			mCurrentTargetView.setPressed(false);
		}
		if(target != null){
			target.setSelected(true);
//			target.setPressed(true);
		}
		mCurrentTargetView = target;
	}

	@Override
	public void onDoAction() {
		if(mCurrentTargetView != null){
			mCurrentTargetView.callOnClick();
		}
	}

	@Override
	public void onRelease() {
		if(mCurrentTargetView != null){
			mCurrentTargetView.setSelected(false);
//			mCurrentTargetView.setPressed(false);
		}
	}

	@Override
	public void onInit() {
		if(mCurrentTargetView != null){
			mCurrentTargetView.setSelected(true);
//			mCurrentTargetView.setPressed(true);
		}
	}

	@Override
	public void onTpMoveDelta(int deltaX, int deltaY) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTpMoveFling(float velocityX, float velocityY) {
		// TODO Auto-generated method stub

	}
	@Override
	public void onPreTarget() {
		int cou = mTargetViews.size();
		if(cou == 0) return;
		View resultView = null;
		
		if(CYCLE_SUPPORT){
			int curr = cou;
			if(mCurrentTargetView != null){
				curr = mTargetViews.indexOf(mCurrentTargetView);
			}else{
				curr = cou;
			}
			int whileCou = 1;
			int nextIndex = (cou+curr-1)%cou;
			View tempView = mTargetViews.get(nextIndex);
			while (tempView.getVisibility()==View.INVISIBLE || tempView.getVisibility()==View.GONE ||
					!tempView.isEnabled() || whileCou==cou+1
					) {
				whileCou++;
				nextIndex = (cou+curr-whileCou)%cou;
				tempView = mTargetViews.get(nextIndex);
			}
			if(whileCou==cou+1){
				tempView = null;
			}
			resultView = tempView;
		}else{
			int curr = 0;
			if(mCurrentTargetView != null){
				curr = mTargetViews.indexOf(mCurrentTargetView);
			}
			if(curr<0 || curr>cou-1){
				curr = 1;
			}
			View tempView = null;
			for (int i = curr-1; i > -1; i--) {
				tempView = mTargetViews.get(i);
				if(tempView.getVisibility()==View.INVISIBLE || tempView.getVisibility()==View.GONE ||
						!tempView.isEnabled()
						){
					continue;
				}else{
					resultView = tempView;
					break;
				}
			}
		}
		setCurrentTargetView(resultView);
	}
	@Override
	public void onNextTarget() {
		int cou = mTargetViews.size();
		if(cou == 0) return;
		View resultView = null;
		
		if(CYCLE_SUPPORT){
			int curr = -1;
			if(mCurrentTargetView != null){
				curr = mTargetViews.indexOf(mCurrentTargetView);
			}else{
				curr = -1;
			}
			int whileCou = 1;
			int nextIndex = (curr+1)%cou;
			View tempView = mTargetViews.get(nextIndex);
			while (tempView.getVisibility()==View.INVISIBLE || tempView.getVisibility()==View.GONE ||
					!tempView.isEnabled() || whileCou==cou+1
					) {
				whileCou++;
				nextIndex = (curr+whileCou)%cou;
				tempView = mTargetViews.get(nextIndex);
			}
			if(whileCou==cou+1){
				tempView = null;
			}
			resultView = tempView;
		}else{
			int curr = 0;
			if(mCurrentTargetView != null){
				curr = mTargetViews.indexOf(mCurrentTargetView);
			}
			if(curr<0 || curr>cou-1){
				curr = -1;
			}
			View tempView = null;
			for (int i = curr+1; i < cou; i++) {
				tempView = mTargetViews.get(i);
				if(tempView.getVisibility()==View.INVISIBLE || tempView.getVisibility()==View.GONE ||
						!tempView.isEnabled()
						){
					continue;
				}else{
					resultView = tempView;
					break;
				}
			}
		}
		setCurrentTargetView(resultView);
	}
	@Override
	public void onFlingDown() {
		// TODO Auto-generated method stub
		
	}
}
