package com.esferalia.aon.gwt.fiscal.server;

import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.model.fiscal.OperationBreakdown;
import com.esferalia.aon.watson.util.AonStringUtils;

public class OperationFormatter {

	public static final SimpleDateFormat FMT = new SimpleDateFormat("dd/MM/yyyy");
	public static final DecimalFormat INT = new DecimalFormat("#,##0");
	public static final DecimalFormat DEC = new DecimalFormat("#,##0.00");
	public static final DecimalFormat DEC2 = new DecimalFormat("#,###.##");

	static final String MAIN_DIV_MSG = "<div style=\"margin-bottom: 5px; font-size: 0.9em;text-align: center;\">{0}</div>";
	static final String DIV_MSG = "<div>{0}</div>";
	static final String LI_MSG = "<li>{0}</li>";
	static final String DIV_MSG_BLUE= "<div style=\"color: blue;\">{0}</div>";
	static final String DIV_MSG_BLUE_BORDER_BOTTOM = "<div style=\"color: blue; border-bottom:solid blue 1px;\">{0}</div>";
	static final String DIV_MSG_BOLD= "<div><b>{0}</b></div>";
	static final String DIV_MSG_BOLD_BORDER_BOTTOM = "<div style=\"border-bottom:solid black 1px;\"><b>{0}</b></div>";
	static final String DIV_MSG_BOLD_BLUE= "<div style=\"color: blue;\"><b>{0}</b></div>";

	public static void formatOperation(final PrintWriter out, Stream<OperationBreakdown> stream) {
		out.print('[');		
		stream.forEach( op -> writeToJSONOperation(out, op) );
		out.print(']');
		out.flush();
	}	
	
	private static void writeToJSONOperation(final PrintWriter out, OperationBreakdown op) {
		out.print('{');
		if (AonStringUtils.isNotBlank( op.getAccount())) out.printf("\"account\":\"%s\"", op.getAccount());
		if (AonStringUtils.isNotBlank( op.getAccountDescription())) out.printf(",\"accountDescription\":\"%s\"", op.getAccountDescription());
		if (AonStringUtils.isNotBlank( op.getConcept())) out.printf(",\"concept\":\"%s\"", op.getFullConcept());
		out.printf(",\"documentNumber\":\"%s\"", AonStringUtils.trimToEmpty(op.getDocNumber()) );
		if (AonStringUtils.isNotBlank( op.getRegistryDocument())) out.printf(",\"registryDocument\":\"%s\"", op.getRegistryDocument());
		if (AonStringUtils.isNotBlank( op.getRegistryName())) out.printf(",\"registryName\":\"%s\"", op.getRegistryName());
		out.printf(",\"entryDate\":\"%1$tY-%1$tm-%1$td\"", op.getEntryDate());
		out.printf(",\"taxDate\":\"%1$tY-%1$tm-%1$td\"", op.getTaxDate());
		if (op.getBase() != null) out.printf(",\"base\":%s", Double.toString( op.getBase()));
		out.printf(",\"percent\":%s", Double.toString( op.getPercent()));
		out.printf(",\"quota\":%s", Double.toString( op.getQuota()));
		out.printf(",\"surchargePercent\":%s", Double.toString( op.getSurchargePercent()));
		out.printf(",\"surchargeQuota\":%s", Double.toString( op.getSurchargeQuota()));
		out.printf(",\"total\":%s", Double.toString( op.getTotal()));
		out.print('}');
		out.print(',');
		out.flush();
	}
	

}
