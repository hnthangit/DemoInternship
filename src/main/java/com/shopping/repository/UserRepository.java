package com.shopping.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.shopping.entity.User;

@Repository("userRepository")
public interface UserRepository extends JpaRepository<User, String>{
	User findByUsername(String username);
	User findByUsernameAndPassword(String username, String password);
}
