package com.esferalia.aon.payroll.ctsql2mysql;


import static com.esferalia.aon.payroll.ctsql2mysql.DefaultMysqlDB.enum2short;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.esferalia.aon.calendar.enumeration.DayType;
import com.esferalia.aon.payroll.calculator.QuoteCalculator.GeneralQuote;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Categoria;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Convenio;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Emprper;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Nivel;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Pagaext;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Percep;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Percniv;
import com.esferalia.aon.payroll.ctsql2mysql.DefaultMysqlDB.CustomQuoteExpressionException;
import com.esferalia.aon.payroll.ctsql2mysql.IConcepts.Concept;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.salary.enumeration.PaymentType;
import com.esferalia.aon.salary.enumeration.SalaryType;


public class MyAgreement extends DefaultCtsqlDBVisitor implements IAgreements {
	
	
	private static java.sql.Date START_DATE = 
		new java.sql.Date(110,01,01) ; 

	public static String getAmountVariable(String code) {
		return  MyConcept.formatCode(code) + "_IMPORTE";
	}
	
	public static String getGtzdoVariable(String code) {
		return  MyConcept.formatCode(code) + "_GARANTIZADO";
	}

	private  int 	level;
	private int 	agreement;
	java.sql.Date 	startDate ;
	
	private IConcepts concepts;
	private DefaultMysqlDB mysqlDB;
	
	protected AbstractCtsqlDB ctsqlDB;

	private Map<String, Integer>	agreements;
	private Map<String, Map<String, Integer>>	levels;
	private Map<String, Map<String, Map<String, String>>>	categories;
	
	
	private Map<String,Map<String,List<String>>> agreementPayments;
	
	private Map<Integer, Integer> agreementConcepts ; 
	private Map<String,Map<String,String>> paymentsLevels;
	private Map<String,Map<String,Map<String,BigDecimal>>> paymentsAmounts;
	
	private Map<String, List<String>> agreemetExtras;
	
	public MyAgreement(DefaultMysqlDB mysqlDB, IConcepts myConcepts) {
		this(mysqlDB, myConcepts, null);
	}
	public MyAgreement(DefaultMysqlDB mysqlDB, IConcepts concepts, Date startDate ) {
		this.mysqlDB = mysqlDB;
		this.concepts = concepts;
		this.startDate = startDate == null ? START_DATE : new java.sql.Date(startDate.getTime());
		this.levels = new HashMap<String, Map<String,Integer>>();
		this.categories = new HashMap<String, Map<String,Map<String,String>>>();
		this.agreements = new HashMap<String, Integer>();
		this.agreementPayments = new HashMap<String, Map<String,List<String>>>();
		
		this.agreementConcepts = new HashMap<Integer, Integer>();
		this.paymentsLevels = new HashMap<String, Map<String,String>>();
		this.paymentsAmounts = new HashMap<String, Map<String, Map<String,BigDecimal>>>();
		
		this.agreemetExtras = new HashMap<String, List<String>>();
	}
	
	public void init(AbstractCtsqlDB ctsqlDB) throws SQLException {
		this.ctsqlDB = ctsqlDB;
	}
	
	
	@Override
	public void visit(AbstractCtsqlDB ctsqlDB) throws SQLException {
		init(ctsqlDB);
		ctsqlDB.visitConvenio(this);
	}
	
	@Override
	public Integer getAgreement(String oldCdg) {
		return agreements.get(oldCdg);
	}
	
	@Override
	public Integer getAgreementLevel(String codCon, String oldCdg) {
		return DefaultMysqlDB.get(levels, codCon, oldCdg);
	}
	
	@Override
	public String getAgreementCategory(String codCon, String nivel, String oldCdg) {
		return DefaultMysqlDB.get(categories, codCon, nivel, oldCdg);
	}
	
	@Override
	public boolean containsExtra(String oldCdg, String codCom ) {
		List<String> extras =  agreemetExtras.get(oldCdg);
		return extras != null ? extras.contains(codCom) : false;
	}
	
	@Override
	public String insertAgreementCategory(String codCon, String nivel, String oldCdg) 
	throws SQLException {
		Integer agreementLevel =getAgreementLevel(codCon, nivel) ;
		if ( agreementLevel == null )
			return null ;
		
		String descripcion = String.format("Categoria %s", oldCdg);
		
		Integer agreementCategory = 
			mysqlDB.insertAgreement_level_category(agreementLevel, descripcion);
		
		DefaultMysqlDB.save(categories, codCon, nivel, oldCdg, descripcion /*agreementCategory*/ );
		
		return descripcion;
	}

	
	public static class PercepPercnivComparator extends DefaultCtsqlDBVisitor {
		
		
		public static int NOT_FOUND 	= -2;
		public static int NOT_EQUALS 	= -1;
		public static int EQUALS 		= 0;
		public static int NOT_AMOUNT 	= 1;
		public static int NOT_GARILT 	= 1<<1;
		
