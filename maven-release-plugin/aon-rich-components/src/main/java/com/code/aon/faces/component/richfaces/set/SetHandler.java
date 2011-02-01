package com.code.aon.faces.component.richfaces.set;

import java.io.IOException;

import javax.el.ELException;
import javax.el.ValueExpression;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;

import com.code.aon.faces.component.richfaces.IRichFacesTags;
import com.sun.facelets.FaceletContext;
import com.sun.facelets.FaceletException;
import com.sun.facelets.tag.TagAttribute;
import com.sun.facelets.tag.TagConfig;
import com.sun.facelets.tag.TagHandler;

public class SetHandler extends TagHandler implements IRichFacesTags {

    private final TagAttribute var;
    
    private final TagAttribute value;
    
    public SetHandler(TagConfig config) {
        super(config);
        this.value = this.getRequiredAttribute(VALUE);
        this.var = this.getRequiredAttribute("var");
    }

    public void apply(FaceletContext ctx, UIComponent parent)
            throws IOException, FacesException, FaceletException, ELException {
    	ValueExpression veVar = this.var.getValueExpression(ctx, Object.class);
        Object valueObj = this.value.getObject(ctx);
        veVar.setValue(ctx, valueObj);
        this.nextHandler.apply(ctx, parent);
    }
    
}
