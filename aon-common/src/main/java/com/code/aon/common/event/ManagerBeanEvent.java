package com.code.aon.common.event;

import java.util.EventObject;

import com.code.aon.common.AonVersion;
import com.code.aon.common.ITransferObject;

/**
 * A "ManagerBean" event gets delivered whenever an operation is performed  
 * over a bean. A ManagerBeanEvent object is sent as an
 * argument to the IManagerBeanListener and IManagerBeanVetoListener methods.
 * 
 * @author 	Consulting & Development.
 *
 */

public class ManagerBeanEvent extends EventObject {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	/**
     * Construct a new <code>ManagerBeanEvent</code>.
	 * 
	 * @param source
	 */
	public ManagerBeanEvent(Object source) {
		super(source);
	}

	/**
	 * Return the <code>ITransferObject</code>
	 * 
	 * @return The <code>ITransferObject</code>
	 */
	public ITransferObject getTo() {
		return (ITransferObject) super.getSource();
	}

}
