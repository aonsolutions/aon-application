package com.code.aon.faces.component.richfaces.spin;

import javax.el.ValueExpression;
import javax.el.VariableMapper;
import javax.faces.component.UIComponent;

import com.code.aon.faces.component.util.FaceletUtil;
import com.code.aon.faces.component.util.HTML;
import com.sun.facelets.FaceletContext;
import com.sun.facelets.el.VariableMapperWrapper;
import com.sun.facelets.tag.TagAttribute;
import com.sun.facelets.tag.TagConfig;
import com.sun.facelets.tag.TagHandler;

/**
 * The Class TabbedPaneComponentHandler.
 * 
 * @author atellitu
 */
public class SpinHandler extends TagHandler {

	private static final String TEMPLATE_PATH = "com/code/aon/faces/component/richfaces/spin/";

	private static final String TEMPLATE = TEMPLATE_PATH + "spin.xhtml";
	
	private static final String CONTROLLER = "controller";
	
	private static final String SPIN_ID = "spinId";
	
	private static final String RENDERED = "rendered";
	
   	private static final String ACTION = "action";
   	
   	private TagAttribute controllerTag;

	/**
	 * The Constructor.
	 * 
	 * @param config
	 *            the config
	 */
	public SpinHandler(TagConfig config) {
		super(config);
		controllerTag = getRequiredAttribute(CONTROLLER);
	}

	private ValueExpression getMethodExpression(FaceletContext ctx, String name,
			Class type, Class[] paramTypes) {
		return FaceletUtil.getMethodExpression(ctx, getAttribute(name), type,
				paramTypes);
	}
	
	private void insertTemplate(FaceletContext ctx, UIComponent component) {
		VariableMapper newMapper = new VariableMapperWrapper(ctx.getVariableMapper());
		newMapper.setVariable(CONTROLLER, controllerTag.getValueExpression(ctx, Object.class));
		ValueExpression action = getMethodExpression(ctx, ACTION, String.class, FaceletUtil.ACTION_SIG);
		if (action == null) {
			action = FaceletUtil.getMethodEmptyExpression(ctx, ACTION,
					String.class, FaceletUtil.ACTION_SIG);
		}
		newMapper.setVariable(ACTION, action);
		ValueExpression id = null;
		TagAttribute idTag = getAttribute(HTML.ID_ATTR);
		if ( idTag != null ) {
			id = idTag.getValueExpression(ctx, String.class);
		} else {
			String idValue = "spin" + this.tagId;
			id = ctx.getExpressionFactory().createValueExpression(ctx, idValue, String.class);
		}
		newMapper.setVariable(SPIN_ID, id);
		FaceletUtil.insertTemplate(ctx, tag, component, FaceletUtil.getTemplate(TEMPLATE), newMapper);
	}

	private boolean isRendered( FaceletContext ctx ) {
		boolean rendered = true;
		TagAttribute renderedTag = getAttribute(RENDERED);
		if ( renderedTag != null ) {
			rendered = renderedTag.getBoolean(ctx);
		}
		return rendered;
	}
	
	@Override
	public void apply(FaceletContext ctx, UIComponent parent) {
		if ( isRendered(ctx) ) {
			insertTemplate( ctx, parent );
		}
	}

}