package com.code.aon.ui.config.event;

import com.code.aon.common.IHeaderObject;
import com.code.aon.config.util.SeriesNumberUtil;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

/**
 * Listener added to the controller of any entity with series and number
 * 
 */
public class HeaderControllerListener extends ControllerAdapter {
	
	/** The table. */
	private String table;

	/**
	 * Gets the table.
	 * 
	 * @return the table
	 */
	public String getTable() {
		return table;
	}

	/**
	 * Sets the table.
	 * 
	 * @param table the table
	 */
	public void setTable(String table) {
		this.table = table;
	}

	/**
	 * Obtains the series and the next number to use
	 * 
	 * @param event the event
	 * 
	 * @throws ControllerListenerException the controller listener exception
	 */
	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		IHeaderObject header = (IHeaderObject)event.getController().getTo();
        if(header.getNumber() == 0) {
        	header.setNumber(SeriesNumberUtil.obtainNumber(header.getSeries(), this.table));
		}
    }

}
