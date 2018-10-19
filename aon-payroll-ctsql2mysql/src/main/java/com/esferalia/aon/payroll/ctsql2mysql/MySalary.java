package com.esferalia.aon.payroll.ctsql2mysql;

import static com.esferalia.aon.payroll.ctsql2mysql.DefaultMysqlDB.SPANISH;
import static com.esferalia.aon.payroll.ctsql2mysql.DefaultMysqlDB.enum2short;
import static com.esferalia.aon.payroll.ctsql2mysql.DefaultMysqlDB.toDouble;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Date;

import com.esferalia.aon.occam.api.model.type.SSRegimeType;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Emprper;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Finidto;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Finindem;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Finipext;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Finiquito;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Nomdto;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Nomdtoex;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Nomina;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Nominadev;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Nominadf;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Nominaex;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Nominaexdf;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractMysqlDB.Salary_embargo;
import com.esferalia.aon.payroll.ctsql2mysql.IConcepts.Concept;
import com.esferalia.aon.payroll.ctsql2mysql.IContracts.FullEmbargo;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.salary.enumeration.DeductionType;
import com.esferalia.aon.salary.enumeration.PaymentType;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.salary.expression.Period;

public class MySalary extends DefaultCtsqlDBVisitor {

	private DefaultMysqlDB mysqlDB;
	IContracts contracts;
	IConcepts concepts;

	private Integer salaryId;
	private Integer contractId;

	private Date lastSalary;

	public MySalary(DefaultMysqlDB mysqlDB, IContracts contracts,
			IConcepts concepts) {
		this.mysqlDB = mysqlDB;
		this.contracts = contracts;
		this.concepts = concepts;
		this.lastSalary = new Date(0);
	}

	public Date getLastSalaryDate() {
		return lastSalary;
	}

