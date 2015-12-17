
package com.invision.camera.app;

public interface GestureTarget {
    void onPreTarget();
    void onNextTarget();
//    void onPrePage();
//    void onNextPage();
//	void onHandDetected(boolean isDetected);
//	void onMove(int x, int y);
//	void onMove(int x, int y, int edgeState);
//	void onMoveDelta(int deltaX, int deltaY, int edgeState);
    
    void onFlingDown();
    void onDoAction();
    void onRelease();
    void onInit();
	void onTpMoveDelta(int deltaX, int deltaY);
	void onTpMoveFling(float velocityX, float velocityY);
}
