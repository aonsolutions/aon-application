package com.esferalia.aon.gwt.fiscal.client.accounting;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.api.model.AccountEntryDetail;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Label;

public class AccountEntryPrinter {
	public static FocusPanel print(AccountEntry entry) {
		final FocusPanel entryPanel = new FocusPanel();
		entryPanel.setTabIndex(Integer.MAX_VALUE);
		FlowPanel panel = new FlowPanel("pre");
		panel.setStyleName(AON.AON_CSS.aonClickableBlock());		
		panel.addStyleName(AON.AON_CSS.aonFixedFont());
		panel.addStyleName(AON.AON_CSS.aonFontMedium());
		panel.addStyleName(AON.AON_CSS.aonMarginBottom());
		
		// ------------------------------------- CABECERA DEL ASIENTO
		panel.add( getHeader(entry));
		
		// ------------------------------------------------- APUNTES		
		double sumD = 0.0;
		double sumC = 0.0;
		for (AccountEntryDetail aed : entry.getDetails()) {
			panel.add(getDetail(aed));
			sumD = AonMathUtils.sum(sumD, aed.getDebit());	
			sumC = AonMathUtils.sum(sumC, aed.getCredit());
		}
		
		// ------------------------------------------------- TOTALES		
		panel.add(getTotals(sumD,sumC));
		// -----------------------------------------------------------
		entryPanel.setWidget(panel);
		return entryPanel;
	}
	
	public static Label getHeader(AccountEntry entry) {
		Label header = new Label();
		header.setStyleName(AON.AON_CSS.aonBold());
		header.addStyleName(AON.AON_CSS.aonTextUnderline());
		header.addStyleName(AON.AON_CSS.aonPre());
		header.setText(toString(entry,160));
		if (AonStringUtils.isNotBlank(entry.getComments())) {
			header.setTitle(entry.getComments());
		}
		return header;
	}

	public static String toString(AccountEntry entry, int lineSize) {
		StringBuffer buf = new StringBuffer();
		buf.append(entry.isConfidential()
			?AonStringUtils.OPEN_BRACKET 
			 + AON.MSG.confidential() 
			 + AonStringUtils.CLOSE_BRACKET 
			 + AonStringUtils.SPACE
			:AonStringUtils.repeat(AonStringUtils.SPACE, 14));
		buf.append(AonStringUtils.SPACE);
		buf.append((entry.getActivity() == null) 
			?AonStringUtils.repeat(AonStringUtils.SPACE, 40)
			:AonStringUtils.rightPad(AonStringUtils.abbreviate(AonStringUtils.defaultString(entry.getActivityDescription()), 39), 40));
		buf.append(AON.MSG.date());
		buf.append(AonStringUtils.COLON);
		buf.append(AonStringUtils.SPACE);
		buf.append(AON.DATE_FORMAT.format(entry.getEntryDate()));
		buf.append(AonStringUtils.SPACE);
		buf.append(AON.MSG.journal());
		buf.append(AonStringUtils.COLON);
		buf.append(AonStringUtils.SPACE);
		buf.append(AonStringUtils.rightPad(entry.getJournal(),10));
		buf.append(AonStringUtils.SPACE);
		if (AonStringUtils.isNotBlank(entry.getComments())) {
			buf.append("[");
			buf.append(AonStringUtils.abbreviate(entry.getComments(), 38));
			buf.append("]");
		} else {
			buf.append(AonStringUtils.repeat(AonStringUtils.SPACE, 40));
		}
		buf.append(AonStringUtils.leftPad(entry.getEntryType().getDescription(), lineSize - buf.length()));
		return buf.toString();
	}
	
	
	
	public static Label getDetail(AccountEntryDetail det) {
		Label detail = new Label(toString(det));
		return detail;
	}
	
	public static Label getTotals(double deb, double cre) {
		Label totals = new Label(toString(deb,cre));
		return totals;
	}

	public static String toString(AccountEntryDetail detail) {
		return toString(detail.getAccountCode()
			,detail.getAccountDescription()
			,detail.getConcept()
			,detail.getDebit()		
			,detail.getCredit()
			,detail.getBalancingAccountCode()
			,detail.getDocumentNumber());
	}
	
	public static String toString(double deb, double cre) {
		return toString(null,null,null,deb,cre,null,null);
	}
	

	public static String toString(String ac,String ad,String c,double deb,double cre,String bc,String dn) {
		StringBuffer buf = new StringBuffer();
		buf.append(AonStringUtils.rightPad(AonStringUtils.defaultString(ac),10));
		buf.append(AonStringUtils.rightPad(AonStringUtils.abbreviate(AonStringUtils.defaultString(ad), 39), 40));
		buf.append(AonStringUtils.rightPad(AonStringUtils.abbreviate( AonStringUtils.defaultString(c), 32), 33));
		buf.append(AonStringUtils.leftPad(AON.FMT.format(deb),17));		
		buf.append(AonStringUtils.leftPad(AON.FMT.format(cre),17));
		buf.append(AonStringUtils.center(AonStringUtils.defaultString(bc),11));
		buf.append(AonStringUtils.rightPad(AonStringUtils.defaultString(dn), 33));
		return buf.toString();
	}
	

}
