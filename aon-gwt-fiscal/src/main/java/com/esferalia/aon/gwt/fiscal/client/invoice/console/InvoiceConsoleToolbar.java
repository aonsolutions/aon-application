package com.esferalia.aon.gwt.fiscal.client.invoice.console;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.google.gwt.event.dom.client.ClickHandler;

class InvoiceConsoleToolbar extends AonToolbar {
	
	private final AonToolbarButton showFilter;
	private final AonToolbarButton hideFilter;
	
	InvoiceConsoleToolbar() {
		super("Monitor de facturas");
		
		showFilter = new AonToolbarButton(AON.MSG.showFilter(), AON.CSS.aonIconFilterOn());
		showFilter.setVisible(false);
		this.add(showFilter);

		hideFilter = new AonToolbarButton(AON.MSG.hideFilter(), AON.CSS.aonIconFilterOff());
		hideFilter.setVisible(true);
		this.add(hideFilter);
		
		showFilter.addClickHandler(e -> {
			hideFilter.setVisible(true);
			showFilter.setVisible(false);
		});
		hideFilter.addClickHandler(e -> {
			hideFilter.setVisible(false);
			showFilter.setVisible(true);
		});

		refresh();
	}
	
	void addClickHandlerToShowFilter( ClickHandler handler ) {
		showFilter.addClickHandler(handler);
	}
	void addClickHandlerToHideFilter( ClickHandler handler ) {
		hideFilter.addClickHandler(handler);
	}

	public void refresh() {
		// Nothing to do yet
	}
	
}
