package com.eduglasses.glassscan.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.eduglasses.glassscan.dao.ContactDao;
import com.eduglasses.glassscan.domain.Contact;
import com.eduglasses.glassscan.service.ContactService;

//@Service("contactService")
public class ContactServiceImpl implements ContactService {

	//@Autowired
	private ContactDao contactDao;
	
	@Override
	public void saveContact(Contact contact) {
		contactDao.saveContact(contact);
	}

	public ContactDao getContactDao() {
		return contactDao;
	}

	public void setContactDao(ContactDao contactDao) {
		this.contactDao = contactDao;
	}
}
