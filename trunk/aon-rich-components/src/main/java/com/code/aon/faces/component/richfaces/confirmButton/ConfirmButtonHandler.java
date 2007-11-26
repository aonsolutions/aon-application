package com.code.aon.faces.component.richfaces.confirmButton;

import java.net.URL;

import javax.el.ExpressionFactory;
import javax.el.MethodExpression;
import javax.el.ValueExpression;
import javax.el.VariableMapper;
import javax.faces.component.UIComponent;
import javax.faces.event.ActionEvent;

import com.code.aon.faces.component.AonComponentHandler;
import com.code.aon.faces.component.MethodValueExpression;
import com.code.aon.faces.component.myfaces.UIComponentTagUtils;
import com.code.aon.faces.component.richfaces.lookup.ILookupTags;
import com.sun.facelets.FaceletContext;
import com.sun.facelets.el.VariableMapperWrapper;
import com.sun.facelets.tag.MetaRuleset;
import com.sun.facelets.tag.TagAttribute;
import com.sun.facelets.tag.jsf.ComponentConfig;

/**
 * The Class ConfirmButtonHandler.
 * 
 * @author atellitu
 */
public class ConfirmButtonHandler extends AonComponentHandler implements
		ILookupTags {

	private static final String CONFIRM_ID = "confirmId";
	
	private static final String CONFIRM_ACTION = "confirmAction";

	private static final String CONFIRM_ACTION_LISTENER = "confirmActionListener";

	private static final String CONFIRM_TITLE = "confirmTitle";

	private static final String CONFIRM_MESSAGE = "confirmMessage";
	
	private static final String IMMEDIATE = "immediate";

	private static final String TEMPLATE_PATH = "com/code/aon/faces/component/richfaces/confirmButton/";
	
	private static final String TEMPLATE = TEMPLATE_PATH + "template.xhtml";

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
	protected MetaRuleset createMetaRuleset(Class type) {
		MetaRuleset mrs = super.createMetaRuleset(type);
		mrs.ignore(CONFIRM_ACTION).ignore(CONFIRM_ACTION_LISTENER);
		mrs.ignore(CONFIRM_TITLE).ignore(CONFIRM_MESSAGE);
		return mrs;
	}
	
	private String getPanelId( FaceletContext ctx ) {
		return getId(ctx) + "ModelPanel";	
	}

	private String getShowScript( FaceletContext ctx ) {
		return "Richfaces.showModalPanel('" + getPanelId(ctx) + "');";
	}
	
	@Override
	protected void setAttributes(FaceletContext ctx, Object instance) {
		super.setAttributes(ctx, instance);
		UIComponent button = (UIComponent) instance;
		UIComponentTagUtils.setStringProperty(ctx.getFacesContext(), button, "onclick", getShowScript(ctx) );		
	}

	@Override
	protected void onComponentPopulated(FaceletContext ctx, UIComponent c,
			UIComponent parent) {
		insertTemplate(ctx, c, parent);
	}

	private URL getTemplate(String resource) {
		ClassLoader loader = this.getClass().getClassLoader();
		return loader.getResource(resource);
	}

	private ValueExpression getValueExpression(FaceletContext ctx,
			TagAttribute tag) {
		return tag.getValueExpression(ctx, Object.class);
	}

	private ValueExpression getMethodExpression(FaceletContext ctx, String name, Class type, Class[] paramTypes ) {
		ValueExpression valueExpression = null;
		TagAttribute tag = getAttribute(name);
		if (tag != null) {
			ValueExpression ve = tag.getValueExpression(ctx, Object.class );
			MethodExpression methodExpression = tag.getMethodExpression( ctx, type, paramTypes );
			valueExpression = new MethodValueExpression( ve, methodExpression );
		}
		return valueExpression;
	}

	private ValueExpression getMethodEmptyExpression(FaceletContext ctx, String name, Class type, Class[] paramTypes ) {
        ExpressionFactory f = ctx.getExpressionFactory();
        ValueExpression ve = f.createValueExpression( ctx, "", Object.class );
        MethodExpression me = f.createMethodExpression(ctx, name, type, paramTypes );
        return new MethodValueExpression( ve, me );
	}
	
	private void insertTemplate(FaceletContext ctx, UIComponent component, UIComponent parent) {
		VariableMapper newMapper = new VariableMapperWrapper(ctx.getVariableMapper());
		TagAttribute tagImmediate = getAttribute(IMMEDIATE); 
		if( tagImmediate != null){
			newMapper.setVariable(IMMEDIATE, getValueExpression(ctx, tagImmediate) );
		} else {
			newMapper.setVariable(IMMEDIATE, ctx.getExpressionFactory().createValueExpression(ctx, "#{" + IMMEDIATE + "}", Boolean.class));
		}
		ValueExpression id = ctx.getExpressionFactory().createValueExpression(ctx, getPanelId(ctx), String.class);
		newMapper.setVariable(CONFIRM_ID, id);
		newMapper.setVariable(CONFIRM_TITLE, getValueExpression(ctx, titleTag));
		newMapper.setVariable(CONFIRM_TITLE, getValueExpression(ctx, titleTag));
		newMapper.setVariable(CONFIRM_MESSAGE, getValueExpression(ctx, messageTag));
		ValueExpression action = getMethodExpression(ctx, CONFIRM_ACTION, String.class, ACTION_SIG);
		if ( action == null ) {
			action = getMethodEmptyExpression(ctx, CONFIRM_ACTION, String.class, ACTION_SIG);
		}
		newMapper.setVariable( CONFIRM_ACTION, action );
		ValueExpression al = getMethodExpression(ctx, CONFIRM_ACTION_LISTENER, null, ACTION_LISTENER_SIG);
		if ( al != null ) {
			newMapper.setVariable( CONFIRM_ACTION_LISTENER, al );				
		}
		insertTemplate(ctx, parent, getTemplate(TEMPLATE), newMapper );
	}
	
}