package com.code.aon.faces.component.richfaces.lookup;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.FacesListener;

public interface LookupChangeListener extends FacesListener {

    public void processValueChange(LookupChangeEvent event)
        throws AbortProcessingException;

}
