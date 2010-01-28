package com.code.aon.faces.component.param;

import java.io.IOException;

import javax.el.ELException;
import javax.el.ValueExpression;
import javax.el.VariableMapper;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;

import com.sun.facelets.FaceletContext;
import com.sun.facelets.FaceletException;
import com.sun.facelets.tag.TagAttribute;
import com.sun.facelets.tag.TagConfig;
import com.sun.facelets.tag.TagHandler;

public class ParamHandler extends TagHandler {

	public static final String NAME = "name";	
	
	public static final String VALUE = "value";
	
	public static final String DEFAULT = "default";

    private final TagAttribute name;

    /**
     * @param config
     */
    public ParamHandler(TagConfig config) {
        super(config);
        this.name = this.getRequiredAttribute(NAME);
    }

	@Override
	public void apply(FaceletContext ctx, UIComponent parent)
			throws IOException, FacesException, FaceletException, ELException {
        String nameStr = this.name.getValue(ctx);
        VariableMapper mapper = ctx.getVariableMapper();
        TagAttribute defaultTag = getAttribute(DEFAULT);
        if ( defaultTag != null ) {
        	if ( mapper.resolveVariable(nameStr) == null ) {
                ValueExpression valueVE = defaultTag.getValueExpression(ctx, Object.class);
                mapper.setVariable(nameStr, valueVE);        		
        	}
        } else {
        	TagAttribute value = getRequiredAttribute(VALUE);
            ValueExpression valueVE = value.getValueExpression(ctx, Object.class);
            mapper.setVariable(nameStr, valueVE);
        }
	}
    
}
