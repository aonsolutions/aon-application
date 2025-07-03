package com.esferalia.aon.gwt.marketing.client.taskholder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomCard;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDockLayout;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomListBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomTextBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog.AonAcceptDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTaskHolderWorkgroupPanel.AonTaskHolderWorkgroupPanelCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTaskHolderWorkgroupPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.marketing.client.project.ProjectStatusSelect;
import com.esferalia.aon.occam.api.model.TaskHolderParams;
import com.esferalia.aon.occam.api.model.registry.RegistryMedia;
import com.esferalia.aon.occam.api.model.security.TaskHolderWorkgroup;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.task.TaskHolder;
import com.esferalia.aon.occam.api.model.type.MediaType;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

public abstract class TaskHolderEntryPanel extends AonCustomDockLayout {
	
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
	private AonCustomTextBox name = new AonCustomTextBox("Nombre");
	private AonCustomTextBox document = new AonCustomTextBox("Documento");
	private AonCustomTextBox email = new AonCustomTextBox("Email");
	private AonCustomTextBox mobile = new AonCustomTextBox("M\u00f3vil");
	private AonCustomListBox userLB = new AonCustomListBox("Usuario");
	
	// ProjectHolders
	private WorkgroupTable projectHolderTable;
	
	private TaskHolderModuleOptions options;

	private TaskHolder taskHolder;
	private List<RegistryMedia> medias = new ArrayList<>();
	private List<User> users = new ArrayList<>();
	private Integer count;

