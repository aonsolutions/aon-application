package com.esferalia.aon.gwt.common.client.widget.solutions;

import java.util.Date;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.occam.api.model.Workgroup;
import com.esferalia.aon.occam.api.model.project.ProjectHolder;
import com.esferalia.aon.occam.api.model.task.TaskHolder;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;

public class AonProjectHolderPanel extends AonCustomDialog {
	
	public static interface AonProjectHolderPanelCallback {
		void onAccept(ProjectHolder projectHolder);
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
	private AonCustomListBox taskHolder = new AonCustomListBox("Operario");
	private AonCustomDateBox start = new AonCustomDateBox("F. Inicio");
	private AonCustomDateBox end = new AonCustomDateBox("F. Fin");
	
	private String domainName;
	private Integer domainId;
	private String user;
	
	private Integer projectId;
	private List<ProjectHolder> projectHolders;
	
	private List<Workgroup> workgroups;
	private List<TaskHolder> taskHolders;
	
	public AonProjectHolderPanel(String domainName, int domain, String user, Integer projectId, List<ProjectHolder> projectHolders,  AonProjectHolderPanelCallback callback) {
		initializeCommonService();
		this.domainName = domainName;
		this.domainId = domain;
		this.user = user;
		
		this.projectId = projectId;
		this.projectHolders = projectHolders;
		
		this.getElement().getStyle().setProperty("min-width", "35rem");
		
		getWorkgroups(workgroups -> {
			getTaskHolders(taskHolders -> {
				show(new ProjectHolder(), callback);
			});
		});
		
	}
	
	public AonProjectHolderPanel(String domainName, int domain, String user, Integer projectId, List<ProjectHolder> projectHolders, ProjectHolder projectHolder, AonProjectHolderPanelCallback callback) {
		initializeCommonService();
		this.domainName = domainName;
		this.domainId = domain;
		this.user = user;
		
		this.projectId = projectId;
		this.projectHolders = projectHolders;
		
		this.getElement().getStyle().setProperty("min-width", "30rem");
		
		getWorkgroups(workgroups -> {
			getTaskHolders(taskHolders -> {
				show(projectHolder, callback);
			});
		});
		
	}
	
