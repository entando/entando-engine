/*
 * Copyright 2025-Present Entando S.r.l. (http://www.entando.com) All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */
package org.entando.entando.aps.system.services.cache;

import java.util.function.Consumer;
import org.entando.entando.aps.system.services.IFeatureFlag;

/**
 * Activable Feature: Enable the use of cache pipelining in the code that makes use of it
 */
public interface IFCacheWithPipeline extends IFeatureFlag {
    boolean CACHE_PIPELINE_ENABLED = IFeatureFlag.readEnablementStatus("CACHE_PIPELINE");

    void openPipeline();

    void closePipeline();

    /**
     * @param cache
     */

    static void openPipeline(Object cache) {
        if (CACHE_PIPELINE_ENABLED) {
            if (cache instanceof IFCacheWithPipeline) {
                ((IFCacheWithPipeline) cache).openPipeline();
            }
        }
    }

    static void closePipeline(Object cache) {
        if (CACHE_PIPELINE_ENABLED) {
            if (cache instanceof IFCacheWithPipeline) {
                ((IFCacheWithPipeline) cache).closePipeline();
            }
        }
    }

    static void pipelined(Object cache, Consumer<IFCacheWithPipeline> pipelinedBlock) {
        if (CACHE_PIPELINE_ENABLED) {
            if (cache instanceof IFCacheWithPipeline) {
                openPipeline(cache);
                pipelinedBlock.accept((IFCacheWithPipeline) cache);
                closePipeline(cache);
                return;
            }
        }
        pipelinedBlock.accept(null);
    }

    default boolean isEnabled() {
        return CACHE_PIPELINE_ENABLED;
    }
}
