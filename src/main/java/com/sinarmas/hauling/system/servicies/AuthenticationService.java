package com.sinarmas.hauling.system.servicies;

import com.sinarmas.hauling.system.pojo.GeneralResponse;
import com.sinarmas.hauling.system.pojo.authentication.AuthenticationRequest;
import com.sinarmas.hauling.system.pojo.authentication.AuthenticationResponse;
import com.sinarmas.hauling.system.pojo.authentication.RegisterRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public interface AuthenticationService {
    AuthenticationResponse register(RegisterRequest request);
    AuthenticationResponse authenticate(AuthenticationRequest request);
    GeneralResponse<AuthenticationResponse> refreshToken(HttpServletRequest request, HttpServletResponse response);
}
