package com.esferalia.aon.gwt.fiscal.client.registry;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.stream.Collectors;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.RegistryService;
import com.esferalia.aon.gwt.common.client.RegistryServiceAsync;
import com.esferalia.aon.gwt.common.client.RegistryServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDateBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog.AonAcceptDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.fiscal.client.MainEntryPoint;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.fee.Fee;
import com.esferalia.aon.occam.api.model.type.BillingPeriod;
import com.esferalia.aon.occam.api.model.type.RegistryStatus;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class CustomerFee extends MainEntryPoint {

	private static RegistryServiceAsync SERVICE;
	private static CommonServiceAsync COMMON_SERVICE;

	private RegistryModuleOptions options;

	private DockLayoutPanel dockLayoutPanel;

	private AonToolbar toolbar;
	private AonToolbarButton saveButton;
	private AonToolbarButton undoAllButton;
	
	private SuggestBox customerSuggestBox;
	private SuggestBox conceptSuggestBox;

	private HTMLPanel container;
	private HTMLPanel messagePanel;
	private ScrollPanel scrollPanel;

	private LinkedList<Fee> feeListAll = new LinkedList<>();
	private LinkedList<Fee> feeList = new LinkedList<>();

	private Map<CheckBox, Fee> selectionModel;

	private boolean hasChange = false;

	private Date findDate = new Date();

	@Override
	public void onModuleLoad() {
		RootLayoutPanel root = RootLayoutPanel.get(getRootPanel() != null ? getRootPanel() : "rootPanel");
		options = new RegistryModuleOptions();
		options.setParentWidget(root);
		options.setDomainName(getCurrentDomainName());
		options.setDomain(getCurrentDomain());
		options.setUser(getCurrentUser());
		this.onModuleLoad(options);
	}

	public void onModuleLoad(final RegistryModuleOptions opt) {
		AON.ensureInjected();

		RegistryServiceAsync registryServiceRaw = GWT.create(RegistryService.class);
		SERVICE = new RegistryServiceAsyncDecorator(registryServiceRaw);

		CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
		COMMON_SERVICE = new CommonServiceAsyncDecorator(commonServiceRaw);

		dockLayoutPanel = new DockLayoutPanel(Unit.PX);
		opt.getParentWidget().add(dockLayoutPanel);
		
		dockLayoutPanel.clear();

		createToolbar();
		dockLayoutPanel.addNorth(toolbar, 50);

		container = new HTMLPanel("");
		container.clear();
		container.addStyleName(AON.CSS.aonFlexColumn());
		container.getElement().getStyle().setProperty("margin", "0 1rem");
		dockLayoutPanel.add(container);

		messagePanel = new HTMLPanel("");
		
		scrollPanel = new ScrollPanel();
		scrollPanel.setHeight((Window.getClientHeight() - 230) + "px");

		if (opt.getConfiguration() == null) {
			COMMON_SERVICE.getAonConfiguration(opt.getDomainName(), opt.getDomain(), opt.getUser(),
					new AsyncCallback<AonConfiguration>() {
						@Override
						public void onSuccess(AonConfiguration result) {
							opt.setConfiguration(result);
							loadModule(opt);
						}

						@Override
						public void onFailure(Throwable caught) {
							dockLayoutPanel.add(new Label(
									AON.MSG.noActiveAccountPeriod() + "[Interno: " + caught.getMessage() + "]"));
						}
					});
		} else {
			loadModule(opt);
		}
	}

	private void loadModule(final RegistryModuleOptions opt) {
		AonMessagePanel.showLoading(messagePanel, "Cargando panel de facturaci\u00f3n ...");
		SERVICE.getCustomerFeeList(opt.getDomainName(), opt.getDomain(), opt.getUser(), findDate,
				new AsyncCallback<LinkedList<Fee>>() {

					@Override
					public void onFailure(Throwable caught) {
						AonMessagePanel.showError(messagePanel, "Error cargando panel de facturaci\u00f3n: " + caught.getMessage());
					}

					@Override
					public void onSuccess(LinkedList<Fee> feeListDB) {
						selectionModel = new HashMap<>();

						feeList.clear();
						feeList.addAll(feeListDB);

						feeListAll.clear();
						feeListAll.addAll(feeListDB);

						createCustomerFeeView();
						setHasChange(false);
						AonMessagePanel.hideMessage(messagePanel);
					}
				});
	}

	private void createCustomerFeeView() {
		container.clear();
		container.add(messagePanel);
		createFilterPanel();
		createFeeTable();
	}

	private void createFilterPanel() {
		HTMLPanel filterPanel = new HTMLPanel("");
		filterPanel.addStyleName(AON.CSS.aonFlexWrap());
		filterPanel.addStyleName(AON.CSS.aonFilterPanel());

		// Period
		HTMLPanel periodItemPanel = new HTMLPanel("");
		periodItemPanel.addStyleName(AON.CSS.aonItemFlex());

		Label periodLabel = new Label("Periodo");
		periodLabel.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		ListBox monthListBox = createMonthListBox();
		ListBox yearListBox = createYearListBox();

		monthListBox.setSelectedIndex(findDate.getMonth());
		monthListBox.addChangeHandler(e -> {
			Integer month = Integer.parseInt(monthListBox.getSelectedValue());
			findDate.setMonth(month);
			loadModule(options);
		});

		yearListBox.addChangeHandler(e -> {
			Integer year = Integer.parseInt(yearListBox.getSelectedValue());
			findDate.setYear(year);
			loadModule(options);
		});

		periodItemPanel.add(periodLabel);
		periodItemPanel.add(monthListBox);
		periodItemPanel.add(yearListBox);

		filterPanel.add(periodItemPanel);

		// Customer
		HTMLPanel customerItemPanel = new HTMLPanel("");
		customerItemPanel.addStyleName(AON.CSS.aonItemFlex());

		Label customerLabel = new Label("Cliente");
		customerLabel.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		createCustomerSuggestBox();

		customerItemPanel.add(customerLabel);
		customerItemPanel.add(customerSuggestBox);

		filterPanel.add(customerItemPanel);

		// Customer
		HTMLPanel conceptItemPanel = new HTMLPanel("");
		conceptItemPanel.addStyleName(AON.CSS.aonItemFlex());

		Label conceptLabel = new Label("Concepto");
		conceptLabel.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		createConceptSuggestBox();

		conceptItemPanel.add(conceptLabel);
		conceptItemPanel.add(conceptSuggestBox);

		filterPanel.add(conceptItemPanel);

		container.add(filterPanel);
	}

	private ListBox createMonthListBox() {
		ListBox lb = new ListBox();

		lb.addItem("Enero", "0");
		lb.addItem("Febrero", "1");
		lb.addItem("Marzo", "2");
		lb.addItem("Abril", "3");
		lb.addItem("Mayo", "4");
		lb.addItem("Junio", "5");
		lb.addItem("Julio", "6");
		lb.addItem("Agosto", "7");
		lb.addItem("Septiembre", "8");
		lb.addItem("Octubre", "9");
		lb.addItem("Noviembre", "10");
		lb.addItem("Diciembre", "11");

		return lb;
	}

	private ListBox createYearListBox() {
		ListBox lb = new ListBox();

		Integer currentYear = new Date().getYear();

		lb.addItem((currentYear + 1900) + "", currentYear.toString());
		lb.addItem((currentYear - 1 + 1900) + "", (currentYear - 1) + "");
		lb.addItem((currentYear - 2 + 1900) + "", (currentYear - 2) + "");

		return lb;
	}
	
	private ListBox createBillingYearListBox() {
		ListBox lb = new ListBox();

		Integer currentYear = new Date().getYear();

		lb.addItem((currentYear + 1 + 1900) + "", (currentYear + 1) + "");
		lb.addItem((currentYear + 1900) + "", currentYear.toString());
		lb.addItem((currentYear - 1 + 1900) + "", (currentYear - 1) + "");
		lb.addItem((currentYear - 2 + 1900) + "", (currentYear - 2) + "");

		return lb;
	}

	private void createCustomerSuggestBox() {
		List<Customer> customers = feeListAll.stream().map(fee -> fee.getCustomer()).collect(Collectors.toList());

		TreeSet<String> customersSuggest = new TreeSet<>();
		customers.forEach(customer -> customersSuggest.add(customer.getName()));

		customerSuggestBox = new SuggestBox();
		customerSuggestBox.setWidth("300px");
		MultiWordSuggestOracle orclSb = (MultiWordSuggestOracle) customerSuggestBox.getSuggestOracle();
		orclSb.addAll(customersSuggest);
		orclSb.setDefaultSuggestionsFromText(customersSuggest);
		customerSuggestBox.setAutoSelectEnabled(false);
		customerSuggestBox.getElement().setPropertyString("placeholder", "Cliente... (Ctrl + espacio para ver sugerencias)");

		customerSuggestBox.getValueBox().addKeyUpHandler(e -> {
			if (e.isControlKeyDown() && e.getNativeKeyCode() == 32) {
				customerSuggestBox.setText("");
				customerSuggestBox.showSuggestionList();
			} else if (e.getNativeKeyCode() == KeyCodes.KEY_ESCAPE)
				customerSuggestBox.hideSuggestionList();
		});

		customerSuggestBox.addSelectionHandler(e -> {
			conceptSuggestBox.setValue("");
			String customerName = customerSuggestBox.getValue();
			feeList.clear();
			feeList.addAll(feeListAll.stream().filter(fee -> AonStringUtils.equalsIgnoreCase(customerName, fee.getCustomer().getName())).collect(Collectors.toList()));
			createFeeTable();
		});
		
		customerSuggestBox.addValueChangeHandler(e -> {
			String customerName = customerSuggestBox.getValue();
			if(AonStringUtils.isBlank(customerName)) {
				conceptSuggestBox.setValue("");
			
				feeList.clear();
				feeList.addAll(feeListAll);
				createFeeTable();
			}
		});
	}

	private void createConceptSuggestBox() {
		List<String> concepts = feeListAll.stream()
				.map(fee -> fee.getItem().getProduct().getName() + " (" + fee.getItem().getProduct().getCode() + ")")
				.collect(Collectors.toList());

		TreeSet<String> conceptsSuggest = new TreeSet<>();
		concepts.forEach(concept -> conceptsSuggest.add(concept));

		conceptSuggestBox = new SuggestBox();
		conceptSuggestBox.setWidth("300px");
		MultiWordSuggestOracle orclSb = (MultiWordSuggestOracle) conceptSuggestBox.getSuggestOracle();
		orclSb.addAll(conceptsSuggest);
		orclSb.setDefaultSuggestionsFromText(conceptsSuggest);
		conceptSuggestBox.setAutoSelectEnabled(false);
		conceptSuggestBox.getElement().setPropertyString("placeholder", "Concepto... (Ctrl + espacio para ver sugerencias)");

		conceptSuggestBox.getValueBox().addKeyUpHandler(e -> {
			if (e.isControlKeyDown() && e.getNativeKeyCode() == 32) {
				conceptSuggestBox.setText("");
				conceptSuggestBox.showSuggestionList();
			} else if (e.getNativeKeyCode() == KeyCodes.KEY_ESCAPE)
				conceptSuggestBox.hideSuggestionList();
		});

		conceptSuggestBox.addSelectionHandler(e -> {
			customerSuggestBox.setValue("");
			String concept = conceptSuggestBox.getValue();
			String productName = concept.split("\\(")[0].trim();
			String productCode = concept.split("\\(")[1].split("\\)")[0].trim();
			feeList.clear();
			feeList.addAll(feeListAll.stream().filter(fee -> AonStringUtils.isNotBlank(fee.getItem().getProduct().getName()) && AonStringUtils.isNotBlank(fee.getItem().getProduct().getCode()) && AonStringUtils.equalsIgnoreCase(productName, fee.getItem().getProduct().getName()) &&  AonStringUtils.equalsIgnoreCase(productCode, fee.getItem().getProduct().getCode())).collect(Collectors.toList()));
			createFeeTable();
		});
		
		conceptSuggestBox.addValueChangeHandler(e -> {
			String concept = conceptSuggestBox.getValue();
			if(AonStringUtils.isBlank(concept)) {
				customerSuggestBox.setValue("");
				
				feeList.clear();
				feeList.addAll(feeListAll);
				createFeeTable();
			}
		});
	}

	private void createFeeTable() {
		scrollPanel.clear();
		if(feeList.isEmpty())
			createEmptyFeeMessage();
		else {
			Grid feeTable = createFeeHeader();
			createFeeBody(feeTable);
			setColumnWidth(feeTable);
		}
	}

	private void createEmptyFeeMessage() {
		HTMLPanel emptyFeePanel = new HTMLPanel("");
		emptyFeePanel.addStyleName(AON.CSS.aonDisplayFlexCenter());
		Label emptyMessage = new Label("NO EXISTEN CUOTAS DE CLIENTES PARA ESTE PERIODO");
		emptyMessage.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		emptyFeePanel.add(emptyMessage);
		scrollPanel.add(emptyFeePanel);
		container.add(scrollPanel);
	}

	private Grid createFeeHeader() {
		Grid feeTable = new Grid(0, 11);
		feeTable.clear();
		feeTable.setWidth("100%");

		int row = feeTable.insertRow(feeTable.getRowCount());

		CheckBox select = new CheckBox();
		select.addValueChangeHandler(e -> {
			selectionModel.forEach((checkBox, fee) -> checkBox.setValue(e.getValue()));
		});

		Label customer = new Label("CLIENTE");
		Label status = new Label("ESTADO");
		Label concept = new Label("CONCEPTO");
		Label period = new Label("PERIODO");
		Label quantity = new Label("CANTIDAD");
		Label price = new Label("PRECIO");
		Label discount = new Label("DESCUENTO");
		Label billingDate = new Label("F. FACTURACI\u00f3nN");
		Label startDate = new Label("F. DESDE");
		startDate.setTitle("F. DESDE FACTURACI\u00f3nN");
		Label endDate = new Label("F. HASTA");
		endDate.setTitle("F. HASTA FACTURACI\u00f3nN");

		select.addStyleName(AON.CSS.aonHeaderTable());
		select.getElement().getStyle().setPaddingLeft(0, Unit.PX);
		customer.addStyleName(AON.CSS.aonHeaderTable());
		status.addStyleName(AON.CSS.aonHeaderTable());
		concept.addStyleName(AON.CSS.aonHeaderTable());
		period.addStyleName(AON.CSS.aonHeaderTable());
		quantity.addStyleName(AON.CSS.aonHeaderTable());
		price.addStyleName(AON.CSS.aonHeaderTable());
		discount.addStyleName(AON.CSS.aonHeaderTable());
		billingDate.addStyleName(AON.CSS.aonHeaderTable());
		startDate.addStyleName(AON.CSS.aonHeaderTable());
		endDate.addStyleName(AON.CSS.aonHeaderTable());

		feeTable.setWidget(row, 0, select);
		feeTable.setWidget(row, 1, customer);
		feeTable.setWidget(row, 2, status);
		feeTable.setWidget(row, 3, concept);
		feeTable.setWidget(row, 4, period);
		feeTable.setWidget(row, 5, quantity);
		feeTable.setWidget(row, 6, price);
		feeTable.setWidget(row, 7, discount);
		feeTable.setWidget(row, 8, billingDate);
		feeTable.setWidget(row, 9, startDate);
		feeTable.setWidget(row, 10, endDate);

		feeTable.getCellFormatter().addStyleName(row, 0, AON.CSS.aonHeaderSticky());
		feeTable.getCellFormatter().addStyleName(row, 1, AON.CSS.aonHeaderSticky());
		feeTable.getCellFormatter().addStyleName(row, 2, AON.CSS.aonHeaderSticky());
		feeTable.getCellFormatter().addStyleName(row, 3, AON.CSS.aonHeaderSticky());
		feeTable.getCellFormatter().addStyleName(row, 4, AON.CSS.aonHeaderSticky());
		feeTable.getCellFormatter().addStyleName(row, 5, AON.CSS.aonHeaderSticky());
		feeTable.getCellFormatter().addStyleName(row, 6, AON.CSS.aonHeaderSticky());
		feeTable.getCellFormatter().addStyleName(row, 7, AON.CSS.aonHeaderSticky());
		feeTable.getCellFormatter().addStyleName(row, 8, AON.CSS.aonHeaderSticky());
		feeTable.getCellFormatter().addStyleName(row, 9, AON.CSS.aonHeaderSticky());
		feeTable.getCellFormatter().addStyleName(row, 10, AON.CSS.aonHeaderSticky());

		scrollPanel.add(feeTable);
		container.add(scrollPanel);
		
		return feeTable;
	}

	private void createFeeBody(Grid feeTable) {
		feeList.forEach(fee -> {
			int row = feeTable.insertRow(feeTable.getRowCount());

			CheckBox select = new CheckBox();
			select.addValueChangeHandler(e -> {});

			Label customerLabel = new Label(fee.getCustomer().getName());

			ListBox statusListBox = createStatusListBox(fee);
			statusListBox.setWidth("90px");
			statusListBox.setHeight("30px");
			setSelectedValueLB(statusListBox, fee.getCustomer().getStatus().getDescription());

			AutoResizeTextArea conceptTextArea = new AutoResizeTextArea(fee);
			conceptTextArea.setWidth("95%");
			conceptTextArea.setValue(fee.getDescription());

			ListBox periodListBox = createPeriodListBox(fee);
			periodListBox.setWidth("100px");
			periodListBox.setHeight("30px");
			setSelectedValueLB(periodListBox, fee.getPeriod().getValue().toString());

			TextBox quantityTextBox = new TextBox();
			quantityTextBox.setWidth("65px");
			quantityTextBox.setHeight("20px");
			quantityTextBox.setValue(null == fee.getQuantity() ? "" : fee.getQuantity().toString());
			quantityTextBox.addValueChangeHandler(e -> {
				fee.setQuantity(Double.parseDouble(e.getValue()));
				setFeeModify(fee);
			});

			TextBox priceTextBox = new TextBox();
			priceTextBox.setWidth("90px");
			priceTextBox.setHeight("20px");
			priceTextBox.setValue(null == fee.getPrice() ? "" : fee.getPrice().toString());
			priceTextBox.addValueChangeHandler(e -> {
				fee.setPrice(Double.parseDouble(e.getValue()));
				setFeeModify(fee);
			});

			TextBox discountTextBox = new TextBox();
			discountTextBox.setWidth("80px");
			discountTextBox.setHeight("20px");
			discountTextBox.setValue(null == fee.getDiscount() ? "" : fee.getDiscount().toString());
			discountTextBox.addValueChangeHandler(e -> {
				String expression = e.getValue();
				String result = e.getValue();
				
				try {
					if(AonStringUtils.contains(expression, ",") && AonStringUtils.contains(expression, "."))
						expression= expression.replaceAll("\\.", "");
					expression= expression.replaceAll(",", ".");
					
					Double expressionValue = evalExpression(expression);
					result = null == expressionValue ? "" : expressionValue.toString();
				} catch (Exception ex) {
					// TODO: handle exception
				}
				
				fee.setDiscount(Double.parseDouble(result));
				setFeeModify(fee);
				
			});
			
			AonDateBox billingDateBox = new AonDateBox();
			billingDateBox.getElement().getStyle().setTextAlign(TextAlign.CENTER);
			billingDateBox.addStyleName("gwt-TextBox");
			billingDateBox.setWidth("65px");
			billingDateBox.setHeight("20px");
			billingDateBox.setValue(fee.getBillingDate());
			billingDateBox.addValueChangeHandler(e -> {
				Date date = e.getValue() == null ? null : DateUtils.getFirstDayOfMonth(e.getValue());
				fee.setBillingDate(date);
				setFeeModify(fee);
			});

			AonDateBox startDateBox = new AonDateBox();
			startDateBox.getElement().getStyle().setTextAlign(TextAlign.CENTER);
			startDateBox.addStyleName("gwt-TextBox");
			startDateBox.setWidth("65px");
			startDateBox.setHeight("20px");
			startDateBox.setValue(fee.getStartDate());
			startDateBox.addValueChangeHandler(e -> {
				fee.setStartDate(e.getValue());
				setFeeModify(fee);
			});

			AonDateBox endDateBox = new AonDateBox();
			endDateBox.getElement().getStyle().setTextAlign(TextAlign.CENTER);
			endDateBox.addStyleName("gwt-TextBox");
			endDateBox.setWidth("65px");
			endDateBox.setHeight("20px");
			endDateBox.setValue(fee.getEndDate());
			endDateBox.addValueChangeHandler(e -> {
				fee.setEndDate(e.getValue());
				setFeeModify(fee);
			});

			checkRowAndModify(feeTable, row, fee, select);
			checkRowAndModify(feeTable, row, fee, customerLabel);
			checkRowAndModify(feeTable, row, fee, statusListBox);
			checkRowAndModify(feeTable, row, fee, conceptTextArea);
			checkRowAndModify(feeTable, row, fee, periodListBox);
			checkRowAndModify(feeTable, row, fee, quantityTextBox);
			checkRowAndModify(feeTable, row, fee, priceTextBox);
			checkRowAndModify(feeTable, row, fee, discountTextBox);
			checkRowAndModify(feeTable, row, fee, billingDateBox);
			checkRowAndModify(feeTable, row, fee, startDateBox);
			checkRowAndModify(feeTable, row, fee, endDateBox);

			feeTable.setWidget(row, 0, select);
			feeTable.setWidget(row, 1, customerLabel);
			feeTable.setWidget(row, 2, statusListBox);
			feeTable.setWidget(row, 3, conceptTextArea);
			feeTable.setWidget(row, 4, periodListBox);
			feeTable.setWidget(row, 5, quantityTextBox);
			feeTable.setWidget(row, 6, priceTextBox);
			feeTable.setWidget(row, 7, discountTextBox);
			feeTable.setWidget(row, 8, billingDateBox);
			feeTable.setWidget(row, 9, startDateBox);
			feeTable.setWidget(row, 10, endDateBox);

			feeTable.getCellFormatter().getElement(row, 2).getStyle().setTextAlign(TextAlign.CENTER);
			feeTable.getCellFormatter().getElement(row, 4).getStyle().setTextAlign(TextAlign.CENTER);
			feeTable.getCellFormatter().getElement(row, 5).getStyle().setTextAlign(TextAlign.CENTER);
			feeTable.getCellFormatter().getElement(row, 6).getStyle().setTextAlign(TextAlign.CENTER);
			feeTable.getCellFormatter().getElement(row, 7).getStyle().setTextAlign(TextAlign.CENTER);
			feeTable.getCellFormatter().getElement(row, 8).getStyle().setTextAlign(TextAlign.CENTER);
			feeTable.getCellFormatter().getElement(row, 9).getStyle().setTextAlign(TextAlign.CENTER);
			feeTable.getCellFormatter().getElement(row, 10).getStyle().setTextAlign(TextAlign.CENTER);

			if (row % 2 == 0) {
				feeTable.getCellFormatter().addStyleName(row, 0, AON.CSS.aonOddTableRow());
				feeTable.getCellFormatter().addStyleName(row, 1, AON.CSS.aonOddTableRow());
				feeTable.getCellFormatter().addStyleName(row, 2, AON.CSS.aonOddTableRow());
				feeTable.getCellFormatter().addStyleName(row, 3, AON.CSS.aonOddTableRow());
				feeTable.getCellFormatter().addStyleName(row, 4, AON.CSS.aonOddTableRow());
				feeTable.getCellFormatter().addStyleName(row, 5, AON.CSS.aonOddTableRow());
				feeTable.getCellFormatter().addStyleName(row, 6, AON.CSS.aonOddTableRow());
				feeTable.getCellFormatter().addStyleName(row, 7, AON.CSS.aonOddTableRow());
				feeTable.getCellFormatter().addStyleName(row, 8, AON.CSS.aonOddTableRow());
				feeTable.getCellFormatter().addStyleName(row, 9, AON.CSS.aonOddTableRow());
				feeTable.getCellFormatter().addStyleName(row, 10, AON.CSS.aonOddTableRow());
			}

			feeTable.getRowFormatter().getElement(row).getStyle().setHeight(25.00, Unit.PX);

			selectionModel.put(select, fee);

		});
	}

	private ListBox createStatusListBox(Fee fee) {
		ListBox lb = new ListBox();
		lb.addItem("Activo", "Activo");
		lb.addItem("Inactivo", "Inactivo");
		lb.addItem("Bloqueado", "Bloqueado");
		lb.addChangeHandler(e -> {
			fee.getCustomer().setStatus(RegistryStatus.safeValueOf(lb.getSelectedValue()));
			setFeeModify(fee);
			
		});
		return lb;
	}

	private ListBox createPeriodListBox(Fee fee) {
		ListBox lb = new ListBox();
		lb.addItem("Sin periodo", "0");
		lb.addItem("Mensual", "1");
		lb.addItem("Bimensual", "2");
		lb.addItem("Trimestral", "3");
		lb.addItem("Cuatrimestral", "3");
		lb.addItem("Semestral", "6");
		lb.addItem("Anual", "12");
		lb.addChangeHandler(e -> {
			fee.setPeriod(BillingPeriod.safeValueOf(lb.getSelectedValue()));
			setFeeModify(fee);
		});
		return lb;
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

	private void checkRowAndModify(Grid grid, int row, Fee fee, Widget widget) {
		if (row % 2 == 0)
			widget.addStyleName(AON.CSS.aonOddTableRow());

		if (fee.isModify()) {
			widget.addStyleName(AON.CSS.aonModifyTableRow());

			grid.getCellFormatter().addStyleName(row, 0, AON.CSS.aonModifyTableRow());
			grid.getCellFormatter().addStyleName(row, 1, AON.CSS.aonModifyTableRow());
			grid.getCellFormatter().addStyleName(row, 2, AON.CSS.aonModifyTableRow());
			grid.getCellFormatter().addStyleName(row, 3, AON.CSS.aonModifyTableRow());
			grid.getCellFormatter().addStyleName(row, 4, AON.CSS.aonModifyTableRow());
			grid.getCellFormatter().addStyleName(row, 5, AON.CSS.aonModifyTableRow());
			grid.getCellFormatter().addStyleName(row, 6, AON.CSS.aonModifyTableRow());
			grid.getCellFormatter().addStyleName(row, 7, AON.CSS.aonModifyTableRow());
			grid.getCellFormatter().addStyleName(row, 8, AON.CSS.aonModifyTableRow());
			grid.getCellFormatter().addStyleName(row, 9, AON.CSS.aonModifyTableRow());
			grid.getCellFormatter().addStyleName(row, 10, AON.CSS.aonModifyTableRow());
		} else {
			widget.removeStyleName(AON.CSS.aonModifyTableRow());

			grid.getCellFormatter().removeStyleName(row, 0, AON.CSS.aonModifyTableRow());
			grid.getCellFormatter().removeStyleName(row, 1, AON.CSS.aonModifyTableRow());
			grid.getCellFormatter().removeStyleName(row, 2, AON.CSS.aonModifyTableRow());
			grid.getCellFormatter().removeStyleName(row, 3, AON.CSS.aonModifyTableRow());
			grid.getCellFormatter().removeStyleName(row, 4, AON.CSS.aonModifyTableRow());
			grid.getCellFormatter().removeStyleName(row, 5, AON.CSS.aonModifyTableRow());
			grid.getCellFormatter().removeStyleName(row, 6, AON.CSS.aonModifyTableRow());
			grid.getCellFormatter().removeStyleName(row, 7, AON.CSS.aonModifyTableRow());
			grid.getCellFormatter().removeStyleName(row, 8, AON.CSS.aonModifyTableRow());
			grid.getCellFormatter().removeStyleName(row, 9, AON.CSS.aonModifyTableRow());
			grid.getCellFormatter().removeStyleName(row, 10, AON.CSS.aonModifyTableRow());
		}

	}

	private void setColumnWidth(Grid feeTable) {
		feeTable.getColumnFormatter().getElement(0).getStyle().setWidth(2, Unit.PCT);
		feeTable.getColumnFormatter().getElement(1).getStyle().setWidth(20, Unit.PCT);
		feeTable.getColumnFormatter().getElement(2).getStyle().setWidth(8, Unit.PCT);
		feeTable.getColumnFormatter().getElement(3).getStyle().setWidth(22, Unit.PCT);
		feeTable.getColumnFormatter().getElement(4).getStyle().setWidth(8, Unit.PCT);
		feeTable.getColumnFormatter().getElement(5).getStyle().setWidth(8, Unit.PCT);
		feeTable.getColumnFormatter().getElement(6).getStyle().setWidth(8, Unit.PCT);
		feeTable.getColumnFormatter().getElement(7).getStyle().setWidth(8, Unit.PCT);
		feeTable.getColumnFormatter().getElement(8).getStyle().setWidth(8, Unit.PCT);
		feeTable.getColumnFormatter().getElement(9).getStyle().setWidth(8, Unit.PCT);
		feeTable.getColumnFormatter().getElement(10).getStyle().setWidth(8, Unit.PCT);
	}

	private void createToolbar() {
		toolbar = new AonToolbar("Panel Facturaci\u00f3n");

		saveButton = new AonToolbarButton(AON.MSG.saveAction(), AON.CSS.aonIconSave());
		saveButton.addClickHandler(e -> {
			AonMessagePanel.showLoading(messagePanel, "Guardando panel facturaci\u00f3n ...");
			setHasChange(false);
			SERVICE.saveCustomerFeeList(options.getDomainName(), options.getDomain(), options.getUser(), feeListAll,
					new AsyncCallback<Void>() {

						@Override
						public void onFailure(Throwable caught) {
							AonMessagePanel.showError(messagePanel, "Error guardando panel de facturaci\u00f3n: " + caught.getMessage());
						}

						@Override
						public void onSuccess(Void result) {
							AonMessagePanel.hideMessage(messagePanel);
							loadModule(options);
						}
					});
		});

		undoAllButton = new AonToolbarButton(AON.MSG.undo() + " todo", AON.CSS.aonIconUndoAll());
		undoAllButton.addClickHandler(e -> {
			AonDialog undoAllDialog = new AonDialog("Restaurar contratos", new HTMLPanel("\u00bfDesea realmente deshacer los cambios sin guardar del panel facturaci\u00f3n\u003f"));
			undoAllDialog.confirm(new AonAcceptDialogCallback() {

				@Override
				public void onCancel() {
					// Not use here
				}

				@Override
				public void onAccept() {
					AonMessagePanel.showLoading(messagePanel, "Deshaciendo cambios panel facturaci\u00f3n ...");
					setHasChange(false);
					loadModule(options);
				}
			});
		});

		toolbar.add(saveButton);
		toolbar.add(undoAllButton);
	}

	// ------------------------------------------ HasChange
	
	private void setFeeModify(Fee fee) {
		fee.setModify(true);
		createFeeTable();
		setHasChange(true);
	}

	private boolean hasChange() {
		return hasChange;
	}

	private void setHasChange(boolean hasChange) {
		this.hasChange = hasChange;
		if (hasChange()) {
			saveButton.getElement().getStyle().clearDisplay();
			undoAllButton.getElement().getStyle().clearDisplay();
		}
		saveButton.setEnabled(hasChange());
		undoAllButton.setEnabled(hasChange());
	}
	
	// ------------------------------------------ Eval Expression
	
	public double evalExpression(String expression) {
		return calculate(expression);
	}

	public final native double calculate(String expression) /*-{
		return eval(expression);
	}-*/;
	
	// ------------------------------------------ AutoResizeTextArea

	public class AutoResizeTextArea extends TextArea implements ValueChangeHandler<String>, KeyUpHandler {

		private final int MIN_HEIGHT = 20;
		private Fee fee;

		public AutoResizeTextArea(Fee fee) {
			super();
			this.fee = fee;
			this.getElement().getStyle().setProperty("resize", "none");
			addKeyUpHandler(this);
			addValueChangeHandler(this);
			Scheduler.get().scheduleDeferred(() -> adjustHeight());
		}

		@Override
		public void setText(String text) {
			super.setText(text);
			Scheduler.get().scheduleDeferred(() -> adjustHeight());
		}

		@Override
		public void setValue(String text) {
			super.setValue(text);
			Scheduler.get().scheduleDeferred(() -> adjustHeight());
		}

		@Override
		public void onKeyUp(KeyUpEvent event) {
			Scheduler.get().scheduleDeferred(() -> adjustHeight());
		}

		@Override
		public void onValueChange(ValueChangeEvent<String> event) {
			Scheduler.get().scheduleDeferred(() -> adjustHeight());
			this.fee.setDescription(event.getValue());
			setFeeModify(this.fee);
		}

		private void adjustHeight() {
			int scrollHeight = getElement().getScrollHeight();
			int offsetHeight = getElement().getOffsetHeight();
			int clientHeight = getElement().getClientHeight();
			int newHeight = Math.max(scrollHeight, MIN_HEIGHT);
			if (newHeight > offsetHeight || newHeight > clientHeight) {
				DOM.setStyleAttribute(getElement(), "height", newHeight + "px");
			}
		}
	}

}
