package com.user.messagesservice;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import com.user.utility.Email;

@Component
public class MessageListener {
	//listen message sending and receiving from FundooEmailQueue
	
	@JmsListener(destination="FundooNoteQueue")
	public void consume(Email email) {
		System.out.println(email);
	 }

}
