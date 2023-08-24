package com.esferalia.aon.gwt.fiscal.client.registry;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.TreeMap;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.RegistryService;
import com.esferalia.aon.gwt.common.client.RegistryServiceAsync;
import com.esferalia.aon.gwt.common.client.RegistryServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.common.client.json.JsonGWTUtils;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonContextMenu;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDateBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog.AonAcceptDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDomainSelectionDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarSmallButton;
import com.esferalia.aon.gwt.fiscal.client.MainEntryPoint;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.BookingCheck;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.DomainCompany;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.aonsolutions.AonApp;
import com.esferalia.aon.occam.api.model.fee.Fee;
import com.esferalia.aon.occam.api.model.product.OldItem;
import com.esferalia.aon.occam.api.model.registry.CustomerFeeParams;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.registry.RegistryItemStatus;
import com.esferalia.aon.occam.api.model.registry.Segment;
import com.esferalia.aon.occam.api.model.security.Booking;
import com.esferalia.aon.occam.api.model.security.BookingResume;
import com.esferalia.aon.occam.api.model.security.DomainTypeInfo;
import com.esferalia.aon.occam.api.model.type.AonStatus;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.DocumentType;
import com.esferalia.aon.occam.api.model.type.DomainType;
import com.esferalia.aon.watson.mutable.MutableInt;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.UrlBuilder;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
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
import com.google.gwt.user.client.ui.Widget;

public class BookingPanel extends MainEntryPoint {
	
	class RemoveCustomerFeeCommand implements ScheduledCommand {

		@Override
		public void execute() {
			removeCustomerFee();
		}

	}
	
	class CreateBookingCommand implements ScheduledCommand {

		@Override
		public void execute() {
			createBooking();
		}

	}
	
	class FeeWithoutbookintMenu extends AonContextMenu {
		
		private Integer id;
		private OldItem item;
		private Customer customer;

		private MenuItem removeCustomerFee;
		private MenuItem createBooking;

		public FeeWithoutbookintMenu() {
			removeCustomerFee = addMenuItem("Eliminar Cuota", new RemoveCustomerFeeCommand(), AON.CSS.aonIconDelete(), "removeCustomerFee");
			createBooking = addMenuItem("Crear Contrataci\u00f3n", new CreateBookingCommand(), AON.CSS.aonIconAdd(), "createBooking");
		}
		
		private MenuItem addMenuItem(String title, ScheduledCommand command, String iconStyle, String debugId) {
			MenuItem item = addItem(title, command, iconStyle, AON.AON_ICON_CMD_BUTTON, AON.CSS.aonCmdItem());
			item.ensureDebugId(debugId);
			return item;
		}
		
		public void setId(Integer id) {
			this.id = id;
		}
		
		public Integer getId() {
			return this.id;
		}

		public OldItem getItem() {
			return item;
		}

		public void setItem(OldItem item) {
			this.item = item;
		}

		public Customer getCustomer() {
			return customer;
		}

		public void setCustomer(Customer customer) {
			this.customer = customer;
		}

	}

	// Services
	private static RegistryServiceAsync SERVICE;
	private static CommonServiceAsync COMMON_SERVICE;

	// Options Config
	private RegistryModuleOptions options;
	
	// Context Menu
	private FeeWithoutbookintMenu feeWithoutbookintMenu;

	// Content
	private DockLayoutPanel dockLayoutPanel;

	private AonToolbar toolbar;
	private ListBox bookingCheckType;
	private AonToolbarButton syncDomains;
	
	// Filter
	private Integer minYear;
	private Integer maxYear;
	
	private HTMLPanel periodItemPanel;
	private ListBox monthListBox;
	private ListBox yearListBox;
	private SuggestBox customerSuggestBox;
	private ListBox customerStatusListBox;
	private ListBox segmentListBox;
	private SuggestBox conceptSuggestBox;
	private ListBox startCompareLB;
	private AonDateBox startDateBox;
	private ListBox endCompareLB;
	private AonDateBox endDateBox;
	
	// BookingCheck Table
	private HTMLPanel container;
	private HTMLPanel messagePanel;
	private DeckPanel deckPanel;
	private ScrollPanel scrollPanel;
	private Grid bookingCheckTable;
	
	// Variables
	private LinkedList<BookingCheck> bookingCheckList = new LinkedList<>();
	
	// Search Variables
	private CustomerFeeParams params;
	private Date billingDate;
	
	private Map<String, Customer> customerSuggestions = new TreeMap<>();
	
	private Map<String, OldItem> productSuggestions = new TreeMap<>();
	
	private int limit = 100;
	private MutableInt offset = new MutableInt(0);
	private MutableInt moreData = new MutableInt(0);
	private MutableInt searchEnabled = new MutableInt(0); 
	private int lastScrollPos = 0;
	
	private DateTimeFormat formatDate = DateTimeFormat.getFormat("dd/MM/yyyy");
	private DateTimeFormat formatFullDate = DateTimeFormat.getFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
	
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
		
		feeWithoutbookintMenu = new FeeWithoutbookintMenu();

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
		periodItemPanel = new HTMLPanel("");
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
		
		periodItemPanel.setVisible(false);

		filterDefaultPanel.add(periodItemPanel);

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
		
		// Product
		HTMLPanel conceptItemPanel = new HTMLPanel("");
		conceptItemPanel.addStyleName(AON.CSS.aonItemFlex());

		Label conceptLabel = new Label("Producto");
		conceptLabel.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		createConceptSuggestBox();

		conceptItemPanel.add(conceptLabel);
		conceptItemPanel.add(conceptSuggestBox);

		filterDefaultPanel.add(conceptItemPanel);
		
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
		
		// Right buttons
		HTMLPanel filterRightPanel = new HTMLPanel("");
		filterRightPanel.addStyleName(AON.CSS.aonFlexWrap());
		filterRightPanel.getElement().getStyle().setProperty("height", "100%");
		filterRightPanel.getElement().getStyle().setProperty("align-items", "flex-start");
		filterRightPanel.getElement().getStyle().setProperty("flex-wrap", "nowrap");
		
		AonToolbarSmallButton resetBtn = new AonToolbarSmallButton("Borrar filtros", AON.CSS.aonIconClear());
		resetBtn.addClickHandler(e -> {
			resetFilter();
			bookingCheckList.clear();
			resetFeeTable();
			enableMoreData();
			offset.setValue(0);
			showInitialMessage();
			onSearchFees();
		});
		
		filterLeftPanel.add(filterDefaultPanel);
		
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
		
		customerSuggestBox.getTextBox().addBlurHandler(e -> {
			String customerQuery = customerSuggestBox.getValue();
			if(AonStringUtils.isBlank(customerQuery)) {
				onSearchFees();
			}
		});
		
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
		
