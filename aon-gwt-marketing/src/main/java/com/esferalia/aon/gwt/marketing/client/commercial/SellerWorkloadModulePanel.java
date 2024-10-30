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
import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
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
			customer.getListBox().setSelectedIndex(1);
			period.getListBox().setSelectedIndex(0);
			
			sellerWorkloadPanel.resetSearchOffset();
			
			onSearch( options );
		});

		addFilterToolbarButton(cleanButton);
		
		period.addItem( "Mes actual", "0");
		period.addItem( "Pr\u00f3ximos 2 meses", "1");
		period.addItem( "Pr\u00f3ximos 3 meses", "2");
		period.getListBox().addChangeHandler(event -> {
			this.clickFilterButton();
			onSearch( options );
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
		
		customer.addItem( "Todas", "");
		customer.addItem( "Con clientes", "1");
		customer.addItem( "Sin clientes", "0");
		customer.getListBox().setSelectedIndex(1);
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
			JSONObject json = new JSONObject();
			
			SellerWorkloadParams sellerWorkloadListParams = getWidgetParams( options );
			
			json.put("period", new JSONString(sellerWorkloadListParams.getPeriod().toString()));
			json.put("scope", new JSONString(sellerWorkloadListParams.getScope().toString()));
			json.put("active", new JSONString(sellerWorkloadListParams.getActive().toString()));
			json.put("customer", new JSONString(sellerWorkloadListParams.getCustomers().toString()));
			json.put("description", new JSONString(sellerWorkloadListParams.getDescription()));
			json.put("isSellersWorkload", new JSONString("true"));
			
			String fileDownloadURL = GWT.getModuleBaseURL()+ "ms/gwt_download_fee/"
	            	+ "?filter=" + btoa(json.toString())
	            	+ "&domain_name=" + options.getDomainName()
	            	+ "&domain_id=" + options.getDomain()
					+ "&username="+ options.getUser();
			
			Window.open( fileDownloadURL, "_blank",null);
		});
		
		addToolbarButton(downloadExcel);
	}
	
	private native String btoa(String str) /*-{
	    return btoa(str);
	}-*/;

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
		return sellerWorkloadPanel.getSellerListPosition(sellerId);
	}
	
	public SellerWorkloadParams getSellerListParams() {
		return getWidgetParams(options);
	}
	
	protected abstract void onSellerWorkloadSelect(SellerWorkload sellerWorkload);

}
