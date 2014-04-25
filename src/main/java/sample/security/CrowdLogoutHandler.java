package sample.security;

import com.atlassian.crowd.exception.ApplicationPermissionException;
import com.atlassian.crowd.exception.InvalidAuthenticationException;
import com.atlassian.crowd.exception.OperationFailedException;
import com.atlassian.crowd.integration.http.CrowdHttpAuthenticator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CrowdLogoutHandler implements LogoutSuccessHandler {

    private static final Logger logger = LoggerFactory.getLogger(CrowdLogoutHandler.class);
    private final CrowdHttpAuthenticator crowdHttpAuthenticator;

    public CrowdLogoutHandler(CrowdHttpAuthenticator crowdHttpAuthenticator) {
        this.crowdHttpAuthenticator = crowdHttpAuthenticator;
    }

    @Override
    public void onLogoutSuccess(final HttpServletRequest request, final HttpServletResponse response, final Authentication authentication) throws IOException, ServletException {

        try {
            crowdHttpAuthenticator.logout(request, response);
        } catch (ApplicationPermissionException e) {
            logger.warn(e.getMessage());
        } catch (InvalidAuthenticationException e) {
            logger.warn(e.getMessage());
        } catch (OperationFailedException e) {
            logger.warn(e.getMessage());
        }

        if (authentication != null && authentication.getPrincipal() != null) {
            logger.debug("User " + authentication.getPrincipal().toString() + " logged out.");
        }
        response.sendRedirect(request.getContextPath());
    }
}