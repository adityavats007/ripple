package in.ripple.user.controllers.vm.model;

import in.ripple.user.controllers.AbstractResponse;
import in.ripple.user.persistence.entity.VirtualMachine;

import java.util.ArrayList;
import java.util.List;

public class GetTopVmsByMemoryResponse extends AbstractResponse {
    List<VirtualMachine> virtualMachineList;

    public GetTopVmsByMemoryResponse() {
        this.virtualMachineList = new ArrayList<>();
    }

    public List<VirtualMachine> getVirtualMachineList() {
        return virtualMachineList;
    }

    public void setVirtualMachineList(List<VirtualMachine> virtualMachineList) {
        this.virtualMachineList = virtualMachineList;
    }
}
