package com.edium.library.util;

import com.edium.library.model.RoleCode;

public interface AppConstants {
    String DEFAULT_PAGE_NUMBER = "0";
    String DEFAULT_PAGE_SIZE = "30";

    int MAX_PAGE_SIZE = Integer.MAX_VALUE;

    long DEFAULT_ORGANIZATION_ID = -1;
    long DEFAULT_GROUP_ID = -1;
    RoleCode DEFAULT_ROLE = RoleCode.ROLE_NORMAL_USER;

    String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
}
