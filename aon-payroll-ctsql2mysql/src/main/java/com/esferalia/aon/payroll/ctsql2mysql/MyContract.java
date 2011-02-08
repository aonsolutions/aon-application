package com.esferalia.aon.payroll.ctsql2mysql;

import java.math.BigDecimal;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;



import com.code.aon.common.util.CommonUtil;
import com.code.aon.employee.enumeration.ContractStatus;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Complemento;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Empresa;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Emprper;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Nomdto;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Nomdtoex;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Nomina;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Nominadev;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Nominaex;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Percep;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Trabajo;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Trabdto;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Trabinci;
import com.esferalia.aon.salary.enumeration.DeductionType;
import com.esferalia.aon.salary.enumeration.PaymentType;
import com.esferalia.aon.salary.enumeration.SalaryType;

import static com.code.aon.employee.calculator.ContractSalaryCalculator.*;

import static com.esferalia.aon.payroll.ctsql2mysql.DefaultMysqlDB.enum2short;

public class MyContract extends DefaultCtsqlDBVisitor {
	
	final static String PASSWORD  			= "demo";

	private static final Map<String, String>QUOTE_EXPRESSIONS = 
		new HashMap<String, String>() {
		{
			put ( "1", AMOUNT );
			put ( "2", "0" );
			put ( "3", AMOUNT + " - (IPREM * 20/100)" );
			put ( "4", "0" );
			put ( "5", AMOUNT );
			put ( "6", AMOUNT );
			put ( "7", AMOUNT );
		}
	};
	
	private static final Map<String, String>IRPF_EXPRESSIONS = 
		new HashMap<String, String>() {
		{
			put ( "1", AMOUNT );
			put ( "2", AMOUNT );
			put ( "3", AMOUNT );
			put ( "4", "0" );
			put ( "5", AMOUNT );
			put ( "6", AMOUNT );
			put ( "7", AMOUNT );
		}
	};
	
	private static class Concept<T>{
		private Integer id;
		private String code;
		private T type;
		
		
		public Concept(Integer id, String code, T type) {
			this.id = id;
			this.code = code;
			this.type = type;
		}
	}
	
	private static class ContractData {
		
		private java.sql.Date	endDate;
		private java.sql.Date 	startDate;
	
		private String 	porCot;
		
		public ContractData(java.sql.Date startDate,
							java.sql.Date endDate,
							String porCot) 
		{
			this.startDate = startDate;
			this.endDate = endDate;
			this.porCot = porCot;
		}
	}
	
	private Integer 		salaryId;
	private Integer 		contractId;
	
	
	private Date 			fromDate;
	private MyPerson 		myPerson;
	private MyEnterprise 	myEnterprise;
	private DefaultMysqlDB 	mysqlDB;
	
	private Integer 		irpfConceptId;

	private List<ContractData> contractDatas;
	private Map<String, Concept<PaymentType>> paymentConcepts ;
	

	public MyContract(DefaultMysqlDB mysqlDB, MyEnterprise myEnterprise, MyPerson myPerson) {
		this ( mysqlDB, myEnterprise, myPerson, null );
	}

	public MyContract(DefaultMysqlDB mysqlDB, MyEnterprise myEnterprise, MyPerson myPerson, Date fromDate) {
		this.mysqlDB = mysqlDB;
		this.fromDate = fromDate;
		this.myPerson = myPerson;
		this.myEnterprise = myEnterprise;
		this.contractDatas = new LinkedList<ContractData>();
		this.paymentConcepts = new HashMap<String, Concept<PaymentType>>();
	}

	private boolean outOfDate ( Date date ) {
		if ( date == null )
			return false;
		if ( fromDate == null )
			return false;
		return fromDate.compareTo(date) > 0 ; 
	}
	
	private boolean is9999 ( Date date ) {
		if ( date == null )
			return false;
		int year =  date.getYear() + 1900;
		return year == 9999; 
	}
	private java.sql.Date getEndDate(java.sql.Date fecFin, Emprper emprper) throws SQLException{
		return is9999(fecFin)? emprper.getFecbaj(): fecFin;
	}

	private boolean isActive( Emprper emprper ) 
	throws SQLException {
		Date fecbaj = emprper.getFecbaj();
		if ( fecbaj == null )
			return true;
		Date today = Calendar.getInstance().getTime();
		return today.compareTo(fecbaj ) < 0;
	}
	
