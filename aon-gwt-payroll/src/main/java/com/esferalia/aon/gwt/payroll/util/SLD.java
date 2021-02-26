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

import com.esferalia.aon.in.payroll.pdf.templates.AltaiPDFTemplate.PDFContract;
import com.esferalia.aon.in.payroll.pdf.templates.common_classes.Deduction;
import com.esferalia.aon.jooq.tables.Registry;
import com.esferalia.aon.jooq.tables.records.RegistryRecord;
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
import solutions.aon.seg.social.SistemaRED_I.LiquidationOrigin;
import solutions.aon.seg.social.SistemaRED_I.LiquidationType;
import solutions.aon.seg.social.SistemaRED_I.Regime;
import solutions.aon.seg.social.exceptions.SegSocialException;
import solutions.aon.seg.social.objects.WorkerLiquidation;

public class SLD {
	public static <T extends ISalary> void getSLDCosts(ISalaryBuilder<T> salaryBuilder, DSLContext ctx,
			final InputStream certificateInputStream, final String certificatePassword, final String certificateType,
			final String ccc, final Regime regime, final Date dateFrom, final Date dateTo) throws SegSocialException {
		Map<String, Map<String, WorkerLiquidation>> map = SistemaRED.getWorkersLiquidationsByCCC(certificateInputStream,
				certificatePassword, certificateType, ccc, regime, dateFrom, dateTo, LiquidationType.TODAS,
				LiquidationOrigin.TODAS);

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

//					Long longTimeUnits = (dateTo.getTime() - dateFrom.getTime()) / 1000 / 60 / 60 / 24;

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

					if (liquidation.getCc_base() != null) {
						salaryBuilder.setCgcBase(liquidation.getCc_base().doubleValue());
						salaryBuilder.setRawCgcBase(liquidation.getCc_base().doubleValue());
					} else {
						salaryBuilder.setCgcBase(0d);
						salaryBuilder.setRawCgcBase(0d);
					}
					
					if (liquidation.getCc_base() != null)
						salaryBuilder.addData(ContextVariable.CGC_BASE.getName(),
								new TimedObject<Double>(roundNumber(liquidation.getCc_base().doubleValue(), 2), period));

					if (liquidation.getCc_businessFee() != null) {
						salaryBuilder.addCost(liquidation.getCc_businessFee().doubleValue(),
								DeductionType.COMMON_CONTINGENCY.getName(new Locale("es", "ES")), dateFrom, dateTo,
								new Deduction().setType(DeductionType.COMMON_CONTINGENCY).setName("CGC_E"),
								Collections.EMPTY_MAP);
						total_enterprise += liquidation.getCc_businessFee().doubleValue();
					}

					if (liquidation.getCc_workerFee() != null) {
						salaryBuilder.addDeduction(liquidation.getCc_workerFee().doubleValue(),
								DeductionType.COMMON_CONTINGENCY.getName(new Locale("es", "ES")), dateFrom, dateTo,
								new Deduction().setType(DeductionType.COMMON_CONTINGENCY).setName("CGC"),
								Collections.EMPTY_MAP);
						total_deduction += liquidation.getCc_workerFee().doubleValue();
					}

					// I DON'T KNOW WHAT IS ccLiquid_base
					if (liquidation.getItWorkAccident_base() != null)
						salaryBuilder.setCgpBase(liquidation.getItWorkAccident_base().doubleValue());
					else
						salaryBuilder.setCgpBase(0d);
					if (liquidation.getItWorkAccident_base() != null)
						salaryBuilder.addData(ContextVariable.CGP_BASE.getName(),
								new TimedObject<Double>(roundNumber(liquidation.getItWorkAccident_base().doubleValue(), 2), period));
					if (liquidation.getItWorkAccident_businessFee() != null) {
						salaryBuilder.addCost(liquidation.getItWorkAccident_businessFee().doubleValue(),
								DeductionType.PROFESSIONAL_CONTINGENCY.getName(new Locale("es", "ES")), dateFrom,
								dateTo,
								new Deduction().setType(DeductionType.PROFESSIONAL_CONTINGENCY).setName("CGP_E"),
								Collections.EMPTY_MAP);
						total_enterprise += liquidation.getItWorkAccident_businessFee().doubleValue();
					}

					if (liquidation.getItWorkAccident_workerFee() != null) {
						salaryBuilder.addDeduction(liquidation.getItWorkAccident_workerFee().doubleValue(),
								DeductionType.PROFESSIONAL_CONTINGENCY.getName(new Locale("es", "ES")), dateFrom,
								dateTo, new Deduction().setType(DeductionType.PROFESSIONAL_CONTINGENCY).setName("CGP"),
								Collections.EMPTY_MAP);
						total_deduction += liquidation.getItWorkAccident_workerFee().doubleValue();
					}

					if (liquidation.getImsWorkAccident_businessFee() != null) {
						salaryBuilder.addCost(liquidation.getImsWorkAccident_businessFee().doubleValue(), "IMS_E",
								dateFrom, dateTo,
								new Deduction().setType(DeductionType.PROFESSIONAL_CONTINGENCY).setName("IMS_E"),
								Collections.EMPTY_MAP);
						total_enterprise += liquidation.getImsWorkAccident_businessFee().doubleValue();
					}

					if (liquidation.getUnemployment_base() != null)
						salaryBuilder.addData(ContextVariable.UNEMPLOY_EMPLOYEE.getName(),
								new TimedObject<Double>(roundNumber(liquidation.getUnemployment_base().doubleValue(), 2), period));
					if (liquidation.getUnemployment_businessFee() != null) {
						salaryBuilder.addCost(liquidation.getUnemployment_businessFee().doubleValue(),
								DeductionType.UNEMPLOYMENT.getName(new Locale("es", "ES")), dateFrom, dateTo,
								new Deduction().setType(DeductionType.UNEMPLOYMENT).setName("DESMPL_E"),
								Collections.EMPTY_MAP);
						total_enterprise += liquidation.getUnemployment_businessFee().doubleValue();
					}

					if (liquidation.getUnemployment_workerFee() != null) {
						salaryBuilder.addDeduction(liquidation.getUnemployment_workerFee().doubleValue(),
								DeductionType.UNEMPLOYMENT.getName(new Locale("es", "ES")), dateFrom, dateTo,
								new Deduction().setType(DeductionType.UNEMPLOYMENT).setName("DESMPL"),
								Collections.EMPTY_MAP);
						total_deduction += liquidation.getUnemployment_workerFee().doubleValue();
					}

					if (liquidation.getFogasa_base() != null)
						salaryBuilder.addData(ContextVariable.FOGASA_ENTERPRISE.getName(),
								new TimedObject<Double>(roundNumber(liquidation.getFogasa_base().doubleValue(), 2), period));
					if (liquidation.getFogasa_businessFee() != null) {
						salaryBuilder.addCost(liquidation.getFogasa_businessFee().doubleValue(),
								DeductionType.FOGASA.getName(new Locale("es", "ES")), dateFrom, dateTo,
								new Deduction().setType(DeductionType.FOGASA).setName("FOGASA_E"),
								Collections.EMPTY_MAP);
						total_enterprise += liquidation.getFogasa_businessFee().doubleValue();
					}

					if (liquidation.getFogasa_workerFee() != null) {
						salaryBuilder.addDeduction(liquidation.getFogasa_workerFee().doubleValue(), "FOGASA", dateFrom,
								dateTo, new Deduction().setType(DeductionType.FOGASA).setName("FOGASA"),
								Collections.EMPTY_MAP);
						total_deduction += liquidation.getFogasa_workerFee().doubleValue();
					}

					if (liquidation.getJobTraining_base() != null)
						salaryBuilder.addData(ContextVariable.FP_EMPLOYEE.getName(),
								new TimedObject<Double>(roundNumber(liquidation.getJobTraining_base().doubleValue(), 2), period));
					if (liquidation.getJobTraining_businessFee() != null) {
						salaryBuilder.addCost(liquidation.getJobTraining_businessFee().doubleValue(),
								DeductionType.JOB_TRAINING.getName(new Locale("es", "ES")), dateFrom, dateTo,
								new Deduction().setType(DeductionType.JOB_TRAINING).setName("FP_E"),
								Collections.EMPTY_MAP);
						total_enterprise += liquidation.getJobTraining_businessFee().doubleValue();
					}

					if (liquidation.getJobTraining_workerFee() != null) {
						salaryBuilder.addDeduction(liquidation.getJobTraining_workerFee().doubleValue(),
								DeductionType.JOB_TRAINING.getName(new Locale("es", "ES")), dateFrom, dateTo,
								new Deduction().setType(DeductionType.JOB_TRAINING).setName("FP"),
								Collections.EMPTY_MAP);
						total_deduction += liquidation.getJobTraining_workerFee().doubleValue();
					}

//					if (liquidation.getGrantsAndBonuses_totalFee() != null) {
//						salaryBuilder.addBonus(liquidation.getGrantsAndBonuses_totalFee().doubleValue()
//							, "BONIFICACIONES"
//							, dateFrom
//							, dateTo
//							, new Bonus().setName("BONIFICACIONES")
//							, Collections.EMPTY_MAP);
//						
//					}

					if (liquidation.getGrantsAndBonuses_businessFee() != null)
						salaryBuilder.addBonus(liquidation.getGrantsAndBonuses_businessFee().doubleValue(), "BONIF_E",
								dateFrom, dateTo, new Bonus().setName("BONIF_E"), Collections.EMPTY_MAP);
//						salaryBuilder.addCost((-1)*liquidation.getGrantsAndBonuses_businessFee().doubleValue()
//							, "BONIFICACIONES"
//							, dateFrom
//							, dateTo
//							, new Deduction().setName("BONIFICACIONES_E")
//							, Collections.EMPTY_MAP);
					if (liquidation.getGrantsAndBonuses_workerFee() != null)
						salaryBuilder.addBonus(liquidation.getGrantsAndBonuses_workerFee().doubleValue(), "BONIF",
								dateFrom, dateTo, new Bonus().setName("BONIF"), Collections.EMPTY_MAP);
//						salaryBuilder.addDeduction((-1)*liquidation.getGrantsAndBonuses_workerFee().doubleValue()
//							, "BONIF"
//							, dateFrom
//							, dateTo
//							, new Deduction().setName("BONIF")
//							, Collections.EMPTY_MAP);

					if (liquidation.getOtherContributionsLiquid_base() != null)
						salaryBuilder.addData("OTHER_BASE", new TimedObject<Double>(
								roundNumber(liquidation.getOtherContributionsLiquid_base().doubleValue(), 2), period));
					if (liquidation.getOtherContributionsLiquid_businessFee() != null) {
						salaryBuilder.addCost(liquidation.getOtherContributionsLiquid_businessFee().doubleValue(),
								DeductionType.OTHER.getName(new Locale("es", "ES")), dateFrom, dateTo,
								new Deduction().setType(DeductionType.OTHER).setName("OTHER_E"), Collections.EMPTY_MAP);
						total_enterprise += liquidation.getOtherContributionsLiquid_businessFee().doubleValue();
					}

					if (liquidation.getOtherContributionsLiquid_workerFee() != null) {
						salaryBuilder.addDeduction(liquidation.getOtherContributionsLiquid_workerFee().doubleValue(),
								DeductionType.OTHER.getName(new Locale("es", "ES")), dateFrom, dateTo,
								new Deduction().setType(DeductionType.OTHER).setName("OTHER"), Collections.EMPTY_MAP);
						total_deduction += liquidation.getOtherContributionsLiquid_workerFee().doubleValue();
					}

					if (liquidation.getTotalLiquid_totalFee() != null)
						salaryBuilder.setTotalLiquid(liquidation.getTotalLiquid_totalFee().doubleValue());
					else
						salaryBuilder.setTotalLiquid(0d);

					salaryBuilder.getSalary();

				}

			}

		}

