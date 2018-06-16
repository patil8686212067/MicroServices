package com.user.utility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

//import com.userservice.utility.Email;

@Component
public class MailService {

	@Autowired
	EmailProperties emailProperties;
	@Autowired
	private MailSender mailSender;
	@Value("${emailAddress}")
    private String emailAddress;
	
    Email email=new Email();
    
	public void setMailSender(MailSender mailSender) {
		this.mailSender = mailSender;
	}

	@Async
	public boolean sendMail(Email email) {
		System.out.println("At @async " + Thread.currentThread().getName());
		String to=null;
		boolean flag = false;
		SimpleMailMessage message = new SimpleMailMessage();
		try {
			
			if (emailProperties.getEmailAddress() != null && emailProperties.getEmailAddress() != ""
					&& emailProperties.getEmailAddress().isEmpty() == false
					&& !emailProperties.getEmailAddress().equals("null")) {
				System.out.println("Email  :" + emailProperties.getEmailAddress());
				
			  to = emailProperties.getEmailAddress();
			}

			System.out.println("mail send to --> :" +email.getTo());
			message.setFrom(emailProperties.getEmail());
			message.setTo(email.getTo());
			message.setSubject(email.getSubject());
			message.setText(email.getMsg());
			mailSender.send(message);
			flag = true;
		} catch (Exception e) {
			flag = false;
			e.printStackTrace();
		}
		return flag;
	}
}

