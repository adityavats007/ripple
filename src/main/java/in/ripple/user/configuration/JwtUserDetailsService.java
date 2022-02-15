package in.ripple.user.configuration;

import in.ripple.user.persistence.dao.UserDaoService;
import in.ripple.user.persistence.entity.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class JwtUserDetailsService implements UserDetailsService {

    @Autowired
    UserDaoService userDaoService;

    @Value("${master.role.api.access}")
    String masterRoleApiAccess;

    @Value("${default.role.api.access}")
    String defaultRoleApiAccess;

    @Value("${master.role.id}")
    String masterId;

    @Value("${default.role.id}")
    String defaultId;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        UserEntity userEntity = userDaoService.findByUserName(username);
        if (null == userEntity) {
            throw new UsernameNotFoundException("No user found");

        }
        List<String> apiListString = new ArrayList<>();
        if (userEntity.getUserName().equals(username)) {
            Set<GrantedAuthority> authorities = new HashSet<>();
//            Role role = roleDaoService.findById(userEntity.getRole());
            if (userEntity.getRole().toString().equalsIgnoreCase("1")) {
                apiListString = Arrays.asList(masterRoleApiAccess.split(",").clone());
            } else if (userEntity.getRole().toString().equalsIgnoreCase("2")) {
//                authorities.add(new SimpleGrantedAuthority("default"));
                apiListString = Arrays.asList(defaultRoleApiAccess.split(",").clone());
            }
            apiListString.forEach(e -> {
                authorities.add(new SimpleGrantedAuthority(e));
            });

            return new User(username, userEntity.getPassword(), authorities);
        } else {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
    }
}
