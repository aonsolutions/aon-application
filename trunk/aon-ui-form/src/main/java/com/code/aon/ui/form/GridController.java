package com.code.aon.ui.form;

import java.util.ArrayList;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;

/**
 * Controller for grid maintenances.
 */
public class GridController extends BasicController {

	/** A list that contains the selected objects of the model. */
	private ArrayList<ITransferObject> checkList= new ArrayList<ITransferObject>();
	
	/**
	 * The empty Constructor.
	 */
	public GridController( ) {
		super.setNew( false );
	}


	/* (non-Javadoc)
	 * @see com.code.aon.ui.form.BasicController#onAccept(javax.faces.event.ActionEvent)
	 */
	@Override
	public void onAccept(ActionEvent event) {
		super.accept(event);
		super.onReset(event);
	}
	
	
	/* (non-Javadoc)
	 * @see com.code.aon.ui.form.BasicController#onRemove(javax.faces.event.ActionEvent)
	 */
	@Override
	public void onRemove(ActionEvent event) {
		super.remove(event);
		super.onReset(event);
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

	/**
	 * Gets the check list empty status.
	 * 
	 * @return the check list is empty or not
	 */
	public boolean isChecklistEmpty() {
		return (this.checkList.size() <= 0);
	}

	/**
	 * Empty the check list
	 * 
	 */
	public void clearCheckList() {
		checkList= new ArrayList<ITransferObject>();
	}
}
