package com.esferalia.aon.dsi;

import static com.esferalia.aon.dsi.jooq.tables.Fnempres.FNEMPRES;
import static com.esferalia.aon.dsi.jooq.tables.Fnnominc.FNNOMINC;
import static com.esferalia.aon.dsi.jooq.tables.Fnnominl.FNNOMINL;
import static com.esferalia.aon.dsi.jooq.tables.Fntconce.FNTCONCE;
import static com.esferalia.aon.dsi.util.EnumUtils.enum2Byte;
import static com.esferalia.aon.jooq.tables.Salary.SALARY;
import static com.esferalia.aon.jooq.tables.SalaryBonus.SALARY_BONUS;
import static com.esferalia.aon.jooq.tables.SalaryCost.SALARY_COST;
import static com.esferalia.aon.jooq.tables.SalaryDeduction.SALARY_DEDUCTION;
import static com.esferalia.aon.jooq.tables.SalaryEmbargo.SALARY_EMBARGO;
import static com.esferalia.aon.jooq.tables.SalaryPayment.SALARY_PAYMENT;
import static com.esferalia.aon.salary.enumeration.DeductionType.COMMON_CONTINGENCY;
import static com.esferalia.aon.salary.enumeration.DeductionType.FOGASA;
import static com.esferalia.aon.salary.enumeration.DeductionType.IRPF;
import static com.esferalia.aon.salary.enumeration.DeductionType.JOB_TRAINING;
import static com.esferalia.aon.salary.enumeration.DeductionType.NON_STRUCTURAL_OVERTIME;
import static com.esferalia.aon.salary.enumeration.DeductionType.OTHER;
import static com.esferalia.aon.salary.enumeration.DeductionType.STRUCTURAL_OVERTIME;
import static com.esferalia.aon.salary.enumeration.DeductionType.UNEMPLOYMENT;
import static java.lang.String.format;
import static org.jooq.tools.StringUtils.isBlank;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.jooq.Condition;
import org.jooq.Cursor;
import org.jooq.DSLContext;
import org.jooq.DeleteConditionStep;
import org.jooq.InsertSetMoreStep;
import org.jooq.InsertSetStep;
import org.jooq.Record;

import net.aonsolutions.core.dbutils.AonSQLException;
import com.esferalia.aon.dsi.jooq.tables.records.FnnomincRecord;
import com.esferalia.aon.dsi.jooq.tables.records.FnnominlRecord;
import com.esferalia.aon.dsi.jooq.tables.records.FntconceRecord;
import com.esferalia.aon.jooq.tables.records.SalaryBonusRecord;
import com.esferalia.aon.jooq.tables.records.SalaryCostRecord;
import com.esferalia.aon.jooq.tables.records.SalaryDeductionRecord;
import com.esferalia.aon.jooq.tables.records.SalaryEmbargoRecord;
import com.esferalia.aon.jooq.tables.records.SalaryPaymentRecord;
import com.esferalia.aon.jooq.tables.records.SalaryRecord;
import com.esferalia.aon.payroll.Pair;
import com.esferalia.aon.salary.enumeration.DeductionType;
import com.esferalia.aon.salary.enumeration.SalaryType;

public class NominaLoader extends AbstractLoader {

	public static interface Callback {
		int getDomain(FnnomincRecord nominc);

		int getContract(FnnomincRecord nominc);
	}

	private boolean replace;

	private List<Integer> toDelete;

	private InsertSetMoreStep<SalaryRecord> insertSetMoreStepSalary;
	private InsertSetMoreStep<SalaryCostRecord> insertSetMoreStepSalaryCost;
	private InsertSetMoreStep<SalaryBonusRecord> insertSetMoreStepSalaryBonus;
	private InsertSetMoreStep<SalaryEmbargoRecord> insertSetMoreStepSalaryEmbargo;
	private InsertSetMoreStep<SalaryPaymentRecord> insertSetMoreStepSalaryPayment;
	private InsertSetMoreStep<SalaryDeductionRecord> insertSetMoreStepSalaryDeduction;
	
	

	public NominaLoader(DSLContext dsiContext, DSLContext aonContext) {
		super(dsiContext, aonContext);
		toDelete = new LinkedList<Integer>();
	}

	public NominaLoader setReplace(boolean replace) {
		this.replace = replace;
		return this;
	}

