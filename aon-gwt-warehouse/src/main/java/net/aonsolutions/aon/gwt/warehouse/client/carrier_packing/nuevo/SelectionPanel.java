package net.aonsolutions.aon.gwt.warehouse.client.carrier_packing.nuevo;

import com.esferalia.aon.gwt.common.client.AON;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;


public class SelectionPanel extends ResizeComposite implements RequiresResize {
	
	static final String BACKGROUND_COLOR = "#DDDDDD";

	public SelectionPanel() {		
		SplitLayoutPanel rootPanel = new SplitLayoutPanel(4);
		
		//  -------------------------- WORKING LOG ------------------------------

		SimpleLayoutPanel centerPanel = new SimpleLayoutPanel();
		centerPanel.setStyleName(AON.AON_CSS.aonFlexContainer());
		centerPanel.addStyleName(AON.AON_CSS.aonPadding2Top());
		centerPanel.setStyleName(AON.AON_CSS.aonInvoicePanel());
		centerPanel.getElement().getStyle().setBackgroundColor(BACKGROUND_COLOR);
		centerPanel.getElement().getStyle().setMarginLeft(0, Unit.PX);

		//  -------------------------- EXTRA PANEL ------------------------------
		SimpleLayoutPanel extraPanel = new SimpleLayoutPanel();
		extraPanel.setStyleName(AON.AON_CSS.aonFlexContainer());
		extraPanel.addStyleName(AON.AON_CSS.aonPadding2Top());
		extraPanel.setStyleName(AON.AON_CSS.aonInvoicePanel());
		extraPanel.getElement().getStyle().setBackgroundColor(BACKGROUND_COLOR);
		extraPanel.getElement().getStyle().setMarginLeft(0, Unit.PX);
		rootPanel.addEast(extraPanel, 380);

		SimpleLayoutPanel centerContainerPanel = new SimpleLayoutPanel();
		ScrollPanel scrollCenterContainer = new ScrollPanel();
		FlowPanel centerContainer = new FlowPanel();	
		
		scrollCenterContainer.setWidget(centerContainer);
		centerContainerPanel.setWidget(scrollCenterContainer);
		centerPanel.setWidget(centerContainerPanel);
		rootPanel.add(centerPanel);

		initWidget(rootPanel);
	}
	
	

}
