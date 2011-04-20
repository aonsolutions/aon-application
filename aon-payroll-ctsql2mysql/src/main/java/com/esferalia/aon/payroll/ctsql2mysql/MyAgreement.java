package com.esferalia.aon.payroll.ctsql2mysql;


import static com.esferalia.aon.payroll.ctsql2mysql.DefaultMysqlDB.enum2short;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Categoria;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Convenio;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Emprper;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Nivel;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Percep;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Percniv;
import com.esferalia.aon.payroll.ctsql2mysql.MyConcepts.Concept;
import com.esferalia.aon.payroll.enumeration.ContractVariables;
import com.esferalia.aon.salary.enumeration.PaymentType;
import com.esferalia.aon.salary.enumeration.SalaryType;


public class MyAgreement extends DefaultCtsqlDBVisitor {
	
	
	private static java.sql.Date START_DATE = 
		new java.sql.Date(110,01,01) ; 

	public static String getAmountVariable(String code) {
		return  MyConcepts.formatCode(code) + "_IMPORTE";
	}
	
	public static String getGtzdoVariable(String code) {
		return  MyConcepts.formatCode(code) + "_GARANTIZADO";
	}

	private  int 	level;
	private int 	agreement;
	java.sql.Date 	startDate ;
	
	private MyConcepts myConcepts;
	private DefaultMysqlDB mysqlDB;
	
	private AbstractCtsqlDB ctsqlDB;

	private Map<String, Integer>	agreements;
	private Map<String, Map<String, Integer>>	levels;
	private Map<String, Map<String, Map<String, Integer>>>	categories;
	
	
	private Map<String,Map<String,List<String>>> agreementPayments;

	public MyAgreement(DefaultMysqlDB mysqlDB, MyConcepts myConcepts) {
		this(mysqlDB, myConcepts, null);
	}
	public MyAgreement(DefaultMysqlDB mysqlDB, MyConcepts myConcepts, Date startDate ) {
		this.mysqlDB = mysqlDB;
		this.myConcepts = myConcepts;
		this.startDate = startDate == null ? START_DATE : new java.sql.Date(startDate.getTime());
		this.levels = new HashMap<String, Map<String,Integer>>();
		this.categories = new HashMap<String, Map<String,Map<String,Integer>>>();
		this.agreements = new HashMap<String, Integer>();
		this.agreementPayments = new HashMap<String, Map<String,List<String>>>();
	}
	
	
	
	@Override
	public void visit(AbstractCtsqlDB ctsqlDB) throws SQLException {
		this.ctsqlDB = ctsqlDB;
		ctsqlDB.visitConvenio(this);
	}
	

	public Integer getAgreement(String oldCdg) {
		return agreements.get(oldCdg);
	}
	
	public Integer getAgreementLevel(String codCon, String oldCdg) {
		return DefaultMysqlDB.get(levels, codCon, oldCdg);
	}

	public Integer getAgreementCategory(String codCon, String nivel, String oldCdg) {
		return DefaultMysqlDB.get(categories, codCon, nivel, oldCdg);
	}

	public Integer insertAgreementCategory(String codCon, String nivel, String oldCdg) 
	throws SQLException {
		Integer agreementLevel =getAgreementLevel(codCon, nivel) ;
		if ( agreementLevel == null )
			return null ;
		
		String descripcion = String.format("Categoria %s", oldCdg);
		
		Integer agreementCategory = 
			mysqlDB.insertAgreement_level_category(agreementLevel, descripcion);
		
		DefaultMysqlDB.save(categories, codCon, nivel, oldCdg, agreementCategory );
		
		return agreementCategory;
	}

	
	public static class PercepPercnivComparator extends DefaultCtsqlDBVisitor {
		
		
		public static int NOT_FOUND 	= -2;
		public static int NOT_EQUALS 	= -1;
		public static int EQUALS 		= 0;
		public static int NOT_AMOUNT 	= 1;
		public static int NOT_GARILT 	= 1<<1;
		
		private Percep percep;
		private int compare = NOT_FOUND;
		
		public PercepPercnivComparator(Percep percep) {
			this.percep = percep;
		}
		