	public void loadNominc(Callback cb, Condition... conditions)
			throws AonSQLException {
		//@formatter:off
		Condition nominc2empres = 
				FNNOMINC.F30SSCODEM.eq(FNEMPRES.F20SSCOD)
				.and(FNNOMINC.F30SSNUMEM.eq(FNEMPRES.F20SSNUM));

		Cursor<Record> nomincCursor = dsiContext.select()
				.from(FNNOMINC)
				.join(FNEMPRES)
				.on(nominc2empres)
				.where(conditions)
				.orderBy(FNNOMINC.F30SSCODEM, 
						FNNOMINC.F30SSNUMEM, 
						FNNOMINC.F30SSCOD, 
						FNNOMINC.F30SSNUM, 
						FNNOMINC.F30MES, 
						FNNOMINC.F30TIPO)
				.fetchLazy();
		//@formatter:on

		//@formatter:off

		Cursor<Record> nominlnCursor = dsiContext.select()
				.from(FNNOMINL)
				.join(FNEMPRES)
				.on(FNNOMINL.F31SSCODEM.eq(FNEMPRES.F20SSCOD))
				.and(FNNOMINL.F31SSNUMEM.eq(FNEMPRES.F20SSNUM))
				.leftOuterJoin(FNTCONCE)
				.on(FNNOMINL.F31SSCODEM.eq(FNTCONCE.F21SSCODEM))
				.and(FNNOMINL.F31SSNUMEM.eq(FNTCONCE.F21SSNUMEM))
				.and(FNNOMINL.F31SSNUM.eq(FNTCONCE.F21SSNUM))
				.and(FNNOMINL.F31SSCOD.eq(FNTCONCE.F21SSCOD))
				.and(FNNOMINL.F31FALTA.eq(FNTCONCE.F21FALTA))
				.and(FNNOMINL.F31NORDEN.eq(FNTCONCE.F21NORDEN))
				.and(FNNOMINL.F31CLAVE.eq(FNTCONCE.F21CLAVE))
				.where(conditions)
				.orderBy(FNNOMINL.F31SSCODEM, 
						FNNOMINL.F31SSNUMEM, 
						FNNOMINL.F31SSCOD, 
						FNNOMINL.F31SSNUM, 
						FNNOMINL.F31MES, 
						FNNOMINL.F31TIPO,
						FNNOMINL.F31NORDEN)
				.fetchLazy();
		//@formatter:on

		FnnominlRecord nominl = null;
		FntconceRecord tconce = null;

		while (nomincCursor.hasNext()) {
			FnnomincRecord nominc = nomincCursor.fetchOneInto(FNNOMINC);
			int domain = cb.getDomain(nominc);
			int contract = cb.getContract(nominc);

			if (nominl == null && nominlnCursor.hasNext()) {
				Record record = nominlnCursor.fetchOne();
				nominl = record.into(new FnnominlRecord());
				tconce = record.into(new FntconceRecord());
			}

			List<Pair<FnnominlRecord, FntconceRecord>> nominlns = new ArrayList<Pair<FnnominlRecord, FntconceRecord>>();

			while (nominl.getF31sscodem().equals(nominc.getF30sscodem())
					&& nominl.getF31ssnumem().equals(nominc.getF30ssnumem())
					&& nominl.getF31sscod().equals(nominc.getF30sscod())
					&& nominl.getF31ssnum().equals(nominc.getF30ssnum())
					&& nominl.getF31mes().equals(nominc.getF30mes())
					&& nominl.getF31tipo().equals(nominc.getF30tipo())) {
				nominlns.add(new Pair<FnnominlRecord, FntconceRecord>(nominl,
						tconce));
				if (!nominlnCursor.hasNext())
					break;
				Record record = nominlnCursor.fetchOne();
				nominl = record.into(new FnnominlRecord());
				tconce = record.into(new FntconceRecord());
			}

			int salary;
			SalaryRecord record = getSalaryRecord(nominc, contract);
			if (record != null) {
				salary = record.getId();
				if (!replace)
					continue; // salary already exists
			} else {
				salary = next(SALARY.getIdentity());
			}

			toDelete.add(salary);
			loadNominc(nominc, nominlns, contract, domain, salary, cb);
		}
	}

	public void execute() {
		delete();
		insert();
	}

	private SalaryRecord getSalaryRecord(FnnomincRecord nominc, int contract) {
		//@formatter:off
		return aonContext
		.select()
		.from(SALARY)
		.where(SALARY.CONTRACT.eq(contract))
		.and(SALARY.ISSUE_DATE.eq(nominc.getF30fechan()))
		.and(SALARY.CHARGE_DATE.eq(nominc.getF30fecha()))
		.and(SALARY.TYPE.eq(enum2Byte(getSalaryType(nominc))))
		.fetchOneInto(SALARY);
		//@formatter:on
	}