	@Override
	public void visitRel_nom_per(Nomina nomina, Emprper emprper)
			throws SQLException {

		if (this.contracts.outOfDate(nomina.getFecfin())) {
			return;
		}

		if (!checkFVisionado(nomina.getFecini(), nomina.getFvisione(),
				nomina.getFvisiont())) {
			return;
		}

		contractId = contracts.getContractId(emprper.getCdg());
		if (contractId == null) {
			MysqlDB.error("nomina[{}]: Contract not found for {}.",
					nomina.getCdg(), emprper.getCdg());
			return;
		}

		Double totalPayment = toDouble(nomina.getTotal_devengos());

		Double itBase = toDouble(nomina.getBase_it());
		Double proExtBase = toDouble(nomina.getBase_proext());
		Double renumeration = toDouble(nomina.getRemuneracion());
		Double rawCgcBase = toDouble(nomina.getTotal_1());
		Double cgcBase = toDouble(nomina.getBase_cg());

		Double hextraBase = toDouble(nomina.getBase_hextras());
		Double nonHextraBase = toDouble(nomina.getBase_hextras_no());

		Double cgpBase = toDouble(nomina.getBase_acc());

		Double ssContributions = toDouble(nomina.getImporte_cuotas());

		Double moneyIrpfBase = toDouble(nomina.getBase_irpf());
		Double kindIrpfBase = toDouble(nomina.getBase_especie());
		Double irpfBase = moneyIrpfBase + kindIrpfBase;

		Double totalDeduction = toDouble(nomina.getTotal_deducir());

		Double totalLiquid = toDouble(nomina.getTotal_liquido());
		Double totalEnterprise = toDouble(nomina.getCuota_empresa());

		Rel_epp_per rel_epp_per = new Rel_epp_per();
		emprper.visitRel_epp_per(rel_epp_per);

		Rel_epp_emp rel_epp_emp = new Rel_epp_emp();
		emprper.visitRel_epp_emp(rel_epp_emp);

		Rel_epp_ccc rel_epp_ccc = new Rel_epp_ccc();
		emprper.visitRel_epp_ccc(rel_epp_ccc);

		SalaryType type = SalaryType.SALARY;

		if ("A".equals(nomina.getTipo())) {
			type = SalaryType.DELAY;
		}

		Double totalIrpf = toDouble(nomina.getImporte_irpf());

		java.sql.Date issueDate = nomina.getFecemi();
		if (issueDate == null)
			issueDate = nomina.getFecfin();
		java.sql.Date chargeDate = nomina.getFeccob();
		//if (chargeDate == null)
		//	chargeDate = nomina.getFeccob();
		if (chargeDate == null)
			chargeDate = issueDate ; // nomina.getFecfin();

		this.salaryId = mysqlDB.insertSalary(enum2short(type), contractId,
				nomina.getFecini(), nomina.getFecfin(),
				rel_epp_emp.getEmprnif_Descripcion(), nomina.getLocalidad(),
				rel_epp_emp.getEmprnif_Numdoc(),
				rel_epp_ccc.getEmprccc_Descripcion(), 
				enum2short(com.esferalia.aon.payroll.enumeration.SSRegimeType.GENERAL), // TODO; SS ? 		
				nomina.getNomper(),
				rel_epp_per.getPersona_Numss(),
				rel_epp_per.getPersona_Numdoc(),
				nomina.getFecant(),
				nomina.getCodbas(),
				nomina.getDescat(),
				nomina.getNummat(),
				nomina.getDiasnomina(), // TODO: Dias efectivos ..
				totalPayment, totalDeduction, totalLiquid, totalEnterprise,
				issueDate, renumeration, proExtBase, itBase, rawCgcBase,
				cgcBase, hextraBase, nonHextraBase, cgpBase, moneyIrpfBase,
				kindIrpfBase, irpfBase, ssContributions, totalIrpf, chargeDate);

		insert_Fvisione(nomina);
		insert_Profesion(nomina);

		Double importeCg = toDouble(nomina.getImporte_cg());
		if (importeCg > 0) {
			double cgPercentage = toDouble(nomina.getPrc_cg());

			if (cgPercentage == 0.00) {
				cgPercentage = importeCg / cgcBase * 100;
				if (Math.abs(4.70 - cgPercentage) < 0.02) {
					cgPercentage = 4.70;
				}
				MysqlDB.info("nomina[{}]: Calculating prc_cgc {}/{} = {}.",
						nomina.getCdg(), importeCg, cgcBase,
						String.format(SPANISH, "%.2f", cgPercentage));

			}

			String cgFunction = String.format(SPANISH, "%.2f", cgPercentage);
			mysqlDB.insertSalary_deduction(this.salaryId,
					enum2short(DeductionType.COMMON_CONTINGENCY), "CGC",
					cgFunction, null, importeCg);
		}

		Double importeAcc = toDouble(nomina.getImporte_acc());

		if (importeAcc > 0) {

			double accPercentage = toDouble(nomina.getPrc_acc());

			if (accPercentage == 0.00) {
				accPercentage = importeAcc / cgpBase * 100;
				if (Math.abs(1.65 - accPercentage) < 0.02) {
					accPercentage = 1.65;
				}
				if (Math.abs(1.70 - accPercentage) < 0.02) {
					accPercentage = 1.70;
				}
				MysqlDB.info("nomina[{}]: Calculating Acc {}/{} = {}.",
						nomina.getCdg(), importeAcc, cgpBase,
						String.format(SPANISH, "%.2f", accPercentage));
			}

			if (accPercentage == 1.65 || accPercentage == 1.70) {
				double jobPercentage = 0.10;
				double uePercentage = accPercentage - jobPercentage;

				double importeJob = jobPercentage * importeAcc / accPercentage;
				String jobFunction = String.format(SPANISH, "%.2f",
						jobPercentage);
				mysqlDB.insertSalary_deduction(this.salaryId,
						enum2short(DeductionType.JOB_TRAINING), "FP",
						jobFunction, null, importeJob);

				String ueFunction = String
						.format(SPANISH, "%.2f", uePercentage);
				mysqlDB.insertSalary_deduction(this.salaryId,
						enum2short(DeductionType.UNEMPLOYMENT), "DESMP",
						ueFunction, null, importeAcc - importeJob);
			} else {
				String accFunction = String.format(SPANISH, "%.2f",
						accPercentage);
				mysqlDB.insertSalary_deduction(this.salaryId,
						enum2short(DeductionType.PROFESSIONAL_CONTINGENCY),
						"CGP", accFunction, null, importeAcc);
			}
		}

		Double importeHex = toDouble(nomina.getImporte_hex());
		if (importeHex > 0) {
			BigDecimal hexPercentage = nomina.getPrc_hex();
			String hexFunction = String.format(SPANISH, "%.2f",
					hexPercentage != null ? hexPercentage : 0);
			mysqlDB.insertSalary_deduction(this.salaryId,
					enum2short(DeductionType.STRUCTURAL_OVERTIME), "ESTR",
					hexFunction, null, importeHex);
		}

		Double importeHexNo = toDouble(nomina.getImporte_hexno());
		if (importeHexNo > 0) {
			BigDecimal hexNoPercentage = nomina.getPrc_hexno();
			String hexNoFunction = String.format("SPANISH, %.2f",
					hexNoPercentage != null ? hexNoPercentage : 0);
			mysqlDB.insertSalary_deduction(this.salaryId,
					enum2short(DeductionType.NON_STRUCTURAL_OVERTIME), "NESTR",
					hexNoFunction, null, importeHexNo);
		}

		Double importeIrpf = toDouble(nomina.getImporte_irpf());

		if (importeIrpf > 0) {
			BigDecimal irpfPercentage = nomina.getPrc_irpf();
			String irpfFunction = String.format(SPANISH, "%.2f",
					irpfPercentage != null ? irpfPercentage : 0);
			mysqlDB.insertSalary_deduction(this.salaryId,
					enum2short(DeductionType.IRPF), "IRPF", irpfFunction, null,
					importeIrpf);
		}

		Double importeIrpfEspecie = toDouble(nomina.getBase_irpf_especie());

		if (importeIrpfEspecie > 0) {
			BigDecimal irpfPercentage = nomina.getPrc_irpf();
			String irpfFunction = String.format(SPANISH, "%.2f",
					irpfPercentage != null ? irpfPercentage : 0);
			mysqlDB.insertSalary_deduction(this.salaryId,
					enum2short(DeductionType.IRPF), "IRPFE", irpfFunction,
					null, importeIrpfEspecie);
		}

		nomina.visitRel_nmd_nom(this);
		nomina.visitRel_dto_nom(this);

		if ("A".equals(nomina.getTipo())) {
//			nomina.visitNominadf_nomina(this);
		}

		add(contractId, nomina.getFecnew());
	}

//	@Override
//	public void visitNominadf_nomina(Nominadf nominadf, Nomina nomina)
//			throws SQLException {
//		mysqlDB.insertSalary_data(ContextVariable.CGC_BASE.getName(),
//				String.format("%.3f", nominadf.getBase_cg()),
//				nominadf.getFecini(), nominadf.getFecfin(), salaryId);
//		mysqlDB.insertSalary_data(ContextVariable.CGP_BASE.getName(),
//				String.format("%.3f", nominadf.getBase_acc()),
//				nominadf.getFecini(), nominadf.getFecfin(), salaryId);
//	}

//	@Override
//	public void visitNominaexdf_nomina(Nominaexdf nominaexdf, Nomina nomina)
//			throws SQLException {
//	}

