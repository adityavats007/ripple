package in.ripple.user.persistence.repository;

import in.ripple.user.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    @Query(nativeQuery = true, value = "select * from user_details where email=?1 and is_deleted!=true")
    UserEntity getUSerByEmail(String email);

    @Query(nativeQuery = true, value = "select * from user_details where user_name=?1 and is_deleted!=true")
    UserEntity getUserByUserName(String userName);

    @Query(nativeQuery = true, value = "select * from user_details where mobile=?1 and is_deleted!=true")
    UserEntity getUserByMobile(String mobile);

}
