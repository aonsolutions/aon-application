package com.esferalia.aon.occam.impl.jooq.dao;

import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.fiscal.FiscalModel;
import com.esferalia.aon.occam.api.model.fiscal.IFiscalModelKey;
import com.esferalia.aon.occam.api.model.fiscal.VatContext;
import com.esferalia.aon.occam.api.model.fiscal.VatSummaryContext;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class VATFormatter {

	public static final SimpleDateFormat FMT = new SimpleDateFormat("dd/MM/yyyy");
	public static final DecimalFormat INT = new DecimalFormat("#,##0");
	public static final DecimalFormat DEC = new DecimalFormat("#,##0.00");
	public static final DecimalFormat DEC2 = new DecimalFormat("#,###.##");
	
	private static final String NO_DATA = "<div>NO SE ENCONTRARON DATOS</div>";
	static final String MAIN_DIV_MSG = "<pre class=\"aon-fixed-font aon-font-small aon-margin-bottom\">{0}<pre>";
	static final String DIV_MSG = "<div>{0}</div>";
	static final String LI_MSG = "<li>{0}</li>";
	static final String DIV_MSG_BLUE= "<div style=\"color: blue;\">{0}</div>";
	static final String DIV_MSG_BLUE_BORDER_BOTTOM = "<div style=\"color: blue; border-bottom:solid blue 1px;\">{0}</div>";
	static final String DIV_MSG_BOLD= "<div><b>{0}</b></div>";
	static final String DIV_MSG_BOLD_BORDER_BOTTOM = "<div style=\"border-bottom:solid black 1px;\"><b>{0}</b></div>";
	static final String DIV_MSG_BOLD_BLUE= "<div style=\"color: blue;\"><b>{0}</b></div>";
	private static final String SPAN_MSG_ORANGE= "<span style=\"color: orange;\">{0}</span>";
	private static final String SPAN_MSG_RED= "<span style=\"color: red;\">{0}</span>";
	
	public static String formatInvoices(String title, String subtitle, Collection<VatContext> list) {
		StringBuilder buf = new StringBuilder();
		String header = AonStringUtils.repeat(" ", 2)
				+ AonStringUtils.rightPad("TIPO",6)
				+ AonStringUtils.rightPad("TRAN.",6)
				+ "S I A R C "
				+ AonStringUtils.rightPad("EPIGR.",8)
				+ AonStringUtils.rightPad("FACTURA",15)
				+ AonStringUtils.rightPad("DOCUMENTO",15)
				+ AonStringUtils.rightPad("TITULAR FACTURA",30)
				+ AonStringUtils.rightPad("FECHA FAC.",10)
				+ AonStringUtils.SPACE
				+ AonStringUtils.rightPad("FECHA IMP.",10)
				+ AonStringUtils.rightPad("TIPO IVA.",10)		
				+ AonStringUtils.leftPad("BASE IMP.",17)		
				+ AonStringUtils.leftPad("% IVA",8)
				+ AonStringUtils.leftPad("CUOTA",15)
				+ AonStringUtils.leftPad("% RE",8)
				+ AonStringUtils.leftPad("CUOTA RE",15)
				+ AonStringUtils.leftPad("% DED.",8)
				+ AonStringUtils.leftPad("CUOTA DED.",15)
				+ AonStringUtils.repeat(" ", 2)
				;
		buf.append(MessageFormat.format(DIV_MSG,AonStringUtils.repeat(" ", header.length())));
		buf.append(MessageFormat.format(DIV_MSG_BOLD,AonStringUtils.center(title, header.length())));
		buf.append(MessageFormat.format(DIV_MSG_BOLD,AonStringUtils.center(subtitle, header.length())));
		buf.append(MessageFormat.format(DIV_MSG,AonStringUtils.leftPad(
				"S (Servicio); I (Inversi\u00F3n); A (R\u00E9gimen agrario); R (Rectificativa); C (Criterio de caja)"
				, header.length())));
		
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
		double sumReQuota = 0;
		double sumDedQuota = 0;
		for (VatContext vat : list) {
			buf.append(MessageFormat.format(DIV_MSG,
				  AonStringUtils.repeat(" ", 2)
				+ AonStringUtils.rightPad(AonStringUtils.substring(vat.getInvoiceType().getDescription(),0,4) ,6)
				+ AonStringUtils.rightPad(AonStringUtils.substring(vat.getTransaction().getDescription(),0,4) ,6)
				+ (vat.isService()?'S':' ') 
				+ ' '
				+ (vat.isInvestment()?'I':' ')
				+ ' '
				+ (vat.isFarmerRegime()?'A':' ')
				+ ' '
				+ (vat.isRectification()?'R':' ')
				+ ' '
				+ (vat.isVatAccrualRegime()? MessageFormat.format(SPAN_MSG_RED ,'C'):' ')
				+ ' '
				+ AonStringUtils.rightPad(AonStringUtils.defaultIfBlank(vat.getEpigraph(), AonStringUtils.SPACE),8)
				+ AonStringUtils.rightPad(vat.getDocumentNumber(),15)
				+ AonStringUtils.rightPad(AonStringUtils.defaultIfBlank(vat.getRegistryDocument(), AonStringUtils.SPACE),15)
				+ AonStringUtils.rightPad(AonStringUtils.abbreviate(vat.getRegistryName(),29),30)
				+ (
				  AonDateUtils.isSameDay(vat.getIssueDate(), vat.getTaxDate())
					?FMT.format(vat.getIssueDate())
					:MessageFormat.format(SPAN_MSG_ORANGE,FMT.format(vat.getIssueDate()))
				  )
				+ AonStringUtils.SPACE
				+ AonStringUtils.rightPad(FMT.format(vat.getTaxDate()),12)					
				+ AonStringUtils.rightPad(AonStringUtils.abbreviate(vat.getVatDeductionType()!=null
						?vat.getVatDeductionType().getName()
						:AonStringUtils.SPACE ,9),10)		
				+ AonStringUtils.leftPad(DEC.format(vat.getBase()),15)		
				+ AonStringUtils.leftPad(DEC.format(vat.getPercentage()) + AonStringUtils.PERCENT,8)
				+ AonStringUtils.leftPad(DEC.format(vat.getQuota()),15)
				+ AonStringUtils.leftPad(DEC.format(vat.getSurchargePercent()) + AonStringUtils.PERCENT,8)
				+ AonStringUtils.leftPad(DEC.format(vat.getSurchargeQuota()),15)
				+ AonStringUtils.leftPad(vat.isSales()?AonStringUtils.SPACE:DEC.format(vat.getDeductiblePercent()) + AonStringUtils.PERCENT,8)
				+ AonStringUtils.leftPad(vat.isSales()?AonStringUtils.SPACE:DEC.format(vat.getDeductibleQuota()),15)
				+ AonStringUtils.repeat(" ", 2)
			));
			sumBase = AonMathUtils.round(sumBase + vat.getBase()); 
			sumQuota = AonMathUtils.round(sumQuota + vat.getQuota());
			sumReQuota = AonMathUtils.round(sumReQuota + vat.getSurchargeQuota());
			sumDedQuota = AonMathUtils.round(sumDedQuota + (vat.isSales()?0:vat.getDeductibleQuota()));
		}
		buf.append(MessageFormat.format(DIV_MSG_BOLD,AonStringUtils.repeat("-", header.length())));
		buf.append(MessageFormat.format(DIV_MSG_BOLD
				 ,AonStringUtils.repeat(" ", 2)
				+ AonStringUtils.repeat(" ", 6)
				+ AonStringUtils.repeat(" ", 6)
				+ AonStringUtils.repeat(" ", 10)
				+ AonStringUtils.repeat(" ", 8)
				+ AonStringUtils.repeat(" ", 15)
				+ AonStringUtils.repeat(" ", 15)
				+ AonStringUtils.leftPad(" ",30)
				+ AonStringUtils.leftPad(" ",12)
				+ AonStringUtils.leftPad(" ",12)
				+ AonStringUtils.rightPad("TOTAL:",9)				
				+ AonStringUtils.leftPad(DEC.format(sumBase),15)
				+ AonStringUtils.leftPad(" ",8)
				+ AonStringUtils.leftPad(DEC.format(sumQuota),15)
				+ AonStringUtils.leftPad(" ",8)
				+ AonStringUtils.leftPad(DEC.format(sumReQuota),15)
				+ AonStringUtils.leftPad(" ",8)
				+ AonStringUtils.leftPad(AonMathUtils.isZero(sumDedQuota)? " " : DEC.format(sumDedQuota),15)
				+ AonStringUtils.repeat(" ", 2)
				));
		buf.append(MessageFormat.format(DIV_MSG_BOLD,AonStringUtils.repeat("-", header.length())));
		return MessageFormat.format(MAIN_DIV_MSG,buf.toString());

	}


	public static String formatSummary(String title, Collection<VatSummaryContext> list) {
		StringBuilder buf = new StringBuilder();
		String header = AonStringUtils.repeat(" ", 2)
				+ AonStringUtils.leftPad("",12)		
				+ AonStringUtils.leftPad("",12)		
				+ AonStringUtils.leftPad("BASE IMP.",15)		
				+ AonStringUtils.leftPad("% IVA",8)
				+ AonStringUtils.leftPad("CUOTA",15)
				+ AonStringUtils.leftPad("CUOTA DED.",15)
				+ AonStringUtils.repeat(" ", 2)
				;
		buf.append(MessageFormat.format(DIV_MSG,AonStringUtils.repeat(" ", header.length())));
		buf.append(MessageFormat.format(DIV_MSG_BOLD,AonStringUtils.center(title, header.length())));
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
		double sumDedQuota = 0;
		for (VatSummaryContext vat : list) {
			buf.append(MessageFormat.format(DIV_MSG,
				  AonStringUtils.repeat(" ", 2)
				+ AonStringUtils.rightPad(vat.isOutput()?"EMITIDAS":"RECIBIDAS",12)
				+ AonStringUtils.rightPad(vat.getSummaryType().getDescription(),12)
				+ AonStringUtils.leftPad(DEC.format(vat.getBase()),15)		
				+ AonStringUtils.leftPad(DEC.format(vat.getPercentage()) + AonStringUtils.PERCENT,8)
				+ AonStringUtils.leftPad(DEC.format(vat.getQuota()),15)
				+ AonStringUtils.leftPad(DEC.format(vat.getDeductibleQuota()),15)
				+ AonStringUtils.repeat(" ", 2)
			));
			sumBase = AonMathUtils.round(sumBase + vat.getBase()); 
			sumQuota = AonMathUtils.round(sumQuota + vat.getQuota());
			sumDedQuota = AonMathUtils.round(sumDedQuota + vat.getDeductibleQuota());
		}
		buf.append(MessageFormat.format(DIV_MSG_BOLD,AonStringUtils.repeat("-", header.length())));
		buf.append(MessageFormat.format(DIV_MSG_BOLD
				 ,AonStringUtils.repeat(" ", 2)
				+ AonStringUtils.leftPad(" ",12)
				+ AonStringUtils.leftPad(" ",12)
				+ AonStringUtils.leftPad(DEC.format(sumBase),15)
				+ AonStringUtils.leftPad(" ",8)
				+ AonStringUtils.leftPad(DEC.format(sumQuota),15)
				+ AonStringUtils.leftPad(DEC.format(sumDedQuota),15)
				+ AonStringUtils.repeat(" ", 2)
				));
		buf.append(MessageFormat.format(DIV_MSG_BOLD,AonStringUtils.repeat("-", header.length())));
		return MessageFormat.format(MAIN_DIV_MSG,buf.toString());

	}
	
	
	public static String formatDiffInvoices(String title
			,String subtitle
			,IFiscalModelKey[] keys
			, LinkedList<FiscalModel> models
			, LinkedList<VatContext> list) {
		StringBuilder buf = new StringBuilder();
		int headerLength = 100; 
		buf.append(MessageFormat.format(DIV_MSG,AonStringUtils.repeat(" ", headerLength)));
		buf.append(MessageFormat.format(DIV_MSG_BOLD,AonStringUtils.center(title, headerLength)));
		buf.append(MessageFormat.format(DIV_MSG_BOLD,AonStringUtils.center("(" + subtitle + ")", headerLength)));		
		buf.append(MessageFormat.format(DIV_MSG_BOLD,AonStringUtils.repeat("-", headerLength)));
		buf.append(MessageFormat.format(DIV_MSG,AonStringUtils.repeat(" ", headerLength)));
		
		double sumBase = 0;
		double sumQuota = 0;
		double sumBeforePeriodBase = 0;
		double sumBeforePeriodQuota = 0;
		double sumPeriodBase = 0;
		double sumPeriodQuota = 0;

		for (VatContext br : list) {
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
				+ AonStringUtils.leftPad(DEC.format(sumBeforePeriodBase),15)		
				+ AonStringUtils.rightPad(" ",8)
				+ AonStringUtils.leftPad(DEC.format(sumBeforePeriodQuota),15)
				+ AonStringUtils.repeat(" ", 2)
				));
		buf.append(MessageFormat.format(DIV_MSG,AonStringUtils.repeat(" ", 2)
				+ AonStringUtils.leftPad("ACUMULADO PER\u00CDODO :",40)
				+ AonStringUtils.SPACE
				+ AonStringUtils.leftPad(DEC.format(sumPeriodBase),15)		
				+ AonStringUtils.rightPad(" ",8)
				+ AonStringUtils.leftPad(DEC.format(sumPeriodQuota),15)
				+ AonStringUtils.repeat(" ", 2)
				));

		buf.append(MessageFormat.format(DIV_MSG_BOLD_BLUE,AonStringUtils.repeat(" ", 2)
				+ AonStringUtils.leftPad("ACUMULADO DESDE 1 DE ENERO (A):",40)
				+ AonStringUtils.SPACE
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
			baseKey = keys[0];
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
	
}
