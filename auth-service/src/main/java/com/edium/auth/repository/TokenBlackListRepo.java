package com.edium.auth.repository;

import com.edium.auth.model.TokenBlackList;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@org.springframework.stereotype.Repository
public interface TokenBlackListRepo extends Repository<TokenBlackList, Long> {
    Optional<TokenBlackList> findByJti(String jti);
    List<TokenBlackList> queryAllByUsername(Long userId);
    void save(TokenBlackList tokenBlackList);
    List<TokenBlackList> deleteAllByUsernameAndExpiresBefore(String username, Long date);

    @Transactional
    @Modifying
    @Query("update TokenBlackList set blackListed = 1 where username = :username and (blackListed <> 1 or blackListed is null)")
    void setBlackListByUser(@Param(value = "username") String username);
}
