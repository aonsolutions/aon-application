package com.esferalia.aon.occam.impl.jooq.dao;

import java.text.MessageFormat;
import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.fiscal.VatContext;
import com.esferalia.aon.occam.api.model.type.Period;
import com.esferalia.aon.watson.util.AonStringUtils;

public class Mod349Formatter extends VATFormatter {
	
	// Esta clase se utilizará para mostrar el detalle de lo declarado en la pestaña de informacion, 
	// lleva algunos datos de Mod349Detail y Mod349
	public static class Mod349DetailInfo {
		
		private Period period;
		private boolean complementary;
		private boolean replacement;
		private double amount;	
		private boolean rectification;
		private Period rectifiedPeriod;
		private double rectifiedAmount;
		
		public Period getPeriod() {
			return period;
		}
		public Mod349DetailInfo setPeriod(Period period) {
			this.period = period;
			return this;
		}
		public boolean isComplementary() {
			return complementary;
		}
		public Mod349DetailInfo setComplementary(boolean complementary) {
			this.complementary = complementary;
			return this;
		}
		public boolean isReplacement() {
			return replacement;
		}
		public Mod349DetailInfo setReplacement(boolean replacement) {
			this.replacement = replacement;
			return this;
		}
		public double getAmount() {
			return amount;
		}
		public Mod349DetailInfo setAmount(double amount) {
			this.amount = amount;
			return this;
		}
		public boolean isRectification() {
			return rectification;
		}
		public Mod349DetailInfo setRectification(boolean rectification) {
			this.rectification = rectification;
			return this;
		}
		public Period getRectifiedPeriod() {
			return rectifiedPeriod;
		}
		public Mod349DetailInfo setRectifiedPeriod(Period rectifiedPeriod) {
			this.rectifiedPeriod = rectifiedPeriod;
			return this;
		}
		public double getRectifiedAmount() {
			return rectifiedAmount;
		}
		public Mod349DetailInfo setRectifiedAmount(double rectifiedAmount) {
			this.rectifiedAmount = rectifiedAmount;
			return this;
		}			
	
	}

