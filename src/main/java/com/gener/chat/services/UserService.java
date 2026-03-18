package com.gener.chat.services;

import com.gener.chat.dtos.request.CreateUserReq;
import com.gener.chat.dtos.response.UserSearchRes;
import com.gener.chat.enums.ErrorCode;
import com.gener.chat.enums.FriendshipStatus;
import com.gener.chat.enums.SuccessCode;
import com.gener.chat.exception.APIException;
import com.gener.chat.models.Friendship;
import com.gener.chat.models.ResponseObject;
import com.gener.chat.models.User;
import com.gener.chat.repositories.FriendshipRepository;
import com.gener.chat.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserResolver userResolver;
    private final FriendshipRepository friendshipRepository;

    public ResponseEntity<ResponseObject> createUser(CreateUserReq req){
        User user = User.builder()
                .passwordHash(passwordEncoder.encode(req.getPassword()))
                .email(req.getEmail())
                .userCode(userResolver.generateUserCode())
                .displayName(req.getDisplayName())
                .phone(req.getPhone())
                .hireDate(req.getHireDate())
                .build();

        User newUser = userRepository.save(user);

        return ResponseEntity.status(200).body(
                ResponseObject.builder()
                        .status(200)
                        .message("Thông tin người dùng")
                        .data(newUser)
                        .build()
        );
    }

    public ResponseEntity<ResponseObject> getProfile() throws APIException {
        return ResponseEntity.status(200).body(
                ResponseObject.builder()
                        .status(200)
                        .message("Thông tin người dùng")
                        .data(getUserFromToken())
                        .build()
        );
    }

    public User getUserFromToken() throws APIException {
        var context = SecurityContextHolder.getContext();
        String id =  context.getAuthentication().getName();
        return userRepository.findById(Long.parseLong(id)).orElseThrow(()->
                new APIException(ErrorCode.USER_NOT_FOUND));
    }

    public ResponseEntity<ResponseObject> getUserByPhoneOrUserCode(String value) throws APIException {
        User currentUser = getUserFromToken();

        List<User> users = userRepository.findByPhoneOrUserCodeExceptSelf(value, currentUser.getId());

        List<UserSearchRes> result = users.stream()
                .map(targetUser -> {
                    Friendship friendship = friendshipRepository.findByUsers(currentUser.getId(), targetUser.getId()).orElse(null);

                    FriendshipStatus friendshipStatus = friendship != null
                            ? friendship.getStatus()
                            : FriendshipStatus.NONE;

                    return UserSearchRes.builder()
                            .id(targetUser.getId())
                            .displayName(targetUser.getDisplayName())
                            .avatarUrl(targetUser.getAvatarUrl())
                            .phone(targetUser.getPhone())
                            .userCode(targetUser.getUserCode())
                            .status(friendshipStatus)
                            .requestedById(friendship == null?null:friendship.getRequestedBy().getId())
                            .build();
                })
                .toList();

        return ResponseEntity.ok(
                ResponseObject.builder()
                        .status(SuccessCode.REQUEST.getStatus())
                        .message("Kết quả tìm kiếm")
                        .data(result)
                        .build()
        );
    }




}
