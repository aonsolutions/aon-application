package com.code.aon.faces.component.icefaces.lookup.button.popup;

import java.util.HashMap;
import java.util.Map;

import javax.el.ValueExpression;
import javax.el.VariableMapper;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;

import com.code.aon.faces.component.icefaces.lookup.ILookupTags;
import com.sun.facelets.FaceletContext;
import com.sun.facelets.el.VariableMapperWrapper;
import com.sun.facelets.tag.TagAttribute;
import com.sun.facelets.tag.TagConfig;
import com.sun.facelets.tag.TagException;
import com.sun.facelets.tag.TagHandler;
import com.sun.facelets.tag.jsf.ComponentSupport;

/**
 * The Class TabbedPaneComponentHandler.
 * 
 * @author atellitu
 */
public class LookupButtonPopupHandler extends TagHandler implements ILookupTags {

	private static final String COMPONENT_TYPE = "com.code.aon.faces.LookupButtonPopup";

	private static final String TEMPLATE = "template";

	private static final String DEFAULT_TEMPLATE = "/facelet/lookup/panelPopup.xhtml";

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

	private String getPath(FaceletContext ctx) {
		TagAttribute templateTag = getAttribute(TEMPLATE);
		return (templateTag != null) ? templateTag.getValue(ctx)
				: DEFAULT_TEMPLATE;
	}

	private void insertTemplate(FaceletContext ctx, UIComponent parent) {
		String path = getPath(ctx);
		VariableMapper orig = ctx.getVariableMapper();
		ctx.setVariableMapper(new VariableMapperWrapper(orig));
		try {
			ValueExpression ve = lookup.getValueExpression(ctx, Object.class);
			ctx.getVariableMapper().setVariable(LOOKUP, ve);
			ctx.includeFacelet(parent, path);
		} catch (Throwable th) {
			throw new TagException(this.tag, "Error inserting template '"
					+ path + "': " + th.getMessage());
		} finally {
			ctx.setVariableMapper(orig);
		}
	}

	@Override
	public void apply(FaceletContext ctx, UIComponent parent) {
		if ( isInsertTemplate(ctx, parent) ) {
			insertTemplate( ctx, parent );
		}
	}

}