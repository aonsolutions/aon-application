package com.esferalia.aon.gwt.fiscal.client.accounting.amortization;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayGrid;
import com.esferalia.aon.gwt.fiscal.client.accounting.amortization.AmortizationPanel.AmortizationPanelCallback;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

class AccountingAmortizationTable extends ScrollPanel {
	
	AccountingAmortizationTable( AmortizationModuleOptions opts, AmortizationPanelCallback callback) {
		setStyleName( AON.CSS.aonScrollArea() );
		setWidget( getTable(opts, callback) );
	}

	private Widget getTable(AmortizationModuleOptions opts, AmortizationPanelCallback callback) {
		AonDisplayGrid grid = new AonDisplayGrid();
		grid.addStyleName( AON.CSS.aonBlockCenter() );
		grid.addHeaderRow()
			.addCell( new Label(AON.MSG.from()), AON.CSS.aonWidth120())
			.addCell( new Label(AON.MSG.until()), AON.CSS.aonWidth120())
			.addCell( new Label(AON.MSG.percent()), AON.CSS.aonWidth80())
			.addCell( new Label(AON.MSG.allocation()) , AON.CSS.aonWidth150())
			.addCell( new Label(AON.MSG.accumulated()), AON.CSS.aonWidth150())
			.addCell( new Label(AON.MSG.pending()), AON.CSS.aonWidth150())
			.addCell( new Label(AON.MSG.allocation()), AON.CSS.aonWidth150())
			.addCell( new Label(AON.MSG.accumulated()), AON.CSS.aonWidth150())
			.addCell( new Label(AON.MSG.pending()), AON.CSS.aonWidth150())
			.addCell( new Label(AON.MSG.taxAdjust()), AON.CSS.aonWidth150())
			.addCell( new Label(AON.MSG.status()), AON.CSS.aonWidth100())
			.addCell( new Label(""), AON.CSS.aonWidth100())
		;
		callback.getAmortization().detailStream()
			.forEach( d -> grid.add( new AmortizationDetailTableRow(opts, callback, d) ) )
		;
		return grid;
	}
	
}