	private void loadNominc(FnnomincRecord nominc,
			Collection<Pair<FnnominlRecord, FntconceRecord>> nominls,
			int contract, int domain, int salary, Callback cb) {

		Integer reg = 0;
		if (!isBlank(nominc.getF30matric()))
			try {
				reg = Integer.parseInt(nominc.getF30matric());
			} catch (NumberFormatException e) {

			}

		InsertSetStep<SalaryRecord> insertSetStepSalary = getSalaryInsertSetStep();
		Date endDate = nominc.getF30fecha();

		Calendar startCalendar = Calendar.getInstance();
		startCalendar.setTime(endDate);
		startCalendar.set(Calendar.DAY_OF_MONTH, 1);
		Date startDate = new Date(startCalendar.getTimeInMillis());
		if (startDate.before(nominc.getF30falta()))
			startDate = nominc.getF30falta();

		int days = (int) ((endDate.getTime() - startDate.getTime()) / (1000 * 60 * 60 * 24)) + 1;

		double irpfBase = 0.00;
		double totalIrpf = 0.00;
		double irpfMoneyBase = 0.00;
		double irpfInKindBase = 0.00;

		if (nominc.getF30birpf1() != null) {
			irpfBase += nominc.getF30birpf1();
			irpfMoneyBase += nominc.getF30birpf1();
		}
		if (nominc.getF30birpf2() != null) {
			irpfBase += nominc.getF30birpf2();
			irpfMoneyBase += nominc.getF30birpf2();
		}
		if (nominc.getF30birpfn() != null) {
			irpfBase += nominc.getF30birpfn();
			irpfInKindBase += nominc.getF30birpfn();
		}
		if (nominc.getF30cirpf1() != null)
			totalIrpf += nominc.getF30cirpf1();
		if (nominc.getF30cirpf2() != null)
			totalIrpf += nominc.getF30cirpf2();

		double totalSegSocial = 0.00;
		if (nominc.getF30ccctra() != null)
			totalSegSocial += nominc.getF30ccctra();
		if (nominc.getF30cfptra() != null)
			totalSegSocial += nominc.getF30cfptra();
		if (nominc.getF30cdetra() != null)
			totalSegSocial += nominc.getF30cdetra();
		if (nominc.getF30chetra() != null)
			totalSegSocial += nominc.getF30chetra();
		if (nominc.getF30chntra() != null)
			totalSegSocial += nominc.getF30chntra();

		double totalEnterprise = 0.00;
		if (nominc.getF30cccemp() != null)
			totalEnterprise += nominc.getF30cccemp();
		if (nominc.getF30cfpemp() != null)
			totalEnterprise += nominc.getF30cfpemp();
		if (nominc.getF30cdeemp() != null)
			totalEnterprise += nominc.getF30cdeemp();
		if (nominc.getF30cheemp() != null)
			totalEnterprise += nominc.getF30cheemp();
		if (nominc.getF30chnemp() != null)
			totalEnterprise += nominc.getF30chnemp();
		if (nominc.getF30cfgemp() != null)
			totalEnterprise += nominc.getF30cfgemp();
		if (nominc.getF30cimstr() != null)
			totalEnterprise += nominc.getF30cimstr();
		if (nominc.getF30cilttr() != null)
			totalEnterprise += nominc.getF30cilttr();

		SalaryType type = getSalaryType(nominc);

		//@formatter:off
		insertSetMoreStepSalary = insertSetStepSalary
				.set(SALARY.ID, salary)
				.set(SALARY.DOMAIN, domain)
				.set(SALARY.CONTRACT, contract)
				.set(SALARY.TYPE, enum2Byte(type))
				
				.set(SALARY.REGISTRATION, reg)
				.set(SALARY.CATEGORY, nominc.getF30categ())
				.set(SALARY.QUOTE_GROUP, nominc.getF30grupo())
				.set(SALARY.SENIORITY_DATE, nominc.getF30fantig())
				
				.set(SALARY.START_DATE, startDate) // TODO: ???
				.set(SALARY.END_DATE, endDate) // TODO: ???
				.set(SALARY.ISSUE_DATE, nominc.getF30fechan()) // TODO: ???
				.set(SALARY.CHARGE_DATE, nominc.getF30fecha()) // TODO: ???
				.set(SALARY.TIME_UNITS, days)

				.set(SALARY.EMPLOYEE_NAME, nominc.getF30nomtra())
				.set(SALARY.EMPLOYEE_DOCUMENT, nominc.getF30nif())
				.set(SALARY.SOCIAL_SECURITY_NUMBER, getSocialSecutiryNumber(nominc))
				
				.set(SALARY.ENTERPRISE_NAME, nominc.getF30nomemp())
				.set(SALARY.CCC, String.format("%s%s%s",
						nominc.getF30sscodem(),  
						nominc.getF30ssnumem(), 
						nominc.getF30ssctrem()))
				.set(SALARY.ENTERPRISE_DOCUMENT, nominc.getF30cif())
				.set(SALARY.ENTERPRISE_ADDRESS, nominc.getF30domicil())

				.set(SALARY.CGC_BASE, nominc.getF30basecc() != null ? nominc.getF30basecc() : 0.00)
				.set(SALARY.CGP_BASE, nominc.getF30baseat() != null ? nominc.getF30baseat() : 0.00)
				.set(SALARY.PRO_EXT_BASE, nominc.getF30pextra() != null ? nominc.getF30pextra() : 0.00)
				.set(SALARY.HEXTRA_BASE, nominc.getF30hextr1() != null ? nominc.getF30hextr1() : 0.00)
				.set(SALARY.NON_HEXTRA_BASE, nominc.getF30hextr2() != null ? nominc.getF30hextr2() : 0.00)
				
				.set(SALARY.IRPF_BASE, irpfBase ) 
				.set(SALARY.TOTAL_IRPF , totalIrpf  )
				.set(SALARY.MONEY_IRPF_BASE , irpfMoneyBase  )
				.set(SALARY.INKIND_IRPF_BASE , irpfInKindBase )

				.set(SALARY.TOTAL_PAYMENT, nominc.getF30totdev() != null ? nominc.getF30totdev() : 0.00 )
				.set(SALARY.TOTAL_LIQUID, nominc.getF30liquido() != null ? nominc.getF30liquido() : 0.00  )
				
				.set(SALARY.REMUNERATION, nominc.getF30totdev() != null ? nominc.getF30totdev() : 0.00  )

				.set(SALARY.TOTAL_ENTERPRISE, totalEnterprise)
				.set(SALARY.SOCIAL_SECURITY_CONTRIBUTIONS, totalSegSocial )
				.set(SALARY.TOTAL_DEDUCTION, totalSegSocial + totalIrpf)
				;
		//@formatter:on

		for (Pair<FnnominlRecord, FntconceRecord> nominl : nominls) {
			loadSalaryPayment(nominl.getFirst(), nominl.getSecond(), domain,
					salary, cb);
		}

		loadSalaryDeductions(nominc, domain, salary);

		loadSalaryCosts(nominc, domain, salary);

		InsertSetStep<SalaryBonusRecord> insertSetStepBonusDeduction = getSalaryBonusInsertSetStep();
		//@formatter:off
		//@formatter:on
	}

