package in.ripple.user.controllers.vm;

import com.google.gson.Gson;
import in.ripple.user.controllers.AbstractRestController;
import in.ripple.user.controllers.vm.model.GetTopVmsByMemoryRequest;
import in.ripple.user.persistence.dao.UserDaoService;
import in.ripple.user.persistence.dao.UserVMMappingDaoService;
import in.ripple.user.persistence.dao.VirtualMachineDaoService;
import in.ripple.user.persistence.entity.UserEntity;
import in.ripple.user.persistence.entity.UserVmMapping;
import in.ripple.user.persistence.entity.VirtualMachine;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

@RestController
@Component("getTopVmByMemory")
public class GetTopNVmsInSystemByMemoryController extends AbstractRestController {

    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(GetTopNVmsInSystemByMemoryController.class);

    @Autowired
    VirtualMachineDaoService virtualMachineDaoService;

    @Autowired
    UserVMMappingDaoService userVMMappingDaoService;

    @Autowired
    UserDaoService userDaoService;

    @Override
    @CrossOrigin
    @PostMapping(value = "/getTopVmByMemory", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    public String process(HttpServletRequest httpRequest, @RequestBody String jsonRequest) throws Exception {

        final GetTopVmsByMemoryRequest vmRequest = new Gson().fromJson(jsonRequest, GetTopVmsByMemoryRequest.class);

        final List<VirtualMachine> vmList = virtualMachineDaoService.findTopNByMemory(vmRequest.getNumberOfMachines(), vmRequest.getOffset());

        final JSONArray jsonArray = new JSONArray();

        if (vmList.size() > 0) {

            vmList.forEach(vm -> {
                try {

                    final JSONObject jsonObject = new JSONObject();


                    jsonObject.put("address", vm.getAddress());

                    jsonObject.put("osType", vm.getOsType());

                    jsonObject.put("memory", vm.getRamInBytes());

                    jsonObject.put("hardDisk", vm.getHardDiskInBytes());

                    jsonObject.put("cpuCores", vm.getCpuCores());

                    jsonObject.put("vm_id", vm.getId());

                    try {
                        long userId = userVMMappingDaoService.findUserByVm(vm.getId());
                        UserEntity userEntity = userDaoService.findById(userId);
                        if (null != userEntity) {
                            jsonObject.put("mapped_to_user_id", userEntity.getId());
                        }
                    } catch (Exception e) {
                        LOG.error("", e);
                    }

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