	public static String formatDiffInvoicesMod349(String title
			,String subtitle
			,Period period
			,LinkedList<Mod349DetailInfo> models
			,LinkedList<VatContext> list) {

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
		double sumBeforePeriodBase = 0;
		double sumPeriodBase = 0;

		for (VatContext br : list) {
			sumBase += br.getBase(); 
			sumBeforePeriodBase += br.isInsidePeriod()?0.0:br.getBase(); 
			sumPeriodBase += br.isInsidePeriod()?br.getBase():0.0; 
		}
		buf.append(MessageFormat.format(DIV_MSG,AonStringUtils.repeat(" ", 2)
				+ AonStringUtils.rightPad("<b>RESUMEN ACUMULADOS</b>",47)
				+ AonStringUtils.SPACE
				+ (AonStringUtils.leftPad(MessageFormat.format(SPAN_MSG_GRAY,"Base imponible"),49))		
				+ AonStringUtils.repeat(" ", 2)
				));
		buf.append(OP_DIV_BOLD);
		buf.append(AonStringUtils.repeat("-", headerLength));
		buf.append(CL_DIV_BOLD);

		buf.append(MessageFormat.format(DIV_MSG,AonStringUtils.repeat(" ", 2)
				+ AonStringUtils.leftPad("ACUMULADO HASTA INICIO DEL PER\u00CDODO :",40)
				+ AonStringUtils.SPACE
				+ (AonStringUtils.leftPad(DEC.format(sumBeforePeriodBase),15))		
				+ AonStringUtils.repeat(" ", 2)
				));
		buf.append(MessageFormat.format(DIV_MSG,AonStringUtils.repeat(" ", 2)
				+ AonStringUtils.leftPad("ACUMULADO EN " + (period.isQuarterPeriod()?"EL ":"") + period.getDescription() + " :",40)
				+ AonStringUtils.SPACE
				+ (AonStringUtils.leftPad(DEC.format(sumPeriodBase),15))		
				+ AonStringUtils.repeat(" ", 2)
				));
		buf.append(OP_DIV_BOLD_LIGHT_BLUE);
		buf.append(AonStringUtils.repeat(" ", 2)
				+ AonStringUtils.leftPad("ACUMULADO DESDE 1 DE ENERO (A):",40)
				+ AonStringUtils.SPACE
				+ (AonStringUtils.leftPad(DEC.format(sumBase),15))		
				+ AonStringUtils.repeat(" ", 2)
				);
		buf.append(CL_DIV_BOLD);
		buf.append(MessageFormat.format(DIV_MSG,AonStringUtils.repeat(" ", headerLength)));

		double sumDeclaredBase = 0;		
		
		buf.append(MessageFormat.format(DIV_MSG,AonStringUtils.repeat(" ", 2)
		+ AonStringUtils.rightPad("<b>DECLARACIONES ANTERIORES</b>",47)
		+ AonStringUtils.SPACE
		+ (AonStringUtils.leftPad(MessageFormat.format(SPAN_MSG_GRAY,"Base imponible"),49))		
		+ (AonStringUtils.rightPad(" ",5))
		+ (AonStringUtils.leftPad(MessageFormat.format(SPAN_MSG_GRAY,"Base declarada anter."),49))		
		+ AonStringUtils.repeat(" ", 2)
		));
		buf.append(OP_DIV_BOLD);
		buf.append(AonStringUtils.repeat("-", headerLength));
		buf.append(CL_DIV_BOLD);
		
		if (models.size() > 0) {
			for (Mod349DetailInfo fm : models) {
				String s = fm.getPeriod().getDescription()+" ";
				if (fm.isRectification())
					s = s + "[Rect.]";
				if (fm.isComplementary())
					s = s + "[Comp.]";
				if (fm.isReplacement())
					s = s + "[Sust.]";
				buf.append(MessageFormat.format(DIV_MSG, AonStringUtils.repeat(" ", 2)						
						+ AonStringUtils.leftPad(AonStringUtils.trim(s)+":",34)
						+ AonStringUtils.SPACE
						+ (AonStringUtils.leftPad(DEC.format(fm.getAmount()),15))		
						+ (AonStringUtils.rightPad(" ",5))
						+ (AonStringUtils.leftPad(DEC.format(fm.getRectifiedAmount()),17))
						+ AonStringUtils.repeat(" ", 2)
						));
				sumDeclaredBase += (fm.getAmount()-fm.getRectifiedAmount());				
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
				+ AonStringUtils.leftPad("TOTAL DECLARADO (B):",34)
				+ AonStringUtils.SPACE
				+ AonStringUtils.leftPad(DEC.format(sumDeclaredBase),15)
				+ (AonStringUtils.rightPad(" (base imp - base ant)",22))
				+ AonStringUtils.repeat(" ", 2)
				);
		buf.append(CL_DIV_BOLD);
		
		double sumToDeclareBase = sumBase - sumDeclaredBase;			

		buf.append(MessageFormat.format(DIV_MSG,AonStringUtils.repeat(" ", headerLength)));
		buf.append(MessageFormat.format(DIV_MSG,AonStringUtils.repeat(" ", 2)
				+ AonStringUtils.rightPad("<b>RESULTADO</b>",47)
				+ AonStringUtils.SPACE
				+ (AonStringUtils.leftPad(MessageFormat.format(SPAN_MSG_GRAY,"Base imponible"),49))		
				+ AonStringUtils.repeat(" ", 2)
				));
		buf.append(OP_DIV_BOLD);
		buf.append(AonStringUtils.repeat("-", headerLength));
		buf.append(CL_DIV_BOLD);
		buf.append(OP_DIV_BOLD_BLUE);
		buf.append(AonStringUtils.repeat(" ", 2)
				+ AonStringUtils.leftPad("TOTAL A DECLARAR (A-B):",40)
				+ AonStringUtils.SPACE
				+ (AonStringUtils.leftPad(DEC.format(sumToDeclareBase),15))		
				+ AonStringUtils.repeat(" ", 2)
				);
		buf.append(CL_DIV_BOLD);
		buf.append(OP_DIV_BOLD);
		buf.append(AonStringUtils.repeat("-", headerLength));
		buf.append(CL_DIV_BOLD);

		return MessageFormat.format(MAIN_DIV_MSG,buf.toString());
	}

}
