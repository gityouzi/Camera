package com.iv.debug;

import android.util.Log;

public final class IVDebug {
	public static boolean sameState = false;
	public static void printStack(String tag){
		StackTraceElement[] ste = Thread.currentThread().getStackTrace();
		Log.w("samuel-stack", "===========begin============");
		for (StackTraceElement e : ste) {
			StringBuffer msg = new StringBuffer();
			msg.append(e.getClassName())
			.append(".").append(e.getMethodName())
			.append("(").append(e.getLineNumber()).append(")");
			Log.w("samuel-stack", tag+" , "+msg.toString());
		}
		Log.w("samuel-stack", "===========end============");
	}
	public static void w(boolean show, String tag, String info){
		if(show){
			Log.w(tag, info);
		}
	}
	public static void w(String tag, String info){
		if(sameState){
			Log.w(tag, info);
		}
	}
}
