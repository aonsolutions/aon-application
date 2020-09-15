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
		return print(entry,false);
	}
		
	public static FocusPanel print(AccountEntry entry, boolean abbrv) {		
		final FocusPanel entryPanel = new FocusPanel();
		entryPanel.setTabIndex(Integer.MAX_VALUE);
		FlowPanel panel = new FlowPanel("pre");
		panel.setStyleName(AON.AON_CSS.aonClickableBlock());		
		panel.addStyleName(AON.AON_CSS.aonFixedFont());
		panel.addStyleName(AON.AON_CSS.aonFontMedium());
		panel.addStyleName(AON.AON_CSS.aonMarginBottom());
		
		// ------------------------------------- CABECERA DEL ASIENTO
		panel.add( getHeader(entry, abbrv));
		
		// ------------------------------------------------- APUNTES		
		double sumD = 0.0;
		double sumC = 0.0;
		for (AccountEntryDetail aed : entry.getDetails()) {
			panel.add(getDetail(aed, abbrv));
			sumD = AonMathUtils.sum(sumD, aed.getDebit());	
			sumC = AonMathUtils.sum(sumC, aed.getCredit());
		}
		
		// ------------------------------------------------- TOTALES		
		panel.add(getTotals(sumD,sumC,abbrv));
		// -----------------------------------------------------------
		entryPanel.setWidget(panel);
		return entryPanel;
	}
	
	public static Label getHeader(AccountEntry entry, boolean abbrv) {
		Label header = new Label();
		header.setStyleName(AON.AON_CSS.aonBold());
		header.addStyleName(AON.AON_CSS.aonTextUnderline());
		header.addStyleName(AON.AON_CSS.aonPre());
		header.setText(toString(entry, abbrv));
		if (AonStringUtils.isNotBlank(entry.getComments())) {
			header.setTitle(entry.getComments());
		}
		return header;
	}
	
	public static String toString(AccountEntry entry, boolean abbrv) {
		int lineSize = abbrv?110:172; 
		StringBuffer buf = new StringBuffer();
		if (!abbrv) {
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
		} else {
			buf.append(AonStringUtils.repeat(AonStringUtils.SPACE, 30));
		}
		buf.append(AonStringUtils.repeat(AonStringUtils.SPACE, 8));
		buf.append(AON.MSG.date());
		buf.append(AonStringUtils.COLON);
		buf.append(AonStringUtils.SPACE);
		buf.append(AON.DATE_FORMAT.format(entry.getEntryDate()));
		buf.append(AonStringUtils.SPACE);
		buf.append(AON.MSG.journal());
		buf.append(AonStringUtils.COLON);
		buf.append(AonStringUtils.SPACE);
		buf.append(AonStringUtils.rightPad(entry.getJournal()==null?"????":""+entry.getJournal(),10));
		if (!abbrv) {
			buf.append(AonStringUtils.SPACE);
			if (AonStringUtils.isNotBlank(entry.getComments())) {
				buf.append("[");
				buf.append(AonStringUtils.abbreviate(AonStringUtils.removeTabsAndNewLine(entry.getComments()), 38));
				buf.append("]");
			} else {
				buf.append(AonStringUtils.repeat(AonStringUtils.SPACE, 40));
			}
		}
		buf.append(AonStringUtils.leftPad(entry.getEntryType().getDescription(), lineSize - buf.length()));
		return buf.toString();
	}
	
	
	
	public static Label getDetail(AccountEntryDetail det, boolean abbrv) {
		Label detail = new Label(toString(det,abbrv));
		detail.setStyleName(null);
		return detail;
	}
	
	public static Label getTotals(double deb, double cre, boolean abbrv) {
		Label totals = new Label(toString(deb,cre,abbrv));
		totals.setStyleName(AON.AON_CSS.aonBold());
		return totals;
	}

	public static String toString(AccountEntryDetail detail, boolean abbrv) {
		return toString(detail.getAccountCode()
			,detail.getAccountDescription()
			,detail.getConcept()
			,detail.getDebit()		
			,detail.getCredit()
			,detail.getBalancingAccountCode()
			,detail.getDocumentNumber()
			,abbrv);
	}
	
	public static String toString(double deb, double cre, boolean abbrv) {
		return toString(null,null,null,deb,cre,null,null,abbrv);
	}
	

	public static String toString(String ac,String ad,String c,double deb,double cre,String bc,String dn,boolean abbrv) {
		StringBuffer buf = new StringBuffer();
		buf.append(AonStringUtils.rightPad(AonStringUtils.defaultString(ac),10));
		buf.append(AonStringUtils.rightPad(AonStringUtils.abbreviate(AonStringUtils.defaultString(ad), abbrv?30:39), abbrv?31:40));
		buf.append(AonStringUtils.rightPad(AonStringUtils.abbreviate( AonStringUtils.defaultString(c), abbrv?20:45), abbrv?21:45));
		buf.append(AonStringUtils.leftPad(AON.FMT.format(deb),17));		
		buf.append(AonStringUtils.leftPad(AON.FMT.format(cre),17));
		buf.append(AonStringUtils.center(AonStringUtils.defaultString(bc),11));
		if (!abbrv) {
			buf.append(AonStringUtils.rightPad(AonStringUtils.defaultString(dn), 33));
		}
		return buf.toString();
	}
	

}
