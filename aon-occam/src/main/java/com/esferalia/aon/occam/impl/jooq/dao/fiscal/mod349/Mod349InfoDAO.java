package com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod349;

import static com.esferalia.aon.occam.impl.jooq.dao.fiscal.DeclarationInfoUtil.DEC2;
import static com.esferalia.aon.occam.impl.jooq.dao.fiscal.DeclarationInfoUtil.blockCenter;
import static com.esferalia.aon.occam.impl.jooq.dao.fiscal.DeclarationInfoUtil.bold;
import static com.esferalia.aon.occam.impl.jooq.dao.fiscal.DeclarationInfoUtil.border;
import static com.esferalia.aon.occam.impl.jooq.dao.fiscal.DeclarationInfoUtil.fontLarger;
import static com.esferalia.aon.occam.impl.jooq.dao.fiscal.DeclarationInfoUtil.marginTop;
import static com.esferalia.aon.occam.impl.jooq.dao.fiscal.DeclarationInfoUtil.paddingLeft;
import static com.esferalia.aon.occam.impl.jooq.dao.fiscal.DeclarationInfoUtil.styledTag;
import static com.esferalia.aon.occam.impl.jooq.dao.fiscal.DeclarationInfoUtil.textCenter;
import static com.esferalia.aon.occam.impl.jooq.dao.fiscal.DeclarationInfoUtil.textRight;
import static com.esferalia.aon.occam.impl.jooq.dao.fiscal.DeclarationInfoUtil.width100;
import static com.esferalia.aon.occam.impl.jooq.dao.fiscal.DeclarationInfoUtil.width150;

import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.json.JSONArray;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.json.VatContextJSON;
import com.esferalia.aon.occam.api.model.finance.EnumVisitors.IFiscalModelKeyInfoVisitor;
import com.esferalia.aon.occam.api.model.fiscal.Mod349;
import com.esferalia.aon.occam.api.model.fiscal.Mod349Detail;
import com.esferalia.aon.occam.api.model.fiscal.VatContext;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Period;
import com.esferalia.aon.watson.util.AonStringUtils;

public class Mod349InfoDAO {
	
	private static final String WIDTH_AUTO = "width: auto;";
	
	private Mod349InfoDAO() {
	}
	
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

