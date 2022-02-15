package in.ripple.user.controllers.vm.model;

import in.ripple.user.persistence.entity.VirtualMachine;

import java.util.List;

public class GetTopVmsByMemoryResponse {
    List<VirtualMachine> vmList;

    public List<VirtualMachine> getVmList() {
        return vmList;
    }

    public void setVmList(List<VirtualMachine> vmList) {
        this.vmList = vmList;
    }
}
