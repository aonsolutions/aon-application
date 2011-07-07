package com.code.aon.faces.component.richfaces.suggestionBox;

import javax.el.MethodExpression;
import javax.faces.component.UIComponent;

import org.richfaces.component.html.HtmlSuggestionBox;
import org.richfaces.taglib.SuggestionBoxTagHandler;

import com.code.aon.faces.component.util.MethodExpressionAdapter;
import com.sun.facelets.FaceletContext;
import com.sun.facelets.tag.TagAttribute;
import com.sun.facelets.tag.jsf.ComponentConfig;

/**
 * The Class TabbedPaneComponentHandler.
 * 
 * @author atellitu
 */
public class SuggestionBoxHandler extends SuggestionBoxTagHandler {
	
	private static final String SUGGESTION_ACTION = "suggestionAction";
	
	private final static Class[] SIGNATURE = new Class[]{Object.class, UIComponent.class};
	
   	/**
	 * The Constructor.
	 * 
	 * @param config the config
	 */
	public SuggestionBoxHandler(ComponentConfig config) {
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
			MethodExpressionAdapter mea = new MethodExpressionAdapter(me, newME, sb);
			sb.setSuggestionAction(mea);
		}
	}
	
}