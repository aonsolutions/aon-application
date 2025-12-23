package com.esferalia.aon.occam.test.accounting.entry;

import java.io.PrintStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.api.model.AccountEntryDetail;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class AccountEntryPrinter {
	
	private static final String TOP_LEFT_CORNER = "\u250C";
	private static final String TOP_RIGHT_CORNER = "\u2510";
	private static final String LOWER_LEFT_CORNER = "\u2514";
	private static final String LOWER_RIGHT_CORNER = "\u2518";
	private static final String VERTICAL_BAR = "\u2502";
	private static final String HORIZONTAL_BAR = "\u2500";
	private static final String VERTICAL_RIGHT_BAR = "\u251C"; // ?
	private static final String VERTICAL_LEFT_BAR = "\u2524"; // ?
	private static final String HORIZONTAL_DOWN_BAR = "\u252C"; // ?
	private static final String HORIZONTAL_UP_BAR = "\u2534"; // ?
	private static final String CROSS = "\u253C"; // ?
	
	private static int LINE_SIZE = 194;
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
	private static final DecimalFormat FMT = new  DecimalFormat("#,##0.00");

	public static void print( PrintStream out, AccountEntry entry) {
		start(out);
		toString(out, entry);
		startInner(out);
		AonCollectionUtils.stream(entry.getDetails())
			.forEach(det -> toString(out, det));
		endInner(out);
		total(out, entry);
		end(out);
		out.println();
		out.flush();
	}
	
	private static void start(PrintStream out) {
		StringBuffer buf = new StringBuffer();
		buf.append(AonStringUtils.SPACE);
		buf.append(TOP_LEFT_CORNER);
		buf.append(AonStringUtils.repeat(HORIZONTAL_BAR, LINE_SIZE - 1 - buf.length()));
		buf.append(TOP_RIGHT_CORNER);
		out.println(buf.toString());
	}
	
	private static void end(PrintStream out) {
		StringBuffer buf = new StringBuffer();
		buf.append(AonStringUtils.SPACE);
		buf.append(AonStringUtils.SPACE);
		buf.append(AonStringUtils.spaces(9));
		buf.append(AonStringUtils.SPACE);
		buf.append(AonStringUtils.spaces(55));
		buf.append(AonStringUtils.SPACE);
		buf.append(AonStringUtils.spaces(45));
		buf.append(LOWER_LEFT_CORNER);
		buf.append(AonStringUtils.repeat(HORIZONTAL_BAR,17));
		buf.append(HORIZONTAL_UP_BAR);
		buf.append(AonStringUtils.repeat(HORIZONTAL_BAR,17));
		buf.append(LOWER_RIGHT_CORNER);
		buf.append(AonStringUtils.spaces(9));
		buf.append(AonStringUtils.SPACE);
		buf.append(AonStringUtils.spaces(33));
		buf.append(AonStringUtils.SPACE);
		out.println(buf.toString());
	}

	private static void startInner(PrintStream out) {
		StringBuffer buf = new StringBuffer();
		buf.append(AonStringUtils.SPACE);
		buf.append(VERTICAL_RIGHT_BAR);
		buf.append(AonStringUtils.repeat(HORIZONTAL_BAR,9));
		buf.append(HORIZONTAL_DOWN_BAR);
		buf.append(AonStringUtils.repeat(HORIZONTAL_BAR,55));
		buf.append(HORIZONTAL_DOWN_BAR);
		buf.append(AonStringUtils.repeat(HORIZONTAL_BAR,45));
		buf.append(HORIZONTAL_DOWN_BAR);
		buf.append(AonStringUtils.repeat(HORIZONTAL_BAR,17));
		buf.append(HORIZONTAL_DOWN_BAR);
		buf.append(AonStringUtils.repeat(HORIZONTAL_BAR,17));
		buf.append(HORIZONTAL_DOWN_BAR);
		buf.append(AonStringUtils.repeat(HORIZONTAL_BAR,9));
		buf.append(HORIZONTAL_DOWN_BAR);
		buf.append(AonStringUtils.repeat(HORIZONTAL_BAR,33));
		buf.append(VERTICAL_LEFT_BAR);
		out.println(buf.toString());
	}
	
	private static void endInner(PrintStream out) {
		StringBuffer buf = new StringBuffer();
		buf.append(AonStringUtils.SPACE);
		buf.append(LOWER_LEFT_CORNER);
		buf.append(AonStringUtils.repeat(HORIZONTAL_BAR,9));
		buf.append(HORIZONTAL_UP_BAR);
		buf.append(AonStringUtils.repeat(HORIZONTAL_BAR,55));
		buf.append(HORIZONTAL_UP_BAR);
		buf.append(AonStringUtils.repeat(HORIZONTAL_BAR,45));
		buf.append(CROSS);
		buf.append(AonStringUtils.repeat(HORIZONTAL_BAR,17));
		buf.append(CROSS);
		buf.append(AonStringUtils.repeat(HORIZONTAL_BAR,17));
		buf.append(CROSS);
		buf.append(AonStringUtils.repeat(HORIZONTAL_BAR,9));
		buf.append(HORIZONTAL_UP_BAR);
		buf.append(AonStringUtils.repeat(HORIZONTAL_BAR,33));
		buf.append(LOWER_RIGHT_CORNER);
		out.println(buf.toString());
	}

	private static void toString(PrintStream out, AccountEntry entry) {
		StringBuffer buf = new StringBuffer();
		buf.append(AonStringUtils.SPACE);
		buf.append(VERTICAL_BAR);
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
		buf.append(AonStringUtils.SPACE);
		if (AonStringUtils.isNotBlank(entry.getComments())) {
			buf.append("[");
			buf.append(AonStringUtils.abbreviate(AonStringUtils.removeTabsAndNewLine(entry.getComments()), 38));
			buf.append("]");
		} else {
			buf.append(AonStringUtils.repeat(AonStringUtils.SPACE, 40));
		}
		buf.append(AonStringUtils.leftPad(entry.getEntryType().getDescription(), LINE_SIZE - 1 - buf.length()));
		buf.append(VERTICAL_BAR);
		out.println(buf.toString());
	}
	
	private static void toString(PrintStream out, AccountEntryDetail detail) {
		StringBuffer buf = new StringBuffer();
		buf.append(AonStringUtils.SPACE);
		buf.append(VERTICAL_BAR);
		buf.append(AonStringUtils.rightPad(AonStringUtils.defaultString(detail.getAccountCode()),9));
		buf.append(VERTICAL_BAR);
		buf.append(AonStringUtils.rightPad(AonStringUtils.abbreviate(AonStringUtils.defaultString(detail.getAccountDescription()), 54), 55));
		buf.append(VERTICAL_BAR);
		buf.append(AonStringUtils.rightPad(AonStringUtils.abbreviate( AonStringUtils.defaultString(detail.getConcept()), 45), 45));
		buf.append(VERTICAL_BAR);
		buf.append(AonStringUtils.leftPad(FMT.format(detail.getDebit()),17));
		buf.append(VERTICAL_BAR);
		buf.append(AonStringUtils.leftPad(FMT.format(detail.getCredit()),17));
		buf.append(VERTICAL_BAR);
		buf.append(AonStringUtils.center(AonStringUtils.defaultString(detail.getBalancingAccountCode()),9));
		buf.append(VERTICAL_BAR);
		buf.append(AonStringUtils.rightPad(AonStringUtils.defaultString(detail.getDocumentNumber()), 33));
		buf.append(VERTICAL_BAR);
		out.println(buf.toString());
	}
	
	private static void total(PrintStream out, AccountEntry entry) {
		StringBuffer buf = new StringBuffer();
		buf.append(AonStringUtils.SPACE);
		buf.append(AonStringUtils.SPACE);
		buf.append(AonStringUtils.spaces(9));
		buf.append(AonStringUtils.SPACE);
		buf.append(AonStringUtils.spaces(55));
		buf.append(AonStringUtils.SPACE);
		buf.append( entry.isSettled() 
			? AonStringUtils.spaces(45)
			: AonStringUtils.leftPad(" ERROR!  (No saldado) ---> ",45));
		buf.append(VERTICAL_BAR);
		buf.append(AonStringUtils.leftPad(FMT.format(entry.getDebitSum()),17));
		buf.append(VERTICAL_BAR);
		buf.append(AonStringUtils.leftPad(FMT.format(entry.getCreditSum()),17));
		buf.append(VERTICAL_BAR);
		buf.append( AonStringUtils.spaces(9) );
		buf.append(AonStringUtils.SPACE);
		buf.append(AonStringUtils.spaces(33));
		buf.append(AonStringUtils.SPACE);
		out.println(buf.toString());
	}

}
