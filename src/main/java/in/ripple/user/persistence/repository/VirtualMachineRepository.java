package in.ripple.user.persistence.repository;

import in.ripple.user.persistence.entity.UserEntity;
import in.ripple.user.persistence.entity.VirtualMachine;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface VirtualMachineRepository extends JpaRepository<VirtualMachine, Long> {


    @Query(nativeQuery = true, value = "select * from virtual_machine where is_allocated= true order by ram_size desc LIMIT ?1 OFFSET ?2")
    public List<VirtualMachine> findTopNByMemory(int size,int offset);

    @Query(nativeQuery = true, value = "select * from virtual_machine where id in ?1")
    public List<VirtualMachine> findAllById(List<Integer> id);

    @Query(nativeQuery = true, value = "select * from virtual_machine where is_allocated = false order by ram_size desc LIMIT ?1 OFFSET ?2")
    public List<VirtualMachine> findByPageSizeAndOffset(int pageSize, int offset);



}
