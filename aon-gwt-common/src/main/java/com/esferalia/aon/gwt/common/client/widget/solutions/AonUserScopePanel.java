package com.esferalia.aon.gwt.common.client.widget.solutions;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.security.UserScope;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

public class AonUserScopePanel extends AonCustomDialog {
	
	public static interface AonUserScopePanelCallback {
		void onAccept(Void result);
		void onCancel();
	}

	static CommonServiceAsync commonService;
	
	private static void initializeCommonService() {
		if (commonService == null) {
			CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
			commonService = new CommonServiceAsyncDecorator(commonServiceRaw);
		}
	}
	
	// Project Info
	private HTMLPanel content = new HTMLPanel("");
	private HTMLPanel messagePanel = new HTMLPanel("");
	
	private AonCustomListBox userLB = new AonCustomListBox("Usuario");
	
	private String domainName;
	private Integer domainId;
	private String user;
	
	private Integer scopeId;
	
	private List<User> users = new ArrayList<>();
	
	public AonUserScopePanel(String domainName, int domain, String user, Integer scopeId, AonUserScopePanelCallback callback) {
		initializeCommonService();
		this.domainName = domainName;
		this.domainId = domain;
		this.user = user;
		this.scopeId = scopeId;
		
		this.getElement().getStyle().setProperty("min-width", "35rem");
		
		setCaption("Nuevo Usuario");
		
		getUsers(users -> {
			show(new UserScope(), callback);
		});
		
	}
	
	public void show(UserScope userScope, AonUserScopePanelCallback callback) {
		content.setStyleName(AON.CSS.aonFlexColumn());
		content.getElement().getStyle().setProperty("padding", "1rem 0");
		
		HTMLPanel container = new HTMLPanel("");
		container.setStyleName(AON.CSS.aonFlexColumn());
		container.getElement().getStyle().setProperty("padding", "1rem");
		
		content.add(messagePanel);
		
		userLB.clearItems();
		userLB.addItem("-", "");
		users.forEach(user -> userLB.addItem(user.getName() + " ( Login: " + user.getLogin() + (null != user.getDomain().getId() && !user.getDomain().getId().equals(domainId) ? " - Entorno" : "" ) + " ) " + (null != user.getAuth() && null != user.getAuth().getEmail() ? ("[ " + user.getAuth().getEmail() + " ]") : ""), null == user.getId() ? "" : user.getId().toString()));
		
		container.add(createRowPanel(userLB, null));
		
		content.add(container);
		
		FlowPanel buttons = new FlowPanel();
    	buttons.setStyleName(AON.CSS.aonTextCenter());
    	
		Button okButton = new Button();
    	okButton.setStyleName(AON.CSS.aonOkButton());
    	okButton.setText( AON.MSG.accept());
    	okButton.addClickHandler(e -> {
    		okButton.setEnabled(false);
    		
    		if(AonStringUtils.isBlank(userLB.getValue())) {
    			okButton.setEnabled(true);
    			AonMessagePanel.showWarning(messagePanel, "El campo usuario es obligatorio");
    		} else {
    			
    			userScope
    				.setDomain(domainId)
    				.setScope(scopeId)
    				.setUserId(Integer.parseInt(userLB.getValue()))
    				;
    			
    			commonService.saveUserScope(domainName, domainId, user, userScope, new AsyncCallback<Void>() {

    				@Override
    				public void onSuccess(Void result) {
    					hide();
    					callback.onAccept(result);
    				}
    				@Override
    				public void onFailure(Throwable caught) {
    					AonMessagePanel.showError(messagePanel, caught.getMessage());
    					okButton.setEnabled(true);
    				}
    			});
    			
    		}
    		
    	});
    	buttons.add(okButton);
    	
    	final Button cancelButton = new Button();
    	cancelButton.setStyleName(AON.CSS.aonCancelButton());
    	cancelButton.addStyleName(AON.CSS.aonMarginLeft());
    	cancelButton.setText( AON.MSG.cancelAction());
    	cancelButton.addClickHandler(e -> {
    		cancelButton.setEnabled(false);
    		hide();
			callback.onCancel();
    	});
    	buttons.add(cancelButton);
    	
		content.add(buttons);
		setWidget(content);
		
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				center();
				show();
				center();
			}
		});
		
	}
	
	private HTMLPanel createRowPanel(Widget w1, Widget w2) {
		HTMLPanel row = new HTMLPanel("");
		row.addStyleName(AON.CSS.aonItemFlex());
		
		row.add(w1);
		
		if(null != w2) row.add(w2);
		
		return row;
	}
	
	private void getUsers(Consumer<List<User>> success) {
		commonService.getUsersForTaskHolder(domainName, domainId, user, true, new AsyncCallback<List<User>>() {
			
			@Override
			public void onSuccess(List<User> UserDb) {
				users.clear();
				users.addAll(UserDb);
				success.accept(users);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				AonMessagePanel.showError(messagePanel, "Error obtenci\u00f3n usuarios. " + caught.getMessage());
			}
			
		});
	}

}
