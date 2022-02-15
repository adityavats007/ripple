package in.ripple.user.persistence.dao;

import in.ripple.user.persistence.entity.RoleAccessMapping;
import in.ripple.user.persistence.repository.RoleAccessMappingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleAccessMappingDaoService {

    @Autowired
    RoleAccessMappingRepository roleAccessMappingRepository;

    public List<Long> getAccessForRoleMappingList(Long id){
        return roleAccessMappingRepository.getAccessForRoleMappingList(id);
    }
}
