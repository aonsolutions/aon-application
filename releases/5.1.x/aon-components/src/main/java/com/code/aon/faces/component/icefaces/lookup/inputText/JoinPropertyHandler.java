/**
 * 
 */
package com.code.aon.faces.component.icefaces.lookup.inputText;

import java.io.IOException;

import javax.el.ELException;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.el.ValueBinding;
import javax.faces.webapp.UIComponentTag;

import com.sun.facelets.FaceletContext;
import com.sun.facelets.FaceletException;
import com.sun.facelets.tag.TagAttribute;
import com.sun.facelets.tag.TagAttributeException;
import com.sun.facelets.tag.TagConfig;
import com.sun.facelets.tag.TagException;
import com.sun.facelets.tag.TagHandler;

/**
 * @author Consulting & Development. Iñaki Ayerbe - 27/11/2006
 *
 */
public class JoinPropertyHandler extends TagHandler {

    private final TagAttribute aliasTag;
    
    private final TagAttribute valueTag;

    /**
	 * Handler constructor.
	 * 
	 * @param config
	 */
	public JoinPropertyHandler(TagConfig config) {
		super(config);
		this.aliasTag = this.getRequiredAttribute("alias");
		this.valueTag = this.getRequiredAttribute("value");
	}


	/* (non-Javadoc)
	 * @see com.sun.facelets.FaceletHandler#apply(com.sun.facelets.FaceletContext, javax.faces.component.UIComponent)
	 */
	public void apply(FaceletContext ctx, UIComponent parent)
			throws IOException, FacesException, FaceletException, ELException {

		if (parent instanceof HtmlLookupInputText) {
            // only process if parent was just created
            if (parent.getParent() == null) {
				String value = valueTag.getValue();
	            if ( UIComponentTag.isValueReference(value) ) {
	    			String alias = aliasTag.getValue(ctx);
	            	ValueBinding vb = ctx.getFacesContext().getApplication().createValueBinding(value);
	    			HtmlLookupInputText text = (HtmlLookupInputText) parent;
	    			text.addJoinProperty(alias, vb);
	            } else {
	                throw new TagAttributeException( this.tag, valueTag, "Tag " + this.tagId + " attribute value must be a value reference, was " + value);
	            }
            }
		} else {
			throw new TagException( this.tag, "Component " + parent.getId() + " is no HtmlLookupInputText");
		}
		this.nextHandler.apply(ctx, parent);
	}

}
