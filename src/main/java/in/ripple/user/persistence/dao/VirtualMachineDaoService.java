package in.ripple.user.persistence.dao;

import in.ripple.user.persistence.entity.UserVmMapping;
import in.ripple.user.persistence.entity.VirtualMachine;
import in.ripple.user.persistence.repository.VirtualMachineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class VirtualMachineDaoService {

    @Autowired
    VirtualMachineRepository virtualMachineRepository;

    public List<VirtualMachine> findTopNByMemory(int size, int offset) {

        return virtualMachineRepository.findTopNByMemory(size, offset);

    }

    public List<VirtualMachine> findAllById(List<Integer> idList) {
        return virtualMachineRepository.findAllById(idList);
    }

    public VirtualMachine save(VirtualMachine machine) {
        return virtualMachineRepository.save(machine);
    }


    public void saveAll(List<VirtualMachine> vms) {
        virtualMachineRepository.saveAll(vms);
    }

    public List<VirtualMachine> findAll(List<Long> ids) {
        return virtualMachineRepository.findAllById(ids);
    }

    public List<VirtualMachine> findByPageSizeAndOffset(int pageSize, int offset) {
        return virtualMachineRepository.findByPageSizeAndOffset(pageSize, offset);
    }
}
