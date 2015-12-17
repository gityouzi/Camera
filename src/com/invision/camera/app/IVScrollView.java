package com.invision.camera.app;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

public class IVScrollView extends ScrollView implements GestureTarget {

	public IVScrollView(Context context) {
		this(context, null);
	}
	public IVScrollView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	public IVScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	@Override
	public void onPreTarget() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onNextTarget() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onFlingDown() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onDoAction() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onRelease() {
		// scroll to top
//		fullScroll(ScrollView.FOCUS_UP);
	}
	@Override
	public void onInit() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onTpMoveDelta(int deltaX, int deltaY) {
		int tempDeltaX = Math.abs(deltaY)>Math.abs(deltaX)?deltaY:deltaX;
		scrollBy(0, tempDeltaX);
	}
	@Override
	public void onTpMoveFling(float velocityX, float velocityY) {
		// TODO Auto-generated method stub
		
	}

}
