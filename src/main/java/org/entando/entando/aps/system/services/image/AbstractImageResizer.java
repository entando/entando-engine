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
package org.entando.entando.aps.system.services.image;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import org.entando.entando.aps.system.services.storage.IStorageManager;
import org.entando.entando.aps.system.services.userprofilepicture.UserProfilePictureVersion;
import org.entando.entando.ent.exception.EntException;

/**
 * Classe astratta base a servizio delle classi delegate al redimensionameno e salvataggio di file tipo immagine.
 * @author E.Santoboni
 */
public abstract class AbstractImageResizer implements IImageResizer {

	@Override
	public void saveResizedImage(String subPath, boolean isProtectedResource,
			ImageIcon imageIcon, ImageDimension dimension, UserProfilePictureVersion version) throws EntException {
		BufferedImage outImage = this.getResizedImage(imageIcon, dimension.getDimx(), dimension.getDimy());
		String filename = subPath.substring(subPath.lastIndexOf("/") + 1);
		String tempFilePath = System.getProperty("java.io.tmpdir") + File.separator + "temp_" + filename;
		try {
			File tempFile = new File(tempFilePath);
	        ImageIO.write(outImage, this.getFileExtension(tempFilePath), tempFile);
			this.getStorageManager().saveFile(subPath, isProtectedResource, new FileInputStream(tempFile));
			long realLength = (int)Math.ceil(tempFile.length() / 1000.0);
			version.setSize(realLength + " Kb");
			tempFile.delete();
		} catch (Throwable t) {
			throw new EntException("Error creating resigned Image", t);
		}
	}
	
	protected abstract BufferedImage getResizedImage(ImageIcon imageIcon, int dimensionX, int dimensionY) throws EntException;
	
	/**
	 * Calcola il rapporto di scala sulla base della dimensione maggiore (tenuto conto
	 * del rapporto finale desiderato).
	 * Il fattore di scala restituito non sarà comunque superiore ad 1.
	 * @param width Dimensione attuale dell'immagine
	 * @param height Dimensione attuale dell'immagine
	 * @param finalWidth Dimensione finale dell'immagine
	 * @param finalHeight Dimensione finale dell'immagine
	 * @return Il fattore di scala da applicare all'immagine
	 */
	protected double computeScale(int width, int height, int finalWidth, int finalHeight) {
		double scale;
		if (((double) width / (double) height) >= ((double) finalWidth / (double) finalHeight)) {
			scale = (double) finalWidth / width;
		} else {
			scale = (double) finalHeight / height;
		}
		if (scale > 1) {
			scale = 1;
		}
		return scale;
	}
	
	protected String getFileExtension(String fileName) {
		return fileName.substring(fileName.lastIndexOf('.')+1).trim();
	}
	
	protected IStorageManager getStorageManager() {
		return _storageManager;
	}
	@Override
	public void setStorageManager(IStorageManager storageManager) {
		this._storageManager = storageManager;
	}
	
	private IStorageManager _storageManager;
	
}
