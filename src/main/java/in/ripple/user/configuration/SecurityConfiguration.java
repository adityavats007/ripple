package in.ripple.user.configuration;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfiguration  extends WebSecurityConfigurerAdapter {

    @Autowired
    private EntryPoint jwtAuthenticationEntryPoint;

    @Autowired
    private UserDetailsService jwtUserDetailsService;

    @Autowired
    private RequestFilter jwtRequestFilter;


    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {


        httpSecurity.csrf().disable()
// dont authenticate this particular request
                .authorizeRequests()
                .requestMatchers(new MyRequestMatcher()).permitAll().
//                .antMatchers("/default/**").access("hasAuthority('default')")
//                .antMatchers("/master/**").access("hasAuthority('master')").

// all other requests need to be authenticated
                anyRequest().authenticated().and().
// make sure we use stateless session; session won't be used to
// store user's state.
        exceptionHandling().authenticationEntryPoint(jwtAuthenticationEntryPoint).and().sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        httpSecurity.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
    }

     /**custom match logic
     1. if the request is login or signup then it will permit
     2. if the request contains headers key "clientAuth" with valid value then the request will go through, this is added in case if internal http call happens
      then the token won't get validated twice. In case it needs to be validated then token can be passed and the custom condition can be removed
      */
    private class MyRequestMatcher implements RequestMatcher {
        public boolean matches(HttpServletRequest request){
            //Define the matching logic here
            if(request.getRequestURL().toString().contains("login")||request.getRequestURL().toString().contains("signUp")){
                return true;
            }
            return false;
        }
    }

}


