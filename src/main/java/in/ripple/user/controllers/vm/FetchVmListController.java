package in.ripple.user.controllers.vm;

import com.google.gson.Gson;
import in.ripple.user.Exception.UserNotFoundException;
import in.ripple.user.configuration.JwtTokenUtil;
import in.ripple.user.controllers.AbstractRestController;
import in.ripple.user.controllers.vm.model.FetchVmListRequest;
import in.ripple.user.controllers.vm.model.GetTopVmsForUserRequest;
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

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@Component("fetchVmList")
public class FetchVmListController extends AbstractRestController {

    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(FetchVmListController.class);

    @Autowired
    VirtualMachineDaoService virtualMachineDaoService;

    @Autowired
    UserVMMappingDaoService userVMMappingDaoService;

    @Autowired
    JwtTokenUtil jwtTokenUtil;

    @Autowired
    UserDaoService userDaoService;

    @Override
    @CrossOrigin
    @PostMapping(value = "/fetchVmList", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    public String process(HttpServletRequest httpRequest, @RequestBody String jsonRequest) throws Exception {

        final FetchVmListRequest vmRequest = new Gson().fromJson(jsonRequest, FetchVmListRequest.class);

        final List<VirtualMachine> vmList = virtualMachineDaoService.findByPageSizeAndOffset(vmRequest.getPageSize(),vmRequest.getOffset());

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
            LOG.info("No vms found");
        }

        return jsonArray.toJSONString();
    }

}
