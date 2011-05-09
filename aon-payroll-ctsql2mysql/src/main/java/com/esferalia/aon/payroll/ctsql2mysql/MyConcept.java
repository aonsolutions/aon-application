package com.esferalia.aon.payroll.ctsql2mysql;

import static com.esferalia.aon.payroll.ctsql2mysql.DefaultMysqlDB.enum2short;
import static com.esferalia.aon.payroll.enumeration.ContractVariables.ACTUAL_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContractVariables.ENTRY_BY_COMPANY_ACCOUNT;
import static com.esferalia.aon.payroll.enumeration.ContractVariables.FREE_IPREM;
import static com.esferalia.aon.payroll.enumeration.ContractVariables.FREE_IPREM_SHORT;
import static com.esferalia.aon.payroll.enumeration.ContractVariables.HOLIDAYS;
import static com.esferalia.aon.payroll.enumeration.ContractVariables.IPREM;
import static com.esferalia.aon.payroll.enumeration.ContractVariables.IPREM_BASE;
import static com.esferalia.aon.payroll.enumeration.ContractVariables.IPREM_BASE_SHORT;
import static com.esferalia.aon.payroll.enumeration.ContractVariables.MONTH_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContractVariables.QUOTE_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContractVariables.SALARY_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContractVariables.SENIOR_BASE;
import static com.esferalia.aon.payroll.enumeration.ContractVariables.SPECIAL_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContractVariables.WORKED_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContractVariables.YEAR_DAYS;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Complemento;
import com.esferalia.aon.salary.enumeration.PaymentType;

public class MyConcept extends DefaultCtsqlDBVisitor implements IConcepts {


	public static String formatCode(String cdg) {
		return String.format("P%s", cdg );
	}
	
	
	@SuppressWarnings("serial")
	public static final Map<String, String>QUOTE_EXPRESSIONS = 
		new HashMap<String, String>() {
		{
			put ( "1", "%1$s" );
			put ( "2", "0.00" );
			put ( "3", "((" + IPREM_BASE +" += %1$s) > ("+ FREE_IPREM +" = " + IPREM +" * " + SALARY_DAYS +"/"+ MONTH_DAYS +" * 0.20)) ? "+ IPREM_BASE +" - ("+ IPREM_BASE + " = " + FREE_IPREM + ") : 0.00" );
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
	

	public static String getQuoteExprFormat(String tipoCot ) {
		return QUOTE_EXPRESSIONS.get(tipoCot);
	}

	public static String getIrpfExprFormat(String tipoCot, String dinEsp ) {
		String irpfExpr = IRPF_EXPRESSIONS.get(tipoCot);
		return "D".equals(dinEsp) ? irpfExpr : String.format(" %s ? 0.00 : %s ", 
				ENTRY_BY_COMPANY_ACCOUNT,  irpfExpr);
	}
	
	public static String getGrtzdoExprFormat(String calculo, double gtzdo) 
	throws SQLException {
		if (calculo.equals("1")) {
			return gtzdo == 1.00 ? 
					"%1$s" : 
					String.format("%%1$s * %.2f", gtzdo );
		}else if (calculo.equals("2")) {
			return gtzdo == 1.00 ? 
					String.format("%%1$s * %s",MONTH_DAYS ) : 
					String.format("%%1$s * %s * %.2f",MONTH_DAYS, gtzdo );
		}	
		return null;
	}

	public static String getExprFormat(String calculo, String indCom, String codeApl) 
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
			return String.format("%s * %%1$s / 100 ", 
					codeApl );
		}else if (calculo.equals("6")) {
			if ("V".equals(indCom)) { 
				return String.format("%%1$s * %s / 30", 
						HOLIDAYS ); // Jodete
			}
			
		}else if (calculo.equals("7")) {
			return String.format("%s * %%1$s / 100 ", 
					SENIOR_BASE);
		}
		return "%1$s";
	}
	
	public static String getExprFormat(String calculo, BigDecimal importe, String indCom, String codeApl) 
	throws SQLException {
		String format = getExprFormat(calculo, indCom, codeApl);
		return DefaultMysqlDB.format(format, String.format("%.3f", importe ) );
	}
	
	public static String getExprFormat(String calculo, String variable, String indCom, String codeApl) 
	throws SQLException {
		String format = getExprFormat(calculo, indCom, codeApl);
		return DefaultMysqlDB.format(format, variable );
	}

	public static String getPorQuote(String porCot , String expr) {
		String porQuote = "M".equalsIgnoreCase(porCot) ? 
				"( " +  expr + " ) / 12 * " + QUOTE_DAYS + "/" + MONTH_DAYS : 
				"( " +  expr + " ) /" + YEAR_DAYS + " *" + QUOTE_DAYS ;
		if ( porQuote.length() > 128 ) {
			MysqlDB.info("porCot{}: Quote expression too long {}", porCot, porQuote );
			porQuote = porQuote.replaceAll(" ", "");
			porQuote = porQuote.replaceAll(IPREM_BASE.getName(), IPREM_BASE_SHORT.getName());
			porQuote = porQuote.replaceAll(FREE_IPREM.getName(), FREE_IPREM_SHORT.getName());
			MysqlDB.info("porCot{}: Quote expression shorted {}", porCot, porQuote );
		}
		return porQuote;
	}

	private DefaultMysqlDB mysqlDB;

	private Map<String, Concept<PaymentType>> paymentConcepts ;
	
	public MyConcept(DefaultMysqlDB mysqlDB) 
	{
		this.mysqlDB = mysqlDB;	
		this.paymentConcepts = new HashMap<String, Concept<PaymentType>>();
	}
	
	public String getCode(String cdg) {
		Concept<PaymentType> concept = paymentConcepts.get(cdg);
		return concept != null ? concept.code : null ;
	}
	
	@Override
	public Concept<PaymentType> getConcept(String codCom) throws SQLException{

		return paymentConcepts.get(codCom);
		
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
		String dinEsp = complemento.getDinesp();
		String irpfExpr = getIrpfExprFormat(tipoCot, dinEsp );
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
