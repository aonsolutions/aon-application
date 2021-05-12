package com.esferalia.aon.gwt.payroll.util;

import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.Enterprise.ENTERPRISE;
import static com.esferalia.aon.jooq.tables.EnterpriseCcc.ENTERPRISE_CCC;
import static com.esferalia.aon.jooq.tables.Person.PERSON;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;

import java.io.InputStream;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import org.jooq.DSLContext;
import org.jooq.Record;

import com.esferalia.aon.in.payroll.pdf.template.AltaiPDFTemplate.PDFContract;
import com.esferalia.aon.in.payroll.pdf.template.commons.Deduction;
import com.esferalia.aon.jooq.tables.Registry;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.salary.ISalary;
import com.esferalia.aon.salary.ISalaryBuilder;
import com.esferalia.aon.salary.bonus.IBonus;
import com.esferalia.aon.salary.enumeration.BonusType;
import com.esferalia.aon.salary.enumeration.DeductionType;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.salary.expression.Period;
import com.esferalia.aon.salary.expression.TimedObject;
import com.ibm.icu.util.Calendar;

import solutions.aon.seg.social.SistemaRED;
import solutions.aon.seg.social.exception.SegSocialException;
import solutions.aon.seg.social.object.WorkerLiquidation;

public class SLD {
	/**
	 * Method to get the SLD costs from Social Security
	 * @param <T> A type extending ISalary
	 * @param salaryBuilder The ISalaryBuilder object
	 * @param ctx The DB context
	 * @param certificateInputStream The InputStream containing the certificate
	 * @param certificatePassword The password of the previous certificate
	 * @param certificateType The type of the certificate
	 * @param ccc The CCC of the enterprise
	 * @param regime The quote regime
	 * @param dateFrom
	 * @param dateTo
	 * @throws SegSocialException
	 */
	
