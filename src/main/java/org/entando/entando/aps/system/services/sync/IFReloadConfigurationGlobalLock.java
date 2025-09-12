package org.entando.entando.aps.system.services.sync;

import com.agiletec.aps.util.ApsWebApplicationUtils;
import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.entando.entando.aps.system.services.IFeatureFlag;
import org.entando.entando.ent.util.EntLogging.EntLogFactory;
import org.entando.entando.ent.util.EntLogging.EntLogger;
import org.springframework.web.context.WebApplicationContext;

public interface IFReloadConfigurationGlobalLock extends IFeatureFlag {
    boolean GLOBAL_LOCK_ENABLED = IFeatureFlag.readEnablementStatus("GLOBAL_LOCK");

    String LOCK_KEY = "reload-config";

    EntLogger logger = EntLogFactory.getSanitizedLogger(IFReloadConfigurationGlobalLock.class);

    static String doLock(WebApplicationContext wac, String user) {
        try {
            if (wac != null) {
                final String uname = StringUtils.isNotBlank(user) ?  user : "system";
                final IGlobalLockManager glm = (IGlobalLockManager) wac.getBean(IGlobalLockManager.BEAN_ID);

                return glm.lock(LOCK_KEY, user, Duration.ofMinutes(5),  Duration.ofMillis(250));
            }
        } catch (Exception e) {
            logger.warn("unexpected error while acquiring the lock {}; message is {}", LOCK_KEY, e.getMessage());
        }
        return null;
    }

    static boolean doUnlock(WebApplicationContext wac, String token) {
        try {
            if (wac != null) {
                IGlobalLockManager glm = (IGlobalLockManager) wac.getBean(IGlobalLockManager.BEAN_ID);

                return glm.unlock(LOCK_KEY, token);
            }
        } catch (Exception e) {
            logger.warn("unexpected error while releasing the lock: {}; message is {}", LOCK_KEY, e.getMessage());
        }
        return false;
    }

    /**
     * Check whether there's a reload in progress. This method returns true if there is a reload in progress and the
     * request involves the frontend of the application. Otherwise, it returns false
     *
     * @param request the request
     * @return true if there's a reload in progress and the user must be redirected to the service page
     */
    static boolean isReloadInProgess(HttpServletRequest request) {
        try {
            if (request != null) {
                final String uri = request.getRequestURI();
                final IGlobalLockManager glm = (IGlobalLockManager) ApsWebApplicationUtils.getBean(
                        IGlobalLockManager.BEAN_ID, request);
                final Pattern fePattern = Pattern.compile("/page/[a-z]{2}/", Pattern.CASE_INSENSITIVE);
                final Pattern apiPattern = Pattern.compile("/api/(?!actuator|health)");

                if (IFReloadConfigurationGlobalLock.GLOBAL_LOCK_ENABLED
                        && (fePattern.matcher(uri).find() || apiPattern.matcher(uri).find())) {
                    return glm.verifyLock(LOCK_KEY).isPresent();
                }
            }
        } catch (Exception e) {
            logger.warn("unexpected error while verifying the lock: {} for redirection; message is {}", LOCK_KEY, e.getMessage());
        }
        return false;
    }

    /**
     * Return the key used by the reload configuration lock
     * @return
     */
    static Optional<Map<String, String>> getLockData(HttpServletRequest request) {
        final IGlobalLockManager glm = (IGlobalLockManager) ApsWebApplicationUtils.getBean(IGlobalLockManager.BEAN_ID,
                request);
        return getLockData(glm);
    }

    static Optional<Map<String, String>> getLockData(IGlobalLockManager glm) {
        try {
            return glm.verifyLock(LOCK_KEY);
        } catch (Exception e) {
            logger.warn("unexpected error while getting data of the lock: {}; message is {}", LOCK_KEY, e.getMessage());
        }
        return Optional.empty();
    }

    default boolean isEnabled() {
        return GLOBAL_LOCK_ENABLED;
    }
}
