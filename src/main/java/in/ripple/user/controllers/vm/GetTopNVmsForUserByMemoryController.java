package in.ripple.user.controllers.vm;

import com.google.gson.Gson;
import in.ripple.user.Exception.InternalException;
import in.ripple.user.Exception.UserNotFoundException;
import in.ripple.user.configuration.JwtTokenUtil;
import in.ripple.user.constants.UserConstants;
import in.ripple.user.controllers.AbstractRestController;
import in.ripple.user.controllers.vm.model.GetTopVmsForUserRequest;
import in.ripple.user.persistence.dao.RoleDaoService;
import in.ripple.user.persistence.dao.UserDaoService;
import in.ripple.user.persistence.dao.UserVMMappingDaoService;
import in.ripple.user.persistence.dao.VirtualMachineDaoService;
import in.ripple.user.persistence.entity.UserEntity;
import in.ripple.user.persistence.entity.VirtualMachine;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

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

    @Autowired
    RoleDaoService roleDaoService;

    @Override
    @CrossOrigin
    @PostMapping(value = "/getTopVmsByUser", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    public String process(HttpServletRequest httpRequest, @RequestBody String jsonRequest) throws Exception {

        final GetTopVmsForUserRequest vmRequest = new Gson().fromJson(jsonRequest, GetTopVmsForUserRequest.class);

        final String token = httpRequest.getHeader("Authorization");//get user from token

        final String userNameFromToken = jwtTokenUtil.getUserNameFromToken(token);//get user name from token

        UserEntity userEntity = userDaoService.findByUserName(userNameFromToken); // get user entity


        if (null == userEntity && vmRequest.getUserId()==null) {
            //for non master roles in which user is only identifiable by JWT

            throw new UserNotFoundException("404", "User Not Found");

        }else if( vmRequest.getUserId()!=null){
            //check if this is non master
            if(roleDaoService.getRoleFromName(UserConstants.MASTER_ROLE).getId()!=userEntity.getRole()){
                throw new InternalException("Not allowed to view other user details");
            }
            //for master roles in which user is identifiable by user id in request body

            userEntity=userDaoService.findById(vmRequest.getUserId());
        }

        final List<Integer> mappedVm = userVMMappingDaoService.findAllVmIdsByUser(userEntity.getId(),vmRequest.getNumberOfMachines(), vmRequest.getOffset());
        final List<VirtualMachine> vmList = virtualMachineDaoService.findAllById(mappedVm);

        final JSONArray jsonArray = new JSONArray();

        if (vmList.size() > 0) {

            vmList.forEach(vm -> {

                try {
                    final JSONObject jsonObject = new JSONObject();

                    jsonObject.put("vm_id", vm.getId());

                    jsonObject.put("address", vm.getAddress());

                    jsonObject.put("osType", vm.getOsType());

                    jsonObject.put("memory", vm.getRamInBytes());

                    jsonObject.put("hardDisk", vm.getHardDiskInBytes());

                    jsonObject.put("cpuCores", vm.getCpuCores());

                    jsonArray.add(jsonObject);
                } catch (Exception e) {

                    LOG.error("Error while processing record for vm with id {}: ", vm.getId());
                }
            });

        } else {
            //throw exception or just log it if no allocated vms are available
            LOG.info("No vms allocated");
        }

        return jsonArray.toJSONString();
    }

}