	@Override
	public void visitRel_nmd_nom(Nominadev nominadev, Nomina nomina)
			throws SQLException {

		String description = nominadev.getDescom();
		String codCom = nominadev.getCodcom();
		String dinEsp = nominadev.getDinesp();

		Concept<PaymentType> concept = concepts.getPaymentConcept(codCom);

		String paymentConcept = concept != null ? concept.code : MyConcept
				.formatCode(codCom);

		PaymentType type = concept != null ? concept.type : mysqlDB
				.getPaymentType(description, dinEsp, null);

		BigDecimal importe = nominadev.getImporte();
		BigDecimal impuni = nominadev.getImpuni();
		BigDecimal unidades = nominadev.getUnidades();

		String function = mysqlDB.getFunction(importe, impuni, unidades);

		Integer salaryPayment = mysqlDB.insertSalary_payment(
				this.salaryId,
				enum2short(type), 
				paymentConcept, 
				description, 
				function,
				importe != null ? importe.doubleValue() : 0.00,
				importe != null ? importe.doubleValue() : 0.00, // TODO: irpf
				importe != null ? importe.doubleValue() : 0.00	// TODO: quote
				
				);
		if (unidades != null && unidades.doubleValue() > 0 && impuni != null
				&& impuni.doubleValue() > 0) {
			mysqlDB.insertSalary_data(String.format("%d_UNITS", salaryPayment),
					String.format("%.3f", unidades), nomina.getFecini(),
					nomina.getFecfin(), this.salaryId);
			mysqlDB.insertSalary_data(
					String.format("%d_UNIT_AMOUNT", salaryPayment),
					String.format("%.3f", impuni), nomina.getFecini(),
					nomina.getFecfin(), this.salaryId);
		}

	}

