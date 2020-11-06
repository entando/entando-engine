package org.entando.entando.aps.system.services;

import org.entando.entando.ent.exception.EntException;

public interface IComponentExistsService {
    boolean exists(String code) throws EntException;
}
