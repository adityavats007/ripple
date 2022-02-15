package in.ripple.user.controllers.vm.model;

import in.ripple.user.persistence.entity.VirtualMachine;

import java.util.List;

public class GetTopVmsFoUserResponse {
    List<VirtualMachine> virtualMachineList;

    public List<VirtualMachine> getVirtualMachineList() {
        return virtualMachineList;
    }

    public void setVirtualMachineList(List<VirtualMachine> virtualMachineList) {
        this.virtualMachineList = virtualMachineList;
    }
}
