package org.entando.entando.aps.system.services.pagemodel;

import java.util.List;

public interface PageModelServiceUtilizer<T> {

    String getManagerName();

    List<T> getPageModelUtilizer(String pageModelCode);
}
