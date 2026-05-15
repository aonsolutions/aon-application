package com.esferalia.aon.gwt.fiscal.client.registry;

import java.util.LinkedHashSet;
import java.util.Set;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.RegistryService;
import com.esferalia.aon.gwt.common.client.RegistryServiceAsync;
import com.esferalia.aon.gwt.common.client.RegistryServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDockLayout;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomMultiSelectBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomerFullPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomerPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomerPanel.AonCustomerPanelCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonRegistryFullPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonRegistryFullPanel.AonRegistryFullPanelCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonSimpleDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.RegistryParams;
import com.esferalia.aon.occam.api.model.registry.CreditorFull;
import com.esferalia.aon.occam.api.model.registry.CustomerFull;
import com.esferalia.aon.occam.api.model.registry.SupplierFull;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.SimplePanel;


public abstract class CustomerModulePanel extends AonCustomDockLayout {
	
	private static RegistryServiceAsync REGISTRY_SERVICE;
	
	private HTMLPanel container;
	private HTMLPanel messagePanel = new HTMLPanel("");
	
	private SimplePanel centerPanel;
	private CustomerPanel customerPanel;
	
	private AonCustomMultiSelectBox status = new AonCustomMultiSelectBox("Estado");
	
	private RegistryModuleOptions options;
	
	public CustomerModulePanel(RegistryModuleOptions options) {
		super("Clientes");
		
		RegistryServiceAsync registryServiceRaw = GWT.create(RegistryService.class);
		REGISTRY_SERVICE = new RegistryServiceAsyncDecorator(registryServiceRaw);
		
		this.options = options;
		
		addButtonsToolbar();
		
		addKeyUpHandler(e -> {
			String value = getSearchTextBox().getValue();
			if(AonStringUtils.isNotBlank(value) && value.length() > 3) {
				onSearch( options );
			} else if(AonStringUtils.isBlank(value)) {
				onSearch( options );
			}
		});
		
		setSearchPlaceholder("Buscar por nombre ...");
		
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

		addFilterWidget(status);
		
		container = new HTMLPanel("");
		container.addStyleName(AON.CSS.aonFlexColumn());
		
		container.add(messagePanel);
	
		centerPanel = new SimpleLayoutPanel();
		centerPanel.setHeight("100%");
		
		container.add(centerPanel);
		
		add(container);
		onSearch( options );
	}

	@Override
	protected void onClearFilter() {
		getSearchTextBox().setValue(null, false);
		
		Set<String> selectedOptions = new LinkedHashSet<String>();
		selectedOptions.add("Activo");
		status.setSelectedOptions(selectedOptions);
		
		customerPanel.resetSearchOffset();
		
		onSearch( options );
	}

	private void addButtonsToolbar() {
		AonToolbarButton newButton = new AonToolbarButton( "Nuevo Cliente", AON.CSS.aonIconAdd());
		newButton.addClickHandler(e -> {
			CustomerFull newCustomer = CustomerFull.initialize(options.getDomain());
			selectCustomer(options, newCustomer, new AonRegistryFullPanelCallback<CustomerFull>() {
				@Override
				public void onError(Throwable caught) {
					AonMessagePanel.showError(messagePanel, caught.getMessage());
				}
					
				@Override
				public void onAccept(CustomerFull rf) {
					customerPanel.resetSearchOffset();
					onSearch( options );
				}

				@Override
				public void onCancel() {
					// Empty method
				}

				@Override
				public void onDocumenthanged(CustomerFull registryFull) {
					// Empty method
				}

				@Override
				public void setFocus(boolean b) {
					// Empty method	
				}
			});
		});
		
		addToolbarButton(newButton);
		
		if(options.getDomainName().contains("aonsolutions.org")) {
			AonToolbarButton newButton2 = new AonToolbarButton( "Nuevo Cliente", AON.CSS.aonIconMoreVertical());
			newButton2.addClickHandler(e -> {
				CustomerFull newCustomer = CustomerFull.initialize(options.getDomain());
				
				AonCustomDialog dialog = new AonCustomDialog();
				dialog.setCaption("Nuevo Cliente");

				AonCustomerPanel aonRecordDataPanel = new AonCustomerPanel(options.getDomainName(), options.getDomain(), options.getUser(), newCustomer,
						options.getConfiguration().getAvailableScopes(), options.getConfiguration().getGeozones(),
						new AonCustomerPanelCallback() {

							@Override
							public void onCancel() {
								dialog.hide();
							}

							@Override
							public void onAccept(CustomerFull customerFull) {
								dialog.hide();
								
								customerPanel.resetSearchOffset();
								onSearch( options );
							}

							@Override public void onAccept(CreditorFull creditorFull) {}

							@Override public void onAccept(SupplierFull SupplierFull) {}
						});

				dialog.add(aonRecordDataPanel);
				dialog.showLoaded();
			});
			
			addToolbarButton(newButton2);
		}
	}
	
