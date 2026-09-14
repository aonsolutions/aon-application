package com.esferalia.aon.gwt.fiscal.client.registry;

import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.RegistryService;
import com.esferalia.aon.gwt.common.client.RegistryServiceAsync;
import com.esferalia.aon.gwt.common.client.RegistryServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDateBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomIntegerBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomListBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomMonthYearSelect;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomNumberBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomSuggestBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomSuggestOracle;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomTextArea;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomTextBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomToogleButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Workplace;
import com.esferalia.aon.occam.api.model.fee.Fee;
import com.esferalia.aon.occam.api.model.finance.InvoicingGroup;
import com.esferalia.aon.occam.api.model.product.OldItem;
import com.esferalia.aon.occam.api.model.registry.Project;
import com.esferalia.aon.occam.api.model.registry.Seller;
import com.esferalia.aon.occam.api.model.type.BillingPeriod;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

public abstract class CustomerFeeDialog extends AonCustomDialog {

	// ------------------------------------------------- Variables (UI)
	
	private HTMLPanel container;
	private HTMLPanel mainPanel;
	
	private HTMLPanel messagePanel;
	
	private AonCustomSuggestBox customerSB;
	
	private AonCustomIntegerBox lineBox;
	private AonCustomToogleButton confidentialTB;
	
	private AonCustomSuggestBox productSB;
	private AonCustomTextArea productDescriptionTA;
	
	private AonCustomNumberBox quantityNB;
	private AonCustomNumberBox priceNB;
	private AonCustomTextBox discountTB;
	
	private AonCustomDateBox startDateBox;
	private AonCustomDateBox endDateBox;
	private AonCustomTextBox totalAmountTB;
	
	private AonCustomMonthYearSelect billingDateSelect;
	private AonCustomListBox periodLB;
	
	private AonCustomSuggestBox workplaceSB;
	private AonCustomSuggestBox sellerSB;
	private AonCustomSuggestBox invoicingGroupSB;
	private AonCustomSuggestBox projectSB;
	
	private HTMLPanel buttonsPanel;
	
	// ------------------------------------------------- Variables
	
	private static RegistryServiceAsync SERVICE;
	private RegistryModuleOptions options;
	
	private Map<String, Customer> customerSuggestions = new TreeMap<>();
	private Map<String, OldItem> productSuggestions = new TreeMap<>();
	
	private Map<String, Workplace> workplaceSuggestions = new TreeMap<>();
	private Map<String, Seller> sellerSuggestions = new TreeMap<>();
	private Map<String, InvoicingGroup> invoicingGroupSuggestions = new TreeMap<>();
	private Map<String, Project> projectSuggestions = new TreeMap<>();

	private Fee fee;
	private boolean isSameProduct = false;
	private boolean isNewFee = false;
	private OldItem item;
	private Customer customer;
	
	private Integer ritem;
	
	private Integer officeDomain;

	// ------------------------------------------------- Constructor

	protected CustomerFeeDialog(OldItem item, RegistryModuleOptions options, Integer officeDomain) {
		setCaption("Edici\u00f3n Cuota");
		
		this.isSameProduct = null != item;
		this.item = item;
		this.options = options;
		this.officeDomain = officeDomain;
		
		initService();
		initView();
		showDialog();
	}
	
	protected CustomerFeeDialog(Fee fee, RegistryModuleOptions options, Integer officeDomain) {
		setCaption("Edici\u00f3n Cuota");
		
		this.fee = fee;
		this.options = options;
		this.officeDomain = officeDomain;
		
		initService();
		initView();
		showDialog();
	}
	
	protected CustomerFeeDialog(Fee fee, Customer customer, RegistryModuleOptions options, Integer officeDomain) {
		setCaption("Edici\u00f3n Cuota");
		
		this.fee = fee;
		this.options = options;
		this.customer = customer;
		this.officeDomain = officeDomain;
		
		initService();
		initView();
		showDialog();
	}
	
	protected CustomerFeeDialog(RegistryModuleOptions options, Integer officeDomain) {
		setCaption("Nueva Cuota");
		
		this.isNewFee = true;
		this.options = options;
		this.officeDomain = officeDomain;
		
		initService();
		initView();
		showDialog();
	}
	
	protected CustomerFeeDialog(RegistryModuleOptions options, Customer customer, Integer officeDomain) {
		setCaption("Nueva Cuota");
		
		this.isNewFee = true;
		this.customer = customer;
		this.options = options;
		this.officeDomain = officeDomain;
		
		initService();
		initView();
		showDialog();
	}
	
