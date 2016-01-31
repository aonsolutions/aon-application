package com.esferalia.aon.occam.impl.jooq.dao;

import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.fiscal.IrpfBreakdown;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class IRPFFormatter {

	private static final SimpleDateFormat FMT = new SimpleDateFormat("dd/MM/yyyy");
	private static final DecimalFormat DEC = new DecimalFormat("#,##0.00");
	private static final String NO_DATA = "<div>NO SE ENCONTRARON DATOS</div>";
	private static final String DIV_MSG = "<div>{0}</div>";
	private static final String DIV_MSG_BOLD= "<div><b>{0}</b></div>";

	public static String formatSalaries(LinkedList<IrpfBreakdown> list) {

		if (list == null || list.size() == 0) {
			return NO_DATA;			
		}
		
		StringBuilder buf = new StringBuilder();
		String header = AonStringUtils.repeat(" ", 2)
				+ AonStringUtils.rightPad("DOCUMENTO",12)
				+ AonStringUtils.rightPad("NOMBRE EMPLEADO",50)
				+ AonStringUtils.rightPad("FECHA",12)					
				+ AonStringUtils.leftPad("CONT.DIN.",10)		
				+ AonStringUtils.leftPad("PERC.DINER.",15)		
				+ AonStringUtils.leftPad("RET.DINER.",15)
				+ AonStringUtils.leftPad("CONT.ESP.",10)
				+ AonStringUtils.leftPad("PERC.ESPEC.",15)		
				+ AonStringUtils.leftPad("RET.ESPEC.",15);
		buf.append(MessageFormat.format(DIV_MSG,AonStringUtils.repeat(" ", header.length())));
		buf.append(MessageFormat.format(DIV_MSG_BOLD,AonStringUtils.center("INFORME I.R.P.F. EN N\u00D3MINAS", header.length())));
		buf.append(MessageFormat.format(DIV_MSG,AonStringUtils.repeat(" ", header.length())));
		buf.append(MessageFormat.format(DIV_MSG_BOLD,AonStringUtils.repeat("-", header.length()))); 
		buf.append(MessageFormat.format(DIV_MSG_BOLD,header));
		buf.append(MessageFormat.format(DIV_MSG_BOLD,AonStringUtils.repeat("-", header.length())));
		
		int moneyCount = 0;
		double moneyBase = 0;
		double moneyQuota = 0;
		int inKindCount = 0;
		double inKindBase = 0;
		double inKindQuota = 0;
		
		String oldDoc = null;
		String doc = "";
		String  name = "";
		boolean moneyCounted = false;
		boolean inKindCounted = false;
		for (IrpfBreakdown br : list) {
			if (!AonStringUtils.equals(oldDoc, br.getDocument())) {
				oldDoc = doc = br.getDocument();
				name = br.getName();
				moneyCounted = false;
				inKindCounted = false;
			} else {
				doc = name = ""; 
			}
			String MC = " ";
			if ( !moneyCounted && br.isMoneyRetention()) {
				moneyCounted = true;
				++moneyCount;
				MC = "1";
			}
			String IC = " ";
			if ( !inKindCounted && br.isInKindRetention()) {
				inKindCounted = true;
				++inKindCount;
				IC = "1";
			}
			 
			buf.append(MessageFormat.format(DIV_MSG,AonStringUtils.repeat(" ", 2)
					+ AonStringUtils.rightPad(doc,12)
					+ AonStringUtils.rightPad(name,50)
					+ AonStringUtils.rightPad(FMT.format(br.getIssueDate()),12)
					+ AonStringUtils.center( MC ,10) 						
					+ AonStringUtils.leftPad(DEC.format(br.getMoneyBase()),15)		
					+ AonStringUtils.leftPad(DEC.format(br.getMoneyQuota()),15)
					+ AonStringUtils.center( IC ,10)
					+ AonStringUtils.leftPad(DEC.format(br.getInKindBase()),15)		
					+ AonStringUtils.leftPad(DEC.format(br.getInKindQuota()),15)
					));
			moneyBase += br.getMoneyBase(); 
			moneyQuota += br.getMoneyQuota();
			inKindBase += br.getInKindBase();
			inKindQuota += br.getInKindQuota();
		}
		buf.append(MessageFormat.format(DIV_MSG_BOLD,AonStringUtils.repeat("-", header.length())));
		buf.append(MessageFormat.format(DIV_MSG_BOLD,AonStringUtils.repeat(" ", 2)
				+ AonStringUtils.rightPad(" ",12)
				+ AonStringUtils.rightPad(" ",50)
				+ AonStringUtils.leftPad("TOTAL",12)
				+ AonStringUtils.center( AonNumberUtils.toString(moneyCount) ,10) 						
				+ AonStringUtils.leftPad(DEC.format(moneyBase),15)		
				+ AonStringUtils.leftPad(DEC.format(moneyQuota),15)
				+ AonStringUtils.center( AonNumberUtils.toString(inKindCount) ,10)
				+ AonStringUtils.leftPad(DEC.format(inKindBase),15)		
				+ AonStringUtils.leftPad(DEC.format(inKindQuota),15)
				));
			
		return buf.toString();
	}
	
	public static String formatInvoices(LinkedList<IrpfBreakdown> list) {
		if (list == null || list.size() == 0) {
			return NO_DATA;			
		}
		
		StringBuilder buf = new StringBuilder();
		String header = AonStringUtils.repeat(" ", 2)
				+ AonStringUtils.rightPad("FACTURA",14)
				+ AonStringUtils.rightPad("TITULAR FACTURA",62)
				+ AonStringUtils.rightPad("FECHA FAC.",12)					
				+ AonStringUtils.rightPad("FECHA IMP.",12)
				+ AonStringUtils.leftPad("CONT.",7)
				+ AonStringUtils.SPACE
				+ AonStringUtils.rightPad("TIPO RET.",15)		
				+ AonStringUtils.leftPad("BASE IMP.",15)		
				+ AonStringUtils.leftPad("PORC.",8)
				+ AonStringUtils.leftPad("CUOTA",15)
				+ AonStringUtils.repeat(" ", 2)
				;
		buf.append(MessageFormat.format(DIV_MSG,AonStringUtils.repeat(" ", header.length())));
		buf.append(MessageFormat.format(DIV_MSG_BOLD,AonStringUtils.center("INFORME I.R.P.F. EN FACTURAS", header.length())));
		buf.append(MessageFormat.format(DIV_MSG,AonStringUtils.repeat(" ", header.length())));
		buf.append(MessageFormat.format(DIV_MSG_BOLD,AonStringUtils.repeat("-", header.length()))); 
		buf.append(MessageFormat.format(DIV_MSG_BOLD,header));
		buf.append(MessageFormat.format(DIV_MSG_BOLD,AonStringUtils.repeat("-", header.length())));
		
		String oldDoc = null;
		String doc = "";
		String  name = "";
		int counted = 0;
		boolean isCounted = false;
		
		double sumBase = 0;
		double sumQuota = 0;

		for (IrpfBreakdown br : list) {
			if (!AonStringUtils.equals(oldDoc, br.getDocument())) {
				oldDoc = doc = br.getDocument();
				name = br.getName();
				isCounted = false;
			} else {
				doc = name = ""; 
			}
			String MC = " ";
			if ( !isCounted) {
				isCounted = true;
				++counted;
				MC = "1";
			}
			buf.append(MessageFormat.format(DIV_MSG,AonStringUtils.repeat(" ", 2)
					+ AonStringUtils.rightPad(br.getDocumentNumber(),14)
					+ AonStringUtils.rightPad(doc,10)
					+ AonStringUtils.rightPad(name,52)
					+ AonStringUtils.rightPad(FMT.format(br.getIssueDate()),12)					
					+ AonStringUtils.rightPad(FMT.format(br.getTaxDate()),12)
					+ AonStringUtils.leftPad(MC,7)
					+ AonStringUtils.SPACE
					+ AonStringUtils.rightPad(br.getWithholdingType().getDescription(),15)		
					+ AonStringUtils.leftPad(DEC.format(br.getBase()),15)		
					+ AonStringUtils.leftPad(DEC.format(br.getPercent()),8)
					+ AonStringUtils.leftPad(DEC.format(br.getQuota()),15)
					+ AonStringUtils.repeat(" ", 2)
					));
			sumBase += br.getBase(); 
			sumQuota += br.getQuota();
		}
		buf.append(MessageFormat.format(DIV_MSG_BOLD,AonStringUtils.repeat("-", header.length())));
		buf.append(MessageFormat.format(DIV_MSG_BOLD,AonStringUtils.repeat(" ", 2)
				+ AonStringUtils.rightPad(" ",14)
				+ AonStringUtils.rightPad(" ",10)
				+ AonStringUtils.rightPad(" ",52)
				+ AonStringUtils.rightPad(" ",12)
				+ AonStringUtils.leftPad("TOTAL..:",12)
				+ AonStringUtils.leftPad( AonNumberUtils.toString(counted) ,7)
				+ AonStringUtils.SPACE
				+ AonStringUtils.rightPad(" ",15)
				+ AonStringUtils.leftPad(DEC.format(sumBase),15)		
				+ AonStringUtils.rightPad(" ",8)
				+ AonStringUtils.leftPad(DEC.format(sumQuota),15)
				+ AonStringUtils.repeat(" ", 2)
				));

		return buf.toString();
	}

}
