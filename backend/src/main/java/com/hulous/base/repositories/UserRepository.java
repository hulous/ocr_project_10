package com.hulous.base.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.hulous.base.entities.User;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Integer> {
  Optional<User> findByEmail(String email);
  boolean existsByEmail(String email);
}