	public static String getInfo(AONContext ctx, Mod349 mod349, Mod349Detail detail, FiscalModelKeyInfo infoKey) {
		
		// Se comprueba si el modelo tiene facturas vinculadas en Alcatraz, para mostrar la información en base a esas facturas vinculadas
		// si no tiene facturas vinculadas en Alcatraz, se asume que es un modelo anterior a la puesta en marcha de Alcatraz en este modelo
		// y por lo tanto la información saldrá como salía antes
		boolean hasAlcatraz = Mod349DAO.hasAlcatrazInvoices(ctx, mod349.getFsModel());
		
		return infoKey.visit( new IFiscalModelKeyInfoVisitor<String>() {
			@Override
			public String visitModelInvoiceVatBreakdown() {
				return formatInvoices349( 
						Mod349DAO.getVatBreakdownInfo(ctx, mod349, detail, hasAlcatraz)  // Si el modelo tiene facturas vinculadas en Alcatraz, entonces se lee todo el año  
						 	.filter(p -> hasAlcatraz ? Mod349DAO.isInvoiceDeclared(ctx, p.getInvoice(), mod349.getFsModel()) : true));
			}
			@Override
			public String visitDiffInvoice() {
				return formatDiffMod349(
						 mod349.getPeriod()			
						,Mod349DAO.getDeclaredModels(ctx, mod349, detail.getType(), detail.getCountry(), detail.getDocument()).collect(Collectors.toCollection(LinkedList::new))			 	
						,Mod349DAO.getVatBreakdownInfo(ctx, mod349, detail, true)
						 	.filter( p -> hasAlcatraz ? (!p.isInsidePeriod() && Mod349DAO.isInvoiceDeclared(ctx, p.getInvoice(), mod349.getPeriod().value()) ) || // Si la factura no es del periodo debe estar vinculada en algun modelo 349 del mismo periodo o anterior
							                            (p.isInsidePeriod() && Mod349DAO.isInvoiceDeclared(ctx, p.getInvoice(), mod349.getFsModel())) // Si la factura es del periodo, debe estar vinculada al modelo del que se saca la info
							                          : true)
						 .collect(Collectors.toCollection(LinkedList::new)));
			}
			@Override public String visitModelInvoiceIrpfBreakdown() {
				return visitNone();
			}
			@Override public String visitInvoice() {
				return visitNone(); 
			}
			@Override public String visitInAccrualInvoice() {
				return visitNone(); 
			}
			@Override public String visitOutAccrualInvoice() {
				return visitNone(); 
			}
			@Override public String visitDiffInAccrualInvoice() {
				return visitNone(); 
			}
			@Override public String visitDiffOutAccrualInvoice() {
				return visitNone(); 
			}
			@Override public String visitSalary() {
				return visitNone(); 
			}
			@Override public String visitDiffSalary() {
				return visitNone(); 
			}
			@Override public String visitActAccount() {
				return visitNone(); 
			}
			@Override public String visitTitle() {
				return visitNone(); 
			}
			@Override public String visitIrpfActivity() {
				return visitNone(); 
			}
			@Override public String visitCorporate() {
				return visitNone(); 
			}
			@Override public String visitModelSalaryIrpfBreakdown() {
				return visitNone(); 
			}
			@Override
			public String visitCompute() {
				return visitNone();
			}
			@Override
			public String visitComputeKey() {
				return visitNone();
			}
			@Override
			public String visitProrratedModelInvoiceVatBreakdown() {
				return visitNone();
			}
			@Override
			public String visitModelOutVatAccrualInvoice() {
				return visitNone();
			}
			@Override
			public String visitModelInVatAccrualInvoice() {
				return visitNone();
			}
			@Override 
			public String visitNone()    {
				return AonStringUtils.EMPTY; 
			}
		});
		
	}
	
	private static String formatInvoices349(Stream<VatContext> vatContextStream) {
		return Objects.requireNonNullElse( vatContextStream
					.map(VatContextJSON::toJSON)
					.collect(JSONArray::new, JSONArray::put, JSONArray::put)
					,new JSONArray()).toString();
	}	
	
