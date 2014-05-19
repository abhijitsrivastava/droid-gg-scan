package com.eduglasses.glassscan.dto;

import java.util.ArrayList;
import java.util.List;

public class UserDetailsDTO {

	private String errorMsg;
	private String email;
	private String password;
	private List<String> contacts = new ArrayList<String>();
	
	public String getEmail() {
		return email;
	}
	public String getPassword() {
		return password;
	}
	public List<String> getContacts() {
		return contacts;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public void setContacts(List<String> contacts) {
		this.contacts = contacts;
	}
	public String getErrorMsg() {
		return errorMsg;
	}
	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}
}
