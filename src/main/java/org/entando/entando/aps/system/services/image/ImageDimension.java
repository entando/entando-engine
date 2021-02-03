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

import java.io.Serializable;

/**
 * Classe rappresentante una dimensione di resize di una risorsa immagine.
 * @author E.Santoboni
 */
public class ImageDimension implements Serializable {

    private int idDim;
    private int dimx;
    private int dimy;

    public ImageDimension(int idDim, int dimx, int dimy) {
        this.idDim = idDim;
        this.dimx = dimx;
        this.dimy = dimy;
    }

    /**
     * Setta l'identificativo del resize.
     * @return Returns L'identificativo del resize.
     */
    public int getIdDim() {
    	return idDim;
    }

    /**
     * Restituisce l'identificativo del resize.
     * @param idDim L'identificativo del resize.
     */
    public void setIdDim(int idDim) {
    	this.idDim = idDim;
    }

    /**
     * Restituisce la dimensione lungo l'asse x in px.
     * @return La dimensione lungo l'asse x in px.
     */
    public int getDimx() {
        return dimx;
    }

    /**
     * Setta la dimensione lungo l'asse x in px.
     * @param dimx La dimensione lungo l'asse x in px.
     */
    public void setDimx(int dimx) {
        this.dimx = dimx;
    }

    /**
     * Restituisce la dimensione lungo l'asse y in px.
     * @return La dimensione lungo l'asse y in px.
     */
    public int getDimy() {
        return dimy;
    }

    /**
     * Setta la dimensione lungo l'asse y in px.
     * @param dimy La dimensione lungo l'asse y in px.
     */
    public void setDimy(int dimy) {
        this.dimy = dimy;
    }

}