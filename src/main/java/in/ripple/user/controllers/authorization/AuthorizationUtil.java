package in.ripple.user.controllers.authorization;

import in.ripple.user.persistence.dao.RoleAccessMappingDaoService;
import in.ripple.user.persistence.dao.RoleDaoService;
import in.ripple.user.persistence.entity.Access;
import in.ripple.user.persistence.entity.RoleAccessMapping;
import in.ripple.user.persistence.entity.UserEntity;
import in.ripple.user.persistence.repository.AccessRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class AuthorizationUtil {

    @Autowired
    RoleDaoService roleDaoService;

    @Autowired
    RoleAccessMappingDaoService roleAccessMappingDaoService;

    @Autowired
    AccessRepository accessRepository;

   public List<Long> getRoleAccessMappingListForUser(Long roleId) {
        List<Long> accessIdList = roleAccessMappingDaoService.getAccessForRoleMappingList(roleId);
        return accessIdList;
    }

    public List<String> getApiAccessForRole(Long roleId) {
        List<Long> accessIdList=getRoleAccessMappingListForUser(roleId);
        List<Access> access = accessRepository.getAccessById(accessIdList);
        List<String> apiAccessList = new ArrayList<>();
        apiAccessList=access.stream().map(e->(e.getApi().toUpperCase())).collect(Collectors.toList());
        return apiAccessList;
    }

}
