package com.code.aon.faces.component.richfaces.confirmButton;

import java.io.IOException;
import java.net.URL;

import javax.el.ELException;
import javax.el.ValueExpression;
import javax.el.VariableMapper;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;

import com.code.aon.faces.component.myfaces.UIComponentTagUtils;
import com.code.aon.faces.component.richfaces.AonAjaxCommandHandler;
import com.code.aon.faces.component.richfaces.IRichFacesTags;
import com.code.aon.faces.component.util.FaceletUtil;
import com.sun.facelets.FaceletContext;
import com.sun.facelets.el.VariableMapperWrapper;
import com.sun.facelets.tag.TagAttribute;
import com.sun.facelets.tag.jsf.ComponentConfig;
import com.sun.facelets.tag.jsf.ComponentSupport;

/**
 * The Class ConfirmButtonHandler.
 * 
 * @author atellitu
 */
public class ConfirmButtonHandler extends AonAjaxCommandHandler implements IRichFacesTags {

	private static final String CONFIRM_ID = "confirmId";

    private static final String COMPONENT_TYPE = "com.code.aon.faces.HtmlConfirmButton";
	
	private static final String CONFIRM_SHOW_WINDOW = "confirmShowWindow";
	
	private static final String CONFIRM_ACTION = "confirmAction";

	private static final String CONFIRM_ACTION_LISTENER = "confirmActionListener";

	private static final String CANCEL_ACTION = "cancelAction";

	private static final String CANCEL_ACTION_LISTENER = "cancelActionListener";
	
	private static final String CANCEL_RE_RENDER = "cancelReRender";
	
	private static final String CONFIRM_TITLE = "confirmTitle";

	private static final String CONFIRM_MESSAGE = "confirmMessage";

	private static final String IMMEDIATE = "immediate";
	
	private static final String CONFIRM_RE_RENDER = "confirmReRender";
	
	private static final String TEMPLATE_PATH = "com/code/aon/faces/component/richfaces/confirmButton/";

	private static final String TEMPLATE = TEMPLATE_PATH + "template.xhtml";
	
	private static final String INNER_TEMPLATE = TEMPLATE_PATH + "innerTemplate.xhtml";

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
	protected void setAttributes(FaceletContext ctx, Object instance) {
		super.setAttributes(ctx, instance);
		String id = getModalPanelId(ctx) + "ReRender";
		UIComponentTagUtils.setStringProperty(ctx.getFacesContext(), (UIComponent)instance, RERENDER, id);
	}
	
	private String getModalPanelId(FaceletContext ctx) {
		return "cb_" + getId(ctx);
	}

	@Override
	protected void onComponentCreated(FaceletContext ctx, UIComponent c,
			UIComponent parent) {
		addConfirmButtonState( ctx, c );		
		insertInnerTemplate(ctx, c);
	}

	private void addConfirmButtonState( FaceletContext ctx, UIComponent component ) {
		UIViewRoot root = ComponentSupport.getViewRoot(ctx, component);
		String stateKey = getStateKey(component);
		root.getAttributes().put( stateKey, Boolean.FALSE );
	}
	
	private String getStateKey( UIComponent component ) {
		return COMPONENT_TYPE + "." + component.getId() + ".showWindow";
	}

	private ValueExpression getStateExpression( FaceletContext ctx, UIComponent component ) {
		String expr = "#{view.attributes['" + getStateKey(component) + "']}";
		return ctx.getExpressionFactory().createValueExpression( ctx, expr, Object.class );
	}

	private ValueExpression getButtonClickExpression( FaceletContext ctx, String id ) {
		String expr = "#{rich:element('" + id + "')}.click();";
		return FaceletUtil.getValueExpression(ctx, expr, Object.class);
	}	
	
	private void insertInnerTemplate(FaceletContext ctx, UIComponent component) {
		VariableMapper newMapper = new VariableMapperWrapper(ctx.getVariableMapper());
		newMapper.setVariable(CONFIRM_SHOW_WINDOW, getStateExpression(ctx, component));
		FaceletUtil.insertTemplate(ctx, tag, component, FaceletUtil.getTemplate(INNER_TEMPLATE), newMapper);
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

	private void addAttribues( FaceletContext ctx, UIComponent component ) {
		VariableMapper mapper = ctx.getVariableMapper();
		TagAttribute tagImmediate = getAttribute(IMMEDIATE);
		if (tagImmediate != null) {
			mapper.setVariable(IMMEDIATE, getValueExpression(ctx,
					tagImmediate));
		} else {
			mapper.setVariable(IMMEDIATE, ctx.getExpressionFactory()
					.createValueExpression(ctx, "false", Boolean.class));
		}
		String panelId = getModalPanelId(ctx);
		ValueExpression id = ctx.getExpressionFactory().createValueExpression(
				ctx, panelId, String.class);
		mapper.setVariable(CONFIRM_ID, id);
		mapper.setVariable(CONFIRM_SHOW_WINDOW, getStateExpression(ctx, component));
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
		ValueExpression cancelAction = getMethodExpression(ctx, CANCEL_ACTION,
				String.class, FaceletUtil.ACTION_SIG);
		if (cancelAction == null) {
			cancelAction = FaceletUtil.getMethodEmptyExpression(ctx, CANCEL_ACTION,
					String.class, FaceletUtil.ACTION_SIG);
		}
		mapper.setVariable(CANCEL_ACTION, cancelAction);
		ValueExpression cl = getMethodExpression(ctx, CANCEL_ACTION_LISTENER,
				null, FaceletUtil.ACTION_LISTENER_SIG);
		if (cl != null) {
			mapper.setVariable(CANCEL_ACTION_LISTENER, cl);
		}
		TagAttribute reRender = getAttribute(RERENDER);
		if (reRender != null) {
			mapper.setVariable(CONFIRM_RE_RENDER, getValueExpression(ctx, reRender));
		}
		TagAttribute cancelReRender = getAttribute(CANCEL_RE_RENDER);
		if (cancelReRender != null) {
			mapper.setVariable(CANCEL_RE_RENDER, getValueExpression(ctx, cancelReRender));
		}
	}

	@Override
	protected void applyNextHandler(FaceletContext ctx, UIComponent component) 
		throws IOException, FacesException, ELException {
		super.applyNextHandler(ctx, component);	
		if ( component.isRendered() ) {
			URL path = FaceletUtil.getTemplate(TEMPLATE);
			VariableMapper orig = ctx.getVariableMapper();
			ctx.setVariableMapper(new VariableMapperWrapper(orig));
			try {
				addAttribues(ctx, component);
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
	
}