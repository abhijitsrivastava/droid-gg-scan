package com.eduglasses.glassscan.controllers.form;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

public class AddContactForm {

	@NotEmpty(message="email must not be empty") 
	@Email(message="email format is incorrect")
	private String contactEmail;

	public String getContactEmail() {
		return contactEmail;
	}

	public void setContactEmail(String contactEmail) {
		this.contactEmail = contactEmail;
	}

}
