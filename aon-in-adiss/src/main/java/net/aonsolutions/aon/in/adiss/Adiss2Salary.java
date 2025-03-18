package net.aonsolutions.aon.in.adiss;

import static com.esferalia.aon.jooq.tables.DeductionConcept.DEDUCTION_CONCEPT;
import static com.esferalia.aon.jooq.tables.Salary.SALARY;
import static com.esferalia.aon.jooq.tables.SalaryCost.SALARY_COST;
import static com.esferalia.aon.jooq.tables.SalaryData.SALARY_DATA;
import static com.esferalia.aon.jooq.tables.SalaryDeduction.SALARY_DEDUCTION;
import static com.esferalia.aon.jooq.tables.SalaryPayment.SALARY_PAYMENT;
import static net.aonsolutions.aon.in.adiss.Adiss2Aon.allZero;
import static net.aonsolutions.aon.in.adiss.Adiss2Aon.firstNotBlank;
import static net.aonsolutions.aon.in.adiss.Adiss2Aon.getDate;
import static net.aonsolutions.aon.in.adiss.Adiss2Aon.toDouble;
import static net.aonsolutions.aon.in.adiss.Adiss2Aon.toInt;

import java.math.BigDecimal;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

import org.jooq.DSLContext;
import org.jooq.Field;

import com.esferalia.aon.jooq.tables.records.ContractRecord;
import com.esferalia.aon.jooq.tables.records.DeductionConceptRecord;
import com.esferalia.aon.jooq.tables.records.SalaryCostRecord;
import com.esferalia.aon.jooq.tables.records.SalaryDataRecord;
import com.esferalia.aon.jooq.tables.records.SalaryDeductionRecord;
import com.esferalia.aon.jooq.tables.records.SalaryPaymentRecord;
import com.esferalia.aon.jooq.tables.records.SalaryRecord;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

import net.aonsolutions.adiss.querydsl.CENTROS;
import net.aonsolutions.adiss.querydsl.CONCEPTOSREGISTROS;
import net.aonsolutions.adiss.querydsl.EMPRESAS;
import net.aonsolutions.adiss.querydsl.NOMINAS;
import net.aonsolutions.adiss.querydsl.NOMINASCONCEPTOS;
import net.aonsolutions.adiss.querydsl.NOMINASEXT;
import net.aonsolutions.adiss.querydsl.TRABAJADORES;
import net.aonsolutions.adiss.querydsl.V_RECIBO_NOMINA;

class Adiss2Salary {

	private Adiss2Salary() {
	}

