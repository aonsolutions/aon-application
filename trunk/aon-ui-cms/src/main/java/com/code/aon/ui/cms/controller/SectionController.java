package com.code.aon.ui.cms.controller;

import java.util.ArrayList;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.form.BasicController;
import com.icesoft.faces.component.ext.RowSelectorEvent;

public class SectionController extends BasicController{

	/** A list that contains the selected objects of the model. */
	private ArrayList<ITransferObject> checkList= new ArrayList<ITransferObject>();

	private boolean cancelOnSelect = false;

	@SuppressWarnings("unused")
	public void onSelect(RowSelectorEvent event) throws ManagerBeanException {
		if (!cancelOnSelect) {
			super.onSelect(new ActionEvent(event.getComponent()));
			FacesContext.getCurrentInstance().getApplication().getNavigationHandler().handleNavigation(FacesContext.getCurrentInstance(), null, "section_form");
		}
		cancelOnSelect = false;
	}
	
	public void onChecked(ValueChangeEvent event) throws ManagerBeanException {
		cancelOnSelect = true; 
	}
	
	/**
	 * Gets the if the selected row is checked.
	 * 
	 * @return the row checked
	 */
	public boolean getRowChecked() {
		ITransferObject to = (ITransferObject) model.getRowData();
		return checkList.contains( to );
	}
	
	/**
	 * Sets the selected row checked.
	 * 
	 * @param rowChecked the row checked
	 */
	public void setRowChecked(boolean rowChecked) {
		if ( rowChecked ) {
			ITransferObject to = (ITransferObject) model.getRowData();
			if (!checkList.contains( to )) {
				checkList.add( to );
			}
		} else {
			ITransferObject to = (ITransferObject) model.getRowData();
			if (checkList.contains( to )) {
				checkList.remove( to );
			}
		}
	}
	
	/**
	 * Gets the check list.
	 * 
	 * @return the check list
	 */
	protected ArrayList<ITransferObject> getCheckList() {
		return checkList;
	}

	/**
	 * Removes all the selected objects.
	 * 
	 * @param event the event
	 */
	public void onRemoveSelected(ActionEvent event){
		try{
			for (ITransferObject to: checkList) {
				getManagerBean().remove(to);
			}
			onSearch( event );
		} catch (ManagerBeanException e) {
			addMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}
	
	public boolean isChecklistEmpty() {
		return (this.checkList.size() <= 0);
	}

	@Override
	public void initializeModel() {
		checkList= new ArrayList<ITransferObject>();
		super.initializeModel();
	}
}
