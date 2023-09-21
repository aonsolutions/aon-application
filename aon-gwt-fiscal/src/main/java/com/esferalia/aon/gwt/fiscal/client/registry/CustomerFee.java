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
import com.esferalia.aon.gwt.common.client.widget.solutions.AonContextMenu;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDateBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog.AonAcceptDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonExpandButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonProgressBarDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarSmallButton;
import com.esferalia.aon.gwt.fiscal.client.MainEntryPoint;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.ImportError;
import com.esferalia.aon.occam.api.model.Workplace;
import com.esferalia.aon.occam.api.model.fee.Fee;
import com.esferalia.aon.occam.api.model.finance.InvoicingGroup;
import com.esferalia.aon.occam.api.model.product.OldItem;
import com.esferalia.aon.occam.api.model.registry.CustomerFeeParams;
import com.esferalia.aon.occam.api.model.registry.CustomerParams;
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
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
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
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.ValueBoxBase.TextAlignment;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class CustomerFee extends MainEntryPoint {
	
	class ExcelExtendCommand implements ScheduledCommand {

		@Override
		public void execute() {
			exportCustomer(true);
		}
	}
	
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
	private AonToolbarButton customerButton;
	private AonToolbarButton backButton;
	private AonExpandButton exportCustomerButton;
	
	private DeckPanel mainDeckPanel;
	
	// Filter
	private boolean expandFilter = false;
	private Integer minYear;
	private Integer maxYear;
	
	private HTMLPanel filterContentPanel;
	private ListBox monthListBox;
	private ListBox yearListBox;
	private ListBox periocityListBox;
	private SuggestBox customerSuggestBox;
	private ListBox customerStatusListBox;
	private ListBox segmentListBox;
	private ListBox startCompareLB;
	private AonDateBox startDateBox;
	private ListBox endCompareLB;
	private AonDateBox endDateBox;
	
	private HTMLPanel filterExpandPanel;
	private SuggestBox conceptSuggestBox;
	private SuggestBox productCategorySuggestBox;
	private SuggestBox productTagSuggestBox;
	private TextBox quantityTextBox;
	private TextBox priceTextBox;
	private TextBox discountTextBox;

	private HTMLPanel filterExpandPanel2;
	private SuggestBox sellerSuggestBox;
	private SuggestBox workplaceSuggestBox;
	private SuggestBox invoicingGroupSuggestBox;
	private SuggestBox projectSuggestBox;
	
	// Filter Customer
	private HTMLPanel filterCustomerContentPanel;
	private SuggestBox customerSuggestBoxCustomer;
	private ListBox customerStatusListBoxCustomer;
	
	// Fee Table
	private HTMLPanel container;
	private HTMLPanel messagePanel;
	private DeckPanel deckPanel;
	private ScrollPanel scrollPanel;
	private Grid feeTable;
	
	// Customer Table
	private HTMLPanel containerCustomer;
	private HTMLPanel messagePanelCustomer;
	private DeckPanel deckPanelCustomer;
	private ScrollPanel scrollPanelCustomer;
	private Grid customerTable;
	
	// Variables
	private Map<CheckBox, Fee> selectionModel = new HashMap<>();
	private LinkedList<Fee> feeList = new LinkedList<>();
	private boolean hasChange = false;
	
	// Variables Customer
	private Map<CheckBox, Customer> selectionModelCustomer = new HashMap<>();
	private LinkedList<Customer> customerList = new LinkedList<>();

	// Search Variables
	private CustomerFeeParams params;
	private CustomerParams customerParams;
	private Date billingDate;
	
	private Map<String, Customer> customerSuggestions = new TreeMap<>();
	
	private Map<String, OldItem> productSuggestions = new TreeMap<>();
	private Map<String, Integer> productCategorySuggestions = new TreeMap<>();
	private Map<String, Integer> productTagSuggestions = new TreeMap<>();
	
	private Map<String, Seller> sellerSuggestions = new TreeMap<>();
	private Map<String, Workplace> workplaceSuggestions = new TreeMap<>();
	private Map<String, InvoicingGroup> invoicingGroupSuggestions = new TreeMap<>();
	private Map<String, Project> projectSuggestions = new TreeMap<>();
	
	private int limit = 100;
	private MutableInt offset = new MutableInt(0);
	private MutableInt moreData = new MutableInt(0);
	private MutableInt searchEnabled = new MutableInt(0); 
	private int lastScrollPos = 0;
	
	private int limitCustomer = 100;
	private MutableInt offsetCustomer = new MutableInt(0);
	private MutableInt moreDataCustomer = new MutableInt(0);
	private MutableInt searchEnabledCustomer = new MutableInt(0); 
	private int lastScrollPosCusotmer = 0;
	
	// Import fees
	private AonProgressBarDialog pbd;
	private LinkedList<String> verror = new LinkedList<>();
	private LinkedList<String> werror = new LinkedList<>();
	
	// ContextMenu
	private ExcelExportMenu excelExportMenu;

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

		excelExportMenu = new ExcelExportMenu();
		
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
		
		initializeMainDeckPanel(opt);
		dockLayoutPanel.add(mainDeckPanel);
		mainDeckPanel.showWidget(0);
	}

	private void initializeMainDeckPanel(final RegistryModuleOptions opt) {
		mainDeckPanel = new DeckPanel();
		initializeCustomerFeePanel(opt);
		initializeCustomerPanel(opt);
	}

	private void initializeCustomerPanel(final RegistryModuleOptions opt) {
		containerCustomer = new HTMLPanel("");
		containerCustomer.clear();
		containerCustomer.addStyleName(AON.CSS.aonFlexColumn());
		mainDeckPanel.add(containerCustomer);

		messagePanelCustomer = new HTMLPanel("");
		containerCustomer.add(messagePanelCustomer);
		
		createCustomerFilterPanel(opt, new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				containerCustomer.add(new Label(AON.MSG.loadError( " [Interno: " + caught.getMessage() + "]")));
			}

			@Override
			public void onSuccess(Void result) {
				deckPanelCustomer = new DeckPanel();
				deckPanelCustomer.getElement().getStyle().setProperty("margin", "0 1rem");
				containerCustomer.add(deckPanelCustomer);
				
				scrollPanelCustomer = new ScrollPanel();
				Window.alert("filterCustomerContentPanel.getOffsetHeight() : " + filterCustomerContentPanel.getOffsetHeight());
				scrollPanelCustomer.setHeight((Window.getClientHeight() - 230 - filterCustomerContentPanel.getOffsetHeight()) + "px");
				scrollPanelCustomer.addScrollHandler(e -> {
					// ------------------------------------ Ignore scroll up.
					int oldScrollPos = lastScrollPosCusotmer;
					lastScrollPosCusotmer = scrollPanelCustomer.getVerticalScrollPosition();
					if (oldScrollPos >= lastScrollPosCusotmer) {
						return;
					}
					// -----------------------------------------------------
					if (isSearchEnabledCustomer()) {
						int maxScrollTop = scrollPanelCustomer.getWidget().getOffsetHeight() - scrollPanelCustomer.getOffsetHeight();
						if (lastScrollPosCusotmer >= maxScrollTop) {
							disableSearchCustomer();
							searchCustomer();
						}
					}
				});
				
				initializeDeckPanelCustomer();

				AonMessagePanel.showLoading(messagePanelCustomer, "Cargando clientes sin cuotas ...");
				resetFilterCustomer();
//				onSearchCustomer();
				AonMessagePanel.hideMessage(messagePanelCustomer);
			}
			
		});
	}

	private void initializeCustomerFeePanel(final RegistryModuleOptions opt) {
		container = new HTMLPanel("");
		container.clear();
		container.addStyleName(AON.CSS.aonFlexColumn());
		mainDeckPanel.add(container);

		messagePanel = new HTMLPanel("");
		container.add(messagePanel);
		
		createFilterPanel(opt, new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				container.add(new Label(AON.MSG.loadError( " [Interno: " + caught.getMessage() + "]")));
			}

			@Override
			public void onSuccess(Void result) {
				deckPanel = new DeckPanel();
				deckPanel.getElement().getStyle().setProperty("margin", "0 1rem");
				container.add(deckPanel);
				
				scrollPanel = new ScrollPanel();
				Window.alert("filterContentPanel.getOffsetHeight() : " + filterContentPanel.getOffsetHeight());
				scrollPanel.setHeight((Window.getClientHeight() - 230 - filterContentPanel.getOffsetHeight()) + "px");
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
		filterContentPanel = new HTMLPanel("");
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

		Label periodicityLabel = new Label("Periodicidad");
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
		
		// Segment
		if (opt.getConfiguration().hasSegments()) {
			HTMLPanel segmentItemPanel = new HTMLPanel("");
			segmentItemPanel.addStyleName(AON.CSS.aonItemFlex());

			Label segmentLabel = new Label("Segmento");
			segmentLabel.getElement().getStyle().setFontWeight(FontWeight.BOLD);
			
			segmentListBox = new ListBox();
			segmentListBox.setHeight("2em");
			segmentListBox.getElement().getStyle().setProperty("padding", "0 5px");
			segmentListBox.addItem("-", "");
			segmentListBox.addItem("SIN SEGMENTO", "-1");
			segmentListBox.setSelectedIndex(0);
			
			for (Segment ea : opt.getConfiguration().getSegments()) {
				segmentListBox.addItem(ea.getName(), AonNumberUtils.toString( ea.getId()));
			}
			segmentListBox.addChangeHandler(event -> onSearchFees());
			
			segmentItemPanel.add(segmentLabel);
			segmentItemPanel.add(segmentListBox);
			
			filterDefaultPanel.add(segmentItemPanel);
		}
		
		// StartDate
		HTMLPanel startDateItemPanel = new HTMLPanel("");
		startDateItemPanel.addStyleName(AON.CSS.aonItemFlex());

		Label startDateLabel = new Label("F. Inicio");
		startDateLabel.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		
		startCompareLB = createCompareListBox();
		startCompareLB.addChangeHandler(e -> {
			if(null != startDateBox.getValue()) onSearchFees();
		});
		
		startDateBox = new AonDateBox();
		startDateBox.getElement().getStyle().setTextAlign(TextAlign.CENTER);
		startDateBox.addStyleName("gwt-TextBox");
		setInputStyle(startDateBox);
		startDateBox.addValueChangeHandler(e -> onSearchFees());

		startDateItemPanel.add(startDateLabel);
		startDateItemPanel.add(startCompareLB);
		startDateItemPanel.add(startDateBox);

		filterDefaultPanel.add(startDateItemPanel);
		
		// EndDate
		HTMLPanel endDateItemPanel = new HTMLPanel("");
		endDateItemPanel.addStyleName(AON.CSS.aonItemFlex());

		Label endDateLabel = new Label("F. Fin");
		endDateLabel.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		
		endCompareLB = createCompareListBox();
		endCompareLB.addChangeHandler(e -> {
			if(null != endDateBox.getValue()) onSearchFees();
		});
		
		endDateBox = new AonDateBox();
		endDateBox.getElement().getStyle().setTextAlign(TextAlign.CENTER);
		endDateBox.addStyleName("gwt-TextBox");
		setInputStyle(endDateBox);
		endDateBox.addValueChangeHandler(e -> onSearchFees());

		endDateItemPanel.add(endDateLabel);
		endDateItemPanel.add(endCompareLB);
		endDateItemPanel.add(endDateBox);

		filterDefaultPanel.add(endDateItemPanel);
		
		// Filter Expand Panel
		filterExpandPanel = new HTMLPanel("");
		filterExpandPanel.addStyleName(AON.CSS.aonFlexWrap());
		filterExpandPanel.getElement().getStyle().setDisplay(Display.NONE);

		// Product
		HTMLPanel conceptItemPanel = new HTMLPanel("");
		conceptItemPanel.addStyleName(AON.CSS.aonItemFlex());

		Label conceptLabel = new Label("Concepto");
		conceptLabel.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		createConceptSuggestBox();

		conceptItemPanel.add(conceptLabel);
		conceptItemPanel.add(conceptSuggestBox);

		filterExpandPanel.add(conceptItemPanel);
		
		// Product Category
		HTMLPanel conceptCategoryItemPanel = new HTMLPanel("");
		conceptCategoryItemPanel.addStyleName(AON.CSS.aonItemFlex());

		Label onceptCategoryLabel = new Label("Categoria");
		onceptCategoryLabel.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		createProductCategorySuggestBox();

		conceptCategoryItemPanel.add(onceptCategoryLabel);
		conceptCategoryItemPanel.add(productCategorySuggestBox);

		filterExpandPanel.add(conceptCategoryItemPanel);
		
		// Product Tag
		HTMLPanel productTagItemPanel = new HTMLPanel("");
		productTagItemPanel.addStyleName(AON.CSS.aonItemFlex());

		Label productTagLabel = new Label("Etiqueta");
		productTagLabel.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		createProductTagSuggestBox();

		productTagItemPanel.add(productTagLabel);
		productTagItemPanel.add(productTagSuggestBox);

		filterExpandPanel.add(productTagItemPanel);
		
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

		filterExpandPanel.add(priceItemPanel);
		
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

		filterExpandPanel.add(discountItemPanel);
		
		// Filter Expand Panel
		filterExpandPanel2 = new HTMLPanel("");
		filterExpandPanel2.addStyleName(AON.CSS.aonFlexWrap());
		filterExpandPanel2.getElement().getStyle().setDisplay(Display.NONE);
		
		// Seller
		HTMLPanel sellerItemPanel = new HTMLPanel("");
		sellerItemPanel.addStyleName(AON.CSS.aonItemFlex());

		Label sellerLabel = new Label("Comercial");
		sellerLabel.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		createSellerSuggestBox();

		sellerItemPanel.add(sellerLabel);
		sellerItemPanel.add(sellerSuggestBox);

		filterExpandPanel2.add(sellerItemPanel);
		
		// Workplace
		HTMLPanel workplaceItemPanel = new HTMLPanel("");
		workplaceItemPanel.addStyleName(AON.CSS.aonItemFlex());

		Label workplaceLabel = new Label("C. Trabajo");
		workplaceLabel.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		createWorkplaceSuggestBox();

		workplaceItemPanel.add(workplaceLabel);
		workplaceItemPanel.add(workplaceSuggestBox);

		filterExpandPanel2.add(workplaceItemPanel);
		
		// Invoicing Group
		HTMLPanel invoincingGroupItemPanel = new HTMLPanel("");
		invoincingGroupItemPanel.addStyleName(AON.CSS.aonItemFlex());

		Label invoincingGroupLabel = new Label("G. Facturaci\u00f3n");
		invoincingGroupLabel.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		createInvoicingGroupSuggestBox();

		invoincingGroupItemPanel.add(invoincingGroupLabel);
		invoincingGroupItemPanel.add(invoicingGroupSuggestBox);

		filterExpandPanel2.add(invoincingGroupItemPanel);
		
		// Project
		HTMLPanel projectItemPanel = new HTMLPanel("");
		projectItemPanel.addStyleName(AON.CSS.aonItemFlex());

		Label projectLabel = new Label("Proyecto");
		projectLabel.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		createProjectSuggestBox();

		projectItemPanel.add(projectLabel);
		projectItemPanel.add(projectSuggestBox);

		filterExpandPanel2.add(projectItemPanel);	
		
		// Right buttons
		HTMLPanel filterRightPanel = new HTMLPanel("");
		filterRightPanel.addStyleName(AON.CSS.aonFlexWrap());
		filterRightPanel.getElement().getStyle().setProperty("height", "100%");
		filterRightPanel.getElement().getStyle().setProperty("align-items", "flex-start");
		filterRightPanel.getElement().getStyle().setProperty("flex-wrap", "nowrap");
		
		AonToolbarSmallButton expandFilterBtn = new AonToolbarSmallButton("Mas filtros", AON.CSS.aonIconTune());
		expandFilterBtn.addClickHandler(e -> {
			expandFilter = !expandFilter;
			if(expandFilter) {
				filterExpandPanel.getElement().getStyle().clearDisplay();
				filterExpandPanel2.getElement().getStyle().clearDisplay();
			} else {
				filterExpandPanel.getElement().getStyle().setDisplay(Display.NONE);
				filterExpandPanel2.getElement().getStyle().setDisplay(Display.NONE);
			}
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
		
		filterLeftPanel.add(filterDefaultPanel);
		filterLeftPanel.add(filterExpandPanel);
		filterLeftPanel.add(filterExpandPanel2);
		
		filterRightPanel.add(resetBtn);
		
		filterContentPanel.add(filterLeftPanel);
		filterContentPanel.add(filterRightPanel);

		container.add(filterContentPanel);
	}
	
	private void createCustomerFilterPanel(final RegistryModuleOptions opt, AsyncCallback<Void> endCallback) {
		HTMLPanel filterCustomerContentPanel = new HTMLPanel("");
		filterCustomerContentPanel.addStyleName(AON.CSS.aonFlexBetween());
		filterCustomerContentPanel.addStyleName(AON.CSS.aonFilterPanel());
		filterCustomerContentPanel.getElement().getStyle().setProperty("margin", "0 1rem");
		
		HTMLPanel filterLeftPanel = new HTMLPanel("");
		filterLeftPanel.addStyleName(AON.CSS.aonFlexColumn());
		
		HTMLPanel filterDefaultPanel = new HTMLPanel("");
		filterDefaultPanel.addStyleName(AON.CSS.aonFlexWrap());
		
		// Customer
		HTMLPanel customerItemPanel = new HTMLPanel("");
		customerItemPanel.addStyleName(AON.CSS.aonItemFlex());

		Label customerLabel = new Label("Cliente");
		customerLabel.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		createCustomerSuggestBoxCustomer();
		createCustomerStatusListBoxCustomer();

		customerItemPanel.add(customerLabel);
		customerItemPanel.add(customerSuggestBoxCustomer);
		customerItemPanel.add(customerStatusListBoxCustomer);

		filterDefaultPanel.add(customerItemPanel);
		
		// Right buttons
		HTMLPanel filterRightPanel = new HTMLPanel("");
		filterRightPanel.addStyleName(AON.CSS.aonFlexWrap());
		filterRightPanel.getElement().getStyle().setProperty("height", "100%");
		filterRightPanel.getElement().getStyle().setProperty("align-items", "flex-start");
		filterRightPanel.getElement().getStyle().setProperty("flex-wrap", "nowrap");
		
		AonToolbarSmallButton resetBtn = new AonToolbarSmallButton("Borrar filtros", AON.CSS.aonIconClear());
		resetBtn.addClickHandler(e -> {
			resetFilterCustomer();
			customerList.clear();
			resetCustomerTable();
			enableMoreDataCustomer();
			offsetCustomer.setValue(0);
			showInitialMessageCustomer();
			onSearchCustomer();
		});
		
		filterLeftPanel.add(filterDefaultPanel);
		
		filterRightPanel.add(resetBtn);
		
		filterCustomerContentPanel.add(filterLeftPanel);
		filterCustomerContentPanel.add(filterRightPanel);

		containerCustomer.add(filterCustomerContentPanel);
		
		endCallback.onSuccess(null);
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
					Integer minYearEntry = firstEntry.get().getKey();
					Integer maxYearEntry = firstEntry.get().getValue();
					
					minYear = minYearEntry;
					maxYear = maxYearEntry;
					
					Integer itYear = maxYear;
					
					while(itYear >= minYear) {
						lb.addItem(itYear.toString(), (itYear - 1900) + "");
						itYear--;
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
		lb.addItem("Cuatrimestral", "4");
		lb.addItem("Semestral", "5");
		lb.addItem("Anual", "6");
		lb.addChangeHandler(e -> onSearchFees());
		return lb;
	}
	
	private ListBox createCompareListBox() {
		ListBox lb = new ListBox();
		lb.setHeight("2em");
		lb.getElement().getStyle().setProperty("padding", "0 5px");

		lb.addItem("=", "0");
		lb.addItem("\u2264", "1");
		lb.addItem("\u2265", "2");
		
		return lb;
	}
	
	// ------- CUSTOMER ---------
	
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
	
	// ------- CUSTOMER (CUSTOMER) ---------
	
	private void createCustomerStatusListBoxCustomer() {
		customerStatusListBoxCustomer = new ListBox();
		customerStatusListBoxCustomer.setHeight("2em");
		customerStatusListBoxCustomer.getElement().getStyle().setProperty("padding", "0 5px");
		customerStatusListBoxCustomer.addItem("-", "");
		customerStatusListBoxCustomer.addItem("Activo", "0");
		customerStatusListBoxCustomer.addItem("Inactivo", "1");
		customerStatusListBoxCustomer.addItem("Bloqueado", "2");
		customerStatusListBoxCustomer.addChangeHandler(e -> onSearchCustomer());
	}
	
	private void createCustomerSuggestBoxCustomer() {
		customerSuggestBoxCustomer = new SuggestBox();
		customerSuggestBoxCustomer.setWidth("225px");
		customerSuggestBoxCustomer.setHeight("2em");
		customerSuggestBoxCustomer.getElement().getStyle().setProperty("padding", "0 5px");
		customerSuggestBoxCustomer.setAutoSelectEnabled(false);
		customerSuggestBoxCustomer.getElement().setPropertyString("placeholder", "Clientes: busque por nombre, nif o alias");
		
		customerSuggestBoxCustomer.addSelectionHandler(e -> {
			customerSuggestBoxCustomer.hideSuggestionList();
			onSearchCustomer();
		});
		
		customerSuggestBoxCustomer.addKeyUpHandler(e -> {
			String customerQuery = customerSuggestBoxCustomer.getValue();
			if(e.isControlKeyDown() && e.getNativeKeyCode() == 32) {
				customerSuggestBoxCustomer.setValue("");
				customerQuery = null;
				getCustomersSuggestionCustomer(customerQuery);
			} else if(AonStringUtils.isNotBlank(customerQuery) && customerQuery.length() > 3)
				getCustomersSuggestionCustomer(customerQuery);
		});
	}
	
	private void getCustomersSuggestionCustomer(String customerQuery) {
		SERVICE.getCustomersSuggestion(options.getDomainName(), options.getDomain(), options.getUser(), customerQuery, new AsyncCallback<Map<String, Customer>>() {
			
			@Override
			public void onSuccess(Map<String, Customer> customerSuggestionsDB) {
				customerSuggestions = customerSuggestionsDB;
				
				MultiWordSuggestOracle orclSb = (MultiWordSuggestOracle) customerSuggestBoxCustomer.getSuggestOracle();
				orclSb.clear();
				orclSb.addAll(customerSuggestions.keySet());
				orclSb.setDefaultSuggestionsFromText(customerSuggestions.keySet());
				customerSuggestBoxCustomer.showSuggestionList();
			}
			
			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub	
			}
			
		});
	}
	
	// ------- PRODUCT ---------

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
	
	private void createProductCategorySuggestBox() {
		productCategorySuggestBox = new SuggestBox();
		productCategorySuggestBox.setWidth("200px");
		productCategorySuggestBox.setHeight("2em");
		productCategorySuggestBox.getElement().getStyle().setProperty("padding", "0 5px");
		productCategorySuggestBox.setAutoSelectEnabled(false);
		productCategorySuggestBox.getElement().setPropertyString("placeholder", "Categoria: busque por descripci\u00f3n");
		
		productCategorySuggestBox.addSelectionHandler(e -> {
			productCategorySuggestBox.hideSuggestionList();
			onSearchFees();
		});
		
		productCategorySuggestBox.addKeyUpHandler(e -> {
			String productCategoryQuery = productCategorySuggestBox.getValue();
			if(e.isControlKeyDown() && e.getNativeKeyCode() == 32) {
				productCategorySuggestBox.setValue("");
				productCategoryQuery = null;
				getProductCategoriesSuggestion(productCategoryQuery);
			} else if(AonStringUtils.isNotBlank(productCategoryQuery) && productCategoryQuery.length() > 3)
				getProductCategoriesSuggestion(productCategoryQuery);
		});
	}
	
	private void getProductCategoriesSuggestion(String productCategoryQuery) {
		SERVICE.getProductCategoriesSuggestion(options.getDomainName(), options.getDomain(), options.getUser(), productCategoryQuery, new AsyncCallback<Map<String, Integer>>() {
			
			@Override
			public void onSuccess(Map<String, Integer> productCategorySuggestionsDB) {
				productCategorySuggestions = productCategorySuggestionsDB;
				
				MultiWordSuggestOracle orclSb = (MultiWordSuggestOracle) productCategorySuggestBox.getSuggestOracle();
				orclSb.clear();
				orclSb.addAll(productCategorySuggestions.keySet());
				orclSb.setDefaultSuggestionsFromText(productCategorySuggestions.keySet());
				productCategorySuggestBox.showSuggestionList();
			}
			
			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub	
			}
			
		});
	}
	
	private void createProductTagSuggestBox() {
		productTagSuggestBox = new SuggestBox();
		productTagSuggestBox.setWidth("200px");
		productTagSuggestBox.setHeight("2em");
		productTagSuggestBox.getElement().getStyle().setProperty("padding", "0 5px");
		productTagSuggestBox.setAutoSelectEnabled(false);
		productTagSuggestBox.getElement().setPropertyString("placeholder", "Etiqueta: busque por descripci\u00f3n");
		
		productTagSuggestBox.addSelectionHandler(e -> {
			productTagSuggestBox.hideSuggestionList();
			onSearchFees();
		});
		
		productTagSuggestBox.addKeyUpHandler(e -> {
			String productTagQuery = productTagSuggestBox.getValue();
			if(e.isControlKeyDown() && e.getNativeKeyCode() == 32) {
				productTagSuggestBox.setValue("");
				productTagQuery = null;
				getProductTagsSuggestion(productTagQuery);
			} else if(AonStringUtils.isNotBlank(productTagQuery) && productTagQuery.length() > 3)
				getProductTagsSuggestion(productTagQuery);
		});
	}
	
	private void getProductTagsSuggestion(String productTagQuery) {
		SERVICE.getProductTagsSuggestion(options.getDomainName(), options.getDomain(), options.getUser(), productTagQuery, new AsyncCallback<Map<String, Integer>>() {
			
			@Override
			public void onSuccess(Map<String, Integer> productTagSuggestionsDB) {
				productTagSuggestions = productTagSuggestionsDB;
				
				MultiWordSuggestOracle orclSb = (MultiWordSuggestOracle) productTagSuggestBox.getSuggestOracle();
				orclSb.clear();
				orclSb.addAll(productTagSuggestions.keySet());
				orclSb.setDefaultSuggestionsFromText(productTagSuggestions.keySet());
				productTagSuggestBox.showSuggestionList();
			}
			
			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub	
			}
			
		});
	}
	
	// ------- SELLER ---------
	
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
	
	// ------- WORKPLACE ---------
	
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
	
	// ------- INVOICING GROUP ---------
	
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
	
	// ------- PROJECT ---------
	
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
	
	// ------- RESET FILTER ---------

	private void resetFilter() {
		monthListBox.setSelectedIndex(0);
		if (null != yearListBox) yearListBox.setSelectedIndex(0);
		periocityListBox.setSelectedIndex(0);
		customerSuggestBox.setValue("");
		customerStatusListBox.setSelectedIndex(0);
		if (null != segmentListBox) segmentListBox.setSelectedIndex(0);
		startCompareLB.setSelectedIndex(0);
		startDateBox.setValue(null);
		endCompareLB.setSelectedIndex(0);
		endDateBox.setValue(null);
		
		conceptSuggestBox.setValue("");
		productCategorySuggestBox.setValue("");
		productTagSuggestBox.setValue("");
		quantityTextBox.setValue("");
		priceTextBox.setValue("");
		discountTextBox.setValue("");
		
		sellerSuggestBox.setValue("");
		workplaceSuggestBox.setValue("");
		invoicingGroupSuggestBox.setValue("");
		projectSuggestBox.setValue("");
		
		if(null != params) {
			params.setMonth(null);
			params.setYear(null);
			params.setPeriodicity(null);
			params.setCustomer(null);
			params.setCustomerStatus(null);
			params.setSegment(null);
			params.setStartCompare((byte)0);
			params.setStartDate(null);
			params.setEndCompare((byte)0);
			params.setEndDate(null);
			
			params.setProduct(null);
			params.setProductCategory(null);
			params.setProductTag(null);
			params.setQuantity(null);
			params.setPrice(null);
			params.setDiscount(null);
			
			params.setSeller(null);
			params.setWorkplace(null);
			params.setInvoicingGroup(null);
			params.setProject(null);
		}
	}
	
	// ------- RESET FILTER CUSTOMER ---------

	private void resetFilterCustomer() {
		customerSuggestBoxCustomer.setValue("");
		customerStatusListBoxCustomer.setSelectedIndex(0);
		
		if(null != customerParams) {
			customerParams.setCustomer(null);
			customerParams.setCustomerStatus(null);
		}
	}
	
	// ------- SEARCH ---------
	
	private void onSearchFees() {
		enableMoreData();
		offset.setValue(0);
		searchFees();
		feeList.clear();
		resetFeeTable();
	}

	private void searchFees() {
		if (!isMoreData()) return;
		
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

	private void createParams() {
		if(null == params) params = new CustomerFeeParams();
		params.setDomain(options.getDomain());
		
		params.setMonth(AonStringUtils.isBlank(monthListBox.getSelectedValue()) ? null : Integer.parseInt(monthListBox.getSelectedValue()));
		params.setYear(AonStringUtils.isBlank(yearListBox.getSelectedValue()) ? null : Integer.parseInt(yearListBox.getSelectedValue()));
		params.setPeriodicity(AonStringUtils.isBlank(periocityListBox.getSelectedValue()) ? null : Byte.parseByte(periocityListBox.getSelectedValue()));
		params.setCustomer(null != customerSuggestions.get(customerSuggestBox.getValue()) ? customerSuggestions.get(customerSuggestBox.getValue()).getName() : null);
		params.setCustomerStatus(AonStringUtils.isBlank(customerStatusListBox.getSelectedValue()) ? null : Byte.parseByte(customerStatusListBox.getSelectedValue()));
		params.setSegment(segmentListBox != null && segmentListBox.getSelectedIndex() > 0 ? AonNumberUtils.toInteger( segmentListBox.getSelectedValue()) : null);
		params.setStartCompare(Byte.parseByte(startCompareLB.getSelectedValue()));
		params.setStartDate(startDateBox.getValue());
		params.setEndCompare(Byte.parseByte(endCompareLB.getSelectedValue()));
		params.setEndDate(endDateBox.getValue());
		
		params.setProduct(null != productSuggestions.get(conceptSuggestBox.getValue()) ? productSuggestions.get(conceptSuggestBox.getValue()).getId() : null);
		params.setProductCategory(null != productCategorySuggestions.get(productCategorySuggestBox.getValue()) ? productCategorySuggestions.get(productCategorySuggestBox.getValue()) : null);
		params.setProductTag(null != productTagSuggestions.get(productTagSuggestBox.getValue()) ? productTagSuggestions.get(productTagSuggestBox.getValue()) : null);
		params.setQuantity(quantityTextBox.getValue());
		params.setPrice(priceTextBox.getValue());
		params.setDiscount(discountTextBox.getValue());
		
		params.setSeller(null != sellerSuggestions.get(sellerSuggestBox.getValue()) ? sellerSuggestions.get(sellerSuggestBox.getValue()).getName() : null);
		params.setWorkplace(null != workplaceSuggestions.get(workplaceSuggestBox.getValue()) ? workplaceSuggestions.get(workplaceSuggestBox.getValue()).getDescription() : null);
		params.setInvoicingGroup(null != invoicingGroupSuggestions.get(invoicingGroupSuggestBox.getValue()) ? invoicingGroupSuggestions.get(invoicingGroupSuggestBox.getValue()).getDescription() : null);
		params.setProject(null != projectSuggestions.get(projectSuggestBox.getValue()) ? projectSuggestions.get(projectSuggestBox.getValue()).getId() : null);
		
		params.setLimit(limit);
		params.setOffset(offset.getValue());
	}
	
	// ------- SEARCH ---------
	
	private void onSearchCustomer() {
		enableMoreDataCustomer();
		offsetCustomer.setValue(0);
		searchCustomer();
		customerList.clear();
		resetCustomerTable();
	}

	private void searchCustomer() {
		if (!isMoreDataCustomer()) return;
		
		createCustomerParams();
		
		SERVICE.getCustomerWithoutFee(options.getDomainName(), options.getDomain(), options.getUser(), customerParams,
			new AsyncCallback<List<Customer>>() {

				@Override
				public void onFailure(Throwable caught) {
					AonMessagePanel.showError(messagePanel, "Error cargando panel de facturaci\u00f3n: " + caught.getMessage());
				}

				@Override
				public void onSuccess(List<Customer> customerListDB) {
					if(customerParams.getOffset() == 0 && (customerListDB == null || customerListDB.isEmpty()))
						showEmptyCustomerMessage();
					else if(customerListDB == null || customerListDB.isEmpty()) {
						disableMoreDataCustomer();
						if(customerList.isEmpty()) showEmptyCustomerMessage();
					} else {
						if(offsetCustomer.getValue() == 0) selectionModelCustomer.clear();
						showCustomerTable();
						customerListDB.forEach( customer -> paintRow(customer));
						customerList.addAll(customerListDB);
						enableMoreDataCustomer();
						offsetCustomer.setValue(offsetCustomer.getValue() + customerListDB.size());
					}
					
					enableSearchCustomer();
				}
			});
		
	}

	private void createCustomerParams() {
		if(null == customerParams) customerParams = new CustomerParams();
		customerParams.setDomain(options.getDomain());
		
		customerParams.setCustomer(null != customerSuggestions.get(customerSuggestBoxCustomer.getValue()) ? customerSuggestions.get(customerSuggestBoxCustomer.getValue()).getName() : null);
		customerParams.setCustomerStatus(AonStringUtils.isBlank(customerStatusListBoxCustomer.getSelectedValue()) ? null : Byte.parseByte(customerStatusListBoxCustomer.getSelectedValue()));
		
		customerParams.setLimit(limitCustomer);
		customerParams.setOffset(offsetCustomer.getValue());
	}
	
	// ------- MAIN PAGE ---------

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
	
	private void resetFeeTable() {
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
		Label startDate = new Label("F. DESDE");
		startDate.setTitle("F. DESDE FACTURACI\u00f3N");
		Label billingDate = new Label("F. FACTURACI\u00f3N");
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
		startDate.addStyleName(AON.CSS.aonHeaderTable());
		billingDate.addStyleName(AON.CSS.aonHeaderTable());
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
		feeTable.setWidget(row, 8, startDate);
		feeTable.setWidget(row, 9, billingDate);
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
		
		AonDateBox startDateBox = new AonDateBox();
		startDateBox.getElement().getStyle().setTextAlign(TextAlign.CENTER);
		startDateBox.addStyleName("gwt-TextBox");
		setInputStyle(startDateBox);
		startDateBox.setValue(fee.getStartDate());
		startDateBox.addValueChangeHandler(e -> {
			fee.setStartDate(e.getValue());
			setFeeModify(fee, row);
		});

		HTMLPanel billingDatePanel = new HTMLPanel("");
		billingDatePanel.addStyleName(AON.CSS.aonItemFlex());
		
		ListBox monthLB = createMonthListBox();
		TextBox yearTB = createYearTextBox();
		
		monthLB.addChangeHandler(e -> {
			fee.setBillingDate(createBillingDate(monthLB.getSelectedValue(), yearTB.getValue()));
			setFeeModify(fee, row);
		});
		
		yearTB.addValueChangeHandler(e -> {
			fee.setBillingDate(createBillingDate(monthLB.getSelectedValue(), yearTB.getValue()));
			setFeeModify(fee, row);
		});
		
		setSelectedValueLB(monthLB, fee.getBillingDate().getMonth() + "");
		yearTB.setValue((fee.getBillingDate().getYear() + 1900) + "");
		
		billingDatePanel.add(monthLB);
		billingDatePanel.add(yearTB);

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
		
		checkFeeStatus(customerLabel, startDateBox, endDateBox, fee);

		checkRowAndModify(feeTable, row, fee, select);
		checkRowAndModify(feeTable, row, fee, customerLabel);
		checkRowAndModify(feeTable, row, fee, statusListBox);
		checkRowAndModify(feeTable, row, fee, conceptTextArea);
		checkRowAndModify(feeTable, row, fee, periodListBox);
		checkRowAndModify(feeTable, row, fee, quantityTextBox);
		checkRowAndModify(feeTable, row, fee, priceTextBox);
		checkRowAndModify(feeTable, row, fee, discountTextBox);
		checkRowAndModify(feeTable, row, fee, startDateBox);
		checkRowAndModify(feeTable, row, fee, billingDatePanel);
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
		feeTable.setWidget(row, 8, startDateBox);
		feeTable.setWidget(row, 9, billingDatePanel);
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

	private Date createBillingDate(String monthStr, String yearStr) {
		if(AonStringUtils.isBlank(monthStr) || AonStringUtils.isBlank(yearStr))
			return null;
		
		return new Date(Integer.parseInt(yearStr) - 1900, Integer.parseInt(monthStr), 1);
	}

	private void checkFeeStatus(Label label, AonDateBox startDateBox, AonDateBox endDateBox, Fee fee) {
		if(null != fee.getEndDate() && fee.getEndDate().before(fee.getBillingDate())) {
			label.getElement().getStyle().setFontWeight(FontWeight.BOLD);
			label.getElement().getStyle().setColor("red");
			label.setTitle("La fecha fin es anterior a la fecha de facturaci\u00f3n");
			
			endDateBox.getElement().getStyle().setColor("red");
			endDateBox.getElement().getStyle().setFontWeight(FontWeight.BOLD);
			endDateBox.setTitle("La fecha fin es anterior a la fecha de facturaci\u00f3n");
			
		} else if(null != fee.getStartDate() && fee.getStartDate().after(fee.getBillingDate())) {
			label.getElement().getStyle().setFontWeight(FontWeight.BOLD);
			label.getElement().getStyle().setColor("red");
			label.setTitle("La fecha inicion es posterior a la fecha de facturaci\u00f3n");
			
			startDateBox.getElement().getStyle().setColor("red");
			startDateBox.getElement().getStyle().setFontWeight(FontWeight.BOLD);
			startDateBox.setTitle("La fecha inicio es posterior a la fecha de facturaci\u00f3n");
			
		}
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
		lb.addItem("Cuatrimestral", "4");
		lb.addItem("Semestral", "5");
		lb.addItem("Anual", "6");
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
	
	// -------------------------------- CUSTOMER TABLE
	
	// ------- MAIN PAGE ---------

	private void initializeDeckPanelCustomer() {
		deckPanelCustomer.clear();
		createInitialMessageCustomer();
		createEmptyFeeMessageCustomer();
		createCustomerTable();
		showInitialMessageCustomer();
	}
	
	private void showInitialMessageCustomer() {
		deckPanelCustomer.showWidget(0);
	}
	
	private void showEmptyCustomerMessage() {
		deckPanelCustomer.showWidget(1);
	}
	
	private void showCustomerTable() {
		deckPanelCustomer.showWidget(2);
	}	
	
	private void createInitialMessageCustomer() {
		HTMLPanel emptyPanel = new HTMLPanel("");
		emptyPanel.addStyleName(AON.CSS.aonDisplayFlexCenter());
		Label emptyMessage = new Label("RELLENE LA BUSQUEDA PARA CARGAR LISTA DE CLIENTES SIN CUOTAS");
		emptyMessage.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		emptyMessage.getElement().getStyle().setMarginTop(1, Unit.EM);
		emptyPanel.add(emptyMessage);
		deckPanelCustomer.add(emptyPanel);
	}

	private void createEmptyFeeMessageCustomer() {
		HTMLPanel emptyFeePanel = new HTMLPanel("");
		emptyFeePanel.addStyleName(AON.CSS.aonDisplayFlexCenter());
		Label emptyMessage = new Label("NO EXISTEN CUOTAS DE CLIENTES PARA ESTE FILTRO");
		emptyMessage.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		emptyMessage.getElement().getStyle().setMarginTop(1, Unit.EM);
		emptyFeePanel.add(emptyMessage);
		deckPanelCustomer.add(emptyFeePanel);
	}
	
	private void createCustomerTable() {
		resetCustomerTable();
		deckPanelCustomer.add(scrollPanelCustomer);
	}
	
	private void resetCustomerTable() {
		scrollPanelCustomer.clear();
		createCustomerHeader();
		setColumnWidthCustomer();
	}

	private void createCustomerHeader() {
		customerTable = new Grid(0, 6);
		customerTable.clear();
		customerTable.setWidth("100%");

		int row = customerTable.insertRow(customerTable.getRowCount());

		CheckBox select = new CheckBox();
		select.addValueChangeHandler(e -> {
			selectionModelCustomer.forEach((checkBox, customer) -> checkBox.setValue(e.getValue()));
		});

		Label document = new Label("DOCUMENTO");
		Label name = new Label("NOMBRE");
		Label alias = new Label("ALIAS");
		Label scope = new Label("AMBITO");
		Label status = new Label("ESTADO");
		
		select.addStyleName(AON.CSS.aonHeaderTable());
		select.getElement().getStyle().setPaddingLeft(0, Unit.PX);
		document.addStyleName(AON.CSS.aonHeaderTable());
		name.addStyleName(AON.CSS.aonHeaderTable());
		alias.addStyleName(AON.CSS.aonHeaderTable());
		scope.addStyleName(AON.CSS.aonHeaderTable());
		status.addStyleName(AON.CSS.aonHeaderTable());

		customerTable.setWidget(row, 0, select);
		customerTable.setWidget(row, 1, document);
		customerTable.setWidget(row, 2, name);
		customerTable.setWidget(row, 3, alias);
		customerTable.setWidget(row, 4, scope);
		customerTable.setWidget(row, 5, status);

		customerTable.getCellFormatter().addStyleName(row, 0, AON.CSS.aonHeaderSticky());
		customerTable.getCellFormatter().addStyleName(row, 1, AON.CSS.aonHeaderSticky());
		customerTable.getCellFormatter().addStyleName(row, 2, AON.CSS.aonHeaderSticky());
		customerTable.getCellFormatter().addStyleName(row, 3, AON.CSS.aonHeaderSticky());
		customerTable.getCellFormatter().addStyleName(row, 4, AON.CSS.aonHeaderSticky());
		customerTable.getCellFormatter().addStyleName(row, 5, AON.CSS.aonHeaderSticky());

		scrollPanelCustomer.add(customerTable);
	}
	
	private void setColumnWidthCustomer() {
		customerTable.getColumnFormatter().getElement(0).getStyle().setWidth(2, Unit.PCT);
		customerTable.getColumnFormatter().getElement(1).getStyle().setWidth(200, Unit.PX);
		customerTable.getColumnFormatter().getElement(3).getStyle().setWidth(20, Unit.PCT);
		customerTable.getColumnFormatter().getElement(4).getStyle().setWidth(150, Unit.PX);
		customerTable.getColumnFormatter().getElement(5).getStyle().setWidth(200, Unit.PX);
	}
	
	private void disableMoreDataCustomer() {
		moreDataCustomer.setValue(-1);
	}
	
	private void enableMoreDataCustomer() {
		moreDataCustomer.setValue(0);
	}
	
	private boolean isMoreDataCustomer() {
		return (moreDataCustomer.getValue() == 0 );
	}
	
	private void enableSearchCustomer() {
		searchEnabledCustomer.setValue(0);
	}
	
	private boolean isSearchEnabledCustomer() {
		return (searchEnabledCustomer.getValue() == 0 );
	}
	
	private void disableSearchCustomer() {
		searchEnabledCustomer.setValue(-1);
	}

	private void paintRow(Customer customer) {
		int row = customerTable.insertRow(customerTable.getRowCount());

		CheckBox select = new CheckBox();
		select.addValueChangeHandler(e -> {
			Optional<CheckBox> checked = selectionModelCustomer.keySet().stream().filter(cb -> cb.getValue()).findAny();
		});
		
		Label documentLabel = new Label(customer.getDocument());
		Label nameLabel = new Label(customer.getName());
		Label aliasLabel = new Label(customer.getAlias());
		Label scopeLabel = new Label(null != customer.getScope() ? customer.getScope().getDescription() : "");
		Label statusLabel = new Label(null != customer.getStatus() ? customer.getStatus().getDescription() : "");

		customerTable.setWidget(row, 0, select);
		customerTable.setWidget(row, 1, documentLabel);
		customerTable.setWidget(row, 2, nameLabel);
		customerTable.setWidget(row, 3, aliasLabel);
		customerTable.setWidget(row, 4, scopeLabel);
		customerTable.setWidget(row, 5, statusLabel);
		
		customerTable.getCellFormatter().getElement(row, 1).getStyle().setTextAlign(TextAlign.CENTER);
		customerTable.getCellFormatter().getElement(row, 4).getStyle().setTextAlign(TextAlign.CENTER);
		customerTable.getCellFormatter().getElement(row, 5).getStyle().setTextAlign(TextAlign.CENTER);

		if (row % 2 == 0) {
			customerTable.getCellFormatter().addStyleName(row, 0, AON.CSS.aonOddTableRow());
			customerTable.getCellFormatter().addStyleName(row, 1, AON.CSS.aonOddTableRow());
			customerTable.getCellFormatter().addStyleName(row, 2, AON.CSS.aonOddTableRow());
			customerTable.getCellFormatter().addStyleName(row, 3, AON.CSS.aonOddTableRow());
			customerTable.getCellFormatter().addStyleName(row, 4, AON.CSS.aonOddTableRow());
			customerTable.getCellFormatter().addStyleName(row, 5, AON.CSS.aonOddTableRow());
		}

		customerTable.getRowFormatter().getElement(row).getStyle().setHeight(25.00, Unit.PX);

		selectionModelCustomer.put(select, customer);
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
				protected void onUpload(String data, String type) {
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
		
		customerButton = new AonToolbarButton("Clientes sin cuotas", AON.CSS.aonIconGroupOff());
		customerButton.addClickHandler(e -> {
			showCustomers();
		});
		
		backButton = new AonToolbarButton("Cuotas", AON.CSS.aonIconBack());
		backButton.setVisible(false);
		backButton.addClickHandler(e -> {
			showCustomerFee();
		});
		
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
		exportCustomerButton.setVisible(false);
		
		toolbar.add(saveButton);
		toolbar.add(undoAllButton);
		toolbar.add(createButton);
		toolbar.add(addValueButton);
		toolbar.add(deleteFeeButton);
		toolbar.add(exportButton);
		toolbar.add(importButton);
		toolbar.add(customerButton);
		toolbar.add(backButton);
		toolbar.add(exportCustomerButton);
	}
	
	private void showCustomerFee() {
		toolbar.setTitle("Panel Facturaci\u00f3n de Cuotas");
		saveButton.setVisible(true);
		undoAllButton.setVisible(true);
		createButton.setVisible(true);
		addValueButton.setVisible(true);
		deleteFeeButton.setVisible(true);
		exportButton.setVisible(true);
		importButton.setVisible(true);
		customerButton.setVisible(true);
		backButton.setVisible(false);
		exportCustomerButton.setVisible(false);
		mainDeckPanel.showWidget(0);
		
	}

	private void showCustomers() {
		toolbar.setTitle("Clientes sin Cuotas");
		saveButton.setVisible(false);
		undoAllButton.setVisible(false);
		createButton.setVisible(false);
		addValueButton.setVisible(false);
		deleteFeeButton.setVisible(false);
		exportButton.setVisible(false);
		importButton.setVisible(false);
		customerButton.setVisible(false);
		backButton.setVisible(true);
		exportCustomerButton.setVisible(true);
		
		if(null == customerList || customerList.isEmpty()) {
			selectionModelCustomer.clear();
			customerList.clear();
			resetCustomerTable();
			enableMoreDataCustomer();
			offsetCustomer.setValue(0);
			searchCustomer();
		}
		
		mainDeckPanel.showWidget(1);
		
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
			
			if(AonStringUtils.isNotBlank(monthListBox.getSelectedValue())) json.put("month", new JSONNumber(Integer.parseInt(monthListBox.getSelectedValue())));
			if(AonStringUtils.isNotBlank(yearListBox.getSelectedValue())) json.put("year", new JSONNumber(Integer.parseInt(yearListBox.getSelectedValue())));
			if(AonStringUtils.isNotBlank(periocityListBox.getSelectedValue())) json.put("periodicity", new JSONNumber(Integer.parseInt(periocityListBox.getSelectedValue())));
			if(null != customerSuggestions.get(customerSuggestBox.getValue())) json.put("customer", new JSONNumber(customerSuggestions.get(customerSuggestBox.getValue()).getId()));
			if(AonStringUtils.isNotBlank(customerStatusListBox.getSelectedValue())) json.put("status", new JSONNumber(Integer.parseInt(customerStatusListBox.getSelectedValue())));
			if(null != segmentListBox && AonStringUtils.isNotBlank(segmentListBox.getSelectedValue())) json.put("segment", new JSONNumber(Integer.parseInt(segmentListBox.getSelectedValue())));
			if(AonStringUtils.isNotBlank(startCompareLB.getSelectedValue())) json.put("startDateCompare", new JSONNumber(Integer.parseInt(startCompareLB.getSelectedValue())));
			if(null != startDateBox.getValue()) json.put("startDate", new JSONNumber(startDateBox.getValue().getTime()));
			if(AonStringUtils.isNotBlank(endCompareLB.getSelectedValue())) json.put("endDateCompare", new JSONNumber(Integer.parseInt(endCompareLB.getSelectedValue())));
			if(null != endDateBox.getValue()) json.put("endDate", new JSONNumber(endDateBox.getValue().getTime()));
			
			if(null != productSuggestions.get(conceptSuggestBox.getValue())) json.put("product", new JSONNumber(productSuggestions.get(conceptSuggestBox.getValue()).getId()));
			if(null != productCategorySuggestions.get(productCategorySuggestBox.getValue())) json.put("productCategory", new JSONNumber(productCategorySuggestions.get(productCategorySuggestBox.getValue())));
			if(null != productTagSuggestions.get(productTagSuggestBox.getValue())) json.put("productTag", new JSONNumber(productTagSuggestions.get(productTagSuggestBox.getValue())));
			if(AonStringUtils.isNotBlank(quantityTextBox.getValue()))  json.put("quantity", new JSONNumber(Double.parseDouble(quantityTextBox.getValue())));
			if(AonStringUtils.isNotBlank(priceTextBox.getValue()))  json.put("price", new JSONNumber(Double.parseDouble(priceTextBox.getValue())));
			if(AonStringUtils.isNotBlank(discountTextBox.getValue()))  json.put("discount", new JSONString(discountTextBox.getValue()));
			
			if(null != sellerSuggestions.get(sellerSuggestBox.getValue())) json.put("seller", new JSONNumber(sellerSuggestions.get(sellerSuggestBox.getValue()).getId()));
			if(null != workplaceSuggestions.get(workplaceSuggestBox.getValue())) json.put("workplace", new JSONNumber(workplaceSuggestions.get(workplaceSuggestBox.getValue()).getId()));
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
	
	private void exportCustomer(boolean extend) {
		LinkedList<Customer> selectedCustomer = selectionModelCustomer.entrySet().stream().filter(a -> a.getKey().getValue()).map(c -> c.getValue()).collect(Collectors.toCollection(LinkedList::new));
		if(selectedCustomer.size() == customerList.size()) {
			// Exportacion masiva (todo seleccionado)
			exportCustomer(null, extend);
		} else {
			List<Integer> customerIds = new ArrayList<>();
			customerIds = selectedCustomer.stream().map(fee -> fee.getId()).collect(Collectors.toList());
			exportCustomer(customerIds, extend);
		}
	}
	
	private void exportCustomer(List<Integer> customerIds, boolean extend) {
		JSONObject json = new JSONObject();
		
		if(null == customerIds) {
			if(null != customerSuggestions.get(customerSuggestBoxCustomer.getValue())) json.put("customer", new JSONNumber(customerSuggestions.get(customerSuggestBoxCustomer.getValue()).getId()));
			if(AonStringUtils.isNotBlank(customerStatusListBoxCustomer.getSelectedValue())) json.put("status", new JSONNumber(Integer.parseInt(customerStatusListBoxCustomer.getSelectedValue())));
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

	private Date createBillingDate() {
		String monthStr = monthListBox.getSelectedValue();
		String yearStr = null == yearListBox ? "" : yearListBox.getSelectedValue();
		
		if(AonStringUtils.isBlank(yearStr)) return null;
		
		if(AonStringUtils.isBlank(monthStr)) return new Date(Integer.parseInt(yearStr), 0, 1);
		else return new Date(Integer.parseInt(yearStr), Integer.parseInt(monthStr), 1);
	}
	
	private boolean checkBillingDate() {
		Integer month = AonStringUtils.isBlank(monthListBox.getSelectedValue()) ? null : Integer.parseInt(monthListBox.getSelectedValue());
		Integer year = AonStringUtils.isBlank(yearListBox.getSelectedValue()) ? null : Integer.parseInt(yearListBox.getSelectedValue());

		return month != null && year == null;
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
