package com.eduglasses.glassscan;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;

import com.eduglass.utils.Utils;
import com.eduglasses.glassscan.capture.CameraActivity;
import com.github.barcodeeye.scan.CaptureQRCodeActivity;

public class LaunchActivity extends Activity {

	private static final String TAG = LaunchActivity.class.getSimpleName();
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// delayed camera activity
		// see: https://code.google.com/p/google-glass-api/issues/detail?id=259
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				processVoiceAction(null);
			}
		}, 100);

	}

	private void processVoiceAction(String command) {
		Log.v(TAG, "Voice command: " + command);
		if("".equals(Utils.getStringPreferences(LaunchActivity.this, Utils.KEY_USERNAME)) || "".equals(Utils.getStringPreferences(LaunchActivity.this, Utils.KEY_USERNAME))) {
			startActivity(CaptureQRCodeActivity.newIntent(this));
		} else {
			startActivity(CameraActivity.newIntent(this));
			//startActivity(CaptureQRCodeActivity.newIntent(this));
		}
		
		
		finish();
	}
}