	private String getSocialSecutiryNumber(FnnomincRecord nominc) {
		return format("%s%s%s", nominc.getF30sscod(), nominc.getF30ssnum(),
				nominc.getF30ssctrl());
	}

	private void loadSalaryPayment(FnnominlRecord nominl,
			FntconceRecord tconce, int domain, int salary, Callback cb) {

		double impor = nominl.getF31impor() != null ? nominl.getF31impor()
				: 0.00;
		double total = nominl.getF31total() != null ? nominl.getF31total()
				: 0.00;

		byte clavecra = StringUtils.isBlank(nominl.getF31clavecra()) ? 1 : Byte
				.parseByte(nominl.getF31clavecra());

		String expression;
		if (nominl.getF31unidade() != null && nominl.getF31unidade() > 0)
			expression = String.format("%.2f * %.2f", impor,
					nominl.getF31unidade());
		else if (nominl.getF31dias() != null && nominl.getF31dias() > 0)
			expression = String.format("%.2f * %d", impor, nominl.getF31dias()
					.intValue());
		else
			expression = String.format("%.2f ", total);

		String concept = CconceLoader.getCode(tconce);
		
		String preffix = getExpressionPreffix(nominl);
		
		int id = getSalaryPaymentId(salary, preffix);

		InsertSetStep<SalaryPaymentRecord> insertSetStepSalaryPayment = getSalaryPaymentInsertSetStep();
		//@formatter:off
		insertSetMoreStepSalaryPayment = insertSetStepSalaryPayment
				.set(SALARY_PAYMENT.ID, id)
				.set(SALARY_PAYMENT.DOMAIN, domain)
				.set(SALARY_PAYMENT.SALARY, salary)
				.set(SALARY_PAYMENT.PAYMENT_CONCEPT, concept )
				.set(SALARY_PAYMENT.DESCRIPTION, nominl.getF31nombre())
				.set(SALARY_PAYMENT.EXPRESSION, String.format("%s %s",preffix ,expression ))
				.set(SALARY_PAYMENT.IRPF, StringUtils.equalsIgnoreCase("S",nominl.getF31irpf())? total: 0.00 )
				.set(SALARY_PAYMENT.QUOTE, StringUtils.equalsIgnoreCase("S",nominl.getF31segsoc())? total: 0.00 )
				.set(SALARY_PAYMENT.TYPE, clavecra)
				.set(SALARY_PAYMENT.AMOUNT, total )
				;
		//@formatter:on
	}

