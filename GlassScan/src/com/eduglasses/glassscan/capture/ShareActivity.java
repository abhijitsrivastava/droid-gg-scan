package com.eduglasses.glassscan.capture;

import java.io.File;
import java.io.FileOutputStream;
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
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.eduglass.utils.GmailSender;
import com.eduglass.utils.Utils;
import com.eduglasses.glassscan.R;
import com.google.android.glass.media.Sounds;
import com.google.android.glass.touchpad.Gesture;
import com.google.android.glass.touchpad.GestureDetector;
import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfWriter;

public class ShareActivity extends Activity implements RecognitionListener {
	
	private static final String TAG = "GlassScan";
	private static final int SPEECH_REQUEST = 0;
	private int speechFlag = 11;
	private GestureDetector mGestureDetector;
	//private Bitmap captureImage;
	private byte[] imageByte;
	private AudioManager mAudioManager;
	private String pdfPath;
	private ProgressDialog progressDialog;
	List<Contact> contactList;
	//private String email;
	private String subject;
	private SpeechRecognizer mSpeechRecognizer = null;
	private TextView textview;
	private String username;
	private String password;
	private String receipent;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_share);
		
		username = Utils.getStringPreferences(this, Utils.KEY_USERNAME);
		password = Utils.getStringPreferences(this, Utils.KEY_PASSWORD);
		Log.d(TAG, username + "/" + password);
		String[] emails = Utils.getStringPreferences(this, Utils.KEY_EMAIL_TEXT).split(",");
		
		contactList = new ArrayList<Contact>();
		for (int i = 0; i < emails.length; i++) {
			contactList.add(new Contact(emails[i], emails[i]));
			Log.d(TAG, emails[i]);
		}
		/*contactList.add(new Contact("amit", "eshankarprasad@gmail.com"));
		contactList.add(new Contact("Abhijit Shrivastva", "dayaqait@gmail.com"));*/
		
		mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		mGestureDetector = createGestureDetector(this);
		
		textview = (TextView) findViewById(R.id.textview);
		
		Bundle bundle = getIntent().getExtras();
		imageByte = bundle.getByteArray("image");
		
		if (imageByte != null) {
			Bitmap captureImage = BitmapFactory.decodeByteArray(imageByte, 0, imageByte.length);
			ImageView imageView = (ImageView) findViewById(R.id.imageview);
			imageView.setImageBitmap(captureImage);
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		Log.d(TAG, "onResume");
		initializeSpeechRecognizer();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		Log.d(TAG, "onDestroy");
		destroySpeechRecognizer();
	}
	
	private void initializeSpeechRecognizer() {
		
		Log.d(TAG, "Inside initializeSpeechRecognizer " + speechFlag + " " + mSpeechRecognizer);
		if(mSpeechRecognizer == null && speechFlag == 11) {
			
			mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(getApplicationContext());
			mSpeechRecognizer.setRecognitionListener(this);
			Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
			//intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "pt");
			intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
			mSpeechRecognizer.startListening(intent);
			Log.d(TAG, "Start listing..." + SpeechRecognizer.isRecognitionAvailable(getApplicationContext()));
		}
	}
	
	private void destroySpeechRecognizer() {
		
		if(mSpeechRecognizer != null) {
			
			mSpeechRecognizer.stopListening();
			mSpeechRecognizer.destroy();
			mSpeechRecognizer = null;
		}
	}
	private GestureDetector createGestureDetector(Context context) {
	    
		GestureDetector gestureDetector = new GestureDetector(context);
	    //Create a base listener for generic gestures
	    gestureDetector.setBaseListener( new GestureDetector.BaseListener() {
            @Override
            public boolean onGesture(Gesture gesture) {
                if (gesture == Gesture.TAP) {                	
                	Log.d(TAG, "Gesture.TAP");
                	mAudioManager.playSoundEffect(Sounds.TAP);
                	createAndSharePDF();
                	destroySpeechRecognizer();
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
            public boolean onScroll(float displacement, float delta, float velocity) {
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
	
	private void createAndSharePDF() {
		
		Log.d(TAG, "createAndSharePDF");
		Log.d(TAG, "Speech flag : " + speechFlag);
		Document document = new Document();
		
		String eduDirectoryPath = Environment.getExternalStorageDirectory() + File.separator + "eduscan";
		
		File dir = new File(eduDirectoryPath);
		if(!dir.exists()) {
			dir.mkdir();
		}
		Log.d(TAG, dir.getAbsolutePath());
		
		pdfPath = dir.getAbsolutePath() + File.separator + "glassscan.pdf";
		
		Log.d(TAG, pdfPath);
	    try {
	    	
	    	File pdfFile = new File(pdfPath);
	    	if(pdfFile.exists()) {
	    		Log.d(TAG, "File exist: " + pdfPath);
	    	} 
	    	
	    	pdfFile.createNewFile();
	    	
	        PdfWriter.getInstance(document, new FileOutputStream(pdfFile));
	        document.open();

	        Image image = Image.getInstance(imageByte);
	        image.scalePercent(40);
	        document.add(image);
	        
	        /*String imageUrl = "file://" + picturePath;
	        Log.d(TAG, imageUrl);
	        Image image2 = Image.getInstance(new URL(imageUrl));
	        document.add(image2);*/

	        document.close();
	        
	        //displaySpeechRecognizer();
	    } catch(Exception e){
	      e.printStackTrace();
	    }
	    
	    /*final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
	    emailIntent.setType("plain/text");
	    emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[] { "eshankarprasad@gmail.com" });
	    emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "EduGlasses-Glass Scan");
	    emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Email Body");
	    emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(pdfUrl));
	    startActivity(Intent.createChooser(emailIntent, "Send mail..."));*/
	    
	    /*progress.setTitle("Sending email");
	    
	    
			// Sending email
			Thread t = new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {   
		            GmailSender sender = new GmailSender("qaitdev@gmail.com", "testing1");
		            sender.addAttachment(pdfPath, "PDF");
		            sender.sendMail("Edu-Glass Scan",   
		                    "PDF is attached",   
		                    "qaitdev@gmail.com",   
		                    "eshankarprasad@gmail.com");
		            
		            Log.d(TAG, "Email sent successfully");
		            mAudioManager.playSoundEffect(Sounds.SUCCESS);
		            progress.dismiss();
		            ShareActivity.this.finish();
		        } catch (Exception e) {   
		            Log.e(TAG, e.getMessage(), e);
		            progress.dismiss();
		            ShareActivity.this.finish();
		            Toast.makeText(ShareActivity.this, "Error occurred, try again.", Toast.LENGTH_LONG).show();
		        }
			}
		});
	    t.start();*/
	    
	    // Try to fetching email id from glass
	    /*
	    Thread contactThread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				
				String name;

	            ContentResolver cr = getContentResolver();
	            Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null,null, null, null);
	            if (cur.getCount() > 0) {
	            	
	                ArrayList<String> emailNameList=new ArrayList<String>();
	                ArrayList<String> emailPhoneList=new ArrayList<String>();
	                while (cur.moveToNext()) {
	                	
	                    String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
	                    name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

	                    Cursor emails = getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI,null,
	                            ContactsContract.CommonDataKinds.Email.CONTACT_ID+ " = " + id, null, null);
	                    while (emails.moveToNext()) {
	                    	
	                        // This would allow you get several email addresses
	                        String emailAddress = emails.getString(emails.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
	                        Log.v(TAG, emailAddress);
	                        if ((!emailAddress.equalsIgnoreCase(""))&&(emailAddress.contains("@"))) {
	                        	
	                            emailNameList.add(name);
	                            emailPhoneList.add(emailAddress);
	                        }
	                    }
	                    emails.close();         
	                }
	            }
			}
		});
	    contactThread.start();
	    */
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
					Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
					intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
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

			List<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
			String spokenText = results.get(0).toLowerCase();
			
			if(speechFlag == 12 && !spokenText.equals("")) {
				
				// Got subject line
				subject = spokenText;
				showToast("Select receipent e-mail");
				// Open menu to select email
				openOptionsMenu();
				
			} else if(spokenText.contains("drive")) {

				// Flag for handling drive
				speechFlag = 2;
				
			} else {
				
				// Error flag
				speechFlag = 10;
			}
		} else {
			
			speechFlag = 11;
			destroySpeechRecognizer();
		}
	}
	
	private void showToast(final String message) {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				Toast.makeText(ShareActivity.this, message,
						Toast.LENGTH_LONG).show();
			}
		});
	}

	private void showProgressDialog(final String title, final String message) {

		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				progressDialog = ProgressDialog.show(ShareActivity.this,
						title, message);
			}
		});
	}

	private void dissmissProgress() {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				if (progressDialog != null) {
					progressDialog.dismiss();
				}
			}
		});
	}
	
	private Contact getContact(String name) {
		
		Contact result = null;
		for(int i=0; i<contactList.size(); i++) {
			
			if(name.equalsIgnoreCase(contactList.get(i).getName())) {
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
					GmailSender sender = new GmailSender(username, password);
					sender.addAttachment(pdfPath, "PDF");
					sender.sendMail(subject, "", username, receipent);
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
			}
		});
		t.start();
	}
	
	// Start implementing RecognizerListener methods
	@Override
	public void onRmsChanged(float rmsdB) {
		//Log.d(TAG, "onRmsChanged");
	}
	
	@Override
	public void onResults(Bundle results) {
		
		Log.d(TAG, "onResults");
		ArrayList<String> strlist = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
		for (int i = 0; i < strlist.size(); i++ ) {
			Log.d(TAG, "result = " + strlist.get(i));
		}
		String spokenText = "";
		if(strlist.size() > 0) {
			spokenText = strlist.get(0);
		}
		
		if(spokenText.contains("mail")) {
			mAudioManager.playSoundEffect(Sounds.SUCCESS);
			destroySpeechRecognizer();
			createAndSharePDF();
			speechFlag = 11;
			displaySpeechRecognizer();
		}
	}
	
	@Override
	public void onReadyForSpeech(Bundle params) {
		Log.d(TAG, "onReadyForSpeech");
	}
	
	@Override
	public void onPartialResults(Bundle partialResults) {
		Log.d(TAG, "onPartialResults");
	}
	
	@Override
	public void onEvent(int eventType, Bundle params) {
		Log.d(TAG, "onEvent");		
	}
	
	@Override
	public void onError(int error) {
		Log.e(TAG, "onError : " + error);
		speechFlag = 11;
		destroySpeechRecognizer();
		initializeSpeechRecognizer();
	}
	
	@Override
	public void onEndOfSpeech() {
		Log.d(TAG, "onEndOfSpeech");		
	}
	
	@Override
	public void onBufferReceived(byte[] buffer) {
		Log.d(TAG, "onBufferReceived");		
	}
	
	@Override
	public void onBeginningOfSpeech() {
		Log.d(TAG, "onBeginningOfSpeech");		
	}
	// End implementing RecognizerListener methods
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /*MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.magic, menu);*/
		
		for (int i = 0; i < contactList.size(); i++) {
			menu.add(0, i + 1, Menu.NONE, contactList.get(i).getEmail());
		}
	    
        Log.d("Creating options menu", "True");
        return true;
    }
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	
		Log.d(TAG, item.getItemId()+"/" + item.getTitle());
		receipent = item.getTitle().toString();
        mAudioManager.playSoundEffect(Sounds.TAP);
        closeOptionsMenu();
        sendEmail();
        
        return(super.onOptionsItemSelected(item));
    }
	
	@Override
    public boolean onPrepareOptionsMenu(Menu menu) {
		
        Log.d("Preparing options menu", "True");
        return super.onPrepareOptionsMenu(menu);
    }
}
