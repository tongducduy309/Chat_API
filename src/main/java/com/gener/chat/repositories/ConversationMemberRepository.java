package com.gener.chat.repositories;

import com.gener.chat.models.Conversation;
import com.gener.chat.models.ConversationMember;
import com.gener.chat.models.ConversationMemberId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ConversationMemberRepository extends JpaRepository<ConversationMember, ConversationMemberId> {
    @Query("""
        select cm.user.id 
        from ConversationMember cm
        where cm.conversation.id = :conversationId
        and cm.leftAt is null
    """)
    List<Long> findActiveMemberIds(@Param("conversationId") Long conversationId);

    @Query("""
        select cm
        from ConversationMember cm
        where cm.conversation.id = :conversationId
        and cm.leftAt is null
    """)
    List<ConversationMember> findActiveMember(@Param("conversationId") Long conversationId);

    Optional<ConversationMember> findByIdConversationIdAndIdUserId(Long conversationId, Long userId);
}
