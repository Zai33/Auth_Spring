package com.kyawgyi.role.repo;

import com.kyawgyi.role.model.ForgetPassword;
import com.kyawgyi.role.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
public interface ForgetPasswordRepo extends JpaRepository<ForgetPassword, Integer> {

    @Query("select fp from ForgetPassword fp where fp.otp = ?1 and fp.user = ?2")
    Optional<ForgetPassword> findByOtpAndUser(Integer otp, Users user);

    Optional<ForgetPassword> findByUser(Users user);

}
