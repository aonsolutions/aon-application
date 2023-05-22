package com.code.aon.faces.component.richfaces.lookup.suggestText;

import java.net.URL;

import jakarta.el.ValueExpression;
import jakarta.el.VariableMapper;
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
	
	private static final String WIDTH_DEFAULT = "300";
	
	private static final String HEIGHT_DEFAULT = "150";
	
	private static final String FRECUENCY_DEFAULT = "0.6";
	
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
	
	private void addAttribue( FaceletContext ctx, VariableMapper mapper, String name, String defaultValue, Class<?> _class ) {
		ValueExpression ve = null;
		TagAttribute tagAttribute = getAttribute(name);
		if (tagAttribute != null) {
			ve = tagAttribute.getValueExpression(ctx, Object.class);
		} else {
			ve = FaceletUtil.getValueExpression(ctx, defaultValue, _class);
		}
		mapper.setVariable(PREFFIX + name, ve);
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
		TagAttribute suggestAlias = getAttribute(SUGGEST_ALIAS);
		if (suggestAlias != null) {
			mapper.setVariable(PREFFIX + SUGGEST_ALIAS, suggestAlias.getValueExpression(ctx, Object.class));
		}
		TagAttribute matchBeginOnly = getAttribute(MATCH_BEGIN_ONLY);
		if (matchBeginOnly != null) {
			mapper.setVariable(PREFFIX + MATCH_BEGIN_ONLY, matchBeginOnly.getValueExpression(ctx, Object.class));
		}
		addAttribue(ctx, mapper, WIDTH, WIDTH_DEFAULT, String.class);		
		addAttribue(ctx, mapper, HEIGHT, HEIGHT_DEFAULT, String.class);
		addAttribue(ctx, mapper, FREQUENCY, FRECUENCY_DEFAULT, Double.class);
		addAttribue(ctx, mapper, MIN_CHARS, MIN_CHARS_DEFAULT, Integer.class);
		TagAttribute focus = getAttribute(FOCUS);
		if ( focus != null ) {
			mapper.setVariable(PREFFIX + FOCUS, focus.getValueExpression(ctx, Object.class));				
		}
		ValueExpression action = FaceletUtil.getMethodExpression(ctx,
				getAttribute(LOOKUP_ACTION),String.class, FaceletUtil.ACTION_SIG);
		if (action == null) {
			action = FaceletUtil.getMethodEmptyExpression(ctx, LOOKUP_ACTION,
					String.class, FaceletUtil.ACTION_SIG);
		}		
		mapper.setVariable(PREFFIX + LOOKUP_ACTION, action);
		TagAttribute controllerListener = getAttribute(CONTROLLER_LISTENER);
		if (controllerListener != null) {
			ValueExpression ve = controllerListener.getValueExpression(ctx, Object.class);
			mapper.setVariable(PREFFIX + CONTROLLER_LISTENER, ve);
		}				
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