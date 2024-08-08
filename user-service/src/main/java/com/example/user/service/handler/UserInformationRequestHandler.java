package com.example.user.service.handler;

import com.example.user.entity.PortfolioItem;
import com.example.user.entity.User;
import com.example.user.entity.exception.UnknownUserException;
import com.example.user.repository.PortfolioItemRepository;
import com.example.user.repository.UserRepository;
import com.example.user.util.EntityMessageMapper;
import com.youyk.user.UserInformation;
import com.youyk.user.UserInformationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class UserInformationRequestHandler {
    private final UserRepository userRepository;
    private final PortfolioItemRepository portfolioItemRepository;
    public UserInformation getUserInformation(UserInformationRequest request) {
        User user = this.userRepository.findById(request.getUserId())
                .orElseThrow(() -> new UnknownUserException(request.getUserId()));

        List<PortfolioItem> portfolioItems = this.portfolioItemRepository.findAllByUserId(request.getUserId());
        return EntityMessageMapper.toUserInformation(user, portfolioItems);
    }
}
