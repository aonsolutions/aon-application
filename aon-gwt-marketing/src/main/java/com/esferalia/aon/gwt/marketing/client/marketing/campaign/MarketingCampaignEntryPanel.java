package com.esferalia.aon.gwt.marketing.client.marketing.campaign;

import java.util.List;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDateBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDoubleBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonErrorPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMarketingActionPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMarketingActionPanel.AonMarketingActionPanelCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonSearchPanelButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog.AonAcceptDialogCallback;
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
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DeckLayoutPanel;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.TextBox;

public abstract class MarketingCampaignEntryPanel extends DeckLayoutPanel {
	
	static CommonServiceAsync commonService;
	
	private static void initializeCommonService() {
		if (commonService == null) {
			CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
			commonService = new CommonServiceAsyncDecorator(commonServiceRaw);
		}
	}
	
	private DockLayoutPanel marketingCampaignEntryPanel;
	private MarketingActionEntryPanel marketingActionEntryPanel;

	private AonToolbar toolbar;
	
	private HTMLPanel container;
	
	private HTMLPanel messagePanel = new HTMLPanel("");

	// MarketingCampaign Info
	private TextBox description = new TextBox();
	private AonDoubleBox budget = new AonDoubleBox(15, 2);
	private Label acumulateBudget = new Label();
	private AonDoubleBox expense = new AonDoubleBox(15, 2);
	private Label acumulateExpense = new Label();
	private ListBox scope = new ListBox();
	private ListBox workgroup = new ListBox();
	private InlineLabel taskHolderLabel = new InlineLabel("Asignado a");
	private ListBox taskHolder = new ListBox();
	private Button active = new Button();
	
	// MarketingAction Search
	private FlowPanel searchPanel;
	private FlowPanel filterPanel;
	
	private TextBox actionDescription;
	private ListBox mediaType;
	private AonDateBox startDate;
	private AonDateBox endDate;
	
	private AonDoubleBox budgetFilter;
	private AonDoubleBox expenseFilter;
	
	private SimpleLayoutPanel centerPanel;
	
	private MarketingActionPanel marketingActionPanel;
	
	private MarketingModuleOptions options;
	private MarketingCampaign marketingCampaign;
	
	public MarketingCampaignEntryPanel(MarketingModuleOptions options) {
		
		this.options = options;
		initializeCommonService();
		
		marketingCampaignEntryPanel = new DockLayoutPanel(Unit.PX);
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
		
		createToolbar();
	}
	
	private void createToolbar() {
		toolbar = new AonToolbar( "CAMPA\u00D1A" );
		
		AonToolbarButton backButton = new AonToolbarButton("Volver al listado de campa\u00f1as", AON.CSS.aonIconBack());
		backButton.addClickHandler(e -> onBackClick());
		toolbar.add(backButton);
		
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
		toolbar.add(deleteButton);
		
		AonToolbarButton saveButton = new AonToolbarButton("Guardar campa\u00f1a", AON.CSS.aonIconSave());
		saveButton.addClickHandler(e -> saveMarketingCampaign());
		toolbar.add(saveButton);
		
		AonToolbarButton newActionButton = new AonToolbarButton("Nueva acci\u00f3n", AON.CSS.aonIconAdd());
		newActionButton.addClickHandler(e -> showMarketingActionDialog());
		toolbar.add(newActionButton);
		
		marketingCampaignEntryPanel.addNorth(toolbar, 50);
	}

