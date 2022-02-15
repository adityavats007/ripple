package in.ripple.user.persistence.entity;

import javax.persistence.*;

@Entity
@Table(name = "virtual_machine")
public class VirtualMachine {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(name = "os_type")
    String osType;

    @Column(name = "ram_size")
    Long ramInBytes;

    @Column(name = "hard_disk_size")
    Long hardDiskInBytes;

    @Column(name = "cpu_cores")
    int cpuCores;

    @Column(name = "address")
    String address;

    @Column(name = "is_allocated")
    Boolean isAllocated;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getOsType() {
        return osType;
    }

    public void setOsType(String osType) {
        this.osType = osType;
    }

    public Long getRamInBytes() {
        return ramInBytes;
    }

    public void setRamInBytes(Long ramInBytes) {
        this.ramInBytes = ramInBytes;
    }

    public Long getHardDiskInBytes() {
        return hardDiskInBytes;
    }

    public void setHardDiskInBytes(Long hardDiskInBytes) {
        this.hardDiskInBytes = hardDiskInBytes;
    }

    public int getCpuCores() {
        return cpuCores;
    }

    public void setCpuCores(int cpuCores) {
        this.cpuCores = cpuCores;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Boolean getAllocated() {
        return isAllocated;
    }

    public void setAllocated(Boolean allocated) {
        isAllocated = allocated;
    }
}
