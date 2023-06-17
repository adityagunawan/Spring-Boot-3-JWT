package com.sinarmas.hauling.system.controllers;

import com.sinarmas.hauling.system.constants.CodeConstant;
import com.sinarmas.hauling.system.constants.MessageConstant;
import com.sinarmas.hauling.system.pojo.BusinessException;
import com.sinarmas.hauling.system.pojo.GeneralResponse;
import com.sinarmas.hauling.system.pojo.authentication.AuthenticationRequest;
import com.sinarmas.hauling.system.pojo.authentication.AuthenticationResponse;
import com.sinarmas.hauling.system.pojo.authentication.RegisterRequest;
import com.sinarmas.hauling.system.servicies.AuthenticationService;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

  @Autowired
  private final AuthenticationService service;

  @PostMapping("/register")
  public GeneralResponse<AuthenticationResponse> register(@RequestBody RegisterRequest request) throws BusinessException {
    try {
      return new GeneralResponse(CodeConstant.CODE_SUCCESS, MessageConstant.REGISTER_SUCCESS,  service.register(request));
    } catch (Exception e) {
      throw new BusinessException(HttpStatus.INTERNAL_SERVER_ERROR, CodeConstant.CODE_INTERNAL_SERVER_ERROR, MessageConstant.INTERNAL_SERVER_ERROR);
    }
  }
  @PostMapping("/authenticate")
  public  GeneralResponse<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) throws BadCredentialsException, BusinessException {
    try {
      return new GeneralResponse(CodeConstant.CODE_SUCCESS, MessageConstant.AUTHENTICATION_SUCCESS,  service.authenticate(request));
    } catch (BadCredentialsException e) {
      log.error(e.getMessage());
      throw new BusinessException(HttpStatus.UNAUTHORIZED, CodeConstant.CODE_INVALID_REQUEST, MessageConstant.AUTHENTICATION_FAILED);
    } catch (Exception e) {
      e.printStackTrace();
      throw new BusinessException(HttpStatus.INTERNAL_SERVER_ERROR, CodeConstant.CODE_INTERNAL_SERVER_ERROR, MessageConstant.INTERNAL_SERVER_ERROR);
    }
  }

  @PostMapping("/refresh-token")
  public GeneralResponse<AuthenticationResponse> refreshToken(HttpServletRequest request, HttpServletResponse response) throws BusinessException, MalformedJwtException {
    try {
      GeneralResponse<AuthenticationResponse> authenticationResponse = service.refreshToken(request, response);
      if (authenticationResponse.getCode().equals(CodeConstant.CODE_SUCCESS)) {
        return authenticationResponse;
      } else {
        throw new BusinessException(HttpStatus.FORBIDDEN, authenticationResponse.getCode(), authenticationResponse.getMessage());
      }
    } catch (MalformedJwtException e) {
      throw new BusinessException(HttpStatus.FORBIDDEN, CodeConstant.CODE_INVALID_REQUEST, MessageConstant.INVALID_TOKEN_REQUEST);
    } catch(Exception e) {
      e.printStackTrace();
      throw new BusinessException(HttpStatus.INTERNAL_SERVER_ERROR, CodeConstant.CODE_INTERNAL_SERVER_ERROR, MessageConstant.INTERNAL_SERVER_ERROR);
    }
  }
}
