/*
 * Created on 18-may-2004
 *
 */
package com.code.aon.jaas.deployment.event;

import java.util.EventObject;

/**
 * This class represents the generated event each time an application is deployed.
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 18-may-2004
 * @since 1.0
 *  
 */
public class DeployerEvent extends EventObject {

	private static final long serialVersionUID = 6242731446737218974L;

	/**
     * Constructor of the event with the application deployed.
     * 
     * @param source
     */
    public DeployerEvent(Object source) {
        super(source);
    }
}