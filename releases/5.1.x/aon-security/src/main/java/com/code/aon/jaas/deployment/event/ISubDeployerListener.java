package com.code.aon.jaas.deployment.event;

/**
 * A "SubDeployer" event gets fired whenever a deployer finds a "bound" security widget.
 * You can register a ISubDeployerListener with a source bean.
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 18-may-2004
 * @since 1.0
 *  
 */
public interface ISubDeployerListener {

    /**
     * This method gets called when a bound role is changed.
     * 
     * @param event A SubDeployerEvent object describing the event source.
     */
    void roleFound(SubDeployerEvent event);

    /**
     * This method gets called when a bound method permission is changed.
     * 
     * @param event A SubDeployerEvent object describing the event source and the permission that has found.
     */
    void methodPermissionFound(SubDeployerEvent event);

    /**
     * This method gets called when a bound security permission is changed.
     * 
     * @param event A SubDeployerEvent object describing the event source and the permission that has found.
     */
    void securityPermissionFound(SubDeployerEvent event);

    /**
     * This method gets called when a bound vendor descriptor is changed.
     * 
     * @param event A SubDeployerEvent object describing the event source.
     */
    void vendorDescriptorFound(SubDeployerEvent event);

    /**
     * This method gets called when a bound domain is changed.
     * 
     * @param event A SubDeployerEvent object describing the event source and the domain that has found.
     */
    void domainFound(SubDeployerEvent event);
}