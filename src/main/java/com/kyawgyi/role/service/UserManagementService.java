package com.kyawgyi.role.service;

import com.kyawgyi.role.dto.ReqRes;
import com.kyawgyi.role.model.Users;
import com.kyawgyi.role.repo.UsersRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
public class UserManagementService {


    private final UsersRepo usersRepo;

    private final JWTUtils jwtUtils;

    private final AuthenticationManager authenticationManager;

    private final PasswordEncoder passwordEncoder;

    public UserManagementService(UsersRepo usersRepo, JWTUtils jwtUtils,AuthenticationManager authenticationManager, PasswordEncoder passwordEncoder) {
        this.usersRepo = usersRepo;
        this.jwtUtils = jwtUtils;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
    }

//    Register
    public ReqRes register (ReqRes registrationRequest) {
        ReqRes resp = new ReqRes();

        if(registrationRequest.getName()==null || registrationRequest.getName().isEmpty()){
            resp.setStatusCode(400);
            resp.setError("Name is missing");
            return resp;
        }

        if(registrationRequest.getPassword()==null || registrationRequest.getPassword().isEmpty()){
            resp.setStatusCode(400);
            resp.setError("Password is missing");
            return resp;
        }

        if(registrationRequest.getEmail()==null || registrationRequest.getEmail().isEmpty()){
            resp.setStatusCode(400);
            resp.setError("Email is missing");
            return resp;
        }

        if (registrationRequest.getCity()==null || registrationRequest.getCity().isEmpty()){
            resp.setStatusCode(400);
            resp.setError("City is missing");
            return resp;
        }

        if (registrationRequest.getRole()==null || registrationRequest.getRole().isEmpty()){
            resp.setStatusCode(400);
            resp.setError("Role is missing");
            return resp;
        }

        try {
            Users user = new Users();
            user.setEmail(registrationRequest.getEmail());
            user.setName(registrationRequest.getName());
            user.setCity(registrationRequest.getCity());
            user.setRole(registrationRequest.getRole());
            user.setPassword(passwordEncoder.encode(registrationRequest.getPassword()));
            Users savedUser = usersRepo.save(user);

            if(savedUser.getId() != null){
                resp.setUsers(savedUser);
                resp.setMessage("User saved Successfully!");
                resp.setStatusCode(200);
            }

        }catch (Exception e) {

            resp.setStatusCode(500);
            resp.setError("Registration Failed: "+ e.getMessage());
        }
        return resp;
    }

//    Login
    public ReqRes login(ReqRes loginRequest) {
        ReqRes resp = new ReqRes();
        try{

            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    loginRequest.getEmail()
                    ,loginRequest.getPassword()));

            var user = usersRepo.findByEmail(loginRequest.getEmail())
                    .orElseThrow();

            var jwt = jwtUtils.generateToken(user);
            var refreshToken = jwtUtils.generateRefreshToken(new HashMap<>(), user);

            resp.setStatusCode(200);
            resp.setToken(jwt);
            resp.setRefreshToken(refreshToken);
            resp.setExpirationTime("24Hrs");
            resp.setMessage("User logged in successfully!");
            ReqRes.UserInfo userInfo = new ReqRes.UserInfo();
            userInfo.setId(user.getId());
            userInfo.setName(user.getName());
            userInfo.setEmail(user.getEmail());
            userInfo.setRole(user.getRole());
            userInfo.setCity(user.getCity());

            resp.setUser(userInfo);

        }catch (Exception e) {

            resp.setStatusCode(500);
            resp.setError(e.getMessage());

        }
        return resp;
    }

//  refrshToken
    public ReqRes refreshToken(ReqRes refreshTokenRequest) {
        ReqRes resp = new ReqRes();
        try{
            String email= jwtUtils.extractUsername(refreshTokenRequest.getToken());
            Users user = usersRepo.findByEmail(email).orElseThrow();
            if(jwtUtils.validateToken(refreshTokenRequest.getToken(), user)){
                var jwt = jwtUtils.generateToken(user);
                resp.setStatusCode(200);
                resp.setToken(jwt);
                resp.setRefreshToken(refreshTokenRequest.getToken());
                resp.setExpirationTime("24Hrs");
                resp.setMessage("Successfully refreshed token!");
            }
            resp.setStatusCode(200);
            return resp;

        }catch (Exception e) {
            resp.setStatusCode(500);
            resp.setError(e.getMessage());
            return resp;

        }
    }

