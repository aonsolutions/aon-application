package com.code.aon.faces.component.icefaces.confirmButton;

import javax.el.ValueExpression;
import javax.el.VariableMapper;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.event.ActionEvent;

import com.code.aon.faces.component.icefaces.AonIceComponentHandler;
import com.code.aon.faces.component.icefaces.lookup.ILookupTags;
import com.code.aon.faces.component.util.FaceletUtil;
import com.sun.facelets.FaceletContext;
import com.sun.facelets.el.VariableMapperWrapper;
import com.sun.facelets.tag.TagAttribute;
import com.sun.facelets.tag.jsf.ComponentConfig;
import com.sun.facelets.tag.jsf.ComponentSupport;

/**
 * The Class ConfirmButtonHandler.
 * 
 * @author atellitu
 */
public class ConfirmButtonHandler extends AonIceComponentHandler implements
		ILookupTags {

    private static final String COMPONENT_TYPE = "com.code.aon.faces.HtmlConfirmButton";
    
	private static final String CONFIRM_SHOW_WINDOW = "confirmShowWindow";
	
	private static final String CONFIRM_ACTION = "confirmAction";

	private static final String CONFIRM_ACTION_LISTENER = "confirmActionListener";

	private static final String CONFIRM_TITLE = "confirmTitle";

	private static final String CONFIRM_MESSAGE = "confirmMessage";
	
	private static final String IMMEDIATE = "immediate";

	private static final String TEMPLATE_PATH = "com/code/aon/faces/component/icefaces/confirmButton/";
	
	private static final String TEMPLATE = TEMPLATE_PATH + "template.xhtml";
	
	private static final String INNER_TEMPLATE = TEMPLATE_PATH + "innerTemplate.xhtml";

	private final static Class[] ACTION_SIG = new Class[0];

	private final static Class[] ACTION_LISTENER_SIG = new Class[] { ActionEvent.class };

	private TagAttribute titleTag;

	private TagAttribute messageTag;

	/**
	 * The Constructor.
	 * 
	 * @param config
	 *            the config
	 */
	public ConfirmButtonHandler(ComponentConfig config) {
		super(config);
		this.titleTag = getRequiredAttribute(CONFIRM_TITLE);
		this.messageTag = getRequiredAttribute(CONFIRM_MESSAGE);
	}

	@Override
	protected void onComponentCreated(FaceletContext ctx, UIComponent c, UIComponent parent) {
		addConfirmButtonState( ctx, c );
		insertInnerTemplate(ctx, c);
	}

	@Override
	protected void onComponentPopulated(FaceletContext ctx, UIComponent c,
			UIComponent parent) {
		insertTemplate(ctx, c, parent);
	}

	private ValueExpression getValueExpression(FaceletContext ctx,
			TagAttribute tag) {
		return tag.getValueExpression(ctx, Object.class);
	}

	private void addConfirmButtonState( FaceletContext ctx, UIComponent component ) {
		UIViewRoot root = ComponentSupport.getViewRoot(ctx, component);
		String stateKey = getStateKey(component);
		if (! root.getAttributes().containsKey(stateKey) ) {
			root.getAttributes().put( stateKey, Boolean.FALSE );
		}
	}
	
	private String getStateKey( UIComponent component ) {
		return COMPONENT_TYPE + "." + component.getId() + ".showWindow";
	}

	private ValueExpression getStateExpression( FaceletContext ctx, UIComponent component ) {
		String expr = "#{view.attributes['" + getStateKey(component) + "']}";
		return ctx.getExpressionFactory().createValueExpression( ctx, expr, Object.class );
	}
	
	private void insertTemplate(FaceletContext ctx, UIComponent component, UIComponent parent) {
		VariableMapper newMapper = new VariableMapperWrapper(ctx.getVariableMapper());
		TagAttribute tagImmediate = getAttribute(IMMEDIATE); 
		if( tagImmediate != null){
			newMapper.setVariable(IMMEDIATE, getValueExpression(ctx, tagImmediate) );
		} else {
			newMapper.setVariable(IMMEDIATE, ctx.getExpressionFactory().createValueExpression(ctx, "false", Boolean.class));
		}
		newMapper.setVariable(CONFIRM_SHOW_WINDOW, getStateExpression(ctx, component));
		newMapper.setVariable(CONFIRM_TITLE, getValueExpression(ctx, titleTag));
		newMapper.setVariable(CONFIRM_MESSAGE, getValueExpression(ctx, messageTag));
		ValueExpression action = FaceletUtil.getMethodExpression(ctx, getAttribute(CONFIRM_ACTION), String.class, ACTION_SIG);
		if ( action == null ) {
			action = FaceletUtil.getMethodEmptyExpression(ctx, CONFIRM_ACTION, String.class, ACTION_SIG);
		}
		newMapper.setVariable( CONFIRM_ACTION, action );
		ValueExpression al = FaceletUtil.getMethodExpression(ctx, getAttribute(CONFIRM_ACTION_LISTENER), null, ACTION_LISTENER_SIG);;
		if ( al != null ) {
			newMapper.setVariable( CONFIRM_ACTION_LISTENER, al );				
		}
		FaceletUtil.insertTemplate(ctx, tag, parent, FaceletUtil.getTemplate(TEMPLATE), newMapper );
	}

	private void insertInnerTemplate(FaceletContext ctx, UIComponent component) {
		VariableMapper newMapper = new VariableMapperWrapper(ctx.getVariableMapper());
        newMapper.setVariable(CONFIRM_SHOW_WINDOW, getStateExpression(ctx, component));
		FaceletUtil.insertTemplate(ctx, tag, component, FaceletUtil.getTemplate(INNER_TEMPLATE), newMapper);
	}
	
}