package com.eduglasses.glassscan;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.eduglasses.glassscan.capture.CameraActivity;

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
		startActivity(CameraActivity.newIntent(this));
		finish();
	}
}
