package com.code.aon.faces.component.richfaces.lookup.suggestText;

import java.net.URL;

import javax.el.ValueExpression;
import javax.el.VariableMapper;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;

import com.code.aon.faces.component.richfaces.lookup.HtmlLookupBasicInput;
import com.code.aon.faces.component.richfaces.lookup.LookupBasicInputHandler;
import com.code.aon.faces.component.util.FaceletUtil;
import com.sun.facelets.FaceletContext;
import com.sun.facelets.el.VariableMapperWrapper;
import com.sun.facelets.tag.TagAttribute;
import com.sun.facelets.tag.jsf.ComponentConfig;

/**
 * The Class TabbedPaneComponentHandler.
 * 
 * @author atellitu
 */
public class LookupSuggestTextHandler extends LookupBasicInputHandler {

	private static final String TEMPLATE_PATH = "com/code/aon/faces/component/richfaces/lookup/";

	private static final String TEMPLATE = TEMPLATE_PATH + "suggestTemplate.xhtml";
	
	private static final String PREFFIX = "aon_lst_";
	
	private static final String MIN_CHARS_DEFAULT = "3";
	
	private TagAttribute lookup;
	
   	/**
	 * The Constructor.
	 * 
	 * @param config the config
	 */
	public LookupSuggestTextHandler(ComponentConfig config) {
		super( config );
		lookup = getRequiredAttribute(LOOKUP);
	}
	
	private void addAttribues( FaceletContext ctx, UIComponent component ) {
		VariableMapper mapper = ctx.getVariableMapper();
		mapper.setVariable(LOOKUP, lookup.getValueExpression(ctx, Object.class));
		TagAttribute selectReRender = getAttribute(SELECT_RE_RENDER);
		if (selectReRender != null) {
			ValueExpression ve = selectReRender.getValueExpression(ctx, Object.class);
			mapper.setVariable(PREFFIX + SELECT_RE_RENDER, ve);
		}		
		TagAttribute columns = getAttribute(COLUMN_EXPRESSION);
		if (columns != null) {
			mapper.setVariable(PREFFIX + COLUMN_EXPRESSION, columns.getValueExpression(ctx, Object.class));
		}
		ValueExpression minCharsVE = null;
		TagAttribute minChars = getAttribute(MIN_CHARS);
		if (minChars != null) {
			minCharsVE = minChars.getValueExpression(ctx, Object.class);
		} else {
			minCharsVE = FaceletUtil.getValueExpression(ctx, MIN_CHARS_DEFAULT, Integer.class);
		}
		mapper.setVariable(PREFFIX + MIN_CHARS, minCharsVE);
	}
	
	@Override
	protected void updateAttributes(FaceletContext ctx, HtmlLookupBasicInput input) {
		super.updateAttributes(ctx, input);
		URL path = FaceletUtil.getTemplate(TEMPLATE);
		VariableMapper orig = ctx.getVariableMapper();
		ctx.setVariableMapper(new VariableMapperWrapper(orig));
		try {
			addAttribues(ctx, input);
			ctx.includeFacelet(input, path );
		} catch (Exception e) {
			throw new FacesException("UIInclude component "
					+ input.getClientId(ctx.getFacesContext())
					+ " could't include page with path " + path, e);
		} finally {
			ctx.setVariableMapper(orig);
		}			
	}

}