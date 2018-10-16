package com.edium.auth.service;

import com.edium.library.payload.GroupDTO;
import com.edium.library.service.share.BaseAclServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class AclServiceImpl extends BaseAclServiceImpl {

    private final RestTemplate restTemplate;

    @Autowired
    public AclServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public GroupDTO getGroupOfUser(Long userId) {
        return restTemplate.getForObject("http://core-service/api/user/" + userId + "/currentGroup", GroupDTO.class);
    }
}
