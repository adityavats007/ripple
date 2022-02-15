package in.ripple.user.controllers.vm.model;

import in.ripple.user.util.AbstractResponse;
import in.ripple.user.persistence.entity.VirtualMachine;

import java.util.ArrayList;
import java.util.List;

public class GetTopVmsFoUserResponse extends AbstractResponse {
    List<VirtualMachine> virtualMachineList;

    public GetTopVmsFoUserResponse() {
        this.virtualMachineList = new ArrayList<>();
    }

    public List<VirtualMachine> getVirtualMachineList() {
        return virtualMachineList;
    }

    public void setVirtualMachineList(List<VirtualMachine> virtualMachineList) {
        this.virtualMachineList = virtualMachineList;
    }
}
