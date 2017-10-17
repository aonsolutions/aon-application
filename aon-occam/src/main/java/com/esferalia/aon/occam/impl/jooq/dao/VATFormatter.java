package com.esferalia.aon.occam.impl.jooq.dao;

import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.LinkedList;
import java.util.stream.Stream;

import org.jooq.tools.json.JSONObject;

import com.esferalia.aon.occam.api.model.fiscal.FiscalModel;
import com.esferalia.aon.occam.api.model.fiscal.IFiscalModelKey;
import com.esferalia.aon.occam.api.model.fiscal.KeyTypes;
import com.esferalia.aon.occam.api.model.fiscal.VatContext;
import com.esferalia.aon.occam.api.model.fiscal.VatSummaryContext;
import com.esferalia.aon.occam.api.model.type.Period;
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
	
	static final String OP_PRE = "<pre class=\"aon-fixed-font aon-font-small aon-margin-bottom\">";
	static final String CL_PRE = "</pre>";
	static final String OP_DIV = "<div>";
	static final String CL_DIV = "</div>";
	
	static final String OP_DIV_BOLD= "<div><b>";
	static final String OP_DIV_BOLD_LIGHT_BLUE = "<div style=\"color: RoyalBlue;\"><b>";
	static final String OP_DIV_BOLD_BLUE = "<div style=\"color: blue;\"><b>";
	static final String CL_DIV_BOLD= "</b></div>";
	
	
	static final String DIV_MSG = "<div>{0}</div>";
