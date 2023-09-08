package com.esferalia.aon.gwt.fiscal.client.registry;

import java.util.Map;
import java.util.TreeMap;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.RegistryService;
import com.esferalia.aon.gwt.common.client.RegistryServiceAsync;
import com.esferalia.aon.gwt.common.client.RegistryServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDateBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarSmallButton;
import com.esferalia.aon.occam.api.model.BookingCheck;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Workplace;
import com.esferalia.aon.occam.api.model.product.OldItem;
import com.esferalia.aon.occam.api.model.registry.RegistryItemStatus;
import com.esferalia.aon.occam.api.model.registry.RegistryMode;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.TextBox;

public abstract class BookingCheckDialog extends AonCustomDialog {

	// ------------------------------------------------- Variables (UI)
	
	private HTMLPanel container;
	private HTMLPanel mainPanel;
	
	private HTMLPanel messagePanel;
	
	private HTMLPanel customerPanel;
	private SuggestBox customerSuggestBox;
	
	private HTMLPanel productPanel;
	private SuggestBox productSuggestBox;
	private TextBox productTextBox;
	
	private HTMLPanel statusPanel;
	private ListBox statusListBox;
	
	private HTMLPanel quantityPanel;
	private TextBox quantityTextBox;
	private AonTableButton quantityVars;
	
	private HTMLPanel pricePanel;
	private TextBox priceTextBox;
	private TextBox discountTextBox;
	
	private HTMLPanel datesPanel;
	private AonDateBox startDateBox;
	private AonDateBox endDateBox;
	
	private HTMLPanel workplacePanel;
	private SuggestBox workplaceSuggestBox;
	
	private HTMLPanel buttonsPanel;
	
	// ------------------------------------------------- Variables
	
	private static RegistryServiceAsync SERVICE;
	private RegistryModuleOptions options;
	
	private Map<String, Customer> customerSuggestions = new TreeMap<>();
	private Map<String, OldItem> productSuggestions = new TreeMap<>();
	private Map<String, Workplace> workplaceSuggestions = new TreeMap<>();
	
	private OldItem item;
	private Customer customer;
	private BookingCheck bookingCheck;
	
	private boolean isNewBookingFee = false;

	// ------------------------------------------------- Constructor

	protected BookingCheckDialog(RegistryModuleOptions options, OldItem item, Customer customer) {
		setCaption("Creador Contrataci\u00f3n");
		
		this.isNewBookingFee = true;
		this.item = item;
		this.customer = customer;
		this.options = options;
		
		RegistryServiceAsync registryServiceRaw = GWT.create(RegistryService.class);
		SERVICE = new RegistryServiceAsyncDecorator(registryServiceRaw);
		
		initView();
		showDialog();
	}
	
	protected BookingCheckDialog(RegistryModuleOptions options, BookingCheck bookingCheck) {
		setCaption("Actualizar Contrataci\u00f3n");
		
		this.isNewBookingFee = false;
		this.item = bookingCheck.getItem();
		this.customer = bookingCheck.getCustomer();
		this.bookingCheck = bookingCheck;
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
		
		if(this.isNewBookingFee || null != this.bookingCheck) {
			createCustomerPanel();
			container.add(customerPanel);
		}
		
		if(this.isNewBookingFee || null != this.bookingCheck) {
			createProductPanel();
			container.add(productPanel);
		} 
		
		createStatusPanel();
		
		createQuantityPanel();
		createPricePanel();
		createDatesPanel();
		
		if(this.isNewBookingFee || null != this.bookingCheck) {
			createWorkplacePanel();
			container.add(workplacePanel);
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
		});
		
		if(null != this.item) productSuggestBox.setValue(this.item.getProduct().getName() + " ( " + this.item.getProduct().getCode() + " )");
		
