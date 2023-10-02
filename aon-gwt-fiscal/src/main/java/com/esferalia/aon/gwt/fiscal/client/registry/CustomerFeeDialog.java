package com.esferalia.aon.gwt.fiscal.client.registry;

import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.RegistryService;
import com.esferalia.aon.gwt.common.client.RegistryServiceAsync;
import com.esferalia.aon.gwt.common.client.RegistryServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDateBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarSmallButton;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.Workplace;
import com.esferalia.aon.occam.api.model.fee.Fee;
import com.esferalia.aon.occam.api.model.finance.InvoicingGroup;
import com.esferalia.aon.occam.api.model.product.OldItem;
import com.esferalia.aon.occam.api.model.registry.Project;
import com.esferalia.aon.occam.api.model.registry.RegistrySeller;
import com.esferalia.aon.occam.api.model.registry.Seller;
import com.esferalia.aon.occam.api.model.type.BillingPeriod;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.ValueBoxBase.TextAlignment;

public abstract class CustomerFeeDialog extends AonCustomDialog {

	// ------------------------------------------------- Variables (UI)
	
	private HTMLPanel container;
	private HTMLPanel mainPanel;
	
	private HTMLPanel messagePanel;
	
	private HTMLPanel customerPanel;
	private SuggestBox customerSuggestBox;
	
	private HTMLPanel lineConfidentialPanel;
	private TextBox lineTextBox;
	private Button confidentialButton;
	
	private HTMLPanel productPanel;
	private SuggestBox productSuggestBox;
	private TextBox productTextBox;
	
	private HTMLPanel quantityPricePanel;
	private TextBox quantityTextBox;
	private TextBox priceTextBox;
	private TextBox discountTextBox;
	
	private HTMLPanel datesPanel;
	private AonDateBox startDateBox;
	private AonDateBox endDateBox;
	
	private HTMLPanel periodicityPanel;
	private ListBox monthListBox;
	private TextBox yearTextBox;
	private Label periodLabel;
	private ListBox periodListBox;
	
	private HTMLPanel workplacePanel;
	private SuggestBox workplaceSuggestBox;
	
	private HTMLPanel sellerPanel;
	private SuggestBox sellerSuggestBox;
	
	private HTMLPanel supportPanel;
	private SuggestBox supportSuggestBox;
	
	private HTMLPanel invoicingGroupPanel;
	private SuggestBox invoicingGroupSuggestBox;
	
	private HTMLPanel projectPanel;
	private SuggestBox projectSuggestBox;
	
	private HTMLPanel buttonsPanel;
	
	// ------------------------------------------------- Variables
	
	private static RegistryServiceAsync SERVICE;
	private RegistryModuleOptions options;
	
	private Map<String, Customer> customerSuggestions = new TreeMap<>();
	private Map<String, OldItem> productSuggestions = new TreeMap<>();
	
	private Map<String, Workplace> workplaceSuggestions = new TreeMap<>();
	private Map<String, Seller> sellerSuggestions = new TreeMap<>();
	private Map<String, RegistrySeller> supportSuggestions = new TreeMap<>();

	private Map<String, InvoicingGroup> invoicingGroupSuggestions = new TreeMap<>();
	private Map<String, Project> projectSuggestions = new TreeMap<>();

	private Fee fee;
	private boolean isSameProduct = false;
	private boolean isNewFee = false;
	private OldItem item; 
	private Customer customer;

	// ------------------------------------------------- Constructor

	protected CustomerFeeDialog(OldItem item, RegistryModuleOptions options) {
		setCaption("Editor Cuotas");
		
		this.isSameProduct = null != item;
		this.item = item;
		this.options = options;
		
		RegistryServiceAsync registryServiceRaw = GWT.create(RegistryService.class);
		SERVICE = new RegistryServiceAsyncDecorator(registryServiceRaw);
		
		initView();
		showDialog();
	}
	
	protected CustomerFeeDialog(Fee fee, RegistryModuleOptions options) {
		setCaption("Editor Cuota");
		
		this.fee = fee;
		this.options = options;
		
		RegistryServiceAsync registryServiceRaw = GWT.create(RegistryService.class);
		SERVICE = new RegistryServiceAsyncDecorator(registryServiceRaw);
		
		initView();
		showDialog();
	}
	
	protected CustomerFeeDialog(RegistryModuleOptions options) {
		setCaption("Creador Cuota");
		
		this.isNewFee = true;
		this.options = options;
		
		RegistryServiceAsync registryServiceRaw = GWT.create(RegistryService.class);
		SERVICE = new RegistryServiceAsyncDecorator(registryServiceRaw);
		
		initView();
		showDialog();
	}
	
	protected CustomerFeeDialog(RegistryModuleOptions options, OldItem item, Customer customer) {
		setCaption("Creador Cuota");
		
		this.isNewFee = true;
		this.item = item;
		this.customer = customer;
		this.options = options;
		
		RegistryServiceAsync registryServiceRaw = GWT.create(RegistryService.class);
		SERVICE = new RegistryServiceAsyncDecorator(registryServiceRaw);
		
		initView();
		showDialog();
	}

	// ------------------------------------------------- Auxiliar Methods

