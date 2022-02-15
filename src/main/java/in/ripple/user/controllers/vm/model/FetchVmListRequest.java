package in.ripple.user.controllers.vm.model;

import java.util.List;

public class FetchVmListRequest {

    int numberOfMachines;

    int offset;

    public int getPageSize() {
        return numberOfMachines;
    }

    public void setPageSize(int pageSize) {
        this.numberOfMachines = pageSize;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }
}
