/**
 * 
 */
package com.code.aon.faces.component.richfaces.lookup.inputText;

import java.io.IOException;

import javax.el.ELException;
import javax.el.ValueExpression;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;

import com.sun.facelets.FaceletContext;
import com.sun.facelets.FaceletException;
import com.sun.facelets.tag.TagAttribute;
import com.sun.facelets.tag.TagAttributeException;
import com.sun.facelets.tag.TagConfig;
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

	@Override
	public void apply(FaceletContext ctx, UIComponent parent)
			throws IOException, FacesException, FaceletException, ELException {

		if (parent instanceof HtmlLookupInputText) {
            // only process if parent was just created
            if (parent.getParent() == null) {
	            if (! valueTag.isLiteral() ) {
	    			String alias = aliasTag.getValue(ctx);
	            	ValueExpression ve = valueTag.getValueExpression(ctx, Object.class);
	    			HtmlLookupInputText text = (HtmlLookupInputText) parent;
	    			text.addJoinProperty(alias, ve);
	            } else {
	                throw new TagAttributeException( this.tag, valueTag, "Tag " + this.tagId + " attribute value must be a value reference, was " + valueTag.getValue());
	            }
            }
		}
		this.nextHandler.apply(ctx, parent);
	}

}
