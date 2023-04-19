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
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.occam.api.model.fee.Fee;
import com.esferalia.aon.occam.api.model.type.BillingPeriod;
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

public abstract class CustomerFeeDialog extends AonCustomDialog {

	// ------------------------------------------------- Variables
	
	private HTMLPanel container;
	
	private HTMLPanel lineConfidentialPanel;
	private TextBox lineTextBox;
	private Button confidentialButton;
	
	private HTMLPanel productPanel;
	private SuggestBox productSuggestBox;
	
	private HTMLPanel quantityPricePanel;
	private TextBox quantityTextBox;
	private TextBox priceTextBox;
	private TextBox discountTextBox;
	
	private HTMLPanel datesPanel;
	private AonDateBox startDateBox;
	private AonDateBox endDateBox;
	
	private HTMLPanel periodicityPanel;
	private AonDateBox billingDateBox;
	private ListBox periodListBox;
	
	private HTMLPanel buttonsPanel;
	
	private static RegistryServiceAsync SERVICE;
	private RegistryModuleOptions options;
	private Map<String, String> productSuggestions = new TreeMap<>();

	private Fee fee;
	private boolean isSameProduct = false;
	private String product;
	private Integer item;

	// ------------------------------------------------- Constructor

	protected CustomerFeeDialog(String product, RegistryModuleOptions options) {
		setCaption("Editor Cuotas");
		
		this.isSameProduct = AonStringUtils.isNotBlank(product);
		this.product = product;
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

	// ------------------------------------------------- Auxiliar Methods

	private void initView() {
		container = new HTMLPanel("");
		container.addStyleName(AON.CSS.aonFlexColumn());
		container.getElement().getStyle().setProperty("margin", "1rem");
		
		if(null != this.fee) {
			createLineConfidentialPanel();
			container.add(lineConfidentialPanel);
		} 
		
		if(null != this.fee || this.isSameProduct) {
			createProductPanel();
			container.add(productPanel);
		} 
		
		createQuantityPricePanel();
		createDatesPanel();
		createPeriodicityPanel();
		
		getButtonsPanel();
		container.add(buttonsPanel);
		
		this.setWidget(container);
	}

	private void createLineConfidentialPanel() {
		lineConfidentialPanel = new HTMLPanel("");
		lineConfidentialPanel.addStyleName(AON.CSS.aonItemFlex());
		
		Label lineLabel = new Label("Linea");
		lineLabel.getElement().getStyle().setProperty("min-width", "5.5rem");
		lineLabel.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		
		lineTextBox = new TextBox();
		lineTextBox.setHeight("2em");
		lineTextBox.getElement().getStyle().setProperty("padding", "0 5px");
		lineTextBox.addValueChangeHandler(e -> { if(null != fee) fee.setLine(Double.parseDouble(e.getValue()));});
		lineTextBox.setValue(fee.getLine().toString());
		
		Label confidentialLabel = new Label("Confidencial");
		confidentialLabel.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		confidentialButton = new Button();
		confidentialButton.addClickHandler(e -> {
			getEnableDisableButton(confidentialButton, !isActiveToggleButton(confidentialButton));
			if(null != fee) fee.setConfidential(isActiveToggleButton(confidentialButton));
		});
		getEnableDisableButton(confidentialButton, fee.isConfidential());
		
		lineConfidentialPanel.add(lineLabel);
		lineConfidentialPanel.add(lineTextBox);
		lineConfidentialPanel.add(confidentialLabel);
		lineConfidentialPanel.add(confidentialButton);
	}

	private void createProductPanel() {
		productPanel = new HTMLPanel("");
		productPanel.addStyleName(AON.CSS.aonItemFlex());
		
		Label productLabel = new Label("Producto");
		productLabel.getElement().getStyle().setProperty("min-width", "5.5rem");
		productLabel.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		
		productSuggestBox = new SuggestBox();
		productSuggestBox.setWidth("100%");
		productSuggestBox.setHeight("2em");
		productSuggestBox.getElement().getStyle().setProperty("padding", "0 5px");
		productSuggestBox.setAutoSelectEnabled(false);
		productSuggestBox.getElement().setPropertyString("placeholder", "Producto: busque por c\u00f3digo o descripci\u00f3n");
		
		productSuggestBox.addSelectionHandler(e -> {
			productSuggestBox.hideSuggestionList();
			SERVICE.getItemIdByProductCode(options.getDomainName(), options.getDomain(), options.getUser(), productSuggestions.get(productSuggestBox.getValue()), new AsyncCallback<Integer>() {

				@Override
				public void onFailure(Throwable caught) {
					// TODO Auto-generated method stub
				}

				@Override
				public void onSuccess(Integer itemId) {
					item = itemId;
					if(null != fee) fee.getItem().setId(itemId);
				}});
		});
		
		productSuggestBox.addKeyUpHandler(e -> {
			String productQuery = productSuggestBox.getValue();
			if(AonStringUtils.isNotBlank(productQuery) && productQuery.length() > 3) {
				SERVICE.getProductsSuggestion(options.getDomainName(), options.getDomain(), options.getUser(), productQuery, new AsyncCallback<Map<String, String>>() {
					
					@Override
					public void onSuccess(Map<String, String> productSuggestionsDB) {
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
		});
		
		if(null != fee) productSuggestBox.setValue(fee.getItem().getProduct().getName() + " ( " + fee.getItem().getProduct().getCode() + " )");
		else if(this.isSameProduct) productSuggestBox.setValue(product);
		
		productPanel.add(productLabel);
		productPanel.add(productSuggestBox);
	}

	private void createQuantityPricePanel() {
		quantityPricePanel = new HTMLPanel("");
		quantityPricePanel.addStyleName(AON.CSS.aonItemFlex());
		
		Label quantityLabel = new Label();
		if(null != this.fee) {
			quantityLabel = new Label("Cantidad");
			quantityLabel.getElement().getStyle().setProperty("min-width", "5.5rem");
			quantityLabel.getElement().getStyle().setFontWeight(FontWeight.BOLD);
			
			quantityTextBox = new TextBox();
			quantityTextBox.setHeight("2em");
			quantityTextBox.getElement().getStyle().setProperty("padding", "0 5px");
			quantityTextBox.addValueChangeHandler(e -> { if(null != fee) fee.setQuantity(Double.parseDouble(e.getValue()));});
			if(null != fee) quantityTextBox.setValue(fee.getQuantity().toString());
		}
		
		Label priceLabel = new Label("Precio");
		if(null == this.fee) priceLabel.getElement().getStyle().setProperty("min-width", "5.5rem");
		priceLabel.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		
		priceTextBox = new TextBox();
		priceTextBox.setHeight("2em");
		priceTextBox.getElement().getStyle().setProperty("padding", "0 5px");
		priceTextBox.addValueChangeHandler(e -> { if(null != fee) fee.setPrice(Double.parseDouble(e.getValue()));});
		if(null != fee) priceTextBox.setValue(fee.getPrice().toString());
		
		Label discountLabel = new Label("Descuento");
		discountLabel.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		
		discountTextBox = new TextBox();
		discountTextBox.setHeight("2em");
		discountTextBox.getElement().getStyle().setProperty("padding", "0 5px");
		discountTextBox.addValueChangeHandler(e -> { if(null != fee) fee.setDiscountExpr(e.getValue());});
		if(null != fee) discountTextBox.setValue(fee.getDiscountExpr());
		
		if(null != this.fee) {
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
		startDateBox.getElement().getStyle().setProperty("padding", "0 5px");
		startDateBox.getElement().getStyle().setTextAlign(TextAlign.CENTER);
		startDateBox.addStyleName("gwt-TextBox");
		startDateBox.addValueChangeHandler(e -> { if(null != fee) fee.setStartDate(e.getValue());});
		if(null != fee) startDateBox.setValue(fee.getStartDate());
		
		Label endDateLabel = new Label("F. Fin");
		endDateLabel.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		
		endDateBox = new AonDateBox();
		endDateBox.setHeight("2em");
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
		
		billingDateBox = new AonDateBox();
		billingDateBox.setHeight("2em");
		billingDateBox.getElement().getStyle().setProperty("padding", "0 5px");
		billingDateBox.getElement().getStyle().setTextAlign(TextAlign.CENTER);
		billingDateBox.addStyleName("gwt-TextBox");
		billingDateBox.addValueChangeHandler(e -> {
			if(null != fee) {
				if(null == e.getValue()) fee.setBillingDate(null);
				else fee.setBillingDate(DateUtils.getFirstDayOfMonth(e.getValue()));
			}
		});
		if(null != fee) billingDateBox.setValue(fee.getBillingDate());
		
		Label periodLabel = new Label();
		if(null != this.fee) {
			periodLabel = new Label("Periodo");
			periodLabel.getElement().getStyle().setFontWeight(FontWeight.BOLD);
			
			periodListBox = new ListBox();
			periodListBox.setHeight("2em");
			periodListBox.addItem("Sin periodo", "0");
			periodListBox.addItem("Mensual", "1");
			periodListBox.addItem("Bimensual", "2");
			periodListBox.addItem("Trimestral", "3");
			periodListBox.addItem("Cuatrimestral", "3");
			periodListBox.addItem("Semestral", "6");
			periodListBox.addItem("Anual", "12");
			periodListBox.addChangeHandler(e -> { if(null != fee) fee.setPeriod(BillingPeriod.safeValueOf(periodListBox.getSelectedValue()));});
			if(null != fee) setSelectedValueLB(periodListBox, fee.getPeriod().getValue().toString());
		}
		
		periodicityPanel.add(billingLabel);
		periodicityPanel.add(billingDateBox);
		
		if(null != this.fee) {
			periodicityPanel.add(periodLabel);
			periodicityPanel.add(periodListBox);
		}
		
		container.add(periodicityPanel);
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
		
		if(null != this.fee) {
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

	private void acceptDialog() {
		onAccept(
			Optional.ofNullable(item),
			Optional.ofNullable(AonStringUtils.isBlank(priceTextBox.getValue()) ? null : Double.parseDouble(priceTextBox.getValue())),
			Optional.ofNullable(AonStringUtils.isBlank(discountTextBox.getValue()) ? null : discountTextBox.getValue()),
			Optional.ofNullable(null == startDateBox.getValue() ? null : startDateBox.getValue()),
			Optional.ofNullable(null == endDateBox.getValue() ? null : endDateBox.getValue()),
			Optional.ofNullable(null == billingDateBox.getValue() ? null : billingDateBox.getValue())
		);
	}

	// ------------------------------------------------- Abstract Methods

	protected abstract void onAccept(Fee fee);
	protected abstract void onAccept(Optional<Integer> item, Optional<Double> price, Optional<String> discountExpr, Optional<Date> startDate, Optional<Date> endDate, Optional<Date> billingDate);

}
