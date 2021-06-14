package com.exadel.sandbox.repository;

import com.exadel.sandbox.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findByEmail(final String email);

    User findUserByUsername(final String username);

    @Query("select u.id from User u where u.username =:username")
    Optional<Long> findUserIdByUsername(@Param("username") String username);
}