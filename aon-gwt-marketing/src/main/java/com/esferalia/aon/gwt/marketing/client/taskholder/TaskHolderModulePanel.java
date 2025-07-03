package com.esferalia.aon.gwt.marketing.client.taskholder;

import java.util.List;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDockLayout;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomListBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTaskHolderPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTaskHolderPanel.AonTaskHolderPanelCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.occam.api.model.TaskHolderParams;
import com.esferalia.aon.occam.api.model.Workgroup;
import com.esferalia.aon.occam.api.model.task.TaskHolder;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.SimplePanel;


public abstract class TaskHolderModulePanel extends AonCustomDockLayout {
	
	private HTMLPanel container;
	private HTMLPanel messagePanel = new HTMLPanel("");
	
	private SimplePanel centerPanel;
	
	private AonCustomListBox status = new AonCustomListBox("Estado");
	private AonCustomListBox workgroup = new AonCustomListBox("Grupo Trabajo");
	
	private AonCustomListBox sort = new AonCustomListBox("Ordenar Por");
	private AonCustomListBox asc = new AonCustomListBox("Orden");
	
	private TaskHolderModuleOptions options;
	
	private TaskHolderPanel taskHolderPanel;
	
	private static CommonServiceAsync commonService;
	
	private static void initializeCommonService() {
		if (commonService == null) {
			CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
			commonService = new CommonServiceAsyncDecorator(commonServiceRaw);
		}
	}
	
	public TaskHolderModulePanel(TaskHolderModuleOptions options) {
		super("Operarios");
		
		initializeCommonService();
		
		this.options = options;
		
		addButtonsToolbar();
		
		
		setSearchPlaceholder("Busqueda por nombre, alias, documento...");
		
		addKeyUpHandler(e -> {
			String value = getSearchTextBox().getValue();
			if(AonStringUtils.isNotBlank(value) && value.length() > 2) {
				onSearch();
			} else if(AonStringUtils.isBlank(value)) {
				onSearch();
			}
		});
			
			
		status.addItem("-", "");
		status.addItem("Activo", "true");
		status.addItem("Inactivo", "false");
		status.addChangeHandler(e -> onSearch());

		workgroup.addItem("-", "");
		getWorkgroups(workgroups -> workgroups.forEach(w -> workgroup.addItem(w.getDescription(), w.getId().toString())));
		workgroup.addChangeHandler(e -> onSearch());
		
		addFilterWidget(status);
		addFilterWidget(workgroup);
		
		sort.addItem("Nombre", "name");
		sort.addItem("Estado", "status");
		sort.getListBox().addChangeHandler(event -> onSearch());
		
		asc.addItem("Ascendente", "true");
		asc.addItem("Descendete", "false");
		asc.getListBox().addChangeHandler(event -> onSearch());
		
		addSortWidget(sort);
		addSortWidget(asc);
		
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
		
		taskHolderPanel.resetSearchOffset();
		
		status.setValue("");
		workgroup.setValue("");
		
		onSearch();
	}

	private void addButtonsToolbar() {
		AonToolbarButton newButton = new AonToolbarButton( "Nuevo Operario", AON.CSS.aonIconAdd());
		newButton.addClickHandler(e -> showTaskHolderDialog());
		
		addToolbarButton(newButton);
	}

	private void showTaskHolderDialog() {
		new AonTaskHolderPanel( options.getDomainName(), options.getDomain(), options.getUser(), new AonTaskHolderPanelCallback() {
				
				@Override
				public void onCancel() {}
				
				@Override
				public void onAccept(TaskHolder taskHolder) {
					onTaskHolderCreate(taskHolder);
				}
		});
	}

	public void onSearch() {
		TaskHolderParams params = getWidgetParams();
		centerPanel.clear();
		taskHolderPanel = new TaskHolderPanel(params) {

			@Override
			protected void onTaskHolderOpen(TaskHolder taskHolder) {
				onTaskHolderSelect(taskHolder);
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
			protected void onTaskHolderCreation(TaskHolder taskHolder) {
				onTaskHolderCreate(taskHolder);
			}
		
		};
		
		centerPanel.setWidget(taskHolderPanel);
	}

	public TaskHolderParams getWidgetParams() {
		TaskHolderParams params = new TaskHolderParams()
			.setDomainName(options.getDomainName())
			.setDomain(options.getDomain())
			.setUser(options.getUser())
			.setDescription(getSearchTextBox().getValue())
			.setStatus(AonStringUtils.isBlank(status.getValue()) ? null : Boolean.parseBoolean(status.getValue()))
			.setWorkgroup(AonStringUtils.isBlank(workgroup.getValue()) ? null : Integer.parseInt(workgroup.getValue()))
			.setOrderBy(sort.getValue())
			.setAsc(Boolean.parseBoolean(asc.getValue()))
			;
		
		return params;
	}
	
	public void getTaskHolderListCount(Consumer<Integer> finish) {
		if(null == taskHolderPanel || null ==  taskHolderPanel.getTable()) finish.accept(0);
		
		taskHolderPanel.getTaskHolderListCount(count -> {
			finish.accept(count);
		});
	}
	
	private void getWorkgroups(Consumer<List<Workgroup>> finish) {
		commonService.getAviableWorkgroups(options.getDomainName(), options.getDomain(), options.getUser(), options.getDomain(), new AsyncCallback<List<Workgroup>>() {
			
			@Override
			public void onSuccess(List<Workgroup> workgroups) {
				finish.accept(workgroups);
			}
			
			@Override
			public void onFailure(Throwable arg0) {
				AonMessagePanel.showError(messagePanel, "Error obteniendo tipos de expedientes. " + arg0.getMessage());
			}
		});
	}
	
	public void showSuccess(String message) {
		AonMessagePanel.showSuccess(messagePanel, message);
	}

	public Integer getTaskHolderListPosition(Integer sellerId) {
		return null == sellerId || null == taskHolderPanel ? 0 : taskHolderPanel.getTaskHolderListPosition(sellerId);
	}
	
	public TaskHolderParams getTaskHolderListParams() {
		return getWidgetParams();
	}
	
	protected abstract void onTaskHolderSelect(TaskHolder taskHolder);
	protected abstract void onTaskHolderCreate(TaskHolder taskHolder);

}
