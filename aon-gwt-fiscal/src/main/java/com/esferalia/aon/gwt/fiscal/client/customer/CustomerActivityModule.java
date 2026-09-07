package com.esferalia.aon.gwt.fiscal.client.customer;

import java.util.logging.Logger;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;

import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.customer.CustomersLinkedParams;
import com.google.gwt.logging.client.ConsoleLogHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DeckLayoutPanel;

import static com.esferalia.aon.gwt.fiscal.client.EntryPointUtils.*;
import com.google.gwt.core.client.EntryPoint;

public class CustomerActivityModule  implements EntryPoint {

	private static final Logger LOGGER = Logger.getLogger(CustomerActivityModule.class.getName());
	static {
		LOGGER.addHandler(new ConsoleLogHandler());
	}

	private DeckLayoutPanel deckLayoutPanel;
	private CustomersLinkedPanel customersLinkedPanel;
	private ActivitySummary activitySummary;
	
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

		customersLinkedPanel = new CustomersLinkedPanel(params) {

			@Override
			protected void onCustomerSelect(Integer customerId, String customerName, Domain customerDomain) {
				showSelectedCustomer(customerId, customerName, customerDomain);
			}

		};
		
		activitySummary = new ActivitySummary(params.getUser());
		
		AonToolbarButton back = new AonToolbarButton(AON.MSG.backAction(), AON.CSS.aonIconBack());
		back.addClickHandler(e -> showCustomersLinkedPanel());
		
		activitySummary.addToolbarButtonStart(back);

		deckLayoutPanel.add(customersLinkedPanel);
		deckLayoutPanel.add(activitySummary);
		deckLayoutPanel.showWidget(customersLinkedPanel);

		root.add(deckLayoutPanel);
	}

	private void showCustomersLinkedPanel() {
		deckLayoutPanel.showWidget(customersLinkedPanel);
	}

	private void showSelectedCustomer(Integer customerId, String customerName, Domain customerDomain) {
		deckLayoutPanel.showWidget(activitySummary);
		activitySummary.showCustomerDomainInfo(customerId, customerName, customerDomain);
	}

}