	private static String formatDiffMod349(Period period, LinkedList<Mod349DetailInfo> models, LinkedList<VatContext> list) {
		
		double sumBase = 0;
		double sumBeforePeriodBase = 0;
		double sumPeriodBase = 0;

		for (VatContext br : list) {
			sumBase += br.getBase(); 
			sumBeforePeriodBase += br.isInsidePeriod()?0.0:br.getBase(); 
			sumPeriodBase += br.isInsidePeriod()?br.getBase():0.0; 
		}
		
		StringBuilder buf = new StringBuilder();
		buf.append("<div style=\"" +
				  "padding-right: 15px; padding-left: 15px; margin-right: auto; "
				+ "margin-left: auto; width:100%; display: flex;flex-wrap: wrap; "
				+ "justify-content: center; box-sizing: border-box"
				+ "\">");
		buf.append("<div style=\"" 
			+ "border-radius: 4px; background: #fff; box-shadow: 0 6px 10px rgba(0,0,0,.08), 0 0 6px rgba(0,0,0,.05);"
			+ "transition: .3s transform cubic-bezier(.155,1.105,.295,1.12),.3s box-shadow,.3s -webkit-transform cubic-bezier(.155,1.105,.295,1.12);"
			+ "padding: 4px 5px 5px 10px; margin: 20px 10px 10px 10px; cursor: pointer;"
			+ "flex: 0 1 40%; min-height: 120px; min-width: 400px;"
			+ "\">");
		
		addTable(buf, "RESUMEN ACUMULADOS", "Base imponible");
		addRow(buf, "ACUMULADO HASTA INICIO DEL PER\u00CDODO", sumBeforePeriodBase);				
		addRow(buf, "ACUMULADO EN " + (period.isQuarterPeriod() ? "EL " : "") + period.getDescription(), sumPeriodBase);
		addRow(buf, "ACUMULADO DESDE 1 DE ENERO (A):", sumBase, bold);
		buf.append("</table>");
		
		double sumDeclaredBase = 0;		
		
		addTable(buf, "DECLARACIONES ANTERIORES", "Base imponible", "Base declarada anter.");
		
		if (models.size() > 0) {
			for (Mod349DetailInfo fm : models) {
				String s = fm.getPeriod().getDescription()+" ";
				if (fm.isRectification())
					s = s + "[Rect.]";
				if (fm.isComplementary())
					s = s + "[Comp.]";
				if (fm.isReplacement())
					s = s + "[Sust.]";
				addRow(buf, AonStringUtils.trim(s), fm.getAmount(), fm.getRectifiedAmount());
				sumDeclaredBase += (fm.getAmount()-fm.getRectifiedAmount());				
			}
		} else {
			buf.append("<tr>")
				.append( MessageFormat.format(styledTag, "td colspan=\"3\"", textCenter+border) )
					.append("NO HAY DECLARACIONES ANTERIORES")
					.append("</td>")
				.append("</tr>");					
		}
		
		addRow(buf, "TOTAL DECLARADO (base imp - base ant) (B):", sumDeclaredBase, bold);
		buf.append("</table>");
		
		double sumToDeclareBase = sumBase - sumDeclaredBase;			

		addTable(buf, "RESULTADO", "Base imponible");
		addRow(buf, "TOTAL A DECLARAR (A-B):", sumToDeclareBase, bold+fontLarger);
		buf.append("</table>");
		
		buf.append("</div>");
		buf.append("</div>");
		
		return buf.toString();
	}
	
	private static void addTable(StringBuilder buf, String header1, String header2) {
		addTable(buf, header1, header2, null);
	}
	
	private static void addTable(StringBuilder buf, String header1, String header2, String header3) {
		buf.append( MessageFormat.format(styledTag, "table cellspacing=\"0\"", blockCenter+marginTop+border+width100) );
		buf.append("<tr>")
			.append( MessageFormat.format(styledTag, "td", textCenter+bold+fontLarger+border+WIDTH_AUTO) )
				.append(header1)
				.append("</td>")
			.append( MessageFormat.format(styledTag, "td", textRight+bold+fontLarger+border+width150) )
				.append(header2)
				.append("</td>");
		if (AonStringUtils.isNotBlank(header3)) {
			buf.append( MessageFormat.format(styledTag, "td", textRight+bold+fontLarger+border+width150) )
				.append(header3);
		}
		buf.append("</tr>");
	}
	
	private static void addRow(StringBuilder buf, String description, double value) {
		addRow(buf, description, value, AonStringUtils.EMPTY);
	}
	
	private static void addRow(StringBuilder buf, String description, double value, String style) {
		addRow(buf, description, value, null, style);
	}
	
	private static void addRow(StringBuilder buf, String description, double value1, double value2) {
		addRow(buf, description, value1, value2, AonStringUtils.EMPTY);
	}
	
	private static void addRow(StringBuilder buf, String description, double value1, Double value2, String style) {
		buf.append("<tr>")
			.append( MessageFormat.format(styledTag, "td", paddingLeft+border+WIDTH_AUTO+style) )
				.append(description)
				.append("</td>")
			.append( MessageFormat.format(styledTag, "td", textRight+width150+border+style) )
				.append(DEC2.format(value1))
				.append("</td>");
		if (value2 != null) {
			buf.append( MessageFormat.format(styledTag, "td", textRight+width150+border+style) )
				.append(DEC2.format(value2.doubleValue()))
				.append("</td>");
		}
		buf.append("</tr>");
	}
		
}
