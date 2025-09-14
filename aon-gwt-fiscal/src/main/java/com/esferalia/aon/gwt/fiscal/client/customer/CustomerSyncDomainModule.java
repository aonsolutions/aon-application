package com.esferalia.aon.gwt.fiscal.client.customer;

import java.util.logging.Logger;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDialog;
import com.esferalia.aon.gwt.fiscal.client.MainEntryPoint;
import com.esferalia.aon.occam.api.model.customer.CustomersLinkedParams;
import com.google.gwt.logging.client.ConsoleLogHandler;
import com.google.gwt.user.client.ui.DeckLayoutPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;

public class CustomerSyncDomainModule extends MainEntryPoint {

	private static final Logger LOGGER = Logger.getLogger(CustomerSyncDomainModule.class.getName());
	static {
		LOGGER.addHandler(new ConsoleLogHandler());
	}

	private DeckLayoutPanel deckLayoutPanel;
	private CustomersNotLinkedPanel customersNotLinkedPanel;
	
	private RootLayoutPanel root;
	
	private CustomersLinkedParams params;
	
	@Override
	public void onModuleLoad() {
		root = RootLayoutPanel.get(getRootPanel() != null ? getRootPanel() : "rootPanel");
		
		params = new CustomersLinkedParams();
		params.setDomainName(getCurrentDomainName());
		params.setDomainId(getCurrentDomain());
		params.setUser(getCurrentUser());
		
		moduleLoad();
	}

	public void moduleLoad() {
		AON.ensureInjected();

		deckLayoutPanel = new DeckLayoutPanel();

		customersNotLinkedPanel = new CustomersNotLinkedPanel(params) {

			@Override
			protected void onCustomerSelect(Integer customerId, String customerName) {
				showSelectedCustomer(customerId, customerName);
			}

		};

		deckLayoutPanel.add(customersNotLinkedPanel);
		deckLayoutPanel.showWidget(customersNotLinkedPanel);

		root.add(deckLayoutPanel);
	}

	private void showSelectedCustomer(Integer customerId, String customerName) {
		SimpleLayoutPanel centerPanel = new SimpleLayoutPanel();
		centerPanel.setHeight("20rem");
		centerPanel.getElement().getStyle().setProperty("margin", "1rem");

		// Clase que contenga los dominios libres
		centerPanel.setWidget(new Label(customerId.toString()));

		AonCustomDialog dialog = new AonCustomDialog();
		dialog.showCloseButton(true);
		dialog.setCaption(customerName);
		dialog.setHeight("25rem");
		dialog.setWidth("65rem");
		dialog.add(centerPanel);
		dialog.showLoaded();
	}

}