	private Pattern kindPattern = 
		Pattern.compile("ESPECIE", Pattern.CASE_INSENSITIVE);
	private Pattern advancePattern = 
		Pattern.compile("ANTICIPO", Pattern.CASE_INSENSITIVE);
	
	private DeductionType getDeductionType(String description) {

		DeductionType deductionType = null;
		
		if (advancePattern.matcher(description).find()) {
			deductionType = DeductionType.ADVANCE_PAYMENT;
		}if (kindPattern.matcher(description).find()) {
			deductionType = DeductionType.IN_KIND;
		}else {
			deductionType = DeductionType.OTHER;
		}
		
		return deductionType;
	}
	
	private double toDouble(BigDecimal bigDecimal) {
		return bigDecimal != null  ? bigDecimal.doubleValue() : 0 ;
	}

	private double toDouble(BigDecimal... bigDecimals) {
		double result = 0;
		for (BigDecimal bigDecimal : bigDecimals) {
			if ( bigDecimal != null  ) {
					result += bigDecimal.doubleValue();
			}
		}
		return result;
	}
	
	private void addContractData(ContractData contractData) {
		contractDatas.add(contractData);
	}
	
	private boolean before ( java.sql.Date oneDate, java.sql.Date anotherDate ) {
		if ( anotherDate == null ) 
			return true;
		if ( oneDate == null ) 
			return false;
		return oneDate.compareTo(anotherDate) <= 0;
	}

	private boolean after( java.sql.Date oneDate, java.sql.Date anotherDate ) {
		if ( oneDate == null ) 
			return true;
		if ( anotherDate == null ) 
			return false;
		return oneDate.compareTo(anotherDate) >= 0;
	}
	
	private java.sql.Date earlier ( java.sql.Date oneDate, java.sql.Date anotherDate ) {
		return before(oneDate, anotherDate) ? oneDate: anotherDate;
	}
	
	private java.sql.Date last( java.sql.Date oneDate, java.sql.Date anotherDate ) {
		return after(oneDate, anotherDate) ? oneDate: anotherDate;
	}

	private List<ContractData> getContractData(java.sql.Date startDate, java.sql.Date endDate) {
		List<ContractData> list = 
			new LinkedList<ContractData>();
		
		for (ContractData contractData : contractDatas) {
			if ( after ( contractData.endDate, startDate ) && 
					before(contractData.startDate, endDate ) ) {
				list.add (new ContractData(
						last(startDate, contractData.startDate), 
						earlier(endDate, contractData.endDate), 
						contractData.porCot));
			}
		}

		return list;
	}
	
	@Override
	public void visit(AbstractCtsqlDB ctsqlDB) throws SQLException {
		this.irpfConceptId = 
			mysqlDB.getDeductionConceptId(IRPF_CODE);
		ctsqlDB.visitComplemento(this);
		ctsqlDB.visitEmprper(this);
	}
	
	private String getCode(String cdg) {
		return String.format("P%s", cdg );
	}
	
	@Override
	public void visitComplemento(Complemento complemento) throws SQLException {
		String code = getCode ( complemento.getCdg() );
		String description = complemento.getDescripcion() ;
		if ( description == null ) {
			description = complemento.getDesabr();
		}
		PaymentType type = mysqlDB.getPaymentType(description, 
				complemento.getDinesp(), complemento.getTipcot());
		Integer paymentConcept = 
			mysqlDB.insertPayment_concept(code, description, enum2short(type) );
		paymentConcepts.put(complemento.getCdg() , new Concept<PaymentType>(paymentConcept, code, type));
	}