//		Period period = new Period(dateFrom, dateTo);
//
//		Iterator<String> it = map.keySet().iterator();
//
//		while (it.hasNext()) {
//			String key = it.next();
//			Map<String, WorkerLiquidation> innerMap = map.get(key);
//			Iterator<String> it2 = innerMap.keySet().iterator();
//			while (it2.hasNext()) {
//				String naf = it2.next();
//				WorkerLiquidation liquidation = innerMap.get(naf);
//
//				double total_deduction = 0;
//				double total_enterprise = 0;
//
//				salaryBuilder.createNewSalary();
//				salaryBuilder.setType(SalaryType.SALARY);
//				salaryBuilder.setRegistration(Integer.MIN_VALUE);
//
//				salaryBuilder.setSocialSecurityNumber(liquidation.getNss());
//				salaryBuilder.setCcc(ccc);
//				salaryBuilder.setStartDate(dateFrom);
//				salaryBuilder.setEndDate(dateTo);
//
//				if (liquidation.getCc_base() != null) {
//					salaryBuilder.setCgcBase(liquidation.getCc_base().doubleValue());
//					salaryBuilder.setRawCgcBase(liquidation.getCc_base().doubleValue());
//				} else {
//					salaryBuilder.setCgcBase(0d);
//					salaryBuilder.setRawCgcBase(0d);
//				}
//
//				if (liquidation.getCc_base() != null)
//					salaryBuilder.addData(ContextVariable.CGC_BASE.getName(),
//							new TimedObject<Double>(liquidation.getCc_base().doubleValue(), period));
//
//				if (liquidation.getCc_businessFee() != null) {
//					salaryBuilder.addCost(liquidation.getCc_businessFee().doubleValue(),
//							DeductionType.COMMON_CONTINGENCY.getName(new Locale("es", "ES")), dateFrom, dateTo,
//							new Deduction().setType(DeductionType.COMMON_CONTINGENCY).setName("CGC_E"),
//							Collections.EMPTY_MAP);
//					total_enterprise += liquidation.getCc_businessFee().doubleValue();
//				}
//
//				if (liquidation.getCc_workerFee() != null) {
//					salaryBuilder.addDeduction(liquidation.getCc_workerFee().doubleValue(),
//							DeductionType.COMMON_CONTINGENCY.getName(new Locale("es", "ES")), dateFrom, dateTo,
//							new Deduction().setType(DeductionType.COMMON_CONTINGENCY).setName("CGC"),
//							Collections.EMPTY_MAP);
//					total_deduction += liquidation.getCc_workerFee().doubleValue();
//				}
//
//				// I DON'T KNOW WHAT IS ccLiquid_base
//				if (liquidation.getItWorkAccident_base() != null)
//					salaryBuilder.setCgpBase(liquidation.getItWorkAccident_base().doubleValue());
//				else
//					salaryBuilder.setCgpBase(0d);
//				if (liquidation.getItWorkAccident_base() != null)
//					salaryBuilder.addData(ContextVariable.CGP_BASE.getName(),
//							new TimedObject<Double>(liquidation.getItWorkAccident_base().doubleValue(), period));
//				if (liquidation.getItWorkAccident_businessFee() != null) {
//					salaryBuilder.addCost(liquidation.getItWorkAccident_businessFee().doubleValue(),
//							DeductionType.PROFESSIONAL_CONTINGENCY.getName(new Locale("es", "ES")), dateFrom, dateTo,
//							new Deduction().setType(DeductionType.PROFESSIONAL_CONTINGENCY).setName("CGP_E"),
//							Collections.EMPTY_MAP);
//					total_enterprise += liquidation.getItWorkAccident_businessFee().doubleValue();
//				}
//
//				if (liquidation.getItWorkAccident_workerFee() != null) {
//					salaryBuilder.addDeduction(liquidation.getItWorkAccident_workerFee().doubleValue(),
//							DeductionType.PROFESSIONAL_CONTINGENCY.getName(new Locale("es", "ES")), dateFrom, dateTo,
//							new Deduction().setType(DeductionType.PROFESSIONAL_CONTINGENCY).setName("CGP"),
//							Collections.EMPTY_MAP);
//					total_deduction += liquidation.getItWorkAccident_workerFee().doubleValue();
//				}
//
//				if (liquidation.getImsWorkAccident_businessFee() != null) {
//					salaryBuilder.addCost(liquidation.getImsWorkAccident_businessFee().doubleValue(), "IMS_E", dateFrom,
//							dateTo, new Deduction().setType(DeductionType.PROFESSIONAL_CONTINGENCY).setName("IMS_E"),
//							Collections.EMPTY_MAP);
//					total_enterprise += liquidation.getImsWorkAccident_businessFee().doubleValue();
//				}
//
//				if (liquidation.getUnemployment_base() != null)
//					salaryBuilder.addData(ContextVariable.UNEMPLOY_EMPLOYEE.getName(),
//							new TimedObject<Double>(liquidation.getUnemployment_base().doubleValue(), period));
//				if (liquidation.getUnemployment_businessFee() != null) {
//					salaryBuilder.addCost(liquidation.getUnemployment_businessFee().doubleValue(),
//							DeductionType.UNEMPLOYMENT.getName(new Locale("es", "ES")), dateFrom, dateTo,
//							new Deduction().setType(DeductionType.UNEMPLOYMENT).setName("DESMPL_E"),
//							Collections.EMPTY_MAP);
//					total_enterprise += liquidation.getUnemployment_businessFee().doubleValue();
//				}
//
//				if (liquidation.getUnemployment_workerFee() != null) {
//					salaryBuilder.addDeduction(liquidation.getUnemployment_workerFee().doubleValue(),
//							DeductionType.UNEMPLOYMENT.getName(new Locale("es", "ES")), dateFrom, dateTo,
//							new Deduction().setType(DeductionType.UNEMPLOYMENT).setName("DESMPL"),
//							Collections.EMPTY_MAP);
//					total_deduction += liquidation.getUnemployment_workerFee().doubleValue();
//				}
//
//				if (liquidation.getFogasa_base() != null)
//					salaryBuilder.addData(ContextVariable.FOGASA_ENTERPRISE.getName(),
//							new TimedObject<Double>(liquidation.getFogasa_base().doubleValue(), period));
//				if (liquidation.getFogasa_businessFee() != null) {
//					salaryBuilder.addCost(liquidation.getFogasa_businessFee().doubleValue(),
//							DeductionType.FOGASA.getName(new Locale("es", "ES")), dateFrom, dateTo,
//							new Deduction().setType(DeductionType.FOGASA).setName("FOGASA_E"), Collections.EMPTY_MAP);
//					total_enterprise += liquidation.getFogasa_businessFee().doubleValue();
//				}
//
//				if (liquidation.getFogasa_workerFee() != null) {
//					salaryBuilder.addDeduction(liquidation.getFogasa_workerFee().doubleValue(), "FOGASA", dateFrom,
//							dateTo, new Deduction().setType(DeductionType.FOGASA).setName("FOGASA"),
//							Collections.EMPTY_MAP);
//					total_deduction += liquidation.getFogasa_workerFee().doubleValue();
//				}
//
//				if (liquidation.getJobTraining_base() != null)
//					salaryBuilder.addData(ContextVariable.FP_EMPLOYEE.getName(),
//							new TimedObject<Double>(liquidation.getJobTraining_base().doubleValue(), period));
//				if (liquidation.getJobTraining_businessFee() != null) {
//					salaryBuilder.addCost(liquidation.getJobTraining_businessFee().doubleValue(),
//							DeductionType.JOB_TRAINING.getName(new Locale("es", "ES")), dateFrom, dateTo,
//							new Deduction().setType(DeductionType.JOB_TRAINING).setName("FP_E"), Collections.EMPTY_MAP);
//					total_enterprise += liquidation.getJobTraining_businessFee().doubleValue();
//				}
//
//				if (liquidation.getJobTraining_workerFee() != null) {
//					salaryBuilder.addDeduction(liquidation.getJobTraining_workerFee().doubleValue(),
//							DeductionType.JOB_TRAINING.getName(new Locale("es", "ES")), dateFrom, dateTo,
//							new Deduction().setType(DeductionType.JOB_TRAINING).setName("FP"), Collections.EMPTY_MAP);
//					total_deduction += liquidation.getJobTraining_workerFee().doubleValue();
//				}
//
////				if (liquidation.getGrantsAndBonuses_totalFee() != null) {
////					salaryBuilder.addBonus(liquidation.getGrantsAndBonuses_totalFee().doubleValue()
////						, "BONIFICACIONES"
////						, dateFrom
////						, dateTo
////						, new Bonus().setName("BONIFICACIONES")
////						, Collections.EMPTY_MAP);
////					
////				}
//
//				if (liquidation.getGrantsAndBonuses_businessFee() != null)
//					salaryBuilder.addBonus(liquidation.getGrantsAndBonuses_businessFee().doubleValue(), "BONIF_E",
//							dateFrom, dateTo, new Bonus().setName("BONIF_E"), Collections.EMPTY_MAP);
////					salaryBuilder.addCost((-1)*liquidation.getGrantsAndBonuses_businessFee().doubleValue()
////						, "BONIFICACIONES"
////						, dateFrom
////						, dateTo
////						, new Deduction().setName("BONIFICACIONES_E")
////						, Collections.EMPTY_MAP);
//				if (liquidation.getGrantsAndBonuses_workerFee() != null)
//					salaryBuilder.addBonus(liquidation.getGrantsAndBonuses_workerFee().doubleValue(), "BONIF", dateFrom,
//							dateTo, new Bonus().setName("BONIF"), Collections.EMPTY_MAP);
////					salaryBuilder.addDeduction((-1)*liquidation.getGrantsAndBonuses_workerFee().doubleValue()
////						, "BONIF"
////						, dateFrom
////						, dateTo
////						, new Deduction().setName("BONIF")
////						, Collections.EMPTY_MAP);
//
//				if (liquidation.getOtherContributionsLiquid_base() != null)
//					salaryBuilder.addData("OTHER_BASE", new TimedObject<Double>(
//							liquidation.getOtherContributionsLiquid_base().doubleValue(), period));
//				if (liquidation.getOtherContributionsLiquid_businessFee() != null) {
//					salaryBuilder.addCost(liquidation.getOtherContributionsLiquid_businessFee().doubleValue(),
//							DeductionType.OTHER.getName(new Locale("es", "ES")), dateFrom, dateTo,
//							new Deduction().setType(DeductionType.OTHER).setName("OTHER_E"), Collections.EMPTY_MAP);
//					total_enterprise += liquidation.getOtherContributionsLiquid_businessFee().doubleValue();
//				}
//
//				if (liquidation.getOtherContributionsLiquid_workerFee() != null) {
//					salaryBuilder.addDeduction(liquidation.getOtherContributionsLiquid_workerFee().doubleValue(),
//							DeductionType.OTHER.getName(new Locale("es", "ES")), dateFrom, dateTo,
//							new Deduction().setType(DeductionType.OTHER).setName("OTHER"), Collections.EMPTY_MAP);
//					total_deduction += liquidation.getOtherContributionsLiquid_workerFee().doubleValue();
//				}
//
//				if (liquidation.getTotalLiquid_totalFee() != null)
//					salaryBuilder.setTotalLiquid(liquidation.getTotalLiquid_totalFee().doubleValue());
//				else
//					salaryBuilder.setTotalLiquid(0d);
//
//				Record record = ctx.select().from(CONTRACT).innerJoin(ENTERPRISE_CCC).onKey().innerJoin(PERSON).onKey()
//						.innerJoin(REGISTRY).onKey().where(
//								ENTERPRISE_CCC.CCC.eq(ccc).and(PERSON.SOCIAL_SECURITY_NUM.eq(liquidation.getNss()))
//										.and(CONTRACT.START_DATE.le(new java.sql.Date(dateFrom.getTime())))
//										.and(CONTRACT.END_DATE.isNull()
//												.or(CONTRACT.END_DATE.ge(new java.sql.Date(dateTo.getTime())))))
//						.fetchOne();
//
//				if (record != null) {
//					RegistryRecord enterpriseRecord = ctx.select().from(ENTERPRISE_CCC).innerJoin(ENTERPRISE)
//							.on(ENTERPRISE.DOMAIN.eq(ENTERPRISE_CCC.DOMAIN)).innerJoin(REGISTRY).onKey()
//							.where(ENTERPRISE_CCC.CCC.eq(ccc)).fetchOneInto(REGISTRY);
//
//					System.out.println(ctx.select().from(CONTRACT).innerJoin(ENTERPRISE_CCC).onKey().innerJoin(PERSON)
//							.onKey().innerJoin(REGISTRY).onKey().where(
//									ENTERPRISE_CCC.CCC.eq(ccc).and(PERSON.SOCIAL_SECURITY_NUM.eq(liquidation.getNss()))
//											.and(CONTRACT.START_DATE.le(new java.sql.Date(dateFrom.getTime())))
//											.and(CONTRACT.END_DATE.isNull()
//													.or(CONTRACT.END_DATE.ge(new java.sql.Date(dateTo.getTime())))))
//							.getSQL());
//
//					salaryBuilder.setContract(new PDFContract().setCcc(ccc)
//							.setNaf(record.get(PERSON.SOCIAL_SECURITY_NUM)).setNif(record.get(REGISTRY.DOCUMENT))
//							.setCif(enterpriseRecord.getDocument()).setEndDate(dateTo).setStartDate(dateFrom)
//							.setEmployeeName(record.get(REGISTRY.NAME)).setEnterpriseName(enterpriseRecord.getName()));
//
//					salaryBuilder.setRegime("" + record.get(CONTRACT.SS_REGIME));
//
//					Long diff = Duration.between(dateFrom.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),
//							dateTo.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()).toDays();
//					salaryBuilder.setTimeUnits(diff.intValue());
//
//					salaryBuilder.setTotalPayment(0d);
//					salaryBuilder.setTotalDeduction(total_deduction);
//					salaryBuilder.setTotalEnterprise(total_enterprise);
//					salaryBuilder.setIssueDate(dateTo);
//					salaryBuilder.setRemuneration(0d);
//					salaryBuilder.setProExtBase(0d);
//					salaryBuilder.setIrpfBase(0d);
//					salaryBuilder.setChargeDate(dateTo);
//
//					salaryBuilder.getSalary();
//				}
//
//			}
//
//		}

	}

