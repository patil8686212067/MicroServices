package com.user.service;
import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

import com.user.exception.USException;
import com.user.model.UpdateUserDTO;
import com.user.model.User;
import com.user.response.UserDTO;

public interface IUserService {
	
	   public void save(UserDTO userDTO, String requestURL);

	   void activation(String token) throws USException;

	   String login(UserDTO userDTO) throws USException;
	   
	   void forgetPassword(String email, String url) throws USException, IOException;
        
	   void changePassword(String token, String newPassword)throws USException;
	   
	   void deleteUser(int userId) throws USException;
	   
	   User getUserByEmail(String email) throws USException;
	   
	   User getProfile(int userId)throws USException;
	   
	   void updateUser(UpdateUserDTO updateUserDto,int userId) throws USException;
	   
	    void uploadProfile(int userId, MultipartFile file) throws USException;
	   
	  /* void uploadProfile(String loggedInUserId, MultipartFile file) throws USException;

	   void forgetPassword(String email, String url) throws USException, IOException;

	   User getUserById(String loggedInUserId) throws USException;

	   String resetPassword(String token) throws USException;

	   User getUserByEmail(String email) throws USException;

	   User getProfile(String loggedInUserId)throws USException;

	   void changePassword(String token, String newPassword)throws USException;*/
	   
	   /*public void register(UserDto userDto, String requestURL);
		  public String login(UserDto userDto);
		  public User getUserById(int userId);
		  int userActivation(String randomId);
		  public boolean forgetPassword(String email, String url);
		  public int resetPassword(UserDto userDto);
		  void uploadImage(MultipartFile uploadProfileImage, int userId) throws IOException;*/
}
