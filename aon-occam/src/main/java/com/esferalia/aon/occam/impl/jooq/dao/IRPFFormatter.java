package com.esferalia.aon.occam.impl.jooq.dao;

import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.TreeMap;
import java.util.stream.Stream;

import org.jooq.tools.json.JSONObject;

import com.esferalia.aon.occam.api.model.fiscal.AccountingBreakdown;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModel;
import com.esferalia.aon.occam.api.model.fiscal.IFiscalModelKey;
import com.esferalia.aon.occam.api.model.fiscal.IrpfBreakdown;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class IRPFFormatter {

	public static final SimpleDateFormat FMT = new SimpleDateFormat("dd/MM/yyyy");
	public static final DecimalFormat INT = new DecimalFormat("#,##0");
	public static final DecimalFormat DEC = new DecimalFormat("#,##0.00");
	public static final DecimalFormat DEC2 = new DecimalFormat("#,###.##");
	
	private static final String NO_DATA = "<div>NO SE ENCONTRARON DATOS</div>";
	static final String MAIN_DIV_MSG = "<div style=\"margin-bottom: 5px; font-size: 0.9em;text-align: center;\">{0}</div>";
	static final String DIV_MSG = "<div>{0}</div>";
	static final String LI_MSG = "<li>{0}</li>";
	static final String DIV_MSG_BLUE= "<div style=\"color: blue;\">{0}</div>";
	static final String DIV_MSG_BLUE_BORDER_BOTTOM = "<div style=\"color: blue; border-bottom:solid blue 1px;\">{0}</div>";
	static final String DIV_MSG_BOLD= "<div><b>{0}</b></div>";
	static final String DIV_MSG_BOLD_BORDER_BOTTOM = "<div style=\"border-bottom:solid black 1px;\"><b>{0}</b></div>";
	static final String DIV_MSG_BOLD_BLUE= "<div style=\"color: blue;\"><b>{0}</b></div>";
	private static final String SPAN_MSG_ORANGE= "<span style=\"color: red;\">{0}</span>";
	
	public static String formatSalaries(String title, String subtitle, LinkedList<IrpfBreakdown> list) {
		StringBuilder buf = new StringBuilder();
		String header = AonStringUtils.repeat(" ", 2)
				+ AonStringUtils.rightPad("DOCUMENTO",12)
				+ AonStringUtils.rightPad("NOMBRE EMPLEADO",50)
				+ AonStringUtils.rightPad("FECHA",12)					
				+ AonStringUtils.leftPad("PERCEPTOR",10)		
				+ AonStringUtils.leftPad("PERCEPCIONES",15)		
				+ AonStringUtils.leftPad("RET./ING.CTA.",15);
		buf.append(MessageFormat.format(DIV_MSG,AonStringUtils.repeat(" ", header.length())));
		buf.append(MessageFormat.format(DIV_MSG_BOLD,AonStringUtils.center(title, header.length())));
		buf.append(MessageFormat.format(DIV_MSG_BOLD,AonStringUtils.center("(" + subtitle + ")", header.length())));
		buf.append(MessageFormat.format(DIV_MSG,AonStringUtils.repeat(" ", header.length())));
		buf.append(MessageFormat.format(DIV_MSG_BOLD,AonStringUtils.repeat("-", header.length()))); 
		buf.append(MessageFormat.format(DIV_MSG_BOLD,header));
		buf.append(MessageFormat.format(DIV_MSG_BOLD,AonStringUtils.repeat("-", header.length())));
		
		if (list == null || list.size() == 0) {
			buf.append(MessageFormat.format(DIV_MSG,AonStringUtils.repeat(" ", header.length())));
			buf.append(NO_DATA);			
			buf.append(MessageFormat.format(DIV_MSG,AonStringUtils.repeat(" ", header.length())));
		}
		
		double sumBase = 0;
		double sumQuota = 0;
		HashSet<String> docs = new HashSet<String>();
		for (IrpfBreakdown br : list) {
			String cont = " ";
			cont=docs.add(br.getRegistryDocument())?"1":" ";
			sumBase += br.getBase();
			sumQuota += br.getQuota();
			buf.append(MessageFormat.format(DIV_MSG
					 ,AonStringUtils.repeat(" ", 2)
					+ AonStringUtils.rightPad(br.getRegistryDocument(),12)
					+ AonStringUtils.rightPad(br.getName(),50)
					+ AonStringUtils.rightPad(FMT.format(br.getIssueDate()),12)
					+ AonStringUtils.leftPad( cont ,10)
					+ AonStringUtils.leftPad(DEC.format(br.getBase()),15)		
					+ AonStringUtils.leftPad(DEC.format(br.getQuota()),15)
					));
		}
		buf.append(MessageFormat.format(DIV_MSG_BOLD,AonStringUtils.repeat("-", header.length())));
		buf.append(MessageFormat.format(DIV_MSG_BOLD,AonStringUtils.repeat(" ", 2)
				+ AonStringUtils.rightPad(" ",12)
				+ AonStringUtils.rightPad(" ",50)
				+ AonStringUtils.leftPad("TOTAL",12)
				+ AonStringUtils.leftPad( AonNumberUtils.toString(docs.size()) ,10) 						
				+ AonStringUtils.leftPad(DEC.format(sumBase),15)		
				+ AonStringUtils.leftPad(DEC.format(sumQuota),15)
				));
			
		return MessageFormat.format(MAIN_DIV_MSG,buf.toString());

	}
	
	public static String formatInvoices(String title, String subtitle, LinkedList<IrpfBreakdown> list) {
		StringBuilder buf = new StringBuilder();
		String header = AonStringUtils.repeat(" ", 2)
				+ AonStringUtils.rightPad("FACTURA",14)
				+ AonStringUtils.rightPad("TITULAR FACTURA",40)
				+ AonStringUtils.rightPad("FECHA FAC.",12)					
				+ AonStringUtils.rightPad("FECHA IMP.",12)
				+ AonStringUtils.SPACE
				+ AonStringUtils.rightPad("TIPO RET.",15)		
				+ AonStringUtils.leftPad("BASE IMP.",15)		
				+ AonStringUtils.leftPad("CUOTA",15)
				+ AonStringUtils.repeat(" ", 2)
				;
		buf.append(MessageFormat.format(DIV_MSG,AonStringUtils.repeat(" ", header.length())));
		buf.append(MessageFormat.format(DIV_MSG_BOLD,AonStringUtils.center(title, header.length())));
		buf.append(MessageFormat.format(DIV_MSG_BOLD,AonStringUtils.center("(" + subtitle + ")", header.length())));
		buf.append(MessageFormat.format(DIV_MSG,AonStringUtils.repeat(" ", header.length())));
		buf.append(MessageFormat.format(DIV_MSG_BOLD,AonStringUtils.repeat("-", header.length()))); 
		buf.append(MessageFormat.format(DIV_MSG_BOLD,header));
		buf.append(MessageFormat.format(DIV_MSG_BOLD,AonStringUtils.repeat("-", header.length())));
		
		if (list == null || list.size() == 0) {
			buf.append(MessageFormat.format(DIV_MSG,AonStringUtils.repeat(" ", header.length())));
			buf.append(NO_DATA);			
			buf.append(MessageFormat.format(DIV_MSG,AonStringUtils.repeat(" ", header.length())));
		}
		
		double sumBase = 0;
		double sumQuota = 0;
		HashSet<String> docs = new HashSet<String>();
		for (IrpfBreakdown br : list) {
			docs.add(br.getRegistryDocument());
			buf.append(MessageFormat.format(DIV_MSG
					 ,AonStringUtils.repeat(" ", 2)
					+ AonStringUtils.rightPad(br.getDocumentNumber(),14)
					+ AonStringUtils.rightPad(br.getRegistryDocument(),10)
					+ AonStringUtils.rightPad(AonStringUtils.abbreviate(br.getName(),29),30)
					+ (AonDateUtils.isSameDay(br.getIssueDate(), br.getTaxDate())
							?AonStringUtils.rightPad(FMT.format(br.getIssueDate()),12)
							:(MessageFormat.format(SPAN_MSG_ORANGE,FMT.format(br.getIssueDate()),12) + AonStringUtils.repeat(" ", 2)) 
						  )
					+ AonStringUtils.rightPad(FMT.format(br.getTaxDate()),12)					
					+ AonStringUtils.SPACE
					+ AonStringUtils.rightPad(AonStringUtils.abbreviate(br.getWithholdingType().getDescription(),14),15)		
					+ AonStringUtils.leftPad(DEC.format(br.getBase()),15)		
					+ AonStringUtils.leftPad(DEC.format(br.getQuota()),15)
					+ AonStringUtils.repeat(" ", 2)
					));
			sumBase += br.getBase(); 
			sumQuota += br.getQuota();
		}
		buf.append(MessageFormat.format(DIV_MSG_BOLD,AonStringUtils.repeat("-", header.length())));
		buf.append(MessageFormat.format(DIV_MSG_BOLD
				 ,AonStringUtils.repeat(" ", 2)
				+ AonStringUtils.rightPad("TOTAL",14)
				+ AonStringUtils.center(AonNumberUtils.toString(docs.size()) + " perceptores",40)
				+ AonStringUtils.leftPad(" ",40)
				+ AonStringUtils.leftPad(DEC.format(sumBase),15)		
				+ AonStringUtils.leftPad(DEC.format(sumQuota),15)
				+ AonStringUtils.repeat(" ", 2)
				));
		docs =  null;
		buf.append(MessageFormat.format(DIV_MSG_BOLD,AonStringUtils.repeat("-", header.length())));
		return MessageFormat.format(MAIN_DIV_MSG,buf.toString());

	}

	public static String formatDiffInvoices(String title
			,String subtitle
			,IFiscalModelKey[] keys
			, LinkedList<FiscalModel> models
			, LinkedList<IrpfBreakdown> list) {
		StringBuilder buf = new StringBuilder();
		int headerLength = 100; 
		buf.append(MessageFormat.format(DIV_MSG,AonStringUtils.repeat(" ", headerLength)));
		buf.append(MessageFormat.format(DIV_MSG_BOLD,AonStringUtils.center(title, headerLength)));
		buf.append(MessageFormat.format(DIV_MSG_BOLD,AonStringUtils.center("(" + subtitle + ")", headerLength)));		
		buf.append(MessageFormat.format(DIV_MSG_BOLD,AonStringUtils.repeat("-", headerLength)));
		buf.append(MessageFormat.format(DIV_MSG,AonStringUtils.repeat(" ", headerLength)));
		
		
		HashSet<String> beforePeriodDocs = new HashSet<String>();
		HashSet<String> periodDocs = new HashSet<String>();
		HashSet<String> acumDocs = new HashSet<String>();
		
		double sumBase = 0;
		double sumQuota = 0;
		double sumBeforePeriodBase = 0;
		double sumBeforePeriodQuota = 0;
		double sumPeriodBase = 0;
		double sumPeriodQuota = 0;

		for (IrpfBreakdown br : list) {
			acumDocs.add(br.getRegistryDocument());
			if (br.isInsidePeriod()) {
				periodDocs.add(br.getRegistryDocument());
			} else {
				beforePeriodDocs.add(br.getRegistryDocument());
			}
			sumBase += br.getBase(); 
			sumQuota += br.getQuota();
			sumBeforePeriodBase += br.isInsidePeriod()?0.0:br.getBase(); 
			sumBeforePeriodQuota += br.isInsidePeriod()?0.0:br.getQuota();
			sumPeriodBase += br.isInsidePeriod()?br.getBase():0.0; 
			sumPeriodQuota += br.isInsidePeriod()?br.getQuota():0.0;
		}
		buf.append(MessageFormat.format(DIV_MSG,AonStringUtils.repeat(" ", 2)
				+ AonStringUtils.leftPad("ACUMULADO HASTA INICIO DEL PER\u00CDODO :",40)
				+ AonStringUtils.SPACE
				+ AonStringUtils.leftPad(AonNumberUtils.toString( beforePeriodDocs.size()),15)
				+ AonStringUtils.leftPad(DEC.format(sumBeforePeriodBase),15)		
				+ AonStringUtils.rightPad(" ",8)
				+ AonStringUtils.leftPad(DEC.format(sumBeforePeriodQuota),15)
				+ AonStringUtils.repeat(" ", 2)
				));
		buf.append(MessageFormat.format(DIV_MSG,AonStringUtils.repeat(" ", 2)
				+ AonStringUtils.leftPad("ACUMULADO PER\u00CDODO :",40)
				+ AonStringUtils.SPACE
				+ AonStringUtils.leftPad(AonNumberUtils.toString( periodDocs.size()),15)
				+ AonStringUtils.leftPad(DEC.format(sumPeriodBase),15)		
				+ AonStringUtils.rightPad(" ",8)
				+ AonStringUtils.leftPad(DEC.format(sumPeriodQuota),15)
				+ AonStringUtils.repeat(" ", 2)
				));

		buf.append(MessageFormat.format(DIV_MSG_BOLD_BLUE,AonStringUtils.repeat(" ", 2)
				+ AonStringUtils.leftPad("ACUMULADO DESDE 1 DE ENERO (A):",40)
				+ AonStringUtils.SPACE
				+ AonStringUtils.leftPad(AonNumberUtils.toString( acumDocs.size()),15)
				+ AonStringUtils.leftPad(DEC.format(sumBase),15)		
				+ AonStringUtils.rightPad(" ",8)
				+ AonStringUtils.leftPad(DEC.format(sumQuota),15)
				+ AonStringUtils.repeat(" ", 2)
				));
		buf.append(MessageFormat.format(DIV_MSG,AonStringUtils.repeat(" ", headerLength)));
		buf.append(MessageFormat.format(DIV_MSG,"Declaraciones anteriores "));
		buf.append(MessageFormat.format(DIV_MSG,AonStringUtils.repeat("-", headerLength)));
		double sumDeclaredBase = 0;
		double sumDeclaredQuota = 0;
		IFiscalModelKey baseKey = null;
		IFiscalModelKey quotaKey = null;
		if (keys.length > 1) {
			baseKey = keys[1];
			quotaKey = keys[2];
		} else {
			quotaKey = keys[0];
		}
		for (FiscalModel fm : models) {
			buf.append(MessageFormat.format(DIV_MSG,AonStringUtils.repeat(" ", 2)
					+ AonStringUtils.leftPad( fm.getPeriod().getDescription(),40)
					+ AonStringUtils.rightPad(fm.isComplementary()?" [Comp.]":fm.isReplacement()?" [Sust.]":" ",16)
					+ AonStringUtils.leftPad(DEC.format(baseKey!=null?fm.getAmount(baseKey):0.0),15)		
					+ AonStringUtils.rightPad(" ",8)
					+ AonStringUtils.leftPad(DEC.format(fm.getAmount(quotaKey)),15)
					+ AonStringUtils.repeat(" ", 2)
					));
			sumDeclaredBase += baseKey!=null?fm.getAmount(baseKey):0.0;
			sumDeclaredQuota += fm.getAmount(quotaKey);
		}

		double sumToDeclareBase = sumBase - sumDeclaredBase;
		double sumToDeclareQuota = sumQuota - sumDeclaredQuota;
		
		buf.append(MessageFormat.format(DIV_MSG,AonStringUtils.repeat("-", headerLength)));
		buf.append(MessageFormat.format(DIV_MSG_BOLD_BLUE,AonStringUtils.repeat(" ", 2)
				+ AonStringUtils.leftPad("TOTAL DECLARADO (B):",40)
				+ AonStringUtils.SPACE
				+ AonStringUtils.leftPad(" ",15)
				+ AonStringUtils.leftPad(DEC.format(sumDeclaredBase),15)		
				+ AonStringUtils.rightPad(" ",8)
				+ AonStringUtils.leftPad(DEC.format(sumDeclaredQuota),15)
				+ AonStringUtils.repeat(" ", 2)
				));
		buf.append(MessageFormat.format(DIV_MSG_BOLD,AonStringUtils.repeat("-", headerLength)));
		buf.append(MessageFormat.format(DIV_MSG_BOLD,AonStringUtils.repeat(" ", 2)
				+ AonStringUtils.leftPad("TOTAL A DECLARAR (A-B):",40)
				+ AonStringUtils.SPACE
				+ AonStringUtils.leftPad(" ",15)
				+ AonStringUtils.leftPad(DEC.format(sumToDeclareBase),15)		
				+ AonStringUtils.rightPad(" ",8)
				+ AonStringUtils.leftPad(DEC.format(sumToDeclareQuota),15)
				+ AonStringUtils.repeat(" ", 2)
				));
		buf.append(MessageFormat.format(DIV_MSG_BOLD,AonStringUtils.repeat("-", headerLength)));

		return MessageFormat.format(MAIN_DIV_MSG,buf.toString());
	}
	
	public static String formatAccountingBreakdown(String title, String subtitle, LinkedList<AccountingBreakdown> list) {
		StringBuilder buf = new StringBuilder();
		String header = AonStringUtils.repeat(" ", 2)
				+ AonStringUtils.rightPad("CUENTA",10)
				+ AonStringUtils.repeat(" ", 21)
				+ AonStringUtils.rightPad("EPIGR.",8)
				+ AonStringUtils.rightPad("ACTIVIDAD",11)
				+ AonStringUtils.leftPad("DEBE",15)		
				+ AonStringUtils.leftPad("HABER",15)
				+ AonStringUtils.leftPad("SAL. DEUDOR",15)		
				+ AonStringUtils.leftPad("SAL. ACREED.",15)
				;				
		buf.append(MessageFormat.format(DIV_MSG,AonStringUtils.repeat(" ", header.length())));
		buf.append(MessageFormat.format(DIV_MSG_BOLD,AonStringUtils.center(title, header.length())));
		buf.append(MessageFormat.format(DIV_MSG_BOLD,AonStringUtils.center("(" + subtitle + ")", header.length())));
		buf.append(MessageFormat.format(DIV_MSG,AonStringUtils.repeat(" ", header.length())));
		buf.append(MessageFormat.format(DIV_MSG_BOLD,AonStringUtils.repeat("-", header.length()))); 
		buf.append(MessageFormat.format(DIV_MSG_BOLD,header));
		buf.append(MessageFormat.format(DIV_MSG_BOLD,AonStringUtils.repeat("-", header.length())));

		TreeMap<String, AccountingBreakdown> map = new TreeMap<String, AccountingBreakdown>();
		for (AccountingBreakdown ab : list) {
			String key = ab.getAccountCode() + "-" + ab.getEpigraph();
			AccountingBreakdown mapped = map.get(key);
			if (mapped == null) {
				mapped = new AccountingBreakdown()
					.setAccount(ab.getAccount())
					.setAccountCode(ab.getAccountCode())
					.setAccountDescription(ab.getAccountDescription())
					.setActivity(ab.getActivity())
					.setActivityDescription(ab.getActivityDescription())
					.setEpigraphSection(ab.getEpigraphSection())
					.setEpigraph(ab.getEpigraph())
					.setRegime(ab.getRegime())
					;
				map.put(key, mapped);
			}
			mapped.setDebit(AonMathUtils.round(mapped.getDebit() + ab.getDebit()));
			mapped.setCredit(AonMathUtils.round(mapped.getCredit() + ab.getCredit()));
		}
		double sumDebit = 0;
		double sumCredit = 0;
		for (AccountingBreakdown ab : map.values()) {
			sumDebit += ab.getDebit();
			sumCredit += ab.getCredit();
			buf.append(MessageFormat.format(DIV_MSG
					 ,AonStringUtils.repeat(" ", 2)
					+ AonStringUtils.rightPad(ab.getAccountCode(),10)
					+ AonStringUtils.rightPad(AonStringUtils.abbreviate(ab.getAccountDescription(),20),21)
					+ AonStringUtils.rightPad(AonStringUtils.defaultIfBlank(ab.getEpigraph(), AonStringUtils.SPACE) ,8)
					+ AonStringUtils.rightPad(AonStringUtils.defaultIfBlank(AonStringUtils.abbreviate(ab.getActivityDescription(),10), AonStringUtils.SPACE),11)
					+ AonStringUtils.leftPad(AonMathUtils.isZero(ab.getDebit())?AonStringUtils.SPACE:DEC2.format(ab.getDebit()),15)		
					+ AonStringUtils.leftPad(AonMathUtils.isZero(ab.getCredit())?AonStringUtils.SPACE:DEC2.format(ab.getCredit()),15)
					+ AonStringUtils.leftPad(AonMathUtils.isGreatherThanZero(ab.getDebitBalance())?DEC2.format(ab.getDebitBalance()):AonStringUtils.SPACE,15)		
					+ AonStringUtils.leftPad(AonMathUtils.isGreatherThanZero(ab.getCreditBalance())?DEC2.format(ab.getCreditBalance()):AonStringUtils.SPACE,15)		
					+ AonStringUtils.repeat(" ", 2)
					));
		}
		double sumDebitBalance = (sumDebit >= sumCredit)?sumDebit - sumCredit:0;
		double sumCreditBalance = (sumCredit >= sumDebit)?sumCredit - sumDebit:0;
		buf.append(MessageFormat.format(DIV_MSG_BOLD,AonStringUtils.repeat("-", header.length())));
		buf.append(MessageFormat.format(DIV_MSG
				 ,AonStringUtils.repeat(" ", 2)
				+ AonStringUtils.repeat(" ",10)
				+ AonStringUtils.repeat(" ",21)
				+ AonStringUtils.repeat(" ",8)
				+ AonStringUtils.repeat(" ",11)
				+ AonStringUtils.leftPad(AonMathUtils.isZero(sumDebit)?AonStringUtils.SPACE:DEC2.format(sumDebit),15)		
				+ AonStringUtils.leftPad(AonMathUtils.isZero(sumCredit)?AonStringUtils.SPACE:DEC2.format(sumCredit),15)		
				+ AonStringUtils.leftPad(AonMathUtils.isGreatherThanZero(sumDebitBalance)?DEC2.format(sumDebitBalance):AonStringUtils.SPACE,15)		
				+ AonStringUtils.leftPad(AonMathUtils.isGreatherThanZero(sumCreditBalance)?DEC2.format(sumCreditBalance):AonStringUtils.SPACE,15)		
				+ AonStringUtils.repeat(" ", 2)
			));
		buf.append(MessageFormat.format(DIV_MSG,AonStringUtils.repeat(" ", header.length())));
		return MessageFormat.format(MAIN_DIV_MSG,buf.toString());
	}
	
	public static void formatInvoices(final PrintWriter out, Stream<IrpfBreakdown> stream, String title, String subtitle) {
		out.print('[');
		stream.forEach( vat -> writeToJSON(out,vat) );
		out.print(']');
		out.flush();
	}
	
	private static void writeToJSON(final PrintWriter out,IrpfBreakdown irpf) {
		out.print('{');
		out.printf("\"invoice\":\"%d\"", irpf.getInvoice());
		if (irpf.getActivity() != null) out.printf(",\"activity\":\"%d\"", irpf.getActivity());
		if (AonStringUtils.isNotBlank( irpf.getActivityDescription())) out.printf(",\"activityDescription\":\"%s\"", irpf.getActivityDescription());
		if (irpf.getIRPFRegime()!=null) out.printf(",\"regime\":\"%d\"",irpf.getIRPFRegime().ordinal());
		if (AonStringUtils.isNotBlank( irpf.getEpigraph())) out.printf(",\"epigraph\":\"%s\"", irpf.getEpigraph());
		out.printf(",\"documentNumber\":\"%s\"", irpf.getDocumentNumber());
		out.printf(",\"referenceCode\":\"%s\"", irpf.getReferenceCode());
		if (AonStringUtils.isNotBlank( irpf.getRegistryDocument())) out.printf(",\"registryDocument\":\"%s\"", irpf.getRegistryDocument());
		if (irpf.getRegistryDocumentType()!=null) out.printf(",\"registryDocumentType\":\"%d\"", irpf.getRegistryDocumentType().ordinal());
		if (irpf.getRegistryDocumentCountry()!=null) out.printf(",\"registryDocumentCountry\":\"%s\"", irpf.getRegistryDocumentCountry().getIso2());
//		if (irpf.getRegistry() != null) out.printf(",\"registry\":\"%d\"", irpf.getRegistry());
		out.printf("," + JSONObject.toString("name", irpf.getName()) );
		out.printf(",\"issueDate\":\"%1$tY-%1$tm-%1$td\"", irpf.getIssueDate());
		out.printf(",\"taxDate\":\"%1$tY-%1$tm-%1$td\"", irpf.getTaxDate());
		out.printf(",\"insidePeriod\":%b", irpf.isInsidePeriod());
		if (irpf.getInvoiceType()!=null) out.printf(",\"invoiceType\":\"%d\"", irpf.getInvoiceType().ordinal());
//		if (irpf.getRectificationType()!=null) out.printf(",\"rectificationType\":\"%d\"", irpf.getRectificationType().ordinal());
//		if (irpf.getRectificationInvoice() != null) out.printf(",\"rectificationInvoice\":\"%d\"", irpf.getRectificationInvoice());
//		if (irpf.isService()) out.print(",\"service\":\"true\"");
		if (irpf.getWithholdingType()!=null) out.printf(",\"withholdingType\":\"%d\"", irpf.getWithholdingType().ordinal());
//		if (irpf.isInvestment() ) out.printf(",\"investment\":\"true\"");
//		if (irpf.isVatAccrualRegime()) out.printf(",\"vatAccrualRegime\":\"true");
//		if (irpf.getVatDeductionType()!=null) out.printf(",\"vatDeductionType\":\"%d\"", irpf.getVatDeductionType().ordinal());
//		if (irpf.isFarmerRegime()) out.printf(",\"farmerRegime\":\"true\"");
		out.printf(",\"base\":%s", Double.toString( irpf.getBase()));
		out.printf(",\"percent\":%s", Double.toString( irpf.getPercent()));
		out.printf(",\"quota\":%s", Double.toString( irpf.getQuota()));
//		if (irpf.getInvestAsset() != null) out.printf(",\"investAsset\":\"%d\"", irpf.getInvestAsset());
		out.printf(",\"deductiblePercent\":%s", Double.toString( irpf.getDeductiblePercent()));
		out.printf(",\"deductibleQuota\":%s", Double.toString( irpf.getDeductibleQuota()));
//		if (irpf.isSurcharge()) out.printf(",\"surcharge\":\"true\"");
//		if (irpf.isSurcharge()) out.printf(",\"surchargePercent\":%s", Double.toString( irpf.getSurchargePercent()));
//		if (irpf.isSurcharge()) out.printf(",\"surchargeQuota\":%s", Double.toString( irpf.getSurchargeQuota()));
		out.print('}');
		out.print(',');
		out.flush();
	}
}
