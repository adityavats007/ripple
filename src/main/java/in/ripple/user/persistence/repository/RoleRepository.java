package in.ripple.user.persistence.repository;

import in.ripple.user.persistence.entity.Role;
import in.ripple.user.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    @Query(nativeQuery = true, value = "select * from role where role =?1 limit 1")
    Role getRoleFromName(String name);

}
