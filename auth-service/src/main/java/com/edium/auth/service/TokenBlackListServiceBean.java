package com.edium.auth.service;

import com.edium.auth.model.TokenBlackList;
import com.edium.auth.repository.TokenBlackListRepo;
import com.edium.library.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.Optional;

@Service
@Transactional
public class TokenBlackListServiceBean implements TokenBlackListService {
    @Autowired
    TokenBlackListRepo tokenBlackListRepo;

    @Override
    public Boolean isBlackListed(String jti) throws ResourceNotFoundException {
        Optional<TokenBlackList> token = tokenBlackListRepo.findByJti(jti);
        if ( token.isPresent() ) {
            return token.get().isBlackListed();
        } else {
            throw new ResourceNotFoundException("Token", "jti", jti);
        }
    }

    @Override
    public void addToEnabledList(String username, String jti, Long expired) {
        // clean all black listed tokens for core
        tokenBlackListRepo.setBlackListByUser(username);

        // Add new token white listed
        TokenBlackList tokenBlackList = new TokenBlackList(username, jti, expired);
        tokenBlackList.setBlackListed(false);
        tokenBlackListRepo.save(tokenBlackList);

        tokenBlackListRepo.deleteAllByUsernameAndExpiresBefore(username, new Date().getTime());
    }

    @Override
    public void addToBlackList(String jti) throws ResourceNotFoundException {
        Optional<TokenBlackList> tokenBlackList = tokenBlackListRepo.findByJti(jti);
        if ( tokenBlackList.isPresent() ) {
            tokenBlackList.get().setBlackListed(true);
            tokenBlackListRepo.save(tokenBlackList.get());
        } else {
            throw new ResourceNotFoundException("Token", "jti", jti);
        }
    }

    @Override
    public void addAllToBlackList(String username) {
        // clean all black listed tokens for core
        tokenBlackListRepo.setBlackListByUser(username);
    }

    @Override
    public boolean existJti(String jti) {
        Optional<TokenBlackList> tokenBlackList = tokenBlackListRepo.findByJti(jti);
        return tokenBlackList.isPresent();
    }
}
