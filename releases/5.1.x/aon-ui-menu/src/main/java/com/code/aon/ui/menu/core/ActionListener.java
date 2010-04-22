package com.code.aon.ui.menu.core;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.application.Application;
import javax.faces.context.FacesContext;
import javax.faces.el.MethodBinding;
import javax.faces.el.ValueBinding;
import javax.faces.event.ActionEvent;
import javax.faces.webapp.UIComponentTag;

import org.apache.myfaces.shared_tomahawk.util.ClassUtils;

import com.code.aon.ui.menu.IActionListener;
import com.code.aon.ui.menu.IMenuItem;
import com.code.aon.ui.menu.IMenuVisitor;
import com.code.aon.ui.menu.MenuVisitorException;
import com.code.aon.ui.menu.jsf.IJSFMenu;
import com.code.aon.ui.menu.jsf.MenuActionListener;
import com.code.aon.ui.menu.jsf.MenuEvent;

/**
 * Defines an action listener of a menu element.
 *
 */
public class ActionListener implements IActionListener {
	
    /**
	 * Logger initialization.
     */
    private static Logger LOGGER = Logger.getLogger(ActionListener.class.getName());

    private static final Class[] MENU_ACTION_LISTENER_ARGS = {MenuEvent.class};	

	private IMenuItem menuItem;
	private String type;
	private String action;
	private MenuActionListener menuActionListener;
	
	public String getAction() {
		return this.action;
	}
	
	public void setAction(String action) {
		this.action = action;
	}
	
	private MethodBinding getMethodBinding(FacesContext context, String action) {
		MethodBinding mb = null;
		if (UIComponentTag.isValueReference(action) ) {
			Application application = context.getApplication();
			mb = application.createMethodBinding(action, MENU_ACTION_LISTENER_ARGS);
		}
		return mb;
	}
	
	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
		setActionListener( this.type );
	}

	private void setActionListener(String type) {
		if ( type != null ) {
            String className;
			try {
	            if ( UIComponentTag.isValueReference(type) ) {
	                FacesContext facesContext = FacesContext.getCurrentInstance();
	                ValueBinding vb = facesContext.getApplication().createValueBinding(type);
	                className = (String)vb.getValue(facesContext);
	            } else {
	                className = type;
	            }
            	this.menuActionListener = (MenuActionListener)ClassUtils.newInstance(className);
            } catch ( Throwable th ) {
            	LOGGER.log( Level.SEVERE, "Error creating ActionListener " + type, th );
            }
		}
	}
	
    public IMenuItem getMenuItem() {
		return this.menuItem;
	}

	public void setMenuItem(IMenuItem menuItem) {
		this.menuItem = menuItem;
	}

	public void execute( ActionEvent actionEvent, IJSFMenu menu ) {
		MenuEvent event = new MenuEvent( actionEvent, menu );		
    	if ( this.menuActionListener != null ) {
    		this.menuActionListener.processAction( event );
    	} else if ( this.action != null ) {
    		try {
    			FacesContext context = FacesContext.getCurrentInstance();
	    		MethodBinding mb = getMethodBinding( context, this.action );
	    		mb.invoke( context, new Object[] {event} );
	        } catch ( Throwable th ) {
	        	LOGGER.log( Level.SEVERE, "Error in invoke of action " + action, th );
	        }
    	}
    }

	public void visit(IMenuVisitor visitor) throws MenuVisitorException {
        visitor.visitActionListener(this);
    }

}