	private void loadSalaryDeductions(FnnomincRecord nominc, int domain,
			int salary) {
		if (nominc.getF30ccctra() != null) {
			int id = getSalaryDeductionId(salary, COMMON_CONTINGENCY);
			InsertSetStep<SalaryDeductionRecord> insertSetStepSalaryDeduction = getSalaryDeductionInsertSetStep();
			//@formatter:off
			insertSetMoreStepSalaryDeduction = insertSetStepSalaryDeduction
					.set(SALARY_DEDUCTION.ID, id)
					.set(SALARY_DEDUCTION.DOMAIN, domain)
					.set(SALARY_DEDUCTION.SALARY, salary)
					.set(SALARY_DEDUCTION.DEDUCTION_CONCEPT, "CGC")
					.set(SALARY_DEDUCTION.DESCRIPTION, String.format("%.2f %%",nominc.getF30tcctra()))
					.set(SALARY_DEDUCTION.EXPRESSION, String.format("BASE_CGC * %.2f/100",nominc.getF30tcctra()))
					.set(SALARY_DEDUCTION.TYPE, enum2Byte(COMMON_CONTINGENCY))
					.set(SALARY_DEDUCTION.AMOUNT, nominc.getF30ccctra())
					;
			//@formatter:on
		}
		if (nominc.getF30cdetra() != null) {
			int id = getSalaryDeductionId(salary, UNEMPLOYMENT);
			InsertSetStep<SalaryDeductionRecord> insertSetStepSalaryDeduction = getSalaryDeductionInsertSetStep();
			//@formatter:off
			insertSetMoreStepSalaryDeduction = insertSetStepSalaryDeduction
					.set(SALARY_DEDUCTION.ID, id)
					.set(SALARY_DEDUCTION.DOMAIN, domain)
					.set(SALARY_DEDUCTION.SALARY, salary)
					.set(SALARY_DEDUCTION.DEDUCTION_CONCEPT, "DESMPL")
					.set(SALARY_DEDUCTION.DESCRIPTION, String.format("%.2f %%",nominc.getF30tdetra()))
					.set(SALARY_DEDUCTION.EXPRESSION, String.format("BASE_CGP * %.2f/100",nominc.getF30tdetra()))
					.set(SALARY_DEDUCTION.TYPE, enum2Byte(UNEMPLOYMENT))
					.set(SALARY_DEDUCTION.AMOUNT, nominc.getF30cdetra())
					;
			//@formatter:on
		}
		if (nominc.getF30cfptra() != null) {
			int id = getSalaryDeductionId(salary, JOB_TRAINING);
			InsertSetStep<SalaryDeductionRecord> insertSetStepSalaryDeduction = getSalaryDeductionInsertSetStep();
			//@formatter:off
			insertSetMoreStepSalaryDeduction = insertSetStepSalaryDeduction
					.set(SALARY_DEDUCTION.ID, id)
					.set(SALARY_DEDUCTION.DOMAIN, domain)
					.set(SALARY_DEDUCTION.SALARY, salary)
					.set(SALARY_DEDUCTION.DEDUCTION_CONCEPT, "FP")
					.set(SALARY_DEDUCTION.DESCRIPTION, String.format("%.2f %%",nominc.getF30tfptra()))
					.set(SALARY_DEDUCTION.EXPRESSION, String.format("BASE_CGP * %.2f/100",nominc.getF30tfptra()))
					.set(SALARY_DEDUCTION.TYPE, enum2Byte(JOB_TRAINING))
					.set(SALARY_DEDUCTION.AMOUNT, nominc.getF30cfptra())
					;
			//@formatter:on
		}
		if (nominc.getF30chetra() != null && nominc.getF30chetra() > 0.00) {
			int id = getSalaryDeductionId(salary, STRUCTURAL_OVERTIME);
			InsertSetStep<SalaryDeductionRecord> insertSetStepSalaryDeduction = getSalaryDeductionInsertSetStep();
			//@formatter:off
			insertSetMoreStepSalaryDeduction = insertSetStepSalaryDeduction
					.set(SALARY_DEDUCTION.ID, id)
					.set(SALARY_DEDUCTION.DOMAIN, domain)
					.set(SALARY_DEDUCTION.SALARY, salary)
					.set(SALARY_DEDUCTION.DEDUCTION_CONCEPT, "ESTR")
					.set(SALARY_DEDUCTION.DESCRIPTION, String.format("%.2f %%",nominc.getF30thetra()))
					.set(SALARY_DEDUCTION.EXPRESSION, String.format("BASE_ESTR * %.2f/100",nominc.getF30thetra()))
					.set(SALARY_DEDUCTION.TYPE, enum2Byte(STRUCTURAL_OVERTIME))
					.set(SALARY_DEDUCTION.AMOUNT, nominc.getF30chetra())
					;
			//@formatter:on
		}
		if (nominc.getF30chntra() != null && nominc.getF30chntra() > 0.00) {
			int id = getSalaryDeductionId(salary, NON_STRUCTURAL_OVERTIME);
			InsertSetStep<SalaryDeductionRecord> insertSetStepSalaryDeduction = getSalaryDeductionInsertSetStep();
			//@formatter:off
			insertSetMoreStepSalaryDeduction = insertSetStepSalaryDeduction
					.set(SALARY_DEDUCTION.ID, id)
					.set(SALARY_DEDUCTION.DOMAIN, domain)
					.set(SALARY_DEDUCTION.SALARY, salary)
					.set(SALARY_DEDUCTION.DEDUCTION_CONCEPT, "NESTR")
					.set(SALARY_DEDUCTION.DESCRIPTION, String.format("%.2f %%",nominc.getF30thntra()))
					.set(SALARY_DEDUCTION.EXPRESSION, String.format("BASE_NESTR * %.2f/100",nominc.getF30thntra()))
					.set(SALARY_DEDUCTION.TYPE, enum2Byte(NON_STRUCTURAL_OVERTIME))
					.set(SALARY_DEDUCTION.AMOUNT, nominc.getF30chntra())
					;
			//@formatter:on
		}

		if (nominc.getF30cirpf1() != null || nominc.getF30cirpf2() != null) {
			double tirpf = 0.00;
			if (nominc.getF30tirpf1() != null)
				tirpf += nominc.getF30tirpf1();
			if (nominc.getF30tirpf2() != null)
				tirpf += nominc.getF30tirpf2();
			double cirpf = 0.00;
			if (nominc.getF30cirpf1() != null)
				cirpf += nominc.getF30cirpf1();
			if (nominc.getF30cirpf2() != null)
				cirpf += nominc.getF30cirpf2();
			int id = getSalaryDeductionId(salary, IRPF);
			InsertSetStep<SalaryDeductionRecord> insertSetStepSalaryDeduction = getSalaryDeductionInsertSetStep();
			//@formatter:off
			insertSetMoreStepSalaryDeduction = insertSetStepSalaryDeduction
					.set(SALARY_DEDUCTION.ID, id)
					.set(SALARY_DEDUCTION.DOMAIN, domain)
					.set(SALARY_DEDUCTION.SALARY, salary)
					.set(SALARY_DEDUCTION.DEDUCTION_CONCEPT, "IRPF")
					.set(SALARY_DEDUCTION.DESCRIPTION, String.format("%.2f %%",tirpf))
					.set(SALARY_DEDUCTION.EXPRESSION, String.format("BASE_IRPF * %.2f/100",tirpf))
					.set(SALARY_DEDUCTION.TYPE, enum2Byte(IRPF))
					.set(SALARY_DEDUCTION.AMOUNT, cirpf)
					;
			//@formatter:on
		}
	}

