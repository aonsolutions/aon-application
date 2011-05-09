package com.esferalia.aon.payroll.ctsql2mysql;

import static com.esferalia.aon.payroll.ctsql2mysql.DefaultMysqlDB.enum2short;
import static com.esferalia.aon.payroll.ctsql2mysql.DefaultMysqlDB.toDouble;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Date;

import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Emprper;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Nomdto;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Nomdtoex;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Nomina;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Nominadev;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Nominaex;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractMysqlDB.Salary_embargo;
import com.esferalia.aon.payroll.ctsql2mysql.DefaultCtsqlDBVisitor.Rel_epp_ccc;
import com.esferalia.aon.payroll.ctsql2mysql.DefaultCtsqlDBVisitor.Rel_epp_emp;
import com.esferalia.aon.payroll.ctsql2mysql.DefaultCtsqlDBVisitor.Rel_epp_per;
import com.esferalia.aon.payroll.ctsql2mysql.IConcepts.Concept;
import com.esferalia.aon.payroll.ctsql2mysql.IContracts.FullEmbargo;
import com.esferalia.aon.salary.enumeration.DeductionType;
import com.esferalia.aon.salary.enumeration.PaymentType;
import com.esferalia.aon.salary.enumeration.SalaryType;

public class MySalary extends DefaultCtsqlDBVisitor {

	private DefaultMysqlDB 	mysqlDB;
	IContracts				contracts;
	IConcepts				concepts;

	private Integer 		salaryId;
	
	
	public MySalary(DefaultMysqlDB mysqlDB, IContracts contracts, IConcepts concepts) {
		this.mysqlDB = mysqlDB;
		this.contracts = contracts;
		this.concepts = concepts;
	}

	@Override
	public void visitRel_nom_per(Nomina nomina, Emprper emprper) throws SQLException {
		
		if ( this.contracts.outOfDate( nomina.getFecfin()))
			return ;

		Integer contractId = 
			contracts.getContractId(emprper.getCdg());
		if ( contractId == null ) {
			MysqlDB.error("nomina[{}]: Contract not found for {}.", nomina.getCdg(), emprper.getCdg());
			return ; 
		}
		
		
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
					contractId, 
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
			concepts.getConcept(codCom);
		
		String paymentConcept = concept != null ? 
				concept.code : MyConcept.formatCode(codCom);
		
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
		
		DeductionType type = MyContract.getDeductionType(concepto);

		Double importe = toDouble(nomdto.getImporte());

		String function = String.format("%.3f", 
				importe != null ? importe : 0);
		
		
		if ( !contracts.hasEmbargo(concepto)){
			mysqlDB.insertSalary_deduction(
					this.salaryId, 
					enum2short(type),
					null,
					concepto, 
					function, 
					importe);
		}
		else {
			FullEmbargo embargo = contracts.getEmbargo(concepto);
			
			Salary_embargo salaryEmbargo =
				new Salary_embargo();
			salaryEmbargo.amount = importe;
			salaryEmbargo.description = concepto;
			salaryEmbargo.salary = this.salaryId;
			
			embargo.addEmbargo(salaryEmbargo, nomina.getFecfin());
			
		} // Es un embargo
	}
	
	@Override
	public void visitRel_pex_per(Nominaex nominaex, Emprper emprper) throws SQLException {
		
		if ( contracts.outOfDate( nominaex.getFecfin()))
			return ;
		
		Integer contractId = 
			contracts.getContractId(emprper.getCdg());
		if ( contractId == null ) {
			MysqlDB.error("nomina[{}]: Contract not found for {}.", nominaex.getCdg(), emprper.getCdg());
			return ; 
		}

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
					contractId, 
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
			concepts.getConcept(nominaex.getCodcom());
		
		String paymentConcept = concept != null ? 
				concept.code : MyConcept.formatCode(nominaex.getCodcom());
		
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
		
		DeductionType type = MyContract.getDeductionType(concepto);

		Double importe = toDouble(nomdtoex.getImporte());

		String function = String.format("%.3f", 
				importe != null ? importe : 0);
		
		if ( !contracts.hasEmbargo(concepto)) {
			mysqlDB.insertSalary_deduction(
					this.salaryId, 
					enum2short(type),
					null,
					concepto, 
					function, 
					importe);
		}
		else {
			FullEmbargo embargo = contracts.getEmbargo(concepto);
			
			Salary_embargo salaryEmbargo =
				new Salary_embargo();
			salaryEmbargo.amount = importe;
			salaryEmbargo.description = concepto;
			salaryEmbargo.salary = this.salaryId;
			
			embargo.addEmbargo(salaryEmbargo,  nominaex.getFecfin());
		}
	}
	

	
}
