package com.esferalia.aon.gwt.marketing.client.project;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomCard;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDateBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDockLayout;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomListBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomTextBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog.AonAcceptDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonProjectHolderPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonProjectHolderPanel.AonProjectHolderPanelCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.occam.api.model.ActivityType;
import com.esferalia.aon.occam.api.model.ProjectParams;
import com.esferalia.aon.occam.api.model.project.ProjectActivity;
import com.esferalia.aon.occam.api.model.project.ProjectHolder;
import com.esferalia.aon.occam.api.model.project.ProjectType;
import com.esferalia.aon.occam.api.model.registry.Project;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

public abstract class ProjectEntryPanel extends AonCustomDockLayout {
	
	// ------------------------------------------------- CommonServiceAsync
	
	static CommonServiceAsync commonService;
	
	private static void initializeCommonService() {
		if (commonService == null) {
			CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
			commonService = new CommonServiceAsyncDecorator(commonServiceRaw);
		}
	}
	
	// ------------------------------------------------- Variables
	
	private final String EMPTY_STRING = "";
	
	private Integer position = -1;
	private AonToolbarButton previusProject;
	private Label projectIteration;
	private AonToolbarButton nextProject;
	
	private ScrollPanel scrollPanel;
	private HTMLPanel container;
	
	private HTMLPanel messagePanel = new HTMLPanel(EMPTY_STRING);
	
	private HTMLPanel gridPanel = new HTMLPanel(EMPTY_STRING);
	
	// ProjectInfo (Table 1)
	private ProjectStatusSelect projectStatus;
	private AonCustomListBox type = new AonCustomListBox("Tipo");
	private AonCustomTextBox name = new AonCustomTextBox("Nombre");
	private AonCustomTextBox alias = new AonCustomTextBox("Alias");
	private AonCustomDateBox date = new AonCustomDateBox("Fecha");
	private AonCustomListBox activity = new AonCustomListBox("Actividad");
	
	// ProjectHolders
	private ProjectHolderTable projectHolderTable;
	
	private ProjectModuleOptions options;

	private List<ProjectType> projectTypes;
	private List<ProjectHolder> projectHolders;
	private List<ActivityType> activityType;
	private Project project;
	private Integer count;
	
	private Integer officeDomain;

	public ProjectEntryPanel(ProjectModuleOptions options, Integer officeDomain) {
		super("Expediente");
		
		this.options = options;
		this.officeDomain = officeDomain;
		
		initializeCommonService();
		
		addButtonsToolbar();
		hideSearchWidget();
		
		scrollPanel = new ScrollPanel();
		
		container = new HTMLPanel("");
		container.addStyleName(AON.CSS.aonFlexColumn());
		
		container.add(messagePanel);
		
		scrollPanel.setWidget(container);
		
		add(scrollPanel);
	}
	
	@Override
	protected void onClearFilter() {}
	
