package com.eduglasses.glassscan.dao.impl;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import com.eduglasses.glassscan.dao.UserDao;
import com.eduglasses.glassscan.domain.User;
import com.eduglasses.glassscan.exception.GlassScanException;
import com.eduglasses.glassscan.util.GlassScanUtil;

//@Repository("userDao")
public class UserDaoImpl extends GenericDaoImpl<User, Long> implements UserDao {

	private static final Logger logger = Logger.getLogger(UserDaoImpl.class);
	
	public void registerUser(User user) {
		Session session = null;
		try {
			session = getSessionFactory().openSession();
			session.save(user);
		} catch (Exception e) {
			logger.fatal("User: " + user + " "+ GlassScanUtil.getExceptionDescriptionString(e));
			throw new GlassScanException();
		} finally {
			session.flush();
			session.close();
		}
	}
	
	@Override
	public User getUserByEmail(String email) {
		Session session = null;
		User user = null;
		try {
			session = getSessionFactory().openSession();
			String queryString = "from User where email = :email";
			Query query = session.createQuery(queryString);
			query.setString("email", email);
			user = (User) query.uniqueResult();
		} catch (Exception e) {
			logger.fatal("Email: " + email + " "
					+ GlassScanUtil.getExceptionDescriptionString(e));
		} finally {
			session.close();
		}
		return user;
	}

	@Override
	public User authenticateUser(String email, String password) {
		Session session = null;
		User user = null;
		try {
			session = getSessionFactory().openSession();
			String queryString = "from User where email = :email and password = :pwd";
			Query query = session.createQuery(queryString);
			query.setString("email", email);
			query.setString("pwd", password);
			user = (User) query.uniqueResult();
		} catch (Exception e) {
			logger.info("Login falied for Email=" + email + " and Password="
					+ "" + password);
			logger.fatal(GlassScanUtil.getExceptionDescriptionString(e));
		} finally {
			session.close();
		}
		return user;
	}
}