	@Override
	public void visitEmprper(Emprper emprper)
			throws SQLException {
		
		if ( outOfDate(emprper.getFecbaj()))
			return ;
		
		Integer workplace = 
			myEnterprise.getWorkplace(emprper.getCodemp(), emprper.getDomicilio());
		if ( workplace == null ){
			mysqlDB.error("emprper[{}] : Not found workplace for {}/{}", 
					emprper.getCdg(), emprper.getCodemp(), emprper.getDomicilio());
			return ;
		}
		Integer person =
			myPerson.getPerson( emprper.getCodper() );
		if ( person == null ){
			mysqlDB.error("emprper[{}] : Not found person {}", 
					emprper.getCdg(), emprper.getCodper());
			return ;
		}
		Integer ccc = 
			myEnterprise.getCCC(emprper.getCodact(), emprper.getCodccc());
		if ( ccc == null ){
			mysqlDB.error("emprper[{}] : Not found CCC {}/{}", 
					emprper.getCdg(), emprper.getCodact(), emprper.getCodccc());
			return ;
		}
		
		this.contractId = 
			mysqlDB.insertContract(person, 
					workplace, 
					ccc, 
					emprper.getFecalt(), 
					emprper.getFecbaj(),
					null,					// TODO: ¿ Calendar ?
					null,
					null,
					enum2short(ContractStatus.PROCESSED));
		
		
		String numDoc = 
			myPerson.getNumDoc(emprper.getCodper());
		
		Integer enterprise = 
			myEnterprise.getEnterprise(emprper.getCodemp());
		
		if ( isActive( emprper )) {
			mysqlDB.insertUser(myPerson.getName(emprper.getCodper()), 
					numDoc, 
					enterprise, 
					person, 
					true, 
					PASSWORD);
		}
		contractDatas.clear();
		emprper.visitTrabajo_emprper(this);
		emprper.visitRel_pcp_epp(this);
		emprper.visitRel_dto_per(this);
	
		emprper.visitRel_nom_per(this);
		emprper.visitRel_pex_per(this);
		//emprper.visitRel_fin_epp(this);
		
		emprper.visitTrabinci_emprper(this);
	}
	
	private String getIRPFExpression(BigDecimal percentage) {
		return String.format("%s * %.2f / 100", 
				IRPF_BASE, percentage != null ? percentage : 0.00 );
	}
	
	@Override
	public void visitTrabajo_emprper(Trabajo trabajo, Emprper emprper )
			throws SQLException {
		
		if ( outOfDate( trabajo.getFecfin()))
			return ;

		Integer person = myPerson.getPerson(emprper.getCodper());

		if ( person == null ){
			mysqlDB.error("emprper[{}] : Not found person {}", 
					emprper.getCdg(), emprper.getCodper());
			return ;
		}
		
		String tc2 = trabajo.getCodtc2();
		if ( tc2 == null ){
			mysqlDB.error("emprper[{}] : Not found TC2 {}", 
					emprper.getCdg(), tc2);
			return ;
		}
		
		
		String conditions = null;
		/*
		Colectivos colectivos = null ; //trabajo.getRel_tra_col();
		if ( colectivos != null ) {
			conditions = colectivos.getDescripcion();
		}*/
		
		java.sql.Date endDate = 
			getEndDate(trabajo.getFecfin(), emprper);
		
		java.sql.Date startDate =
			trabajo.getFecini();
			
		
		String description = trabajo.getDestc2();
		
		mysqlDB.insertContract_data(
				this.contractId, 
				trabajo.getCodtc2(), 
				description, 
				conditions, 
				trabajo.getFecini(), 
				endDate,
				trabajo.getCodbas(),
				trabajo.getDescat(),
				trabajo.getNummat(),
				trabajo.getFecant());
		
		BigDecimal irpf = trabajo.getIrpf();
		/*
		String irpfFunction = getIRPFExpression(irpf);
		
		mysqlDB.insertContract_deduction(
				enum2short(DeductionType.IRPF),
				this.irpfConceptId,
				this.contractId, 
				null, 
				(short) 1,
				irpfFunction, 
				trabajo.getFecini(), 
				endDate,
				null );
		*/
		mysqlDB.insertContract_context(IRPF_PERCENT, 
				this.contractId, 
				String.format("%.2f",irpf), 
				trabajo.getFecini(), 
				endDate);
		
		
		ContractData contractData = 
			new ContractData(startDate, endDate, trabajo.getProcot());
		addContractData(contractData);
	}
	
	@Override
	public void visitRel_dto_per(Trabdto trabdto, Emprper emprper) throws SQLException {

		if ( outOfDate( trabdto.getFecfin()))
			return ;
		
		String concepto = trabdto.getConcepto();
		
		DeductionType type = getDeductionType(concepto);

		BigDecimal importe = trabdto.getImporte();
		String function = String.format("%.3f", 
				importe != null ? importe : 0);
		
		
		java.sql.Date endDate = 
			getEndDate(trabdto.getFecfin(), emprper);

		mysqlDB.insertContract_deduction(enum2short(type), 
				null,
				this.contractId, 
				concepto, 
				(short) 0,
				function, 
				trabdto.getFecini(), 
				endDate,
				(short) 0 );
	}

