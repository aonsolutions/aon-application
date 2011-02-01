/**
 * 
 */
package com.code.aon.jaas.auth.session;

/**
 * Thrown if an attempt is made to create new session information for an existing
 * session.getId(). The user should firstly clear the existing session.
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 16/05/2007
 *
 */
public class SessionAlreadyUsedLoginException extends AuthenticationLoginException {

    public SessionAlreadyUsedLoginException(String msg, Object arg) {
        super(msg, arg);
    }
}