	private void initView() {
		mainPanel = new HTMLPanel("");
		mainPanel.addStyleName(AON.CSS.aonFlexColumn());
		mainPanel.getElement().getStyle().setProperty("margin-top", ".5rem");
		
		messagePanel = new HTMLPanel("");
		mainPanel.add(messagePanel);
		
		container = new HTMLPanel("");
		container.addStyleName(AON.CSS.aonFlexColumn());
		container.getElement().getStyle().setProperty("margin", "0 1rem 1rem");
		
		if(this.isNewFee) {
			createCustomerPanel();
			container.add(customerPanel);
		}
		
		if(null != this.fee || this.isNewFee) {
			createLineConfidentialPanel();
			container.add(lineConfidentialPanel);
		} 
		
		if(null != this.fee || this.isSameProduct || this.isNewFee) {
			createProductPanel();
			container.add(productPanel);
		} 
		
		createQuantityPricePanel();
		createDatesPanel();
		createPeriodicityPanel();
		
		if(null != this.fee || this.isNewFee) {
			createWorkplacePanel();
			container.add(workplacePanel);
			
			createSellerPanel();
			container.add(sellerPanel);
			
			createSupportPanel();
			container.add(supportPanel);
			
			createInvoicingGroupPanel();
			container.add(invoicingGroupPanel);
			
			createProjectPanel();
			container.add(projectPanel);
		} 
		
		getButtonsPanel();
		container.add(buttonsPanel);
		
		mainPanel.add(container);
		    
		this.setWidget(mainPanel);
	}
	
	private void createCustomerPanel() {
		customerPanel = new HTMLPanel("");
		customerPanel.addStyleName(AON.CSS.aonItemFlex());
		
		Label customerLabel = new Label("Cliente");
		customerLabel.getElement().getStyle().setProperty("min-width", "5.5rem");
		customerLabel.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		
		customerSuggestBox = new SuggestBox();
		customerSuggestBox.setWidth("100%");
		customerSuggestBox.setHeight("2em");
		customerSuggestBox.getElement().getStyle().setProperty("padding", "0 5px");
		customerSuggestBox.getElement().getStyle().setProperty("min-width", "400px");
		customerSuggestBox.setAutoSelectEnabled(false);
		customerSuggestBox.getElement().setPropertyString("placeholder", "Cliente: busque por nombre, nif o alias");
		
		customerSuggestBox.addSelectionHandler(e -> {
			customerSuggestBox.hideSuggestionList();
			projectSuggestBox.setValue("");
			if(null != this.fee) this.fee.setProject(null);
		});
		
		customerSuggestBox.addKeyUpHandler(e -> {
			String customerQuery = customerSuggestBox.getValue();
			if(e.isControlKeyDown() && e.getNativeKeyCode() == 32) {
				customerSuggestBox.setValue("");
				customerQuery = null;
				getCustomersSuggestion(customerQuery);
			} else if(AonStringUtils.isNotBlank(customerQuery) && customerQuery.length() > 3) 
				getCustomersSuggestion(customerQuery);
		});
		
		if(null != this.customer) {
			customerSuggestBox.setEnabled(false);
			customerSuggestBox.setValue(this.customer.getName() + " ( " + this.customer.getDocument() + " )" + (AonStringUtils.isBlank(this.customer.getAlias()) ? "" : " - " + this.customer.getAlias()) );
		}
		
		customerPanel.add(customerLabel);
		customerPanel.add(customerSuggestBox);
	}

