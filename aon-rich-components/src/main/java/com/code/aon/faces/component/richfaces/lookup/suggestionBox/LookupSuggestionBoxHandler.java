package com.code.aon.faces.component.richfaces.lookup.suggestionBox;

import jakarta.el.MethodExpression;

import org.richfaces.component.html.HtmlSuggestionBox;
import org.richfaces.taglib.SuggestionBoxTagHandler;

import com.code.aon.faces.component.richfaces.lookup.ILookupConstants;
import com.code.aon.faces.component.util.MethodExpressionAdapter;
import com.sun.facelets.FaceletContext;
import com.sun.facelets.tag.TagAttribute;
import com.sun.facelets.tag.jsf.ComponentConfig;

/**
 * The Class TabbedPaneComponentHandler.
 * 
 * @author atellitu
 */
public class LookupSuggestionBoxHandler extends SuggestionBoxTagHandler {
	
	private static final String SUGGESTION_ACTION = "suggestionAction";
	
	private final static Class[] SIGNATURE = new Class[]{Object.class, Object[].class};
	
   	/**
	 * The Constructor.
	 * 
	 * @param config the config
	 */
	public LookupSuggestionBoxHandler(ComponentConfig config) {
		super( config );
	}

	@Override
	protected void setAttributes( FaceletContext ctx, Object instance ) {
		super.setAttributes(ctx, instance);
		HtmlSuggestionBox sb = (HtmlSuggestionBox) instance;
		MethodExpression me = sb.getSuggestionAction();
		if ( me != null ) {
			TagAttribute ta = getAttribute(SUGGESTION_ACTION);
			MethodExpression newME = ta.getMethodExpression(ctx, null, SIGNATURE);
			Object[] properties = new Object[3];
			TagAttribute suggestAlias = getAttribute(ILookupConstants.SUGGEST_ALIAS);
			if (suggestAlias != null) {
				properties[0] =  suggestAlias.getObject(ctx, String.class);
			}
			Boolean matchBeginOnly = Boolean.FALSE;
			TagAttribute matchBeginOnlyTag = getAttribute(ILookupConstants.MATCH_BEGIN_ONLY);
			if (matchBeginOnlyTag != null) {
				matchBeginOnly =  (Boolean) matchBeginOnlyTag.getObject(ctx, Boolean.class);
			}			
			properties[1] = matchBeginOnly != null ? matchBeginOnly : Boolean.FALSE;
			TagAttribute controllerListener = getAttribute(ILookupConstants.CONTROLLER_LISTENER);
			if (controllerListener != null) {
				properties[2] = controllerListener.getValueExpression(ctx, Object.class);
			}
			MethodExpressionAdapter mea = new MethodExpressionAdapter(me, newME, properties);
			sb.setSuggestionAction(mea);
		}
	}
	
}