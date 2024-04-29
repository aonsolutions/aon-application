package com.esferalia.aon.gwt.fiscal.client.registry;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.RegistryService;
import com.esferalia.aon.gwt.common.client.RegistryServiceAsync;
import com.esferalia.aon.gwt.common.client.RegistryServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.fee.Fee;
import com.esferalia.aon.occam.api.model.product.OldItem;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.SuggestBox;

public abstract class SyncCustomerFeeDialog extends AonCustomDialog {

	// ------------------------------------------------- Variables (UI)
	
	private HTMLPanel container;
	private HTMLPanel mainPanel;
	
	private HTMLPanel messagePanel;
	
	private HTMLPanel customerFeePanel;
	private SuggestBox customerFeeSuggestBox;
	
	private HTMLPanel buttonsPanel;
	
	// ------------------------------------------------- Variables
	
	private static RegistryServiceAsync SERVICE;
	private RegistryModuleOptions options;
	
	private Map<String, Fee> customerFeeSuggestions = new TreeMap<>();

	private OldItem item;
	private Customer customer;
	
	private Fee selectedCustomerFee;
	private Integer ritem;

	// ------------------------------------------------- Constructor

	public SyncCustomerFeeDialog(RegistryModuleOptions options, OldItem item, Customer customer, Integer ritem) {
		setCaption("Vincular Cuota Existente");
		
		this.item = item;
		this.customer = customer;
		this.options = options;
		this.ritem = ritem;
		
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
		
		createCustomerFeePanel();
		container.add(customerFeePanel);
		
		getButtonsPanel();
		container.add(buttonsPanel);
		
		mainPanel.add(container);
		
		this.setWidget(mainPanel);
	}

	private void createCustomerFeePanel() {
		customerFeePanel = new HTMLPanel("");
		customerFeePanel.addStyleName(AON.CSS.aonItemFlex());
		
		Label customerFeeLabel = new Label("Cuota");
		customerFeeLabel.getElement().getStyle().setProperty("min-width", "5.5rem");
		customerFeeLabel.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		
		customerFeeSuggestBox = new SuggestBox();
		customerFeeSuggestBox.setWidth("100%");
		customerFeeSuggestBox.setHeight("2em");
		customerFeeSuggestBox.getElement().getStyle().setProperty("padding", "0 5px");
		customerFeeSuggestBox.getElement().getStyle().setProperty("min-width", "400px");
		customerFeeSuggestBox.setAutoSelectEnabled(false);
		customerFeeSuggestBox.getElement().setPropertyString("placeholder", "Cuota: ctrl + espacio para ver sugerencias");
		
		getCustomerFeeSuggestion(null);
		
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

		});
		
		customerFeeSuggestBox.addValueChangeHandler(e -> {
			if(AonStringUtils.isBlank(customerFeeSuggestBox.getValue())) AonMessagePanel.showError(messagePanel, "El campo cuota es obligatorio");
		});
		
		customerFeePanel.add(customerFeeLabel);
		customerFeePanel.add(customerFeeSuggestBox);
	}
	
	private void getCustomerFeeSuggestion(String customerFeeQuery) {
		SERVICE.getCustomerFeeSuggestion(options.getDomainName(), options.getDomain(), options.getUser(), item.getId(), customer.getId(), customerFeeQuery, new AsyncCallback<Map<String, Fee>>() {
			
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
		acceptBtnDialog = new Button();
		acceptBtnDialog.setStyleName(AON.CSS.aonDialogButton());
		acceptBtnDialog.addStyleName(AON.CSS.aonIconSave());
		acceptBtnDialog.setText(AON.MSG.saveAction());
		acceptBtnDialog.addClickHandler(e -> {
			hide();
			onSyncCustomerFee(selectedCustomerFee, ritem);
		});

		buttonsPanel.add(acceptBtnDialog);
	}

	// ------------------------------------------------- Abstract Methods

	protected abstract void onSyncCustomerFee(Fee fee, Integer ritem);

}
