package com.esferalia.aon.gwt.fiscal.client.registry;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDockLayout;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomMultiSelectBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonRegistryPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonRegistryPanel.AonCustomerPanelCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.RegistryParams;
import com.esferalia.aon.occam.api.model.registry.CreditorFull;
import com.esferalia.aon.occam.api.model.registry.CustomerFull;
import com.esferalia.aon.occam.api.model.registry.RegistrySource;
import com.esferalia.aon.occam.api.model.registry.SupplierFull;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.SimplePanel;


public abstract class CustomerModulePanel extends DeckPanel {
	
	private static CommonServiceAsync COMMON_SERVICE;
	
	private AonCustomDockLayout aonCustomDockLayout;
	
	private HTMLPanel container;
	private HTMLPanel messagePanel = new HTMLPanel("");
	
	private SimplePanel centerPanel;
	private CustomerPanel customerPanel;
	
	private AonCustomMultiSelectBox status = new AonCustomMultiSelectBox("Estado");
	
	private RegistryEntryPanel registryEntryPanel;
	
	private RegistryModuleOptions options;
	
	private List<Account> accounts;
	
	public CustomerModulePanel(RegistryModuleOptions options) {
		super();
		
		CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
		COMMON_SERVICE = new CommonServiceAsyncDecorator(commonServiceRaw);
		
		this.options = options;
		
		aonCustomDockLayout = new AonCustomDockLayout("Clientes") {
			
			@Override
			protected void onClearFilter() {
				aonCustomDockLayout.getSearchTextBox().setValue(null, false);
				
				Set<String> selectedOptions = new LinkedHashSet<String>();
				selectedOptions.add("Activo");
				status.setSelectedOptions(selectedOptions);
				
				customerPanel.resetSearchOffset();
				
				onSearch( options );
			}
		};
		
		addButtonsToolbar();
		
		aonCustomDockLayout.addKeyUpHandler(e -> {
			String value = aonCustomDockLayout.getSearchTextBox().getValue();
			if(AonStringUtils.isNotBlank(value) && value.length() > 3) {
				onSearch( options );
			} else if(AonStringUtils.isBlank(value)) {
				onSearch( options );
			}
		});
		
		aonCustomDockLayout.setSearchPlaceholder("Buscar por nombre ...");
		
		Set<String> customerStatusoptions = new LinkedHashSet<String>();
		customerStatusoptions.add("Activo");
		customerStatusoptions.add("Inactivo");
		customerStatusoptions.add("Bloqueado");
		status.setOptions(customerStatusoptions);

		status.addBlurHandler(new BlurHandler() {
			@Override
			public void onBlur(BlurEvent event) {
				if (status.getSelectedOptions().isEmpty()) {
					Set<String> selectedOptions = new LinkedHashSet<String>();
					selectedOptions.add("Activo");
					selectedOptions.add("Bloqueado");
					status.setSelectedOptions(selectedOptions);
				}

				onSearch(options);
			}
		});

		Set<String> selectedOptions = new LinkedHashSet<String>();
		selectedOptions.add("Activo");
		selectedOptions.add("Bloqueado");
		status.setSelectedOptions(selectedOptions);

		aonCustomDockLayout.addFilterWidget(status);
		
		container = new HTMLPanel("");
		container.addStyleName(AON.CSS.aonFlexColumn());
		
		container.add(messagePanel);
	
		centerPanel = new SimpleLayoutPanel();
		centerPanel.setHeight("100%");
		
		container.add(centerPanel);
		
		aonCustomDockLayout.add(container);
		
		add(aonCustomDockLayout);
		showWidget(0);
		
		getContext(end -> onSearch( options ));
		
	}

	private void addButtonsToolbar() {
		AonToolbarButton newButton = new AonToolbarButton( "Nuevo Cliente", AON.CSS.aonIconAdd());
		newButton.addClickHandler(e -> {
			CustomerFull newCustomer = CustomerFull.initialize(options.getDomain());
			
			AonCustomDialog dialog = new AonCustomDialog();
			dialog.setCaption("Nuevo Cliente");
			dialog.setResizable(false);

			AonRegistryPanel aonRecordDataPanel = new AonRegistryPanel(options.getDomainName(), options.getDomain(), options.getUser(), newCustomer,
					options.getConfiguration().getAvailableScopes(), options.getConfiguration().getGeozones(), options.getConfiguration().getPayMethods(),
					new AonCustomerPanelCallback() {

						@Override
						public void onCancel() {
							dialog.hide();
						}

						@Override
						public void onAccept(CustomerFull customerFull) {
							dialog.hide();
							
							aonCustomDockLayout.getSearchTextBox().setValue(customerFull.getRegistry().getDocument());
							
							customerPanel.resetSearchOffset();
							onSearch( options );
						}

						@Override public void onAccept(CreditorFull creditorFull) {}

						@Override public void onAccept(SupplierFull SupplierFull) {}
					});

			dialog.add(aonRecordDataPanel);
			dialog.showLoaded();
		});
		
		aonCustomDockLayout.addToolbarButton(newButton);
	}

	public void onSearch( RegistryModuleOptions options ) {
		RegistryParams params = getWidgetParams( options );
		centerPanel.clear();
		customerPanel = new CustomerPanel(params, centerPanel, accounts) {

			@Override
			protected void onCustomerOpen(Customer customer) {
				if(getWidgetCount() > 1) CustomerModulePanel.this.remove(1);
				
				registryEntryPanel = new RegistryEntryPanel(options, RegistrySource.CUSTOMER, customer.getId()) {

					@Override
					protected void onBack() {
						CustomerModulePanel.this.showWidget(0);
						customerPanel.resetSearchOffset();
						onSearch( options );
					}
					
				};
				registryEntryPanel.showBackButton();
				
				CustomerModulePanel.this.add(registryEntryPanel);
				CustomerModulePanel.this.showWidget(1);
							
			}
			
			@Override
			protected void onShowErrorMessage(String errorMessage) {
				AonMessagePanel.showError(messagePanel, errorMessage);
			}
		
		};
		
		centerPanel.setWidget(customerPanel);
	}

	public RegistryParams getWidgetParams( RegistryModuleOptions options) {
		RegistryParams params = new RegistryParams()
			.setDomainName(options.getDomainName())
			.setDomain(options.getDomain())
			.setUser(options.getUser())
			.setDescription(aonCustomDockLayout.getSearchTextBox().getValue())
			;
		
		params.setActive(status.getSelectedOptions().contains("Activo"));
		params.setInactive(status.getSelectedOptions().contains("Inactivo"));
		params.setBlocked(status.getSelectedOptions().contains("Bloqueado"));
		
		return params;
	}
	
	public RegistryParams getSellerListParams() {
		return getWidgetParams(options);
	}
	
	private void getContext(Consumer<Void> end) {
		COMMON_SERVICE.getAccountsForRegistry(options.getDomainName(), options.getDomain(), options.getUser(), RegistrySource.CUSTOMER, new AsyncCallback<List<Account>>() {

			@Override
			public void onFailure(Throwable caught) {
				AonMessagePanel.showError(messagePanel, "Error cuentas contables: " + caught.getMessage());
			}

			@Override
			public void onSuccess(List<Account> accountsDB) {
				accounts = accountsDB;
				end.accept(null);
			}
		});
	}
	
	protected abstract void onCustomerCreate(CustomerFull customerFull);

}
