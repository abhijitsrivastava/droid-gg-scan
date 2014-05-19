package com.eduglasses.glassscan.controllers.form;

import java.util.List;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

import com.google.gdata.data.contacts.ContactEntry;

public class SelectContactForm {
	private List<ContactEntry> contactsList;
	
	private List<String> selectedEmailList;

	public List<ContactEntry> getContactsList() {
		return contactsList;
	}

	public void setContactsList(List<ContactEntry> contactsList) {
		this.contactsList = contactsList;
	}

	public List<String> getSelectedEmailList() {
		return selectedEmailList;
	}

	public void setSelectedEmailList(List<String> selectedEmailList) {
		this.selectedEmailList = selectedEmailList;
	}
	
}
