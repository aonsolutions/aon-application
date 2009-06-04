package com.code.aon.faces.component.richfaces.lookup.inputText;

import java.util.HashMap;
import java.util.Map;

import javax.el.ExpressionFactory;
import javax.el.MethodExpression;
import javax.el.ValueExpression;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;

import org.apache.commons.beanutils.PropertyUtils;

import com.code.aon.common.dao.AliasEntry;
import com.code.aon.common.dao.DAOConstantsResolver;
import com.code.aon.faces.component.richfaces.AonAjaxInputHandler;
import com.code.aon.faces.component.richfaces.lookup.ILookupTags;
import com.code.aon.faces.component.sandbox.ValueChangeNotifierHandler;
import com.code.aon.faces.component.util.FaceletUtil;
import com.code.aon.ui.form.BasicController;
import com.sun.facelets.FaceletContext;
import com.sun.facelets.tag.TagAttribute;
import com.sun.facelets.tag.jsf.ComponentConfig;

/**
 * The Class TabbedPaneComponentHandler.
 * 
 * @author atellitu
 */
public class LookupInputTextHandler extends AonAjaxInputHandler implements ILookupTags {

    private static final String VALUE_CHANGE_LISTENER = "lookupChanged";
	
   	/**
	 * The Constructor.
	 * 
	 * @param config the config
	 */
	public LookupInputTextHandler(ComponentConfig config) {
		super( config );
		setAjaxNeeded( true );
	}

	private void setValueChangeNotifier( FaceletContext ctx, UIInput text ) {
		String lookup = getRequiredAttribute(LOOKUP).getValue();
		String valueChangeListener = FaceletUtil.appendExpression( lookup, VALUE_CHANGE_LISTENER);
		ValueChangeNotifierHandler.setupClassListener(ctx, text, valueChangeListener);
	}
	
	private void setLookupChangeListener( FaceletContext ctx, HtmlLookupInputText text ) {	
		TagAttribute vcl = getAttribute(LOOKUP_CHANGE_LISTENER);
		if ( vcl != null ) {
			MethodExpression me = vcl.getMethodExpression(ctx, null, FaceletUtil.LOOKUP_CHANGE_LISTENER_SIG);
			text.setLookupChangeListener( me );
		}
	}
	
	private String getPropertyClassName( FaceletContext ctx, HtmlLookupInputText text ) {
		String className = null;		
		BasicController controller = text.getLookup().getController();
		if ( text.getLookupProperty() != null ) {
			try {
				Object value = controller.getManagerBean().createNewTo();
				Class<?> _class = PropertyUtils.getPropertyType( value, text.getLookupProperty() );
				if ( _class != null ) {
					className = _class.getName();
				}
			} catch (Throwable e) {
				// LOGGER.severe( e.getMessage() );
			}
		} else {
			className = controller.getPojo();	
		}			
		return className;
	}
	
	private ValueExpression getParentBinding( FaceletContext ctx, ValueExpression ve ) {
		String parentExpression = ve.getExpressionString();
		int pos = parentExpression.lastIndexOf('.');
		if (pos != -1) {
			String expression = parentExpression.substring(0, pos) + "}";
			return ctx.getExpressionFactory().createValueExpression(ctx,expression, Object.class);
		}
		return null;
	}		
	
	private ValueExpression getPropertyExpression( FaceletContext ctx, HtmlLookupInputText text ) {
		ValueExpression ve = text.getValueExpression("value");
		String propertyClassName = getPropertyClassName(ctx, text);
		while (ve != null) {
			String type = ve.getType(ctx).getName();
			if (type.equals(propertyClassName)) {
				return ve;
			} 
			ve = getParentBinding(ctx, ve);
		}
		return null;
	}	
	
	private Map<String, ValueExpression> calculateJoinBindings( FaceletContext ctx, HtmlLookupInputText text ) {
		Map<String, ValueExpression> joinBindingsMap = new HashMap<String, ValueExpression>();
		DAOConstantsResolver resolver = new DAOConstantsResolver();
		String expression = text.getProperty().getExpressionString();
		ExpressionFactory factory = ctx.getExpressionFactory();
		BasicController controller = text.getLookup().getController();
		for (AliasEntry entry : resolver.getIdentifierAliasEntryList(controller.getPojo())) {
			String value = FaceletUtil.appendExpression(expression, entry.getAccessPath());
			joinBindingsMap.put(entry.getAlias(), factory.createValueExpression(ctx, value, Object.class));
		}
		return joinBindingsMap;
	}
	
	/**
	 * Sets the attributes.
	 * 
	 * @param instance the instance
	 * @param ctx the ctx
	 */
	@Override
	protected void setAttributes(FaceletContext ctx, Object instance) {
		super.setAttributes(ctx, instance);
		HtmlLookupInputText text = (HtmlLookupInputText) instance;
		setValueChangeNotifier(ctx, text);
		setLookupChangeListener(ctx, text);
		TagAttribute propertyTag = getAttribute(PROPERTY);
		if (propertyTag == null) {
			ValueExpression ve = getPropertyExpression(ctx, text);
			text.setProperty(ve);
		}		
	}

	@Override
	protected void onComponentPopulated(FaceletContext ctx, UIComponent c,
			UIComponent parent) {
		HtmlLookupInputText text = (HtmlLookupInputText) c;		
		Map<String, ValueExpression> joinBindingsMap = text.getJoinBindingsMap(); 
		if (joinBindingsMap.isEmpty()) {
			joinBindingsMap = calculateJoinBindings(ctx, text);
			text.setJoinBindingsMap(joinBindingsMap);
		}
	}
	
}