//	public static <T extends ISalary> void getSLDCostsByNAFs(ISalaryBuilder<T> salaryBuilder, DSLContext ctx,
//			final InputStream certificateInputStream, final String certificatePassword, final String certificateType,
//			final String ccc, final Regime regime, final Date dateFrom, final Date dateTo, String... nafs)
//			throws SegSocialException {
//		Map<String, Map<String, WorkerLiquidation>> map = SistemaRED.getWorkersLiquidationsByCCCandNAFs(
//				certificateInputStream, certificatePassword, certificateType, ccc, regime, dateFrom, dateTo,
//				LiquidationType.TODAS, LiquidationOrigin.TODAS, nafs);
//
//		Period period = new Period(dateFrom, dateTo);
//
//		Iterator<String> it = map.keySet().iterator();
//
//		while (it.hasNext()) {
//			String key = it.next();
//			Map<String, WorkerLiquidation> innerMap = map.get(key);
//			Iterator<String> it2 = innerMap.keySet().iterator();
//			while (it2.hasNext()) {
//				String naf = it2.next();
//				WorkerLiquidation liquidation = innerMap.get(naf);
//
//				Record record = ctx.select().from(CONTRACT).innerJoin(ENTERPRISE_CCC).onKey().innerJoin(PERSON).onKey()
//						.innerJoin(REGISTRY).onKey().where(
//								ENTERPRISE_CCC.CCC.eq(ccc).and(PERSON.SOCIAL_SECURITY_NUM.eq(liquidation.getNss()))
//										.and(CONTRACT.START_DATE.le(new java.sql.Date(dateFrom.getTime())))
//										.and(CONTRACT.END_DATE.isNull()
//												.or(CONTRACT.END_DATE.ge(new java.sql.Date(dateTo.getTime())))))
//						.fetchOne();
//
//				if (record != null) {
//
//					double total_deduction = 0;
//					double total_enterprise = 0;
//
//					salaryBuilder.createNewSalary();
//					
//					switch (key) {
//					case "L93":
//						salaryBuilder.setType(SalaryType.NOT_ENJOYED_VACATIONS);
//						break;
//					default:
//						salaryBuilder.setType(SalaryType.SALARY);
//					}
//					
//					
//					salaryBuilder.setRegistration(Integer.MIN_VALUE);
//
//					salaryBuilder.setSocialSecurityNumber(liquidation.getNss());
//					salaryBuilder.setCcc(ccc);
//					salaryBuilder.setStartDate(dateFrom);
//					salaryBuilder.setEndDate(dateTo);
//
//					RegistryRecord enterpriseRecord = ctx.select().from(ENTERPRISE_CCC).innerJoin(ENTERPRISE)
//							.on(ENTERPRISE.DOMAIN.eq(ENTERPRISE_CCC.DOMAIN)).innerJoin(REGISTRY).onKey()
//							.where(ENTERPRISE_CCC.CCC.eq(ccc)).fetchAnyInto(REGISTRY);
//
//					salaryBuilder.setContract(new PDFContract().setCcc(ccc)
//							.setNaf(record.get(PERSON.SOCIAL_SECURITY_NUM)).setNif(record.get(REGISTRY.DOCUMENT))
//							.setCif(enterpriseRecord.getDocument()).setEndDate(dateTo).setStartDate(dateFrom)
//							.setEmployeeName(record.get(REGISTRY.NAME)).setEnterpriseName(enterpriseRecord.getName()));
//
//					salaryBuilder.setRegime("" + record.get(CONTRACT.SS_REGIME));
//
//					Date difference = new Date(dateTo.getTime() - dateFrom.getTime());
//
//					Calendar calDiff = Calendar.getInstance();
//					calDiff.setTime(difference);
//
////					Long longTimeUnits = (dateTo.getTime() - dateFrom.getTime()) / 1000 / 60 / 60 / 24;
//
//					salaryBuilder.setEmployeeName(record.get(REGISTRY.NAME));
//					salaryBuilder.setEnterpriseName(enterpriseRecord.getName());
//					salaryBuilder.setEnterpriseDocument(enterpriseRecord.getDocument());
//					salaryBuilder.setSeniorityDate(record.get(CONTRACT.SENIORITY_DATE));
//					salaryBuilder.setEmployeeDocument(record.get(REGISTRY.DOCUMENT));
//					salaryBuilder.setTimeUnits(calDiff.get(Calendar.DAY_OF_YEAR));
//					salaryBuilder.setTotalPayment(0d);
//					salaryBuilder.setTotalDeduction(total_deduction);
//					salaryBuilder.setTotalEnterprise(total_enterprise);
//					salaryBuilder.setIssueDate(dateTo);
//					salaryBuilder.setRemuneration(0d);
//					salaryBuilder.setProExtBase(0d);
//					salaryBuilder.setIrpfBase(0d);
//					salaryBuilder.setChargeDate(dateTo);
//
//					if (liquidation.getCc_base() != null) {
//						salaryBuilder.setCgcBase(liquidation.getCc_base().doubleValue());
//						salaryBuilder.setRawCgcBase(liquidation.getCc_base().doubleValue());
//					} else {
//						salaryBuilder.setCgcBase(0d);
//						salaryBuilder.setRawCgcBase(0d);
//					}
//
//					if (liquidation.getCc_base() != null)
//						salaryBuilder.addData(ContextVariable.CGC_BASE.getName(),
//								new TimedObject<Double>(liquidation.getCc_base().doubleValue(), period));
//
//					if (liquidation.getCc_businessFee() != null) {
//						salaryBuilder.addCost(liquidation.getCc_businessFee().doubleValue(),
//								DeductionType.COMMON_CONTINGENCY.getName(new Locale("es", "ES")), dateFrom, dateTo,
//								new Deduction().setType(DeductionType.COMMON_CONTINGENCY).setName("CGC_E"),
//								Collections.EMPTY_MAP);
//						total_enterprise += liquidation.getCc_businessFee().doubleValue();
//					}
//
//					if (liquidation.getCc_workerFee() != null) {
//						salaryBuilder.addDeduction(liquidation.getCc_workerFee().doubleValue(),
//								DeductionType.COMMON_CONTINGENCY.getName(new Locale("es", "ES")), dateFrom, dateTo,
//								new Deduction().setType(DeductionType.COMMON_CONTINGENCY).setName("CGC"),
//								Collections.EMPTY_MAP);
//						total_deduction += liquidation.getCc_workerFee().doubleValue();
//					}
//
//					// I DON'T KNOW WHAT IS ccLiquid_base
//					if (liquidation.getItWorkAccident_base() != null)
//						salaryBuilder.setCgpBase(liquidation.getItWorkAccident_base().doubleValue());
//					else
//						salaryBuilder.setCgpBase(0d);
//					if (liquidation.getItWorkAccident_base() != null)
//						salaryBuilder.addData(ContextVariable.CGP_BASE.getName(),
//								new TimedObject<Double>(liquidation.getItWorkAccident_base().doubleValue(), period));
//					if (liquidation.getItWorkAccident_businessFee() != null) {
//						salaryBuilder.addCost(liquidation.getItWorkAccident_businessFee().doubleValue(),
//								DeductionType.PROFESSIONAL_CONTINGENCY.getName(new Locale("es", "ES")), dateFrom,
//								dateTo,
//								new Deduction().setType(DeductionType.PROFESSIONAL_CONTINGENCY).setName("CGP_E"),
//								Collections.EMPTY_MAP);
//						total_enterprise += liquidation.getItWorkAccident_businessFee().doubleValue();
//					}
//
//					if (liquidation.getItWorkAccident_workerFee() != null) {
//						salaryBuilder.addDeduction(liquidation.getItWorkAccident_workerFee().doubleValue(),
//								DeductionType.PROFESSIONAL_CONTINGENCY.getName(new Locale("es", "ES")), dateFrom,
//								dateTo, new Deduction().setType(DeductionType.PROFESSIONAL_CONTINGENCY).setName("CGP"),
//								Collections.EMPTY_MAP);
//						total_deduction += liquidation.getItWorkAccident_workerFee().doubleValue();
//					}
//
//					if (liquidation.getImsWorkAccident_businessFee() != null) {
//						salaryBuilder.addCost(liquidation.getImsWorkAccident_businessFee().doubleValue(), "IMS_E",
//								dateFrom, dateTo,
//								new Deduction().setType(DeductionType.PROFESSIONAL_CONTINGENCY).setName("IMS_E"),
//								Collections.EMPTY_MAP);
//						total_enterprise += liquidation.getImsWorkAccident_businessFee().doubleValue();
//					}
//
//					if (liquidation.getUnemployment_base() != null)
//						salaryBuilder.addData(ContextVariable.UNEMPLOY_EMPLOYEE.getName(),
//								new TimedObject<Double>(liquidation.getUnemployment_base().doubleValue(), period));
//					if (liquidation.getUnemployment_businessFee() != null) {
//						salaryBuilder.addCost(liquidation.getUnemployment_businessFee().doubleValue(),
//								DeductionType.UNEMPLOYMENT.getName(new Locale("es", "ES")), dateFrom, dateTo,
//								new Deduction().setType(DeductionType.UNEMPLOYMENT).setName("DESMPL_E"),
//								Collections.EMPTY_MAP);
//						total_enterprise += liquidation.getUnemployment_businessFee().doubleValue();
//					}
//
//					if (liquidation.getUnemployment_workerFee() != null) {
//						salaryBuilder.addDeduction(liquidation.getUnemployment_workerFee().doubleValue(),
//								DeductionType.UNEMPLOYMENT.getName(new Locale("es", "ES")), dateFrom, dateTo,
//								new Deduction().setType(DeductionType.UNEMPLOYMENT).setName("DESMPL"),
//								Collections.EMPTY_MAP);
//						total_deduction += liquidation.getUnemployment_workerFee().doubleValue();
//					}
//
//					if (liquidation.getFogasa_base() != null)
//						salaryBuilder.addData(ContextVariable.FOGASA_ENTERPRISE.getName(),
//								new TimedObject<Double>(liquidation.getFogasa_base().doubleValue(), period));
//					if (liquidation.getFogasa_businessFee() != null) {
//						salaryBuilder.addCost(liquidation.getFogasa_businessFee().doubleValue(),
//								DeductionType.FOGASA.getName(new Locale("es", "ES")), dateFrom, dateTo,
//								new Deduction().setType(DeductionType.FOGASA).setName("FOGASA_E"),
//								Collections.EMPTY_MAP);
//						total_enterprise += liquidation.getFogasa_businessFee().doubleValue();
//					}
//
//					if (liquidation.getFogasa_workerFee() != null) {
//						salaryBuilder.addDeduction(liquidation.getFogasa_workerFee().doubleValue(), "FOGASA", dateFrom,
//								dateTo, new Deduction().setType(DeductionType.FOGASA).setName("FOGASA"),
//								Collections.EMPTY_MAP);
//						total_deduction += liquidation.getFogasa_workerFee().doubleValue();
//					}
//
//					if (liquidation.getJobTraining_base() != null)
//						salaryBuilder.addData(ContextVariable.FP_EMPLOYEE.getName(),
//								new TimedObject<Double>(liquidation.getJobTraining_base().doubleValue(), period));
//					if (liquidation.getJobTraining_businessFee() != null) {
//						salaryBuilder.addCost(liquidation.getJobTraining_businessFee().doubleValue(),
//								DeductionType.JOB_TRAINING.getName(new Locale("es", "ES")), dateFrom, dateTo,
//								new Deduction().setType(DeductionType.JOB_TRAINING).setName("FP_E"),
//								Collections.EMPTY_MAP);
//						total_enterprise += liquidation.getJobTraining_businessFee().doubleValue();
//					}
//
//					if (liquidation.getJobTraining_workerFee() != null) {
//						salaryBuilder.addDeduction(liquidation.getJobTraining_workerFee().doubleValue(),
//								DeductionType.JOB_TRAINING.getName(new Locale("es", "ES")), dateFrom, dateTo,
//								new Deduction().setType(DeductionType.JOB_TRAINING).setName("FP"),
//								Collections.EMPTY_MAP);
//						total_deduction += liquidation.getJobTraining_workerFee().doubleValue();
//					}
//
////					if (liquidation.getGrantsAndBonuses_totalFee() != null) {
////						salaryBuilder.addBonus(liquidation.getGrantsAndBonuses_totalFee().doubleValue()
////							, "BONIFICACIONES"
////							, dateFrom
////							, dateTo
////							, new Bonus().setName("BONIFICACIONES")
////							, Collections.EMPTY_MAP);
////						
////					}
//
//					if (liquidation.getGrantsAndBonuses_businessFee() != null)
//						salaryBuilder.addBonus(liquidation.getGrantsAndBonuses_businessFee().doubleValue(), "BONIF_E",
//								dateFrom, dateTo, new Bonus().setName("BONIF_E"), Collections.EMPTY_MAP);
////						salaryBuilder.addCost((-1)*liquidation.getGrantsAndBonuses_businessFee().doubleValue()
////							, "BONIFICACIONES"
////							, dateFrom
////							, dateTo
////							, new Deduction().setName("BONIFICACIONES_E")
////							, Collections.EMPTY_MAP);
//					if (liquidation.getGrantsAndBonuses_workerFee() != null)
//						salaryBuilder.addBonus(liquidation.getGrantsAndBonuses_workerFee().doubleValue(), "BONIF",
//								dateFrom, dateTo, new Bonus().setName("BONIF"), Collections.EMPTY_MAP);
////						salaryBuilder.addDeduction((-1)*liquidation.getGrantsAndBonuses_workerFee().doubleValue()
////							, "BONIF"
////							, dateFrom
////							, dateTo
////							, new Deduction().setName("BONIF")
////							, Collections.EMPTY_MAP);
//
//					if (liquidation.getOtherContributionsLiquid_base() != null)
//						salaryBuilder.addData("OTHER_BASE", new TimedObject<Double>(
//								liquidation.getOtherContributionsLiquid_base().doubleValue(), period));
//					if (liquidation.getOtherContributionsLiquid_businessFee() != null) {
//						salaryBuilder.addCost(liquidation.getOtherContributionsLiquid_businessFee().doubleValue(),
//								DeductionType.OTHER.getName(new Locale("es", "ES")), dateFrom, dateTo,
//								new Deduction().setType(DeductionType.OTHER).setName("OTHER_E"), Collections.EMPTY_MAP);
//						total_enterprise += liquidation.getOtherContributionsLiquid_businessFee().doubleValue();
//					}
//
//					if (liquidation.getOtherContributionsLiquid_workerFee() != null) {
//						salaryBuilder.addDeduction(liquidation.getOtherContributionsLiquid_workerFee().doubleValue(),
//								DeductionType.OTHER.getName(new Locale("es", "ES")), dateFrom, dateTo,
//								new Deduction().setType(DeductionType.OTHER).setName("OTHER"), Collections.EMPTY_MAP);
//						total_deduction += liquidation.getOtherContributionsLiquid_workerFee().doubleValue();
//					}
//
//					if (liquidation.getTotalLiquid_totalFee() != null)
//						salaryBuilder.setTotalLiquid(liquidation.getTotalLiquid_totalFee().doubleValue());
//					else
//						salaryBuilder.setTotalLiquid(0d);
//
//					salaryBuilder.getSalary();
//
//				}
//
//			}
//
//		}
//
//	}
	
	public static <T extends ISalary> void getSLDCostsByNAFs(ISalaryBuilder<T> salaryBuilder, DSLContext ctx,
			final InputStream certificateInputStream, final String certificatePassword, final String certificateType,
			final String ccc, final Regime regime, final Date dateFrom, final Date dateTo, String... nafs)
			throws SegSocialException {
		Map<String, Map<String, WorkerLiquidation>> map = SistemaRED.getWorkersLiquidationsByCCCandNAFs(
				certificateInputStream, certificatePassword, certificateType, ccc, regime, dateFrom, dateTo,
				LiquidationType.TODAS, LiquidationOrigin.TODAS, nafs);

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
				
				System.out.println(
						ctx.select().from(CONTRACT).innerJoin(ENTERPRISE_CCC).onKey().innerJoin(PERSON).onKey()
						.innerJoin(r1).on(r1.ID.eq(PERSON.REGISTRY))
						.innerJoin(ENTERPRISE).on(ENTERPRISE.DOMAIN.eq(ENTERPRISE_CCC.DOMAIN))
						.innerJoin(r2).on(r2.ID.eq(ENTERPRISE.REGISTRY))
						.where(
								ENTERPRISE_CCC.CCC.eq(ccc).and(PERSON.SOCIAL_SECURITY_NUM.eq(liquidation.getNss()))
										.and(CONTRACT.START_DATE.le(new java.sql.Date(dateFrom.getTime())))
										.and(CONTRACT.END_DATE.isNull()
												.or(CONTRACT.END_DATE.ge(new java.sql.Date(dateTo.getTime()))))
								).getSQL()
						);

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

//					Long longTimeUnits = (dateTo.getTime() - dateFrom.getTime()) / 1000 / 60 / 60 / 24;

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

					if (liquidation.getCc_base() != null) {
						salaryBuilder.setCgcBase(liquidation.getCc_base().doubleValue());
						salaryBuilder.setRawCgcBase(liquidation.getCc_base().doubleValue());
					} else {
						salaryBuilder.setCgcBase(0d);
						salaryBuilder.setRawCgcBase(0d);
					}

					if (liquidation.getCc_base() != null)
						salaryBuilder.addData(ContextVariable.CGC_BASE.getName(),
								new TimedObject<Double>(roundNumber(liquidation.getCc_base().doubleValue(), 2), period));

					if (liquidation.getCc_businessFee() != null) {
						salaryBuilder.addCost(liquidation.getCc_businessFee().doubleValue(),
								DeductionType.COMMON_CONTINGENCY.getName(new Locale("es", "ES")), dateFrom, dateTo,
								new Deduction().setType(DeductionType.COMMON_CONTINGENCY).setName("CGC_E"),
								Collections.EMPTY_MAP);
					}

					if (liquidation.getCc_workerFee() != null) {
						salaryBuilder.addDeduction(liquidation.getCc_workerFee().doubleValue(),
								DeductionType.COMMON_CONTINGENCY.getName(new Locale("es", "ES")), dateFrom, dateTo,
								new Deduction().setType(DeductionType.COMMON_CONTINGENCY).setName("CGC"),
								Collections.EMPTY_MAP);
					}

					// I DON'T KNOW WHAT IS ccLiquid_base
					if (liquidation.getItWorkAccident_base() != null)
						salaryBuilder.setCgpBase(liquidation.getItWorkAccident_base().doubleValue());
					else
						salaryBuilder.setCgpBase(0d);
					if (liquidation.getItWorkAccident_base() != null)
						salaryBuilder.addData(ContextVariable.CGP_BASE.getName(),
								new TimedObject<Double>(roundNumber(liquidation.getItWorkAccident_base().doubleValue(), 2), period));
					if (liquidation.getItWorkAccident_businessFee() != null) {
						salaryBuilder.addCost(liquidation.getItWorkAccident_businessFee().doubleValue(),
								DeductionType.PROFESSIONAL_CONTINGENCY.getName(new Locale("es", "ES")), dateFrom,
								dateTo,
								new Deduction().setType(DeductionType.PROFESSIONAL_CONTINGENCY).setName("CGP_E"),
								Collections.EMPTY_MAP);
					}

					if (liquidation.getItWorkAccident_workerFee() != null) {
						salaryBuilder.addDeduction(liquidation.getItWorkAccident_workerFee().doubleValue(),
								DeductionType.PROFESSIONAL_CONTINGENCY.getName(new Locale("es", "ES")), dateFrom,
								dateTo, new Deduction().setType(DeductionType.PROFESSIONAL_CONTINGENCY).setName("CGP"),
								Collections.EMPTY_MAP);
					}

					if (liquidation.getImsWorkAccident_businessFee() != null) {
						salaryBuilder.addCost(liquidation.getImsWorkAccident_businessFee().doubleValue(), "IMS_E",
								dateFrom, dateTo,
								new Deduction().setType(DeductionType.PROFESSIONAL_CONTINGENCY).setName("IMS_E"),
								Collections.EMPTY_MAP);
					}

					if (liquidation.getUnemployment_base() != null)
						salaryBuilder.addData(ContextVariable.UNEMPLOY_EMPLOYEE.getName(),
								new TimedObject<Double>(roundNumber(liquidation.getUnemployment_base().doubleValue(), 2), period));
					if (liquidation.getUnemployment_businessFee() != null) {
						salaryBuilder.addCost(liquidation.getUnemployment_businessFee().doubleValue(),
								DeductionType.UNEMPLOYMENT.getName(new Locale("es", "ES")), dateFrom, dateTo,
								new Deduction().setType(DeductionType.UNEMPLOYMENT).setName("DESMPL_E"),
								Collections.EMPTY_MAP);
					}

					if (liquidation.getUnemployment_workerFee() != null) {
						salaryBuilder.addDeduction(liquidation.getUnemployment_workerFee().doubleValue(),
								DeductionType.UNEMPLOYMENT.getName(new Locale("es", "ES")), dateFrom, dateTo,
								new Deduction().setType(DeductionType.UNEMPLOYMENT).setName("DESMPL"),
								Collections.EMPTY_MAP);
					}

					if (liquidation.getFogasa_base() != null)
						salaryBuilder.addData(ContextVariable.FOGASA_ENTERPRISE.getName(),
								new TimedObject<Double>(roundNumber(liquidation.getFogasa_base().doubleValue(), 2), period));
					if (liquidation.getFogasa_businessFee() != null) {
						salaryBuilder.addCost(liquidation.getFogasa_businessFee().doubleValue(),
								DeductionType.FOGASA.getName(new Locale("es", "ES")), dateFrom, dateTo,
								new Deduction().setType(DeductionType.FOGASA).setName("FOGASA_E"),
								Collections.EMPTY_MAP);
					}

					if (liquidation.getFogasa_workerFee() != null) {
						salaryBuilder.addDeduction(liquidation.getFogasa_workerFee().doubleValue(), "FOGASA", dateFrom,
								dateTo, new Deduction().setType(DeductionType.FOGASA).setName("FOGASA"),
								Collections.EMPTY_MAP);
					}

					if (liquidation.getJobTraining_base() != null)
						salaryBuilder.addData(ContextVariable.FP_EMPLOYEE.getName(),
								new TimedObject<Double>(roundNumber(liquidation.getJobTraining_base().doubleValue(), 2), period));
					if (liquidation.getJobTraining_businessFee() != null) {
						salaryBuilder.addCost(liquidation.getJobTraining_businessFee().doubleValue(),
								DeductionType.JOB_TRAINING.getName(new Locale("es", "ES")), dateFrom, dateTo,
								new Deduction().setType(DeductionType.JOB_TRAINING).setName("FP_E"),
								Collections.EMPTY_MAP);
					}

					if (liquidation.getJobTraining_workerFee() != null) {
						salaryBuilder.addDeduction(liquidation.getJobTraining_workerFee().doubleValue(),
								DeductionType.JOB_TRAINING.getName(new Locale("es", "ES")), dateFrom, dateTo,
								new Deduction().setType(DeductionType.JOB_TRAINING).setName("FP"),
								Collections.EMPTY_MAP);
					}

//					if (liquidation.getGrantsAndBonuses_totalFee() != null) {
//						salaryBuilder.addBonus(liquidation.getGrantsAndBonuses_totalFee().doubleValue()
//							, "BONIFICACIONES"
//							, dateFrom
//							, dateTo
//							, new Bonus().setName("BONIFICACIONES")
//							, Collections.EMPTY_MAP);
//						
//					}

					if (liquidation.getGrantsAndBonuses_businessFee() != null)
						salaryBuilder.addBonus(liquidation.getGrantsAndBonuses_businessFee().doubleValue(), "BONIF_E",
								dateFrom, dateTo, new Bonus().setName("BONIF_E"), Collections.EMPTY_MAP);
//						salaryBuilder.addCost((-1)*liquidation.getGrantsAndBonuses_businessFee().doubleValue()
//							, "BONIFICACIONES"
//							, dateFrom
//							, dateTo
//							, new Deduction().setName("BONIFICACIONES_E")
//							, Collections.EMPTY_MAP);
					if (liquidation.getGrantsAndBonuses_workerFee() != null)
						salaryBuilder.addBonus(liquidation.getGrantsAndBonuses_workerFee().doubleValue(), "BONIF",
								dateFrom, dateTo, new Bonus().setName("BONIF"), Collections.EMPTY_MAP);
					
//						salaryBuilder.addDeduction((-1)*liquidation.getGrantsAndBonuses_workerFee().doubleValue()
//							, "BONIF"
//							, dateFrom
//							, dateTo
//							, new Deduction().setName("BONIF")
//							, Collections.EMPTY_MAP);

//					if (liquidation.getOtherContributionsLiquid_base() != null)
//						salaryBuilder.addData("OTHER_BASE", new TimedObject<Double>(
//								liquidation.getOtherContributionsLiquid_base().doubleValue(), period));
//					if (liquidation.getOtherContributionsLiquid_businessFee() != null) {
//						salaryBuilder.addCost(liquidation.getOtherContributionsLiquid_businessFee().doubleValue(),
//								DeductionType.OTHER.getName(new Locale("es", "ES")), dateFrom, dateTo,
//								new Deduction().setType(DeductionType.OTHER).setName("OTHER_E"), Collections.EMPTY_MAP);
//						total_enterprise += liquidation.getOtherContributionsLiquid_businessFee().doubleValue();
//					}
//
//					if (liquidation.getOtherContributionsLiquid_workerFee() != null) {
//						salaryBuilder.addDeduction(liquidation.getOtherContributionsLiquid_workerFee().doubleValue(),
//								DeductionType.OTHER.getName(new Locale("es", "ES")), dateFrom, dateTo,
//								new Deduction().setType(DeductionType.OTHER).setName("OTHER"), Collections.EMPTY_MAP);
//						total_deduction += liquidation.getOtherContributionsLiquid_workerFee().doubleValue();
//					}
					
					if (liquidation.getTotalLiquid_businessFee() != null)
						salaryBuilder.setTotalEnterprise(liquidation.getTotalLiquid_businessFee().doubleValue());
					else
						salaryBuilder.setTotalEnterprise(0d);
					
					if (liquidation.getTotalLiquid_workerFee() != null)
						salaryBuilder.setTotalDeduction(liquidation.getTotalLiquid_workerFee().doubleValue());
					else
						salaryBuilder.setTotalDeduction(0d);
					
					if (liquidation.getTotalLiquid_totalFee() != null)
						salaryBuilder.setTotalSS(liquidation.getTotalLiquid_totalFee().doubleValue());
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
