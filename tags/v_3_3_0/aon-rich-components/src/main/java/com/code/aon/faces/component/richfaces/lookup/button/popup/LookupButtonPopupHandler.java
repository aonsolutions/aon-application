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

	private static final String LOOKUP_MODAL_PANEL_MAP = "com.code.aon.faces.LookupButtonPopup.map";
	
	private static final String LOOKUP_ID = "lookupId";
	
	private static final String TEMPLATE_PATH = "com/code/aon/faces/component/richfaces/lookup/";

	private static final String TEMPLATE = TEMPLATE_PATH + "panelPopup.xhtml";

	private TagAttribute lookup;

	/**
	 * The Constructor.
	 * 
	 * @param config
	 *            the config
	 */
	public LookupButtonPopupHandler(TagConfig config) {
		super(config);
		lookup = getRequiredAttribute(LOOKUP);
	}

	private boolean isInsertTemplate(FaceletContext ctx, UIComponent parent) {
		boolean insert = true;
		UIViewRoot root = ComponentSupport.getViewRoot(ctx, parent);
		Map map = (Map) root.getAttributes().get(LOOKUP_MODAL_PANEL_MAP);
		if (map == null) {
			map = new HashMap();
			root.getAttributes().put(LOOKUP_MODAL_PANEL_MAP, map);
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
		String panelId = LookupButtonHandler.getModalPanelId(ctx, lookup);
		ValueExpression id = ctx.getExpressionFactory().createValueExpression(
				ctx, panelId, String.class);
		newMapper.setVariable(LOOKUP_ID, id);
		FaceletUtil.insertTemplate(ctx, tag, component, FaceletUtil.getTemplate(TEMPLATE), newMapper);
	}
	

	@Override
	public void apply(FaceletContext ctx, UIComponent parent) {
		if ( isInsertTemplate(ctx, parent) ) {
			insertTemplate( ctx, parent );
		}
	}

}