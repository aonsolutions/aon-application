package com.esferalia.aon.payroll.ctsql2mysql;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.collections.map.HashedMap;


import com.code.aon.employee.enumeration.ContractStatus;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Complemento;
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

public class MyContract extends DefaultCtsqlDBVisitor {
	
	final static String PASSWORD  			= "demo";

	private static final String MONTH_DAYS = "dias_mes";
	private static final String WORKED_DAYS = "dias_trabajados";
	private static final String ACTUAL_DAYS = "dias_efectivos";
	private static final String SPECIAL_DAYS = "dias_especiales";
	private static final String OLD_BASE = "base_antiguedad";
	private static final String IRPF_BASE = "base_irpf";

	private static class PaymentConcept{
		private Integer id;
		private String code;
		private PaymentType type;
		
		
		public PaymentConcept(Integer id, String code, 
				PaymentType type) {
			this.id = id;
			this.code = code;
			this.type = type;
		}
	}

	
	private Integer 		salaryId;
	private Integer 		contractId;
	
	private Date 			fromDate;
	private MyPerson 		myPerson;
	private MyEnterprise 	myEnterprise;
	private DefaultMysqlDB 	mysqlDB;
	
	private Map<String, PaymentConcept> paymentConcepts ;
	

	public MyContract(DefaultMysqlDB mysqlDB, MyEnterprise myEnterprise, MyPerson myPerson) {
		this ( mysqlDB, myEnterprise, myPerson, null );
	}

	public MyContract(DefaultMysqlDB mysqlDB, MyEnterprise myEnterprise, MyPerson myPerson, Date fromDate) {
		this.mysqlDB = mysqlDB;
		this.fromDate = fromDate;
		this.myPerson = myPerson;
		this.myEnterprise = myEnterprise;
		this.paymentConcepts = new HashMap<String, PaymentConcept>();
	}

	private boolean outOfDate ( Date date ) {
		if ( date == null )
			return false;
		if ( fromDate == null )
			return false;
		return fromDate.compareTo(date) > 0 ; 
	}
	
	
	private boolean isActive( Emprper emprper ) 
	throws SQLException {
		Date today = Calendar.getInstance().getTime();
		return today.compareTo(emprper.getFecbaj() ) < 0;
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
	
	
	@Override
	public void visit(AbstractCtsqlDB ctsqlDB) throws SQLException {
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
				complemento.getDinesp());
		Integer paymentConcept = 
			mysqlDB.insertPayment_concept(code, description, MysqlDB.enum2short(type) );
		paymentConcepts.put(complemento.getCdg() , new PaymentConcept(paymentConcept, code, type));
	}

	@Override
	public void visitEmprper(Emprper emprper)
			throws SQLException {
		
		if ( outOfDate(emprper.getFecbaj()))
			return ;
		
		Integer workplace = 
			myEnterprise.getWorkplace(emprper.getCodemp(), emprper.getDomicilio(), emprper.getCodact());
		if ( workplace == null ){
			mysqlDB.error("emprper[{}] : Not found workplace for {}/{}/{}", 
					emprper.getCdg(), emprper.getCodemp(), emprper.getDomicilio(), emprper.getCodact());
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
					null,
					null,
					DefaultMysqlDB.enum2short(ContractStatus.PROCESSED));
		
		
		String numDoc = 
			myPerson.getNumDoc(emprper.getCodper());
		
		Integer enterprise = 
			myEnterprise.getEnterprise(emprper.getCodemp());
		
		mysqlDB.insertUser(myPerson.getName(emprper.getCodper()), 
				numDoc, 
				enterprise, 
				person, 
				true, 
				PASSWORD);

		emprper.visitTrabajo_emprper(this);
		emprper.visitRel_pcp_epp(this);
		emprper.visitRel_dto_per(this);
		emprper.visitRel_nom_per(this);
		emprper.visitRel_pex_per(this);
		emprper.visitTrabinci_emprper(this);
	}
	
