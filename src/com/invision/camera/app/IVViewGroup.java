package com.invision.camera.app;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.Scroller;

import com.android.givallery3d.R;

//import com.invision.settings.utils.Anim;
//import com.invision.settings.utils.AnimUtils;

@SuppressLint("NewApi") public abstract class IVViewGroup extends ViewGroup implements GestureTarget {
    static final String TAG = "IVViewGroup";

	public IVViewGroup(Context context) {
		this(context, null);
	}
	public IVViewGroup(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	public IVViewGroup(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle); 

        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.IVViewGroup, defStyle, 0);
        mWidthGap =  a.getDimensionPixelSize(R.styleable.IVViewGroup_widthGap, 0);
        mHeightGap =  a.getDimensionPixelSize(R.styleable.IVViewGroup_heightGap, 0);
        mCountX = a.getInt(R.styleable.IVViewGroup_itemCountX, DEFAULT_ITEM_COUNT_X);
        mCountY = a.getInt(R.styleable.IVViewGroup_itemCountY, DEFAULT_ITEM_COUNT_Y);
        a.recycle();
        mScroller = new Scroller(context,new ScrollInterpolator()); 
        mScrollerQuick = new Scroller(context); 
        mScrollerTouch = new Scroller(context); 
		// TODO Auto-generated constructor stub
	}

    private static final int DEFAULT_ITEM_COUNT_X = 4;
    private static final int DEFAULT_ITEM_COUNT_Y = 1;
	//sub
	public int mCellWidth;
	public int mCellHeight;
	public int mCountX;
	public int mCountY;
	public int mWidth;
	public int mHeight;
	public int mWidthGap = 0;
	public int mHeightGap = 0;

    // move effect
	public int mCurrentX = 0;
    public int mCurrentScreenX = 0;
    public int mScrollX = 0;
    public int mMaxScrollX = 0;
    private int mScreenWidth = 0;
    private float SCROLL_DURATION_UNIT = 1.5f;
    private final static int SCROLL_NONE = 0;
    private final static int SCROLL_LEFT = 1;
    private final static int SCROLL_RIGHT = 2;
    private int mScrollDirect = SCROLL_NONE;
    protected Scroller mScroller;
    protected Scroller mScrollerQuick;
    protected Scroller mScrollerTouch;
    public boolean isHandDetected = false;
    public boolean isTpDetected = false;
    public boolean isTouchDetected = false;
    private SelectedTarget mSelectedView = null;
    private final static boolean MOVE_LINKAGE = true;
    private final static boolean MOVE_SELECTED_CENTER = true;
    private final static int LINKAGE_DELTA = 5;

	private int mTpMoveX = -1;
	

    private static final int TOUCH_STATE_REST = 0;
    private static final int TOUCH_STATE_SCROLLING = 1;
    private static int mTouchState = TOUCH_STATE_REST;  
    private int mTouchSlop = 10;;  
    private float mLastMotionX;  
    private static final int SNAP_VELOCITY = 600;

    private VelocityTracker mVelocityTracker;

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // The children are given the same width and height as the scrollLayout  
        final int count = getChildCount();  
        final int widthSize = MeasureSpec.getSize(widthMeasureSpec); 
        final int heightSize = MeasureSpec.getSize(heightMeasureSpec); 

        int hSpace = widthSize - getPaddingLeft() - getPaddingRight();//-((View)getParent()).getPaddingLeft()-((View)getParent()).getPaddingRight();
        int vSpace = heightSize - getPaddingTop() - getPaddingBottom();
        mWidth = hSpace;
        mHeight = vSpace;
        mCellWidth = (hSpace-mWidthGap)/mCountX-mWidthGap;
        mCellHeight = (int) (mCellWidth*1.5f);
        
        for (int i = 0; i < count; i++) {  
            int childWidthMode;
            final View child = getChildAt(i);
            final LayoutParams lp = (LayoutParams) child.getLayoutParams();
            if (lp.width == LayoutParams.WRAP_CONTENT) {
            	childWidthMode = MeasureSpec.AT_MOST;
            } else {
            	childWidthMode = MeasureSpec.EXACTLY;
            }
            
            int childHeightMode;
            if (lp.height == LayoutParams.WRAP_CONTENT) {
            	childHeightMode = MeasureSpec.AT_MOST;
            } else {
            	childHeightMode = MeasureSpec.EXACTLY;
            }
            
            final int childWidthMeasureSpec =
            		MeasureSpec.makeMeasureSpec(mCellWidth, childWidthMode);
            final int childHeightMeasureSpec =
            		MeasureSpec.makeMeasureSpec(mCellHeight, childHeightMode);
            child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
        }
	}
	@Override
	protected void onLayout(boolean arg0, int arg1, int arg2, int arg3, int arg4) {
		// TODO Auto-generated method stub

		int childLeft = mWidthGap+getPaddingLeft();
		int childTop = mHeight/2;
		final int childCount = getChildCount();
        float rr = 1.0f;
        mCurrentX = mCurrentScreenX+mScrollX;
		for (int i=0; i<childCount; i++) {
			final View childView = getChildAt(i);
			childView.setScaleX(Anim.IM_NOR_SCALE);
			childView.setScaleY(Anim.IM_NOR_SCALE);
			if (childView.getVisibility() != View.GONE) {
				
				int childWidth = childView.getMeasuredWidth();
				int childHeight = childView.getMeasuredHeight();

				// max scroll x
				if(i == childCount-1){
					mMaxScrollX = childLeft+mWidthGap + childWidth;// + childWidth+mWidthGap;
				}

                // effect two icon
            	int delta = mCurrentX-(childLeft+childWidth/2);
            	float tempR = Math.abs(delta)*1.0f/(childWidth*1.0f+mWidthGap);
                // effect three icon
//            	int delta = mCurrentX-(childLeft+lp.width/2);
//            	float tempR = Math.abs(delta)*1.0f/(lp.width*2.0f);

            	if((!isHandDetected && !isTpDetected) || isTouchDetected){
            		tempR = 1.0f;
            	}
				childView.layout(childLeft, childTop-childHeight/2,
						childLeft+childWidth, childTop-childHeight/2+childHeight);
            	if(tempR<1.0f){
            		rr = 1-tempR;
            		if(rr>=0.5f){
            			setSelectedView(childView);
            		}else{
            			setSelectedView(null);
            		}
            		childView.setScaleX(1.0f+rr*Anim.IM_SCALE);
            		childView.setScaleY(1.0f+rr*Anim.IM_SCALE);
            	}else{
            		childView.setScaleX(1.0f);
            		childView.setScaleY(1.0f);
            	}
//				childTop += childHeight;
				childLeft += childWidth+mWidthGap;
			}
		}
	}
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
    	// TODO Auto-generated method stub
        final int action = ev.getAction();  
        if ((action == MotionEvent.ACTION_MOVE)  
                && (mTouchState == TOUCH_STATE_SCROLLING)) {  
            return true;  
        }  
        final float x = ev.getX();  
        final float y = ev.getY();  
        switch (action) {  
        case MotionEvent.ACTION_MOVE:  
            final int xDiff = (int) Math.abs(mLastMotionX - x);
            if (xDiff > mTouchSlop && mTouchState==TOUCH_STATE_REST) {
            	mTouchState = TOUCH_STATE_SCROLLING;
            }  
            break;  
  
        case MotionEvent.ACTION_DOWN:  
            mLastMotionX = x;
            mTouchState = mScroller.isFinished() ? TOUCH_STATE_REST : TOUCH_STATE_SCROLLING;  
            break;  
        case MotionEvent.ACTION_CANCEL:  
        case MotionEvent.ACTION_UP:
        	mTouchState = TOUCH_STATE_REST;  
            break;  
        }  
        return mTouchState == TOUCH_STATE_SCROLLING; 
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
    	// TODO Auto-generated method stub
    	isTouchDetected = true;
    	isTpDetected = false;
		setSelectedView(null);
    	if(!mScrollerTouch.isFinished()){
    		mScrollerTouch.abortAnimation();
    	}
    	if(mScreenWidth == 0){
    		Rect rect = new Rect();
    		getHitRect(rect);
    		int screenWidth = rect.width();
    		mScreenWidth = screenWidth;
    	}
    	if (mVelocityTracker == null) {
    		mVelocityTracker = VelocityTracker.obtain();
    	}
    	mVelocityTracker.addMovement(event);
        final int action = event.getAction();  
        final float x = event.getX();  
        final float y = event.getY();  

        switch (action) {  
        case MotionEvent.ACTION_DOWN:  
            mLastMotionX = x;  
            break;  
  
        case MotionEvent.ACTION_MOVE:  
            final int xDiff = (int) Math.abs(mLastMotionX - x);
            if (xDiff > mTouchSlop && mTouchState==TOUCH_STATE_REST) {
            	mTouchState = TOUCH_STATE_SCROLLING;
            }
            int deltaX = (int) (mLastMotionX - x);  
            mLastMotionX = x;  
            scrollBy(deltaX, 0); 
            mScrollX += deltaX;
            requestLayout();
            break;  
  
        case MotionEvent.ACTION_UP: 
			int delta = 0;
			if(mMaxScrollX>mScreenWidth && (mMaxScrollX-mScrollX-mScreenWidth<0)){
				delta = mMaxScrollX-mScrollX-mScreenWidth;
			}else if(mScrollX<0 || mMaxScrollX<=mScreenWidth){
				delta = -mScrollX;
			}
			if(delta != 0){
				animScrollBy(delta, (int) (Math.abs(delta)*0.5f));
			}else if(mTouchState == TOUCH_STATE_SCROLLING){
				final VelocityTracker velocityTracker = mVelocityTracker;  
				velocityTracker.computeCurrentVelocity(1000);  
				int velocityX = (int) velocityTracker.getXVelocity(); 
				int velocityY = (int) velocityTracker.getYVelocity(); 
	            if (velocityX > SNAP_VELOCITY || velocityX < -SNAP_VELOCITY) {
	            	isTouchDetected = true;
	            	mScrollerTouch.fling(mScrollX, 0, -(int)velocityX, -(int)velocityY, 0, mMaxScrollX-mScreenWidth, 0, 0);
	            }else{
	            	if(!mScrollerTouch.isFinished()){
	            		mScrollerTouch.abortAnimation();
	            	}
	            	isTouchDetected = false;
	            }
			}

			
            mTouchState = TOUCH_STATE_REST; 
            break;  
        case MotionEvent.ACTION_CANCEL:
        	mTouchState = TOUCH_STATE_REST;  
            break;  
        }  
        return true;  
    }
    public void refreshScroll(){
		if(mScrollX>mMaxScrollX+(mScreenWidth/2-mCellWidth/2)-mScreenWidth){
			mScrollX = mMaxScrollX+(mScreenWidth/2-mCellWidth/2)-mScreenWidth;
		}
		scrollTo(mScrollX, mScrollerQuick.getCurrY());
		requestLayout();
    }
    public void tpMoveDelta(int deltaX, int deltaY){
    	deltaX = Math.abs(deltaY)>Math.abs(deltaX)?deltaY:deltaX;
    	if(mTpMoveX == -1){
    		Rect rect = new Rect();
    		getHitRect(rect);
    		int screenWidth = rect.width();
    		mScreenWidth = screenWidth;
    		mTpMoveX = (int) (screenWidth/(2));
        	mCurrentScreenX = mScreenWidth/2;
    	}
    	isTpDetected = true;
    	if(MOVE_SELECTED_CENTER){
        	mCurrentScreenX = mScreenWidth/2;
    		if(!mScrollerQuick.isFinished()){
    			mScrollerQuick.abortAnimation();
    			requestLayout();
    		}
    		int tempQ = mScrollX+deltaX;
    		if((tempQ+mScreenWidth/2-mCellWidth/2 > 0 && 
    				(tempQ-(mScreenWidth/2-mCellWidth/2) < (mMaxScrollX-mScreenWidth) ||
    				((mMaxScrollX<=mScreenWidth) && tempQ<0)
    						)
    				)){
    			mScrollX += deltaX;
    		}
    		if((mMaxScrollX>mScreenWidth) && mScrollX>mMaxScrollX+(mScreenWidth/2-mCellWidth/2)-mScreenWidth){
    			mScrollX = mMaxScrollX+(mScreenWidth/2-mCellWidth/2)-mScreenWidth;
    		}
    		scrollTo(mScrollX, mScrollerQuick.getCurrY());
    		requestLayout();
    	}else{
    		int linkageDelta = 0;
    		if(deltaX < 0){
    			linkageDelta = LINKAGE_DELTA;
    		}else if(deltaX > 0){
    			linkageDelta = -LINKAGE_DELTA;
    		}
    		if(!MOVE_LINKAGE){
    			linkageDelta = 0;
    		}
    		deltaX *= 2;
    		int temp = mTpMoveX-deltaX;
    		if(temp > 0 && temp < mScreenWidth){
    			mTpMoveX -= deltaX;
    		}
    		handleMoveEvent(mTpMoveX, 0, mCellWidth/2+mWidthGap, mScreenWidth-mCellWidth/2-mWidthGap, linkageDelta);
    	}
    }
    public void tpMoveFling(float velocityX, float velocityY){
    	velocityX = Math.abs(velocityY)>Math.abs(velocityX)?velocityY:velocityX;
    	if(MOVE_SELECTED_CENTER){
    		mScrollerQuick.fling(mScrollX, 0, -(int)velocityX, -(int)velocityY, -mScreenWidth/2, mMaxScrollX-mScreenWidth+mScreenWidth/2, 0, 0);
    	}
    }
    @Override
    public void computeScroll() {
    	// TODO Auto-generated method stub
    	if(MOVE_SELECTED_CENTER){
            if (mScrollerQuick.computeScrollOffset()) {
            	if(mScrollerQuick.getCurrX()+mScreenWidth/2<0){
            		mScrollerQuick.abortAnimation();
            		scrollTo(0, mScroller.getCurrY()); 
                    mScrollX = 0;
                	requestLayout();
            	}else if(mScrollerQuick.getCurrX()-mScreenWidth/2 > (mMaxScrollX-mScreenWidth)){
        			mScrollX = mMaxScrollX-mScreenWidth;
            		scrollTo(mScrollX, mScrollerQuick.getCurrY()); 
                	requestLayout();
                	mScrollerQuick.abortAnimation();
            	}else{
            		// scroll
            		scrollTo(mScrollerQuick.getCurrX(), mScrollerQuick.getCurrY());  
            		mScrollX = mScrollerQuick.getCurrX();
            		requestLayout();
            	}
                //must call this method  
                postInvalidate();  
            }
    	}
    	if(isTouchDetected){
            if (mScrollerTouch.computeScrollOffset()) {
            	if(mScrollerTouch.getCurrX()+mScreenWidth/2<0){
            		mScrollerTouch.abortAnimation();
            		scrollTo(0, mScroller.getCurrY()); 
                    mScrollX = 0;
                	requestLayout();
            	}else if(mScrollerTouch.getCurrX()-mScreenWidth/2 > (mMaxScrollX-mScreenWidth)){
        			mScrollX = mMaxScrollX-mScreenWidth;
            		scrollTo(mScrollX, mScrollerTouch.getCurrY()); 
                	requestLayout();
                	mScrollerTouch.abortAnimation();
            	}else{
            		// scroll
            		scrollTo(mScrollerTouch.getCurrX(), mScrollerTouch.getCurrY());  
            		mScrollX = mScrollerTouch.getCurrX();
            		requestLayout();
            	}
                //must call this method  
                postInvalidate();  
            }else{
            	isTouchDetected = false;
            }
    	}
        // mScroller end ?  
        if (mScroller.computeScrollOffset()) {
        	if(mScroller.getCurrX()<0){
        		mScroller.abortAnimation();
    			mScrollDirect = SCROLL_NONE;
        		scrollTo(0, mScroller.getCurrY()); 
                mScrollX = 0;
            	requestLayout();
        	}else if(mScroller.getCurrX() > (mMaxScrollX-mScreenWidth)){
    			mScrollDirect = SCROLL_NONE;
    			mScrollX = mMaxScrollX-mScreenWidth;
        		scrollTo(mScrollX, mScroller.getCurrY()); 
            	requestLayout();
            	mScroller.abortAnimation();
        	}else{
        		// scroll
        		scrollTo(mScroller.getCurrX(), mScroller.getCurrY());  
        		mScrollX = mScroller.getCurrX();
        		requestLayout();
        	}
            //must call this method  
            postInvalidate();  
        }else{
        	// scroll end, reset state
			mScrollDirect = SCROLL_NONE;
        }
    	super.computeScroll();
    }
    private void handleMoveEvent(int x, int y, int scrollZoneLeft, int scrollZoneRight, int linkageDeltaX) {
    	if(!isHandDetected && !isTpDetected){
    		return;
    	}
    	Log.w(TAG, "handleMoveEvent x="+x+" ; scrollZoneLeft="+scrollZoneLeft+" ; scrollZoneRight="+scrollZoneRight+" ; linkageDeltaX="+linkageDeltaX+" ; mScrollDirect="+mScrollDirect+" ; mScroller.isFinished()="+mScroller.isFinished());
    	requestLayout();
    	if(mScrollDirect == SCROLL_LEFT || mScrollDirect == SCROLL_RIGHT){
    		if(x>scrollZoneLeft && x<scrollZoneRight && !mScroller.isFinished()){
    			mScroller.abortAnimation();
    			requestLayout();
    			mScrollDirect = SCROLL_NONE;
    		}
    	}else if(x < scrollZoneLeft){
    		mScrollDirect = SCROLL_LEFT;
    		int duration = 0;
    		if(mScrollX>0){
    			duration = (int) (Math.abs(mScrollX)*SCROLL_DURATION_UNIT);
    			mScroller.startScroll(mScrollX, 0, -(mScrollX), 0, duration);
    		}
			return;
    	}else if(x>scrollZoneRight){
    		mScrollDirect = SCROLL_RIGHT;
    		int duration = 0;
    		if(mMaxScrollX-mScrollX-mScreenWidth>0){
    			duration = (int) ((Math.abs(mMaxScrollX-mScrollX))*SCROLL_DURATION_UNIT);
    			mScroller.startScroll(mScrollX, 0, mMaxScrollX-mScrollX, 0, duration);
    		}
    		return;
    	}else{
    		if(linkageDeltaX != 0){
				mScrollX += linkageDeltaX;
        		scrollTo(mScrollX, mScroller.getCurrY());
    		}
    		mCurrentScreenX = x;
        	requestLayout();
    	}
    }
    public SelectedTarget getSelectedView() {
		return mSelectedView;
	}
    public void setSelectedView(View view) {
    	if(!(view instanceof SelectedTarget)){
    		return;
    	}
    	SelectedTarget target = (SelectedTarget)view;
    	if(mSelectedView == null){
        	mSelectedView = target;
        	mSelectedView.selectedChange(true);
    	}
    	if(mSelectedView != null && !mSelectedView.equals(view)){
        	mSelectedView.selectedChange(false);
        	target.selectedChange(true);
    		mSelectedView = target;
    	}
    }
    public void releaseSelectedView() {
    	if(mSelectedView != null){
    		mSelectedView.selectedChange(false);
    		mSelectedView = null;
    	}
    }
	private static class ScrollInterpolator implements Interpolator {
        public ScrollInterpolator() {
        }
        public float getInterpolation(float t) {
//            t -= 1.0f;
//            return t*t*t*t*t + 1;
            return t;
        }
    }
    private boolean mMoveAnimPlaying = false;
    private ValueAnimator mMoveAnim = null;
    private void animScrollBy(final int delta, final int duration){
    	if(mMoveAnimPlaying) return;
        ValueAnimator va = AnimUtils.ofFloat(0f, 1f);
        mMoveAnim = va;
        va.setDuration(duration);
        va.addUpdateListener(new AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float r = ((Float) animation.getAnimatedValue()).floatValue();
            	scrollTo((int) (mScrollX+r*delta), mScroller.getCurrY());
            }
        });
        va.addListener(new AnimatorListenerAdapter() {
        	@Override
        	public void onAnimationStart(Animator animation) {
        		// TODO Auto-generated method stub
        		mMoveAnimPlaying = true;
        	}
        	@Override
        	public void onAnimationEnd(Animator animation) {
        		mScrollX = mScrollX+delta;
            	scrollTo(mScrollX, mScroller.getCurrY());
            	mMoveAnim = null;
            	mMoveAnimPlaying = false;
        	}
        });
        va.start();
    }
	@Override
	public void onDoAction() {
		// TODO Auto-generated method stub
		if(mSelectedView != null){
			mSelectedView.doAction();
		}
	}

	@Override
	public void onRelease() {
		// TODO Auto-generated method stub
		isTpDetected = false;
		releaseSelectedView();
		requestLayout();
	}

	@Override
	public void onInit() {
		// TODO Auto-generated method stub
		isTpDetected = true;
		requestLayout();
	}

	@Override
	public void onTpMoveDelta(int deltaX, int deltaY) {
		// TODO Auto-generated method stub
		tpMoveDelta(deltaX, deltaY);
	}

	@Override
	public void onTpMoveFling(float velocityX, float velocityY) {
		// TODO Auto-generated method stub
		tpMoveFling(velocityX, velocityY);
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
}