	public static <T extends ISalary> void getSLDCosts(ISalaryBuilder<T> salaryBuilder, DSLContext ctx,
			final InputStream certificateInputStream, final String certificatePassword, final String certificateType,
			final String ccc, final SistemaRED.Regime regime, final Date dateFrom, final Date dateTo) throws SegSocialException {
		
		
		Map<String, Map<String, WorkerLiquidation>> map = SistemaRED.getWorkersLiquidationsByCCC(certificateInputStream,
				certificatePassword, certificateType, ccc, regime, dateFrom, dateTo, SistemaRED.LiquidationType.TODAS,
				SistemaRED.LiquidationOrigin.TODAS);

		Period period = new Period(dateFrom, dateTo);

		Iterator<String> it = map.keySet().iterator();

		while (it.hasNext()) {
			String key = it.next();
			Map<String, WorkerLiquidation> innerMap = map.get(key);
			Iterator<String> it2 = innerMap.keySet().iterator();
			while (it2.hasNext()) {
				String naf = it2.next();
				WorkerLiquidation liquidation = innerMap.get(naf);
				
				Registry r1 = REGISTRY.as("r1");
				Registry r2 = REGISTRY.as("r2");

				Record record = ctx.select().from(CONTRACT).innerJoin(ENTERPRISE_CCC).onKey().innerJoin(PERSON).onKey()
						.innerJoin(r1).on(r1.ID.eq(PERSON.REGISTRY))
						.innerJoin(ENTERPRISE).on(ENTERPRISE.DOMAIN.eq(ENTERPRISE_CCC.DOMAIN))
						.innerJoin(r2).on(r2.ID.eq(ENTERPRISE.REGISTRY))
						.where(
							ENTERPRISE_CCC.CCC.eq(ccc)
								.and(PERSON.SOCIAL_SECURITY_NUM.eq(liquidation.getNss()))
								.and(CONTRACT.START_DATE.le(new java.sql.Date(dateFrom.getTime())))
								.and(CONTRACT.END_DATE.isNull()
									.or(CONTRACT.END_DATE.ge(new java.sql.Date(dateTo.getTime()))))
						).fetchOne();

				if (record != null) {

					double total_deduction = 0;
					double total_enterprise = 0;

					salaryBuilder.createNewSalary();
					
					switch (key) {
					case "L00":
						salaryBuilder.setType(SalaryType.SALARY);
						break;
					case "L13":
						salaryBuilder.setType(SalaryType.SETTLE);
						break;
					case "L03":
						salaryBuilder.setType(SalaryType.SALARY);
						break;
					default:
						salaryBuilder.setType(SalaryType.SALARY);
					}
					
					
					salaryBuilder.setRegistration(Integer.MIN_VALUE);

					salaryBuilder.setSocialSecurityNumber(liquidation.getNss());
					salaryBuilder.setCcc(ccc);
					salaryBuilder.setStartDate(dateFrom);
					salaryBuilder.setEndDate(dateTo);

					salaryBuilder.setContract(new PDFContract().setCcc(ccc)
							.setNaf(record.get(PERSON.SOCIAL_SECURITY_NUM)).setNif(record.get(r1.DOCUMENT))
							.setCif(record.get(r2.DOCUMENT)).setEndDate(dateTo).setStartDate(dateFrom)
							.setEmployeeName(record.get(REGISTRY.NAME)).setEnterpriseName(record.get(r2.NAME)));

					salaryBuilder.setRegime("" + record.get(CONTRACT.SS_REGIME));

					Date difference = new Date(dateTo.getTime() - dateFrom.getTime());

					Calendar calDiff = Calendar.getInstance();
					calDiff.setTime(difference);


					salaryBuilder.setEmployeeName(record.get(r1.NAME));
					salaryBuilder.setEnterpriseName(record.get(r2.NAME));
					salaryBuilder.setEnterpriseDocument(record.get(r2.DOCUMENT));
					salaryBuilder.setSeniorityDate(record.get(CONTRACT.SENIORITY_DATE));
					salaryBuilder.setEmployeeDocument(record.get(r1.DOCUMENT));
					salaryBuilder.setTimeUnits(calDiff.get(Calendar.DAY_OF_YEAR));
					salaryBuilder.setTotalPayment(0d);
					salaryBuilder.setTotalDeduction(total_deduction);
					salaryBuilder.setTotalEnterprise(total_enterprise);
					salaryBuilder.setIssueDate(dateTo);
					salaryBuilder.setRemuneration(0d);
					salaryBuilder.setProExtBase(0d);
					salaryBuilder.setIrpfBase(0d);
					salaryBuilder.setChargeDate(dateTo);

					if (liquidation.getCcBase() != null) {
						salaryBuilder.setCgcBase(liquidation.getCcBase().doubleValue());
						salaryBuilder.setRawCgcBase(liquidation.getCcBase().doubleValue());
					} else {
						salaryBuilder.setCgcBase(0d);
						salaryBuilder.setRawCgcBase(0d);
					}
					
					if (liquidation.getCcBase() != null)
						salaryBuilder.addData(ContextVariable.CGC_BASE.getName(),
								new TimedObject<Double>(roundNumber(liquidation.getCcBase().doubleValue(), 2), period));

					if (liquidation.getCcBusinessFee() != null) {
						salaryBuilder.addCost(liquidation.getCcBusinessFee().doubleValue(),
								DeductionType.COMMON_CONTINGENCY.getName(new Locale("es", "ES")), dateFrom, dateTo,
								new Deduction().setType(DeductionType.COMMON_CONTINGENCY).setName("CGC_E"),
								Collections.emptyMap());
						total_enterprise += liquidation.getCcBusinessFee().doubleValue();
					}

					if (liquidation.getCcWorkerFee() != null) {
						salaryBuilder.addDeduction(liquidation.getCcWorkerFee().doubleValue(),
								DeductionType.COMMON_CONTINGENCY.getName(new Locale("es", "ES")), dateFrom, dateTo,
								new Deduction().setType(DeductionType.COMMON_CONTINGENCY).setName("CGC"),
								Collections.emptyMap());
						total_deduction += liquidation.getCcWorkerFee().doubleValue();
					}

					// I DON'T KNOW WHAT IS ccLiquid_base
					if (liquidation.getItWorkAccidentBase() != null)
						salaryBuilder.setCgpBase(liquidation.getItWorkAccidentBase().doubleValue());
					else
						salaryBuilder.setCgpBase(0d);
					if (liquidation.getItWorkAccidentBase() != null)
						salaryBuilder.addData(ContextVariable.CGP_BASE.getName(),
								new TimedObject<Double>(roundNumber(liquidation.getItWorkAccidentBase().doubleValue(), 2), period));
					if (liquidation.getItWorkAccidentBusinessFee() != null) {
						salaryBuilder.addCost(liquidation.getItWorkAccidentBusinessFee().doubleValue(),
								DeductionType.PROFESSIONAL_CONTINGENCY.getName(new Locale("es", "ES")), dateFrom,
								dateTo,
								new Deduction().setType(DeductionType.PROFESSIONAL_CONTINGENCY).setName("CGP_E"),
								Collections.emptyMap());
						total_enterprise += liquidation.getItWorkAccidentBusinessFee().doubleValue();
					}

					if (liquidation.getItWorkAccidentWorkerFee() != null) {
						salaryBuilder.addDeduction(liquidation.getItWorkAccidentWorkerFee().doubleValue(),
								DeductionType.PROFESSIONAL_CONTINGENCY.getName(new Locale("es", "ES")), dateFrom,
								dateTo, new Deduction().setType(DeductionType.PROFESSIONAL_CONTINGENCY).setName("CGP"),
								Collections.emptyMap());
						total_deduction += liquidation.getItWorkAccidentWorkerFee().doubleValue();
					}

					if (liquidation.getImsWorkAccidentBusinessFee() != null) {
						salaryBuilder.addCost(liquidation.getImsWorkAccidentBusinessFee().doubleValue(), "IMS_E",
								dateFrom, dateTo,
								new Deduction().setType(DeductionType.PROFESSIONAL_CONTINGENCY).setName("IMS_E"),
								Collections.emptyMap());
						total_enterprise += liquidation.getImsWorkAccidentBusinessFee().doubleValue();
					}

					if (liquidation.getUnemploymentBase() != null)
						salaryBuilder.addData(ContextVariable.UNEMPLOY_EMPLOYEE.getName(),
								new TimedObject<Double>(roundNumber(liquidation.getUnemploymentBase().doubleValue(), 2), period));
					if (liquidation.getUnemploymentBusinessFee() != null) {
						salaryBuilder.addCost(liquidation.getUnemploymentBusinessFee().doubleValue(),
								DeductionType.UNEMPLOYMENT.getName(new Locale("es", "ES")), dateFrom, dateTo,
								new Deduction().setType(DeductionType.UNEMPLOYMENT).setName("DESMPL_E"),
								Collections.emptyMap());
						total_enterprise += liquidation.getUnemploymentBusinessFee().doubleValue();
					}

					if (liquidation.getUnemploymentWorkerFee() != null) {
						salaryBuilder.addDeduction(liquidation.getUnemploymentWorkerFee().doubleValue(),
								DeductionType.UNEMPLOYMENT.getName(new Locale("es", "ES")), dateFrom, dateTo,
								new Deduction().setType(DeductionType.UNEMPLOYMENT).setName("DESMPL"),
								Collections.emptyMap());
						total_deduction += liquidation.getUnemploymentWorkerFee().doubleValue();
					}

					if (liquidation.getFogasaBase() != null)
						salaryBuilder.addData(ContextVariable.FOGASA_ENTERPRISE.getName(),
								new TimedObject<Double>(roundNumber(liquidation.getFogasaBase().doubleValue(), 2), period));
					if (liquidation.getFogasaBusinessFee() != null) {
						salaryBuilder.addCost(liquidation.getFogasaBusinessFee().doubleValue(),
								DeductionType.FOGASA.getName(new Locale("es", "ES")), dateFrom, dateTo,
								new Deduction().setType(DeductionType.FOGASA).setName("FOGASA_E"),
								Collections.emptyMap());
						total_enterprise += liquidation.getFogasaBusinessFee().doubleValue();
					}

					if (liquidation.getFogasaWorkerFee() != null) {
						salaryBuilder.addDeduction(liquidation.getFogasaWorkerFee().doubleValue(), "FOGASA", dateFrom,
								dateTo, new Deduction().setType(DeductionType.FOGASA).setName("FOGASA"),
								Collections.emptyMap());
						total_deduction += liquidation.getFogasaWorkerFee().doubleValue();
					}

					if (liquidation.getJobTrainingBase() != null)
						salaryBuilder.addData(ContextVariable.FP_EMPLOYEE.getName(),
								new TimedObject<Double>(roundNumber(liquidation.getJobTrainingBase().doubleValue(), 2), period));
					if (liquidation.getJobTrainingBusinessFee() != null) {
						salaryBuilder.addCost(liquidation.getJobTrainingBusinessFee().doubleValue(),
								DeductionType.JOB_TRAINING.getName(new Locale("es", "ES")), dateFrom, dateTo,
								new Deduction().setType(DeductionType.JOB_TRAINING).setName("FP_E"),
								Collections.emptyMap());
						total_enterprise += liquidation.getJobTrainingBusinessFee().doubleValue();
					}

					if (liquidation.getJobTrainingWorkerFee() != null) {
						salaryBuilder.addDeduction(liquidation.getJobTrainingWorkerFee().doubleValue(),
								DeductionType.JOB_TRAINING.getName(new Locale("es", "ES")), dateFrom, dateTo,
								new Deduction().setType(DeductionType.JOB_TRAINING).setName("FP"),
								Collections.emptyMap());
						total_deduction += liquidation.getJobTrainingWorkerFee().doubleValue();
					}


					if (liquidation.getGrantsAndBonusesBusinessFee() != null)
						salaryBuilder.addBonus(liquidation.getGrantsAndBonusesBusinessFee().doubleValue(), "BONIF_E",
								dateFrom, dateTo, new Bonus().setName("BONIF_E"), Collections.emptyMap());
					if (liquidation.getGrantsAndBonusesWorkerFee() != null)
						salaryBuilder.addBonus(liquidation.getGrantsAndBonusesWorkerFee().doubleValue(), "BONIF",
								dateFrom, dateTo, new Bonus().setName("BONIF"), Collections.emptyMap());

					if (liquidation.getOtherContributionsLiquidBase() != null)
						salaryBuilder.addData("OTHER_BASE", new TimedObject<Double>(
								roundNumber(liquidation.getOtherContributionsLiquidBase().doubleValue(), 2), period));
					if (liquidation.getOtherContributionsLiquidBusinessFee() != null) {
						salaryBuilder.addCost(liquidation.getOtherContributionsLiquidBusinessFee().doubleValue(),
								DeductionType.OTHER.getName(new Locale("es", "ES")), dateFrom, dateTo,
								new Deduction().setType(DeductionType.OTHER).setName("OTHER_E"), Collections.emptyMap());
						total_enterprise += liquidation.getOtherContributionsLiquidBusinessFee().doubleValue();
					}

					if (liquidation.getOtherContributionsLiquidWorkerFee() != null) {
						salaryBuilder.addDeduction(liquidation.getOtherContributionsLiquidWorkerFee().doubleValue(),
								DeductionType.OTHER.getName(new Locale("es", "ES")), dateFrom, dateTo,
								new Deduction().setType(DeductionType.OTHER).setName("OTHER"), Collections.emptyMap());
						total_deduction += liquidation.getOtherContributionsLiquidWorkerFee().doubleValue();
					}

					if (liquidation.getTotalLiquidTotalFee() != null)
						salaryBuilder.setTotalLiquid(liquidation.getTotalLiquidTotalFee().doubleValue());
					else
						salaryBuilder.setTotalLiquid(0d);

					salaryBuilder.getSalary();

				}

			}

		}
	}

