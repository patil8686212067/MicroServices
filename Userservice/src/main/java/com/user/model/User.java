package com.user.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import com.user.response.UserDTO;

@Entity
@DynamicUpdate(value = true)
@Table(name = "User")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class User implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GenericGenerator(name = "user", strategy = "increment")
	@GeneratedValue(generator = "user")
	private int userId;
	private String name;
	private String email;
	private String password;
	private String mobileNumber;
	private boolean isActivated;
	private String role;
	private String picUrl;

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		if(name!=null) {
		this.name = name;
		}
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		if (email != null) {
			this.email = email;
		}
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		if(password!=null)
		{
			this.password=password;
		}
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		if (mobileNumber != null) {
			this.mobileNumber = mobileNumber;
		}
	}

	public boolean isActivated() {
		return isActivated;
	}

	public void setActivated(boolean isActivated) {
		
		this.isActivated = isActivated;
		
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		if(role!=null) {
		this.role = role;
		}
	}

	public String getPicUrl() {
		return picUrl;
	}

	public void setPicUrl(String picUrl) {
		if(picUrl!=null) {
			this.picUrl = picUrl;
		}
		this.picUrl = picUrl;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public User() {
	}

	public User(UserDTO userDTO) {
		this.setName(userDTO.getName());
		this.setEmail(userDTO.getEmail());
		this.setMobileNumber(userDTO.getMobileNumber());
		this.setRole(userDTO.getRole());
	}

	public User(UpdateUserDTO updateUserDTO) {

		this.setUserId(updateUserDTO.getUserId());
		this.setName(updateUserDTO.getName());
		this.setEmail(updateUserDTO.getEmail());
		this.setPassword(updateUserDTO.getPassword());
		this.setMobileNumber(updateUserDTO.getMobileNumber());
		this.setActivated(updateUserDTO.isActivated());
		this.setRole(updateUserDTO.getRole());
		this.setPicUrl(updateUserDTO.getPicUrl());

	}

}