		private Percep percep;
		private BigDecimal importe;
		private int compare = NOT_FOUND;
		
		public PercepPercnivComparator(Percep percep, BigDecimal importe) {
			this.percep = percep;
			this.importe = importe;
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
            
			if ( importe == null || ! importe.equals(percep.getImporte()) ){
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
	
	@Override
	public int hasPayment(String cdg, String nivel, String codcom,  Percep percep) 
	throws SQLException {
		
		String realNivel = DefaultMysqlDB.get(this.paymentsLevels, cdg, codcom);
		if ( realNivel == null ) {
			return PercepPercnivComparator.NOT_FOUND;
		}
		
		BigDecimal importe = DefaultMysqlDB.get(this.paymentsAmounts, cdg, nivel, codcom);

		PercepPercnivComparator comparator = 
			new PercepPercnivComparator(percep, importe );
		this.ctsqlDB.visitPercniv(cdg, realNivel, codcom, comparator);
		
		return comparator.compare;
	}
	
	@Override
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
		this.agreementConcepts.clear();
		this.agreement = mysqlDB.insertAgreement(null,	//TODO: ¿ Calendar ?  
				description);
		agreements.put(convenio.getCdg(), this.agreement);
		
		convenio.visitRel_niv_con(this);
		convenio.visitRel_cat_con(this);
		
		agreemetExtras.put(convenio.getCdg(), 
				new LinkedList<String>());
		convenio.visitRel_pga_con(this);
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
			MysqlDB.error("categoria[{}]: Not found nivel retributivo {} ", 
					categoria.getCdg(), categoria.getNivel() );
			return;
		}
		String description = categoria.getDescripcion();
		int categoryId =
			mysqlDB.insertAgreement_level_category(level, description);
		
		DefaultMysqlDB.save(categories, categoria.getCodcon(), categoria.getNivel(), categoria.getCdg(), description /*categoryId*/);
	}
	
	@Override
	public void visitRel_pga_con(Pagaext pagaext, Convenio convenio)
			throws SQLException {
		
		Concept<PaymentType> concept = 
			concepts.getPaymentConcept(pagaext.getCodcom());
		
		if ( concept == null ){
			MysqlDB.error("pagaext[{}] : Not found concept {} ", 
					pagaext.getCdg(), pagaext.getCodcom());
			return ;
		}

		agreemetExtras.get(convenio.getCdg()).add(pagaext.getCodcom()); // TODO: ??? Extras
		
		Integer agreementPayment =  
			agreementConcepts.get(concept.id);
		if ( agreementPayment == null ){
			MysqlDB.error("pagaext[{}] : Not found payment for concept {} in agreement {} ", 
					pagaext.getCdg(), pagaext.getCodcom(), pagaext.getCdg() );
			return ;
		}
		
		
		String perIni = pagaext.getPerini();
		String startDate = 
			String.format("%s/%s%s", 
					perIni.substring(0, 2), 
					perIni.substring(2,4),
					pagaext.getIndini().equals("1") ? " -1": "");
		
		
		String perFin = pagaext.getPerfin();
		String endDate = 
			String.format("%s/%s%s", 
					perFin.substring(0, 2), 
					perFin.substring(2,4),
					pagaext.getIndfin().equals("1") ? " -1": "");

		String fecCob = pagaext.getFeccob();
		String issueDate = 
			String.format("%s/%s", 
					fecCob.substring(0, 2), 
					fecCob.substring(2,4) );

		mysqlDB.insertAgreement_extra(
				agreement, 
				agreementPayment, 
				startDate, 
				endDate, 
				issueDate);

	}
	
