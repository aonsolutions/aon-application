package com.esferalia.aon.gwt.fiscal.client.domainstat;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.occam.api.model.DomainInvoiceStatParams;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;

class DomainInvoiceStatPanel extends DockLayoutPanel {
	
	private SimpleLayoutPanel container = new SimpleLayoutPanel();

	public DomainInvoiceStatPanel( final DomainInvoiceStatModuleOptions options ) {
		super(Unit.PX);
		AON.ensureInjected();
		AonToolbar toolbar = new AonToolbar(AON.MSG.domainInvoiceStat());
		this.addNorth(toolbar, AonToolbar.HEIGTH);
		
		DomainInvoiceStatFilterPanel filterPanel = new DomainInvoiceStatFilterPanel( options);
		this.addNorth(filterPanel, DomainInvoiceStatFilterPanel.HEIGTH);
		filterPanel.addValueChangeHandler( e -> search(options, e.getValue()) );
		
		this.add( container );
		search(options, filterPanel.getParams( options ));
	}

	private void search(DomainInvoiceStatModuleOptions options, DomainInvoiceStatParams params) {
		container.clear();
		DomainInvoiceStatTable table = new DomainInvoiceStatTable(options, params);
		container.setWidget( table );
	}

}
