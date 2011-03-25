package com.esferalia.aon.payroll.ctsql2mysql;

import java.math.BigDecimal;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.regex.Pattern;




import com.esferalia.aon.payroll.enumeration.ContractStatus;
import com.esferalia.aon.payroll.enumeration.LeaveType;
import com.esferalia.aon.payroll.enumeration.SSRegimeType;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Emprper;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Nomdto;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Nomdtoex;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Nomina;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Nominadev;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Nominaex;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Parteit;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Parteitnu;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Percep;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Trabajo;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Trabdto;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Trabinci;
import com.esferalia.aon.payroll.ctsql2mysql.MyConcepts.Concept;
import com.esferalia.aon.salary.enumeration.DeductionType;
import com.esferalia.aon.salary.enumeration.PaymentType;
import com.esferalia.aon.salary.enumeration.SalaryType;

import static com.esferalia.aon.payroll.enumeration.ContractVariables.*;

import static com.esferalia.aon.payroll.ctsql2mysql.DefaultMysqlDB.enum2short;

public class MyContract extends DefaultCtsqlDBVisitor {
	
	final static String PASSWORD  			= "demo";

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
	
	private class TrabajoAggregate extends DefaultCtsqlDBVisitor{
		
		@Override
		public void visitTrabajo_emprper(Trabajo trabajo, Emprper emprper)
				throws SQLException {
			
			java.sql.Date fecFin = trabajo.getFecfin();
			java.sql.Date fecBaja = emprper.getFecbaj();
			
			if ( fecFin == null || DefaultMysqlDB.is9999(fecFin) ||
				( fecBaja != null && fecFin.compareTo(fecBaja)>=0 ) ){
				MyContract.this.codCon = trabajo.getCodcon();
				MyContract.this.nivel = trabajo.getNivel();
				MyContract.this.agreementCategoryId = 
					MyContract.this.myAgreement.getAgreementCategory(trabajo.getCodcon(), trabajo.getNivel(), trabajo.getCodcat());
				if ( MyContract.this.agreementCategoryId == null ) {
					mysqlDB.error("trabajo[{}] : Unknow category  {}/{}/{}", 
							trabajo.getCdg(), trabajo.getCodcon(), trabajo.getNivel(), trabajo.getCodcat() );
					MyContract.this.agreementCategoryId = 
						MyContract.this.myAgreement.insertAgreementCategory(trabajo.getCodcon(), trabajo.getNivel(), trabajo.getCodcat());
					
				}
			}
		}
	}
	
	private Integer 		salaryId;
	private Integer 		contractId;
	private Integer 		agreementCategoryId;
	private String			codCon;
	private String			nivel;
	private Queue<String>	gtzdos;
	
	private Date 			fromDate;
	private MyPerson 		myPerson;
	private MyConcepts 		myConcepts;
	private MyEnterprise 	myEnterprise;
	private MyAgreement 	myAgreement;
	private MyCalendar 	myCalendar;
	private DefaultMysqlDB 	mysqlDB;
	
	private Integer 		irpfConceptId;

	private List<ContractData> contractDatas;
	private Map<Date, Integer> parteIts;
	
	private Integer registration ;
	private java.sql.Date seniorityDate ;
	

	
	public MyContract(DefaultMysqlDB mysqlDB, MyEnterprise myEnterprise, MyPerson myPerson, MyConcepts myConcepts, MyAgreement myAgreement, MyCalendar myCalendar) {
		this ( mysqlDB, myEnterprise, myPerson, myConcepts, myAgreement, myCalendar, null );
	}

	public MyContract(DefaultMysqlDB mysqlDB, MyEnterprise myEnterprise, MyPerson myPerson,  MyConcepts myConcepts, MyAgreement myAgreement, MyCalendar myCalendar, Date fromDate) {
		this.mysqlDB = mysqlDB;
		this.fromDate = fromDate;
		this.myPerson = myPerson;
		this.myConcepts = myConcepts;
		this.myAgreement = myAgreement;
		this.myEnterprise = myEnterprise;
		this.myCalendar = myCalendar;
		this.contractDatas = new LinkedList<ContractData>();
		this.parteIts = new HashMap<Date, Integer>();
		this.gtzdos = new LinkedList<String>();
	}