		@Override
		public void visitPercniv(Percniv percniv) throws SQLException {
			if ( ! percniv.getCalculo().equals(percep.getCalculo()) ){
				compare = NOT_EQUALS ;
				return ;
			}
			if ( ! percniv.getTipcot().equals(percep.getTipcot()) ){
				compare = NOT_EQUALS ;
				return ;
			}
			if ( ! percniv.getMes().equals(percep.getMes()) ){
				compare = NOT_EQUALS ;
				return ;
			}
			if ( ! percniv.getDinesp().equals(percep.getDinesp()) ){
				compare = NOT_EQUALS ;
				return ;
			}
			
			
            /*
			String perIndCom = percep.getIndcom() != null ? percep.getIndcom(): "";
			String nivIndCom = percniv.getIndcom()!= null ? percniv.getIndcom():"";
			if ( !nivIndCom.equals(perIndCom) ){
                compare = NOT_EQUALS  ;
                return ;
            }*/
			
			String perRedExt = percep.getRedext() != null ? percep.getRedext(): "";
			String nivRedExt = percniv.getRedext()!= null ? percniv.getRedext():"";
            if ( !nivRedExt.equals(perRedExt) ){
                    compare = NOT_EQUALS  ;
                    return ;
            } // TODO: Solo cuando sea paga extra.
            
			if ( ! percniv.getImporte().equals(percep.getImporte()) ){
				compare = NOT_AMOUNT  | NOT_GARILT;
				return;
			}
			
			BigDecimal perGarilt = percep.getGarilt() != null ? percep.getGarilt() : BigDecimal.ZERO;
			BigDecimal nivGarilt = percniv.getGarilt()!= null ? percniv.getGarilt() : BigDecimal.ZERO;
            if ( !perGarilt.equals(nivGarilt) ){
                    compare = NOT_GARILT  ;
                    return;
            }
            
            compare = EQUALS;
		}

	}
	
	public static class EmprperNivelComparator extends DefaultCtsqlDBVisitor{
		
		private List<String> nivelCodComs ; 
		
		public EmprperNivelComparator (List<String> nivelCodComs) {
			this.nivelCodComs = new ArrayList<String>( nivelCodComs );
		}
		
		@Override
		public void visitRel_pcp_epp(Percep percep, Emprper emprper)
				throws SQLException {
			String codCom = percep.getCodcom();
			if ( codCom == null )
				return;
			java.sql.Date fecFin = percep.getFecfin();
			java.sql.Date fecBaja = emprper.getFecbaj();
			
			if ( fecFin == null || DefaultMysqlDB.is9999(fecFin) ||
					( fecBaja != null && fecFin.compareTo(fecBaja)>=0 ) ){
				this.nivelCodComs.remove(codCom);
			}
		}
		
		public boolean inherits() {
			return nivelCodComs!= null && nivelCodComs.isEmpty();
		}
	
	}
	
	public int hasPayment(String cdg, String nivel, String codcom,  Percep percep) 
	throws SQLException {
		PercepPercnivComparator comparator = 
			new PercepPercnivComparator(percep);
		this.ctsqlDB.visitPercniv(cdg, nivel, codcom, comparator);
		
		return comparator.compare;
	}
	
	
	public boolean inherits(Emprper emprper, String codcon, String nivel  ) throws SQLException {
		List<String> codcoms = 
			DefaultMysqlDB.get(agreementPayments, codcon, nivel);
		if ( codcoms == null ) {
			return false;
		}
		if ( codcoms.isEmpty() ){
			return true;
		}
		
		EmprperNivelComparator emprperNivelComparator =
			new EmprperNivelComparator(codcoms);
		emprper.visitRel_pcp_epp(emprperNivelComparator);
		return emprperNivelComparator.inherits();
	}
	
	
	@Override
	public void visitConvenio(Convenio convenio) throws SQLException {
		
		String description = convenio.getDescripcion();
		this.agreement = mysqlDB.insertAgreement(null,	//TODO: ¿ Calendar ?  
				description);
		agreements.put(convenio.getCdg(), this.agreement);
		
		convenio.visitRel_niv_con(this);
		convenio.visitRel_cat_con(this);
	}
	
	@Override
	public void visitRel_niv_con(Nivel nivel, Convenio convenio)
			throws SQLException {
		this.level =  
			mysqlDB.insertAgreement_level(this.agreement, nivel.getCdg());
		
		DefaultMysqlDB.save(levels, nivel.getCodcon(), nivel.getCdg(), this.level);
		DefaultMysqlDB.save(agreementPayments, nivel.getCodcon(), nivel.getCdg(), new LinkedList<String>() );
		
		
		nivel.visitPercniv_nivel(this);
		
	}
	