	private void addButtonsToolbar() {
		AonToolbarButton backButton = new AonToolbarButton( "Volver", AON.CSS.aonIconBack());
		backButton.addClickHandler(e -> onBackClick());
		addToolbarButton(backButton);
		
		AonToolbarButton saveButton = new AonToolbarButton( "Guardar Expediente", AON.CSS.aonIconSave());
		saveButton.addClickHandler(e -> {
			saveButton.setEnabled(false);
			
			if(AonStringUtils.isBlank(type.getValue())) {
				saveButton.setEnabled(true);
    			AonMessagePanel.showWarning(messagePanel, "El campo tipo es obligatorio");
    		} else {
    			AonMessagePanel.showLoading(messagePanel, "Guardando expediente ...");
    			
    			project.setType(AonStringUtils.isBlank(type.getValue()) ? null : new ProjectType().setId(Integer.parseInt(type.getValue())));
        		project.setName(name.getValue());
        		project.setAlias(alias.getValue());
        		project.setDate(date.getValue());
        		project.setActive(projectStatus.getValue());
        		
        		if(!AonStringUtils.isBlank(activity.getValue())) {
        			if(project.getProjectActivities().isEmpty()) {
	        			ProjectActivity projectActivity = new ProjectActivity()
	    					.setActive(true)
	    					.setActivityType(new ActivityType().setId(Integer.parseInt(activity.getValue())))
	    					.setDomain( officeDomain )
	    					.setProject(project.getId());
	                    			
	        			project.getProjectActivities().add(projectActivity);
        			} else {
        				project.getProjectActivities().get(0).setActivityType(new ActivityType().setId(Integer.parseInt(activity.getValue())));
        			}
        		} else
        			project.getProjectActivities().stream().forEach(act -> act.setRemoved(true));
        		
        		commonService.saveProject(options.getDomainName(), options.getDomain(), options.getUser(), project, new AsyncCallback<Project>() {

    				@Override
    				public void onSuccess(Project savedProject) {
    					AonMessagePanel.showSuccess(messagePanel, "Expediente guardado correctamente");
    					saveButton.setEnabled(true);
    					
    					if(!savedProject.isActive()) {
    						// Close projectHolder if it is open
    						Optional<ProjectHolder> projectHolder = savedProject.getProjectHolders().stream().filter(ph -> null == ph.getEndDate()).findFirst();
    						if(projectHolder.isPresent()) {
    							projectHolder.get().setEndDate(new Date());
    							
    							AonMessagePanel.showLoading(messagePanel, "Cerrando periodo operario ...");
    							
    							commonService.saveProjectHolder(options.getDomainName(), options.getDomain(), options.getUser(), projectHolder.get(), new AsyncCallback<ProjectHolder>() {
    								
            	    				@Override
            	    				public void onSuccess(ProjectHolder projectHolder) {
            	    					AonMessagePanel.showSuccess(messagePanel, "Operario cerrado correctamente");
            	    					setProject(savedProject, position);
            	    				}
            	    				
            	    				@Override
            	    				public void onFailure(Throwable caught) {}
            	    				
            	    			});
    						}
    					}
    				}
    				
    				@Override
    				public void onFailure(Throwable caught) {
    					AonMessagePanel.showError(messagePanel, caught.getMessage());
    				}
    			});
    		}
			
			
			
		});
		addToolbarButton(saveButton);
		
		AonToolbarButton deleteButton = new AonToolbarButton( "Borrar Expediente", AON.CSS.aonIconDelete());
		deleteButton.addClickHandler(e -> {
			deleteButton.setEnabled(false);
			AonDialog dialog = new AonDialog("Eliminaci\u00f3n Expediente",
					new HTML("Se va a proceder a eliminar el expediente <b>" + project.getName() + "</b>.<br>\u00bfEsta seguro que desea proceder con la eliminaci\u00f3n\u003f. Este proceso ser\u00e1 irreversible"));
			
			dialog.confirm(new AonAcceptDialogCallback() {

				@Override
				public void onCancel() {
					deleteButton.setEnabled(true);
				}

				@Override
				public void onAccept() {
					onProjectDeleteClick(project.getId());
				}
			});
		});
		addToolbarButton(deleteButton);
		
		previusProject = new AonToolbarButton("Anterior Expediente", AON.CSS.aonIconLeft());
		previusProject.setEnabled(position > 0);
		previusProject.addClickHandler(e -> {
			position = position - 1;
			getNextProject(position, nextProject -> onProjectSelectionChange(nextProject, position));
		});
		addToolbarButton(previusProject);
		
		getProjectListCount(count -> {
			this.count = count;
			
			projectIteration = new Label((null == project ? "ND" : (position + 1)) + " / " + this.count);
			addToolbarButton(projectIteration);
			
			nextProject = new AonToolbarButton("Siguiente Expediente", AON.CSS.aonIconRight());
			nextProject.setEnabled(position < (this.count - 1));
			nextProject.addClickHandler(e -> {
				position = position + 1;
				getNextProject(position, nextProject -> onProjectSelectionChange(nextProject, position));
			});
			addToolbarButton(nextProject);
		});
		
		
	}
	
	private void showProjectHolderDialog() {
		new AonProjectHolderPanel( options.getDomainName(), options.getDomain(), options.getUser(), project.getId(), officeDomain, projectHolders, new AonProjectHolderPanelCallback() {
			
			@Override
			public void onCancel() {}
			
			@Override
			public void onAccept(ProjectHolder projectHolder) {
				setProject(project, position);
			}
		});
	}
	
	

	private void getNextProject(Integer nextPos, Consumer<Project> projectLoad) {
		ProjectParams params = getProjectListParams();
		params.setOffset(nextPos);
		params.setLimit(1);
		
		commonService.getProjects(params, new AsyncCallback<List<Project>>() {
			
			@Override
			public void onSuccess(List<Project> projects) {
				projectLoad.accept(projects.get(0));
			}
			
			@Override
			public void onFailure(Throwable caught) {
				// Error
			}
		});
	}

	private void hideNavegationOptions() {
		previusProject.setVisible(false);
		projectIteration.setVisible(false);
		nextProject.setVisible(false);
	}

	private void showNavegationOptions() {
		previusProject.setVisible(true);
		projectIteration.setVisible(true);
		nextProject.setVisible(true);
	}

	public void setProject(Project project, Integer sellectPos) {
		this.position = sellectPos;
		this.project = project;
		setProject(project, finish -> {
			if(position >= 0) showNavegationOptions();
			else hideNavegationOptions();
		});
	}
	
