package com.esferalia.aon.gwt.marketing.client.marketing.campaign;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDialog.AonCustomDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDockLayout;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomListBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomNumberBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMarketingCampaignPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMarketingCampaignPanel.AonMarketingCampaignPanelCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.marketing.client.marketing.MarketingModuleOptions;
import com.esferalia.aon.occam.api.model.MarketingCampaign;
import com.esferalia.aon.occam.api.model.MarketingCompaignParams;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;

public abstract class MarketingCampaignModulePanel extends AonCustomDockLayout {

	private HTMLPanel container;
	private HTMLPanel messagePanel = new HTMLPanel("");
	
	private AonToolbarButton deleteButton;
	
	private SimpleLayoutPanel centerPanel;
	
	private AonCustomListBox scope = new AonCustomListBox("Ambito");
	private AonCustomListBox active = new AonCustomListBox("Activo");
	
	private AonCustomNumberBox budget = new AonCustomNumberBox("Presupuesto");
	private AonCustomNumberBox expense = new AonCustomNumberBox("Gastos");
	
	private AonCustomListBox sort = new AonCustomListBox("Ordenar Por");
	private AonCustomListBox asc = new AonCustomListBox("Orden");
	
	private MarketingModuleOptions options;
	
	private MarketingCompaignPanel marketingCompaignPanel;
	
	public MarketingCampaignModulePanel(MarketingModuleOptions options) {
		super("CAMPA\u00D1AS");
		
		this.options = options;
		
		addButtonsToolbar();
		
		hideToolbarFilterMessages();
		setSearchPlaceholder("Busque por descripci\u00f3n...");
		addKeyUpHandler(e -> {
			String value = getSearchTextBox().getValue();
			if(AonStringUtils.isNotBlank(value) && value.length() > 3) {
				onSearch( options );
			} else if(AonStringUtils.isBlank(value)) {
				onSearch( options );
			}
		});
		
		budget.addValueChangeHandler(e -> onSearch( options ));
		expense.addValueChangeHandler(e -> onSearch( options ));
		
		scope.addItem("-", "");
		options.getConfiguration().getAvailableScopes().forEach(sc -> scope.addItem(sc.getDescription(), sc.getId() + ""));
		scope.getListBox().setSelectedIndex(0);
		scope.getListBox().addChangeHandler(event -> onSearch( options ));
		
		active.addItem( "Todas", "");
		active.addItem( "Inactivas", "0");
		active.addItem( "Activas", "1");
		active.getListBox().setSelectedIndex(2);
		active.getListBox().addChangeHandler(event -> onSearch( options ));
		
		addFilterWidget(budget);
		addFilterWidget(expense);
		addFilterWidget(scope);
		addFilterWidget(active);
		
		sort.addItem("Nombre", "name");
		sort.addItem("Presupuesto", "budget");
		sort.addItem("Gastos", "expense");
		sort.getListBox().addChangeHandler(event -> onSearch( options ));
		
		asc.addItem("Ascendente", "true");
		asc.addItem("Descendete", "false");
		asc.getListBox().addChangeHandler(event -> onSearch( options ));
		
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
		onSearch( options );
		
		Scheduler.get().scheduleDeferred(new Command() {
	        public void execute() {
	        	getSearchTextBox().setFocus(true);
	        }
	    });		
	}

	@Override
	protected void onClearFilter() {
		getSearchTextBox().setValue(null, false);
		scope.getListBox().setSelectedIndex(0);
		active.getListBox().setSelectedIndex(0);
		budget.setValue(null);
		expense.setValue(null);
		
		marketingCompaignPanel.resetSearchOffset();
		
		onSearch( options );
	}
	
	private void addButtonsToolbar() {
		AonToolbarButton newButton = new AonToolbarButton( "Nueva Campa\u00f1a", AON.CSS.aonIconAdd());
		newButton.addClickHandler(e -> showMarketingCompaignDialog());
		addToolbarButton(newButton);
		
		deleteButton = new AonToolbarButton( "Borrar Agente Comercial", AON.CSS.aonIconDelete());
		deleteButton.addClickHandler(e -> {
			deleteButton.setEnabled(false);
			marketingCompaignPanel.deleteCampaigns();
		});
		deleteButton.setEnabled(false);
		
		addToolbarButton(deleteButton);
	}
	
	private void showMarketingCompaignDialog() {
		final AonCustomDialog dialog = new AonCustomDialog();
		dialog.setCaption( "NUEVA CAMPA\u00D1A" );
		final AonMarketingCampaignPanel marketingCampaignPanel = new AonMarketingCampaignPanel( options.getDomainName(), options.getDomain(), options.getUser(), options.getConfiguration().getAvailableScopes(), new AonMarketingCampaignPanelCallback() {
			
			@Override
			public void onCancel() {
				dialog.hide();
			}
			
			@Override
			public void onAccept(MarketingCampaign marketingCampaign) {
				dialog.hide();
				onMarketingCampaignSelect(marketingCampaign);
			}
		}) {

			@Override
			protected void onResize() {
				dialog.showLoaded();
			}};
		
		dialog.add( marketingCampaignPanel );
		dialog.showLoadedCB(new AonCustomDialogCallback() {
			
			@Override
			public void onEnd() {
				marketingCampaignPanel.focusDescription();
			}
		});
	}

	public void onSearch( MarketingModuleOptions options ) {
		MarketingCompaignParams params = getWidgetParams( options );
		marketingCompaignPanel = new MarketingCompaignPanel(params) {

			@Override
			protected void onMarketingCampaignOpen(MarketingCampaign marketingCampaign) {
				onMarketingCampaignSelect(marketingCampaign);
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
			protected void onDeleteEnable(boolean enabled) {
				deleteButton.setEnabled(enabled);
			}
		
		};
			
		centerPanel.setWidget(marketingCompaignPanel);
	}

	public MarketingCompaignParams getWidgetParams( MarketingModuleOptions options) {
		MarketingCompaignParams params = new MarketingCompaignParams()
			.setDomainName(options.getDomainName())
			.setDomain(options.getDomain())
			.setUser(options.getUser())
			.setDescription(getSearchTextBox().getValue())
			.setScope(AonStringUtils.isBlank(scope.getValue()) ? null :Integer.parseInt(scope.getValue()))
			.setActive(AonStringUtils.isBlank(active.getValue()) ? null :Byte.parseByte(active.getValue()))
			.setBudget(budget.getValue())
			.setExpense(expense.getValue())
			.setOrderBy(sort.getValue())
			.setAsc(Boolean.parseBoolean(asc.getValue()))
			;
		
		if(budget.isBetweenNumbers()) {
			params
				.setBudget(null)
				.setGTBudget(budget.getGTValue())
				.setLTBudget(budget.getLTValue())
				.setBetweenBudgetNumbers(budget.isBetweenNumbers())
				;
		} else {
			params
				.setBudget(budget.getValue())
				.setGTBudget(null)
				.setLTBudget(null)
				.setBetweenBudgetNumbers(budget.isBetweenNumbers())
				;
		}
		
		if(expense.isBetweenNumbers()) {
			params
				.setExpense(null)
				.setGTExpense(expense.getGTValue())
				.setLTExpense(expense.getLTValue())
				.setBetweenExpenseNumbers(expense.isBetweenNumbers())
				;
		} else {
			params
				.setExpense(expense.getValue())
				.setGTExpense(null)
				.setLTExpense(null)
				.setBetweenExpenseNumbers(expense.isBetweenNumbers())
				;
		}
		
		return params;
	}
	
	protected abstract void onMarketingCampaignSelect(MarketingCampaign marketingCampaign);
	
}
