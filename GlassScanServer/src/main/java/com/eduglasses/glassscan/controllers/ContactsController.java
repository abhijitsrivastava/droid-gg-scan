package com.eduglasses.glassscan.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.eduglasses.glassscan.controllers.form.SelectContactForm;

@Controller
public class ContactsController {
	
	@RequestMapping(value = "/selectcontact", method = RequestMethod.GET)
	public String getLoginPage(ModelMap map) {
		SelectContactForm form = new SelectContactForm();
		map.addAttribute("selectContactForm", form);
		return "selectContact";
	}


}
