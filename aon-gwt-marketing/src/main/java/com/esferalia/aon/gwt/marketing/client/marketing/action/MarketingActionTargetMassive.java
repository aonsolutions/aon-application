package com.esferalia.aon.gwt.marketing.client.marketing.action;

import java.util.List;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDockLayout;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomListBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.gwt.marketing.client.marketing.MarketingModuleOptions;
import com.esferalia.aon.occam.api.model.Advertising;
import com.esferalia.aon.occam.api.model.MarketingAction;
import com.esferalia.aon.occam.api.model.MarketingActionParams;
import com.esferalia.aon.occam.api.model.MarketingActionTarget;
import com.esferalia.aon.occam.api.model.MarketingActionTargetMassiveParams;
import com.esferalia.aon.occam.api.model.project.ProjectActivity;
import com.esferalia.aon.occam.api.model.project.ProjectType;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.SimplePanel;

public abstract class MarketingActionTargetMassive extends SimplePanel {
	
	public static interface AonMarketingActionTargetMassivePanelCallback {
		void onAccept();
		void onCancel();
	}

	static CommonServiceAsync commonService;
	
	private static void initializeCommonService() {
		if (commonService == null) {
			CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
			commonService = new CommonServiceAsyncDecorator(commonServiceRaw);
		}
	}
	
	private AonCustomDockLayout dockLayout;
	private HTMLPanel container;
	private HTMLPanel messagePanel = new HTMLPanel("");
	
	private SimpleLayoutPanel centerPanel;
	
	private AonCustomListBox scope = new AonCustomListBox("Ambito");
	private AonCustomListBox entity = new AonCustomListBox("Entidad");
	private AonCustomListBox advertising = new AonCustomListBox("Propaganda");
	private AonCustomListBox projectType = new AonCustomListBox("Tipo Expediente");
	private AonCustomListBox projectActivity = new AonCustomListBox("Tipo Actividad");
	private AonCustomListBox status = new AonCustomListBox("Estado");
	private AonCustomListBox customer = new AonCustomListBox("Cliente");
	private AonCustomListBox mkAction = new AonCustomListBox("Acci\u00f3n Comercial");
	
	private AonCustomListBox sort = new AonCustomListBox("Ordenar Por");
	private AonCustomListBox asc = new AonCustomListBox("Orden");
	
	private MarketingActionTargetMassivePanel marketingActionTargetMassivePanel;
	
	private MarketingModuleOptions options;
	
	private Button okButton;
	
	private Integer iterator = 0;
	private boolean isTablet = false;
	
