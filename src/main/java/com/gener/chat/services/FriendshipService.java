package com.gener.chat.services;

import com.gener.chat.enums.ErrorCode;
import com.gener.chat.enums.FriendshipStatus;
import com.gener.chat.enums.SuccessCode;
import com.gener.chat.exception.APIException;
import com.gener.chat.models.Friendship;
import com.gener.chat.models.FriendshipId;
import com.gener.chat.models.ResponseObject;
import com.gener.chat.models.User;
import com.gener.chat.repositories.FriendshipRepository;
import com.gener.chat.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class FriendshipService {
    private final FriendshipRepository friendshipRepository;
    private final UserService userService;
    private final UserRepository userRepository;
    private final ApplicationEventPublisher publisher;

    @Transactional
    public ResponseEntity<ResponseObject> sendFriendRequest(Long peerId) throws APIException {
        User currentUser = userService.getUserFromToken();

        if (currentUser.getId().equals(peerId)) {
            throw new APIException(ErrorCode.SELF_FRIEND_REQUEST);
        }

        User peer = userRepository.findById(peerId)
                .orElseThrow(() -> new APIException(ErrorCode.USER_NOT_FOUND));

        Optional<Friendship> existingFriendship = friendshipRepository.findByUsers(currentUser.getId(), peerId);

        if (existingFriendship.isPresent()) {
            Friendship friendship = existingFriendship.get();
            switch (friendship.getStatus()) {
                case ACCEPTED -> throw new APIException(ErrorCode.FRIENDSHIP_ALREADY_EXISTS);
                case BLOCKED -> throw new APIException(ErrorCode.FRIEND_REQUEST_NOT_ALLOWED);
                case PENDING -> throw new APIException(ErrorCode.FRIEND_REQUEST_ALREADY_SENT);
                case NONE -> {
                    break;
                }

                default -> throw new APIException(ErrorCode.FRIEND_REQUEST_INVALID);
            }
        }

        Friendship friendship = new Friendship();
        friendship.setId(new FriendshipId(currentUser.getId(), peer.getId()));
        friendship.setUser(currentUser);
        friendship.setPeer(peer);
        friendship.setStatus(FriendshipStatus.PENDING);
        friendship.setRequestedBy(currentUser);

        Friendship friendshipSaved = friendshipRepository.save(friendship);

        publisher.publishEvent(friendship);


        return ResponseEntity.ok(
                ResponseObject.builder()
                        .status(SuccessCode.REQUEST.getStatus())
                        .message("Gửi yêu cầu kết bạn thành công")
                        .data(friendshipSaved)
                        .build()
        );
    }

    @Transactional
    public ResponseEntity<ResponseObject> cancelFriendRequest(Long peerId) throws APIException {
        User currentUser = userService.getUserFromToken();

        if (currentUser.getId().equals(peerId)) {
            throw new APIException(ErrorCode.FRIEND_REQUEST_INVALID);
        }

        User peer = userRepository.findById(peerId)
                .orElseThrow(() -> new APIException(ErrorCode.USER_NOT_FOUND));

        Friendship friendship = friendshipRepository
                .findById(new FriendshipId(currentUser.getId(), peer.getId()))
                .orElseThrow(() -> new APIException(ErrorCode.FRIEND_REQUEST_NOT_FOUND));

        if (friendship.getStatus() != FriendshipStatus.PENDING) {
            throw new APIException(ErrorCode.FRIEND_REQUEST_INVALID);
        }

        friendshipRepository.delete(friendship);
        friendship.setStatus(FriendshipStatus.NONE);
        publisher.publishEvent(friendship);
        return ResponseEntity.ok(
                ResponseObject.builder()
                        .status(SuccessCode.REQUEST.getStatus())
                        .message("Hủy yêu cầu kết bạn thành công")
                        .data(null)
                        .build()
        );
    }

    @Transactional
    public ResponseEntity<ResponseObject> acceptFriendRequest(Long peerId) throws APIException {
        User currentUser = userService.getUserFromToken();

        if (currentUser.getId().equals(peerId)) {
            throw new APIException(ErrorCode.FRIEND_REQUEST_INVALID);
        }

        User peer = userRepository.findById(peerId)
                .orElseThrow(() -> new APIException(ErrorCode.USER_NOT_FOUND));

        Friendship friendship = friendshipRepository
                .findById(new FriendshipId(peer.getId(), currentUser.getId()))
                .orElseThrow(() -> new APIException(ErrorCode.FRIEND_REQUEST_NOT_FOUND));

        if (friendship.getStatus() == FriendshipStatus.ACCEPTED) {
            throw new APIException(ErrorCode.FRIENDSHIP_ALREADY_EXISTS);
        }

        if (friendship.getStatus() == FriendshipStatus.BLOCKED) {
            throw new APIException(ErrorCode.FRIEND_REQUEST_NOT_ALLOWED);
        }

        if (friendship.getStatus() != FriendshipStatus.PENDING) {
            throw new APIException(ErrorCode.FRIEND_REQUEST_INVALID);
        }

        friendship.setStatus(FriendshipStatus.ACCEPTED);
        friendshipRepository.save(friendship);
        publisher.publishEvent(friendship);
        return ResponseEntity.ok(
                ResponseObject.builder()
                        .status(SuccessCode.REQUEST.getStatus())
                        .message("Chấp nhận lời mời kết bạn thành công")
                        .data(null)
                        .build()
        );
    }

    @Transactional
    public ResponseEntity<ResponseObject> rejectFriendRequest(Long peerId) throws APIException {
        User currentUser = userService.getUserFromToken();

        if (currentUser.getId().equals(peerId)) {
            throw new APIException(ErrorCode.FRIEND_REQUEST_INVALID);
        }

        User peer = userRepository.findById(peerId)
                .orElseThrow(() -> new APIException(ErrorCode.USER_NOT_FOUND));

        Friendship friendship = friendshipRepository
                .findById(new FriendshipId(peer.getId(), currentUser.getId()))
                .orElseThrow(() -> new APIException(ErrorCode.FRIEND_REQUEST_NOT_FOUND));

        if (friendship.getStatus() != FriendshipStatus.PENDING) {
            throw new APIException(ErrorCode.FRIEND_REQUEST_INVALID);
        }

        friendship.setStatus(FriendshipStatus.NONE);
        friendshipRepository.save(friendship);
        publisher.publishEvent(friendship);
        return ResponseEntity.ok(
                ResponseObject.builder()
                        .status(SuccessCode.REQUEST.getStatus())
                        .message("Từ chối yêu cầu kết bạn thành công")
                        .data(null)
                        .build()
        );
    }

    @Transactional
    public ResponseEntity<ResponseObject> blockUser(Long peerId) throws APIException {
        User currentUser = userService.getUserFromToken();

        if (currentUser.getId().equals(peerId)) {
            throw new APIException(ErrorCode.FRIEND_REQUEST_INVALID);
        }

        User peer = userRepository.findById(peerId)
                .orElseThrow(() -> new APIException(ErrorCode.USER_NOT_FOUND));

        Optional<Friendship> existingFriendship =
                friendshipRepository.findByUsers(currentUser.getId(), peerId);

        Friendship friendship;

        if (existingFriendship.isPresent()) {
            friendship = existingFriendship.get();

            if (friendship.getStatus() == FriendshipStatus.BLOCKED) {
                throw new APIException(ErrorCode.USER_ALREADY_BLOCKED);
            }

            friendship.setStatus(FriendshipStatus.BLOCKED);
        } else {
            friendship = new Friendship();
            friendship.setId(new FriendshipId(currentUser.getId(), peer.getId()));
            friendship.setUser(currentUser);
            friendship.setPeer(peer);
            friendship.setRequestedBy(currentUser);
            friendship.setStatus(FriendshipStatus.BLOCKED);
        }

        friendshipRepository.save(friendship);
        publisher.publishEvent(friendship);

        return ResponseEntity.ok(
                ResponseObject.builder()
                        .status(SuccessCode.REQUEST.getStatus())
                        .message("Đã chặn người dùng")
                        .data(null)
                        .build()
        );
    }

    @Transactional
    public ResponseEntity<ResponseObject> unfriend(Long peerId) throws APIException {
        User currentUser = userService.getUserFromToken();

        if (currentUser.getId().equals(peerId)) {
            throw new APIException(ErrorCode.FRIEND_REQUEST_INVALID);
        }

        userRepository.findById(peerId)
                .orElseThrow(() -> new APIException(ErrorCode.USER_NOT_FOUND));

        Friendship friendship = friendshipRepository.findByUsers(currentUser.getId(), peerId)
                .orElseThrow(() -> new APIException(ErrorCode.FRIENDSHIP_NOT_FOUND));

        if (friendship.getStatus() != FriendshipStatus.ACCEPTED) {
            throw new APIException(ErrorCode.FRIENDSHIP_INVALID);
        }

        friendshipRepository.delete(friendship);
        friendship.setStatus(FriendshipStatus.NONE);
        publisher.publishEvent(friendship);

        return ResponseEntity.ok(
                ResponseObject.builder()
                        .status(SuccessCode.REQUEST.getStatus())
                        .message("Hủy kết bạn thành công")
                        .data(null)
                        .build()
        );
    }

    @Transactional
    public ResponseEntity<ResponseObject> unblockUser(Long peerId) throws APIException {
        User currentUser = userService.getUserFromToken();

        if (currentUser.getId().equals(peerId)) {
            throw new APIException(ErrorCode.FRIEND_REQUEST_INVALID);
        }

        userRepository.findById(peerId)
                .orElseThrow(() -> new APIException(ErrorCode.USER_NOT_FOUND));

        Friendship friendship = friendshipRepository.findByUsers(currentUser.getId(), peerId)
                .orElseThrow(() -> new APIException(ErrorCode.FRIENDSHIP_NOT_FOUND));

        if (friendship.getStatus() != FriendshipStatus.BLOCKED) {
            throw new APIException(ErrorCode.FRIENDSHIP_INVALID);
        }

        friendshipRepository.delete(friendship);
        friendship.setStatus(FriendshipStatus.NONE);
        publisher.publishEvent(friendship);
        return ResponseEntity.ok(
                ResponseObject.builder()
                        .status(SuccessCode.REQUEST.getStatus())
                        .message("Bỏ chặn người dùng thành công")
                        .data(null)
                        .build()
        );
    }




}
