package com.code.aon.faces.component.richfaces.lookup.inputText;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.el.ExpressionFactory;
import javax.el.ValueExpression;
import javax.faces.component.UIComponent;

import com.code.aon.common.dao.AliasEntry;
import com.code.aon.common.dao.DAOConstantsResolver;
import com.code.aon.faces.component.richfaces.lookup.LookupBasicInputHandler;
import com.code.aon.faces.component.util.BasicComponentConfig;
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
public class LookupInputTextHandler extends LookupBasicInputHandler {

    private static final String LOOKUP_CHANGED = "lookupChanged";
	
   	/**
	 * The Constructor.
	 * 
	 * @param config the config
	 */
	public LookupInputTextHandler(ComponentConfig config) {
		super( config );
		setAjaxNeeded( true );		
	}
	
	@Override
	protected void initAjaxSupport(List<TagAttribute> attributes) {
		super.initAjaxSupport(attributes);
    	TagAttribute lookupTag = getAttribute(LOOKUP);
    	if ( lookupTag != null ) {
        	String value = FaceletUtil.appendExpression( lookupTag.getValue(), LOOKUP_CHANGED );
        	attributes.add( BasicComponentConfig.newAttribute(tag, ACTION_LISTENER, value) );
    	}
    	TagAttribute actionTag = getAttribute(LOOKUP_ACTION);
    	if ( actionTag != null ) {
			String value = actionTag.getValue();
			attributes.add( BasicComponentConfig.newAttribute(tag, ACTION, value) );					    		
    	}
	}

	private Map<String, ValueExpression> calculateJoinBindings( FaceletContext ctx, HtmlLookupInputText text ) {
		Map<String, ValueExpression> joinBindingsMap = new HashMap<String, ValueExpression>();
		if ( text.getProperty() != null ) {
			DAOConstantsResolver resolver = new DAOConstantsResolver();
			String expression = text.getProperty().getExpressionString();
			ExpressionFactory factory = ctx.getExpressionFactory();
			BasicController controller = text.getLookup().getController();
			for (AliasEntry entry : resolver.getIdentifierAliasEntryList(controller.getPojo())) {
				String value = FaceletUtil.appendExpression(expression, entry.getAccessPath());
				joinBindingsMap.put(entry.getAlias(), factory.createValueExpression(ctx, value, Object.class));
			}
		}
		return joinBindingsMap;
	}

	@Override
	protected void onComponentPopulated(FaceletContext ctx, UIComponent c, UIComponent parent) {
		HtmlLookupInputText text = (HtmlLookupInputText) c;
		Map<String, ValueExpression> joinBindingsMap = text.getJoinBindingsMap(); 
		if (joinBindingsMap.isEmpty()) {
			joinBindingsMap = calculateJoinBindings(ctx, text);
			text.setJoinBindingsMap(joinBindingsMap);
		}
	}
	
}