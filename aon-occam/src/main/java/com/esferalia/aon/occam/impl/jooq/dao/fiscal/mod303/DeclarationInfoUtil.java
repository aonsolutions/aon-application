package com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod303;

import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModel;
import com.esferalia.aon.occam.api.model.fiscal.IFiscalModelKey;
import com.esferalia.aon.occam.api.model.fiscal.Mod303;
import com.esferalia.aon.occam.api.model.type.Mod303Key;
import com.esferalia.aon.occam.impl.jooq.dao.vat.VATDAO;
import com.esferalia.aon.watson.mutable.MutableDouble;
import com.esferalia.aon.watson.server.AonObjectUtils;
import com.esferalia.aon.watson.util.AonMathUtils;

public class DeclarationInfoUtil {
	
	public static final DecimalFormat DEC2 = new DecimalFormat("#,##0.00");
	public static final SimpleDateFormat FMT = new SimpleDateFormat("dd/MM/yyyy");
	public static final String fullStyledTag = "<{0} style = \"{1}\">{2}</{0}>";
	public static final String styledTag = "<{0} style = \"{1}\">"; 
	public static final String marginTop = "margin-top: 20px;";
	public static final String border = "border: solid gray 1px; padding: 2px 5px;";
	public static final String borderCollapse = "border-collapse: collapse;";
	public static final String paddingLeft = "padding-left: 15px;";
	public static final String noWrap = "white-space: nowrap;";
	public static final String fontMedium = "font-size: 1.1em;";
	public static final String fontLarger = "font-size: 1.2em;";
	public static final String textLeft = "text-align: left;";
	public static final String textCenter = "text-align: center;";
	public static final String textRight= "text-align: right;";
	public static final String colorLightYellow = "background-color: lightyellow;";
	public static final String colorLightGreen = "background-color:  #DAF7A6 ;";
	public static final String colorLightOrange = "background-color: #FFD580;";
	
	public static final String textUnderline = "text-decoration: underline;";
	public static final String width500 = "width: 500px;";
	public static final String width150 = "width: 150px;";
	public static final String width200 = "width: 200px;";
	public static final String bold = "font-weight: bold;";
	public static final String blockCenter = "margin-left: auto;margin-right: auto;";
	
	private DeclarationInfoUtil() {
		
	}
	public static class ExplainRowManager implements Function<FiscalModel,String> {
		private double sum = 0.0;
		private boolean something = false;
	
		@Override
		public String apply(FiscalModel fm) {
			something = true;
			sum(fm.getDeclarationResult());
			return new StringBuilder()
				.append("<tr>")
					.append( MessageFormat.format(styledTag, "td", paddingLeft+border+noWrap) )
						.append("Resultado de la liquidaci\u00F3n "
							+ fm.getModelFullName()
							+ AonObjectUtils.defaultIfNull(fm.getDeclarationResultType(), t -> " (" + t.getDescription() + ")"))
					.append("</td>")
					.append( MessageFormat.format(styledTag, "td", textRight+width150+border) )				
						.append(DEC2.format(fm.getDeclarationResult()))
					.append("</td>")
				.append("</tr>")
				.toString();		
		}
		public boolean hasSomething() {
			return something;
		}
		public ExplainRowManager setSomething(boolean something) {
			this.something = something;
			return this;
		}
		public double getSum() {
			return sum;
		}
		public ExplainRowManager sum(double amount) {
			sum = AonMathUtils.round(sum + amount);
			return this;
		} 
	}
	
