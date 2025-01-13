package net.aonsolutions.occam.impl.handler;

import java.io.PrintStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import com.esferalia.aon.watson.util.AonStringUtils;

import net.aonsolutions.occam.api.model.AccountEntry;
import net.aonsolutions.occam.api.model.AccountEntryDetail;
import net.aonsolutions.occam.api.model.util.InvoiceTextPrinter.ConsoleColors;

public class AccountEntryPrinter {
	
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
	private static final DecimalFormat FMT = new  DecimalFormat("#,##0.00");
	
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

	private AccountEntryPrinter() {
	}
	private int getLineSize() {
		return 150;
	}

	public static void print(AccountEntry entry) {
		print(System.out, entry);
	}

	public static void print( PrintStream out, AccountEntry entry) {
		new AccountEntryPrinter()
			.header(entry, out)
		;
		out.flush();
	}
	
	private AccountEntryPrinter header(AccountEntry entry, PrintStream out) {
		StringBuilder buf = new StringBuilder();
		buf.append(AonStringUtils.SPACE);
		buf.append(TOP_LEFT_CORNER);
		buf.append(AonStringUtils.repeat(HORIZONTAL_BAR, getLineSize() - buf.length()));
		buf.append(TOP_RIGHT_CORNER);
		out.println(buf.toString());
		
		buf = new StringBuilder();
		buf.append(AonStringUtils.SPACE);
		buf.append(VERTICAL_BAR);
		buf.append(entry.isConfidential()
			?AonStringUtils.OPEN_BRACKET + "Confid." + AonStringUtils.CLOSE_BRACKET + AonStringUtils.SPACE
			:AonStringUtils.repeat(AonStringUtils.SPACE, 14));
		buf.append(AonStringUtils.SPACE);
		buf.append((entry.getActivity().isEmpty()) 
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
		buf.append(AonStringUtils.leftPad(entry.getEntryType().getDescription(), getLineSize() - buf.length()));
		buf.append(VERTICAL_BAR);
		out.println(buf.toString());
		
		
		buf = new StringBuilder();
		buf.append(AonStringUtils.SPACE);
		buf.append(VERTICAL_RIGHT_BAR);
		buf.append(AonStringUtils.repeat(HORIZONTAL_BAR, 10));
		buf.append(HORIZONTAL_DOWN_BAR);
		buf.append(AonStringUtils.repeat(HORIZONTAL_BAR, 32));
		buf.append(HORIZONTAL_DOWN_BAR);
		buf.append(AonStringUtils.repeat(HORIZONTAL_BAR, 35));
		buf.append(HORIZONTAL_DOWN_BAR);
		buf.append(AonStringUtils.repeat(HORIZONTAL_BAR, 17));
		buf.append(HORIZONTAL_DOWN_BAR);
		buf.append(AonStringUtils.repeat(HORIZONTAL_BAR, 17));
		buf.append(HORIZONTAL_DOWN_BAR);
		buf.append(AonStringUtils.repeat(HORIZONTAL_BAR, 11));
		buf.append(HORIZONTAL_DOWN_BAR);
		buf.append(AonStringUtils.repeat(HORIZONTAL_BAR, 20));
		buf.append(VERTICAL_LEFT_BAR);
		out.println(buf.toString());
		
		buf = new StringBuilder();
		buf.append(AonStringUtils.SPACE);
		buf.append(VERTICAL_BAR);
		buf.append(AonStringUtils.center("Cuenta", 10));
		buf.append(VERTICAL_BAR);
		buf.append(AonStringUtils.center("Descripción cuenta", 32));
		buf.append(VERTICAL_BAR);
		buf.append(AonStringUtils.center("Concepto", 35));
		buf.append(VERTICAL_BAR);
		buf.append(AonStringUtils.center("Debe", 17));
		buf.append(VERTICAL_BAR);
		buf.append(AonStringUtils.center("Haber", 17));
		buf.append(VERTICAL_BAR);
		buf.append(AonStringUtils.center("Contrapart.", 11));
		buf.append(VERTICAL_BAR);
		buf.append(AonStringUtils.center("Num. Documento ", 20));
		buf.append(VERTICAL_BAR);
		out.println(buf.toString());
		
		buf = new StringBuilder();
		buf.append(AonStringUtils.SPACE);
		buf.append(VERTICAL_RIGHT_BAR);
		buf.append(AonStringUtils.repeat(HORIZONTAL_BAR, 10));
		buf.append(CROSS);
		buf.append(AonStringUtils.repeat(HORIZONTAL_BAR, 32));
		buf.append(CROSS);
		buf.append(AonStringUtils.repeat(HORIZONTAL_BAR, 35));
		buf.append(CROSS);
		buf.append(AonStringUtils.repeat(HORIZONTAL_BAR, 17));
		buf.append(CROSS);
		buf.append(AonStringUtils.repeat(HORIZONTAL_BAR, 17));
		buf.append(CROSS);
		buf.append(AonStringUtils.repeat(HORIZONTAL_BAR, 11));
		buf.append(CROSS);
		buf.append(AonStringUtils.repeat(HORIZONTAL_BAR, 20));
		buf.append(VERTICAL_LEFT_BAR);
		out.println(buf.toString());
		
		entry.detailStream().forEach(det -> print(out,det));
		
		buf = new StringBuilder();
		buf.append(AonStringUtils.SPACE);
		buf.append(VERTICAL_RIGHT_BAR);
		buf.append(AonStringUtils.repeat(HORIZONTAL_BAR, 10));
		buf.append(HORIZONTAL_UP_BAR);
		buf.append(AonStringUtils.repeat(HORIZONTAL_BAR, 32));
		buf.append(HORIZONTAL_UP_BAR);
		buf.append(AonStringUtils.repeat(HORIZONTAL_BAR, 35));
		buf.append(CROSS);
		buf.append(AonStringUtils.repeat(HORIZONTAL_BAR, 17));
		buf.append(CROSS);
		buf.append(AonStringUtils.repeat(HORIZONTAL_BAR, 17));
		buf.append(CROSS);
		buf.append(AonStringUtils.repeat(HORIZONTAL_BAR, 11));
		buf.append(HORIZONTAL_UP_BAR);
		buf.append(AonStringUtils.repeat(HORIZONTAL_BAR, 20));
		buf.append(VERTICAL_LEFT_BAR);
		out.println(buf.toString());
		
		buf = new StringBuilder();
		buf.append(AonStringUtils.SPACE);
		buf.append(VERTICAL_BAR);
		buf.append(AonStringUtils.spaces(10));
		buf.append(AonStringUtils.SPACE);
		buf.append(AonStringUtils.spaces(32));
		buf.append(AonStringUtils.SPACE);
		buf.append(AonStringUtils.spaces(35));
		buf.append(VERTICAL_BAR);
		if (entry.isSettled()) {
			buf.append(ConsoleColors.whiteBold(AonStringUtils.leftPad(FMT.format(entry.getDebitSum()),17)));		
			buf.append(VERTICAL_BAR);
			buf.append(ConsoleColors.whiteBold(AonStringUtils.leftPad(FMT.format(entry.getCreditSum()),17)));
		} else {
			buf.append(ConsoleColors.redBold(AonStringUtils.leftPad(FMT.format(entry.getDebitSum()),17)));		
			buf.append(VERTICAL_BAR);
			buf.append(ConsoleColors.redBold(AonStringUtils.leftPad(FMT.format(entry.getCreditSum()),17)));
		}
		buf.append(VERTICAL_BAR);
		buf.append(AonStringUtils.spaces(11));
		buf.append(AonStringUtils.SPACE);
		buf.append(AonStringUtils.spaces(20));
		buf.append(VERTICAL_BAR);
		out.println(buf.toString());
		
		buf = new StringBuilder();
		buf.append(AonStringUtils.SPACE);
		buf.append(LOWER_LEFT_CORNER);
		buf.append(AonStringUtils.repeat(HORIZONTAL_BAR, 10));
		buf.append(HORIZONTAL_BAR);
		buf.append(AonStringUtils.repeat(HORIZONTAL_BAR, 32));
		buf.append(HORIZONTAL_BAR);
		buf.append(AonStringUtils.repeat(HORIZONTAL_BAR, 35));
		buf.append(HORIZONTAL_UP_BAR);
		buf.append(AonStringUtils.repeat(HORIZONTAL_BAR, 17));
		buf.append(HORIZONTAL_UP_BAR);
		buf.append(AonStringUtils.repeat(HORIZONTAL_BAR, 17));
		buf.append(HORIZONTAL_UP_BAR);
		buf.append(AonStringUtils.repeat(HORIZONTAL_BAR, 11));
		buf.append(HORIZONTAL_BAR);
		buf.append(AonStringUtils.repeat(HORIZONTAL_BAR, 20));
		buf.append(LOWER_RIGHT_CORNER);
		out.println(buf.toString());

		return this;
	}
	private AccountEntryPrinter print(PrintStream out, AccountEntryDetail det) {
		StringBuilder buf = new StringBuilder();
		buf.append(AonStringUtils.SPACE);
		buf.append(VERTICAL_BAR);
		buf.append(AonStringUtils.rightPad(AonStringUtils.defaultString(det.getAccountCode().orElse(AonStringUtils.EMPTY)),10));
		buf.append(VERTICAL_BAR);
		buf.append(AonStringUtils.rightPad(AonStringUtils.abbreviate(det.getAccountDescription().orElse(AonStringUtils.EMPTY), 32),32));
		buf.append(VERTICAL_BAR);
		buf.append(AonStringUtils.rightPad(AonStringUtils.abbreviate( AonStringUtils.defaultString(det.getConcept()), 35), 35));
		buf.append(VERTICAL_BAR);
		buf.append(AonStringUtils.leftPad(FMT.format(det.getDebit()),17));
		buf.append(VERTICAL_BAR);
		buf.append(AonStringUtils.leftPad(FMT.format(det.getCredit()),17));
		buf.append(VERTICAL_BAR);
		buf.append(AonStringUtils.center(AonStringUtils.defaultString(det.getBalancingAccountCode().orElse(AonStringUtils.EMPTY)),11));
		buf.append(VERTICAL_BAR);
		buf.append(AonStringUtils.rightPad(AonStringUtils.defaultString(det.getDocumentNumber()), 20));
		buf.append(VERTICAL_BAR);
		out.println(buf.toString());
		return this;
	}
	
}