	@Override
	public void visitRel_cat_con(Categoria categoria, Convenio convenio)
			throws SQLException {
		Integer level = 
			DefaultMysqlDB.get(levels, categoria.getCodcon(), categoria.getNivel());

		if ( level == null ) {
			mysqlDB.error("categoria[{}]: Not found nivel retributivo {} ", 
					categoria.getCdg(), categoria.getNivel() );
			return;
		}
		String description = categoria.getDescripcion();
		int categoryId =
			mysqlDB.insertAgreement_level_category(level, description);
		
		DefaultMysqlDB.save(categories, categoria.getCodcon(), categoria.getNivel(), categoria.getCdg(), categoryId);
	}
	
	@Override
	public void visitPercniv_nivel(Percniv percniv, Nivel nivel)
			throws SQLException {
		
		String description = percniv.getDescom();
		
		PaymentType paymetType = 
			mysqlDB.getPaymentType( description, 
								percniv.getDinesp(), 
								percniv.getTipcot());
		
		Concept<PaymentType> concept = 
			myConcepts.getConcept(percniv.getCodcom());
		
		String exprFormat = getExprFormat(percniv);

		if ( concept == null ){
			mysqlDB.error("percniv[{}] : Not found concept {} ", percniv.getCdg(), percniv.getCodcom());
			return ;
		}
		
		if ( paymetType == concept.type ){
			paymetType = null;
		}

		if ( description != null && description.equals(concept.description)){
			description = null;
		}
		
		String tipCot = percniv.getTipcot();
		String irpf = null;
		String quote = null;
		if ( !tipCot.equals(concept.quote) ){
			String dinEsp = percniv.getDinesp();
			irpf = MyConcepts.getIrpfExprFormat(tipCot, dinEsp);
			quote = MyConcepts.getQuoteExprFormat(tipCot);
		}
		String variable = getAmountVariable ( percniv.getCodcom() );
		String amount = DefaultMysqlDB.format(exprFormat, variable );
		
		
		SalaryType salaryType = SalaryType.SALARY;
		if ( "P".equals(percniv.getIndcom()) &&
				"6".equals(percniv.getCalculo()) )
		{
			salaryType = SalaryType.EXTRA;
		}
		
		Short month = MyConcepts.getMonth(percniv.getMes());
		if ( month != null ) {
			quote = myConcepts.getPorQuote(percniv.getRedext(), 
					MyConcepts.getQuoteExprFormat(tipCot) );
		}
		

		mysqlDB.insertAgreement_level_payment(
				this.level, 
				DefaultMysqlDB.enum2short(paymetType), 
				amount,
				description,
				startDate,
				null,
				month,
				concept.id ,
				enum2short(salaryType),
				(short) 1,
				DefaultMysqlDB.format ( irpf, concept.code ),
				DefaultMysqlDB.format ( quote, concept.code ));
		
		
		BigDecimal data = percniv.getImporte();
		if ( data != null && data.doubleValue() != 0.00  ) {
			mysqlDB.insertAgreement_level_data(
					variable, 
					this.level, 
					String.format("%.3f", data ), 
					this.startDate, 
					null);
			List<String>  codComs = DefaultMysqlDB.get(agreementPayments, nivel.getCodcon(), nivel.getCdg() );
			if ( codComs != null ) {
				codComs.add(percniv.getCodcom());
			}

			double garilt = toDouble(percniv.getGarilt()); 
			
			if ( garilt > 0.00 ) {
				String grtzdo = 
					myConcepts.getGrtzdoExprFormat(percniv.getCalculo(), garilt/100 );
				if ( grtzdo  != null ){
					mysqlDB.insertAgreement_level_data(
							getGtzdoVariable( percniv.getCodcom() ), 
							this.level, 
							DefaultMysqlDB.format(grtzdo, variable ), 
							this.startDate, 
							null);
				}
			}
		}
		
	}

	private String getExprFormat(Percniv percniv) 
	throws SQLException {

		String calculo =  percniv.getCalculo();
		String indCom = percniv.getIndcom();
		String comApl = percniv.getCodcomapl();
		
		return myConcepts.getExprFormat(calculo, indCom, comApl);
	}
	
	
	
	private double toDouble(BigDecimal bigDecimal) {
		return bigDecimal != null  ? bigDecimal.doubleValue() : 0 ;
	}
	
	
}