	@Override
	public void visitRel_nom_per(Nomina nomina, Emprper emprper) throws SQLException {
		
		if ( outOfDate( nomina.getFecfin()))
			return ;
		
		
		Double totalPayment = toDouble(nomina.getTotal_devengos()); 

		Double itBase = toDouble(nomina.getBase_it());
		Double proExtBase = toDouble(nomina.getBase_proext());
		Double renumeration = toDouble(nomina.getRemuneracion());
		Double rawCgcBase = toDouble(nomina.getTotal_1());
		Double cgcBase = toDouble(nomina.getBase_cg());

		Double hextraBase = toDouble(nomina.getBase_hextras() );
		Double nonHextraBase = toDouble(nomina.getBase_hextras_no() );

		Double cgpBase = toDouble(nomina.getBase_acc());
		
		Double ssContributions = toDouble(nomina.getImporte_cuotas());

		Double irpfBase = toDouble(nomina.getBase_irpf());
		
		Double totalDeduction = toDouble(nomina.getTotal_deducir());

		Double totalLiquid = toDouble(nomina.getTotal_liquido());
		Double totalEnterprise = toDouble(nomina.getCuota_empresa());
		
		Rel_epp_per  rel_epp_per = new Rel_epp_per(); 
		emprper.visitRel_epp_per(rel_epp_per);
		
		Rel_epp_emp rel_epp_emp = new Rel_epp_emp();
		emprper.visitRel_epp_emp(rel_epp_emp);
		
		Rel_epp_ccc rel_epp_ccc = new Rel_epp_ccc();
		emprper.visitRel_epp_ccc(rel_epp_ccc);
		
		
		SalaryType type = SalaryType.SALARY;
		
		if ( "A".equals(nomina.getTipo() ) ) {
			type = SalaryType.DELAY;
		}
		
		this.salaryId= 
			mysqlDB.insertSalary(
					enum2short(type),
					this.contractId, 
					nomina.getFecini(), 
					nomina.getFecfin(),
					rel_epp_emp.getEmprnif_Descripcion(),
					nomina.getLocalidad(),
					rel_epp_emp.getEmprnif_Numdoc(),
					rel_epp_ccc.getEmprccc_Descripcion(),
					nomina.getNomper(),
					rel_epp_per.getPersona_Numss(),
					rel_epp_per.getPersona_Numdoc(),
					nomina.getFecant(),
					nomina.getCodbas(),
					nomina.getDescat(), 
					nomina.getNummat(), 
					nomina.getDiasnomina(), // TODO: Dias efectivos .. 
					totalPayment, 
					totalDeduction, 
					totalLiquid,
					totalEnterprise, 
					nomina.getFecemi(), 
					renumeration, 
					proExtBase, 
					itBase,
					rawCgcBase,
					cgcBase,
					hextraBase,
					nonHextraBase,
					cgpBase,
					irpfBase,
					ssContributions);
		
		Double importeCg = toDouble(nomina.getImporte_cg());
		if ( importeCg > 0 ) {
			BigDecimal cgPercentage = 
				nomina.getPrc_cg();
			String cgFunction = String.format("%.2f%%", 
					cgPercentage != null ? cgPercentage : 0);
			mysqlDB.insertSalary_deduction(this.salaryId, 
					enum2short(DeductionType.COMMON_CONTINGENCY), 
					null,
					null, 
					cgFunction, 
					importeCg);
		}

		Double importeAcc = toDouble(nomina.getImporte_acc());
		
		if ( importeAcc > 0  ){ 
			
			double accPercentage = toDouble(nomina.getPrc_acc());
			
			if ( accPercentage == 1.65 ||  accPercentage == 1.70 ) {
				double jobPercentage = 0.10;
				double uePercentage = accPercentage - jobPercentage; 
				
				double importeJob = jobPercentage * importeAcc / accPercentage;
				String jobFunction = String.format("%.2f%%", jobPercentage);
				mysqlDB.insertSalary_deduction(this.salaryId, 
						enum2short(DeductionType.JOB_TRAINING),
						null,
						null, 
						jobFunction, 
						importeJob);

				String ueFunction = String.format("%.2f%%", uePercentage);
				mysqlDB.insertSalary_deduction(this.salaryId, 
						enum2short(DeductionType.UNEMPLOYMENT),
						null,
						null, 
						ueFunction, 
						importeAcc - importeJob);
			}
			else {
				String accFunction = String.format("%.2f%%", accPercentage);
				mysqlDB.insertSalary_deduction(this.salaryId, 
						enum2short(DeductionType.PROFESSIONAL_CONTINGENCY),
						null,
						null, 
						accFunction, 
						importeAcc);
			}
		}
		
		Double importeHex = toDouble(nomina.getImporte_hex());
		if ( importeHex > 0 ) {
			BigDecimal hexPercentage = 
				nomina.getPrc_hex();
			String hexFunction = String.format("%.2f%%", 
					hexPercentage != null ? hexPercentage : 0);
			mysqlDB.insertSalary_deduction(this.salaryId, 
					enum2short(DeductionType.STRUCTURAL_OVERTIME), 
					null, 
					null,
					hexFunction, 
					importeHex);
		}

		Double importeHexNo = toDouble(nomina.getImporte_hexno());
		if ( importeHexNo > 0 ) {
			BigDecimal hexNoPercentage = 
				nomina.getPrc_hexno();
			String hexNoFunction = String.format("%.2f%%", 
					hexNoPercentage != null ? hexNoPercentage : 0);
			mysqlDB.insertSalary_deduction(this.salaryId, 
					enum2short(DeductionType.NON_STRUCTURAL_OVERTIME), 
					null,
					null,
					hexNoFunction, 
					importeHexNo);
		}


		Double importeIrpf = toDouble(nomina.getImporte_irpf());
		
		if ( importeIrpf > 0 ){ 
			BigDecimal irpfPercentage = 
				nomina.getPrc_irpf();
			String irpfFunction = String.format("%.2f%%", 
					irpfPercentage != null ? irpfPercentage : 0);
			mysqlDB.insertSalary_deduction(this.salaryId, 
					enum2short(DeductionType.IRPF), 
					null,
					null,
					irpfFunction, 
					importeIrpf);
		}
		
		nomina.visitRel_nmd_nom(this);
		nomina.visitRel_dto_nom(this);
	}
	
	
	private Short getMonth(Integer mes ) {
		if ( mes == null )
			return null;
		if ( mes == 0 )
			return null;
		Integer month = mes - 1 ;
		return month.shortValue();
	}
	
	
	
