package in.ripple.user.controllers.vm;

import com.google.gson.Gson;
import in.ripple.user.util.ErrorResponse;
import in.ripple.user.Exception.InternalException;
import in.ripple.user.Exception.UserNotFoundException;
import in.ripple.user.configuration.JwtTokenUtil;
import in.ripple.user.controllers.AbstractRestController;
import in.ripple.user.controllers.vm.model.AssignVmRequest;
import in.ripple.user.controllers.vm.model.AssignVmResponse;
import in.ripple.user.persistence.dao.UserDaoService;
import in.ripple.user.persistence.dao.UserVMMappingDaoService;
import in.ripple.user.persistence.dao.VirtualMachineDaoService;
import in.ripple.user.persistence.entity.UserEntity;
import in.ripple.user.persistence.entity.UserVmMapping;
import in.ripple.user.persistence.entity.VirtualMachine;
import in.ripple.user.util.Validator;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@Component("assignVm")
public class VmProvisionerController extends AbstractRestController {

    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(VmProvisionerController.class);

    @Autowired
    UserDaoService userDaoService;

    @Autowired
    VirtualMachineDaoService virtualMachineDaoService;

    @Autowired
    UserVMMappingDaoService userVMMappingDaoService;

    @Autowired
    JwtTokenUtil jwtTokenUtil;

    @Autowired
    Validator validator;

    @PreAuthorize("hasAuthority('assignVm')")
    @Override
    @CrossOrigin
    @PostMapping(value = "{userRole}/assignVm", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    public Object process(HttpServletRequest httpRequest, @RequestBody String jsonRequest) throws Exception {

        try {
            LOG.info("started processing request for vm assignment");

            final String token = httpRequest.getHeader("Authorization");//get user from token

            final String userNameFromToken = jwtTokenUtil.getUserNameFromToken(token);// to assign the VM to this user

            final UserEntity userEntity = userDaoService.findByUserName(userNameFromToken);

            if (null == userEntity) {
                throw new UserNotFoundException("404", "User Not Found");
            }


            final AssignVmRequest vm = new Gson().fromJson(jsonRequest, AssignVmRequest.class);

            VirtualMachine virtualMachine = new VirtualMachine();

            UserVmMapping userVmMapping = new UserVmMapping();
            try {
                //get values from user input
                String os = vm.getOperatingSystem();

                long ramSize = vm.getRamSize();

                long hardDiskSize = vm.getHardDiskSize();

                int cpuCores = vm.getNumberOfCpuCores();
                //validate
                if (!(validator.isValidDiskSize(ramSize) && validator.isValidDiskSize(hardDiskSize))) {

                    throw new InternalException("", "Invalid input");
                }

                //Assign the VM
                LOG.info("Assigning VM to user {}", userEntity.getUserName());

                virtualMachine.setOsType(os);

                virtualMachine.setRamInBytes(ramSize);

                virtualMachine.setHardDiskInBytes(hardDiskSize);

                virtualMachine.setCpuCores(cpuCores);

                virtualMachine.setAllocated(true);

                virtualMachine.setAddress("127.0l0.1");//assigned some static ip

                virtualMachine = virtualMachineDaoService.save(virtualMachine);

                //VM assigned

                //Create the user to vm mapping
                userVmMapping.setVmId(virtualMachine.getId());

                Long userId = userEntity.getId();

                userVmMapping.setUserId(userId);

                userVmMapping.setAllocated(true);

                userVMMappingDaoService.save(userVmMapping);

                LOG.info("VM assigned");

            } catch (Exception e) {
                throw new InternalException(e.getMessage());
            }
        } catch (InternalException e) {
            return new ResponseEntity(new ErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage()), HttpStatus.BAD_REQUEST);
        } catch (UserNotFoundException e) {
            return new ResponseEntity(new ErrorResponse(HttpStatus.NOT_FOUND, "User not found"), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity(new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Some internal error occurred"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
         AssignVmResponse response= new AssignVmResponse("200","VM assignment done");
         return new ResponseEntity(response, HttpStatus.OK);
    }

}
