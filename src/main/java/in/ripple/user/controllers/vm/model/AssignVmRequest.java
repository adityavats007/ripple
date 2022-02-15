package in.ripple.user.controllers.vm.model;

import org.springframework.lang.NonNull;

import java.lang.annotation.Native;

public class AssignVmRequest {

    @NonNull
    String operatingSystem;

    @NonNull
    long ramSizeInBytesInBytes;

    @NonNull
    long hardDiskSizeInBytes;

    @NonNull
    int numberOfCpuCores;

    public String getOperatingSystem() {
        return operatingSystem;
    }

    public void setOperatingSystem(String operatingSystem) {
        this.operatingSystem = operatingSystem;
    }

    public long getRamSize() {
        return ramSizeInBytesInBytes;
    }

    public void setRamSize(long ramSizeInBytesInBytes) {
        this.ramSizeInBytesInBytes = ramSizeInBytesInBytes;
    }

    public long getHardDiskSize() {
        return hardDiskSizeInBytes;
    }

    public void setHardDiskSize(long hardDiskSize) {
        this.hardDiskSizeInBytes = hardDiskSize;
    }

    public int getNumberOfCpuCores() {
        return numberOfCpuCores;
    }

    public void setNumberOfCpuCores(int numberOfCpuCores) {
        this.numberOfCpuCores = numberOfCpuCores;
    }
}
