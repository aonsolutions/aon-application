package com.code.aon.faces.component.icefaces.lookup.button.popup;

import java.util.HashMap;
import java.util.Map;

import javax.el.ExpressionFactory;
import javax.el.ValueExpression;
import javax.el.VariableMapper;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;

import com.code.aon.faces.component.AonComponentHandler;
import com.code.aon.faces.component.icefaces.lookup.ILookupTags;
import com.sun.facelets.FaceletContext;
import com.sun.facelets.el.VariableMapperWrapper;
import com.sun.facelets.tag.TagAttribute;
import com.sun.facelets.tag.TagException;
import com.sun.facelets.tag.jsf.ComponentConfig;
import com.sun.facelets.tag.jsf.ComponentSupport;

/**
 * The Class TabbedPaneComponentHandler.
 * 
 * @author atellitu
 */
public class LookupButtonPopupHandler extends AonComponentHandler implements ILookupTags {

	private static final String WINDOW_TITLE = "windowTitle";

	private static final String TEMPLATE = "template";

	private static final String DEFAULT_TEMPLATE = "/facelet/lookup/panelPopup.xhtml";

	private String windowTitle;
	
   	private String lookup;
	/**
	 * The Constructor.
	 * 
	 * @param config
	 *            the config
	 */
	public LookupButtonPopupHandler(ComponentConfig config) {
		super(config);
		lookup = getRequiredAttribute(LOOKUP).getValue();
	}


	private boolean isInsertTemplate(FaceletContext ctx, LookupButtonPopup component) {
		boolean insert = true;
		UIViewRoot root = ComponentSupport.getViewRoot(ctx, component);
		Map map = (Map) root.getAttributes().get(LookupButtonPopup.COMPONENT_TYPE);
		if (map == null) {
			map = new HashMap();
			root.getAttributes().put(LookupButtonPopup.COMPONENT_TYPE, map);
		} else {
			String value = (String) map.get(lookup);
			insert = (value == null) || (value.equals(component.getId()));
		}
		if (insert) {
			map.put(lookup, component.getId());
		}
		return insert;
	}

	private String getPath(FaceletContext ctx) {
		TagAttribute templateTag = getAttribute(TEMPLATE);
		return (templateTag != null) ? templateTag.getValue(ctx) : DEFAULT_TEMPLATE;
	}

	private void addParameter(FaceletContext ctx, String name, String value) {
		ExpressionFactory f = ctx.getExpressionFactory();
		ValueExpression valueVE = f.createValueExpression(ctx, value, Object.class);
		ctx.getVariableMapper().setVariable(name, valueVE);
	}

	private void insertTemplate(FaceletContext ctx, UIComponent parent, LookupButtonPopup component) {
		String path = getPath(ctx);
		VariableMapper orig = ctx.getVariableMapper();
		ctx.setVariableMapper(new VariableMapperWrapper(orig));
		try {
			addParameter(ctx, LOOKUP, lookup );
			ctx.includeFacelet(parent, path);
		} catch (Throwable th) {
			throw new TagException(this.tag, "Error inserting template '" + path + "': " + th.getMessage());
		} finally {
			ctx.setVariableMapper(orig);
		}
	}

	@Override
	protected void onComponentPopulated(FaceletContext ctx, UIComponent c, UIComponent parent) {
		LookupButtonPopup component = (LookupButtonPopup) c;
		if (isInsertTemplate(ctx, component)) {
			insertTemplate(ctx, parent, component);
		}
	}

}