	protected CustomerFeeDialog(RegistryModuleOptions options, OldItem item, Customer customer, Integer officeDomain) {
		setCaption("Nueva Cuota");
		
		this.isNewFee = true;
		this.item = item;
		this.customer = customer;
		this.options = options;
		this.officeDomain = officeDomain;
		
		initService();
		initView();
		showDialog();
	}
	
	public CustomerFeeDialog(RegistryModuleOptions options, OldItem item, Customer customer, Integer ritem, Integer officeDomain) {
		setCaption("Nueva Cuota");
		
		this.isNewFee = true;
		this.item = item;
		this.customer = customer;
		this.options = options;
		this.ritem = ritem;
		this.officeDomain = officeDomain;
		
		initService();
		initView();
		showDialog();
	}

	private void initService() {
		RegistryServiceAsync registryServiceRaw = GWT.create(RegistryService.class);
		SERVICE = new RegistryServiceAsyncDecorator(registryServiceRaw);
	}

	// ------------------------------------------------- Auxiliar Methods

	private void initView() {
		mainPanel = new HTMLPanel("");
		mainPanel.addStyleName(AON.CSS.aonFlexColumn2());
		mainPanel.getElement().getStyle().setProperty("margin-top", ".5rem");
		
		messagePanel = new HTMLPanel("");
		mainPanel.add(messagePanel);
		
		container = new HTMLPanel("");
		container.addStyleName(AON.CSS.aonFlexColumn2());
		container.getElement().getStyle().setProperty("margin", "0 1rem 1rem");
		container.getElement().getStyle().setProperty("gap", ".75rem");
		container.getElement().getStyle().setProperty("min-width", "44rem");
		
		if(this.isNewFee)
			container.add(createCustomerPanel());
		
		if(null != this.fee || this.isNewFee)
			container.add(createLineConfidentialPanel());
		
		if(null != this.fee || this.isSameProduct || this.isNewFee)
			container.add(createProductPanel());
		
		container.add(createQuantityPricePanel());
		container.add(createDatesPanel());
		container.add(createPeriodicityPanel());
		
		if(null != this.fee || this.isNewFee) {
			container.add(createWorkplacePanel());
			container.add(createSellerPanel());
			container.add(createInvoicingGroupPanel());
			container.add(createProjectPanel());
		}
		
		container.add(createButtonsPanel());
		
		mainPanel.add(container);
		
		this.setWidget(mainPanel);
	}
	
	private HTMLPanel createRow() {
		HTMLPanel row = new HTMLPanel("");
		row.addStyleName(AON.CSS.aonItemFlex());
		row.setWidth("100%");
		return row;
	}
	
	// ------------------------------------------------- Cliente
	
	private Widget createCustomerPanel() {
		customerSB = new AonCustomSuggestBox("Cliente", new AonCustomSuggestOracle());
		customerSB.setAutoSelectEnabled(false);
		customerSB.setLimit(50);
		customerSB.setMinWidth("25rem");
		customerSB.setPlaceHolder("Busque por nombre, nif o alias (Ctrl + espacio para sugerencias)");
		
		customerSB.addSelectionHandler(e -> {
			customerSB.hideSuggestionList();
			customerSB.removeError();
			
			if(null != projectSB) projectSB.setValue("");
			if(null != this.fee) this.fee.setProject(null);
		});
		
		customerSB.addRemoteSuggestionsHandler(3, this::getCustomersSuggestion);
		
		if(null != this.customer) {
			customerSB.setEnable(false);
			customerSB.setValue(this.customer.getName() + " ( " + this.customer.getDocument() + " )" + (AonStringUtils.isBlank(this.customer.getAlias()) ? "" : " - " + this.customer.getAlias()) );
		}
		
		HTMLPanel row = createRow();
		row.add(customerSB);
		return row;
	}