	private String getExpression(Percep percep) 
	throws SQLException {
		String calculo =  percep.getCalculo();
		BigDecimal importe = percep.getImporte();
		
		if (calculo.equals("1")) {
			return String.format("%.3f * %s / %s", 
					importe , WORKED_DAYS , MONTH_DAYS );
		}else if (calculo.equals("2")) {
			return String.format("%.3f * %s ", 
					importe , WORKED_DAYS );
		}else if (calculo.equals("3")) {
			return String.format("%.3f * %s ", 
					importe , ACTUAL_DAYS );
		}else if (calculo.equals("4")) {
			return String.format("%.3f * %s ", 
					importe , SPECIAL_DAYS);
		}else if (calculo.equals("5")) {
			String concept = getCode(percep.getComapl());
			return String.format("%s * %.2f / 100 ", 
					concept , importe );
		}else if (calculo.equals("6")) {
			if ("V".equals(percep.getIndcom())) { 
				return String.format("%.3f * %s / %s", 
						importe , HOLIDAYS, MONTH_DAYS );
			}
		}else if (calculo.equals("7")) {
			return String.format("%s * %.2f / 100 ", 
					SENIOR_BASE, importe );
		}
		
		return String.format("%.3f", importe );
	}
	
	private String getPorQuote(String porCot , String expr) {
		return "M".equalsIgnoreCase(porCot) ? 
				expr.replace(AMOUNT, "(" + AMOUNT + "/12)") : 
					expr.replace(AMOUNT, "(" + AMOUNT + "*" + WORKED_DAYS + "/" + YEAR_DAYS + ")");
	}