	static SalaryRecord insertSalary(NOMINAS nomina, TRABAJADORES trabajador, CENTROS centro, EMPRESAS empresa,
			ContractRecord contractRecord, DSLContext dslContext) {
		// SALARY
		// EXTRA
		// SETTLE
		// DELAY

		byte type = 0;

		Date startDate = getDate(nomina.getFECHANOMINAINI());
		Date endDate = getDate(nomina.getFECHANOMINAFIN());

		// re-entrant by contract & start_date & end_date
		SalaryRecord salaryRecord = dslContext.selectFrom(SALARY)
				.where(SALARY.CONTRACT.eq(contractRecord.getId()).and(SALARY.TYPE.eq(type))
						.and(SALARY.START_DATE.eq(startDate)).and(SALARY.END_DATE.eq(endDate)))
				.fetchOptional().orElseGet(() -> dslContext.newRecord(SALARY));
		salaryRecord.setType(type);
		salaryRecord.setDomain(contractRecord.getDomain());
		salaryRecord.setContract(contractRecord.getId());

		salaryRecord.setStartDate(startDate);
		salaryRecord.setEndDate(endDate);
		salaryRecord.setIssueDate(endDate);
		salaryRecord.setChargeDate(endDate);

		salaryRecord.setEmployeeName(nomina.getNOMTRABAJADOR());
		salaryRecord.setEmployeeDocument(nomina.getNIFTRABAJADOR());
		salaryRecord.setSocialSecurityNumber(Adiss2Contract.getSocialSecurityNum(trabajador));

		salaryRecord.setEnterpriseName(empresa.getNOMEMPRESA());
		salaryRecord.setEnterpriseDocument(empresa.getCIF());
		salaryRecord.setCcc(Adiss2Aon.getCcc(centro));

		salaryRecord.setEnterpriseAddress(getEnterpriseAddress(empresa));

		int timeUnits = toInt(nomina.getDIASCOTIZACION(), nomina.getDIASNATURALES());
		salaryRecord.setTimeUnits(timeUnits);

		salaryRecord.setCgcBase(toDouble(nomina.getIMPORTE()));
		salaryRecord.setCgpBase(toDouble(nomina.getIMPORTE2()));
		salaryRecord.setIrpfBase(toDouble(nomina.getIMPORTE11()));
		salaryRecord.setMoneyIrpfBase(toDouble(nomina.getIMPORTE11()));
		salaryRecord.setTotalEnterprise(toDouble(nomina.getIMPORTE6()));
		salaryRecord.setTotalLiquid(toDouble(nomina.getIMPORTE10()));
		salaryRecord.setTotalIrpf(toDouble(nomina.getIMPORTE12()));
		salaryRecord.setTotalPayment(toDouble(nomina.getIMPORTE9()));
		salaryRecord.setProExtBase(toDouble(nomina.getPRORRATEOEXTRAS(), nomina.getIMPORTE4()));

		salaryRecord.setCategory(trabajador.getCATEGORIADESC());
		salaryRecord.setSeniorityDate(getDate(trabajador.getFECHAANTIGUEDAD()));

		// GRUPOTARIFA 0 = RETA
		salaryRecord.setSsRegime(AonNumberUtils.todouble(nomina.getGRUPOTARIFA()) == 0.00 ? (byte) 3 : (byte) 0);

		salaryRecord.setRegistration(0);
		salaryRecord.store();

		// COMMON_CONTINGENCY = 0
		// PROFESSIONAL_CONTINGENCY = 1
		// UNEMPLOYMENT = 2
		// JOB_TRAINING = 3
		// STRUCTURAL_OVERTIME = 4
		// NON_STRUCTURAL_OVERTIME = 5
		// IRPF = 6
		// ADVANCE_PAYMENT = 7
		// IN_KIND = 8
		// OTHER = 9
		// FOGASA = 10
		// IT = 11
		// INS = 12
		// MEI = 13
		// SOLIDARITY = 14

		return salaryRecord;
	}

	static List<SalaryDataRecord> insertSalaryDatas(NOMINAS nomina, TRABAJADORES trabajador, CENTROS centro,
			EMPRESAS empresa, SalaryRecord salaryRecord, DSLContext dslContext) {

		List<SalaryDataRecord> salaryDataRecords = new ArrayList<>();

		Date startDate = salaryRecord.getStartDate();
		Date endDate = salaryRecord.getEndDate();

		if (AonNumberUtils.between(AonNumberUtils.toint(nomina.getTIPOCONTRATO()), 100, 990))
			insertSalaryData(dslContext, startDate, endDate, salaryRecord, "TC2",
					"\"" + nomina.getTIPOCONTRATO() + "\"").ifPresent(salaryDataRecords::add);

		if (AonNumberUtils.between(AonNumberUtils.toint(nomina.getGRUPOTARIFA()), 1, 10))
			insertSalaryData(dslContext, startDate, endDate, salaryRecord, "GRUPO_COTIZACION",
					String.format("\"%s\"", AonStringUtils.leftPad(nomina.getGRUPOTARIFA(), 2, '0')))
					.ifPresent(salaryDataRecords::add);

		insertSalaryData(dslContext, startDate, endDate, salaryRecord, "PORCENTAJE_IRPF", nomina.getPORIRPF())
				.ifPresent(salaryDataRecords::add);
		insertSalaryData(dslContext, startDate, endDate, salaryRecord, "COEFICIENTE_PARCIALIDAD",
				toDouble(nomina.getPRCNTJORNADA()) / 100.00).ifPresent(salaryDataRecords::add);

		return salaryDataRecords;
	}

