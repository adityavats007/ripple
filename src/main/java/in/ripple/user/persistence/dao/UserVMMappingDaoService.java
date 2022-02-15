package in.ripple.user.persistence.dao;

import in.ripple.user.persistence.entity.UserVmMapping;
import in.ripple.user.persistence.entity.VirtualMachine;
import in.ripple.user.persistence.repository.UserVMMappingRepository;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class UserVMMappingDaoService {
    @Autowired
    UserVMMappingRepository userVMMappingRepository;

    public UserVmMapping save(UserVmMapping userVmMapping) {
        return userVMMappingRepository.save(userVmMapping);
    }

    public List<UserVmMapping> findMachinesByUser(long userID) {
        return userVMMappingRepository.findMachinesByUser(userID);
    }

    public void deAllocateMachinesForUser(long userID) {
         userVMMappingRepository.deAllocateMachinesForUser(userID);
    }

    public List<Integer> findAllVmIdsByUser(long userID, int pageSize, int offset) {
        return userVMMappingRepository.findAllVmIdsByUser(userID,pageSize,offset);
    }

    public void saveAll(List<UserVmMapping> userVmMappings) {
         userVMMappingRepository.saveAll(userVmMappings);
    }

    public long findUserByVm(long vmId) {
        return userVMMappingRepository.findUserByVm(vmId);
    }


}
