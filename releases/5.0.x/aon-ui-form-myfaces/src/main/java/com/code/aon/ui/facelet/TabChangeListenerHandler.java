/**
 * 
 */
package com.code.aon.ui.facelet;

import java.io.IOException;

import javax.el.ELException;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;
import javax.faces.webapp.UIComponentTag;

import org.apache.myfaces.custom.tabbedpane.HtmlPanelTabbedPane;
import org.apache.myfaces.custom.tabbedpane.TabChangeListener;
import org.apache.myfaces.shared_tomahawk.util.ClassUtils;

import com.sun.facelets.FaceletContext;
import com.sun.facelets.FaceletException;
import com.sun.facelets.tag.TagAttribute;
import com.sun.facelets.tag.TagConfig;
import com.sun.facelets.tag.TagException;
import com.sun.facelets.tag.TagHandler;

/**
 * @author Consulting & Development. Iñaki Ayerbe - 27/11/2006
 *
 */
public class TabChangeListenerHandler extends TagHandler {

    private final TagAttribute type;

    /**
	 * Handler constructor.
	 * 
	 * @param config
	 */
	public TabChangeListenerHandler(TagConfig config) {
		super(config);
		// helper method for getting a required attribute
		this.type = this.getRequiredAttribute("type");		
	}


	/* (non-Javadoc)
	 * @see com.sun.facelets.FaceletHandler#apply(com.sun.facelets.FaceletContext, javax.faces.component.UIComponent)
	 */
	public void apply(FaceletContext ctx, UIComponent parent)
			throws IOException, FacesException, FaceletException, ELException {

		String type = this.type.getValue(ctx);
		if (parent instanceof HtmlPanelTabbedPane) {
			String className;
			if (UIComponentTag.isValueReference(type)) {
				FacesContext facesContext = ctx.getFacesContext();
				ValueBinding valueBinding = facesContext.getApplication().createValueBinding(type);
				className = (String) valueBinding.getValue(facesContext);
			} else {
				className = type;
			}
			TabChangeListener listener = (TabChangeListener) ClassUtils.newInstance(className);
			((HtmlPanelTabbedPane) parent).addTabChangeListener(listener);
		} else {
			throw new TagException( this.tag, "Component " + parent.getId() + " is no HtmlPanelTabbedPane");
		}
		this.nextHandler.apply(ctx, parent);
	}

}
