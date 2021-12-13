package com.blockchainvotingweb.data.service;

import com.blockchainvotingweb.data.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {

    User findByUsername(String username);
}