package com.code.aon.ui.marketplace.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import com.code.aon.common.ICollectionProvider;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.marketplace.tickets.Trace;
import com.code.aon.ui.menu.jsf.MenuEvent;

/**
 * Controller used in the Trace maintenance.
 */
public class TraceController implements ICollectionProvider {

	/** The current trace. */
	private Trace to;

	/** Determines if the current trace is saved or not. */
	private boolean saved;

	/**
	 * Gets the current trace.
	 * 
	 * @return the to
	 */
	public Trace getTo() {
		return to;
	}

	/**
	 * Sets the current trace.
	 * 
	 * @param to the to
	 */
	public void setTo(Trace to) {
		this.to = to;
	}

	/**
	 * Adds the crotal info to the current trace.
	 * 
	 * @param event the event
	 * 
	 * @throws ManagerBeanException the manager bean exception
	 */
	public void addCrotal(ValueChangeEvent event) throws ManagerBeanException {
		this.to.setCrotal(event.getNewValue().toString());
	}

	/**
	 * Adds the born info to the current trace.
	 * 
	 * @param event the event
	 * 
	 * @throws ManagerBeanException the manager bean exception
	 */
	public void addBorn(ValueChangeEvent event) throws ManagerBeanException {
		this.to.setBorn(event.getNewValue().toString());
	}

	/**
	 * Adds the raised info to the current trace.
	 * 
	 * @param event the event
	 * 
	 * @throws ManagerBeanException the manager bean exception
	 */
	public void addRaised(ValueChangeEvent event) throws ManagerBeanException {
		this.to.setRaised(event.getNewValue().toString());
	}

	/**
	 * Adds the sacrificed info to the current trace.
	 * 
	 * @param event the event
	 * 
	 * @throws ManagerBeanException the manager bean exception
	 */
	public void addSacrificed(ValueChangeEvent event)
			throws ManagerBeanException {
		this.to.setSacrificed(event.getNewValue().toString());
	}

	/**
	 * Adds the quartered info to the current trace.
	 * 
	 * @param event the event
	 * 
	 * @throws ManagerBeanException the manager bean exception
	 */
	public void addQuartered(ValueChangeEvent event)
			throws ManagerBeanException {
		this.to.setQuartered(event.getNewValue().toString());
	}
	
	/**
	 * Adds the start date info to the current trace.
	 * 
	 * @param event the event
	 */
	public void addStartDate(ValueChangeEvent event){
		this.to.setStartDate((Date)event.getNewValue());
	}
	
	/**
	 * Adds the end date info to the current trace.
	 * 
	 * @param event the event
	 */
	public void addEndDate(ValueChangeEvent event){
		this.to.setEndDate((Date)event.getNewValue());
	}

	/**
	 * Adds the name info to the current trace.
	 * 
	 * @param event the event
	 * 
	 * @throws ManagerBeanException the manager bean exception
	 */
	public void addName(ValueChangeEvent event)
			throws ManagerBeanException {
		this.to.setName(event.getNewValue().toString());
	}
	
	/**
	 * Adds the expiry date info to the current trace.
	 * 
	 * @param event the event
	 */
	public void addExpiry(ValueChangeEvent event){
		this.to.setExpiry((Date)event.getNewValue());
	}
	

	/**
	 * Initializes the controller.
	 * 
	 * @param event the event
	 */
	@SuppressWarnings("unused")
	public void onReset(MenuEvent event) {
		this.to = new Trace();
		this.saved = false;
	}

	/**
	 * Sets <code>saved</code> flag true.
	 * 
	 * @param event the event
	 */
	@SuppressWarnings("unused")
	public void onSave(ActionEvent event) {
		this.saved = true;
	}

	/**
	 * Checks if is saved or not.
	 * 
	 * @return true, if is saved
	 */
	public boolean isSaved(){
		return this.saved;
	}
	
	/**
	 * Gets the collection.
	 * 
	 * @return the collection
	 */
	public Collection<Trace> getCollection() {
		Collection<Trace> traces = new ArrayList<Trace>();
		traces.add(to);
		return traces;
	}

	@SuppressWarnings("unchecked")
	public Collection getCollection(boolean arg0) throws ManagerBeanException {
		return getCollection();
	}
}