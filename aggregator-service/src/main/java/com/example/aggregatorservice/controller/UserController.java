package com.example.aggregatorservice.controller;

import com.example.aggregatorservice.service.UserService;
import com.youyk.user.UserInformation;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("user")
public class UserController {
    private final UserService userService;

    ///user/123이라는 URL로 요청이 들어오면, userId는 123이 됩니다.
    //이 메소드가 생성하는 응답의 Content-Type을 application/json으로 설정합니다.
    // 이는 이 메소드가 JSON 형식의 응답을 반환함을 나타냅니다.
    //여기 보면 UserInformation을 반환하고 있고 MediaType.APPLICATION_JSON_VALUE로 설정되어 있으나
    //그렇다고 자동으로 json으로 변환해준다는 것은 아니다. 그렇기 때문에 message converter를 사용해야 한다.
    //그래서 build.gradle에 implementation 'com.google.protopuf.protobuf-java-util:3.11.4'를 추가해준다.
    //따라서 ApplicationConfiguration.java에 ProtobufJsonFormatHttpMessageConverter를 추가해준다.
    @GetMapping(value = "{userId}",produces = MediaType.APPLICATION_JSON_VALUE)
    public UserInformation getUserInformation(@PathVariable Integer userId){
        return this.userService.getUserInformation(userId);
    }
}
