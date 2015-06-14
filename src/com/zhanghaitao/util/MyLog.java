package com.zhanghaitao.util;

import android.util.Log;

public class MyLog {
	public static final int VERBOSE = 1;
	public static final int DEBUG = 2;
	public static final int INFO = 3;
	public static final int WARN = 4;
	public static final int ERROR = 5;
	public static final int NOTHING = 6;
	private static String myTag = "ZHT";
	//可设置屏蔽打印消息：LEVEL=NOTHING
	public static final int LEVEL = VERBOSE;
	
	public static void v(String tag, String msg) {
		if (LEVEL <= VERBOSE) {
			Log.v(tag, msg);
		}
	}
	
	public static void d(String tag, String msg) {
		if (LEVEL <= DEBUG) {
			Log.d(tag, msg);
		}
	}
	
	public static void i(String tag, String msg) {
		if (LEVEL <= INFO) {
			Log.i(tag, msg);
		}
	}
	
	public static void w(String tag, String msg) {
		if (LEVEL <= WARN) {
			Log.w(tag, msg);
		}
	}
	
	public static void e(String tag, String msg) {
		if (LEVEL <= ERROR) {
			Log.e(tag, msg);
		}
	}
	
	public static void v(String msg) {
		if (LEVEL <= VERBOSE) {
			Log.v(myTag, msg);
		}
	}
	
	public static void w(String msg) {
		if (LEVEL <= VERBOSE) {
			Log.w(myTag, msg);
		}
	}
}