	private String getIRPFExpression(BigDecimal percentage) {
		return String.format("%s * %.2f / 100", 
				IRPF_BASE, percentage != null ? percentage : 0.00 );
	}
	
	@Override
	public void visitTrabajo_emprper(Trabajo trabajo, Emprper emprper )
			throws SQLException {
		
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
		
		String description = trabajo.getDestc2();
		
		mysqlDB.insertContract_data(
				this.contractId, 
				trabajo.getCodtc2(), 
				description, 
				conditions, 
				trabajo.getFecini(), 
				trabajo.getFecfin(),
				trabajo.getCodbas(),
				trabajo.getDescat());
		
		BigDecimal irpf = trabajo.getIrpf();
		String irpfFunction = getIRPFExpression(irpf);
		
		
		mysqlDB.insertContract_deduction(
				DefaultMysqlDB.enum2short(DeductionType.IRPF),
				null,
				this.contractId, 
				null, 
				(short) 1,
				irpfFunction, 
				trabajo.getFecini(), 
				trabajo.getFecfin(),
				null );
		
	}
	
	@Override
	public void visitRel_dto_per(Trabdto trabdto, Emprper emprper) throws SQLException {

		String concepto = trabdto.getConcepto();
		
		DeductionType type = getDeductionType(concepto);

		BigDecimal importe = trabdto.getImporte();
		String function = String.format("%.3f", 
				importe != null ? importe : 0);
		
		mysqlDB.insertContract_deduction(DefaultMysqlDB.enum2short(type), 
				null,
				this.contractId, 
				concepto, 
				(short) 0,
				function, 
				trabdto.getFecini(), 
				trabdto.getFecfin(),
				(short) 0 );
	}

