/**
 * 
 */
package com.code.aon.jaas.valves;

import java.io.Serializable;

import com.code.aon.jaas.auth.AuthPrincipal;

/**
 * Serialized Principal used to communicate between aon web applications.
 *  
 * @author Consulting & Development. Iñaki Ayerbe - 09/11/2007
 *
 */
public class BackDoorPrincipal implements Serializable {

	private static final long serialVersionUID = -4601368885462012734L;

	private AuthPrincipal principal;
	private String password;
	private int requestHashcode;

	public BackDoorPrincipal(String principal, String password, int requestHashcode) {
		this.principal = new AuthPrincipal( principal );
		this.password = password;
		this.requestHashcode = requestHashcode;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @return the principal
	 */
	public AuthPrincipal getPrincipal() {
		return principal;
	}

	/**
	 * @return the requestHashcode
	 */
	public int getRequestHashcode() {
		return requestHashcode;
	}

}