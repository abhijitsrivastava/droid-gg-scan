package com.eduglasses.glassscan.dao.impl;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import com.eduglasses.glassscan.dao.ContactDao;
import com.eduglasses.glassscan.domain.Contact;
import com.eduglasses.glassscan.exception.GlassScanException;
import com.eduglasses.glassscan.util.GlassScanUtil;

//@Repository("contactDao")
public class ContactDaoImpl extends GenericDaoImpl<Contact, Long> implements ContactDao {

	private static final Logger logger = Logger.getLogger(ContactDaoImpl.class);
	
	@Override
	public void saveContact(Contact contact) {
		Session session = null;
		try {
			session = getSessionFactory().openSession();
			session.save(contact);
		} catch (Exception e) {
			logger.fatal(GlassScanUtil.getExceptionDescriptionString(e));
			throw new GlassScanException();
		} finally {
			session.flush();
			session.close();
		}
	}
}
