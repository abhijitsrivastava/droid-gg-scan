package com.eduglasses.glassscan.controllers;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.eduglasses.glassscan.controllers.form.RegistrationForm;
import com.eduglasses.glassscan.controllers.form.SelectContactForm;
import com.eduglasses.glassscan.util.GlassScanUtil;
import com.eduglasses.glassscan.util.GoogleContactsAPI;
import com.eduglasses.glassscan.util.PropertiesFileReaderUtil;
import com.google.gdata.data.contacts.ContactEntry;
import com.google.gdata.data.extensions.Email;

@Controller
public class RegistrationController {
	
	@RequestMapping(value = "/registration", method = RequestMethod.GET)
	public String getLoginPage(ModelMap map) {
		RegistrationForm form = new RegistrationForm();
		map.addAttribute("registrationForm", form);
		return "registration";
	}

	@RequestMapping(value = "/registration", method = RequestMethod.POST)
	public String registerUser(@Valid RegistrationForm registrationForm,
			BindingResult result, ModelMap map, HttpServletRequest request) {

		if (result.hasErrors()) {
			return "registration";
		}		

	
		String code = registrationForm.getCode();
		
		HttpSession httpSession = request.getSession();
		httpSession.setAttribute("code", code);
		
		String urlParameters = "code="+code+"&client_id=523728010956-5ehm6m10hi1vqhpmm3771l9727v3ldko.apps.googleusercontent.com&"+
								"client_secret=-oObGCKTAqT4PyyYW0BbtQtr&redirect_uri=urn:ietf:wg:oauth:2.0:oob&grant_type=authorization_code";
		
		JSONObject response = sendHttpRequest(urlParameters);
		StringBuilder stringBuilder = new StringBuilder("");
		
		if(response!= null){
			List<ContactEntry> contactList= getContactsList(response.optString("access_token"));
			List<String> contactsList= new ArrayList<String>();
			for (ContactEntry contactEntry : contactList) {
				for (Email email : contactEntry.getEmailAddresses()) {
					contactsList.add(email.getAddress().toString());
				}
			}
			HttpSession session = request.getSession();
			session.setAttribute("contactsList", contactsList);
			session.setAttribute("accessToken", response.optString("access_token"));
			session.setAttribute("refreshToken", response.optString("refresh_token"));
			return "redirect:/selectcontact.htm";
		}else{
			return "registration";
		}
		
		/*if(response != null) {
			stringBuilder.append("access_token=").append(response.optString("access_token"));
			stringBuilder.append(",");
			stringBuilder.append("refresh_token=").append(response.optString("refresh_token"));
		} else {
			stringBuilder.append("access_token=");
			stringBuilder.append(",");
			stringBuilder.append("refresh_token=");
		}
		
		String QRCodeFileLoc = PropertiesFileReaderUtil.getApplicationProperty("qr.code.storage.path");
		
		boolean isQRCodeGenerated = GlassScanUtil.generateQRCode(stringBuilder.toString(), code, QRCodeFileLoc);
		
		if(!isQRCodeGenerated) {
			
			String serverURL = PropertiesFileReaderUtil.getApplicationProperty("server.url");
			String QRCodeURL = PropertiesFileReaderUtil.getApplicationProperty("qr.code.url");
			String QRCodeImageURL = serverURL+"/"+QRCodeURL+"/"+ code.replace(".", "_").replace(",", "_").replace("/", "_").replace("\\", "_") + ".png";
			map.addAttribute("QRCodeImageURL",QRCodeImageURL);
		}*/
		
		//return "QRCode";
		
	}
	
	//Get All Email Contacts 
	

	@RequestMapping(value = "/selectedEmailsForQRCode", method = RequestMethod.POST)
	public String emailContacts(SelectContactForm selectContactForm,
			BindingResult result, ModelMap map, HttpServletRequest request) {

		List<String> selectedEmailList = selectContactForm
				.getSelectedEmailList();
		System.out.println("reached");
		HttpSession session = request.getSession();
		String accessTokenString=  session.getAttribute("accessToken").toString();
		String refreshTokenString=  session.getAttribute("refreshToken").toString();
		String code= session.getAttribute("code").toString();
		
		StringBuilder stringBuilder = new StringBuilder("");
		stringBuilder.append(accessTokenString);
		stringBuilder.append(",");
		stringBuilder.append(refreshTokenString);
		stringBuilder.append(",");
		if (null == selectedEmailList) {
			map.addAttribute("noContactSelected", "true");
			return "selectContact";
		} else if (selectedEmailList.size() > 20) {
			map.addAttribute("moreContactSelected", "true");
			return "selectContact";
		} else {
			Iterator<String> itr = selectedEmailList.iterator();
			while (itr.hasNext()) {
				stringBuilder.append(itr.next()).append(",");
			}

			String QRCodeFileLoc = PropertiesFileReaderUtil
					.getApplicationProperty("qr.code.storage.path");
			
			boolean isQRCodeGenerated = GlassScanUtil.generateQRCode(
					stringBuilder.toString(), code, QRCodeFileLoc);

			if (!isQRCodeGenerated) {

				String serverURL = PropertiesFileReaderUtil
						.getApplicationProperty("server.url");
				String QRCodeURL = PropertiesFileReaderUtil
						.getApplicationProperty("qr.code.url");
				String QRCodeImageURL = serverURL
						+ "/"
						+ QRCodeURL
						+ "/"
						+ code.replace(".", "_").replace(",", "_")
								.replace("/", "_").replace("\\", "_") + ".png";
				map.addAttribute("QRCodeImageURL", QRCodeImageURL);
			}
			return "QRCode";
		}
	}
	
	
	private List<ContactEntry> getContactsList(String accessToken){
		List<ContactEntry> contactList = null;
		if (GoogleContactsAPI.getInstance().Login(accessToken)) {
			try {
				contactList = GoogleContactsAPI.getInstance().getEntries();
				} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		return contactList;
	}
	
	
	private JSONObject sendHttpRequest(String urlParameters) {

		System.out.println(urlParameters);

		JSONObject response = null;
		InputStream in = null;
		HttpURLConnection conn = null;
		String jsonString = null;
		OutputStream os = null;

		try {

			String urlString = "https://accounts.google.com/o/oauth2/token";

			URL url = new URL(urlString);
			conn = (HttpsURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			//conn.setRequestProperty("Content-Type", "application/json");
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

			// Send post request
			os = conn.getOutputStream();
			os.write(urlParameters.getBytes());
			os.flush();

			if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
				return null;
			}

			in = new BufferedInputStream(conn.getInputStream());
			jsonString = getStringFromInputStream(in);
			System.out.println("JSON Response : " + jsonString);
			response = new JSONObject(jsonString);
			
		} catch (MalformedURLException e) {

			e.printStackTrace();

		} catch (IOException e) {
			// TODO: handle exception
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (in != null) {
					in.close();
				}
				if (os != null) {
					os.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return response;
	}
	
	/*
	 * Method returns String from input stream.
	 * 
	 * @param is
	 * 
	 * @return jsonString
	 */
	private String getStringFromInputStream(InputStream is) {

		BufferedReader br = null;
		StringBuilder sb = new StringBuilder();

		String line;
		try {

			br = new BufferedReader(new InputStreamReader(is));
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return sb.toString();
	}
	
}
