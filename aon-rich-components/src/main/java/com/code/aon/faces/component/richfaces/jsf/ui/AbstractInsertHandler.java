package com.code.aon.faces.component.richfaces.jsf.ui;

import java.io.IOException;
import java.util.Objects;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;

import com.sun.facelets.FaceletContext;
import com.sun.facelets.FaceletException;
import com.sun.facelets.TemplateClient;
import com.sun.facelets.tag.TagAttribute;
import com.sun.facelets.tag.TagAttributeException;
import com.sun.facelets.tag.TagConfig;
import com.sun.facelets.tag.TagHandler;

import jakarta.el.ELException;

public abstract class AbstractInsertHandler extends TagHandler implements TemplateClient{

	protected final String name;
	protected final String elVar;
	protected final TagAttribute filter;

	/**
     * @param config
     */
	protected AbstractInsertHandler(TagConfig config) {
        super(config);
        this.elVar = "el";
        TagAttribute nameAttr = this.getAttribute("name");
        if (nameAttr != null) {
            if (!nameAttr.isLiteral()) {
                throw new TagAttributeException(this.tag, nameAttr, "Must be Literal");
            }
            this.name = nameAttr.getValue();
        } else {
            this.name = null;
        }
        this.filter = this.getAttribute("filter");
    }

	
	public String getName() {
		return name;
	}
	
	public String getElVar() {
		return elVar;
	}
	
	public TagAttribute getFilter() {
		return filter;
	}
	
    @Override
    public boolean apply(FaceletContext ctx, UIComponent parent, String name)
    		throws IOException, FacesException, FaceletException, ELException {
        if (Objects.equals(this.name, name)) {
            this.nextHandler.apply(ctx, parent);
            return true;
        }
        return false;
    }
   
	protected static boolean exists(UIComponent parent, UIComponent e) {
		if ( Objects.equals(parent.getId(), e.getId())) {
			return true;
		} else {
			for ( UIComponent child: parent.getChildren() ) {
				if ( exists(child, e)) {
					return true;
				}
			}
			return false;
		}
	}

    

}