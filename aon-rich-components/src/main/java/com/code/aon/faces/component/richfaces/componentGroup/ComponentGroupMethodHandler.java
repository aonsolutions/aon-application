package com.code.aon.faces.component.richfaces.componentGroup;

import java.io.IOException;

import javax.el.ELException;
import javax.el.MethodExpression;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;

import com.code.aon.faces.component.richfaces.IRichFacesTags;
import com.code.aon.faces.component.util.FaceletUtil;
import com.sun.facelets.FaceletContext;
import com.sun.facelets.FaceletException;
import com.sun.facelets.tag.TagAttribute;
import com.sun.facelets.tag.TagConfig;
import com.sun.facelets.tag.TagHandler;

public class ComponentGroupMethodHandler extends TagHandler implements IRichFacesTags {

	private final TagAttribute family;
	
	private final TagAttribute method;
	
    public ComponentGroupMethodHandler(TagConfig config) {
        super(config);
        this.family = this.getRequiredAttribute(FAMILY);
        this.method = this.getRequiredAttribute(METHOD);
    }

    public void apply(FaceletContext ctx, UIComponent parent)
            throws IOException, FacesException, FaceletException, ELException {
		if ( FaceletUtil.isRendered(ctx, tag) ) {
			MethodExpression me = method.getMethodExpression(ctx, null, FaceletUtil.COMPONENT_GROUP_SIG);
			ComponentGroupMethod cgm = new ComponentGroupMethod( family.getValue(ctx), me );
			ComponentGroup componentGroup = ComponentGroup.getComponentGroup(ctx, parent);
			if ( componentGroup != null ) {
				componentGroup.add(cgm);
			}
		} else {
	        this.nextHandler.apply(ctx, parent);
		}
    }
    
}
