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
import com.eduglasses.glassscan.util.GoogleContactsAPI;
import com.google.gdata.data.contacts.ContactEntry;
import com.google.gdata.data.extensions.Email;

@Controller
public class ContactsController {
	
	@RequestMapping(value = "/selectcontact", method = RequestMethod.GET)
	public String getLoginPage(ModelMap map) {
		SelectContactForm form = new SelectContactForm();
		map.addAttribute("selectContactForm", form);
		return "selectContact";
	}


}
