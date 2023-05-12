package com.code.aon.faces.component.param;

import java.io.IOException;

import jakarta.el.ELException;
import jakarta.el.ValueExpression;
import jakarta.el.VariableMapper;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.faces.component.richfaces.IRichFacesTags;
import com.sun.facelets.FaceletContext;
import com.sun.facelets.FaceletException;
import com.sun.facelets.tag.TagAttribute;
import com.sun.facelets.tag.TagConfig;
import com.sun.facelets.tag.TagHandler;

public class ParamHandler extends TagHandler implements IRichFacesTags {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ParamHandler.class);

    private final TagAttribute name;

    private static final String TEST_VALUE = "testValue";
    
    /**
     * @param config
     */
    public ParamHandler(TagConfig config) {
        super(config);
        this.name = this.getRequiredAttribute(NAME);
    }

	private void addValueExmpression( FaceletContext ctx, String nameStr, TagAttribute tag ) {
		boolean setValue = true;
		ValueExpression valueVE = tag.getValueExpression(ctx, Object.class);
		TagAttribute testValueTag = getAttribute(TEST_VALUE);
		if ( (testValueTag != null) && testValueTag.getBoolean(ctx) ) {
			try {
				valueVE.getValue(ctx.getFacesContext().getELContext());
			} catch ( Throwable th ) {
				LOGGER.debug( "Invalid expression for " + nameStr, th);
				setValue = false;
			}
		}
		if ( setValue ) {
			ctx.getVariableMapper().setVariable(nameStr, valueVE);
		}
	}    
    
	@Override
	public void apply(FaceletContext ctx, UIComponent parent)
			throws IOException, FacesException, FaceletException, ELException {
        String nameStr = this.name.getValue(ctx);
        VariableMapper mapper = ctx.getVariableMapper();
        TagAttribute defaultTag = getAttribute(DEFAULT);
        if ( defaultTag != null ) {
        	if ( mapper.resolveVariable(nameStr) == null ) {
        		addValueExmpression(ctx, nameStr, defaultTag);
        	}
        } else {
        	TagAttribute value = getRequiredAttribute(VALUE);
        	addValueExmpression(ctx, nameStr, value);
        }
	}
    
}
