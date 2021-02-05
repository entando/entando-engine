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
package org.entando.entando.aps.system.services.imageresize;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.PixelGrabber;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import org.entando.entando.ent.exception.EntException;
import org.entando.entando.ent.util.EntLogging.EntLogFactory;
import org.entando.entando.ent.util.EntLogging.EntLogger;

/**
 * Classe delegata al redimensionameno e salvataggio di file tipo immagine di tipo PNG.
 * @author E.Santoboni
 */
public class PNGImageResizer extends AbstractImageResizer {

	private static final EntLogger _logger = EntLogFactory.getSanitizedLogger(PNGImageResizer.class);

	@Override
	@Deprecated
	public void saveResizedImage(ImageIcon imageIcon, String filePath, ImageResizeDimension dimension) throws EntException {
		BufferedImage imageResized = this.getResizedImage(imageIcon, dimension.getDimx(), dimension.getDimy());
		try {
			File file = new File(filePath);
	        ImageIO.write(imageResized, this.getFileExtension(filePath), file);
		} catch (Exception e) {
			throw new EntException("Error in saveResizedImage: " + filePath, e);
		}
	}
	
	/**
	 * Crea e restituisce un'immagine in base all'immagine master ed alle dimensioni massime consentite.
	 * L'immagine risultante sarà un'immagine rispettante le proporzioni dell'immagine sorgente.
	 * @param imageIcon L'immagine sorgente.
	 * @param dimensioneX la dimensione orizzontale massima.
	 * @param dimensioneY La dimensione verticale massima.
	 * @return L'immagine risultante.
	 * @throws EntException In caso di errore.
	 */
	@Override
	protected BufferedImage getResizedImage(ImageIcon imageIcon, int dimensioneX, int dimensioneY) throws EntException {
    	Image image = imageIcon.getImage();
    	BufferedImage bi = this.toBufferedImage(imageIcon, dimensioneX, dimensioneY);
    	double scale = this.computeScale(image.getWidth(null), image.getHeight(null), dimensioneX, dimensioneY);
		int scaledW = (int) (scale * image.getWidth(null));
		int scaledH = (int) (scale * image.getHeight(null));
		BufferedImage biRes = new BufferedImage(bi.getColorModel(), 
        		bi.getColorModel().createCompatibleWritableRaster(scaledW, scaledH), 
        		bi.isAlphaPremultiplied() , null);
        AffineTransform tx = new AffineTransform();
		tx.scale(scale, scale);
        Graphics2D bufImageGraphics = biRes.createGraphics();
        bufImageGraphics.drawImage(image, tx, null);
        return biRes;
	}
	
	protected BufferedImage toBufferedImage(ImageIcon imageIcon, int dimensioneX, int dimensioneY) throws EntException {
		Image image = imageIcon.getImage();
		if (image instanceof BufferedImage) {
			return (BufferedImage) image;
		}
		// This code ensures that all the pixels in the image are loaded
		image = new ImageIcon(image).getImage();
		// Determine if the image has transparent pixels.
		boolean hasAlpha = this.hasAlpha(image);
		// Create a buffered image with a format that's compatible with the
		// screen
		try {
			// Determine the type of transparency of the new buffered image
			int transparency = Transparency.OPAQUE;
			if (hasAlpha) {
				transparency = Transparency.BITMASK;
			}
			// Create the buffered image
            BufferedImage bimage = getGraphicsConfiguration().createCompatibleImage(image.getWidth(null),
                    image.getHeight(null), transparency);
			Graphics graphics = bimage.createGraphics();
			graphics.drawImage(image, 0, 0, null);
			graphics.dispose();

			return bimage;
		} catch (HeadlessException e) {
			_logger.warn("The system does not have a screen. Trying best effort approach.");
            return toBufferedImageWhenScreenIsNotPresent(imageIcon, dimensioneX, dimensioneY, image, hasAlpha);
        }
	}

    protected BufferedImage toBufferedImageWhenScreenIsNotPresent(ImageIcon imageIcon, float dimensioneX,
            float dimensioneY, Image image, boolean hasAlpha) {

        int type = BufferedImage.TYPE_INT_RGB;
        if (hasAlpha) {
            type = BufferedImage.TYPE_INT_ARGB;
        }

        BufferedImage source = new BufferedImage(
                imageIcon.getIconWidth(),
                imageIcon.getIconHeight(),
                type);

        Graphics graphics = source.createGraphics();
        imageIcon.paintIcon(null, graphics, 0, 0);
        graphics.dispose();

        BufferedImage bufferedImage = new BufferedImage(image.getWidth(null), image.getHeight(null), source.getType());
        Graphics2D graphics2D = bufferedImage.createGraphics();
        graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        float sx = dimensioneX / source.getWidth();
        float sy = dimensioneY / source.getHeight();
        graphics2D.scale(sx, sy);
        graphics2D.drawImage(source, 0, 0, null);
        graphics2D.dispose();

        return bufferedImage;
    }

    private GraphicsConfiguration getGraphicsConfiguration() {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gs = ge.getDefaultScreenDevice();
        return gs.getDefaultConfiguration();
    }

    protected boolean hasAlpha(Image image) throws EntException {
        // If buffered image, the color model is readily available
        if (image instanceof BufferedImage) {
            BufferedImage bimage = (BufferedImage)image;
            return bimage.getColorModel().hasAlpha();
        }
        // Use a pixel grabber to retrieve the image's color model. Grabbing a single pixel is usually sufficient
        PixelGrabber pg = new PixelGrabber(image, 0, 0, 1, 1, false);
        try {
            pg.grabPixels();
        } catch (Exception e) {
        	throw new EntException("Error grabbing a single pixel", e);
        }
        // Get the image's color model
        ColorModel cm = pg.getColorModel();
        return cm.hasAlpha();
	}
	
}
