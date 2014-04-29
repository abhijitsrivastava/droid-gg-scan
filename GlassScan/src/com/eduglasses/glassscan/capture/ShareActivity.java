package com.eduglasses.glassscan.capture;

import java.io.File;
import java.io.FileOutputStream;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.eduglasses.glassscan.R;
import com.google.android.glass.touchpad.Gesture;
import com.google.android.glass.touchpad.GestureDetector;
import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfWriter;

public class ShareActivity extends Activity {
	
	private static final String TAG = "GlassScan";
	private GestureDetector mGestureDetector;
	//private Bitmap captureImage;
	private byte[] imageByte;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_share);
		mGestureDetector = createGestureDetector(this);
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
		
	}
	
private GestureDetector createGestureDetector(Context context) {
	    
		GestureDetector gestureDetector = new GestureDetector(context);
	    //Create a base listener for generic gestures
	    gestureDetector.setBaseListener( new GestureDetector.BaseListener() {
            @Override
            public boolean onGesture(Gesture gesture) {
                if (gesture == Gesture.TAP) {                	
                	Log.d(TAG, "Gesture.TAP");
                	createAndSharePDF();
                    return true;
                } else if (gesture == Gesture.TWO_TAP) {
                	
                    // do something on two finger tap
                	Log.d(TAG, "Gesture.TWO_TAP");
                    return true;
                } else if (gesture == Gesture.SWIPE_RIGHT) {
                	
                    // do something on right (forward) swipe
                	Log.d(TAG, "Gesture.SWIPE_RIGHT");
                    return true;
                } else if (gesture == Gesture.SWIPE_LEFT) {
                	
                    // do something on left (backwards) swipe
                	Log.d(TAG, "Gesture.SWIPE_LEFT");
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
		Document document = new Document();
		
		String eduDirectoryPath = Environment.getExternalStorageDirectory() + File.separator + "eduscan";
		
		File dir = new File(eduDirectoryPath);
		if(!dir.exists()) {
			dir.mkdir();
		}
		Log.d(TAG, dir.getAbsolutePath());
		
		String pdfUrl = dir.getAbsolutePath() + File.separator + "glassscan.pdf";
		
		Log.d(TAG, pdfUrl);
	    try {
	    	
	    	File pdfFile = new File(pdfUrl);
	    	if(pdfFile.exists()) {
	    		Log.d(TAG, "File exist: " + pdfUrl);
	    	} 
	    	
	    	pdfFile.createNewFile();
	    	
	        PdfWriter.getInstance(document, new FileOutputStream(pdfFile));
	        document.open();

	        Image image = Image.getInstance(imageByte);
	        image.scalePercent(20);
	        document.add(image);
	        
	        /*String imageUrl = "file://" + picturePath;
	        Log.d(TAG, imageUrl);
	        Image image2 = Image.getInstance(new URL(imageUrl));
	        document.add(image2);*/

	        document.close();
	    } catch(Exception e){
	      e.printStackTrace();
	    }
	    
	    final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
	    emailIntent.setType("plain/text");
	    emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[] { "eshankarprasad@gmail.com" });
	    emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "EduGlasses-Glass Scan");
	    emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Email Body");
	    emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(pdfUrl));
	    startActivity(Intent.createChooser(emailIntent, "Send mail..."));

	}
}
