package com.esferalia.aon.gwt.fiscal.client.registry;

import java.util.LinkedHashSet;
import java.util.Set;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDockLayout;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomMultiSelectBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonRegistryPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonRegistryPanel.AonCustomerPanelCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.occam.api.model.RegistryParams;
import com.esferalia.aon.occam.api.model.registry.Creditor;
import com.esferalia.aon.occam.api.model.registry.CreditorFull;
import com.esferalia.aon.occam.api.model.registry.CustomerFull;
import com.esferalia.aon.occam.api.model.registry.RegistrySource;
import com.esferalia.aon.occam.api.model.registry.SupplierFull;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.SimplePanel;


public abstract class CreditorModulePanel extends DeckPanel {
	
	private AonCustomDockLayout aonCustomDockLayout;
	
	private HTMLPanel container;
	private HTMLPanel messagePanel = new HTMLPanel("");
	
	private SimplePanel centerPanel;
	private CreditorPanel creditorPanel;
	
	private AonCustomMultiSelectBox status = new AonCustomMultiSelectBox("Estado");
	
	private RegistryEntryPanel registryEntryPanel;
	
	private RegistryModuleOptions options;
	
	public CreditorModulePanel(RegistryModuleOptions options) {
		super();
	
		this.options = options;
		
		aonCustomDockLayout = new AonCustomDockLayout("Acreedores") {
			
			@Override
			protected void onClearFilter() {
				aonCustomDockLayout.getSearchTextBox().setValue(null, false);
				
				Set<String> selectedOptions = new LinkedHashSet<String>();
				selectedOptions.add("Activo");
				status.setSelectedOptions(selectedOptions);
				
				creditorPanel.resetSearchOffset();
				
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
		AonToolbarButton newButton = new AonToolbarButton( "Nuevo Acreedor", AON.CSS.aonIconAdd());
		newButton.addClickHandler(e -> {
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
		
		aonCustomDockLayout.addToolbarButton(newButton);
	}
	
	public void onSearch( RegistryModuleOptions options ) {
		RegistryParams params = getWidgetParams( options );
		centerPanel.clear();
		creditorPanel = new CreditorPanel(params, centerPanel) {

			@Override
			protected void onCreditorOpen(Creditor creditor) {
				if(getWidgetCount() > 1) CreditorModulePanel.this.remove(1);
				
				registryEntryPanel = new RegistryEntryPanel(options, RegistrySource.CREDITOR, creditor.getId()) {

					@Override
					protected void onBack() {
						CreditorModulePanel.this.showWidget(0);
						creditorPanel.resetSearchOffset();
						onSearch( options );
					}
					
				};
				registryEntryPanel.showBackButton();
				
				CreditorModulePanel.this.add(registryEntryPanel);
				CreditorModulePanel.this.showWidget(1);
			}

			@Override
			protected void onShowErrorMessage(String errorMessage) {
				AonMessagePanel.showError(messagePanel, errorMessage);
			}
		
		};
		
		centerPanel.setWidget(creditorPanel);
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
	
	protected abstract void onCreditorCreate(CreditorFull creditor);

}