	public TaskHolderEntryPanel(TaskHolderModuleOptions options) {
		super("Expediente");
		
		this.options = options;
		
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
		
		AonToolbarButton saveButton = new AonToolbarButton( "Guardar Operario", AON.CSS.aonIconSave());
		saveButton.addClickHandler(e -> {
			saveButton.setEnabled(false);
			
			if(AonStringUtils.isBlank(name.getValue()) || AonStringUtils.isBlank(document.getValue())) {
				saveButton.setEnabled(true);
    			AonMessagePanel.showWarning(messagePanel, "El campo nombre y documento son obligatorio");
    		} else {
    			AonMessagePanel.showLoading(messagePanel, "Guardando operario ...");
    			
    			taskHolder.setName(name.getValue());
    			taskHolder.setDocument(document.getValue());
        		taskHolder.setActive(projectStatus.getValue());
        		taskHolder.setUserId(AonStringUtils.isBlank(userLB.getValue()) ? null : Integer.parseInt(userLB.getValue()));
        		
        		commonService.saveTaskHolder(options.getDomainName(), options.getDomain(), options.getUser(), taskHolder, new AsyncCallback<TaskHolder>() {

    				@Override
    				public void onSuccess(TaskHolder savedProject) {
    					AonMessagePanel.showSuccess(messagePanel, "Operario guardado correctamente");
    					saveButton.setEnabled(true);
    				}
    				
    				@Override
    				public void onFailure(Throwable caught) {
    					AonMessagePanel.showError(messagePanel, caught.getMessage());
    				}
    			});
        		
        		// Save email
        		Optional<RegistryMedia> emailOpt = medias.stream().filter(m -> m.getMedia().equals(MediaType.EMAIL)).findFirst();
        		
        		if(emailOpt.isEmpty() && AonStringUtils.isNotBlank(email.getValue())) {
        			RegistryMedia rmedia = new RegistryMedia()
        					.setDomain(options.getDomain())
        					.setMedia(MediaType.EMAIL)
        					.setRegistry(taskHolder.getRegistry())
        					.setValue(email.getValue());
        			
        			commonService.saveRegistryMedia(options.getDomainName(), options.getDomain(), options.getUser(), rmedia, new AsyncCallback<RegistryMedia>() {

        				@Override
        				public void onSuccess(RegistryMedia registryMedia) {}
        				
        				@Override
        				public void onFailure(Throwable caught) {}
        				
        			});
        		} else if(!emailOpt.isEmpty() && !AonStringUtils.equalsIgnoreCase(emailOpt.get().getValue(), email.getValue())) {
        			emailOpt.get().setValue(email.getValue());
        			
        			commonService.saveRegistryMedia(options.getDomainName(), options.getDomain(), options.getUser(), emailOpt.get(), new AsyncCallback<RegistryMedia>() {

        				@Override
        				public void onSuccess(RegistryMedia registryMedia) {}
        				
        				@Override
        				public void onFailure(Throwable caught) {}
        				
        			});
        		}
        		
        		// Save cellular
        		Optional<RegistryMedia> cellularlOpt = medias.stream().filter(m -> m.getMedia().equals(MediaType.CELLULAR)).findFirst();
        		
        		if(cellularlOpt.isEmpty() && AonStringUtils.isNotBlank(mobile.getValue())) {
        			RegistryMedia rmedia = new RegistryMedia()
        					.setDomain(options.getDomain())
        					.setMedia(MediaType.CELLULAR)
        					.setRegistry(taskHolder.getRegistry())
        					.setValue(mobile.getValue());
        			
        			commonService.saveRegistryMedia(options.getDomainName(), options.getDomain(), options.getUser(), rmedia, new AsyncCallback<RegistryMedia>() {

        				@Override
        				public void onSuccess(RegistryMedia registryMedia) {}
        				
        				@Override
        				public void onFailure(Throwable caught) {}
        				
        			});
        		} else if(!cellularlOpt.isEmpty() && !AonStringUtils.equalsIgnoreCase(cellularlOpt.get().getValue(), mobile.getValue())) {
        			cellularlOpt.get().setValue(mobile.getValue());
        			
        			commonService.saveRegistryMedia(options.getDomainName(), options.getDomain(), options.getUser(), cellularlOpt.get(), new AsyncCallback<RegistryMedia>() {

        				@Override
        				public void onSuccess(RegistryMedia registryMedia) {}
        				
        				@Override
        				public void onFailure(Throwable caught) {}
        				
        			});
        		}
        		
    		}
			
			
			
		});
		addToolbarButton(saveButton);
		
		AonToolbarButton deleteButton = new AonToolbarButton( "Borrar Operario", AON.CSS.aonIconDelete());
		deleteButton.addClickHandler(e -> {
			deleteButton.setEnabled(false);
			AonDialog dialog = new AonDialog("Eliminaci\u00f3n Operario",
					new HTML("Se va a proceder a eliminar el operario <b>" + taskHolder.getName() + "</b>.<br>\u00bfEsta seguro que desea proceder con la eliminaci\u00f3n\u003f. Este proceso ser\u00e1 irreversible"));
			
			dialog.confirm(new AonAcceptDialogCallback() {

				@Override
				public void onCancel() {
					deleteButton.setEnabled(true);
				}

				@Override
				public void onAccept() {
					onTaskHolderDeleteClick(taskHolder.getId());
					deleteButton.setEnabled(true);
				}
			});
		});
		addToolbarButton(deleteButton);
		
		previusProject = new AonToolbarButton("Anterior Expediente", AON.CSS.aonIconLeft());
		previusProject.setEnabled(position > 0);
		previusProject.addClickHandler(e -> {
			position = position - 1;
			getNextTaskHolder(position, nextProject -> onTaskHolderSelectionChange(nextProject, position));
		});
		addToolbarButton(previusProject);
		
		getTaskHolderListCount(count -> {
			this.count = count;
			
			projectIteration = new Label((null == taskHolder ? "ND" : (position + 1)) + " / " + this.count);
			addToolbarButton(projectIteration);
			
			nextProject = new AonToolbarButton("Siguiente Expediente", AON.CSS.aonIconRight());
			nextProject.setEnabled(position < (this.count - 1));
			nextProject.addClickHandler(e -> {
				position = position + 1;
				getNextTaskHolder(position, nextProject -> onTaskHolderSelectionChange(nextProject, position));
			});
			addToolbarButton(nextProject);
		});
		
		
	}

