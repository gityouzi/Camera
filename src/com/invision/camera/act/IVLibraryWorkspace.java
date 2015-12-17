package com.invision.camera.act;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import com.invision.camera.app.IVViewGroup;


@SuppressLint("NewApi") public class IVLibraryWorkspace extends IVViewGroup {
	private static final int MSG_REQUEST_REFRESHSCROLL = 1;
    private final LayoutInflater mLayoutInflater;

	public IVLibraryWorkspace(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
        mLayoutInflater = LayoutInflater.from(context);
		// TODO Auto-generated constructor stub
	}

	public IVLibraryWorkspace(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
		// TODO Auto-generated constructor stub
	}
	public IVLibraryWorkspace(Context context) {
		this(context, null);
		// TODO Auto-generated constructor stub
	}
	@Override
	protected void onFinishInflate() {
		// TODO Auto-generated method stub
		super.onFinishInflate();
		
	}
	private Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			
			switch (msg.what) {
			case MSG_REQUEST_REFRESHSCROLL:
				refreshScroll();
				break;

			default:
				break;
			}
			
		};
	};
}
