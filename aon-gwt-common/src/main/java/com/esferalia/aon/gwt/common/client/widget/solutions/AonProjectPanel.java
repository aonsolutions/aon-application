package com.esferalia.aon.gwt.common.client.widget.solutions;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.occam.api.model.ActivityType;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Workgroup;
import com.esferalia.aon.occam.api.model.project.ProjectActivity;
import com.esferalia.aon.occam.api.model.project.ProjectHolder;
import com.esferalia.aon.occam.api.model.project.ProjectType;
import com.esferalia.aon.occam.api.model.registry.CustomerFull;
import com.esferalia.aon.occam.api.model.registry.Project;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.task.TaskHolder;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

public class AonProjectPanel extends AonCustomDialog {
	
	public static interface AonProjectPanelCallback {
		void onAccept(Project project);
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
	
	private AonCustomListBox type = new AonCustomListBox("Tipo");
	private AonCustomTextBox name = new AonCustomTextBox("Nombre");
	private AonCustomTextBox alias = new AonCustomTextBox("Alias");
	private AonCustomDateBox date = new AonCustomDateBox("Fecha");
	private AonCustomListBox workgroup = new AonCustomListBox("Grupo Trabajo");
	private AonCustomListBox taskHolder = new AonCustomListBox("Operario");
	private AonCustomListBox activity = new AonCustomListBox("Actividad");
	
	private String domainName;
	private Integer domainId;
	private String user;
	
	private Integer registry;
	private CustomerFull customer;
	
	private List<ProjectType> projectTypes;
	private List<Workgroup> workgroups;
	private List<TaskHolder> taskHolders;
	private List<ActivityType> activityType;
	
	public AonProjectPanel(String domainName, int domain, String user, Integer registry, AonProjectPanelCallback callback) {
		initializeCommonService();
		this.domainName = domainName;
		this.domainId = domain;
		this.user = user;
		
		this.registry = registry;
		
		this.getElement().getStyle().setProperty("min-width", "35rem");
		
		setCaption("Nuevo Expediente");
		
		getAviableProjectType(projectTypes -> {
			getCustomer(customer -> {
				getWorkgroups(workgroups -> {
					getTaskHolders(taskHolders -> {
						getActivityTypes(projectActivities -> {
							show(new Project(), callback);
						});
					});
				});
			});
		});
		
	}
	
