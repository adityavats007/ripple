package in.ripple.user.persistence.repository;

import in.ripple.user.persistence.entity.RoleAccessMapping;
import in.ripple.user.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface RoleAccessMappingRepository extends JpaRepository<RoleAccessMapping, Long> {

    @Query(nativeQuery = true, value = "select access from role_access_mapping where role =?1")
    List<Long> getAccessForRoleMappingList(Long id);

}