	@Override
	public void visitRel_dto_nom(Nomdto nomdto, Nomina nomina)
			throws SQLException {

		String concepto = nomdto.getConcepto();

		DeductionType type = MyContract.getDeductionType(concepto);

		Double importe = toDouble(nomdto.getImporte());

		String function = String.format("%.3f", importe != null ? importe : 0);

		if (!contracts.hasEmbargo(concepto)) {
			mysqlDB.insertSalary_deduction(this.salaryId, enum2short(type),
					null, concepto, function, importe);
		} else {
			FullEmbargo embargo = contracts.getEmbargo(concepto);

			Salary_embargo salaryEmbargo = new Salary_embargo();
			salaryEmbargo.amount = importe;
			salaryEmbargo.description = concepto;
			salaryEmbargo.salary = this.salaryId;

			embargo.addEmbargo(salaryEmbargo, nomina.getFecfin());

		} // Es un embargo
	}

	@Override
	public void visitRel_pex_per(Nominaex nominaex, Emprper emprper)
			throws SQLException {

		if (contracts.outOfDate(nominaex.getFecfin()))
			return;

		if (!checkFVisionado(nominaex.getFecini(), nominaex.getFvisione(),
				nominaex.getFvisiont())) {
			return;
		}

		Integer contractId = contracts.getContractId(emprper.getCdg());
		if (contractId == null) {
			MysqlDB.error("nomina[{}]: Contract not found for {}.",
					nominaex.getCdg(), emprper.getCdg());
			return;
		}

		Double totalPayment = toDouble(nominaex.getImporte());
		Double totalDeduction = toDouble(nominaex.getTotal_deducir());
		Double totalLiquid = toDouble(nominaex.getLiquido());

		Double baseIRPF = totalPayment;

		Rel_epp_per rel_epp_per = new Rel_epp_per();
		emprper.visitRel_epp_per(rel_epp_per);

		Rel_epp_emp rel_epp_emp = new Rel_epp_emp();
		emprper.visitRel_epp_emp(rel_epp_emp);

		Rel_epp_ccc rel_epp_ccc = new Rel_epp_ccc();
		emprper.visitRel_epp_ccc(rel_epp_ccc);

		Double totalIrpf = toDouble(nominaex.getImpirpf());

		java.sql.Date issueDate = nominaex.getFecemi();
		if (issueDate == null)
			issueDate = nominaex.getFecfin();

		java.sql.Date chargeDate = nominaex.getFeccob();
		//if (chargeDate == null)
		//	chargeDate = nominaex.getFeccob();
		if (chargeDate == null)
			chargeDate = issueDate ; //nominaex.getFecfin();

		this.salaryId = mysqlDB.insertSalary(
				enum2short(SalaryType.EXTRA),
				contractId, nominaex.getFecini(), 
				nominaex.getFecfin(),
				rel_epp_emp.getEmprnif_Descripcion(), 
				nominaex.getLocalidad(),
				rel_epp_emp.getEmprnif_Numdoc(),
				rel_epp_ccc.getEmprccc_Descripcion(), 
				enum2short(com.esferalia.aon.payroll.enumeration.SSRegimeType.GENERAL), // TODO; SS ? 		
				nominaex.getNomper(),
				rel_epp_per.getPersona_Numss(),
				rel_epp_per.getPersona_Numdoc(), 
				nominaex.getFecant(), 
				null,
				nominaex.getDescat(),
				nominaex.getNummat(),
				0, // TODO: Dias efectivos ..
				totalPayment, 
				totalDeduction, 
				totalLiquid, 
				0.00, 
				issueDate,
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
				0.00, 
				totalIrpf, 
				chargeDate);
		insert_Fvisione(nominaex);
		insert_Profesion(nominaex);
		

		String function = String.format(SPANISH, "%.2f",
				totalPayment != null ? totalPayment : 0);

		Concept<PaymentType> concept = concepts.getPaymentConcept(nominaex
				.getCodcom());

		String paymentConcept = concept != null ? concept.code : MyConcept
				.formatCode(nominaex.getCodcom());

		PaymentType type = concept != null ? concept.type : mysqlDB
				.getPaymentType(nominaex.getDescom(), "D", null);

		mysqlDB.insertSalary_payment(
				this.salaryId, 
				enum2short(type),
				paymentConcept, 
				nominaex.getDescom(), 
				function, 
				totalPayment,
				totalPayment, 	// TODO: irpf
				totalPayment	// TODO: quote
				);

		Double importeIrpf = toDouble(nominaex.getImpirpf());

		if (importeIrpf > 0) {
			BigDecimal irpfPercentage = nominaex.getIrpf();
			String irpfFunction = String.format(SPANISH, "%.2f",
					irpfPercentage != null ? irpfPercentage : 0);
			mysqlDB.insertSalary_deduction(this.salaryId,
					enum2short(DeductionType.IRPF), "IRPF", irpfFunction, null,
					importeIrpf);

		}

		nominaex.visitNomdtoex_nominaex(this);

		return;
	}

