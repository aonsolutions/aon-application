package com.esferalia.aon.gwt.fiscal.client.registry;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.RegistryService;
import com.esferalia.aon.gwt.common.client.RegistryServiceAsync;
import com.esferalia.aon.gwt.common.client.RegistryServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.common.client.json.BookingJSON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.fiscal.client.MainEntryPoint;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.security.Booking;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
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
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;

public class DomainBookingResumeModule extends MainEntryPoint {
	
	// Services
	private static RegistryServiceAsync SERVICE;
	private static CommonServiceAsync COMMON_SERVICE;

	// Options Config
	private RegistryModuleOptions options;

	// Content
	private DockLayoutPanel dockLayoutPanel;
	
	private ScrollPanel scrollPanel;
	private HTMLPanel container;
	
	private HTMLPanel domainPanel;
	private HTMLPanel domainChildsPanel;
	
	private HTMLPanel messagePanel;

	private AonToolbar toolbar;
	private AonToolbarButton backBtn;
	private ListBox enterprisesView;
	
	private Booking domainBooking;
	
	private RegistryModuleOptions opt;
	
	private DateTimeFormat formatDate = DateTimeFormat.getFormat("dd/MM/yyyy");
	
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
		
		this.opt = opt;
		
		dockLayoutPanel = new DockLayoutPanel(Unit.PX);
		this.opt.getParentWidget().add(dockLayoutPanel);
		
		dockLayoutPanel.clear();
		
