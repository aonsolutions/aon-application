package com.code.aon.faces.component.richfaces.componentGroup;

import java.io.IOException;

import javax.el.ELException;
import javax.el.MethodExpression;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;

import com.code.aon.faces.component.richfaces.IRichFacesTags;
import com.code.aon.faces.component.util.FaceletUtil;
import com.sun.facelets.FaceletContext;
import com.sun.facelets.FaceletException;
import com.sun.facelets.tag.TagAttribute;
import com.sun.facelets.tag.TagConfig;
import com.sun.facelets.tag.TagHandler;
import com.sun.facelets.tag.jsf.ComponentSupport;

public class ComponentGroupHandler extends TagHandler implements IRichFacesTags {

	private final TagAttribute family;
	
	private final TagAttribute method;
	
    public ComponentGroupHandler(TagConfig config) {
        super(config);
        this.family = this.getRequiredAttribute(FAMILY);
        this.method = this.getRequiredAttribute(METHOD);
    }

    public void apply(FaceletContext ctx, UIComponent parent)
            throws IOException, FacesException, FaceletException, ELException {
		if ( FaceletUtil.isRendered(ctx, tag) ) {
			UIViewRoot root = ComponentSupport.getViewRoot(ctx, parent);
			MethodExpression me = method.getMethodExpression(ctx, null, FaceletUtil.COMPONENT_GROUP_SIG);
			ComponentGroup cg = new ComponentGroup( family.getValue(ctx), me );
			root.getAttributes().put( ComponentGroup.CURRENT_COMPONENT_GROUP, cg );
	        this.nextHandler.apply(ctx, parent);
			root.getAttributes().remove( ComponentGroup.CURRENT_COMPONENT_GROUP );        			
		} else {
	        this.nextHandler.apply(ctx, parent);
		}
    }
    
}
