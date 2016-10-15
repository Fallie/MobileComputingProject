package com.example.wyyz.snapchat.util;

import android.util.Log;

public class AppLog {

	private static final boolean isDebug = true;
	private static final boolean isInfo = true;
	private static final boolean isError = true;
	private static final boolean isWarn = true;
	private static final boolean isVerbose = true;

	public static void d(String tag, String message) {
		if (isDebug) {
			Log.d(tag, message);
		}
	}

	public static void i(String tag, String message) {
		if (isInfo) {
			Log.i(tag, message);
		}
	}

	public static void e(String tag, String message) {
		if (isError) {
			Log.e(tag, message);
		}
	}

	public static void w(String tag, String message) {
		if (isWarn) {
			Log.e(tag, message);
		}
	}

	public static void v(String tag, String message) {
		if (isVerbose) {
			Log.v(tag, message);
		}
	}
}