	/**
	 * Method to get the SLD costs from Social Security, this may be used in case there are too much entries and the S.S. page asks for individual NAF input
	 * @param <T> A type extending ISalary
	 * @param salaryBuilder The ISalaryBuilder object
	 * @param ctx The DB context
	 * @param certificateInputStream The InputStream containing the certificate
	 * @param certificatePassword The password of the previus certificate
	 * @param certificateType The type of the certificate
	 * @param ccc The CCC of the enterprise
	 * @param regime The quote regime
	 * @param dateFrom
	 * @param dateTo
	 * @param nafs The NAFs of employees whose info is desired
	 * @throws SegSocialException
	 */
	public static <T extends ISalary> void getSLDCostsByNAFs(ISalaryBuilder<T> salaryBuilder, DSLContext ctx,
			final InputStream certificateInputStream, final String certificatePassword, final String certificateType,
			final String ccc, final SistemaRED.Regime regime, final Date dateFrom, final Date dateTo, String... nafs)
			throws SegSocialException {
		Map<String, Map<String, WorkerLiquidation>> map = SistemaRED.getWorkersLiquidationsByCCCandNAFs(
				certificateInputStream, certificatePassword, certificateType, ccc, regime, dateFrom, dateTo,
				SistemaRED.LiquidationType.TODAS, SistemaRED.LiquidationOrigin.TODAS, nafs);

		Period period = new Period(dateFrom, dateTo);

		Iterator<String> it = map.keySet().iterator();

		while (it.hasNext()) {
			String key = it.next();
			Map<String, WorkerLiquidation> innerMap = map.get(key);
			Iterator<String> it2 = innerMap.keySet().iterator();
			while (it2.hasNext()) {
				String naf = it2.next();
				WorkerLiquidation liquidation = innerMap.get(naf);
				
				Registry r1 = REGISTRY.as("r1");
				Registry r2 = REGISTRY.as("r2");

				Record record = ctx.select().from(CONTRACT).innerJoin(ENTERPRISE_CCC).onKey().innerJoin(PERSON).onKey()
						.innerJoin(r1).on(r1.ID.eq(PERSON.REGISTRY))
						.innerJoin(ENTERPRISE).on(ENTERPRISE.DOMAIN.eq(ENTERPRISE_CCC.DOMAIN))
						.innerJoin(r2).on(r2.ID.eq(ENTERPRISE.REGISTRY))
						.where(
								ENTERPRISE_CCC.CCC.eq(ccc).and(PERSON.SOCIAL_SECURITY_NUM.eq(liquidation.getNss()))
										.and(CONTRACT.START_DATE.le(new java.sql.Date(dateFrom.getTime())))
										.and(CONTRACT.END_DATE.isNull()
												.or(CONTRACT.END_DATE.ge(new java.sql.Date(dateTo.getTime()))))
								).fetchOne();

				if (record != null) {

					salaryBuilder.createNewSalary();
					
					switch (key) {
					default:
						salaryBuilder.setType(SalaryType.SALARY);
					}
					
					
					salaryBuilder.setRegistration(Integer.MIN_VALUE);

					salaryBuilder.setSocialSecurityNumber(liquidation.getNss());
					salaryBuilder.setCcc(ccc);
					salaryBuilder.setStartDate(dateFrom);
					salaryBuilder.setEndDate(dateTo);
					

					salaryBuilder.setContract(new PDFContract().setCcc(ccc)
							.setNaf(record.get(PERSON.SOCIAL_SECURITY_NUM)).setNif(record.get(r1.DOCUMENT))
							.setCif(record.get(r2.DOCUMENT)).setEndDate(dateTo).setStartDate(dateFrom)
							.setEmployeeName(record.get(REGISTRY.NAME)).setEnterpriseName(record.get(r2.NAME)));

					salaryBuilder.setRegime("" + record.get(CONTRACT.SS_REGIME));

					Date difference = new Date(dateTo.getTime() - dateFrom.getTime());

					Calendar calDiff = Calendar.getInstance();
					calDiff.setTime(difference);

					salaryBuilder.setEmployeeName(record.get(r1.NAME));
					salaryBuilder.setEnterpriseName(record.get(r2.NAME));
					salaryBuilder.setEnterpriseDocument(record.get(r2.DOCUMENT));
					salaryBuilder.setSeniorityDate(record.get(CONTRACT.SENIORITY_DATE));
					salaryBuilder.setEmployeeDocument(record.get(r1.DOCUMENT));
					salaryBuilder.setTimeUnits(calDiff.get(Calendar.DAY_OF_YEAR));
					
					salaryBuilder.addData("DIAS_NOMINA", new TimedObject<Integer>(calDiff.get(Calendar.DAY_OF_YEAR), period));
					
					salaryBuilder.setCategory(record.get(CONTRACT.CATEGORY_DESCRIPTION));
					salaryBuilder.setTotalPayment(0d);
					salaryBuilder.setIssueDate(dateTo);
					salaryBuilder.setRemuneration(0d);
					salaryBuilder.setProExtBase(0d);
					salaryBuilder.setIrpfBase(0d);
					salaryBuilder.setChargeDate(dateTo);

					if (liquidation.getCcBase() != null) {
						salaryBuilder.setCgcBase(liquidation.getCcBase().doubleValue());
						salaryBuilder.setRawCgcBase(liquidation.getCcBase().doubleValue());
					} else {
						salaryBuilder.setCgcBase(0d);
						salaryBuilder.setRawCgcBase(0d);
					}

					if (liquidation.getCcBase() != null)
						salaryBuilder.addData(ContextVariable.CGC_BASE.getName(),
								new TimedObject<Double>(roundNumber(liquidation.getCcBase().doubleValue(), 2), period));

					if (liquidation.getCcBusinessFee() != null) {
						salaryBuilder.addCost(liquidation.getCcBusinessFee().doubleValue(),
								DeductionType.COMMON_CONTINGENCY.getName(new Locale("es", "ES")), dateFrom, dateTo,
								new Deduction().setType(DeductionType.COMMON_CONTINGENCY).setName("CGC_E"),
								Collections.emptyMap());
					}

					if (liquidation.getCcWorkerFee() != null) {
						salaryBuilder.addDeduction(liquidation.getCcWorkerFee().doubleValue(),
								DeductionType.COMMON_CONTINGENCY.getName(new Locale("es", "ES")), dateFrom, dateTo,
								new Deduction().setType(DeductionType.COMMON_CONTINGENCY).setName("CGC"),
								Collections.emptyMap());
					}

					// I DON'T KNOW WHAT IS ccLiquid_base
					if (liquidation.getItWorkAccidentBase() != null)
						salaryBuilder.setCgpBase(liquidation.getItWorkAccidentBase().doubleValue());
					else
						salaryBuilder.setCgpBase(0d);
					if (liquidation.getItWorkAccidentBase() != null)
						salaryBuilder.addData(ContextVariable.CGP_BASE.getName(),
								new TimedObject<Double>(roundNumber(liquidation.getItWorkAccidentBase().doubleValue(), 2), period));
					if (liquidation.getItWorkAccidentBusinessFee() != null) {
						salaryBuilder.addCost(liquidation.getItWorkAccidentBusinessFee().doubleValue(),
								DeductionType.PROFESSIONAL_CONTINGENCY.getName(new Locale("es", "ES")), dateFrom,
								dateTo,
								new Deduction().setType(DeductionType.PROFESSIONAL_CONTINGENCY).setName("CGP_E"),
								Collections.emptyMap());
					}

					if (liquidation.getItWorkAccidentWorkerFee() != null) {
						salaryBuilder.addDeduction(liquidation.getItWorkAccidentWorkerFee().doubleValue(),
								DeductionType.PROFESSIONAL_CONTINGENCY.getName(new Locale("es", "ES")), dateFrom,
								dateTo, new Deduction().setType(DeductionType.PROFESSIONAL_CONTINGENCY).setName("CGP"),
								Collections.emptyMap());
					}

					if (liquidation.getImsWorkAccidentBusinessFee() != null) {
						salaryBuilder.addCost(liquidation.getImsWorkAccidentBusinessFee().doubleValue(), "IMS_E",
								dateFrom, dateTo,
								new Deduction().setType(DeductionType.PROFESSIONAL_CONTINGENCY).setName("IMS_E"),
								Collections.emptyMap());
					}

					if (liquidation.getUnemploymentBase() != null)
						salaryBuilder.addData(ContextVariable.UNEMPLOY_EMPLOYEE.getName(),
								new TimedObject<Double>(roundNumber(liquidation.getUnemploymentBase().doubleValue(), 2), period));
					if (liquidation.getUnemploymentBusinessFee() != null) {
						salaryBuilder.addCost(liquidation.getUnemploymentBusinessFee().doubleValue(),
								DeductionType.UNEMPLOYMENT.getName(new Locale("es", "ES")), dateFrom, dateTo,
								new Deduction().setType(DeductionType.UNEMPLOYMENT).setName("DESMPL_E"),
								Collections.emptyMap());
					}

					if (liquidation.getUnemploymentWorkerFee() != null) {
						salaryBuilder.addDeduction(liquidation.getUnemploymentWorkerFee().doubleValue(),
								DeductionType.UNEMPLOYMENT.getName(new Locale("es", "ES")), dateFrom, dateTo,
								new Deduction().setType(DeductionType.UNEMPLOYMENT).setName("DESMPL"),
								Collections.emptyMap());
					}

					if (liquidation.getFogasaBase() != null)
						salaryBuilder.addData(ContextVariable.FOGASA_ENTERPRISE.getName(),
								new TimedObject<Double>(roundNumber(liquidation.getFogasaBase().doubleValue(), 2), period));
					if (liquidation.getFogasaBusinessFee() != null) {
						salaryBuilder.addCost(liquidation.getFogasaBusinessFee().doubleValue(),
								DeductionType.FOGASA.getName(new Locale("es", "ES")), dateFrom, dateTo,
								new Deduction().setType(DeductionType.FOGASA).setName("FOGASA_E"),
								Collections.emptyMap());
					}

					if (liquidation.getFogasaWorkerFee() != null) {
						salaryBuilder.addDeduction(liquidation.getFogasaWorkerFee().doubleValue(), "FOGASA", dateFrom,
								dateTo, new Deduction().setType(DeductionType.FOGASA).setName("FOGASA"),
								Collections.emptyMap());
					}

					if (liquidation.getJobTrainingBase() != null)
						salaryBuilder.addData(ContextVariable.FP_EMPLOYEE.getName(),
								new TimedObject<Double>(roundNumber(liquidation.getJobTrainingBase().doubleValue(), 2), period));
					if (liquidation.getJobTrainingBusinessFee() != null) {
						salaryBuilder.addCost(liquidation.getJobTrainingBusinessFee().doubleValue(),
								DeductionType.JOB_TRAINING.getName(new Locale("es", "ES")), dateFrom, dateTo,
								new Deduction().setType(DeductionType.JOB_TRAINING).setName("FP_E"),
								Collections.emptyMap());
					}

					if (liquidation.getJobTrainingWorkerFee() != null) {
						salaryBuilder.addDeduction(liquidation.getJobTrainingWorkerFee().doubleValue(),
								DeductionType.JOB_TRAINING.getName(new Locale("es", "ES")), dateFrom, dateTo,
								new Deduction().setType(DeductionType.JOB_TRAINING).setName("FP"),
								Collections.emptyMap());
					}


					if (liquidation.getGrantsAndBonusesBusinessFee() != null)
						salaryBuilder.addBonus(liquidation.getGrantsAndBonusesBusinessFee().doubleValue(), "BONIF_E",
								dateFrom, dateTo, new Bonus().setName("BONIF_E"), Collections.emptyMap());

					if (liquidation.getGrantsAndBonusesWorkerFee() != null)
						salaryBuilder.addBonus(liquidation.getGrantsAndBonusesWorkerFee().doubleValue(), "BONIF",
								dateFrom, dateTo, new Bonus().setName("BONIF"), Collections.emptyMap());
					
					if (liquidation.getTotalLiquidBusinessFee() != null)
						salaryBuilder.setTotalEnterprise(liquidation.getTotalLiquidBusinessFee().doubleValue());
					else
						salaryBuilder.setTotalEnterprise(0d);
					
					if (liquidation.getTotalLiquidWorkerFee() != null)
						salaryBuilder.setTotalDeduction(liquidation.getTotalLiquidWorkerFee().doubleValue());
					else
						salaryBuilder.setTotalDeduction(0d);
					
					if (liquidation.getTotalLiquidTotalFee() != null)
						salaryBuilder.setTotalSS(liquidation.getTotalLiquidTotalFee().doubleValue());
					else
						salaryBuilder.setTotalSS(0d);
					
					salaryBuilder.getSalary();

				}

			}

		}

	}
	
	private static <T extends Number> Double roundNumber(T number, int precission) {
		if(number == null)
			return null;
		Double num = number.doubleValue();
		int prec = 1;
		for (int i=0; i<precission; i++)
			prec*=10;
		return ((double)Math.round(num*prec))/prec;
	}

	private static class Bonus implements IBonus {

		private BonusType type;
		private String name;
		private double amount;
		private String description;
		private String expression;

		@Override
		public BonusType getType() {
			return type;
		}

		@Override
		public String getName() {
			return name;
		}

		@Override
		public double getAmount() {
			return amount;
		}

		@Override
		public String getDescription() {
			return description;
		}

		@Override
		public String getExpression() {
			return expression;
		}

		public Bonus setType(BonusType type) {
			this.type = type;
			return this;
		}

		public Bonus setName(String name) {
			this.name = name;
			return this;
		}

		public Bonus setAmount(double amount) {
			this.amount = amount;
			return this;
		}

		public Bonus setDescription(String description) {
			this.description = description;
			return this;
		}

		public Bonus setExpression(String expression) {
			this.expression = expression;
			return this;
		}

	}

}
