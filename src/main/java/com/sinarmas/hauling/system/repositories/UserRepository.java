package com.sinarmas.hauling.system.repositories;

import java.util.Optional;

import com.sinarmas.hauling.system.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {

  Optional<User> findByEmail(String email);

}
