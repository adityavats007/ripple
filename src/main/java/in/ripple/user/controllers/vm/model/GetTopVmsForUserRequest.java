package in.ripple.user.controllers.vm.model;

import java.util.List;

public class GetTopVmsForUserRequest {
        Long userId;
        List<String> vmAllocationStatusList;
        int numberOfMachines;
        int offset;


    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public List<String> getVmAllocationStatusList() {
        return vmAllocationStatusList;
    }

    public void setVmAllocationStatusList(List<String> vmAllocationStatusList) {
        this.vmAllocationStatusList = vmAllocationStatusList;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public int getNumberOfMachines() {
        return numberOfMachines;
    }

    public void setnumberOfMachines(int size) {
        this.numberOfMachines = size;
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
