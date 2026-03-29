package com.gener.chat.repositories;

import com.gener.chat.enums.FriendshipStatus;
import com.gener.chat.models.Friendship;
import com.gener.chat.models.FriendshipId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FriendshipRepository extends JpaRepository<Friendship, FriendshipId> {
    @Query("""
        select f
        from Friendship f
        where f.user.id <> :userId
          and (f.peer.phone = :value or f.peer.userCode = :value)
    """)
    Optional<Friendship> findByUserIdAndPeerPhoneOrPeerUserCode(
            @Param("value") String value,
            @Param("userId") Long userId
    );

    @Query("""
        select f
        from Friendship f
        where f.user.id = :userId
          and (f.peer.phone = :value or f.peer.userCode = :value)
          and f.status = :status
    """)
    Optional<Friendship> findByUserIdAndPeerPhoneOrPeerUserCodeAndStatus(
            @Param("userId") Long userId,
            @Param("value") String value,
            @Param("status") FriendshipStatus status
    );

    @Query("""
    SELECT f FROM Friendship f
    WHERE (f.user.id = :userId OR f.peer.id = :userId)
    AND f.status = 'ACCEPTED'
""")
    List<Friendship> findAcceptedFriends(Long userId);

    @Query("""
        select f
        from Friendship f
        where (f.user.id = :userId and f.peer.id = :peerId)
           or (f.user.id = :peerId and f.peer.id = :userId)
    """)
    Optional<Friendship> findByUsers(
            @Param("userId") Long userId,
            @Param("peerId") Long peerId
    );
}
