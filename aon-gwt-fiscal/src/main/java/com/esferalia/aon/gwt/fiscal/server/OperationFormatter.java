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

	public static void formatIva(final PrintWriter out, Stream<OperationBreakdown> stream, String title, String subtitle) {
		out.print('[');
		stream.forEach( vat -> writeToJSONIva(out,vat) );
		out.print(']');
		out.flush();
	}
	
	public static void formatIrpf(final PrintWriter out, Stream<OperationBreakdown> stream, String title, String subtitle) {
		out.print('[');
		stream.forEach( vat -> writeToJSONIrpf(out,vat) );
		out.print(']');
		out.flush();
	}

	private static void writeToJSONIva(final PrintWriter out, OperationBreakdown irpf) {
		out.print('{');
		if (AonStringUtils.isNotBlank( irpf.getAccountDescription())) out.printf("\"accountDescription\":\"%s\"", irpf.getAccountDescription());
		if (AonStringUtils.isNotBlank( irpf.getConcept())) out.printf(",\"concept\":\"%s\"", irpf.getFullConcept());
		out.printf(",\"documentNumber\":\"%s\"", irpf.getDocNumber());
		if (AonStringUtils.isNotBlank( irpf.getRegistryDocument())) out.printf(",\"registryDocument\":\"%s\"", irpf.getRegistryDocument());
		if (AonStringUtils.isNotBlank( irpf.getRegistryName())) out.printf(",\"registryName\":\"%s\"", irpf.getRegistryName());
		out.printf(",\"entryDate\":\"%1$tY-%1$tm-%1$td\"", irpf.getEntryDate());
		out.printf(",\"taxDate\":\"%1$tY-%1$tm-%1$td\"", irpf.getTaxDate());
		if (irpf.getBase() != null) out.printf(",\"base\":%s", Double.toString( irpf.getBase()));
		out.printf(",\"percent\":%s", Double.toString( irpf.getPercent()));
		out.printf(",\"quota\":%s", Double.toString( irpf.getQuota()));
		out.printf(",\"surchargePercent\":%s", Double.toString( irpf.getSurchargePercent()));
		out.printf(",\"surchargeQuota\":%s", Double.toString( irpf.getSurchargeQuota()));
		out.printf(",\"total\":%s", Double.toString( irpf.getTotal()));
		out.printf(",\"expenses\":%b", irpf.getExpenses());
		out.printf(",\"irpf\":%b", irpf.getIrpf());
		if (irpf.getActivity() != null) out.printf(",\"activity\":\"%d\"", irpf.getActivity());
		out.printf(",\"activityDescription\":\"%s\"", irpf.getActivityDescription());
		out.print('}');
		out.print(',');
		out.flush();
	}

	private static void writeToJSONIrpf(final PrintWriter out, OperationBreakdown irpf) {
		out.print('{');
		if (AonStringUtils.isNotBlank( irpf.getAccount())) out.printf("\"account\":\"%s\"", irpf.getAccount());
		if (AonStringUtils.isNotBlank( irpf.getAccountDescription())) out.printf(",\"accountDescription\":\"%s\"", irpf.getAccountDescription());
		if (AonStringUtils.isNotBlank( irpf.getConcept())) out.printf(",\"concept\":\"%s\"", irpf.getFullConcept());
		out.printf(",\"documentNumber\":\"%s\"", irpf.getDocNumber());
		if (AonStringUtils.isNotBlank( irpf.getRegistryDocument())) out.printf(",\"registryDocument\":\"%s\"", irpf.getRegistryDocument());
		if (AonStringUtils.isNotBlank( irpf.getRegistryName())) out.printf(",\"registryName\":\"%s\"", irpf.getRegistryName());
		out.printf(",\"entryDate\":\"%1$tY-%1$tm-%1$td\"", irpf.getEntryDate());
		out.printf(",\"taxDate\":\"%1$tY-%1$tm-%1$td\"", irpf.getTaxDate());
		if (irpf.getBase() != null) out.printf(",\"base\":%s", Double.toString( irpf.getBase()));
		out.printf(",\"percent\":%s", Double.toString( irpf.getPercent()));
		out.printf(",\"quota\":%s", Double.toString( irpf.getQuota()));
		out.printf(",\"surchargePercent\":%s", Double.toString( irpf.getSurchargePercent()));
		out.printf(",\"surchargeQuota\":%s", Double.toString( irpf.getSurchargeQuota()));
		out.printf(",\"total\":%s", Double.toString( irpf.getTotal()));
		out.printf(",\"expenses\":%s", irpf.getExpenses());
		out.printf(",\"irpf\":%b", irpf.getIrpf());
		if (irpf.getActivity() != null) out.printf(",\"activity\":\"%d\"", irpf.getActivity());
		out.printf(",\"activityDescription\":\"%s\"", irpf.getActivityDescription());
		out.print('}');
		out.print(',');
		out.flush();
	}

}
