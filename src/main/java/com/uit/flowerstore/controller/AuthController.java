package com.uit.flowerstore.controller;


import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uit.flowerstore.payload.request.LoginRequest;
import com.uit.flowerstore.payload.request.SignupRequest;
import com.uit.flowerstore.payload.response.MessageResponse;
import com.uit.flowerstore.payload.response.UserInfoResponse;
import com.uit.flowerstore.repository.RoleRepository;
import com.uit.flowerstore.repository.UserRepository;
import com.uit.flowerstore.security.jwt.JwtUtils;
import com.uit.flowerstore.payload.response.JwtResponse;
import com.uit.flowerstore.domain.ERole;
import com.uit.flowerstore.domain.Role;
import com.uit.flowerstore.domain.User;
import com.uit.flowerstore.security.services.UserDetailsImpl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;




//@CrossOrigin(origins = "*", maxAge = 3600)
//for Angular Client (withCredentials)
@CrossOrigin(origins = "http://localhost:8081", maxAge = 3600, allowCredentials = "true")
@RestController
@RequestMapping("/api/auth")
public class AuthController {
@Autowired
AuthenticationManager authenticationManager;

@Autowired
UserRepository userRepository;

@Autowired
RoleRepository roleRepository;

@Autowired
PasswordEncoder encoder;

@Autowired
JwtUtils jwtUtils;



@PostMapping("/signin")
public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

  Authentication authentication = authenticationManager
      .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

  SecurityContextHolder.getContext().setAuthentication(authentication);

  UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

  ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(userDetails);

  List<String> roles = userDetails.getAuthorities().stream()
      .map(item -> item.getAuthority())
      .collect(Collectors.toList());

  return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
      .body(new UserInfoResponse(userDetails.getId(),
                                 userDetails.getUsername(),
                                 userDetails.getEmail(),
                                 roles));
}

@PostMapping("/signup")
public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
  if (userRepository.existsByUsername(signUpRequest.getUsername())) {
    return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken!"));
  }

  if (userRepository.existsByEmail(signUpRequest.getEmail())) {
    return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use!"));
  }

  // Create new user's account
  User user = new User(signUpRequest.getUsername(),
                       signUpRequest.getEmail(),
                       encoder.encode(signUpRequest.getPassword()));

  Set<String> strRoles = signUpRequest.getRole();
  Set<Role> roles = new HashSet<>();

  if (strRoles == null) {
	  Role userRole = roleRepository.findByName(ERole.ROLE_USER)
	      .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
	  roles.add(userRole);
	} else {
	  boolean isAdmin = false;
	  for (String role : strRoles) {
	    if (role.equals("admin")) {
	      Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
	          .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
	      roles.add(adminRole);
	      isAdmin = true;
	      break;
	    }
	  }
	  
	  if (!isAdmin) {
	    Role userRole = roleRepository.findByName(ERole.ROLE_USER)
	        .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
	    roles.add(userRole);
	  }
	}
	









  user.setRoles(roles);
  userRepository.save(user);

String token = jwtUtils.generateToken(user);

//  return ResponseEntity.ok(new MessageResponse("User registered successfully!"));

return ResponseEntity.ok(new AuthResponse("User registered successfully!", token));
}





@PostMapping("/signout")
public ResponseEntity<?> logoutUser() {
  ResponseCookie cookie = jwtUtils.getCleanJwtCookie();
  return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString())
      .body(new MessageResponse("You've been signed out!"));
}

public class AuthResponse {
    private String message;
    private String token;

    public AuthResponse(String message, String token) {
      this.message = message;
      this.token = token;
    }

	public String getMessage() {
		return message;
	}

	public String getToken() {
		return token;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void setToken(String token) {
		this.token = token;
	}

    // Getters and setters
  }
}