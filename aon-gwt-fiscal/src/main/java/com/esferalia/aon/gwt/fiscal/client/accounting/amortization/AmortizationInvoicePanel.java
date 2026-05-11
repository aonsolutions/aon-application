package com.esferalia.aon.gwt.fiscal.client.accounting.amortization;

import java.util.Objects;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.Wnd;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.fiscal.client.accounting.amortization.AmortizationPanel.AmortizationPanelCallback;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;

class AmortizationInvoicePanel extends DockLayoutPanel {

	private final SimpleLayoutPanel contentPanel;
	private final SimpleLayoutPanel eastPanel;
	
	AmortizationInvoicePanel(AmortizationModuleOptions opts, AmortizationPanelCallback callback) {
		super(Unit.PX);
		
		AonToolbar toolbar = new AonToolbar( );
		addNorth(toolbar, AonToolbar.HEIGTH);
		
		AonToolbarButton addButton = new AonToolbarButton(AON.MSG.addInvoice(), AON.CSS.aonIconAdd());
		addButton.addClickHandler( event -> onAddInvoice( opts, callback) );
		toolbar.add(addButton);
		
		eastPanel = new SimpleLayoutPanel();
		addEast(eastPanel, 0);
		
		this.addAttachHandler(e -> {
			Wnd.consoleLog("AmortizationInvoicePanel attached, adjusting layout...");
			
			Wnd.getCSSOptionalVariable(toolbar, "height-adjust")
			.map( AonNumberUtils::toInteger ).filter(Objects::nonNull)
			.ifPresent( height -> this.setWidgetSize(toolbar, height) );

			Wnd.getCSSOptionalVariable(eastPanel, "width-adjust")
			.map( AonNumberUtils::toInteger ).filter(Objects::nonNull)
			.ifPresent( width -> this.setWidgetSize(eastPanel, width) );
		
		});

		contentPanel = new SimpleLayoutPanel();
		add(contentPanel);
		
		search(opts, callback);
		
	}

	private void search(AmortizationModuleOptions opts, AmortizationPanelCallback callback) {
		contentPanel.clear();
		AmortizationInvoiceTable table = new AmortizationInvoiceTable(opts, callback);
		contentPanel.setWidget(table);
	}

	private void onAddInvoice(AmortizationModuleOptions opts, AmortizationPanelCallback callback) {
		eastPanel.clear();
		AmortizationInvoiceSelectionPanel p = new AmortizationInvoiceSelectionPanel(opts, callback);
		eastPanel.setWidget(p);

		this.setWidgetSize(eastPanel, (Window.getClientWidth() / 2));
		this.animate(200); 
	}
	
	
}
