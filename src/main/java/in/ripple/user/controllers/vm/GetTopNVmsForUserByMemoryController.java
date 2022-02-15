package in.ripple.user.controllers.vm;

import com.google.gson.Gson;
import in.ripple.user.util.ErrorResponse;
import in.ripple.user.exception.InternalException;
import in.ripple.user.exception.UserNotFoundException;
import in.ripple.user.configuration.JwtTokenUtil;
import in.ripple.user.controllers.AbstractRestController;
import in.ripple.user.controllers.vm.model.GetTopVmsFoUserResponse;
import in.ripple.user.controllers.vm.model.GetTopVmsForUserRequest;
import in.ripple.user.persistence.dao.UserDaoService;
import in.ripple.user.persistence.dao.UserVMMappingDaoService;
import in.ripple.user.persistence.dao.VirtualMachineDaoService;
import in.ripple.user.persistence.entity.UserEntity;
import in.ripple.user.persistence.entity.VirtualMachine;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@Component("getTopVmsByUser")
public class GetTopNVmsForUserByMemoryController extends AbstractRestController {

    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(GetTopNVmsForUserByMemoryController.class);

    @Autowired
    VirtualMachineDaoService virtualMachineDaoService;

    @Autowired
    UserVMMappingDaoService userVMMappingDaoService;

    @Autowired
    JwtTokenUtil jwtTokenUtil;

    @Autowired
    UserDaoService userDaoService;


    @Value("${master.role.id}")
    String masterId;

    @Value("${default.role.id}")
    String defaultId;


    @PreAuthorize("hasAuthority('getTopVmsByUser')")
    @Override
    @CrossOrigin
    @PostMapping(value = "{userRole}/getTopVmsByUser", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    public Object process(HttpServletRequest httpRequest, @RequestBody String jsonRequest) throws Exception {
        GetTopVmsFoUserResponse response = new GetTopVmsFoUserResponse();
        try {
            final GetTopVmsForUserRequest vmRequest = new Gson().fromJson(jsonRequest, GetTopVmsForUserRequest.class);

            final String token = httpRequest.getHeader("Authorization");//get user from token

            final String userNameFromToken = jwtTokenUtil.getUserNameFromToken(token);//get user name from token

            UserEntity userEntity = userDaoService.findByUserName(userNameFromToken); // get user entity


            if (null == userEntity && vmRequest.getUserId() == null) {
                //for non master roles in which user is only identifiable by JWT

                throw new UserNotFoundException("404", "User Not Found");

            } else if (vmRequest.getUserId() != null) {
                //check if this is non master
                if (Long.parseLong(masterId) != userEntity.getRole()) {

                    throw new InternalException("Not allowed to view other user details");
                }
                //for master roles in which user is identifiable by user id in request body

                userEntity = userDaoService.findById(vmRequest.getUserId());
            }

            final List<Integer> mappedVm = userVMMappingDaoService.findAllVmIdsByUser(userEntity.getId(), vmRequest.getNumberOfMachines(), vmRequest.getOffset());
            final List<VirtualMachine> vmList = virtualMachineDaoService.findAllById(mappedVm);

            if (vmList.size() > 0) {
                response.setVirtualMachineList(vmList);
            } else {
                //throw exception or just log it if no allocated vms are available
                LOG.info("No vms allocated");
            }
        } catch (InternalException e) {

            return new ResponseEntity(new ErrorResponse(HttpStatus.FORBIDDEN, "Not allowed to view other's details"), HttpStatus.FORBIDDEN);
        } catch (UserNotFoundException e) {
            return new ResponseEntity(new ErrorResponse(HttpStatus.NOT_FOUND, "User not found"), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity(new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Some internal error occurred"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.setMessage("VM list fetched successfully");
        response.setCode("200");
        return new ResponseEntity(response, HttpStatus.OK);
    }

}
