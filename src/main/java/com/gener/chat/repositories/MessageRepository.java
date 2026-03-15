package com.gener.chat.repositories;

import com.gener.chat.models.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface MessageRepository extends JpaRepository<Message, Long> {
    @Query("select coalesce(max(m.seq), 0) from Message m where m.conversation.id = :cid")
    Long maxSeq(@Param("cid") Long conversationId);

    List<Message> findTop50ByConversationIdOrderBySeqDesc(Long conversationId);

    @Query("""
    select m
    from Message m
    where m.conversation.id = :conversationId
      and not exists (
          select 1
          from MessageDeletion md
          where md.message.id = m.id
            and md.user.id = :userId
      )
    order by m.seq asc
""")
    List<Message> findVisibleMessages(
            @Param("conversationId") Long conversationId,
            @Param("userId") Long userId
    );

    default Optional<Message> findTopVisibleMessage(Long conversationId, Long userId) {
        return findVisibleMessages(conversationId, userId)
                .stream()
                .findFirst();
    }

    @Query("""
    select m
    from Message m
    where m.conversation.id = :conversationId
      and lower(m.content) like lower(concat('%', :keyword, '%'))
      and not exists (
          select 1
          from MessageDeletion md
          where md.message.id = m.id
          and md.user.id = :userId
      )
    order by m.seq desc
""")
    List<Message> searchMessages(
            Long conversationId,
            Long userId,
            String keyword
    );
}
