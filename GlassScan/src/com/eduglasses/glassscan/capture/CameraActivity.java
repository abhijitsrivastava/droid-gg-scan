package com.eduglasses.glassscan.capture;

import java.io.IOException;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.Toast;

import com.eduglass.utils.GoogleContactsAPI;
import com.eduglass.utils.Utils;
import com.eduglasses.glassscan.BaseGlassActivity;
import com.eduglasses.glassscan.R;
import com.eduglasses.glassscan.image.ImageManager;
import com.google.gdata.data.contacts.ContactEntry;
import com.google.gdata.data.extensions.Email;

/**
 * A placeholder fragment containing a simple view.
 */
public class CameraActivity extends BaseGlassActivity implements
		SurfaceHolder.Callback {

	private static final String TAG = CameraActivity.class.getSimpleName();
	private static final String IMAGE_PREFIX = "GlassScan_";

	private Camera camera;
	private boolean mHasSurface;
	private ImageManager mImageManager;

	public static Intent newIntent(Context context) {
		Intent intent = new Intent(context, CameraActivity.class);
		return intent;
	}

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.activity_camera);
		mImageManager = new ImageManager(this);

		mHasSurface = false;
		//Utils.updateContacts(this);
		if (!GoogleContactsAPI.getInstance().isUpdated()) {
			GoogleContactsAPI.getInstance().setUpdated(true);
			new FetchContactTask().execute();
		}
		/*
		 * Bundle bundle = getIntent().getExtras(); String credentials =
		 * bundle.getString("QRCodeData"); Log.d(TAG, credentials);
		 */

		/*
		 * Thread thread = new Thread(new Runnable() {
		 * 
		 * @Override public void run() { String[] names = getAccountNames();
		 * Log.d(TAG, names.length + ""); for (int i = 0; i < names.length; i++)
		 * { Log.d(TAG, names[i]); }
		 * 
		 * String token = "";
		 * 
		 * try { token = GoogleAuthUtil.getToken(CameraActivity.this, names[0],
		 * "oauth2:https://mail.google.com/mail/feed/atom"); Log.d(TAG, token);
		 * } catch(Exception e) { Log.d(TAG, e.getMessage()); } } });
		 * 
		 * thread.start();
		 */
		// uncomment to debug the application.
		// android.os.Debug.waitForDebugger();

	}

	/*
	 * private String[] getAccountNames() { AccountManager mAccountManager =
	 * AccountManager.get(this); Account[] accounts =
	 * mAccountManager.getAccountsByType( GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE);
	 * String[] names = new String[accounts.length]; for (int i = 0; i <
	 * names.length; i++) { names[i] = accounts[i].name; } return names; }
	 */


	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		startCamera();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		GoogleContactsAPI.getInstance().destroy();
	}
	
	@Override
	protected void onPause() {
		if (!mHasSurface) {
			SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
			SurfaceHolder surfaceHolder = surfaceView.getHolder();
			surfaceHolder.removeCallback(this);
		}
		camera.release();
		super.onPause();
	}

	private void startCamera() {
		SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
		SurfaceHolder surfaceHolder = surfaceView.getHolder();
		if (mHasSurface) {
			// The activity was paused but not stopped, so the surface still
			// exists. Therefore
			// surfaceCreated() won't be called, so init the camera here.
			initCamera(surfaceHolder);
		} else {
			// Install the callback and wait for surfaceCreated() to init the
			// camera.
			surfaceHolder.addCallback(this);
		}

	}

	private void initCamera(SurfaceHolder surfaceHolder) {
		if (surfaceHolder == null) {
			throw new IllegalStateException("No SurfaceHolder provided");
		}
		camera = Camera.open();
		try {
			camera.setPreviewDisplay(surfaceHolder);
		} catch (IOException e) {
			e.printStackTrace();
		}
		camera.startPreview();
	}

	@Override
	protected boolean onTap() {
		camera.takePicture(null, null, mPicture);
		return super.onTap();
	}

	PictureCallback mPicture = new PictureCallback() {
		@Override
		public void onPictureTaken(byte[] captureData, Camera camera) {

			Intent intent = new Intent(getApplicationContext(),
					ShareActivity.class);
			intent.putExtra("image", captureData);
			startActivity(intent);

			/*
			 * Bitmap captureImage = null; if (captureData != null) {
			 * captureImage = getBitmapFromByteArray(captureData); }
			 * 
			 * 
			 * Uri imageUri = null;
			 * 
			 * String imageName = IMAGE_PREFIX + System.currentTimeMillis() +
			 * ".png"; try { imageUri = mImageManager.saveImage(imageName,
			 * captureImage); Log.v(TAG, "Saving image as: " + imageName);
			 * 
			 * 
			 * 
			 * } catch (IOException e) { Log.e(TAG, "Failed to save image!", e);
			 * }
			 */
		}
	};

	protected Bitmap getBitmapFromByteArray(byte[] captureData) {
		Bitmap captureImage = null;
		captureImage = BitmapFactory.decodeByteArray(captureData, 0,
				captureData.length, null);
		// Mutable copy:
		captureImage = captureImage.copy(Bitmap.Config.ARGB_8888, true);
		return captureImage;
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (holder == null) {
			Log.e(TAG,
					"*** WARNING *** surfaceCreated() gave us a null surface!");
		}
		if (!mHasSurface) {
			mHasSurface = true;
			initCamera(holder);
		}

	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		mHasSurface = false;
	}
	
	class FetchContactTask extends AsyncTask<String, Void, String> {

		private ProgressDialog progress = null;
		
	    @Override
	    protected String doInBackground(String... params) {
	        

			String separator = "";
			String emailString = "";
			if (GoogleContactsAPI.getInstance().Login(
					Utils.getStringPreferences(CameraActivity.this,
							Utils.KEY_USERNAME),
					Utils.getStringPreferences(CameraActivity.this,
							Utils.KEY_PASSWORD))) {
				List<ContactEntry> contactList = null;
				StringBuilder emailText = new StringBuilder();
				try {
					contactList = GoogleContactsAPI.getInstance()
							.getEntries();
					for (ContactEntry contactEntry : contactList) {
						for (Email email : contactEntry
								.getEmailAddresses()) {
							emailText.append(separator
									+ email.getAddress());
							separator = ",";
						}
					}
					if (null != contactList) {
						emailString = emailText.toString();
					}
					
					//Log.d("Utils", "emails :" + emailText.toString());
				} catch (Exception e) {
					e.printStackTrace();
					emailString = "exception";
				}
			}
		
	        return emailString;
	    }

	    @Override
	    protected void onPostExecute(String result) {
	    		       
	    	if(!"".equals(result)) {
	    	   
	    		if(progress != null) {
	    			progress.dismiss();
						progress = null;
	    		}
	    		
	    		if("exception".equals(result)) {
	    			Toast.makeText(CameraActivity.this, "Getting error while fetching google contact. Please check your Internet connection and try again.", Toast.LENGTH_LONG).show();
	    		} else {
	    			Utils.saveStringPreferences(CameraActivity.this, Utils.KEY_EMAIL_TEXT, result);
	    		}
	       }
	    }

	    @Override
	    protected void onPreExecute() {
	    	//getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	    	if(progress == null) {
	    		progress = ProgressDialog.show(CameraActivity.this, "Fetching google contact", "Please wait...");
	    		progress.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	    		//progress.set
	    	}
	    }

	    @Override
	    protected void onProgressUpdate(Void... values) {}
		@Override
		protected void onCancelled() {
			super.onCancelled();
			if(progress != null) {
				progress.dismiss();
				progress = null;
			}
		}
	}

}
