package in.ripple.user.persistence.entity;


import javax.persistence.*;

@Entity
@Table(name = "user_vm_mapping")
public class UserVmMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(name = "user_id")
    Long userId;

    @Column(name = "vm_id")
    Long vmId;

    @Column(name = "is_allocated")
    Boolean isAllocated;

    public Long getUserId() {
        return userId;
    }

    public long getId() {
        return id;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getVmId() {
        return vmId;
    }

    public void setVmId(Long vmId) {
        this.vmId = vmId;
    }

    public Boolean getAllocated() {
        return isAllocated;
    }

    public void setAllocated(Boolean allocated) {
        isAllocated = allocated;
    }
}