	private void getCustomersSuggestion(String customerQuery) {
		SERVICE.getCustomersSuggestion(options.getDomainName(), options.getDomain(), options.getUser(), customerQuery, new AsyncCallback<Map<String, Customer>>() {
			
			@Override
			public void onSuccess(Map<String, Customer> customerSuggestionsDB) {
				customerSuggestions = customerSuggestionsDB;
				
				MultiWordSuggestOracle orclSb = (MultiWordSuggestOracle) customerSuggestBox.getSuggestOracle();
				orclSb.clear();
				orclSb.addAll(customerSuggestions.keySet());
				orclSb.setDefaultSuggestionsFromText(customerSuggestions.keySet());
				customerSuggestBox.showSuggestionList();
			}
			
			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub	
			}
			
		});
	}

	private void createLineConfidentialPanel() {
		lineConfidentialPanel = new HTMLPanel("");
		lineConfidentialPanel.addStyleName(AON.CSS.aonItemFlex());
		
		Label lineLabel = new Label("Linea");
		lineLabel.getElement().getStyle().setProperty("min-width", "5.5rem");
		lineLabel.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		
		lineTextBox = new TextBox();
		lineTextBox.setHeight("2em");
		lineTextBox.setWidth("8.8em");
		lineTextBox.getElement().getStyle().setProperty("padding", "0 5px");
		lineTextBox.addValueChangeHandler(e -> { if(null != fee) fee.setLine(Double.parseDouble(e.getValue()));});
		if(null != fee) lineTextBox.setValue(fee.getLine().toString());
		
		Label confidentialLabel = new Label("Confidencial");
		confidentialLabel.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		confidentialButton = new Button();
		confidentialButton.addClickHandler(e -> {
			getEnableDisableButton(confidentialButton, !isActiveToggleButton(confidentialButton));
			if(null != fee) fee.setSecurityLevel(SecurityLevel.safeValueOf(isActiveToggleButton(confidentialButton) ? 1 : 0));
		});
		getEnableDisableButton(confidentialButton, null != fee && fee.getSecurityLevel() != null && fee.getSecurityLevel() == SecurityLevel.CONFIDENTIAL);
		
		lineConfidentialPanel.add(lineLabel);
		lineConfidentialPanel.add(lineTextBox);
		lineConfidentialPanel.add(confidentialLabel);
		lineConfidentialPanel.add(confidentialButton);
	}

	private void createProductPanel() {
		productPanel = new HTMLPanel("");
		productPanel.addStyleName(AON.CSS.aonFlexColumn());
		
		HTMLPanel itemPanel = new HTMLPanel("");
		itemPanel.addStyleName(AON.CSS.aonItemFlex());
		
		Label productLabel = new Label("Producto");
		productLabel.getElement().getStyle().setProperty("min-width", "5.5rem");
		productLabel.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		
		productSuggestBox = new SuggestBox();
		productSuggestBox.setWidth("100%");
		productSuggestBox.setHeight("2em");
		productSuggestBox.getElement().getStyle().setProperty("padding", "0 5px");
		productSuggestBox.getElement().getStyle().setProperty("min-width", "400px");
		productSuggestBox.setAutoSelectEnabled(false);
		productSuggestBox.getElement().setPropertyString("placeholder", "Producto: busque por c\u00f3digo o descripci\u00f3n");
		
		productSuggestBox.addSelectionHandler(e -> {
			productSuggestBox.hideSuggestionList();
			item = productSuggestions.get(productSuggestBox.getValue());
			
			productSuggestBox.setEnabled(false);
			productTextBox.setValue(item.getProduct().getName());
			productTextBox.setVisible(true);
			
			if(null != fee) fee.setItem(item);
		});
		
		productSuggestBox.addKeyUpHandler(e -> {
			String productQuery = productSuggestBox.getValue();
			if(e.isControlKeyDown() && e.getNativeKeyCode() == 32) {
				productSuggestBox.setValue("");
				productQuery = null;
				getProductsSuggestion(productQuery);
			} else  if(AonStringUtils.isNotBlank(productQuery) && productQuery.length() > 3)
				getProductsSuggestion(productQuery);
		});
		
		AonToolbarSmallButton resetBtn = new AonToolbarSmallButton("Borrar producto", AON.CSS.aonIconClear());
		resetBtn.addClickHandler(e -> {
			productTextBox.setValue("");
			productTextBox.setVisible(false);
			productSuggestBox.setEnabled(true);
			productSuggestBox.setValue("");
		});
		
		productTextBox = new TextBox();
		productTextBox.getElement().getStyle().setProperty("margin-left", "6rem");
		productTextBox.addValueChangeHandler(e ->{
			if(null != item) {
				item.getProduct().setName(productTextBox.getValue());
				item.getProduct().setModify(true);
			}
			if(null != fee) {
				fee.getItem().getProduct().setName(productTextBox.getValue());
				fee.getItem().getProduct().setModify(true);
			}
		});
		
		if(null != fee) productSuggestBox.setValue(fee.getItem().getProduct().getName() + " ( " + fee.getItem().getProduct().getCode() + " )");
		else if(this.isSameProduct || null != this.item) productSuggestBox.setValue(this.item.getProduct().getName() + " ( " + this.item.getProduct().getCode() + " )");
		
		if(null != fee || this.isSameProduct || null != this.item) {
			productSuggestBox.setEnabled(false);
			productTextBox.setValue(null != fee ? fee.getItem().getProduct().getName() : this.item.getProduct().getName());
			productTextBox.setVisible(true);
		}
		
		itemPanel.add(productLabel);
		itemPanel.add(productSuggestBox);
		itemPanel.add(resetBtn);
		productPanel.add(itemPanel);
		productPanel.add(productTextBox);
	}

	private void getProductsSuggestion(String productQuery) {
		SERVICE.getProductsSuggestion(options.getDomainName(), options.getDomain(), options.getUser(), productQuery, new AsyncCallback<Map<String, OldItem>>() {
			
			@Override
			public void onSuccess(Map<String, OldItem> productSuggestionsDB) {
				productSuggestions = productSuggestionsDB;
				
				MultiWordSuggestOracle orclSb = (MultiWordSuggestOracle) productSuggestBox.getSuggestOracle();
				orclSb.clear();
				orclSb.addAll(productSuggestions.keySet());
				orclSb.setDefaultSuggestionsFromText(productSuggestions.keySet());
				productSuggestBox.showSuggestionList();
			}
			
			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub	
			}
			
		});
	}

	private void createQuantityPricePanel() {
		quantityPricePanel = new HTMLPanel("");
		quantityPricePanel.addStyleName(AON.CSS.aonItemFlex());
		
		Label quantityLabel = new Label();
		if(null != this.fee || this.isNewFee) {
			quantityLabel = new Label("Cantidad");
			quantityLabel.getElement().getStyle().setProperty("min-width", "5.5rem");
			quantityLabel.getElement().getStyle().setFontWeight(FontWeight.BOLD);
			
			quantityTextBox = new TextBox();
			quantityTextBox.setWidth("8.8em");
			quantityTextBox.setHeight("2em");
			quantityTextBox.getElement().getStyle().setProperty("padding", "0 5px");
			quantityTextBox.addValueChangeHandler(e -> { if(null != fee) fee.setQuantity(Double.parseDouble(e.getValue()));});
			if(null != fee && null != fee.getQuantity()) quantityTextBox.setValue(fee.getQuantity().toString());
		}
		
		Label priceLabel = new Label("Precio");
		if(null == this.fee && !this.isNewFee) priceLabel.getElement().getStyle().setProperty("min-width", "5.5rem");
		else priceLabel.setWidth("3.5rem");
		priceLabel.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		
		priceTextBox = new TextBox();
		priceTextBox.setHeight("2em");
		priceTextBox.setWidth("8.8em");
		priceTextBox.getElement().getStyle().setProperty("padding", "0 5px");
		priceTextBox.addValueChangeHandler(e -> { if(null != fee) fee.setPrice(Double.parseDouble(e.getValue()));});
		if(null != fee && null != fee.getPrice()) priceTextBox.setValue(fee.getPrice().toString());
		
		Label discountLabel = new Label("Dto.");
		discountLabel.setWidth("3.5rem");
		discountLabel.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		
		discountTextBox = new TextBox();
		discountTextBox.setHeight("2em");
		discountTextBox.setWidth("8.8em");
		discountTextBox.getElement().getStyle().setProperty("padding", "0 5px");
		discountTextBox.addValueChangeHandler(e -> { if(null != fee) fee.setDiscountExpr(e.getValue());});
		if(null != fee) discountTextBox.setValue(fee.getDiscountExpr());
		
		if(null != this.fee || this.isNewFee) {
			quantityPricePanel.add(quantityLabel);
			quantityPricePanel.add(quantityTextBox);
		}
		
		quantityPricePanel.add(priceLabel);
		quantityPricePanel.add(priceTextBox);
		
		quantityPricePanel.add(discountLabel);
		quantityPricePanel.add(discountTextBox);
		
		container.add(quantityPricePanel);
	}

	private void createDatesPanel() {
		datesPanel = new HTMLPanel("");
		datesPanel.addStyleName(AON.CSS.aonItemFlex());
		
		Label startDateLabel = new Label("F. Inicio");
		startDateLabel.getElement().getStyle().setProperty("min-width", "5.5rem");
		startDateLabel.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		
		startDateBox = new AonDateBox();
		startDateBox.setHeight("2em");
		startDateBox.setWidth("8.8em");
		startDateBox.getElement().getStyle().setProperty("padding", "0 5px");
		startDateBox.getElement().getStyle().setTextAlign(TextAlign.CENTER);
		startDateBox.addStyleName("gwt-TextBox");
		startDateBox.addValueChangeHandler(e -> { if(null != fee) fee.setStartDate(e.getValue());});
		if(null != fee) startDateBox.setValue(fee.getStartDate());
		
		Label endDateLabel = new Label("F. Fin");
		endDateLabel.setWidth("3.5rem");
		endDateLabel.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		
		endDateBox = new AonDateBox();
		endDateBox.setHeight("2em");
		endDateBox.setWidth("8.8em");
		endDateBox.getElement().getStyle().setProperty("padding", "0 5px");
		endDateBox.getElement().getStyle().setTextAlign(TextAlign.CENTER);
		endDateBox.addStyleName("gwt-TextBox");
		endDateBox.addValueChangeHandler(e -> { if(null != fee) fee.setEndDate(e.getValue());});
		if(null != fee) endDateBox.setValue(fee.getEndDate());
		
		datesPanel.add(startDateLabel);
		datesPanel.add(startDateBox);
		datesPanel.add(endDateLabel);
		datesPanel.add(endDateBox);
		
		container.add(datesPanel);
	}

	private void createPeriodicityPanel() {
		periodicityPanel = new HTMLPanel("");
		periodicityPanel.addStyleName(AON.CSS.aonItemFlex());
		
		Label billingLabel = new Label("F. Facturaci\u00f3n");
		billingLabel.getElement().getStyle().setProperty("min-width", "5.5rem");
		billingLabel.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		
		monthListBox = createMonthListBox();
		monthListBox.addChangeHandler(e -> {
			if(null != fee) fee.setBillingDate(createBillingDate());
		});
		if(null != fee) setSelectedValueLB(monthListBox, fee.getBillingDate().getMonth() + "");
		
		yearTextBox = createYearTextBox();
		yearTextBox.addValueChangeHandler(e -> {
			if(null != fee) fee.setBillingDate(createBillingDate());
		});
		if(null != fee) yearTextBox.setValue((fee.getBillingDate().getYear() + 1900) + "");
		
		periodicityPanel.add(billingLabel);
		periodicityPanel.add(monthListBox);
		periodicityPanel.add(yearTextBox);
		
		periodLabel = new Label();
		if(null != this.fee || this.isNewFee) {
			periodLabel = new Label("Periodo");
			periodLabel.setWidth("3.5rem");
			periodLabel.getElement().getStyle().setFontWeight(FontWeight.BOLD);
			
			periodListBox = new ListBox();
			periodListBox.setHeight("2em");
			periodListBox.setWidth("9em");
			periodListBox.addItem("Sin periodo", "0");
			periodListBox.addItem("Mensual", "1");
			periodListBox.addItem("Bimensual", "2");
			periodListBox.addItem("Trimestral", "3");
			periodListBox.addItem("Cuatrimestral", "4");
			periodListBox.addItem("Semestral", "5");
			periodListBox.addItem("Anual", "6");
			periodListBox.addChangeHandler(e -> { if(null != fee) fee.setPeriod(BillingPeriod.safeValueOf(periodListBox.getSelectedValue()));});
			if(null != fee) setSelectedValueLB(periodListBox, fee.getPeriod().getValue().toString());
			
			periodicityPanel.add(periodLabel);
			periodicityPanel.add(periodListBox);
		}
		
		container.add(periodicityPanel);
	}
	
	private TextBox createYearTextBox() {
		TextBox tb = new TextBox();
		tb.setMaxLength(4);
		tb.setHeight("2em");
		tb.setWidth("4em");
		tb.setAlignment(TextAlignment.CENTER);
		tb.getElement().getStyle().setProperty("padding", "0 5px");
		tb.getElement().getStyle().setProperty("placeholder", "aaaa");
		
		return tb;
	}

	private void createWorkplacePanel() {
		workplacePanel = new HTMLPanel("");
		workplacePanel.addStyleName(AON.CSS.aonItemFlex());
		
		Label workplaceLabel = new Label("C. Trabajo");
		workplaceLabel.getElement().getStyle().setProperty("min-width", "5.5rem");
		workplaceLabel.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		
		workplaceSuggestBox = new SuggestBox();
		workplaceSuggestBox.setWidth("100%");
		workplaceSuggestBox.setHeight("2em");
		workplaceSuggestBox.getElement().getStyle().setProperty("padding", "0 5px");
		workplaceSuggestBox.getElement().getStyle().setProperty("min-width", "400px");
		workplaceSuggestBox.setAutoSelectEnabled(false);
		workplaceSuggestBox.getElement().setPropertyString("placeholder", "C. Trabajo: busque por descripci\u00f3n");
		
		workplaceSuggestBox.addSelectionHandler(e -> {
			workplaceSuggestBox.hideSuggestionList();
			if(null != fee) fee.setWorkplace(workplaceSuggestions.get(workplaceSuggestBox.getValue()));
		});
		
		workplaceSuggestBox.addValueChangeHandler(e -> {
			if(null != fee && AonStringUtils.isBlank(workplaceSuggestBox.getValue())) AonMessagePanel.showError(messagePanel, "El campo centro de trabajo es obligatorio");
		});
		
		workplaceSuggestBox.addKeyUpHandler(e -> {
			String workplaceQuery = workplaceSuggestBox.getValue();
			if(e.isControlKeyDown() && e.getNativeKeyCode() == 32) {
				workplaceSuggestBox.setValue("");
				workplaceQuery = null;
				getWorkplacesSuggestion(workplaceQuery);
			} else if(AonStringUtils.isNotBlank(workplaceQuery) && workplaceQuery.length() > 3) 
				getWorkplacesSuggestion(workplaceQuery);
		});
		
		if(null != fee) workplaceSuggestBox.setValue(fee.getWorkplace().getDescription());
		
		workplacePanel.add(workplaceLabel);
		workplacePanel.add(workplaceSuggestBox);
	}

	private void getWorkplacesSuggestion(String workplaceQuery) {
		SERVICE.getWorkplacesSuggestion(options.getDomainName(), options.getDomain(), options.getUser(), workplaceQuery, new AsyncCallback<Map<String, Workplace>>() {
			
			@Override
			public void onSuccess(Map<String, Workplace> workplaceSuggestionsDB) {
				workplaceSuggestions = workplaceSuggestionsDB;
				
				MultiWordSuggestOracle orclSb = (MultiWordSuggestOracle) workplaceSuggestBox.getSuggestOracle();
				orclSb.clear();
				orclSb.addAll(workplaceSuggestions.keySet());
				orclSb.setDefaultSuggestionsFromText(workplaceSuggestions.keySet());
				workplaceSuggestBox.showSuggestionList();
			}
			
			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub	
			}
			
		});
	}

	private void createSellerPanel() {
		sellerPanel = new HTMLPanel("");
		sellerPanel.addStyleName(AON.CSS.aonItemFlex());
		
		Label sellerLabel = new Label("Comercial");
		sellerLabel.getElement().getStyle().setProperty("min-width", "5.5rem");
		sellerLabel.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		
		sellerSuggestBox = new SuggestBox();
		sellerSuggestBox.setWidth("100%");
		sellerSuggestBox.setHeight("2em");
		sellerSuggestBox.getElement().getStyle().setProperty("padding", "0 5px");
		sellerSuggestBox.getElement().getStyle().setProperty("min-width", "400px");
		sellerSuggestBox.setAutoSelectEnabled(false);
		sellerSuggestBox.getElement().setPropertyString("placeholder", "Comercial: busque por nombre");
		
		sellerSuggestBox.addSelectionHandler(e -> {
			sellerSuggestBox.hideSuggestionList();
			if(null != fee) fee.setSeller(sellerSuggestions.get(sellerSuggestBox.getValue()));
		});
		
		sellerSuggestBox.addValueChangeHandler(e -> {
			if(null != fee && AonStringUtils.isBlank(sellerSuggestBox.getValue())) fee.setSeller(null);
		});
		
		sellerSuggestBox.addKeyUpHandler(e -> {
			String sellerQuery = sellerSuggestBox.getValue();
			if(e.isControlKeyDown() && e.getNativeKeyCode() == 32) {
				sellerSuggestBox.setValue("");
				sellerQuery = null;
				getSellersSuggestion(sellerQuery);
			} else if(AonStringUtils.isNotBlank(sellerQuery) && sellerQuery.length() > 3)
				getSellersSuggestion(sellerQuery);
		});
		
		if(null != fee) sellerSuggestBox.setValue(fee.getSeller().getName());
		
		sellerPanel.add(sellerLabel);
		sellerPanel.add(sellerSuggestBox);
	}
	
	
	private void createSupportPanel(){
		supportPanel = new HTMLPanel("");
		supportPanel.addStyleName(AON.CSS.aonItemFlex());
		
		Label supportLabel = new Label("Soporte");
		supportLabel.getElement().getStyle().setProperty("min-width", "5.5rem");
		supportLabel.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		
		supportSuggestBox = new SuggestBox();
		supportSuggestBox.setWidth("100%");
		supportSuggestBox.setHeight("2em");
		supportSuggestBox.getElement().getStyle().setProperty("padding", "0 5px");
		supportSuggestBox.getElement().getStyle().setProperty("min-width", "400px");
		supportSuggestBox.setAutoSelectEnabled(false);
		supportSuggestBox.getElement().setPropertyString("placeholder", "Soporte: busque por nombre");
		
		supportSuggestBox.addSelectionHandler(e -> {
			supportSuggestBox.hideSuggestionList();
			if(null != fee) fee.setSeller(sellerSuggestions.get(supportSuggestBox.getValue()));
		});
		
		supportSuggestBox.addValueChangeHandler(e -> {
			if(null != fee && AonStringUtils.isBlank(supportSuggestBox.getValue())) fee.setSeller(null);
		});
		
		supportSuggestBox.addKeyUpHandler(e -> {
			String sellerQuery = supportSuggestBox.getValue();
			if(e.isControlKeyDown() && e.getNativeKeyCode() == 32) {
				supportSuggestBox.setValue("");
				sellerQuery = null;
				getSupportSuggestion(sellerQuery);
			} else if(AonStringUtils.isNotBlank(sellerQuery) && sellerQuery.length() > 3)
				getSupportSuggestion(sellerQuery);
		});
		
		if(null != fee) supportSuggestBox.setValue(fee.getSeller().getName());
		
		supportPanel.add(supportLabel);
		supportPanel.add(supportSuggestBox);
		
		
	}

	private void getSellersSuggestion(String sellerQuery) {
		SERVICE.getSellersSuggestion(options.getDomainName(), options.getDomain(), options.getUser(), sellerQuery, new AsyncCallback<Map<String, Seller>>() {
			
			@Override
			public void onSuccess(Map<String, Seller> sellerSuggestionsDB) {
				sellerSuggestions = sellerSuggestionsDB;
				
				MultiWordSuggestOracle orclSb = (MultiWordSuggestOracle) sellerSuggestBox.getSuggestOracle();
				orclSb.clear();
				orclSb.addAll(sellerSuggestions.keySet());
				orclSb.setDefaultSuggestionsFromText(sellerSuggestions.keySet());
				sellerSuggestBox.showSuggestionList();
			}
			
			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub	
			}
			
		});
	}
	
	
	private void getSupportSuggestion(String sellerQuery) {
		SERVICE.getSupporstSuggestion(options.getDomainName(), options.getDomain(), options.getUser(), sellerQuery, new AsyncCallback<Map<String, RegistrySeller>>() {
			
			@Override
			public void onSuccess(Map<String, RegistrySeller> suppSuggestionsDB) {
				supportSuggestions = suppSuggestionsDB;
				
				MultiWordSuggestOracle orclSb = (MultiWordSuggestOracle) supportSuggestBox.getSuggestOracle();
				orclSb.clear();
				orclSb.addAll(supportSuggestions.keySet());
				orclSb.setDefaultSuggestionsFromText(supportSuggestions.keySet());
				supportSuggestBox.showSuggestionList();
			}
			
			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub	
			}
			
		});
	}

	private void createInvoicingGroupPanel() {
		invoicingGroupPanel = new HTMLPanel("");
		invoicingGroupPanel.addStyleName(AON.CSS.aonItemFlex());
		
		Label invoicingGroupLabel = new Label("G. Facturaci\u00f3n");
		invoicingGroupLabel.getElement().getStyle().setProperty("min-width", "5.5rem");
		invoicingGroupLabel.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		
		invoicingGroupSuggestBox = new SuggestBox();
		invoicingGroupSuggestBox.setWidth("100%");
		invoicingGroupSuggestBox.setHeight("2em");
		invoicingGroupSuggestBox.getElement().getStyle().setProperty("padding", "0 5px");
		invoicingGroupSuggestBox.getElement().getStyle().setProperty("min-width", "400px");
		invoicingGroupSuggestBox.setAutoSelectEnabled(false);
		invoicingGroupSuggestBox.getElement().setPropertyString("placeholder", "G. Facturaci\u00f3n: busque por descripci\u00f3n");
		
		invoicingGroupSuggestBox.addSelectionHandler(e -> {
			invoicingGroupSuggestBox.hideSuggestionList();
			if(null != fee) fee.setInvoicingGroup(invoicingGroupSuggestions.get(invoicingGroupSuggestBox.getValue()));
		});
		
		invoicingGroupSuggestBox.addValueChangeHandler(e -> {
			if(null != fee && AonStringUtils.isBlank(invoicingGroupSuggestBox.getValue())) fee.setInvoicingGroup(null);
		});
		
		invoicingGroupSuggestBox.addKeyUpHandler(e -> {
			String invoicingGroupQuery = invoicingGroupSuggestBox.getValue();
			if(e.isControlKeyDown() && e.getNativeKeyCode() == 32) {
				invoicingGroupSuggestBox.setValue("");
				invoicingGroupQuery = null;
				getInvoicingGroupsSuggestion(invoicingGroupQuery);
			} else if(AonStringUtils.isNotBlank(invoicingGroupQuery) && invoicingGroupQuery.length() > 3)
				getInvoicingGroupsSuggestion(invoicingGroupQuery);
		});
		
		if(null != fee) invoicingGroupSuggestBox.setValue(fee.getInvoicingGroup().getDescription());
		
		invoicingGroupPanel.add(invoicingGroupLabel);
		invoicingGroupPanel.add(invoicingGroupSuggestBox);
	}
	
	private void getInvoicingGroupsSuggestion(String invoicingGroupQuery) {
		SERVICE.getInvoicingGroupsSuggestion(options.getDomainName(), options.getDomain(), options.getUser(), invoicingGroupQuery, new AsyncCallback<Map<String, InvoicingGroup>>() {
			
			@Override
			public void onSuccess(Map<String, InvoicingGroup> invoicingGroupSuggestionsDB) {
				invoicingGroupSuggestions = invoicingGroupSuggestionsDB;
				
				MultiWordSuggestOracle orclSb = (MultiWordSuggestOracle) invoicingGroupSuggestBox.getSuggestOracle();
				orclSb.clear();
				orclSb.addAll(invoicingGroupSuggestions.keySet());
				orclSb.setDefaultSuggestionsFromText(invoicingGroupSuggestions.keySet());
				invoicingGroupSuggestBox.showSuggestionList();
			}
			
			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub	
			}
			
		});
	}

	private void createProjectPanel() {
		projectPanel = new HTMLPanel("");
		projectPanel.addStyleName(AON.CSS.aonItemFlex());
		
		Label projectLabel = new Label("Proyecto");
		projectLabel.getElement().getStyle().setProperty("min-width", "5.5rem");
		projectLabel.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		
		projectSuggestBox = new SuggestBox();
		projectSuggestBox.setWidth("100%");
		projectSuggestBox.setHeight("2em");
		projectSuggestBox.getElement().getStyle().setProperty("padding", "0 5px");
		projectSuggestBox.getElement().getStyle().setProperty("min-width", "400px");
		projectSuggestBox.setAutoSelectEnabled(false);
		projectSuggestBox.getElement().setPropertyString("placeholder", "Proyecto: busque por descripci\u00f3n");
		
		projectSuggestBox.addSelectionHandler(e -> {
			projectSuggestBox.hideSuggestionList();
			if(null != fee) fee.setProject(projectSuggestions.get(projectSuggestBox.getValue()));
		});
		
		projectSuggestBox.addValueChangeHandler(e -> {
			if(null != fee && AonStringUtils.isBlank(projectSuggestBox.getValue())) fee.setProject(null);
		});
		
		projectSuggestBox.addKeyUpHandler(e -> {
			String projectQuery = projectSuggestBox.getValue();
			
			Integer customerId = null;
			if(AonStringUtils.isNotBlank(customerSuggestBox.getValue())) {
				Customer customer = customerSuggestions.get(customerSuggestBox.getValue());
				if(null != customer) customerId = customer.getId();
				else if(null != fee && null != fee.getCustomer()) customerId = fee.getCustomer().getId();
			}
			
			if(e.isControlKeyDown() && e.getNativeKeyCode() == 32) {
				projectSuggestBox.setValue("");
				projectQuery = null;
				getProjectsSuggestion(customerId, projectQuery);
			} else if(AonStringUtils.isNotBlank(projectQuery) && projectQuery.length() > 3)
				getProjectsSuggestion(customerId, projectQuery);
		});
		
		if(null != fee) projectSuggestBox.setValue(fee.getProject().getName());
		
		projectPanel.add(projectLabel);
		projectPanel.add(projectSuggestBox);
	}
	
	private void getProjectsSuggestion(Integer customerId, String projectQuery) {
		SERVICE.getProjectsSuggestion(options.getDomainName(), options.getDomain(), options.getUser(), customerId, projectQuery, new AsyncCallback<Map<String, Project>>() {
			
			@Override
			public void onSuccess(Map<String, Project> projectProjectSuggestionsDB) {
				projectSuggestions = projectProjectSuggestionsDB;
				
				MultiWordSuggestOracle orclSb = (MultiWordSuggestOracle) projectSuggestBox.getSuggestOracle();
				orclSb.clear();
				orclSb.addAll(projectSuggestions.keySet());
				orclSb.setDefaultSuggestionsFromText(projectSuggestions.keySet());
				projectSuggestBox.showSuggestionList();
			}
			
			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub	
			}
			
		});
	}

	private Date createBillingDate() {
		if(AonStringUtils.isBlank(monthListBox.getSelectedValue()) || AonStringUtils.isBlank(yearTextBox.getValue())) return null;
		
		return new Date(Integer.parseInt(yearTextBox.getValue()) - 1900, Integer.parseInt(monthListBox.getSelectedValue()), 1);
	}

	private ListBox createMonthListBox() {
		ListBox lb = new ListBox();
		lb.setHeight("2em");
		lb.getElement().getStyle().setProperty("padding", "0 5px");

		lb.addItem("-", "");
		lb.addItem("Ene.", "0");
		lb.addItem("Feb.", "1");
		lb.addItem("Mar.", "2");
		lb.addItem("Abr.", "3");
		lb.addItem("May.", "4");
		lb.addItem("Jun.", "5");
		lb.addItem("Jul.", "6");
		lb.addItem("Ago.", "7");
		lb.addItem("Sep.", "8");
		lb.addItem("Oct.", "9");
		lb.addItem("Nov.", "10");
		lb.addItem("Dic.", "11");

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

	private void getEnableDisableButton(Button button, boolean disabled) {
		button.removeStyleName(disabled ? AON.AON_ICON_DISABLE : AON.AON_ICON_ENABLE);
		button.removeStyleName(AON.AON_NO_MARGIN);
		button.removeStyleName(AON.AON_EDIT_DATA_TABLE_BUTTON);
		
		button.setStyleName(!disabled ? AON.AON_ICON_DISABLE : AON.AON_ICON_ENABLE );
		button.setStyleName(AON.AON_NO_MARGIN, true);
		button.setStyleName(AON.AON_EDIT_DATA_TABLE_BUTTON, true);
	}
	
	private boolean isActiveToggleButton(Button button) {
		return AonStringUtils.containsIgnoreCase(button.getStyleName(), AON.AON_ICON_ENABLE);
	}

	private void showDialog() {
		// Show center
		Scheduler.get().scheduleDeferred(() -> {
			center();
			show();
		});
	}

	// ------------------------------------------------- ButtonsPanel

	private void getButtonsPanel() {
		buttonsPanel = new HTMLPanel("");
		buttonsPanel.addStyleName(AON.CSS.aonDisplayFlexEnd());
		
		Button closeBtnDialog = new Button();
		closeBtnDialog.setStyleName(AON.CSS.aonCancelButtonSmall());
		closeBtnDialog.setText(AON.MSG.cancelAction());
		closeBtnDialog.getElement().getStyle().setMarginRight(10, Unit.PX);
		closeBtnDialog.addClickHandler(e -> hide());

		buttonsPanel.add(closeBtnDialog);
		
		Button acceptBtnDialog = null;
		
		if(this.isNewFee) {
			acceptBtnDialog = new Button();
			acceptBtnDialog.setStyleName(AON.CSS.aonDialogButton());
			acceptBtnDialog.addStyleName(AON.CSS.aonIconSave());
			acceptBtnDialog.setText(AON.MSG.saveAction());
			acceptBtnDialog.addClickHandler(e -> {
				createNewFee();
			});
		} else if(null != this.fee) {
			acceptBtnDialog = new Button();
			acceptBtnDialog.setStyleName(AON.CSS.aonDialogButton());
			acceptBtnDialog.addStyleName(AON.CSS.aonIconSave());
			acceptBtnDialog.setText(AON.MSG.saveAction());
			acceptBtnDialog.addClickHandler(e -> {
				hide();
				onAccept(fee);
			});
		} else {
			acceptBtnDialog = new Button();
			acceptBtnDialog.setStyleName(AON.CSS.aonDialogButton());
			acceptBtnDialog.addStyleName(AON.CSS.aonIconSave());
			acceptBtnDialog.setText(AON.MSG.saveAction());
			acceptBtnDialog.addClickHandler(e -> {
				hide();
				acceptDialog();
			});
		}

		buttonsPanel.add(acceptBtnDialog);
	}

	private void createNewFee() {
		Fee newFee = new Fee();
		
		if(AonStringUtils.isNotBlank(projectSuggestBox.getValue())) newFee.setProject(projectSuggestions.get(projectSuggestBox.getValue()));
		
		if(null != this.customer) newFee.setCustomer(this.customer);
		else if(AonStringUtils.isNotBlank(customerSuggestBox.getValue())) newFee.setCustomer(customerSuggestions.get(customerSuggestBox.getValue()));
		
		if(AonStringUtils.isBlank(customerSuggestBox.getValue())) {
			AonMessagePanel.showError(messagePanel, "El campo cliente es obligatorio");
			return;
		}
		
		newFee.setLine(AonStringUtils.isBlank(lineTextBox.getValue()) ? 1.00 : Double.parseDouble(lineTextBox.getValue()));
		newFee.setItem(item);
		
		if(item == null) {
			AonMessagePanel.showError(messagePanel, "El campo producto es obligatorio");
			return;
		}
		
		newFee.setDescription(newFee.getItem().getProduct().getName());
		newFee.setQuantity(AonStringUtils.isBlank(quantityTextBox.getValue()) ? null : Double.parseDouble(quantityTextBox.getValue()));
		newFee.setPrice(AonStringUtils.isBlank(priceTextBox.getValue()) ? null : Double.parseDouble(priceTextBox.getValue()));
		newFee.setDiscountExpr(discountTextBox.getValue());
		
		newFee.setStartDate(startDateBox.getValue());
		newFee.setEndDate(endDateBox.getValue());
		newFee.setBillingDate(createBillingDate());
		
		if(newFee.getStartDate() == null || newFee.getBillingDate() == null) {
			AonMessagePanel.showError(messagePanel, "Las fechas desde y facturaci\u00f3n son obligatorias");
			return;
		}
		
		newFee.setPeriod(BillingPeriod.safeValueOf(periodListBox.getSelectedValue()));
		newFee.setSecurityLevel(SecurityLevel.safeValueOf(isActiveToggleButton(confidentialButton) ? 1 : 0));
		
		if(AonStringUtils.isNotBlank(invoicingGroupSuggestBox.getValue())) newFee.setInvoicingGroup(invoicingGroupSuggestions.get(invoicingGroupSuggestBox.getValue()));
		if(AonStringUtils.isNotBlank(sellerSuggestBox.getValue())) newFee.setSeller(sellerSuggestions.get(sellerSuggestBox.getValue()));
		if(AonStringUtils.isNotBlank(workplaceSuggestBox.getValue())) newFee.setWorkplace(workplaceSuggestions.get(workplaceSuggestBox.getValue()));
		
		if(AonStringUtils.isBlank(workplaceSuggestBox.getValue())) {
			AonMessagePanel.showError(messagePanel, "El campo centro de trabajo es obligatorio");
			return;
		}
		
		hide();
		onCreate(newFee);
	}

	private void acceptDialog() {
		onAccept(
			Optional.ofNullable(item),
			Optional.ofNullable(AonStringUtils.isBlank(priceTextBox.getValue()) ? null : Double.parseDouble(priceTextBox.getValue())),
			Optional.ofNullable(AonStringUtils.isBlank(discountTextBox.getValue()) ? null : discountTextBox.getValue()),
			Optional.ofNullable(null == startDateBox.getValue() ? null : startDateBox.getValue()),
			Optional.ofNullable(null == endDateBox.getValue() ? null : endDateBox.getValue()),
			Optional.ofNullable(createBillingDate())
		);
	}

	// ------------------------------------------------- Abstract Methods

	protected abstract void onAccept(Fee fee);
	protected abstract void onAccept(Optional<OldItem> item, Optional<Double> price, Optional<String> discountExpr, Optional<Date> startDate, Optional<Date> endDate, Optional<Date> billingDate);
	protected abstract void onCreate(Fee fee);

}