	@Override
	public void visitNomdtoex_nominaex(Nomdtoex nomdtoex, Nominaex nominaex)
			throws SQLException {
		String concepto = nomdtoex.getConcepto();

		DeductionType type = MyContract.getDeductionType(concepto);

		Double importe = toDouble(nomdtoex.getImporte());

		String function = String.format("%.3f", importe != null ? importe : 0);

		if (!contracts.hasEmbargo(concepto)) {
			mysqlDB.insertSalary_deduction(this.salaryId, enum2short(type),
					null, concepto, function, importe);
		} else {
			FullEmbargo embargo = contracts.getEmbargo(concepto);

			Salary_embargo salaryEmbargo = new Salary_embargo();
			salaryEmbargo.amount = importe;
			salaryEmbargo.description = concepto;
			salaryEmbargo.salary = this.salaryId;

			embargo.addEmbargo(salaryEmbargo, nominaex.getFecfin());
		}
	}

	private static class FiniquitoNomina extends DefaultCtsqlDBVisitor {

		private Finiquito finiquito;

		private String enterpriseName;
		private String enterpriseAddress;

		private String employeeName;
		private java.sql.Date seniorityDate;
		private String quoteGroup;
		private String category;
		private Integer registration;

		public FiniquitoNomina(Finiquito finiquito) {
			this.finiquito = finiquito;
		}

		@Override
		public void visitRel_nom_per(Nomina nomina, Emprper emprper)
				throws SQLException {

			if (finiquito.getFecbaj().equals(nomina.getFecfin())) {
				enterpriseName = nomina.getNomemp();
				enterpriseAddress = nomina.getLocalidad();

				employeeName = nomina.getNomper();
				seniorityDate = nomina.getFecant();
				quoteGroup = nomina.getCodbas();
				category = nomina.getDescat();
				registration = nomina.getNummat();

			}

		}
	}

