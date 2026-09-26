//package com.finpay.wallet_service.controller;
//
//import com.finpay.wallet_service.dto.RegisterRequest;
//import com.finpay.wallet_service.service.UserService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@RequestMapping("/api/users") // Sets a base URL path. Every endpoint inside this controller will start with /api/users.
//@RequiredArgsConstructor
//
//public class UserController {
//    private final UserService userService;
//
//    @PostMapping("/register")
//    public void register(@RequestBody RegisterRequest request){
//        userService.registerUser(request.getEmail(), request.getPassword());
//
//    }
//
//
//}
