package com.code.aon.faces.component.richfaces.lookup;

import javax.faces.component.UIComponent;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.FacesEvent;
import javax.faces.event.FacesListener;
import javax.faces.event.PhaseId;

public class LookupChangeEvent extends FacesEvent {

    // ------------------------------------------------------------ Constructors


    /**
     * <p>Construct a new event object from the specified source component,
     * old value, and new value.</p>
     *
     * <p>The default {@link PhaseId} for this event is {@link
     * PhaseId#ANY_PHASE}.</p>
     *
     * @param component Source {@link UIComponent} for this event
     * @param oldValue The previous local value of this {@link UIComponent}
     * @param newValue The new local value of thie {@link UIComponent}
     *
     * @throws IllegalArgumentException if <code>component</code> is
     *  <code>null</code>
     */
    public LookupChangeEvent(UIComponent component, Object newValue) {

        super(component);
        this.newValue = newValue;
    }

    // -------------------------------------------------------------- Properties


    /**
     * <p>The current local value of the source {@link UIComponent}.</p>
     */
    private Object newValue = null;


    /**
     * <p>Return the current local value of the source {@link UIComponent}.
     * </p>
     */
    public Object getNewValue() {
        return (this.newValue);
    }


    // ------------------------------------------------- Event Broadcast Methods

    public boolean isAppropriateListener(FacesListener listener) {
        return (listener instanceof LookupChangeListener);
    }

    /**
     * @throws AbortProcessingException {@inheritDoc}
     */ 
    public void processListener(FacesListener listener) {
        ((LookupChangeListener) listener).processValueChange(this);
    }

}
