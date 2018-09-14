package com.edium.library.repository.share;

import com.edium.library.model.share.AclResourceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AclResourceTypeRepository extends JpaRepository<AclResourceType, Long> {

    Optional<AclResourceType> findByType(String resourceType);

}