	static List<SalaryDeductionRecord> insertSalaryDeductions(NOMINAS nomina, TRABAJADORES trabajador, CENTROS centro,
			EMPRESAS empresa, SalaryRecord salaryRecord, DSLContext dslContext) {
		return insertSalaryDeduction(dslContext, salaryRecord, "IRPF", salaryRecord.getTotalIrpf())
				.map(Collections::singletonList).orElseGet(Collections::emptyList);
	}

	static List<SalaryCostRecord> insertSalaryCosts(NOMINAS nomina, TRABAJADORES trabajador, CENTROS centro,
			EMPRESAS empresa, SalaryRecord salaryRecord, DSLContext dslContext) {
		List<SalaryCostRecord> salaryCostRecords = new ArrayList<>();
		insertSalaryCost(dslContext, salaryRecord, "CGC_E", nomina.getSEGSOCEMPCONTCOMU(), (byte) 0, null)
				.ifPresent(salaryCostRecords::add);
		insertSalaryCost(dslContext, salaryRecord, "DESMPL_E", nomina.getSEGSOCEMPDESEMPLEO(), (byte) 2, null)
				.ifPresent(salaryCostRecords::add);
		insertSalaryCost(dslContext, salaryRecord, "FOGASA_E", nomina.getSEGSOCEMPFOGASA(), (byte) 10, null)
				.ifPresent(salaryCostRecords::add);
		insertSalaryCost(dslContext, salaryRecord, "FP_E", nomina.getSEGSOCEMPFORMPROF(), (byte) 3, null)
				.ifPresent(salaryCostRecords::add);
		insertSalaryCost(dslContext, salaryRecord, "IT_E", nomina.getSEGSOCEMPIT(), (byte) 11, null)
				.ifPresent(salaryCostRecords::add);
		insertSalaryCost(dslContext, salaryRecord, "IMS_E", nomina.getSEGSOCEMPIMS(), (byte) 12, null)
				.ifPresent(salaryCostRecords::add);
		return salaryCostRecords;
	}

	static String getEnterpriseAddress(EMPRESAS empresa) {
		return Adiss2Aon.join(" ", empresa.getVIA(), empresa.getCPMUNICIPIO(), empresa.getMUNICIPIO(),
				empresa.getLOCALIDAD());
	}

	static SalaryRecord insertExtra(NOMINASEXT extra, TRABAJADORES trabajador, CENTROS centro, EMPRESAS empresa,
			ContractRecord contractRecord, DSLContext dslContext) {
		return null;
	}

