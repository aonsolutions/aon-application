package com.esferalia.aon.gwt.marketing.client.commercial;

import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.AonDateUtils;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDockLayout;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomListBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomMultiSelectBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonSearchPanelButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.occam.api.model.SellerWorkloadParams;
import com.esferalia.aon.occam.api.model.registry.SellerWorkload;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
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
	private AonCustomListBox active = new AonCustomListBox("Estado Agente");
	private AonCustomListBox customer = new AonCustomListBox("Agentes");
	private AonCustomMultiSelectBox customerStatus = new AonCustomMultiSelectBox("Estado Cliente");
	private AonCustomListBox type = new AonCustomListBox("Tipo");
	
	private AonCustomListBox sort = new AonCustomListBox("Ordenar Por");
	private AonCustomListBox asc = new AonCustomListBox("Orden");
	
	private SellerModuleOptions options;
	
	private SellerWorkloadPanel sellerWorkloadPanel;
	
	public SellerWorkloadModulePanel(SellerModuleOptions options) {
		super("Carga Trabajo");
		
		this.options = options;
		addButtonsToolbar();
		
		addKeyUpHandler(e -> {
			String value = getSearchTextBox().getValue();
			if(AonStringUtils.isNotBlank(value) && value.length() > 2) {
				onSearch( options );
			} else if(AonStringUtils.isBlank(value)) {
				onSearch( options );
			}
		});
		
		setSearchPlaceholder("Buscar por nombre ...");
		
		Date date = DateUtils.getFirstDayOfMonth(new Date());
		
		period.addItem( "Mes anterior ("  + AonDateUtils.formatMonthYear(DateUtils.addMonths2Date(date, -1)) + ")", "0");
		period.addItem( "Mes anterior y actual", "1");
		period.addItem( "Mes actual ("  + AonDateUtils.formatMonthYear(DateUtils.addMonths2Date(date, 1)) + ")", "2");
		period.addItem( "Mes actual y pr\u00f3ximo mes", "3");
		period.addItem( "Pr\u00f3ximo mes ("  + AonDateUtils.formatMonthYear(DateUtils.addMonths2Date(date, 1)) + ")", "4");
		period.setValue("2");
		period.getListBox().addChangeHandler(event -> {
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
		customer.getListBox().addChangeHandler(event -> {
			customerStatus.setVisible(customer.getListBox().getSelectedIndex() == 1);
			onSearch( options );	
		});
		
		// Customer Status
		Set<String> customerStatusoptions = new LinkedHashSet<String>();
		customerStatusoptions.add("Activo");
		customerStatusoptions.add("Inactivo");
		customerStatusoptions.add("Bloqueado");
		customerStatus.setOptions(customerStatusoptions);
		
		customerStatus.addBlurHandler(new BlurHandler() {
            @Override
            public void onBlur(BlurEvent event) {
            	if(customerStatus.getSelectedOptions().isEmpty()) {
            		Set<String> selectedOptions = new LinkedHashSet<String>();
            		selectedOptions.add("Activo");
            		customerStatus.setSelectedOptions(selectedOptions);
            	}
            	
            	onSearch( options );
            }
        });
		
		Set<String> selectedOptions = new LinkedHashSet<String>();
		selectedOptions.add("Activo");
		customerStatus.setSelectedOptions(selectedOptions);
		
		type.addItem( "Cuotas", "false");
		type.addItem( "Expedientes", "true");
		type.getListBox().addChangeHandler(event -> {
			boolean typeValue = Boolean.parseBoolean(type.getValue());
			scope.setVisible(!typeValue);
			active.setVisible(!typeValue);
			onSearch( options );
		});
		
		addFilterWidget(period);
		addFilterWidget(scope);
		addFilterWidget(active);
		addFilterWidget(customer);
		addFilterWidget(customerStatus);
		addFilterWidget(type);
		
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
		customer.getListBox().setSelectedIndex(1);
		Set<String> selectedOptions = new LinkedHashSet<String>();
		selectedOptions.add("Activo");
		customerStatus.setSelectedOptions(selectedOptions);
		period.setValue("2");
		
		sellerWorkloadPanel.resetSearchOffset();
		
		onSearch( options );
	}

	private void addButtonsToolbar() {
		AonToolbarButton downloadExcel = new AonToolbarButton("Exportar Excel", AON.CSS.aonIconDownload());
		downloadExcel.addClickHandler(e -> {
			JSONObject json = new JSONObject();
			
			SellerWorkloadParams sellerWorkloadListParams = getWidgetParams( options );
			
			if(null != sellerWorkloadListParams.getScope())
				json.put("scope", new JSONString(sellerWorkloadListParams.getScope().toString()));
			
			if(null != sellerWorkloadListParams.getPeriod())
				json.put("period", new JSONString(sellerWorkloadListParams.getPeriod().toString()));
			
			if(null != sellerWorkloadListParams.getActive())
				json.put("active", new JSONString(sellerWorkloadListParams.getActive().toString()));
			
			if(null != sellerWorkloadListParams.getCustomers())
				json.put("customer", new JSONString(sellerWorkloadListParams.getCustomers().toString()));
			
			if(null != sellerWorkloadListParams.getCustomerActive())
				json.put("customerActive", new JSONString( Boolean.toString( sellerWorkloadListParams.getCustomerActive() )));
			
			if(null != sellerWorkloadListParams.getCustomerInactive())
				json.put("customerInactive", new JSONString( Boolean.toString( sellerWorkloadListParams.getCustomerInactive() )));
			
			if(null != sellerWorkloadListParams.getCustomerBlocked())
				json.put("customerBlocked", new JSONString( Boolean.toString( sellerWorkloadListParams.getCustomerBlocked() )));
			
			json.put("description", new JSONString(sellerWorkloadListParams.getDescription()));
			json.put("isSellersWorkload", new JSONString("true"));
			json.put("byProject", new JSONString(type.getValue()));
			
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
			.setPeriod(Byte.parseByte(period.getValue()))
			.setCustomerActive(customerStatus.getSelectedOptions().contains("Activo"))
			.setCustomerInactive(customerStatus.getSelectedOptions().contains("Inactivo"))
			.setCustomerBlocked(customerStatus.getSelectedOptions().contains("Bloqueado"))
			;
		
		sellerWorkloadParams.setDomainName(options.getDomainName())
			.setDomain(options.getDomain())
			.setUser(options.getUser())
			.setDescription(getSearchTextBox().getValue())
			.setScope(AonStringUtils.isBlank(scope.getValue()) ? null : Integer.parseInt(scope.getValue()))
			.setActive(AonStringUtils.isBlank(active.getValue()) ? null : Byte.parseByte(active.getValue()))
			.setByProject(Boolean.parseBoolean(type.getValue()))
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
