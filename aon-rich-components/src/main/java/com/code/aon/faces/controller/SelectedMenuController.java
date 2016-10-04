package com.code.aon.faces.controller;

import static com.code.aon.faces.controller.IRichConstants.SELECTED_MENU_CONTROLLER_NAME;

import java.io.Serializable;

import javax.el.ExpressionFactory;
import javax.el.MethodExpression;
import javax.faces.component.UICommand;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ActionListener;
import javax.faces.event.MethodExpressionActionListener;

import org.ajax4jsf.component.html.HtmlAjaxCommandLink;
import org.apache.commons.lang.StringUtils;

import com.code.aon.AonVersion;
import com.code.aon.faces.component.util.FaceletUtil;

public class SelectedMenuController implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private static final String SELECTED_CLASS = "aon-top-menu-item-select";
	
	private static final String ON_MENU_SELECT = "onMenuSelect";
	
	private static final String ON_MENU_RESET = "onMenuReset";
	
	private String lastMenuAction;
		
	public void onMenuReset( ActionEvent event ) {
		if(!checkIssues(event.getComponent().getId()))
			setLastMenuAction(null);
	}

	private String getLastMenuAction() {
		return lastMenuAction;
	}

	public void setLastMenuAction(String lastMenuAction) {
		this.lastMenuAction = lastMenuAction;
	}

	private void addActionListener( UICommand command, String action ) {
		FacesContext ctx = FacesContext.getCurrentInstance();
        ExpressionFactory f = ctx.getApplication().getExpressionFactory();
        MethodExpression me = f.createMethodExpression(ctx.getELContext(),
        		"#{" + SELECTED_MENU_CONTROLLER_NAME + "." + action + "}", null, FaceletUtil.ACTION_LISTENER_SIG);
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
			addActionListener(command, ON_MENU_SELECT);
			MethodExpression expression = command.getActionExpression();
			if ( expression != null ) {
				String action = expression.getExpressionString();
				String last = getLastMenuAction();
				if ( StringUtils.equals(last, action) && (component instanceof HtmlAjaxCommandLink) ) {
					HtmlAjaxCommandLink commandLink = (HtmlAjaxCommandLink) component;
					String styleClass = commandLink.getStyleClass();
					styleClass = StringUtils.join(new String[]{SELECTED_CLASS, styleClass}, " ");
					commandLink.setStyleClass(styleClass);
				}				
			}
		}
	}

	public void updateCommandsToReset( UIComponent component, UIComponent parent ) {
		if ( component.isRendered() && (UICommand.class.isAssignableFrom(component.getClass())) ) {
			UICommand command = (UICommand) component;
			addActionListener(command, ON_MENU_RESET);
		}
	}

	public boolean isDocument() {
		if(getLastMenuAction()!=null)
			return getLastMenuAction().equals("gwt_documents");
		else return false;
	}
	
	public boolean isIssues() {
		if(getLastMenuAction()!=null)
			return getLastMenuAction().equals("gwt_issues");
		else return false;
	}
	
	public boolean checkIssues(String id){
		if(getLastMenuAction().equals("gwt_issues")){
			switch (id) {
			case "pendingAlarms": return false;
			case "home": return false;
			case "menu-quickIssue": return false;
			case "alarm": return false;
			case "favorites": return false;
			case "advancedMode": return false;
			case "config_userProfile": return false;
			case "changePassword": return false;
			case "webmap": return false;
			case "spanishLanguage": return false;
			case "englishLanguage": return false;
			default: return true;	
			}
		}	
		return false;
	}
}