	static Optional<SalaryPaymentRecord> insertSalaryPayment(Optional<CONCEPTOSREGISTROS> registro,
			NOMINASCONCEPTOS concepto, NOMINAS nomina, TRABAJADORES trabajador, CENTROS centro, EMPRESAS empresa,
			SalaryRecord salaryRecord, DSLContext dslContext) {

		int codigo = AonNumberUtils.toint(concepto.getCODCONCEPTO());
		String name = getNameFor(registro, concepto, SALARY_PAYMENT.PAYMENT_CONCEPT);
		String description = AonStringUtils
				.trim(registro.map(r -> firstNotBlank(r.getDESCRIPCION(), r.getDESCRIPCION2())).orElse(null));

//	    Tipo T: para importes fijos que se cobran por el total del período.
//	    Tipo N: para importes que se cobran por día natural (la aplicación multiplicará por el número de días naturales del período).
//	    Tipo L: para importes que se cobran por día laboral (la aplicación multiplicará por el número de días laborales del período).
//	    Tipo X: su tratamiento es idéntico al del día laboral, con la diferencia de que en los días X se consideran también laborales los festivos entre semana.
//	    Tipo F: sólo actúa si, posteriormente, se informan número de unidades desde el punto: "Calculo/ Gestión de Incidencias (código de incidencia 1 = Conceptos Variables)". La aplicación multiplicará el importe de este concepto por el número de unidades entradas. Es de utilidad para calcular horas extras, dietas, kilometraje,...
//	    Tipo S: son literales que no afectan a los importes. Permiten reflejar en la nómina literales a nivel informativo.
//	    Tipo H: el importe se multiplicará por las horas realizadas en el período.
	    
	    BigDecimal irpf = concepto.getIMPORTECONCEPTO();
		BigDecimal amount = concepto.getIMPORTECONCEPTO();
		BigDecimal quote = salaryRecord.getSsRegime() == 3 ? BigDecimal.ZERO : concepto.getIMPORTECONCEPTO();
		
		if (AonNumberUtils.between(codigo, 1, 399)) {
			// Del código 001 al 399. Conceptos que cotizan a todo (Contingencias
			// Comunes Accidentes) y tributan a I.R.P.F. Ejemplo: salario base, plus
			// convenio, etc.

			return insertSalaryPayment(dslContext, salaryRecord, name, description, amount, irpf, quote);
		} else if (AonNumberUtils.between(codigo, 400, 449)) {
			// Del código 400 al 449. Conceptos que sï¿½lo cotizan a Accidentes y tributan a
			// I.R.P.F. Ejemplo: horas extras.
			return insertSalaryPayment(dslContext, salaryRecord, name, description, amount, irpf, quote);
		} else if (AonNumberUtils.between(codigo, 450, 599)) {
			// Del código 450 al 599. Conceptos que sï¿½lo tributan a I.R.P.F. Ejemplo:
			// prestaciï¿½n de IT
			return insertSalaryPayment(dslContext, salaryRecord, name, description, amount, irpf, BigDecimal.ZERO);

		} else if (AonNumberUtils.between(codigo, 600, 699)) {
			// Del código 600 al 699. Conceptos que no cotizan, ni tributan. Ejemplo:
			// dietas.
			return insertSalaryPayment(dslContext, salaryRecord, name, description, amount, BigDecimal.ZERO,
					BigDecimal.ZERO);

		} else if (AonNumberUtils.between(codigo, 700, 799)) {
			// Del código 700 al 799. Conceptos de descuento. Significa que cualquier
			// concepto situado entre estos códigos se tratarï¿½ como un descuento a
			// efectos
			// del cï¿½lculo de la nï¿½mina.
			return Optional.empty();

		} else if (AonNumberUtils.between(codigo, 900, 912)) {
			// Del código 901 al 912. Conceptos de Pagas Extras. códigos de conceptos
			// que el
			// programa asigna automï¿½ticamente a las diferentes pagas extras que puede
			// tener
			// definidas el trabajador a nivel particular. A la paga extra nï¿½mero 1 le
			// corresponde el código 901, a la nï¿½mero 2 el código 902,... y asï¿½
			// sucesivamente. Este código aparecerï¿½ asociado al importe de la paga en la
			// hoja de salario. agrupaciones predefinidas conceptos
			return Optional.empty();

		} else if (AonNumberUtils.between(codigo, 800, 999)) {
			// Del código 800 al 999. Conceptos predefinidos que utiliza la aplicaciï¿½n
			// para
			// determinados cï¿½lculos internos como son las bases de cotizaciï¿½n, las
			// deducciones de Seguridad Social o la retenciï¿½n a cuenta del I.R.P.F. Estos
			// conceptos le serï¿½n de utilidad, posteriormente, para el Resumen de
			// Nï¿½mina,
			// Generador de Informes,...
			return Optional.empty();
		} else {
			return Optional.empty();

		}
	}

