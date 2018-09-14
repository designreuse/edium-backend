package com.edium.library.model;

public class AuthorityCode {

    public enum COURSE {
        COURSE_READ_ALL,
        COURSE_READ_GROUP_CHILD,
        COURSE_READ_GROUP,
        COURSE_READ_ACL,

        COURSE_WRITE_ALL,
        COURSE_WRITE_GROUP_CHILD,
        COURSE_WRITE_GROUP,
        COURSE_WRITE_ACL,

        COURSE_DELETE_ALL,
        COURSE_DELETE_GROUP_CHILD,
        COURSE_DELETE_GROUP,
        COURSE_DELETE_ACL,
    }

}