	public void show(Project project, AonProjectPanelCallback callback) {
		content.setStyleName(AON.CSS.aonFlexColumn());
		content.getElement().getStyle().setProperty("padding", "1rem 0");
		
		HTMLPanel container = new HTMLPanel("");
		container.setStyleName(AON.CSS.aonFlexColumn());
		container.getElement().getStyle().setProperty("padding", "1rem");
		
		content.add(messagePanel);
		
		type.clearItems();
		type.addItem("-", "");
		projectTypes.forEach(projectType -> type.addItem(projectType.getDescription(), projectType.getId().toString()));
		type.addChangeHandler(e -> {
			if(AonStringUtils.isNotBlank(type.getValue()))
				name.setValue(type.getListBox().getSelectedItemText());
		});
		
		name.setValue(customer.getRegistry().getName());
		date.setValue(new Date());
		
		container.add(createRowPanel(type, date));
		container.add(createRowPanel(name, alias));
		
		workgroup.addItem("-", "");
		workgroups.forEach(workgroupIt -> workgroup.addItem(workgroupIt.getDescription(), workgroupIt.getId().toString()));
		
		taskHolder.addItem("-", "");
		taskHolders.forEach(taskHoldeIt -> taskHolder.addItem(taskHoldeIt.getName(), taskHoldeIt.getId().toString()));
		taskHolder.addChangeHandler(e -> center());
		container.add(createRowPanel(workgroup, taskHolder));
		
		activity.addItem("-", "");
		activityType.forEach(act -> activity.addItem(act.getDescription(), act.getId().toString()));
		container.add(activity);
		
		content.add(container);
		
		FlowPanel buttons = new FlowPanel();
    	buttons.setStyleName(AON.CSS.aonTextCenter());
    	
    	Button okButton = new Button();
    	okButton.setStyleName(AON.CSS.aonOkButton());
    	okButton.setText( AON.MSG.accept());
    	okButton.addClickHandler(e -> {
    		okButton.setEnabled(false);
    		
    		if(AonStringUtils.isBlank(type.getValue())) {
    			okButton.setEnabled(true);
    			AonMessagePanel.showWarning(messagePanel, "El campo tipo es obligatorio");
    		} else if(AonStringUtils.isBlank(taskHolder.getValue()) ) {
    			okButton.setEnabled(true);
    			AonMessagePanel.showWarning(messagePanel, "El operario es obligatorio");
    		} else {
    			project.setDomain(new Domain().setId(domainId));
        		project.setType(AonStringUtils.isBlank(type.getValue()) ? null : new ProjectType().setId(Integer.parseInt(type.getValue())));
        		project.setRegistry(new Registry().setId(registry));
        		project.setName(name.getValue());
        		project.setAlias(alias.getValue());
        		project.setDate(date.getValue());
        		project.setActive(true);
        		
        		if(AonStringUtils.isNotBlank(activity.getValue())){
        			ProjectActivity projectActivity = new ProjectActivity()
					.setActive(true)
					.setActivityType(new ActivityType().setId(Integer.parseInt(activity.getValue())))
					.setDomain(domainId)
					.setProject(project.getId());
        			
        			List<ProjectActivity> projectActivities = new ArrayList<>();
        			projectActivities.add(projectActivity);
        			
        			project.setProjectActivities(projectActivities);
        		}
        		
        		ProjectHolder projectHolder = new ProjectHolder();
        		projectHolder.setDomain(domainId);
        		projectHolder.setStartDate(date.getValue());
        		projectHolder.setWorkgroup(AonStringUtils.isBlank(workgroup.getValue()) ? null : new Workgroup().setId(Integer.parseInt(workgroup.getValue())));
        		
        		TaskHolder taskHolderObj = null;
        		if(!AonStringUtils.isBlank(taskHolder.getValue())){
        			taskHolderObj = new TaskHolder();
        			taskHolderObj.setId(Integer.parseInt(taskHolder.getValue()));
        		}
        		projectHolder.setTaskHolder(taskHolderObj);
        		
        		project.setProjectHolder(projectHolder);
        		
        		commonService.saveProject(domainName, getAbsoluteLeft(), user, project, new AsyncCallback<Project>() {

    				@Override
    				public void onSuccess(Project project) {
    					hide();
    					callback.onAccept(project);
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
				name.setFocus(true);
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
		row.add(w2);
		
		return row;
	}
	
	private void getAviableProjectType(Consumer<List<ProjectType>> success) {
		commonService.getAviableProjectType(domainName, domainId, user, new AsyncCallback<List<ProjectType>>() {
			
			@Override
			public void onSuccess(List<ProjectType> projectTypesDb) {
				projectTypes = projectTypesDb;
				success.accept(projectTypes);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				AonMessagePanel.showError(messagePanel, "Error obtenci\u00f3n tipos de expedientes. " + caught.getMessage());
			}
			
		});
	}
	
	private void getCustomer(Consumer<CustomerFull> success) {
		commonService.getCustomer(domainName, domainId, user, registry, new AsyncCallback<CustomerFull>() {
			
			@Override
			public void onSuccess(CustomerFull customerDb) {
				customer = customerDb;
				success.accept(customer);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				AonMessagePanel.showError(messagePanel, "Error obtenci\u00f3n tipos de expedientes. " + caught.getMessage());
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
	
	private void getActivityTypes(Consumer<List<ActivityType>> success) {
		commonService.getActivityTypes(domainName, domainId, user, new AsyncCallback<List<ActivityType>>() {
			
			@Override
			public void onSuccess(List<ActivityType> activityTypeDb) {
				activityType = activityTypeDb;
				success.accept(activityType);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				AonMessagePanel.showError(messagePanel, "Error obtenci\u00f3n actividades. " + caught.getMessage());
			}
			
		});
	}

}
