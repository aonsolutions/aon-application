/**
 * 
 */
package com.code.aon.jaas.auth.session.event;

/**
 * @author Consulting & Development. Iñaki Ayerbe - 23/05/2007
 *
 */
public interface ExpiredSessionListener {

	void expiredSession(ExpiredSessionEvent event);
}
