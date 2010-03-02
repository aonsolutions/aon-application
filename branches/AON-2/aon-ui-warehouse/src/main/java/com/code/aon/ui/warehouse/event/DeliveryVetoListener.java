package com.code.aon.ui.warehouse.event;

import javax.faces.event.AbortProcessingException;

import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.warehouse.Delivery;
import com.code.aon.warehouse.enumeration.DeliveryStatus;

/**
 * A Delivery listener to avoid deleting closed Deliveies 
 * 
 * @author Consulting & Development.
 * @since 1.0
 *
 */
public class DeliveryVetoListener extends ControllerAdapter {
	
	/**
	 * If DeliveryStatus is Closed avoid deleting it
	 * 
	 * @see com.code.aon.ui.form.event.ControllerAdapter#beforeBeanRemoved(com.code.aon.ui.form.event.ControllerEvent)
	 */
	@Override
	public void beforeBeanRemoved(ControllerEvent event) throws ControllerListenerException {
		if( ((Delivery)event.getController().getTo()).getStatus().equals(DeliveryStatus.CLOSED) ){
			String args = "Delivery status: Closed";
			AonUtil.addFatalMessage(args);
			throw new AbortProcessingException(args);
		}
	}

}