	public static <T extends FiscalModel,R extends FiscalModel,K extends IFiscalModelKey> String getExplain( AONContext ctx, T mod, K key, ExplainRowManager rowManager) {
		StringBuilder buf = new StringBuilder();
		buf.append("<div style=\"" +
				  "padding-right: 15px; padding-left: 15px; margin-right: auto; "
				+ "margin-left: auto; width:100%; display: flex;flex-wrap: wrap; "
				+ "justify-content: center; box-sizing: border-box"
				+ "\">")
			.append("<div style=\"" 
				+ "border-radius: 4px; background: #fff; box-shadow: 0 6px 10px rgba(0,0,0,.08), 0 0 6px rgba(0,0,0,.05);"
				+ "transition: .3s transform cubic-bezier(.155,1.105,.295,1.12),.3s box-shadow,.3s -webkit-transform cubic-bezier(.155,1.105,.295,1.12);"
				+ "padding: 4px 5px 5px 10px; margin: 20px 10px 10px 10px; cursor: pointer;"
				+ "flex: 0 1 40%; min-height: 120px; min-width: 750px;"
				+ "\">");
		buf.append(rowManager.apply( mod ));
		buf.append("</div>");
		buf.append("</div>");
		return buf.toString();
	}
	
	
	public static <T extends FiscalModel,R extends FiscalModel,K extends IFiscalModelKey> String getExplain( AONContext ctx, T mod, K key, Stream<R> stream, ExplainRowManager rowManager) {
		StringBuilder buf = new StringBuilder();
		buf.append("<div style=\"" +
				  "padding-right: 15px; padding-left: 15px; margin-right: auto; "
				+ "margin-left: auto; width:100%; display: flex;flex-wrap: wrap; "
				+ "justify-content: center; box-sizing: border-box"
				+ "\">")
			.append("<div style=\"" 
				+ "border-radius: 4px; background: #fff; box-shadow: 0 6px 10px rgba(0,0,0,.08), 0 0 6px rgba(0,0,0,.05);"
				+ "transition: .3s transform cubic-bezier(.155,1.105,.295,1.12),.3s box-shadow,.3s -webkit-transform cubic-bezier(.155,1.105,.295,1.12);"
				+ "padding: 4px 5px 5px 10px; margin: 20px 10px 10px 10px; cursor: pointer;"
				+ "flex: 0 1 40%; min-height: 120px; min-width: 350px;"
				+ "\">");
		buf.append( MessageFormat.format(styledTag, "table cellspacing=\"0\"",  blockCenter+marginTop+borderCollapse ) )
			.append("<tr>")
				.append( MessageFormat.format(styledTag, "td colspan=\"2\"",  textCenter+bold+fontLarger+border) )
					.append("Casilla " + key.getBoxFormatted())
				.append("</td>")
			.append("</tr>");
		
		stream.forEach( fm ->  buf.append( rowManager.apply(fm) ));
		if (rowManager.hasSomething()) {
			buf.append("<tr>")
				.append( MessageFormat.format(styledTag, "td",  bold+fontLarger+border) )
					.append("Total")
				.append("</td>")
				.append( MessageFormat.format(styledTag, "td",  bold+textRight+width150+fontLarger+border) )
					.append(DEC2.format(AonMathUtils.round(rowManager.getSum())))
				.append("</td>")
			.append("</tr>");
		} else {
			buf.append("<tr>")
				.append( MessageFormat.format(styledTag, "td colspan=\"2\"",  textCenter+fontLarger+border) )
					.append("No se han encontrado datos para el c\u00E1lculo.")
				.append("</td>")
			.append("</tr>");
		}
		buf.append("</div>");
		buf.append("</div>");
		return buf.toString();
	}