	public MarketingActionTargetMassive(MarketingModuleOptions options, MarketingAction marketingAction, AonMarketingActionTargetMassivePanelCallback callback) {
		initializeCommonService();
		
		this.options = options;
		this.isTablet = Window.getClientWidth() <= 980;
		
		ensureDebugId("actionTargetMassive");
		
		getElement().getStyle().setProperty("padding", "1rem");
		getElement().getStyle().setProperty("width", isTablet ? "50rem" : "70rem");
		
		dockLayout = new AonCustomDockLayout("Clientes Potenciales", true) {
			
			@Override
			protected void onClearFilter() {
				marketingActionTargetMassivePanel.resetSearchOffset();
				
				getSearchTextBox().setValue(null, false);
				
				scope.setValue("");
				entity.setValue("");
				advertising.setValue("0");
				projectType.setValue("");
				projectActivity.setValue("");
				status.setValue("");
				customer.setValue("0");
				mkAction.setValue("");
				
				onSearch();
			}
		};
		
		dockLayout.getElement().getStyle().setProperty("height", "25rem");
		dockLayout.ensureDebugId("actionTargetMassiveDock");
		
		dockLayout.hideToolbarFilterMessages();
		dockLayout.setSearchPlaceholder("Busque por descripci\u00f3n...");
		dockLayout.addKeyUpHandler(e -> {
			String value = dockLayout.getSearchTextBox().getValue();
			if(AonStringUtils.isNotBlank(value) && value.length() > 3) {
				onSearch();
			} else if(AonStringUtils.isBlank(value)) {
				onSearch();
			}
		});
		
		scope.addItem("-", "");
		options.getConfiguration().getAvailableScopes().forEach(sc -> scope.addItem(sc.getDescription(), sc.getId() + ""));
		scope.getListBox().setSelectedIndex(0);
		scope.getListBox().addChangeHandler(event -> onSearch());
		
		entity.addItem("-", "");
		entity.addItem("Persona Fisica", "1");
		entity.addItem("Persona Juridica", "0");
		entity.getListBox().addChangeHandler(event -> onSearch());
		
		advertising.addItem( "-", "");
		for(int i=0; i< Advertising.values().length; i++) {
			Advertising marketingActionMediaType = Advertising.values()[i];
			advertising.addItem(marketingActionMediaType.getDescription(), marketingActionMediaType.ordinal() + "");
		}
		advertising.getListBox().addChangeHandler(event -> onSearch());
		
		projectType.addItem("-", "");
		getAviableProjectType(projectTypeList -> {
			projectTypeList.forEach(projectTypeIt -> projectType.addItem(projectTypeIt.getDescription(), projectTypeIt.getId().toString()));
		});
		projectType.getListBox().addChangeHandler(event -> onSearch());
		
		projectActivity.addItem("-", "");
		getAviableProjectActivity(projectActivitiesList -> {
			projectActivitiesList.forEach(projectActivityIt -> projectActivity.addItem(projectActivityIt.getActivityType().getDescription(), projectActivityIt.getId().toString()));
		});
		projectActivity.getListBox().addChangeHandler(event -> onSearch());
		
		mkAction.addItem("-", "");
		getAviableMarketingAction(marketingActionList -> {
			marketingActionList.forEach(marketingActionIt -> mkAction.addItem(marketingActionIt.getDescription(), marketingActionIt.getId().toString()));
		});
		mkAction.getListBox().addChangeHandler(event -> onSearch());
		
		status.addItem( "Todas", "");
		status.addItem( "Inactivas", "1");
		status.addItem( "Activas", "0");
		status.getListBox().addChangeHandler(event -> onSearch());
		
		customer.addItem( "Sin cliente", "false");
		customer.addItem( "Con Cliente", "true");
		customer.getListBox().addChangeHandler(event -> onSearch());
		
		dockLayout.addFilterWidget(scope);
		dockLayout.addFilterWidget(entity);
		dockLayout.addFilterWidget(advertising);
		dockLayout.addFilterWidget(projectType);
		dockLayout.addFilterWidget(projectActivity);
		dockLayout.addFilterWidget(status);
		dockLayout.addFilterWidget(customer);
		dockLayout.addFilterWidget(mkAction);
		
		sort.addItem("Nombre", "name");
		sort.getListBox().addChangeHandler(event -> onSearch());
		
		asc.addItem("Ascendente", "true");
		asc.addItem("Descendete", "false");
		asc.getListBox().addChangeHandler(event -> onSearch());
		
		dockLayout.addSortWidget(sort);
		dockLayout.addSortWidget(asc);
		
		container = new HTMLPanel("");
		container.addStyleName(AON.CSS.aonFlexColumn());
		
		container.add(messagePanel);
	
		centerPanel = new SimpleLayoutPanel();
		centerPanel.setHeight("100%");
		
		container.add(centerPanel);
		
		FlowPanel buttons = new FlowPanel();
    	buttons.setStyleName(AON.CSS.aonTextCenter());
    	
    	okButton = new Button();
    	okButton.setStyleName(AON.CSS.aonOkButton());
    	okButton.setText( AON.MSG.accept());
    	okButton.setEnabled(false);
    	okButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				okButton.setEnabled(false);
				
				if(marketingActionTargetMassivePanel.isAllSelected()) {
					MarketingActionTargetMassiveParams params = getWidgetParams();
					params.setOffset(0);
					params.setLimit(Integer.MAX_VALUE);
					commonService.getMarketingActionTargets(params, new AsyncCallback<List<MarketingActionTarget>>() {
						
						@Override
						public void onSuccess(List<MarketingActionTarget> marketingActionTargets) {
							AonMessagePanel.showError(messagePanel, "Eliminando agentes comerciales seleccionados...");
							iterator = 0;
							createMarketingActionTarget(marketingActionTargets);
							
						}
						
						private void createMarketingActionTarget(List<MarketingActionTarget> marketingActionTargets) {
							if(iterator == marketingActionTargets.size()) {
								marketingActionTargetMassivePanel.resetSearchOffset();
								onSearch();
								AonMessagePanel.showSuccess(messagePanel, "Agentes comerciales eliminados correctamente");
								callback.onAccept();
								okButton.setEnabled(true);
							} else {
								MarketingActionTarget newMarketingActionTarget = new MarketingActionTarget().setMarketingAction(marketingAction);
								newMarketingActionTarget.setId(marketingActionTargets.get(iterator).getId());
								newMarketingActionTarget.setActionTargetDomain(options.getDomain());
								newMarketingActionTarget.setActionTargetStatus((byte)0); // Pendiente
								
								
								commonService.saveMarketingActionTarget(params.getDomainName(), params.getDomain(), params.getUser(), newMarketingActionTarget, new AsyncCallback<MarketingActionTarget>() {
									
									@Override
									public void onSuccess(MarketingActionTarget result) {
										iterator++;
										createMarketingActionTarget(marketingActionTargets);
									}
									
									@Override
									public void onFailure(Throwable caught) {
										AonMessagePanel.showError(messagePanel, "Error borrado: " + caught.getMessage());
									}
								});
							}
						}

						@Override
						public void onFailure(Throwable caught) {
							// Error
						}
					});
				} else {
					AonMessagePanel.showError(messagePanel, "Eliminando agentes comerciales seleccionados...");
					iterator = 0;
					createMarketingActionTarget(marketingActionTargetMassivePanel.getSelectedTargets());
				}
				
				
			}

