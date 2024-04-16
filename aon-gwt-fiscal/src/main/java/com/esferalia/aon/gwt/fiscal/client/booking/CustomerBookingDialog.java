package com.esferalia.aon.gwt.fiscal.client.booking;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.RegistryService;
import com.esferalia.aon.gwt.common.client.RegistryServiceAsync;
import com.esferalia.aon.gwt.common.client.RegistryServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonContextMenu;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog.AonAcceptDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarSmallButton;
import com.esferalia.aon.gwt.fiscal.client.registry.CustomerFeeDialog;
import com.esferalia.aon.gwt.fiscal.client.registry.RegistryModuleOptions;
import com.esferalia.aon.gwt.fiscal.client.registry.SyncCustomerFeeDialog;
import com.esferalia.aon.occam.api.model.BookingCheck;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.aonsolutions.AonApp;
import com.esferalia.aon.occam.api.model.fee.Fee;
import com.esferalia.aon.occam.api.model.product.OldItem;
import com.esferalia.aon.occam.api.model.registry.CustomerFeeParams;
import com.esferalia.aon.occam.api.model.registry.RegistryItemStatus;
import com.esferalia.aon.occam.api.model.security.Booking;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.DomainType;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SuggestBox;

public abstract class CustomerBookingDialog extends AonCustomDialog {
	
	// ------- BookingWithOutFee
	
	class AonAppUsersCommand implements ScheduledCommand {

		@Override
		public void execute() {
			aonAppUsers(bookingWithOutFeeMenu.getCustomer());
		}
		
		public void aonAppUsers(Customer customer) {
			AonMessagePanel.showLoading(messagePanel, "Obteniendo contrataci\u00f3n ... ");
        	
        	String host = isLocalDev ? "localhost:8080" : "aon.solutions";
    		String endPoint =  "/ms/api/booking/customer";
    		
    		bookingApi.getCustomerBooking(host, endPoint, customer.getId(), new AsyncCallback<List<Booking>>() {
    			
    			@Override
    			public void onSuccess(List<Booking> bookingList) {
    				AonMessagePanel.hideMessage(messagePanel);
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
    			}
    			
    			@Override
    			public void onFailure(Throwable exception) {
    				AonMessagePanel.showError(messagePanel, exception.getMessage());
    			}
    		});
		}

	}
	
	class ConectaUsersCommand implements ScheduledCommand {

		@Override
		public void execute() {
			conectaUsers(bookingWithOutFeeMenu.getCustomer());
		}
		
		public void conectaUsers(Customer customer) {
			AonMessagePanel.showLoading(messagePanel, "Obteniendo contrataci\u00f3n ... ");
        	
        	String host = isLocalDev ? "localhost:8080" : "aon.solutions";
    		String endPoint =  "/ms/api/booking/customer";
    		
    		bookingApi.getCustomerBooking(host, endPoint, customer.getId(), new AsyncCallback<List<Booking>>() {
    			
    			@Override
    			public void onSuccess(List<Booking> bookingList) {
    				AonMessagePanel.hideMessage(messagePanel);
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
    			}
    			
    			@Override
    			public void onFailure(Throwable exception) {
    				AonMessagePanel.showError(messagePanel, exception.getMessage());
    			}
    		});
		}

	}
	
	class CreateCustomerFeeCommand implements ScheduledCommand {

		@Override
		public void execute() {
			createCustomerFee(bookingWithOutFeeMenu.getItem(), bookingWithOutFeeMenu.getCustomer(), bookingWithOutFeeMenu.getId());
		}
		
