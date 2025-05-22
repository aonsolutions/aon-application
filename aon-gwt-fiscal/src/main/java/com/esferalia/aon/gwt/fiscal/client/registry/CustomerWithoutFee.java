package com.esferalia.aon.gwt.fiscal.client.registry;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.RegistryService;
import com.esferalia.aon.gwt.common.client.RegistryServiceAsync;
import com.esferalia.aon.gwt.common.client.RegistryServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonContextMenu;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDockLayout;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomListBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonExpandButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.registry.CustomerParams;
import com.esferalia.aon.watson.mutable.MutableInt;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.SimplePanel;

public abstract class CustomerWithoutFee extends AonCustomDockLayout {
	
	// Customer Excel Export
	class ExcelExtendCommand implements ScheduledCommand {

		@Override
		public void execute() {
			exportCustomer(true);
		}
	}
	
	private void exportCustomer(boolean extend) {

		List<Integer> customerIdSelectedList = selectedItems.entrySet()
				.stream()
				.filter(entry -> AonStringUtils.containsIgnoreCase(entry.getValue().getStyleName(), AON.CSS.aonIconChecked()))
				.map(entry -> entry.getKey())
				.collect(Collectors.toList());
		
		// Exportacion masiva (todo seleccionado)
		if(customerIdSelectedList.size() == rowCustomers.size())
			exportCustomer(null, extend);
		else 
			exportCustomer(customerIdSelectedList, extend);
		
	}
	
	private void exportCustomer(List<Integer> customerIds, boolean extend) {
		JSONObject json = new JSONObject();
		
		if(null == customerIds || customerIds.isEmpty()) {
			if(AonStringUtils.isNotBlank(getSearchTextBox().getValue())) json.put("description", new JSONString(getSearchTextBox().getValue()));
			if(AonStringUtils.isNotBlank(status.getValue())) json.put("status", new JSONString(status.getValue()));
		} else {
			JSONObject feeJson = new JSONObject();
			for(int i=0; i<customerIds.size(); i++) 
				feeJson.put("customerId"+i, new JSONString(customerIds.get(i).toString()));
			
			json.put("customerIds", feeJson);
		}
		
		String fileDownloadURL = GWT.getModuleBaseURL()+ "download_customer_without_fee/"
            	+ "?filter=" + btoa(json.toString())
            	+ "&domain=" + options.getDomainName()
            	+ "&domainId=" + options.getDomain()
				+ "&login="+ options.getUser()
				+ "&type=excel";
		
		if(extend) fileDownloadURL += "&extend=true";
		
		Window.open( fileDownloadURL, "_blank",null);
	}
	
	private native String btoa(String str) /*-{
	    return btoa(str);
	}-*/;
	
	class ExcelExportMenu extends AonContextMenu {

		public ExcelExportMenu() {
			addMenuItem("Excel detallado", new ExcelExtendCommand(), AON.CSS.aonIconExcel(), "excelExtend");
		}
		
		private MenuItem addMenuItem(String title, ScheduledCommand command, String iconStyle, String debugId) {
			MenuItem item = addItem(title, command, iconStyle, AON.AON_ICON_CMD_BUTTON, AON.CSS.aonCmdItem());
			item.ensureDebugId(debugId);
			return item;
		}
	}
	
	// Services
	private static RegistryServiceAsync SERVICE;
	private static CommonServiceAsync COMMON_SERVICE;
	
	// Variables (UI)
	
	private HTMLPanel container;
	private HTMLPanel messagePanel = new HTMLPanel("");
	
	private AonToolbarButton backButton;
	private AonExpandButton exportCustomerButton;
	private AonCustomListBox status = new AonCustomListBox("Estado");
	
	private AonCustomListBox sort = new AonCustomListBox("Ordenar Por");
	private AonCustomListBox asc = new AonCustomListBox("Orden");
	
	private SimplePanel tableContainer;
	private ScrollPanel tableScrollPanel;
	private AonCustomTable tab;
	
