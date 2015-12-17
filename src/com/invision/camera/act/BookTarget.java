package com.invision.camera.act;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.invision.camera.app.Anim;
import com.invision.camera.app.IVActivity;
import com.invision.camera.app.SelectedTarget;

@SuppressLint("NewApi") public class BookTarget extends LinearLayout implements SelectedTarget,View.OnClickListener {

	public BookTarget(Context context) {
		this(context, null);
		// TODO Auto-generated constructor stub
	}

	public BookTarget(Context context, AttributeSet attrs) {
		this(context, attrs, -1);
		// TODO Auto-generated constructor stub
	}

	public BookTarget(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}
	@Override
	protected void onFinishInflate() {
		// TODO Auto-generated method stub
		super.onFinishInflate();
		setOnClickListener(this);
	}
	public IVActivity myActivity;
	public void setup(IVActivity activity){
		myActivity = activity;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		LinearLayout.LayoutParams lp = (android.widget.LinearLayout.LayoutParams) getChildAt(0).getLayoutParams();
		int cellWidth = MeasureSpec.getSize(widthMeasureSpec);
		float margin = Anim.IM_SCALE/(1+Anim.IM_SCALE)*cellWidth/2.0f;
		int mg = (int) margin;
		lp.setMargins(mg, mg, mg, mg);
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}
	@Override
	public void setScaleX(float scaleX) {
		// TODO Auto-generated method stub
		getChildAt(0).setScaleX(scaleX);
//		super.setScaleX(scaleX);
	}
	@Override
	public void setScaleY(float scaleY) {
		// TODO Auto-generated method stub
		getChildAt(0).setScaleY(scaleY);
//		super.setScaleY(scaleY);
	}
	@Override
	public void selectedChange(boolean selected) {
		// TODO Auto-generated method stub
	}

	@Override
	public void doAction() {
		// TODO Auto-generated method stub
		int position = (Integer) getTag();
		if(myActivity!=null){
//			myActivity.onItemClick(position);
		}
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		doAction();
	}

}
