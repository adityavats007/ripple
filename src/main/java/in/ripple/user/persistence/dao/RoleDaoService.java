package in.ripple.user.persistence.dao;

import in.ripple.user.persistence.entity.Role;
import in.ripple.user.persistence.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;


@Service
public class RoleDaoService {
    @Autowired
    RoleRepository roleRepository;

    public Role getRoleFromName(String name){
        return roleRepository.getRoleFromName(name);
    }





}