	private void loadSalaryCosts(FnnomincRecord nominc, int domain, int salary) {

		if (nominc.getF30cccemp() != null) {
			int id = getSalaryCostId(salary, COMMON_CONTINGENCY);
			InsertSetStep<SalaryCostRecord> insertSetStepSalaryCost = getSalaryCostInsertSetStep();
			//@formatter:off
			insertSetMoreStepSalaryCost = insertSetStepSalaryCost
					.set(SALARY_COST.ID, id)
					.set(SALARY_COST.DOMAIN, domain)
					.set(SALARY_COST.SALARY, salary)
					.set(SALARY_COST.COST_CONCEPT, "CGC_E")
					.set(SALARY_COST.DESCRIPTION, String.format("%.2f %%",nominc.getF30tccemp()))
					.set(SALARY_COST.TYPE, enum2Byte(COMMON_CONTINGENCY))
					.set(SALARY_DEDUCTION.AMOUNT, nominc.getF30cccemp())
					;
			//@formatter:on
		}

		if (nominc.getF30cdeemp() != null) {
			int id = getSalaryCostId(salary, UNEMPLOYMENT);
			InsertSetStep<SalaryCostRecord> insertSetStepSalaryCost = getSalaryCostInsertSetStep();
			//@formatter:off
			insertSetMoreStepSalaryCost = insertSetStepSalaryCost
					.set(SALARY_COST.ID, id)
					.set(SALARY_COST.DOMAIN, domain)
					.set(SALARY_COST.SALARY, salary)
					.set(SALARY_COST.COST_CONCEPT, "DESMPL_E")
					.set(SALARY_COST.DESCRIPTION, String.format("%.2f %%",nominc.getF30tdeemp()))
					.set(SALARY_COST.TYPE, enum2Byte(UNEMPLOYMENT))
					.set(SALARY_COST.AMOUNT, nominc.getF30cdeemp())
					;
			//@formatter:on
		}

		if (nominc.getF30cfpemp() != null) {
			int id = getSalaryCostId(salary, JOB_TRAINING);
			InsertSetStep<SalaryCostRecord> insertSetStepSalaryCost = getSalaryCostInsertSetStep();
			//@formatter:off
			insertSetMoreStepSalaryCost = insertSetStepSalaryCost
					.set(SALARY_COST.ID, id)
					.set(SALARY_COST.DOMAIN, domain)
					.set(SALARY_COST.SALARY, salary)
					.set(SALARY_COST.COST_CONCEPT, "FP_E")
					.set(SALARY_COST.DESCRIPTION, String.format("%.2f %%",nominc.getF30tfpemp()))
					.set(SALARY_COST.TYPE, enum2Byte(JOB_TRAINING))
					.set(SALARY_COST.AMOUNT, nominc.getF30cfpemp())
					;
			//@formatter:on
		}

		if (nominc.getF30cfgemp() != null) {
			int id = getSalaryCostId(salary, FOGASA);
			InsertSetStep<SalaryCostRecord> insertSetStepSalaryCost = getSalaryCostInsertSetStep();
			//@formatter:off
			insertSetMoreStepSalaryCost = insertSetStepSalaryCost
					.set(SALARY_COST.ID, id)
					.set(SALARY_COST.DOMAIN, domain)
					.set(SALARY_COST.SALARY, salary)
					.set(SALARY_COST.COST_CONCEPT, "FOGASA_E")
					.set(SALARY_COST.DESCRIPTION, String.format("%.2f %%",nominc.getF30tfgemp()))
					.set(SALARY_COST.TYPE, enum2Byte(FOGASA))
					.set(SALARY_COST.AMOUNT, nominc.getF30cfgemp())
					;
			//@formatter:on
		}

		if (nominc.getF30cheemp() != null) {
			int id = getSalaryCostId(salary, STRUCTURAL_OVERTIME);
			InsertSetStep<SalaryCostRecord> insertSetStepSalaryCost = getSalaryCostInsertSetStep();
			//@formatter:off
			insertSetMoreStepSalaryCost = insertSetStepSalaryCost
					.set(SALARY_COST.ID, id)
					.set(SALARY_COST.DOMAIN, domain)
					.set(SALARY_COST.SALARY, salary)
					.set(SALARY_COST.COST_CONCEPT, "ESTR_E")
					.set(SALARY_COST.DESCRIPTION, String.format("%.2f %%",nominc.getF30theemp()))
					.set(SALARY_COST.TYPE, enum2Byte(STRUCTURAL_OVERTIME))
					.set(SALARY_COST.AMOUNT, nominc.getF30cheemp())
					;
			//@formatter:on
		}

		if (nominc.getF30chnemp() != null) {
			int id = getSalaryCostId(salary, NON_STRUCTURAL_OVERTIME);
			InsertSetStep<SalaryCostRecord> insertSetStepSalaryCost = getSalaryCostInsertSetStep();
			//@formatter:off
			insertSetMoreStepSalaryCost = insertSetStepSalaryCost
					.set(SALARY_COST.ID, id)
					.set(SALARY_COST.DOMAIN, domain)
					.set(SALARY_COST.SALARY, salary)
					.set(SALARY_COST.COST_CONCEPT, "NOESTR_E")
					.set(SALARY_COST.DESCRIPTION, String.format("%.2f %%",nominc.getF30thnemp()))
					.set(SALARY_COST.TYPE, enum2Byte(NON_STRUCTURAL_OVERTIME))
					.set(SALARY_COST.AMOUNT, nominc.getF30chnemp())
					;
			//@formatter:on
		}

		if (nominc.getF30cilttr() != null) {
			int id = getSalaryCostId(salary, "IT_E");
			InsertSetStep<SalaryCostRecord> insertSetStepSalaryCost = getSalaryCostInsertSetStep();
			//@formatter:off
			insertSetMoreStepSalaryCost = insertSetStepSalaryCost
					.set(SALARY_COST.ID, id)
					.set(SALARY_COST.DOMAIN, domain)
					.set(SALARY_COST.SALARY, salary)
					.set(SALARY_COST.COST_CONCEPT, "IT_E")
					.set(SALARY_COST.DESCRIPTION, String.format("%.2f %%",nominc.getF30tcpilt()))
					.set(SALARY_COST.TYPE, enum2Byte(OTHER))
					.set(SALARY_COST.AMOUNT, nominc.getF30cilttr())
					;
			//@formatter:on
		}

		if (nominc.getF30cimstr() != null) {
			int id = getSalaryCostId(salary, "IMS_E");
			InsertSetStep<SalaryCostRecord> insertSetStepSalaryCost = getSalaryCostInsertSetStep();
			//@formatter:off
			insertSetMoreStepSalaryCost = insertSetStepSalaryCost
					.set(SALARY_COST.ID, id)
					.set(SALARY_COST.DOMAIN, domain)
					.set(SALARY_COST.SALARY, salary)
					.set(SALARY_COST.COST_CONCEPT, "IMS_E")
					.set(SALARY_COST.DESCRIPTION, String.format("%.2f %%",nominc.getF30tcpims()))
					.set(SALARY_COST.TYPE, enum2Byte(OTHER))
					.set(SALARY_COST.AMOUNT, nominc.getF30cimstr())
					;
			//@formatter:on
		}
	}