		private void createCustomerFee(OldItem oldItem, Customer customer, Integer ritem) {
			new CustomerFeeDialog(options, oldItem, customer, ritem) {
				
				@Override
				protected void onAccept(Fee fee) {}
				
				@Override
				protected void onCreate(Fee fee) {
					SERVICE.createCustomerFeeList(options.getDomainName(), options.getDomain(), options.getUser(), fee, new AsyncCallback<Fee>() {
						
						@Override
						public void onSuccess(Fee result) {
							AonMessagePanel.showSuccess(messagePanel, "Se ha creado la cuota correctamente");
							initializeBooking();
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
				protected void onCreate(Fee fee, Integer ritem) {
					SERVICE.createCustomerFeeList(options.getDomainName(), options.getDomain(), options.getUser(), fee, new AsyncCallback<Fee>() {
						
						@Override
						public void onSuccess(Fee customerFee) {
							AonMessagePanel.showSuccess(messagePanel, "Se ha creado la cuota correctamente");
							
							SERVICE.updateRitemCustomerFee(options.getDomainName(), options.getDomain(), options.getUser(), customerFee.getId(), ritem, new AsyncCallback<Void>() {
								
								@Override
								public void onSuccess(Void result) {
									AonMessagePanel.showSuccess(messagePanel, "Vinculada la cuota con la contrataci\u00f3n");
									initializeBooking();
								}
								
								@Override
								public void onFailure(Throwable caught) {
									AonMessagePanel.showError(messagePanel, "Error creando cuota: " + caught.getMessage());
								}
							});

						}
						
						@Override
						public void onFailure(Throwable caught) {
							AonMessagePanel.showError(messagePanel, "Error creando cuota: " + caught.getMessage());
						}
					});
				}
				
			};
		}

	}
	
	class SyncCustomerFeeCommand implements ScheduledCommand {

		@Override
		public void execute() {
			syncCustomerFee(bookingWithOutFeeMenu.getItem(), bookingWithOutFeeMenu.getCustomer(), bookingWithOutFeeMenu.getId());
		}
		
		private void syncCustomerFee(OldItem oldItem, Customer customer, Integer ritem) {
			new SyncCustomerFeeDialog(options, oldItem, customer, ritem) {
				
				@Override
				protected void onSyncCustomerFee(Fee fee, Integer ritem) {
					SERVICE.updateRitemCustomerFee(options.getDomainName(), options.getDomain(), options.getUser(), fee.getId(), ritem, new AsyncCallback<Void>() {
						
						@Override
						public void onSuccess(Void result) {
							AonMessagePanel.showSuccess(messagePanel, "Vinculada la cuota con la contrataci\u00f3n");
							initializeBooking();
						}
						
						@Override
						public void onFailure(Throwable caught) {
							AonMessagePanel.showError(messagePanel, "Error creando cuota: " + caught.getMessage());
						}
					});
				}
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
							initializeBooking();
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
							initializeBooking();
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
		private Integer childDomian;
		
		private MenuItem createCustomerFee;
		private MenuItem syncCustomerFee;
		private MenuItem updateBooking;
		private MenuItem deleteBooking;
		private MenuItem conectaUsers;
		private MenuItem aonAppUsers;
		

		public BookingWithOutFeeMenu() {
			createCustomerFee = addMenuItem("Crear Cuota", new CreateCustomerFeeCommand(), AON.CSS.aonIconAdd(), "createCustomerFee");
			syncCustomerFee = addMenuItem("Vincular Cuota Existente", new SyncCustomerFeeCommand(), AON.CSS.aonIconSync(), "syncCustomerFee");
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
			this.syncCustomerFee.setVisible(!hasFee);
		}

	}
	
	// Variables
	
	private BookingWithOutFeeMenu bookingWithOutFeeMenu;

	private HTMLPanel bookingPanel;
	private HTMLPanel messagePanel;
	private DeckPanel bookingDeckPanel;
	private ScrollPanel scrollPanel;
	private HTMLPanel scrollContentPanel;
	private Grid bookingGrid;
	private HTMLPanel bookingMessagePanel;
	
	private RegistryServiceAsync SERVICE; 
	private RegistryModuleOptions options;
	private Integer customerId;
	
	private BookingApi bookingApi;
	private static String SESSION_API = "AONd95770f269e711eb94390242ac130002";
	
	private LinkedList<BookingCheck> bookingList;
	
	private boolean isChildBookig = false;
	private Domain childDomain;
	private Booking domainBooking;
	
	private SuggestBox customerFeeSuggestBox;
	private Map<String, Fee> customerFeeSuggestions = new TreeMap<>();
	private Fee selectedCustomerFee;
	private int iterator = 0;
	
	private boolean isLocalDev = false;
	
	public CustomerBookingDialog(RegistryModuleOptions options, int customerId) {
		super();
		
		this.options = options;
		this.customerId = customerId;
		
		this.isChildBookig = false;
		
		bookingWithOutFeeMenu = new BookingWithOutFeeMenu();
		
		RegistryServiceAsync registryServiceRaw = GWT.create(RegistryService.class);
		SERVICE = new RegistryServiceAsyncDecorator(registryServiceRaw);
		
		this.bookingApi = new BookingApi(SESSION_API);
		
		bookingPanel = new HTMLPanel("");
		bookingPanel.addStyleName(AON.CSS.aonFlexColumn());
		bookingPanel.getElement().getStyle().setProperty("padding", "1rem");
		
		this.setCaption("Contrataciones");
		
		this.add(bookingPanel);
		
		initializeBooking();
		
		this.showCloseButton(true);
		this.getCloseButton().addClickHandler(e -> {
			hide();
			onCloseRefresh();
		});
	}

	public CustomerBookingDialog(RegistryModuleOptions options, int customerId, Domain childDomain, Booking domainBooking) {
		super();
		
		this.options = options;
		this.customerId = customerId;
		this.childDomain = childDomain;
		this.domainBooking = domainBooking;
		
		this.isChildBookig = true;
		
		bookingWithOutFeeMenu = new BookingWithOutFeeMenu();
		
		RegistryServiceAsync registryServiceRaw = GWT.create(RegistryService.class);
		SERVICE = new RegistryServiceAsyncDecorator(registryServiceRaw);
		
		this.bookingApi = new BookingApi(SESSION_API);
		
		bookingPanel = new HTMLPanel("");
		bookingPanel.addStyleName(AON.CSS.aonFlexColumn());
		bookingPanel.getElement().getStyle().setProperty("padding", "1rem");
		
		this.setCaption("Contrataciones");
		
		this.add(bookingPanel);
		
		initializeBooking();
		
		this.showCloseButton(true);
		this.getCloseButton().addClickHandler(e -> {
			hide();
			onCloseRefresh();
		});
	}
	
	// -------- Booking

	private void initializeBooking() {
		bookingPanel.clear();
		
		if(this.isChildBookig) {
			bookingPanel.add(createSyncCustomerFeeToolbar());
		}
		
		messagePanel = new HTMLPanel("");
		
		bookingDeckPanel = new DeckPanel();
		createBookingGrid();
		createBookingWithOutFeeMessage();
		
		scrollPanel = new ScrollPanel();
		scrollPanel.setHeight("500px");
		
		scrollContentPanel = new HTMLPanel("");
		scrollContentPanel.addStyleName(AON.CSS.aonFlexColumn());
		scrollPanel.add(scrollContentPanel);
		
		scrollContentPanel.add(bookingGrid);
		
		bookingDeckPanel.add(scrollPanel);
		bookingDeckPanel.add(bookingMessagePanel);
		
		bookingPanel.add(messagePanel);
		bookingPanel.add(bookingDeckPanel);
	}
	
	private HTMLPanel createSyncCustomerFeeToolbar() {
		HTMLPanel toolbar = new HTMLPanel("");
		toolbar.addStyleName(AON.CSS.aonItemFlex());
		toolbar.getElement().getStyle().setProperty("border-bottom", "solid #c4c4c4 1px");
		toolbar.getElement().getStyle().setProperty("min-height", "37px");
		
		Label syncCustomerFeesLabel = new Label("Sincroniar Cuotas a partir de ");
		
		AonToolbarSmallButton syncCustomerFees = new AonToolbarSmallButton("Sincroniar Cuotas a partir de una base", AON.CSS.aonIconSync()); 
		syncCustomerFees.addClickHandler(e -> {
			if(null != selectedCustomerFee)
				createDraftCustomerFees();
			else
				AonMessagePanel.showError(messagePanel, "El campo cuota es obligatorio");
			
		});
		syncCustomerFees.setEnabled(false);
		
		customerFeeSuggestBox = new SuggestBox();
		customerFeeSuggestBox.setHeight("2em");
		customerFeeSuggestBox.getElement().getStyle().setProperty("padding", "0 5px");
		customerFeeSuggestBox.getElement().getStyle().setProperty("min-width", "400px");
		customerFeeSuggestBox.setAutoSelectEnabled(false);
		customerFeeSuggestBox.getElement().setPropertyString("placeholder", "Cuota: ctrl + espacio para ver sugerencias");
		
		SERVICE.getCustomerFeeSuggestion(options.getDomainName(), options.getDomain(), options.getUser(), null, customerId, null, new AsyncCallback<Map<String, Fee>>() {
			
			@Override
			public void onSuccess(Map<String, Fee> customerFeeSuggestionsDB) {
				customerFeeSuggestions = customerFeeSuggestionsDB;
				
				List<String> suggestions = new ArrayList<String>();
				customerFeeSuggestions.values().forEach(fee -> suggestions.add("[" + fee.getId() + "] " + fee.getDescription()));
				
				MultiWordSuggestOracle orclSb = (MultiWordSuggestOracle) customerFeeSuggestBox.getSuggestOracle();
				orclSb.clear();
				orclSb.addAll(suggestions);
				orclSb.setDefaultSuggestionsFromText(suggestions);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub	
			}
			
		});
		
		customerFeeSuggestBox.addSelectionHandler(e -> {
			customerFeeSuggestBox.hideSuggestionList();
			
			Integer feeId = Integer.parseInt(customerFeeSuggestBox.getValue().split("\\[")[1].split("\\]")[0]);
			List<Fee> fees = customerFeeSuggestions.values().stream().collect(Collectors.toList());
			for(Fee fee : fees) {
				if(fee.getId().equals(feeId)) {
					selectedCustomerFee = fee;
					break;
				}
			}
			
			syncCustomerFees.setEnabled(true);
		});
		
		customerFeeSuggestBox.addValueChangeHandler(e -> {
			if(AonStringUtils.isBlank(customerFeeSuggestBox.getValue())) { 
				//AonMessagePanel.showError(messagePanel, "El campo cuota es obligatorio");
				syncCustomerFees.setEnabled(false);
			}
		});
		
//		customerFeeSuggestBox.addKeyUpHandler(e -> {
//			String customerFeeQuery = customerFeeSuggestBox.getValue();
//			if(e.isControlKeyDown() && e.getNativeKeyCode() == 32) {
//				customerFeeSuggestBox.setValue("");
//				customerFeeQuery = null;
//				getCustomerFeeSuggestion(customerFeeQuery);
//			} else if(AonStringUtils.isNotBlank(customerFeeQuery) && customerFeeQuery.length() > 3) 
//				getCustomerFeeSuggestion(customerFeeQuery);
//		});
		
		toolbar.add(syncCustomerFeesLabel);
		toolbar.add(customerFeeSuggestBox);
		toolbar.add(syncCustomerFees);
		
		return toolbar;
	}

	private void createDraftCustomerFees() {
		if(!bookingList.isEmpty()) {
			iterator = 0;
			createAndSaveCustomerFee();
		}
	}

	private void createAndSaveCustomerFee() {
		if(iterator == bookingList.size()) {
			AonMessagePanel.showSuccess(messagePanel, "Sincronizaci\u00f3n de cuotas finalizada correctamente");
			initializeBooking();
		} else {
			BookingCheck booking = bookingList.get(iterator);
			if(null == booking.getCustomerFee()) {
				
				// Check if its Licencia Adicional
				Double usrPrice = 0.00;
				Double usrDiscount = 0.00;
				Double usrQuantity = 0.00;
				
				String usrDescription = booking.getItem().getProduct().getName().contains("/") ? (booking.getItem().getProduct().getName().split("/")[0].trim() + " /") : booking.getItem().getProduct().getName();
				
				
				if(AonStringUtils.endsWith(booking.getItem().getBarcode(), "USR") || AonStringUtils.endsWith(booking.getEdiSalesCode(), "USR") ||
						AonStringUtils.containsIgnoreCase(booking.getItem().getBarcode(), "USR-A") || AonStringUtils.containsIgnoreCase(booking.getEdiSalesCode(), "USR-A")) {
					Optional<Fee> usrFee = customerFeeSuggestions.values().stream().filter(fee -> booking.getItem().getId().equals(fee.getItem().getId())).findFirst();
					
					List<String> parentApps = domainBooking.getApps().stream().filter(aonApp -> AonStringUtils.isNotBlank(aonApp.getDescription())).map(aonApp -> aonApp.getDescription()).collect(Collectors.toList());
					
					List<String> childApps = childDomain.getApps().stream().filter(domainApp -> null != domainApp.getApp() && AonStringUtils.isNotBlank(domainApp.getApp().getDescription())).map(domainApp -> domainApp.getApp().getDescription()).collect(Collectors.toList());
					List<String> childAppsDiff = childApps.stream().filter(app -> !parentApps.contains(app)).collect(Collectors.toList());
					
					Integer portalUsersCount = null == childDomain.getUsers() ? 0 : (int) childDomain.getUsers().stream().filter(user -> user.isActive() && user.isPortal()).count();			
					List<User> activeUsers = childDomain.getUsers().stream().filter(user -> user.isActive()).collect(Collectors.toList());
					Integer activeUsersDiff = null == activeUsers ? 0 : (activeUsers.size() - portalUsersCount);
					
					if(!childAppsDiff.isEmpty()) activeUsersDiff--;
					
					usrPrice = usrFee.isPresent() ? usrFee.get().getPrice() : 0.00;
					usrDiscount = usrFee.isPresent() ? usrFee.get().getDiscount() : 0.00;
					usrQuantity = activeUsersDiff.doubleValue();
					
					if(AonStringUtils.containsIgnoreCase(booking.getItem().getBarcode(), "USR-A") || AonStringUtils.containsIgnoreCase(booking.getEdiSalesCode(), "USR-A")) {
						usrDescription = usrDescription + childDomain.getDescription();
					}
					
				}
				
				Fee draftFee = new Fee();
				draftFee.setDomain(selectedCustomerFee.getDomain());
				draftFee.setProject(selectedCustomerFee.getProject());
				draftFee.setCustomer(selectedCustomerFee.getCustomer());
				draftFee.setItem(booking.getItem());
				draftFee.setDescription(usrDescription + (!booking.getItem().getId().equals(selectedCustomerFee.getItem().getId()) ? "" : childDomain.getDescription()) );
				
				if(AonStringUtils.endsWith(booking.getItem().getBarcode(), "USR") || AonStringUtils.endsWith(booking.getEdiSalesCode(), "USR") ||
						AonStringUtils.containsIgnoreCase(booking.getItem().getBarcode(), "USR-A") || AonStringUtils.containsIgnoreCase(booking.getEdiSalesCode(), "USR-A")) {
					draftFee.setPrice(usrPrice);
					draftFee.setDiscount(usrDiscount);
					draftFee.setQuantity(usrQuantity);
				} else {
					draftFee.setPrice(!booking.getItem().getId().equals(selectedCustomerFee.getItem().getId()) ? 0.00 : selectedCustomerFee.getPrice());
					draftFee.setDiscount(!booking.getItem().getId().equals(selectedCustomerFee.getItem().getId()) ? 0.00 : selectedCustomerFee.getDiscount());
					draftFee.setQuantity(1.00);
				}
				
				draftFee.setStartDate(selectedCustomerFee.getStartDate());
				draftFee.setEndDate(selectedCustomerFee.getEndDate());
				draftFee.setBillingDate(selectedCustomerFee.getBillingDate());
				draftFee.setPeriod(selectedCustomerFee.getPeriod());
				draftFee.setSecurityLevel(selectedCustomerFee.getSecurityLevel());
				draftFee.setInvoicingGroup(selectedCustomerFee.getInvoicingGroup());
				draftFee.setSeller(selectedCustomerFee.getSeller());
				draftFee.setWorkplace(selectedCustomerFee.getWorkplace());
				draftFee.setSellerSupport(selectedCustomerFee.getSellerSupport());
				draftFee.setSellerComercial(selectedCustomerFee.getSellerComercial());
				
				SERVICE.createCustomerFeeList(options.getDomainName(), options.getDomain(), options.getUser(), draftFee, new AsyncCallback<Fee>() {
					
					@Override
					public void onSuccess(Fee savedFee) {
						SERVICE.updateRitemCustomerFee(options.getDomainName(), options.getDomain(), options.getUser(), savedFee.getId(), booking.getId(), new AsyncCallback<Void>() {
							
							@Override
							public void onSuccess(Void result) {
								iterator++;
								createAndSaveCustomerFee();
							}
							
							@Override
							public void onFailure(Throwable caught) {
								AonMessagePanel.showError(messagePanel, "Error creando cuota: " + caught.getMessage());
								iterator++;
								createAndSaveCustomerFee();
							}
						});
					}
					
					@Override
					public void onFailure(Throwable arg0) {
						iterator++;
						createAndSaveCustomerFee();
					}
				});
				
			} else {
				iterator++;
				createAndSaveCustomerFee();
			}
		}
		
	}

	private void getCustomerFeeSuggestion(String customerFeeQuery) {
		SERVICE.getCustomerFeeSuggestion(options.getDomainName(), options.getDomain(), options.getUser(), null, customerId, customerFeeQuery, new AsyncCallback<Map<String, Fee>>() {
			
			@Override
			public void onSuccess(Map<String, Fee> customerFeeSuggestionsDB) {
				customerFeeSuggestions = customerFeeSuggestionsDB;
				
				List<String> suggestions = new ArrayList<String>();
				customerFeeSuggestions.values().forEach(fee -> suggestions.add("[" + fee.getId() + "] " + fee.getDescription()));
				
				MultiWordSuggestOracle orclSb = (MultiWordSuggestOracle) customerFeeSuggestBox.getSuggestOracle();
				orclSb.clear();
				orclSb.addAll(suggestions);
				orclSb.setDefaultSuggestionsFromText(suggestions);
				customerFeeSuggestBox.showSuggestionList();
			}
			
			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub	
			}
			
		});
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
				.setCustomer(customerId)
				.setOffset(0)
				.setLimit(100);
		
		if(!isChildBookig)
		
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
								bookingList = bookingCheckListDB;
//								bookingList.sort((o1, o2) -> o2.hasFee().compareTo(o1.hasFee()));
								bookingList.sort((o1, o2) -> o2.getItem().getProduct().getName().compareTo(o1.getItem().getProduct().getName()));
								fillBookingGrid();
							}
							
							showDialog();
						}
					});
		
		else {
			
			params.setChildDomain(childDomain.getId());
			
			SERVICE.getCustomerChildBookingCheckList(options.getDomainName(), options.getDomain(), options.getUser(), params,
					new AsyncCallback<LinkedList<BookingCheck>>() {
	
						@Override
						public void onFailure(Throwable caught) {
							AonMessagePanel.showError(messagePanel, "Error cargando panel de facturaci\u00f3n: " + caught.getMessage());
						}
	
						@Override
						public void onSuccess(LinkedList<BookingCheck> bookingCheckListDB) {
							if(bookingCheckListDB.isEmpty()) showBookingMessage();
							else {
								bookingList = bookingCheckListDB;
//								bookingList.sort((o1, o2) -> o2.hasFee().compareTo(o1.hasFee()));
								bookingList.sort((o1, o2) -> o2.getItem().getProduct().getName().compareTo(o1.getItem().getProduct().getName()));
								fillBookingGrid();
							}
							
							showDialog();
						}
					});
			
		}
		
	}

	private void fillBookingGrid() {
		showBookingGrid();
		
		for(BookingCheck bookingCheck : bookingList) {
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
		
		if(isAditionalUser(bookingCheck))
			quantityRItem--;
		
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
	
	// -------- Auxiliar Methods
	
	private String getProductStatus(BookingCheck bookingCheck) {
		if(bookingCheck.getStatus().equals(RegistryItemStatus.ACTIVE)) return "Facturable";
		else if(bookingCheck.getStatus().equals(RegistryItemStatus.INTERESTED)) return "No Facturable";
		else if(bookingCheck.getStatus().equals(RegistryItemStatus.REFUSED)) return " No Contratado";
		else if(bookingCheck.getStatus().equals(RegistryItemStatus.INACTIVE)) return "Inactivo";
		else return "";
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
	
	public void showDialog() {
		// Show center
		Scheduler.get().scheduleDeferred(() -> {
			center();
			show();
		});
	}
	
	protected abstract void onCloseRefresh();
	
}
