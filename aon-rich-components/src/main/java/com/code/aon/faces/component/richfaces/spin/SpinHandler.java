package com.code.aon.faces.component.richfaces.spin;

import static com.code.aon.faces.component.util.HTML.STYLE_ATTR;

import jakarta.el.ValueExpression;
import jakarta.el.VariableMapper;
import javax.faces.component.UIComponent;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.faces.component.richfaces.IRichFacesTags;
import com.code.aon.faces.component.util.FaceletUtil;
import com.code.aon.faces.component.util.HTML;
import com.code.aon.ui.form.IController;
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
public class SpinHandler extends TagHandler implements IRichFacesTags {

	private static final String TEMPLATE_PATH = "com/code/aon/faces/component/richfaces/spin/";

	private static final String TEMPLATE = TEMPLATE_PATH + "spin.xhtml";
	
	private static final String SPIN_ID = "spinId";
	
	private static final String PREFFIX = "aon_spin_";
   	
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
		ValueExpression disableHotKeys = FaceletUtil.getBooleanValueExpression(ctx, getAttribute(DISABLE_HOT_KEYS));
		newMapper.setVariable(DISABLE_HOT_KEYS, disableHotKeys);		
		TagAttribute style = getAttribute(STYLE_ATTR);
		if (style != null) {
			newMapper.setVariable(PREFFIX + STYLE_ATTR, style.getValueExpression(ctx, String.class));
		}		
		FaceletUtil.insertTemplate(ctx, tag, component, FaceletUtil.getTemplate(TEMPLATE), newMapper);
	}
	
	private boolean isRendered(FaceletContext ctx, UIComponent parent) {
		boolean rendered = false;
		if ( FaceletUtil.isRendered(ctx, tag) && parent.isRendered() ) {
			IController controller = (IController) controllerTag.getObject(ctx);
			try {
				rendered = (controller.getModel().getRowCount() > 1);
			} catch (ManagerBeanException e) {
				rendered = true;
			}
		}
		return rendered;
	}
	
	@Override
	public void apply(FaceletContext ctx, UIComponent parent) {
		if ( isRendered(ctx, parent) ) {
			insertTemplate( ctx, parent );
		}
	}

}