	private void insert() {
		execute(insertSetMoreStepSalary);
		execute(insertSetMoreStepSalaryDeduction);
		execute(insertSetMoreStepSalaryCost);
		execute(insertSetMoreStepSalaryPayment);

		insertSetMoreStepSalary = null;
		insertSetMoreStepSalaryCost = null;
		insertSetMoreStepSalaryDeduction = null;
		insertSetMoreStepSalaryPayment = null;
	}

	private void delete() {
		if (toDelete.isEmpty())
			return;
		aonContext.delete(SALARY_COST).where(SALARY_COST.SALARY.in(toDelete))
				.execute();
		aonContext.delete(SALARY_PAYMENT)
				.where(SALARY_PAYMENT.SALARY.in(toDelete)).execute();
		aonContext.delete(SALARY_DEDUCTION)
				.where(SALARY_DEDUCTION.SALARY.in(toDelete)).execute();
		aonContext.delete(SALARY).where(SALARY.ID.in(toDelete)).execute();

		toDelete.clear();
	}

	// ------------------------------------------------------------------------

	private int getSalaryCostId(int salary, String concept) {
		SalaryCostRecord record = getSalaryCostRecord(salary, concept);
		return record != null ? record.getId()
				: next(SALARY_COST.getIdentity());
	}