	@Override
	public void visitRel_fin_epp(Finiquito finiquito, Emprper emprper)
			throws SQLException {

		if (this.contracts.outOfDate(finiquito.getFecbaj()))
			return;

		if (!checkFVisionado(finiquito.getFecbaj(), finiquito.getFvisione(),
				finiquito.getFvisiont())) {
			return;
		}

		Integer contractId = contracts.getContractId(emprper.getCdg());
		if (contractId == null) {
			MysqlDB.error("nomina[{}]: Contract not found for {}.",
					finiquito.getCdg(), emprper.getCdg());
			return;
		}

		Double totalPayment = toDouble(finiquito.getTotal_conceptos());

		Double rawCgcBase = toDouble(finiquito.getBasecg());
		Double cgcBase = toDouble(finiquito.getBasecg());
		Double cgpBase = toDouble(finiquito.getBaseacc());

		Double importeCg = toDouble(finiquito.getImportecg());
		Double importeAcc = toDouble(finiquito.getImporteacc());

		Double ssContributions = importeCg + importeAcc;

		Double moneyIrpfBase = toDouble(finiquito.getBase());
		Double irpfBase = moneyIrpfBase;

		Double importeIrf = toDouble(finiquito.getImporte_irpf());

		Double totalDeduction = importeIrf + ssContributions; // TODO : Faltan
																// los
																// descuentos

		Double totalLiquid = toDouble(finiquito.getLiquido());
		Double totalEnterprise = toDouble(finiquito.getCostessemp());

		SalaryType type = SalaryType.SETTLE;

		FiniquitoNomina nomina = new FiniquitoNomina(finiquito);
		emprper.visitRel_nom_per(nomina);

		Rel_epp_per rel_epp_per = new Rel_epp_per();
		emprper.visitRel_epp_per(rel_epp_per);

		Rel_epp_emp rel_epp_emp = new Rel_epp_emp();
		emprper.visitRel_epp_emp(rel_epp_emp);

		Rel_epp_ccc rel_epp_ccc = new Rel_epp_ccc();
		emprper.visitRel_epp_ccc(rel_epp_ccc);

		if (nomina.registration == null) {
			MysqlDB.error("finiquito[{}]: Nomina not found for {}.",
					finiquito.getCdg(), emprper.getCdg());
			nomina.registration = 0;
		}

		
		Double totalIrpf = toDouble(finiquito.getImporte_irpf());

		java.sql.Date issuDate = finiquito.getFeccobreal();
		if (issuDate == null)
			issuDate = finiquito.getFecbaj();

		java.sql.Date chargeDate = finiquito.getFeccobreal();
		if (chargeDate == null)
			chargeDate = finiquito.getFecbaj();

		this.salaryId = mysqlDB.insertSalary(enum2short(type), contractId,
				emprper.getFecalt(), finiquito.getFecbaj(),
				nomina.enterpriseName, nomina.enterpriseAddress,
				rel_epp_emp.getEmprnif_Numdoc(),
				rel_epp_ccc.getEmprccc_Descripcion(),
				enum2short(com.esferalia.aon.payroll.enumeration.SSRegimeType.GENERAL), // TODO; SS ? 		
				nomina.employeeName,
				rel_epp_per.getPersona_Numss(),
				rel_epp_per.getPersona_Numdoc(), nomina.seniorityDate,
				nomina.quoteGroup, nomina.category, nomina.registration,
				0, // nomina.getDiasnomina(), // TODO:???? ..
				totalPayment, totalDeduction, totalLiquid, totalEnterprise,
				issuDate, totalPayment, // renumeration,
				0.00, // proExtBase,
				0.00, // itBase,
				rawCgcBase, cgcBase, 0.00, // hextraBase,
				0.00, // nonHextraBase,
				cgpBase, moneyIrpfBase, 0.00, // kindIrpfBase,
				irpfBase, ssContributions, totalIrpf, chargeDate);
		
		insert_Fvisione( finiquito.getFvisione(), emprper.getFecalt(), finiquito.getFecbaj() );

		// mysqlDB.insertSalary_payment(salary, type, payment_concept,
		// description, expression, amount);

		Integer noHolidays = finiquito.getDiasvac();
		double vacImporte = toDouble(finiquito.getVacimporte());
		if (noHolidays > 0 && vacImporte > 0) {
			mysqlDB.insertContract_data(ContextVariable.NO_HOLIDAYS.getName(),
					contractId, noHolidays.toString(), finiquito.getFecbaj(),
					finiquito.getFecbaj());
			mysqlDB.insertContract_data(
					ContextVariable.HOLIDAY_AMOUNT.getName(), contractId,
					String.format("%.3f", (vacImporte / noHolidays)),
					finiquito.getFecbaj(), finiquito.getFecbaj());
		} // end-if vacacines que cotizan,
		else if (vacImporte > 0) {
			Integer conceptId = mysqlDB.getPaymentConceptId(
					mysqlDB.getDefaultDomain(), "FIVAC");
			mysqlDB.insertContract_payment(null, contractId, conceptId, null,
					(short) 1, String.format("%.3f", vacImporte), null, "0.00", // NO
																				// cotizan
					finiquito.getFecbaj(), null, finiquito.getFecbaj(), null);
		} // vacaciones que no cotizan

		if (vacImporte > 0) {
			mysqlDB.insertSalary_payment(
					salaryId,
					MysqlDB.enum2short(PaymentType.CRA_0001), 
					"FIVAC",
					"Vacaciones no disfrutadas", 
					null, 
					vacImporte,
					vacImporte,
					vacImporte);
		}

		Double importeIrpf = toDouble(finiquito.getImporte_irpf());

		if (importeIrpf > 0) {
			BigDecimal irpfPercentage = finiquito.getIrpf();
			String description = String.format(SPANISH, "%.2f",
					irpfPercentage != null ? irpfPercentage : 0);
			mysqlDB.insertSalary_deduction(this.salaryId,
					enum2short(DeductionType.IRPF), "IRPF", description, null,
					importeIrpf);

		}
		if (importeCg > 0) {
			BigDecimal cgPercentage = finiquito.getPrccg();
			String cgFunction = String.format(SPANISH, "%.2f",
					cgPercentage != null ? cgPercentage : 0);
			mysqlDB.insertSalary_deduction(this.salaryId,
					enum2short(DeductionType.COMMON_CONTINGENCY), "CGC",
					cgFunction, null, importeCg);
		}

		if (importeAcc > 0) {

			double accPercentage = toDouble(finiquito.getPrcacc());

			if (accPercentage == 1.65 || accPercentage == 1.70) {
				double jobPercentage = 0.10;
				double uePercentage = accPercentage - jobPercentage;

				double importeJob = jobPercentage * importeAcc / accPercentage;
				String jobFunction = String.format(SPANISH, "%.2f",
						jobPercentage);
				mysqlDB.insertSalary_deduction(this.salaryId,
						enum2short(DeductionType.JOB_TRAINING), "FP",
						jobFunction, null, importeJob);

				String ueFunction = String
						.format(SPANISH, "%.2f", uePercentage);
				mysqlDB.insertSalary_deduction(this.salaryId,
						enum2short(DeductionType.UNEMPLOYMENT), "DESMP",
						ueFunction, null, importeAcc - importeJob);
			} else {
				String accFunction = String.format(SPANISH, "%.2f",
						accPercentage);
				mysqlDB.insertSalary_deduction(this.salaryId,
						enum2short(DeductionType.PROFESSIONAL_CONTINGENCY),
						"CGP", accFunction, null, importeAcc);
			}
		}

		finiquito.visitRel_fpe_fin(this);
		finiquito.visitRel_fii_fin(this);
		finiquito.visitRel_fid_fin(this);
	}

