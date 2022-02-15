package in.ripple.user.controllers.authentication;

import com.google.gson.Gson;
import in.ripple.user.constants.UserConstants;
import in.ripple.user.controllers.AbstractRestController;
import in.ripple.user.controllers.authentication.model.DeleteUserRequest;
import in.ripple.user.controllers.authentication.model.DeleteUserResponse;
import in.ripple.user.controllers.vm.VmProvisionerController;
import in.ripple.user.persistence.dao.UserDaoService;
import in.ripple.user.persistence.dao.UserVMMappingDaoService;
import in.ripple.user.persistence.dao.VirtualMachineDaoService;
import in.ripple.user.persistence.entity.UserEntity;

import in.ripple.user.persistence.entity.UserVmMapping;
import in.ripple.user.persistence.entity.VirtualMachine;
import in.ripple.user.util.HashUtil;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Component("deleteUser")
public class DeleteUserController extends AbstractRestController {

    private static final Logger LOG = LoggerFactory.getLogger(DeleteUserController.class);


    @Autowired
    UserDaoService userDaoService;

    @Autowired
    UserVMMappingDaoService userVMMappingDaoService;

    @Autowired
    VirtualMachineDaoService virtualMachineDaoService;

    @Override
    @CrossOrigin
    @PostMapping(value = "/deleteUser", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    public String process(HttpServletRequest httpRequest, @RequestBody String jsonRequest) throws Exception {

        LOG.info("started executing {}", this.getClass().getName());

        final DeleteUserRequest vmRequest = new Gson().fromJson(jsonRequest, DeleteUserRequest.class);

        final String idType = vmRequest.getIdentifierType();

        final String idValue = vmRequest.getIdentifierValue();

        UserEntity userEntity = null;

        if (UserConstants.MOBILE.equalsIgnoreCase(idType)) {

            userEntity = userDaoService.findByEmail(idValue);

        } else if (UserConstants.EMAIL.equalsIgnoreCase(idType)) {

            userEntity = userDaoService.findByMobile(idValue);

        } else if (UserConstants.USER_NAME.equalsIgnoreCase(idType)) {

            userEntity = userDaoService.findByUserName(idValue);

        }
        if (null != userEntity) {

            userEntity.setDeleted(true);
            userDaoService.save(userEntity);//first marking the user as deleted

            try {
                //de allocate the VMs
                deAllocateVm(userEntity);

            } catch (Exception e) {
                //consuming this exception.
                LOG.error("Error while deAllocation of vms", e);
            }

        }

        DeleteUserResponse deleteUserResponse = new DeleteUserResponse();
        JSONObject response= new JSONObject();
        response.put("message","User deleted successfully");
        return response.toJSONString();
    }

    //soft deletion of mapped VMs. This method just marks the allocation status as false for a user

    void deAllocateVm(UserEntity userEntity) {


        long userId = userEntity.getId();

        List<Long> vmIdList = new ArrayList<>();

        List<UserVmMapping> userVmMappings = userVMMappingDaoService.findMachinesByUser(userId);

        List<UserVmMapping> updatedUserVmMapping = new ArrayList<>();


        //remove the vm mapping from the user
        if (userVmMappings.size() > 0) {
            LOG.info("started deAllocation of VM");
            userVmMappings.forEach(userVmMapping -> {

                userVmMapping.setAllocated(false);

                updatedUserVmMapping.add(userVmMapping);

            });
            userVMMappingDaoService.saveAll(updatedUserVmMapping);
            LOG.info("Mapped vms are de allocated for user {}", userEntity.getUserName());
        }else{
            LOG.info("No allocated VMs for this user");
        }
        //free the VMs in vmIdList

        vmIdList = userVmMappings.stream().map(e -> (e.getVmId())).collect(Collectors.toList());

        List<VirtualMachine> virtualMachines = virtualMachineDaoService.findAll(vmIdList);

        List<VirtualMachine> updatedVirtualMachines = new ArrayList<>();
        if (virtualMachines.size() > 0) {
            LOG.info("Freeing the vms for re allocation");
            virtualMachines.forEach(machine -> {

                machine.setAllocated(false);

                updatedVirtualMachines.add(machine);
            });

            virtualMachineDaoService.saveAll(updatedVirtualMachines);

        }

    }


}
