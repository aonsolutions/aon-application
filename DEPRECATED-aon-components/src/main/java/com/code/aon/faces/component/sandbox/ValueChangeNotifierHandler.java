package com.code.aon.faces.component.sandbox;

import javax.faces.component.EditableValueHolder;
import javax.faces.component.UIComponent;
import javax.faces.webapp.UIComponentTag;

import com.sun.facelets.FaceletContext;
import com.sun.facelets.tag.TagConfig;
import com.sun.facelets.tag.TagException;
import com.sun.facelets.tag.TagHandler;
import com.sun.facelets.tag.jsf.ComponentSupport;

/**
 * The Class TabbedPaneComponentHandler.
 * 
 * @author atellitu
 */
public class ValueChangeNotifierHandler extends TagHandler {

    private String method;
    
	/**
	 * The Constructor.
	 * 
	 * @param config
	 *            the config
	 */
	public ValueChangeNotifierHandler(TagConfig config) {
		super(config);
		this.method = this.getRequiredAttribute("method").getValue();
	}

	/**
	 * Setup class listener.
	 * 
	 * @param component the component
	 * @param ctx the ctx
	 * @param method the method
	 */
	public static void setupClassListener(FaceletContext ctx, EditableValueHolder component, String method) {
		if (UIComponentTag.isValueReference(method)) {
			ValueChangeCollector vcc = new ValueChangeCollector();
			vcc.restoreState( ctx.getFacesContext(), new Object[]{method} );
			component.addValueChangeListener( vcc );
		} else {
			throw new IllegalArgumentException( "Invalid expression " + method);
		}
	}

	/**
	 * Apply.
	 * 
	 * @param ctx the ctx
	 * @param parent the parent
	 */
	public void apply(FaceletContext ctx, UIComponent parent) {	
		// Component was just created, so we add the Listener
		if (parent instanceof EditableValueHolder) {
			if (ComponentSupport.isNew(parent)) {			
            	setupClassListener( ctx, (EditableValueHolder) parent, this.method );
            }
		} else {
			throw new TagException(this.tag, "Component " + parent.getId()
					+ " is no EditableValueHolder");
		}
	}

}