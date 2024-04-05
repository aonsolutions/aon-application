package com.esferalia.aon.gwt.fiscal.client.booking;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.RegistryServiceAsync;
import com.esferalia.aon.gwt.common.client.json.BookingJSON;
import com.esferalia.aon.gwt.common.client.json.DomainCompanyJSON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonContextMenu;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog.AonAcceptDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDomainSyncSelectionDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarSmall;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarSmallButton;
import com.esferalia.aon.gwt.fiscal.client.registry.CustomerFeeDialog;
import com.esferalia.aon.gwt.fiscal.client.registry.RegistryModuleOptions;
import com.esferalia.aon.occam.api.model.BookingCheck;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.DomainCompany;
import com.esferalia.aon.occam.api.model.aonsolutions.AonApp;
import com.esferalia.aon.occam.api.model.fee.Fee;
import com.esferalia.aon.occam.api.model.product.OldItem;
import com.esferalia.aon.occam.api.model.registry.CustomerFeeParams;
import com.esferalia.aon.occam.api.model.registry.RegistryItemStatus;
import com.esferalia.aon.occam.api.model.security.Booking;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.AonStatus;
import com.esferalia.aon.occam.api.model.type.DomainType;
import com.esferalia.aon.occam.api.model.type.RegistryStatus;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.Display;
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
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.ScrollPanel;

public class BookingCustomer extends HTMLPanel {
	
	// ------- BookingWithOutFee
	
	class AonAppUsersCommand implements ScheduledCommand {

		@Override
		public void execute() {
			aonAppUsers(bookingWithOutFeeMenu.getCustomer());
		}
		
		public void aonAppUsers(Customer customer) {
			// Create the base URL
			String baseUrl = "/ms/api/booking/customer";

			// Create a URL builder and add query parameters
			UrlBuilder urlBuilder = new UrlBuilder();
			urlBuilder.setProtocol(Window.Location.getProtocol()); // Use the current protocol
//			urlBuilder.setHost("localhost:8080");
			urlBuilder.setHost("aon.solutions"); 
			urlBuilder.setPath(baseUrl);
			
			urlBuilder.setParameter("customer", customer.getId().toString());
			
			// Create the request builder with the complete URL
			RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.GET, urlBuilder.buildString());
			requestBuilder.setHeader("session_id", "AONd95770f269e711eb94390242ac130002");
			