	static Optional<SalaryDeductionRecord> insertSalaryDeduction(Optional<CONCEPTOSREGISTROS> registro,
			NOMINASCONCEPTOS concepto, NOMINAS nomina, TRABAJADORES trabajador, CENTROS centro, EMPRESAS empresa,
			SalaryRecord salaryRecord, DSLContext dslContext) {

		int codigo = AonNumberUtils.toint(concepto.getCODCONCEPTO());

		if (AonNumberUtils.equals(codigo, 993)) {
			// COTIZACION ADIC. SOLIDARIDAD
			return insertSalaryDeduction(dslContext, salaryRecord, "SOLIDARIDAD_I", concepto.getIMPORTECONCEPTO());
		} else if (AonNumberUtils.equals(codigo, 994)) {
			// COTIZACION MEI
			return insertSalaryDeduction(dslContext, salaryRecord, "MEI", concepto.getIMPORTECONCEPTO());
		} else if (AonNumberUtils.equals(codigo, 995)) {
			// COTIZACION CONT.COMUNES
			return insertSalaryDeduction(dslContext, salaryRecord, "CGC", concepto.getIMPORTECONCEPTO());
		} else if (AonNumberUtils.equals(codigo, 996)) {
			// COTIZACION FORMACION PROF.
			return insertSalaryDeduction(dslContext, salaryRecord, "FP", concepto.getIMPORTECONCEPTO());
		} else if (AonNumberUtils.equals(codigo, 997)) {
			// COTIZACION DESEMPLEO.
			return insertSalaryDeduction(dslContext, salaryRecord, "DESMPL", concepto.getIMPORTECONCEPTO());
		} else {
			return Optional.empty();
		}
	}

	static void checkSalary(SalaryRecord salaryRecord, List<SalaryDataRecord> salaryDataRecords,
			List<SalaryPaymentRecord> salaryPaymentRecords, List<SalaryDeductionRecord> salaryDeductionRecords,
			List<SalaryCostRecord> salaryCostRecords) {
		// check remuneration
		Double paymentsIrpf = salaryPaymentRecords.stream()
				.collect(Collectors.summingDouble(SalaryPaymentRecord::getIrpf));
		Double paymentsQuote = salaryPaymentRecords.stream()
				.collect(Collectors.summingDouble(SalaryPaymentRecord::getQuote));
		Double paymentsAmount = salaryPaymentRecords.stream()
				.collect(Collectors.summingDouble(SalaryPaymentRecord::getAmount));

		assert AonNumberUtils.equals(salaryRecord.getCgcBase(), paymentsQuote)
				: "Salary's Common Contingencies Base (" + salaryRecord.getCgcBase() + ") and sum of payments ("
						+ paymentsQuote + ") are different." + salaryRecord.getId();

		assert AonNumberUtils.equals(salaryRecord.getIrpfBase(), paymentsIrpf)
				: "Salary's I.R.P.F Base (" + salaryRecord.getIrpfBase() + ") and sum of payments (" + paymentsIrpf
						+ ") are different." + salaryRecord.getId();

		salaryRecord.setRemuneration(paymentsAmount);

		Double costsAmount = salaryCostRecords.stream().collect(Collectors.summingDouble(SalaryCostRecord::getAmount));

//		assert AonNumberUtils.equals(salaryRecord.getTotalEnterprise(), costsAmount)
//		: "Salary's Total Enterprise (" + salaryRecord.getTotalEnterprise() + ") and sum of costs (" + costsAmount
//				+ ") are different." + salaryRecord.getId();

		salaryRecord.store();

	}

	static Optional<SalaryPaymentRecord> insertSalaryPayment(DSLContext dslContext, SalaryRecord salaryRecord,
			String name, String description, BigDecimal amount, BigDecimal irpf, BigDecimal quote) {
		return insertSalaryPayment(dslContext, salaryRecord, name, description, toDouble(amount), toDouble(irpf),
				toDouble(quote));
	}

	static Optional<SalaryPaymentRecord> insertSalaryPayment(DSLContext dslContext, SalaryRecord salaryRecord,
			String name, String description, Double amount) {
		return insertSalaryPayment(dslContext, salaryRecord, name, description, amount, amount, amount);
	}

	static Optional<SalaryPaymentRecord> insertSalaryPayment(DSLContext dslContext, SalaryRecord salaryRecord,
			String name, String description, String expression, BigDecimal amount, BigDecimal irpf, BigDecimal quote) {
		return insertSalaryPayment(dslContext, salaryRecord, name, description, toDouble(amount), toDouble(irpf),
				toDouble(quote));
	}

