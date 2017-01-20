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
		panel.addStyleName(AON.AON_CSS.aonFontMedium());
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
		buf.append(AonStringUtils.rightPad(finance.getInvoice().getDocumentNumber(),14));
		buf.append(AonStringUtils.rightPad(finance.getRegistryAccountCode(),10));
		buf.append(AonStringUtils.rightPad(AonStringUtils.abbreviate(finance.getRegistryName(),29),30));
		buf.append(AonStringUtils.SPACE);
		buf.append(AON.DATE_FORMAT.format(finance.getDueDate()));
		buf.append(AonStringUtils.SPACE);
		buf.append(AonStringUtils.leftPad(AON.FMT.format(finance.getAmount()),15));		
		buf.append(AonStringUtils.SPACE);
		return buf.toString();
	}
}
