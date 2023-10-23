package com.esferalia.aon.occam.test.accounting.entry;

import java.io.PrintStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.api.model.AccountEntryDetail;
import com.esferalia.aon.watson.util.AonStringUtils;

public class AccountEntryPrinter {
	
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
	private static final DecimalFormat FMT = new  DecimalFormat("#,##0.00");

	static void print( PrintStream out, AccountEntry entry) {
		boolean abbrv = true; 
		out.println(sep(abbrv));
		out.println(toString(entry,abbrv));
		out.println(sepInner(abbrv));
		entry.getDetails().stream().forEach(det -> out.println(toString(det,abbrv)));
		out.println(sep(abbrv));
		out.println();
		out.flush();
	}
	public static String sep(boolean abbrv) {
		int lineSize = abbrv?110:172;
		return AonStringUtils.repeat(AonStringUtils.ASTERISK, lineSize);
	}
	public static String sepInner(boolean abbrv) {
		int lineSize = abbrv?110:172;
		return AonStringUtils.repeat(AonStringUtils.HYPHEN, lineSize);
	}
	public static String toString(AccountEntry entry, boolean abbrv) {
		int lineSize = abbrv?110:172; 
		StringBuffer buf = new StringBuffer();
		if (!abbrv) {
			buf.append(entry.isConfidential()
					?AonStringUtils.OPEN_BRACKET 
							+ "Confid." 
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
		buf.append("Fecha");
		buf.append(AonStringUtils.COLON);
		buf.append(AonStringUtils.SPACE);
		buf.append(DATE_FORMAT.format(entry.getEntryDate()));
		buf.append(AonStringUtils.SPACE);
		buf.append("Diario");
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
		buf.append(AonStringUtils.leftPad(FMT.format(deb),17));		
		buf.append(AonStringUtils.leftPad(FMT.format(cre),17));
		buf.append(AonStringUtils.center(AonStringUtils.defaultString(bc),11));
		if (!abbrv) {
			buf.append(AonStringUtils.rightPad(AonStringUtils.defaultString(dn), 33));
		}
		return buf.toString();
	}
	

}
