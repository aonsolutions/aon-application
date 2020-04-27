package com.esferalia.aon.gwt.fiscal.client.finance;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.occam.api.model.finance.Finance;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTML;

public class FinancePrinter {
	public static FocusPanel print(Finance finance) {
		final FocusPanel entryPanel = new FocusPanel();
		HTML line = new HTML();		
		line.addStyleName(AON.AON_CSS.aonFixedFont());
		line.addStyleName(AON.AON_CSS.aonFontSmall());
		line.addStyleName(AON.AON_CSS.aonPre());
		line.setHTML(toString(finance,160));
		entryPanel.setWidget(line);
		return entryPanel;
	}
	
	public static SafeHtml toString(Finance finance, int lineSize) {
		SafeHtmlBuilder buf = new SafeHtmlBuilder();
		buf.appendEscaped(AonStringUtils.SPACE);
		buf.appendEscaped(finance.isPayment()?"P":"C");
		buf.appendEscaped(AonStringUtils.SPACE);
		if (AonStringUtils.isNotBlank( finance.getRegistryAccountCode())) {
			buf.appendEscaped(AonStringUtils.rightPad(finance.getRegistryAccountCode(),10));
		} else {
			buf.appendEscaped("????????? ");
		}
		buf.appendEscaped(AonStringUtils.rightPad(AonStringUtils.abbreviate(finance.getRegistryName(),19),20));
		buf.appendEscaped(AonStringUtils.SPACE);
		String concept = finance.getConcept(); 
		if (finance.getInvoice() != null) {
			if ( !finance.getInvoice().isSales() ) {
				concept = finance.getInvoice().getReferenceCode();
			}
		} 
		buf.appendEscaped(AonStringUtils.rightPad(AonStringUtils.abbreviate(concept,19),20));
		buf.appendEscaped(finance.getDueDate()==null?"--/--/----":AON.DATE_FORMAT.format(finance.getDueDate()));
		buf.appendEscaped(AonStringUtils.leftPad(AON.FMT.format(finance.getAmount()),15));		
		buf.appendEscaped(AonStringUtils.SPACE);
		buf.appendEscaped(AonStringUtils.rightPad(AonStringUtils.abbreviate(AonStringUtils.defaultIfBlank(finance.getPayMethodName(), "----"),10),11));
		if (finance.isReturned()) {
			buf.appendHtmlConstant("<span style=\"color: red;\">");
			buf.appendEscaped(AonStringUtils.rightPad(AonStringUtils.abbreviate(finance.getFinanceStatus().getDescription(),7),8));
			buf.appendHtmlConstant("</span>"); 
		} else {
			buf.appendHtmlConstant("<span style=\"color: Royalblue;\">");
			buf.appendEscaped(AonStringUtils.rightPad(AonStringUtils.abbreviate(finance.getFinanceStatus().getDescription(),7),8));
			buf.appendHtmlConstant("</span>"); 
		}
		return buf.toSafeHtml();
	}
}
