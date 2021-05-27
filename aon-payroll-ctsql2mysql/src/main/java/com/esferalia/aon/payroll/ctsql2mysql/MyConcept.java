package com.esferalia.aon.payroll.ctsql2mysql;

import static com.esferalia.aon.payroll.ctsql2mysql.DefaultMysqlDB.enum2short;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.*;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Bonifica;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Complemento;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Tipboni;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractMysqlDB.Payment_concept;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.payroll.sql.AbstractSQL.IPaymentConcept;
import com.esferalia.aon.salary.enumeration.PaymentType;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.salary.enumeration.BonusType;

public class MyConcept extends DefaultCtsqlDBVisitor implements IConcepts {

	
	
	public static String formatCode(String cdg) {
		return cdg == null || cdg.trim().isEmpty() ? null : String.format("P%s", cdg );
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
	
	public static String getCurrent(String variable) {
		return variable + "_" + CURRENT;
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

	public static String getExprFormat(String calculo, String indCom, String codeApl, String redExt, double garilt) 
	throws SQLException {
		if (calculo.equals("1")) {
			return String.format("/*user*/%%1$s/**/ * %s / %s", 
					WORKED_DAYS , MONTH_DAYS );
		}else if (calculo.equals("2")) {
			return String.format("/*user*/%%1$s/**/ * %s ", 
					WORKED_DAYS );
		}else if (calculo.equals("3")) {
			return String.format("/*user*/%%1$s/**/ * %s ", 
					ACTUAL_DAYS );
		}else if (calculo.equals("4")) {
			return String.format("/*user*/%%1$s/**/ * %s ", 
					SPECIAL_DAYS);
		}else if (calculo.equals("5")) {
			return String.format("%s * /*user*/%%1$s/**/ / 100 ", 
					codeApl );
		}else if (calculo.equals("6")) {
			if ("V".equals(indCom)) { 
				return String.format("/*user*/%%1$s/**/ * %s / 30", 
						HOLIDAYS ); // Jodete
			}
			if ("P".equals(indCom)) {
				if ( "S".equalsIgnoreCase(redExt)) {
					return String.format("/*user*/%%1$s/**/ * %s / %s", 
							WORKED_WEEKS , PAY_WEEKS );
				}
/*
				
				COMENTADO POR EUKE, la variable GUARANTEED_DAYS ha 
				desaparecido y no se cual es la alternativa.
				
				if ( garilt == 100.0 ) {
					if ( "M".equalsIgnoreCase(redExt)) {
						return String.format("%%1$s * %s ( %s + %s ) / %s", 
								MONTHS, QUOTE_DAYS , GUARANTEED_DAYS, PAY_MONTHS);
					} 
					return String.format("%%1$s * ( %s + %s ) / %s", 
							QUOTE_DAYS, GUARANTEED_DAYS , PAY_DAYS );
				}
*/
				if ( "M".equalsIgnoreCase(redExt)) {
					return String.format("%%1$s * %s ( %s ) / %s", 
							MONTHS, QUOTE_DAYS , PAY_MONTHS);
				} 
				return String.format("%%1$s * %s / %s", 
						QUOTE_DAYS , PAY_DAYS );
			} // paga extra 
		}else if (calculo.equals("7")) {
			return String.format("%s * %%1$s / 100 ", 
					SENIOR_BASE);
		}
		return "%1$s";
	}
	
	public static String getExpr(String calculo, BigDecimal importe, String indCom, String codeApl, String redExt, double garilt) 
	throws SQLException {
		String format = getExprFormat(calculo, indCom, codeApl, redExt, garilt);
		return DefaultMysqlDB.format(format, String.format("%.3f", importe ) );
	}
	
	public static String getExpr(String calculo, String variable, String indCom, String codeApl, String redExt, double garilt) 
	throws SQLException {
		String format = getExprFormat(calculo, indCom, codeApl, redExt, garilt);
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
	
	public static String getExpr ( Bonifica bonifica ) throws SQLException{

		double importe = DefaultMysqlDB.toDouble(bonifica.getImporte());
		
		String tipo = bonifica.getTipo();
		
		if ( tipo == null || tipo.equals("M")) {
			if ( "S".equals(bonifica.getProrrateo() )){
				return String.format("%.3f * %s / %s ", importe, BONUS_DAYS, MONTH_DAYS ) ;
			}
			else {
				return String.format("%.3f ", importe) ;
			}
		}else if ( tipo.equals("A")){
			if ( "S".equals(bonifica.getProrrateo() )){
				return String.format("( %.3f / 12 ) * %s / %s", importe, BONUS_DAYS, MONTH_DAYS) ;
			}
			else{
				return String.format("%.3f / 12 ", importe) ;
			}
		}
		else if ( tipo.equals("D")) {
			return String.format("%.3f * %s ", importe, BONUS_DAYS ) ;
		}
		return null;
	}

	public static String getExpr ( Tipboni tipboni ) throws SQLException{
		
		StringBuffer exprBuffer = 
			new StringBuffer();
		
		String calculo = tipboni.getCalculo();

		double prcCg = tipboni.getPrc_cg().doubleValue(); // It's not null
		double prcAcc = tipboni.getPrc_acc().doubleValue(); // It's not null
		double prcAccFgs = tipboni.getPrc_accfgs().doubleValue(); // It's not null
		
		
		if ( calculo.equals("0")) { // % sobre cuota Empresa
			if ( prcCg == prcAcc &&  prcAcc == prcAccFgs ) {
				exprBuffer.append(String.format ( "%s * %.2f / 100", ENTERPRISE_QUOTA, prcCg));
			}
			else {
				if ( prcCg != 0.00 ) {
					exprBuffer.append(String.format(" %s * %.2f / 100", 
							CGC_ENTERPRISE, prcCg  ));
				}
				if ( prcAcc != 0.00 ) {
					if ( exprBuffer.length() > 0 )  { 
						exprBuffer.append(" + ");
					}
					exprBuffer.append(String.format("( %s + %s ) * %.2f / 100 )", 
							IT_ENTERPRISE, IMS_ENTERPRISE, prcAcc ));
				}
				if ( prcAccFgs != 0.00 ) {
					if ( exprBuffer.length() > 0 )  { 
						exprBuffer.append(" + ");
					}
					exprBuffer.append(String.format("(( %s + %s + %s ) * %.2f / 100 )", 
							UNEMPLOY_ENTERPRISE, FP_ENTERPRISE, FOGASA_ENTERPRISE,  prcAccFgs  ));
				}
			}
		}
		else if ( calculo.equals("3")) { // Reducción sobre % Empresa
			if ( prcCg != 0.00 ) {
				exprBuffer.append(String.format("%s * %.2f / 100", 
						CGC_BASE, prcCg  ));
			}
			if ( prcAcc != 0.00 ) {
				if ( exprBuffer.length() > 0 )  { 
					exprBuffer.append(" + ");
				}
				exprBuffer.append(String.format("%s * %.2f / 100 )", 
						CGP_BASE, prcAcc ));
			}
		}
		
		if ( exprBuffer.length() == 0 ) {
			return null;
		}
		else {
			return String.format("( %s ) * %s / %s ", 
					exprBuffer.toString(), BONUS_DAYS, SALARY_DAYS);
		}
	}

	private DefaultMysqlDB mysqlDB;

	private Map<String, Concept<PaymentType>> 	paymentConcepts ;
	private Map<Integer, Bonus> 				bonusConcepts ;
	
	public MyConcept(DefaultMysqlDB mysqlDB) 
	{
		this.mysqlDB = mysqlDB;	
		this.paymentConcepts = new HashMap<String, Concept<PaymentType>>();
		this.bonusConcepts = new HashMap<Integer, Bonus>();
		
		try {
			this.paymentConcepts.put("9w", getConceptPayment("ATEP"));  //PRESTACION ACCIDENTE LABORAL
			updateConceptPayment("ATEP","PRESTACION ACCIDENTE LABORAL");
			this.paymentConcepts.put("9y", getConceptPayment("ECEMP")); //PRESTACION ENFERMEDAD A CARGO DE LA EMPRESA
			updateConceptPayment("ECEMP","PRESTACION ENFERMEDAD A CARGO DE LA EMPRESA");
			this.paymentConcepts.put("9A", getConceptPayment("GTZDO"));	
			//this.paymentConcepts.put("9z", getPaymentConcept("ECSS")); 	//PREST.ENFERMEDAD,MATERNIDAD Y/O R.E.
			//put("9z", getPaymentConcept("MTNAD")); 	//PREST.ENFERMEDAD,MATERNIDAD Y/O R.E.
		} catch (SQLException e) {
		} 		

	}
	
	public String getCode(String cdg) {
		Concept<PaymentType> concept = paymentConcepts.get(cdg);
		return concept != null ? concept.code : null ;
	}
	
	@Override
	public Concept<PaymentType> getPaymentConcept(String codCom) throws SQLException{

		return paymentConcepts.get(codCom);
		
	}
	
	@Override
	public Bonus getBonusConcept(Integer cdg) throws SQLException{

		return bonusConcepts.get(cdg);
		
	}

	
	@Override
	public void visit(AbstractCtsqlDB ctsqlDB) throws SQLException {
		ctsqlDB.visitComplemento(this);
		ctsqlDB.visitTipboni(this);
	}
	
	
	@Override
	public void visitComplemento(Complemento complemento) throws SQLException {
		
		String cdg = complemento.getCdg();
		
		if ( paymentConcepts.get(cdg) != null ) {
			return;
		}
		
		Concept<PaymentType> concept  = mysqlDB.getSystemPaymentConcept(complemento);
		if ( concept != null ) {
			paymentConcepts.put(cdg, concept);
			return;
		}
		
		String code = formatCode ( complemento.getCdg() );
		String description = complemento.getDescripcion() ;
		if ( description == null ) {
			description = complemento.getDesabr();
		}
		PaymentType type =
				mysqlDB.getPaymentType(complemento, PaymentType.CRA_0000);
//				mysqlDB.getPaymentType(description, 
//				complemento.getDinesp(), complemento.getTipcot())
				;
		
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
		concept = 
			new Concept<PaymentType>(paymentConcept, code, type, tipoCot, description);
		paymentConcepts.put(complemento.getCdg() , concept );
	}
	
	
	@Override
	public void visitTipboni(Tipboni tipboni) throws SQLException {
		String description = tipboni.getDescripcion();
		String expression = getExpr(tipboni);
		BonusType bonusType = null;
		if ( "S".equals(tipboni.getBoniss() )){
			bonusType = BonusType.SOCIAL_SECURITY;
		}
		Integer bonusConceptId = 
			mysqlDB.insertBonus_concept(
					expression, 
					description,
					enum2short(bonusType));
		
		Bonus bonus = 
			new Bonus(bonusConceptId, tipboni.getCalculo(), description, expression);
		bonusConcepts.put(tipboni.getCdg(), bonus );
	}
	
	
	private Concept<PaymentType> getConceptPayment ( String code ) 
	throws SQLException {
		ResultSet rs = null; 
		PreparedStatement stmt = null ;
		try {
			stmt = mysqlDB.mysqlConnection.prepareStatement("SELECT * FROM payment_concept WHERE code = ?");
			stmt.setString(1, code);
			rs = stmt.executeQuery();
			if ( rs.next() ){
				int id = rs.getInt("id");
				String quote = rs.getString("quote");
				String description = rs.getString("description");
				PaymentType type = PaymentType.values()[rs.getInt("type")];
				return new Concept<PaymentType>(id, code, type, quote, description);
			}
			else {
				return null;
			}
		}
		finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		}
	}
	
	private void updateConceptPayment ( String code, String description ) 
	throws SQLException {
		PreparedStatement stmt = null ;
		try {
			stmt = mysqlDB.mysqlConnection.prepareStatement("UPDATE payment_concept set description = ?  WHERE code = ?");
			stmt.setString(1, description);
			stmt.setString(1, code);
			stmt.executeUpdate();
		}
		finally {
			if ( stmt != null )
				stmt.close();
		}
	}
	
}