	private int getSalaryPaymentId(int salary, String expression) {
		SalaryPaymentRecord record = getSalaryPaymentRecord(salary, expression);
		return record != null ? record.getId() : next(SALARY_PAYMENT
				.getIdentity());
	}

	private int getSalaryCostId(int salary, DeductionType type) {
		SalaryCostRecord record = getSalaryCostRecord(salary, type);
		return record != null ? record.getId()
				: next(SALARY_COST.getIdentity());
	}

	private int getSalaryDeductionId(int salary, DeductionType type) {
		SalaryDeductionRecord record = getSalaryDeductionRecord(salary, type);
		return record != null ? record.getId() : next(SALARY_DEDUCTION
				.getIdentity());
	}

	private SalaryCostRecord getSalaryCostRecord(int salary, String concept) {
		//@formatter:off
		return aonContext
		.select()
		.from(SALARY_COST)
		.where(SALARY_COST.SALARY.eq(salary))
		.and(SALARY_COST.COST_CONCEPT.eq(concept))
		.fetchOneInto(SALARY_COST);
		//@formatter:on
	}

	private SalaryCostRecord getSalaryCostRecord(int salary, DeductionType type) {
		//@formatter:off
		return aonContext
		.select()
		.from(SALARY_COST)
		.where(SALARY_COST.SALARY.eq(salary))
		.and(SALARY_COST.TYPE.eq(enum2Byte(type)))
		.fetchOneInto(SALARY_COST);
		//@formatter:on
	}

	private SalaryDeductionRecord getSalaryDeductionRecord(int salary,
			DeductionType type) {
		//@formatter:off
		return aonContext
		.select()
		.from(SALARY_DEDUCTION)
		.where(SALARY_DEDUCTION.SALARY.eq(salary))
		.and(SALARY_DEDUCTION.TYPE.eq(enum2Byte(type)))
		.fetchOneInto(SALARY_DEDUCTION);
		//@formatter:on
	}

	private SalaryPaymentRecord getSalaryPaymentRecord(int salary,
			String preffix) {
		//@formatter:off
		return aonContext
		.select()
		.from(SALARY_PAYMENT)
		.where(SALARY_PAYMENT.SALARY.eq(salary))
		.and(SALARY_PAYMENT.EXPRESSION.startsWith(preffix))
		.fetchOneInto(SALARY_PAYMENT);
		//@formatter:on
	}

	private String getExpressionPreffix(FnnominlRecord nominl) {
		return String.format("/*NORDEN: %s, CLAVE: %s*/",
				nominl.getF31norden(), nominl.getF31clave());
	}

	private SalaryType getSalaryType(FnnomincRecord nominc) {
		int tipo = Integer.parseInt(nominc.getF30tipo());
		switch (tipo) {
		case 2:
			return SalaryType.EXTRA;
		case 3:
			return SalaryType.SETTLE;

		default:
			return SalaryType.SALARY;
		}
	}

	private InsertSetStep<SalaryRecord> getSalaryInsertSetStep() {
		return get(insertSetMoreStepSalary, SALARY);
	}

	private InsertSetStep<SalaryCostRecord> getSalaryCostInsertSetStep() {
		return get(insertSetMoreStepSalaryCost, SALARY_COST);
	}

	private InsertSetStep<SalaryBonusRecord> getSalaryBonusInsertSetStep() {
		return get(insertSetMoreStepSalaryBonus, SALARY_BONUS);
	}

	private InsertSetStep<SalaryEmbargoRecord> getSalaryEmbargoInsertSetStep() {
		return get(insertSetMoreStepSalaryEmbargo, SALARY_EMBARGO);
	}

	private InsertSetStep<SalaryPaymentRecord> getSalaryPaymentInsertSetStep() {
		return get(insertSetMoreStepSalaryPayment, SALARY_PAYMENT);
	}

	private InsertSetStep<SalaryDeductionRecord> getSalaryDeductionInsertSetStep() {
		return get(insertSetMoreStepSalaryDeduction, SALARY_DEDUCTION);
	}

	private static FnnominlRecord getFnnominlRecord(Record record) {
		return record.into(new FnnominlRecord());
	}

}
