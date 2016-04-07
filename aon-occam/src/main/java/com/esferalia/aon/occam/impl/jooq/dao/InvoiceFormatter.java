package com.esferalia.aon.occam.impl.jooq.dao;

import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.finance.InvoiceDetail;
import com.esferalia.aon.watson.util.AonStringUtils;

public class InvoiceFormatter {

	public static final SimpleDateFormat FMT = new SimpleDateFormat("dd/MM/yyyy");
	public static final DecimalFormat DEC = new DecimalFormat("#,##0.00");
	private static final String NO_DATA = "<div>NO SE ENCONTRARON DATOS</div>";
	static final String MAIN_DIV_START = "<pre style=\"font-size: 0.9em;\">";
	static final String MAIN_DIV_END = "</pre>";
	static final String DIV_MSG = "<div>{0}</div>";
	static final String DIV_MSG_BORDER_BOTTOM = "<div style=\"border-bottom:solid black 1px;\">{0}</div>";
	static final String LI_MSG = "<li>{0}</li>";
	static final String DIV_MSG_BLUE= "<div style=\"color: blue;\">{0}</div>";
	static final String DIV_MSG_BLUE_BORDER_BOTTOM = "<div style=\"color: blue; border-bottom:solid blue 1px;\">{0}</div>";
	static final String DIV_MSG_BOLD= "<div><b>{0}</b></div>";
	static final String DIV_MSG_BOLD_BORDER_BOTTOM = "<div style=\"border-bottom:solid black 1px;\"><b>{0}</b></div>";
	static final String DIV_MSG_BOLD_BLUE= "<div style=\"color: blue;\"><b>{0}</b></div>";
	
	public static String formatInvoices(String title, String subtitle, LinkedList<InvoiceDetail> list) {
		StringBuilder buf = new StringBuilder();
		String header = AonStringUtils.repeat(" ", 2)
				+ AonStringUtils.rightPad("FACTURA",15)
				+ AonStringUtils.rightPad("FECHA FAC.",12)					
				+ AonStringUtils.rightPad("TITULAR FACTURA",40)
				+ AonStringUtils.rightPad("PROV.",6)
				+ AonStringUtils.rightPad("AGTE. CCIAL.",15)
				+ AonStringUtils.rightPad("CTR. TRAB.",15)
				+ AonStringUtils.SPACE
				+ AonStringUtils.rightPad("CONCEPTO",20)
				+ AonStringUtils.rightPad("CATEGOR\u00CDA",20)
				+ AonStringUtils.leftPad("CANTIDAD",10)		
				+ AonStringUtils.leftPad("BASE IMP.",15)		
				+ AonStringUtils.repeat(" ", 2)
				;
		buf.append(MAIN_DIV_START);
		buf.append(MessageFormat.format(DIV_MSG,AonStringUtils.repeat(" ", header.length())));
		buf.append(MessageFormat.format(DIV_MSG_BOLD,AonStringUtils.center(title, header.length())));
		buf.append(MessageFormat.format(DIV_MSG,AonStringUtils.repeat(" ", header.length())));
		buf.append(MessageFormat.format(DIV_MSG_BOLD,AonStringUtils.repeat("-", header.length()))); 
		buf.append(MessageFormat.format(DIV_MSG_BOLD,header));
		buf.append(MessageFormat.format(DIV_MSG_BOLD,AonStringUtils.repeat("-", header.length())));
		
		if (list == null || list.size() == 0) {
			buf.append(MessageFormat.format(DIV_MSG,AonStringUtils.repeat(" ", header.length())));
			buf.append(NO_DATA);			
			buf.append(MessageFormat.format(DIV_MSG,AonStringUtils.repeat(" ", header.length())));
		}
		double sumBase = 0.0;
		for (InvoiceDetail det : list) {
			sumBase = sumBase + det.getTaxableBase(); 
			buf.append(MessageFormat.format(DIV_MSG
					 ,AonStringUtils.repeat(" ", 2)
					+ AonStringUtils.rightPad(det.getInvoice().getDocumentNumber(),15)
					+ AonStringUtils.rightPad(FMT.format(det.getInvoice().getIssueDate()),12)					
					+ AonStringUtils.rightPad(det.getInvoice().getRegistryDocument(),10)
					+ AonStringUtils.rightPad(AonStringUtils.abbreviate(det.getInvoice().getRegistryName(),29),30)
					+ AonStringUtils.rightPad(AonStringUtils.substring(AonStringUtils.trimToEmpty( det.getInvoice().getRegistryProvince()),0,5),6)
					+ AonStringUtils.rightPad(AonStringUtils.abbreviate( (det.getSeller()!=null?det.getSeller().getRegistryName():AonStringUtils.SPACE) ,14),15)
					+ AonStringUtils.SPACE
					+ AonStringUtils.rightPad(AonStringUtils.abbreviate(det.getWorkPlace() ,14),15)
					+ AonStringUtils.rightPad(AonStringUtils.abbreviate(
							AonStringUtils.removeTabsAndNewLine( det.getDescription() )
							 
							,19),20)
					+ AonStringUtils.rightPad(AonStringUtils.abbreviate(
							det.getItem()!=null?det.getItem().getCategory():AonStringUtils.SPACE 
							,19),20)
					+ AonStringUtils.leftPad(DEC.format(det.getQuantity()),10)
					+ AonStringUtils.leftPad(DEC.format(det.getTaxableBase()),15)
					+ AonStringUtils.repeat(" ", 2)
					));
		}
		buf.append(MessageFormat.format(DIV_MSG_BOLD,AonStringUtils.repeat("-", header.length())));

		buf.append(MessageFormat.format(DIV_MSG_BOLD
				 ,AonStringUtils.repeat(" ", 148)
				+ AonStringUtils.rightPad("TOTAL",5)
				+ AonStringUtils.leftPad(" ",2)
				+ AonStringUtils.leftPad(DEC.format(sumBase),15)		
				+ AonStringUtils.repeat(" ", 2)
				));
		buf.append(MessageFormat.format(DIV_MSG_BOLD,AonStringUtils.repeat("-", header.length())));
		buf.append(MAIN_DIV_END);
		return buf.toString();
	}
}