			private void createMarketingActionTarget(List<Integer> selectedTargets) {
				if(iterator == selectedTargets.size()) {
					marketingActionTargetMassivePanel.resetSearchOffset();
					onSearch();
					AonMessagePanel.showSuccess(messagePanel, "Agentes comerciales eliminados correctamente");
					callback.onAccept();
					okButton.setEnabled(true);
				} else {
					MarketingActionTarget newMarketingActionTarget = new MarketingActionTarget().setMarketingAction(marketingAction);
					newMarketingActionTarget.setId(selectedTargets.get(iterator));
					newMarketingActionTarget.setActionTargetDomain(options.getDomain());
					newMarketingActionTarget.setActionTargetStatus((byte)0); // Pendiente
					
					
					commonService.saveMarketingActionTarget(options.getDomainName(), options.getDomain(), options.getUser(), newMarketingActionTarget, new AsyncCallback<MarketingActionTarget>() {
						
						@Override
						public void onSuccess(MarketingActionTarget result) {
							iterator++;
							createMarketingActionTarget(selectedTargets);
						}
						
						@Override
						public void onFailure(Throwable caught) {
							AonMessagePanel.showError(messagePanel, "Error borrado: " + caught.getMessage());
						}
					});
				}
			}
		});
    	
    	buttons.add(okButton);
    	
    	Button cancelButton = new Button();
    	cancelButton.setStyleName(AON.CSS.aonCancelButton());
    	cancelButton.addStyleName(AON.CSS.aonMarginLeft());
    	cancelButton.setText( AON.MSG.cancelAction());
    	cancelButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				cancelButton.setEnabled(false);
				callback.onCancel();
			}
		});
    	buttons.add(cancelButton);
		
    	container.add(buttons);
		
		dockLayout.add(container);
    	
		dockLayout.setSearchZIndex(70);
		dockLayout.setPopupHeight("20rem");
    	setWidget(dockLayout);
		
    	onSearch();
    	
		Scheduler.get().scheduleDeferred(new Command() {
	        public void execute() {
	        	dockLayout.getSearchTextBox().setFocus(true);
	        	onResize();
	        }
	    });	
	}

	public void onSearch() {
		MarketingActionTargetMassiveParams params = getWidgetParams();
		marketingActionTargetMassivePanel = new MarketingActionTargetMassivePanel(params) {

			@Override
			protected void onShowErrorMessage(String errorMessage) {
				AonMessagePanel.showError(messagePanel, errorMessage);
			}

			@Override
			protected void onShowLoadingMessage(String loadingMessage) {
				AonMessagePanel.showLoading(messagePanel, loadingMessage);
			}

			@Override
			protected void onShowSuccessMessage(String successMessage) {
				AonMessagePanel.showSuccess(messagePanel, successMessage);
			}

			@Override
			protected void onAcceptEnable(boolean enable) {
				okButton.setEnabled(enable);
			}
			
		};
		centerPanel.setWidget(marketingActionTargetMassivePanel);
	}
	
	private MarketingActionTargetMassiveParams getWidgetParams() {
		
		MarketingActionTargetMassiveParams params = new MarketingActionTargetMassiveParams()
				.setDomainName(options.getDomainName())
				.setDomain(options.getDomain())
				.setUser(options.getUser())
				.setDescription(dockLayout.getSearchTextBox().getValue())
				.setScope(AonStringUtils.isBlank(scope.getValue()) ? null : Integer.parseInt(scope.getValue()))
				.setEntity(AonStringUtils.isBlank(entity.getValue()) ? null : Byte.parseByte(entity.getValue()))
				.setAdvertising(AonStringUtils.isBlank(advertising.getValue()) ? null : Byte.parseByte(advertising.getValue()))
				.setProjectType(AonStringUtils.isBlank(projectType.getValue()) ? null : Integer.parseInt(projectType.getValue()))
				.setProjectActivity(AonStringUtils.isBlank(projectActivity.getValue()) ? null : Integer.parseInt(projectActivity.getValue()))
				.setStatus(AonStringUtils.isBlank(status.getValue()) ? null : Byte.parseByte(status.getValue()))
				.setCustomer(Boolean.parseBoolean(customer.getValue()))
				.setMkAction(AonStringUtils.isBlank(mkAction.getValue()) ? null : Integer.parseInt(mkAction.getValue()))
				.setOrderBy(sort.getValue())
				.setAsc(Boolean.parseBoolean(asc.getValue()))
				;
				
		return params;
	}

	private void getAviableProjectType(Consumer<List<ProjectType>> success) {
		commonService.getAviableProjectType(options.getDomainName(), options.getDomain(), options.getUser(), new AsyncCallback<List<ProjectType>>() {
			
			@Override
			public void onSuccess(List<ProjectType> projectTypes) {
				success.accept(projectTypes);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				AonMessagePanel.showError(messagePanel, "Error obteniendo workgroups: " + caught.getMessage());
			}
		});
	}
	
	private void getAviableProjectActivity(Consumer<List<ProjectActivity>> success) {
		commonService.getAviableProjectActivity(options.getDomainName(), options.getDomain(), options.getUser(), new AsyncCallback<List<ProjectActivity>>() {
			
			@Override
			public void onSuccess(List<ProjectActivity> projectActivities) {
				success.accept(projectActivities);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				AonMessagePanel.showError(messagePanel, "Error obteniendo workgroups: " + caught.getMessage());
			}
		});
	}
	
	private void getAviableMarketingAction(Consumer<List<MarketingAction>> success) {
		MarketingActionParams params = new MarketingActionParams()
				.setDomainName(options.getDomainName())
				.setDomain(options.getDomain())
				.setUser(options.getUser())
				.setOffset(0)
				.setLimit(Integer.MAX_VALUE);
		
		commonService.getMarketingActions(params, new AsyncCallback<List<MarketingAction>>() {
			
			@Override
			public void onSuccess(List<MarketingAction> marketingActions) {
				success.accept(marketingActions);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				AonMessagePanel.showError(messagePanel, "Error obteniendo workgroups: " + caught.getMessage());
			}
		});
	}

	protected abstract void onResize();

}
