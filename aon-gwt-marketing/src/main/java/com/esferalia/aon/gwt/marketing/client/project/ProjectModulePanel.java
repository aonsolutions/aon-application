package com.esferalia.aon.gwt.marketing.client.project;

import java.util.List;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDateBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDockLayout;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomListBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonProjectPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonProjectPanel.AonProjectPanelCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.occam.api.model.ProjectParams;
import com.esferalia.aon.occam.api.model.project.ProjectType;
import com.esferalia.aon.occam.api.model.registry.Project;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.SimplePanel;


public abstract class ProjectModulePanel extends AonCustomDockLayout {
	
	private HTMLPanel container;
	private HTMLPanel messagePanel = new HTMLPanel("");
	
	private SimplePanel centerPanel;
	
	private AonCustomListBox type = new AonCustomListBox("Tipo");
	private AonCustomDateBox date = new AonCustomDateBox("Fecha");
	
	private AonCustomListBox sort = new AonCustomListBox("Ordenar Por");
	private AonCustomListBox asc = new AonCustomListBox("Orden");
	
	private ProjectModuleOptions options;
	
	private ProjectPanel projectPanel;
	
	private Integer customerId;
	
	private static CommonServiceAsync commonService;
	
	private static void initializeCommonService() {
		if (commonService == null) {
			CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
			commonService = new CommonServiceAsyncDecorator(commonServiceRaw);
		}
	}
	
	public ProjectModulePanel(ProjectModuleOptions options, Integer customer) {
		super(customer <= 0 ? "Expedientes" : null);
		
		initializeCommonService();
		
		this.options = options;
		this.customerId = customer > 0 ? customer : null;
		
		if(null == customerId) {
			addButtonsToolbar();
			
			if(null == customerId) {
				setSearchPlaceholder("Busqueda por nombre, alias, titular...");
				
				addKeyUpHandler(e -> {
					String value = getSearchTextBox().getValue();
					if(AonStringUtils.isNotBlank(value) && value.length() > 2) {
						onSearch();
					} else if(AonStringUtils.isBlank(value)) {
						onSearch();
					}
				});
			} else
				showSeachButton();
			
			
			type.addItem("-", "");
			getProjectTypes(projectTypes -> projectTypes.forEach(projectType -> type.addItem(projectType.getDescription(), projectType.getId().toString())));
			type.addChangeHandler(e -> onSearch());
			
			date.addValueChangeHandler(e -> onSearch());
			
			addFilterWidget(type);
			addFilterWidget(date);
			
			sort.addItem("Nombre", "name");
			sort.addItem("Tipo", "type");
			sort.addItem("Fecha", "date");
			sort.getListBox().addChangeHandler(event -> onSearch());
			
			asc.addItem("Ascendente", "true");
			asc.addItem("Descendete", "false");
			asc.getListBox().addChangeHandler(event -> onSearch());
			
			addSortWidget(sort);
			addSortWidget(asc);
		}
		
		container = new HTMLPanel("");
		container.addStyleName(AON.CSS.aonFlexColumn());
		
		container.add(messagePanel);
	
		centerPanel = new SimpleLayoutPanel();
		centerPanel.setHeight("100%");
		centerPanel.getElement().getStyle().setProperty("margin-left", "1rem");
		
		container.add(centerPanel);
		
		add(container);
		onSearch();
	}

	@Override
	protected void onClearFilter() {
		getSearchTextBox().setValue(null, false);
		
		projectPanel.resetSearchOffset();
		
		type.setValue("");
		date.setValue(null);
		
		onSearch();
	}

	private void addButtonsToolbar() {
		AonToolbarButton newButton = new AonToolbarButton( "Nuevo Expediente", AON.CSS.aonIconAdd());
		newButton.addClickHandler(e -> showSellerDialog());
		
		addToolbarButton(newButton);
	}

	private void showSellerDialog() {
		 new AonProjectPanel( options.getDomainName(), options.getDomain(), options.getUser(), customerId, new AonProjectPanelCallback() {
				
				@Override
				public void onCancel() {}
				
				@Override
				public void onAccept(Project project) {
					onProjectCreate(project);
				}
		});
	}

	public void onSearch() {
		ProjectParams params = getWidgetParams();
		centerPanel.clear();
		projectPanel = new ProjectPanel(params, customerId) {

			@Override
			protected void onProjectOpen(Project project) {
				onProjectSelect(project);
			}

			@Override
			protected void onShowErrorMessage(String errorMessage) {
				AonMessagePanel.showError(messagePanel, errorMessage);
			}

			@Override
			protected void onShowSuccessMessage(String successMessage) {
				AonMessagePanel.showSuccess(messagePanel, successMessage);
			}

			@Override
			protected void onShowLoadingMessage(String loadingMessage) {
				AonMessagePanel.showLoading(messagePanel, loadingMessage);
			}

			@Override
			protected void onProjectCreation(Project project) {
				onProjectCreate(project);
			}
		
		};
		
		centerPanel.setWidget(projectPanel);
	}

	public ProjectParams getWidgetParams() {
		ProjectParams params = new ProjectParams()
			.setDomainName(options.getDomainName())
			.setDomain(options.getDomain())
			.setUser(options.getUser());
		
		if(null != customerId)
			params.setRegistry(customerId);
		else {
			params.setDescription(getSearchTextBox().getValue());
			params.setDate(date.getValue());
			params.setProjectType(AonStringUtils.isBlank(type.getValue()) ? null : Integer.parseInt(type.getValue()));
		}
		
		params.setOrderBy(sort.getValue())
			.setAsc(Boolean.parseBoolean(asc.getValue()))
			;
		
		return params;
	}
	
	public void getSellerListCount(Consumer<Integer> finish) {
		if(null == projectPanel || null ==  projectPanel.getTable()) finish.accept(0);
		
		projectPanel.getSellerListCount(count -> {
			finish.accept(count);
		});
	}
	
	private void getProjectTypes(Consumer<List<ProjectType>> finish) {
		commonService.getAviableProjectType(options.getDomainName(), options.getDomain(), options.getUser(), new AsyncCallback<List<ProjectType>>() {
			
			@Override
			public void onSuccess(List<ProjectType> projectTypes) {
				finish.accept(projectTypes);
			}
			
			@Override
			public void onFailure(Throwable arg0) {
				AonMessagePanel.showError(messagePanel, "Error obteniendo tipos de expedientes. " + arg0.getMessage());
			}
		});
	}
	public Integer getSellerListPosition(Integer sellerId) {
		return null == sellerId || null == projectPanel ? 0 : projectPanel.getSellerListPosition(sellerId);
	}
	
	public ProjectParams getProjectListParams() {
		return getWidgetParams();
	}
	
	protected abstract void onProjectSelect(Project project);
	protected abstract void onProjectCreate(Project project);

}