		conceptSuggestBox.getTextBox().addBlurHandler(e -> {
			String productQuery = conceptSuggestBox.getValue();
			if(AonStringUtils.isBlank(productQuery)) {
				onSearchFees();
			}
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
	
	// ------- RESET FILTER ---------

	private void resetFilter() {
		monthListBox.setSelectedIndex(0);
		if (null != yearListBox) yearListBox.setSelectedIndex(0);
		customerSuggestBox.setValue("");
		customerStatusListBox.setSelectedIndex(0);
		if (null != segmentListBox) segmentListBox.setSelectedIndex(0);
		conceptSuggestBox.setValue("");
		startCompareLB.setSelectedIndex(0);
		startDateBox.setValue(null);
		endCompareLB.setSelectedIndex(0);
		endDateBox.setValue(null);
		
		if(null != params) {
			params.setMonth(null);
			params.setYear(null);
			params.setCustomer(null);
			params.setCustomerStatus(null);
			params.setSegment(null);
			params.setProduct(null);
			params.setStartCompare((byte)0);
			params.setStartDate(null);
			params.setEndCompare((byte)0);
			params.setEndDate(null);
		}
	}
	
	// ------- SEARCH ---------
	
	private void onSearchFees() {
		enableMoreData();
		offset.setValue(0);
		searchFees();
		bookingCheckList.clear();
		resetFeeTable();
	}

	private void searchFees() {
		if (!isMoreData()) return;
		
		createParams();
		
		switch (Integer.parseInt(bookingCheckType.getSelectedValue())) {
		case 0:
			SERVICE.getBookingWithoutFeeList(options.getDomainName(), options.getDomain(), options.getUser(), params,
					new AsyncCallback<LinkedList<BookingCheck>>() {
	
						@Override
						public void onFailure(Throwable caught) {
							AonMessagePanel.showError(messagePanel, "Error cargando panel de facturaci\u00f3n: " + caught.getMessage());
						}
	
						@Override
						public void onSuccess(LinkedList<BookingCheck> bookingCheckListDB) {
							loadData(bookingCheckListDB);
						}
					});
			break;
		case 1:
			SERVICE.getFeeWithoutBookingList(options.getDomainName(), options.getDomain(), options.getUser(), params,
					new AsyncCallback<LinkedList<BookingCheck>>() {
	
						@Override
						public void onFailure(Throwable caught) {
							AonMessagePanel.showError(messagePanel, "Error cargando panel de facturaci\u00f3n: " + caught.getMessage());
						}
	
						@Override
						public void onSuccess(LinkedList<BookingCheck> bookingCheckListDB) {
							loadData(bookingCheckListDB);
						}
					});
			break;
		default:
			SERVICE.getBookingCheckList(options.getDomainName(), options.getDomain(), options.getUser(), params,
					new AsyncCallback<LinkedList<BookingCheck>>() {
	
						@Override
						public void onFailure(Throwable caught) {
							AonMessagePanel.showError(messagePanel, "Error cargando panel de facturaci\u00f3n: " + caught.getMessage());
						}
	
						@Override
						public void onSuccess(LinkedList<BookingCheck> bookingCheckListDB) {
							loadData(bookingCheckListDB);
						}
					});
			break;
		}
	}
	
	private void loadData(LinkedList<BookingCheck> feeListDB) {
		if(params.getOffset() == 0 && (feeListDB == null || feeListDB.isEmpty()))
			showEmptyFeeMessage();
		else if(feeListDB == null || feeListDB.isEmpty()) {
			disableMoreData();
			if(bookingCheckList.isEmpty()) showEmptyFeeMessage();
		} else {
			showFeeTable();
			feeListDB.forEach( bookingCheck -> paintRow(bookingCheck));
			bookingCheckList.addAll(feeListDB);
			enableMoreData();
			offset.setValue(offset.getValue() + feeListDB.size());
		}
		
		enableSearch();
	}

	private void createParams() {
		if(null == params) params = new CustomerFeeParams();
		params.setDomain(options.getDomain());
		
//		Window.alert("conceptSuggestBox : " + conceptSuggestBox.getValue() + "\nItem : " + productSuggestions.get(conceptSuggestBox.getValue()) + "\nproductSuggestions size : " + productSuggestions.size()
//		+ "\n" + (productSuggestions.size() == 1 ? productSuggestions.keySet().stream().findFirst().get() : ""));
		
		params.setMonth(AonStringUtils.isBlank(monthListBox.getSelectedValue()) ? null : Integer.parseInt(monthListBox.getSelectedValue()));
		params.setYear(AonStringUtils.isBlank(yearListBox.getSelectedValue()) ? null : Integer.parseInt(yearListBox.getSelectedValue()));
		params.setCustomer(null != customerSuggestions.get(customerSuggestBox.getValue()) ? customerSuggestions.get(customerSuggestBox.getValue()).getName() : null);
		params.setCustomerStatus(AonStringUtils.isBlank(customerStatusListBox.getSelectedValue()) ? null : Byte.parseByte(customerStatusListBox.getSelectedValue()));
		params.setSegment(segmentListBox != null && segmentListBox.getSelectedIndex() > 0 ? AonNumberUtils.toInteger( segmentListBox.getSelectedValue()) : null);
		params.setProduct(null != productSuggestions.get(conceptSuggestBox.getValue()) ? productSuggestions.get(conceptSuggestBox.getValue()).getId() : null);
		params.setStartCompare(Byte.parseByte(startCompareLB.getSelectedValue()));
		params.setStartDate(startDateBox.getValue());
		params.setEndCompare(Byte.parseByte(endCompareLB.getSelectedValue()));
		params.setEndDate(endDateBox.getValue());
		
		params.setLimit(limit);
		params.setOffset(offset.getValue());
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
		Label emptyMessage = new Label("RELLENE LA BUSQUEDA PARA CARGAR EL PANEL DE CONTRATACION");
		emptyMessage.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		emptyMessage.getElement().getStyle().setMarginTop(1, Unit.EM);
		emptyPanel.add(emptyMessage);
		deckPanel.add(emptyPanel);
	}

	private void createEmptyFeeMessage() {
		HTMLPanel emptyFeePanel = new HTMLPanel("");
		emptyFeePanel.addStyleName(AON.CSS.aonDisplayFlexCenter());
		Label emptyMessage = new Label("NO EXISTEN DATOS PARA ESTE PERIODO/FILTRO");
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
		bookingCheckTable = new Grid(0, 10);
		bookingCheckTable.clear();
		bookingCheckTable.setWidth("100%");

		int row = bookingCheckTable.insertRow(bookingCheckTable.getRowCount());

		Label type = new Label("TIPO");
		Label customer = new Label("CLIENTE");
		Label customerStatus = new Label("ESTADO");
		Label concept = new Label("PRODUCTO");
		Label conceptStatus = new Label("ESTADO");
		Label quantity = new Label("CANTIDAD");
		Label startDate = new Label("F. DESDE");
		Label endDate = new Label("F. HASTA");
		Label url = new Label("");
		Label action = new Label("");
		
		type.addStyleName(AON.CSS.aonHeaderTable());
		customer.addStyleName(AON.CSS.aonHeaderTable());
		customerStatus.addStyleName(AON.CSS.aonHeaderTable());
		concept.addStyleName(AON.CSS.aonHeaderTable());
		conceptStatus.addStyleName(AON.CSS.aonHeaderTable());
		quantity.addStyleName(AON.CSS.aonHeaderTable());
		startDate.addStyleName(AON.CSS.aonHeaderTable());
		endDate.addStyleName(AON.CSS.aonHeaderTable());
		url.addStyleName(AON.CSS.aonHeaderTable());
		action.addStyleName(AON.CSS.aonHeaderTable());

		bookingCheckTable.setWidget(row, 0, type);
		bookingCheckTable.setWidget(row, 1, customer);
		bookingCheckTable.setWidget(row, 2, customerStatus);
		bookingCheckTable.setWidget(row, 3, concept);
		bookingCheckTable.setWidget(row, 4, conceptStatus);
		bookingCheckTable.setWidget(row, 5, quantity);
		bookingCheckTable.setWidget(row, 6, startDate);
		bookingCheckTable.setWidget(row, 7, endDate);
		bookingCheckTable.setWidget(row, 8, url);
		bookingCheckTable.setWidget(row, 9, action);

		bookingCheckTable.getCellFormatter().addStyleName(row, 0, AON.CSS.aonHeaderSticky());
		bookingCheckTable.getCellFormatter().addStyleName(row, 1, AON.CSS.aonHeaderSticky());
		bookingCheckTable.getCellFormatter().addStyleName(row, 2, AON.CSS.aonHeaderSticky());
		bookingCheckTable.getCellFormatter().addStyleName(row, 3, AON.CSS.aonHeaderSticky());
		bookingCheckTable.getCellFormatter().addStyleName(row, 4, AON.CSS.aonHeaderSticky());
		bookingCheckTable.getCellFormatter().addStyleName(row, 5, AON.CSS.aonHeaderSticky());
		bookingCheckTable.getCellFormatter().addStyleName(row, 6, AON.CSS.aonHeaderSticky());
		bookingCheckTable.getCellFormatter().addStyleName(row, 7, AON.CSS.aonHeaderSticky());
		bookingCheckTable.getCellFormatter().addStyleName(row, 8, AON.CSS.aonHeaderSticky());
		bookingCheckTable.getCellFormatter().addStyleName(row, 9, AON.CSS.aonHeaderSticky());

		scrollPanel.add(bookingCheckTable);
	}
	
	private void setColumnWidth() {
		bookingCheckTable.getColumnFormatter().getElement(0).getStyle().setWidth(80, Unit.PX);
		bookingCheckTable.getColumnFormatter().getElement(2).getStyle().setWidth(80, Unit.PX);
		bookingCheckTable.getColumnFormatter().getElement(3).getStyle().setWidth(30, Unit.PCT);
		bookingCheckTable.getColumnFormatter().getElement(4).getStyle().setWidth(80, Unit.PX);
		bookingCheckTable.getColumnFormatter().getElement(5).getStyle().setWidth(80, Unit.PX);
		bookingCheckTable.getColumnFormatter().getElement(6).getStyle().setWidth(80, Unit.PX);
		bookingCheckTable.getColumnFormatter().getElement(7).getStyle().setWidth(80, Unit.PX);
		bookingCheckTable.getColumnFormatter().getElement(8).getStyle().setWidth(25, Unit.PX);
		bookingCheckTable.getColumnFormatter().getElement(9).getStyle().setWidth(25, Unit.PX);
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

	private void paintRow(BookingCheck bookingCheck) {
		int row = bookingCheckTable.insertRow(bookingCheckTable.getRowCount());

		Label typeLabel = new Label(getType());
		typeLabel.setTitle(getTypeTitle());
		
		Label customerLabel = new Label(bookingCheck.getCustomer().getName());
		Label customerStatusLabel = new Label(bookingCheck.getCustomer().getStatus().getDescription());
		Label productLabel = new Label(getProductDescription(bookingCheck));
		Label productStatusLabel = new Label(Integer.parseInt(bookingCheckType.getSelectedValue()) == 1 ? getFeeStatus(bookingCheck) : getProductStatus(bookingCheck));
		Label quantityLabel = new Label(AonStringUtils.isBlank(bookingCheck.getQuantity()) ? "1.0" : bookingCheck.getQuantity());
		Label startDateLabel = new Label(formatDate(bookingCheck.getStartDate()));
		Label endDateLabel = new Label(formatDate(bookingCheck.getEndDate()));
		
		AonToolbarSmallButton actionBtn = new AonToolbarSmallButton(getActionTitle(), AON.CSS.aonIconMoreVertical());
		if(Integer.parseInt(bookingCheckType.getSelectedValue()) == 0) {
			actionBtn.removeStyleName(AON.CSS.aonIconMoreVertical());
			actionBtn.addStyleName(AON.CSS.aonIconAdd());
		}
		actionBtn.addClickHandler(e -> {
			switch (Integer.parseInt(bookingCheckType.getSelectedValue())) {
				case 0:
					createCustomerFee(bookingCheck.getItem(), bookingCheck.getCustomer());
					break;
				case 1:
					NativeEvent nativeEvent = e.getNativeEvent();
					feeWithoutbookintMenu.setPopupPosition(nativeEvent.getClientX() - 150, nativeEvent.getClientY());
					feeWithoutbookintMenu.show();
					feeWithoutbookintMenu.setId(bookingCheck.getId());
					feeWithoutbookintMenu.setItem(bookingCheck.getItem());
					feeWithoutbookintMenu.setCustomer(bookingCheck.getCustomer());
					break;
				default:
					break;
			}
		});
		
		switch (Integer.parseInt(bookingCheckType.getSelectedValue())) {
			case 0:
				actionBtn.setVisible(true);
				break;
			case 1:
				actionBtn.setVisible(true);
				break;
			default:
				if(!bookingCheck.getStatus().equals(RegistryItemStatus.ACTIVE)) {
					actionBtn = new AonToolbarSmallButton(getBookingStatus(bookingCheck.getStatus()), AON.CSS.aonIconInfo());
				} else actionBtn.setVisible(false);
				break;
		}
		
		AonToolbarSmallButton urlBtn = new AonToolbarSmallButton("URL", AON.CSS.aonIconInfo());
		urlBtn.addClickHandler(e -> {checkCustomerDomains(bookingCheck);});
		
		bookingCheckTable.setWidget(row, 0, typeLabel);
		bookingCheckTable.setWidget(row, 1, customerLabel);
		bookingCheckTable.setWidget(row, 2, customerStatusLabel);
		bookingCheckTable.setWidget(row, 3, productLabel);
		bookingCheckTable.setWidget(row, 4, productStatusLabel);
		bookingCheckTable.setWidget(row, 5, quantityLabel);
		bookingCheckTable.setWidget(row, 6, startDateLabel);
		bookingCheckTable.setWidget(row, 7, endDateLabel);
		bookingCheckTable.setWidget(row, 8, urlBtn);
		bookingCheckTable.setWidget(row, 9, actionBtn);
		
		if(Integer.parseInt(bookingCheckType.getSelectedValue()) == 0) {
			productLabel.getElement().getStyle().setFontWeight(FontWeight.BOLD);
			productLabel.getElement().getStyle().setColor("red");
			productLabel.setTitle("Existe contrataci\u00f3n, pero no cuota");
		} else if(Integer.parseInt(bookingCheckType.getSelectedValue()) == 1) {
			productLabel.getElement().getStyle().setFontWeight(FontWeight.BOLD);
			productLabel.getElement().getStyle().setColor("orange");
			productLabel.setTitle("Existe una cuota sin contrataci\u00f3n");
		} else if(Integer.parseInt(bookingCheckType.getSelectedValue()) == 2) {
			if(!bookingCheck.getStatus().equals(RegistryItemStatus.ACTIVE)) {
				productLabel.getElement().getStyle().setFontWeight(FontWeight.BOLD);
				productLabel.getElement().getStyle().setColor("orange");
				productLabel.setTitle("Existe contrataci\u00f3n asociada, pero no es tipo 'Facturable'");
			}
		}
		
		if (row % 2 == 0) {
			typeLabel.addStyleName(AON.CSS.aonOddTableRow());
			customerLabel.addStyleName(AON.CSS.aonOddTableRow());
			customerStatusLabel.addStyleName(AON.CSS.aonOddTableRow());
			productLabel.addStyleName(AON.CSS.aonOddTableRow());
			productStatusLabel.addStyleName(AON.CSS.aonOddTableRow());
			quantityLabel.addStyleName(AON.CSS.aonOddTableRow());
			startDateLabel.addStyleName(AON.CSS.aonOddTableRow());
			endDateLabel.addStyleName(AON.CSS.aonOddTableRow());
			urlBtn.addStyleName(AON.CSS.aonOddTableRow());
			actionBtn.addStyleName(AON.CSS.aonOddTableRow());
			
			bookingCheckTable.getCellFormatter().addStyleName(row, 0, AON.CSS.aonOddTableRow());
			bookingCheckTable.getCellFormatter().addStyleName(row, 1, AON.CSS.aonOddTableRow());
			bookingCheckTable.getCellFormatter().addStyleName(row, 2, AON.CSS.aonOddTableRow());
			bookingCheckTable.getCellFormatter().addStyleName(row, 3, AON.CSS.aonOddTableRow());
			bookingCheckTable.getCellFormatter().addStyleName(row, 4, AON.CSS.aonOddTableRow());
			bookingCheckTable.getCellFormatter().addStyleName(row, 5, AON.CSS.aonOddTableRow());
			bookingCheckTable.getCellFormatter().addStyleName(row, 6, AON.CSS.aonOddTableRow());
			bookingCheckTable.getCellFormatter().addStyleName(row, 7, AON.CSS.aonOddTableRow());
			bookingCheckTable.getCellFormatter().addStyleName(row, 8, AON.CSS.aonOddTableRow());
			bookingCheckTable.getCellFormatter().addStyleName(row, 9, AON.CSS.aonOddTableRow());
		}
		
		bookingCheckTable.getCellFormatter().getElement(row, 0).getStyle().setTextAlign(TextAlign.CENTER);
		bookingCheckTable.getCellFormatter().getElement(row, 2).getStyle().setTextAlign(TextAlign.CENTER);
		bookingCheckTable.getCellFormatter().getElement(row, 4).getStyle().setTextAlign(TextAlign.CENTER);
		bookingCheckTable.getCellFormatter().getElement(row, 5).getStyle().setTextAlign(TextAlign.CENTER);
		bookingCheckTable.getCellFormatter().getElement(row, 6).getStyle().setTextAlign(TextAlign.CENTER);
		bookingCheckTable.getCellFormatter().getElement(row, 7).getStyle().setTextAlign(TextAlign.CENTER);
		bookingCheckTable.getCellFormatter().getElement(row, 8).getStyle().setTextAlign(TextAlign.CENTER);
		bookingCheckTable.getCellFormatter().getElement(row, 9).getStyle().setTextAlign(TextAlign.CENTER);

		bookingCheckTable.getRowFormatter().getElement(row).getStyle().setHeight(25.00, Unit.PX);
	}

	private void checkCustomerDomains(BookingCheck bookingCheck) {
		AonMessagePanel.showLoading(messagePanel, "Obteniendo dominios del cliente ...");
		
		// Create the base URL
		String baseUrl = "/ms/api/domain/";

		// Create a URL builder and add query parameters
		UrlBuilder urlBuilder = new UrlBuilder();
		urlBuilder.setProtocol(Window.Location.getProtocol()); // Use the current protocol
		urlBuilder.setHost("aon.solutions"); 
		urlBuilder.setPath(baseUrl);
		
		urlBuilder.setParameter("customer", bookingCheck.getCustomer().getId().toString());
		
		// Create the request builder with the complete URL
		RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.GET, urlBuilder.buildString());
		requestBuilder.setHeader("session_id", "AONd95770f269e711eb94390242ac130002");
		
		Window.alert("checkCustomerDomains RequestBuilder GET : " + urlBuilder.buildString());
		Window.alert("checkCustomerDomains RequestBuilder Header (session_id) : " + requestBuilder.getHeader("session_id"));

		try {
		    // Send the request
		    requestBuilder.sendRequest(null, new RequestCallback() {
		        public void onResponseReceived(Request request, Response response) {
		            if (response.getStatusCode() == 200) {
		            	
		                String responseBody = response.getText();
		                Window.alert("Customer Domain \n" + responseBody);
		                List<DomainCompany> companies = parseDomainCompanyJSON(responseBody);
		                AonMessagePanel.hideMessage(messagePanel);
		                showDomainsDialog(companies);
		                
		            } else {
		            	AonMessagePanel.showError(messagePanel, response.getText());
		            }
		        }

				public void onError(Request request, Throwable exception) {
					AonMessagePanel.showError(messagePanel, exception.getMessage());
		        }
		    });
		} catch (RequestException e) {
			AonMessagePanel.showError(messagePanel, e.getMessage());
		}
	}
	
	private void showDomainsDialog(List<DomainCompany> companies) {
		HTMLPanel widget = new HTMLPanel("");
		widget.addStyleName(AON.CSS.aonFlexColumn());
		
		for(DomainCompany domainCompany : companies) {
			HTMLPanel widgetDomain = new HTMLPanel("");
			widgetDomain.addStyleName(AON.CSS.aonItemFlex());
			
			Label domain = new Label(domainCompany.getDomain().getDescription());
			AonTableButton btn = new AonTableButton("Ir a", AON.CSS.aonIconSend());
			btn.addClickHandler(e -> {
				Window.open(domainCompany.getDomain().getName(),"_blank","");
			});
			Label status = new Label("Estado : " + (domainCompany.getDomain().isActive() ? "Activo" : "Inactivo"));
			
			widgetDomain.add(domain);
			widgetDomain.add(status);
			widgetDomain.add(btn);
			
			widget.add(widgetDomain);
		}
		
		AonDialog dialog = new AonDialog("Dominios", widget);
		dialog.info();
	}
	
	private List<DomainCompany> parseDomainCompanyJSON(String responseBody) {
		JSONArray arr = JSONParser.parseStrict(responseBody).isArray();
		LinkedList<DomainCompany> list = new LinkedList<>();
		
		for(Integer i = 0; i < arr.size(); i++) {
			list.add(fromJSONToDomainCompany(arr.get(i)));
		}
 		
		return list;
	}
	
	private Booking parseBookingJSON(String responseBody) {
		Booking booking = fromJSONToBooking(JSONParser.parseStrict(responseBody).isObject());
		return booking;
	}
	
	private Booking fromJSONToBooking(JSONObject jsonObj) {
		List<AonApp> apps = new LinkedList<>();
		List<AonApp> parentApps = new LinkedList<>();
		
		JSONArray appsArr = JsonGWTUtils.getJSONArray(jsonObj, IJsonNames.APPS);
		for(int i=0; i<appsArr.size(); i++)
			apps.add(AonApp.safeValueOf(appsArr.get(i).toString()));
		
		JSONArray parentAppsArr = JsonGWTUtils.getJSONArray(jsonObj, IJsonNames.PARENT_APPS);
		for(int i=0; i<parentAppsArr.size(); i++)
			parentApps.add(AonApp.safeValueOf(parentAppsArr.get(i).toString()));
		
		return new Booking()
			.setDomain(parseDomain(jsonObj.get(IJsonNames.DOMAIN)))
			.setCompany(parseCompany(jsonObj.get(IJsonNames.COMPANY)))
			.setType(DomainType.safeValueOf(JsonGWTUtils.getString(jsonObj, IJsonNames.TYPE)))
			.setApps(apps)
			.setParentApps(parentApps)
			.setNumberOfUsers(JsonGWTUtils.getInt(jsonObj, IJsonNames.NUMBER_OF_USERS))
			.setPayer(JsonGWTUtils.getString(jsonObj, IJsonNames.PAYER));
	}

	private DomainCompany fromJSONToDomainCompany(JSONValue json) {
		if(json == null) return new DomainCompany();
		return new DomainCompany()
				.setSchema(json.isObject().get(IJsonNames.SCHEMA).toString())
				.setDomain(parseDomain(json.isObject().get(IJsonNames.DOMAIN)))
				.setCompany(parseCompany(json.isObject().get(IJsonNames.COMPANY)))
		;
	}

	private Domain parseDomain(JSONValue json) {
		if(json == null) return new Domain();
		
		JSONObject jsonObj = json.isObject();
		
		return new Domain()
			.setId(JsonGWTUtils.getInteger(jsonObj,IJsonNames.ID))
			.setName(JsonGWTUtils.getString(jsonObj, IJsonNames.NAME))
			.setDescription(JsonGWTUtils.getString(jsonObj, IJsonNames.DESCRIPTION))
			.setOwner(JsonGWTUtils.getString(jsonObj, IJsonNames.OWNER))
			.setParentId(JsonGWTUtils.optInteger(jsonObj, IJsonNames.PARENT_ID))
			.setDomainType( DomainType.safeValueOf( JsonGWTUtils.getString(jsonObj,IJsonNames.DOMAIN_TYPE) ))
			.setEnableHeredity(JsonGWTUtils.getboolean(jsonObj, IJsonNames.ENABLE_HEREDITY))
			.setDomainManagement(JsonGWTUtils.getboolean(jsonObj, IJsonNames.DOMAIN_MANAGEMENT))
			.setDisableDomainManagement(JsonGWTUtils.getboolean(jsonObj, IJsonNames.DISABLE_DOMAIN_MANAGEMENT))
			.setActive(JsonGWTUtils.getboolean(jsonObj, IJsonNames.ACTIVE))
			.setScope(JsonGWTUtils.getInteger(jsonObj,IJsonNames.SCOPE))
			.setMaxDefinedUsers( JsonGWTUtils.getInteger(jsonObj,IJsonNames.MAX_DEFINED_USERS))
			.setDefinedUsers( JsonGWTUtils.getInteger(jsonObj,IJsonNames.DEFINED_USERS))
			.setMaxDocumentSize( JsonGWTUtils.getInteger(jsonObj,IJsonNames.MAX_DOCUMENT_SIZE))
			.setMaxTotalDocumentSize( JsonGWTUtils.getInteger(jsonObj,IJsonNames.MAX_TOTAL_DOCUMENT_SIZE))
			.setLastAccessUser(JsonGWTUtils.getString(jsonObj, IJsonNames.LAST_ACCESS_USER))
			.setLastAccessDate(JsonGWTUtils.getDate(jsonObj, IJsonNames.LAST_ACCESS_DATE))
			.setExpirationDate(JsonGWTUtils.getDate(jsonObj, IJsonNames.EXPIRATION_DATE))
			.setCreationUser(JsonGWTUtils.getString(jsonObj, IJsonNames.CREATION_USER))
			.setCreationDate(JsonGWTUtils.getDate(jsonObj, IJsonNames.CREATION_DATE))
			.setModificationUser(JsonGWTUtils.getString(jsonObj, IJsonNames.MODIFICATION_USER))
			.setModificationDate(JsonGWTUtils.getDate(jsonObj, IJsonNames.MODIFICATION_DATE))
			.setAonCustomer(JsonGWTUtils.getInteger(jsonObj, IJsonNames.AON_CUSTOMER))
			.setAonStatus(AonStatus.safeValueOf(JsonGWTUtils.getString(jsonObj,IJsonNames.AON_STATUS)))
		;
	}

	private Company parseCompany(JSONValue json) {
		if(json == null) return new Company();
		
		JSONObject jsonObj = json.isObject();
		
		return new Company()
				.copy(parseRegistry(json))
				.setActive(JsonGWTUtils.getboolean(jsonObj, IJsonNames.ACTIVE))
				.setSurcharge(JsonGWTUtils.getboolean(jsonObj, IJsonNames.SURCHARGE))
				.setWithholding(JsonGWTUtils.getboolean(jsonObj, IJsonNames.WITHHOLDING))
				.setVatAccrualPayment(JsonGWTUtils.getboolean(jsonObj, IJsonNames.VAT_ACCRUAL_PAYMENT))
				.seteInvoice(JsonGWTUtils.getboolean(jsonObj, IJsonNames.E_INVOICE));
	}
	
	public Registry parseRegistry(JSONValue json) {
		if(json == null) {
			return new Registry();
		}
		
		JSONObject jsonObj = json.isObject();
		
		return new Registry() 
			.setId(JsonGWTUtils.getInteger(jsonObj, IJsonNames.ID))
			.setDomain(parseDomain(json))
			.setDocument(JsonGWTUtils.getString(jsonObj,IJsonNames.DOCUMENT))
			.setDocumentCountry(Country.safeValueOf(JsonGWTUtils.getString(jsonObj,IJsonNames.DOCUMENT_COUNTRY)))
			.setDocumentType(DocumentType.safeValueOf(JsonGWTUtils.getString(jsonObj,IJsonNames.DOCUMENT_TYPE)))
			.setName(JsonGWTUtils.getString(jsonObj,IJsonNames.NAME))
			.setAlias(JsonGWTUtils.getString(jsonObj,IJsonNames.ALIAS))
			.setLegalPerson(JsonGWTUtils.getboolean(jsonObj, IJsonNames.LEGAL_PERSON))
			.setNationality(Country.safeValueOf(JsonGWTUtils.getString(jsonObj,IJsonNames.NATIONALITY)))
			.setConfidential(JsonGWTUtils.getboolean(jsonObj, IJsonNames.CONFIDENTIAL))
			.setGlobal(JsonGWTUtils.getboolean(jsonObj, IJsonNames.GLOBAL))
			.setDirty(JsonGWTUtils.getboolean(jsonObj, IJsonNames.DIRTY));
	}

	private String getFeeStatus(BookingCheck bookingCheck) {
		return 	bookingCheck.getEndDate() == null || 
				(new Date().before(bookingCheck.getEndDate()) && 
				bookingCheck.getEndDate().after(bookingCheck.getStartDate())) 
				? "Facturable" : "Expirado";
	}

	private String getProductStatus(BookingCheck bookingCheck) {
		if(bookingCheck.getStatus().equals(RegistryItemStatus.ACTIVE)) return "Factrable";
		else if(bookingCheck.getStatus().equals(RegistryItemStatus.INTERESTED)) return "No Factrable";
		else if(bookingCheck.getStatus().equals(RegistryItemStatus.REFUSED)) return " No Contratado";
		else if(bookingCheck.getStatus().equals(RegistryItemStatus.INACTIVE)) return "Inactivo";
		else return "";
	}

	private String getTypeTitle() {
		switch (Integer.parseInt(bookingCheckType.getSelectedValue())) {
			case 0:
				return "Contrataci\u00f3n";
			case 1:
				return "Cuota";
			default:
				return "Contrataci\u00f3n";
		}
	}
	
	private String getType() {
		switch (Integer.parseInt(bookingCheckType.getSelectedValue())) {
			case 0:
				return "Contr.";
			case 1:
				return "Cuota";
			default:
				return "Contr.";
		}
	}

	private String getBookingStatus(RegistryItemStatus status) {
		switch (status) {
			case INTERESTED:
				return "La contrataci\u00f3n asociada es No Facturable";
			case REFUSED:
				return "La contrataci\u00f3n asociada es No Contratado";
			case INACTIVE:
				return "La contrataci\u00f3n asociada es Inactiva";
			default:
				return "";
		}
	}

	private String getActionTitle() {
		switch (Integer.parseInt(bookingCheckType.getSelectedValue())) {
			case 0:
				return "Crear cuota para la contrataci\u00f3n";
			case 1:
				return "Saneador";
			default:
				return "";
		}
	}

	private String getProductDescription(BookingCheck bookingCheck) {
		if(null == bookingCheck.getItem()) return null;
		
		String productDescription = bookingCheck.getItem().getProduct().getName();
		String productCode = bookingCheck.getItem().getProduct().getCode();
		
		return AonStringUtils.isBlank(productDescription) ? productCode : productDescription + " - (" + productCode + ")";
	}

	private void setInputStyle(Widget widget) {
		widget.setHeight("2em");
		widget.getElement().getStyle().setProperty("padding", "0 5px");
	}
	
	private String formatDate(Date date) {
		if(null == date) return null;
		return formatDate.format(date);
	}

	// -------------------------------- TOOLBAR
	
	private void createToolbar() {
		toolbar = new AonToolbar("Panel Contrataci\u00f3n");
		
		bookingCheckType = new ListBox();
		bookingCheckType.addItem("Contrataci\u00f3n Sin Cuotas", "0");
		bookingCheckType.addItem("Cuotas Sin Contrataci\u00f3n", "1");
		bookingCheckType.addItem("Contrataci\u00f3n Correcta", "2");
		
		bookingCheckType.addChangeHandler(e -> {
			checkPeriodVisibility();
			resetFilter();
			bookingCheckList.clear();
			resetFeeTable();
			enableMoreData();
			offset.setValue(0);
			showInitialMessage();
			onSearchFees();
		});
		
		syncDomains = new AonToolbarButton("Sincronizar dominio", AON.CSS.aonIconCloudSync());
		syncDomains.addClickHandler(e -> {
			syncDomain();
		});
		
		toolbar.add(bookingCheckType);
		toolbar.add(syncDomains);
	}

	private void syncDomain() {
		AonMessagePanel.showLoading(messagePanel, "Obteniendo dominios ...");
		
		// Create the base URL
		String baseUrl = "/ms/api/domain/";

		// Create a URL builder and add query parameters
		UrlBuilder urlBuilder = new UrlBuilder();
		urlBuilder.setProtocol(Window.Location.getProtocol()); // Use the current protocol
		urlBuilder.setHost("aon.solutions"); 
		urlBuilder.setPath(baseUrl);
		
//		urlBuilder.setParameter("linked", "false");
		
		// Create the request builder with the complete URL
		RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.GET, urlBuilder.buildString());
		requestBuilder.setHeader("session_id", "AONd95770f269e711eb94390242ac130002");
		
		Window.alert("syncDomain RequestBuilder GET : " + urlBuilder.buildString());
		Window.alert("syncDomain RequestBuilder Header (session_id) : " + requestBuilder.getHeader("session_id"));
		
		try {
		    // Send the request
		    requestBuilder.sendRequest(null, new RequestCallback() {
		        public void onResponseReceived(Request request, Response response) {
		            if (response.getStatusCode() == 200) {

		                String responseBody = response.getText();
		                Window.alert("Domains \n" + responseBody);
		                List<DomainCompany> companies = parseDomainCompanyJSON(responseBody);
		                AonMessagePanel.hideMessage(messagePanel);
		                showSelectDomainsDialog(companies);
		                
		            } else {
		            	AonMessagePanel.showError(messagePanel, response.getText());
		            }
		        }

				public void onError(Request request, Throwable exception) {
					AonMessagePanel.showError(messagePanel, exception.getMessage());
		        }
		    });
		} catch (RequestException e) {
			AonMessagePanel.showError(messagePanel, e.getMessage());
		}
	}
	
	private void showSelectDomainsDialog(List<DomainCompany> companies) {
		new AonDomainSelectionDialog("Dominios", companies) {
			
			@Override
			protected void onAccept(DomainCompany domainCompany) {
				AonMessagePanel.showLoading(messagePanel, "Obteniendo contrataci\u00f3n para el dominio seleccionado ...");
				
				// Create the base URL
				String baseUrl = "/ms/api/booking/";

				// Create a URL builder and add query parameters
				UrlBuilder urlBuilder = new UrlBuilder();
				urlBuilder.setProtocol(Window.Location.getProtocol()); // Use the current protocol
				urlBuilder.setHost("aon.solutions"); 
				urlBuilder.setPath(baseUrl);
				
				urlBuilder.setParameter("domainName", domainCompany.getDomain().getName());
				urlBuilder.setParameter("domainId", domainCompany.getDomain().getId().toString());
				
				// Create the request builder with the complete URL
				RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.GET, urlBuilder.buildString());
				requestBuilder.setHeader("session_id", "AONd95770f269e711eb94390242ac130002");
				
				Window.alert("booking RequestBuilder GET : " + urlBuilder.buildString());
				Window.alert("booking RequestBuilder Header (session_id) : " + requestBuilder.getHeader("session_id"));
				
				try {
				    // Send the request
				    requestBuilder.sendRequest(null, new RequestCallback() {
				        public void onResponseReceived(Request request, Response response) {
				            if (response.getStatusCode() == 200) {

				                String responseBody = response.getText();
				                Window.alert("Booking \n" + responseBody);
				                Booking booking = parseBookingJSON(responseBody);
				                AonMessagePanel.hideMessage(messagePanel);
				                updateBookingRitems(domainCompany, booking);
				                
				            } else {
				            	AonMessagePanel.showError(messagePanel, response.getText());
				            }
				        }

						public void onError(Request request, Throwable exception) {
							AonMessagePanel.showError(messagePanel, exception.getMessage());
				        }
				    });
				} catch (RequestException e) {
					AonMessagePanel.showError(messagePanel, e.getMessage());
				}
			}
		
		};
	}