	static Optional<SalaryPaymentRecord> insertSalaryPayment(DSLContext dslContext, SalaryRecord salaryRecord,
			String name, String description, Double amount, Double irpf, Double quote) {
		if (allZero(amount, irpf, quote))
			return Optional.empty();
		SalaryPaymentRecord salaryPaymentRecord = dslContext.selectFrom(SALARY_PAYMENT)
				.where(SALARY_PAYMENT.SALARY.eq(salaryRecord.getId()).and(SALARY_PAYMENT.PAYMENT_CONCEPT.eq(name)))
				.fetchOptional().orElseGet(() -> dslContext.newRecord(SALARY_PAYMENT));
		salaryPaymentRecord.setSalary(salaryRecord.getId());
		salaryPaymentRecord.setDomain(salaryRecord.getDomain());
		salaryPaymentRecord.setPaymentConcept(name);
		salaryPaymentRecord.setDescription(description);
		salaryPaymentRecord.setAmount(amount);
		salaryPaymentRecord.setIrpf(irpf);
		salaryPaymentRecord.setQuote(quote);
		// TODO: salaryPaymentRecord.setExpression(...);
		salaryPaymentRecord.setType(null);
		salaryPaymentRecord.store();

		return Optional.of(salaryPaymentRecord);
	}

	static Optional<SalaryDeductionRecord> insertSalaryDeduction(DSLContext dslContext, SalaryRecord salaryRecord,
			String name, BigDecimal amount) {
		return insertSalaryDeduction(dslContext, salaryRecord, name, toDouble(amount));
	}

	static Optional<SalaryDeductionRecord> insertSalaryDeduction(DSLContext dslContext, SalaryRecord salaryRecord,
			String name, Double amount) {

		if (AonNumberUtils.isNotValid(amount) || amount == 0.00)
			return Optional.empty();

		SalaryDeductionRecord salaryDeductionRecord = dslContext.selectFrom(SALARY_DEDUCTION)
				.where(SALARY_DEDUCTION.SALARY.eq(salaryRecord.getId())
						.and(SALARY_DEDUCTION.DEDUCTION_CONCEPT.eq(name)))
				.fetchOptional().orElseGet(() -> dslContext.newRecord(SALARY_DEDUCTION));
		salaryDeductionRecord.setDomain(salaryRecord.getDomain());
		salaryDeductionRecord.setSalary(salaryRecord.getId());
		salaryDeductionRecord.setDeductionConcept(name);
		salaryDeductionRecord.setAmount(amount);

		getDeductionConcept(name, dslContext).ifPresentOrElse(deductionConcept -> {
			salaryDeductionRecord.setType(deductionConcept.getType());
			salaryDeductionRecord.setExpression(deductionConcept.getExpression());
			salaryDeductionRecord.setDescription(deductionConcept.getDescription());
		}, () -> salaryDeductionRecord.setType((byte) 9) /* OTHER */ );

		salaryDeductionRecord.store();

		return Optional.of(salaryDeductionRecord);
	}

	private static Optional<SalaryCostRecord> insertSalaryCost(DSLContext dslContext, SalaryRecord salaryRecord,
			String name, Double amount, Byte type, String description) {

		if (AonNumberUtils.isNotValid(amount) || amount == 0.00)
			return Optional.empty();

		SalaryCostRecord salaryCostRecord = dslContext.selectFrom(SALARY_COST)
				.where(SALARY_COST.SALARY.eq(salaryRecord.getId()).and(SALARY_COST.COST_CONCEPT.eq(name)))
				.fetchOptional().orElseGet(() -> dslContext.newRecord(SALARY_COST));
		salaryCostRecord.setDomain(salaryRecord.getDomain());
		salaryCostRecord.setSalary(salaryRecord.getId());
		salaryCostRecord.setType(type);
		salaryCostRecord.setAmount(amount);
		salaryCostRecord.setCostConcept(name);
		salaryCostRecord.setDescription(description);

		salaryCostRecord.store();

		return Optional.of(salaryCostRecord);
	}

