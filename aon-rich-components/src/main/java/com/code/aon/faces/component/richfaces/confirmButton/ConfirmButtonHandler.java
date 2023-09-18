package com.code.aon.faces.component.richfaces.confirmButton;

import static com.code.aon.faces.controller.IRichConstants.ATTRIBUTE_PREFFIX;

import java.io.IOException;
import java.net.URL;

import jakarta.el.ELException;
import jakarta.el.ValueExpression;
import jakarta.el.VariableMapper;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;

import org.apache.commons.lang.StringUtils;

import com.code.aon.faces.component.myfaces.UIComponentTagUtils;
import com.code.aon.faces.component.richfaces.AonAjaxCommandHandler;
import com.code.aon.faces.component.richfaces.IRichFacesTags;
import com.code.aon.faces.component.util.FaceletUtil;
import com.code.aon.faces.component.util.HTML;
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
public class ConfirmButtonHandler extends AonAjaxCommandHandler implements IRichFacesTags, HTML {

	private static final int MESSAGE_LINE_LENGTH = 80;
	
	private static final String MIN_WIDTH_1 = "360";
	
	private static final String MIN_WIDTH_2 = "460";
	
	private static final String MIN_HEIGHT_1 = "130";   
		
	private static final String MIN_HEIGHT_2 = "150";		
	
	private static final String PREFFIX = "aon_cb_";
	
	private static final String CONFIRM_SHOW_WINDOW = "showWindow";
	
	private static final String CONFIRM_ACTION = "confirmAction";

	private static final String CONFIRM_ACTION_LISTENER = "confirmActionListener";
	
	private static final String CONFIRM_ON_CLICK = "confirmOnClick";
	
	private static final String CONFIRM_ON_COMPLETE = "confirmOnComplete";

	private static final String CANCEL_ACTION = "cancelAction";

	private static final String CANCEL_ACTION_LISTENER = "cancelActionListener";
	
	private static final String CANCEL_RE_RENDER = "cancelReRender";
	
	private static final String CONFIRM_TITLE = "confirmTitle";

	private static final String CONFIRM_MESSAGE = "confirmMessage";

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
		super.onComponentCreated(ctx, c, parent);
		addConfirmButtonState( ctx, c );		
		insertInnerTemplate(ctx, c);
	}

	private void addConfirmButtonState( FaceletContext ctx, UIComponent component ) {
		UIViewRoot root = ComponentSupport.getViewRoot(ctx, component);
		String stateKey = getStateKey(component);
		root.getAttributes().put( stateKey, Boolean.FALSE );
	}
	
	private String getStateKey( UIComponent component ) {
		return ATTRIBUTE_PREFFIX + "." + component.getId() + ".showWindow";
	}

	private ValueExpression getStateExpression( FaceletContext ctx, UIComponent component ) {
		String expr = "#{view.attributes['" + getStateKey(component) + "']}";
		return ctx.getExpressionFactory().createValueExpression( ctx, expr, Object.class );
	}
	
	private void insertInnerTemplate(FaceletContext ctx, UIComponent component) {
		VariableMapper newMapper = new VariableMapperWrapper(ctx.getVariableMapper());
		newMapper.setVariable(PREFFIX + CONFIRM_SHOW_WINDOW, getStateExpression(ctx, component));
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
	
	private void setDimension( VariableMapper mapper, FaceletContext ctx ) {
		boolean bigBox = StringUtils.length(messageTag.getValue(ctx)) > MESSAGE_LINE_LENGTH;		
		ValueExpression ve = null;
		TagAttribute minWidth = getAttribute(MIN_WIDTH);
		if (minWidth != null) {
			ve = getValueExpression(ctx, minWidth);
		} else {
			ve = FaceletUtil.getValueExpression(ctx, bigBox ? MIN_WIDTH_2: MIN_WIDTH_1, Integer.class);
		}
		mapper.setVariable(PREFFIX + MIN_WIDTH, ve);
		TagAttribute minHeight = getAttribute(MIN_HEIGHT);
		if (minHeight != null) {
			ve = getValueExpression(ctx, minHeight);
		} else {
			ve = FaceletUtil.getValueExpression(ctx, bigBox ? MIN_HEIGHT_2: MIN_HEIGHT_1, Integer.class);
		}
		mapper.setVariable(PREFFIX + MIN_HEIGHT, ve);
	}

	private void addAttribues( FaceletContext ctx, UIComponent component ) {
		VariableMapper mapper = ctx.getVariableMapper();
		TagAttribute tagImmediate = getAttribute(IMMEDIATE);
		mapper.setVariable(PREFFIX + IMMEDIATE, FaceletUtil.getBooleanValueExpression(ctx, tagImmediate));
		String panelId = getModalPanelId(ctx);
		ValueExpression id = ctx.getExpressionFactory().createValueExpression(
				ctx, panelId, String.class);
		mapper.setVariable(PREFFIX + ID_ATTR, id);
		mapper.setVariable(PREFFIX + CONFIRM_SHOW_WINDOW, getStateExpression(ctx, component));
		mapper.setVariable(PREFFIX + CONFIRM_TITLE, getValueExpression(ctx, titleTag));
		mapper.setVariable(PREFFIX + CONFIRM_MESSAGE, getValueExpression(ctx, messageTag));
		ValueExpression action = getMethodExpression(ctx, CONFIRM_ACTION,
				String.class, FaceletUtil.ACTION_SIG);
		if (action == null) {
			action = FaceletUtil.getMethodEmptyExpression(ctx, CONFIRM_ACTION,
					String.class, FaceletUtil.ACTION_SIG);
		}
		mapper.setVariable(PREFFIX + CONFIRM_ACTION, action);
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
		mapper.setVariable(PREFFIX + CANCEL_ACTION, cancelAction);
		ValueExpression cl = getMethodExpression(ctx, CANCEL_ACTION_LISTENER,
				null, FaceletUtil.ACTION_LISTENER_SIG);
		if (cl != null) {
			mapper.setVariable(PREFFIX + CANCEL_ACTION_LISTENER, cl);
		}
		TagAttribute reRender = getAttribute(RERENDER);
		if (reRender != null) {
			mapper.setVariable(PREFFIX + RERENDER, getValueExpression(ctx, reRender));
		}
		TagAttribute cancelReRender = getAttribute(CANCEL_RE_RENDER);
		if (cancelReRender != null) {
			mapper.setVariable(PREFFIX + CANCEL_RE_RENDER, getValueExpression(ctx, cancelReRender));
		}
		TagAttribute onClick = getAttribute(CONFIRM_ON_CLICK);
		if (onClick != null) {
			mapper.setVariable(PREFFIX + CONFIRM_ON_CLICK, getValueExpression(ctx, onClick));
		}
		TagAttribute onComplete = getAttribute(CONFIRM_ON_COMPLETE);
		if (onComplete != null) {
			mapper.setVariable(PREFFIX + CONFIRM_ON_COMPLETE, getValueExpression(ctx, onComplete));
		}
		setDimension(mapper, ctx);
	}

	@Override
	protected void applyNextHandler(FaceletContext ctx, UIComponent component) 
		throws IOException, FacesException, ELException {
		super.applyNextHandler(ctx, component);	
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