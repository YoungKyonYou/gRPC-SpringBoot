package com.example.aggregatorservice.service;

import com.youyk.user.UserInformation;
import com.youyk.user.UserInformationRequest;
import com.youyk.user.UserServiceGrpc;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    //이름 user-service는 application.yml에 설정한 이름과 동일해야 한다.
    @GrpcClient("user-service")
    private UserServiceGrpc.UserServiceBlockingStub userClient;

    public UserInformation getUserInformation(int userId){
        UserInformationRequest request = UserInformationRequest.newBuilder()
                .setUserId(userId)
                .build();
        return this.userClient.getUserInformation(request);
    }
}