	private void updateBookingRitems(DomainCompany domainCompany, Booking booking) {
		AonMessagePanel.showLoading(messagePanel, "Sincronizando contrataci\u00f3n para el dominio seleccionado ...");
		
		// Create the base URL
		String baseUrl = "/ms/api/domain/booking/";

		// Create a URL builder and add query parameters
		UrlBuilder urlBuilder = new UrlBuilder();
		urlBuilder.setProtocol(Window.Location.getProtocol()); // Use the current protocol
		urlBuilder.setHost("aon.solutions"); 
		urlBuilder.setPath(baseUrl);
		
		urlBuilder.setParameter("domain", toJson(domainCompany).toString());
		urlBuilder.setParameter("booking", toJson(booking).toString());
		
		// Create the request builder with the complete URL
		RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.PUT, urlBuilder.buildString());
		requestBuilder.setHeader("session_id", "AONd95770f269e711eb94390242ac130002");
		
		Window.alert("booking sync RequestBuilder GET : " + urlBuilder.buildString());
		Window.alert("booking sync RequestBuilder Header (session_id) : " + requestBuilder.getHeader("session_id"));
		
		try {
		    // Send the request
		    requestBuilder.sendRequest(null, new RequestCallback() {
		        public void onResponseReceived(Request request, Response response) {
		            if (response.getStatusCode() == 200) {

		                String responseBody = response.getText();
		                Window.alert("Booking Sync \n" + responseBody);
		                AonMessagePanel.showSuccess(messagePanel, "La sincronizaci\u00f3n se ha realizado correctamente");
		                
		                
		            } else {
		            	AonMessagePanel.showError(messagePanel, response.getText());
		            }
		        }

				public void onError(Request request, Throwable exception) {
					AonMessagePanel.showError(messagePanel, exception.getMessage());
		        }
		    });
		} catch (RequestException e) {
			AonMessagePanel.showError(messagePanel, e.getMessage());
		}
	}
	
	private JSONObject toJson(Booking booking) {
		JSONArray apps = new JSONArray();
		JSONArray parentApps = new JSONArray();
		
		for(int i=0; i<booking.getApps().size(); i++)
			apps.set(i, new JSONString(booking.getApps().get(i).name()));
		
		for(int i=0; i<booking.getParentApps().size(); i++)
			parentApps.set(i, new JSONString(booking.getParentApps().get(i).name()));

		return new JSONObject()
			.put(IJsonNames.DOMAIN, toJSON(booking.getDomain()))
			.isObject()
			.put(IJsonNames.COMPANY, toJSON(booking.getCompany()))
			.isObject()
			.put(IJsonNames.TYPE, new JSONString(booking.getType() != null ? booking.getType().name() : null))
			.isObject()
			.put(IJsonNames.APPS, apps) 
			.isObject()
			.put(IJsonNames.PARENT_APPS, parentApps) 
			.isObject()
			.put(IJsonNames.NUMBER_OF_USERS, new JSONString(booking.getNumberOfUsers() + ""))
			.isObject()
			.put(IJsonNames.PAYER, new JSONString(booking.getPayer()))
			.isObject()
			.put(IJsonNames.RESUME, booking.getResume() != null ? bookingResume(booking.getResume()): null)
			.isObject();
	}
	
	private static JSONObject bookingResume(BookingResume resume) {
		JSONObject o = new JSONObject();
		if(resume != null) { 
			JSONObject domain = new JSONObject();
			resume.getDomainTypes().keySet().forEach(r ->{
				DomainTypeInfo dti = resume.getDomainTypes().get(r);
				JSONObject oa = new JSONObject();
				oa.put("number", new JSONString(dti.getNumber() + ""));
				
				JSONObject apps = new JSONObject();
				dti.getChildApps().keySet().forEach(r2 -> apps.put(r2.name(), new JSONString(dti.getChildApps().get(r2) + "")));
				oa.put(IJsonNames.APPS, apps);

				oa.put("childs", childJson(dti.getChilds()));

				domain.put(r.name(), oa);	
			});
			o.put(IJsonNames.DOMAIN, domain);

			JSONObject user = new JSONObject();
			resume.getUserTypes().keySet().forEach(r -> user.put(r.name(), new JSONString(resume.getUserTypes().get(r) + "")));
			user.put("childDefinedUsers", new JSONString(resume.getChildDefinedUsers() + ""));
			user.put("childBillingUsers", new JSONString(resume.getChildBillingUsers() + ""));
			
			o.put(IJsonNames.USER, user);
		}
		return o;
	}
	
	private static JSONArray childJson(List<Domain> childs) {
		JSONArray array = new JSONArray();
		
		for(int i=0; i<childs.size(); i++) {
			JSONArray apps = new JSONArray();
			Domain d = childs.get(i);
			
			for(int j=0; j<d.getApps().size(); j++)
				apps.set(j, new JSONString(d.getApps().get(j).getApp().name()));
			
			JSONObject json = new JSONObject();
			json.put(IJsonNames.ID, new JSONString(d.getId().toString()));
			json.put(IJsonNames.NAME, new JSONString(d.getName()));
			json.put(IJsonNames.DESCRIPTION, new JSONString(d.getDescription()));
			json.put(IJsonNames.APPS, apps);
			json.put(IJsonNames.MAX_DEFINED_USERS, new JSONString(d.getMaxDefinedUsers() + ""));
			
			array.set(i, json);
		}
		
		return array;
	}

	private JSONObject toJson(DomainCompany domainCompany) {
		if(domainCompany == null) return new JSONObject();
		return new JSONObject()
				.put(IJsonNames.SCHEMA, new JSONString(domainCompany.getSchema()))
				.isObject()
				.put(IJsonNames.DOMAIN, toJSON(domainCompany.getDomain()))
				.isObject()
				.put(IJsonNames.COMPANY,toJSON(domainCompany.getCompany()))
				.isObject()
			;	
	}

	private JSONObject toJSON(Domain domain) {
		if(domain == null) return new JSONObject();
		return new JSONObject()
			.put(IJsonNames.ID, new JSONString(domain.getId().toString()))
			.isObject()
			.put(IJsonNames.NAME,new JSONString( domain.getName()))
			.isObject()
			.put(IJsonNames.DESCRIPTION, new JSONString(domain.getDescription()))
			.isObject()
			.put(IJsonNames.OWNER, new JSONString(domain.getOwner()))
			.isObject()
			.put(IJsonNames.PARENT_ID, new JSONString(domain.getParentId().toString()))
			.isObject()
			.put(IJsonNames.DOMAIN_TYPE, new JSONString(domain.getDomainType() == null?null:domain.getDomainType().toString()))
			.isObject()
			.put(IJsonNames.ENABLE_HEREDITY, new JSONString(domain.isEnableHeredity() + ""))
			.isObject()
			.put(IJsonNames.DOMAIN_MANAGEMENT, new JSONString(domain.isDomainManagement() + ""))
			.isObject()
			.put(IJsonNames.DISABLE_DOMAIN_MANAGEMENT, new JSONString(domain.isDisableDomainManagement() + ""))
			.isObject()
			.put(IJsonNames.ACTIVE, new JSONString(domain.isActive() + ""))
			.isObject()
			.put(IJsonNames.SCOPE, new JSONString(domain.getScope()+ ""))
			.isObject()
			.put(IJsonNames.MAX_DEFINED_USERS, new JSONString(domain.getMaxDefinedUsers() + ""))
			.isObject()
			.put(IJsonNames.DEFINED_USERS, new JSONString(domain.getDefinedUsers()+ ""))
			.isObject()
			.put(IJsonNames.MAX_DOCUMENT_SIZE, new JSONString(domain.getMaxDocumentSize() + ""))
			.isObject()
			.put(IJsonNames.MAX_TOTAL_DOCUMENT_SIZE,new JSONString( domain.getMaxTotalDocumentSize() + ""))
			.isObject()
			.put(IJsonNames.LAST_ACCESS_USER, new JSONString(domain.getLastAccessUser()))
			.isObject()
			.put(IJsonNames.LAST_ACCESS_DATE, new JSONString(formatFullDate.format(domain.getLastAccessDate())))
			.isObject()
			.put(IJsonNames.EXPIRATION_DATE, new JSONString(formatFullDate.format(domain.getExpirationDate())))
			.isObject()
			.put(IJsonNames.CREATION_USER, new JSONString(domain.getCreationUser()))
			.isObject()
			.put(IJsonNames.CREATION_DATE, new JSONString(formatFullDate.format(domain.getCreationDate())))
			.isObject()
			.put(IJsonNames.MODIFICATION_USER, new JSONString(domain.getModificationUser()))
			.isObject()
			.put(IJsonNames.MODIFICATION_DATE, new JSONString(formatFullDate.format(domain.getModificationDate())))
			.isObject()
			.put(IJsonNames.AON_CUSTOMER, new JSONString(domain.getAonCustomer() + ""))
			.isObject()
			.put(IJsonNames.AON_STATUS,new JSONString(domain.getAonStatus() == null?null:domain.getAonStatus().toString()))
			.isObject()
			;
	}

	private JSONObject toJSON(Company company) {
		return toJSONRegistry(company)
				.put(IJsonNames.ACTIVE, new JSONString(company.isActive() + ""))
				.isObject()
				.put(IJsonNames.SURCHARGE, new JSONString(company.isSurcharge() + ""))
				.isObject()
				.put(IJsonNames.WITHHOLDING, new JSONString(company.isWithholding() + ""))
				.isObject()
				.put(IJsonNames.VAT_ACCRUAL_PAYMENT, new JSONString(company.isVatAccrualPayment() + ""))
				.isObject()
				.put(IJsonNames.E_INVOICE,new JSONString( company.iseInvoice() + ""))
				.isObject();
	}

	private JSONObject toJSONRegistry(Company registry) {
		if(registry == null || registry.isEmpty()) return new JSONObject();
		return new JSONObject()
			.put(IJsonNames.ID, new JSONString(registry.getId().toString()))
			.isObject()
			.put(IJsonNames.DOMAIN, toJSON(registry.getDomain()))
			.isObject()
			.put(IJsonNames.DOCUMENT, new JSONString(registry.getDocument()))
			.isObject()
			.put(IJsonNames.DOCUMENT_COUNTRY, new JSONString(registry.getDocumentCountry() != null ? registry.getDocumentCountry().getIso2(): null))
			.isObject()
			.put(IJsonNames.DOCUMENT_TYPE, new JSONString(registry.getDocumentType() != null ? registry.getDocumentType().name(): null))
			.isObject()
			.put(IJsonNames.NAME, new JSONString(registry.getName()))
			.isObject()
			.put(IJsonNames.ALIAS, new JSONString(registry.getAlias()))
			.isObject()
			.put(IJsonNames.LEGAL_PERSON, new JSONString(registry.isLegalPerson() + ""))
			.isObject()
			.put(IJsonNames.NATIONALITY, new JSONString(registry.getNationality() != null ? registry.getNationality().getIso2() : null))
			.isObject()
			.put(IJsonNames.CONFIDENTIAL, new JSONString(registry.isConfidential() + ""))
			.isObject();
	}

	private void checkPeriodVisibility() {
		switch (Integer.parseInt(bookingCheckType.getSelectedValue())) {
			case 0:
				periodItemPanel.setVisible(false);
				break;
			case 1:
				periodItemPanel.setVisible(true);
				break;
			default:
				periodItemPanel.setVisible(true);
				break;
		}
	}
	
	// -------------------------------- FIXING METHODS

	private void removeCustomerFee() {
		AonDialog dialog = new AonDialog("Eliminaci\u00f3n Cuota",
				new HTML("Se va a proceder a eliminar la cuota.<br>\u00bfEsta seguro que desea proceder con la eliminaci\u00f3n\u003f. Este proceso ser\u00e1 irreversible"));
		
		dialog.confirm(new AonAcceptDialogCallback() {

			@Override
			public void onCancel() {
				// Nothing to do here
			}

			@Override
			public void onAccept() {
				AonMessagePanel.showLoading(messagePanel, "Elimando cuota seleccionada ...");
				LinkedList<Fee> selectedFees = new LinkedList<>();
				selectedFees.add(new Fee().setId(feeWithoutbookintMenu.getId()));
				
				SERVICE.deleteCustomerFeeList(options.getDomainName(), options.getDomain(), options.getUser(), selectedFees,
						new AsyncCallback<Void>() {

							@Override
							public void onFailure(Throwable caught) {
								AonMessagePanel.showError(messagePanel, "Error eliminando cuota: " + caught.getMessage());
							}

							@Override
							public void onSuccess(Void result) {
								AonMessagePanel.showSuccess(messagePanel, "Se han eliminado la cuota correctamente");
								bookingCheckList.clear();
								resetFeeTable();
								enableMoreData();
								offset.setValue(0);
								showInitialMessage();
								onSearchFees();
							}
						});
			}
		});
	}

	private void createBooking() {
		new BookingCheckDialog(options, feeWithoutbookintMenu.getItem(), feeWithoutbookintMenu.getCustomer()) {
			
			@Override
			protected void onCreate(BookingCheck bookingCheck) {
				SERVICE.saveBookingCheck(options.getDomainName(), options.getDomain(), options.getUser(), bookingCheck, new AsyncCallback<Void>() {
					
					@Override
					public void onSuccess(Void result) {
						AonMessagePanel.showSuccess(messagePanel, "Se ha creado la contrataci\u00f3n correctamente");
						resetFeeTable();
						enableMoreData();
						offset.setValue(0);
						showInitialMessage();
						onSearchFees();
					}
					
					@Override
					public void onFailure(Throwable caught) {
						AonMessagePanel.showError(messagePanel, "Error creando contrataci\u00f3n: " + caught.getMessage());
					}
				});
			}
		};
	}
	
	private void createCustomerFee(OldItem oldItem, Customer customer) {
		new CustomerFeeDialog(options, oldItem, customer) {
			
			@Override
			protected void onAccept(Fee fee) {}
			
			@Override
			protected void onCreate(Fee fee) {
				SERVICE.createCustomerFeeList(options.getDomainName(), options.getDomain(), options.getUser(), fee, new AsyncCallback<Void>() {
					
					@Override
					public void onSuccess(Void result) {
						AonMessagePanel.showSuccess(messagePanel, "Se ha creado la cuota correctamente");
						resetFeeTable();
						enableMoreData();
						offset.setValue(0);
						showInitialMessage();
						onSearchFees();
					}
					
					@Override
					public void onFailure(Throwable caught) {
						AonMessagePanel.showError(messagePanel, "Error creando cuota: " + caught.getMessage());
					}
				});
			}

			@Override
			protected void onAccept(Optional<OldItem> item, Optional<Double> price, Optional<String> discountExpr, Optional<Date> startDate, Optional<Date> endDate, Optional<Date> billingDate) {}
			
		};
	}

}
