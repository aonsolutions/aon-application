package com.code.aon.jaas.deployment.core;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.code.aon.jaas.deployment.ast.IApplicationDescriptor;
import com.code.aon.jaas.deployment.ast.IMethod;
import com.code.aon.jaas.deployment.ast.INodeVisitor;
import com.code.aon.jaas.deployment.ast.IPermission;
import com.code.aon.jaas.deployment.ast.IResource;
import com.code.aon.jaas.deployment.ast.ISecurityDescriptor;
import com.code.aon.jaas.deployment.ast.ISecurityRole;
import com.code.aon.jaas.deployment.event.ISubDeployerListener;
import com.code.aon.jaas.deployment.event.SubDeployerEvent;
import com.code.aon.jaas.vendor.deployment.ast.IVendorDescriptor;

/**
 * // TODO [iayerbe] Documéntame!
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 18-may-2004
 * @since 1.0
 *  
 */
public class SubDeployerVisitor implements INodeVisitor {

    // Manages the listener list.
    private List<ISubDeployerListener> listeners = new LinkedList<ISubDeployerListener>();

    // Manages the active role.
    private String activeRole;

    /**
     * Add a ISubDeployerListener to the listener list.
     * The listener is registered for all properties.
     * The same listener object is only added one time.
     * If <code>l</code> is null, no exception is thrown and no action is taken.
     *
     * @param l  The ISubDeployerListener to be added
     */
    public void addSubDeployerListener(ISubDeployerListener l) {
    	if (l == null) {
    	    return;
    	}
        if (!this.listeners.contains(l)) {
        	this.listeners.add(l);
        }
    }

    /**
     * Remove a ISubDeployerListener from the listener list.
     * This removes a ISubDeployerListener that was registered for all properties.
     * If <code>l</code> is null, or was never added, no exception is
     * thrown and no action is taken.
     *
     * @param l The ISubDeployerListener to be removed
     */
    public void removeSubDeployerListener(ISubDeployerListener l) {
    	if (l == null) {
    	    return;
    	}
    	this.listeners.remove(l);
    }

    /**
	 * @return the activeRole
	 */
	public String getActiveRole() {
		return activeRole;
	}

	/*
     * (non-Javadoc)
     * 
     * @see com.code.aon.jaas.deployment.ast.INodeVisitor#visitApplicationDescriptor(com.code.aon.jaas.deployment.ast.IApplicationDescriptor)
     */
    public void visitApplicationDescriptor(IApplicationDescriptor descriptor) {
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.code.aon.jaas.deployment.ast.INodeVisitor#visitAssemblyDescriptor(com.code.aon.jaas.deployment.ast.ISecurityDescriptor)
     */
    public void visitAssemblyDescriptor(ISecurityDescriptor descriptor) {
        visitDescriptor(descriptor);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.code.aon.jaas.deployment.ast.INodeVisitor#visitWebDescriptor(com.code.aon.jaas.deployment.ast.ISecurityDescriptor)
     */
    public void visitWebDescriptor(ISecurityDescriptor descriptor) {
        visitDescriptor(descriptor);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.code.aon.jaas.deployment.ast.INodeVisitor#visitSecurityRole(com.code.aon.jaas.deployment.ast.ISecurityRole)
     */
    public void visitSecurityRole(ISecurityRole role) {
        activeRole = role.getName();
        fireRoleAdded(new SubDeployerEvent(this));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.code.aon.jaas.deployment.ast.INodeVisitor#visitMethodPermission(com.code.aon.jaas.deployment.ast.IPermission)
     */
    public void visitMethodPermission(IPermission permission) {
        Iterator<String> iter = permission.roles().iterator();
        while (iter.hasNext()) {
            activeRole = iter.next();
            Iterator iterator = permission.resources().iterator();
            while (iterator.hasNext()) {
                ((IMethod) iterator.next()).accept(this);

            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.code.aon.jaas.deployment.ast.INodeVisitor#visitSecurityConstraint(com.code.aon.jaas.deployment.ast.IPermission)
     */
    public void visitSecurityConstraint(IPermission permission) {
        Iterator<String> iter = permission.roles().iterator();
        while (iter.hasNext()) {
            activeRole = iter.next();
            Iterator<IResource> iterator = permission.resources().iterator();
            while (iterator.hasNext()) {
                iterator.next().accept(this);
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.code.aon.jaas.deployment.ast.INodeVisitor#visitMethod(com.code.aon.jaas.deployment.ast.IMethod)
     */
    public void visitMethod(IMethod method) {
        fireMethodPermissionAdded(new SubDeployerEvent(this, method));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.code.aon.jaas.deployment.ast.INodeVisitor#visitResource(com.code.aon.jaas.deployment.ast.IResource)
     */
    public void visitResource(IResource resource) {
        fireSecurityPermissionAdded(new SubDeployerEvent(this, resource));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.code.aon.jaas.deployment.ast.INodeVisitor#visitVendorDescriptor(com.code.aon.jaas.vendor.deployment.ast.IVendorDescriptor)
     */
    public void visitVendorDescriptor(IVendorDescriptor vendor) {
        fireVendorDescriptorFound(new SubDeployerEvent(vendor));
    }

    /**
     * Fire an existing SubDeployerEvent to any registered listeners.
     * 
     * @param event
     */
    protected void fireRoleAdded(SubDeployerEvent event) {
        Iterator<ISubDeployerListener> iter = this.listeners.iterator();
        while (iter.hasNext()) {
            ISubDeployerListener element = iter.next();
            element.roleFound(event);
        }
    }

    /**
     * Fire an existing SubDeployerEvent to any registered listeners.
     * 
     * @param event
     */
    protected void fireMethodPermissionAdded(SubDeployerEvent event) {
        Iterator<ISubDeployerListener> iter = this.listeners.iterator();
        while (iter.hasNext()) {
            ISubDeployerListener element = iter.next();
            element.methodPermissionFound(event);
        }
    }

    /**
     * Fire an existing SubDeployerEvent to any registered listeners.
     * 
     * @param event
     */
    protected void fireSecurityPermissionAdded(SubDeployerEvent event) {
        Iterator<ISubDeployerListener> iter = this.listeners.iterator();
        while (iter.hasNext()) {
            ISubDeployerListener element = iter.next();
            element.securityPermissionFound(event);
        }
    }

    /**
     * Fire an existing SubDeployerEvent to any registered listeners.
     * 
     * @param event
     */
    protected void fireVendorDescriptorFound(SubDeployerEvent event) {
        Iterator<ISubDeployerListener> iter = this.listeners.iterator();
        while (iter.hasNext()) {
            ISubDeployerListener element = iter.next();
            element.vendorDescriptorFound(event);
        }
    }

    /**
     * Fire an existing SubDeployerEvent to any registered listeners.
     * 
     * @param event The SubDeployerEvent object.
     */
    protected void fireDomainFound(SubDeployerEvent event) {
        Iterator<ISubDeployerListener> iter = this.listeners.iterator();
        while (iter.hasNext()) {
            ISubDeployerListener element = iter.next();
            element.domainFound(event);
        }
    }

    /**
     * // TODO [iayerbe] Documéntame!
     * 
     * @param descriptor
     */
    private void visitDescriptor(ISecurityDescriptor descriptor) {
        Iterator<ISecurityRole> srIter = descriptor.roles().iterator();
        while (srIter.hasNext()) {
        	srIter.next().accept(this);
        }
        Iterator<IPermission> permIter = descriptor.permissions().iterator();
        while (permIter.hasNext()) {
        	permIter.next().accept(this);
        }
    }

}