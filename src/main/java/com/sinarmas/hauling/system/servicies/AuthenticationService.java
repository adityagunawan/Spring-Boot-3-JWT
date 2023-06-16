package com.sinarmas.hauling.system.servicies;

import com.sinarmas.hauling.system.pojo.AuthenticationRequest;
import com.sinarmas.hauling.system.pojo.AuthenticationResponse;
import com.sinarmas.hauling.system.pojo.RegisterRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public interface AuthenticationService {
    AuthenticationResponse register(RegisterRequest request);
    AuthenticationResponse authenticate(AuthenticationRequest request);
    void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException;
}
