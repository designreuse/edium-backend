package com.edium.auth.repository;

import com.edium.auth.model.TokenBlackList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Optional;

@Repository
public interface TokenBlackListRepo extends JpaRepository<TokenBlackList, Long> {
    Optional<TokenBlackList> findByJti(String jti);

    void deleteAllByUsernameAndExpiresBefore(String username, Long date);

    @Transactional
    @Modifying
    @Query("update TokenBlackList set blackListed = 1 where username = :username and (blackListed <> 1 or blackListed is null)")
    void setBlackListByUser(@Param(value = "username") String username);
}