package com.esferalia.aon.gwt.fiscal.client.invoice.console;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.InlineLabel;

class InvoiceConsoleToolbar extends AonToolbar {

	static interface ToolbarAsyncCallback {
		public void onStartRunning();
		public void onEndRunning();
	}	
	
	private final AonToolbarButton showFilter;
	private final AonToolbarButton hideFilter;
	
	private InlineLabel runningLabel = new InlineLabel("Ejecutando");
	
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
		
		runningLabel.setVisible(false);
		runningLabel.setStyleName(AON.CSS.aonMarginLeft());
		runningLabel.addStyleName(AON.CSS.aonColorWhite());
		runningLabel.addStyleName(AON.CSS.aonBizkaiaBackgroundColor());
		runningLabel.addStyleName(AON.CSS.aonPadding());
		runningLabel.addStyleName(AON.CSS.aonBold());
		this.add(runningLabel);
		
	}
	
	void addClickHandlerToShowFilter( ClickHandler handler ) {
		showFilter.addClickHandler(handler);
	}
	void addClickHandlerToHideFilter( ClickHandler handler ) {
		hideFilter.addClickHandler(handler);
	}

	void refresh() {
		runningLabel.setVisible(false);
	}

	void startRun(String string) {
		runningLabel.setText(string);
		runningLabel.setVisible(true);
	}

	void endRun() {
		runningLabel.setVisible(false);
	}
	
}