	private void getCustomersSuggestion(String customerQuery) {
		SERVICE.getCustomersSuggestion(options.getDomainName(), options.getDomain(), options.getUser(), officeDomain, customerQuery, new AsyncCallback<Map<String, Customer>>() {
			
			@Override
			public void onSuccess(Map<String, Customer> customerSuggestionsDB) {
				customerSuggestions = customerSuggestionsDB;
				
				customerSB.setSuggestions(customerSuggestions.keySet());
				customerSB.showSuggestionList();
			}
			
			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub	
			}
			
		});
	}

	// ------------------------------------------------- Linea / Confidencial

	private Widget createLineConfidentialPanel() {
		lineBox = new AonCustomIntegerBox("Linea");
		lineBox.hideNearBy();
		lineBox.setMaxWidth("10rem");
		lineBox.addValueChangeHandler(e -> { if(null != fee) setFeeLine(lineBox.getValue()); });
		if(null != fee && null != fee.getLine()) lineBox.setValue(fee.getLine().intValue());
		
		confidentialTB = new AonCustomToogleButton("Confidencial");
		confidentialTB.getElement().getStyle().setProperty("max-width", "10rem");
		confidentialTB.setValue(null != fee && null != fee.getSecurityLevel() && fee.getSecurityLevel() == SecurityLevel.CONFIDENTIAL);
		confidentialTB.addValueChangeHandler(e -> {
			if(null != fee) fee.setSecurityLevel(SecurityLevel.safeValueOf(Boolean.TRUE.equals(e.getValue()) ? 1 : 0));
		});
		
		HTMLPanel row = createRow();
		row.add(lineBox);
		row.add(confidentialTB);
		return row;
	}
	
	private void setFeeLine(Integer lineValue) {
		if(null == lineValue) fee.setLine((Short) null);
		else fee.setLine(lineValue.doubleValue());
	}

	// ------------------------------------------------- Producto

	private Widget createProductPanel() {
		HTMLPanel productPanel = new HTMLPanel("");
		productPanel.addStyleName(AON.CSS.aonFlexColumn2());
		productPanel.setWidth("100%");
		
		productSB = new AonCustomSuggestBox("Producto", new AonCustomSuggestOracle());
		productSB.setAutoSelectEnabled(false);
		productSB.setLimit(50);
		productSB.setMinWidth("25rem");
		productSB.setPlaceHolder("Busque por c\u00f3digo o descripci\u00f3n (Ctrl + espacio para sugerencias)");
		
		productDescriptionTA = new AonCustomTextArea("Descripci\u00f3n");
		productDescriptionTA.setAutoResize(true, this::center);
		productDescriptionTA.setVisible(false);
		productDescriptionTA.addValueChangeHandler(e -> {
			if(null != fee) fee.setDescription(productDescriptionTA.getValue());
		});
		
		productSB.addSelectionHandler(e -> {
			productSB.hideSuggestionList();
			productSB.removeError();
			
			item = productSuggestions.get(productSB.getValue());
			if(null == item) return;
			
			productSB.setEnable(false);
			if(AonStringUtils.isBlank(productDescriptionTA.getValue()))
				productDescriptionTA.setValue(item.getProduct().getName());
			productDescriptionTA.setVisible(true);
			
			if(null != fee) {
				fee.setDescription(item.getProduct().getName());
				fee.setItem(item);
			}
		});
		
		productSB.addRemoteSuggestionsHandler(3, this::getProductsSuggestion);
		
		AonTableButton resetBtn = new AonTableButton("Borrar producto", AON.CSS.aonIconClear());
		resetBtn.addClickHandler(e -> {
			productDescriptionTA.setValue(null == fee ? "" : fee.getDescription());
			productDescriptionTA.setVisible(false);
			productSB.setEnable(true);
			productSB.setValue("");
			
			item = null;
			
			if(null != fee) {
				fee.setDescription(AonStringUtils.EMPTY);
				fee.setItem(null);
				fee.setQuantity(1.00);
				fee.setPrice(0.00);
				fee.setDiscountExpr("0.00");
			}
			
			if(null != quantityNB) quantityNB.setValue(1.00);
			priceNB.setValue(0.00);
			discountTB.setValue("0.00");
			totalAmountTB.setValue("0.00");
		});
		productSB.addButton(resetBtn);
		
		if(null != fee) productSB.setValue(fee.getItem().getProduct().getName() + " ( " + fee.getItem().getProduct().getCode() + " )");
		else if(this.isSameProduct || null != this.item) productSB.setValue(this.item.getProduct().getName() + " ( " + this.item.getProduct().getCode() + " )");
		
		if(null != fee || this.isSameProduct || null != this.item) {
			productSB.setEnable(false);
			productDescriptionTA.setValue(null != fee ? fee.getDescription() : "");
			productDescriptionTA.setVisible(true);
		}
		
		HTMLPanel row = createRow();
		row.add(productSB);
		
		productPanel.add(row);
		productPanel.add(productDescriptionTA);
		
		return productPanel;
	}

	private void getProductsSuggestion(String productQuery) {
		SERVICE.getProductsSuggestion(options.getDomainName(), options.getDomain(), options.getUser(), officeDomain, productQuery, new AsyncCallback<Map<String, OldItem>>() {
			
			@Override
			public void onSuccess(Map<String, OldItem> productSuggestionsDB) {
				productSuggestions = productSuggestionsDB;
				
				productSB.setSuggestions(productSuggestions.keySet());
				productSB.showSuggestionList();
			}
			
			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub	
			}
			
		});
	}

	// ------------------------------------------------- Cantidad / Precio / Dto.

	private Widget createQuantityPricePanel() {
		HTMLPanel row = createRow();
		
		if(null != this.fee || this.isNewFee) {
			quantityNB = new AonCustomNumberBox("Cantidad");
			quantityNB.hideNearBy();
			quantityNB.setMaxWidth("12rem");
			quantityNB.addValueChangeHandler(e -> {
				if(null != fee) fee.setQuantity(null == quantityNB.getValue() ? 1.00 : quantityNB.getValue());
				recalculateTotalAmount();
			});
			
			if(null != fee && null != fee.getQuantity()) quantityNB.setValue(fee.getQuantity());
			else if(this.isNewFee) quantityNB.setValue(1.00);
			
			row.add(quantityNB);
		}
		
		priceNB = new AonCustomNumberBox("Precio", 2);
		priceNB.hideNearBy();
		priceNB.setMaxWidth("12rem");
		priceNB.addValueChangeHandler(e -> {
			if(null != fee) fee.setPrice(null == priceNB.getValue() ? 0.00 : priceNB.getValue());
			recalculateTotalAmount();
		});
		if(null != fee && null != fee.getPrice()) priceNB.setValue(fee.getPrice());
		
		discountTB = new AonCustomTextBox("Dto.");
		discountTB.setMaxWidth("12rem");
		discountTB.addValueChangeHandler(e -> {
			String expression = discountTB.getValue();
			expression = expression.replaceAll(",", ".");
			try {
				evalExpression(expression);
				if(null != fee) fee.setDiscountExpr(expression);
				discountTB.setValue(expression);
				discountTB.removeError();
				recalculateTotalAmount();
			} catch (Exception exception) {
				discountTB.addError();
				AonMessagePanel.showError(messagePanel, "Descuento con formato incorrecto");
			}
		});
		
		if(null != fee) {
			String expression = fee.getDiscountExpr();
			if(AonStringUtils.contains(expression, ",")) AonMessagePanel.showError(messagePanel, "Se debe usar '.' para decimales");
			try {
				evalExpression(expression);
			} catch (Exception exception) {
				AonMessagePanel.showError(messagePanel, "Descuento con formato incorrecto");
			}
			
			discountTB.setValue(fee.getDiscountExpr());
		}
		
		row.add(priceNB);
		row.add(discountTB);
		
		return row;
	}
	
	public double evalExpression(String expression) {
		return calculate(expression);
	}

	public final native double calculate(String expression) /*-{
		return eval(expression);
	}-*/;

	// ------------------------------------------------- Fechas / Importe

	private Widget createDatesPanel() {
		startDateBox = new AonCustomDateBox("F. Inicio");
		startDateBox.getElement().getStyle().setProperty("max-width", "14rem");
		startDateBox.addValueChangeHandler(e -> {
			startDateBox.removeError();
			if(null != fee) fee.setStartDate(e.getValue());
		});
		if(null != fee) startDateBox.setValue(fee.getStartDate());
		
		endDateBox = new AonCustomDateBox("F. Fin");
		endDateBox.getElement().getStyle().setProperty("max-width", "14rem");
		endDateBox.addValueChangeHandler(e -> { if(null != fee) fee.setEndDate(e.getValue()); });
		if(null != fee) endDateBox.setValue(fee.getEndDate());
		
		totalAmountTB = new AonCustomTextBox("Importe");
		totalAmountTB.setMaxWidth("14rem");
		totalAmountTB.setEnable(false);
		if(null != fee && null != fee.getPrice()) recalculateTotalAmount();
		
		HTMLPanel row = createRow();
		row.add(startDateBox);
		row.add(endDateBox);
		row.add(totalAmountTB);
		
		return row;
	}
	
	private void recalculateTotalAmount() {
		double quantity = (null == quantityNB || null == quantityNB.getValue()) ? 1.00 : quantityNB.getValue();
		double price = (null == priceNB || null == priceNB.getValue()) ? 0.00 : priceNB.getValue();
		
		double dto = 0.00;
		try {
			if(AonStringUtils.isNotBlank(discountTB.getValue()))
				dto = evalExpression(discountTB.getValue().replaceAll(",", "."));
		} catch (Exception e) {
			dto = 0.00;
		}
		
		totalAmountTB.setValue(String.valueOf(getTotalNetPrice(quantity, price, dto)));
	}
	
	private double getTotalNetPrice(double quantity, double price, double dto) {
		return roundTwoDecimals( getNetCost(price, dto) * quantity );
	}

	private double getNetCost(double price, double dto) {
		return price * (1 - dto/100);
	}
	
	private double roundTwoDecimals(double value) {
	    return Math.round(value * 100.0) / 100.0;
	}

	// ------------------------------------------------- Periodicidad

	private Widget createPeriodicityPanel() {
		billingDateSelect = new AonCustomMonthYearSelect("F. Facturaci\u00f3n");
		billingDateSelect.setMaxWidth("18rem");
		billingDateSelect.addValueChangeHandler(e -> {
			billingDateSelect.removeError();
			if(null != fee) fee.setBillingDate(e.getValue());
		});
		if(null != fee) billingDateSelect.setValue(fee.getBillingDate());
		
		HTMLPanel row = createRow();
		row.add(billingDateSelect);
		
		if(null != this.fee || this.isNewFee) {
			periodLB = new AonCustomListBox("Periodo");
			periodLB.setMaxWidth("14rem");
			periodLB.addItem("Sin periodo", "0");
			periodLB.addItem("Mensual", "1");
			periodLB.addItem("Bimensual", "2");
			periodLB.addItem("Trimestral", "3");
			periodLB.addItem("Cuatrimestral", "4");
			periodLB.addItem("Semestral", "5");
			periodLB.addItem("Anual", "6");
			periodLB.addChangeHandler(e -> {
				if(null != fee) fee.setPeriod(BillingPeriod.safeValueOf(periodLB.getValue()));
			});
			if(null != fee && null != fee.getPeriod()) periodLB.setValue(fee.getPeriod().getValue().toString());
			
			row.add(periodLB);
		}
		
		return row;
	}

	// ------------------------------------------------- Centro de trabajo

	private Widget createWorkplacePanel() {
		workplaceSB = new AonCustomSuggestBox("C. Trabajo", new AonCustomSuggestOracle());
		workplaceSB.setAutoSelectEnabled(false);
		workplaceSB.setLimit(50);
		workplaceSB.setMinWidth("25rem");
		workplaceSB.setPlaceHolder("Busque por descripci\u00f3n (Ctrl + espacio para sugerencias)");
		
		workplaceSB.addSelectionHandler(e -> {
			workplaceSB.hideSuggestionList();
			workplaceSB.removeError();
			if(null != fee) fee.setWorkplace(workplaceSuggestions.get(workplaceSB.getValue()));
		});
		
		workplaceSB.addValueChangeHandler(e -> {
			if(null != fee && AonStringUtils.isBlank(workplaceSB.getValue())) {
				workplaceSB.addError();
				AonMessagePanel.showError(messagePanel, "El campo centro de trabajo es obligatorio");
			}
		});
		
		workplaceSB.addRemoteSuggestionsHandler(3, this::getWorkplacesSuggestion);
		
		if(null != fee && null != fee.getWorkplace()) workplaceSB.setValue(fee.getWorkplace().getDescription());
		
		HTMLPanel row = createRow();
		row.add(workplaceSB);
		return row;
	}

	private void getWorkplacesSuggestion(String workplaceQuery) {
		SERVICE.getWorkplacesSuggestion(options.getDomainName(), options.getDomain(), options.getUser(), officeDomain, workplaceQuery, new AsyncCallback<Map<String, Workplace>>() {
			
			@Override
			public void onSuccess(Map<String, Workplace> workplaceSuggestionsDB) {
				workplaceSuggestions = workplaceSuggestionsDB;
				
				workplaceSB.setSuggestions(workplaceSuggestions.keySet());
				workplaceSB.showSuggestionList();
			}
			
			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub	
			}
			
		});
	}

	// ------------------------------------------------- Comercial

	private Widget createSellerPanel() {
		sellerSB = new AonCustomSuggestBox("Comercial", new AonCustomSuggestOracle());
		sellerSB.setAutoSelectEnabled(false);
		sellerSB.setLimit(50);
		sellerSB.setMinWidth("25rem");
		sellerSB.setPlaceHolder("Busque por nombre (Ctrl + espacio para sugerencias)");
		
		sellerSB.addSelectionHandler(e -> {
			sellerSB.hideSuggestionList();
			if(null != fee) fee.setSeller(sellerSuggestions.get(sellerSB.getValue()));
		});
		
		sellerSB.addValueChangeHandler(e -> {
			if(null != fee && AonStringUtils.isBlank(sellerSB.getValue())) fee.setSeller(null);
		});
		
		sellerSB.addRemoteSuggestionsHandler(3, this::getSellersSuggestion);
		
		if(null != fee && null != fee.getSellerComercial()) sellerSB.setValue(fee.getSellerComercial().getName());
		
		HTMLPanel row = createRow();
		row.add(sellerSB);
		return row;
	}

	private void getSellersSuggestion(String sellerQuery) {
		getSellersSuggestion(sellerQuery, null);
	}
	
	private void getSellersSuggestion(String sellerQuery, Consumer<Void> end) {
		SERVICE.getSellersSuggestion(options.getDomainName(), options.getDomain(), options.getUser(), officeDomain, sellerQuery, new AsyncCallback<Map<String, Seller>>() {
			
			@Override
			public void onSuccess(Map<String, Seller> sellerSuggestionsDB) {
				sellerSuggestions = sellerSuggestionsDB;
				
				sellerSB.setSuggestions(sellerSuggestions.keySet());
				sellerSB.showSuggestionList();
				
				if(null != end) end.accept(null);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub	
			}
			
		});
	}

	// ------------------------------------------------- Grupo de facturaci\u00f3n

	private Widget createInvoicingGroupPanel() {
		invoicingGroupSB = new AonCustomSuggestBox("G. Facturaci\u00f3n", new AonCustomSuggestOracle());
		invoicingGroupSB.setAutoSelectEnabled(false);
		invoicingGroupSB.setLimit(50);
		invoicingGroupSB.setMinWidth("25rem");
		invoicingGroupSB.setPlaceHolder("Busque por descripci\u00f3n (Ctrl + espacio para sugerencias)");
		
		invoicingGroupSB.addSelectionHandler(e -> {
			invoicingGroupSB.hideSuggestionList();
			if(null != fee) fee.setInvoicingGroup(invoicingGroupSuggestions.get(invoicingGroupSB.getValue()));
		});
		
		invoicingGroupSB.addValueChangeHandler(e -> {
			if(null != fee && AonStringUtils.isBlank(invoicingGroupSB.getValue())) fee.setInvoicingGroup(null);
		});
		
		invoicingGroupSB.addRemoteSuggestionsHandler(3, this::getInvoicingGroupsSuggestion);
		
		if(null != fee && null != fee.getInvoicingGroup()) invoicingGroupSB.setValue(fee.getInvoicingGroup().getDescription());
		
		HTMLPanel row = createRow();
		row.add(invoicingGroupSB);
		return row;
	}
	
	private void getInvoicingGroupsSuggestion(String invoicingGroupQuery) {
		SERVICE.getInvoicingGroupsSuggestion(options.getDomainName(), options.getDomain(), options.getUser(), officeDomain, invoicingGroupQuery, new AsyncCallback<Map<String, InvoicingGroup>>() {
			
			@Override
			public void onSuccess(Map<String, InvoicingGroup> invoicingGroupSuggestionsDB) {
				invoicingGroupSuggestions = invoicingGroupSuggestionsDB;
				
				invoicingGroupSB.setSuggestions(invoicingGroupSuggestions.keySet());
				invoicingGroupSB.showSuggestionList();
			}
			
			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub	
			}
			
		});
	}

	// ------------------------------------------------- Expediente

	private Widget createProjectPanel() {
		projectSB = new AonCustomSuggestBox("Expediente", new AonCustomSuggestOracle());
		projectSB.setAutoSelectEnabled(false);
		projectSB.setLimit(50);
		projectSB.setMinWidth("25rem");
		projectSB.setPlaceHolder("Busque por descripci\u00f3n (Ctrl + espacio para sugerencias)");
		
		projectSB.addSelectionHandler(e -> {
			projectSB.hideSuggestionList();
			Project project = projectSuggestions.get(projectSB.getValue());
			if(null != fee) fee.setProject(project);
			
			// Set seller to fee
			if(null != project && null != project.getProjectHolder() 
					&& null != project.getProjectHolder().getTaskHolder() 
					&& (null != project.getProjectHolder().getTaskHolder().getId() ||  !project.getProjectHolders().isEmpty() )) {
				
				Integer taskHolderId = null;
				if(null != project.getProjectHolder().getTaskHolder().getId()) taskHolderId = project.getProjectHolder().getTaskHolder().getId();
				else taskHolderId = project.getProjectHolders().stream().sorted((o1, o2) -> o2.getStartDate().compareTo(o1.getStartDate())).findFirst().get().getTaskHolder().getId();
					
				SERVICE.getSellerByTaskHolder(options.getDomainName(), options.getDomain(), options.getUser(), taskHolderId, officeDomain, new AsyncCallback<Seller>() {

					@Override
					public void onFailure(Throwable arg0) {}

					@Override
					public void onSuccess(Seller seller) {
						if(null == seller) return;
						
						if(null != fee) fee.setSeller(seller);
						
						getSellersSuggestion(null, end -> {
							sellerSB.setValue(seller.getName());
							sellerSB.hideSuggestionList();
						});
					}
					
				});	
			}
		});
		
		projectSB.addValueChangeHandler(e -> {
			if(null != fee && AonStringUtils.isBlank(projectSB.getValue())) fee.setProject(null);
		});
		
		projectSB.addRemoteSuggestionsHandler(3, query -> getProjectsSuggestion(resolveCustomerId(), query));
		
		if(null != fee && null != fee.getProject()) projectSB.setValue(fee.getProject().getName());
		
		HTMLPanel row = createRow();
		row.add(projectSB);
		return row;
	}
	
	private Integer resolveCustomerId() {
		if (null != customer) return customer.getId();

		if (null != customerSB && AonStringUtils.isNotBlank(customerSB.getValue())) {
			Customer selectedCustomer = customerSuggestions.get(customerSB.getValue());
			if (null != selectedCustomer) return selectedCustomer.getId();
		}

		return (null != fee && null != fee.getCustomer()) ? fee.getCustomer().getId() : null;
	}
	
	private void getProjectsSuggestion(Integer customerId, String projectQuery) {
		SERVICE.getProjectsSuggestion(options.getDomainName(), options.getDomain(), options.getUser(), customerId, officeDomain, projectQuery, new AsyncCallback<Map<String, Project>>() {
			
			@Override
			public void onSuccess(Map<String, Project> projectProjectSuggestionsDB) {
				projectSuggestions = projectProjectSuggestionsDB;
				
				projectSB.setSuggestions(projectSuggestions.keySet());
				projectSB.showSuggestionList();
			}
			
			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub	
			}
			
		});
	}

	private void showDialog() {
		// Show center
		Scheduler.get().scheduleDeferred(() -> {
			center();
			show();
		});
	}

	// ------------------------------------------------- ButtonsPanel

	private Widget createButtonsPanel() {
		buttonsPanel = new HTMLPanel("");
		buttonsPanel.addStyleName(AON.CSS.aonDisplayFlexEnd());
		
		Button closeBtnDialog = new Button();
		closeBtnDialog.setStyleName(AON.CSS.aonCancelButtonSmall());
		closeBtnDialog.setText(AON.MSG.cancelAction());
		closeBtnDialog.getElement().getStyle().setMarginRight(10, Unit.PX);
		closeBtnDialog.addClickHandler(e -> hide());

		buttonsPanel.add(closeBtnDialog);
		
		Button acceptBtnDialog = new Button();
		acceptBtnDialog.setStyleName(AON.CSS.aonOkButtonSmall());
		acceptBtnDialog.setText(AON.MSG.saveAction());
		
		if(this.isNewFee) {
			acceptBtnDialog.addClickHandler(e -> createNewFee());
		} else if(null != this.fee) {
			acceptBtnDialog.addClickHandler(e -> {
				hide();
				onAccept(fee);
			});
		} else {
			acceptBtnDialog.addClickHandler(e -> {
				hide();
				acceptDialog();
			});
		}

		buttonsPanel.add(acceptBtnDialog);
		
		return buttonsPanel;
	}

	private void createNewFee() {
		cleanErrorStyles();
		
		Fee newFee = new Fee();
		newFee.setDomain(new Domain().setId(officeDomain));
		
		if(AonStringUtils.isNotBlank(projectSB.getValue())) newFee.setProject(projectSuggestions.get(projectSB.getValue()));
		
		if(null != this.customer) newFee.setCustomer(this.customer);
		else if(AonStringUtils.isNotBlank(customerSB.getValue())) newFee.setCustomer(customerSuggestions.get(customerSB.getValue()));
		
		if(AonStringUtils.isBlank(customerSB.getValue())) {
			customerSB.addError();
			AonMessagePanel.showError(messagePanel, "El campo cliente es obligatorio");
			return;
		}
		
		setNewFeeLine(newFee);
		
		newFee.setItem(item);
		
		if(item == null) {
			productSB.addError();
			AonMessagePanel.showError(messagePanel, "El campo producto es obligatorio");
			return;
		}
		
		newFee.setDescription(newFee.getItem().getProduct().getName());
		
		newFee.setQuantity(null == quantityNB.getValue() ? 1.00 : quantityNB.getValue());
		newFee.setPrice(null == priceNB.getValue() ? 0.00 : priceNB.getValue());
		newFee.setDiscountExpr(discountTB.getValue());
		
		newFee.setStartDate(startDateBox.getValue());
		newFee.setEndDate(endDateBox.getValue());
		newFee.setBillingDate(billingDateSelect.getValue());
		
		if(newFee.getStartDate() == null || newFee.getBillingDate() == null) {
			if(null == newFee.getStartDate()) startDateBox.addError();
			if(null == newFee.getBillingDate()) billingDateSelect.addError();
			AonMessagePanel.showError(messagePanel, "Las fechas desde y facturaci\u00f3n son obligatorias");
			return;
		}
		
		if(null != newFee.getEndDate() && newFee.getEndDate().before(newFee.getStartDate())) {
			endDateBox.addError();
			AonMessagePanel.showError(messagePanel, "La fecha de inicio no puede ser posterior a la fecha de fin");
			return;
		}
		
		newFee.setPeriod(BillingPeriod.safeValueOf(periodLB.getValue()));
		newFee.setSecurityLevel(SecurityLevel.safeValueOf(Boolean.TRUE.equals(confidentialTB.getValue()) ? 1 : 0));
		
		if(AonStringUtils.isNotBlank(invoicingGroupSB.getValue())) newFee.setInvoicingGroup(invoicingGroupSuggestions.get(invoicingGroupSB.getValue()));
		if(AonStringUtils.isNotBlank(sellerSB.getValue())) newFee.setSeller(sellerSuggestions.get(sellerSB.getValue()));
		if(AonStringUtils.isNotBlank(workplaceSB.getValue())) newFee.setWorkplace(workplaceSuggestions.get(workplaceSB.getValue()));
		
		if(AonStringUtils.isBlank(workplaceSB.getValue())) {
			workplaceSB.addError();
			AonMessagePanel.showError(messagePanel, "El campo centro de trabajo es obligatorio");
			return;
		}
		
		hide();
		
		if(null == ritem)
			onCreate(newFee);
		else
			onCreate(newFee, ritem);
		
	}
	
	private void setNewFeeLine(Fee newFee) {
		Integer lineValue = lineBox.getValue();
		if(null == lineValue) newFee.setLine((Short) null);
		else newFee.setLine(lineValue.doubleValue());
	}

	private void acceptDialog() {
		onAccept(
			Optional.ofNullable(item),
			Optional.ofNullable(priceNB.getValue()),
			Optional.ofNullable(AonStringUtils.isBlank(discountTB.getValue()) ? null : discountTB.getValue()),
			Optional.ofNullable(startDateBox.getValue()),
			Optional.ofNullable(endDateBox.getValue()),
			Optional.ofNullable(billingDateSelect.getValue())
		);
	}
	
	// ------------------------------------------------- Errores

	private void cleanErrorStyles() {
		if(null != customerSB) customerSB.removeError();
		if(null != productSB) productSB.removeError();
		if(null != discountTB) discountTB.removeError();
		if(null != startDateBox) startDateBox.removeError();
		if(null != endDateBox) endDateBox.removeError();
		if(null != billingDateSelect) billingDateSelect.removeError();
		if(null != workplaceSB) workplaceSB.removeError();
	}

	// ------------------------------------------------- Abstract Methods

	protected abstract void onAccept(Fee fee);
	protected abstract void onAccept(Optional<OldItem> item, Optional<Double> price, Optional<String> discountExpr, Optional<Date> startDate, Optional<Date> endDate, Optional<Date> billingDate);
	protected abstract void onCreate(Fee fee);
	protected abstract void onCreate(Fee fee, Integer ritem);

}