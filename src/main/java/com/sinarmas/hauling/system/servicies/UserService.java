package com.sinarmas.hauling.system.servicies;

import com.sinarmas.hauling.system.models.User;

import java.util.Optional;

public interface UserService {
    Optional<User> findByEmail(String email);
}
