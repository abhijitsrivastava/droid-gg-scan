package com.eduglasses.utils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.gdata.client.Query;
import com.google.gdata.client.contacts.ContactsService;
import com.google.gdata.data.contacts.ContactEntry;
import com.google.gdata.data.contacts.ContactFeed;

/**
 * Google Contacts API as a singleton.
 * 
 * @author Mohamed Mansour
 * @since 2010-06-23
 */
public class GoogleContactsAPI {
	private static GoogleContactsAPI obj;
	private final ContactsService service;
	private static final String DEFAULT_FEED = "http://www.google.com/m8/feeds/contacts/";
	private URL feedUrl;

	private String emailAddress = null;

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public boolean isUpdated() {
		return isUpdated;
	}

	public void setUpdated(boolean isUpdated) {
		this.isUpdated = isUpdated;
	}

	private boolean isUpdated = false;

	/**
	 * Single instance.
	 * 
	 * @return This object.
	 */
	public static GoogleContactsAPI getInstance() {
		if (obj == null) {
			obj = new GoogleContactsAPI();
		}
		return obj;
	}

	private GoogleContactsAPI() {
		service = new ContactsService("Google-Contacts-Management");
	}

	/**
	 * Login into Google Accounts.
	 * 
	 * @param username
	 *            Your Google email address.
	 * @param password
	 *            Your Google password.
	 * @return
	 */
	public boolean Login(String access_token) {
		try {
			GoogleCredential gc = new GoogleCredential();

			gc.setAccessToken(access_token);

			service.setOAuth2Credentials(gc);

			feedUrl = new URL(
					"https://www.google.com/m8/feeds/contacts/default/full");

			return true;
		} catch (MalformedURLException e) {
			return false;
		}
	}

	/**
	 * Get the total contact entries.
	 * 
	 * @return number of entries.
	 */
	public int getTotalEntries() {
		Query query = new Query(feedUrl);
		try {
			ContactFeed feed = service.query(query, ContactFeed.class);

			return feed.getTotalResults();
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	/**
	 * Fetch all contacts.
	 * 
	 * @param callback
	 *            Callback that gets fired everytime a request has been
	 *            recieved.
	 * @return List of contacts.
	 * @throws Exception
	 *             In case an exception occurred.
	 */
	public List<ContactEntry> getEntries() throws Exception {
		ContactFeed feed;
		Query query = new Query(feedUrl);
		List<ContactEntry> googleContacts = new ArrayList<ContactEntry>();
		do {
			feed = service.query(query, ContactFeed.class);
			System.out.println("persons email"
					+ feed.getAuthors().get(0).getEmail());
			googleContacts.addAll(feed.getEntries());
			query.setStartIndex(feed.getEntries().size()
					+ query.getStartIndex());
			// Breaking the loop ones we get the auther email adddress.
			break;
		} while (feed.getTotalResults() > query.getStartIndex());

		setEmailAddress(feed.getAuthors().get(0).getEmail());
		return googleContacts;
	}

	public void destroy() {
		obj = null;
	}
}
