package com.esferalia.aon.gwt.marketing.client.marketing.campaign;

import java.util.List;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomCard;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomCheckBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDateBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDockLayout;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomListBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomNumberBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomTextBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog.AonAcceptDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMarketingActionPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMarketingActionPanel.AonMarketingActionPanelCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.marketing.client.marketing.MarketingModuleOptions;
import com.esferalia.aon.gwt.marketing.client.marketing.action.MarketingActionEntryPanel;
import com.esferalia.aon.gwt.marketing.client.marketing.action.MarketingActionPanel;
import com.esferalia.aon.occam.api.model.MarketingAction;
import com.esferalia.aon.occam.api.model.MarketingAction.MarketingActionMediaType;
import com.esferalia.aon.occam.api.model.MarketingActionParams;
import com.esferalia.aon.occam.api.model.MarketingCampaign;
import com.esferalia.aon.occam.api.model.Workgroup;
import com.esferalia.aon.occam.api.model.security.Scope;
import com.esferalia.aon.occam.api.model.task.TaskHolder;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DeckLayoutPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public abstract class MarketingCampaignEntryPanel extends DeckLayoutPanel {
	
	static CommonServiceAsync commonService;
	
	private static void initializeCommonService() {
		if (commonService == null) {
			CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
			commonService = new CommonServiceAsyncDecorator(commonServiceRaw);
		}
	}
	
	private AonCustomDockLayout marketingCampaignEntryPanel;
	private MarketingActionEntryPanel marketingActionEntryPanel;
	
	private HTMLPanel container;
	
	private HTMLPanel messagePanel = new HTMLPanel("");

	// MarketingCampaign Info
	private AonCustomTextBox description = new AonCustomTextBox("Descripci\u00f3n");
	private AonCustomListBox workgroup = new AonCustomListBox("Grupo gesti\u00f3n");
	private AonCustomListBox taskHolder = new AonCustomListBox("Asignado a");
	private AonCustomListBox scope = new AonCustomListBox("Ambito");
	private AonCustomCheckBox active = new AonCustomCheckBox("Activo");
	
	private AonCustomNumberBox budget = new AonCustomNumberBox("Presupuesto");
	private AonCustomNumberBox expense = new AonCustomNumberBox("Gastos");
	
	// MarketingAction Search
	private AonCustomListBox mediaType = new AonCustomListBox("Canal");
	private AonCustomNumberBox budgetFilter = new AonCustomNumberBox("Presupuesto");
	private AonCustomNumberBox expenseFilter = new AonCustomNumberBox("Gastos");
	
	private AonCustomDateBox startDate = new AonCustomDateBox("F. Inicio");
	private AonCustomDateBox endDate = new AonCustomDateBox("F. Fin");
	
	private AonCustomListBox sort = new AonCustomListBox("Ordenar Por");
	private AonCustomListBox asc = new AonCustomListBox("Orden");
	
	private SimpleLayoutPanel centerPanel;
	
	private MarketingActionPanel marketingActionPanel;
	
	private MarketingModuleOptions options;
	private MarketingCampaign marketingCampaign;
	
	public MarketingCampaignEntryPanel(MarketingModuleOptions options) {
		this.options = options;
		initializeCommonService();
	}
	
	private void addButtonsToolbar() {
		AonToolbarButton backButton = new AonToolbarButton("Volver al listado de campa\u00f1as", AON.CSS.aonIconBack());
		backButton.addClickHandler(e -> onBackClick());
		marketingCampaignEntryPanel.addToolbarButton(backButton);
		
		AonToolbarButton deleteButton = new AonToolbarButton( "Borrar Campa\u00f1a", AON.CSS.aonIconDelete());
		deleteButton.addClickHandler(e -> {
			deleteButton.setEnabled(false);
			AonDialog dialog = new AonDialog("Eliminaci\u00f3n Campa\u00f1a",
					new HTML("Se va a proceder a eliminar la campa\u00f1a <b>" + marketingCampaign.getDescription() + "</b>.<br>\u00bfEsta seguro que desea proceder con la eliminaci\u00f3n\u003f. Este proceso ser\u00e1 irreversible"));
			
			dialog.confirm(new AonAcceptDialogCallback() {

				@Override
				public void onCancel() {
					deleteButton.setEnabled(true);
				}

				@Override
				public void onAccept() {
					onCampaignDeleteClick(marketingCampaign);
				}
			});
			
		});
		marketingCampaignEntryPanel.addToolbarButton(deleteButton);
		
		AonToolbarButton saveButton = new AonToolbarButton("Guardar campa\u00f1a", AON.CSS.aonIconSave());
		saveButton.addClickHandler(e -> saveMarketingCampaign(saveButton));
		marketingCampaignEntryPanel.addToolbarButton(saveButton);
		
		AonToolbarButton newActionButton = new AonToolbarButton("Nueva acci\u00f3n", AON.CSS.aonIconAdd());
		newActionButton.addClickHandler(e -> showMarketingActionDialog());
		marketingCampaignEntryPanel.addToolbarButton(newActionButton);
	}
	
	private void saveMarketingCampaign(AonToolbarButton saveButton) {
		saveButton.setEnabled(false);
		
		AonMessagePanel.showLoading(messagePanel, "Guardando campa\u00f1a " + this.marketingCampaign.getDescription());
		
		marketingCampaign.setActive(active.getValue());
		marketingCampaign.setDescription(description.getValue());
		marketingCampaign.setScope(new Scope().setId(Integer.parseInt(scope.getValue())));
		marketingCampaign.setBudget(budget.getValue());
		marketingCampaign.setExpense(expense.getValue());
		marketingCampaign.setWorkgroup(AonStringUtils.isBlank(workgroup.getValue()) ? null : new Workgroup().setId(Integer.parseInt(workgroup.getValue())));
		marketingCampaign.setTaskHolder(AonStringUtils.isBlank(taskHolder.getValue()) ? null : new TaskHolder().setRegistry(Integer.parseInt(taskHolder.getValue())));
		
		commonService.saveMarketingCampaign(options.getDomainName(), options.getDomain(), options.getUser(), marketingCampaign, new AsyncCallback<MarketingCampaign>() {
			
			@Override
			public void onSuccess(MarketingCampaign result) {
				marketingCampaign = result;
				AonMessagePanel.showSuccess(messagePanel, "Campa\u00f1a " + marketingCampaign.getDescription()+ " guardada correctamente");
				saveButton.setEnabled(true);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				AonMessagePanel.showError(messagePanel, "Error guardado: " + caught.getMessage());
				saveButton.setEnabled(true);
			}
		});
	}

	private void showMarketingActionDialog() {
		final AonCustomDialog dialog = new AonCustomDialog();
		dialog.setCaption( "ACCI\u00d3N" );
		final AonMarketingActionPanel marketingActionPanel = new AonMarketingActionPanel( options.getDomainName(), options.getDomain(), options.getUser(), this.marketingCampaign, new AonMarketingActionPanelCallback() {
			
			@Override
			public void onCancel() {
				dialog.hide();
			}
			
			@Override
			public void onAccept(MarketingAction marketingAction) {
				dialog.hide();
				showSelectedMarketingAction(marketingAction);
			}
		}) {

			@Override
			protected void onResize() {
				dialog.showLoaded();
			}};
		
		dialog.add( marketingActionPanel );
		dialog.showLoaded();
	}

	public void setMarketingCampaign(MarketingCampaign marketingCampaign) {
		if(null != marketingCampaignEntryPanel) remove(marketingCampaignEntryPanel);
		if(null != marketingActionEntryPanel) remove(marketingActionEntryPanel);
		
		this.marketingCampaign = marketingCampaign;
		
		marketingCampaignEntryPanel = new AonCustomDockLayout("CAMPA\u00D1A" + " " + marketingCampaign.getDescription()) {
			
			@Override
			protected void onClearFilter() {
				getSearchTextBox().setValue(null, false);
				mediaType.setValue("");
				startDate.setValue(null);
				endDate.setValue(null);
				budgetFilter.setValue(null);
				expenseFilter.setValue(null);
				
				marketingActionPanel.resetSearchOffset();
				
				onSearchActions();
			}
		};
		
		marketingCampaignEntryPanel.addKeyUpHandler(e -> {
			String value = marketingCampaignEntryPanel.getSearchTextBox().getValue();
			if(AonStringUtils.isNotBlank(value) && value.length() > 3) {
				onSearchActions();
			} else if(AonStringUtils.isBlank(value)) {
				onSearchActions();
			}
		});
		
		add(marketingCampaignEntryPanel);
		
		marketingActionEntryPanel = new MarketingActionEntryPanel(options) {
			
			@Override
			protected void onActionBackClick() {
				reloadMarketingCampaign();
			}

			@Override
			protected void onActionDeleteClick(MarketingAction marketingAction) {
				deleteMarketingAction(marketingAction);
			}
		};
		
		add(marketingActionEntryPanel);
		
		marketingCampaignEntryPanel.hideToolbarFilterMessages();
		marketingCampaignEntryPanel.setSearchPlaceholder("Busque por descripci\u00f3n...");
		marketingCampaignEntryPanel.addKeyUpHandler(e -> {
			String value = marketingCampaignEntryPanel.getSearchTextBox().getValue();
			if(AonStringUtils.isNotBlank(value) && value.length() > 3) {
				onSearchActions();
			} else if(AonStringUtils.isBlank(value)) {
				onSearchActions();
			}
		});
		addButtonsToolbar();
		
		container = new HTMLPanel("");
		container.addStyleName(AON.CSS.aonFlexColumn());
		
		container.add(messagePanel);
		
		// Cards Panel
		HTMLPanel cardsPanel = new HTMLPanel("");
		cardsPanel.setStyleName(AON.CSS.aonItemFlex());
		cardsPanel.getElement().getStyle().setProperty("align-items", "flex-start");
		cardsPanel.getElement().getStyle().setProperty("padding", "0 1rem");
		
		// Info Card
		AonCustomCard infoCard = new AonCustomCard("Informaci\u00f3n");
		infoCard.getElement().getStyle().setProperty("min-height", "13rem");
		infoCard.getElement().getStyle().setProperty("width", "100%");
		infoCard.add(createInfoCardContent());
		cardsPanel.add(infoCard);
		
		// Expenses Card
		AonCustomCard accountingCard = new AonCustomCard("Contabilidad");
		accountingCard.getElement().getStyle().setProperty("min-height", "13rem");
		accountingCard.getElement().getStyle().setProperty("width", "100%");
		accountingCard.add(createAccountingCardContent());
		cardsPanel.add(accountingCard);
		
		container.add(cardsPanel);
		
		// Actions Card
		HTMLPanel actionsPanel = new HTMLPanel("");
		actionsPanel.setStyleName(AON.CSS.aonItemFlex());
		actionsPanel.getElement().getStyle().setProperty("align-items", "flex-start");
		actionsPanel.getElement().getStyle().setProperty("padding", "0 1rem");
		
		AonCustomCard actionsCard = new AonCustomCard("Acciones");
		actionsCard.getElement().getStyle().setProperty("min-height", "13rem");
		actionsCard.getElement().getStyle().setProperty("width", "100%");
		
		centerPanel = new SimpleLayoutPanel();
		centerPanel.setHeight((Window.getClientHeight() - 505) + "px");
		actionsCard.add(centerPanel);
		
		actionsPanel.add(actionsCard);
		
		container.add(actionsPanel);
		
		marketingCampaignEntryPanel.add(container);
		
		createMarketingActions();
		
		Scheduler.get().scheduleDeferred(new Command() {
	        public void execute() {
	        	description.setFocus(true);
	        }
	    });		
	}

	private Widget createInfoCardContent() {
		HTMLPanel content = new HTMLPanel("");
		content.addStyleName(AON.CSS.aonFlexColumn());
		
		HTMLPanel row1 = new HTMLPanel("");
		row1.setStyleName(AON.CSS.aonItemFlex());
		
		description.setValue(this.marketingCampaign.getDescription());
		description.getTextBox().setMaxLength(64);
		row1.add(description);
		
		content.add(row1);
		
		HTMLPanel row2 = new HTMLPanel("");
		row2.setStyleName(AON.CSS.aonItemFlex());
		
		workgroup.addItem("-", "");
		workgroup.addChangeHandler(e -> {
			if(AonStringUtils.isBlank(workgroup.getValue())) {
				taskHolder.getListBox().setSelectedIndex(0);
				taskHolder.setVisible(false);
			} else {
				taskHolder.setVisible(true);
				getAviableTaskHolders(Integer.parseInt(workgroup.getValue()), taskHolders -> { 
					taskHolder.clearItems();
					taskHolder.addItem("-", "");
					taskHolders.forEach(taskHolderIt -> taskHolder.addItem(taskHolderIt.getName(), taskHolderIt.getRegistry().toString()));
				});
			}
		});
		
		taskHolder.addItem("-", "");
		
		getAviableWorkgroups(workgroups -> {
			workgroups.forEach(workgroupIt -> workgroup.addItem(workgroupIt.getDescription(), workgroupIt.getId().toString()));
			workgroup.setValue(null != marketingCampaign.getWorkgroup() ? marketingCampaign.getWorkgroup().getId().toString() : null);
			
			if(null != marketingCampaign.getWorkgroup()) {
				getAviableTaskHolders(marketingCampaign.getWorkgroup().getId(), taskHolders -> {
					taskHolder.clearItems();
					taskHolder.addItem("-", ""); 
					taskHolders.forEach(taskHolderIt -> taskHolder.addItem(taskHolderIt.getName(), taskHolderIt.getRegistry().toString()));
					taskHolder.setValue(null != marketingCampaign.getTaskHolder() ? marketingCampaign.getTaskHolder().getRegistry().toString() : null);
				});
			} else {
				taskHolder.setVisible(false);
			}	
		});
		
		row2.add(workgroup);
		row2.add(taskHolder);
		
		content.add(row2);
		
		HTMLPanel row3 = new HTMLPanel("");
		row3.setStyleName(AON.CSS.aonItemFlex());
		
		scope.clearItems();
		options.getConfiguration().getAvailableScopes().forEach(as -> scope.addItem(as.getDescription(), as.getId().toString()));
		scope.setValue(null != marketingCampaign.getScope() ? marketingCampaign.getScope().getId().toString() : null);
		
		active.setValue(this.marketingCampaign.isActive());
		
		row3.add(scope);
		row3.add(active);
		
		content.add(row3);
		
		return content;
	}
	
	private Widget createAccountingCardContent() {
		HTMLPanel content = new HTMLPanel("");
		content.addStyleName(AON.CSS.aonFlexColumn());
		
		HTMLPanel row1 = new HTMLPanel("");
		row1.setStyleName(AON.CSS.aonItemFlex());
		
		HTMLPanel budgetPanel = new HTMLPanel("");
		budgetPanel.setStyleName(AON.CSS.aonItemFlex());
		
		budget.hideNearBy();
		budget.setValue(marketingCampaign.getBudget());
		Label actionBudgets = new Label(" + " + AON.FMT.format(marketingCampaign.getActions().stream().mapToDouble(action -> action.getBudget()).sum()) + " \u20ac");
		actionBudgets.setWidth("10rem");
		actionBudgets.getElement().getStyle().setProperty("padding-top", "1rem");
		
		budgetPanel.add(budget);
		budgetPanel.add(actionBudgets);
		
		AonCustomTextBox accumulateBudget = new AonCustomTextBox("P. Acumulado");
		accumulateBudget.setEnable(false);
		accumulateBudget.setValue(AON.FMT.format(marketingCampaign.getBudget() + marketingCampaign.getActions().stream().mapToDouble(action -> action.getBudget()).sum()) + " \u20ac");
		
		row1.add(budgetPanel);
		row1.add(accumulateBudget);
		
		content.add(row1);
		
		HTMLPanel row2 = new HTMLPanel("");
		row2.setStyleName(AON.CSS.aonItemFlex());
		
		HTMLPanel expensePanel = new HTMLPanel("");
		expensePanel.setStyleName(AON.CSS.aonItemFlex());
		
		expense.hideNearBy();
		expense.setValue(marketingCampaign.getExpense());
		Label actionExpenses = new Label(" + " + AON.FMT.format(marketingCampaign.getActions().stream().mapToDouble(action -> action.getExpense()).sum()) + " \u20ac");
		actionExpenses.setWidth("10rem");
		actionExpenses.getElement().getStyle().setProperty("padding-top", "1rem");
		
		
		expensePanel.add(expense);
		expensePanel.add(actionExpenses);
		
		AonCustomTextBox accumulateExpense = new AonCustomTextBox("G. Acumulados");
		accumulateExpense.setEnable(false);
		accumulateExpense.setValue(AON.FMT.format(marketingCampaign.getExpense() + marketingCampaign.getActions().stream().mapToDouble(action -> action.getExpense()).sum()) + " \u20ac");
		
		row2.add(expensePanel);
		row2.add(accumulateExpense);
		
		content.add(row2);
		
		return content;
	}

	private void createMarketingActions() {
		mediaType.clearItems();
		mediaType.addItem( "-", "");
		for(int i=0; i<MarketingActionMediaType.values().length; i++) {
			MarketingActionMediaType marketingActionMediaType = MarketingActionMediaType.values()[i];
			mediaType.addItem(marketingActionMediaType.getDescription(), marketingActionMediaType.getValue().toString());
		}
		mediaType.setValue("");
		mediaType.addChangeHandler(event -> onSearchActions());
		marketingCampaignEntryPanel.addFilterWidget(mediaType);
		
		budgetFilter.addValueChangeHandler(e -> onSearchActions());
		marketingCampaignEntryPanel.addFilterWidget(budgetFilter);
		
		expenseFilter.addValueChangeHandler(e -> onSearchActions());
		marketingCampaignEntryPanel.addFilterWidget(expenseFilter);
		
		HTMLPanel datesPanel = new HTMLPanel(AonStringUtils.EMPTY);
		datesPanel.addStyleName(AON.CSS.aonItemFlex());
		
		startDate.addValueChangeHandler(event -> onSearchActions());
		endDate.addValueChangeHandler(event -> onSearchActions());
		
		datesPanel.add(startDate);
		datesPanel.add(endDate);
		marketingCampaignEntryPanel.addFilterWidget(datesPanel);
		
		sort.clearItems();
		sort.addItem("Descripci\u00f3n", "name");
		sort.addItem("Canal", "media");
		sort.addItem("Presupuesto", "budget");
		sort.addItem("Gastos", "expense");
		sort.addItem("F. Inicio", "start");
		sort.addItem("F. Fin", "end");
		sort.getListBox().addChangeHandler(event -> onSearchActions());
		
		asc.clearItems();
		asc.addItem("Ascendente", "true");
		asc.addItem("Descendete", "false");
		asc.getListBox().addChangeHandler(event -> onSearchActions());
		
		marketingCampaignEntryPanel.addSortWidget(sort);
		marketingCampaignEntryPanel.addSortWidget(asc);
		
		showMarketingActionList();
	}
	
	public void onSearchActions() {
		MarketingActionParams params = getWidgetParams( options );
		marketingActionPanel = new MarketingActionPanel(params) {

			@Override
			protected void onMarketingActionOpen(MarketingAction marketingAction) {
				showWidget(1);
				marketingActionEntryPanel.setMarketingAction(marketingAction);
			}

			@Override
			protected void onShowErrorMessage(String errorMessage) {
				AonMessagePanel.showError(messagePanel, errorMessage);
			}

		};
			
		centerPanel.setWidget(marketingActionPanel);
	}

	public MarketingActionParams getWidgetParams( MarketingModuleOptions options) {
		MarketingActionParams params = new MarketingActionParams()
			.setDomainName(options.getDomainName())
			.setDomain(options.getDomain())
			.setUser(options.getUser())
			.setDescription(marketingCampaignEntryPanel.getSearchTextBox().getValue())
			.setMediaType(AonStringUtils.isBlank(mediaType.getValue()) ? null : Byte.parseByte(mediaType.getValue()))
			.setStartDate(startDate.getValue())
			.setEndDate(endDate.getValue())
			.setMarketingCampaign(this.marketingCampaign)
			.setBudget(budgetFilter.getValue())
			.setExpense(expenseFilter.getValue())
			.setOrderBy(sort.getValue())
			.setAsc(Boolean.parseBoolean(asc.getValue()))
			;
		
		if(budgetFilter.isBetweenNumbers()) {
			params
				.setBudget(null)
				.setGTBudget(budgetFilter.getGTValue())
				.setLTBudget(budgetFilter.getLTValue())
				.setBetweenBudgetNumbers(budgetFilter.isBetweenNumbers())
				;
		} else {
			params
				.setBudget(budgetFilter.getValue())
				.setGTBudget(null)
				.setLTBudget(null)
				.setBetweenBudgetNumbers(budgetFilter.isBetweenNumbers())
				;
		}
		
		if(expenseFilter.isBetweenNumbers()) {
			params
				.setExpense(null)
				.setGTExpense(expenseFilter.getGTValue())
				.setLTExpense(expenseFilter.getLTValue())
				.setBetweenExpenseNumbers(expenseFilter.isBetweenNumbers())
				;
		} else {
			params
				.setExpense(expenseFilter.getValue())
				.setGTExpense(null)
				.setLTExpense(null)
				.setBetweenExpenseNumbers(expenseFilter.isBetweenNumbers())
				;
		}
		
		return params;
	}
	
	private void showMarketingActionList() {
		showWidget(0);
		onSearchActions();
	}
	
	private void reloadMarketingCampaign() {
		showWidget(0);
		commonService.getMarketingCampaign(options.getDomainName(), options.getDomain(), options.getUser(), marketingCampaign.getId(), new AsyncCallback<MarketingCampaign>() {
			
			@Override
			public void onSuccess(MarketingCampaign marketingCampaign) {
				setMarketingCampaign(marketingCampaign);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				AonMessagePanel.showError(messagePanel, "Error obteniendo campa\u00f1as: " + caught.getMessage());
			}
		});
	}
	
	private void showSelectedMarketingAction(MarketingAction marketingAction) {
		showWidget(1);
		marketingActionEntryPanel.setMarketingAction(marketingAction);
	}
	
	private void deleteMarketingAction(MarketingAction marketingAction) {
		commonService.deleteMarketingAction(options.getDomainName(), options.getDomain(), options.getUser(), marketingAction.getId(), new AsyncCallback<Void>() {
			
			@Override
			public void onSuccess(Void result) {
				showMarketingActionList();
			}
			
			@Override
			public void onFailure(Throwable caught) {
				AonMessagePanel.showError(messagePanel, "Error borrado: " + caught.getMessage());
			}
		});
	}
	
	private void getAviableWorkgroups(Consumer<List<Workgroup>> success) {
		commonService.getAviableWorkgroups(options.getDomainName(), options.getDomain(), options.getUser(), new AsyncCallback<List<Workgroup>>() {
			
			@Override
			public void onSuccess(List<Workgroup> workgroups) {
				success.accept(workgroups);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				AonMessagePanel.showError(messagePanel, "Error obteniendo workgroups: " + caught.getMessage());
			}
		});
	}
	
	private void getAviableTaskHolders(Integer workgroup, Consumer<List<TaskHolder>> success) {
		commonService.getAviableTaskHolders(options.getDomainName(), options.getDomain(), options.getUser(), workgroup, new AsyncCallback<List<TaskHolder>>() {
			
			@Override
			public void onSuccess(List<TaskHolder> taskHolders) {
				success.accept(taskHolders);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				AonMessagePanel.showError(messagePanel, "Error obteniendo workgroups: " + caught.getMessage());
			}
		});
	}
	
	protected abstract void onBackClick();
	protected abstract void onCampaignDeleteClick(MarketingCampaign marketingCampaign);

}