	private void selectCustomer(RegistryModuleOptions opt, CustomerFull customer, AonRegistryFullPanelCallback<CustomerFull> panelCallback) {
		final AonSimpleDialog dialog = new AonSimpleDialog();
		dialog.setWidth(AonRegistryFullPanel.MIN_WIDTH +  "px");
		dialog.setHeight(AonRegistryFullPanel.MIN_HEIGHT +  "px");
		dialog.setCaption(AON.MSG.customer());
		
		AonCustomerFullPanel aonCustomerFullPanel = new AonCustomerFullPanel(opt, customer, new AonRegistryFullPanelCallback<CustomerFull>() {
			@Override
			public void setFocus(boolean b) {
				if (panelCallback != null) panelCallback.setFocus(b);
			}
			
			@Override
			public void onError(Throwable caught) {
				if (panelCallback != null) panelCallback.onError(caught);
			}
			
			@Override
			public void onCancel() {
				dialog.hide();
				if (panelCallback != null) panelCallback.onCancel();
			}
			
			@Override
			public void onAccept(CustomerFull rf) {
				dialog.hide();
				if (panelCallback != null) panelCallback.onAccept(rf);
				customerPanel.resetSearchOffset();
				onSearch( options );
			}
			@Override
			public void onDocumenthanged(CustomerFull registryFull) {
				if (panelCallback != null) panelCallback.onDocumenthanged(registryFull);
			}	
		});
		
		dialog.add( aonCustomerFullPanel );
		dialog.center();
		dialog.show();
		
		Scheduler.get().scheduleDeferred(() -> aonCustomerFullPanel.setFocus(true));				
	}

	public void onSearch( RegistryModuleOptions options ) {
		RegistryParams params = getWidgetParams( options );
		centerPanel.clear();
		customerPanel = new CustomerPanel(params, centerPanel) {

			@Override
			protected void onCustomerOpen(Customer customer) {
				// Open dialog customer
				selectCustomer(options, customer, new AonRegistryFullPanelCallback<CustomerFull>() {
					@Override
					public void onError(Throwable caught) {
						AonMessagePanel.showError(messagePanel, caught.getMessage());
					}
						
					@Override
					public void onAccept(CustomerFull rf) {
						// Add new customer to table
					}

					@Override
					public void onCancel() {
						// Empty method
					}

					@Override
					public void onDocumenthanged(CustomerFull registryFull) {
						// Empty method
					}

					@Override
					public void setFocus(boolean b) {
						// Empty method	
					}
				});
			}

			@Override
			protected void onShowErrorMessage(String errorMessage) {
				AonMessagePanel.showError(messagePanel, errorMessage);
			}
		
		};
		
		centerPanel.setWidget(customerPanel);
	}
	
	private void selectCustomer(RegistryModuleOptions opt, Customer customer, AonRegistryFullPanelCallback<CustomerFull> panelCallback) {
		
		REGISTRY_SERVICE.getCustomerFull(opt.getDomainName(), opt.getDomain(), opt.getUser(), customer.getId(), new AsyncCallback<CustomerFull>() {	
			@Override
			public void onSuccess(CustomerFull result) {
				selectCustomer(opt, result, panelCallback);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				AonMessagePanel.showError(messagePanel, caught.getMessage());	
			}
		});					
	}

	public RegistryParams getWidgetParams( RegistryModuleOptions options) {
		RegistryParams params = new RegistryParams()
			.setDomainName(options.getDomainName())
			.setDomain(options.getDomain())
			.setUser(options.getUser())
			.setDescription(getSearchTextBox().getValue())
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
