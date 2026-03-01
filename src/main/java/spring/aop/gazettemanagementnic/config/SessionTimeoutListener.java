package spring.aop.gazettemanagementnic.config;

import jakarta.servlet.annotation.WebListener;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;

/**
 * Listener for HTTP session timeout events
 * Performs cleanup when a session expires/times out
 */
@WebListener
public class SessionTimeoutListener implements HttpSessionListener {

    /**
     * Called when a session is destroyed (timeout or explicit logout)
     * @param se the session event
     */
    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        // Session has been invalidated/timed out
        // Clear any logged-in user information
        se.getSession().removeAttribute("loggedInUser");
        se.getSession().removeAttribute("userRole");
        System.out.println("Session expired/destroyed for user: " + se.getSession().getId());
    }

    /**
     * Called when a session is created
     * @param se the session event
     */
    @Override
    public void sessionCreated(HttpSessionEvent se) {
        System.out.println("New session created: " + se.getSession().getId());
        // Set default timeout (though this is configured in application.properties)
    }
}
