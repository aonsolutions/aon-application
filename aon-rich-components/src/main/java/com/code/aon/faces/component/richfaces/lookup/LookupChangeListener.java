package com.code.aon.faces.component.richfaces.lookup;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.FacesListener;
import javax.faces.event.ValueChangeEvent;

public interface LookupChangeListener extends FacesListener {

    public void processValueChange(LookupChangeEvent event)
        throws AbortProcessingException;

}