	private void saveMarketingCampaign() {
		AonMessagePanel.showLoading(messagePanel, "Guardando campa\u00f1a " + this.marketingCampaign.getDescription());
		
		marketingCampaign.setActive(isActiveToggleButton(active));
		marketingCampaign.setDescription(description.getValue());
		marketingCampaign.setScope(new Scope().setId(Integer.parseInt(scope.getSelectedValue())));
		marketingCampaign.setBudget(budget.getValue());
		marketingCampaign.setExpense(expense.getValue());
		marketingCampaign.setWorkgroup(0 == workgroup.getSelectedIndex() ? null : new Workgroup().setId(Integer.parseInt(workgroup.getSelectedValue())));
		marketingCampaign.setTaskHolder(0 == taskHolder.getSelectedIndex() ? null : new TaskHolder().setRegistry(Integer.parseInt(taskHolder.getSelectedValue())));
		
		commonService.saveMarketingCampaign(options.getDomainName(), options.getDomain(), options.getUser(), marketingCampaign, new AsyncCallback<MarketingCampaign>() {
			
			@Override
			public void onSuccess(MarketingCampaign result) {
				marketingCampaign = result;
				AonMessagePanel.showSuccess(messagePanel, "Campa\u00f1a " + marketingCampaign.getDescription()+ " guardada correctamente");
			}
			
			@Override
			public void onFailure(Throwable caught) {
				AonMessagePanel.showError(messagePanel, "Error guardado: " + caught.getMessage());
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
		this.marketingCampaign = marketingCampaign;
		
		marketingCampaignEntryPanel.clear();
		
		createToolbar();
		toolbar.setTitle("Campa\u00f1a " + marketingCampaign.getDescription());
		
		container = new HTMLPanel("");
		container.addStyleName(AON.CSS.aonFlexColumn());
		
		container.add(messagePanel);
		
		FlowPanel rootPanel = new FlowPanel();
		rootPanel.setStyleName(AON.CSS.aonFlexColumnBetween());
		
		final AonErrorPanel errorPanel = new AonErrorPanel();
		errorPanel.addStyleName(AON.CSS.aonMarginTop());
		rootPanel.add(errorPanel);
		
		FlowPanel tablePanel = new FlowPanel();
		tablePanel.setStyleName(AON.CSS.aonScrollArea());

		FlexTable table = new FlexTable();
		table.setStyleName(AON.CSS.aonTable());
		table.getElement().getStyle().setProperty("width", "30rem");
		
		table.setWidget(0, 0, new InlineLabel("Descripci\u00f3n"));
		table.getCellFormatter().setStyleName(0, 0, AON.CSS.aonTableLabel());
		description.setValue(marketingCampaign.getDescription());
		description.setMaxLength(64);
		description.setStyleName(AON.CSS.aonInputText());
		addInputStyle(description.getElement());

		table.setWidget(0,1,description);
		table.getFlexCellFormatter().setColSpan(0, 1, 3);
		
		table.setWidget(1,0,new InlineLabel("Presupuesto"));
		table.getCellFormatter().setStyleName(1, 0, AON.CSS.aonTableLabel());
		budget.setValue(marketingCampaign.getBudget());
		addInputStyle(budget.getElement());
		table.setWidget(1,1,budget);
		
		table.setWidget(1,2,new InlineLabel("P. Acumulado"));
		table.getCellFormatter().setStyleName(1, 2, AON.CSS.aonTableLabel());
		acumulateBudget.addStyleName(AON.CSS.aonTextRight());
		acumulateBudget.setText(AON.FMT.format(marketingCampaign.getActions().stream().mapToDouble(action -> action.getBudget()).sum()) + " \u20ac");
		table.setWidget(1,3,acumulateBudget);
		
		table.setWidget(2,0,new InlineLabel("Gastos"));
		table.getCellFormatter().setStyleName(2, 0, AON.CSS.aonTableLabel());
		expense.setValue(marketingCampaign.getExpense());
		addInputStyle(expense.getElement());
		table.setWidget(2,1,expense);
		
		table.setWidget(2,2,new InlineLabel("G. Acumulados"));
		table.getCellFormatter().setStyleName(2, 2, AON.CSS.aonTableLabel());
		acumulateExpense.addStyleName(AON.CSS.aonTextRight());
		acumulateExpense.setText(AON.FMT.format(marketingCampaign.getActions().stream().mapToDouble(action -> action.getExpense()).sum()) + " \u20ac");
		table.setWidget(2,3,acumulateExpense);
		
		workgroup = new ListBox();
		addSelectStyle(workgroup.getElement());
		workgroup.addItem("-", "");
		workgroup.addChangeHandler(e -> {
			if(workgroup.getSelectedIndex() == 0) {
				taskHolder.setSelectedIndex(0);
				taskHolder.setVisible(false);
				taskHolderLabel.setVisible(false);
			} else {
				taskHolder.setVisible(true);
				taskHolderLabel.setVisible(true);
				getAviableTaskHolders(Integer.parseInt(workgroup.getSelectedValue()), taskHolders -> { 
					taskHolder.clear();
					taskHolder.addItem("-", "");
					taskHolders.forEach(taskHolderIt -> taskHolder.addItem(taskHolderIt.getName(), taskHolderIt.getRegistry().toString()));
				});
			}
		});
		
		taskHolder = new ListBox();
		addSelectStyle(taskHolder.getElement());
		taskHolder.addItem("-", "");
		
		getAviableWorkgroups(workgroups -> {
			workgroups.forEach(workgroupIt -> workgroup.addItem(workgroupIt.getDescription(), workgroupIt.getId().toString()));
			setSelectedValueLB(workgroup, null != marketingCampaign.getWorkgroup() ? marketingCampaign.getWorkgroup().getId().toString() : null);
			
			if(null != marketingCampaign.getWorkgroup()) {
				getAviableTaskHolders(marketingCampaign.getWorkgroup().getId(), taskHolders -> {
					taskHolder.clear();
					taskHolder.addItem("-", ""); 
					taskHolders.forEach(taskHolderIt -> taskHolder.addItem(taskHolderIt.getName(), taskHolderIt.getRegistry().toString()));
					setSelectedValueLB(taskHolder, null != marketingCampaign.getTaskHolder() ? marketingCampaign.getTaskHolder().getRegistry().toString() : null);
					taskHolderLabel.setVisible(true);
				});
			} else {
				taskHolder.setVisible(false);
				taskHolderLabel.setVisible(false);
			}	
		});
		
		table.setWidget(3,0,new InlineLabel("Grupo trabajo"));
		table.getCellFormatter().setStyleName(3, 0, AON.CSS.aonTableLabel());
		table.setWidget(3,1,workgroup);
		
		table.setWidget(3,2,taskHolderLabel);
		table.getCellFormatter().setStyleName(3, 2, AON.CSS.aonTableLabel());
		table.setWidget(3,3,taskHolder);
		
		table.setWidget(4,0,new InlineLabel(AON.MSG.scope()));
		table.getCellFormatter().setStyleName(4, 0, AON.CSS.aonTableLabel());
		scope.clear();
		addSelectStyle(scope.getElement());
		options.getConfiguration().getAvailableScopes().forEach(as -> scope.addItem(as.getDescription(), as.getId().toString()));
		scope.setStyleName(AON.CSS.aonInputText());
		setSelectedValueLB(scope, null != marketingCampaign.getScope() ? marketingCampaign.getScope().getId().toString() : null);
		table.setWidget(4,1,scope);
		
		active = new Button();
		table.setWidget(5,0,new InlineLabel("Activo"));
		table.getCellFormatter().setStyleName(5, 0, AON.CSS.aonTableLabel());
		getEnableDisableButton(active, marketingCampaign.isActive());
		active.addClickHandler(e -> getEnableDisableButton(active, !isActiveToggleButton(active)));
		table.setWidget(5,1,active);
		
		table.getColumnFormatter().getElement(0).getStyle().setProperty("width", "3rem");
		
		tablePanel.add( table );
		
		rootPanel.add( tablePanel );
		
		container.add(rootPanel);
		
		marketingCampaignEntryPanel.add(container);
		
		createMarketingActions();
		
		Scheduler.get().scheduleDeferred(new Command() {
	        public void execute() {
	        	description.setFocus(true);
	        }
	    });		
	}
	
	private void createMarketingActions() {
		actionDescription = new TextBox();
		actionDescription.setVisibleLength(50);
		actionDescription.setStyleName(AON.CSS.aonInputText());
		actionDescription.addValueChangeHandler(event -> onSearchActions());
		
		mediaType = new ListBox();
		mediaType.addItem( "-", "");
		for(int i=0; i<MarketingActionMediaType.values().length; i++) {
			MarketingActionMediaType marketingActionMediaType = MarketingActionMediaType.values()[i];
			mediaType.addItem(marketingActionMediaType.getDescription(), marketingActionMediaType.getValue().toString());
		}
		mediaType.setSelectedIndex(0);
		mediaType.setStyleName(AON.CSS.aonInputText());
		mediaType.addChangeHandler(event -> onSearchActions());
		
		startDate = new AonDateBox();
		startDate.setStyleName(AON.CSS.aonInputText());
		startDate.addValueChangeHandler(event -> onSearchActions());
		
		endDate = new AonDateBox();
		endDate.setStyleName(AON.CSS.aonInputText());
		endDate.addValueChangeHandler(event -> onSearchActions());
		
		budgetFilter = new AonDoubleBox(15, 2);
		budgetFilter.addValueChangeHandler(e -> onSearchActions());
		expenseFilter = new AonDoubleBox(15, 2);
		expenseFilter.addValueChangeHandler(e -> onSearchActions());
		
		searchPanel = new FlowPanel();
		searchPanel.setStyleName(AON.CSS.aonSearchPanel());
		searchPanel.addStyleName(AON.CSS.aonFlexBetween());
		searchPanel.getElement().getStyle().setProperty("margin", "0 1rem");
		
		filterPanel = new FlowPanel();
		filterPanel.addStyleName(AON.CSS.aonItemFlex());
		
		Label descriptionLabel = new Label(AON.MSG.description());
		descriptionLabel.setStyleName(AON.CSS.aonSearchPanelLabel());
		filterPanel.add(descriptionLabel);
		filterPanel.add(actionDescription);
		
		Label typeLabel = new Label("Canal");
		typeLabel.setStyleName(AON.CSS.aonSearchPanelLabel());
		filterPanel.add(typeLabel);
		filterPanel.add(mediaType);

		Label startDateLabel = new Label("F. Inicio");
		startDateLabel.setStyleName(AON.CSS.aonSearchPanelLabel());
		filterPanel.add(startDateLabel);
		filterPanel.add(startDate);
		
		Label endDateLabel = new Label("F. Fin");
		endDateLabel.setStyleName(AON.CSS.aonSearchPanelLabel());
		filterPanel.add(endDateLabel);
		filterPanel.add(endDate);
		
		Label budgetLabel = new Label("Presupuesto");
		budgetLabel.setStyleName(AON.CSS.aonSearchPanelLabel());
		filterPanel.add(budgetLabel);
		filterPanel.add(budgetFilter);
		
		Label expenseLabel = new Label("Gastos");
		expenseLabel.setStyleName(AON.CSS.aonSearchPanelLabel());
		filterPanel.add(expenseLabel);
		filterPanel.add(expenseFilter);

		searchPanel.add(filterPanel);
		
		AonSearchPanelButton cleanButton = new AonSearchPanelButton( AON.MSG.clean(), AON.CSS.aonIconClear() );
		cleanButton.addClickHandler(event -> {
			actionDescription.setValue(null,false);
			mediaType.setSelectedIndex(0);
			startDate.setValue(null,false);
			endDate.setValue(null,false);
			budgetFilter.setValue(null);
			expenseFilter.setValue(null);
			
			marketingActionPanel.resetSearchOffset();
			
			onSearchActions();
		});

		AonSearchPanelButton refreshButton = new AonSearchPanelButton( AON.MSG.refresh(), AON.CSS.aonIconRefresh() );
		refreshButton.addStyleName(AON.CSS.aonMarginLeft());
		refreshButton.addClickHandler(event -> onSearchActions());

		FlowPanel buttonsPanel = new FlowPanel();
		buttonsPanel.add( cleanButton );
		buttonsPanel.add( refreshButton );
		searchPanel.add(buttonsPanel);
		
		centerPanel = new SimpleLayoutPanel();
		centerPanel.setHeight((Window.getClientHeight() - 240) + "px");
		
		container.add(searchPanel);
		container.add(centerPanel);
		
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
		return new MarketingActionParams()
			.setDomainName(options.getDomainName())
			.setDomain(options.getDomain())
			.setUser(options.getUser())
			.setDescription(actionDescription.getValue())
			.setMediaType(mediaType.getSelectedIndex() == 0 ? null : Byte.parseByte(mediaType.getSelectedValue()))
			.setStartDate(startDate.getValue())
			.setEndDate(endDate.getValue())
			.setMarketingCampaign(this.marketingCampaign)
			.setBudget(budgetFilter.getValue())
			.setExpense(expenseFilter.getValue())
			;
	}

	private void getEnableDisableButton(Button button, boolean disabled) {
		button.removeStyleName(disabled ? AON.AON_ICON_DISABLE : AON.AON_ICON_ENABLE);
		button.removeStyleName(AON.AON_NO_MARGIN);
		button.removeStyleName(AON.AON_EDIT_DATA_TABLE_BUTTON);
		
		button.setStyleName(!disabled ? AON.AON_ICON_DISABLE : AON.AON_ICON_ENABLE );
		button.setStyleName(AON.AON_NO_MARGIN, true);
		button.setStyleName(AON.AON_EDIT_DATA_TABLE_BUTTON, true);
	}
	
	private boolean isActiveToggleButton(Button button) {
		return AonStringUtils.containsIgnoreCase(button.getStyleName(), AON.AON_ICON_ENABLE);
	}
	
	private void addInputStyle(Element el) {
		el.getStyle().setProperty("width", "-moz-available");
		el.getStyle().setProperty("width", "-webkit-fill-available");
		el.getStyle().setProperty("height", "1.1rem");
	}
	
	private void addSelectStyle(Element el) {
		el.getStyle().setProperty("width", "-moz-available");
		el.getStyle().setProperty("width", "-webkit-fill-available");
		el.getStyle().setProperty("height", "1.2rem");
	}

	private void setSelectedValueLB(ListBox lBox, String str) {
	    String text = str;
	    int indexToFind = 0;
	    for (int i = 0; i < lBox.getItemCount(); i++) {
	        if (lBox.getValue(i).equals(text)) {
	            indexToFind = i;
	            break;
	        }
	    }
	    lBox.setSelectedIndex(indexToFind);
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
				AonMessagePanel.showError(messagePanel, "Error obteniendo campaña: " + caught.getMessage());
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
