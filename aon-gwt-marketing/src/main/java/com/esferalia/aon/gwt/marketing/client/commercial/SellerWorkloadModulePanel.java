package com.esferalia.aon.gwt.marketing.client.commercial;

import java.util.function.Consumer;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDockLayout;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomListBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonSearchPanelButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.occam.api.model.SellerWorkloadParams;
import com.esferalia.aon.occam.api.model.registry.SellerWorkload;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;


public abstract class SellerWorkloadModulePanel extends AonCustomDockLayout {
	
	private HTMLPanel container;
	private HTMLPanel messagePanel = new HTMLPanel("");
	
	private SimpleLayoutPanel centerPanel;
	
	private AonSearchPanelButton cleanButton;
	
	private AonCustomListBox period = new AonCustomListBox("Periocidad");
	private AonCustomListBox scope = new AonCustomListBox("Ambito");
	private AonCustomListBox active = new AonCustomListBox("Activo");
	private AonCustomListBox customer = new AonCustomListBox("Clientes");
	
	private AonCustomListBox sort = new AonCustomListBox("Ordenar Por");
	private AonCustomListBox asc = new AonCustomListBox("Orden");
	
	private SellerModuleOptions options;
	
	private SellerWorkloadPanel sellerWorkloadPanel;
	
	public SellerWorkloadModulePanel(SellerModuleOptions options) {
		super("Carga Trabajo");
		
		this.options = options;
		
		addButtonsToolbar();
		getSearchTextBox().addKeyUpHandler(e -> {
			String value = getSearchTextBox().getValue();
			if(AonStringUtils.isNotBlank(value) && value.length() > 3) {
				onSearch( options );
			} else if(AonStringUtils.isBlank(value)) {
				onSearch( options );
			}
		});
		
		
		addFilterMenu();
		
		cleanButton = new AonSearchPanelButton( AON.MSG.clean(), AON.CSS.aonIconClear() );
		cleanButton.addClickHandler(event -> {
			getSearchTextBox().setValue(null, false);
			scope.getListBox().setSelectedIndex(0);
			active.getListBox().setSelectedIndex(0);
			customer.getListBox().setSelectedIndex(0);
			period.getListBox().setSelectedIndex(0);
			
			sellerWorkloadPanel.resetSearchOffset();
			
			onSearch( options );
		});

		addFilterToolbarButton(cleanButton);
		
		period.addItem( "Mes actual", "0");
		period.addItem( "Pr\u00f3ximos 2 meses", "1");
		period.addItem( "Pr\u00f3ximos 3 meses", "2");
		period.getListBox().addChangeHandler(event -> onSearch( options ));
		
		scope.addItem("-", "");
		options.getConfiguration().getAvailableScopes().forEach(sc -> scope.addItem(sc.getDescription(), sc.getId() + ""));
		scope.getListBox().setSelectedIndex(0);
		scope.getListBox().addChangeHandler(event -> onSearch( options ));
		
		active.addItem( "Todas", "");
		active.addItem( "Inactivas", "1");
		active.addItem( "Activas", "0");
		active.getListBox().setSelectedIndex(2);
		active.getListBox().addChangeHandler(event -> onSearch( options ));
		
		customer.addItem( "Todas", "");
		customer.addItem( "Con clientes", "1");
		customer.addItem( "Sin clientes", "0");
		customer.getListBox().setSelectedIndex(0);
		customer.getListBox().addChangeHandler(event -> onSearch( options ));
		
		addFilterWidget(period);
		addFilterWidget(scope);
		addFilterWidget(active);
		addFilterWidget(customer);
		
		addSortMenu();
		
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

	private void addButtonsToolbar() {
		AonToolbarButton downloadExcel = new AonToolbarButton("Exportar Excel", AON.CSS.aonIconDownload());
		downloadExcel.addClickHandler(e -> {
			Window.alert("Exportar cargas de trabajo a Excel");
			
//			String fileDownloadURL = 
//					"/ms/api/seller-excel/" + 
//					"?domainId=" + options.getDomain() + 
//					"&domainName=" + options.getDomainName() + 
//					"&login=" + options.getUser() +
//					"&description=" + getSearchTextBox().getValue() +
//					"&scope=" + scope.getValue() +
//					"&active=" + active.getValue() +
//					"&orderBy=" + sort.getValue() +
//					"&asc=" + asc.getValue()
//					;
//			
//			Window.open(fileDownloadURL, "_blank", null);
		});
		addToolbarButton(downloadExcel);
	}

	public void onSearch( SellerModuleOptions options ) {
		SellerWorkloadParams params = getWidgetParams( options );
		sellerWorkloadPanel = new SellerWorkloadPanel(params) {

			@Override
			protected void onSellerWorkloadOpen(SellerWorkload sellerWorkload) {
				onSellerWorkloadSelect(sellerWorkload);
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
			
		centerPanel.setWidget(sellerWorkloadPanel);
	}

	public SellerWorkloadParams getWidgetParams( SellerModuleOptions options) {
		SellerWorkloadParams sellerWorkloadParams = new SellerWorkloadParams()
			.setCustomers(AonStringUtils.isBlank(customer.getValue()) ? null : Byte.parseByte(customer.getValue()))
			.setPeriod(Byte.parseByte(period.getValue()));
			
		sellerWorkloadParams.setDomainName(options.getDomainName())
			.setDomain(options.getDomain())
			.setUser(options.getUser())
			.setDescription(getSearchTextBox().getValue())
			.setScope(AonStringUtils.isBlank(scope.getValue()) ? null : Integer.parseInt(scope.getValue()))
			.setActive(AonStringUtils.isBlank(active.getValue()) ? null : Byte.parseByte(active.getValue()))
			.setOrderBy(sort.getValue())
			.setAsc(Boolean.parseBoolean(asc.getValue()))
			;
		
		return sellerWorkloadParams;
	}
	
	public void getSellerListCount(Consumer<Integer> finish) {
		if(null == sellerWorkloadPanel || null ==  sellerWorkloadPanel.getTable()) finish.accept(0);
		
		sellerWorkloadPanel.getSellerListCount(count -> {
			finish.accept(count);
		});
	}

	public Integer getSellerListPosition(Integer sellerId) {
		return null == sellerId || null == sellerWorkloadPanel ? 0 : sellerWorkloadPanel.getSellerListPosition(sellerId);
	}
	
	public SellerWorkloadParams getSellerListParams() {
		return getWidgetParams(options);
	}
	
	protected abstract void onSellerWorkloadSelect(SellerWorkload sellerWorkload);

}
