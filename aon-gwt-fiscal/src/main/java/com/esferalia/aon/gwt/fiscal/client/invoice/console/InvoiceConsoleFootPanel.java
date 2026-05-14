package com.esferalia.aon.gwt.fiscal.client.invoice.console;

import java.util.function.Supplier;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMinimizePanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTabLayoutPanel;
import com.esferalia.aon.gwt.fiscal.client.invoice.AonOpenIfNeededEvent;
import com.esferalia.aon.gwt.fiscal.client.invoice.AonOpenIfNeededHandler;
import com.esferalia.aon.gwt.fiscal.client.invoice.HasOpenIfNeededHandlers;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Widget;

class InvoiceConsoleFootPanel extends AonMinimizePanel implements HasOpenIfNeededHandlers {

	private AonTabLayoutPanel tabLayout;
	 
	public InvoiceConsoleFootPanel() {
		setStyleName(AON.CSS.aonSelector());
		tabLayout = new AonTabLayoutPanel(26, Unit.PX);
		tabLayout.setWidth("100%");
		
		add(tabLayout);
		
		tabLayout.setAnimationDuration(300);
		tabLayout.addSelectionHandler(event -> AonOpenIfNeededEvent.fire(InvoiceConsoleFootPanel.this) );
	}

	@Override
	public HandlerRegistration addOpenIfNeededHandler(AonOpenIfNeededHandler handler) {
		return super.addHandler(handler, AonOpenIfNeededEvent.getType());
	}

	public Widget addWidget( String title, Supplier<Widget> widgetSupplier) {
		return tabLayout.getOrCreateWidget(title, widgetSupplier);
	}
}
