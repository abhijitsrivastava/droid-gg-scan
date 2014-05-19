package com.eduglasses.glassscan.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "contact")
public class Contact implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue
	@Column(name = "contact_id")
	private long contactId;
	
	@Column(name = "contact_email", length = 70)
	private String contactEmail;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	public long getContactId() {
		return contactId;
	}

	public String getContactEmail() {
		return contactEmail;
	}

	public User getUser() {
		return user;
	}

	public void setContactId(long contactId) {
		this.contactId = contactId;
	}

	public void setContactEmail(String contactEmail) {
		this.contactEmail = contactEmail;
	}

	public void setUser(User user) {
		this.user = user;
	}
}
