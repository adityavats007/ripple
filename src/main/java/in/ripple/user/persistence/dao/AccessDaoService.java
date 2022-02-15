package in.ripple.user.persistence.dao;

import in.ripple.user.persistence.entity.Access;
import in.ripple.user.persistence.repository.AccessRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class AccessDaoService{
    @Autowired
    AccessRepository accessRepository;

    List<Access> getAccessById(List<Long> idList){
        return accessRepository.getAccessById(idList);
    }

}
