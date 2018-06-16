package com.user.service;


import java.io.IOException;


import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.user.exception.EmailAlreadyExistsException;
import com.user.exception.USException;
import com.user.model.UpdateUserDTO;
import com.user.model.User;
import com.user.repository.UserRepository;
import com.user.response.UserDTO;
import com.user.utility.Email;
import com.user.utility.S3Service;
import com.user.utility.TokenUtils;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;
@Service
public class IUserServiceImpl implements IUserService{

	
	@Autowired
	UserRepository userRepository;
	@Autowired
	public com.user.utility.MailService mailService;
	@Autowired 
	TokenUtils tokenUtils;
    @Autowired
	 private S3Service s3Service;

	@Autowired
	JmsTemplate template;
	
	  private final Logger logger = LoggerFactory.getLogger(IUserServiceImpl.class);
	@Transactional
	public void save(UserDTO userDTO, String requestURL) {

		
		
		User userFromDB =userRepository.findByEmail(userDTO.getEmail());
	
		if (userFromDB != null) 
		{
			throw new EmailAlreadyExistsException();//for duplicate mail
		}

		User user = new User(userDTO);
		
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		String hashCode = passwordEncoder.encode(userDTO.getPassword());
		user.setPassword(hashCode);

		/*String randomId = UUID.randomUUID().toString();
		user.setRandomId(randomId);
*/
		userRepository.save(user);// jpa given

		String to = user.getEmail();
		int id =user.getUserId();
		String token = tokenUtils.generateToken(id);
		
		String subject = "FundooPay registration verification";
		String message = requestURL + "/activate/" + token;
		Email email=new Email();
		      email.setTo(to);
		      email.setSubject(subject);
		      email.setMsg(message);
		    //mailService.sendMail(email);
		template.send(new MessageCreator() {
			@Override
			public Message createMessage(Session session) throws JMSException {
				ObjectMessage message = session.createObjectMessage(email);
				return message;
			}
		});
	}
	@Override
	public void  activation(String token) throws USException {
		
		int  userId=tokenUtils.verifyToken(token);
		User user = userRepository.findById(userId).get();
		 if (user == null) {
	         throw new USException(105);
	      }
		user.setActivated(true);
		userRepository.save(user);
		
		
	}
	@Override
	public String login(UserDTO userDTO) throws USException {
		
		User user = new User();
		user.setEmail(userDTO.getEmail());
		user.setPassword(userDTO.getPassword());
		System.out.println(user.getEmail());
       User userDbObj =userRepository.findByEmail(user.getEmail());
      
		
       if (userDbObj != null && userDbObj.isActivated() == true
				&& BCrypt.checkpw(user.getPassword(),userDbObj.getPassword())) 
           {
			int id = userDbObj.getUserId();
			String token = tokenUtils.generateToken(id);
			System.out.println("toekn genrated :" + token);
			logger.info("token genrate" + token);

			return token;
		  }
		return null;
	}
	
	@Override
	public void forgetPassword(String email, String requestUrl) throws USException, IOException {
		
		 User userDB = userRepository.findByEmail(email);
	      if (userDB == null)
	         throw new USException(110);
	      
	      String jwtToken = tokenUtils.generateToken(userDB.getUserId());
			String to = userDB.getEmail();
			String subject = "Link to reset your  password";

			String message = requestUrl + "/changepassword/" + jwtToken;
			Email email1=new Email();
			 email1.setTo(to);
		      email1.setSubject(subject);
		      email1.setMsg(message);
			
			mailService.sendMail(email1);
	      
	      
	}
	@Override
	public void changePassword(String jwtToken, String newPassword) throws USException {
		
		 int  userId=tokenUtils.verifyToken(jwtToken);
	     User user=userRepository.findById(userId).get();
	  
	      if (user == null) {
	         throw new USException(105);
	      }
	      BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

			String hashCode = passwordEncoder.encode(newPassword);
			user.setPassword(hashCode);
			userRepository.save(user);
	      
		
	}
	@Override
	public void deleteUser(int userId) throws USException {
		  userRepository.deleteById(userId);
		
	}
	@Override
	public User getUserByEmail(String email) throws USException {
		User user=userRepository.findByEmail(email);
		return user;
	}
	@Override
	public User getProfile(int userId) throws USException {
		
		User user = userRepository.findById(userId).get();
	      user.setPassword(null);
	      return user;
	}
	
	@Override
	public void updateUser(UpdateUserDTO updateUserDTO,int userId) throws USException {
		 User user=userRepository.findById(userId).get();
		 if (user == null) {
	         throw new USException(105);
	      }
    //  User user =new User(updateUserDTO);
       //user.setActivated(updateUserDTO.isActivated());
       user.setEmail(updateUserDTO.getEmail());
       user.setMobileNumber(updateUserDTO.getMobileNumber());
       user.setPassword(updateUserDTO.getPassword());
       user.setRole(updateUserDTO.getRole());
       user.setPicUrl(updateUserDTO.getPicUrl());
       user.setName(updateUserDTO.getName());
       //user.setUserId(updateUserDTO.getUserId());
       

		 userRepository.save(user);

		
	}
	@Override
	public void uploadProfile(int userId, MultipartFile file) throws USException {
	  	
		String imageUrl = s3Service.saveImageToS3(userId + "-USER", file);
	      User fromDB = userRepository.findById(userId).get();
	      fromDB.setPicUrl(imageUrl);
	      userRepository.save(fromDB);

	      
    
		
	}

	

}
