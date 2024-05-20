package com.esferalia.aon.gwt.marketing.client.commercial;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDockLayout;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomListBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonSearchPanelButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonSellerPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonSellerPanel.AonSellerPanelCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.occam.api.model.SellerParams;
import com.esferalia.aon.occam.api.model.registry.Seller;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;


public abstract class SellerModulePanel extends AonCustomDockLayout {

	private HTMLPanel container;
	private HTMLPanel messagePanel = new HTMLPanel("");
	
	private SimpleLayoutPanel centerPanel;
	
	private AonCustomListBox scope = new AonCustomListBox("Ambito");
	private AonCustomListBox active = new AonCustomListBox("Activo");
	
	private AonSearchPanelButton cleanButton;
	
	private SellerModuleOptions options;
	
	private SellerPanel sellerPanel;
	
	public SellerModulePanel(SellerModuleOptions options) {
		super("Agentes comerciales");
		this.options = options;
		
		addButtonsToolbar();
		getSearchTextBox().addValueChangeHandler(e -> {
			onSearch( options );
		});
		
		addFilterMenu();
		
		cleanButton = new AonSearchPanelButton( AON.MSG.clean(), AON.CSS.aonIconClear() );
		cleanButton.addClickHandler(event -> {
			getSearchTextBox().setValue(null,false);
			scope.getListBox().setSelectedIndex(0);
			active.getListBox().setSelectedIndex(0);
			
			sellerPanel.resetSearchOffset();
			
			onSearch( options );
		});

		addFilterToolbarButton(cleanButton);
		
		scope.addItem("-", "");
		options.getConfiguration().getAvailableScopes().forEach(sc -> scope.addItem(sc.getDescription(), sc.getId() + ""));
		scope.getListBox().setSelectedIndex(0);
		scope.getListBox().addChangeHandler(event -> onSearch( options ));
		
		active.addItem( "Todas", "");
		active.addItem( "Inactivas", "1");
		active.addItem( "Activas", "0");
		active.getListBox().setSelectedIndex(2);
		active.getListBox().addChangeHandler(event -> onSearch( options ));
		
		addFilterWidget(scope);
		addFilterWidget(active);
		
		addUtilitiesMenu();
		addUtilityOption(new AonSearchPanelButton("Descargar fichero", AON.CSS.aonIconDownload()), "Exportar Excel");
		addUtilityOption(new AonSearchPanelButton("Subir fichero", AON.CSS.aonIconUpload()), "Subir Fichero");
		
		container = new HTMLPanel("");
		container.addStyleName(AON.CSS.aonFlexColumn());
		
		container.add(messagePanel);
	
		centerPanel = new SimpleLayoutPanel();
//		centerPanel.setHeight((Window.getClientHeight() - 180) + "px");
		centerPanel.setHeight("100%");
		centerPanel.getElement().getStyle().setProperty("margin-left", "1rem");
		
		container.add(centerPanel);
		
		add(container);
		onSearch( options );
	}
	
	private void addButtonsToolbar() {
		final AonToolbarButton newButton = new AonToolbarButton( "Nuevo Agente Comercial", AON.CSS.aonIconAdd());
		newButton.addClickHandler(e -> showSellerDialog());
		
		addToolbarButton(newButton);
	}
	
	private void showSellerDialog() {
		final AonCustomDialog dialog = new AonCustomDialog();
		dialog.setCaption( "NUEVO AGENTE COMERCIAL" );
		final AonSellerPanel marketingCampaignPanel = new AonSellerPanel( options.getDomainName(), options.getDomain(), options.getUser(), options.getConfiguration().getAvailableScopes(), new AonSellerPanelCallback() {
			
			@Override
			public void onCancel() {
				dialog.hide();
			}
			
			@Override
			public void onAccept(Seller seller) {
				dialog.hide();
				onSellerSelect(seller);
			}
		}) {

			@Override
			protected void onResize() {
				dialog.showLoaded();
			}};
		
		dialog.add( marketingCampaignPanel );
		dialog.showLoaded();
	}

	public void onSearch( SellerModuleOptions options ) {
		SellerParams params = getWidgetParams( options );
		sellerPanel = new SellerPanel(params) {

			@Override
			protected void onSellerOpen(Seller seller) {
				onSellerSelect(seller);
			}

			@Override
			protected void onShowErrorMessage(String errorMessage) {
				AonMessagePanel.showError(messagePanel, errorMessage);
			}
		
		};
			
		centerPanel.setWidget(sellerPanel);
	}

	public SellerParams getWidgetParams( SellerModuleOptions options) {
		return new SellerParams()
			.setDomainName(options.getDomainName())
			.setDomain(options.getDomain())
			.setUser(options.getUser())
			.setDescription(getSearchTextBox().getValue())
			.setScope(AonStringUtils.isBlank(scope.getValue()) ? null : Integer.parseInt(scope.getValue()))
			.setActive(AonStringUtils.isBlank(active.getValue()) ? null : Byte.parseByte(active.getValue()))
			;
	}
	
	protected abstract void onSellerSelect(Seller seller);
	
}
