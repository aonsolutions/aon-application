package com.code.aon.ui.common.components;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.FacesListener;

/**
 * @author ecastellano
 *
 */
public interface LookupChangeListener extends FacesListener {

    /**
     * @param event
     * @throws AbortProcessingException
     */
    public void processValueChange(LookupChangeEvent event)
        throws AbortProcessingException;

}
