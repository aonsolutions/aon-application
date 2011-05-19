package com.code.aon.faces.component.richfaces.lookup.inputText;

import java.util.HashMap;
import java.util.Map;

import javax.el.ExpressionFactory;
import javax.el.ValueExpression;

import com.code.aon.common.dao.AliasEntry;
import com.code.aon.common.dao.DAOConstantsResolver;
import com.code.aon.faces.component.richfaces.lookup.HtmlLookupBasicInput;
import com.code.aon.faces.component.richfaces.lookup.LookupBasicInputHandler;
import com.code.aon.faces.component.util.FaceletUtil;
import com.code.aon.ui.form.BasicController;
import com.sun.facelets.FaceletContext;
import com.sun.facelets.tag.jsf.ComponentConfig;

/**
 * The Class TabbedPaneComponentHandler.
 * 
 * @author atellitu
 */
public class LookupInputTextHandler extends LookupBasicInputHandler {

   	/**
	 * The Constructor.
	 * 
	 * @param config the config
	 */
	public LookupInputTextHandler(ComponentConfig config) {
		super( new LookupInputTextConfig(config) );
		setAjaxNeeded( true );		
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
	protected void updateAttributes(FaceletContext ctx, HtmlLookupBasicInput input) {
		super.updateAttributes(ctx, input);
		HtmlLookupInputText text = (HtmlLookupInputText) input;
		Map<String, ValueExpression> joinBindingsMap = text.getJoinBindingsMap(); 
		if (joinBindingsMap.isEmpty()) {
			joinBindingsMap = calculateJoinBindings(ctx, text);
			text.setJoinBindingsMap(joinBindingsMap);
		}
	}
	
}