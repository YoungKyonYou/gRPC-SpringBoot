package com.example.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class UserServiceApplication {

    public static void main(String[] args) {
        try{
            SpringApplication.run(UserServiceApplication.class, args);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


}