		if (opt.getConfiguration() == null) {
			COMMON_SERVICE.getAonConfiguration(this.opt.getDomainName(), this.opt.getDomain(), this.opt.getUser(),
					new AsyncCallback<AonConfiguration>() {
						@Override
						public void onSuccess(AonConfiguration result) {
							opt.setConfiguration(result);
							loadModule();
						}

						@Override
						public void onFailure(Throwable caught) {
							dockLayoutPanel.add(new Label(AON.MSG.loadError( " [Interno: " + caught.getMessage() + "]")));
						}
					});
		} else {
			loadModule();
		}
	}

	private void loadModule() {
		createToolbar();
		dockLayoutPanel.addNorth(toolbar, 50);
		
		scrollPanel = new ScrollPanel();
		scrollPanel.setHeight((Window.getClientHeight() - 170) + "px");
		
		HTMLPanel mainContainer = new HTMLPanel("");
		mainContainer.addStyleName(AON.CSS.aonFlexColumn());
		
		container = new HTMLPanel("");
		container.addStyleName(AON.CSS.aonFlexColumn());
		container.getElement().getStyle().setProperty("margin", "0 1rem");
		
		messagePanel = new HTMLPanel("");
		
		domainPanel = new HTMLPanel("");
		domainPanel.addStyleName(AON.CSS.aonFlexColumn());
		
		domainChildsPanel = new HTMLPanel("");
		domainChildsPanel.addStyleName(AON.CSS.aonFlexColumn());
		
		container.add(domainPanel);
		container.add(domainChildsPanel);
		
		mainContainer.add(messagePanel);
		mainContainer.add(container);
		
		scrollPanel.add(mainContainer);
		
		dockLayoutPanel.add(scrollPanel);
		
		getBookingResume();
	}

	private void getBookingResume() {
		AonMessagePanel.showLoading(messagePanel, "Obteniendo contrataci\u00f3n de " + this.opt.getConfiguration().getDomain().getDescription());
		
		// Create the base URL
		String baseUrl = "/ms/api/booking/";

		// Create a URL builder and add query parameters
		UrlBuilder urlBuilder = new UrlBuilder();
		urlBuilder.setProtocol(Window.Location.getProtocol()); // Use the current protocol
		urlBuilder.setHost(Window.Location.getHost()); 
		urlBuilder.setPath(baseUrl);
		
		// Create the request builder with the complete URL
		RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.GET, urlBuilder.buildString());
		requestBuilder.setHeader("session_id", "AONd95770f269e711eb94390242ac130002");
		
		try {
		    // Send the request
		    requestBuilder.sendRequest(null, new RequestCallback() {
		        public void onResponseReceived(Request request, Response response) {
		            if (response.getStatusCode() == 200) {
		            	String domainBookingJson = response.getText();
		            	domainBooking = BookingJSON.parseBookingJSON(JSONParser.parseStrict(domainBookingJson).isObject());
		            	AonMessagePanel.hideMessage(messagePanel);
		            	createDomainBookingResume();
		            	createDomainChildsBookingResume();
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

	private void createDomainBookingResume() {
		domainPanel.clear();
		
		Grid domainTable = new Grid(0, 7);
		domainTable.clear();
		domainTable.setWidth("100%");

		int row = domainTable.insertRow(domainTable.getRowCount());

		Label name = new Label("DOMINIO");
		Label booking = new Label("EXTENSIONES CONTRATADAS");
		Label bookingNum = new Label("N\u00b0 EXTENSIONES");
		Label enterprises = new Label("EMPRESAS");
		Label users = new Label("USUARIOS");
		Label portalUsers = new Label("USR. PORTAL");
		Label type = new Label("TIPO");
		
		name.addStyleName(AON.CSS.aonHeaderTable());
		booking.addStyleName(AON.CSS.aonHeaderTable());
		bookingNum.addStyleName(AON.CSS.aonHeaderTable());
		enterprises.addStyleName(AON.CSS.aonHeaderTable());
		users.addStyleName(AON.CSS.aonHeaderTable());
		portalUsers.addStyleName(AON.CSS.aonHeaderTable());
		type.addStyleName(AON.CSS.aonHeaderTable());

		domainTable.setWidget(row, 0, name);
		domainTable.setWidget(row, 1, booking);
		domainTable.setWidget(row, 2, bookingNum);
		domainTable.setWidget(row, 3, enterprises);
		domainTable.setWidget(row, 4, users);
		domainTable.setWidget(row, 5, portalUsers);
		domainTable.setWidget(row, 6, type);
		
		domainTable.getCellFormatter().addStyleName(row, 0, AON.CSS.aonHeaderSticky());
		domainTable.getCellFormatter().addStyleName(row, 1, AON.CSS.aonHeaderSticky());
		domainTable.getCellFormatter().addStyleName(row, 2, AON.CSS.aonHeaderSticky());
		domainTable.getCellFormatter().addStyleName(row, 3, AON.CSS.aonHeaderSticky());
		domainTable.getCellFormatter().addStyleName(row, 4, AON.CSS.aonHeaderSticky());
		domainTable.getCellFormatter().addStyleName(row, 5, AON.CSS.aonHeaderSticky());
		domainTable.getCellFormatter().addStyleName(row, 6, AON.CSS.aonHeaderSticky());
		
		domainTable.getColumnFormatter().getElement(0).getStyle().setWidth(300, Unit.PX);
		domainTable.getColumnFormatter().getElement(2).getStyle().setWidth(100, Unit.PX);
		domainTable.getColumnFormatter().getElement(3).getStyle().setWidth(100, Unit.PX);
		domainTable.getColumnFormatter().getElement(4).getStyle().setWidth(80, Unit.PX);
		domainTable.getColumnFormatter().getElement(5).getStyle().setWidth(80, Unit.PX);
		domainTable.getColumnFormatter().getElement(6).getStyle().setWidth(100, Unit.PX);
		
		int newRow = domainTable.insertRow(domainTable.getRowCount());
		
		Label domainNameLabel  = new Label(domainBooking.getDomain().getDescription());
		
		List<String> apps = domainBooking.getApps().stream().map(aonApp -> aonApp.getDescription()).collect(Collectors.toList());
		apps.sort((o1, o2) -> o1.compareTo(o2));
		
		Label bookingLabel = new Label(String.join(", ", apps));
		Label bookingNumLabel = new Label(domainBooking.getApps().size() + "");
		
		List<Domain> childs = domainBooking.getResume().getChilds();
		List<Domain> activeChilds = domainBooking.getResume().getChilds().stream().filter(domain -> domain.isActive()).collect(Collectors.toList());
		Label enterprisesLabel = new Label(activeChilds.size() + " / " + childs.size());
		enterprisesLabel.setTitle(activeChilds.size() + " empresas activas / " + childs.size() + " empresas");
		
		Label usersLabel = new Label(domainBooking.getNumberOfUsers() + " / " + domainBooking.getDomain().getMaxDefinedUsers());
		
		Integer portalUsersCount = null == domainBooking.getDomain().getUsers() ? 0 : (int) domainBooking.getDomain().getUsers().stream().filter(user -> user.isActive() && user.isPortal()).count();			
		Label portalUsersLabel = new Label(portalUsersCount + "");
		
		Label typeLabel = new Label(domainBooking.getType().getName());
				
		domainTable.setWidget(newRow, 0, domainNameLabel);
		domainTable.setWidget(newRow, 1, bookingLabel);
		domainTable.setWidget(newRow, 2, bookingNumLabel);
		domainTable.setWidget(newRow, 3, enterprisesLabel);
		domainTable.setWidget(newRow, 4, usersLabel);
		domainTable.setWidget(newRow, 5, portalUsersLabel);
		domainTable.setWidget(newRow, 6, typeLabel);
		
		if (newRow % 2 == 0) {
			domainNameLabel.addStyleName(AON.CSS.aonOddTableRow());
			bookingLabel.addStyleName(AON.CSS.aonOddTableRow());
			bookingNumLabel.addStyleName(AON.CSS.aonOddTableRow());
			enterprisesLabel.addStyleName(AON.CSS.aonOddTableRow());
			usersLabel.addStyleName(AON.CSS.aonOddTableRow());
			portalUsersLabel.addStyleName(AON.CSS.aonOddTableRow());
			typeLabel.addStyleName(AON.CSS.aonOddTableRow());
			
			domainTable.getCellFormatter().addStyleName(newRow, 0, AON.CSS.aonOddTableRow());
			domainTable.getCellFormatter().addStyleName(newRow, 1, AON.CSS.aonOddTableRow());
			domainTable.getCellFormatter().addStyleName(newRow, 2, AON.CSS.aonOddTableRow());
			domainTable.getCellFormatter().addStyleName(newRow, 3, AON.CSS.aonOddTableRow());
			domainTable.getCellFormatter().addStyleName(newRow, 4, AON.CSS.aonOddTableRow());
			domainTable.getCellFormatter().addStyleName(newRow, 5, AON.CSS.aonOddTableRow());
			domainTable.getCellFormatter().addStyleName(newRow, 6, AON.CSS.aonOddTableRow());
		}
		
		domainTable.getCellFormatter().getElement(newRow, 2).getStyle().setTextAlign(TextAlign.CENTER);
		domainTable.getCellFormatter().getElement(newRow, 3).getStyle().setTextAlign(TextAlign.CENTER);
		domainTable.getCellFormatter().getElement(newRow, 4).getStyle().setTextAlign(TextAlign.CENTER);
		domainTable.getCellFormatter().getElement(newRow, 5).getStyle().setTextAlign(TextAlign.CENTER);
		
		domainTable.getRowFormatter().getElement(newRow).getStyle().setHeight(25.00, Unit.PX);
		
		domainPanel.add(domainTable);
	}
	
	private void createDomainChildsBookingResume() {
		boolean allEnterprises = enterprisesView.getSelectedIndex() == 1;
		domainChildsPanel.clear();
		
		Grid domainChildsTable = new Grid(0, 7);
		domainChildsTable.clear();
		domainChildsTable.setWidth("100%");

		int row = domainChildsTable.insertRow(domainChildsTable.getRowCount());

		Label name = new Label("EMPRESA");
		Label booking = new Label("EXTENSIONES CONTRATADAS");
		Label bookingNum = new Label("N\u00b0 EXTENSIONES");
		Label status = new Label("ESTADO");
		Label users = new Label("USUARIOS");
		Label portalUsers = new Label("USR. PORTAL");
		Label type = new Label("TIPO");
		
		name.addStyleName(AON.CSS.aonHeaderTable());
		booking.addStyleName(AON.CSS.aonHeaderTable());
		bookingNum.addStyleName(AON.CSS.aonHeaderTable());
		status.addStyleName(AON.CSS.aonHeaderTable());
		users.addStyleName(AON.CSS.aonHeaderTable());
		portalUsers.addStyleName(AON.CSS.aonHeaderTable());
		type.addStyleName(AON.CSS.aonHeaderTable());

		domainChildsTable.setWidget(row, 0, name);
		domainChildsTable.setWidget(row, 1, booking);
		domainChildsTable.setWidget(row, 2, bookingNum);
		domainChildsTable.setWidget(row, 3, status);
		domainChildsTable.setWidget(row, 4, users);
		domainChildsTable.setWidget(row, 5, portalUsers);
		domainChildsTable.setWidget(row, 6, type);
		
		domainChildsTable.getCellFormatter().addStyleName(row, 0, AON.CSS.aonHeaderSticky());
		domainChildsTable.getCellFormatter().addStyleName(row, 1, AON.CSS.aonHeaderSticky());
		domainChildsTable.getCellFormatter().addStyleName(row, 2, AON.CSS.aonHeaderSticky());
		domainChildsTable.getCellFormatter().addStyleName(row, 3, AON.CSS.aonHeaderSticky());
		domainChildsTable.getCellFormatter().addStyleName(row, 4, AON.CSS.aonHeaderSticky());
		domainChildsTable.getCellFormatter().addStyleName(row, 5, AON.CSS.aonHeaderSticky());
		domainChildsTable.getCellFormatter().addStyleName(row, 6, AON.CSS.aonHeaderSticky());
		
		domainChildsTable.getColumnFormatter().getElement(0).getStyle().setWidth(300, Unit.PX);
		domainChildsTable.getColumnFormatter().getElement(2).getStyle().setWidth(100, Unit.PX);
		domainChildsTable.getColumnFormatter().getElement(3).getStyle().setWidth(100, Unit.PX);
		domainChildsTable.getColumnFormatter().getElement(4).getStyle().setWidth(80, Unit.PX);
		domainChildsTable.getColumnFormatter().getElement(5).getStyle().setWidth(80, Unit.PX);
		domainChildsTable.getColumnFormatter().getElement(6).getStyle().setWidth(100, Unit.PX);
		
		List<String> parentApps = domainBooking.getApps().stream().map(aonApp -> aonApp.getDescription()).collect(Collectors.toList());
		List<Domain> childsDomain = domainBooking.getResume().getChilds();
		childsDomain.sort((o1, o2) -> o1.getDescription().compareTo(o2.getDescription()));
		
		for(Domain domainChild : childsDomain) {
			
			// Get child and parent diff apps
			List<String> childApps = domainChild.getApps().stream().map(domainApp -> domainApp.getApp().getDescription()).collect(Collectors.toList());
			List<String> parentAppsDiff = childApps.stream().filter(app -> parentApps.contains(app)).collect(Collectors.toList());
			parentAppsDiff.sort((o1, o2) -> o1.compareTo(o2));
			
			List<String> childAppsDiff = childApps.stream().filter(app -> !parentApps.contains(app)).collect(Collectors.toList());
			childAppsDiff.sort((o1, o2) -> o1.compareTo(o2));
			
			// If empresasfacturables has no child app skip
			if(!allEnterprises && childAppsDiff.size() == 0) continue;
			
			// Grid widgets columns
			Label domainNameLabel  = new Label(domainChild.getDescription());
			
			Label bookingLabel = new Label(
					(childAppsDiff.size() == 0 ? "Sin contrataciones" : String.join(", ", childAppsDiff)) + 
					" / " + 
					(parentAppsDiff.size() == 0 ? "Sin extensiones heredadas" : String.join(", ", parentAppsDiff)));
			
			Label bookingNumLabel = new Label(childAppsDiff.size() + " / " + parentAppsDiff.size() + " (" +  childApps.size() + ")");
			bookingNumLabel.setTitle(childAppsDiff.size() + " extensiones facturables / " + parentAppsDiff.size() + " extensiones heredadas del padre");
			
			String statusMessage = domainChild.getExpirationDate() != null && domainChild.getExpirationDate().before(new Date()) ? "Expirado" : (domainChild.isActive() ? "Activo" : "Inactivo");
			Label statusLabel = new Label(statusMessage);
			statusLabel.setTitle(AonStringUtils.equalsIgnoreCase(statusMessage, "Expirado") ? ("F. expiraci\u00f3n : " + formatDate(domainChild.getExpirationDate())) : "");
			
			Integer portalUsersCount = null == domainChild.getUsers() ? 0 : (int) domainChild.getUsers().stream().filter(user -> user.isActive() && user.isPortal()).count();			
			List<User> activeUsers = domainChild.getUsers().stream().filter(user -> user.isActive()).collect(Collectors.toList());
			Integer activeUsersDiff = null == activeUsers ? 0 : (activeUsers.size() - portalUsersCount);

			Label usersLabel = new Label(activeUsersDiff + " / " + domainChild.getMaxDefinedUsers());
			
			Label portalUsersLabel = new Label(portalUsersCount + "");
			
			Label typeLabel = new Label(domainChild.getDomainType().getName());
			
			// Check user diffs
			if(activeUsersDiff < domainChild.getMaxDefinedUsers()) {
				usersLabel.getElement().getStyle().setColor("orange");
				usersLabel.setTitle("Existe mas usuarios contratados que activos");
			} else if(activeUsersDiff > domainChild.getMaxDefinedUsers()) {
				usersLabel.getElement().getStyle().setColor("red");
				usersLabel.setTitle("Existe mas usuarios activos que contratados");
			}
			
			// Add row
			int newRow = domainChildsTable.insertRow(domainChildsTable.getRowCount());
			
			domainChildsTable.setWidget(newRow, 0, domainNameLabel);
			domainChildsTable.setWidget(newRow, 1, bookingLabel);
			domainChildsTable.setWidget(newRow, 2, bookingNumLabel);
			domainChildsTable.setWidget(newRow, 3, statusLabel);
			domainChildsTable.setWidget(newRow, 4, usersLabel);
			domainChildsTable.setWidget(newRow, 5, portalUsersLabel);
			domainChildsTable.setWidget(newRow, 6, typeLabel);
			
			List<Label> rowLabels = new ArrayList<>();
			rowLabels.add(domainNameLabel);
			rowLabels.add(bookingLabel);
			rowLabels.add(bookingNumLabel);
			rowLabels.add(statusLabel);
			rowLabels.add(usersLabel);
			rowLabels.add(portalUsersLabel);
			rowLabels.add(typeLabel);
			
			for(Label label : rowLabels) {
				label.addMouseOverHandler(e -> addHighlightRow(domainChildsTable, newRow));
				label.addMouseOutHandler(e -> removeHighlightRow(domainChildsTable, newRow));
			}
			
			if (newRow % 2 == 0) {
				domainNameLabel.addStyleName(AON.CSS.aonOddTableRow());
				bookingLabel.addStyleName(AON.CSS.aonOddTableRow());
				bookingNumLabel.addStyleName(AON.CSS.aonOddTableRow());
				statusLabel.addStyleName(AON.CSS.aonOddTableRow());
				usersLabel.addStyleName(AON.CSS.aonOddTableRow());
				portalUsersLabel.addStyleName(AON.CSS.aonOddTableRow());
				typeLabel.addStyleName(AON.CSS.aonOddTableRow());
				
				domainChildsTable.getCellFormatter().addStyleName(newRow, 0, AON.CSS.aonOddTableRow());
				domainChildsTable.getCellFormatter().addStyleName(newRow, 1, AON.CSS.aonOddTableRow());
				domainChildsTable.getCellFormatter().addStyleName(newRow, 2, AON.CSS.aonOddTableRow());
				domainChildsTable.getCellFormatter().addStyleName(newRow, 3, AON.CSS.aonOddTableRow());
				domainChildsTable.getCellFormatter().addStyleName(newRow, 4, AON.CSS.aonOddTableRow());
				domainChildsTable.getCellFormatter().addStyleName(newRow, 5, AON.CSS.aonOddTableRow());
				domainChildsTable.getCellFormatter().addStyleName(newRow, 6, AON.CSS.aonOddTableRow());
			}
			
			domainChildsTable.getCellFormatter().getElement(newRow, 2).getStyle().setTextAlign(TextAlign.CENTER);
			domainChildsTable.getCellFormatter().getElement(newRow, 3).getStyle().setTextAlign(TextAlign.CENTER);
			domainChildsTable.getCellFormatter().getElement(newRow, 4).getStyle().setTextAlign(TextAlign.CENTER);
			domainChildsTable.getCellFormatter().getElement(newRow, 5).getStyle().setTextAlign(TextAlign.CENTER);
			
			domainChildsTable.getRowFormatter().getElement(newRow).getStyle().setHeight(25.00, Unit.PX);
		}
		
		domainChildsPanel.add(domainChildsTable);
	}
	
	private void addHighlightRow(Grid grid, int row) {
		grid.getRowFormatter().addStyleName(row, AON.CSS.aonRowHighlight());
		for(int column=0; column < grid.getColumnCount(); column++) {
			grid.getWidget(row, column).addStyleName(AON.CSS.aonRowHighlight());
			grid.getCellFormatter().addStyleName(row, column, AON.CSS.aonRowHighlight());
		}
	}

	private void removeHighlightRow(Grid grid, int row) {
		grid.getRowFormatter().removeStyleName(row, AON.CSS.aonRowHighlight());
		for(int column=0; column < grid.getColumnCount(); column++) {
			grid.getWidget(row, column).removeStyleName(AON.CSS.aonRowHighlight());
			grid.getCellFormatter().removeStyleName(row, column, AON.CSS.aonRowHighlight());
		}
	}

	private String formatDate(Date date) {
		if(null == date) return "";
		return formatDate.format(date);
	}
	
	// -------------------------------- TOOLBAR
	
	private void createToolbar() {
		toolbar = new AonToolbar("Resumen Contrataci\u00f3n");
		
		backBtn = new AonToolbarButton("Volver inicio", AON.CSS.aonIconBack());
		backBtn.addClickHandler(e -> back());
		
		toolbar.add(backBtn);
		
		Label showLabel = new Label("Ver:");
		showLabel.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		toolbar.add(showLabel);
		
		enterprisesView = new ListBox();
		enterprisesView.addItem("Empresas con contrataciones");
		enterprisesView.addItem("Todas las empresas");
		enterprisesView.addChangeHandler(e -> onEnterprisesView());
		
		toolbar.add(enterprisesView);
	}
	
	public static native void back()
	/*-{
		$wnd.document.getElementById('headerOptionsForm:home').click();
	}-*/;
	

	private void onEnterprisesView() {
		createDomainChildsBookingResume();
	}
	
}
