package com.esferalia.aon.occam.impl.jooq.dao.fiscal;

import static com.esferalia.aon.watson.j2html.TagCreator.div;

import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.function.Function;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModel;
import com.esferalia.aon.occam.api.model.fiscal.IFiscalModelKey;
import com.esferalia.aon.watson.j2html.tags.DomContent;
import com.esferalia.aon.watson.server.AonObjectUtils;
import com.esferalia.aon.watson.util.AonMathUtils;

public class DeclarationInfoUtil {
	
	public static final DecimalFormat DEC2 = new DecimalFormat("#,##0.00");
	public static final SimpleDateFormat FMT = new SimpleDateFormat("dd/MM/yyyy");
	public static final String fullStyledTag = "<{0} style = \"{1}\">{2}</{0}>";
	public static final String styledTag = "<{0} style = \"{1}\">";
	// STYLES
	public static final String border = "border: solid gray 1px; padding: 2px 5px;";
	public static final String borderCollapse = "border-collapse: collapse;";
	public static final String boxSizingBorderBox = "box-sizing: border-box;";
	public static final String colorLightGreen = "background-color:  #DAF7A6 ;";
	public static final String colorLightOrange = "background-color: #FFD580;";
	public static final String colorLightYellow = "background-color: lightyellow;";
	public static final String displayFlex = "display: flex;";
	public static final String flexWrap = "flex-wrap: wrap;"; 
	public static final String fontLarger = "font-size: 1.2em;";
	public static final String fontMedium = "font-size: 1.1em;";
	public static final String justifyContentCenter = "justify-content: center;"; 
	public static final String marginLeftAuto = "margin-left: auto;";
	public static final String marginLeft1em = "margin-left: 1em;";
	public static final String marginRightAuto = "margin-right: auto;"; 
	public static final String marginTop = "margin-top: 20px;";
	public static final String noWrap = "white-space: nowrap;";
	public static final String paddingLeft = "padding-left: 15px;";
	public static final String paddingRight = "padding-right: 15px;";
	public static final String textLeft = "text-align: left;";
	public static final String textCenter = "text-align: center;";
	public static final String textRight= "text-align: right;";
	public static final String width100 = "width:100%;";
	
 

	
	
	
	
	public static final String textUnderline = "text-decoration: underline;";
	public static final String width500 = "width: 500px;";
	public static final String width150 = "width: 150px;";
	public static final String width200 = "width: 200px;";
	public static final String bold = "font-weight: bold;";
	public static final String blockCenter = "margin-left: auto;margin-right: auto;";
	
	protected DeclarationInfoUtil() {
	}
	
	public static interface IExplainRowManager<T extends DomContent> extends Function<FiscalModel,T>{}
	
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
	
	public static <T extends FiscalModel,X extends DomContent> String wrap(T mod, IExplainRowManager<X> rowManager) {
		return div() 
			.with( rowManager.apply( mod ) )
			.withStyle(		
			  paddingRight+paddingLeft+marginRightAuto+
			  marginLeftAuto+width100+displayFlex+
			  flexWrap+justifyContentCenter+boxSizingBorderBox)
			.render()
		;
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
	
}