//	static final String LI_MSG = "<li>{0}</li>";
//	static final String DIV_MSG_BLUE= "<div style=\"color: blue;\">{0}</div>";
//	static final String DIV_MSG_BLUE_BORDER_BOTTOM = "<div style=\"color: blue; border-bottom:solid blue 1px;\">{0}</div>";
//	static final String DIV_MSG_BOLD= "<div><b>{0}</b></div>";
//	static final String DIV_MSG_BOLD_BORDER_BOTTOM = "<div style=\"border-bottom:solid black 1px;\"><b>{0}</b></div>";
//	static final String DIV_MSG_BOLD_BLUE= "<div style=\"color: blue;\"><b>{0}</b></div>";
	private static final String SPAN_MSG_ORANGE= "<span style=\"color: orange;\">{0}</span>";
	private static final String SPAN_MSG_GRAY= "<span style=\"color: gray;\">{0}</span>";
	private static final String SPAN_MSG_RED= "<span style=\"color: red;\">{0}</span>";

	private static final String LEGEND = "S (Servicio); I (Inversi\u00F3n); A (R\u00E9gimen agrario); R (Rectificativa); C (Criterio de caja)";
	
	private static final String HEADER = AonStringUtils.repeat(" ", 2)
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
			+ AonStringUtils.SPACE
			+ AonStringUtils.rightPad("N. REFERENCIA.",25)
			+ AonStringUtils.repeat(" ", 2)
			;

	public static String formatInvoices(String title, String subtitle, Collection<VatContext> list) {
		StringBuilder buf = new StringBuilder();
		buf.append(MessageFormat.format(DIV_MSG,AonStringUtils.repeat(" ", HEADER.length())));
		buf.append(OP_DIV_BOLD);
		buf.append(AonStringUtils.center(title, HEADER.length()));
		buf.append(CL_DIV_BOLD);
		if (AonStringUtils.isNotBlank(subtitle)) {
			subtitle = AonStringUtils.abbreviate(subtitle, 200);
			buf.append(OP_DIV_BOLD);
			buf.append(AonStringUtils.center(subtitle, HEADER.length()));
			buf.append(CL_DIV_BOLD);
		}
		buf.append(MessageFormat.format(DIV_MSG,AonStringUtils.leftPad(LEGEND, HEADER.length())));
		
		buf.append(OP_DIV_BOLD);
		buf.append(AonStringUtils.repeat("-", HEADER.length())); 
		buf.append(CL_DIV_BOLD);
		buf.append(OP_DIV_BOLD);
		buf.append(HEADER);
		buf.append(CL_DIV_BOLD);
		buf.append(OP_DIV_BOLD);
		buf.append(AonStringUtils.repeat("-", HEADER.length()));
		buf.append(CL_DIV_BOLD);
		
		if (list == null || list.size() == 0) {
			buf.append(MessageFormat.format(DIV_MSG,AonStringUtils.repeat(" ", HEADER.length())));
			buf.append(NO_DATA);			
			buf.append(MessageFormat.format(DIV_MSG,AonStringUtils.repeat(" ", HEADER.length())));
		}
		
		double sumBase = 0;
		double sumQuota = 0;
		double sumReQuota = 0;
		double sumDedQuota = 0;
		for (VatContext vat : list) {
			buf.append( mapToHtml(vat));
			sumBase = sumBase + vat.getBase();
			sumQuota = sumQuota + vat.getQuota();
			sumReQuota = sumReQuota + vat.getSurchargeQuota();
			sumDedQuota = sumDedQuota + (vat.isSales()?0:vat.getDeductibleQuota());
		}
		buf.append(OP_DIV_BOLD);
		buf.append(AonStringUtils.repeat("-", HEADER.length()));
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
				+ AonStringUtils.leftPad(" ",8)
				+ AonStringUtils.leftPad(AonMathUtils.isZero(sumDedQuota)? " " : DEC.format(sumDedQuota),15)
				+ AonStringUtils.SPACE
				+ AonStringUtils.rightPad(" ",25)
				+ AonStringUtils.repeat(" ", 2)
				);
		buf.append(CL_DIV_BOLD);
		buf.append(OP_DIV_BOLD);
		buf.append(AonStringUtils.repeat("-", HEADER.length()));
		buf.append(CL_DIV_BOLD);
		return MessageFormat.format(MAIN_DIV_MSG,buf.toString());

	}


	private static String mapToHtml(VatContext vat) {
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
				+ AonStringUtils.leftPad(vat.isSales()?AonStringUtils.SPACE:DEC.format(vat.getDeductiblePercent()) + AonStringUtils.PERCENT,8)
				+ AonStringUtils.leftPad(vat.isSales()?AonStringUtils.SPACE:DEC.format(vat.getDeductibleQuota()),15)
				+ AonStringUtils.SPACE
				+ AonStringUtils.rightPad(AonStringUtils.abbreviate(vat.getReferenceCode(),25),25) 
				+ AonStringUtils.repeat(" ", 2)
				+ CL_DIV
				;
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
		buf.append(OP_DIV_BOLD);
		buf.append(AonStringUtils.center(title, header.length()));
		buf.append(CL_DIV_BOLD);
		buf.append(OP_DIV_BOLD);
		buf.append(AonStringUtils.repeat("-", header.length())); 
		buf.append(CL_DIV_BOLD);
		buf.append(OP_DIV_BOLD);
		buf.append(header);
		buf.append(CL_DIV_BOLD);
		buf.append(OP_DIV_BOLD);
		buf.append(AonStringUtils.repeat("-", header.length()));
		buf.append(CL_DIV_BOLD);
		
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
		buf.append(OP_DIV_BOLD);
		buf.append(AonStringUtils.repeat("-", header.length()));
		buf.append(CL_DIV_BOLD);
		buf.append(OP_DIV_BOLD);
		buf.append(AonStringUtils.repeat(" ", 2)
				+ AonStringUtils.leftPad(" ",12)
				+ AonStringUtils.leftPad(" ",12)
				+ AonStringUtils.leftPad(DEC.format(sumBase),15)
				+ AonStringUtils.leftPad(" ",8)
				+ AonStringUtils.leftPad(DEC.format(sumQuota),15)
				+ AonStringUtils.leftPad(DEC.format(sumDedQuota),15)
				+ AonStringUtils.repeat(" ", 2)
				);
		buf.append(CL_DIV_BOLD);
		buf.append(OP_DIV_BOLD);
		buf.append(AonStringUtils.repeat("-", header.length()));
		buf.append(CL_DIV_BOLD);
		return MessageFormat.format(MAIN_DIV_MSG,buf.toString());

	}
	
	
	public static String formatDiffInvoices(String title
			,String subtitle
			,Period period
			,IFiscalModelKey[] keys
			,KeyTypes[] keyTypes
			,LinkedList<FiscalModel> models
			,LinkedList<VatContext> list) {

		IFiscalModelKey baseKey = null;
		IFiscalModelKey quotaKey = null;
		IFiscalModelKey quotaDedKey = null;
		if (keyTypes == null) {
			if (keys.length == 4) {
				baseKey = keys[0];
				quotaKey = keys[2];
				quotaDedKey = keys[3];
			} else if (keys.length == 3) {
				baseKey = keys[0];
				quotaKey = keys[2];
			} else if (keys.length == 2) {
				baseKey = keys[0];
				quotaKey = keys[1];
			} else {
				quotaKey = keys[0];
			}
		} else {
			for (int i = 0; i < keyTypes.length; i++) {
				if (keyTypes[i] == KeyTypes.BASE) {
					baseKey = keys[i];
				} else if (keyTypes[i] == KeyTypes.QUOTA) {
					quotaKey = keys[i];	
				} else if (keyTypes[i] == KeyTypes.DEDUCTIBLE_QUOTA) {
					quotaDedKey = keys[i];	
				}
			}
		}
		boolean hasBase = baseKey != null;
		boolean hasQuota = quotaKey != null;
		boolean hasQuotaDed = quotaDedKey != null;		
		
		StringBuilder buf = new StringBuilder();
		int headerLength = 105; 
		buf.append(MessageFormat.format(DIV_MSG,AonStringUtils.repeat(" ", headerLength)));
		buf.append(OP_DIV_BOLD);
		buf.append(AonStringUtils.center(title, headerLength));
		buf.append(CL_DIV_BOLD);
		if (AonStringUtils.isNotBlank(subtitle)) {
			subtitle = AonStringUtils.abbreviate(subtitle, 200);
			buf.append(OP_DIV_BOLD);
			buf.append(AonStringUtils.center("(" + subtitle + ")", headerLength));		
			buf.append(CL_DIV_BOLD);
		}
		buf.append(OP_DIV_BOLD);
		buf.append(AonStringUtils.repeat("-", headerLength));
		buf.append(CL_DIV_BOLD);
		buf.append(MessageFormat.format(DIV_MSG,AonStringUtils.repeat(" ", headerLength)));
		
		double sumBase = 0;
		double sumQuota = 0;
		double sumQuotaDed = 0;
		double sumBeforePeriodBase = 0;
		double sumBeforePeriodQuota = 0;
		double sumBeforePeriodQuotaDed = 0;
		double sumPeriodBase = 0;
		double sumPeriodQuota = 0;
		double sumPeriodQuotaDed = 0;

		for (VatContext br : list) {
			sumBase += br.getBase(); 
			sumQuota += br.getQuota();
			sumQuotaDed += br.getDeductibleQuota();
			
			sumBeforePeriodBase += br.isInsidePeriod()?0.0:br.getBase(); 
			sumBeforePeriodQuota += br.isInsidePeriod()?0.0:br.getQuota();
			sumBeforePeriodQuotaDed += br.isInsidePeriod()?0.0:br.getDeductibleQuota();
			
			sumPeriodBase += br.isInsidePeriod()?br.getBase():0.0; 
			sumPeriodQuota += br.isInsidePeriod()?br.getQuota():0.0;
			sumPeriodQuotaDed += br.isInsidePeriod()?br.getDeductibleQuota():0.0;
		}
		buf.append(MessageFormat.format(DIV_MSG,AonStringUtils.repeat(" ", 2)
				+ AonStringUtils.rightPad("<b>RESUMEN ACUMULADOS</b>",47)
				+ AonStringUtils.SPACE
				+ (!hasBase?AonStringUtils.EMPTY:AonStringUtils.leftPad(MessageFormat.format(SPAN_MSG_GRAY,"Base impon."),49))		
				+ (!hasQuota?AonStringUtils.EMPTY:AonStringUtils.rightPad(" ",5))
				+ (!hasQuota?AonStringUtils.EMPTY:AonStringUtils.leftPad(MessageFormat.format(SPAN_MSG_GRAY,"Cuota"),49))		
				+ (!hasQuotaDed?AonStringUtils.EMPTY:AonStringUtils.rightPad(" ",5))
				+ (!hasQuotaDed?AonStringUtils.EMPTY:AonStringUtils.leftPad(MessageFormat.format(SPAN_MSG_GRAY,"Cuota deduc."),49))		
				+ AonStringUtils.repeat(" ", 2)
				));
		buf.append(OP_DIV_BOLD);
		buf.append(AonStringUtils.repeat("-", headerLength));
		buf.append(CL_DIV_BOLD);

		buf.append(MessageFormat.format(DIV_MSG,AonStringUtils.repeat(" ", 2)
				+ AonStringUtils.leftPad("ACUMULADO HASTA INICIO DEL PER\u00CDODO :",40)
				+ AonStringUtils.SPACE
				+ (!hasBase?AonStringUtils.EMPTY:AonStringUtils.leftPad(DEC.format(sumBeforePeriodBase),15))		
				+ (!hasQuota?AonStringUtils.EMPTY:AonStringUtils.rightPad(" ",5))
				+ (!hasQuota?AonStringUtils.EMPTY:AonStringUtils.leftPad(DEC.format(sumBeforePeriodQuota),15))
				+ (!hasQuotaDed?AonStringUtils.EMPTY:AonStringUtils.rightPad(" ",5))
				+ (!hasQuotaDed?AonStringUtils.EMPTY:AonStringUtils.leftPad(DEC.format(sumBeforePeriodQuotaDed),15))
				+ AonStringUtils.repeat(" ", 2)
				));
		buf.append(MessageFormat.format(DIV_MSG,AonStringUtils.repeat(" ", 2)
				+ AonStringUtils.leftPad("ACUMULADO EN " + (period.isQuarterPeriod()?"EL ":"") + period.getDescription() + " :",40)
				+ AonStringUtils.SPACE
				+ (!hasBase?AonStringUtils.EMPTY:AonStringUtils.leftPad(DEC.format(sumPeriodBase),15))		
				+ (!hasQuota?AonStringUtils.EMPTY:AonStringUtils.rightPad(" ",5))
				+ (!hasQuota?AonStringUtils.EMPTY:AonStringUtils.leftPad(DEC.format(sumPeriodQuota),15))
				+ (!hasQuotaDed?AonStringUtils.EMPTY:AonStringUtils.rightPad(" ",5))
				+ (!hasQuotaDed?AonStringUtils.EMPTY:AonStringUtils.leftPad(DEC.format(sumPeriodQuotaDed),15))
				+ AonStringUtils.repeat(" ", 2)
				));
		buf.append(OP_DIV_BOLD_LIGHT_BLUE);
		buf.append(AonStringUtils.repeat(" ", 2)
				+ AonStringUtils.leftPad("ACUMULADO DESDE 1 DE ENERO (A):",40)
				+ AonStringUtils.SPACE
				+ (!hasBase?AonStringUtils.EMPTY:AonStringUtils.leftPad(DEC.format(sumBase),15))		
				+ (!hasQuota?AonStringUtils.EMPTY:AonStringUtils.rightPad(" ",5))
				+ (!hasQuota?AonStringUtils.EMPTY:AonStringUtils.leftPad(DEC.format(sumQuota),15))
				+ (!hasQuotaDed?AonStringUtils.EMPTY:AonStringUtils.rightPad(" ",5))
				+ (!hasQuotaDed?AonStringUtils.EMPTY:AonStringUtils.leftPad(DEC.format(sumQuotaDed),15))
				+ AonStringUtils.repeat(" ", 2)
				);
		buf.append(CL_DIV_BOLD);
		buf.append(MessageFormat.format(DIV_MSG,AonStringUtils.repeat(" ", headerLength)));

		double sumDeclaredBase = 0;
		double sumDeclaredQuota = 0;
		double sumDeclaredQuotaDed = 0;

		if (models.size() > 0) {
			buf.append(MessageFormat.format(DIV_MSG,AonStringUtils.repeat(" ", 2)
					+ AonStringUtils.rightPad("<b>DECLARACIONES ANTERIORES</b>",47)
					+ AonStringUtils.SPACE
					+ (!hasBase?AonStringUtils.EMPTY:AonStringUtils.leftPad(MessageFormat.format(SPAN_MSG_GRAY,"Base impon."),49))		
					+ (!hasQuota?AonStringUtils.EMPTY:AonStringUtils.rightPad(" ",5))
					+ (!hasQuota?AonStringUtils.EMPTY:AonStringUtils.leftPad(MessageFormat.format(SPAN_MSG_GRAY,"Cuota"),49))		
					+ (!hasQuotaDed?AonStringUtils.EMPTY:AonStringUtils.rightPad(" ",5))
					+ (!hasQuotaDed?AonStringUtils.EMPTY:AonStringUtils.leftPad(MessageFormat.format(SPAN_MSG_GRAY,"Cuota deduc."),49))		
					+ AonStringUtils.repeat(" ", 2)
					));
			buf.append(OP_DIV_BOLD);
			buf.append(AonStringUtils.repeat("-", headerLength));
			buf.append(CL_DIV_BOLD);
			for (FiscalModel fm : models) {
				buf.append(MessageFormat.format(DIV_MSG,AonStringUtils.repeat(" ", 2)
						+ AonStringUtils.leftPad( 
								AonStringUtils.trim(fm.getPeriod().getDescription()) + (fm.isComplementary()?" [Comp.]:":fm.isReplacement()?" [Sust.]:":":")
								,40)
						+ AonStringUtils.SPACE
						+ (!hasBase?AonStringUtils.EMPTY:AonStringUtils.leftPad(DEC.format(fm.getAmount(baseKey)),15))		
						+ (!hasQuota?AonStringUtils.EMPTY:AonStringUtils.rightPad(" ",5))
						+ (!hasQuota?AonStringUtils.EMPTY:AonStringUtils.leftPad(DEC.format(fm.getAmount(quotaKey)),15))
						+ (!hasQuotaDed?AonStringUtils.EMPTY:AonStringUtils.rightPad(" ",5))
						+ (!hasQuotaDed?AonStringUtils.EMPTY:AonStringUtils.leftPad(DEC.format(fm.getAmount(quotaDedKey)),15))
						+ AonStringUtils.repeat(" ", 2)
						));
				sumDeclaredBase += hasBase?fm.getAmount(baseKey):0.0;
				sumDeclaredQuota += hasQuota?fm.getAmount(quotaKey):0.0;
				sumDeclaredQuotaDed += hasQuotaDed?fm.getAmount(quotaDedKey):0.0;
			}
	
		} else {
			buf.append(MessageFormat.format(DIV_MSG,AonStringUtils.repeat(" ", 2)
					+ AonStringUtils.leftPad("<b>NO HAY DECLARACIONES ANTERIORES</b>",25)
					+ AonStringUtils.SPACE
					+ AonStringUtils.rightPad(" ",45)
					));
		}
		buf.append(OP_DIV_BOLD_LIGHT_BLUE);
		buf.append(AonStringUtils.repeat(" ", 2)
				+ AonStringUtils.leftPad("TOTAL DECLARADO (B):",40)
				+ AonStringUtils.SPACE
				+ (!hasBase?AonStringUtils.EMPTY:AonStringUtils.leftPad(DEC.format(sumDeclaredBase),15))		
				+ (!hasQuota?AonStringUtils.EMPTY:AonStringUtils.rightPad(" ",5))
				+ (!hasQuota?AonStringUtils.EMPTY:AonStringUtils.leftPad(DEC.format(sumDeclaredQuota),15))
				+ (!hasQuotaDed?AonStringUtils.EMPTY:AonStringUtils.rightPad(" ",5))
				+ (!hasQuotaDed?AonStringUtils.EMPTY:AonStringUtils.leftPad(DEC.format(sumDeclaredQuotaDed),15))
				+ AonStringUtils.repeat(" ", 2)
				);
		buf.append(CL_DIV_BOLD);
		
		double sumToDeclareBase = sumBase - sumDeclaredBase;
		double sumToDeclareQuota = sumQuota - sumDeclaredQuota;
		double sumToDeclareQuotaDed = sumQuotaDed - sumDeclaredQuotaDed;

		buf.append(MessageFormat.format(DIV_MSG,AonStringUtils.repeat(" ", headerLength)));
		buf.append(MessageFormat.format(DIV_MSG,AonStringUtils.repeat(" ", 2)
				+ AonStringUtils.rightPad("<b>RESULTADO</b>",47)
				+ AonStringUtils.SPACE
				+ (!hasBase?AonStringUtils.EMPTY:AonStringUtils.leftPad(MessageFormat.format(SPAN_MSG_GRAY,"Base impon."),49))		
				+ (!hasQuota?AonStringUtils.EMPTY:AonStringUtils.rightPad(" ",5))
				+ (!hasQuota?AonStringUtils.EMPTY:AonStringUtils.leftPad(MessageFormat.format(SPAN_MSG_GRAY,"Cuota"),49))		
				+ (!hasQuotaDed?AonStringUtils.EMPTY:AonStringUtils.rightPad(" ",5))
				+ (!hasQuotaDed?AonStringUtils.EMPTY:AonStringUtils.leftPad(MessageFormat.format(SPAN_MSG_GRAY,"Cuota deduc."),49))		
				+ AonStringUtils.repeat(" ", 2)
				));
		buf.append(OP_DIV_BOLD);
		buf.append(AonStringUtils.repeat("-", headerLength));
		buf.append(CL_DIV_BOLD);
		buf.append(OP_DIV_BOLD_BLUE);
		buf.append(AonStringUtils.repeat(" ", 2)
				+ AonStringUtils.leftPad("TOTAL A DECLARAR (A-B):",40)
				+ AonStringUtils.SPACE
				+ (!hasBase?AonStringUtils.EMPTY:AonStringUtils.leftPad(DEC.format(sumToDeclareBase),15))		
				+ (!hasQuota?AonStringUtils.EMPTY:AonStringUtils.rightPad(" ",5))
				+ (!hasQuota?AonStringUtils.EMPTY:AonStringUtils.leftPad(DEC.format(sumToDeclareQuota),15))
				+ (!hasQuotaDed?AonStringUtils.EMPTY:AonStringUtils.rightPad(" ",5))
				+ (!hasQuotaDed?AonStringUtils.EMPTY:AonStringUtils.leftPad(DEC.format(sumToDeclareQuotaDed),15))
				+ AonStringUtils.repeat(" ", 2)
				);
		buf.append(CL_DIV_BOLD);
		buf.append(OP_DIV_BOLD);
		buf.append(AonStringUtils.repeat("-", headerLength));
		buf.append(CL_DIV_BOLD);

		return MessageFormat.format(MAIN_DIV_MSG,buf.toString());
	}

	
	/// ********************************************
	/// ********************************************
	/// ************************************ NEW ***
	/// ********************************************
	/// ********************************************
	public static void formatInvoices(final PrintWriter out, Stream<VatContext> stream, String title, String subtitle) {
		out.print('[');
		stream.forEach( vat -> writeToJSON(out,vat) );
		out.print(']');
		out.flush();
	}
	
	private static void writeToJSON(final PrintWriter out,VatContext vat) {
		
		out.print('{');
		out.printf("\"invoice\":\"%d\"", vat.getInvoice());
		if (vat.getActivity() != null) out.printf(",\"activity\":\"%d\"", vat.getActivity());
		if (AonStringUtils.isNotBlank( vat.getActivityDescription())) out.printf(",\"activityDescription\":\"%s\"", vat.getActivityDescription());
		if (vat.getVatRegime()!=null) out.printf(",\"vatRegime\":\"%d\"",vat.getVatRegime().ordinal());
		if (AonStringUtils.isNotBlank( vat.getEpigraph())) out.printf(",\"epigraph\":\"%s\"", vat.getEpigraph());
		out.printf(",\"documentNumber\":\"%s\"", vat.getDocumentNumber());
		out.printf(",\"referenceCode\":\"%s\"", vat.getReferenceCode());
		if (AonStringUtils.isNotBlank( vat.getRegistryDocument())) out.printf(",\"registryDocument\":\"%s\"", vat.getRegistryDocument());
		if (vat.getRegistryDocumentType()!=null) out.printf(",\"registryDocumentType\":\"%d\"", vat.getRegistryDocumentType().ordinal());
		if (vat.getRegistryDocumentCountry()!=null) out.printf(",\"registryDocumentCountry\":\"%s\"", vat.getRegistryDocumentCountry().getIso2());
		if (vat.getRegistry() != null) out.printf(",\"registry\":\"%d\"", vat.getRegistry());
		out.printf("," + JSONObject.toString("registryName", vat.getRegistryName()) );
		out.printf(",\"issueDate\":\"%1$tY-%1$tm-%1$td\"", vat.getIssueDate());
		out.printf(",\"taxDate\":\"%1$tY-%1$tm-%1$td\"", vat.getTaxDate());
		out.printf(",\"insidePeriod\":%b", vat.isInsidePeriod());
		if (vat.getInvoiceType()!=null) out.printf(",\"invoiceType\":\"%d\"", vat.getInvoiceType().ordinal());
		if (vat.getRectificationType()!=null) out.printf(",\"rectificationType\":\"%d\"", vat.getRectificationType().ordinal());
		if (vat.getRectificationInvoice() != null) out.printf(",\"rectificationInvoice\":\"%d\"", vat.getRectificationInvoice());
		if (vat.isService()) out.print(",\"service\":\"true\"");
		if (vat.getTransaction()!=null) out.printf(",\"transaction\":\"%d\"", vat.getTransaction().ordinal());
		if (vat.isInvestment() ) out.printf(",\"investment\":\"true\"");
		if (vat.isVatAccrualRegime()) out.printf(",\"vatAccrualRegime\":\"true");
		if (vat.getVatDeductionType()!=null) out.printf(",\"vatDeductionType\":\"%d\"", vat.getVatDeductionType().ordinal());
		if (vat.isFarmerRegime()) out.printf(",\"farmerRegime\":\"true\"");
		out.printf(",\"base\":%s", Double.toString( vat.getBase()));
		out.printf(",\"percentage\":%s", Double.toString( vat.getPercentage()));
		out.printf(",\"quota\":%s", Double.toString( vat.getQuota()));
		if (vat.getInvestAsset() != null) out.printf(",\"investAsset\":\"%d\"", vat.getInvestAsset());
		out.printf(",\"deductiblePercent\":%s", Double.toString( vat.getDeductiblePercent()));
		out.printf(",\"deductibleQuota\":%s", Double.toString( vat.getDeductibleQuota()));
		if (vat.isSurcharge()) out.printf(",\"surcharge\":\"true\"");
		if (vat.isSurcharge()) out.printf(",\"surchargePercent\":%s", Double.toString( vat.getSurchargePercent()));
		if (vat.isSurcharge()) out.printf(",\"surchargeQuota\":%s", Double.toString( vat.getSurchargeQuota()));
		out.print('}');
		out.print(',');
		out.flush();
	}
}
