package com.esferalia.aon.occam.impl.jooq.dao;

import java.text.MessageFormat;
import java.util.Collection;

import com.esferalia.aon.occam.api.model.fiscal.VatContext;
import com.esferalia.aon.occam.api.model.type.InvoiceTransactionType;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class Mod347Formatter extends VATFormatter {
	
	public static String formatInvoices347(String title, String subtitle, Collection<VatContext> list, int year, boolean isVatAccrual) {
		
		String header = AonStringUtils.repeat(" ", 2)
				+ AonStringUtils.rightPad("TIPO",6)
				+ AonStringUtils.rightPad("TRAN.",6)
				+ "S I A R C "
				+ AonStringUtils.rightPad("EPIGR.",8)
				+ AonStringUtils.rightPad("N\u00BA DOCUMENTO",15)
				+ AonStringUtils.rightPad("TITULAR FACTURA",45)
				+ AonStringUtils.rightPad("FECHA FAC.",10)
				+ AonStringUtils.SPACE
				+ AonStringUtils.rightPad("FECHA IMP.",10)
				+ AonStringUtils.rightPad("TIPO IVA.",10)				
				+ AonStringUtils.leftPad(isVatAccrual ? "BASE RECC" : "BASE IMP.",17)				
				+ AonStringUtils.leftPad("% IVA",8)
				+ AonStringUtils.leftPad("CUOTA",15)
				+ AonStringUtils.leftPad("% RE",8)
				+ AonStringUtils.leftPad("CUOTA REQ.",15)				
				+ AonStringUtils.leftPad(isVatAccrual ? "TOTAL RECC" : "TOTAL FAC.",15)				
				+ AonStringUtils.SPACE
				+ (isVatAccrual ?AonStringUtils.rightPad("ESTADO",10):AonStringUtils.SPACE)
				+ (isVatAccrual ? AonStringUtils.leftPad("IMPORTE 347",15) : "")					
				+ AonStringUtils.SPACE
				+ AonStringUtils.SPACE
				+ AonStringUtils.rightPad("N\u00BA REFERENCIA.",25)
				+ AonStringUtils.repeat(" ", 2);
		
		int headerLength = header.length();
		
		StringBuilder buf = new StringBuilder();
		buf.append(MessageFormat.format(DIV_MSG,AonStringUtils.repeat(" ", headerLength)));
		buf.append(OP_DIV_BOLD);
		buf.append(AonStringUtils.center(title, headerLength));
		buf.append(CL_DIV_BOLD);
		if (AonStringUtils.isNotBlank(subtitle)) {
			subtitle = AonStringUtils.abbreviate(subtitle, 200);
			buf.append(OP_DIV_BOLD);
			buf.append(AonStringUtils.center(subtitle, headerLength));
			buf.append(CL_DIV_BOLD);
		}
		buf.append(MessageFormat.format(DIV_MSG,AonStringUtils.leftPad(LEGEND, headerLength)));
		
		buf.append(OP_DIV_BOLD);
		buf.append(AonStringUtils.repeat("-", headerLength)); 
		buf.append(CL_DIV_BOLD);
		buf.append(OP_DIV_BOLD);
		buf.append(header);
		buf.append(CL_DIV_BOLD);
		buf.append(OP_DIV_BOLD);
		buf.append(AonStringUtils.repeat("-", headerLength));
		buf.append(CL_DIV_BOLD);
		
		if (list == null || list.size() == 0) {
			buf.append(MessageFormat.format(DIV_MSG,AonStringUtils.repeat(" ", headerLength)));
			buf.append(NO_DATA);			
			buf.append(MessageFormat.format(DIV_MSG,AonStringUtils.repeat(" ", headerLength)));
		}
		
		double sumBase = 0;
		double sumQuota = 0;
		double sumReQuota = 0;
		double sumTotal = 0;
		double sum347 = 0;
		for (VatContext vat : list) {
			buf.append(mapToHtml347(vat,year));
			if (!vat.isFinancePending()) {
				sumBase = sumBase + vat.getBase();
				sumQuota = sumQuota + vat.getQuota();
				sumReQuota = sumReQuota + vat.getSurchargeQuota();
				if (!vat.isSales() && ((vat.getTransaction() == InvoiceTransactionType.OTHER_ISP) || (vat.getTransaction() == InvoiceTransactionType.EXTRACOMMUNITY && vat.isService())) ) {
					sumTotal = sumTotal + vat.getBase();
				} else {
					sumTotal = sumTotal + vat.getBase() + vat.getQuota() + vat.getSurchargeQuota();
				}
			}
			if (vat.getTaxDate().compareTo(AonDateUtils.getYearFirstDay(year)) >= 0) {
				sum347 = sum347 + vat.getAmount347();
			}
		}
		buf.append(OP_DIV_BOLD);
		buf.append(AonStringUtils.repeat("-", headerLength));
		buf.append(CL_DIV_BOLD);
		buf.append(OP_DIV_BOLD);
		buf.append(AonStringUtils.repeat(" ", 2)
				+ AonStringUtils.repeat(" ", 6)
				+ AonStringUtils.repeat(" ", 6)
				+ AonStringUtils.repeat(" ", 10)
				+ AonStringUtils.repeat(" ", 8)
				+ AonStringUtils.repeat(" ", 15)
				+ AonStringUtils.repeat(" ", 15)
				+ AonStringUtils.leftPad(" ",30)
				+ AonStringUtils.leftPad(" ",10)
				+ AonStringUtils.SPACE
				+ AonStringUtils.leftPad(" ",10)
				+ AonStringUtils.rightPad("TOTAL:",10)				
				+ AonStringUtils.leftPad(DEC.format(sumBase),17)
				+ AonStringUtils.leftPad(" ",8)
				+ AonStringUtils.leftPad(DEC.format(sumQuota),15)
				+ AonStringUtils.leftPad(" ",8)
				+ AonStringUtils.leftPad(DEC.format(sumReQuota),15)				
				+ AonStringUtils.leftPad(DEC.format(sumTotal),15)
				+ AonStringUtils.SPACE
				+ (isVatAccrual ? AonStringUtils.rightPad( AonStringUtils.SPACE,10):AonStringUtils.SPACE)
				+ (isVatAccrual ? AonStringUtils.leftPad(DEC.format(sum347),15) : "")
				+ AonStringUtils.SPACE
				+ AonStringUtils.SPACE
				+ AonStringUtils.rightPad(" ",25)
				+ AonStringUtils.repeat(" ", 2)
				);
		buf.append(CL_DIV_BOLD);
		buf.append(OP_DIV_BOLD);
		buf.append(AonStringUtils.repeat("-", headerLength));
		buf.append(CL_DIV_BOLD);
		return MessageFormat.format(MAIN_DIV_MSG,buf.toString());

	}
	
	private static String mapToHtml347(VatContext vat, int year) {
		return OP_DIV
				+ AonStringUtils.repeat(" ", 2)
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
				+ AonStringUtils.leftPad(DEC.format(
						vat.isFinancePending()?0.0
						:( (!vat.isSales() && ((vat.getTransaction() == InvoiceTransactionType.OTHER_ISP) || (vat.getTransaction() == InvoiceTransactionType.EXTRACOMMUNITY && vat.isService()))) ? vat.getBase() : vat.getBase()+vat.getQuota()+vat.getSurchargeQuota()))
						,15)
				+ AonStringUtils.SPACE
				+ (vat.isVatAccrualRegime() ? AonStringUtils.rightPad( vat.isFinancePending()?"PENDIENTE":"PAG./COB.",10):AonStringUtils.SPACE)
				+ (vat.isVatAccrualRegime() ? AonStringUtils.leftPad(DEC.format( (vat.getTaxDate().compareTo(AonDateUtils.getYearFirstDay(year)) >= 0) ? vat.getAmount347() : 0.0),15) : "")
				
				+ AonStringUtils.SPACE
				+ AonStringUtils.SPACE
				+ AonStringUtils.rightPad(AonStringUtils.abbreviate(vat.getReferenceCode(),25),25) 
				+ AonStringUtils.repeat(" ", 2)
				+ CL_DIV
				;
	}	

}
