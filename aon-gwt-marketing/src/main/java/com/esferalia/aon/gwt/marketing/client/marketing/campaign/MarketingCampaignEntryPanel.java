package com.esferalia.aon.gwt.marketing.client.marketing.campaign;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDateBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonErrorPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMarketingActionPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMarketingActionPanel.AonMarketingActionPanelCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonSearchPanelButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.marketing.client.marketing.MarketingModuleOptions;
import com.esferalia.aon.gwt.marketing.client.marketing.action.MarketingActionEntryPanel;
import com.esferalia.aon.gwt.marketing.client.marketing.action.MarketingActionPanel;
import com.esferalia.aon.occam.api.model.MarketingAction;
import com.esferalia.aon.occam.api.model.MarketingAction.MarketingActionMediaType;
import com.esferalia.aon.occam.api.model.MarketingActionParams;
import com.esferalia.aon.occam.api.model.MarketingCampaign;
import com.esferalia.aon.occam.api.model.security.Scope;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DeckLayoutPanel;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
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
	private ListBox scope = new ListBox();
	private Button active = new Button();
	
	// MarketingAction Search
	private FlowPanel searchPanel;
	private FlowPanel filterPanel;
	
	private TextBox actionDescription;
	private ListBox mediaType;
	private AonDateBox startDate;
	private AonDateBox endDate;
	
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
				showMarketingActionList();
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
		table.addStyleName(AON.CSS.aonWidthAll());
		
		table.setWidget(0, 0, new InlineLabel("Descripci\u00f3n"));
		table.getCellFormatter().setStyleName(0, 0, AON.CSS.aonTableLabel());
		table.getCellFormatter().getElement(0, 0).setPropertyString("min-width", "135px");
		description.setValue(marketingCampaign.getDescription());
		description.setMaxLength(64);
		description.setStyleName(AON.CSS.aonInputText());
		description.addStyleName(AON.CSS.aonWidthAll());

		table.setWidget(0,1,description);
		table.getCellFormatter().setStyleName(0, 1, AON.CSS.aonWidthAll());
		table.getFlexCellFormatter().setColSpan(0, 1, 3);
		
		table.setWidget(1,0,new InlineLabel(AON.MSG.scope()));
		table.getCellFormatter().setStyleName(1, 0, AON.CSS.aonTableLabel());
		table.getCellFormatter().getElement(1, 0).setPropertyString("min-width", "135px");
		scope.clear();
		options.getConfiguration().getAvailableScopes().forEach(as -> scope.addItem(as.getDescription(), as.getId().toString()));
		scope.setStyleName(AON.CSS.aonInputText());
		setSelectedValueLB(scope, null != marketingCampaign.getScope() ? marketingCampaign.getScope().getId().toString() : null);
		table.setWidget(1,1,scope);
		
		active = new Button();
		table.setWidget(2,0,new InlineLabel("Activo"));
		table.getCellFormatter().setStyleName(2, 0, AON.CSS.aonTableLabel());
		table.getCellFormatter().getElement(2, 0).setPropertyString("min-width", "135px");
		getEnableDisableButton(active, marketingCampaign.isActive());
		active.addClickHandler(e -> getEnableDisableButton(active, !isActiveToggleButton(active)));
		table.setWidget(2,1,active);
		
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

		searchPanel.add(filterPanel);
		
		AonSearchPanelButton cleanButton = new AonSearchPanelButton( AON.MSG.clean(), AON.CSS.aonIconClear() );
		cleanButton.addClickHandler(event -> {
			actionDescription.setValue(null,false);
			mediaType.setSelectedIndex(0);
			startDate.setValue(null,false);
			endDate.setValue(null,false);
			
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
	
	private void showSelectedMarketingAction(MarketingAction marketingAction) {
		showWidget(1);
		marketingActionEntryPanel.setMarketingAction(marketingAction);
	}
	
	protected abstract void onBackClick();

}
