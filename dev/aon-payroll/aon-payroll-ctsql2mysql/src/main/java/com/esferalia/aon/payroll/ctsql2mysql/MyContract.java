package com.esferalia.aon.payroll.ctsql2mysql;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Date;
import java.util.regex.Pattern;

import org.dom4j.tree.AbstractBranch;

import com.code.aon.employee.enumeration.ContractStatus;
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
import com.esferalia.aon.payroll.ctsql2mysql.DefaultCtsqlDBVisitor.Rel_epp_per;
import com.esferalia.aon.salary.enumeration.DeductionType;
import com.esferalia.aon.salary.enumeration.PaymentType;

public class MyContract extends DefaultCtsqlDBVisitor {
	
	private Integer 		salaryId;
	private Integer 		contractId;
	
	private Date 			fromDate;
	private MyPerson 		myPerson;
	private MyEnterprise 	myEnterprise;
	private DefaultMysqlDB 	mysqlDB;

	public MyContract(DefaultMysqlDB mysqlDB, MyEnterprise myEnterprise, MyPerson myPerson) {
		this ( mysqlDB, myEnterprise, myPerson, null );
	}

	public MyContract(DefaultMysqlDB mysqlDB, MyEnterprise myEnterprise, MyPerson myPerson, Date fromDate) {
		this.mysqlDB = mysqlDB;
		this.fromDate = fromDate;
		this.myPerson = myPerson;
		this.myEnterprise = myEnterprise;
	}

