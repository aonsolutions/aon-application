package com.code.aon.ui.menu.core;

import com.code.aon.ui.menu.IMenuVisitor;
import com.code.aon.ui.menu.IRoot;
import com.code.aon.ui.menu.MenuVisitorException;

/**
 * Default implementation of {@link com.code.aon.ui.menu.IRoot}.
 * 
 * @author Consulting & Development. Eugenio Castellano - 07-mar-2005
 * @since 1.0
 */
public class Root extends Menu implements IRoot {

	/** The roles defined. */
	private boolean rolesDefined;
	
    public boolean isRolesDefined() {
    	return this.rolesDefined;
    }

    public void setRolesDefined( boolean roles ) {
    	this.rolesDefined = roles;
    }

    /**
     * Checks if <code>role</code> is defined.
     * 
     * @param role the role to be checked.
     * 
     * @return <code>true</code> if is allowed.
     */
    public boolean isAllowed( String role ) {
    	return getRoles().contains(role);
    }
    
    public void visit(IMenuVisitor visitor) throws MenuVisitorException {
        visitor.visitRoot(this);
    }
    
}