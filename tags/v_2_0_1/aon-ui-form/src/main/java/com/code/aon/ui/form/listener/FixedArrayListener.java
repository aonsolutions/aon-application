package com.code.aon.ui.form.listener;

import java.util.List;

import javax.faces.application.Application;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

import org.apache.commons.beanutils.BeanUtils;

import com.code.aon.common.ITransferObject;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

/**
 * FixedArrayListener listener for the ArrayControllers.
 */
public class FixedArrayListener extends ControllerAdapter {

	/** The array controller name. */
	private String arrayControllerName;	
	
	/** The fixed property name. */
	private String fixedPropertyName;	

	/** The rows. */
	private List<String> rows;
	
	/**
	 * Sets the array controller name.
	 * 
	 * @param arrayControllerName the array controller name
	 */
	public void setArrayControllerName(String arrayControllerName) {
		this.arrayControllerName = arrayControllerName;
	}

	/**
	 * Sets the fixed property name.
	 * 
	 * @param fixedPropertyName the fixed property name
	 */
	public void setFixedPropertyName(String fixedPropertyName) {
		this.fixedPropertyName = fixedPropertyName;
	}

	/**
	 * Sets the rows.
	 * 
	 * @param rows the rows
	 */
	public void setRows(List<String> rows) {
		this.rows = rows;
	}
	
	/**
	 * Gets the array controller.
	 * 
	 * @return the array controller
	 */
	private IController getArrayController() {
		FacesContext ctx = FacesContext.getCurrentInstance();
		Application app = ctx.getApplication();
		ValueBinding vb = app.createValueBinding("#{" + this.arrayControllerName + "}");
		return (IController) vb.getValue(ctx);
	}

	
	/* (non-Javadoc)
	 * @see com.code.aon.ui.form.event.ControllerAdapter#afterBeanCreated(com.code.aon.ui.form.event.ControllerEvent)
	 */
	@Override	
	public void afterBeanCreated(ControllerEvent event)
			throws ControllerListenerException {
		IController arrayController = getArrayController();
		for( String row : rows ) {
			arrayController.onReset(null);
			ITransferObject to = arrayController.getTo();
			try {
				if ( row != null ) {
					BeanUtils.setProperty( to, this.fixedPropertyName, row );
				}
			} catch (Throwable e) {
				throw new ControllerListenerException(e.getMessage(), e);
			}
			arrayController.onAccept(null);
		}
	}

}
