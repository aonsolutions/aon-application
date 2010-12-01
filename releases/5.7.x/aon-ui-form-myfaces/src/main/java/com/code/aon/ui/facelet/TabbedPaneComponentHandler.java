package com.code.aon.ui.facelet;

import javax.faces.application.Application;
import javax.faces.context.FacesContext;
import javax.faces.el.MethodBinding;

import org.apache.myfaces.custom.tabbedpane.HtmlPanelTabbedPane;
import org.apache.myfaces.custom.tabbedpane.TabChangeEvent;

import com.sun.facelets.FaceletContext;
import com.sun.facelets.tag.TagAttribute;
import com.sun.facelets.tag.TagException;
import com.sun.facelets.tag.jsf.ComponentConfig;
import com.sun.facelets.tag.jsf.ComponentHandler;

/**
 * The Class TabbedPaneComponentHandler.
 * 
 * @author atellitu
 */
public class TabbedPaneComponentHandler extends ComponentHandler {

	/** The Constant METHOD_BINDING_ATTR_NAME. */
	static final String METHOD_BINDING_ATTR_NAME = "tabChangeListener";

	/** The Constant METHOD_BINDING_SIGNATURE. */
	static final Class[] METHOD_BINDING_SIGNATURE = { TabChangeEvent.class };

	/**
	 * The Constructor.
	 * 
	 * @param config the config
	 */
	public TabbedPaneComponentHandler(ComponentConfig config) {
		super( config );
	}

	/**
	 * Sets the attributes.
	 * 
	 * @param instance the instance
	 * @param ctx the ctx
	 */
	@Override
	protected void setAttributes(FaceletContext ctx, Object instance) {
		super.setAttributes(ctx, instance);
		try {
			final HtmlPanelTabbedPane component = (HtmlPanelTabbedPane) instance;
			final TagAttribute tagAttribute = TabbedPaneComponentHandler.this
					.getAttribute(METHOD_BINDING_ATTR_NAME);
			if (null != tagAttribute) {
				Application application = FacesContext.getCurrentInstance().getApplication();
				final MethodBinding methodBinding = application.createMethodBinding(tagAttribute.getValue(), METHOD_BINDING_SIGNATURE );
				component.setTabChangeListener(methodBinding);
			}
		} catch (Exception e) {
			throw new TagException(this.tag, e);
		}
	}

}