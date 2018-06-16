package com.user.exception;

import com.user.response.Response;

public class EmailAlreadyExistsException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public EmailAlreadyExistsException() {
		super("Email already registered");
	}

	public Response getResponse() {
		Response response = new Response();
		//response.setMsg(this.getMessage());
		//response.setStatus(-1);
		response.setMessage(this.getMessage());
		response.setStatusCode(-1);
		return response;
	}
}
