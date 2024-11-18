package com.esferalia.aon.gwt.fiscal.client.finance.utilities;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonLayoutPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.occam.api.model.Domain;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public class FinanceUtilitiesModulePanel extends AonLayoutPanel{

	protected static interface IOption extends HasSelectionHandlers<IOption> {
		Widget getSidebarWidget();
		String getOptionDescription();
	}
	
	public FinanceUtilitiesModulePanel(FinanceUtilitiesModuleOptions options, Domain domain) {
		super( Unit.PX);
	
		setStyleName(AON.CSS.aonSelector());
		AonToolbar toolbarPanel = new AonToolbar("Utilidades de facturaci\u00F3a / tesorer\u00EDa");
		addNorth(toolbarPanel, AonToolbar.HEIGTH);
		
		SimpleLayoutPanel sidebar = new SimpleLayoutPanel();
		sidebar.setStyleName(AON.CSS.aonBorderRight());
		addWest(sidebar, 275);
		
		ScrollPanel scrollPanel = new ScrollPanel(); 
		scrollPanel.setStyleName(AON.CSS.aonWidthAll());
		sidebar.setWidget(scrollPanel);
		
		FlowPanel sidebarMenu = new FlowPanel();
		scrollPanel.setWidget(sidebarMenu);
		SimpleLayoutPanel content = new SimpleLayoutPanel();
		sidebarMenu.add(getInvoicesOptionsPanel(options, content,domain));
		sidebarMenu.add(getFinanceOptionsPanel(options, content,domain));
		add( content );
	}
	
	private Widget getInvoicesOptionsPanel(FinanceUtilitiesModuleOptions options, SimpleLayoutPanel content, Domain domain) {
		DisclosurePanel invoiceDisclosurePanel = new DisclosurePanel("FACTURAS");
		invoiceDisclosurePanel.setOpen(true);
		FlowPanel invoicePanel = new FlowPanel();
		invoiceDisclosurePanel.add(invoicePanel);
		
		if (options.isAdvancedMode()) {
			InvoiceIntegrityCheck invoiceIntegrityCheck = new InvoiceIntegrityCheck(options,domain);
			invoicePanel.add(invoiceIntegrityCheck.getSidebarWidget());
			invoiceIntegrityCheck.addSelectionHandler( event -> content.setWidget( invoiceIntegrityCheck ));
		}
		
		ActivityUpdateCheck activityTypeCheck = new ActivityUpdateCheck(options,domain);
		invoicePanel.add(activityTypeCheck.getSidebarWidget());
		activityTypeCheck.addSelectionHandler( event -> content.setWidget( activityTypeCheck ));
		
		WithholdingTypeCheck withholdingTypeCheck = new WithholdingTypeCheck(options,domain);
		invoicePanel.add(withholdingTypeCheck.getSidebarWidget());
		withholdingTypeCheck.addSelectionHandler( event -> content.setWidget( withholdingTypeCheck ));

		return invoiceDisclosurePanel;
		
	}

	private Widget getFinanceOptionsPanel(FinanceUtilitiesModuleOptions options, SimpleLayoutPanel content, Domain domain) {
		DisclosurePanel financeDisclosurePanel = new DisclosurePanel("VENCIMIENTOS");
		financeDisclosurePanel.setOpen(true);
		FlowPanel financePanel = new FlowPanel();
		financeDisclosurePanel.add(financePanel);
		
		MissingFinanceInvoicesCheck missingFinanceInvoices = new MissingFinanceInvoicesCheck(options,domain);
		financePanel.add(missingFinanceInvoices.getSidebarWidget());
		missingFinanceInvoices.addSelectionHandler( event -> content.setWidget( missingFinanceInvoices ));

		
		FinanceInvoiceIntegrityCheck financeInvoiceIntegrityCheck = new FinanceInvoiceIntegrityCheck(options,domain);
		financePanel.add(financeInvoiceIntegrityCheck.getSidebarWidget());
		financeInvoiceIntegrityCheck.addSelectionHandler( event -> {
			content.setWidget( financeInvoiceIntegrityCheck );
			financeInvoiceIntegrityCheck.run();
		});
		return financeDisclosurePanel;
	}
	
}
