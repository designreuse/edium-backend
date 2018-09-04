INSERT INTO oauth_client_details (client_id, resource_ids, client_secret, scope, authorized_grant_types, authorities, access_token_validity)
    VALUES ('admin', 'admin-resource', '$2a$10$I7pizsIZDriQUV0BH5IQmut1YpuAn.RYqQ.lm4UZl3CpK3t7UVB5u', 'trust', 'client_credentials', 'ROLE_ADMIN', 28800);

INSERT INTO oauth_client_details (client_id, resource_ids, client_secret, scope, authorized_grant_types, authorities, access_token_validity)
    VALUES ('sample', 'sample-resource', '$2a$10$I7pizsIZDriQUV0BH5IQmut1YpuAn.RYqQ.lm4UZl3CpK3t7UVB5u', 'read,write', 'client_credentials', 'ROLE_CLIENT', 60);

INSERT INTO oauth_client_details (client_id, resource_ids, client_secret, scope, authorized_grant_types, authorities, access_token_validity)
    VALUES ('another', 'sample-resource', '$2a$10$YwGLqLAHVzEzO4lV.1.wm.1kpFtaXa/xhwWQbPYillCginsVi2U8S', 'read,write', 'client_credentials', 'ROLE_CLIENT', 60);

INSERT INTO oauth_client_details (client_id, resource_ids, client_secret, scope, authorized_grant_types, authorities, access_token_validity)
    VALUES ('bogus', 'bogus-resource', '$2a$10$gESF6FbqpiczVLj2zs1TCe48Vtc/pfuIKngWZ1VltJwnd.AFwcqv6', 'bogus', 'client_credentials', 'ROLE_CLIENT', 60);