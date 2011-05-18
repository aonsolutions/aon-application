package com.code.aon.faces.component.richfaces.goToButton;

import javax.el.MethodExpression;
import javax.el.ValueExpression;
import javax.faces.component.UICommand;
import javax.faces.component.UIComponent;
import javax.faces.event.ActionListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.faces.component.myfaces.UIComponentTagUtils;
import com.code.aon.faces.component.richfaces.AonAjaxComponentHandler;
import com.code.aon.faces.component.richfaces.IRichFacesTags;
import com.code.aon.faces.component.util.FaceletUtil;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;
import com.sun.facelets.FaceletContext;
import com.sun.facelets.tag.TagAttribute;
import com.sun.facelets.tag.jsf.ComponentConfig;

/**
 * The Class TabbedPaneComponentHandler.
 * 
 * @author atellitu
 */
public class GoToButtonHandler extends AonAjaxComponentHandler implements IRichFacesTags {

	private final static Logger LOGGER = LoggerFactory.getLogger(GoToButtonHandler.class);
	
	private static final String BACK_ACTION = "backAction";
	
	private static final String BACK_ACTION_LISTENER = "backActionListener";
	
	private TagAttribute controllerTag;
	
	private TagAttribute propertyTag;
	
   	/**
	 * The Constructor.
	 * 
	 * @param config the config
	 */
	public GoToButtonHandler(ComponentConfig config) {
		super( config );
		controllerTag = getRequiredAttribute(CONTROLLER);
		propertyTag = getRequiredAttribute(PROPERTY);
	}
	
	private BasicController getController( FaceletContext ctx ) {
		return (BasicController) controllerTag.getObject(ctx);
	}

	private ValueExpression getTo( FaceletContext ctx ) {
		return propertyTag.getValueExpression(ctx, ITransferObject.class);
	}
	
	/**
	 * Sets the attributes.
	 * 
	 * @param instance the instance
	 * @param ctx the ctx
	 */
	@Override
	protected void setAttributes(FaceletContext ctx, Object instance) {
		super.setAttributes(ctx, instance);
		UICommand button = (UICommand) instance;
		BasicController controller = getController(ctx);	
		TagAttribute actionTag = getAttribute(ACTION);
		if ( actionTag == null ) {
			String action = controller.getBeanName() + "_form";
			MethodExpression me = FaceletUtil.getMethodExpression(ctx, action, String.class, FaceletUtil.ACTION_SIG);
			button.setActionExpression(me);		
		}
		TagAttribute actionListenerTag = getAttribute(ACTION_LISTENER);
		if ( actionListenerTag == null ) {
			ActionListener al = new GoToActionListener(controller, getTo(ctx));
			button.addActionListener(al);
		}
		String backAction = null;
		TagAttribute backActionTag = getAttribute(BACK_ACTION);
		if ( backActionTag != null ) {
			backAction = backActionTag.getValue(ctx);
		} else {
			backAction = AonUtil.getConfigurationController().getCurrentAction();
		}
		controller.setBackAction(backAction);
		String backActionListener = null;
		TagAttribute balTag = getAttribute(BACK_ACTION_LISTENER);
		if ( balTag != null ) {
			backActionListener = balTag.getValue(ctx);
		} else {
			backActionListener = controller.getBeanName() + ".onBack";
		}
		controller.setBackActionListener(backActionListener);
	}

	private boolean isResolved( FaceletContext ctx ) {
		Object to = getTo(ctx).getValue(ctx);
		if ( to != null ) {
			try {
				IManagerBean bean = BeanManager.getManagerBean(to.getClass());
				return bean.getId( (ITransferObject) to) != null;
			} catch (ManagerBeanException e) {
				LOGGER.error( "Error getting id", e );
			}
		}
		return false;
	}	
	
	@Override
	protected void onComponentPopulated(FaceletContext ctx, UIComponent c,
			UIComponent parent) {
		TagAttribute renderedTag = getAttribute(RENDERED);
		if ( renderedTag == null ) {
			String rendered = Boolean.toString(isResolved(ctx));
			UIComponentTagUtils.setBooleanProperty(ctx.getFacesContext(), c, RENDERED, rendered);
		}
	}
	
}