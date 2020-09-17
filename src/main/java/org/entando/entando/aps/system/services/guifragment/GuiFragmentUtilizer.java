/*
 * Copyright 2015-Present Entando Inc. (http://www.entando.com) All rights reserved.
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
package org.entando.entando.aps.system.services.guifragment;

import java.util.List;

import org.entando.entando.ent.exception.EntException;

/**
 * Basic interface for those services whose handled elements are based on fragment.
 * @author E.Santoboni
 */
public interface GuiFragmentUtilizer {
	
	/**
	 * Return the id of the utilizing service.
	 * @return The id of the utilizer.
	 */
	public String getName();
	
	/**
	 * Return the list of the objects that use the fragment with the given name.
	 * @param guiFragmentCode The name of the fragment
	 * @return The list of the objects that use the fragment with the given code.
	 * @throws EntException In case of error
	 */
	public List getGuiFragmentUtilizers(String guiFragmentCode) throws EntException;
	
}
