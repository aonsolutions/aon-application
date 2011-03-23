package com.esferalia.aon.payroll.ctsql2mysql;

import static com.esferalia.aon.payroll.ctsql2mysql.DefaultMysqlDB.enum2short;
import static com.esferalia.aon.payroll.enumeration.ContractVariables.ACTUAL_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContractVariables.HOLIDAYS;
import static com.esferalia.aon.payroll.enumeration.ContractVariables.MONTH_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContractVariables.SENIOR_BASE;
import static com.esferalia.aon.payroll.enumeration.ContractVariables.SPECIAL_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContractVariables.WORKED_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContractVariables.YEAR_DAYS;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Complemento;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Percep;
import com.esferalia.aon.salary.enumeration.PaymentType;

public class MyConcepts extends DefaultCtsqlDBVisitor {

	public static class Concept<T>{
		public T type;
		public Integer id;
		public String code;
		public String quote;
		public String description;
		
		
		public Concept(Integer id, String code, T type, String quote, String description) {
			this.id = id;
			this.code = code;
			this.type = type;
			this.quote = quote;
			this.description = description;
		}
	}

	public static String formatCode(String cdg) {
		return String.format("P%s", cdg );
	}
	
	
	@SuppressWarnings("serial")
	public static final Map<String, String>QUOTE_EXPRESSIONS = 
		new HashMap<String, String>() {
		{
			put ( "1", "%1$s" );
			put ( "2", "0.00" );
			put ( "3", " %1$s > (IPREM * 0.20) ? %1$s - (IPREM * 0.20) : 0" );
			put ( "4", "0.00" );
			put ( "5", "%1$s");
			put ( "6", "%1$s" );
			put ( "7", "%1$s");
		}
	};
	
	@SuppressWarnings("serial")
	public static final Map<String, String>IRPF_EXPRESSIONS = 
		new HashMap<String, String>() {
		{
			put ( "1", "%1$s" );
			put ( "2", "%1$s" );
			put ( "3", "%1$s" );
			put ( "4", "0.00" );
			put ( "5", "%1$s");
			put ( "6", "%1$s" );
			put ( "7", "%1$s" );
		}
	};
	public static Short getMonth(Integer mes ) {
		if ( mes == null )
			return null;
		if ( mes == 0 )
			return null;
		Integer month = mes - 1 ;
		return month.shortValue();
	}
	
	public static String getIrpfExprFormat(String tipoCot ) {
		return IRPF_EXPRESSIONS.get(tipoCot);
	}

	public static String getQuoteExprFormat(String tipoCot ) {
		return QUOTE_EXPRESSIONS.get(tipoCot);
	}
	
	
	private DefaultMysqlDB mysqlDB;

	private Map<String, Concept<PaymentType>> paymentConcepts ;
	
	public MyConcepts(DefaultMysqlDB mysqlDB) 
	{
		this.mysqlDB = mysqlDB;	
		this.paymentConcepts = new HashMap<String, Concept<PaymentType>>();
	}
	
	public String getCode(String cdg) {
		Concept<PaymentType> concept = paymentConcepts.get(cdg);
		return concept != null ? concept.code : null ;
	}
	
	public Concept<PaymentType> getConcept(String codCom) {

		return paymentConcepts.get(codCom);
		
	}
	
	
	public String getExprFormat(String calculo, String indCom, String comApl) 
	throws SQLException {
		if (calculo.equals("1")) {
			return String.format("%%1$s * %s / %s", 
					WORKED_DAYS , MONTH_DAYS );
		}else if (calculo.equals("2")) {
			return String.format("%%1$s * %s ", 
					WORKED_DAYS );
		}else if (calculo.equals("3")) {
			return String.format("%%1$s * %s ", 
					ACTUAL_DAYS );
		}else if (calculo.equals("4")) {
			return String.format("%%1$s * %s ", 
					SPECIAL_DAYS);
		}else if (calculo.equals("5")) {
			String concept = getCode(comApl);
			return String.format("%s * %%1$s / 100 ", 
					concept );
		}else if (calculo.equals("6")) {
			if ("V".equals(indCom)) { 
				return String.format("%%1$s * %s / %s", 
						HOLIDAYS, MONTH_DAYS );
			}
		}else if (calculo.equals("7")) {
			return String.format("%s * %%1$s / 100 ", 
					SENIOR_BASE);
		}
		return "%1$s";
	}

	public String getExprFormat(String calculo, BigDecimal importe, String indCom, String comApl) 
	throws SQLException {
		String format = getExprFormat(calculo, indCom, comApl);
		return DefaultMysqlDB.format(format, String.format("%.3f", importe ) );
	}
	
	public static String getPorQuote(String porCot , String expr) {
		return "M".equalsIgnoreCase(porCot) ? 
				"( " +  expr + " ) / 12 * " + WORKED_DAYS + "/" + MONTH_DAYS : 
				expr + "/" + YEAR_DAYS + " *" + WORKED_DAYS ;
	}

	@Override
	public void visit(AbstractCtsqlDB ctsqlDB) throws SQLException {
		ctsqlDB.visitComplemento(this);
	}
	
	
	@Override
	public void visitComplemento(Complemento complemento) throws SQLException {
		String code = formatCode ( complemento.getCdg() );
		String description = complemento.getDescripcion() ;
		if ( description == null ) {
			description = complemento.getDesabr();
		}
		PaymentType type = mysqlDB.getPaymentType(description, 
				complemento.getDinesp(), complemento.getTipcot());
		
		String tipoCot = complemento.getTipcot();
		String irpfExpr = getIrpfExprFormat(tipoCot);
		String quoteExpr = getQuoteExprFormat(tipoCot);
		
		Integer paymentConcept = 
			mysqlDB.insertPayment_concept(
					code, 
					description, 
					enum2short(type) , 
					(short) 0, 
					null, 
					DefaultMysqlDB.format(irpfExpr, code), 
					DefaultMysqlDB.format(quoteExpr, code));
		Concept<PaymentType> concept = 
			new Concept<PaymentType>(paymentConcept, code, type, tipoCot, description);
		paymentConcepts.put(complemento.getCdg() , concept );
	}
	
	
}
