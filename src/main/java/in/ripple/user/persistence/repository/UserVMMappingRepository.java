package in.ripple.user.persistence.repository;

import in.ripple.user.persistence.entity.UserEntity;
import in.ripple.user.persistence.entity.UserVmMapping;
import in.ripple.user.persistence.entity.VirtualMachine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface UserVMMappingRepository extends JpaRepository<UserVmMapping, Long> {

    @Query(nativeQuery = true, value = "select id from user_vm_mapping uvm join virtual_machine vm on uvm.vm_id=vm.id where is_allocated= true and uvm.user_id=?1 order by vm.memory LIMIT ?2 OFFSET ?3")
    public List<Integer> findTopNMachineIdsByUser(long user, int size, int offset);

    @Query(nativeQuery = true, value = "select * from user_vm_mapping uvm join virtual_machine vm on uvm.vm_id=vm.id where uvm.is_allocated= true and uvm.user_id=?1 ")
    public List<UserVmMapping> findMachinesByUser(long user);

    @Modifying
    @Query(nativeQuery = true, value = "update user_vm_mapping set is_allocated = false where user_id = ?1")
    public void deAllocateMachinesForUser(long user);

    @Query(nativeQuery = true, value = "select vm_id from user_vm_mapping where user_id = ?1 and is_allocated=true LIMIT ?2 OFFSET ?3")
    public List<Integer> findAllVmIdsByUser(long user, int pageSize, int offset);

    @Query(nativeQuery = true, value = "select user_id from user_vm_mapping where vm_id = ?1 and is_allocated=true LIMIT 1")
    public long findUserByVm(long vm);



}