//    getAllUsers
    public ReqRes getAllUsers() {
        ReqRes resp = new ReqRes();
        try{
            List<Users> result = usersRepo.findAll();
            if(!result.isEmpty()){
                resp.setUsersList(result);
                resp.setStatusCode(200);
                resp.setMessage("Successfully retrieved all users!");
            }else {
                resp.setStatusCode(500);
                resp.setMessage("No users found!");
            }
            return resp;

        }catch (Exception e) {
            resp.setStatusCode(500);
            resp.setError("Error Occurred: "+e.getMessage());
            return resp;

        }
    }

//    FindbyId
    public ReqRes getUserById(int id) {
        ReqRes resp = new ReqRes();
        try{
            Users user = usersRepo.findById(id).orElseThrow(()->new RuntimeException("User Not Found"));
            resp.setUsers(user);
            resp.setStatusCode(200);
            resp.setMessage("Users with id '" + id + "' found successfully!");

        }catch (Exception e) {
            resp.setStatusCode(500);
            resp.setError("Error Occurred: "+e.getMessage());
        }
        return resp;
    }

//    Delete User
    public ReqRes deleteUser(int id) {
        ReqRes resp = new ReqRes();
        try{
            Optional<Users> user = usersRepo.findById(id);
            if(user.isPresent()){
                usersRepo.deleteById(id);
                resp.setStatusCode(200);
                resp.setMessage("User delete Successfully!");
            }else {
                resp.setStatusCode(404);
                resp.setMessage("User not for deletion!");
            }
        }catch (Exception e) {
            resp.setStatusCode(500);
            resp.setError("Error Occurred while deleting: "+e.getMessage());
        }
        return resp;
    }

//    UPdate User
    public ReqRes updateUser(int id,Users updatedUser) {
        ReqRes resp = new ReqRes();
        try{
            Optional<Users> user = usersRepo.findById(id);
            if(user.isPresent()){
                Users existingUser = user.get();
                existingUser.setEmail(updatedUser.getEmail());
                existingUser.setName(updatedUser.getName());
                existingUser.setCity(updatedUser.getCity());
                existingUser.setRole(updatedUser.getRole());

//                if(updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()){
//                    existingUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
//                }

                Users savedUser = usersRepo.save(existingUser);
                resp.setUsers(savedUser);
                resp.setStatusCode(200);
                resp.setMessage("User updated successfully!");

            }else {
                resp.setStatusCode(404);
                resp.setMessage("User not found!");
            }
        }catch (Exception e) {
            resp.setStatusCode(500);
            resp.setError("Error Occurred while updating user : "+e.getMessage());
        }
        return resp;
    }

//    getProfile
    public ReqRes getMyInfo(String email) {
        ReqRes resp = new ReqRes();
        try{
            Optional<Users> user = usersRepo.findByEmail(email);
            if(user.isPresent()){
                resp.setUsers(user.get());
                resp.setStatusCode(200);
                resp.setMessage("User found successfully!");
            }else {
                resp.setStatusCode(404);
                resp.setMessage("User not found!");
            }
        }catch (Exception e) {
            resp.setStatusCode(500);
            resp.setError("Error Occurred while getting info : "+e.getMessage());
        }
        return resp;
    }

//    logout
    public ReqRes logout(String token){
        ReqRes resp = new ReqRes();
        try{
//            somethings

            resp.setStatusCode(200);
            resp.setMessage("User logged out successfully!");
        }catch (Exception e) {
            resp.setStatusCode(500);
            resp.setError("Error Occurred while logout : "+e.getMessage());
        }
        return resp;
    }

}
