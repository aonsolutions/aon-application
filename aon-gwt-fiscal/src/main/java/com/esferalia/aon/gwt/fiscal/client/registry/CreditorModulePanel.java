package com.esferalia.aon.gwt.fiscal.client.registry;

import java.util.LinkedHashSet;
import java.util.Set;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.RegistryService;
import com.esferalia.aon.gwt.common.client.RegistryServiceAsync;
import com.esferalia.aon.gwt.common.client.RegistryServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCreditorFullPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDockLayout;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomMultiSelectBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonRegistryPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonRegistryPanel.AonCustomerPanelCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonRegistryFullPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonRegistryFullPanel.AonRegistryFullPanelCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonSimpleDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.occam.api.model.RegistryParams;
import com.esferalia.aon.occam.api.model.registry.Creditor;
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


public abstract class CreditorModulePanel extends AonCustomDockLayout {
	
	private static RegistryServiceAsync REGISTRY_SERVICE;
	
	private HTMLPanel container;
	private HTMLPanel messagePanel = new HTMLPanel("");
	
	private SimplePanel centerPanel;
	private CreditorPanel creditorPanel;
	
	private AonCustomMultiSelectBox status = new AonCustomMultiSelectBox("Estado");
	
	private RegistryModuleOptions options;
	
	public CreditorModulePanel(RegistryModuleOptions options) {
		super("Acreedores");
		
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
		
		creditorPanel.resetSearchOffset();
		
		onSearch( options );
	}

	private void addButtonsToolbar() {
		AonToolbarButton newButton = new AonToolbarButton( "Nuevo Acreedor", AON.CSS.aonIconAdd());
		newButton.addClickHandler(e -> {
			final AonSimpleDialog dialog = new AonSimpleDialog();
			dialog.setWidth(AonRegistryFullPanel.MIN_WIDTH +  "px");
			dialog.setHeight(AonRegistryFullPanel.MIN_HEIGHT +  "px");
			dialog.setCaption(AON.MSG.creditor());
			AonCreditorFullPanel aonCreditorFullPanel = new AonCreditorFullPanel(options, CreditorFull.initialize(options.getDomain()), new AonRegistryFullPanelCallback<CreditorFull>() {
				
				@Override
				public void onError(Throwable caught) {
					AonMessagePanel.showError(messagePanel,caught.getMessage());
				}
				
				@Override
				public void onAccept(CreditorFull cf) {
					dialog.hide();
					creditorPanel.resetSearchOffset();
					onSearch( options );
				}
				
				@Override
				public void onCancel() {
					dialog.hide();
					// Empty method
				}

				@Override
				public void onDocumenthanged(CreditorFull creditorFull) {
					// Empty method
				}
				
				@Override
				public void setFocus(boolean b) {
					// Empty method
				}
			});
			dialog.add( aonCreditorFullPanel );
			dialog.center();
			dialog.show();
			
			Scheduler.get().scheduleDeferred(() -> aonCreditorFullPanel.setFocus(true));
		});
		
		addToolbarButton(newButton);
		
		if(options.getDomainName().contains("aonsolutions.org")) {
			AonToolbarButton newButton2 = new AonToolbarButton( "Nuevo Acreedor", AON.CSS.aonIconMoreVertical());
			newButton2.addClickHandler(e -> {
				CreditorFull newCreditor = CreditorFull.initialize(options.getDomain());
				
				AonCustomDialog dialog = new AonCustomDialog();
				dialog.setCaption("Nuevo Acreedor");
				dialog.setResizable(false);
	
				AonRegistryPanel aonRecordDataPanel = new AonRegistryPanel(options.getDomainName(), options.getDomain(), options.getUser(), newCreditor,
						options.getConfiguration().getAvailableScopes(), options.getConfiguration().getGeozones(), options.getConfiguration().getPayMethods(),
						new AonCustomerPanelCallback() {
	
							@Override
							public void onCancel() {
								dialog.hide();
							}
	
							@Override public void onAccept(CustomerFull customerFull) {}
	
							@Override public void onAccept(CreditorFull creditorFull) {
								dialog.hide();
								
								creditorPanel.resetSearchOffset();
								onSearch( options );
							}
	
							@Override public void onAccept(SupplierFull SupplierFull) {}
						});
	
				dialog.add(aonRecordDataPanel);
				dialog.showLoaded();
			});
			
			addToolbarButton(newButton2);
		}
	}
	
	private void selectCreditor(RegistryModuleOptions opt, CreditorFull creditor, AonRegistryFullPanelCallback<CreditorFull> panelCallback) {
		final AonSimpleDialog dialog = new AonSimpleDialog();
		dialog.setWidth(AonRegistryFullPanel.MIN_WIDTH +  "px");
		dialog.setHeight(AonRegistryFullPanel.MIN_HEIGHT +  "px");
		dialog.setCaption(AON.MSG.creditor());
		
		AonCreditorFullPanel aonCreditorFullPanel = new AonCreditorFullPanel(opt, creditor, new AonRegistryFullPanelCallback<CreditorFull>() {
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
			public void onAccept(CreditorFull cf) {
				dialog.hide();
				if (panelCallback != null) panelCallback.onAccept(cf);
				creditorPanel.resetSearchOffset();
				onSearch( options );
			}
			
			@Override
			public void onDocumenthanged(CreditorFull creditorFull) {
				if (panelCallback != null) panelCallback.onDocumenthanged(creditorFull);
			}
		});
		dialog.add( aonCreditorFullPanel );
		dialog.center();
		dialog.show();
		
		Scheduler.get().scheduleDeferred(() -> aonCreditorFullPanel.setFocus(true));
	}
	
	public void onSearch( RegistryModuleOptions options ) {
		RegistryParams params = getWidgetParams( options );
		centerPanel.clear();
		creditorPanel = new CreditorPanel(params, centerPanel) {

			@Override
			protected void onCreditorOpen(Creditor creditor) {
				// Open dialog customer
				selectCreditor(options, creditor, new AonRegistryFullPanelCallback<CreditorFull>() {
					@Override
					public void onError(Throwable caught) {
						AonMessagePanel.showError(messagePanel, caught.getMessage());
					}
						
					@Override
					public void onAccept(CreditorFull rf) {
						// Add new customer to table
					}

					@Override
					public void onCancel() {
						// Empty method
					}

					@Override
					public void onDocumenthanged(CreditorFull registryFull) {
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
		
		centerPanel.setWidget(creditorPanel);
	}
	
	private void selectCreditor(RegistryModuleOptions opt, Creditor creditor, AonRegistryFullPanelCallback<CreditorFull> panelCallback) {
		
		REGISTRY_SERVICE.getCreditorFull(opt.getDomainName(), opt.getDomain(), opt.getUser(), creditor.getId(), new AsyncCallback<CreditorFull>() {	
			@Override
			public void onSuccess(CreditorFull result) {
				selectCreditor(opt, result, panelCallback);
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
	
	protected abstract void onCreditorCreate(CreditorFull creditor);

}