	@Override
	public void visitRel_pcp_epp(Percep percep, Emprper emprper)
			throws SQLException {
		
		if ( outOfDate( percep.getFecfin()))
			return ;

		Concept<PaymentType> concept = 
			getPaymentConcept(percep.getCodcom(), percep.getDescom(), percep.getDinesp());
		
		Short month = getMonth(percep.getMes()) ;

		java.sql.Date endDate = 
			getEndDate(percep.getFecfin(), emprper);
		
		String script = getExpression(percep);
		
		String tipcot = percep.getTipcot();
		
		String irpf = IRPF_EXPRESSIONS.get(tipcot);
		
		String quote = QUOTE_EXPRESSIONS.get(tipcot);
		
		if ( "P".equals(percep.getIndcom()) &&
				"6".equals(percep.getCalculo()) )
		{
			
			java.sql.Date startDate = percep.getFecini();
			List<ContractData> datas = 
				getContractData(startDate, endDate);
			
			String porCot = null;
			String porQuote  = null;
			for (ContractData data : datas) {
				
				if ( porCot == null || porCot.equals(data.porCot)){
					porCot = data.porCot;
					endDate = data.endDate;
					continue;
				}
				
				porQuote = getPorQuote(porCot, quote);
				
				mysqlDB.insertContract_payment(
						enum2short(PaymentType.SALARY_SUPPLEMENTS),
						this.contractId, 
						concept.id,
						percep.getDescom(), 
						(short) 0,
						script, 
						irpf,
						porQuote,
						startDate, 
						month, 
						endDate,
						enum2short(SalaryType.EXTRA));
				
				porCot = data.porCot;
				startDate = data.startDate;
			}
			porQuote = getPorQuote(porCot, quote);
			mysqlDB.insertContract_payment(
					enum2short(PaymentType.SALARY_SUPPLEMENTS),
					this.contractId, 
					concept.id,
					percep.getDescom(), 
					(short) 0,
					script, 
					irpf,
					porQuote,
					startDate, 
					month, 
					endDate,
					enum2short(SalaryType.EXTRA));
		}
		else {
			PaymentType type = 
				mysqlDB.getPaymentType(percep.getDescom(), 
						percep.getDinesp(),
						percep.getTipcot());
			
			if ( type == PaymentType.SALARY_IN_KIND ){
				String ingEspEmp = 
					myEnterprise.getIngEspEmp(emprper.getCodact());
				if ( "S".equalsIgnoreCase(ingEspEmp ) ){
					irpf = "0";
				}
			}
			
			mysqlDB.insertContract_payment(
					enum2short(type), 
					this.contractId, 
					concept.id,
					percep.getDescom(), 
					(short) 0,
					script, 
					irpf,
					quote,
					percep.getFecini(), 
					null,
					endDate,
					enum2short(SalaryType.SALARY));
		}
	}
	
	
	private Concept<PaymentType> getPaymentConcept(String codCom, String description, String dinEsp) {

		Concept<PaymentType> concept = 
			paymentConcepts.get(codCom);
		
		if ( concept != null ) { 
			return concept;
		}
		
		String code = null;
		if ( codCom != null ) {
			code = getCode( codCom);
		}
		
		PaymentType type = 
			mysqlDB.getPaymentType(description, dinEsp, null );
		
		concept = 
			new Concept<PaymentType>(null, code, type);
		
		return concept;
	}

	@Override
	public void visitRel_nmd_nom(Nominadev nominadev, Nomina nomina)
			throws SQLException {
		
		String description = 
			nominadev.getDescom();
		String codCom = 
			nominadev.getCodcom();
		String dinEsp = 
			nominadev.getDinesp();

		Concept<PaymentType> concept = 
			getPaymentConcept(codCom, description, dinEsp);
		
		BigDecimal importe = nominadev.getImporte();
		BigDecimal impuni = nominadev.getImpuni();
		BigDecimal unidades = nominadev.getUnidades();
		
		String function = mysqlDB.getFunction(importe, impuni, unidades);
		
		mysqlDB.insertSalary_payment(this.salaryId, 
				enum2short(concept.type), 
				concept.code,
				description, 
				function,
				importe != null ? importe.doubleValue() : 0.00 );
		
	}
	
	
	@Override
	public void visitRel_dto_nom(Nomdto nomdto, Nomina nomina) throws SQLException {

		String concepto = nomdto.getConcepto();
		
		DeductionType type = getDeductionType(concepto);

		Double importe = toDouble(nomdto.getImporte());

		String function = String.format("%.3f", 
				importe != null ? importe : 0);
		
		
		mysqlDB.insertSalary_deduction(
				this.salaryId, 
				enum2short(type),
				null,
				concepto, 
				function, 
				importe);
	}
	

