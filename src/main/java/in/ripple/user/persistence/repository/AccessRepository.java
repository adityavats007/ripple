package in.ripple.user.persistence.repository;

import in.ripple.user.persistence.entity.Access;
import in.ripple.user.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface AccessRepository extends JpaRepository<Access, Long> {
    @Query(nativeQuery = true, value = "select * from access where id in ?1")
    List<Access> getAccessById(List<Long> idList);


}