	@Override
	public void visitRel_fpe_fin(Finipext finipext, Finiquito finiquito)
			throws SQLException {

		String description = finipext.getDescom();
		String codCom = finipext.getCodcom();
		String dinEsp = "D"; // Dinerito

		Concept<PaymentType> concept = concepts.getPaymentConcept(codCom);

		String paymentConcept = concept != null ? concept.code : MyConcept
				.formatCode(codCom);

		PaymentType type = concept != null ? concept.type : mysqlDB
				.getPaymentType(description, dinEsp, null);

		double importe = toDouble(finipext.getImporte());
		

		mysqlDB.insertSalary_payment(
				this.salaryId, 
				enum2short(type),
				paymentConcept, 
				description, 
				null, 
				importe,
				0.00, // TODO: 0.00 ?
				0.00  // TODO: 0.00 ?
				);
	}

	@Override
	public void visitRel_fii_fin(Finindem finindem, Finiquito finiquito)
			throws SQLException {
		String description = finindem.getTexto();

		double importe = toDouble(finindem.getImporte());
		boolean sujetoAIrpf = "S".equalsIgnoreCase(finindem.getIrpf());
		
		// TODO: CRA001 ?
		mysqlDB.insertSalary_payment(
				this.salaryId,
				enum2short(PaymentType.CRA_0001),
				"INDEM", 
				description, 
				null, 
				importe,
				sujetoAIrpf ? importe: 0.00,
				0.00
				);

		String irpf = finindem.getIrpf();

		Integer conceptId = mysqlDB.getPaymentConceptId(
				mysqlDB.getDefaultDomain(), "INDEM");
		mysqlDB.insertContract_payment(null, contractId, conceptId,
				description, (short) 1, String.format("%.3f", importe),
				"S".equals(irpf) ? "INDEM" : null, null, finiquito.getFecbaj(),
				null, finiquito.getFecbaj(), null);
	}

