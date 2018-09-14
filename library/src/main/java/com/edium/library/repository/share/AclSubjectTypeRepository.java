package com.edium.library.repository.share;

import com.edium.library.model.share.AclSubjectType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AclSubjectTypeRepository extends JpaRepository<AclSubjectType, Long> {

    Optional<AclSubjectType> findByType(String subjectType);

}
