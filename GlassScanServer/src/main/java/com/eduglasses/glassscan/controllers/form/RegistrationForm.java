package com.eduglasses.glassscan.controllers.form;

import org.hibernate.validator.constraints.NotEmpty;

public class RegistrationForm {

	@NotEmpty(message="Verification code must not be empty") 
	private String code;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
}
