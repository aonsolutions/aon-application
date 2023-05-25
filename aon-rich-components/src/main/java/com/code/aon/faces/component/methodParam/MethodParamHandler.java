package com.code.aon.faces.component.methodParam;

import static com.code.aon.faces.component.richfaces.lookup.ILookupConstants.LOOKUP_CHANGE_LISTENER;

import java.io.IOException;

import jakarta.el.ELException;
import jakarta.el.MethodExpression;
import jakarta.el.ValueExpression;
import jakarta.el.VariableMapper;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;

import org.apache.commons.lang.StringUtils;

import com.code.aon.faces.component.param.ParamHandler;
import com.code.aon.faces.component.richfaces.IRichFacesTags;
import com.code.aon.faces.component.util.FaceletUtil;
import com.code.aon.faces.component.util.MethodValueExpression;
import com.sun.facelets.FaceletContext;
import com.sun.facelets.FaceletException;
import com.sun.facelets.tag.TagAttribute;
import com.sun.facelets.tag.TagConfig;
import com.sun.facelets.tag.TagHandler;

public class MethodParamHandler extends TagHandler implements IRichFacesTags {

	private static final String TYPE = "type";
	
	private static final String RESOLVE = "resolve";
	
	private final TagAttribute name;
	
	private Class<?> returnType;
	
	private Class<?>[] paramTypes;

	/**
	 * @param config
	 */
	public MethodParamHandler(TagConfig config) {
		super(config);
		this.name = this.getRequiredAttribute(ParamHandler.NAME);
	}
	
	private void resolveTypes( FaceletContext ctx ) {
		String type = ACTION;
		TagAttribute typeTag = getAttribute(TYPE);
		if ( typeTag != null ) {
			type = typeTag.getValue(ctx);
		}
		if ( ACTION.equals(type) ) {
			this.returnType = String.class;
			this.paramTypes = FaceletUtil.ACTION_SIG;
		} else if ( ACTION_LISTENER.equals(type) ) {
			this.returnType = null;
			this.paramTypes = FaceletUtil.ACTION_LISTENER_SIG;			
		} else if ( VALUE_CHANGE_LISTENER.equals(type) ) {
			this.returnType = null;
			this.paramTypes = FaceletUtil.VALUE_CHANGE_LISTENER_SIG;			
		} else if ( VALIDATOR.equals(type) ) {
			this.returnType = null;
			this.paramTypes = FaceletUtil.VALIDATOR_SIG;			
		} else if ( NODE_SELECT_LISTENER.equals(type) ) {
			this.returnType = null;
			this.paramTypes = FaceletUtil.NODE_SELECT_LISTENER_SIG;			
		} else if ( LOOKUP_CHANGE_LISTENER.equals(type) ) {
			this.returnType = null;
			this.paramTypes = FaceletUtil.LOOKUP_CHANGE_LISTENER_SIG;			
		}
	}
	
	private boolean isResolve( FaceletContext ctx ) {
		if ( this.returnType != null ) {
			TagAttribute resolveTag = getAttribute(RESOLVE);
			if ( resolveTag != null ) {
				return resolveTag.getBoolean(ctx);
			}			
		}
		return false;
	}

	private ValueExpression getResolvedMethodExpression( FaceletContext ctx, TagAttribute valueTag, Class type, Class[] paramTypes ) {
		String expression = valueTag.getValue(ctx);
		ValueExpression valueVE = null;
		if ( StringUtils.isBlank(expression) ) {
			valueVE = FaceletUtil.getMethodEmptyExpression(ctx, name.getValue(ctx), returnType, paramTypes);
		} else {
			ValueExpression ve = FaceletUtil.getValueExpression(ctx, expression, Object.class );
			MethodExpression methodExpression = FaceletUtil.getMethodExpression( ctx, expression, type, paramTypes );
			valueVE = new MethodValueExpression( ve, methodExpression );			
		}
		return valueVE;
	}
	
	private ValueExpression getValueExmpression( FaceletContext ctx, String nameStr, TagAttribute tag ) {
		ValueExpression valueVE = null;
		if ( (tag == null) || (StringUtils.isBlank(tag.getValue())) ) {
			valueVE = FaceletUtil.getMethodEmptyExpression(ctx, nameStr, returnType, paramTypes);
		} else {
			if ( isResolve(ctx) ) {
				valueVE = getResolvedMethodExpression(ctx, tag, returnType, paramTypes);
			} else {
				valueVE = FaceletUtil.getMethodExpression(ctx, tag, returnType, paramTypes);
			}			
		}
		return valueVE;
	}
	
	public void apply(FaceletContext ctx, UIComponent parent)
			throws IOException, FacesException, FaceletException, ELException {
		String nameStr = this.name.getValue(ctx);
		VariableMapper mapper = ctx.getVariableMapper();
		resolveTypes(ctx);
		TagAttribute defaultTag = getAttribute(ParamHandler.DEFAULT);
		if ( defaultTag != null ) {
        	if ( mapper.resolveVariable(nameStr) == null ) {
        		ValueExpression valueVE = getValueExmpression(ctx, nameStr, defaultTag);
        		mapper.setVariable(nameStr, valueVE);
        	}			
		} else {
			TagAttribute valueTag = getAttribute(ParamHandler.VALUE);
			ValueExpression valueVE = getValueExmpression(ctx, nameStr, valueTag);
			mapper.setVariable(nameStr, valueVE);
		}
	}

}
