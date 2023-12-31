package com.sinarmas.hauling.system.servicies;

import com.sinarmas.hauling.system.config.JwtService;
import com.sinarmas.hauling.system.constants.CodeConstant;
import com.sinarmas.hauling.system.constants.MessageConstant;
import com.sinarmas.hauling.system.models.User;
import com.sinarmas.hauling.system.pojo.GeneralResponse;
import com.sinarmas.hauling.system.pojo.authentication.AuthenticationRequest;
import com.sinarmas.hauling.system.pojo.authentication.AuthenticationResponse;
import com.sinarmas.hauling.system.pojo.authentication.RegisterRequest;
import com.sinarmas.hauling.system.repositories.UserRepository;
import com.sinarmas.hauling.system.token.Token;
import com.sinarmas.hauling.system.token.TokenRepository;
import com.sinarmas.hauling.system.token.TokenType;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService{
  private final UserRepository repository;
  private final TokenRepository tokenRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;
  private final AuthenticationManager authenticationManager;

  @Override
  public AuthenticationResponse register(RegisterRequest request) {
    var user = User.builder()
        .firstname(request.getFirstname())
        .lastname(request.getLastname())
        .email(request.getEmail())
        .password(passwordEncoder.encode(request.getPassword()))
        .role(request.getRole())
        .build();
    var savedUser = repository.save(user);
    var jwtToken = jwtService.generateToken(user);
    var refreshToken = jwtService.generateRefreshToken(user);
    saveUserToken(savedUser, jwtToken);
    return AuthenticationResponse.builder()
        .accessToken(jwtToken)
            .refreshToken(refreshToken)
        .build();
  }

  @Override
  public AuthenticationResponse authenticate(AuthenticationRequest request) {
    authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                    request.getEmail(),
                    request.getPassword()
            )
    );
    var user = repository.findByEmail(request.getEmail())
            .orElseThrow();
    var jwtToken = jwtService.generateToken(user);
    var refreshToken = jwtService.generateRefreshToken(user);
    revokeAllUserTokens(user);
    saveUserToken(user, jwtToken);
    return AuthenticationResponse.builder()
            .accessToken(jwtToken)
            .refreshToken(refreshToken)
            .build();
  }


  @Override
  public GeneralResponse<AuthenticationResponse> refreshToken(HttpServletRequest request, HttpServletResponse response) {
    final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
    final String refreshToken;
    final String userEmail;
    boolean isTokenValid = true;
    if (authHeader == null ||!authHeader.startsWith("Bearer ")) {
      isTokenValid = false;
    }
    refreshToken = authHeader.substring(7);
    userEmail = jwtService.extractUsername(refreshToken);
    AuthenticationResponse authToken = new AuthenticationResponse();
    if (userEmail != null) {
      var user = this.repository.findByEmail(userEmail)
              .orElseThrow();
      if (jwtService.isTokenValid(refreshToken, user)) {
        var accessToken = jwtService.generateToken(user);
        revokeAllUserTokens(user);
        saveUserToken(user, accessToken);
        authToken =  AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
      } else {
        isTokenValid = false;
      }
    } else {
      isTokenValid = false;
    }

    if (isTokenValid) {
      return new GeneralResponse<>(CodeConstant.CODE_SUCCESS, MessageConstant.SUCCESS, authToken);
    } else {
      return new GeneralResponse<>(CodeConstant.CODE_INVALID_REQUEST, MessageConstant.INVALID_TOKEN_REQUEST, null);
    }
  }

  private void saveUserToken(User user, String jwtToken) {
    var token = Token.builder()
        .user(user)
        .token(jwtToken)
        .tokenType(TokenType.BEARER)
        .expired(false)
        .revoked(false)
        .build();
    tokenRepository.save(token);
  }

  private void revokeAllUserTokens(User user) {
    var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
    if (validUserTokens.isEmpty())
      return;
    validUserTokens.forEach(token -> {
      token.setExpired(true);
      token.setRevoked(true);
    });
    tokenRepository.saveAll(validUserTokens);
  }
}
