package com.edium.auth.service;

import com.edium.library.exception.ResourceNotFoundException;
import org.springframework.scheduling.annotation.Async;

public interface TokenBlackListService {
    public Boolean isBlackListed( String jti ) throws ResourceNotFoundException;

    @Async
    public void addToEnabledList(String username, String jti, Long expired );

    @Async
    public void addToBlackList(String jti ) throws ResourceNotFoundException;

    @Async
    public void addAllToBlackList(String username);

    public boolean existJti(String jti);
}
