package com.esferalia.aon.gwt.common.client.widget.solutions;

import java.util.List;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.occam.api.model.Workgroup;
import com.esferalia.aon.occam.api.model.security.TaskHolderWorkgroup;
import com.esferalia.aon.occam.api.model.task.TaskHolder;
import com.esferalia.aon.occam.api.model.task.TaskHolderWorkgroupType;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;

public class AonTaskHolderWorkgroupPanel extends AonCustomDialog {
	
	public static interface AonTaskHolderWorkgroupPanelCallback {
		void onAccept(TaskHolderWorkgroup taskHolderWorkgroup);
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
	
	private AonCustomListBox workgroup = new AonCustomListBox("Grupo Trabajo");
	private AonCustomListBox type = new AonCustomListBox("Tipo Usuario");
	private AonCustomDateBox start = new AonCustomDateBox("F. Inicio");
	private AonCustomDateBox end = new AonCustomDateBox("F. Fin");
	
	private String domainName;
	private Integer domainId;
	private String user;
	
	private List<Workgroup> workgroups;
	
	public AonTaskHolderWorkgroupPanel(String domainName, int domain, String user, TaskHolder taskHolder, AonTaskHolderWorkgroupPanelCallback callback) {
		initializeCommonService();
		this.domainName = domainName;
		this.domainId = domain;
		this.user = user;
		
		this.getElement().getStyle().setProperty("min-width", "35rem");
		
		getWorkgroups(workgroups -> {
			TaskHolderWorkgroup taskHolderWorkgroup = new TaskHolderWorkgroup().setDomain(domainId).setTaskHolder(taskHolder);
			show(taskHolderWorkgroup, callback);
		});
		
	}
	
	public AonTaskHolderWorkgroupPanel(String domainName, int domain, String user, TaskHolderWorkgroup taskHolderWorkgroup, AonTaskHolderWorkgroupPanelCallback callback) {
		initializeCommonService();
		this.domainName = domainName;
		this.domainId = domain;
		this.user = user;
		
		this.getElement().getStyle().setProperty("min-width", "30rem");
		
		getWorkgroups(workgroups -> {
			show(taskHolderWorkgroup, callback);
		});
		
	}
	
	public void show(TaskHolderWorkgroup taskHolderWorkgroup, AonTaskHolderWorkgroupPanelCallback callback) {
		content.setStyleName(AON.CSS.aonFlexColumn());
		content.getElement().getStyle().setProperty("padding", "1rem 0");
		
		HTMLPanel container = new HTMLPanel("");
		container.setStyleName(AON.CSS.aonFlexColumn());
		container.getElement().getStyle().setProperty("padding", "1rem");
		
		content.add(messagePanel);
		
		workgroup.addItem("-", "");
		workgroups.forEach(workgroupIt -> workgroup.addItem(workgroupIt.getDescription(), workgroupIt.getId().toString()));
		container.add(workgroup);
		
		type.addItem("-", "");
		type.addItem(TaskHolderWorkgroupType.USER.getDescription(), TaskHolderWorkgroupType.USER.getName());
		type.addItem(TaskHolderWorkgroupType.ADMIN.getDescription(), TaskHolderWorkgroupType.ADMIN.getName());
		container.add(type);
		
		container.add(start);
		container.add(end);
		
		content.add(container);
		
		FlowPanel buttons = new FlowPanel();
    	buttons.setStyleName(AON.CSS.aonTextCenter());
    	
    	Button okButton = new Button();
    	okButton.setStyleName(AON.CSS.aonOkButton());
    	okButton.setText( AON.MSG.accept());
    	okButton.addClickHandler(e -> {
    		okButton.setEnabled(false);
    		
    		if(null == start.getValue()) {
    			okButton.setEnabled(true);
    			AonMessagePanel.showWarning(messagePanel, "El campo fecha de inicio es obligatorio");
    		} else if(AonStringUtils.isBlank(workgroup.getValue()) ) {
    			okButton.setEnabled(true);
    			AonMessagePanel.showWarning(messagePanel, "El campo grupo de trabajo es obligatorio");
    		} else {
    			
    			taskHolderWorkgroup.setStartDate(start.getValue());
    			taskHolderWorkgroup.setEndDate(end.getValue());
    			taskHolderWorkgroup.setWorkgroup(AonStringUtils.isBlank(workgroup.getValue()) ? null : new Workgroup().setId(Integer.parseInt(workgroup.getValue())));
    			taskHolderWorkgroup.setTaskHolderWorkgroupType(TaskHolderWorkgroupType.safeValueOf(type.getValue()));
        		
        		// Guardar el nuevo operario / grupo de trabajo
        		commonService.saveTaskHolderWorkgroup(domainName, domainId, user, taskHolderWorkgroup, new AsyncCallback<TaskHolderWorkgroup>() {

    				@Override
    				public void onSuccess(TaskHolderWorkgroup taskHolderWorkgroup) {
    		    		hide();
    					callback.onAccept(taskHolderWorkgroup);
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
    	
    	if(taskHolderWorkgroup.getId() != null) {
    		workgroup.setValue(null != taskHolderWorkgroup.getWorkgroup().getId() ? taskHolderWorkgroup.getWorkgroup().getId().toString() : "");
    		type.setValue(taskHolderWorkgroup.getTaskHolderWorkgroupType().getName());
    		start.setValue(taskHolderWorkgroup.getStartDate());
    		end.setValue(taskHolderWorkgroup.getEndDate());
    	}
    	
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
	
	private void getWorkgroups(Consumer<List<Workgroup>> success) {
		commonService.getAviableWorkgroups(domainName, domainId, user, domainId, new AsyncCallback<List<Workgroup>>() {
			
			@Override
			public void onSuccess(List<Workgroup> workgroupsDb) {
				workgroups = workgroupsDb;
				success.accept(workgroups);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				AonMessagePanel.showError(messagePanel, "Error obtenci\u00f3n grupos de trabajo. " + caught.getMessage());
			}
			
		});
	}

}
