package sample.security;

import com.atlassian.crowd.model.group.Group;
import com.atlassian.crowd.model.user.User;
import com.atlassian.crowd.service.client.CrowdClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class CrowdAuthenticationProvider implements AuthenticationProvider {

    private static final Logger logger = LoggerFactory.getLogger(CrowdAuthenticationProvider.class);
    private CrowdClient crowdClient;

    @Inject
    public CrowdAuthenticationProvider(CrowdClient crowdClient) {
        this.crowdClient = crowdClient;
    }

    /**
     * @see org.springframework.security.authentication.AuthenticationProvider#authenticate(org.springframework.security.core.Authentication)
     */
    @Override
    public Authentication authenticate(final Authentication auth) throws AuthenticationException {

        try {
            User user = null;

            if( auth.getCredentials() != null && !auth.getCredentials().toString().equalsIgnoreCase("n/a")) {
                user = crowdClient.authenticateUser(auth.getName(), auth.getCredentials().toString());
            }
            return getAuthenticationForUser(user);

        } catch (Exception e) {
            logger.error(e.getMessage());
        }

        return null;
    }

    /**
     * Return the Authentication Object for the given user.
     * @param user
     * @return
     * @throws Exception
     */
    private Authentication getAuthenticationForUser(User user) throws Exception {
        UsernamePasswordAuthenticationToken upAuth = null;

        if (user != null) {
            List<GrantedAuthority> grantedAuthorities = new ArrayList<GrantedAuthority>();

            List<Group> groupsForUser = crowdClient.getGroupsForNestedUser(user.getName(), 0, -1);

            if( groupsForUser != null && !groupsForUser.isEmpty() ) {
                for(Group group : groupsForUser) {
                    grantedAuthorities.add(new SimpleGrantedAuthority(group.getName()));
                }
            }

            upAuth = new UsernamePasswordAuthenticationToken(user.getName(), "", grantedAuthorities);
            upAuth.setDetails(user);
            this.logger.debug("LOGIN attempt for user " + user.getName() + " successful");
        }

        return upAuth;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean supports(final Class<? extends Object> arg0) {
        return true;
    }
}