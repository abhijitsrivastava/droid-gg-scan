package com.eduglasses.glassscan.controllers;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.eduglasses.glassscan.dto.UserDetailsDTO;

@Controller
public class SignOutController {

	private static final Logger logger = Logger
			.getLogger(SignOutController.class);

	@RequestMapping(value = "/signOut", method = RequestMethod.GET)
	public String signOut(HttpSession session) {
		logger.info("Signing out user :"
				+ ((UserDetailsDTO) session.getAttribute("user")).getEmail());
		session.invalidate();
		return "redirect:/registration.htm";
	}


}
