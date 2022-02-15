package in.ripple.user.controllers.vm;

import com.google.gson.Gson;
import in.ripple.user.controllers.AbstractRestController;
import in.ripple.user.controllers.vm.model.GetTopVmsByMemoryRequest;
import in.ripple.user.controllers.vm.model.GetTopVmsByMemoryResponse;
import in.ripple.user.persistence.dao.UserDaoService;
import in.ripple.user.persistence.dao.UserVMMappingDaoService;
import in.ripple.user.persistence.dao.VirtualMachineDaoService;
import in.ripple.user.persistence.entity.VirtualMachine;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

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

    @PreAuthorize("hasAuthority('getTopVmByMemory')")
    @Override
    @CrossOrigin
    @PostMapping(value = "{userRole}/getTopVmByMemory", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    public Object process(HttpServletRequest httpRequest, @RequestBody String jsonRequest) throws Exception {
        GetTopVmsByMemoryResponse response = new GetTopVmsByMemoryResponse();
        final GetTopVmsByMemoryRequest vmRequest = new Gson().fromJson(jsonRequest, GetTopVmsByMemoryRequest.class);

        final List<VirtualMachine> vmList = virtualMachineDaoService.findTopNByMemory(vmRequest.getNumberOfMachines(), vmRequest.getOffset());

        if (vmList.size() > 0) {
            response.setVirtualMachineList(vmList);
        } else {
            //throw exception or just log it if no allocated vms are available
            LOG.info("No vms allocated");
        }
        response.setMessage("VM list fetched successfully");
        response.setCode("200");
        return new ResponseEntity(response, HttpStatus.OK);
    }
}
