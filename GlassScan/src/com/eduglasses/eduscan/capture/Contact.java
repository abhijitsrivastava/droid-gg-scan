package com.eduglasses.eduscan.capture;

public class Contact {

	private String name;
	private String email;
	
	public Contact(String name, String email) {
		super();
		this.name = name;
		this.email = email;
	}
	
	public Contact(Contact contact) {
		
		this.name = contact.getName();
		this.email = contact.getEmail();
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
}
