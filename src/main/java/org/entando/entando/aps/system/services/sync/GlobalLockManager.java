package org.entando.entando.aps.system.services.sync;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import org.entando.entando.aps.system.services.sync.exception.GlobalLockEntException;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component("GlobalLockManager")
@Order(99)
public class GlobalLockManager implements IGlobalLockManager {

    @Override
    public String tryLock(String var1, String var2, Duration var3) throws GlobalLockEntException {
        return "";
    }

    @Override
    public String lock(String var1, String var2, Duration var3, Duration var4) throws GlobalLockEntException {
        return "";
    }

    @Override
    public boolean unlock(String var1, String var2) throws GlobalLockEntException {
        return false;
    }

    @Override
    public Optional<Map<String, String>> verifyLock(String var1) throws GlobalLockEntException {
        return Optional.empty();
    }
}
