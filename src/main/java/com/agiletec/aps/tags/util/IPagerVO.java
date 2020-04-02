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

package com.agiletec.aps.tags.util;

/**
 * Interfaccia di utilità per il tag paginatore. Rappresenta l'oggetto che permette di estrarre i parametri necessari per la corretta
 * visualizzazione dell'iter corrente (l'insieme di oggetti "sottoinsieme della lista principale" che deve essere visualizzato nella pagina
 * corrente).
 *
 * @author E.Santoboni
 */
public interface IPagerVO {

    /**
     * Array di utilità; restituisce l'array ordinato degli indici numerici degli item.
     *
     * @return L'array ordinato degli indici numerici degli item.
     */
    int[] getItems();

    /**
     * Costruisce e restituisce il nome del parametro tramite il quale individuare dalla request l'identificativo del item richiesto. Il
     * metodo viene richiamato all'interno della jsp che genera il paginatore.
     *
     * @return Il nome del parametro tramite il quale individuare dalla request l'identificativo del item richiesto.
     */
    String getParamItemName();

    /**
     * Restituisce il numero massimo di elementi della lista per ogni item.
     *
     * @return Il numero massimo di elementi della lista per ogni item.
     */
    int getMax();

    /**
     * Restituisce l'identificativo numerico del gruppo item precedente.
     *
     * @return L'identificativo numerico del gruppo item precedente.
     */
    int getPrevItem();

    /**
     * Restituisce il size della lista principale.
     *
     * @return Il size della lista principale.
     */
    int getSize();

    /**
     * Restituisce l'identificativo numerico del gruppo item successivo.
     *
     * @return L'identificativo numerico del gruppo item successivo.
     */
    int getNextItem();

    /**
     * Restituisce l'identificativo numerico del gruppo item corrente.
     *
     * @return L'identificativo numerico del gruppo item corrente.
     */
    int getCurrItem();

    /**
     * Restituisce l'indice di partenza sulla lista principale dell'item corrente.
     *
     * @return L'indice di partenza sulla lista principale dell'item corrente.
     */
    int getBegin();

    /**
     * Restituisce l'indice di arrivo sulla lista principale dell'item corrente.
     *
     * @return L'indice di arrivo sulla lista principale dell'item corrente.
     */
    int getEnd();

    /**
     * Restituisce l'identificativo numerico dell'ultimo gruppo iter.
     *
     * @return L'identificativo numerico dell'ultimo gruppo item.
     */
    int getMaxItem();

    /**
     * Setta l'identificativo del paginatore.
     *
     * @return L'identificativo del paginatore.
     */
    String getPagerId();

    int getBeginItemAnchor();

    int getEndItemAnchor();

    boolean isAdvanced();

    int getOffset();

}
