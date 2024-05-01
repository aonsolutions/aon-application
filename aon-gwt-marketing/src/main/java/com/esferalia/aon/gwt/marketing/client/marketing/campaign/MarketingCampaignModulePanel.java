package com.esferalia.aon.gwt.marketing.client.marketing.campaign;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMarketingCampaignPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMarketingCampaignPanel.AonMarketingCampaignPanelCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonSearchPanelButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.marketing.client.marketing.MarketingModuleOptions;
import com.esferalia.aon.occam.api.model.MarketingCampaign;
import com.esferalia.aon.occam.api.model.MarketingCompaignParams;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.TextBox;


public abstract class MarketingCampaignModulePanel extends DockLayoutPanel {

	private HTMLPanel container;
	private HTMLPanel messagePanel = new HTMLPanel("");
	
	private SimpleLayoutPanel northPanel;
	private SimpleLayoutPanel centerPanel;
	
	private FlowPanel searchPanel;
	private FlowPanel filterPanel;
	
	private TextBox description;
	private ListBox scope;
	private ListBox active;
	
	private AonSearchPanelButton cleanButton;
	private AonSearchPanelButton refreshButton;
	
	private MarketingModuleOptions options;
	
	private MarketingCompaignPanel marketingCompaignPanel;
	
	public MarketingCampaignModulePanel(MarketingModuleOptions options) {
		super(Unit.PX);
		addStyleName(AON.CSS.aonScrollArea());
		addStyleName(AON.CSS.aonMarginBottom());
		
		this.options = options;
		
		createToolbar();
		
		container = new HTMLPanel("");
		container.addStyleName(AON.CSS.aonFlexColumn());
		
		container.add(messagePanel);
		
		northPanel = new SimpleLayoutPanel();
		northPanel.setHeight("35px");
		northPanel.getElement().getStyle().setProperty("margin", "0 1rem");
		
		description = new TextBox();
		description.setVisibleLength(50);
		description.setStyleName(AON.CSS.aonInputText());
		description.addValueChangeHandler(event -> onSearch( options ));
		
		scope = new ListBox();
		scope.addItem( "-", "");
		options.getConfiguration().getAvailableScopes().forEach(sc -> scope.addItem(sc.getDescription(), sc.getId() + ""));
		scope.setSelectedIndex(0);
		scope.setStyleName(AON.CSS.aonInputText());
		scope.addChangeHandler(event -> onSearch( options ));
		
		active = new ListBox();
		active.addItem( "Todas", "");
		active.addItem( "Inactivas", "0");
		active.addItem( "Activas", "1");
		active.setSelectedIndex(2);
		active.setStyleName(AON.CSS.aonInputText());
		active.addChangeHandler(event -> onSearch( options ));
		
		searchPanel = new FlowPanel();
		searchPanel.setStyleName(AON.CSS.aonSearchPanel());
		searchPanel.addStyleName(AON.CSS.aonFlexBetween());
		
		filterPanel = new FlowPanel();
		filterPanel.addStyleName(AON.CSS.aonItemFlex());
		
		Label aliasLabel = new Label(AON.MSG.description());
		aliasLabel.setStyleName(AON.CSS.aonSearchPanelLabel());
		filterPanel.add(aliasLabel);
		filterPanel.add(description);
		
		Label typeLabel = new Label(AON.MSG.scope());
		typeLabel.setStyleName(AON.CSS.aonSearchPanelLabel());
		filterPanel.add(typeLabel);
		filterPanel.add(scope);

		filterPanel.add(active);

		searchPanel.add(filterPanel);
		
		cleanButton = new AonSearchPanelButton( AON.MSG.clean(), AON.CSS.aonIconClear() );
		cleanButton.addClickHandler(event -> {
			description.setValue(null,false);
			scope.setSelectedIndex(0);
			active.setSelectedIndex(0);
			
			marketingCompaignPanel.resetSearchOffset();
			
			onSearch( options );
		});

		refreshButton = new AonSearchPanelButton( AON.MSG.refresh(), AON.CSS.aonIconRefresh() );
		refreshButton.addStyleName(AON.CSS.aonMarginLeft());
		refreshButton.addClickHandler(event -> onSearch( options ));

		FlowPanel buttonsPanel = new FlowPanel();
		buttonsPanel.add( cleanButton );
		buttonsPanel.add( refreshButton );
		searchPanel.add(buttonsPanel);
		
		northPanel.setWidget(searchPanel);

		centerPanel = new SimpleLayoutPanel();
		centerPanel.setHeight((Window.getClientHeight() - 230) + "px");
		
		container.add(northPanel);
		container.add(centerPanel);
		
		add(container);
		onSearch( options );
	}
	
	private void createToolbar() {
		AonToolbar toolbar = new AonToolbar( "CAMPA\u00D1AS" );
		
		final AonToolbarButton newButton = new AonToolbarButton( "Nueva Campa\u00f1a", AON.CSS.aonIconAdd());
		newButton.addClickHandler(e -> showMarketingCompaignDialog());
		toolbar.add(newButton);
		
		addNorth(toolbar, 50);
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
		dialog.showLoaded();
	}

	public void onSearch( MarketingModuleOptions options ) {
		MarketingCompaignParams params = getWidgetParams( options );
		marketingCompaignPanel = new MarketingCompaignPanel(params, options) {

			@Override
			protected void onMarketingCampaignOpen(MarketingCampaign marketingCampaign) {
				onMarketingCampaignSelect(marketingCampaign);
			}

			@Override
			protected void onShowErrorMessage(String errorMessage) {
				AonMessagePanel.showError(messagePanel, errorMessage);
			}
		
		};
			
		centerPanel.setWidget(marketingCompaignPanel);
	}

	public MarketingCompaignParams getWidgetParams( MarketingModuleOptions options) {
		return new MarketingCompaignParams()
			.setDomainName(options.getDomainName())
			.setDomain(options.getDomain())
			.setUser(options.getUser())
			.setDescription(description.getValue())
			.setScope(scope.getSelectedIndex() == 0 ? null : Integer.parseInt(scope.getSelectedValue()))
			.setActive(active.getSelectedIndex() == 0 ? null : Byte.parseByte(active.getSelectedValue()))
			;
	}
	
	protected abstract void onMarketingCampaignSelect(MarketingCampaign marketingCampaign);
	
}
