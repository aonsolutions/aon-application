package com.esferalia.aon.gwt.common.client.widget.solutions;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.task.TaskHolder;
import com.esferalia.aon.occam.api.model.type.RegistryStatus;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

public class AonTaskHolderPanel extends AonCustomDialog {
	
	public static interface AonTaskHolderPanelCallback {
		void onAccept(TaskHolder taskHolder);
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
	
	private List<User> users = new ArrayList<>();
	
	public AonTaskHolderPanel(String domainName, int domain, String user, AonTaskHolderPanelCallback callback) {
		initializeCommonService();
		this.domainName = domainName;
		this.domainId = domain;
		this.user = user;
		
		this.getElement().getStyle().setProperty("min-width", "35rem");
		
		setCaption("Nuevo Operario");
		
		getUsers(users -> {
			show(new TaskHolder(), callback);
		});
		
	}
	
	public void show(TaskHolder taskHolder, AonTaskHolderPanelCallback callback) {
		content.setStyleName(AON.CSS.aonFlexColumn());
		content.getElement().getStyle().setProperty("padding", "1rem 0");
		
		HTMLPanel container = new HTMLPanel("");
		container.setStyleName(AON.CSS.aonFlexColumn());
		container.getElement().getStyle().setProperty("padding", "1rem");
		
		content.add(messagePanel);
		
		userLB.clearItems();
		userLB.addItem("-", "");
		users.forEach(user -> userLB.addItem(user.getName() + "( " + user.getLogin() + (null != user.getDomain().getId() && !user.getDomain().getId().equals(domainId) ? " - Dom. Padre" : "" ) + " )", null == user.getId() ? "" : user.getId().toString()));
		
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
    			
    			getUser(Integer.parseInt(userLB.getValue()), userIt -> {
    				
    				TaskHolder newTaskHolder = new TaskHolder();
        			newTaskHolder.setDomain(new Domain().setId(domainId));
        			newTaskHolder.setUserId(Integer.parseInt(userLB.getValue()));
        			
        			if(null != userIt.getRegistry().getId())
        				newTaskHolder.copy(userIt.getRegistry());
        			else
        				newTaskHolder.setName(userIt.getName());
        			
        			newTaskHolder.setStatus(RegistryStatus.ACTIVE);
        			
        			commonService.saveTaskHolder(domainName, domainId, user, newTaskHolder, new AsyncCallback<TaskHolder>() {

        				@Override
        				public void onSuccess(TaskHolder taskHolder) {
        					hide();
        					callback.onAccept(taskHolder);
        				}
        				@Override
        				public void onFailure(Throwable caught) {
        					AonMessagePanel.showError(messagePanel, caught.getMessage());
        					okButton.setEnabled(true);
        				}
        			});
        			
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
		commonService.getUsersForTaskHolder(domainName, domainId, user, false, new AsyncCallback<List<User>>() {
			
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
	
	private void getUser(Integer userId, Consumer<User> success) {
		commonService.getUser(domainName, domainId, user, userId, new AsyncCallback<User>() {
			
			@Override
			public void onSuccess(User userDb) {
				success.accept(userDb);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				AonMessagePanel.showError(messagePanel, "Error obtenci\u00f3n usuario. " + caught.getMessage());
			}
			
		});
	}

}
