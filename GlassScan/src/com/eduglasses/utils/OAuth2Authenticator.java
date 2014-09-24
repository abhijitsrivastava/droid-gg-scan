/* Copyright 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.eduglasses.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Provider;
import java.security.Security;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Logger;

import javax.activation.CommandMap;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.activation.MailcapCommandMap;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.URLName;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import com.sun.mail.smtp.SMTPTransport;

/**
 * Performs OAuth2 authentication.
 * 
 * <p>
 * Before using this class, you must call {@code initialize} to install the
 * OAuth2 SASL provider.
 */
public class OAuth2Authenticator {

	private Session session;
	private Multipart multipart;

	private String email = null;
	private String access_token;

	private static final Logger logger = Logger
			.getLogger(OAuth2Authenticator.class.getName());

	public static final class OAuth2Provider extends Provider {
		private static final long serialVersionUID = 1L;

		public OAuth2Provider() {
			super("Google OAuth2 Provider", 1.0,
					"Provides the XOAUTH2 SASL Mechanism");
			put("SaslClientFactory.XOAUTH2",
					"com.eduglass.utils.OAuth2SaslClientFactory");
		}
	}

	public OAuth2Authenticator(String email, String access_token) {

		this.email = email;
		this.access_token = access_token;
		multipart = new MimeMultipart();

		/**
		 * It sounds like you're running your Java code "in" the database.
		 * JavaMail depends on some configuration files to map MIME types to
		 * Java classes (e.g., "maultipart/mixed" to
		 * "javax.mail.internet.MimeMultipart"). These configuration files are
		 * loaded using the ClassLoader for the application. If the ClassLoader
		 * doesn't function properly, these configuration files won't be found.
		 * 
		 * I vaguely remember hearing about such a problem in the Oracle
		 * database. You should contact support to find out if this is a known
		 * problem and find out if a fix is available.
		 * 
		 * As a workaround, you can try adding the following to your application
		 */
		MailcapCommandMap mc = (MailcapCommandMap) CommandMap
				.getDefaultCommandMap();
		mc.addMailcap("text/html;; x-java-content-handler=com.sun.mail.handlers.text_html");
		mc.addMailcap("text/xml;; x-java-content-handler=com.sun.mail.handlers.text_xml");
		mc.addMailcap("text/plain;; x-java-content-handler=com.sun.mail.handlers.text_plain");
		mc.addMailcap("multipart/*;; x-java-content-handler=com.sun.mail.handlers.multipart_mixed");
		mc.addMailcap("message/rfc822;; x-java-content- handler=com.sun.mail.handlers.message_rfc822");
	}

	/**
	 * Installs the OAuth2 SASL provider. This must be called exactly once
	 * before calling other methods on this class.
	 */
	public static void initialize() {
		Security.addProvider(new OAuth2Provider());
	}

	/**
	 * To Add an Attachment in the Email.
	 * 
	 * @param filename
	 * @param subject
	 * @throws Exception
	 */
	public void addAttachment(String filename) throws Exception {

		// Adding Attachment.
		BodyPart messageBodyPart = new MimeBodyPart();
		DataSource source = new FileDataSource(filename);
		messageBodyPart.setDataHandler(new DataHandler(source));
		messageBodyPart.setFileName("glassscan.pdf");
		multipart.addBodyPart(messageBodyPart);

		// Adding Attachment Subject.
		BodyPart messageBodyPart2 = new MimeBodyPart();
		messageBodyPart2.setText("Sent on Google Glass from GlassScan");
		multipart.addBodyPart(messageBodyPart2);
	}

	/**
	 * Synchronized method to send an Email.
	 * 
	 * @param subject
	 * @param recipients
	 * @throws Exception
	 */
	public synchronized void sendMail(String subject, String recipients)
			throws Exception {
		initialize();
		Properties props = new Properties();
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.starttls.required", "true");
		props.put("mail.smtp.sasl.enable", "true");
		props.put("mail.smtp.sasl.mechanisms", "XOAUTH2");
		props.put("mail.smtp.auth", "true");
		props.put(OAuth2SaslClientFactory.OAUTH_TOKEN_PROP, access_token);

		session = Session.getInstance(props);
		// session.setDebug(true);

		final URLName unusedUrlName = null;
		SMTPTransport transport = new SMTPTransport(session, unusedUrlName);

		// If the password is non-null, SMTP tries to do AUTH LOGIN.
		final String emptyPassword = "";
		transport.connect(Constants.HOST, Constants.PORT, email, emptyPassword);

		// -- Create a new message --
		final MimeMessage msg = new MimeMessage(session);
		DataHandler handler = new DataHandler(new ByteArrayDataSource(
				"".getBytes(), "text/plain"));

		// -- Set the FROM and TO fields --
		msg.setFrom(new InternetAddress(email));
		msg.setRecipients(Message.RecipientType.TO,
				InternetAddress.parse(recipients, false));

		msg.setSubject(subject);

		msg.setDataHandler(handler);
		msg.setContent(multipart);

		transport.sendMessage(msg, msg.getAllRecipients());
		transport.close();

	}

	/**
	 * 
	 * @author qainfotech
	 * 
	 */
	public class ByteArrayDataSource implements DataSource {
		private byte[] data;
		private String type;

		public ByteArrayDataSource(byte[] data, String type) {
			super();
			this.data = data;
			this.type = type;
		}

		public ByteArrayDataSource(byte[] data) {
			super();
			this.data = data;
		}

		public void setType(String type) {
			this.type = type;
		}

		public String getContentType() {
			if (type == null)
				return "application/octet-stream";
			else
				return type;
		}

		public InputStream getInputStream() throws IOException {
			return new ByteArrayInputStream(data);
		}

		public String getName() {
			return "ByteArrayDataSource";
		}

		public OutputStream getOutputStream() throws IOException {
			throw new IOException("Not Supported");
		}
	}
}