			try {
			    // Send the request
			    requestBuilder.sendRequest(null, new RequestCallback() {
			        public void onResponseReceived(Request request, Response response) {
			            if (response.getStatusCode() == 200) {
			            	String responseBody = response.getText();
			            	List<Booking> bookingList = BookingJSON.parseBookingJSONArr(responseBody);
			            	if(!bookingList.isEmpty()) {
			            		Booking booking = bookingList.get(0);
			            		if(null != booking.getResume() && !booking.getResume().getChilds().isEmpty()) {
			            			
			            			List<String> barCodes = new ArrayList<>();
			            			String barCode = bookingWithOutFeeMenu.bookingCheck.getItem().getBarcode();
			            			
			            			if(barCode.contains("/")) {
			            				String[] splits = barCode.split("/");
			            				for(int i=0; i < splits.length; i++)
			            					barCodes.add(splits[i].trim());
			            			} else
			            				barCodes.add(barCode);
			            			
			            			DomainType domainType = DomainType.safeValueOf(Integer.parseInt(barCodes.stream().filter(barCodeIt -> barCodeIt.split("\\.").length == 3).findFirst().get().split("\\.")[1]));
			            			AonApp aonApp = AonApp.safeValueOf(Integer.parseInt(barCodes.stream().filter(barCodeIt -> barCodeIt.split("\\.").length == 3).findFirst().get().split("\\.")[2]));
			            			
			            			// Create Widget
			            			String htmlBody = "<ul>";
			            			
			            			// Sort child domains
			            			booking.getResume().getChilds().sort((o1, o2) -> o1.getDescription().compareTo(o2.getDescription()));
			            			
			            			if(domainType.equals(DomainType.OFFICE)) {
			            				for(Domain childDomain : booking.getResume().getChilds()) {
				            				if(null != childDomain.getApps() && childDomain.getDomainType().equals(DomainType.OFFICE)) {
				            					
				            					long hasApp = childDomain.getApps().stream().filter(domainApp -> domainApp.getApp().equals(aonApp)).count();
					            				
				            					if(hasApp > 0) {
					            					htmlBody += "<li>(" + childDomain.getMaxDefinedUsers() + " usr.) -- " +  childDomain.getDescription() + " -- " + childDomain.getName() + "</li>";
					            				}
				            				}
				            			}
			            			} else {
			            				for(Domain childDomain : booking.getResume().getChilds()) {
				            				if(null != childDomain.getApps() && !childDomain.getDomainType().equals(DomainType.OFFICE)) {
				            					
				            					long hasApp = childDomain.getApps().stream().filter(domainApp -> domainApp.getApp().equals(aonApp)).count();
					            				
				            					if(hasApp > 0) {
					            					htmlBody += "<li>(" + childDomain.getMaxDefinedUsers() + " usr.) -- " +  childDomain.getDescription() + " -- " + childDomain.getName() + "</li>";
					            				}
				            				}
				            			}
			            			}
			            			
			            			htmlBody += "</ul>";
			            			
			            			HTMLPanel html = new HTMLPanel(htmlBody);
			            			html.getElement().getStyle().setPaddingLeft(2, Unit.EM);
			            			
			            			AonDialog dialog = new AonDialog(aonApp.getDescription(), html);
			            			dialog.info();
			            		}
			            	}
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

	}
	
	class ConectaUsersCommand implements ScheduledCommand {

		@Override
		public void execute() {
			conectaUsers(bookingWithOutFeeMenu.getCustomer());
		}
		
		public void conectaUsers(Customer customer) {
			// Create the base URL
			String baseUrl = "/ms/api/booking/customer";

			// Create a URL builder and add query parameters
			UrlBuilder urlBuilder = new UrlBuilder();
			urlBuilder.setProtocol(Window.Location.getProtocol()); // Use the current protocol
//			urlBuilder.setHost("localhost:8080");
			urlBuilder.setHost("aon.solutions"); 
			urlBuilder.setPath(baseUrl);
			
			urlBuilder.setParameter("customer", customer.getId().toString());
			
			// Create the request builder with the complete URL
			RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.GET, urlBuilder.buildString());
			requestBuilder.setHeader("session_id", "AONd95770f269e711eb94390242ac130002");
			
			try {
			    // Send the request
			    requestBuilder.sendRequest(null, new RequestCallback() {
			        public void onResponseReceived(Request request, Response response) {
			            if (response.getStatusCode() == 200) {
			            	String responseBody = response.getText();
			            	List<Booking> bookingList = BookingJSON.parseBookingJSONArr(responseBody);
			            	if(!bookingList.isEmpty()) {
			            		Booking booking = bookingList.get(0);
			            		if(null != booking.getResume() && !booking.getResume().getChilds().isEmpty()) {
			            			List<Domain> childConectaUsers = booking.getResume().getChilds().stream().filter(child -> child.getMaxDefinedUsers() != null && child.getMaxDefinedUsers() > 1).collect(Collectors.toList());
			            			
			            			// Sort child domains
			            			childConectaUsers.sort((o1, o2) -> o1.getDescription().compareTo(o2.getDescription()));
			            			
			            			// Create Widget
			            			String htmlBody = "<ul>";
			            			
			            			for(Domain childConectaUser : childConectaUsers) {
			            				htmlBody += "<li>" + (childConectaUser.getMaxDefinedUsers() - 1) + " x " + childConectaUser.getDescription() + "</li>";
			            			}
			            			
			            			htmlBody += "</ul>";
			            			
			            			HTMLPanel html = new HTMLPanel(htmlBody);
			            			html.getElement().getStyle().setPaddingLeft(2, Unit.EM);
			            			
			            			AonDialog dialog = new AonDialog("Usuario Adicional Conect\u0040", html);
			            			dialog.info();
			            			
			            		}
			            	}
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

	}
	
	class CreateCustomerFeeCommand implements ScheduledCommand {

		@Override
		public void execute() {
			createCustomerFee(bookingWithOutFeeMenu.getItem(), bookingWithOutFeeMenu.getCustomer());
		}
		
		private void createCustomerFee(OldItem oldItem, Customer customer) {
			new CustomerFeeDialog(options, oldItem, customer) {
				
				@Override
				protected void onAccept(Fee fee) {}
				
				@Override
				protected void onCreate(Fee fee) {
					SERVICE.createCustomerFeeList(options.getDomainName(), options.getDomain(), options.getUser(), fee, new AsyncCallback<Fee>() {
						
						@Override
						public void onSuccess(Fee customerFee) {
							AonMessagePanel.showSuccess(messagePanel, "Se ha creado la cuota correctamente");
							setBookingCustomer(customer, customerDomains);
						}
						
						@Override
						public void onFailure(Throwable caught) {
							AonMessagePanel.showError(messagePanel, "Error creando cuota: " + caught.getMessage());
						}
					});
				}

				@Override
				protected void onAccept(Optional<OldItem> item, Optional<Double> price, Optional<String> discountExpr, Optional<Date> startDate, Optional<Date> endDate, Optional<Date> billingDate) {}

				@Override
				protected void onCreate(Fee fee, Integer ritem) {}
				
			};
		}

	}
	
	class UpdateBookingCommand implements ScheduledCommand {

		@Override
		public void execute() {
			updateBooking();
		}
		
		private void updateBooking() {
			new BookingCheckDialog(options, bookingWithOutFeeMenu.getBookingCheck()) {
				
				@Override
				protected void onCreate(BookingCheck bookingCheck) {}
				
				@Override
				protected void onUpdate(BookingCheck bookingCheck) {
					SERVICE.saveBookingCheck(options.getDomainName(), options.getDomain(), options.getUser(), bookingCheck, new AsyncCallback<Void>() {
						
						@Override
						public void onSuccess(Void result) {
							AonMessagePanel.showSuccess(messagePanel, "Se ha actualizado la contrataci\u00f3n correctamente");
							setBookingCustomer(customer, customerDomains);
						}
						
						@Override
						public void onFailure(Throwable caught) {
							AonMessagePanel.showError(messagePanel, "Error actualizando contrataci\u00f3n: " + caught.getMessage());
						}
					});
				}
			};
		}

	}
	
	class RemoveBookingCommand implements ScheduledCommand {

		@Override
		public void execute() {
			removeBooking();
		}
		
		private void removeBooking() {
			AonDialog dialog = new AonDialog("Eliminaci\u00f3n Contrataci\u00f3n",
					new HTML("Se va a proceder a eliminar la contrataci\u00f3n.<br>\u00bfEsta seguro que desea proceder con la eliminaci\u00f3n\u003f. Este proceso ser\u00e1 irreversible"));
			
			dialog.confirm(new AonAcceptDialogCallback() {

				@Override
				public void onCancel() {
					// Nothing to do here
				}

				@Override
				public void onAccept() {
					AonMessagePanel.showLoading(messagePanel, "Elimando contrataci\u00f3n seleccionada ...");
					LinkedList<BookingCheck> selectedBookings = new LinkedList<>();
					selectedBookings.add(new BookingCheck().setId(bookingWithOutFeeMenu.getId()));
					
					SERVICE.deleteBookingList(options.getDomainName(), options.getDomain(), options.getUser(), selectedBookings,new AsyncCallback<Void>() {

						@Override
						public void onFailure(Throwable caught) {
							AonMessagePanel.showError(messagePanel, "Error eliminando contrataci\u00f3n: " + caught.getMessage());
						}

						@Override
						public void onSuccess(Void result) {
							AonMessagePanel.showSuccess(messagePanel, "Se han eliminado la contrataci\u00f3n correctamente");
							setBookingCustomer(customer, customerDomains);
						}
					});
				}
			});
		}

	}
	
	class BookingWithOutFeeMenu extends AonContextMenu {
		
		private Integer id;
		private OldItem item;
		private Customer customer;
		private BookingCheck bookingCheck;
		
		private MenuItem createCustomerFee;
		private MenuItem updateBooking;
		private MenuItem deleteBooking;
		private MenuItem conectaUsers;
		private MenuItem aonAppUsers;
		

		public BookingWithOutFeeMenu() {
			createCustomerFee = addMenuItem("Crear Cuota", new CreateCustomerFeeCommand(), AON.CSS.aonIconAdd(), "createCustomerFee");
			updateBooking = addMenuItem("Act. Contrataci\u00f3n", new UpdateBookingCommand(), AON.CSS.aonIconEdit(), "updateBooking");
			deleteBooking = addMenuItem("Eliminar Contrataci\u00f3n", new RemoveBookingCommand(), AON.CSS.aonIconDelete(), "deleteBooking");
			conectaUsers = addMenuItem("Info. Usuarios", new ConectaUsersCommand(), AON.CSS.aonIconInfo(), "conectaUsers");
			aonAppUsers = addMenuItem("Info. Empresas", new AonAppUsersCommand(), AON.CSS.aonIconInfo(), "aonAppUsers");
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
		
		public BookingCheck getBookingCheck() {
			return bookingCheck;
		}

		public void setBookingCheck(BookingCheck bookingCheck) {
			this.bookingCheck = bookingCheck;
			
			List<String> barCodes = new ArrayList<>();
			String barCode = this.bookingCheck.getItem().getBarcode();
			if(barCode.contains("/")) {
				String[] splits = barCode.split("/");
				for(int i=0; i < splits.length; i++)
					barCodes.add(splits[i].trim());
			} else
				barCodes.add(barCode);
			
			this.conectaUsers.setVisible(barCodes.contains("01.00.USR"));
			
			boolean isChildBarCode = !barCodes.isEmpty() && barCodes.stream().filter(barCodeIt -> barCodeIt.split("\\.").length == 3).count() > 0;
			this.aonAppUsers.setVisible(isChildBarCode && !barCodes.contains("01.00.USR"));
		}
		
		public void setHasFee(boolean hasFee) {
			this.createCustomerFee.setVisible(!hasFee);
		}

	}
	
	// ------- Fee
	
	class EditCustomerFeeCommand implements ScheduledCommand {

		@Override
		public void execute() {
			editCustomerFee();
		}
		
		private void editCustomerFee() {
			new CustomerFeeDialog(feeMenu.getFee(), options) {
				
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
									AonMessagePanel.showSuccess(messagePanel, "Se ha actualizado la cuota correctamente");
									setBookingCustomer(customer, customerDomains);
								}
							});
				}

				@Override
				protected void onAccept(Optional<OldItem> item, Optional<Double> price, Optional<String> discountExpr, Optional<Date> startDate, Optional<Date> endDate, Optional<Date> billingDate) {}

				@Override
				protected void onCreate(Fee fee) {}

				@Override
				protected void onCreate(Fee fee, Integer ritem) {}
				
			};
		}

	}
	
	class RemoveCustomerFeeCommand implements ScheduledCommand {

		@Override
		public void execute() {
			removeCustomerFee();
		}
		
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
					selectedFees.add(new Fee().setId(feeMenu.getId()));
					
					SERVICE.deleteCustomerFeeList(options.getDomainName(), options.getDomain(), options.getUser(), selectedFees,
							new AsyncCallback<Void>() {

								@Override
								public void onFailure(Throwable caught) {
									AonMessagePanel.showError(messagePanel, "Error eliminando cuota: " + caught.getMessage());
								}

								@Override
								public void onSuccess(Void result) {
									AonMessagePanel.showSuccess(messagePanel, "Se han eliminado la cuota correctamente");
									setBookingCustomer(customer, customerDomains);
								}
							});
				}
			});
		}

	}
	
	class CreateBookingCommand implements ScheduledCommand {

		@Override
		public void execute() {
			createBooking();
		}
		
		private void createBooking() {
			new BookingCheckDialog(options, feeMenu.getItem(), feeMenu.getCustomer()) {
				
				@Override
				protected void onCreate(BookingCheck bookingCheck) {
					SERVICE.saveBookingCheck(options.getDomainName(), options.getDomain(), options.getUser(), bookingCheck, new AsyncCallback<Void>() {
						
						@Override
						public void onSuccess(Void result) {
							AonMessagePanel.showSuccess(messagePanel, "Se ha creado la contrataci\u00f3n correctamente");
							setBookingCustomer(customer, customerDomains);
						}
						
						@Override
						public void onFailure(Throwable caught) {
							AonMessagePanel.showError(messagePanel, "Error creando contrataci\u00f3n: " + caught.getMessage());
						}
					});
				}
				
				@Override
				protected void onUpdate(BookingCheck bookingCheck) {}
			};
		}

	}
	
	class FeeMenu extends AonContextMenu {
		
		private Integer id;
		private OldItem item;
		private Customer customer;
		private Fee fee;
		
		private MenuItem editCustomerFee;
		private MenuItem removeCustomerFee;
		private MenuItem createBooking;

		public FeeMenu() {
			editCustomerFee = addMenuItem("Editar Cuota", new EditCustomerFeeCommand(), AON.CSS.aonIconEdit(), "editCustomerFee");
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

		public Fee getFee() {
			return fee;
		}

		public void setFee(Fee fee) {
			this.fee = fee;
		}

	}
	
	// ------- Variables
	
	private BookingWithOutFeeMenu bookingWithOutFeeMenu;
	private FeeMenu feeMenu;
	
	private ScrollPanel scrollPanel;
	private HTMLPanel container;
	
	private HTMLPanel messagePanel;
	private HTMLPanel customerPanel;
	private HTMLPanel domainsPanel;
	
	private HTMLPanel bookingPanel;
	private AonToolbarSmallButton toolbarBookingDiscBtn;
	private DeckPanel bookingDeckPanel;
	private Grid bookingGrid;
	private HTMLPanel bookingMessagePanel;
	
	private HTMLPanel feePanel;
	private AonToolbarSmallButton toolbarFeeDiscBtn;
	private DeckPanel feeDeckPanel;
	private Grid feeGrid;
	private HTMLPanel feeMessagePanel;
	
	private Customer customer;
	private List<DomainCompany> customerDomains;
	
	private boolean isBookingOpen = true;
	private boolean isFeeOpen = true;
	
	private RegistryServiceAsync SERVICE; 
	private RegistryModuleOptions options;
	
	private DateTimeFormat formatDate = DateTimeFormat.getFormat("dd/MM/yyyy");
	private DateTimeFormat formatBillingDate = DateTimeFormat.getFormat("MM/yyyy");
	
	// ------- Constructor
	
	public BookingCustomer(RegistryServiceAsync service, RegistryModuleOptions opt) {
		super("");
		
		this.SERVICE = service;
		this.options = opt;
		
		bookingWithOutFeeMenu = new BookingWithOutFeeMenu();
		feeMenu = new FeeMenu();
		
		scrollPanel = new ScrollPanel();
		scrollPanel.setHeight((Window.getClientHeight() - 170) + "px");
		
		HTMLPanel mainContainer = new HTMLPanel("");
		mainContainer.addStyleName(AON.CSS.aonFlexColumn());
		
		container = new HTMLPanel("");
		container.addStyleName(AON.CSS.aonFlexColumn());
		container.getElement().getStyle().setProperty("margin", "0 1rem");
		
		messagePanel = new HTMLPanel("");
		
		customerPanel = new HTMLPanel("");
		customerPanel.addStyleName(AON.CSS.aonFlexColumn());
		
		domainsPanel = new HTMLPanel("");
		domainsPanel.addStyleName(AON.CSS.aonFlexColumn());
		
		bookingPanel = new HTMLPanel("");
		bookingPanel.addStyleName(AON.CSS.aonFlexColumn());
		
		feePanel = new HTMLPanel("");
		feePanel.addStyleName(AON.CSS.aonFlexColumn());
		
		container.add(customerPanel);
		container.add(domainsPanel);
		
		container.add(bookingPanel);
		container.add(feePanel);
		
		mainContainer.add(messagePanel);
		mainContainer.add(container);
		
		scrollPanel.add(mainContainer);
		
		this.add(scrollPanel);
	}

	public void setBookingCustomer(Customer customer, List<DomainCompany> customerDomains) {
		this.customer = customer;
		this.customerDomains = customerDomains;
		
		this.customerPanel.clear();
		this.domainsPanel.clear();
		
		initializeCustomer();
		initializeDomains();
		
		this.bookingPanel.clear();
		this.feePanel.clear();
		
		initializeBooking();
		initializeFee();
	}
	
	// ---------- Cliente

	private void initializeCustomer() {
		Grid customerTable = new Grid(0, 11);
		customerTable.clear();
		customerTable.setWidth("100%");

		int row = customerTable.insertRow(customerTable.getRowCount());

		Label type = new Label("TIPO");
		Label id = new Label("ID");
		Label eschema = new Label("ESQUEMA");
		Label name = new Label("NOMBRE");
		Label document = new Label("DOCUMENTO");
		Label status = new Label("ESTADO");
		Label billable = new Label("FACT.");
		Label creation = new Label("CREACION");
		Label lastModif = new Label("ULT. MODIF.");
		Label alias = new Label("ALIAS");
		Label action = new Label("");
		
		type.addStyleName(AON.CSS.aonHeaderTable());
		id.addStyleName(AON.CSS.aonHeaderTable());
		eschema.addStyleName(AON.CSS.aonHeaderTable());
		name.addStyleName(AON.CSS.aonHeaderTable());
		document.addStyleName(AON.CSS.aonHeaderTable());
		status.addStyleName(AON.CSS.aonHeaderTable());
		billable.addStyleName(AON.CSS.aonHeaderTable());
		creation.addStyleName(AON.CSS.aonHeaderTable());
		lastModif.addStyleName(AON.CSS.aonHeaderTable());
		alias.addStyleName(AON.CSS.aonHeaderTable());
		action.addStyleName(AON.CSS.aonHeaderTable());

		customerTable.setWidget(row, 0, type);
		customerTable.setWidget(row, 1, id);
		customerTable.setWidget(row, 2, eschema);
		customerTable.setWidget(row, 3, name);
		customerTable.setWidget(row, 4, document);
		customerTable.setWidget(row, 5, status);
		customerTable.setWidget(row, 6, billable);
		customerTable.setWidget(row, 7, creation);
		customerTable.setWidget(row, 8, lastModif);
		customerTable.setWidget(row, 9, alias);
		customerTable.setWidget(row, 10, action);
		
		customerTable.getCellFormatter().addStyleName(row, 0, AON.CSS.aonHeaderSticky());
		customerTable.getCellFormatter().addStyleName(row, 1, AON.CSS.aonHeaderSticky());
		customerTable.getCellFormatter().addStyleName(row, 2, AON.CSS.aonHeaderSticky());
		customerTable.getCellFormatter().addStyleName(row, 3, AON.CSS.aonHeaderSticky());
		customerTable.getCellFormatter().addStyleName(row, 4, AON.CSS.aonHeaderSticky());
		customerTable.getCellFormatter().addStyleName(row, 5, AON.CSS.aonHeaderSticky());
		customerTable.getCellFormatter().addStyleName(row, 6, AON.CSS.aonHeaderSticky());
		customerTable.getCellFormatter().addStyleName(row, 7, AON.CSS.aonHeaderSticky());
		customerTable.getCellFormatter().addStyleName(row, 8, AON.CSS.aonHeaderSticky());
		customerTable.getCellFormatter().addStyleName(row, 9, AON.CSS.aonHeaderSticky());
		customerTable.getCellFormatter().addStyleName(row, 10, AON.CSS.aonHeaderSticky());
		
		customerTable.getColumnFormatter().getElement(0).getStyle().setWidth(65, Unit.PX);
		customerTable.getColumnFormatter().getElement(1).getStyle().setWidth(60, Unit.PX);
		customerTable.getColumnFormatter().getElement(2).getStyle().setWidth(190, Unit.PX);

		customerTable.getColumnFormatter().getElement(4).getStyle().setWidth(80, Unit.PX);
		customerTable.getColumnFormatter().getElement(5).getStyle().setWidth(60, Unit.PX);
		customerTable.getColumnFormatter().getElement(6).getStyle().setWidth(50, Unit.PX);
		customerTable.getColumnFormatter().getElement(7).getStyle().setWidth(80, Unit.PX);
		customerTable.getColumnFormatter().getElement(8).getStyle().setWidth(80, Unit.PX);
		customerTable.getColumnFormatter().getElement(9).getStyle().setWidth(215, Unit.PX);
		customerTable.getColumnFormatter().getElement(10).getStyle().setWidth(25, Unit.PX);
		
		int newRow = customerTable.insertRow(customerTable.getRowCount());
		
		Label domainTypeLabel  = new Label("Cliente");
		Label idLabel = new Label(this.customer.getId().toString());
		Label schemaLabel = new Label(this.customer.getDomain().getName());
		Label descriptionLabel = new Label(this.customer.getName());
		Label documentLabel = new Label(this.customer.getDocument());
		Label statusLabel = new Label(this.customer.getStatus().getDescription());
		Label billableLabel = new Label(this.customer.isBillable() ? "SI" : "NO");
		Label expirationLabel = new Label(formatDate(this.customer.getCreationDate()));
		Label lastAccessLabel = new Label(formatDate(this.customer.getModificationDate()));
		
		Label nameLabel = new Label(this.customer.getAlias());
		nameLabel.getElement().getStyle().setCursor(Cursor.POINTER);
		nameLabel.addClickHandler(e ->{
			enableRemoteDomain(customer.getDomain().getId(), customer.getAlias());
		});
		
		// Status
		if (this.customer.getStatus().equals(RegistryStatus.INACTIVE)) {
			statusLabel.getElement().getStyle().setFontWeight(FontWeight.BOLD);
			statusLabel.getElement().getStyle().setColor("red");
		} else if (this.customer.getStatus().equals(RegistryStatus.BLOCKED)) {
			statusLabel.getElement().getStyle().setFontWeight(FontWeight.BOLD);
			statusLabel.getElement().getStyle().setColor("orange");
		}
		
		if(!this.customer.isBillable()) {
			billableLabel.getElement().getStyle().setFontWeight(FontWeight.BOLD);
			billableLabel.getElement().getStyle().setColor("orange");
		}
		
		customerTable.setWidget(newRow, 0, domainTypeLabel);
		customerTable.setWidget(newRow, 1, idLabel);
		customerTable.setWidget(newRow, 2, schemaLabel);
		customerTable.setWidget(newRow, 3, descriptionLabel);
		customerTable.setWidget(newRow, 4, documentLabel);
		customerTable.setWidget(newRow, 5, statusLabel);
		customerTable.setWidget(newRow, 6, billableLabel);
		customerTable.setWidget(newRow, 7, expirationLabel);
		customerTable.setWidget(newRow, 8, lastAccessLabel);
		customerTable.setWidget(newRow, 9, nameLabel);
		
		HTMLPanel buttonPanel = new HTMLPanel("");
		buttonPanel.addStyleName(AON.CSS.aonItemFlex());
		buttonPanel.getElement().getStyle().setProperty("min-width", "50px");
		
		AonTableButton syncBtn = new AonTableButton("Sincronizar Contrataci\u00f3n", AON.CSS.aonIconSync());
		if (newRow % 2 == 0) syncBtn.addStyleName(AON.CSS.aonOddTableRow());
		syncBtn.addClickHandler(e -> {
			syncCustomerDomains();
		});
		
		AonTableButton unSyncBtn = new AonTableButton("Desincronizar Contrataci\u00f3n", AON.CSS.aonIconSyncDisabled());
		if (newRow % 2 == 0) unSyncBtn.addStyleName(AON.CSS.aonOddTableRow());
		unSyncBtn.addClickHandler(e -> {
			unSyncCustomerDomains();
		});
		
		buttonPanel.add(syncBtn);
		buttonPanel.add(unSyncBtn);
		
		customerTable.setWidget(newRow, 10, buttonPanel);
		
		if (newRow % 2 == 0) {
			domainTypeLabel.addStyleName(AON.CSS.aonOddTableRow());
			idLabel.addStyleName(AON.CSS.aonOddTableRow());
			schemaLabel.addStyleName(AON.CSS.aonOddTableRow());
			descriptionLabel.addStyleName(AON.CSS.aonOddTableRow());
			documentLabel.addStyleName(AON.CSS.aonOddTableRow());
			statusLabel.addStyleName(AON.CSS.aonOddTableRow());
			billableLabel.addStyleName(AON.CSS.aonOddTableRow());
			expirationLabel.addStyleName(AON.CSS.aonOddTableRow());
			lastAccessLabel.addStyleName(AON.CSS.aonOddTableRow());
			nameLabel.addStyleName(AON.CSS.aonOddTableRow());
			
			customerTable.getCellFormatter().addStyleName(newRow, 0, AON.CSS.aonOddTableRow());
			customerTable.getCellFormatter().addStyleName(newRow, 1, AON.CSS.aonOddTableRow());
			customerTable.getCellFormatter().addStyleName(newRow, 2, AON.CSS.aonOddTableRow());
			customerTable.getCellFormatter().addStyleName(newRow, 3, AON.CSS.aonOddTableRow());
			customerTable.getCellFormatter().addStyleName(newRow, 4, AON.CSS.aonOddTableRow());
			customerTable.getCellFormatter().addStyleName(newRow, 5, AON.CSS.aonOddTableRow());
			customerTable.getCellFormatter().addStyleName(newRow, 6, AON.CSS.aonOddTableRow());
			customerTable.getCellFormatter().addStyleName(newRow, 7, AON.CSS.aonOddTableRow());
			customerTable.getCellFormatter().addStyleName(newRow, 8, AON.CSS.aonOddTableRow());
			customerTable.getCellFormatter().addStyleName(newRow, 9, AON.CSS.aonOddTableRow());
			customerTable.getCellFormatter().addStyleName(newRow, 10, AON.CSS.aonOddTableRow());
		}
		
		customerTable.getCellFormatter().getElement(newRow, 0).getStyle().setTextAlign(TextAlign.CENTER);
		customerTable.getCellFormatter().getElement(newRow, 1).getStyle().setTextAlign(TextAlign.CENTER);
		customerTable.getCellFormatter().getElement(newRow, 4).getStyle().setTextAlign(TextAlign.CENTER);
		customerTable.getCellFormatter().getElement(newRow, 5).getStyle().setTextAlign(TextAlign.CENTER);
		customerTable.getCellFormatter().getElement(newRow, 6).getStyle().setTextAlign(TextAlign.CENTER);
		customerTable.getCellFormatter().getElement(newRow, 7).getStyle().setTextAlign(TextAlign.CENTER);
		customerTable.getCellFormatter().getElement(newRow, 8).getStyle().setTextAlign(TextAlign.CENTER);
		customerTable.getCellFormatter().getElement(newRow, 10).getStyle().setTextAlign(TextAlign.CENTER);
		
		customerTable.getRowFormatter().getElement(newRow).getStyle().setHeight(25.00, Unit.PX);
		
		customerPanel.add(customerTable);
		
	}

	private void syncCustomerDomains() {
		AonDialog dialog = new AonDialog("Sincronizaci\u00f3n Dominios Cliente",
				new HTML("Se va a proceder a sincronizar los dominios del cliente <b>" + this.customer.getName() + "</b>.<br>\u00bfEsta seguro que desea proceder con la sincronizaci\u00f3n\u003f"));
		
		dialog.confirm(new AonAcceptDialogCallback() {

			@Override
			public void onCancel() {
				// Nothing to do here
			}

			@Override
			public void onAccept() {
				syncDomains();
			}
		});
	}
	
	private void unSyncCustomerDomains() {
		AonDialog dialog = new AonDialog("Desincronizaci\u00f3n Dominios Cliente",
				new HTML("Se va a proceder a desincronizar los dominios del cliente <b>" + this.customer.getName() + "</b>.<br>\u00bfEsta seguro que desea proceder con la desincronizaci\u00f3n\u003f. Este proceso sera irreversible."));
		
		dialog.confirm(new AonAcceptDialogCallback() {

			@Override
			public void onCancel() {
				// Nothing to do here
			}

			@Override
			public void onAccept() {
				unSyncDomains();
			}
		});
	}
	
	// ---------- Dominios

	private void initializeDomains() {
		ScrollPanel scrollPanel = new ScrollPanel();
		scrollPanel.getElement().getStyle().setProperty("max-height", "130px");
		
		Grid domainTable = new Grid(0, 11);
		domainTable.clear();
		domainTable.setWidth("100%");

		int row = domainTable.insertRow(domainTable.getRowCount());

		Label type = new Label("TIPO");
		Label id = new Label("ID");
		Label eschema = new Label("ESQUEMA");
		Label name = new Label("NOMBRE");
		Label document = new Label("DOCUMENTO");
		Label status = new Label("ESTADO");
		Label billable = new Label("FACT.");
		Label expire = new Label("EXPIRA");
		Label lastAccess = new Label("ULT. ACCESO");
		Label description = new Label("URL");
		Label url = new Label("");
		
		type.addStyleName(AON.CSS.aonHeaderTable());
		id.addStyleName(AON.CSS.aonHeaderTable());
		eschema.addStyleName(AON.CSS.aonHeaderTable());
		name.addStyleName(AON.CSS.aonHeaderTable());
		document.addStyleName(AON.CSS.aonHeaderTable());
		status.addStyleName(AON.CSS.aonHeaderTable());
		billable.addStyleName(AON.CSS.aonHeaderTable());
		expire.addStyleName(AON.CSS.aonHeaderTable());
		lastAccess.addStyleName(AON.CSS.aonHeaderTable());
		description.addStyleName(AON.CSS.aonHeaderTable());
		url.addStyleName(AON.CSS.aonHeaderTable());

		domainTable.setWidget(row, 0, type);
		domainTable.setWidget(row, 1, id);
		domainTable.setWidget(row, 2, eschema);
		domainTable.setWidget(row, 3, name);
		domainTable.setWidget(row, 4, document);
		domainTable.setWidget(row, 5, status);
		domainTable.setWidget(row, 6, billable);
		domainTable.setWidget(row, 7, expire);
		domainTable.setWidget(row, 8, lastAccess);
		domainTable.setWidget(row, 9, description);
		domainTable.setWidget(row, 10, url);
		
		domainTable.getCellFormatter().addStyleName(row, 0, AON.CSS.aonHeaderSticky());
		domainTable.getCellFormatter().addStyleName(row, 1, AON.CSS.aonHeaderSticky());
		domainTable.getCellFormatter().addStyleName(row, 2, AON.CSS.aonHeaderSticky());
		domainTable.getCellFormatter().addStyleName(row, 3, AON.CSS.aonHeaderSticky());
		domainTable.getCellFormatter().addStyleName(row, 4, AON.CSS.aonHeaderSticky());
		domainTable.getCellFormatter().addStyleName(row, 5, AON.CSS.aonHeaderSticky());
		domainTable.getCellFormatter().addStyleName(row, 6, AON.CSS.aonHeaderSticky());
		domainTable.getCellFormatter().addStyleName(row, 7, AON.CSS.aonHeaderSticky());
		domainTable.getCellFormatter().addStyleName(row, 8, AON.CSS.aonHeaderSticky());
		domainTable.getCellFormatter().addStyleName(row, 9, AON.CSS.aonHeaderSticky());
		domainTable.getCellFormatter().addStyleName(row, 10, AON.CSS.aonHeaderSticky());
		
		domainTable.getColumnFormatter().getElement(0).getStyle().setWidth(65, Unit.PX);
		domainTable.getColumnFormatter().getElement(1).getStyle().setWidth(60, Unit.PX);
		domainTable.getColumnFormatter().getElement(2).getStyle().setWidth(190, Unit.PX);

		domainTable.getColumnFormatter().getElement(4).getStyle().setWidth(80, Unit.PX);
		domainTable.getColumnFormatter().getElement(5).getStyle().setWidth(60, Unit.PX);
		domainTable.getColumnFormatter().getElement(6).getStyle().setWidth(50, Unit.PX);
		domainTable.getColumnFormatter().getElement(7).getStyle().setWidth(80, Unit.PX);
		domainTable.getColumnFormatter().getElement(8).getStyle().setWidth(80, Unit.PX);
		domainTable.getColumnFormatter().getElement(9).getStyle().setWidth(215, Unit.PX);
		domainTable.getColumnFormatter().getElement(10).getStyle().setWidth(25, Unit.PX);
		
		scrollPanel.add(domainTable);
		
		// Create empty line for sync
		if(null == customerDomains || customerDomains.isEmpty()) {
			int newRow = domainTable.insertRow(domainTable.getRowCount());
			
			domainTable.setWidget(newRow, 0, new Label());
			domainTable.setWidget(newRow, 1, new Label());
			domainTable.setWidget(newRow, 2, new Label());
			domainTable.setWidget(newRow, 3, new Label());
			domainTable.setWidget(newRow, 4, new Label());
			domainTable.setWidget(newRow, 5, new Label());
			domainTable.setWidget(newRow, 6, new Label());
			domainTable.setWidget(newRow, 7, new Label());
			domainTable.setWidget(newRow, 8, new Label());
			domainTable.setWidget(newRow, 9, new Label());
			
			HTMLPanel buttonPanel = new HTMLPanel("");
			buttonPanel.addStyleName(AON.CSS.aonItemFlex());
			buttonPanel.getElement().getStyle().setProperty("min-width", "50px");
						
			AonTableButton syncDomainBtn = new AonTableButton("Vincular Dominio", AON.CSS.aonIconLink());
			syncDomainBtn.addClickHandler(e -> syncCustomerToDomain());
			
			buttonPanel.add(syncDomainBtn);
			
			domainTable.setWidget(newRow, 10, buttonPanel);
			
			domainTable.getCellFormatter().getElement(newRow, 10).getStyle().setTextAlign(TextAlign.CENTER);
			
			domainTable.getRowFormatter().getElement(newRow).getStyle().setHeight(25.00, Unit.PX);
			
		} else {
			for(DomainCompany domainCompany : customerDomains) {
				int newRow = domainTable.insertRow(domainTable.getRowCount());
				
				Label domainTypeLabel  = new Label(domainCompany.getDomain().getDomainType().getName());
				Label idLabel = new Label(domainCompany.getDomain().getId().toString());
				Label schemaLabel = new Label(domainCompany.getSchema());
				Label descriptionLabel = new Label(domainCompany.getDomain().getDescription());
				Label documentLabel = new Label(domainCompany.getCompany().getDocument());
				Label statusLabel = new Label(domainCompany.getDomain().isActive() ? "Activo" : "Inactivo");
				Label billableLabel = new Label(domainCompany.getDomain().getAonStatus().equals(AonStatus.BILLABLE) ? "SI" : "NO");
				Label expirationLabel = new Label(formatDate(domainCompany.getDomain().getExpirationDate()));
				Label lastAccessLabel = new Label(formatDate(domainCompany.getDomain().getLastAccessDate()));
				Label nameLabel = new Label(domainCompany.getDomain().getName());
				nameLabel.getElement().getStyle().setCursor(Cursor.POINTER);
				nameLabel.addClickHandler(e ->{
					enableRemoteDomain(domainCompany.getDomain().getId(), domainCompany.getDomain().getName());
				});
				
				// Status
				if (!domainCompany.getDomain().isActive()) {
					statusLabel.getElement().getStyle().setFontWeight(FontWeight.BOLD);
					statusLabel.getElement().getStyle().setColor("red");
				}
				
				if (domainCompany.getDomain().getAonStatus().equals(AonStatus.NOT_BILLABLE)) {
					billableLabel.getElement().getStyle().setFontWeight(FontWeight.BOLD);
					billableLabel.getElement().getStyle().setColor("orange");
				}
				
				if(null != domainCompany.getDomain().getExpirationDate()) {
					Date today = new Date();
					if(domainCompany.getDomain().getExpirationDate().after(today)) {
						expirationLabel.getElement().getStyle().setFontWeight(FontWeight.BOLD);
						expirationLabel.getElement().getStyle().setColor("orange");
					} else if(domainCompany.getDomain().getExpirationDate().before(today)) {
						expirationLabel.getElement().getStyle().setFontWeight(FontWeight.BOLD);
						expirationLabel.getElement().getStyle().setColor("red");
					}
				}
				
				domainTable.setWidget(newRow, 0, domainTypeLabel);
				domainTable.setWidget(newRow, 1, idLabel);
				domainTable.setWidget(newRow, 2, schemaLabel);
				domainTable.setWidget(newRow, 3, descriptionLabel);
				domainTable.setWidget(newRow, 4, documentLabel);
				domainTable.setWidget(newRow, 5, statusLabel);
				domainTable.setWidget(newRow, 6, billableLabel);
				domainTable.setWidget(newRow, 7, expirationLabel);
				domainTable.setWidget(newRow, 8, lastAccessLabel);
				domainTable.setWidget(newRow, 9, nameLabel);
				
				HTMLPanel buttonPanel = new HTMLPanel("");
				buttonPanel.addStyleName(AON.CSS.aonItemFlex());
				buttonPanel.getElement().getStyle().setProperty("min-width", "50px");
							
				AonTableButton usersBtn = new AonTableButton("Usuarios Dominio", AON.CSS.aonIconGroup());
				usersBtn.addClickHandler(e -> openUserTooltip(domainCompany.getDomain()));
				
				AonTableButton unsyncDomainBtn = new AonTableButton("Desvincular Dominio", AON.CSS.aonIconLinkOff());
				unsyncDomainBtn.addClickHandler(e -> unSyncDomain(domainCompany));
				
				buttonPanel.add(usersBtn);
				buttonPanel.add(unsyncDomainBtn);
				
				domainTable.setWidget(newRow, 10, buttonPanel);
				
				if (newRow % 2 == 0) {
					domainTypeLabel.addStyleName(AON.CSS.aonOddTableRow());
					idLabel.addStyleName(AON.CSS.aonOddTableRow());
					schemaLabel.addStyleName(AON.CSS.aonOddTableRow());
					descriptionLabel.addStyleName(AON.CSS.aonOddTableRow());
					documentLabel.addStyleName(AON.CSS.aonOddTableRow());
					statusLabel.addStyleName(AON.CSS.aonOddTableRow());
					billableLabel.addStyleName(AON.CSS.aonOddTableRow());
					expirationLabel.addStyleName(AON.CSS.aonOddTableRow());
					lastAccessLabel.addStyleName(AON.CSS.aonOddTableRow());
					nameLabel.addStyleName(AON.CSS.aonOddTableRow());
					usersBtn.addStyleName(AON.CSS.aonOddTableRow());
					
					domainTable.getCellFormatter().addStyleName(newRow, 0, AON.CSS.aonOddTableRow());
					domainTable.getCellFormatter().addStyleName(newRow, 1, AON.CSS.aonOddTableRow());
					domainTable.getCellFormatter().addStyleName(newRow, 2, AON.CSS.aonOddTableRow());
					domainTable.getCellFormatter().addStyleName(newRow, 3, AON.CSS.aonOddTableRow());
					domainTable.getCellFormatter().addStyleName(newRow, 4, AON.CSS.aonOddTableRow());
					domainTable.getCellFormatter().addStyleName(newRow, 5, AON.CSS.aonOddTableRow());
					domainTable.getCellFormatter().addStyleName(newRow, 6, AON.CSS.aonOddTableRow());
					domainTable.getCellFormatter().addStyleName(newRow, 7, AON.CSS.aonOddTableRow());
					domainTable.getCellFormatter().addStyleName(newRow, 8, AON.CSS.aonOddTableRow());
					domainTable.getCellFormatter().addStyleName(newRow, 9, AON.CSS.aonOddTableRow());
					domainTable.getCellFormatter().addStyleName(newRow, 10, AON.CSS.aonOddTableRow());
				}
				
				domainTable.getCellFormatter().getElement(newRow, 0).getStyle().setTextAlign(TextAlign.CENTER);
				domainTable.getCellFormatter().getElement(newRow, 1).getStyle().setTextAlign(TextAlign.CENTER);
				domainTable.getCellFormatter().getElement(newRow, 4).getStyle().setTextAlign(TextAlign.CENTER);
				domainTable.getCellFormatter().getElement(newRow, 5).getStyle().setTextAlign(TextAlign.CENTER);
				domainTable.getCellFormatter().getElement(newRow, 6).getStyle().setTextAlign(TextAlign.CENTER);
				domainTable.getCellFormatter().getElement(newRow, 7).getStyle().setTextAlign(TextAlign.CENTER);
				domainTable.getCellFormatter().getElement(newRow, 8).getStyle().setTextAlign(TextAlign.CENTER);
				domainTable.getCellFormatter().getElement(newRow, 10).getStyle().setTextAlign(TextAlign.CENTER);
				
				domainTable.getRowFormatter().getElement(newRow).getStyle().setHeight(25.00, Unit.PX);
			}
		}
		
		domainsPanel.add(scrollPanel);
		
	}

	private void syncCustomerToDomain() {
		AonMessagePanel.showLoading(messagePanel, "Obteniendo dominios ...");
		
		// Create the base URL
		String baseUrl = "/ms/api/domain/";

		// Create a URL builder and add query parameters
		UrlBuilder urlBuilder = new UrlBuilder();
		urlBuilder.setProtocol(Window.Location.getProtocol()); // Use the current protocol
//		urlBuilder.setHost("localhost:8080");
		urlBuilder.setHost("aon.solutions"); 
		urlBuilder.setPath(baseUrl);
		
		// Create the request builder with the complete URL
		RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.GET, urlBuilder.buildString());
		requestBuilder.setHeader("session_id", "AONd95770f269e711eb94390242ac130002");
		
		try {
		    // Send the request
		    requestBuilder.sendRequest(null, new RequestCallback() {
		        public void onResponseReceived(Request request, Response response) {
		            if (response.getStatusCode() == 200) {

		                String responseBody = response.getText();
		                List<DomainCompany> companies = DomainCompanyJSON.parseDomainCompanyJSONArr(responseBody);
		                AonMessagePanel.hideMessage(messagePanel);
		                showSelectSyncDomainsDialog(companies);
		                
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
	
	private void showSelectSyncDomainsDialog(List<DomainCompany> companies) {
		new AonDomainSyncSelectionDialog("Viculaci\u00f3n Dominios", companies, customer) {
			
			@Override
			protected void onAccept(DomainCompany domainCompany) {
				// Unsync Domain aonCustomer
            	AonMessagePanel.showLoading(messagePanel, "Vinculando dominio del cliente " + customer.getName() + " ...");
        		
        		// Create the base URL
        		String baseUrl = "/ms/api/domain/";

        		// Create a URL builder and add query parameters
        		UrlBuilder urlBuilder = new UrlBuilder();
        		urlBuilder.setProtocol(Window.Location.getProtocol()); // Use the current protocol
//        		urlBuilder.setHost("localhost:8080");
        		urlBuilder.setHost("aon.solutions"); 
        		urlBuilder.setPath(baseUrl);
        		
        		// Create the request builder with the complete URL
        		RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.PUT, urlBuilder.buildString());
        		requestBuilder.setHeader("session_id", "AONd95770f269e711eb94390242ac130002");
        		
        		JSONObject body = new JSONObject();
        		body.put("customer", new JSONNumber(customer.getId()));
        		
        		JSONArray domains = new JSONArray();
        		domains.set(0, DomainCompanyJSON.domainCompanyToJSON(domainCompany));
        		body.put("domains", domains);
        		
        		try {
        		    // Send the request
        		    requestBuilder.sendRequest(body.toString(), new RequestCallback() {
        		        public void onResponseReceived(Request request, Response response) {
        		            if (response.getStatusCode() == 200) {
        		            	
        		            	AonMessagePanel.showLoading(messagePanel, "Sincronizando contrataci\u00f3n para el cliente " + customer.getName() + " ...");
        		        		
        		        		// Create the base URL
        		        		String baseUrl = "/ms/api/domain/booking-customer/";

        		        		// Create a URL builder and add query parameters
        		        		UrlBuilder urlBuilder = new UrlBuilder();
        		        		urlBuilder.setProtocol(Window.Location.getProtocol()); // Use the current protocol
        		        		urlBuilder.setHost(Window.Location.getHost()); 
        		        		urlBuilder.setPath(baseUrl);
        		        		
        		        		// Create the request builder with the complete URL
        		        		RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.PUT, urlBuilder.buildString());
        		        		requestBuilder.setHeader("session_id", "AONd95770f269e711eb94390242ac130002");
        		        		
        		        		requestBuilder.setHeader("domain_name", options.getDomainName());
        		        		requestBuilder.setHeader("domain_login", options.getUser());
        		        		requestBuilder.setHeader("domain_id", String.valueOf(options.getDomain()));
        		        		
        		        		JSONObject body = new JSONObject();
        		        		body.put("customer", new JSONNumber(customer.getId()));
        		        		
        		        		try {
        		        		    // Send the request
        		        		    requestBuilder.sendRequest(body.toString(), new RequestCallback() {
        		        		        public void onResponseReceived(Request request, Response response) {
        		        		            if (response.getStatusCode() == 200) {
        		        		            	
        		        		            	AonMessagePanel.showSuccess(messagePanel, "La sincronizaci\u00f3n del cliente " + customer.getName() + " se ha realizado correctamente");
        		        	            		
        		        		            	Timer timer = new Timer() {
        		        		           		     @Override
        		        		           		     public void run() {
        		        		           		    	AonMessagePanel.showLoading(messagePanel, "Obteniendo dominios del cliente ...");
        		        		           				
        		        		           				// Create the base URL
        		        		           				String baseUrl = "/ms/api/domain/" + customer.getId().toString();

        		        		           				// Create a URL builder and add query parameters
        		        		           				UrlBuilder urlBuilder = new UrlBuilder();
        		        		           				urlBuilder.setProtocol(Window.Location.getProtocol()); // Use the current protocol
//        		        		           				urlBuilder.setHost("localhost:8080");
        		        		           				urlBuilder.setHost("aon.solutions"); 
        		        		           				urlBuilder.setPath(baseUrl);
        		        		           				
        		        		           				// Create the request builder with the complete URL
        		        		           				RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.GET, urlBuilder.buildString());
        		        		           				requestBuilder.setHeader("session_id", "AONd95770f269e711eb94390242ac130002");
        		        		           				
        		        		           				try {
        		        		           				    // Send the request
        		        		           				    requestBuilder.sendRequest(null, new RequestCallback() {
        		        		           				        public void onResponseReceived(Request request, Response response) {
        		        		           				            if (response.getStatusCode() == 200) {
        		        		           				            	
        		        		           				                String responseBody = response.getText();
        		        		           				                List<DomainCompany> companies = DomainCompanyJSON.parseDomainCompanyJSONArr(responseBody);
        		        		           				                AonMessagePanel.hideMessage(messagePanel);
        		        		           				                
        		        		           				                setBookingCustomer(customer, companies);
        		        		           				                
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
        		        		           		timer.schedule(2500);
        		          		           		
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
	
	private void unSyncDomain(DomainCompany domainCompany) {
		AonMessagePanel.showLoading(messagePanel, "Desincronizando contrataci\u00f3n para el cliente " + customer.getName() + " ...");
		
		// Create the base URL
		String baseUrl = "/ms/api/domain/booking/";

		// Create a URL builder and add query parameters
		UrlBuilder urlBuilder = new UrlBuilder();
		urlBuilder.setProtocol(Window.Location.getProtocol()); // Use the current protocol
		urlBuilder.setHost(Window.Location.getHost()); 
		urlBuilder.setPath(baseUrl);
		
		// Create the request builder with the complete URL
		RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.DELETE, urlBuilder.buildString());
		requestBuilder.setHeader("session_id", "AONd95770f269e711eb94390242ac130002");
		
		JSONObject body = new JSONObject();
		body.put("customer", new JSONNumber(customer.getId()));
		
		try {
		    // Send the request
		    requestBuilder.sendRequest(body.toString(), new RequestCallback() {
		        public void onResponseReceived(Request request, Response response) {
		            if (response.getStatusCode() == 200) {
		            	
		            	// Unsync Domain aonCustomer
		            	AonMessagePanel.showLoading(messagePanel, "Desvinculando dominio del cliente " + customer.getName() + " ...");
		        		
		        		// Create the base URL
		        		String baseUrl = "/ms/api/domain/";

		        		// Create a URL builder and add query parameters
		        		UrlBuilder urlBuilder = new UrlBuilder();
		        		urlBuilder.setProtocol(Window.Location.getProtocol()); // Use the current protocol
//		        		urlBuilder.setHost("localhost:8080");
		        		urlBuilder.setHost("aon.solutions"); 
		        		urlBuilder.setPath(baseUrl);
		        		
		        		// Create the request builder with the complete URL
		        		RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.PUT, urlBuilder.buildString());
		        		requestBuilder.setHeader("session_id", "AONd95770f269e711eb94390242ac130002");
		        		
		        		JSONObject body = new JSONObject();
		        		
		        		JSONArray domains = new JSONArray();
		        		domains.set(0, DomainCompanyJSON.domainCompanyToJSON(domainCompany));
		        		body.put("domains", domains);
		        		
		        		try {
		        		    // Send the request
		        		    requestBuilder.sendRequest(body.toString(), new RequestCallback() {
		        		        public void onResponseReceived(Request request, Response response) {
		        		            if (response.getStatusCode() == 200) {
		        		            	
		        		            	AonMessagePanel.showSuccess(messagePanel, "Dominio desvinculado del cliente correctamente");
		        		            	
		        		            	Timer timer = new Timer() {
			       		           		     @Override
			       		           		     public void run() {
			       		           		    	// Remove unsync domain from customerDomains
			       		           		    	customerDomains = customerDomains.stream().filter(domainCompanyIt -> !domainCompanyIt.getDomain().getId().equals(domainCompany.getDomain().getId())).collect(Collectors.toList());
			       		           		    	setBookingCustomer(customer, customerDomains);
			       		           		     }
			       		           		};	
			       		           		timer.schedule(2500);
		        		            	
		          		           		
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

	private String formatDate(Date date) {
		if(null == date) return "";
		return formatDate.format(date);
	}
	
	// -------- Booking
	
	private void initializeBooking() {
		bookingDeckPanel = new DeckPanel();
		createBookingGrid();
		createBookingWithOutFeeMessage();
		
		bookingDeckPanel.add(bookingGrid);
		bookingDeckPanel.add(bookingMessagePanel);
		
		bookingPanel.add(createBookingWithoutFeeToolbar());
		bookingPanel.add(bookingDeckPanel);
	}
	
	private void showBookingGrid() {
		bookingDeckPanel.showWidget(0);
	}
	
	private void showBookingMessage() {
		bookingDeckPanel.showWidget(1);
	}
	
	private void createBookingGrid() {
		bookingGrid = new Grid(0, 6);
		bookingGrid.clear();
		bookingGrid.setWidth("100%");

		int row = bookingGrid.insertRow(bookingGrid.getRowCount());

		Label concept = new Label("PRODUCTO");
		Label code = new Label("CODIGO");
		Label fee = new Label("CUOTA");
		Label conceptStatus = new Label("ESTADO");
		Label quantity = new Label("CANTIDAD");
		Label action = new Label("");
		
		concept.addStyleName(AON.CSS.aonHeaderTable());
		code.addStyleName(AON.CSS.aonHeaderTable());
		fee.addStyleName(AON.CSS.aonHeaderTable());
		conceptStatus.addStyleName(AON.CSS.aonHeaderTable());
		conceptStatus.addStyleName(AON.CSS.aonHeaderTable());
		quantity.addStyleName(AON.CSS.aonHeaderTable());
		action.addStyleName(AON.CSS.aonHeaderTable());

		bookingGrid.setWidget(row, 0, concept);
		bookingGrid.setWidget(row, 1, code);
		bookingGrid.setWidget(row, 2, fee);
		bookingGrid.setWidget(row, 3, conceptStatus);
		bookingGrid.setWidget(row, 4, quantity);
		bookingGrid.setWidget(row, 5, action);
		
		bookingGrid.getCellFormatter().addStyleName(row, 0, AON.CSS.aonHeaderSticky());
		bookingGrid.getCellFormatter().addStyleName(row, 1, AON.CSS.aonHeaderSticky());
		bookingGrid.getCellFormatter().addStyleName(row, 2, AON.CSS.aonHeaderSticky());
		bookingGrid.getCellFormatter().addStyleName(row, 3, AON.CSS.aonHeaderSticky());
		bookingGrid.getCellFormatter().addStyleName(row, 4, AON.CSS.aonHeaderSticky());
		bookingGrid.getCellFormatter().addStyleName(row, 5, AON.CSS.aonHeaderSticky());
		
		setColumnWidthBooking();
		fillBooking();
	}

	private void setColumnWidthBooking() {
		bookingGrid.getColumnFormatter().getElement(1).getStyle().setWidth(160, Unit.PX);
		bookingGrid.getColumnFormatter().getElement(2).getStyle().setWidth(50, Unit.PX);
		bookingGrid.getColumnFormatter().getElement(3).getStyle().setWidth(85, Unit.PX);
		bookingGrid.getColumnFormatter().getElement(4).getStyle().setWidth(160, Unit.PX);
		bookingGrid.getColumnFormatter().getElement(5).getStyle().setWidth(25, Unit.PX);
	}

	private void fillBooking() {
		CustomerFeeParams params = new CustomerFeeParams()
				.setDomain(options.getDomain())
				.setCustomer(this.customer.getId())
				.setOffset(0)
				.setLimit(100);
		
		SERVICE.getCustomerBookingCheckList(options.getDomainName(), options.getDomain(), options.getUser(), params,
				new AsyncCallback<LinkedList<BookingCheck>>() {

					@Override
					public void onFailure(Throwable caught) {
						AonMessagePanel.showError(messagePanel, "Error cargando panel de facturaci\u00f3n: " + caught.getMessage());
					}

					@Override
					public void onSuccess(LinkedList<BookingCheck> bookingCheckListDB) {
						if(bookingCheckListDB.isEmpty()) showBookingMessage();
						else {
							bookingCheckListDB.sort((o1, o2) -> o2.hasFee().compareTo(o1.hasFee()));
							fillBookingGrid(bookingCheckListDB);
						}
					}
				});
	}

	private void fillBookingGrid(LinkedList<BookingCheck> bookingCheckListDB) {
		showBookingGrid();
		
		for(BookingCheck bookingCheck : bookingCheckListDB) {
			List<Label> rowLabels = new ArrayList<>();
			
			int row = bookingGrid.insertRow(bookingGrid.getRowCount());
			
			Label productLabel = new Label(bookingCheck.getItem().getProduct().getName());
			
			Label codeLabel = new Label(bookingCheck.getItem().getProduct().getCode());
			Label feeLabel = new Label(bookingCheck.hasFee() ? "SI" : "NO");
			if(!bookingCheck.hasFee() && !bookingCheck.getStatus().equals(RegistryItemStatus.INTERESTED)) {
				feeLabel.getElement().getStyle().setFontWeight(FontWeight.BOLD);
				feeLabel.getElement().getStyle().setColor("red");
			}
			
			Label productStatusLabel = new Label(getProductStatus(bookingCheck));
			
			Label quantityLabel = createQuantityLabel(bookingCheck);
			
			AonToolbarSmallButton actionBtn = new AonToolbarSmallButton("Acciones", AON.CSS.aonIconMoreVertical());
			actionBtn.addClickHandler(e -> {
				addHighlightRow(bookingGrid, row);
				NativeEvent nativeEvent = e.getNativeEvent();
				bookingWithOutFeeMenu.setPopupPosition(nativeEvent.getClientX() - 150, nativeEvent.getClientY());
				bookingWithOutFeeMenu.show();
				bookingWithOutFeeMenu.setId(bookingCheck.getId());
				bookingWithOutFeeMenu.setItem(bookingCheck.getItem());
				bookingWithOutFeeMenu.setCustomer(bookingCheck.getCustomer());
				bookingWithOutFeeMenu.setBookingCheck(bookingCheck);
				bookingWithOutFeeMenu.setHasFee(bookingCheck.hasFee());
				bookingWithOutFeeMenu.addCloseHandler(ev -> removeHighlightRow(bookingGrid, row));
			});
			
			bookingGrid.setWidget(row, 0, productLabel);
			bookingGrid.setWidget(row, 1, codeLabel);
			bookingGrid.setWidget(row, 2, feeLabel);
			bookingGrid.setWidget(row, 3, productStatusLabel);
			bookingGrid.setWidget(row, 4, quantityLabel);
			bookingGrid.setWidget(row, 5, actionBtn);
			
			rowLabels.add(productLabel);
			rowLabels.add(codeLabel);
			rowLabels.add(feeLabel);
			rowLabels.add(productStatusLabel);
			rowLabels.add(quantityLabel);
			
			for(Label label : rowLabels) {
				label.addMouseOverHandler(e -> addHighlightRow(bookingGrid, row));
				label.addMouseOutHandler(e -> removeHighlightRow(bookingGrid, row));
			}
			
			actionBtn.addMouseOverHandler(e -> addHighlightRow(bookingGrid, row));
			actionBtn.addMouseOutHandler(e -> removeHighlightRow(bookingGrid, row));
			
			if (row % 2 == 0) {
				productLabel.addStyleName(AON.CSS.aonOddTableRow());
				codeLabel.addStyleName(AON.CSS.aonOddTableRow());
				feeLabel.addStyleName(AON.CSS.aonOddTableRow());
				productStatusLabel.addStyleName(AON.CSS.aonOddTableRow());
				quantityLabel.addStyleName(AON.CSS.aonOddTableRow());
				actionBtn.addStyleName(AON.CSS.aonOddTableRow());
				
				bookingGrid.getCellFormatter().addStyleName(row, 0, AON.CSS.aonOddTableRow());
				bookingGrid.getCellFormatter().addStyleName(row, 1, AON.CSS.aonOddTableRow());
				bookingGrid.getCellFormatter().addStyleName(row, 2, AON.CSS.aonOddTableRow());
				bookingGrid.getCellFormatter().addStyleName(row, 3, AON.CSS.aonOddTableRow());
				bookingGrid.getCellFormatter().addStyleName(row, 4, AON.CSS.aonOddTableRow());
				bookingGrid.getCellFormatter().addStyleName(row, 5, AON.CSS.aonOddTableRow());
			}
			
			bookingGrid.getCellFormatter().getElement(row, 2).getStyle().setTextAlign(TextAlign.CENTER);
			bookingGrid.getCellFormatter().getElement(row, 3).getStyle().setTextAlign(TextAlign.CENTER);
			bookingGrid.getCellFormatter().getElement(row, 5).getStyle().setTextAlign(TextAlign.CENTER);
				
			bookingGrid.getRowFormatter().getElement(row).getStyle().setHeight(25.00, Unit.PX);
			
			
		}

	}
	
	private Label createQuantityLabel(BookingCheck bookingCheck) {
		Label quantityLabel = new Label();
		Integer quantityFee = AonStringUtils.isNotBlank(bookingCheck.getQuantityFee()) ? Integer.parseInt(bookingCheck.getQuantityFee()) : 0;
		Integer quantityRItem = AonStringUtils.isNotBlank(bookingCheck.getQuantityRItem()) ? Integer.parseInt(bookingCheck.getQuantityRItem()) : 0;
		
		if(isAditionalUser(bookingCheck)) {
//			Window.alert(bookingCheck.getItem().getProduct().getName());
			quantityRItem--;
		}
		
		if(quantityFee != quantityRItem) {
			quantityLabel.setText(quantityFee.toString() + "  /  " +  quantityRItem.toString());
			quantityLabel.setTitle("No coincide el num. de cuotas con el de contrataciones");
			quantityLabel.getElement().getStyle().setFontWeight(FontWeight.BOLD);
			quantityLabel.getElement().getStyle().setColor("red");
		} else quantityLabel.setText(quantityRItem.toString());
		
		return quantityLabel;
	}
	
	private boolean isAditionalUser(BookingCheck bookingCheck) {
		List<String> barCodes = new ArrayList<>();
		String barCode = bookingCheck.getItem().getBarcode();
		if(barCode.contains("/")) {
			String[] splits = barCode.split("/");
			for(int i=0; i < splits.length; i++)
				barCodes.add(splits[i].trim());
		} else
			barCodes.add(barCode);
		
		RegExp regExp = RegExp.compile("^\\d{2}.USR");
		for(String barCodeIt : barCodes) {
			MatchResult matcher = regExp.exec(barCodeIt);
			if(null != matcher) return true;
		}
		
		return false;
	}

	private void createBookingWithOutFeeMessage() {
		bookingMessagePanel = new HTMLPanel("");
		bookingMessagePanel.addStyleName(AON.CSS.aonDisplayFlexCenter());
		bookingMessagePanel.add(new Label("No existen contrataciones sin cuotas para este cliente"));
	}

	private AonToolbarSmall createBookingWithoutFeeToolbar() {
		AonToolbarSmall toolbar = new AonToolbarSmall("Contrataciones");
		
		toolbarBookingDiscBtn = new AonToolbarSmallButton("Desplegar Contrataciones", AON.CSS.aonIconDown());
		toolbarBookingDiscBtn.addClickHandler(e -> {
			isBookingOpen = !isBookingOpen;
			handleIcon(toolbarBookingDiscBtn, isBookingOpen);
			if(isBookingOpen) bookingDeckPanel.getElement().getStyle().clearDisplay();
			else bookingDeckPanel.getElement().getStyle().setDisplay(Display.NONE);
		});
		
		toolbar.add(toolbarBookingDiscBtn);
		
		return toolbar;
		
	}
	
	// -------- Fees
	
	private void initializeFee() {
		feeDeckPanel = new DeckPanel();
		createFeeGrid();
		createFeeMessage();
		
		feeDeckPanel.add(feeGrid);
		feeDeckPanel.add(feeMessagePanel);
		
		feePanel.add(createFeeToolbar());
		feePanel.add(feeDeckPanel);
	}
	
	private void showFeeGrid() {
		feeDeckPanel.showWidget(0);
	}
	
	private void showFeeMessage() {
		feeDeckPanel.showWidget(1);
	}
	
	private void createFeeGrid() {
		feeGrid = new Grid(0, 12);
		feeGrid.clear();
		feeGrid.setWidth("100%");

		int row = feeGrid.insertRow(feeGrid.getRowCount());

		Label concept = new Label("PRODUCTO");
		Label code = new Label("CODIGO");
		Label conceptStatus = new Label("ESTADO");
		Label ritem = new Label("CONTRAT.");
		Label periodicity = new Label("PERIODO");
		Label quantity = new Label("CANTIDAD");
		Label price = new Label("PRECIO");
		Label discount = new Label("DESCUENTO");
		Label startDate = new Label("F. DESDE");
		Label endDate = new Label("F. HASTA");
		Label billingDate = new Label("F. FACTUR.");
		Label action = new Label("");
		
		concept.addStyleName(AON.CSS.aonHeaderTable());
		code.addStyleName(AON.CSS.aonHeaderTable());
		conceptStatus.addStyleName(AON.CSS.aonHeaderTable());
		ritem.addStyleName(AON.CSS.aonHeaderTable());
		periodicity.addStyleName(AON.CSS.aonHeaderTable());
		quantity.addStyleName(AON.CSS.aonHeaderTable());
		price.addStyleName(AON.CSS.aonHeaderTable());
		discount.addStyleName(AON.CSS.aonHeaderTable());
		startDate.addStyleName(AON.CSS.aonHeaderTable());
		endDate.addStyleName(AON.CSS.aonHeaderTable());
		billingDate.addStyleName(AON.CSS.aonHeaderTable());
		action.addStyleName(AON.CSS.aonHeaderTable());

		
		feeGrid.setWidget(row, 0, concept);
		feeGrid.setWidget(row, 1, code);
		feeGrid.setWidget(row, 2, conceptStatus);
		feeGrid.setWidget(row, 3, ritem);
		feeGrid.setWidget(row, 4, periodicity);
		feeGrid.setWidget(row, 5, quantity);
		feeGrid.setWidget(row, 6, price);
		feeGrid.setWidget(row, 7, discount);
		feeGrid.setWidget(row, 8, startDate);
		feeGrid.setWidget(row, 9, endDate);
		feeGrid.setWidget(row, 10, billingDate);
		feeGrid.setWidget(row, 11, action);
		
		feeGrid.getCellFormatter().addStyleName(row, 0, AON.CSS.aonHeaderSticky());
		feeGrid.getCellFormatter().addStyleName(row, 1, AON.CSS.aonHeaderSticky());
		feeGrid.getCellFormatter().addStyleName(row, 2, AON.CSS.aonHeaderSticky());
		feeGrid.getCellFormatter().addStyleName(row, 3, AON.CSS.aonHeaderSticky());
		feeGrid.getCellFormatter().addStyleName(row, 4, AON.CSS.aonHeaderSticky());
		feeGrid.getCellFormatter().addStyleName(row, 5, AON.CSS.aonHeaderSticky());
		feeGrid.getCellFormatter().addStyleName(row, 6, AON.CSS.aonHeaderSticky());
		feeGrid.getCellFormatter().addStyleName(row, 7, AON.CSS.aonHeaderSticky());
		feeGrid.getCellFormatter().addStyleName(row, 8, AON.CSS.aonHeaderSticky());
		feeGrid.getCellFormatter().addStyleName(row, 9, AON.CSS.aonHeaderSticky());
		feeGrid.getCellFormatter().addStyleName(row, 10, AON.CSS.aonHeaderSticky());
		feeGrid.getCellFormatter().addStyleName(row, 11, AON.CSS.aonHeaderSticky());
		
		setColumnWidthFee();
		fillFee();
	}

	private void setColumnWidthFee() {
		feeGrid.getColumnFormatter().getElement(1).getStyle().setWidth(160, Unit.PX);
		feeGrid.getColumnFormatter().getElement(2).getStyle().setWidth(85, Unit.PX);
		feeGrid.getColumnFormatter().getElement(3).getStyle().setWidth(80, Unit.PX);
		feeGrid.getColumnFormatter().getElement(4).getStyle().setWidth(80, Unit.PX);
		feeGrid.getColumnFormatter().getElement(5).getStyle().setWidth(80, Unit.PX);
		feeGrid.getColumnFormatter().getElement(6).getStyle().setWidth(80, Unit.PX);
		feeGrid.getColumnFormatter().getElement(7).getStyle().setWidth(80, Unit.PX);
		feeGrid.getColumnFormatter().getElement(8).getStyle().setWidth(80, Unit.PX);
		feeGrid.getColumnFormatter().getElement(9).getStyle().setWidth(80, Unit.PX);
		feeGrid.getColumnFormatter().getElement(10).getStyle().setWidth(80, Unit.PX);
		feeGrid.getColumnFormatter().getElement(11).getStyle().setWidth(25, Unit.PX);
	}

	private void fillFee() {
		CustomerFeeParams params = new CustomerFeeParams()
				.setDomain(options.getDomain())
				.setCustomer(this.customer.getId())
				.setOffset(0)
				.setLimit(Integer.MAX_VALUE);
		
		SERVICE.getCustomerFeeList(options.getDomainName(), options.getDomain(), options.getUser(), params, new AsyncCallback<LinkedList<Fee>>() {

			@Override
			public void onFailure(Throwable caught) {
				AonMessagePanel.showError(messagePanel, "Error cargando panel de facturaci\u00f3n: " + caught.getMessage());
			}

			@Override
			public void onSuccess(LinkedList<Fee> feesDB) {
				if(feesDB.isEmpty()) showFeeMessage();
				else {
					// Order by has booking associated
					// feesDB.sort((o1, o2) -> o2.hasRItem().compareTo(o1.hasRItem()));
					fillFeeGrid(feesDB);
				}
			}
		});
	}

	private void fillFeeGrid(LinkedList<Fee> feesDB) {
		showFeeGrid();
		
		for(Fee fee : feesDB) {
			List<Label> rowLabels = new ArrayList<>();
			
			int row = feeGrid.insertRow(feeGrid.getRowCount());

			Label productLabel = new Label(fee.getItem().getProduct().getName());
			Label codeLabel = new Label(fee.getItem().getProduct().getCode());
			Label productStatusLabel = new Label(getFeeStatus(fee));
			
			Label ritemLabel = new Label(fee.hasRItem() ? "SI" : "NO");
			if(!fee.hasRItem() && !AonStringUtils.containsIgnoreCase(fee.getItem().getBarcode(), "info")) {
				ritemLabel.getElement().getStyle().setFontWeight(FontWeight.BOLD);
				ritemLabel.getElement().getStyle().setColor("red");
			}
			
			Label periodicityLabel = new Label(getPeriodicityLabel(fee.getPeriod().getValue()));
			Label quantityLabel = new Label(fee.getQuantity() == null ? "0" : fee.getQuantity().intValue() + "");
			Label priceLabel = new Label(formatDouble(fee.getPrice()));
			Label discountyLabel = new Label(formatDouble(fee.getDiscountExpr()));
			Label startDateLabel = new Label(formatDate(fee.getStartDate()));
			Label endDateLabel = new Label(formatDate(fee.getEndDate()));
			Label billingDateLabel = new Label(formatBillingDate(fee.getBillingDate()));
			
			AonToolbarSmallButton actionBtn = new AonToolbarSmallButton("Acciones", AON.CSS.aonIconMoreVertical());
			actionBtn.addClickHandler(e -> {
				addHighlightRow(feeGrid, row);
				NativeEvent nativeEvent = e.getNativeEvent();
				feeMenu.setPopupPosition(nativeEvent.getClientX() - 150, nativeEvent.getClientY());
				feeMenu.show();
				feeMenu.setId(fee.getId());
				feeMenu.setItem(fee.getItem());
				feeMenu.setCustomer(fee.getCustomer());
				feeMenu.setFee(fee);
				feeMenu.addCloseHandler(ev -> removeHighlightRow(feeGrid, row));
			});
			
			feeGrid.setWidget(row, 0, productLabel);
			feeGrid.setWidget(row, 1, codeLabel);
			feeGrid.setWidget(row, 2, productStatusLabel);
			feeGrid.setWidget(row, 3, ritemLabel);
			feeGrid.setWidget(row, 4, periodicityLabel);
			feeGrid.setWidget(row, 5, quantityLabel);
			feeGrid.setWidget(row, 6, priceLabel);
			feeGrid.setWidget(row, 7, discountyLabel);
			feeGrid.setWidget(row, 8, startDateLabel);
			feeGrid.setWidget(row, 9, endDateLabel);
			feeGrid.setWidget(row, 10, billingDateLabel);
			feeGrid.setWidget(row, 11, actionBtn);
			
			rowLabels.add(productLabel);
			rowLabels.add(codeLabel);
			rowLabels.add(productStatusLabel);
			rowLabels.add(ritemLabel);
			rowLabels.add(periodicityLabel);
			rowLabels.add(quantityLabel);
			rowLabels.add(priceLabel);
			rowLabels.add(discountyLabel);
			rowLabels.add(startDateLabel);
			rowLabels.add(endDateLabel);
			rowLabels.add(billingDateLabel);
			
			for(Label label : rowLabels) {
				label.addMouseOverHandler(e -> addHighlightRow(feeGrid, row));
				label.addMouseOutHandler(e -> removeHighlightRow(feeGrid, row));
			}
			
			actionBtn.addMouseOverHandler(e -> addHighlightRow(feeGrid, row));
			actionBtn.addMouseOutHandler(e -> removeHighlightRow(feeGrid, row));
			
			if (row % 2 == 0) {
				productLabel.addStyleName(AON.CSS.aonOddTableRow());
				codeLabel.addStyleName(AON.CSS.aonOddTableRow());
				productStatusLabel.addStyleName(AON.CSS.aonOddTableRow());
				ritemLabel.addStyleName(AON.CSS.aonOddTableRow());
				periodicityLabel.addStyleName(AON.CSS.aonOddTableRow());
				quantityLabel.addStyleName(AON.CSS.aonOddTableRow());
				priceLabel.addStyleName(AON.CSS.aonOddTableRow());
				discountyLabel.addStyleName(AON.CSS.aonOddTableRow());
				startDateLabel.addStyleName(AON.CSS.aonOddTableRow());
				endDateLabel.addStyleName(AON.CSS.aonOddTableRow());
				billingDateLabel.addStyleName(AON.CSS.aonOddTableRow());
				actionBtn.addStyleName(AON.CSS.aonOddTableRow());
				
				feeGrid.getCellFormatter().addStyleName(row, 0, AON.CSS.aonOddTableRow());
				feeGrid.getCellFormatter().addStyleName(row, 1, AON.CSS.aonOddTableRow());
				feeGrid.getCellFormatter().addStyleName(row, 2, AON.CSS.aonOddTableRow());
				feeGrid.getCellFormatter().addStyleName(row, 3, AON.CSS.aonOddTableRow());
				feeGrid.getCellFormatter().addStyleName(row, 4, AON.CSS.aonOddTableRow());
				feeGrid.getCellFormatter().addStyleName(row, 5, AON.CSS.aonOddTableRow());
				feeGrid.getCellFormatter().addStyleName(row, 6, AON.CSS.aonOddTableRow());
				feeGrid.getCellFormatter().addStyleName(row, 7, AON.CSS.aonOddTableRow());
				feeGrid.getCellFormatter().addStyleName(row, 8, AON.CSS.aonOddTableRow());
				feeGrid.getCellFormatter().addStyleName(row, 9, AON.CSS.aonOddTableRow());
				feeGrid.getCellFormatter().addStyleName(row, 10, AON.CSS.aonOddTableRow());
				feeGrid.getCellFormatter().addStyleName(row, 11, AON.CSS.aonOddTableRow());
			}
			
			feeGrid.getCellFormatter().getElement(row, 2).getStyle().setTextAlign(TextAlign.CENTER);
			feeGrid.getCellFormatter().getElement(row, 3).getStyle().setTextAlign(TextAlign.CENTER);
			feeGrid.getCellFormatter().getElement(row, 4).getStyle().setTextAlign(TextAlign.CENTER);
			feeGrid.getCellFormatter().getElement(row, 5).getStyle().setTextAlign(TextAlign.CENTER);
			feeGrid.getCellFormatter().getElement(row, 6).getStyle().setTextAlign(TextAlign.CENTER);
			feeGrid.getCellFormatter().getElement(row, 7).getStyle().setTextAlign(TextAlign.CENTER);
			feeGrid.getCellFormatter().getElement(row, 8).getStyle().setTextAlign(TextAlign.CENTER);
			feeGrid.getCellFormatter().getElement(row, 9).getStyle().setTextAlign(TextAlign.CENTER);
			feeGrid.getCellFormatter().getElement(row, 10).getStyle().setTextAlign(TextAlign.CENTER);
			feeGrid.getCellFormatter().getElement(row, 11).getStyle().setTextAlign(TextAlign.CENTER);
				
			feeGrid.getRowFormatter().getElement(row).getStyle().setHeight(25.00, Unit.PX);
		}

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
	
	private String formatBillingDate(Date date) {
		if(null == date) return "";
		return formatBillingDate.format(date);
	}
	
	private String formatDouble(Double number) {
		if(null == number) return "0.00";
		return NumberFormat.getFormat("0.00").format(number);
	}
	
	private String formatDouble(String number) {
		if(AonStringUtils.isBlank(number)) return "0.00";
		return NumberFormat.getFormat("0.00").format(Double.parseDouble(number));
	}

	private String getPeriodicityLabel(Integer periodicityIdx) {
		switch (periodicityIdx) {
			case 1:
				return "Mensual";
			case 2:
				return "Bimensual";
			case 3:
				return "Trimestral";
			case 4:
				return "Cuatrimestral";
			case 5:
				return "Semestral";
			case 6:
				return "Anual";
			default:
				return "Sin Periodo";
		}
	}

	private void createFeeMessage() {
		feeMessagePanel = new HTMLPanel("");
		feeMessagePanel.addStyleName(AON.CSS.aonDisplayFlexCenter());
		feeMessagePanel.add(new Label("No existen cuotas para este cliente"));
	}

	private AonToolbarSmall createFeeToolbar() {
		AonToolbarSmall toolbar = new AonToolbarSmall("Cuotas");
		
		toolbarFeeDiscBtn = new AonToolbarSmallButton("Desplegar Cuotas", AON.CSS.aonIconDown());
		toolbarFeeDiscBtn.addClickHandler(e -> {
			isFeeOpen = !isFeeOpen;
			handleIcon(toolbarFeeDiscBtn, isFeeOpen);
			if(isFeeOpen) feeDeckPanel.getElement().getStyle().clearDisplay();
			else feeDeckPanel.getElement().getStyle().setDisplay(Display.NONE);
		});
		
		toolbar.add(toolbarFeeDiscBtn);
		
		return toolbar;
		
	}
	
	// -------- Auxiliar Methods
	
	private String getProductStatus(BookingCheck bookingCheck) {
		if(bookingCheck.getStatus().equals(RegistryItemStatus.ACTIVE)) return "Facturable";
		else if(bookingCheck.getStatus().equals(RegistryItemStatus.INTERESTED)) return "No Facturable";
		else if(bookingCheck.getStatus().equals(RegistryItemStatus.REFUSED)) return " No Contratado";
		else if(bookingCheck.getStatus().equals(RegistryItemStatus.INACTIVE)) return "Inactivo";
		else return "";
	}
	
	private String getFeeStatus(Fee fee) {
		if(AonStringUtils.containsIgnoreCase(fee.getItem().getBarcode(), "info"))
			return "Informativo";
		
		return 	fee.getEndDate() == null || 
				(new Date().before(fee.getEndDate()) && fee.getEndDate().after(fee.getStartDate())) 
				? "Facturable" : "Expirado";
	}
	
	private void handleIcon(AonToolbarSmallButton button, boolean open) {
		if (open) {
			button.removeStyleName(AON.CSS.aonIconLeft());
			button.addStyleName(AON.CSS.aonIconDown());

			if (button.equals(toolbarBookingDiscBtn))
				button.setTitle("Colapsar Contrataciones");
			if (button.equals(toolbarFeeDiscBtn))
				button.setTitle("Colapsar Cuotas");
		} else {
			button.removeStyleName(AON.CSS.aonIconDown());
			button.addStyleName(AON.CSS.aonIconLeft());

			if (button.equals(toolbarBookingDiscBtn))
				button.setTitle("Desplegar Contrataciones");
			if (button.equals(toolbarFeeDiscBtn))
				button.setTitle("Desplegar Cuotas");
		}
	}
	
	// -------- Servlets Methods
	
	private void enableRemoteDomain(Integer domainId, String url) {
		AonDialog dialog = new AonDialog("Acceso remoto",
				new HTML("Se va a acceder al dominio <b>" + url + "</b>.<br>\u00bfQuiere activar el acceso remoto para este dominio\u003f"));
		
		dialog.confirm(new AonAcceptDialogCallback() {

			@Override
			public void onCancel() {
				Window.open("https://" + url, "_blank", "");
			}

			@Override
			public void onAccept() {
				// Create the base URL
				String baseUrl = "/ms/api/domain/remote/";

				// Create a URL builder and add query parameters
				UrlBuilder urlBuilder = new UrlBuilder();
				urlBuilder.setProtocol(Window.Location.getProtocol()); // Use the current protocol
				urlBuilder.setHost("aon.solutions"); 
				urlBuilder.setPath(baseUrl);
				
				// Create the request builder with the complete URL
				RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.PUT, urlBuilder.buildString());
				requestBuilder.setHeader("session_id", "AONd95770f269e711eb94390242ac130002");
				
				requestBuilder.setHeader("domain_name", url);
				requestBuilder.setHeader("domain_id", domainId.toString());
				
				try {
				    // Send the request
				    requestBuilder.sendRequest(null, new RequestCallback() {
				        public void onResponseReceived(Request request, Response response) {
				            if (response.getStatusCode() == 200) {
				            	Window.open("https://" + url, "_blank", "");
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
		});
	}
	
	private void openUserTooltip(Domain domain) {
		AonMessagePanel.showLoading(messagePanel, "Obteniendo usuarios para el dominio " + domain.getDescription() + " ...");
		
		// Create the base URL
		String baseUrl = "/ms/api/user/";

		// Create a URL builder and add query parameters
		UrlBuilder urlBuilder = new UrlBuilder();
		urlBuilder.setProtocol(Window.Location.getProtocol()); // Use the current protocol
//		urlBuilder.setHost("localhost:8080"); 
		urlBuilder.setHost("aon.solutions"); 
		urlBuilder.setPath(baseUrl);
		
		// Create the request builder with the complete URL
		RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.GET, urlBuilder.buildString());
		requestBuilder.setHeader("session_id", "AONd95770f269e711eb94390242ac130002");
		
		requestBuilder.setHeader("domain_name", domain.getName());
		requestBuilder.setHeader("domain_id", domain.getId().toString());
		
		try {
		    // Send the request
		    requestBuilder.sendRequest(null, new RequestCallback() {
		        public void onResponseReceived(Request request, Response response) {
		            if (response.getStatusCode() == 200) {
		            	String responseBody = response.getText();
		            	List<User> users = DomainCompanyJSON.parseUsersJSONArr(responseBody);
		            	
		            	HTMLPanel container = new HTMLPanel("");
		        		container.addStyleName(AON.CSS.aonFlexColumn());
		        		container.getElement().getStyle().setProperty("margin", "0 1rem");
		        		
		        		users.forEach(user -> {
		        			HTMLPanel row = new HTMLPanel("");
		        			row.addStyleName(AON.CSS.aonItemFlex());
		        			
		        			Label name = new Label("(" + user.getLogin() + ") " + user.getName());
		        			
		        			AonTableButton copy = new AonTableButton("Copiar login", AON.CSS.aonIconCopy());
		        			copy.addClickHandler(e -> copyToClipboard(user.getLogin()));
		        			
		        			row.add(copy);
		        			row.add(name);
		        			
		        			container.add(row);
		        		});
		            	
		        		AonDialog dialog = new AonDialog("Usuarios " + domain.getDescription(), container);
            			dialog.info();
		        		
						AonMessagePanel.hideMessage(messagePanel);
		            } else {
		            	AonMessagePanel.showError(messagePanel, response.getText());
		            }
		        }

				private final native void copyToClipboard(String text) /*-{
					var textField = $doc.createElement('textarea');
			        textField.value = text;
			        $doc.body.appendChild(textField);
			        textField.select();
			        $doc.execCommand('copy');
			        $doc.body.removeChild(textField);
				}-*/;

				public void onError(Request request, Throwable exception) {
					AonMessagePanel.showError(messagePanel, exception.getMessage());
		        }
		    });
		} catch (RequestException e) {
			AonMessagePanel.showError(messagePanel, e.getMessage());
		}
	}

	
	private void syncDomains() {
		updateBookingRitems();
	}
	
	private void updateBookingRitems() {
		AonMessagePanel.showLoading(messagePanel, "Sincronizando contrataci\u00f3n para el cliente " + customer.getName() + " ...");
		
		// Create the base URL
		String baseUrl = "/ms/api/domain/booking-customer/";

		// Create a URL builder and add query parameters
		UrlBuilder urlBuilder = new UrlBuilder();
		urlBuilder.setProtocol(Window.Location.getProtocol()); // Use the current protocol
		urlBuilder.setHost(Window.Location.getHost()); 
		urlBuilder.setPath(baseUrl);
		
		// Create the request builder with the complete URL
		RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.PUT, urlBuilder.buildString());
		requestBuilder.setHeader("session_id", "AONd95770f269e711eb94390242ac130002");
		
		requestBuilder.setHeader("domain_name", options.getDomainName());
		requestBuilder.setHeader("domain_login", options.getUser());
		requestBuilder.setHeader("domain_id", String.valueOf(options.getDomain()));
		
		JSONObject body = new JSONObject();
		body.put("customer", new JSONNumber(customer.getId()));
		
		try {
		    // Send the request
		    requestBuilder.sendRequest(body.toString(), new RequestCallback() {
		        public void onResponseReceived(Request request, Response response) {
		            if (response.getStatusCode() == 200) {
		            	
		            	AonMessagePanel.showSuccess(messagePanel, "La sincronizaci\u00f3n del cliente " + customer.getName() + " se ha realizado correctamente");
	            		
		            	Timer timer = new Timer() {
		           		     @Override
		           		     public void run() {
		           		    	setBookingCustomer(customer, customerDomains);;
		           		     }
		           		};
		           		timer.schedule(2500);
		           		
		           		Timer logTimer = new Timer() {
  		           		     @Override
  		           		     public void run() {
  		           		    	 showSyncLogs(response.getText());
  		           		     }
  		           		};
  		           		logTimer.schedule(4500);
  		           		
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
	
	private void unSyncDomains() {
		AonMessagePanel.showLoading(messagePanel, "Desincronizando contrataci\u00f3n para el cliente " + customer.getName() + " ...");
		
		// Create the base URL
		String baseUrl = "/ms/api/domain/booking/";

		// Create a URL builder and add query parameters
		UrlBuilder urlBuilder = new UrlBuilder();
		urlBuilder.setProtocol(Window.Location.getProtocol()); // Use the current protocol
		urlBuilder.setHost(Window.Location.getHost()); 
		urlBuilder.setPath(baseUrl);
		
		// Create the request builder with the complete URL
		RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.DELETE, urlBuilder.buildString());
		requestBuilder.setHeader("session_id", "AONd95770f269e711eb94390242ac130002");
		
		JSONObject body = new JSONObject();
		body.put("customer", new JSONNumber(customer.getId()));
		
		try {
		    // Send the request
		    requestBuilder.sendRequest(body.toString(), new RequestCallback() {
		        public void onResponseReceived(Request request, Response response) {
		            if (response.getStatusCode() == 200) {
		            	
		            	AonMessagePanel.showSuccess(messagePanel, "La desincronizaci\u00f3n del cliente " + customer.getName() + " se ha realizado correctamente");
	            		
		            	Timer timer = new Timer() {
		           		     @Override
		           		     public void run() {
		           		    	setBookingCustomer(customer, customerDomains);;
		           		     }
		           		};
		           		timer.schedule(2500);
  		           		
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
	
	private void showSyncLogs(String response) {
		if(AonStringUtils.isNotBlank(response) && AonStringUtils.containsIgnoreCase(response, "logs"))
    		AonMessagePanel.showWarning(messagePanel, parseErrors(response));
	}

	private HTMLPanel parseErrors(String jsonErrors) {
		String htmlBody = "<ul>";
		
		JSONObject jsonObj = JSONParser.parseStrict(jsonErrors).isObject();
		JSONArray arr = jsonObj.get("logs").isArray();
		
		for(Integer i = 0; i < arr.size(); i++)
			htmlBody += "<li>" + arr.get(i).isObject().get("log").isString().stringValue() + "</li>";

		htmlBody += "</ul>";
		
		HTMLPanel html = new HTMLPanel(htmlBody);
		html.getElement().getStyle().setPaddingLeft(2, Unit.EM);
		
		return html;
	}
	
}
