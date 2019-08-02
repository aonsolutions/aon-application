package net.aonsolutions.aon.tedi.test;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.api.model.AccountEntryDetail;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class AccountEntryPrinter {
	private static SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("dd/MM/yyyy");
	private static DecimalFormat FMT  = new DecimalFormat("#,##0.00");
	private static int lineSize = 160;
	
	public static void print(AccountEntry entry) {
		// ------------------------------------- CABECERA DEL ASIENTO
		System.out.println();
		System.out.println( getHeader(entry) );
		System.out.println( AonStringUtils.repeat("-", lineSize) );

		// ------------------------------------------------- APUNTES		
		double sumD = 0.0;
		double sumC = 0.0;
		for (AccountEntryDetail aed : entry.getDetails()) {
			
			System.out.println( getDetail(aed));

			sumD = AonMathUtils.sum(sumD, aed.getDebit());	
			sumC = AonMathUtils.sum(sumC, aed.getCredit());
		}
		// ------------------------------------------------- TOTALES
		System.out.println( AonStringUtils.repeat("-", lineSize) );
		System.out.println(getTotals(sumD,sumC));
		System.out.println();
		// -----------------------------------------------------------
	}
	
	public static String getHeader(AccountEntry entry) {
		StringBuffer buf = new StringBuffer();
		buf.append(entry.isConfidential()
			?AonStringUtils.OPEN_BRACKET 
			 + "Confidential" 
			 + AonStringUtils.CLOSE_BRACKET 
			 + AonStringUtils.SPACE
			:AonStringUtils.repeat(AonStringUtils.SPACE, 14));
		buf.append(AonStringUtils.SPACE);
		buf.append((entry.getActivity() == null) 
			?AonStringUtils.repeat(AonStringUtils.SPACE, 40)
			:AonStringUtils.rightPad(AonStringUtils.abbreviate(AonStringUtils.defaultString(entry.getActivityDescription()), 39), 40));
		buf.append("Fecha");
		buf.append(AonStringUtils.COLON);
		buf.append(AonStringUtils.SPACE);
		buf.append(DATE_FORMATTER.format(entry.getEntryDate()));
		buf.append(AonStringUtils.SPACE);
		buf.append("N\u00BA de diario");
		buf.append(AonStringUtils.COLON);
		buf.append(AonStringUtils.SPACE);
		buf.append(AonStringUtils.rightPad( AonNumberUtils.toString(entry.getJournal() ),10));
		buf.append(AonStringUtils.SPACE);
		if (AonStringUtils.isNotBlank(entry.getComments())) {
			buf.append("[");
			buf.append(AonStringUtils.abbreviate(AonStringUtils.removeTabsAndNewLine(entry.getComments()), 38));
			buf.append("]");
		} else {
			buf.append(AonStringUtils.repeat(AonStringUtils.SPACE, 40));
		}
		buf.append(AonStringUtils.leftPad(entry.getEntryType().getDescription(), lineSize - buf.length()));
		return buf.toString();
	}
	
	public static String getDetail(AccountEntryDetail detail) {
		return toString(detail.getAccountCode()
			,detail.getAccountDescription()
			,detail.getConcept()
			,detail.getDebit()		
			,detail.getCredit()
			,detail.getBalancingAccountCode()
			,detail.getDocumentNumber());
	}

	public static String getTotals(double deb, double cre) {
		return toString(null,null,null,deb,cre,null,null);
	}
	
	public static String toString(String ac,String ad,String c,double deb,double cre,String bc,String dn) {
		StringBuffer buf = new StringBuffer();
		buf.append(AonStringUtils.rightPad(AonStringUtils.defaultString(ac),10));
		buf.append(AonStringUtils.rightPad(AonStringUtils.abbreviate(AonStringUtils.defaultString(ad), 39), 40));
		buf.append(AonStringUtils.rightPad(AonStringUtils.abbreviate( AonStringUtils.defaultString(c), 32), 33));
		buf.append(AonStringUtils.leftPad(FMT.format(deb),17));		
		buf.append(AonStringUtils.leftPad(FMT.format(cre),17));
		buf.append(AonStringUtils.center(AonStringUtils.defaultString(bc),11));
		buf.append(AonStringUtils.rightPad(AonStringUtils.defaultString(dn), 33));
		return buf.toString();
	}
	

}
