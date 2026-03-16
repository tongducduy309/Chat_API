package com.gener.chat.repositories;

import com.gener.chat.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    @Query("""
    select u
    from User u
    where u.phone = :value or u.email = :value
""")
    Optional<User> findByPhoneOrEmail(@Param("value") String value);
    @Query("""
    select u
    from User u
    where (u.phone = :value or u.userCode = :value)
    and u.id <> :userId
""")
    List<User> findByPhoneOrUserCodeExceptSelf(String value, Long userId);

    @Query("""
    select max(u.userCode)
    from User u
    where substring(u.userCode,1,4) = :prefix
    """)
    String findMaxUserCodeByPrefix(String prefix);
}