	private boolean outOfDate ( Date date ) {
		if ( date == null )
			return false;
		if ( fromDate == null )
			return false;
		return fromDate.compareTo(date) > 0 ; 
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
	
	private Pattern overtimePattern = 
		Pattern.compile("HORAS\\s+EXTRA", Pattern.CASE_INSENSITIVE);
	private Pattern baseSalaryPattern = 
		Pattern.compile("SALARIO\\s+BASE", Pattern.CASE_INSENSITIVE);
	private Pattern compensationPattern = 
		Pattern.compile("INDEMNIZACION", Pattern.CASE_INSENSITIVE);
	private Pattern noticePattern = 
		Pattern.compile("INDEMNIZACION.*AVISO", Pattern.CASE_INSENSITIVE);
	private Pattern movingPattern = 
		Pattern.compile("INDEMNIZACION.*TRASLADO", Pattern.CASE_INSENSITIVE);
	private Pattern dismissalPattern = 
		Pattern.compile("INDEMNIZACION.*DESPIDO", Pattern.CASE_INSENSITIVE);

	
	private PaymentType getPaymentType(String description, String dinEsp ) {

		PaymentType paymetType = null;
		
		if ( "E".equalsIgnoreCase(dinEsp)){
			paymetType = PaymentType.SALARY_IN_KIND;
		}else if (baseSalaryPattern.matcher(description).find()) {
			paymetType = PaymentType.BASE_SALARY;
		}else if (overtimePattern.matcher(description).find()) {
			paymetType = PaymentType.OVERTIME_HOURS;
		}else if (noticePattern.matcher(description).find()) {
			paymetType = PaymentType.MOVING_COMPENSATION;
		}else if (movingPattern.matcher(description).find()) {
			paymetType = PaymentType.MOVING_COMPENSATION;
		}else if (dismissalPattern.matcher(description).find()) {
			paymetType = PaymentType.MOVING_COMPENSATION;
		}else if (compensationPattern.matcher(description).find()) {
			paymetType = PaymentType.COMPENSATION_OR_PREPAID_EXPENSES;
		}else {
			paymetType = PaymentType.SALARY_SUPPLEMENTS;
		}
		
		return paymetType;
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
	
	private String getFunction(BigDecimal importe, BigDecimal impuni, BigDecimal unidades) {
		if ( impuni != null && impuni.doubleValue() != 0 ) {
			if ( unidades != null ) {
				return String.format("%.3f * %.3f", impuni, unidades );
			} 
		}
		return String.format("%.3f", importe );
	}
	
	
	
	@Override
	public void visit(AbstractCtsqlDB ctsqlDB) throws SQLException {
		ctsqlDB.visitEmprper(this);
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
		
		emprper.visitTrabajo_emprper(this);
		emprper.visitRel_pcp_epp(this);
		emprper.visitRel_dto_per(this);
		
		emprper.visitRel_nom_per(this);
		emprper.visitRel_pex_per(this);
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
		String irpfFunction = String.format("%.3f%%", irpf != null ? irpf : 0.00 );
		
		mysqlDB.insertContract_deduction(
				DefaultMysqlDB.enum2short(DeductionType.IRPF), 
				this.contractId, 
				null, 
				irpfFunction, 
				trabajo.getFecini(), 
				trabajo.getFecfin());
		
	}
	
	@Override
	public void visitRel_dto_per(Trabdto trabdto, Emprper emprper) throws SQLException {

		
		String concepto = trabdto.getConcepto();
		
		DeductionType type = getDeductionType(concepto);

		BigDecimal importe = trabdto.getImporte();
		String function = String.format("%.3f", 
				importe != null ? importe : 0);
		
		mysqlDB.insertContract_deduction(DefaultMysqlDB.enum2short(type), 
				this.contractId, 
				concepto, 
				function, 
				trabdto.getFecini(), 
				trabdto.getFecfin());
	}

	@Override
	public void visitRel_nom_per(Nomina nomina, Emprper emprper) throws SQLException {
		
		if ( outOfDate( nomina.getFecfin()))
			return ;
		
		Double totalPayment = toDouble(nomina.getTotal_devengos()); 
		Double totalDeduction = toDouble(nomina.getTotal_deducir());
		Double totalLiquid = toDouble(nomina.getTotal_liquido());
		
		Double baseConcom = toDouble(nomina.getBase_concom());
		Double baseIRPF = toDouble(nomina.getBase_irpf());
		Double baseprorrataPagas = toDouble(nomina.getBase_proext());
		Double renumeration = toDouble(nomina.getRemuneracion());
		Double baseHExtras = toDouble(nomina.getBase_hextras(), nomina.getBase_hextras_no());
		Double baseProfessional = toDouble(nomina.getBase_acc());
		
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
					nomina.getFecemi(), 
					renumeration, 
					baseprorrataPagas, 
					baseConcom, 
					baseProfessional,
					baseHExtras,
					baseIRPF);
		
		Double importeCg = toDouble(nomina.getImporte_cg());
		if ( importeCg > 0 ) {
			BigDecimal cgPercentage = 
				nomina.getPrc_cg();
			String cgFunction = String.format("%.2f%%", 
					cgPercentage != null ? cgPercentage : 0);
			mysqlDB.insertSalary_deduction(this.salaryId, 
					DefaultMysqlDB.enum2short(DeductionType.COMMON_CONTINGENCY), 
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
						jobFunction, 
						importeJob);

				String ueFunction = String.format("%.2f%%", uePercentage);
				mysqlDB.insertSalary_deduction(this.salaryId, 
						DefaultMysqlDB.enum2short(DeductionType.UNEMPLOYMENT), 
						null, 
						ueFunction, 
						importeAcc - importeJob);
			}
			else {
				String accFunction = String.format("%.2f%%", accPercentage);
				mysqlDB.insertSalary_deduction(this.salaryId, 
						DefaultMysqlDB.enum2short(DeductionType.PROFESSIONAL_CONTINGENCY), 
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
					irpfFunction, 
					importeIrpf);
		}
		
		nomina.visitRel_nmd_nom(this);
		nomina.visitRel_dto_nom(this);
	}
	
	@Override
	public void visitRel_pcp_epp(Percep percep, Emprper emprper)
			throws SQLException {
		
		PaymentType paymetType = 
			getPaymentType(percep.getDescom(), percep.getDinesp());
		
		BigDecimal impuni = percep.getImpuni();
		BigDecimal importe = percep.getImporte();
		BigDecimal unidades = percep.getUnidades();
		String script = getFunction(importe, impuni, unidades);
		
		mysqlDB.insertContract_payment(
				DefaultMysqlDB.enum2short(paymetType), 
				this.contractId, 
				percep.getDescom(), 
				script, 
				percep.getFecini(), 
				percep.getFecfin());
	}


	@Override
	public void visitRel_nmd_nom(Nominadev nominadev, Nomina nomina)
			throws SQLException {
		
		String description = 
			nominadev.getDescom();
		
		PaymentType paymetType = 
			getPaymentType( description, nominadev.getDinesp());
		
		BigDecimal importe = nominadev.getImporte();
		BigDecimal impuni = nominadev.getImpuni();
		BigDecimal unidades = nominadev.getUnidades();
		
		String function = getFunction(importe, impuni, unidades);
		
		mysqlDB.insertSalary_payment(this.salaryId, 
				DefaultMysqlDB.enum2short(paymetType), 
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
					nominaex.getFecemi(), 
					0.00, 
					0.00, 
					0.00, 
					0.00, 
					0.00,
					baseIRPF);
		
		String function = String.format("%.2f%%", 
				totalPayment != null ? totalPayment : 0);

		mysqlDB.insertSalary_payment(this.salaryId, 
				DefaultMysqlDB.enum2short(PaymentType.SALARY_SUPPLEMENTS), 
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
				concepto, 
				function, 
				importe);
	}

}