	private boolean outOfDate ( Date date ) {
		if ( date == null )
			return false;
		if ( fromDate == null )
			return false;
		return fromDate.compareTo(date) > 0 ; 
	}
	
	private java.sql.Date getEndDate(java.sql.Date fecFin, Emprper emprper) throws SQLException{
		return DefaultMysqlDB.is9999(fecFin)? emprper.getFecbaj(): fecFin;
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
	
	private Pattern delayPattern = 
		Pattern.compile("ATRASOS", Pattern.CASE_INSENSITIVE);

	private SalaryType getSalaryType(String description) {

		SalaryType salaryType = null;
		
		if (delayPattern.matcher(description).find()) {
			salaryType = salaryType.DELAY;
		}else {
			salaryType = SalaryType.SALARY;
		}
		
		return salaryType;
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
			mysqlDB.getDeductionConceptId("IRPF");
		ctsqlDB.visitEmprper(this);
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
			mysqlDB.debug("emprper[{}] : Without CCC {}/{}", 
					emprper.getCdg(), emprper.getCodact(), emprper.getCodccc());
		}
		
		Integer activityId = myEnterprise.getActivityId(emprper.getCodact());
		
		
		registration = null;
		seniorityDate = null;
		emprper.visitTrabajo_emprper(new DefaultCtsqlDBVisitor(){
			@Override
			public void visitTrabajo_emprper(Trabajo trabajo, Emprper emprper)
					throws SQLException {
				if ( outOfDate(trabajo.getFecfin()) ) 
					return;
				registration = trabajo.getNummat();	
				seniorityDate = trabajo.getFecant();	
			}
		});
		
		String codCCC = emprper.getCodccc();
		SSRegimeType ssRegimeType;
		if ( ccc == null && "A".equalsIgnoreCase(codCCC) ) {
			ssRegimeType = SSRegimeType.SELF_EMPLOYED;
		} else {
			String indRegimen = myEnterprise.getIndRegimen(emprper.getCodact());
			if ( "A".equalsIgnoreCase(indRegimen)) {
				ssRegimeType = SSRegimeType.AGRICULTURAL;
			}else if ( "M".equalsIgnoreCase(indRegimen)) {
				ssRegimeType = SSRegimeType.SEA_WORKERS;
			} else {
				ssRegimeType = SSRegimeType.GENERAL;
			}
		}
		
		this.codCon = null; 
		this.nivel = null;
		this.agreementCategoryId = null; 
		emprper.visitTrabajo_emprper(new TrabajoAggregate());
		
		if ( !myAgreement.inherits(emprper, this.codCon, this.nivel) ) {
			this.agreementCategoryId = null;
		}
		
		Integer wcalendar = myEnterprise.getCalendar(workplace);
		Integer calendar = myCalendar.getCalendar(emprper.getCodemp(), emprper.getDomicilio(), emprper.getCodact());
		if ( calendar == wcalendar ) {
			calendar = null;
		}
		else {
			mysqlDB.info("emprper{}: Has diferent calendar {} {} ", emprper.getCdg(), calendar, wcalendar );
		}
		
		this.contractId = 
			mysqlDB.insertContract(person, 
					workplace, 
					ccc, 
					emprper.getFecalt(), 
					emprper.getFecbaj(),
					calendar,
					null,
					null,
					enum2short(ContractStatus.PROCESSED),
					registration,
					seniorityDate,
					activityId,
					enum2short(ssRegimeType),
					this.agreementCategoryId);
		
		if ( this.agreementCategoryId != null ) {
			mysqlDB.info("emprper[{}]: [{}:{}] Inherits payments from {}-{}", 
					emprper.getCdg(), emprper.getCodper(), this.contractId, this.codCon, this.nivel);
			
		}
		
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
		
		emprper.visitRel_pit_epp(this);
		emprper.visitPitnu_emprper(this);
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
			
		
		mysqlDB.insertContract_data(CATEGORY.getName(), 
				this.contractId, 
				String.format("\"%s\"", trabajo.getCodcat()), 
				trabajo.getFecini(), 
				endDate);
		mysqlDB.insertContract_data(TC2.getName(), 
				this.contractId, 
				String.format("\"%s\"", tc2), 
				trabajo.getFecini(), 
				endDate);
		Boolean indefinite = "123".indexOf(tc2.charAt(0)) != -1; 
		mysqlDB.insertContract_data(INDEFINITE.getName(), 
				this.contractId, 
				String.format("%b", indefinite ), 
				trabajo.getFecini(), 
				endDate);
		mysqlDB.insertContract_data(QUOTE_GROUP.getName(), 
				this.contractId, 
				String.format("\"%s\"", trabajo.getCodbas()), 
				trabajo.getFecini(), 
				endDate);
		
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
		mysqlDB.insertContract_data(IRPF_PERCENT.getName(), 
				this.contractId, 
				String.format("%.2f",irpf), 
				trabajo.getFecini(), 
				endDate);
		
		
		ContractData contractData = 
			new ContractData(startDate, 
					endDate, 
					trabajo.getProcot());
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

		Double moneyIrpfBase = toDouble(nomina.getBase_irpf());
		Double kindIrpfBase = toDouble(nomina.getBase_especie());
		Double irpfBase = moneyIrpfBase + kindIrpfBase;
		
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
					moneyIrpfBase,
					kindIrpfBase,
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
	
	
	
	
	
	private String getExpression(Percep percep) 
	throws SQLException {

		String calculo =  percep.getCalculo();
		BigDecimal importe = percep.getImporte();
		String indCom = percep.getIndcom();
		String comApl = percep.getComapl();
		
		return myConcepts.getExprFormat(calculo, importe, indCom, comApl);
	}
	


	@Override
	public void visitRel_pcp_epp(Percep percep, Emprper emprper)
			throws SQLException {
		
		if ( outOfDate( percep.getFecfin()))
			return ;


		Concept<PaymentType> concept = 
			myConcepts.getConcept(percep.getCodcom());
		
		if ( concept == null ){
			mysqlDB.error("precep[{}] : Not found concept {} ", percep.getCdg(), percep.getCodcom());
			return ;
		}
			
		
		Short month = MyConcepts.getMonth(percep.getMes()) ;

		java.sql.Date endDate = 
			getEndDate(percep.getFecfin(), emprper);
		
		String script = getExpression(percep);
		
		String tipcot = percep.getTipcot();

		String irpf = null;
		if ( ! tipcot.equals(concept.quote )) {
			irpf = MyConcepts.getIrpfExprFormat(tipcot);
		}
		
		String description = percep.getDescom();
		if ( description.equals(concept.description) ){
			description = null;
		}
		
		if ( "P".equals(percep.getIndcom()) &&
				"6".equals(percep.getCalculo()) )
		{
			
			java.sql.Date startDate = percep.getFecini();
			List<ContractData> datas = 
				getContractData(startDate, endDate);
			
			PaymentType type = PaymentType.SALARY_SUPPLEMENTS;
			if ( concept.type == type ) {
				type = null;
			}
			
			String quote = MyConcepts.getQuoteExprFormat(tipcot);
			
			String porCot = null;
			String porQuote  = null;
			for (ContractData data : datas) {
				
				if ( porCot == null || porCot.equals(data.porCot)){
					porCot = data.porCot;
					endDate = data.endDate;
					continue;
				}
				
				if ( !inheritFromAgreement(percep, endDate) || 
					!percep.getRedext().replace('S','D').equals(porCot) )
				{ 
					porQuote = MyConcepts.getPorQuote(porCot, 
							MyConcepts.getQuoteExprFormat(tipcot));
					mysqlDB.insertContract_payment(
							enum2short(type),
							this.contractId, 
							concept.id,
							description, 
							(short) 0,
							script, 
							DefaultMysqlDB.format( irpf, concept.code ),
							DefaultMysqlDB.format ( porQuote, concept.code ),
							startDate, 
							month, 
							endDate,
							enum2short(SalaryType.EXTRA));
				} // end-if: Si el prorrateo no es mensual y la percepción es diferente.
				
				porCot = data.porCot;
				startDate = data.startDate;
			}

			if ( !inheritFromAgreement(percep, endDate) || 
				!percep.getRedext().replace('S','D').equals(porCot) )
			{ 
				porQuote = MyConcepts.getPorQuote(porCot, quote);
				mysqlDB.insertContract_payment(
						enum2short(type),
						this.contractId, 
						concept.id,
						description, 
						(short) 0,
						script, 
						DefaultMysqlDB.format ( irpf, concept.code ),
						DefaultMysqlDB.format ( porQuote, concept.code ),
						startDate, 
						month, 
						endDate,
						enum2short(SalaryType.EXTRA));
			} // end-if: Si el prorrateo no es mensual y la percepción es diferente.
		}
		else {
			
			
			if ( inheritFromAgreement(percep, endDate ) ) {
				return ;
			}
			
			PaymentType type = 
				mysqlDB.getPaymentType(percep.getDescom(), 
						percep.getDinesp(),
						percep.getTipcot());
			
			
			if ( type == PaymentType.SALARY_IN_KIND ){
				String ingEspEmp = 
					myEnterprise.getIngEspEmp(emprper.getCodact());
				if ( "S".equalsIgnoreCase(ingEspEmp ) ){
					irpf = "0.00";
				} //end-if: IRPF en especie a cuenta de  la empresa.
			} // end-if: En especie 
			
			if ( type == concept.type ){
				type = null;
			} //end-if: El tipo de precepción es igual al del concepto
			
			String quote = null;
			if ( ! tipcot.equals(concept.quote )) {
				quote = MyConcepts.getQuoteExprFormat(tipcot);
			} //end-if: La cotización es igual a la del concepto
			
			SalaryType salaryType = getSalaryType(percep.getDesabr());

			mysqlDB.insertContract_payment(
					enum2short(type), 
					this.contractId, 
					concept.id,
					description, 
					(short) 0,
					script, 
					DefaultMysqlDB.format ( irpf, concept.code ),
					DefaultMysqlDB.format ( quote, concept.code ),
					percep.getFecini(), 
					null,
					endDate,
					enum2short(salaryType));

						

		}
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
			myConcepts.getConcept(codCom);
		
		String paymentConcept = concept != null ? 
				concept.code : MyConcepts.formatCode(codCom);
		
		PaymentType type = concept != null ? 
				concept.type : mysqlDB.getPaymentType(description, dinEsp, null ) ;
		
		BigDecimal importe = nominadev.getImporte();
		BigDecimal impuni = nominadev.getImpuni();
		BigDecimal unidades = nominadev.getUnidades();
		
		String function = mysqlDB.getFunction(importe, impuni, unidades);
		
		mysqlDB.insertSalary_payment(this.salaryId, 
				enum2short(type ), 
				paymentConcept,
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
					0.00, 
					baseIRPF,
					0.00);
		
		String function = String.format("%.2f%%", 
				totalPayment != null ? totalPayment : 0);
		
		Concept<PaymentType> concept = 
			myConcepts.getConcept(nominaex.getCodcom());
		
		String paymentConcept = concept != null ? 
				concept.code : MyConcepts.formatCode(nominaex.getCodcom());
		
		PaymentType type = concept != null ? 
				concept.type : mysqlDB.getPaymentType(nominaex.getDescom(), "D", null ) ;
		
		mysqlDB.insertSalary_payment(this.salaryId, 
				enum2short(type), 
				paymentConcept,
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
			name = ACTUAL_DAYS.getName();
			expression = String.format("%d", trabinci.getCantidad());
		}else if ( codinc.contains("ESPECIALES")) {
			name = SPECIAL_DAYS.getName();
			expression = String.format("%d", trabinci.getCantidad());
		}else {
			name = codinc;
		}

		java.sql.Date endDate = 
			getEndDate(trabinci.getFecfin(), emprper);
		
		mysqlDB.insertContract_data(name, 
				this.contractId, 
				expression, 
				trabinci.getFecini(), 
				endDate);
	}

	
	private LeaveType getLeaveType(String tipoIt, String riesgo ) {
		if ( "E".equalsIgnoreCase(tipoIt) ) {
			return LeaveType.COMMON_DISEASE;
		}
		if ( "A".equalsIgnoreCase(tipoIt) ) {
			return LeaveType.OCCUPATIONAL_DISEASE;
		}
		if ( "M".equalsIgnoreCase(tipoIt) ) {
			return LeaveType.MATERNITY;
		}
		if ( "S".equalsIgnoreCase(riesgo)) {
			return LeaveType.PREGNANCY_RISK;
		}
		return null;
	}
	
	@Override
	public void visitRel_pit_epp(Parteit parteit, Emprper emprper)
			throws SQLException {
		
		java.sql.Date endDate = 
			getEndDate(parteit.getFecfin(), emprper);
		
		Double dailyCgcBase = toDouble(parteit.getBasediacg());
		Double dailyCgpBase = toDouble(parteit.getBasediaacc());
		Double dailyRegBase = toDouble(parteit.getBaseregdia());
		
		Short type = 
			enum2short(getLeaveType(parteit.getTipoit(), parteit.getRiesgo()));
		
		
		
		Integer id = mysqlDB.insertContract_leave(
				type, 
				this.contractId, 
				null, 
				parteit.getFecini(), 
				endDate, 
				dailyCgcBase, 
				dailyCgpBase,
				parteIts.get(parteit.getFeciniori()),
				dailyRegBase,
				null);
		
		parteIts.put(parteit.getFecini(), id);
	}
	
	@Override
	public void visitPitnu_emprper(Parteitnu parteitnu, Emprper emprper)
			throws SQLException {
		java.sql.Date endDate = 
			getEndDate(parteitnu.getFecfin(), emprper);
		
		Double dailyCgcBase = toDouble(parteitnu.getBasediacg());
		Double dailyCgpBase = toDouble(parteitnu.getBasediaacc());
		Double dailyRegBase = toDouble(parteitnu.getBaseregdia());
		
		Short type = 
			enum2short(getLeaveType(parteitnu.getTipoit(), parteitnu.getRiesgo()));
		
		Integer id = mysqlDB.insertContract_leave(
				type, 
				this.contractId, 
				null, 
				parteitnu.getFecini(), 
				endDate, 
				dailyCgcBase, 
				dailyCgpBase,
				parteIts.get(parteitnu.getFeciniori()),
				dailyRegBase,
				null);

		parteIts.put(parteitnu.getFecini(), id);
	}

	private boolean inheritFromAgreement (Percep percep, java.sql.Date endDate) 
	throws SQLException {

		if ( this.agreementCategoryId == null ) {
			return false;
		} // end-if : 

		int compare = 
			myAgreement.hasPayment(this.codCon, 
					this.nivel,
					percep.getCodcom(), 
					percep);
		
		if (compare == -1 ) {
			return false;
		}
		
		if ( compare == 0 ) {
			return true ;
		} // end-if : Son exactamante iguales

		//Sólo tienen diferente el importe
		mysqlDB.insertContract_data(
					MyAgreement.getAmountVariable(percep.getCodcom()), 
					this.contractId, 
					String.format("%.3f", percep.getImporte()), 
					percep.getFecini(), 
					endDate);
		
		return true ;
	}
	
	private void addGtzdo(Percep percep, String variable ) 
	throws SQLException {

		double garilt = toDouble(percep.getGarilt()); 
		
		if ( garilt > 0.00 ) {
			String grtzdo = 
				myConcepts.getGrtzdoExprFormat(percep.getCalculo(), garilt/100 );
			if ( grtzdo  != null ){
				gtzdos.add(DefaultMysqlDB.format(grtzdo, variable ) );
			}
		}
	}

}