	static Optional<SalaryDataRecord> insertSalaryData(DSLContext dslContext, Date startDate, Date endDate,
			SalaryRecord salaryRecord, String name, BigDecimal amount) {

		if (amount == null)
			return Optional.empty();

		return insertSalaryData(dslContext, startDate, endDate, salaryRecord, name, amount.doubleValue());

	}

	static Optional<SalaryDataRecord> insertSalaryData(DSLContext dslContext, Date startDate, Date endDate,
			SalaryRecord salaryRecord, String name, Double amount) {
		if (AonNumberUtils.isNotValid(amount) || amount == 0.00)
			return Optional.empty();
		SalaryDataRecord salaryDataRecord = dslContext.selectFrom(SALARY_DATA)
				.where(SALARY_DATA.SALARY.eq(salaryRecord.getId()).and(SALARY_DATA.NAME.eq(name))).fetchOptional()
				.orElseGet(() -> dslContext.newRecord(SALARY_DATA));
		salaryDataRecord.setDomain(salaryRecord.getDomain());
		salaryDataRecord.setSalary(salaryRecord.getId());
		salaryDataRecord.setName(name);
		salaryDataRecord.setEndDate(endDate);
		salaryDataRecord.setStartDate(startDate);
		salaryDataRecord.setExpression(String.format("%1$.2f", amount));
		salaryDataRecord.store();
		return Optional.of(salaryDataRecord);
	}

	static Optional<SalaryDataRecord> insertSalaryData(DSLContext dslContext, Date startDate, Date endDate,
			SalaryRecord salaryRecord, String name, String expression) {
		SalaryDataRecord salaryDataRecord = dslContext.selectFrom(SALARY_DATA)
				.where(SALARY_DATA.SALARY.eq(salaryRecord.getId()).and(SALARY_DATA.NAME.eq(name))).fetchOptional()
				.orElseGet(() -> dslContext.newRecord(SALARY_DATA));
		salaryDataRecord.setDomain(salaryRecord.getDomain());
		salaryDataRecord.setSalary(salaryRecord.getId());
		salaryDataRecord.setName(name);
		salaryDataRecord.setEndDate(endDate);
		salaryDataRecord.setStartDate(startDate);
		salaryDataRecord.setExpression(expression);
		salaryDataRecord.store();
		return Optional.of(salaryDataRecord);
	}

	static Optional<DeductionConceptRecord> getDeductionConcept(String name, DSLContext dslContext) {
		return dslContext.selectFrom(DEDUCTION_CONCEPT)
				.where(DEDUCTION_CONCEPT.DOMAIN.eq(0).and(DEDUCTION_CONCEPT.CODE.eq(name)))
				.fetchOptionalInto(DEDUCTION_CONCEPT);
	}

	static Date getEndDate(V_RECIBO_NOMINA nomina) {
		try {
			if (AonStringUtils.equalsIgnoreCase("MENSUAL", nomina.getPERIODO_NOMINA()))
				return new Date(new SimpleDateFormat("dd MMM yy 'a' dd MMM yy", Locale.of("ES", "es"))
						.parse(nomina.getPERIODO_NOMINA_FECHAS()).getTime());
		} catch (ParseException e) {
		}
		try {
			return new Date(new SimpleDateFormat("dd MMM yy", Locale.of("ES", "es"))
					.parse(nomina.getPERIODO_NOMINA_FECHAS()).getTime());
		} catch (ParseException e) {
		}
		return getDate(nomina.getFECHA());
	}

	static String getNameFor(Optional<CONCEPTOSREGISTROS> registro, NOMINASCONCEPTOS concepto, Field<String> field) {
		String description = AonStringUtils
				.trim(registro.map(r -> firstNotBlank(r.getDESCRIPCION(), r.getDESCRIPCION2())).orElse(""));
		if (AonStringUtils.isBlank(description)) {
			return null;
		}

		int length = field.getDataType().length();

		if (AonStringUtils.length(description) > length) {
			description = AonStringUtils.substringAfter(AonStringUtils.substring(description, -length), " ");
		}

		return AonStringUtils.upperCase(AonStringUtils.replace(description, " ", "_"));
	}

}
