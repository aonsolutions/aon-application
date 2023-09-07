package com.code.aon.faces.component.richfaces.goToButton;

import java.io.Serializable;

import jakarta.el.ELContext;
import jakarta.el.MethodExpression;
import jakarta.el.ValueExpression;
import javax.faces.component.UICommand;
import javax.faces.component.UIComponent;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.faces.component.myfaces.UIComponentTagUtils;
import com.code.aon.faces.component.richfaces.AonAjaxComponentHandler;
import com.code.aon.faces.component.richfaces.IRichFacesTags;
import com.code.aon.faces.component.util.FaceletUtil;
import com.code.aon.faces.controller.RichLookupBean;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;
import com.sun.facelets.FaceletContext;
import com.sun.facelets.tag.MetaRuleset;
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
	
	private TagAttribute propertyIdTag;
	
   	/**
	 * The Constructor.
	 * 
	 * @param config the config
	 */
	public GoToButtonHandler(ComponentConfig config) {
		super( config );
		controllerTag = getRequiredAttribute(CONTROLLER);
		propertyIdTag  = getAttribute(PROPERTY_ID);
		if ( propertyIdTag  == null ) {
			propertyTag = getAttribute(PROPERTY);
		}
	}
	
	@Override
	protected MetaRuleset createMetaRuleset(Class type) {
		MetaRuleset set = super.createMetaRuleset(type);
		return set.ignore(ACTION_LISTENER);
	}	
	
	private BasicController getController( FaceletContext ctx ) {
		return (BasicController) controllerTag.getObject(ctx);
	}

	private ValueExpression getToExpression( FaceletContext ctx ) {
		if ( propertyTag != null ) {
			return propertyTag.getValueExpression(ctx, ITransferObject.class);
		}
		return null;
	}

	private ValueExpression getIdExpression( FaceletContext ctx ) {
		if ( propertyIdTag != null ) {
			return propertyIdTag.getValueExpression(ctx, Serializable.class);	
		}
		return null;
	}
	
	public static ITransferObject getTo( ELContext ctx, ValueExpression toVE, ValueExpression idVE, BasicController controller ) {
		ITransferObject to = null;
		if ( toVE != null ) {
			to = (ITransferObject) toVE.getValue(ctx);
		} else if ( idVE != null ) {
			Serializable id = (Serializable) idVE.getValue(ctx);
			if ( id != null ) {
				try {
					to = controller.getManagerBean().get(id);
				} catch (ManagerBeanException e) {
					LOGGER.warn( "No ITransferObject for id " + id, e);
				}
			}
		}
		return to;
	}
	
	private ITransferObject getTo( FaceletContext ctx ) {
		return getTo(ctx, getToExpression(ctx), getIdExpression(ctx), getController(ctx));
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
		ValueExpression controllerVE = controllerTag.getValueExpression(ctx, BasicController.class);
		GoToActionListener gtal = new GoToActionListener(controllerVE);
		button.addActionListener(gtal);
		TagAttribute actionTag = getAttribute(ACTION);
		if ( actionTag == null ) {
			String action = controller.getBeanName() + "_form";
			MethodExpression me = FaceletUtil.getMethodExpression(ctx, action, String.class, FaceletUtil.ACTION_SIG);
			button.setActionExpression(me);		
		}
		TagAttribute actionListenerTag = getAttribute(ACTION_LISTENER);
		if ( actionListenerTag != null ) {
			MethodExpression me = actionListenerTag.getMethodExpression(ctx, null, FaceletUtil.ACTION_LISTENER_SIG);
			gtal.setActionListener( me );
		} else {
			gtal.setToExpression( getToExpression(ctx) );
			gtal.setIdExpression( getIdExpression(ctx) );
		}
		String backAction = null;
		TagAttribute backActionTag = getAttribute(BACK_ACTION);
		if ( backActionTag != null ) {
			backAction = backActionTag.getValue(ctx);
		}
		if ( StringUtils.isEmpty(backAction) ) {
			backAction = AonUtil.getConfigurationController().getCurrentAction();
		}
		gtal.setBackAction(backAction);
		String backActionListener = null;
		TagAttribute balTag = getAttribute(BACK_ACTION_LISTENER);
		if ( balTag != null ) {
			backActionListener = balTag.getValue(ctx);
		} else {
			backActionListener = controller.getBeanName() + ".onBack";
		}
		gtal.setBackActionListener(backActionListener);
	}

	private boolean isResolved( FaceletContext ctx ) {
		Object to = getTo(ctx);
		if ( to != null ) {
			return RichLookupBean.isResolved( (ITransferObject) to );	
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