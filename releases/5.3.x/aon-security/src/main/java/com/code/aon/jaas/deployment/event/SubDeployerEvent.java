package com.code.aon.jaas.deployment.event;

import java.util.EventObject;

/**
 * A "SubDeployer" event gets delivered whenever a deployer finds a "bound" security widget.
 * A SubDeployerEvent object is sent as an argument to the ISubDeployerListener methods.
 * <P>
 * Normally SubDeployerEvents are accompanied by the permission object.
 * <P>
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 18-may-2004
 * @since 1.0
 *  
 */
public class SubDeployerEvent extends EventObject {

	/**
     * // TODO [iayerbe] Documéntame!
     */
    Object permission;

    /**
     * Constructs a new <code>SubDeployerEvent</code>.
     * 
     * @param source
     */
    public SubDeployerEvent(Object source) {
        super(source);
    }

    /**
     * Constructs a new <code>SubDeployerEvent</code>.
     * 
     * @param source
     * @param permission Object
     */
    public SubDeployerEvent(Object source, Object permission) {
        super(source);
        this.permission = permission;
    }

    /**
     * // TODO [iayerbe] Documéntame!
     * 
     * @return Object
     */
    public Object getPermission() {
        return permission;
    }

}