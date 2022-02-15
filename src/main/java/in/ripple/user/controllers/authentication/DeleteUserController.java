package in.ripple.user.controllers.authentication;

import com.google.gson.Gson;
import in.ripple.user.util.ErrorResponse;
import in.ripple.user.Exception.InternalException;
import in.ripple.user.Exception.UserNotFoundException;
import in.ripple.user.constants.UserConstants;
import in.ripple.user.controllers.AbstractRestController;
import in.ripple.user.controllers.authentication.model.DeleteUserRequest;
import in.ripple.user.controllers.authentication.model.DeleteUserResponse;
import in.ripple.user.persistence.dao.UserDaoService;
import in.ripple.user.persistence.dao.UserVMMappingDaoService;
import in.ripple.user.persistence.dao.VirtualMachineDaoService;
import in.ripple.user.persistence.entity.UserEntity;

import in.ripple.user.persistence.entity.UserVmMapping;
import in.ripple.user.persistence.entity.VirtualMachine;
import in.ripple.user.util.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @Autowired
    Validator validator;

    @PreAuthorize("hasAuthority('deleteUser')")
    @Override
    @CrossOrigin
    @PostMapping(value = "{userRole}/deleteUser", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    public Object process(HttpServletRequest httpRequest, @RequestBody String jsonRequest) throws Exception {

        LOG.info("started executing {}", this.getClass().getName());
        DeleteUserResponse deleteUserResponse = new DeleteUserResponse();
        final DeleteUserRequest vmRequest = new Gson().fromJson(jsonRequest, DeleteUserRequest.class);
    try {
        final String idType = vmRequest.getIdentifierType();

        final String idValue = vmRequest.getIdentifierValue();
        if (!(validator.isValidString(idType) && validator.isValidString(idValue))) {
            LOG.error("Invalid input");
            throw new InternalException("Invalid input");
        }

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

        } else {
            LOG.info("No user found");

            throw new UserNotFoundException("No user found");
        }
    } catch (UserNotFoundException e){
        return new ResponseEntity(new ErrorResponse(HttpStatus.NOT_FOUND, e.getMessage()), HttpStatus.NOT_FOUND);

    } catch (InternalException e){
        return new ResponseEntity(new ErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage()), HttpStatus.BAD_REQUEST);

    } catch (Exception e){
        return new ResponseEntity(new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);

    }
        deleteUserResponse.setMessage("user deleted successfully");
        deleteUserResponse.setCode("200");
        return new ResponseEntity(deleteUserResponse, HttpStatus.OK);
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