	@Override
	public void visitRel_pex_per(Nominaex nominaex, Emprper emprper) throws SQLException {
		
		if ( outOfDate( nominaex.getFecfin()))
			return ;
		
		Double totalPayment = toDouble(nominaex.getImporte()); 
		Double totalDeduction = toDouble(nominaex.getTotal_deducir());
		Double totalLiquid = toDouble(nominaex.getLiquido());
		
		Double baseIRPF = totalPayment;
		

		Rel_epp_per  rel_epp_per = new Rel_epp_per(); 
		emprper.visitRel_epp_per(rel_epp_per);
		
		Rel_epp_emp rel_epp_emp = new Rel_epp_emp();
		emprper.visitRel_epp_emp(rel_epp_emp);
		
		Rel_epp_ccc rel_epp_ccc = new Rel_epp_ccc();
		emprper.visitRel_epp_ccc(rel_epp_ccc);

		this.salaryId= 
			mysqlDB.insertSalary(
					enum2short(SalaryType.EXTRA),
					this.contractId, 
					nominaex.getFecini(), 
					nominaex.getFecfin(), 
					rel_epp_emp.getEmprnif_Descripcion(),
					nominaex.getLocalidad(), 
					rel_epp_emp.getEmprnif_Numdoc(),
					rel_epp_ccc.getEmprccc_Descripcion(),
					nominaex.getNomper(),
					rel_epp_per.getPersona_Numss(),
					rel_epp_per.getPersona_Numdoc(),
					nominaex.getFecant(),
					null,
					nominaex.getDescat(), 
					nominaex.getNummat(), 
					0, 					// TODO: Dias efectivos .. 
					totalPayment, 
					totalDeduction, 
					totalLiquid,
					0.00,
					nominaex.getFecemi(), 
					0.00, 
					0.00, 
					0.00, 
					0.00, 
					0.00,
					0.00, 
					0.00, 
					0.00, 
					baseIRPF,
					0.00);
		
		String function = String.format("%.2f%%", 
				totalPayment != null ? totalPayment : 0);
		
		Concept<PaymentType> concept = 
			getPaymentConcept(nominaex.getCodcom(), nominaex.getDescom(), "D");
		
		mysqlDB.insertSalary_payment(this.salaryId, 
				enum2short(concept.type), 
				concept.code,
				nominaex.getDescom(),
				function, 
				totalPayment);
		
		Double importeIrpf = toDouble(nominaex.getImpirpf());
		
		if ( importeIrpf > 0 ){ 
			BigDecimal irpfPercentage = 
				nominaex.getIrpf();
			String irpfFunction = String.format("%.2f%%", 
					irpfPercentage != null ? irpfPercentage : 0);
			mysqlDB.insertSalary_deduction(this.salaryId, 
					enum2short(DeductionType.IRPF), 
					null, 
					null,
					irpfFunction, 
					importeIrpf);
			
		}

		
		nominaex.visitNomdtoex_nominaex(this);
		
		return ;
	}
	
	
	@Override
	public void visitNomdtoex_nominaex(Nomdtoex nomdtoex, Nominaex nominaex)
			throws SQLException {
		String concepto = nomdtoex.getConcepto();
		
		DeductionType type = getDeductionType(concepto);

		Double importe = toDouble(nomdtoex.getImporte());

		String function = String.format("%.3f", 
				importe != null ? importe : 0);
		
		
		mysqlDB.insertSalary_deduction(
				this.salaryId, 
				enum2short(type),
				null,
				concepto, 
				function, 
				importe);
	}
	
	@Override
	public void visitTrabinci_emprper(Trabinci trabinci, Emprper emprper)
			throws SQLException {

		if ( outOfDate( trabinci.getFecfin()))
			return ;
		
		String name = null ;
		String expression = null;
		String codinc = trabinci.getCodinc();
		if ( codinc.contains("EFECTIVOS")) {
			name = ACTUAL_DAYS;
			expression = String.format("%d", trabinci.getCantidad());
		}else if ( codinc.contains("ESPECIALES")) {
			name = SPECIAL_DAYS;
			expression = String.format("%d", trabinci.getCantidad());
		}else {
			name = codinc;
		}

		java.sql.Date endDate = 
			getEndDate(trabinci.getFecfin(), emprper);
		
		mysqlDB.insertContract_context(name, 
				this.contractId, 
				expression, 
				trabinci.getFecini(), 
				endDate);
	}


}
