package sample.config;

import com.atlassian.crowd.integration.http.CrowdHttpAuthenticator;
import com.atlassian.crowd.integration.http.CrowdHttpAuthenticatorImpl;
import com.atlassian.crowd.integration.http.util.CrowdHttpTokenHelperImpl;
import com.atlassian.crowd.integration.http.util.CrowdHttpValidationFactorExtractorImpl;
import com.atlassian.crowd.integration.rest.service.factory.RestCrowdClientFactory;
import com.atlassian.crowd.service.client.ClientPropertiesImpl;
import com.atlassian.crowd.service.client.CrowdClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import sample.security.CrowdAuthenticationProvider;
import sample.security.RestAccessDeniedHandler;
import sample.security.RestAuthenticationEntryPoint;

import java.util.Properties;

@EnableWebSecurity
@Configuration
public class CrowdSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private Environment env;

    @Override
    public void configure(WebSecurity web) throws Exception {
        web
            .ignoring()
                .antMatchers("/api/logout");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .csrf()
                .disable()
            .headers()
                .xssProtection()
                    .disable()
            .exceptionHandling()
                .authenticationEntryPoint(new RestAuthenticationEntryPoint())
                .accessDeniedHandler(new RestAccessDeniedHandler())
            .and()
        .authorizeRequests()
            .antMatchers("/api/login").permitAll()
            .antMatchers("/api/**").hasAuthority("pse-users");

    }



    public ClientPropertiesImpl clientProperties() {
        Properties p = new Properties();
        p.setProperty("application.name", env.getProperty("crowd.application.name", "application_name"));
        p.setProperty("application.password", env.getProperty("crowd.application.password", "application_password"));
        p.setProperty("crowd.server.url", env.getProperty("crowd.server.url", "http://localhost:8095/crowd"));
        p.setProperty("session.validationInterval", env.getProperty("crowd.session.validationinterval", "0"));
        return ClientPropertiesImpl.newInstanceFromProperties(p);
    }

    @Bean
    public CrowdClient crowdClient() {
        return new RestCrowdClientFactory().newInstance( clientProperties());
    }

    @Bean
    public CrowdHttpAuthenticator crowdHttpAuthenticator() {
        return new CrowdHttpAuthenticatorImpl(crowdClient(), clientProperties(), CrowdHttpTokenHelperImpl.getInstance(CrowdHttpValidationFactorExtractorImpl.getInstance()));
    }

    @Bean
    public CrowdAuthenticationProvider crowdAuthenticationProvider() {
        return new CrowdAuthenticationProvider(crowdClient());
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(crowdAuthenticationProvider());
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}