	@Override
	public void visitPercniv_nivel(Percniv percniv, Nivel nivel)
			throws SQLException {
		
		String description = percniv.getDescom();
		
		PaymentType paymetType =
			mysqlDB.getPaymentType(percniv, PaymentType.CRA_0000);	
//			mysqlDB.getPaymentType( description, 
//								percniv.getDinesp(), 
//								percniv.getTipcot())
			;
		
		Concept<PaymentType> concept = 
			concepts.getPaymentConcept(percniv.getCodcom());
		
		String exprFormat = getExprFormat(percniv);

		if ( concept == null ){
			MysqlDB.error("percniv[{}] : Not found concept {} ", percniv.getCdg(), percniv.getCodcom());
			return ;
		}
		
		if ( paymetType == concept.type ){
			paymetType = null;
		}

		if ( description != null && description.equals(concept.description)){
			description = null;
		}
		
		String tipCot = percniv.getTipcot();
		String irpfFormat = null;
		String quoteFormat = null;
		if ( !tipCot.equals(concept.quote) ){
			String dinEsp = percniv.getDinesp();
			irpfFormat = MyConcept.getIrpfExprFormat(tipCot, dinEsp);
			quoteFormat = MyConcept.getQuoteExprFormat(tipCot);
		}
		String variable = getAmountVariable ( percniv.getCodcom() );
		String amount = DefaultMysqlDB.format(exprFormat, variable );
		
		SalaryType salaryType = SalaryType.SALARY;
		if ( MyAgreement.isExtra(percniv))
		{
			salaryType = SalaryType.EXTRA;
			amount = DefaultMysqlDB.format(exprFormat, MyConcept.getCurrent(variable));
		}
		

		String quote = null;

		Short month = MyConcept.getMonth(percniv.getMes());
		if ( month != null ) {
			quoteFormat = MyConcept.getPorQuote(percniv.getRedext(), 
										MyConcept.getQuoteExprFormat(tipCot));
			quote = DefaultMysqlDB.format ( quoteFormat, variable );
		} else {
			quote = DefaultMysqlDB.format ( quoteFormat, ContextVariable.PAYMENT /*concept.code*/  );
		}

		String irpf = DefaultMysqlDB.format ( irpfFormat, ContextVariable.PAYMENT /*concept.code*/ );
		
		
		try {
			irpf = quote = mysqlDB.getQuote(percniv, concept);
		} catch ( Exception e ) {
			
		}
		
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(startDate);
		calendar.set(Calendar.MONTH, 0);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		java.sql.Date agreementStartDate = new java.sql.Date(calendar.getTimeInMillis() );
		
		if ( ! this.agreementConcepts.containsKey(concept.id)) {
			Integer agreementpayment = 
				mysqlDB.insertAgreement_payment(
						agreement, 
						concept.id , 
						DefaultMysqlDB.enum2short(paymetType), 
						amount, 
						description, 
						agreementStartDate, 
						null, 
						month, 
						enum2short(salaryType), 
						(short) 1, 
						irpf, 
						quote);
			this.agreementConcepts.put(concept.id, agreementpayment);
			// para este nivel es el qeue manda .
			DefaultMysqlDB.save(this.paymentsLevels, percniv.getCdg(), percniv.getCodcom(), percniv.getNivel());
		}
		
		BigDecimal data = percniv.getImporte();
		if ( data != null && data.doubleValue() != 0.00  ) {
			mysqlDB.insertAgreement_level_data(
					variable, 
					this.level, 
					String.format("%.3f", data ), 
					agreementStartDate, 
					null);
			DefaultMysqlDB.save(this.paymentsAmounts, percniv.getCdg(), percniv.getNivel(), percniv.getCodcom(), data);
			List<String>  codComs = DefaultMysqlDB.get(agreementPayments, nivel.getCodcon(), nivel.getCdg() );
			if ( codComs != null ) {
				codComs.add(percniv.getCodcom());
			}
			
			
			double garilt = toDouble(percniv.getGarilt()); 
			
			if ( !MyAgreement.isExtra(percniv) && garilt > 0.00 ) {
				String grtzdo = 
					MyConcept.getGrtzdoExprFormat(percniv.getCalculo(), garilt/100 );
				if ( grtzdo  != null ){
					mysqlDB.insertAgreement_level_data(
							getGtzdoVariable( percniv.getCodcom() ), 
							this.level, 
							DefaultMysqlDB.format(grtzdo, variable ), 
							agreementStartDate, 
							null);
				}
			}
		}
		
	}

	public static String getExprFormat(Percniv percniv) 
	throws SQLException {
		String calculo =  percniv.getCalculo();
		String indCom = percniv.getIndcom();
		String comApl = percniv.getCodcomapl();
		String redExt = percniv.getRedext();
		double garilt = MysqlDB.toDouble(percniv.getGarilt());
		
		return MyConcept.getExprFormat(calculo, indCom, comApl, redExt, garilt);
	}
	
	
	
	private double toDouble(BigDecimal bigDecimal) {
		return bigDecimal != null  ? bigDecimal.doubleValue() : 0 ;
	}
	
	public static Boolean isExtra(Percniv percniv) throws SQLException{
		return ( "P".equals(percniv.getIndcom()) &&
				"6".equals(percniv.getCalculo()) );
		
	}
	
}