	private void getNextTaskHolder(Integer nextPos, Consumer<TaskHolder> projectLoad) {
		TaskHolderParams params = getTaskHolderListParams();
		params.setOffset(nextPos);
		params.setLimit(1);
		
		commonService.getTaskHolderList(params, new AsyncCallback<List<TaskHolder>>() {
			
			@Override
			public void onSuccess(List<TaskHolder> projects) {
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

	public void setTaskHolder(TaskHolder taskHolder, Integer sellectPos) {
		this.position = sellectPos;
		this.taskHolder = taskHolder;
		setTaskHolder(taskHolder, finish -> {
			if(position >= 0) showNavegationOptions();
			else hideNavegationOptions();
		});
	}
	
	public void setTaskHolder(TaskHolder taskHolder, Consumer<Void> finish) {
		this.taskHolder = taskHolder;
		getTaskHolder(this.taskHolder.getId(), taskHolderIt -> {
			getTaskHolderListCount(count -> {
				getRegistryMedias(this.taskHolder.getId(), mediasIt -> {
					getUsers(userList -> {
						this.count = count;
						
						setToolbarTitle(taskHolder.getName());
						projectIteration.setText((null == taskHolder ? "ND" : (position + 1)) + " / " + count);
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
		
		projectStatus = new ProjectStatusSelect(taskHolder.isActive());
		AonCustomCard infoCard = new AonCustomCard("Informaci\u00f3n General", projectStatus);
		infoCard.setToolbarWidgetShown();
		
		name.setValue(taskHolder.getName());
		document.setValue(taskHolder.getDocument());
		
		Optional<RegistryMedia> emailOpt = medias.stream().filter(m -> m.getMedia().equals(MediaType.EMAIL)).findFirst();
		Optional<RegistryMedia> cellularlOpt = medias.stream().filter(m -> m.getMedia().equals(MediaType.CELLULAR)).findFirst();
		
		email.setValue(emailOpt.isEmpty() ? "" : emailOpt.get().getValue());
		mobile.setValue(cellularlOpt.isEmpty() ? "" : cellularlOpt.get().getValue());
		
		userLB.clearItems();
		userLB.addItem("-", "");
		users.forEach(user -> userLB.addItem(user.getName() + "( " + user.getLogin() + (null != user.getDomain().getId() && !user.getDomain().getId().equals(options.getDomain()) ? " - Dom. Padre" : "" ) + " )", null == user.getId() ? "" : user.getId().toString()));
		userLB.setValue(null == taskHolder.getUserId() ? "" : taskHolder.getUserId().toString());
		userLB.addChangeHandler(e -> checkUserAviable());
		
		HTMLPanel table = createTable();
		table.add(createRow(name, document));
		table.add(createRow(email, mobile));
		table.add(createRow(userLB, null));
		
		infoCard.add(table);
		gridPanel.add(infoCard);
		
		AonToolbarButton newButton = new AonToolbarButton( "Nuevo Grupo Trabajo", AON.CSS.aonIconAdd());
		newButton.addClickHandler(e -> showTaskHolderWorkgroupDialog());
		
		AonCustomCard workgroupCard = new AonCustomCard("Grupos de Trabajo", newButton);
		
		projectHolderTable = new WorkgroupTable(options.getDomainName(), options.getDomain(), options.getUser(), this.taskHolder) {

			@Override
			protected void onDelete() {
				setTaskHolder(taskHolder, position);
			}

			@Override
			protected void onTaskHolderWorkgroupAdd() {
				setTaskHolder(taskHolder, position);
			}
			
		};
		
		workgroupCard.add(projectHolderTable);
		gridPanel.add( workgroupCard );
		
		container.add(gridPanel);
	}
	
	private void checkUserAviable() {
		if(AonStringUtils.isNotBlank(userLB.getValue())){
			Integer originalUserId = taskHolder.getUserId();
			
			commonService.getTaskHolderList(new TaskHolderParams().setDomainName(options.getDomainName()).setDomain(options.getDomain()).setUser(options.getUser()).setOffset(0).setLimit(Integer.MAX_VALUE), new AsyncCallback<List<TaskHolder>>() {
				
				@Override
				public void onSuccess(List<TaskHolder> taskHolderList) {
					List<Integer> taskHolderUsers = taskHolderList.stream().map(th -> th.getUserId()).filter(Objects::nonNull).collect(Collectors.toList());
					if(taskHolderUsers.contains(Integer.parseInt(userLB.getValue()))) {
						AonMessagePanel.showError(messagePanel, "No se puede seleccionar este usuario ya que esta en uso por otro operario");
						userLB.setValue(null == originalUserId ? "" : originalUserId.toString());
					}		
				}
				
				@Override
				public void onFailure(Throwable arg0) {}
				
			});
		}
	}

	private void showTaskHolderWorkgroupDialog() {
		new AonTaskHolderWorkgroupPanel( options.getDomainName(), options.getDomain(), options.getUser(), this.taskHolder,  new AonTaskHolderWorkgroupPanelCallback() {
			
			@Override
			public void onCancel() {}
			
			@Override
			public void onAccept(TaskHolderWorkgroup taskHolderWorkgroup) {
				setTaskHolder(taskHolder, position);
			}
		});
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

	public void showError(String message) {
		AonMessagePanel.showError(messagePanel, message);
	}
	
	public void showSuccess(String message) {
		AonMessagePanel.showSuccess(messagePanel, message);
	}
	
	private void getTaskHolder(Integer taskHolderId, Consumer<TaskHolder> success) {
		commonService.getTaskHolder(options.getDomainName(), options.getDomain(), options.getUser(), taskHolderId, new AsyncCallback<TaskHolder>() {
			
			@Override
			public void onSuccess(TaskHolder projectHoldersDb) {
				taskHolder = projectHoldersDb;
				success.accept(projectHoldersDb);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				AonMessagePanel.showError(messagePanel, "Error obteniendo operario: " + caught.getMessage());
			}
			
		});
	}
	
	private void getRegistryMedias(Integer taskHolderId, Consumer<List<RegistryMedia>> success) {
		commonService.getRegistryMedias(options.getDomainName(), options.getDomain(), options.getUser(), taskHolderId, new AsyncCallback<List<RegistryMedia>>() {
			
			@Override
			public void onSuccess(List<RegistryMedia> mediasDB) {
				medias = mediasDB;
				success.accept(medias);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				AonMessagePanel.showError(messagePanel, "Error obteniendo medias: " + caught.getMessage());
			}
			
		});
	}
	
	private void getUsers(Consumer<List<User>> success) {
		commonService.getUsersForTaskHolder(options.getDomainName(), options.getDomain(), options.getUser(), false, new AsyncCallback<List<User>>() {
			
			@Override
			public void onSuccess(List<User> UserDb) {
				users.clear();
				users.addAll(UserDb);
				
				if(null != taskHolder.getUserId()) {
					commonService.getUser(options.getDomainName(), options.getDomain(), options.getUser(), taskHolder.getUserId(),  new AsyncCallback<User>() {
						
						@Override
						public void onSuccess(User userDb) {
							users.add(userDb);
							success.accept(users);
						}
						
						@Override
						public void onFailure(Throwable caught) {
							AonMessagePanel.showError(messagePanel, "Error obtenci\u00f3n usuario. " + caught.getMessage());
						}
						
					});
				} else 
					success.accept(users);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				AonMessagePanel.showError(messagePanel, "Error obtenci\u00f3n usuarios. " + caught.getMessage());
			}
			
		});
	}

	protected abstract void onBackClick();
	protected abstract void onTaskHolderDeleteClick(Integer taskHolderId);
	
	protected abstract void getTaskHolderListCount(Consumer<Integer> finish);
	protected abstract TaskHolderParams getTaskHolderListParams();
	
	protected abstract void onTaskHolderSelectionChange(TaskHolder taskHolder, Integer position);

}
