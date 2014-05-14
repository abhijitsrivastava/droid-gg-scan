package com.eduglasses.glassscan.service;

import com.eduglasses.glassscan.domain.User;

public interface UserService {

	public void registerUser(User user);
	
	public User getUserByEmail(String email);
	
	public User authenticateUser(String email, String password);
}
