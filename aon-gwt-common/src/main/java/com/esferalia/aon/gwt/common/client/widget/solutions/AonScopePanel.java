package com.esferalia.aon.gwt.common.client.widget.solutions;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.occam.api.model.security.Scope;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

public class AonScopePanel extends AonCustomDialog {
	
	public static interface AonScopePanelCallback {
		void onAccept(Scope scope);
		void onCancel();
		boolean existsScopeWithDescription(String description);
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
	
	private AonCustomTextBox descriptionTextBox = new AonCustomTextBox("Descripci\u00f3n");
	private AonCustomToogleButton assignAllUsers = new AonCustomToogleButton("Asig. todos usuarios");
	private AonCustomMultiSelectBox domainUsersSelect = new AonCustomMultiSelectBox("Usuarios");
	
	private AonScopePanelCallback callback;
	
	private String domainName;
	private Integer domainId;
	private String user;
	
	private ArrayList<User> domainUsers = new ArrayList<User>();
	
	public AonScopePanel(String domainName, int domain, String user, ArrayList<User> domainUsers, AonScopePanelCallback callback) {
		initializeCommonService();
		this.domainName = domainName;
		this.domainId = domain;
		this.user = user;
		
		this.callback = callback;
		
		this.domainUsers = domainUsers;
		
		this.getElement().getStyle().setProperty("min-width", "35rem");
		
		setCaption("Nuevo \u00c1mbito");
		
		show(new Scope());
		
	}
	
	public void show(Scope scope) {
		content.setStyleName(AON.CSS.aonFlexColumn2());
		content.getElement().getStyle().setProperty("padding", "1rem");
		
		content.add(messagePanel);
		
		assignAllUsers.getElement().getStyle().setProperty("max-width", "10rem");
		
		assignAllUsers.addValueChangeHandler(e -> {
			domainUsersSelect.setVisible(!e.getValue());
		});
		
		Set<String> usersOptions = new LinkedHashSet<String>();
		this.domainUsers.forEach(u -> usersOptions.add(u.getName()));
		domainUsersSelect.setOptions(usersOptions);
		
		content.add(createRowPanel(descriptionTextBox, assignAllUsers));
		content.add(createRowPanel(domainUsersSelect));
		
		FlowPanel buttons = new FlowPanel();
    	buttons.setStyleName(AON.CSS.aonTextCenter());
    	
		Button okButton = new Button();
    	okButton.setStyleName(AON.CSS.aonOkButton());
    	okButton.setText( AON.MSG.accept());
    	okButton.addClickHandler(e -> {
    		okButton.setEnabled(false);
    		
    		if(AonStringUtils.isBlank(descriptionTextBox.getValue())) {
    			okButton.setEnabled(true);
    			AonMessagePanel.showWarning(messagePanel, "El campo descripci\u00f3n es obligatorio");
    		} else if(callback.existsScopeWithDescription(descriptionTextBox.getValue())) {
				okButton.setEnabled(true);
				AonMessagePanel.showWarning(messagePanel, "Ya existe un \u00e1mbito con la misma descripci\u00f3n");
			} else {
    			
    			scope.setDomain(domainId);
    			scope.setDescription(descriptionTextBox.getValue());
    			
    			ArrayList<User> selectedUsers = new ArrayList<User>();
    			if(!assignAllUsers.getValue()) {
    				domainUsersSelect.getSelectedOptions().forEach(selectedOpt -> {
    					User user = domainUsers.stream().filter(u -> AonStringUtils.equals(u.getName(), selectedOpt)).findFirst().orElse(null);
    					if(null != user) selectedUsers.add(user);
    				});
    			}
    			
    			commonService.saveScopeAndAssign(domainName, domainId, user, scope, assignAllUsers.getValue(), selectedUsers, new AsyncCallback<Scope>() {

    				@Override
    				public void onSuccess(Scope newScope) {
    					hide();
    					callback.onAccept(newScope);
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
	
	private HTMLPanel createRowPanel(Widget ...widgets) {
		HTMLPanel row = new HTMLPanel("");
		row.addStyleName(AON.CSS.aonItemFlex());
		
		for(int i=0; i < widgets.length; i++)
			row.add(widgets[i]);
		
		return row;
	}
}
