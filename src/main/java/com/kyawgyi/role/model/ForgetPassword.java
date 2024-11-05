package com.kyawgyi.role.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Table(name = "forget_password")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class ForgetPassword {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private int otp;

    @Column(nullable = false)
    private Date expiryDate;

    @OneToOne
    private Users user;
}
