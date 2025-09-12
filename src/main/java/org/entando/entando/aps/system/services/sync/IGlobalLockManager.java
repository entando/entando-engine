package org.entando.entando.aps.system.services.sync;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import org.entando.entando.aps.system.services.sync.exception.GlobalLockEntException;

public interface IGlobalLockManager {

    String BEAN_ID = "GlobalLockManager";

    /**
     * Lock property: creation data
     */
    String LOCk_PROP_CREATED_AT = "createdAt";
    /**
     * Lock property:: general purpose data
     */
    String LOCK_PROP_DATA = "gpData";
    /**
     * UUID of the lock, returned when the lock is created successfully
     */
    String LOCK_PROP_TOKEN = "token";

    String tryLock(String var1, String var2, Duration var3) throws GlobalLockEntException;

    String lock(String var1, String var2, Duration var3, Duration var4) throws GlobalLockEntException;

    boolean unlock(String var1, String var2) throws GlobalLockEntException;

    Optional<Map<String, String>> verifyLock(String var1) throws GlobalLockEntException;

}
