package com.eduglasses.glassscan.dao;

import com.eduglasses.glassscan.domain.User;


public interface UserDao {

	public void registerUser(User user);
	
	public User getUserByEmail(String email);
	
	public User authenticateUser(String email, String password);
}