		if(null != this.item) {
			productSuggestBox.setEnabled(false);
			productTextBox.setValue(this.item.getProduct().getName());
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
	
	private void createStatusPanel() {
		statusPanel = new HTMLPanel("");
		statusPanel.addStyleName(AON.CSS.aonItemFlex());
		
		Label statusLabel = new Label("Estado");
		statusLabel.getElement().getStyle().setProperty("min-width", "5.5rem");
		statusLabel.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		
		statusListBox = new ListBox();
		statusListBox.setHeight("2em");
		statusListBox.addItem("Facturable", "0");
		statusListBox.addItem("No Facturable", "1");
		statusListBox.addItem("No Contratado", "2");
		statusListBox.addItem("Inactivo", "3");
		
		if(null != bookingCheck)
			setSelectedValueLB(statusListBox, bookingCheck.getStatus().ordinal() + "");
		
		statusPanel.add(statusLabel);
		statusPanel.add(statusListBox);
		
		container.add(statusPanel);
	}

	private void createQuantityPanel() {
		quantityPanel = new HTMLPanel("");
		quantityPanel.addStyleName(AON.CSS.aonItemFlex());
		
		Label quantityLabel = new Label();
		quantityLabel = new Label("Cantidad");
		quantityLabel.getElement().getStyle().setProperty("min-width", "5.5rem");
		quantityLabel.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		
		quantityTextBox = new TextBox();
		quantityTextBox.setWidth("100%");
		quantityTextBox.setHeight("2em");
		quantityTextBox.getElement().getStyle().setProperty("padding", "0 5px");
		
		if(null != this.bookingCheck) quantityTextBox.setValue(bookingCheck.getQuantity());
		
		quantityVars = new AonTableButton("Variables disponibles:\n\nNUMUSR\nNUMEMP", AON.CSS.aonIconInfo());
		
		quantityPanel.add(quantityLabel);
		quantityPanel.add(quantityTextBox);
		quantityPanel.add(quantityVars);
		
		container.add(quantityPanel);
	}
	
	private void createPricePanel() {
		pricePanel = new HTMLPanel("");
		pricePanel.addStyleName(AON.CSS.aonItemFlex());
		
		Label priceLabel = new Label("Precio");
		priceLabel.getElement().getStyle().setProperty("min-width", "5.5rem");
		priceLabel.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		
		priceTextBox = new TextBox();
		priceTextBox.setHeight("2em");
		priceTextBox.setWidth("8.8em");
		priceTextBox.getElement().getStyle().setProperty("padding", "0 5px");
		
		Label discountLabel = new Label("Dto.");
		discountLabel.setWidth("3.5rem");
		discountLabel.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		
		discountTextBox = new TextBox();
		discountTextBox.setHeight("2em");
		discountTextBox.setWidth("8.8em");
		discountTextBox.getElement().getStyle().setProperty("padding", "0 5px");
		
		if(null != bookingCheck) {
			priceTextBox.setValue(bookingCheck.getPrice() != null ? bookingCheck.getPrice().toString() : null);
			discountTextBox.setValue(bookingCheck.getDiscountExpr());
		}
		
		pricePanel.add(priceLabel);
		pricePanel.add(priceTextBox);
		
		pricePanel.add(discountLabel);
		pricePanel.add(discountTextBox);
		
		container.add(pricePanel);
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
		if(null != bookingCheck) startDateBox.setValue(bookingCheck.getStartDate());
		
		Label endDateLabel = new Label("F. Fin");
		endDateLabel.setWidth("3.5rem");
		endDateLabel.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		
		endDateBox = new AonDateBox();
		endDateBox.setHeight("2em");
		endDateBox.setWidth("8.8em");
		endDateBox.getElement().getStyle().setProperty("padding", "0 5px");
		endDateBox.getElement().getStyle().setTextAlign(TextAlign.CENTER);
		endDateBox.addStyleName("gwt-TextBox");
		if(null != bookingCheck) endDateBox.setValue(bookingCheck.getEndDate());
		
		datesPanel.add(startDateLabel);
		datesPanel.add(startDateBox);
		datesPanel.add(endDateLabel);
		datesPanel.add(endDateBox);
		
		container.add(datesPanel);
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
		
		if(null != bookingCheck) workplaceSuggestBox.setValue(null != bookingCheck.getWorkplace() ? bookingCheck.getWorkplace().getDescription() : null);
		
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
		
		if(this.isNewBookingFee || null != this.bookingCheck) {
			acceptBtnDialog = new Button();
			acceptBtnDialog.setStyleName(AON.CSS.aonDialogButton());
			acceptBtnDialog.addStyleName(AON.CSS.aonIconSave());
			acceptBtnDialog.setText(AON.MSG.saveAction());
			acceptBtnDialog.addClickHandler(e -> {
				createNewFee();
			});
		}

		buttonsPanel.add(acceptBtnDialog);
	}

	private void createNewFee() {
		if(null != bookingCheck)
			updateBookingCheck();
		else {
			BookingCheck bookingCheck = new BookingCheck();
			
			bookingCheck.setDomain(new Domain().setId(options.getDomain()));
			
			if(null != this.customer) bookingCheck.setCustomer(this.customer);
			else if(AonStringUtils.isNotBlank(customerSuggestBox.getValue())) bookingCheck.setCustomer(customerSuggestions.get(customerSuggestBox.getValue()));
			
			if(AonStringUtils.isBlank(customerSuggestBox.getValue())) {
				AonMessagePanel.showError(messagePanel, "El campo cliente es obligatorio");
				return;
			}
			
			bookingCheck.setItem(item);
			
			if(item == null) {
				AonMessagePanel.showError(messagePanel, "El campo producto es obligatorio");
				return;
			}
			
			bookingCheck.setType(RegistryMode.BOOKING);
			bookingCheck.setStatus(RegistryItemStatus.safeValueOf(Byte.parseByte(statusListBox.getSelectedValue())));
			
			bookingCheck.setQuantity(quantityTextBox.getValue());
			
			// EVAL QUANTITY
			String quantity = bookingCheck.getQuantity();
			quantity = quantity.replaceAll("NUMUSR", "1");
			quantity = quantity.replaceAll("NUMEMP", "1");
			try {
				evalExpression(quantity);
			} catch (Exception e) {
				AonMessagePanel.showError(messagePanel, "Las expresi\u00f3n de la cantidad no es correcta");
				return;
			}
			
			
			bookingCheck.setPrice(AonStringUtils.isBlank(priceTextBox.getValue()) ? null : Double.parseDouble(priceTextBox.getValue()));
			bookingCheck.setDiscountExpr(discountTextBox.getValue());
			
			bookingCheck.setStartDate(startDateBox.getValue());
			bookingCheck.setEndDate(endDateBox.getValue());
			
			if(AonStringUtils.isNotBlank(workplaceSuggestBox.getValue())) bookingCheck.setWorkplace(workplaceSuggestions.get(workplaceSuggestBox.getValue()));
			
			hide();
			onCreate(bookingCheck);
		}
	}

	private void updateBookingCheck() {
		if(null != this.customer) bookingCheck.setCustomer(this.customer);
		else if(AonStringUtils.isNotBlank(customerSuggestBox.getValue())) bookingCheck.setCustomer(customerSuggestions.get(customerSuggestBox.getValue()));
		
		if(AonStringUtils.isBlank(customerSuggestBox.getValue())) {
			AonMessagePanel.showError(messagePanel, "El campo cliente es obligatorio");
			return;
		}
		
		bookingCheck.setItem(item);
		
		if(item == null) {
			AonMessagePanel.showError(messagePanel, "El campo producto es obligatorio");
			return;
		}
		
		bookingCheck.setType(RegistryMode.BOOKING);
		bookingCheck.setStatus(RegistryItemStatus.safeValueOf(Byte.parseByte(statusListBox.getSelectedValue())));
		
		bookingCheck.setQuantity(quantityTextBox.getValue());
		// EVAL QUANTITY
		String quantity = bookingCheck.getQuantity();
		quantity = quantity.replaceAll("NUMUSR", "1");
		quantity = quantity.replaceAll("NUMEMP", "1");
		try {
			evalExpression(quantity);
		} catch (Exception e) {
			AonMessagePanel.showError(messagePanel, "Las expresi\u00f3n de la cantidad no es correcta");
			return;
		}
		
		bookingCheck.setPrice(AonStringUtils.isBlank(priceTextBox.getValue()) ? null : Double.parseDouble(priceTextBox.getValue()));
		bookingCheck.setDiscountExpr(discountTextBox.getValue());
		
		bookingCheck.setStartDate(startDateBox.getValue());
		bookingCheck.setEndDate(endDateBox.getValue());
		
		if(AonStringUtils.isNotBlank(workplaceSuggestBox.getValue())) bookingCheck.setWorkplace(workplaceSuggestions.get(workplaceSuggestBox.getValue()));
		
		hide();
		onUpdate(bookingCheck);
	}
	
	public double evalExpression(String expression) {
		return calculate(expression);
	}

	public final native double calculate(String expression) /*-{
		return eval(expression);
	}-*/;

	// ------------------------------------------------- Abstract Methods

	protected abstract void onCreate(BookingCheck bookingCheck);
	protected abstract void onUpdate(BookingCheck bookingCheck);

}
