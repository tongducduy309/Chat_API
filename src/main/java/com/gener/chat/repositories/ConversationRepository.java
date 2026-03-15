package com.gener.chat.repositories;

import com.gener.chat.models.Conversation;
import com.gener.chat.models.User;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from Conversation c where c.id = :id")
    Optional<Conversation> findByIdForUpdate(@Param("id") Long id);

    @Query("""
        select c
        from Conversation c
        join ConversationMember cm1 on cm1.conversation = c
        join ConversationMember cm2 on cm2.conversation = c
        where c.type = com.gener.chat.enums.ConversationType.DIRECT
        and cm1.user.id = :user1
        and cm2.user.id = :user2
        and cm1.leftAt is null
        and cm2.leftAt is null
    """)
    Optional<Conversation> findDirectConversation(
            @Param("user1") Long user1,
            @Param("user2") Long user2
    );

    @Query("""
        select c
        from Conversation c
        join ConversationMember cm1 on cm1.conversation = c
        where cm1.user.id=:userId
    """)
    List<Conversation> findAllByUserId(Long userId);

    @Query("""
    select distinct c
    from Conversation c
    join ConversationMember myCm on myCm.conversation = c
    join ConversationMember cm on cm.conversation = c
    join cm.user u
    where myCm.user.id = :userId
      and myCm.leftAt is null
      and cm.leftAt is null
      and c.lastMessage is not null
      and (
            lower(coalesce(c.title, '')) like lower(concat('%', :keyword, '%'))
         or (
              cm.user.id <> :userId
              and (
                    lower(coalesce(cm.nickname, '')) like lower(concat('%', :keyword, '%'))
                 or lower(coalesce(u.displayName, '')) like lower(concat('%', :keyword, '%'))
              )
            )
      )
""")
    List<Conversation> searchConversationsByUserIdAndKeyword(
            @Param("userId") Long userId,
            @Param("keyword") String keyword
    );


}