	public void show(ProjectHolder projectHolder, AonProjectHolderPanelCallback callback) {
		content.setStyleName(AON.CSS.aonFlexColumn());
		content.getElement().getStyle().setProperty("padding", "1rem 0");
		
		HTMLPanel container = new HTMLPanel("");
		container.setStyleName(AON.CSS.aonFlexColumn());
		container.getElement().getStyle().setProperty("padding", "1rem");
		
		content.add(messagePanel);
		
		workgroup.addItem("-", "");
		workgroups.forEach(workgroupIt -> workgroup.addItem(workgroupIt.getDescription(), workgroupIt.getId().toString()));
		container.add(workgroup);
		
		taskHolder.addItem("-", "");
		taskHolders.forEach(taskHoldeIt -> taskHolder.addItem(taskHoldeIt.getName(), taskHoldeIt.getId().toString()));
		container.add(taskHolder);
		
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
    		} else if(AonStringUtils.isBlank(taskHolder.getValue()) ) {
    			okButton.setEnabled(true);
    			AonMessagePanel.showWarning(messagePanel, "El campo operario es obligatorio");
    		} else {
    			
    			if(projectHolder.getId() == null) {
	    			projectHolder.setDomain(domainId);
	    			projectHolder.setProject(projectId);
    			}
    			
    			projectHolder.setStartDate(start.getValue());
    			projectHolder.setEndDate(end.getValue());
        		projectHolder.setWorkgroup(AonStringUtils.isBlank(workgroup.getValue()) ? null : new Workgroup().setId(Integer.parseInt(workgroup.getValue())));
        		
        		TaskHolder taskHolderObj = null;
        		if(!AonStringUtils.isBlank(taskHolder.getValue())){
        			taskHolderObj = new TaskHolder();
        			taskHolderObj.setId(Integer.parseInt(taskHolder.getValue()));
        		}
        		projectHolder.setTaskHolder(taskHolderObj);
        		
        		if(projectHolder.getId() == null) {
	        		// Actualizar el resto de operarios o grupos de trabajo
	        		if(null != projectHolder.getTaskHolder().getId()) {
	        			 List<ProjectHolder> optProjectHolders = projectHolders.stream()
	        				.filter(projectHolderIt -> 
	        					null != projectHolderIt.getTaskHolder().getId() && 
	        					(null == projectHolderIt.getEndDate() || projectHolderIt.getEndDate().after(start.getValue()))
	        				).collect(Collectors.toList());
	        			 
	        			 Date endDate = DateUtils.addDays2Date(start.getValue(), -1);
	        			 
	        			 optProjectHolders.forEach(projectHolderIt -> {
	        				 projectHolderIt.setEndDate(endDate);
	        				 
	        				 commonService.saveProjectHolder(domainName, domainId, user, projectHolderIt, new AsyncCallback<ProjectHolder>() {
	
	        	    				@Override
	        	    				public void onSuccess(ProjectHolder projectHolder) {}
	        	    				
	        	    				@Override
	        	    				public void onFailure(Throwable caught) {}
	        	    			});
	        			 });
	        		} else if(null != projectHolder.getWorkgroup().getId()) {
	        			List<ProjectHolder> optProjectHolders = projectHolders.stream()
	            				.filter(projectHolderIt -> 
	            					null != projectHolderIt.getWorkgroup().getId() && 
	            					(null == projectHolderIt.getEndDate() || projectHolderIt.getEndDate().after(start.getValue()))
	            				).collect(Collectors.toList());
	            			 
	            			 Date endDate = DateUtils.addDays2Date(start.getValue(), -1);
	            			 
	            			 optProjectHolders.forEach(projectHolderIt -> {
	            				 projectHolderIt.setEndDate(endDate);
	            				 
	            				 commonService.saveProjectHolder(domainName, domainId, user, projectHolderIt, new AsyncCallback<ProjectHolder>() {
	
	            	    				@Override
	            	    				public void onSuccess(ProjectHolder projectHolder) {}
	            	    				
	            	    				@Override
	            	    				public void onFailure(Throwable caught) {}
	            	    			});
	            			 });
	        		}
        		}
        		
        		// Guardar el nuevo operario o grupo de trabajo
        		commonService.saveProjectHolder(domainName, domainId, user, projectHolder, new AsyncCallback<ProjectHolder>() {

    				@Override
    				public void onSuccess(ProjectHolder projectHolder) {
    		    		hide();
    					callback.onAccept(projectHolder);
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
    	
    	if(projectHolder.getId() != null) {
    		workgroup.setValue(null != projectHolder.getWorkgroup().getId() ? projectHolder.getWorkgroup().getId().toString() : "");
    		taskHolder.setValue(null != projectHolder.getTaskHolder().getId() ? projectHolder.getTaskHolder().getId().toString() : "");
    		start.setValue(projectHolder.getStartDate());
    		end.setValue(projectHolder.getEndDate());
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
		commonService.getAviableWorkgroups(domainName, domainId, user, new AsyncCallback<List<Workgroup>>() {
			
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
	
	private void getTaskHolders(Consumer<List<TaskHolder>> success) {
		commonService.getTaskHolders(domainName, domainId, user, new AsyncCallback<List<TaskHolder>>() {
			
			@Override
			public void onSuccess(List<TaskHolder> taskHoldersDb) {
				taskHolders = taskHoldersDb.stream()
						.filter(taskHolder -> AonStringUtils.isNotBlank(taskHolder.get().getName()))
						.sorted((th1, th2) -> th1.getName().compareTo(th2.getName()))
						.collect(Collectors.toList());
				success.accept(taskHolders);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				AonMessagePanel.showError(messagePanel, "Error obtenci\u00f3n operarios. " + caught.getMessage());
			}
			
		});
	}

}
