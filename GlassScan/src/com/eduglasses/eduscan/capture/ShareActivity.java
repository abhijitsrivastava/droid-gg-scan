package com.eduglasses.eduscan.capture;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.text.SpannableStringBuilder;
import android.text.style.AbsoluteSizeSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.eduglass.utils.OAuth2Authenticator;
import com.eduglass.utils.Session;
import com.eduglass.utils.Utils;
import com.eduglasses.eduscan.R;
import com.github.barcodeeye.scan.CaptureQRCodeActivity;
import com.google.android.glass.media.Sounds;
import com.google.android.glass.touchpad.Gesture;
import com.google.android.glass.touchpad.GestureDetector;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfWriter;

public class ShareActivity extends Activity {

	private static final String TAG = "GlassScan";
	private static final int SPEECH_REQUEST = 0;
	private int speechFlag = 11;
	private GestureDetector mGestureDetector;

	private byte[] imageByte;
	private AudioManager mAudioManager;
	private String pdfPath;
	private ProgressDialog progressDialog;
	List<Contact> contactList;

	private String subject;
	private SpeechRecognizer mSpeechRecognizer = null;
	private String username;
	private String password;
	private String receipent;
	private Activity activity;
	private boolean shareOptionFlag = true;
	private boolean onOptionsItemSelectedFlag = false;
	private View progress;

