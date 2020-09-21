package org.entando.entando.aps.system.services.health;

import com.agiletec.aps.system.services.health.IHealthDAO;
import org.entando.entando.ent.util.EntLogging.EntLogger;
import org.entando.entando.ent.util.EntLogging.EntLogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HealthService implements IHealthService {

    private final EntLogger logger = EntLogFactory.getSanitizedLogger(getClass());

    private IHealthDAO healthDAO;

    @Autowired
    public HealthService(IHealthDAO healthDAO) {
        this.healthDAO = healthDAO;
    }

    public HealthService setHealthDAO(IHealthDAO healthDAO) {
        this.healthDAO = healthDAO;
        return this;
    }

    @Override
    public boolean isHealthy() {

        // check port schema connectivity
        if (! this.healthDAO.isPortDBConnectionHealthy()) {
            logger.error("Can't establish connection with Port database schema");
            return false;
        }

        // check serv schema connectivity
        if (! this.healthDAO.isServDBConnectionHealthy()) {
            logger.error("Can't establish connection with Serv database schema");
            return false;
        }

        return true;
    }
}
