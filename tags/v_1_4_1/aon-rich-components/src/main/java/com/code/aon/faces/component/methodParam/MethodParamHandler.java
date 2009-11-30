package com.code.aon.faces.component.methodParam;

import java.io.IOException;

import javax.el.ELException;
import javax.el.ValueExpression;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;

import com.code.aon.faces.component.util.FaceletUtil;
import com.sun.facelets.FaceletContext;
import com.sun.facelets.FaceletException;
import com.sun.facelets.tag.TagAttribute;
import com.sun.facelets.tag.TagConfig;
import com.sun.facelets.tag.TagHandler;

public class MethodParamHandler extends TagHandler {

	private static final String VALUE = "value";

	private static final String NAME = "name";
	
	private static final String TYPE = "type";
	
	private static final String ACTION = "action";
	
	private static final String ACTION_LISTENER = "actionListener";	
	
	private static final String VALUE_CHANGE_LISTENER = "valueChangeListener";
	
	private static final String VALIDATOR = "validator";

	private final TagAttribute name;
	
	private Class<?> returnType;
	
	private Class<?>[] paramTypes;

	/**
	 * @param config
	 */
	public MethodParamHandler(TagConfig config) {
		super(config);
		this.name = this.getRequiredAttribute(NAME);
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
		}
	}

	public void apply(FaceletContext ctx, UIComponent parent)
			throws IOException, FacesException, FaceletException, ELException {
		String nameStr = this.name.getValue(ctx);
		resolveTypes(ctx);
		ValueExpression valueVE = null;
		TagAttribute valueTag = getAttribute(VALUE);
		if ( valueTag != null ) {
			valueVE = FaceletUtil.getMethodExpression(ctx, valueTag, returnType, paramTypes);
		} else {
			valueVE = FaceletUtil.getMethodEmptyExpression(ctx, nameStr, returnType, paramTypes);
		}
		ctx.getVariableMapper().setVariable(nameStr, valueVE);
	}

}