	private String updateContacts = "Update Contacts";

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_layout_share);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		activity = this;

		progress = findViewById(R.id.progress);

		username = Utils.getStringPreferences(this, Utils.KEY_USERNAME);
		password = Utils.getStringPreferences(this, Utils.KEY_PASSWORD);
		Log.d(TAG, username + "/" + password);
		String[] emails = Utils
				.getStringPreferences(this, Utils.KEY_EMAIL_TEXT).split(",");

		contactList = new ArrayList<Contact>();
		for (int i = 0; i < emails.length; i++) {
			contactList.add(new Contact(emails[i], emails[i]));
			Log.d(TAG, emails[i]);
		}
		/*
		 * contactList.add(new Contact("amit", "eshankarprasad@gmail.com"));
		 * contactList.add(new Contact("Abhijit Shrivastva",
		 * "dayaqait@gmail.com"));
		 */

		mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		mGestureDetector = createGestureDetector(this);
		initializeSpeechRecognizer();

		imageByte = Session.getInstant().getImageBytes();

		if (imageByte != null) {
			Bitmap captureImage = BitmapFactory.decodeByteArray(imageByte, 0,
					imageByte.length);
			ImageView imageView = (ImageView) findViewById(R.id.imageview);
			imageView.setImageBitmap(captureImage);
		}

		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			subject = bundle.getString("subject");
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		// shareOptionFlag = true;
		Log.d(TAG, "onResume");
		// initializeSpeechRecognizer();
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (subject != null) {
					shareOptionFlag = false;
					// Open menu to select email
					openOptionsMenu();
					showToast("Swipe left/right to scroll");
				}

			}
		}, 1000);

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		Log.d(TAG, "onDestroy");
		destroySpeechRecognizer();
		mAudioManager = null;
		mGestureDetector = null;
		contactList = null;
	}

	private void initializeSpeechRecognizer() {

		if (mSpeechRecognizer == null) {

			mSpeechRecognizer = SpeechRecognizer
					.createSpeechRecognizer(getApplicationContext());

			Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
			// intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "pt");
			intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
					RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		}
	}

	private void destroySpeechRecognizer() {

		if (mSpeechRecognizer != null) {

			mSpeechRecognizer.stopListening();
			mSpeechRecognizer.destroy();
			mSpeechRecognizer = null;
		}
	}

	private GestureDetector createGestureDetector(Context context) {

		GestureDetector gestureDetector = new GestureDetector(context);
		// Create a base listener for generic gestures
		gestureDetector.setBaseListener(new GestureDetector.BaseListener() {
			@Override
			public boolean onGesture(Gesture gesture) {
				if (gesture == Gesture.TAP) {
					Log.d(TAG, "Gesture.TAP");
					mAudioManager.playSoundEffect(Sounds.TAP);
					openOptionsMenu();
					showToast("Swipe left/right to scroll");
					// createAndSharePDF();
					// destroySpeechRecognizer();
					return true;
				}
				return false;
			}
		});

		gestureDetector.setFingerListener(new GestureDetector.FingerListener() {
			@Override
			public void onFingerCountChanged(int previousCount, int currentCount) {
				// do something on finger count changes
			}
		});

		gestureDetector.setScrollListener(new GestureDetector.ScrollListener() {
			@Override
			public boolean onScroll(float displacement, float delta,
					float velocity) {
				// do something on scrolling
				return true;
			}
		});
		return gestureDetector;
	}

	/*
	 * Send generic motion events to the gesture detector
	 */
	@Override
	public boolean onGenericMotionEvent(MotionEvent event) {
		if (mGestureDetector != null) {
			return mGestureDetector.onMotionEvent(event);
		}
		return false;
	}

	private void createAndSharePDF(boolean sendEmail) {

		Log.d(TAG, "createAndSharePDF");
		Log.d(TAG, "Speech flag : " + speechFlag);
		Document document = new Document();

		String eduDirectoryPath = Environment.getExternalStorageDirectory()
				+ File.separator + "eduscan";

		File dir = new File(eduDirectoryPath);
		if (!dir.exists()) {
			dir.mkdir();
		}
		Log.d(TAG, dir.getAbsolutePath());

		pdfPath = dir.getAbsolutePath() + File.separator + "glassscan.pdf";

		Log.d(TAG, pdfPath);
		try {

			File pdfFile = new File(pdfPath);
			if (pdfFile.exists()) {
				Log.d(TAG, "File exist: " + pdfPath);
			}

			pdfFile.createNewFile();
			PdfWriter.getInstance(document, new FileOutputStream(pdfFile));

			Image image = Image.getInstance(imageByte);
			image.scalePercent(40);

			document.open();
			document.add(image);

			document.close();
			speechFlag = 11;

			if (sendEmail) {
				displaySpeechRecognizer();
			} else {
				uploadToDrive();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void displaySpeechRecognizer() {

		runOnUiThread(new Runnable() {

			@Override
			public void run() {

				String promptText = "";
				switch (speechFlag) {

				case 11: // After speaking result "Email"
					promptText = "Speak subject line";
					speechFlag = 12;
					Intent intent = new Intent(
							RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
					intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
							RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
					intent.putExtra(RecognizerIntent.EXTRA_PROMPT, promptText);
					startActivityForResult(intent, SPEECH_REQUEST);
					break;

				case 2: // After speaking result "Drive"
					promptText = "";
					showToast("Service not available");
					break;

				case 10:
					promptText = "";
					showToast("Invalid voice command");
					speechFlag = 11;
					break;
				default:
					break;
				}
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == SPEECH_REQUEST && resultCode == RESULT_OK) {

			List<String> results = data
					.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
			String spokenText = results.get(0).toLowerCase();

			if (speechFlag == 12 && !spokenText.equals("")) {

				// Got subject line
				subject = spokenText;
				shareOptionFlag = false;

				// Open menu to select email
				openOptionsMenu();
				showToast("Swipe left/right to scroll");

			} else {

				// Error flag
				speechFlag = 10;
			}
		} else {

			speechFlag = 11;
			shareOptionFlag = true;
			destroySpeechRecognizer();
		}
	}

	private void showToast(final String message) {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				Toast.makeText(ShareActivity.this, message, Toast.LENGTH_LONG)
						.show();
			}
		});
	}

	private void showProgressDialog(final String title, final String message) {

		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				/*
				 * progressDialog = ProgressDialog.show(ShareActivity.this,
				 * title, message);
				 */
				progress.setVisibility(View.VISIBLE);
				/*
				 * progressDialog.getWindow().addFlags(
				 * WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
				 */

			}
		});
	}

	private void dissmissProgress() {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				/*
				 * if (progressDialog != null) { progressDialog.dismiss(); }
				 */
				if (progress.getVisibility() == View.VISIBLE) {
					progress.setVisibility(View.GONE);
				}

			}
		});
	}

	private Contact getContact(String name) {

		Contact result = null;
		for (int i = 0; i < contactList.size(); i++) {

			if (name.equalsIgnoreCase(contactList.get(i).getName())) {
				result = new Contact(contactList.get(i));
				break;
			}
		}

		return result;
	}

	private void sendEmail() {

		speechFlag = 11;
		destroySpeechRecognizer();
		Log.d(TAG, subject);
		Log.d(TAG, receipent);
		Log.d(TAG, pdfPath);

		// Sending email
		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {

				showProgressDialog("Sending e-mail", "Please wait...");

				try {
					OAuth2Authenticator auth2Authenticator = new OAuth2Authenticator(
							Utils.getStringPreferences(activity,
									Utils.KEY_USERNAME),
							Utils.getStringPreferences(activity,
									Utils.KEY_ACCESS_TOKEN));
					auth2Authenticator.addAttachment(pdfPath);

					Thread.currentThread().setContextClassLoader(
							getClass().getClassLoader());

					auth2Authenticator.sendMail(subject, receipent);

					mAudioManager.playSoundEffect(Sounds.SUCCESS);
					dissmissProgress();
					showToast("Email sent successfully");
					ShareActivity.this.finish();
				} catch (Exception e) {
					Log.e(TAG, e.getMessage(), e);
					dissmissProgress();
					ShareActivity.this.finish();
					showToast("Error occurred, try again");
				}

				shareOptionFlag = true;
			}
		});
		t.start();
	}

	private void uploadToDrive() {
		Log.d(TAG, "uploadToDrive");
		shareOptionFlag = true;
		speechFlag = 11;
		destroySpeechRecognizer();

		Thread uploadToDriveThread = new Thread(new Runnable() {

			@Override
			public void run() {

				try {
					showProgressDialog("Uploading to google drive",
							"Please wait...");
					Utils.insertFile(Utils.getStringPreferences(activity,
							Utils.KEY_ACCESS_TOKEN), "Glass-Scan",
							"Uploaded via Google Glass", "", "application/pdf",
							pdfPath);
					dissmissProgress();
					ShareActivity.this.finish();
					showToast("Uploaded successfully");
				} catch (GeneralSecurityException e) {
					showToast("Error occurred, try again...");
					dissmissProgress();
					Log.e(TAG, e.getMessage());
					e.printStackTrace();
				} catch (IOException e) {
					showToast("Error occurred, try again...");
					dissmissProgress();
					Log.e(TAG, e.getMessage());
					e.printStackTrace();
				} catch (URISyntaxException e) {
					showToast("Error occurred, try again...");
					dissmissProgress();
					Log.e(TAG, e.getMessage());
					e.printStackTrace();
				}

			}
		});
		uploadToDriveThread.start();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		/*
		 * MenuInflater inflater = getMenuInflater();
		 * inflater.inflate(R.menu.magic, menu);
		 */

		Log.d(TAG, "Creating options menu: True");
		return true;
	}

	private CharSequence wrapInSpan(CharSequence value) {
		SpannableStringBuilder sb = new SpannableStringBuilder(value);
		sb.setSpan(new AbsoluteSizeSpan(26), 0, value.length(), 0);
		return sb;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		Log.d(TAG, "onOptionsItemSelected : shareOptionFlag : "
				+ shareOptionFlag);

		Log.d(TAG, item.getItemId() + "/" + item.getTitle());

		onOptionsItemSelectedFlag = true;

		if (shareOptionFlag) {

			createAndSharePDF("e-mail".equalsIgnoreCase("" + item.getTitle()));

		} else {

			receipent = item.getTitle().toString();
			mAudioManager.playSoundEffect(Sounds.TAP);
			closeOptionsMenu();

			if ("".equals(receipent)) {

				showToast("Contact not found");
			} else if (receipent.equalsIgnoreCase(updateContacts)) {
				Intent intent = new Intent(this, CaptureQRCodeActivity.class);
				intent.putExtra("subject", subject);
				startActivity(intent);
				Session.getInstant().setActivity(ShareActivity.this);
				// finish();

			} else {

				Document document = new Document();

				String eduDirectoryPath = Environment
						.getExternalStorageDirectory()
						+ File.separator
						+ "eduscan";

				File dir = new File(eduDirectoryPath);
				if (!dir.exists()) {
					dir.mkdir();
				}
				Log.d(TAG, dir.getAbsolutePath());

				pdfPath = dir.getAbsolutePath() + File.separator
						+ "glassscan.pdf";

				Log.d(TAG, pdfPath);
				try {

					File pdfFile = new File(pdfPath);
					if (pdfFile.exists()) {
						Log.d(TAG, "File exist: " + pdfPath);
					}

					pdfFile.createNewFile();
					PdfWriter.getInstance(document, new FileOutputStream(
							pdfFile));

					Image image = Image.getInstance(imageByte);
					image.scalePercent(40);

					document.open();
					document.add(image);

					document.close();
					speechFlag = 11;
					sendEmail();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (BadElementException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (DocumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		return (super.onOptionsItemSelected(item));
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {

		Log.d(TAG, "Preparing options menu: True");
		Log.d(TAG, "shareOptionFlag: " + shareOptionFlag);
		menu.clear();
		if (shareOptionFlag) {

			menu.add(0, 1, Menu.NONE, "e-mail");
			menu.add(0, 2, Menu.NONE, "Drive");
		} else {

			for (int i = 0; i < contactList.size(); i++) {
				// menu.add(0, i + 1, Menu.NONE, contactList.get(i).getEmail());

				menu.add(0, i + 1, Menu.NONE,
						wrapInSpan(contactList.get(i).getEmail()))
						.setTitleCondensed(contactList.get(i).getEmail());
			}
			menu.add(0, contactList.size(), Menu.NONE,
					wrapInSpan(updateContacts));
		}

		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public void onOptionsMenuClosed(Menu menu) {
		// TODO Auto-generated method stub
		if (onOptionsItemSelectedFlag == false) {
			Log.d(TAG, "Gesture.SWIPE_DOWN ... onOptionsMenuClosed");
			shareOptionFlag = true;
		}

		onOptionsItemSelectedFlag = false;
		super.onOptionsMenuClosed(menu);
	}
}