	// Variables
	
	private RegistryModuleOptions options;
	private ExcelExportMenu excelExportMenu;
	
	private CustomerParams params;
	
	private Map<Integer, Customer> rowCustomers = new HashMap<>();
	private Map<Integer, AonTableButton> selectedItems = new HashMap<>();
	
	// Table UI
	
	private final int limit = 100;
	private final MutableInt offset = new MutableInt(0);
	private final MutableInt moreData = new MutableInt(0);
	private final MutableInt searchEnabled = new MutableInt(0);
	
	private int lastScrollPos = 0;

	private static enum COLS {
		  CHK(AonStringUtils.EMPTY					,"2rem"				,"")
		, DOC(AON.MSG.document()					,"6rem"				,"white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
		, DES(AON.MSG.name()						,"-moz-available"	,"min-width: 5rem; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
		, BUD(AON.MSG.alias()						,"12rem"			,"white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
		, TYP(AON.MSG.scope()						,"10rem"			,"white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
		, ACT("Estado"								,"6rem"				,"white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
		;

		String headerLabel;
		String colWidth;
		String cellStyleClass;

		private COLS(String headerLabel,String colWidth,String cellStyleClass) {
			this.headerLabel = headerLabel;
			this.colWidth = colWidth;
			this.cellStyleClass = cellStyleClass;
		}
		public String getColWidth() {
			return colWidth;
		}
		public String getHeaderLabel() {
			return headerLabel;
		}
		public String getStyles() {
			return cellStyleClass;
		}
	}

	// Constructor
	
	public CustomerWithoutFee(RegistryModuleOptions opt) {
		super("Clientes sin cuotas");
		
		excelExportMenu = new ExcelExportMenu();
		
		RegistryServiceAsync registryServiceRaw = GWT.create(RegistryService.class);
		SERVICE = new RegistryServiceAsyncDecorator(registryServiceRaw);

		CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
		COMMON_SERVICE = new CommonServiceAsyncDecorator(commonServiceRaw);
		
		this.options = opt;
		
		createToolbar();
	}

	private void createToolbar() {
		hideToolbarFilterMessages();
		setSearchPlaceholder("Busque por nombre, documento ...");
		addKeyUpHandler(e -> {
			String value = getSearchTextBox().getValue();
			if(AonStringUtils.isNotBlank(value) && value.length() > 2) {
				onSearch();
			} else if(AonStringUtils.isBlank(value)) {
				onSearch();
			}
		});
		
		backButton = new AonToolbarButton("Cuotas", AON.CSS.aonIconBack());
		backButton.addClickHandler(e -> showCustomerFee());
		addToolbarButton(backButton);
		
		exportCustomerButton = new AonExpandButton("Exportar Clientes",AON.CSS.aonIconExcel()) {

			@Override
			public void onExpandClick(ClickEvent event) {
				NativeEvent nativeEvent = event.getNativeEvent();
				excelExportMenu.setPopupPosition(nativeEvent.getClientX(), nativeEvent.getClientY());
				excelExportMenu.show();
			}

			@Override
			public void onDefaultClick(ClickEvent evet) {
				exportCustomer(false);
			}
		};
		addToolbarButton(exportCustomerButton);
		
		status.clearItems();
		status.addItem("-", "");
		status.addItem("Activo", "0");
		status.addItem("Inactivo", "1");
		status.addItem("Bloqueado", "2");
		status.addChangeHandler(e -> onSearch());
		addFilterWidget(status);
		
		sort.addItem("Nombre", "name");
		sort.addItem("Documento", "document");
		sort.addItem("Estado", "status");
		sort.getListBox().addChangeHandler(event -> onSearch());
		
		asc.addItem("Ascendente", "true");
		asc.addItem("Descendete", "false");
		asc.getListBox().addChangeHandler(event -> onSearch());
		
		addSortWidget(sort);
		addSortWidget(asc);
		
		container = new HTMLPanel("");
		container.addStyleName(AON.CSS.aonFlexColumn());
		
		container.add(messagePanel);
	
		tableContainer = new SimpleLayoutPanel();
		tableContainer.setHeight("100%");
		tableContainer.getElement().getStyle().setProperty("margin-left", "1rem");
		
		container.add(tableContainer);
		
		add(container);
	}

	@Override
	protected void onClearFilter() {
		getSearchTextBox().setValue(null, false);
		status.setValue(null);
		sort.setValue("name");
		resetSearchOffset();
		onSearch();
	}
	
	public void onSearch() {
		getWidgetParams();
		resetSearchOffset();
		onSearchData();
	}
	
	private void resetSearchOffset() {
		offset.setValue(0);
	}
	
	public boolean isSearchEnabled() {
		return (searchEnabled.getValue() == 0 );
	}
	public void disableSearch() {
		searchEnabled.setValue(-1);
	}
	public void enableSearch() {
		searchEnabled.setValue(0);
	}
	public boolean isMoreData() {
		return (moreData.getValue() == 0 );
	}
	public void disableMoreData() {
		moreData.setValue(-1);
	}
	public void enableMoreData() {
		moreData.setValue(0);
	}
	
	private void onSearchData() {
		enableMoreData();
		searchData();
	}
	
	private void getWidgetParams() {
		params = new CustomerParams()
				.setDomainName(options.getDomainName())
				.setDomain(options.getDomain())
				.setUser(options.getUser())
				.setDescription(getSearchTextBox().getValue())
				.setStatus(AonStringUtils.isBlank(status.getValue()) ? null : Byte.parseByte(status.getValue()))
				.setOrderBy(sort.getValue())
				.setAsc(Boolean.parseBoolean(asc.getValue()))
				;
	}
	
	private void searchData() {
		tableContainer.clear();
		tab = new AonCustomTable();
		tableScrollPanel = new ScrollPanel(tab);
		tableScrollPanel.getElement().getStyle().setProperty("margin", "0 1rem");
		
		tableScrollPanel.addScrollHandler(e -> {
			// ------------------------------------ Ignore scroll up.
			int oldScrollPos = lastScrollPos;
			lastScrollPos = tableScrollPanel.getVerticalScrollPosition();
			if (oldScrollPos >= lastScrollPos) {
				return;
			}
			// -----------------------------------------------------
			if (isSearchEnabled()) {
				int maxScrollTop = tableScrollPanel.getWidget().getOffsetHeight() - tableScrollPanel.getOffsetHeight();
				if (lastScrollPos >= maxScrollTop) {
					disableSearch();
					searchDataList();
				}
			}
		});
		
		paintHeader();
		tableContainer.add(tableScrollPanel);
		searchDataList();
	}
	
	private void paintHeader() {
		tab.createHeader();
		for ( COLS col : COLS.values()) 
			if(col == COLS.CHK) {
				AonTableButton checkAllButton = new AonTableButton(AON.MSG.selectAction(), AON.CSS.aonIconCheck());
				checkAllButton.addClickHandler(e -> {
					List<AonTableButton> selectedItemList = selectedItems.values().stream().filter(check -> AonStringUtils.containsIgnoreCase(check.getStyleName(), AON.CSS.aonIconChecked())).collect(Collectors.toList());
					if (selectedItemList.size() == rowCustomers.size() || AonStringUtils.containsIgnoreCase(checkAllButton.getStyleName(), AON.CSS.aonIconChecked())) {
						checkAllButton.addStyleName(AON.CSS.aonIconCheck());
						checkAllButton.removeStyleName(AON.CSS.aonIconChecked());
						selectedItems.values().forEach(check ->{
							check.addStyleName(AON.CSS.aonIconCheck());
							check.removeStyleName(AON.CSS.aonIconChecked());
						});
					} else {
						checkAllButton.addStyleName(AON.CSS.aonIconChecked());
						checkAllButton.removeStyleName(AON.CSS.aonIconCheck());
						selectedItems.values().forEach(check ->{
							check.addStyleName(AON.CSS.aonIconChecked());
							check.removeStyleName(AON.CSS.aonIconCheck());
						});
					}
				});
				
				tab.addHeader(checkAllButton, col.getColWidth());
			} else tab.addHeader(new Label(col.getHeaderLabel()), col.getColWidth(), col.getStyles());
	}

	private void searchDataList() {
		if (!isMoreData()) return;
		
		params.setOffset(offset.intValue());
		params.setLimit(limit);
		
		getList(customers -> {
			boolean something = false;
			
			for( Customer customer : customers) {
				something = true;
				paintRow(customer);
			}
			
			if (customers.size() < limit) {
				disableMoreData();
			} else {
				offset.setValue(offset.intValue() + customers.size() - 1);
				enableMoreData();
			}
			
			if (!something) {
				FlowPanel line = new FlowPanel();
				InlineLabel label = new InlineLabel(AON.MSG.noData());
				line.add(label);
				tableContainer.clear();
				tableContainer.add(line);
				disableMoreData();
			}
			enableSearch();
			
		});
	}
	
	private void paintRow(Customer customer) {
		HTMLPanel row = tab.createRow();
		row.addDomHandler(e -> {}, ClickEvent.getType());
		
		AonTableButton checkButton = new AonTableButton(AON.MSG.selectAction(), AON.CSS.aonIconCheck());
		checkButton.addClickHandler(e -> {
			e.stopPropagation();
			if (AonStringUtils.containsIgnoreCase(checkButton.getStyleName(), AON.CSS.aonIconChecked())) {
				checkButton.addStyleName(AON.CSS.aonIconCheck());
				checkButton.removeStyleName(AON.CSS.aonIconChecked());
			} else {
				checkButton.addStyleName(AON.CSS.aonIconChecked());
				checkButton.removeStyleName(AON.CSS.aonIconCheck());
			}
		});
		tab.addRow(row, checkButton, COLS.CHK.getColWidth());
		
		Label document = new Label(customer.getDocument());
		document.setTitle(customer.getDocument());
		tab.addInlineStyle(document, COLS.DOC.getStyles());
		tab.addRow(row, document, COLS.DOC.getColWidth());
		
		Label name = new Label(customer.getName());
		name.setTitle(customer.getName());
		tab.addInlineStyle(name, COLS.DES.getStyles());
		tab.addRow(row, name, COLS.DES.getColWidth());
		
		Label alias = new Label(customer.getAlias());
		alias.setTitle(customer.getAlias());
		tab.addInlineStyle(alias, COLS.BUD.getStyles());
		tab.addRow(row, alias, COLS.BUD.getColWidth());
		
		String scopeValue = customer.getScope() == null ? null : customer.getScope().getDescription();
		Label scope = new Label(scopeValue);
		scope.setTitle(scopeValue);
		tab.addInlineStyle(scope, COLS.TYP.getStyles());
		tab.addRow(row, scope, COLS.TYP.getColWidth());
		
		String statusValue = null == customer.getStatus() ? "N/D" : customer.getStatus().getDescription();
		Label status = new Label(statusValue);
		status.setTitle(statusValue);
		tab.addInlineStyle(status, COLS.ACT.getStyles());
		tab.addRow(row, status, COLS.ACT.getColWidth());
		
		rowCustomers.put(customer.getId(), customer);
		selectedItems.put(customer.getId(), checkButton);
	}
	
	private void getList(Consumer<List<Customer>> success) {
		SERVICE.getCustomerWithoutFee(params, new AsyncCallback<List<Customer>>() {
			
			@Override
			public void onSuccess(List<Customer> customers) {
				success.accept(customers);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				AonMessagePanel.showError(messagePanel, caught.getMessage());
			}
		});
	}

	protected abstract void showCustomerFee();
	
}