	@Override
	public void visitRel_fid_fin(Finidto finidto, Finiquito finiquito)
			throws SQLException {

		String description = finidto.getTexto();
		Double importe = toDouble(finidto.getImporte());

		DeductionType type = MyContract.getDeductionType(description);

		mysqlDB.insertSalary_deduction(this.salaryId, enum2short(type), null,
				null, description, importe);

		mysqlDB.insertContract_deduction(enum2short(type), null, contractId,
				description, (short) 1, String.format("%s ? %.3f : 0.00",
						ContextVariable.SETTLE, importe),
				finiquito.getFecbaj(), finiquito.getFecbaj(), null);
	}

	private void add(Integer contractId, Date endDate) {
		lastSalary = Period.max(lastSalary, endDate);
	}

	private boolean checkFVisionado(Date fecini, Date fvisione, Date fvisiont) {
		if (!this.contracts.checkFVisionado())
			return true;

		if (fvisione != null)
			if (fecini.compareTo(fvisione) > 0)
				return false;
		if (fvisiont != null)
			if (fecini.compareTo(fvisiont) > 0)
				return false;

		return true;
	}
	
	private void insert_Profesion(Nomina nomina) throws SQLException{
		insert_Profession(nomina.getProfesion(), nomina.getFecini(), nomina.getFecfin());
	}

	private void insert_Profesion(Nominaex nomina) throws SQLException{
		insert_Profession(nomina.getProfesion(), nomina.getFecini(), nomina.getFecfin());
	}

	private void insert_Fvisione(Nomina nomina) throws SQLException{
		insert_Fvisione(nomina.getFvisione(), nomina.getFecini(), nomina.getFecfin());
		
	}
	private void insert_Fvisione(Nominaex nomina) throws SQLException{
		insert_Fvisione(nomina.getFvisione(), nomina.getFecini(), nomina.getFecfin());
		
	}


	private void insert_Fvisione(java.sql.Date fVisione, java.sql.Date fecIni, java.sql.Date fecFin) throws SQLException{
		mysqlDB.insertSalary_data(
				ContextVariable.ENTERPRISE_SITE_DATE.getName(),
				fVisione == null ? null : String.format("%1$tY%1$tm%1$td", fVisione),
				fecIni, fecFin, this.salaryId);
		
	}

	private void insert_Profession(String profesion, java.sql.Date fecIni, java.sql.Date fecFin) throws SQLException{
		if ( profesion == null )
			return;
		mysqlDB.insertSalary_data(
				ContextVariable.PROFESSION.getName(),
				profesion,
				fecIni, fecFin, this.salaryId);
		
	}

}
