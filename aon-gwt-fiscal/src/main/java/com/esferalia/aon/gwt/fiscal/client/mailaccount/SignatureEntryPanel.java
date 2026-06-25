package com.esferalia.aon.gwt.fiscal.client.mailaccount;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.RegistryService;
import com.esferalia.aon.gwt.common.client.RegistryServiceAsync;
import com.esferalia.aon.gwt.common.client.RegistryServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDockLayout;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomListBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.SignatureTable;
import com.esferalia.aon.gwt.fiscal.client.registry.RegistryModuleOptions;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.HTMLPanel;

public class SignatureEntryPanel extends AonCustomDockLayout {
	
	// ------------------------------------------------- CommonServiceAsync

	static CommonServiceAsync commonService;

	private static void initializeCommonService() {
		if (commonService == null) {
			CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
			commonService = new CommonServiceAsyncDecorator(commonServiceRaw);
		}
	}
	
	// ------------------------------------------------- RegistryServiceAsync
	
	static RegistryServiceAsync registryService;
	
	private static void initializeRegistryService() {
		if (registryService == null) {
			RegistryServiceAsync registryServiceRaw = GWT.create(RegistryService.class);
			registryService = new RegistryServiceAsyncDecorator(registryServiceRaw);
		}
	}

	// ------------------------------------------------- Variables

	private AonCustomListBox type = new AonCustomListBox("Tipo");

	private RegistryModuleOptions options;

	private HTMLPanel container;
	private HTMLPanel messagePanel;
	private SignatureTable signatureTable;
	
	// ------------------------------------------------- Constructor

	public SignatureEntryPanel(RegistryModuleOptions options) {
		super("Firmas Correo");

		this.options = options;

		initializeCommonService();
		initializeRegistryService();
		
		createToolbar();
		
		addKeyUpHandler(e -> {
			String value = getSearchTextBox().getValue();
			if(AonStringUtils.isNotBlank(value) && value.length() > 2) {
				signatureTable.setSearchPattern(value);
				signatureTable.onSearch();
			} else if(AonStringUtils.isBlank(value)) {
				signatureTable.setSearchPattern(null);
				signatureTable.onSearch();
			}
		});
		
		setSearchPlaceholder("Buscar por nombre ...");
		
		type.addItem( "Todas", "all");
		type.addItem( "Empresa", "enterprise");
		type.addItem( "Usuario", "user");
		type.getListBox().addChangeHandler(event -> {
			signatureTable.setTypeFilter(type.getValue());
			signatureTable.onSearch();
		});
		
		addFilterWidget(type);
		
		messagePanel = new HTMLPanel(AonStringUtils.EMPTY);

		container = new HTMLPanel(AonStringUtils.EMPTY);
		container.addStyleName(AON.CSS.aonFlexColumn2());
		container.getElement().getStyle().setProperty("padding", "1rem");
		
		container.add(messagePanel);

		signatureTable = new SignatureTable(this.options.getDomainName(), this.options.getDomain(), this.options.getUser(), false) {
			
			@Override
			protected void onShowErrorMessage(String errorMessage) {
				AonMessagePanel.showError(messagePanel, errorMessage);
			}
		};
		
		container.add(signatureTable);

		add(container);
	}

	@Override
	protected void onClearFilter() {
		getSearchTextBox().setValue(null);
		type.getListBox().setSelectedIndex(0);
		signatureTable.setSearchPattern(null);
		signatureTable.setTypeFilter("all");
		signatureTable.onSearch();
		
	}

	private void createToolbar() {
		AonToolbarButton createButton = new AonToolbarButton(AON.MSG.newAction(), AON.CSS.aonIconAdd());
		createButton.addClickHandler(e -> {
			signatureTable.createSignature();
		});
		addToolbarButton(createButton);
	}

}
