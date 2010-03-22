/**
 * 
 */
package com.code.aon.jaas.auth.session;

/**
 * Thrown if <code>IAccessPolicy</code> is not found.
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 16/05/2007
 *
 */
public class AccessPolicyNotFoundException extends AuthenticationLoginException {

    public AccessPolicyNotFoundException(String msg, Object arg) {
        super(msg, arg);
    }
}
