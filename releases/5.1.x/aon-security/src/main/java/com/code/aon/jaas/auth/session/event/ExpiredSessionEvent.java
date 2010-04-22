/**
 * 
 */
package com.code.aon.jaas.auth.session.event;

import java.util.EventObject;

/**
 * @author Consulting & Development. Iñaki Ayerbe - 23/05/2007
 *
 */
public class ExpiredSessionEvent extends EventObject {

	public ExpiredSessionEvent(Object source) {
		super(source);
	}

}
