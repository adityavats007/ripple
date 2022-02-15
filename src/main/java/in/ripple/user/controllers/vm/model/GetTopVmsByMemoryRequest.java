package in.ripple.user.controllers.vm.model;

import java.util.List;

public class GetTopVmsByMemoryRequest {
    List<String> vmAllocationStatusList;
    int numberOfMachines;
    int offset;

    public List<String> getVmAllocationStatusList() {
        return vmAllocationStatusList;
    }

    public void setVmAllocationStatusList(List<String> vmAllocationStatusList) {
        this.vmAllocationStatusList = vmAllocationStatusList;
    }

    public int getNumberOfMachines() {
        return numberOfMachines;
    }

    public void setNumberOfMachines(int numberOfMachines) {
        this.numberOfMachines = numberOfMachines;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }
}
