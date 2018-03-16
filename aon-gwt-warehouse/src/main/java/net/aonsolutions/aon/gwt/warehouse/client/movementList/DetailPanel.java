package net.aonsolutions.aon.gwt.warehouse.client.movementList;

import java.util.LinkedList;

import com.esferalia.aon.gwt.api.client.warehouse.JsStockStat;
import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Label;

public class DetailPanel extends FlowPanel implements HasSelectionHandlers<JsStockStat>{
	
	private Main parent;
	
	public DetailPanel(Main parent, LinkedList<JsStockStat> itemList) {
		super("pre");
		this.parent = parent;
		createPanel(itemList);
	}
	
	@Override
	public HandlerRegistration addSelectionHandler(SelectionHandler<JsStockStat> handler) {
		return super.addHandler(handler, SelectionEvent.getType());
	}
	
	private void createPanel(LinkedList<JsStockStat> itemList){
		
		final FlowPanel headerPanel = new FlowPanel("pre");
		headerPanel.setStyleName(AON.AON_CSS.aonFixedFont());
		headerPanel.addStyleName(AON.AON_CSS.aonFontMedium());
		Label header = new Label(""
				+ AonStringUtils.rightPad("Producto", 75)
				+ AonStringUtils.rightPad("Entradas", 15)
				+ AonStringUtils.rightPad("Salidas", 15)
				+ AonStringUtils.rightPad("Saldo", 15)
				+ "      ");  //6
		header.setStyleName(AON.AON_CSS.aonBold());
		header.addStyleName(AON.AON_CSS.aonMarginTop());
		header.addStyleName(AON.AON_CSS.aonBorderTop());
		header.addStyleName(AON.AON_CSS.aonBorderBottom());
		headerPanel.add(header);
		this.add(headerPanel);
		
		for (final JsStockStat item: itemList) {
			final FocusPanel entryPanel = print(item);
			this.add(entryPanel);
			entryPanel.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					parent.onSelectItem(item);
				}
			});
		}
		
	}
	
	private FocusPanel print(JsStockStat item) {
		final FocusPanel entryPanel = new FocusPanel();
		entryPanel.setTabIndex(Integer.MAX_VALUE);
		FlowPanel panel = new FlowPanel("pre");
		panel.setStyleName(AON.AON_CSS.aonClickableBlock());		
		panel.addStyleName(AON.AON_CSS.aonFixedFont());
		panel.addStyleName(AON.AON_CSS.aonFontMedium());
		panel.addStyleName(AON.AON_CSS.aonMarginBottom());
		panel.add(getLine(item));
		entryPanel.setWidget(panel);
		return entryPanel;
	}
	
	private Label getLine(JsStockStat item) {
		StringBuffer buf = new StringBuffer();
		buf.append(AonStringUtils.SPACE);
		buf.append(AonStringUtils.rightPad("", 9));
		buf.append(AonStringUtils.rightPad(AonStringUtils.abbreviate(AonStringUtils.defaultString(item.getProductName()), 74), 75));
		buf.append(AonStringUtils.rightPad(AonStringUtils.defaultString(item.getInputs()+""), 15));
		buf.append(AonStringUtils.rightPad(AonStringUtils.defaultString(item.getOutputs()+""), 15));
		buf.append(AonStringUtils.rightPad(AonStringUtils.defaultString(item.getBalance()+""), 15));
		buf.append(AonStringUtils.rightPad("", 17));
		Label line = new Label(buf.toString());
		line.setStyleName(AON.AON_CSS.aonBold());
		return line;
	}
	
	
}