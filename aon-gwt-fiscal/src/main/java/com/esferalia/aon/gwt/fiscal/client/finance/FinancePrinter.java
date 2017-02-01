package com.esferalia.aon.gwt.fiscal.client.finance;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.occam.api.model.finance.Finance;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Label;

public class FinancePrinter {
	public static FocusPanel print(Finance finance) {
		final FocusPanel entryPanel = new FocusPanel();
		entryPanel.setTabIndex(Integer.MAX_VALUE);
		FlowPanel panel = new FlowPanel();
		panel.setStyleName(AON.AON_CSS.aonClickableBlock());		
		panel.addStyleName(AON.AON_CSS.aonFixedFont());
		panel.addStyleName(AON.AON_CSS.aonFontSmall());
		panel.addStyleName(AON.AON_CSS.aonPre());
		Label header = new Label();
		header.setText(toString(finance,160));
		panel.add( header);
		entryPanel.setWidget(panel);
		return entryPanel;
	}
	
	public static String toString(Finance finance, int lineSize) {
		StringBuffer buf = new StringBuffer();
		buf.append(AonStringUtils.SPACE);
		buf.append(finance.isPayment()?"P":"C");
		buf.append(AonStringUtils.SPACE);
		if (AonStringUtils.isNotBlank( finance.getRegistryAccountCode())) {
			buf.append(AonStringUtils.rightPad(finance.getRegistryAccountCode(),10));
		} else {
			buf.append("????????? ");
		}
		buf.append(AonStringUtils.rightPad(AonStringUtils.abbreviate(finance.getRegistryName(),19),20));
		buf.append(AonStringUtils.SPACE);
		String concept = finance.getConcept(); 
		if (finance.getInvoice() != null) {
			if ( !finance.getInvoice().isSales() ) {
				concept = finance.getInvoice().getReferenceCode();
			}
		} 
		buf.append(AonStringUtils.rightPad(AonStringUtils.abbreviate(concept,19),20));
		buf.append(AON.DATE_FORMAT.format(finance.getDueDate()));
		buf.append(AonStringUtils.leftPad(AON.FMT.format(finance.getAmount()),15));		
		buf.append(AonStringUtils.SPACE);
		buf.append(AonStringUtils.rightPad(AonStringUtils.abbreviate(finance.getFinanceStatus().getDescription(),7),8));
		return buf.toString();
	}
}
