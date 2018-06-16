package com.user.response;

public class Response {
	
	 private long statusCode;
	   private String message;
	   
	public long getStatusCode() {
		return statusCode;
	}
	public void setStatusCode(long statusCode) {
		this.statusCode = statusCode;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
	 @Override
	   public String toString()
	   {
	      return "Response [statusCode=" + statusCode + ", message=" + message + "]";
	   }
}
