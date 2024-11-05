package com.kyawgyi.role.repo;


import com.kyawgyi.role.model.Users;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UsersRepo extends JpaRepository<Users,Integer> {

    Optional<Users> findByEmail(String email);

    @Modifying
    @Transactional
    @Query("update Users u set u.password = ?2 where u.email = ?1")
    void updatePassword(String email, String password);
}
