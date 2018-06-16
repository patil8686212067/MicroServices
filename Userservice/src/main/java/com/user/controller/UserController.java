package com.user.controller;

import java.io.IOException;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/*import com.fundoonotes.exception.UnAuthorizedAccessUser;
import com.fundoonotes.userservice.UserDto;
import com.fundoonotes.utility.CustomResponse;*/
import com.user.config.ApplicationConfiguration;
import com.user.exception.USException;
import com.user.model.UpdateUserDTO;
import com.user.model.User;
import com.user.response.Response;
import com.user.response.UserDTO;
import com.user.response.UserFieldErrors;
import com.user.service.IUserServiceImpl;
import com.user.utility.UserValidator;


@RestController
@RequestMapping("/user")
public class UserController {
	
  private final Logger logger = LoggerFactory.getLogger(UserController.class);
  
    @Autowired
	private IUserServiceImpl userService;

	@Autowired
	private UserValidator userValidator;

	   @Value("${redirect.url}")
	   private String redirectUrl;

	   @Value("${redirect.reset.url}")
	   private String redirectResetPassURL;

		@Value("${frontEndHost}")
		private String frontEndHost;
	   
	//register
	@RequestMapping(value = "/save", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> registerUser(@Validated @RequestBody UserDTO userDTO, BindingResult bindingResult,
			HttpServletRequest request) throws Exception {

		userValidator.validate(userDTO, bindingResult);
		List<FieldError> errors = bindingResult.getFieldErrors();

		UserFieldErrors response = new UserFieldErrors();
		
		if (bindingResult.hasErrors()) {
			
			logger.info("This is an info log entry");
			
			response.setMessage("registration fail please try once again..?");
            response.setStatusCode(501);
			return new ResponseEntity<UserFieldErrors>(response, HttpStatus.CONFLICT);
		}

		String url = request.getRequestURL().toString().substring(0, request.getRequestURL().lastIndexOf("/"));
		userService.save(userDTO, url);

		 response.setMessage("user register successfully");
		 response.setStatusCode(200);
		 logger.info("This is info message");

		return new ResponseEntity<UserFieldErrors>(response, HttpStatus.CREATED);

	}
	
  //activate user
	@RequestMapping(value = "/activate/{token:.+}", method = RequestMethod.PUT)
	   public ResponseEntity<Response> active(@PathVariable("token") String token, HttpServletResponse res,
	         HttpServletRequest req)
	   {
	      logger.debug("User Activate", token);
	      try {
	         userService.activation(token);
	         logger.info("redirected to login page");
	         res.sendRedirect(redirectUrl);
	         return null;
	      } catch (USException e) {
	         logger.error(e.getLogMessage());
	         return new ResponseEntity<>(e.getErrorResponse(), HttpStatus.INTERNAL_SERVER_ERROR);
	      } catch (Exception e) {
	         logger.error(e.getMessage());
	         e.printStackTrace();
	         USException fn = new USException(101, new Object[] { "user activate - " + e.getMessage() }, e);
	         return new ResponseEntity<>(fn.getErrorResponse(), HttpStatus.INTERNAL_SERVER_ERROR);
	      }
	   }
	
	  //login
	@RequestMapping(value = "/login", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	   public ResponseEntity<?> login(@RequestBody UserDTO userDTO, HttpServletResponse resp)
	   {
	      logger.debug("user login");
	      Response response = new Response();
	      String token = null;
	      try {
	         token = userService.login(userDTO);
	         if(token != null)
	         {
	        	 resp.setHeader("Authorization", token);
	 			response.setMessage("user login successfully");
	 			response.setStatusCode(200);
	 			return new ResponseEntity<Response>(response, HttpStatus.OK);
	           }
	         }catch (USException e) {
	         logger.error(e.getLogMessage());
	         return new ResponseEntity<Response>(e.getErrorResponse(), HttpStatus.INTERNAL_SERVER_ERROR);
	      } catch (Exception e) {
	         logger.error(e.getMessage());
	         USException fn = new USException(101, new Object[] { "user login - " + e.getMessage() }, e);
	         return new ResponseEntity<Response>(fn.getErrorResponse(), HttpStatus.INTERNAL_SERVER_ERROR);
	      }
	      
	      return new ResponseEntity<Response>(response, HttpStatus.CONFLICT);
	   }
	//forgot password --resetpassword--resepassworedLink api
	
	@RequestMapping(value = "/forgotpassword", method = RequestMethod.POST)
	  public ResponseEntity<Response> forgetPassword(HttpServletRequest request, @RequestParam String email)
	   {
	      Response response = new Response();
	      
	    
	      String requestUrl = request.getRequestURL().toString().substring(0, request.getRequestURL().lastIndexOf("/"));
	      try {
	         userService.forgetPassword(email, requestUrl);
	      } catch (USException e) {
	         logger.error(e.getLogMessage());
	         return new ResponseEntity<>(e.getErrorResponse(), HttpStatus.INTERNAL_SERVER_ERROR);
	      } catch (Exception e) {
	         logger.error(e.getMessage());
	         USException fn = new USException(101, new Object[] { "forget password - " + e.getMessage() }, e);
	         return new ResponseEntity<>(fn.getErrorResponse(), HttpStatus.INTERNAL_SERVER_ERROR);
	      }
	      response.setStatusCode(200);
	      response.setMessage(ApplicationConfiguration.getMessageAccessor().getMessage("200"));
	      logger.debug("Update successfull", response);
	      return new ResponseEntity<>(response, HttpStatus.OK);
	   }
	
	//reset password redirection link
	@RequestMapping(value = "/resetpassword/{jwtToken:.+}", method = RequestMethod.GET)
	public void resetPasswordLink(@PathVariable("jwtToken") String jwtToken, HttpServletResponse response,
			HttpServletRequest request) throws IOException {

		logger.info("In side reset password link");
		System.out.print("url for front end-->" + request.getHeader("origin"));
		System.out.print("your fronENd url " + frontEndHost);
		response.sendRedirect(frontEndHost + "/resetpassword?jwtToken=" + jwtToken);

	}
	 //11-->changepassword
	 @RequestMapping(value = "/changepassword/{jwtToken:.+}", method = RequestMethod.POST)
	   public ResponseEntity<Response> changePassword(@PathVariable("jwtToken") String jwtToken, @RequestParam String newPassword)
	   {
	      try {
	         userService.changePassword(jwtToken, newPassword);
	      } 
	      catch (USException e) {
	         logger.error(e.getMessage());
	         return new ResponseEntity<>(e.getErrorResponse(), HttpStatus.INTERNAL_SERVER_ERROR);
	      } 
	      catch (Exception e) {
	         logger.error(e.getMessage());
	         USException fn = new USException(101, new Object[] { "forget password - " + e.getMessage() }, e);
	         return new ResponseEntity<>(fn.getErrorResponse(), HttpStatus.INTERNAL_SERVER_ERROR);
	      }
	      Response response = new Response();
	      response.setStatusCode(200);
	      response.setMessage(ApplicationConfiguration.getMessageAccessor().getMessage("200"));
	      logger.debug("Update successfull", response);
	      return new ResponseEntity<>(response, HttpStatus.OK);
	   }
     //delete user by id from requestAttribute
	 @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
		public ResponseEntity<Response> deleteUser(@RequestParam int userId) {
		 
		    Response response = new Response();
		    try {
				userService.deleteUser(userId);
				
			} catch (USException e) {
				
				e.printStackTrace();
				response.setMessage(ApplicationConfiguration.getMessageAccessor().getMessage("104"));
				response.setStatusCode(104);
				USException fn = new USException(124, new Object[] { "user not found- " + e.getMessage() }, e);
		         return new ResponseEntity<>(fn.getErrorResponse(), HttpStatus.INTERNAL_SERVER_ERROR);
			}
		    response.setStatusCode(125);
		    response.setMessage(ApplicationConfiguration.getMessageAccessor().getMessage("125"));
		    return new ResponseEntity<Response>(response, HttpStatus.OK);
		}
	 //get userby email
	 @RequestMapping(value = "/getbyemail", method = RequestMethod.GET)
		public ResponseEntity<?> getUser(@RequestParam String email) throws USException {
		 Response response = new Response();
			User user = userService.getUserByEmail(email);
			if (user != null) {
				return new ResponseEntity<User>(user, HttpStatus.OK);
			}
			response.setMessage(ApplicationConfiguration.getMessageAccessor().getMessage("104"));
			response.setStatusCode(104);
			return new ResponseEntity<Response>(response, HttpStatus.CONFLICT);
		}
    //User profile
	 
	 @RequestMapping(value = "/profile", method = RequestMethod.GET)
	   public ResponseEntity<?> getProfile(@RequestParam int userId)
	   {
	      logger.debug("Getting User profile");
	      User user = null;
	      try {
	         user = userService.getProfile(userId);
	      } catch (USException e) {
	         e.printStackTrace();
	         logger.error(e.getMessage());
	         return new ResponseEntity<>(e.getErrorResponse(), HttpStatus.INTERNAL_SERVER_ERROR);
	      } catch (Exception e) {
	         e.printStackTrace();
	         logger.error(e.getMessage());
	         USException us = new USException(101, new Object[] { "forget password - " + e.getMessage() }, e);
	         return new ResponseEntity<>(us.getErrorResponse(), HttpStatus.INTERNAL_SERVER_ERROR);
	      }
	      logger.debug("");
	      return new ResponseEntity<User>(user, HttpStatus.OK);
	   }
	 //userupdate update user
	 
	 @RequestMapping(value = "/update", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
		public ResponseEntity<?> updateUser(@RequestBody UpdateUserDTO updateUserDTO,@RequestParam int userId) {

			
			Response response = new Response();

			try {

				userService.updateUser(updateUserDTO,userId);
				
                logger.info("user update successfully");
				response.setMessage(ApplicationConfiguration.getMessageAccessor().getMessage("126"));
				response.setStatusCode(126);
				
				return new ResponseEntity<Response>(response, HttpStatus.OK);
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("useris  not update");
				response.setMessage(ApplicationConfiguration.getMessageAccessor().getMessage("127"));
				response.setStatusCode(126);
				logger.error("user is not updated");
				
				return new ResponseEntity<Response>(response, HttpStatus.CONFLICT);
			}
		}
	 //upload user profile pic
	
	 @RequestMapping(value = "/image", method = RequestMethod.PUT)
	   public ResponseEntity<?> saveImage(@RequestParam int userId, @RequestPart MultipartFile file)
	   {
	      Response response = new Response();
	      try {
	         userService.uploadProfile(userId, file);
	      } catch (USException e) {
	         logger.error(e.getMessage());
	         return new ResponseEntity<Response>(e.getErrorResponse(), HttpStatus.INTERNAL_SERVER_ERROR);
	      } catch (Exception e) {
	         logger.error(e.getMessage());
	         USException fn = new USException(101, new Object[] { "user login - " + e.getMessage() }, e);
	         return new ResponseEntity<Response>(fn.getErrorResponse(), HttpStatus.INTERNAL_SERVER_ERROR);
	      }
	      response.setStatusCode(200);
	      response.setMessage("Image uploaded successfull");
	      logger.debug("Image uploaded successfull", response);
	      return new ResponseEntity<Response>(response, HttpStatus.OK);
	   }
}
