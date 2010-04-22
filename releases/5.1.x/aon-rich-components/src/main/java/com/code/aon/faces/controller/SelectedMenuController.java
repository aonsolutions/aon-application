package com.code.aon.faces.controller;

import javax.el.ExpressionFactory;
import javax.el.MethodExpression;
import javax.faces.component.UICommand;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ActionListener;
import javax.faces.event.MethodExpressionActionListener;

import org.ajax4jsf.component.html.HtmlAjaxCommandLink;
import org.apache.commons.lang.StringUtils;

import com.code.aon.faces.component.util.FaceletUtil;

public class SelectedMenuController {
	
	private static final String CONTROLLER_NAME = "selectedMenu";
	
	private static final String SELECTED_MENU_ATTRIBUTE = "com.code.aon.faces.selectedMenu";

	private static final String SELECTED_CLASS = "aon-top-menu-item-select";
	
	public String getLastMenuAction() {
		UIViewRoot root = FacesContext.getCurrentInstance().getViewRoot();
		return (String) root.getAttributes().get( SELECTED_MENU_ATTRIBUTE );
	}

	public void setLastMenuAction(String lastAction) {
		UIViewRoot root = FacesContext.getCurrentInstance().getViewRoot();
		if ( lastAction == null ) {
			root.getAttributes().remove( SELECTED_MENU_ATTRIBUTE );
		} else {
			root.getAttributes().put( SELECTED_MENU_ATTRIBUTE, lastAction );
		}
	}
	
	private void addActionListener( UICommand command ) {
		FacesContext ctx = FacesContext.getCurrentInstance();
        ExpressionFactory f = ctx.getApplication().getExpressionFactory();
        MethodExpression me = f.createMethodExpression(ctx.getELContext(),
        		"#{" + CONTROLLER_NAME + ".onMenuSelect}", null, FaceletUtil.ACTION_LISTENER_SIG);
        ActionListener listener = new MethodExpressionActionListener(me);
        command.addActionListener(listener);
	}
	
	public void onMenuSelect( ActionEvent event ) {
		UICommand command = (UICommand) event.getComponent();
		MethodExpression expression = command.getActionExpression();
		if ( expression != null ) {
			String action = expression.getExpressionString();
			setLastMenuAction(action);
		}					
	}
	
	public void updateCommands( UIComponent component, UIComponent parent ) {
		if ( component.isRendered() ) {
			UICommand command = (UICommand) component;
			addActionListener(command);
			MethodExpression expression = command.getActionExpression();
			if ( expression != null ) {
				String action = expression.getExpressionString();
				String last = getLastMenuAction();
				if ( StringUtils.equals(last, action) ) {
					HtmlAjaxCommandLink commandLink = (HtmlAjaxCommandLink) component;
					String styleClass = commandLink.getStyleClass();
					styleClass = StringUtils.join(new String[]{SELECTED_CLASS, styleClass}, " ");
					commandLink.setStyleClass(styleClass);
					setLastMenuAction(null);
				}				
			}
		}
	}
	
}