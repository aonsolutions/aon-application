package com.esferalia.aon.occam.impl.jooq.dao;

import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.fiscal.FiscalModel;
import com.esferalia.aon.occam.api.model.fiscal.IFiscalModelKey;
import com.esferalia.aon.occam.api.model.fiscal.IrpfBreakdown;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class IRPFFormatter {

	public static final SimpleDateFormat FMT = new SimpleDateFormat("dd/MM/yyyy");
	public static final DecimalFormat DEC = new DecimalFormat("#,##0.00");
	private static final String NO_DATA = "<div>NO SE ENCONTRARON DATOS</div>";
	static final String DIV_MSG = "<div>{0}</div>";
	static final String DIV_MSG_BORDER_BOTTOM = "<div style=\"border-bottom:solid black 1px;\">{0}</div>";
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
			cont=docs.add(br.getDocument())?"1":" ";
			sumBase += br.getBase();
			sumQuota += br.getQuota();
			buf.append(MessageFormat.format(DIV_MSG
					 ,AonStringUtils.repeat(" ", 2)
					+ AonStringUtils.rightPad(br.getDocument(),12)
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
			
		return buf.toString();
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
			docs.add(br.getDocument());
			buf.append(MessageFormat.format(DIV_MSG
					 ,AonStringUtils.repeat(" ", 2)
					+ AonStringUtils.rightPad(br.getDocumentNumber(),14)
					+ AonStringUtils.rightPad(br.getDocument(),10)
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
		return buf.toString();
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
			acumDocs.add(br.getDocument());
			if (br.isInsidePeriod()) {
				periodDocs.add(br.getDocument());
			} else {
				beforePeriodDocs.add(br.getDocument());
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

		return buf.toString();
	}
}
