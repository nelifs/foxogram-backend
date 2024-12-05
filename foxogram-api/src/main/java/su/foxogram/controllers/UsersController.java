package su.foxogram.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import su.foxogram.constants.APIConstants;
import su.foxogram.constants.AttributesConstants;
import su.foxogram.dtos.request.UserDeleteDTO;
import su.foxogram.dtos.request.UserEditDTO;
import su.foxogram.dtos.response.MFAKeyDTO;
import su.foxogram.dtos.response.OkDTO;
import su.foxogram.dtos.response.UserDTO;
import su.foxogram.exceptions.*;
import su.foxogram.models.User;
import su.foxogram.services.UsersService;

import java.security.NoSuchAlgorithmException;

@Slf4j
@RestController
@RequestMapping(value = APIConstants.USERS, produces = "application/json")
public class UsersController {

	private final UsersService usersService;

	public UsersController(UsersService usersService) {
		this.usersService = usersService;
	}

	@GetMapping("/@me")
	public UserDTO getYourself(@RequestAttribute(value = AttributesConstants.USER) User authenticatedUser) {
		return new UserDTO(authenticatedUser, true);
	}

	@GetMapping("/{userKey}")
	public UserDTO getUser(@PathVariable String userKey) throws UserNotFoundException {
		User fetchedUser = usersService.getUser(userKey);

		return new UserDTO(fetchedUser, false);
	}

	@PatchMapping("/@me")
	public UserDTO editUser(@RequestAttribute(value = AttributesConstants.USER) User authenticatedUser, @Valid @RequestBody UserEditDTO userEditRequest) throws UserCredentialsDuplicateException {
		authenticatedUser = usersService.editUser(authenticatedUser, userEditRequest);

		return new UserDTO(authenticatedUser, false);
	}

	@DeleteMapping("/@me/")
	public OkDTO deleteUser(@RequestAttribute(value = AttributesConstants.USER) User user, @RequestAttribute(value = AttributesConstants.ACCESS_TOKEN) String accessToken, @RequestBody UserDeleteDTO body, HttpServletRequest request) throws UserCredentialsIsInvalidException, CodeIsInvalidException {
		String password = body.getPassword();
		log.info("USER deletion requested ({}, {}) request", user.getId(), user.getEmail());

		usersService.requestUserDelete(user, password);

		return new OkDTO(true);
	}

	@PostMapping("/@me/delete/confirm/")
	public OkDTO deleteUserConfirm(@RequestAttribute(value = AttributesConstants.USER) User user, @RequestAttribute(value = AttributesConstants.ACCESS_TOKEN) String accessToken, HttpServletRequest request) throws CodeIsInvalidException, CodeExpiredException {
		log.info("USER deletion confirm ({}, {}) request", user.getId(), user.getEmail());

		usersService.confirmUserDelete(user);

		return new OkDTO(true);
	}

	@PostMapping("/@me/mfa/")
	public MFAKeyDTO setupMFA(@RequestAttribute(value = AttributesConstants.USER) User user) throws NoSuchAlgorithmException, MFAIsAlreadySetException {
		log.info("USER mfa setup ({}, {}) request", user.getId(), user.getEmail());

		String key = usersService.setupMFA(user);

		return new MFAKeyDTO(key);
	}

	@DeleteMapping("/@me/mfa/")
	public OkDTO deleteMFA(@RequestAttribute(value = AttributesConstants.USER) User user) throws NoSuchAlgorithmException, MFAIsAlreadySetException, MFAIsNotSetException {
		log.info("USER mfa delete ({}, {}) request", user.getId(), user.getEmail());

		usersService.deleteMFA(user);

		return new OkDTO(true);
	}

	@PostMapping("/@me/mfa/setup/validate")
	public OkDTO mfaValidate(@RequestAttribute(value = AttributesConstants.USER) User user) {
		log.info("USER mfa validation ({}, {}) request", user.getId(), user.getEmail());

		return new OkDTO(true);
	}
}