	@Override
	public void visitRel_nom_per(Nomina nomina, Emprper emprper) throws SQLException {
		
		if ( outOfDate( nomina.getFecfin()))
			return ;
		
		Double totalPayment = toDouble(nomina.getTotal_devengos()); 
		Double totalDeduction = toDouble(nomina.getTotal_deducir());
		Double totalLiquid = toDouble(nomina.getTotal_liquido());
		
		Double baseConcom = toDouble(nomina.getTotal_1());
		Double baseIRPF = toDouble(nomina.getBase_irpf());
		Double baseprorrataPagas = toDouble(nomina.getBase_proext());
		Double renumeration = toDouble(nomina.getRemuneracion());
		Double baseHExtras = toDouble(nomina.getBase_hextras(), nomina.getBase_hextras_no());
		Double baseProfessional = toDouble(nomina.getBase_acc());
		Double ssContributions = toDouble(nomina.getImporte_cuotas());
		Double totalEnterprise = toDouble(nomina.getCuota_empresa());
		
		Rel_epp_per  rel_epp_per = new Rel_epp_per(); 
		emprper.visitRel_epp_per(rel_epp_per);
		
		Rel_epp_emp rel_epp_emp = new Rel_epp_emp();
		emprper.visitRel_epp_emp(rel_epp_emp);
		
		Rel_epp_ccc rel_epp_ccc = new Rel_epp_ccc();
		emprper.visitRel_epp_ccc(rel_epp_ccc);
		
		this.salaryId= 
			mysqlDB.insertSalary(this.contractId, 
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
					baseprorrataPagas, 
					baseConcom, 
					baseProfessional,
					baseHExtras,
					baseIRPF,
					ssContributions);
		
		Double importeCg = toDouble(nomina.getImporte_cg());
		if ( importeCg > 0 ) {
			BigDecimal cgPercentage = 
				nomina.getPrc_cg();
			String cgFunction = String.format("%.2f%%", 
					cgPercentage != null ? cgPercentage : 0);
			mysqlDB.insertSalary_deduction(this.salaryId, 
					DefaultMysqlDB.enum2short(DeductionType.COMMON_CONTINGENCY), 
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
						DefaultMysqlDB.enum2short(DeductionType.JOB_TRAINING),
						null,
						null, 
						jobFunction, 
						importeJob);

				String ueFunction = String.format("%.2f%%", uePercentage);
				mysqlDB.insertSalary_deduction(this.salaryId, 
						DefaultMysqlDB.enum2short(DeductionType.UNEMPLOYMENT),
						null,
						null, 
						ueFunction, 
						importeAcc - importeJob);
			}
			else {
				String accFunction = String.format("%.2f%%", accPercentage);
				mysqlDB.insertSalary_deduction(this.salaryId, 
						DefaultMysqlDB.enum2short(DeductionType.PROFESSIONAL_CONTINGENCY),
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
					DefaultMysqlDB.enum2short(DeductionType.STRUCTURAL_OVERTIME), 
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
					DefaultMysqlDB.enum2short(DeductionType.NON_STRUCTURAL_OVERTIME), 
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
					DefaultMysqlDB.enum2short(DeductionType.IRPF), 
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
		}else if (calculo.equals("7")) {
			return String.format("%s * %.2f / 100 ", 
					OLD_BASE, importe );
		}
		
		return String.format("%.3f", importe );
	}
	
	@Override
	public void visitRel_pcp_epp(Percep percep, Emprper emprper)
			throws SQLException {
		
		PaymentConcept concept = 
			getPaymentConcept(percep.getCodcom(), percep.getDescom(), percep.getDinesp());
		
		String script = getExpression(percep);
		
		Short month = getMonth(percep.getMes()) ;
		
		mysqlDB.insertContract_payment(
				DefaultMysqlDB.enum2short(concept.type), 
				this.contractId, 
				concept.id,
				percep.getDescom(), 
				(short) 0,
				script, 
				percep.getFecini(), 
				month,
				percep.getFecfin());
	}
	
	
	private PaymentConcept getPaymentConcept(String codCom, String description, String dinEsp) {

		PaymentConcept concept = 
			paymentConcepts.get(codCom);
		
		if ( concept != null ) { 
			return concept;
		}
		
		String code = null;
		if ( codCom != null ) {
			code = getCode( codCom);
		}
		
		PaymentType type = 
			mysqlDB.getPaymentType(description, dinEsp);
		
		concept = new PaymentConcept(null, code, type);
		
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

		PaymentConcept concept = 
			getPaymentConcept(codCom, description, dinEsp);
		
		BigDecimal importe = nominadev.getImporte();
		BigDecimal impuni = nominadev.getImpuni();
		BigDecimal unidades = nominadev.getUnidades();
		
		String function = mysqlDB.getFunction(importe, impuni, unidades);
		
		mysqlDB.insertSalary_payment(this.salaryId, 
				MysqlDB.enum2short(concept.type), 
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
				DefaultMysqlDB.enum2short(type),
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
			mysqlDB.insertSalary(this.contractId, 
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
					baseIRPF,
					0.00);
		
		String function = String.format("%.2f%%", 
				totalPayment != null ? totalPayment : 0);
		
		PaymentConcept concept = 
			getPaymentConcept(nominaex.getCodcom(), nominaex.getDescom(), "D");
		
		mysqlDB.insertSalary_payment(this.salaryId, 
				DefaultMysqlDB.enum2short(concept.type), 
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
					DefaultMysqlDB.enum2short(DeductionType.IRPF), 
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
				DefaultMysqlDB.enum2short(type),
				null,
				concepto, 
				function, 
				importe);
	}
	
	@Override
	public void visitTrabinci_emprper(Trabinci trabinci, Emprper emprper)
			throws SQLException {
		
		String name = null ;
		String expression = null;
		String codinc = trabinci.getCodinc();
		if ( codinc.equals("EFECTIVOS")) {
			name = ACTUAL_DAYS;
			expression = String.format("%d", trabinci.getCantidad());
		}if ( codinc.equals("ESPECIALES")) {
			name = SPECIAL_DAYS;
			expression = String.format("%d", trabinci.getCantidad());
		}else {
			name = codinc;
		}
		
		mysqlDB.insertContract_event(name, 
				this.contractId, 
				expression, 
				trabinci.getFecini(), 
				trabinci.getFecfin());
	}

}
