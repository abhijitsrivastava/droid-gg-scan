package com.eduglasses.glassscan.domain;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "user")
public class User implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue
	@Column(name = "user_id")
	private long userId;
	
	@Column(name = "email", nullable = false, unique = true, length = 70)
	private String email;
	
	@Column(name = "password", length = 50)
	private String password;

	@OneToMany(fetch = FetchType.EAGER, mappedBy = "user")
	private Set<Contact> contacts = new LinkedHashSet<Contact>();
	
	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public Set<Contact> getContacts() {
		return contacts;
	}

	public void setContacts(Set<Contact> contacts) {
		this.contacts = contacts;
	}

	@Override
	public String toString() {
		return "User [user_id =" + userId + ", email =" + email + "]";
	}

}