	public void setProject(Project project, Consumer<Void> finish) {
		this.project = project;
		getProjectHolders(project.getId(), projectHoldersDb -> {
			getAviableProjectType(projectTypes -> {
				getActivityTypes(projectActivities -> {
					getProjectListCount(count -> {
						this.count = count;
						
						this.projectHolders = projectHoldersDb;
						this.project.setProjectHolders(projectHoldersDb);
						
						setToolbarTitle(project.getName());
						projectIteration.setText((null == project ? "ND" : (position + 1)) + " / " + count);
						previusProject.setEnabled(position > 0);
						nextProject.setEnabled(position < (this.count - 1));
						
						paintView();
						
						finish.accept(null);
					});
				});
			});
		});
	}
	
	private void paintView() {
		gridPanel.clear();
		gridPanel.setStyleName(AON.CSS.aonGridTwoCols());
		gridPanel.getElement().getStyle().setProperty("padding", "0 1rem");
		
		projectStatus = new ProjectStatusSelect(project.isActive());
		AonCustomCard infoCard = new AonCustomCard("Informaci\u00f3n General", projectStatus);
		infoCard.setToolbarWidgetShown();
		
		type.clearItems();
		type.addItem("-", "");
		projectTypes.forEach(projectType -> type.addItem(projectType.getDescription(), projectType.getId().toString()));
		
		activity.clearItems();
		activity.addItem("-", "");
		activityType.forEach(act -> activity.addItem(act.getDescription(), act.getId().toString()));
		
		type.setValue(project.getType().getId().toString());
		date.setValue(project.getDate());
		name.setValue(project.getName());
		alias.setValue(project.getAlias());
		activity.setValue(project.getProjectActivities().isEmpty() || null == project.getProjectActivities().get(0).getActivityType() ? "" : project.getProjectActivities().get(0).getActivityType().getId().toString());
		
		HTMLPanel table = createTable();
		table.add(createRow(type, date));
		table.add(createRow(name, alias));
		table.add(createRow(activity, null));
		
		infoCard.add(table);
		gridPanel.add(infoCard);
		
		AonToolbarButton newButton = new AonToolbarButton( "Nuevo Operario", AON.CSS.aonIconAdd());
		newButton.addClickHandler(e -> showProjectHolderDialog());
		AonCustomCard projectHolderCard = new AonCustomCard("Operarios", newButton);
		
		projectHolderTable = new ProjectHolderTable(options.getDomainName(), options.getDomain(), options.getUser(), this.project, officeDomain) {

			@Override
			protected void onDelete() {
				setProject(project, position);
			}

			@Override
			protected void onProjectHolderAdd() {
				setProject(project, position);
			}
			
		};
		
		projectHolderCard.add(projectHolderTable);
		gridPanel.add( projectHolderCard );
		
		container.add(gridPanel);
	}
	
	private HTMLPanel createRow(Widget w1, Widget w2) {
		HTMLPanel panel = new HTMLPanel("");
		panel.setStyleName(AON.CSS.aonItemFlex());
		
		panel.add(w1);
		
		if(null != w2) panel.add(w2);
		
		return panel;
	}
	
	private HTMLPanel createTable() {
		HTMLPanel table = new HTMLPanel("");
		table.setStyleName(AON.CSS.aonFlexColumn());
		return table;
	}

	private void getProjectHolders(Integer projectId, Consumer<List<ProjectHolder>> success) {
		commonService.getProjectHolders(options.getDomainName(), options.getDomain(), options.getUser(), projectId, new AsyncCallback<List<ProjectHolder>>() {
			
			@Override
			public void onSuccess(List<ProjectHolder> projectHoldersDb) {
				success.accept(projectHoldersDb);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				AonMessagePanel.showError(messagePanel, "Error obteniendo expediente: " + caught.getMessage());
			}
			
		});
	}

	private void getAviableProjectType(Consumer<List<ProjectType>> success) {
		commonService.getAviableProjectType(options.getDomainName(), options.getDomain(), options.getUser(), officeDomain, new AsyncCallback<List<ProjectType>>() {
			
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
	
	private void getActivityTypes(Consumer<List<ActivityType>> success) {
		commonService.getActivityTypes(options.getDomainName(), options.getDomain(), options.getUser(), officeDomain, new AsyncCallback<List<ActivityType>>() {
			
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
	
	protected abstract void onBackClick();
	protected abstract void onProjectDeleteClick(Integer projectId);
	
	protected abstract void getProjectListCount(Consumer<Integer> finish);
	protected abstract ProjectParams getProjectListParams();
	
	protected abstract void onProjectSelectionChange(Project project, Integer position);

}
