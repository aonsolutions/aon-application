package com.esferalia.aon.gwt.fiscal.client.registry;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDockLayout;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomMultiSelectBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonRegistryPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonRegistryPanel.AonCustomerPanelCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.RegistryParams;
import com.esferalia.aon.occam.api.model.registry.CreditorFull;
import com.esferalia.aon.occam.api.model.registry.CustomerFull;
import com.esferalia.aon.occam.api.model.registry.RegistrySource;
import com.esferalia.aon.occam.api.model.registry.SupplierFull;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.SimplePanel;


public abstract class CustomerModulePanel extends DeckPanel {
	
	
	private AonCustomDockLayout aonCustomDockLayout;
	
	private HTMLPanel container;
	private HTMLPanel messagePanel = new HTMLPanel("");
	
	private SimplePanel centerPanel;
	private CustomerPanel customerPanel;
	
	private AonCustomMultiSelectBox status = new AonCustomMultiSelectBox("Estado");
	
	private RegistryEntryPanel registryEntryPanel;
	
	private RegistryModuleOptions options;
	
	
	public CustomerModulePanel(RegistryModuleOptions options) {
		super();
		
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
		
		onSearch( options );
		
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
		customerPanel = new CustomerPanel(params, centerPanel) {

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

					@Override
					protected void onPrev(Integer registryId) {
						List<Customer> customers = customerPanel.getCustomers();

					    // Buscar índice del actual
					    int index = -1;
					    for (int i = 0; i < customers.size(); i++) {
					        if (customers.get(i).getId().equals(registryId)) {
					            index = i;
					            break;
					        }
					    }

					    if (index == -1) {
					        Window.alert("No se encontró el creditor con id " + registryId);
					        return;
					    }

					    Customer prev = (index > 0) ? customers.get(index - 1) : null;
					    					    // Ejemplo de uso
					    if (prev != null) {
					    	registryEntryPanel.loadNewRegistry(prev.getId());
					    } else {
					        Window.alert("No hay anterior");
					    }
					
					}

					@Override
					protected void onNext(Integer registryId) {
						List<Customer> customers = customerPanel.getCustomers();

					    // Buscar índice del actual
					    int index = -1;
					    for (int i = 0; i < customers.size(); i++) {
					        if (customers.get(i).getId().equals(registryId)) {
					            index = i;
					            break;
					        }
					    }

					    if (index == -1) {
					        Window.alert("No se encontró el creditor con id " + registryId);
					        return;
					    }

					    Customer next = (index < customers.size() - 1) ? customers.get(index + 1) : null;

					    if (next != null) {
					    	registryEntryPanel.loadNewRegistry(next.getId());
					    } else {
					        Window.alert("No hay siguiente");
					    }
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
	
	protected abstract void onCustomerCreate(CustomerFull customerFull);

}
