package in.ripple.user.controllers.vm;

import com.google.gson.Gson;
import in.ripple.user.util.ErrorResponse;
import in.ripple.user.configuration.JwtTokenUtil;
import in.ripple.user.controllers.AbstractRestController;
import in.ripple.user.controllers.vm.model.FetchVmListRequest;
import in.ripple.user.controllers.vm.model.FetchVmListResponse;
import in.ripple.user.persistence.dao.UserDaoService;
import in.ripple.user.persistence.dao.UserVMMappingDaoService;
import in.ripple.user.persistence.dao.VirtualMachineDaoService;
import in.ripple.user.persistence.entity.VirtualMachine;
import org.json.simple.JSONArray;
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

    @PreAuthorize("hasAuthority('fetchVmList')")
    @Override
    @CrossOrigin
    @PostMapping(value = "{userRole}/fetchVmList", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    public Object process(HttpServletRequest httpRequest, @RequestBody String jsonRequest) throws Exception {

        FetchVmListResponse response = new FetchVmListResponse();

        final FetchVmListRequest vmRequest = new Gson().fromJson(jsonRequest, FetchVmListRequest.class);

        final List<VirtualMachine> vmList = virtualMachineDaoService.findByPageSizeAndOffset(vmRequest.getPageSize(), vmRequest.getOffset());

        final JSONArray jsonArray = new JSONArray();

        if (vmList.size() > 0) {
            try {
                response.setVirtualMachineList(vmList);
            } catch (Exception e) {
                LOG.error("Error while setting vm list", e);
                return new ResponseEntity(new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Some internal error occurred"), HttpStatus.INTERNAL_SERVER_ERROR);
            }

        } else {
            //throw exception or just log it if no allocated vms are available
            LOG.info("No vms found");
        }
        response.setMessage("VM list fetched successfully");
        response.setCode("200");
        return new ResponseEntity(response, HttpStatus.OK);
    }

}