	public static String getRegularizationExplain(AONContext ctx, Mod303 mod303, Mod303Key key) {
		StringBuilder buf = new StringBuilder();
		buf.append("<div "
				+ "style=\"" 
				+ "padding-right: 15px; padding-left: 15px; margin-right: auto; "
				+ "margin-left: auto; width:100%; display: flex;flex-wrap: wrap; "
				+ "justify-content: center; box-sizing: border-box"
				+ "\">");
		buf.append( MessageFormat.format(styledTag, "table cellspacing=\"0\"",  blockCenter+marginTop ) )
			.append("<tr>")
				.append( MessageFormat.format(styledTag, "td colspan=\"12\"",  textCenter+bold+fontLarger+border) )
					.append("Casilla " + key.getBoxFormatted())
				.append("</td>")
			.append("</tr>")
			.append("<tr>")
				.append( MessageFormat.format(styledTag, "td",  textCenter+bold+fontLarger+border) )
					.append("")
				.append("</td>")
				.append( MessageFormat.format(styledTag, "td colspan=\"5\"", textRight+bold+border+noWrap+textCenter+colorLightYellow))
					.append("DECLARADO")
				.append("</td>")
				.append( MessageFormat.format(styledTag, "td colspan=\"5\"", textRight+bold+border+noWrap+textCenter+colorLightGreen))
					.append("NUEVO %")
				.append("</td>")
				.append( MessageFormat.format(styledTag, "td colspan=\"5\"", textRight+bold+border+noWrap+textCenter+colorLightOrange))
					.append("A DECLARAR")
				.append("</td>")
			.append("</tr>")
			
			.append("<tr>")
				.append( MessageFormat.format(styledTag, "td", border+noWrap) )
					.append("Declaraci\u00F3n")
				.append("</td>")
				.append( MessageFormat.format(styledTag, "td colspan=\"3\"", textRight+border+noWrap+textCenter+colorLightYellow))
					.append("IVA con prorrata")
				.append("</td>")
				.append( MessageFormat.format(styledTag, "td", textRight+border+noWrap+textCenter+colorLightYellow))
					.append("IVA sin prorrata")
				.append("</td>")
				.append( MessageFormat.format(styledTag, "td", textRight+border+noWrap+textCenter+colorLightYellow))
					.append("Total IVA deducido")
				.append("</td>")
				

				.append( MessageFormat.format(styledTag, "td colspan=\"3\"", textRight+border+noWrap+textCenter+colorLightGreen))
					.append("IVA con prorrata")
				.append("</td>")
				.append( MessageFormat.format(styledTag, "td", textRight+border+noWrap+textCenter+colorLightGreen))
					.append("IVA sin prorrata")
				.append("</td>")
				.append( MessageFormat.format(styledTag, "td", textRight+border+noWrap+textCenter+colorLightGreen))
					.append("Total IVA deducido")
				.append("</td>")
				
				.append( MessageFormat.format(styledTag, "td colspan=\"2\"", textRight+border+noWrap+textCenter+colorLightOrange))
					.append("Diferencia")
				.append("</td>")
			.append("</tr>")
			
			;
		
		MutableDouble sumDeclared = new MutableDouble();
		MutableDouble sumMustDeclared = new MutableDouble();
		MutableDouble sumDiference = new MutableDouble();

		ExplainRowManager rowManager =  new ExplainRowManager() {
			
			@Override
			public String apply(FiscalModel fm) {
				Mod303 m303 = (Mod303) fm;
				setSomething(true);
				sum(fm.getDeclarationResult());
				double percent = m303.getProratePercent();	

				Mod303Declaration dec =  Mod303Declaration.getInstance(m303);
				MutableDouble sumProrratedMustDeclared = new MutableDouble();
				MutableDouble sumUnprorratedMustDeclared = new MutableDouble();
				VATDAO.getModelVatBreakdown(ctx, m303)
					.filter( br -> Arrays.stream(dec.getProrateKeys())
							.map(dec::getKey )
							.anyMatch(kd -> kd.acceptValue(m303, br)))
					.forEach(br -> {
						if (Mod303Declaration.mustApplyProrrate(m303,br)) {
							sumProrratedMustDeclared.add(br.getDeductibleQuota());
						} else {
							sumUnprorratedMustDeclared.add(br.getDeductibleQuota());
						}
					});
				
				double declared = 0.0;
				if ( fm.getMap() != null && !fm.getMap().isEmpty()) {
					for(String key : fm.getMap().keySet() ) {
						if (dec.isProrrated( Mod303Key.getKey(key))) {
							declared = declared + fm.getAmount(key); 
						}
					}
				}
				double prorratedQuota = sumProrratedMustDeclared.doubleValue(); 
				double unprorratedQuota = sumUnprorratedMustDeclared.doubleValue();
				
				double mustProrrated = AonMathUtils.round(prorratedQuota * mod303.getProratePercent() / 100);
				double mustDeclared = AonMathUtils.round(mustProrrated +  unprorratedQuota);
				
				double diference = AonMathUtils.round(mustDeclared - declared);
		
				sumDeclared.add(declared); 
				sumMustDeclared.add(mustDeclared);
				sumDiference.add(diference);
				
				return new StringBuilder()
					.append("<tr>")
						.append( MessageFormat.format(styledTag, "td", border+noWrap) )
							.append(fm.getModelFullName()
								+ AonObjectUtils.defaultIfNull(fm.getDeclarationResultType(), t -> " (" + t.getDescription() + ")"))
						.append("</td>")
						
						.append( MessageFormat.format(styledTag, "td", textRight+width200+border+colorLightYellow) )				
							.append(DEC2.format(prorratedQuota))
						.append("</td>")
						.append( MessageFormat.format(styledTag, "td", textRight+width150+border+noWrap+colorLightYellow) )				
							.append(DEC2.format(percent))
							.append(" %")
						.append("</td>")
						.append( MessageFormat.format(styledTag, "td", textRight+width150+border+noWrap+colorLightYellow) )				
							.append(DEC2.format(AonMathUtils.round(declared - unprorratedQuota)))
						.append("</td>")
						.append( MessageFormat.format(styledTag, "td", textRight+width200+border+colorLightYellow) )				
							.append(DEC2.format(unprorratedQuota))
						.append("</td>")
						.append( MessageFormat.format(styledTag, "td", textRight+width200+border+colorLightYellow) )				
							.append(DEC2.format(declared))
						.append("</td>")
						
						.append( MessageFormat.format(styledTag, "td", textRight+width200+border+colorLightGreen) )				
							.append(DEC2.format(prorratedQuota))
						.append("</td>")
						.append( MessageFormat.format(styledTag, "td", textRight+width150+border+noWrap+colorLightGreen) )				
						.append(DEC2.format(mod303.getProratePercent()))
							.append(" %")
						.append("</td>")
						.append( MessageFormat.format(styledTag, "td", textRight+width150+border+noWrap+colorLightGreen) )				
							.append(DEC2.format(mustProrrated))
						.append("</td>")
						.append( MessageFormat.format(styledTag, "td", textRight+width200+border+colorLightGreen) )				
							.append(DEC2.format(unprorratedQuota))
						.append("</td>")
						.append( MessageFormat.format(styledTag, "td", textRight+width200+border+colorLightGreen) )				
							.append(DEC2.format(mustDeclared))
						.append("</td>")
						.append( MessageFormat.format(styledTag, "td", textRight+width200+border+colorLightOrange) )				
							.append(DEC2.format(diference))
						.append("</td>")
					.append("</tr>")
					.toString()
					;		
			}
		};		
		
		
		Mod303DAO.getPreviousEffectiveModels(ctx, mod303)
			.forEach( fm ->  buf.append( rowManager.apply(fm) ));
		
		if (rowManager.hasSomething()) {
			buf.append("<tr>")
				.append( MessageFormat.format(styledTag, "td",  bold+fontLarger+border) )
					.append("Total")
				.append("</td>")
				.append( MessageFormat.format(styledTag, "td",  bold+textRight+width200+border+colorLightYellow) )
					.append("")
				.append("</td>")
				.append( MessageFormat.format(styledTag, "td",  bold+textRight+width150+border+colorLightYellow) )
					.append("")
				.append("</td>")
				.append( MessageFormat.format(styledTag, "td",  bold+textRight+width150+border+colorLightYellow) )
					.append("")
				.append("</td>")
				
				
				.append( MessageFormat.format(styledTag, "td",  bold+textRight+width150+border+colorLightYellow) )
					.append("")
				.append("</td>")
				.append( MessageFormat.format(styledTag, "td",  bold+textRight+width200+border+colorLightYellow) )
					.append(DEC2.format(AonMathUtils.round(sumDeclared.getValue())))
				.append("</td>")
				.append( MessageFormat.format(styledTag, "td",  bold+textRight+width150+border+colorLightGreen) )
					.append("")
				.append("</td>")
				.append( MessageFormat.format(styledTag, "td",  bold+textRight+width150+border+colorLightGreen) )
					.append("")
				.append("</td>")
				.append( MessageFormat.format(styledTag, "td",  bold+textRight+width150+border+colorLightGreen) )
					.append("")
				.append("</td>")
				.append( MessageFormat.format(styledTag, "td",  bold+textRight+width150+border+colorLightGreen) )
					.append("")
				.append("</td>")
				.append( MessageFormat.format(styledTag, "td",  bold+textRight+width200+border+colorLightGreen) )
					.append(DEC2.format(AonMathUtils.round(sumMustDeclared.getValue())))
				.append("</td>")
				.append( MessageFormat.format(styledTag, "td",  bold+textRight+width200+border+colorLightOrange) )
					.append(DEC2.format(AonMathUtils.round(sumDiference.getValue())))
				.append("</td>")
			.append("</tr>");
		} else {
			buf.append("<tr>")
				.append( MessageFormat.format(styledTag, "td colspan=\"2\"",  textCenter+fontLarger+border) )
					.append("No se han encontrado datos para el c\u00E1lculo.")
				.append("</td>")
			.append("</tr>");
		}
		buf.append("</div>");
		buf.append("</div>");
		return buf.toString();
	}
	
}