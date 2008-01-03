package com.code.aon.faces.component.richfaces.lookup.button.popup;

import java.util.HashMap;
import java.util.Map;

import javax.el.ValueExpression;
import javax.el.VariableMapper;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;

import com.code.aon.faces.component.richfaces.IRichFacesTags;
import com.code.aon.faces.component.richfaces.lookup.ILookupTags;
import com.code.aon.faces.component.richfaces.lookup.button.LookupButtonHandler;
import com.code.aon.faces.component.util.FaceletUtil;
import com.sun.facelets.FaceletContext;
import com.sun.facelets.el.VariableMapperWrapper;
import com.sun.facelets.tag.TagAttribute;
import com.sun.facelets.tag.TagConfig;
import com.sun.facelets.tag.TagHandler;
import com.sun.facelets.tag.jsf.ComponentSupport;

/**
 * The Class TabbedPaneComponentHandler.
 * 
 * @author atellitu
 */
public class LookupButtonPopupHandler extends TagHandler implements ILookupTags, IRichFacesTags {

	private static final String COMPONENT_TYPE = "com.code.aon.faces.LookupButtonPopup";
	
	private static final String LOOKUP_ID = "lookupId";
	
   	private static final String WINDOW_TITLE = "windowTitle";
   	
   	private static final String LOOKUP_RE_RENDER = "lookupReRender";

	private static final String TEMPLATE_PATH = "com/code/aon/faces/component/richfaces/lookup/";

	private static final String TEMPLATE = TEMPLATE_PATH + "panelPopup.xhtml";

	private TagAttribute lookup;
	
   	private TagAttribute windowTitle;	

	/**
	 * The Constructor.
	 * 
	 * @param config
	 *            the config
	 */
	public LookupButtonPopupHandler(TagConfig config) {
		super(config);
		lookup = getRequiredAttribute(LOOKUP);
		windowTitle = getRequiredAttribute(WINDOW_TITLE);
	}

	private boolean isInsertTemplate(FaceletContext ctx, UIComponent parent) {
		boolean insert = true;
		UIViewRoot root = ComponentSupport.getViewRoot(ctx, parent);
		Map map = (Map) root.getAttributes().get(COMPONENT_TYPE);
		if (map == null) {
			map = new HashMap();
			root.getAttributes().put(COMPONENT_TYPE, map);
		} else {
			String value = (String) map.get(lookup);
			insert = (value == null) || (value.equals(this.tagId));
		}
		if (insert) {
			map.put(lookup, this.tagId);
		}
		return insert;
	}

	private void insertTemplate(FaceletContext ctx, UIComponent component) {
		VariableMapper newMapper = new VariableMapperWrapper(ctx.getVariableMapper());
		newMapper.setVariable(LOOKUP, lookup.getValueExpression(ctx, Object.class));
		String idString = LookupButtonHandler.getModalPanelId(ctx, lookup);
		ValueExpression id = ctx.getExpressionFactory().createValueExpression(
				ctx, idString, String.class);
		newMapper.setVariable(LOOKUP_ID, id);
		newMapper.setVariable(WINDOW_TITLE, windowTitle.getValueExpression(ctx, Object.class));
		TagAttribute reRender = getAttribute(RERENDER);
		if ( reRender != null ) {
			newMapper.setVariable(LOOKUP_RE_RENDER, reRender.getValueExpression(ctx, Object.class));
		}
		FaceletUtil.insertTemplate(ctx, tag, component, FaceletUtil.getTemplate(TEMPLATE), newMapper);
	}
	

	@Override
	public void apply(FaceletContext ctx, UIComponent parent) {
		if ( isInsertTemplate(ctx, parent) ) {
			insertTemplate( ctx, parent );
		}
	}

}