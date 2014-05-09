package com.eduglass.utils;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.eduglasses.glassscan.capture.CameraActivity;
import com.google.gdata.data.contacts.ContactEntry;
import com.google.gdata.data.extensions.Email;

public class Utils {

	public static final String KEY_USERNAME = "username";
	public static final String KEY_PASSWORD = "password";
	public static final String KEY_EMAIL_TEXT = "emails";
	
	public static void saveStringPreferences(Context context, String key, String value) {
		SharedPreferences sPrefs = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor editor = sPrefs.edit();
		editor.putString(key, value);
		editor.commit();
	}
	
	public static String getStringPreferences(Context context, String key) {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		String savedPref = sharedPreferences.getString(key, "");
		return savedPref;
	}
	
	public static void updateContacts(final Activity context) {
		if (!GoogleContactsAPI.getInstance().isUpdated()) {
			GoogleContactsAPI.getInstance().setUpdated(true);
			new Thread(new Runnable() {
				public void run() {
					String separator = "";
					if (GoogleContactsAPI.getInstance().Login(
							Utils.getStringPreferences(context,
									Utils.KEY_USERNAME),
							Utils.getStringPreferences(context,
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
								Utils.saveStringPreferences(context,
										Utils.KEY_EMAIL_TEXT,
										emailText.toString());
							}							
							Log.d("Utils", "emails :" + emailText.toString());
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}).start();
		}
	}
}
