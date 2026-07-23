package com.esferalia.aon.gwt.marketing.client.commercial;

import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.common.client.AON;
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
	
	private AonCustomListBox month = new AonCustomListBox("Mes");
	private AonCustomListBox year = new AonCustomListBox("A\u00f1o");
	private AonCustomListBox compareWith = new AonCustomListBox("Comparar con");
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
		int currentMonth = DateUtils.getMonth(date);
		int currentYear = DateUtils.getYear(date);

		String[] monthNames = new String[] {
			"Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
			"Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
		};

		for (int i = 0; i < monthNames.length; i++) {
			month.addItem(monthNames[i], Integer.toString(i));
		}
		month.setValue(Integer.toString(currentMonth));
		month.getListBox().addChangeHandler(event -> onSearch(options));

		for (int y = currentYear; y >= currentYear - 3; y--) {
			year.addItem(Integer.toString(y), Integer.toString(y));
		}
		year.setValue(Integer.toString(currentYear));
		year.getListBox().addChangeHandler(event -> onSearch(options));

		compareWith.addItem("Sin comparativa", "none");
		compareWith.addItem("Con mes anterior", "previous");
		compareWith.addItem("Con mes siguiente", "next");
		compareWith.setValue("none");
		compareWith.getListBox().addChangeHandler(event -> onSearch(options));
		
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
		
		HTMLPanel monthYearPanel = new HTMLPanel("");
		monthYearPanel.addStyleName(AON.CSS.aonItemFlex());
		monthYearPanel.addStyleName("month");
		monthYearPanel.add(month);
		monthYearPanel.add(year);

		addFilterWidget(monthYearPanel);
		addFilterWidget(compareWith);
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
		
		AonMessagePanel.showLoading(messagePanel, "Cargando ... (si tiene muchos agentes/clientes, puede tardar un poco)");
		
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
		Date now = new Date();
		month.setValue(Integer.toString(DateUtils.getMonth(now)));
		year.setValue(Integer.toString(DateUtils.getYear(now)));
		compareWith.setValue("none");
		
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

			if(null != sellerWorkloadListParams.getPeriodStart())
				json.put("periodStart", new JSONString(Long.toString(sellerWorkloadListParams.getPeriodStart().getTime())));

			if(null != sellerWorkloadListParams.getPeriodEnd())
				json.put("periodEnd", new JSONString(Long.toString(sellerWorkloadListParams.getPeriodEnd().getTime())));
			
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
			
			@Override
			protected void onHideMessage() {
				AonMessagePanel.hideMessage(messagePanel);
			}
		
		};
			
		centerPanel.setWidget(sellerWorkloadPanel);
	}

	public SellerWorkloadParams getWidgetParams( SellerModuleOptions options) {
		SellerWorkloadParams sellerWorkloadParams = new SellerWorkloadParams()
			.setCustomers(AonStringUtils.isBlank(customer.getValue()) ? null : Byte.parseByte(customer.getValue()))
			.setPeriod(getSelectedPeriod())
			.setPeriodStart(getSelectedPeriodStart())
			.setPeriodEnd(getSelectedPeriodEnd())
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

	private Date getSelectedPeriodStart() {
		Date selectedStart = DateUtils.getDate(Integer.parseInt(month.getValue()), Integer.parseInt(year.getValue()));

		if ("previous".equals(compareWith.getValue())) {
			selectedStart = DateUtils.addMonths2Date(selectedStart, -1);
		}

		return DateUtils.getFirstDayOfMonth(selectedStart);
	}

	private Date getSelectedPeriodEnd() {
		Date selectedEnd = DateUtils.getDate(Integer.parseInt(month.getValue()), Integer.parseInt(year.getValue()));

		if ("next".equals(compareWith.getValue())) {
			selectedEnd = DateUtils.addMonths2Date(selectedEnd, 1);
		}

		selectedEnd = DateUtils.getLastDayOfMonth(selectedEnd);
		selectedEnd.setHours(23);
		selectedEnd.setMinutes(59);
		selectedEnd.setSeconds(59);

		return selectedEnd;
	}

	private Byte getSelectedPeriod() {
		Date currentDate = DateUtils.getFirstDayOfMonth(new Date());
		Date selectedDate = DateUtils.getDate(Integer.parseInt(month.getValue()), Integer.parseInt(year.getValue()));

		int monthDiff = DateUtils.getMonths(selectedDate, currentDate);

		String compareValue = compareWith.getValue();
		if ("previous".equals(compareValue)) {
			if (monthDiff >= 1) {
				return (byte) 3;
			}
			return (byte) 1;
		}

		if ("next".equals(compareValue)) {
			if (monthDiff <= -1) {
				return (byte) 1;
			}
			return (byte) 3;
		}

		if (monthDiff <= -1) {
			return (byte) 0;
		}
		if (monthDiff >= 1) {
			return (byte) 4;
		}
		return (byte) 2;
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
