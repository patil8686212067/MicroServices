package com.user.response;

import java.util.List;

import org.springframework.validation.FieldError;

public class UserFieldErrors extends Response{
	List<FieldError> errors;

	public List<FieldError> getErrors() {
		return errors;
	}

	public void setErrors(List<FieldError> errors) {
		this.errors = errors;
	}

}
