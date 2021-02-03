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

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import org.entando.entando.aps.system.services.storage.IStorageManager;
import org.entando.entando.ent.exception.EntException;
import org.entando.entando.ent.util.EntLogging.EntLogFactory;
import org.entando.entando.ent.util.EntLogging.EntLogger;

/**
 * Classe di default delegata al redimensionameno e salvataggio di file tipo immagine.
 * @author E.Santoboni
 */
public class DefaultImageResizer extends AbstractImageResizer {

	private static final EntLogger _logger = EntLogFactory.getSanitizedLogger(DefaultImageResizer.class);

	@Override
	@Deprecated
	public void saveResizedImage(ImageIcon imageIcon, String filePath, ImageDimension dimension) throws EntException {
		BufferedImage outImage = this.getResizedImage(imageIcon, dimension.getDimx(), dimension.getDimy());
		try {
			File file = new File(filePath);
	        ImageIO.write(outImage, this.getFileExtension(filePath), file);
		} catch (Throwable t) {
			_logger.error("Error creating resized Image", t);
			//String msg = "Error creating resigned Image";
			//ApsSystemUtils.logThrowable(t, this, "saveImageResized", msg);
			throw new EntException("Error creating resized Image", t);
		}
	}
	
	@Override
	protected BufferedImage getResizedImage(ImageIcon imageIcon, int dimensionX, int dimensionY) throws EntException {
		try {
			Image image = imageIcon.getImage();
			double scale = this.computeScale(image.getWidth(null), image.getHeight(null), dimensionX, dimensionY);
			int scaledW = (int) (scale * image.getWidth(null));
			int scaledH = (int) (scale * image.getHeight(null));
			BufferedImage outImage = new BufferedImage(scaledW, scaledH, BufferedImage.TYPE_INT_RGB);
			AffineTransform tx = new AffineTransform();
			tx.scale(scale, scale);
			Graphics2D g2d = outImage.createGraphics();
			g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2d.drawImage(image, tx, null);
			g2d.dispose();
			return outImage;
		} catch (Throwable t) {
			_logger.error("Error creating resized Image", t);
			//ApsSystemUtils.logThrowable(t, this, "getResizedImage", msg);
			throw new EntException("Error creating resized Image", t);
		}
	}
	
}
