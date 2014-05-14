package com.eduglasses.glassscan.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.eduglasses.glassscan.dao.UserDao;
import com.eduglasses.glassscan.domain.User;
import com.eduglasses.glassscan.service.UserService;

//@Service("userService")
public class UserServiceImpl implements UserService {

	//@Autowired
	private UserDao userDao;
	
	@Override
	public void registerUser(User user) {
		userDao.registerUser(user);
	}

	@Override
	public User getUserByEmail(String email) {
		return userDao.getUserByEmail(email);
	}
	
	@Override
	public User authenticateUser(String email, String password) {
		return userDao.authenticateUser(email, password);
	}
	
	public UserDao getUserDao() {
		return userDao;
	}

	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}
}
