package com.esferalia.aon.gwt.marketing.client.commercial;

import java.util.function.Consumer;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDockLayout;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomListBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonSellerPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonSellerPanel.AonSellerPanelCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.occam.api.model.SellerParams;
import com.esferalia.aon.occam.api.model.registry.Seller;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;


public abstract class SellerModulePanel extends AonCustomDockLayout {
	
	private HTMLPanel container;
	private HTMLPanel messagePanel = new HTMLPanel("");
	
	private AonToolbarButton deleteButton;
	
	private SimpleLayoutPanel centerPanel;
	
	private AonCustomListBox scope = new AonCustomListBox("Ambito");
	private AonCustomListBox active = new AonCustomListBox("Activo");
	
	private AonCustomListBox sort = new AonCustomListBox("Ordenar Por");
	private AonCustomListBox asc = new AonCustomListBox("Orden");
	
	private SellerModuleOptions options;
	
	private SellerPanel sellerPanel;
	
	public SellerModulePanel(SellerModuleOptions options) {
		super("Agentes Comerciales");
		
		this.options = options;
		
		addButtonsToolbar();
		
		addKeyUpHandler(e -> {
			String value = getSearchTextBox().getValue();
			if(AonStringUtils.isNotBlank(value) && value.length() > 3) {
				onSearch( options );
			} else if(AonStringUtils.isBlank(value)) {
				onSearch( options );
			}
		});
		
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
		
		sort.addItem("Nombre", "name");
		sort.addItem("Alias", "alias");
		sort.addItem("Documento", "document");
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
	}

	@Override
	protected void onClearFilter() {
		getSearchTextBox().setValue(null, false);
		scope.getListBox().setSelectedIndex(0);
		active.getListBox().setSelectedIndex(0);
		
		sellerPanel.resetSearchOffset();
		
		onSearch( options );
	}

	private void addButtonsToolbar() {
		AonToolbarButton newButton = new AonToolbarButton( "Nuevo Agente Comercial", AON.CSS.aonIconAdd());
		newButton.addClickHandler(e -> showSellerDialog());
		
		addToolbarButton(newButton);
		
		AonToolbarButton downloadExcel = new AonToolbarButton("Exportar Excel", AON.CSS.aonIconDownload());
		downloadExcel.addClickHandler(e -> {
			String fileDownloadURL = 
					"/ms/api/seller-excel/" + 
					"?domainId=" + options.getDomain() + 
					"&domainName=" + options.getDomainName() + 
					"&login=" + options.getUser() +
					"&description=" + getSearchTextBox().getValue() +
					"&scope=" + scope.getValue() +
					"&active=" + active.getValue() +
					"&orderBy=" + sort.getValue() +
					"&asc=" + asc.getValue()
					;
			
			Window.open(fileDownloadURL, "_blank", null);
		});
		addToolbarButton(downloadExcel);
		
		deleteButton = new AonToolbarButton( "Borrar Agente Comercial", AON.CSS.aonIconDelete());
		deleteButton.addClickHandler(e -> {
			deleteButton.setEnabled(false);
			sellerPanel.deleteSellers();
		});
		deleteButton.setEnabled(false);
		
		addToolbarButton(deleteButton);
	}

	private void showSellerDialog() {
		final AonCustomDialog dialog = new AonCustomDialog();
		dialog.setCaption( "Nuevo Agente Comercial" );
		final AonSellerPanel marketingCampaignPanel = new AonSellerPanel( options.getDomainName(), options.getDomain(), options.getUser(), options.getConfiguration().getAvailableScopes(), new AonSellerPanelCallback() {
			
			@Override
			public void onCancel() {
				dialog.hide();
			}
			
			@Override
			public void onAccept(Seller seller) {
				dialog.hide();
				onSellerCreate(seller);
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
			protected void onDeleteEnable(boolean enabled) {
				deleteButton.setEnabled(enabled);
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
			.setOrderBy(sort.getValue())
			.setAsc(Boolean.parseBoolean(asc.getValue()))
			;
	}
	
	public void getSellerListCount(Consumer<Integer> finish) {
		if(null == sellerPanel || null ==  sellerPanel.getTable()) finish.accept(0);
		
		sellerPanel.getSellerListCount(count -> {
			finish.accept(count);
		});
	}

	public Integer getSellerListPosition(Integer sellerId) {
		return null == sellerId || null == sellerPanel ? 0 : sellerPanel.getSellerListPosition(sellerId);
	}
	
	public SellerParams getSellerListParams() {
		return getWidgetParams(options);
	}
	
	protected abstract void onSellerSelect(Seller seller);
	protected abstract void onSellerCreate(Seller seller);

}
