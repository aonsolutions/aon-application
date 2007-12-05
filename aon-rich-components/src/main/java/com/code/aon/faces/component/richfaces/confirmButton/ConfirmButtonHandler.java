package com.code.aon.faces.component.richfaces.confirmButton;

import java.net.URL;

import javax.el.ValueExpression;
import javax.el.VariableMapper;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;

import com.code.aon.faces.component.AonComponentHandler;
import com.code.aon.faces.component.myfaces.UIComponentTagUtils;
import com.code.aon.faces.component.util.FaceletUtil;
import com.sun.facelets.FaceletContext;
import com.sun.facelets.el.VariableMapperWrapper;
import com.sun.facelets.tag.MetaRuleset;
import com.sun.facelets.tag.TagAttribute;
import com.sun.facelets.tag.jsf.ComponentConfig;

/**
 * The Class ConfirmButtonHandler.
 * 
 * @author atellitu
 */
public class ConfirmButtonHandler extends AonComponentHandler {

	private static final String CONFIRM_ID = "confirmId";

	private static final String CONFIRM_ACTION = "confirmAction";

	private static final String CONFIRM_ACTION_LISTENER = "confirmActionListener";

	private static final String CONFIRM_TITLE = "confirmTitle";

	private static final String CONFIRM_MESSAGE = "confirmMessage";

	private static final String IMMEDIATE = "immediate";

	private static final String TEMPLATE_PATH = "com/code/aon/faces/component/richfaces/confirmButton/";

	private static final String TEMPLATE = TEMPLATE_PATH + "template.xhtml";

	private TagAttribute titleTag;

	private TagAttribute messageTag;

	/**
	 * The Constructor.
	 * 
	 * @param config
	 *            the config
	 */
	public ConfirmButtonHandler(ComponentConfig config) {
		super(config);
		this.titleTag = getRequiredAttribute(CONFIRM_TITLE);
		this.messageTag = getRequiredAttribute(CONFIRM_MESSAGE);
	}

	@Override
	protected MetaRuleset createMetaRuleset(Class type) {
		MetaRuleset mrs = super.createMetaRuleset(type);
		mrs.ignore(CONFIRM_ACTION).ignore(CONFIRM_ACTION_LISTENER);
		mrs.ignore(CONFIRM_TITLE).ignore(CONFIRM_MESSAGE);
		return mrs;
	}

	private String getPanelId(FaceletContext ctx) {
		return getId(ctx) + "ModelPanel";
	}

	private String getShowScript(FaceletContext ctx) {
		return "Richfaces.showModalPanel('" + getPanelId(ctx) + "');";
	}

	@Override
	protected void setAttributes(FaceletContext ctx, Object instance) {
		super.setAttributes(ctx, instance);
		UIComponent button = (UIComponent) instance;
		UIComponentTagUtils.setStringProperty(ctx.getFacesContext(), button,
				"onclick", getShowScript(ctx));
	}

	private ValueExpression getValueExpression(FaceletContext ctx,
			TagAttribute tag) {
		return tag.getValueExpression(ctx, Object.class);
	}

	public ValueExpression getMethodExpression(FaceletContext ctx, String name,
			Class type, Class[] paramTypes) {
		return FaceletUtil.getMethodExpression(ctx, getAttribute(name), type,
				paramTypes);
	}

	private void addAttribues( FaceletContext ctx ) {
		VariableMapper mapper = ctx.getVariableMapper();
		TagAttribute tagImmediate = getAttribute(IMMEDIATE);
		if (tagImmediate != null) {
			mapper.setVariable(IMMEDIATE, getValueExpression(ctx,
					tagImmediate));
		} else {
			mapper.setVariable(IMMEDIATE, ctx.getExpressionFactory()
					.createValueExpression(ctx, "false", Boolean.class));
		}
		ValueExpression id = ctx.getExpressionFactory().createValueExpression(
				ctx, getPanelId(ctx), String.class);
		mapper.setVariable(CONFIRM_ID, id);
		mapper.setVariable(CONFIRM_TITLE, getValueExpression(ctx, titleTag));
		mapper.setVariable(CONFIRM_MESSAGE, getValueExpression(ctx,
				messageTag));
		ValueExpression action = getMethodExpression(ctx, CONFIRM_ACTION,
				String.class, FaceletUtil.ACTION_SIG);
		if (action == null) {
			action = FaceletUtil.getMethodEmptyExpression(ctx, CONFIRM_ACTION,
					String.class, FaceletUtil.ACTION_SIG);
		}
		mapper.setVariable(CONFIRM_ACTION, action);
		ValueExpression al = getMethodExpression(ctx, CONFIRM_ACTION_LISTENER,
				null, FaceletUtil.ACTION_LISTENER_SIG);
		if (al != null) {
			mapper.setVariable(CONFIRM_ACTION_LISTENER, al);
		}
	}

	@Override
	protected void applyNextHandler(FaceletContext ctx, UIComponent component) {
		URL path = getTemplate(TEMPLATE);
		VariableMapper orig = ctx.getVariableMapper();
		ctx.setVariableMapper(new VariableMapperWrapper(orig));
		try {
			super.nextHandler.apply(ctx, component);
			addAttribues(ctx);
			ctx.includeFacelet(component, path );
		} catch (Exception e) {
			throw new FacesException("UIInclude component "
					+ component.getClientId(ctx.getFacesContext())
					+ " could't include page with path " + path, e);
		} finally {
			ctx.setVariableMapper(orig);
		}
	}
}