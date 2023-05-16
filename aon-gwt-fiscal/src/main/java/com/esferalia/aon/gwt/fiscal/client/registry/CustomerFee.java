package com.esferalia.aon.gwt.fiscal.client.registry;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.TreeMap;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.RegistryService;
import com.esferalia.aon.gwt.common.client.RegistryServiceAsync;
import com.esferalia.aon.gwt.common.client.RegistryServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.common.client.widget.Upload;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDateBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog.AonAcceptDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonProgressBarDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarSmallButton;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.fiscal.client.MainEntryPoint;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.ImportError;
import com.esferalia.aon.occam.api.model.Workplace;
import com.esferalia.aon.occam.api.model.fee.Fee;
import com.esferalia.aon.occam.api.model.finance.InvoicingGroup;
import com.esferalia.aon.occam.api.model.product.OldItem;
import com.esferalia.aon.occam.api.model.registry.CustomerFeeParams;
import com.esferalia.aon.occam.api.model.registry.Project;
import com.esferalia.aon.occam.api.model.registry.Segment;
import com.esferalia.aon.occam.api.model.registry.Seller;
import com.esferalia.aon.occam.api.model.type.BillingPeriod;
import com.esferalia.aon.occam.api.model.type.RegistryStatus;
import com.esferalia.aon.watson.mutable.MutableInt;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class CustomerFee extends MainEntryPoint {

	// Services
	private static RegistryServiceAsync SERVICE;
	private static CommonServiceAsync COMMON_SERVICE;

	// Options Config
	private RegistryModuleOptions options;

	// Content
	private DockLayoutPanel dockLayoutPanel;

	private AonToolbar toolbar;
	private AonToolbarButton saveButton;
	private AonToolbarButton undoAllButton;
	private AonToolbarButton createButton;
	private AonToolbarButton addValueButton;
	private AonToolbarButton deleteFeeButton;
	private AonToolbarButton exportButton;
	private AonToolbarButton importButton;
	
	// Filter
	private boolean expandFilter = false;
	
	private ListBox periocityListBox;
	private ListBox monthListBox;
	private ListBox yearListBox;
	private SuggestBox customerSuggestBox;
	private ListBox customerStatusListBox;
	private SuggestBox conceptSuggestBox;
	private TextBox priceTextBox;
	private TextBox discountTextBox;
	
	private HTMLPanel filterExpandPanel;
	private AonDateBox startDateBox;
	private AonDateBox endDateBox;
	private TextBox quantityTextBox;
	private SuggestBox workplaceSuggestBox;
	private SuggestBox sellerSuggestBox;
	private SuggestBox invoicingGroupSuggestBox;
	private SuggestBox projectSuggestBox;
	private ListBox segmentListBox;
	
	// Fee Table
	private HTMLPanel container;
	private HTMLPanel messagePanel;
	private DeckPanel deckPanel;
	private ScrollPanel scrollPanel;
	private Grid feeTable;
	
	// Variables
	private Map<CheckBox, Fee> selectionModel = new HashMap<>();
	private LinkedList<Fee> feeList = new LinkedList<>();
	private boolean hasChange = false;

	// Search Variables
	private CustomerFeeParams params;
	private Date billingDate;
	
	private Map<String, Customer> customerSuggestions = new TreeMap<>();
	private Map<String, OldItem> productSuggestions = new TreeMap<>();
	
	private Map<String, Workplace> workplaceSuggestions = new TreeMap<>();
	private Map<String, Seller> sellerSuggestions = new TreeMap<>();
	private Map<String, InvoicingGroup> invoicingGroupSuggestions = new TreeMap<>();
	private Map<String, Project> projectSuggestions = new TreeMap<>();
	
	final private int limit = 100;
	final private MutableInt offset = new MutableInt(0);
	final private MutableInt moreData = new MutableInt(0);
	final private MutableInt searchEnabled = new MutableInt(0); 
	private int lastScrollPos = 0;
	
	// Import fees
	AonProgressBarDialog pbd;
	LinkedList<String> verror = new LinkedList<>();
	LinkedList<String> werror = new LinkedList<>();

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
							dockLayoutPanel.add(new Label(AON.MSG.loadError( " [Interno: " + caught.getMessage() + "]")));
						}
					});
		} else {
			loadModule(opt);
		}
	}

	private void loadModule(final RegistryModuleOptions opt) {
		createToolbar();
		dockLayoutPanel.addNorth(toolbar, 50);

		container = new HTMLPanel("");
		container.clear();
		container.addStyleName(AON.CSS.aonFlexColumn());
		dockLayoutPanel.add(container);

		messagePanel = new HTMLPanel("");
		container.add(messagePanel);
		
		createFilterPanel(opt, new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				dockLayoutPanel.add(new Label(AON.MSG.loadError( " [Interno: " + caught.getMessage() + "]")));
			}

			@Override
			public void onSuccess(Void result) {
				deckPanel = new DeckPanel();
				deckPanel.getElement().getStyle().setProperty("margin", "0 1rem");
				container.add(deckPanel);
				
				scrollPanel = new ScrollPanel();
				scrollPanel.setHeight((Window.getClientHeight() - 230) + "px");
				scrollPanel.addScrollHandler(e -> {
					// ------------------------------------ Ignore scroll up.
					int oldScrollPos = lastScrollPos;
					lastScrollPos = scrollPanel.getVerticalScrollPosition();
					if (oldScrollPos >= lastScrollPos) {
						return;
					}
					// -----------------------------------------------------
					if (isSearchEnabled()) {
						int maxScrollTop = scrollPanel.getWidget().getOffsetHeight() - scrollPanel.getOffsetHeight();
						if (lastScrollPos >= maxScrollTop) {
							disableSearch();
							searchFees();
						}
					}
				});
				
				initializeDeckPanel();

				AonMessagePanel.showLoading(messagePanel, "Cargando panel de facturaci\u00f3n ...");
				resetFilter();
				setHasChange(false);
				onSearchFees();
				AonMessagePanel.hideMessage(messagePanel);
			}
			
		});
		
	}

	private void createFilterPanel(final RegistryModuleOptions opt, AsyncCallback<Void> endCallback) {
		HTMLPanel filterContentPanel = new HTMLPanel("");
		filterContentPanel.addStyleName(AON.CSS.aonFlexBetween());
		filterContentPanel.addStyleName(AON.CSS.aonFilterPanel());
		filterContentPanel.getElement().getStyle().setProperty("margin", "0 1rem");
		
		HTMLPanel filterLeftPanel = new HTMLPanel("");
		filterLeftPanel.addStyleName(AON.CSS.aonFlexColumn());
		
		HTMLPanel filterDefaultPanel = new HTMLPanel("");
		filterDefaultPanel.addStyleName(AON.CSS.aonFlexWrap());
		
		// Period
		HTMLPanel periodItemPanel = new HTMLPanel("");
		periodItemPanel.addStyleName(AON.CSS.aonItemFlex());

		Label periodLabel = new Label("F. Facturaci\u00f3n");
		periodLabel.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		monthListBox = createMonthListBox();
		monthListBox.addChangeHandler(e -> onSearchFees());
		
		createYearListBox(lb -> {
			yearListBox = lb;
			yearListBox.addChangeHandler(e -> onSearchFees());
			periodItemPanel.add(yearListBox);
			endCallback.onSuccess(null);
		});
		
		periodItemPanel.add(periodLabel);
		periodItemPanel.add(monthListBox);

		filterDefaultPanel.add(periodItemPanel);
		
		// Periodicity
		HTMLPanel periodicityItemPanel = new HTMLPanel("");
		periodicityItemPanel.addStyleName(AON.CSS.aonItemFlex());

		Label periodicityLabel = new Label("Periocidad");
		periodicityLabel.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		periocityListBox = createPeriodicityListBox();
		
		periodicityItemPanel.add(periodicityLabel);
		periodicityItemPanel.add(periocityListBox);

		filterDefaultPanel.add(periodicityItemPanel);

		// Customer
		HTMLPanel customerItemPanel = new HTMLPanel("");
		customerItemPanel.addStyleName(AON.CSS.aonItemFlex());

		Label customerLabel = new Label("Cliente");
		customerLabel.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		createCustomerSuggestBox();
		createCustomerStatusListBox();

		customerItemPanel.add(customerLabel);
		customerItemPanel.add(customerSuggestBox);
		customerItemPanel.add(customerStatusListBox);

		filterDefaultPanel.add(customerItemPanel);

		// Product
		HTMLPanel conceptItemPanel = new HTMLPanel("");
		conceptItemPanel.addStyleName(AON.CSS.aonItemFlex());

		Label conceptLabel = new Label("Concepto");
		conceptLabel.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		createConceptSuggestBox();

		conceptItemPanel.add(conceptLabel);
		conceptItemPanel.add(conceptSuggestBox);

		filterDefaultPanel.add(conceptItemPanel);
		
		// Price
		HTMLPanel priceItemPanel = new HTMLPanel("");
		priceItemPanel.addStyleName(AON.CSS.aonItemFlex());

		Label priceLabel = new Label("Precio");
		priceLabel.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		priceTextBox = new TextBox();
		priceTextBox.setWidth("80px");
		priceTextBox.setHeight("2em");
		priceTextBox.getElement().getStyle().setProperty("padding", "0 5px");
		priceTextBox.addValueChangeHandler(e -> onSearchFees());

		priceItemPanel.add(priceLabel);
		priceItemPanel.add(priceTextBox);

		filterDefaultPanel.add(priceItemPanel);
		
		// Discount
		HTMLPanel discountItemPanel = new HTMLPanel("");
		discountItemPanel.addStyleName(AON.CSS.aonItemFlex());

		Label discountLabel = new Label("Descuento");
		discountLabel.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		discountTextBox = new TextBox();
		discountTextBox.setWidth("80px");
		discountTextBox.setHeight("2em");
		discountTextBox.getElement().getStyle().setProperty("padding", "0 5px");
		discountTextBox.addValueChangeHandler(e -> onSearchFees());

		discountItemPanel.add(discountLabel);
		discountItemPanel.add(discountTextBox);

		filterDefaultPanel.add(discountItemPanel);
		
		filterLeftPanel.add(filterDefaultPanel);
		
		filterExpandPanel = new HTMLPanel("");
		filterExpandPanel.addStyleName(AON.CSS.aonFlexWrap());
		filterExpandPanel.getElement().getStyle().setDisplay(Display.NONE);
		
		// StartDate
		HTMLPanel startDateItemPanel = new HTMLPanel("");
		startDateItemPanel.addStyleName(AON.CSS.aonItemFlex());

		Label startDateLabel = new Label("F. Inicio");
		startDateLabel.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		
		startDateBox = new AonDateBox();
		startDateBox.getElement().getStyle().setTextAlign(TextAlign.CENTER);
		startDateBox.addStyleName("gwt-TextBox");
		setInputStyle(startDateBox);
		startDateBox.addValueChangeHandler(e -> onSearchFees());

		startDateItemPanel.add(startDateLabel);
		startDateItemPanel.add(startDateBox);

		filterExpandPanel.add(startDateItemPanel);
		
		// EndDate
		HTMLPanel endDateItemPanel = new HTMLPanel("");
		endDateItemPanel.addStyleName(AON.CSS.aonItemFlex());

		Label endDateLabel = new Label("F. Fin");
		endDateLabel.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		
		endDateBox = new AonDateBox();
		endDateBox.getElement().getStyle().setTextAlign(TextAlign.CENTER);
		endDateBox.addStyleName("gwt-TextBox");
		setInputStyle(endDateBox);
		endDateBox.addValueChangeHandler(e -> onSearchFees());

		endDateItemPanel.add(endDateLabel);
		endDateItemPanel.add(endDateBox);

		filterExpandPanel.add(endDateItemPanel);
		
		// Quantity
		HTMLPanel quantityItemPanel = new HTMLPanel("");
		quantityItemPanel.addStyleName(AON.CSS.aonItemFlex());

		Label quantityLabel = new Label("Cantidad");
		quantityLabel.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		quantityTextBox = new TextBox();
		quantityTextBox.setWidth("80px");
		quantityTextBox.setHeight("2em");
		quantityTextBox.getElement().getStyle().setProperty("padding", "0 5px");
		quantityTextBox.addValueChangeHandler(e -> onSearchFees());

		quantityItemPanel.add(quantityLabel);
		quantityItemPanel.add(quantityTextBox);

		filterExpandPanel.add(quantityItemPanel);
		
		// Workplace
		HTMLPanel workplaceItemPanel = new HTMLPanel("");
		workplaceItemPanel.addStyleName(AON.CSS.aonItemFlex());

		Label workplaceLabel = new Label("C. Trabajo");
		workplaceLabel.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		createWorkplaceSuggestBox();

		workplaceItemPanel.add(workplaceLabel);
		workplaceItemPanel.add(workplaceSuggestBox);

		filterExpandPanel.add(workplaceItemPanel);
		
		// Seller
		HTMLPanel sellerItemPanel = new HTMLPanel("");
		sellerItemPanel.addStyleName(AON.CSS.aonItemFlex());

		Label sellerLabel = new Label("Comercial");
		sellerLabel.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		createSellerSuggestBox();

		sellerItemPanel.add(sellerLabel);
		sellerItemPanel.add(sellerSuggestBox);

		filterExpandPanel.add(sellerItemPanel);
		
		// Invoicing Group
		HTMLPanel invoincingGroupItemPanel = new HTMLPanel("");
		invoincingGroupItemPanel.addStyleName(AON.CSS.aonItemFlex());

		Label invoincingGroupLabel = new Label("G. Facturaci\u00f3n");
		invoincingGroupLabel.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		createInvoicingGroupSuggestBox();

		invoincingGroupItemPanel.add(invoincingGroupLabel);
		invoincingGroupItemPanel.add(invoicingGroupSuggestBox);

		filterExpandPanel.add(invoincingGroupItemPanel);
		
		// Invoicing Group
		HTMLPanel projectItemPanel = new HTMLPanel("");
		projectItemPanel.addStyleName(AON.CSS.aonItemFlex());

		Label projectLabel = new Label("Proyecto");
		projectLabel.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		createProjectSuggestBox();

		projectItemPanel.add(projectLabel);
		projectItemPanel.add(projectSuggestBox);

		filterExpandPanel.add(projectItemPanel);
		
		if (opt.getConfiguration().hasSegments()) {
			FlowPanel segmentPanel = new FlowPanel();
			InlineLabel segmentLabel = new InlineLabel("Segmento");
			segmentLabel.setStyleName(AON.CSS.aonBold());
			segmentLabel.addStyleName(AON.CSS.aonMarginRight());
			segmentPanel.add(segmentLabel);
			
			segmentListBox = new ListBox();
			
			
			segmentListBox.setWidth("120px");
			segmentListBox.addItem("-- Todos --", "");
			segmentListBox.setSelectedIndex(0);
			for (Segment ea : opt.getConfiguration().getSegments()) {
				segmentListBox.addItem(ea.getName(), AonNumberUtils.toString( ea.getId()));
			}
			segmentListBox.addChangeHandler(event -> onSearchFees());
			
			segmentPanel.add(segmentListBox);
			filterExpandPanel.add(segmentPanel);
		}
		
		
		filterLeftPanel.add(filterExpandPanel);
		
		HTMLPanel filterRightPanel = new HTMLPanel("");
		filterRightPanel.addStyleName(AON.CSS.aonFlexWrap());
		filterRightPanel.getElement().getStyle().setProperty("height", "100%");
		filterRightPanel.getElement().getStyle().setProperty("align-items", "flex-start");
		
		AonToolbarSmallButton expandFilterBtn = new AonToolbarSmallButton("Mas filtros", AON.CSS.aonIconTune());
		expandFilterBtn.addClickHandler(e -> {
			expandFilter = !expandFilter;
			if(expandFilter) filterExpandPanel.getElement().getStyle().clearDisplay();
			else filterExpandPanel.getElement().getStyle().setDisplay(Display.NONE);
		});
		filterRightPanel.add(expandFilterBtn);
		
		AonToolbarSmallButton resetBtn = new AonToolbarSmallButton("Borrar filtros", AON.CSS.aonIconClear());
		resetBtn.addClickHandler(e -> {
			resetFilter();
			feeList.clear();
			resetFeeTable();
			enableMoreData();
			offset.setValue(0);
			showInitialMessage();
			onSearchFees();
		});
		filterRightPanel.add(resetBtn);
		
		filterContentPanel.add(filterLeftPanel);
		filterContentPanel.add(filterRightPanel);

		container.add(filterContentPanel);
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

	private void createYearListBox(Consumer<ListBox> consumer) {
		SERVICE.getMinMaxCustomerFeeYear(options.getDomainName(), options.getDomain(), options.getUser(), new AsyncCallback<Map<Integer, Integer>>() {

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onSuccess(Map<Integer, Integer> minMaxYear) {
				Optional<Entry<Integer, Integer>> firstEntry = minMaxYear.entrySet().stream().findFirst();
				
				ListBox lb = new ListBox();
				lb.setHeight("2em");
				lb.getElement().getStyle().setProperty("padding", "0 5px");
				lb.addItem("-", "");
				
				if(firstEntry.isPresent()) {
					Integer minYear = firstEntry.get().getKey();
					Integer maxYear = firstEntry.get().getValue();
					
					while(maxYear >= minYear) {
						lb.addItem(maxYear.toString(), (maxYear - 1900) + "");
						maxYear--;
					}
				} 
				
				consumer.accept(lb);
			}
			
		});
	}
	
	private ListBox createPeriodicityListBox() {
		ListBox lb = new ListBox();
		lb.setHeight("2em");
		lb.getElement().getStyle().setProperty("padding", "0 5px");
		
		lb.addItem("-", "");
		lb.addItem("Sin periodo", "0");
		lb.addItem("Mensual", "1");
		lb.addItem("Bimensual", "2");
		lb.addItem("Trimestral", "3");
		lb.addItem("Cuatrimestral", "3");
		lb.addItem("Semestral", "6");
		lb.addItem("Anual", "12");
		lb.addChangeHandler(e -> onSearchFees());
		return lb;
	}
	
	private void createCustomerStatusListBox() {
		customerStatusListBox = new ListBox();
		customerStatusListBox.setHeight("2em");
		customerStatusListBox.getElement().getStyle().setProperty("padding", "0 5px");
		customerStatusListBox.addItem("-", "");
		customerStatusListBox.addItem("Activo", "0");
		customerStatusListBox.addItem("Inactivo", "1");
		customerStatusListBox.addItem("Bloqueado", "2");
		customerStatusListBox.addChangeHandler(e -> onSearchFees());
	}
	
	private void createCustomerSuggestBox() {
		customerSuggestBox = new SuggestBox();
		customerSuggestBox.setWidth("225px");
		customerSuggestBox.setHeight("2em");
		customerSuggestBox.getElement().getStyle().setProperty("padding", "0 5px");
		customerSuggestBox.setAutoSelectEnabled(false);
		customerSuggestBox.getElement().setPropertyString("placeholder", "Clientes: busque por nombre, nif o alias");
		
		customerSuggestBox.addSelectionHandler(e -> {
			customerSuggestBox.hideSuggestionList();
			onSearchFees();
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

	private void createConceptSuggestBox() {
		conceptSuggestBox = new SuggestBox();
		conceptSuggestBox.setWidth("240px");
		conceptSuggestBox.setHeight("2em");
		conceptSuggestBox.getElement().getStyle().setProperty("padding", "0 5px");
		conceptSuggestBox.setAutoSelectEnabled(false);
		conceptSuggestBox.getElement().setPropertyString("placeholder", "Producto: busque por c\u00f3digo o descripci\u00f3n");
		
		conceptSuggestBox.addSelectionHandler(e -> {
			conceptSuggestBox.hideSuggestionList();
			onSearchFees();
		});
		
		conceptSuggestBox.addKeyUpHandler(e -> {
			String productQuery = conceptSuggestBox.getValue();
			if(e.isControlKeyDown() && e.getNativeKeyCode() == 32) {
				conceptSuggestBox.setValue("");
				productQuery = null;
				getProductsSuggestion(productQuery);
			} else if(AonStringUtils.isNotBlank(productQuery) && productQuery.length() > 3)
				getProductsSuggestion(productQuery);
		});
	}
	
	private void getProductsSuggestion(String productQuery) {
		SERVICE.getProductsSuggestion(options.getDomainName(), options.getDomain(), options.getUser(), productQuery, new AsyncCallback<Map<String, OldItem>>() {
			
			@Override
			public void onSuccess(Map<String, OldItem> productSuggestionsDB) {
				productSuggestions = productSuggestionsDB;
				
				MultiWordSuggestOracle orclSb = (MultiWordSuggestOracle) conceptSuggestBox.getSuggestOracle();
				orclSb.clear();
				orclSb.addAll(productSuggestions.keySet());
				orclSb.setDefaultSuggestionsFromText(productSuggestions.keySet());
				conceptSuggestBox.showSuggestionList();
			}
			
			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub	
			}
			
		});
	}
	
	private void createWorkplaceSuggestBox() {
		workplaceSuggestBox = new SuggestBox();
		workplaceSuggestBox.setWidth("240px");
		workplaceSuggestBox.setHeight("2em");
		workplaceSuggestBox.getElement().getStyle().setProperty("padding", "0 5px");
		workplaceSuggestBox.setAutoSelectEnabled(false);
		workplaceSuggestBox.getElement().setPropertyString("placeholder", "C. Trabajo: busque descripci\u00f3n");
		
		workplaceSuggestBox.addSelectionHandler(e -> {
			workplaceSuggestBox.hideSuggestionList();
			onSearchFees();
		});
		
		workplaceSuggestBox.addKeyUpHandler(e -> {
			String pworkplaceQuery = workplaceSuggestBox.getValue();
			if(e.isControlKeyDown() && e.getNativeKeyCode() == 32) {
				workplaceSuggestBox.setValue("");
				pworkplaceQuery = null;
				getWorkplaceSuggestion(pworkplaceQuery);
			} else if(AonStringUtils.isNotBlank(pworkplaceQuery) && pworkplaceQuery.length() > 3)
				getWorkplaceSuggestion(pworkplaceQuery);
		});
	}
	
	private void getWorkplaceSuggestion(String workplaceQuery) {
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
	
	private void createSellerSuggestBox() {
		sellerSuggestBox = new SuggestBox();
		sellerSuggestBox.setWidth("240px");
		sellerSuggestBox.setHeight("2em");
		sellerSuggestBox.getElement().getStyle().setProperty("padding", "0 5px");
		sellerSuggestBox.setAutoSelectEnabled(false);
		sellerSuggestBox.getElement().setPropertyString("placeholder", "Comercial: busque descripci\u00f3n");
		
		sellerSuggestBox.addSelectionHandler(e -> {
			sellerSuggestBox.hideSuggestionList();
			onSearchFees();
		});
		
		sellerSuggestBox.addKeyUpHandler(e -> {
			String sellerQuery = sellerSuggestBox.getValue();
			if(e.isControlKeyDown() && e.getNativeKeyCode() == 32) {
				sellerSuggestBox.setValue("");
				sellerQuery = null;
				getSellerSuggestion(sellerQuery);
			} else if(AonStringUtils.isNotBlank(sellerQuery) && sellerQuery.length() > 3)
				getSellerSuggestion(sellerQuery);
		});
	}
	
	private void getSellerSuggestion(String sellerQuery) {
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
	
	private void createInvoicingGroupSuggestBox() {
		invoicingGroupSuggestBox = new SuggestBox();
		invoicingGroupSuggestBox.setWidth("240px");
		invoicingGroupSuggestBox.setHeight("2em");
		invoicingGroupSuggestBox.getElement().getStyle().setProperty("padding", "0 5px");
		invoicingGroupSuggestBox.setAutoSelectEnabled(false);
		invoicingGroupSuggestBox.getElement().setPropertyString("placeholder", "G. Facturaci\u00f3n: busque descripci\u00f3n");
		
		invoicingGroupSuggestBox.addSelectionHandler(e -> {
			invoicingGroupSuggestBox.hideSuggestionList();
			onSearchFees();
		});
		
		invoicingGroupSuggestBox.addKeyUpHandler(e -> {
			String invoicingGroupQuery = invoicingGroupSuggestBox.getValue();
			if(e.isControlKeyDown() && e.getNativeKeyCode() == 32) {
				invoicingGroupSuggestBox.setValue("");
				invoicingGroupQuery = null;
				getInvoicingGroupQuerySuggestion(invoicingGroupQuery);
			} else if(AonStringUtils.isNotBlank(invoicingGroupQuery) && invoicingGroupQuery.length() > 3)
				getInvoicingGroupQuerySuggestion(invoicingGroupQuery);
		});
	}
	
	private void getInvoicingGroupQuerySuggestion(String invoicingGroupQuery) {
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
	
	private void createProjectSuggestBox() {
		projectSuggestBox = new SuggestBox();
		projectSuggestBox.setWidth("240px");
		projectSuggestBox.setHeight("2em");
		projectSuggestBox.getElement().getStyle().setProperty("padding", "0 5px");
		projectSuggestBox.setAutoSelectEnabled(false);
		projectSuggestBox.getElement().setPropertyString("placeholder", "Proyecto: busque descripci\u00f3n");
		
		projectSuggestBox.addSelectionHandler(e -> {
			projectSuggestBox.hideSuggestionList();
			onSearchFees();
		});
		
		projectSuggestBox.addKeyUpHandler(e -> {
			String projectQuery = projectSuggestBox.getValue();
			if(e.isControlKeyDown() && e.getNativeKeyCode() == 32) {
				projectSuggestBox.setValue("");
				projectQuery = null;
				getProjectQuerySuggestion(projectQuery);
			} else if(AonStringUtils.isNotBlank(projectQuery) && projectQuery.length() > 3)
				getProjectQuerySuggestion(projectQuery);
		});
	}
	
	private void getProjectQuerySuggestion(String projectQuery) {
		SERVICE.getProjectsSuggestion(options.getDomainName(), options.getDomain(), options.getUser(), null, projectQuery, new AsyncCallback<Map<String, Project>>() {
			
			@Override
			public void onSuccess(Map<String, Project> projectSuggestionsDB) {
				projectSuggestions = projectSuggestionsDB;
				
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

	private void resetFilter() {
		monthListBox.setSelectedIndex(0);
		if (null != yearListBox) yearListBox.setSelectedIndex(0);
		customerSuggestBox.setValue("");
		customerStatusListBox.setSelectedIndex(0);
		conceptSuggestBox.setValue("");
		priceTextBox.setValue("");
		discountTextBox.setValue("");
		
		startDateBox.setValue(null);
		endDateBox.setValue(null);
		quantityTextBox.setValue("");
		
		if(null != params) {
			params.setMonth(null);
			params.setYear(null);
			params.setCustomer(null);
			params.setProductCode(null);
			params.setCustomerStatus(null);
			params.setPrice(null);
			params.setDiscount(null);
			
			params.setStartDate(null);
			params.setEndDate(null);
			params.setQuantity(null);
			params.setWorkplace(null);
			params.setSeller(null);
			params.setInvoicingGroup(null);
			params.setProject(null);
		}
	}
	
	private void onSearchFees() {
		if(checkBillingDate()) return;
		enableMoreData();
		offset.setValue(0);
		searchFees();
		feeList.clear();
		resetFeeTable();
	}

	private void searchFees() {
		if (!isMoreData() || checkBillingDate()) return;
		
		createParams();
		
		SERVICE.getCustomerFeeList(options.getDomainName(), options.getDomain(), options.getUser(), params,
			new AsyncCallback<LinkedList<Fee>>() {

				@Override
				public void onFailure(Throwable caught) {
					AonMessagePanel.showError(messagePanel, "Error cargando panel de facturaci\u00f3n: " + caught.getMessage());
				}

				@Override
				public void onSuccess(LinkedList<Fee> feeListDB) {
					if(params.getOffset() == 0 && (feeListDB == null || feeListDB.isEmpty()))
						showEmptyFeeMessage();
					else if(feeListDB == null || feeListDB.isEmpty()) {
						disableMoreData();
						if(feeList.isEmpty()) showEmptyFeeMessage();
					} else {
						if(offset.getValue() == 0) selectionModel.clear();
						showFeeTable();
						feeListDB.forEach( fee -> paintRow(fee));
						feeList.addAll(feeListDB);
						enableMoreData();
						offset.setValue(offset.getValue() + feeListDB.size());
					}
					
					enableSearch();
					setHasChange(false);
				}
			});
		
	}
	
	private boolean checkBillingDate() {
		Integer month = AonStringUtils.isBlank(monthListBox.getSelectedValue()) ? null : Integer.parseInt(monthListBox.getSelectedValue());
		Integer year = AonStringUtils.isBlank(yearListBox.getSelectedValue()) ? null : Integer.parseInt(yearListBox.getSelectedValue());

		return month != null && year == null;
	}

	private void createParams() {
		if(null == params) params = new CustomerFeeParams();
		params.setDomain(options.getDomain());
		params.setMonth(AonStringUtils.isBlank(monthListBox.getSelectedValue()) ? null : Integer.parseInt(monthListBox.getSelectedValue()));
		params.setYear(AonStringUtils.isBlank(yearListBox.getSelectedValue()) ? null : Integer.parseInt(yearListBox.getSelectedValue()));
		params.setPeriodicity(AonStringUtils.isBlank(periocityListBox.getSelectedValue()) ? null : Byte.parseByte(periocityListBox.getSelectedValue()));
		params.setCustomer(null != customerSuggestions.get(customerSuggestBox.getValue()) ? customerSuggestions.get(customerSuggestBox.getValue()).getName() : null);
		params.setCustomerStatus(AonStringUtils.isBlank(customerStatusListBox.getSelectedValue()) ? null : Byte.parseByte(customerStatusListBox.getSelectedValue()));
		params.setProductCode(null != productSuggestions.get(conceptSuggestBox.getValue()) ? productSuggestions.get(conceptSuggestBox.getValue()).getProduct().getCode() : null);
		params.setPrice(priceTextBox.getValue());
		params.setDiscount(discountTextBox.getValue());
		
		params.setStartDate(startDateBox.getValue());
		params.setEndDate(endDateBox.getValue());
		params.setQuantity(quantityTextBox.getValue());
		params.setWorkplace(null != workplaceSuggestions.get(workplaceSuggestBox.getValue()) ? workplaceSuggestions.get(workplaceSuggestBox.getValue()).getDescription() : null);
		params.setSeller(null != sellerSuggestions.get(sellerSuggestBox.getValue()) ? sellerSuggestions.get(sellerSuggestBox.getValue()).getName() : null);
		params.setInvoicingGroup(null != invoicingGroupSuggestions.get(invoicingGroupSuggestBox.getValue()) ? invoicingGroupSuggestions.get(invoicingGroupSuggestBox.getValue()).getDescription() : null);
		params.setProject(null != projectSuggestions.get(projectSuggestBox.getValue()) ? projectSuggestions.get(projectSuggestBox.getValue()).getId() : null);
		
		if (segmentListBox != null && segmentListBox.getSelectedIndex() > 0) {
			params.setSegment( AonNumberUtils.toInteger( segmentListBox.getSelectedValue()));
		}

		
		params.setLimit(limit);
		params.setOffset(offset.getValue());
	}

	private Date createBillingDate() {
		String monthStr = monthListBox.getSelectedValue();
		String yearStr = null == yearListBox ? "" : yearListBox.getSelectedValue();
		
		if(AonStringUtils.isBlank(yearStr)) return null;
		
		if(AonStringUtils.isBlank(monthStr)) return new Date(Integer.parseInt(yearStr), 0, 1);
		else return new Date(Integer.parseInt(yearStr), Integer.parseInt(monthStr), 1);
	}

	private void initializeDeckPanel() {
		deckPanel.clear();
		createInitialMessage();
		createEmptyFeeMessage();
		createFeeTable();
		showInitialMessage();
	}
	
	private void showInitialMessage() {
		deckPanel.showWidget(0);
	}
	
	private void showEmptyFeeMessage() {
		deckPanel.showWidget(1);
	}
	
	private void showFeeTable() {
		deckPanel.showWidget(2);
	}	
	
	private void createInitialMessage() {
		HTMLPanel emptyPanel = new HTMLPanel("");
		emptyPanel.addStyleName(AON.CSS.aonDisplayFlexCenter());
		Label emptyMessage = new Label("RELLENE LA BUSQUEDA PARA CARGAR EL PANEL DE FACTURACION");
		emptyMessage.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		emptyMessage.getElement().getStyle().setMarginTop(1, Unit.EM);
		emptyPanel.add(emptyMessage);
		deckPanel.add(emptyPanel);
	}

	private void createEmptyFeeMessage() {
		HTMLPanel emptyFeePanel = new HTMLPanel("");
		emptyFeePanel.addStyleName(AON.CSS.aonDisplayFlexCenter());
		Label emptyMessage = new Label("NO EXISTEN CUOTAS DE CLIENTES PARA ESTE PERIODO/FILTRO");
		emptyMessage.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		emptyMessage.getElement().getStyle().setMarginTop(1, Unit.EM);
		emptyFeePanel.add(emptyMessage);
		deckPanel.add(emptyFeePanel);
	}
	
	private void createFeeTable() {
		resetFeeTable();
		deckPanel.add(scrollPanel);
	}
	
	private void resetFeeTable () {
		scrollPanel.clear();
		createFeeHeader();
		setColumnWidth();
	}

	private void createFeeHeader() {
		feeTable = new Grid(0, 12);
		feeTable.clear();
		feeTable.setWidth("100%");

		int row = feeTable.insertRow(feeTable.getRowCount());

		CheckBox select = new CheckBox();
		select.addValueChangeHandler(e -> {
			selectionModel.forEach((checkBox, fee) -> checkBox.setValue(e.getValue()));
			addValueButton.setEnabled(e.getValue());
			deleteFeeButton.setEnabled(e.getValue());
			exportButton.setEnabled(e.getValue());
		});

		Label customer = new Label("CLIENTE");
		Label status = new Label("ESTADO");
		Label concept = new Label("CONCEPTO");
		Label period = new Label("PERIODO");
		Label quantity = new Label("CANTIDAD");
		Label price = new Label("PRECIO");
		Label discount = new Label("DESCUENTO");
		Label billingDate = new Label("F. FACTURACI\u00f3N");
		Label startDate = new Label("F. DESDE");
		startDate.setTitle("F. DESDE FACTURACI\u00f3N");
		Label endDate = new Label("F. HASTA");
		endDate.setTitle("F. HASTA FACTURACI\u00f3N");
		Label info = new Label("");

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
		info.addStyleName(AON.CSS.aonHeaderTable());

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
		feeTable.setWidget(row, 11, info);

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
		feeTable.getCellFormatter().addStyleName(row, 11, AON.CSS.aonHeaderSticky());

		scrollPanel.add(feeTable);
	}
	
	private void setColumnWidth() {
		feeTable.getColumnFormatter().getElement(0).getStyle().setWidth(2, Unit.PCT);
		feeTable.getColumnFormatter().getElement(1).getStyle().setWidth(20, Unit.PCT);
		feeTable.getColumnFormatter().getElement(2).getStyle().setWidth(8, Unit.PCT);
		feeTable.getColumnFormatter().getElement(3).getStyle().setWidth(19, Unit.PCT);
		feeTable.getColumnFormatter().getElement(4).getStyle().setWidth(8, Unit.PCT);
		feeTable.getColumnFormatter().getElement(5).getStyle().setWidth(5, Unit.PCT);
		feeTable.getColumnFormatter().getElement(6).getStyle().setWidth(8, Unit.PCT);
		feeTable.getColumnFormatter().getElement(7).getStyle().setWidth(8, Unit.PCT);
		feeTable.getColumnFormatter().getElement(8).getStyle().setWidth(9, Unit.PCT);
		feeTable.getColumnFormatter().getElement(9).getStyle().setWidth(9, Unit.PCT);
		feeTable.getColumnFormatter().getElement(10).getStyle().setWidth(9, Unit.PCT);
		feeTable.getColumnFormatter().getElement(11).getStyle().setWidth(3, Unit.PCT);
	}
	
	private void disableMoreData() {
		moreData.setValue(-1);
	}
	
	private void enableMoreData() {
		moreData.setValue(0);
	}
	
	private boolean isMoreData() {
		return (moreData.getValue() == 0 );
	}
	
	private void enableSearch() {
		searchEnabled.setValue(0);
	}
	
	private boolean isSearchEnabled() {
		return (searchEnabled.getValue() == 0 );
	}
	
	private void disableSearch() {
		searchEnabled.setValue(-1);
	}

	private void paintRow(Fee fee) {
		int row = feeTable.insertRow(feeTable.getRowCount());

		CheckBox select = new CheckBox();
		select.addValueChangeHandler(e -> {
			Optional<CheckBox> checked = selectionModel.keySet().stream().filter(cb -> cb.getValue()).findAny();
			addValueButton.setEnabled(checked.isPresent());
			deleteFeeButton.setEnabled(checked.isPresent());
			exportButton.setEnabled(checked.isPresent());
		});
		
		Label customerLabel = new Label(fee.getCustomer().getName());

		ListBox statusListBox = createStatusListBox(fee, row);
		statusListBox.setWidth("85px");
		setInputStyle(statusListBox);
		setSelectedValueLB(statusListBox, fee.getCustomer().getStatus().getDescription());

		AutoResizeTextArea conceptTextArea = new AutoResizeTextArea(fee, row);
		conceptTextArea.setWidth("95%");
		conceptTextArea.setValue(fee.getDescription());

		ListBox periodListBox = createPeriodListBox(fee, row);
		periodListBox.setWidth("100px");
		setInputStyle(periodListBox);
		setSelectedValueLB(periodListBox, fee.getPeriod().getValue().toString());

		TextBox quantityTextBox = new TextBox();
		quantityTextBox.setWidth("50px");
		setInputStyle(quantityTextBox);
		quantityTextBox.setValue(null == fee.getQuantity() ? "" : fee.getQuantity().toString());
		quantityTextBox.addValueChangeHandler(e -> {
			fee.setQuantity(Double.parseDouble(e.getValue()));
			setFeeModify(fee, row);
		});

		TextBox priceTextBox = new TextBox();
		priceTextBox.setWidth("80px");
		setInputStyle(priceTextBox);
		priceTextBox.setValue(null == fee.getPrice() ? "" : fee.getPrice().toString());
		priceTextBox.addValueChangeHandler(e -> {
			fee.setPrice(Double.parseDouble(e.getValue()));
			setFeeModify(fee, row);
		});

		TextBox discountTextBox = new TextBox();
		discountTextBox.setWidth("80px");
		setInputStyle(discountTextBox);
		discountTextBox.setValue(null == fee.getDiscountExpr() ? "" : fee.getDiscountExpr());
		discountTextBox.addValueChangeHandler(e -> {
			String expression = e.getValue();
			String result = e.getValue();
			
			try {
				if(AonStringUtils.contains(expression, ",") && AonStringUtils.contains(expression, "."))
					expression= expression.replaceAll("\\.", "");
				expression= expression.replaceAll(",", ".");
				
				Double expressionValue = evalExpression(expression);
				result = null == expressionValue ? "" : expressionValue.toString();
				fee.setDiscount(Double.parseDouble(result));
			} catch (Exception ex) {
				AonMessagePanel.showWarning(messagePanel, new HTML("La expresi\u00f3n de <b>Descuento</b> que ha introducido no es correcta"));
			}
			
			fee.setDiscountExpr(expression);
			setFeeModify(fee, row);
			
		});
		
		AonDateBox billingDateBox = new AonDateBox();
		billingDateBox.getElement().getStyle().setTextAlign(TextAlign.CENTER);
		billingDateBox.addStyleName("gwt-TextBox");
		setInputStyle(billingDateBox);
		billingDateBox.setValue(fee.getBillingDate());
		billingDateBox.addValueChangeHandler(e -> {
			Date date = e.getValue() == null ? null : DateUtils.getFirstDayOfMonth(e.getValue());
			billingDateBox.setValue(date);
			fee.setBillingDate(date);
			setFeeModify(fee, row);
		});

		AonDateBox startDateBox = new AonDateBox();
		startDateBox.getElement().getStyle().setTextAlign(TextAlign.CENTER);
		startDateBox.addStyleName("gwt-TextBox");
		setInputStyle(startDateBox);
		startDateBox.setValue(fee.getStartDate());
		startDateBox.addValueChangeHandler(e -> {
			fee.setStartDate(e.getValue());
			setFeeModify(fee, row);
		});

		AonDateBox endDateBox = new AonDateBox();
		endDateBox.getElement().getStyle().setTextAlign(TextAlign.CENTER);
		endDateBox.addStyleName("gwt-TextBox");
		setInputStyle(endDateBox);
		endDateBox.setValue(fee.getEndDate());
		endDateBox.addValueChangeHandler(e -> {
			fee.setEndDate(e.getValue());
			setFeeModify(fee, row);
		});
		
		AonToolbarSmallButton infoBtn = new AonToolbarSmallButton("", AON.CSS.aonIconInfo());
		infoBtn.setTitle(createFeeInfo(fee));

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
		checkRowAndModify(feeTable, row, fee, infoBtn);

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
		feeTable.setWidget(row, 11, infoBtn);

		feeTable.getCellFormatter().getElement(row, 2).getStyle().setTextAlign(TextAlign.CENTER);
		feeTable.getCellFormatter().getElement(row, 4).getStyle().setTextAlign(TextAlign.CENTER);
		feeTable.getCellFormatter().getElement(row, 5).getStyle().setTextAlign(TextAlign.CENTER);
		feeTable.getCellFormatter().getElement(row, 6).getStyle().setTextAlign(TextAlign.CENTER);
		feeTable.getCellFormatter().getElement(row, 7).getStyle().setTextAlign(TextAlign.CENTER);
		feeTable.getCellFormatter().getElement(row, 8).getStyle().setTextAlign(TextAlign.CENTER);
		feeTable.getCellFormatter().getElement(row, 9).getStyle().setTextAlign(TextAlign.CENTER);
		feeTable.getCellFormatter().getElement(row, 10).getStyle().setTextAlign(TextAlign.CENTER);
		feeTable.getCellFormatter().getElement(row, 11).getStyle().setTextAlign(TextAlign.CENTER);

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
			feeTable.getCellFormatter().addStyleName(row, 11, AON.CSS.aonOddTableRow());
		}

		feeTable.getRowFormatter().getElement(row).getStyle().setHeight(25.00, Unit.PX);

		selectionModel.put(select, fee);
	}

	private ListBox createStatusListBox(Fee fee, int row) {
		ListBox lb = new ListBox();
		lb.addItem("Activo", "Activo");
		lb.addItem("Inactivo", "Inactivo");
		lb.addItem("Bloqueado", "Bloqueado");
		lb.addChangeHandler(e -> {
			fee.getCustomer().setStatus(RegistryStatus.safeValueOf(lb.getSelectedValue()));
			setFeeModify(fee, row);
			
		});
		return lb;
	}

	private ListBox createPeriodListBox(Fee fee, int row) {
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
			setFeeModify(fee, row);
		});
		return lb;
	}

	private String createFeeInfo(Fee fee) {
		String tooltip = "";
		
		tooltip += "Seguridad: " + fee.getSecurityLevel().getName();
		tooltip += "\nC. Trabajo: " + fee.getWorkplace().getDescription();
		
		if(null != fee.getSeller() && AonStringUtils.isNotBlank(fee.getSeller().getName())) tooltip += "\nComercial: " + fee.getSeller().getName();
		if(null != fee.getInvoicingGroup() && AonStringUtils.isNotBlank(fee.getInvoicingGroup().getDescription())) tooltip += "\nG. Facturac\u00f3n: " + fee.getInvoicingGroup().getDescription();
		if(null != fee.getProject() && AonStringUtils.isNotBlank(fee.getProject().getName())) tooltip += "\nProyecto: " + fee.getProject().getName();
		
		return tooltip;
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
			grid.getCellFormatter().addStyleName(row, 11, AON.CSS.aonModifyTableRow());
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
			grid.getCellFormatter().removeStyleName(row, 11, AON.CSS.aonModifyTableRow());
		}

	}
	
	private void setModifyColor(int row) {
		feeTable.getCellFormatter().addStyleName(row, 0, AON.CSS.aonModifyTableRow());
		feeTable.getCellFormatter().addStyleName(row, 1, AON.CSS.aonModifyTableRow());
		feeTable.getCellFormatter().addStyleName(row, 2, AON.CSS.aonModifyTableRow());
		feeTable.getCellFormatter().addStyleName(row, 3, AON.CSS.aonModifyTableRow());
		feeTable.getCellFormatter().addStyleName(row, 4, AON.CSS.aonModifyTableRow());
		feeTable.getCellFormatter().addStyleName(row, 5, AON.CSS.aonModifyTableRow());
		feeTable.getCellFormatter().addStyleName(row, 6, AON.CSS.aonModifyTableRow());
		feeTable.getCellFormatter().addStyleName(row, 7, AON.CSS.aonModifyTableRow());
		feeTable.getCellFormatter().addStyleName(row, 8, AON.CSS.aonModifyTableRow());
		feeTable.getCellFormatter().addStyleName(row, 9, AON.CSS.aonModifyTableRow());
		feeTable.getCellFormatter().addStyleName(row, 10, AON.CSS.aonModifyTableRow());
		feeTable.getCellFormatter().addStyleName(row, 11, AON.CSS.aonModifyTableRow());
		
		feeTable.getWidget(row, 0).addStyleName(AON.CSS.aonModifyTableRow());
		feeTable.getWidget(row, 1).addStyleName(AON.CSS.aonModifyTableRow());
		feeTable.getWidget(row, 2).addStyleName(AON.CSS.aonModifyTableRow());
		feeTable.getWidget(row, 3).addStyleName(AON.CSS.aonModifyTableRow());
		feeTable.getWidget(row, 4).addStyleName(AON.CSS.aonModifyTableRow());
		feeTable.getWidget(row, 5).addStyleName(AON.CSS.aonModifyTableRow());
		feeTable.getWidget(row, 6).addStyleName(AON.CSS.aonModifyTableRow());
		feeTable.getWidget(row, 7).addStyleName(AON.CSS.aonModifyTableRow());
		feeTable.getWidget(row, 8).addStyleName(AON.CSS.aonModifyTableRow());
		feeTable.getWidget(row, 9).addStyleName(AON.CSS.aonModifyTableRow());
		feeTable.getWidget(row, 10).addStyleName(AON.CSS.aonModifyTableRow());
		feeTable.getWidget(row, 11).addStyleName(AON.CSS.aonModifyTableRow());
	}
	
	private void setInputStyle(Widget widget) {
		widget.setHeight("2em");
		widget.getElement().getStyle().setProperty("padding", "0 5px");
	}

	// -------------------------------- TOOLBAR
	
	private void createToolbar() {
		toolbar = new AonToolbar("Panel Facturaci\u00f3n de Cuotas");

		saveButton = new AonToolbarButton(AON.MSG.saveAction(), AON.CSS.aonIconSave());
		saveButton.addClickHandler(e -> {
			AonMessagePanel.showLoading(messagePanel, "Guardando panel facturaci\u00f3n ...");
			setHasChange(false);
			SERVICE.saveCustomerFeeList(options.getDomainName(), options.getDomain(), options.getUser(), feeList,
					new AsyncCallback<Integer>() {

						@Override
						public void onFailure(Throwable caught) {
							AonMessagePanel.showError(messagePanel, "Error guardando panel de facturaci\u00f3n: " + caught.getMessage());
						}

						@Override
						public void onSuccess(Integer updates) {
							AonMessagePanel.showSuccess(messagePanel, "Se han actualizado " + updates + " cuotas correctamente");
							addValueButton.setEnabled(false);
							deleteFeeButton.setEnabled(false);
							exportButton.setEnabled(false);
							selectionModel.clear();
							setHasChange(false);
							feeList.clear();
							resetFeeTable();
							enableMoreData();
							offset.setValue(0);
							searchFees();
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
					addValueButton.setEnabled(false);
					deleteFeeButton.setEnabled(false);
					exportButton.setEnabled(false);
					selectionModel.clear();
					setHasChange(false);
					feeList.clear();
					resetFeeTable();
					enableMoreData();
					offset.setValue(0);
					searchFees();
					AonMessagePanel.hideMessage(messagePanel);
				}
			});
		});
		
		createButton = new AonToolbarButton(AON.MSG.newAction(), AON.CSS.aonIconAdd());
		createButton.addClickHandler(e -> {
			new CustomerFeeDialog(options) {
				
				@Override
				protected void onCreate(Fee fee) {
					SERVICE.createCustomerFeeList(options.getDomainName(), options.getDomain(), options.getUser(), fee, new AsyncCallback<Void>() {
						
						@Override
						public void onSuccess(Void result) {
							AonMessagePanel.showSuccess(messagePanel, "Se ha creado la cuota correctamente");
							addValueButton.setEnabled(false);
							exportButton.setEnabled(false);
							selectionModel.clear();
							setHasChange(false);
							feeList.clear();
							resetFeeTable();
							enableMoreData();
							offset.setValue(0);
							searchFees();
						}
						
						@Override
						public void onFailure(Throwable caught) {
							AonMessagePanel.showError(messagePanel, "Error creando cuota: " + caught.getMessage());
						}
					});
				}
				
				@Override
				protected void onAccept(Optional<OldItem> item, Optional<Double> price, Optional<String> discountExpr,
						Optional<Date> startDate, Optional<Date> endDate, Optional<Date> billingDate) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				protected void onAccept(Fee fee) {
					// TODO Auto-generated method stub
					
				}
			};
		});
		
		addValueButton = new AonToolbarButton("Editar Cuota", AON.CSS.aonIconEdit());
		addValueButton.setEnabled(false);
		addValueButton.addClickHandler(e -> {
			long selectedItems = selectionModel.keySet().stream().filter(cb -> cb.getValue()).count();
			if(selectedItems == 1) {
				Optional<Fee> selectedFee = selectionModel.entrySet().stream().filter(entry -> entry.getKey().getValue()).map(entry -> entry.getValue()).findFirst();
				if(selectedFee.isPresent())
					new CustomerFeeDialog(selectedFee.get(), options) {
						
						@Override
						protected void onAccept(Fee fee) {
							LinkedList<Fee> fees = new LinkedList<Fee>();
							fee.setModify(true);
							fees.add(fee);
							
							SERVICE.saveCustomerFeeList(options.getDomainName(), options.getDomain(), options.getUser(), fees,
									new AsyncCallback<Integer>() {

										@Override
										public void onFailure(Throwable caught) {
											AonMessagePanel.showError(messagePanel, "Error guardando panel de facturaci\u00f3n: " + caught.getMessage());
										}

										@Override
										public void onSuccess(Integer updates) {
											AonMessagePanel.showSuccess(messagePanel, "Se han actualizado " + updates + " cuotas correctamente");
											selectionModel.clear();
											addValueButton.setEnabled(false);
											exportButton.setEnabled(false);
											setHasChange(false);
											feeList.clear();
											resetFeeTable();
											enableMoreData();
											offset.setValue(0);
											searchFees();
										}
									});
						}

						@Override
						protected void onAccept(Optional<OldItem> item, Optional<Double> price, Optional<String> discountExpr, Optional<Date> startDate, Optional<Date> endDate, Optional<Date> billingDate) {}

						@Override
						protected void onCreate(Fee fee) {}
						
					};
			} else {
				new CustomerFeeDialog(productSuggestions.get(conceptSuggestBox.getValue()), options) {
					
					@Override
					protected void onAccept(Fee fee) {}
					
					@Override
					protected void onCreate(Fee fee) {}

					@Override
					protected void onAccept(Optional<OldItem> item, Optional<Double> price, Optional<String> discountExpr, Optional<Date> startDate, Optional<Date> endDate, Optional<Date> billingDate) {
						Fee fee = new Fee();
						
						if(item.isPresent()) fee.setItem(item.get());
						if(price.isPresent()) fee.setPrice(price.get());
						if(discountExpr.isPresent()) fee.setDiscountExpr(discountExpr.get());
						if(startDate.isPresent()) fee.setStartDate(startDate.get());
						if(endDate.isPresent()) fee.setEndDate(endDate.get());
						if(billingDate.isPresent()) fee.setBillingDate(billingDate.get());
						
						if(selectedItems == feeList.size()) {
							// Cambio masivo (todo seleccionado)
							offset.setValue(0);
							createParams();
							
							SERVICE.getCustomerProductsUpdates(options.getDomainName(), options.getDomain(), options.getUser(), params, new AsyncCallback<Map<Integer,Integer>>() {
								
								@Override
								public void onSuccess(Map<Integer, Integer> result) {
									Optional<Entry<Integer, Integer>> resultEntry = result.entrySet().stream().findFirst();
									AonDialog dialog = new AonDialog("Edici\u00f3n Cuotas",
											new HTML("El cambio afectara a <b>" + resultEntry.get().getKey() + " clientes</b> y <b>" + resultEntry.get().getValue() + " cuotas</b>.<br>\u00bfEsta seguro que desea proceder a la actualizacion\u003f"));
									
									dialog.confirm(new AonAcceptDialogCallback() {

										@Override
										public void onCancel() {
											// Nothing to do here
										}

										@Override
										public void onAccept() {
											AonMessagePanel.showLoading(messagePanel, "Actualizando datos de cuotas masivamente ...");
											SERVICE.saveMassiveCustomerFee(options.getDomainName(), options.getDomain(), options.getUser(), fee, params,
												new AsyncCallback<Integer>() {

													@Override
													public void onFailure(Throwable caught) {
														AonMessagePanel.showError(messagePanel, "Error guardando panel de facturaci\u00f3n: " + caught.getMessage());
													}

													@Override
													public void onSuccess(Integer updates) {
														AonMessagePanel.showSuccess(messagePanel, "Se han actualizado " + updates + " cuotas correctamente");
														addValueButton.setEnabled(false);
														exportButton.setEnabled(false);
														selectionModel.clear();
														setHasChange(false);
														feeList.clear();
														resetFeeTable();
														enableMoreData();
														offset.setValue(0);
														searchFees();
													}
												});
										}
									});
								}
								
								@Override
								public void onFailure(Throwable caught) {
									// TODO Auto-generated method stub
									
								}
							});
						} else {
							// Solo cuotas seleccionadas
							LinkedList<Fee> selectedFees = selectionModel.entrySet().stream().filter(e -> e.getKey().getValue()).map(e -> e.getValue()).collect(Collectors.toCollection(LinkedList::new));
							AonDialog dialog = new AonDialog("Edici\u00f3n Cuotas",
									new HTML("El cambio afectara a <b>" + selectedFees.size() + " cuotas</b>.<br>\u00bfEsta seguro que desea proceder a la actualizacion\u003f"));
							
							dialog.confirm(new AonAcceptDialogCallback() {

								@Override
								public void onCancel() {
									// Nothing to do here
								}

								@Override
								public void onAccept() {
									AonMessagePanel.showLoading(messagePanel, "Actualizando datos de cuotas masivamente ...");
									selectedFees.forEach(feeIt -> {
										feeIt.setModify(true);
										
										if(null != fee.getPeriod()) feeIt.setPeriod(fee.getPeriod());
										if(null != fee.getQuantity()) feeIt.setQuantity(fee.getQuantity());
										if(null != fee.getPrice()) feeIt.setPrice(fee.getPrice());
										if(null != fee.getDiscountExpr()) feeIt.setDiscountExpr(fee.getDiscountExpr());
										
										if(null != fee.getBillingDate()) feeIt.setBillingDate(fee.getBillingDate());
										if(null != fee.getStartDate()) feeIt.setStartDate(fee.getStartDate());
										if(null != fee.getEndDate()) feeIt.setEndDate(fee.getEndDate());
										
										SERVICE.saveCustomerFeeList(options.getDomainName(), options.getDomain(), options.getUser(), selectedFees,
												new AsyncCallback<Integer>() {

													@Override
													public void onFailure(Throwable caught) {
														AonMessagePanel.showError(messagePanel, "Error guardando panel de facturaci\u00f3n: " + caught.getMessage());
													}

													@Override
													public void onSuccess(Integer updates) {
														AonMessagePanel.showSuccess(messagePanel, "Se han actualizado " + updates + " cuotas correctamente");
														selectionModel.clear();
														addValueButton.setEnabled(false);
														exportButton.setEnabled(false);
														setHasChange(false);
														feeList.clear();
														resetFeeTable();
														enableMoreData();
														offset.setValue(0);
														searchFees();
													}
												});
									});
								}
							});
						} 
					}
					
				};
			}
		});
		
		deleteFeeButton = new AonToolbarButton("Eliminar", AON.CSS.aonIconDelete());
		deleteFeeButton.setEnabled(false);
		deleteFeeButton.addClickHandler(event -> {
			LinkedList<Fee> selectedFees = selectionModel.entrySet().stream().filter(e -> e.getKey().getValue()).map(e -> e.getValue()).collect(Collectors.toCollection(LinkedList::new));
			if(selectedFees.size() == feeList.size()) {
				// Cambio masivo (todo seleccionado)
				offset.setValue(0);
				createParams();
				
				SERVICE.getCustomerProductsUpdates(options.getDomainName(), options.getDomain(), options.getUser(), params, new AsyncCallback<Map<Integer,Integer>>() {
					
					@Override
					public void onSuccess(Map<Integer, Integer> result) {
						Optional<Entry<Integer, Integer>> resultEntry = result.entrySet().stream().findFirst();
						AonDialog dialog = new AonDialog("Eliminaci\u00f3n Cuotas",
								new HTML("Se va a proceder a eliminar <b>" + resultEntry.get().getValue() + " cuotas</b>.<br>\u00bfEsta seguro que desea proceder con la eliminaci\u00f3n\u003f. Este proceso ser\u00e1 irreversible"));
						
						dialog.confirm(new AonAcceptDialogCallback() {

							@Override
							public void onCancel() {
								// Nothing to do here
							}

							@Override
							public void onAccept() {
								AonMessagePanel.showLoading(messagePanel, "Elimando cuotas seleccionadas ...");
								SERVICE.deleteCustomerFeeList(options.getDomainName(), options.getDomain(), options.getUser(), params,
										new AsyncCallback<Void>() {

											@Override
											public void onFailure(Throwable caught) {
												AonMessagePanel.showError(messagePanel, "Error eliminando cuotas: " + caught.getMessage());
											}

											@Override
											public void onSuccess(Void result) {
												AonMessagePanel.showSuccess(messagePanel, "Se han eliminado " + resultEntry.get().getValue() + " cuotas correctamente");
												selectionModel.clear();
												addValueButton.setEnabled(false);
												exportButton.setEnabled(false);
												setHasChange(false);
												feeList.clear();
												resetFeeTable();
												enableMoreData();
												offset.setValue(0);
												searchFees();
											}
										});
							}
						});
					}
					
					@Override
					public void onFailure(Throwable caught) {
						// TODO Auto-generated method stub
						
					}
				});
			} else {
				AonDialog dialog = new AonDialog("Eliminaci\u00f3n Cuotas",
						new HTML("Se va a proceder a eliminar <b>" + selectedFees.size() + " cuotas</b>.<br>\u00bfEsta seguro que desea proceder con la eliminaci\u00f3n\u003f<br>Este proceso ser\u00e5 irreversible"));
				
				dialog.confirm(new AonAcceptDialogCallback() {

					@Override
					public void onCancel() {
						// Nothing to do here
					}

					@Override
					public void onAccept() {
						AonMessagePanel.showLoading(messagePanel, "Elimando cuotas seleccionadas ...");
						SERVICE.deleteCustomerFeeList(options.getDomainName(), options.getDomain(), options.getUser(), selectedFees,
								new AsyncCallback<Void>() {

									@Override
									public void onFailure(Throwable caught) {
										AonMessagePanel.showError(messagePanel, "Error eliminando cuotas: " + caught.getMessage());
									}

									@Override
									public void onSuccess(Void result) {
										AonMessagePanel.showSuccess(messagePanel, "Se han eliminado " + selectedFees.size() + " cuotas correctamente");
										selectionModel.clear();
										addValueButton.setEnabled(false);
										exportButton.setEnabled(false);
										setHasChange(false);
										feeList.clear();
										resetFeeTable();
										enableMoreData();
										offset.setValue(0);
										searchFees();
									}
								});
					}
				});
			}
		});
		
		exportButton = new AonToolbarButton("Exportar Cuotas", AON.CSS.aonIconExcel());
		exportButton.setEnabled(false);
		exportButton.addClickHandler(e -> {
			LinkedList<Fee> selectedFees = selectionModel.entrySet().stream().filter(a -> a.getKey().getValue()).map(c -> c.getValue()).collect(Collectors.toCollection(LinkedList::new));
			if(selectedFees.size() == feeList.size()) {
				// Exportacion masiva (todo seleccionado)
				exportFees(null);
			} else {
				List<Integer> feeIds = new ArrayList<>();
				feeIds = selectedFees.stream().map(fee -> fee.getId()).collect(Collectors.toList());
				exportFees(feeIds);
			}
		});
		
		importButton = new AonToolbarButton("Importar Cuotas", AON.CSS.aonIconUploadFile());
		importButton.addClickHandler(e -> {
			Upload upload = new Upload() {
				
				@Override
				protected void onUpload(String data) {
					pbd = new AonProgressBarDialog("Procesando Excel...") {};
					pbd.addStyleName("gwt-PopupPanel-template");
					pbd.setGlassEnabled(true);
					pbd.center();
					pbd.show();
					
					SERVICE.parseFeeFile(options.getConfiguration().getDomain(), options.getConfiguration().getUser(), data, new AsyncCallback<List<Fee>>() {
						@Override
						public void onSuccess(List<Fee> result) {
							pbd.completed();
							pbd.hide();
							pbd = new AonProgressBarDialog("Importando Cuotas...") {};
							pbd.addStyleName("gwt-PopupPanel-template");
							pbd.setGlassEnabled(true);
							pbd.center();
							pbd.show();
							insertFee(result, 0);
						}
							
						@Override
						public void onFailure(Throwable caught) {}
					});
				}
			};
			upload.upload();
		});

		toolbar.add(saveButton);
		toolbar.add(undoAllButton);
		toolbar.add(createButton);
		toolbar.add(addValueButton);
		toolbar.add(deleteFeeButton);
		toolbar.add(exportButton);
		toolbar.add(importButton);
	}
	
	private void insertFee(List<Fee> fee, Integer index) {
		Integer lines = fee.size();
		AsyncCallback<ImportError> callback = new AsyncCallback<ImportError>() {
			@Override
			public void onSuccess(ImportError result) {
				Double progress = (result.getLine().doubleValue() / lines.doubleValue()) * 100.0;
				if(!result.getError()) {
					verror.add(result.getTextError().getFirst());
				}
				if(result.getTextWarning() != null && result.getTextWarning().size() > 0) {
					werror.addAll(result.getTextWarning());
				}
				pbd.updateProgress(progress.intValue());
				if(result.getLine() < lines - 1) {
					insertFee(fee, result.getLine() + 1);
				} else {
					pbd.completed();
					pbd.hide();
					ImportError error = new ImportError();
					error.setError(verror.size() == 0);
					error.setTextError(verror);
					error.setTextWarning(werror);
					VerticalPanel vPanel = new VerticalPanel();
					error.getTextError().forEach(errorIt -> vPanel.add(new Label(errorIt)));
					error.getTextWarning().forEach(warnIt -> vPanel.add(new Label(warnIt)));
					AonDialog dialog = new AonDialog("Importar Cuotas", vPanel);
					dialog.info();
				}
			}
				
			@Override public void onFailure(Throwable caught) {
				pbd.completed();
				pbd.hide();
			}
		};	
		
		SERVICE.importFee(options.getConfiguration().getDomain(), options.getConfiguration().getUser(), fee.get(index), index, callback);
	}

	private void exportFees(List<Integer> feeIds) {
		JSONObject json = new JSONObject();
		
		if(null == feeIds) {
			if(!checkBillingDate() && null != createBillingDate()) json.put("from", new JSONNumber(createBillingDate().getTime()));
			if(null != customerSuggestions.get(customerSuggestBox.getValue())) json.put("customer", new JSONNumber(customerSuggestions.get(customerSuggestBox.getValue()).getId()));
			if(AonStringUtils.isNotBlank(customerStatusListBox.getSelectedValue())) json.put("status", new JSONNumber(Integer.parseInt(customerStatusListBox.getSelectedValue())));
			if(null != productSuggestions.get(conceptSuggestBox.getValue())) json.put("item", new JSONNumber(productSuggestions.get(conceptSuggestBox.getValue()).getId()));
			if(AonStringUtils.isNotBlank(periocityListBox.getSelectedValue()))  json.put("period", new JSONNumber(Integer.parseInt(periocityListBox.getSelectedValue())));
			if(AonStringUtils.isNotBlank(priceTextBox.getValue()))  json.put("price", new JSONNumber(Double.parseDouble(priceTextBox.getValue())));
			if(AonStringUtils.isNotBlank(discountTextBox.getValue()))  json.put("discount", new JSONString(discountTextBox.getValue()));
			
			if(null != startDateBox.getValue()) json.put("startDate", new JSONNumber(startDateBox.getValue().getTime()));
			if(null != endDateBox.getValue()) json.put("endDate", new JSONNumber(endDateBox.getValue().getTime()));
			if(AonStringUtils.isNotBlank(quantityTextBox.getValue()))  json.put("quantity", new JSONNumber(Double.parseDouble(quantityTextBox.getValue())));
			
			if(null != workplaceSuggestions.get(workplaceSuggestBox.getValue())) json.put("workplace", new JSONNumber(workplaceSuggestions.get(workplaceSuggestBox.getValue()).getId()));
			if(null != sellerSuggestions.get(sellerSuggestBox.getValue())) json.put("seller", new JSONNumber(sellerSuggestions.get(sellerSuggestBox.getValue()).getId()));
			if(null != invoicingGroupSuggestions.get(invoicingGroupSuggestBox.getValue())) json.put("invoicingGroup", new JSONNumber(invoicingGroupSuggestions.get(invoicingGroupSuggestBox.getValue()).getId()));
			if(null != projectSuggestions.get(projectSuggestBox.getValue())) json.put("project", new JSONNumber(projectSuggestions.get(projectSuggestBox.getValue()).getId()));
		} else {
			JSONObject feeJson = new JSONObject();
			for(int i=0; i<feeIds.size(); i++) 
				feeJson.put("feeId"+i, new JSONString(feeIds.get(i).toString()));
			
			json.put("feeIds", feeJson);
		}
		
		String fileDownloadURL = GWT.getModuleBaseURL()+ "ms/gwt_download_fee/"
            	+ "?filter=" + btoa(json.toString())
            	+ "&domain_name=" + options.getDomainName()
            	+ "&domain_id=" + options.getDomain()
				+ "&username="+ options.getUser();
		
		Window.open( fileDownloadURL, "_blank",null);
	}
	
	private native String btoa(String str) /*-{
	    return btoa(str);
	}-*/;

	// ------------------------------------------ HasChange
	
	private void setFeeModify(Fee fee, int row) {
		fee.setModify(true);
		setModifyColor(row);
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
		private int row;

		public AutoResizeTextArea(Fee fee, int row) {
			super();
			this.fee = fee;
			this.row = row;
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
			setFeeModify(this.fee, this.row);
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
