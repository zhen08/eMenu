package com.today.emenu;

import android.util.Log;

public final class Debug {
	static final void Log(String str){
		if (Constants.DEBUGLOG){
			Log.d("eMenu",str);
		}
	}
}
