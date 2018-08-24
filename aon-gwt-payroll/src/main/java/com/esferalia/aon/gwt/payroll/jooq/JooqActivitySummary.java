package com.esferalia.aon.gwt.payroll.jooq;

import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.ContractLeave.CONTRACT_LEAVE;
import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.Person.PERSON;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.Salary.SALARY;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.jooq.Condition;
import org.jooq.Record4;
import org.jooq.Record5;
import org.jooq.Record6;
import org.jooq.Result;
import org.jooq.impl.DSL;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.Domain;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.gwt.common.server.AonServletUtils;
import com.esferalia.aon.gwt.payroll.shared.ActivitySummaryObject;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.payroll.enumeration.LeaveType;
import com.esferalia.aon.salary.enumeration.SalaryType;

public class JooqActivitySummary {

	public static List<ActivitySummaryObject> getActivitySummary(
			String domainName, boolean parentDomain, Integer domainId,
			Date startDate, Date endDate, Boolean starts, Boolean ends,
			Boolean salary, Boolean salaryExtra, Boolean salarySettle,
			Boolean salaryOther, Boolean itCommonDisease,
			Boolean itOccupationalDisease, Boolean itMaternity, Boolean itOther) {

		if (domainName != null && domainId != null && startDate != null
				&& endDate != null) {
			AONContext ctx = null;
			try {
				ctx = AONContext.getAONContext(domainName, domainId,
						AonServletUtils.getLoggedUser());

				Map<Integer, ActivitySummaryObject> summaryMap = null;
				Map<Integer, ActivitySummaryObject> salaryMap = null;
				Map<Integer, ActivitySummaryObject> itMap = null;
				if (parentDomain) {
					try {
						Integer[] childDomains = getChildDomainIDs(domainId);
						summaryMap = getSummaryEnterprise(childDomains,
								domainId, domainName, startDate, endDate,
								starts, ends);
						salaryMap = getSummaryEnterpriseSalary(childDomains,
								domainId, domainName, startDate, endDate,
								salary, salaryExtra, salarySettle, salaryOther);
						itMap = getSummaryEnterpriseIT(childDomains, domainId,
								domainName, startDate, endDate,
								itCommonDisease, itOccupationalDisease,
								itMaternity, itOther);
					} catch (ManagerBeanException e) {
						throw new RuntimeException(e.getMessage());
					}
				} else {
					summaryMap = getSummaryEmployee(domainId, domainName,
							startDate, endDate, starts, ends);
					salaryMap = getSummaryEmployeeSalary(domainId, domainName,
							startDate, endDate, salary, salaryExtra,
							salarySettle, salaryOther);
					itMap = getSummaryEmployeeIT(domainId, domainName,
							startDate, endDate, itCommonDisease,
							itOccupationalDisease, itMaternity, itOther);
				}
				fillMapData(summaryMap, salaryMap);
				fillMapData(summaryMap, itMap);

				return new ArrayList<>(summaryMap
						.values()
						.stream()
						.sorted((o1, o2) -> o1.getFullname().compareTo(
								o2.getFullname())).collect(Collectors.toList()));
			} finally {
				if (ctx != null)
					ctx.close();
			}
		}
		return new ArrayList<>();
	}

	private static Integer[] getChildDomainIDs(Integer domainId)
			throws ManagerBeanException {
		IManagerBean beanManager = BeanManager.getManagerBean(Domain.class);

		Criteria criteria = new Criteria();
		criteria.addEqualExpression(
				beanManager.getFieldName(IEntityAlias.DOMAIN_PARENT_ID),
				domainId);

		List<ITransferObject> tos = beanManager.getList(criteria);

		if (tos == null || tos.isEmpty())
			return new Integer[] {};

		Integer[] ids = new Integer[tos.size()];
		for (int i = 0; i < ids.length; i++)
			ids[i] = ((Domain) tos.get(i)).getId();

		return ids;
	}

	private static void fillMapData(
			Map<Integer, ActivitySummaryObject> summaryMap,
			Map<Integer, ActivitySummaryObject> dataMap) {
		dataMap.keySet()
				.stream()
				.forEach(
						key -> {
							ActivitySummaryObject obj = summaryMap.get(key);
							if (obj != null) {
								if (dataMap.get(key).getSalaryCount() != null) {
									obj.setSalaryCount(dataMap.get(key)
											.getSalaryCount());
								}
								if (dataMap.get(key).getSalaryExtraCount() != null) {
									obj.setSalaryExtraCount(dataMap.get(key)
											.getSalaryExtraCount());
								}
								if (dataMap.get(key).getSalarySettleCount() != null) {
									obj.setSalarySettleCount(dataMap.get(key)
											.getSalarySettleCount());
								}
								if (dataMap.get(key).getSalaryOtherCount() != null) {
									obj.setSalaryOtherCount(dataMap.get(key)
											.getSalaryOtherCount());
								}
								if (dataMap.get(key).getItCommonDiseaseCount() != null) {
									obj.setItCommonDiseaseCount(dataMap
											.get(key).getItCommonDiseaseCount());
								}
								if (dataMap.get(key)
										.getItOccupationalDiseaseCount() != null) {
									obj.setItOccupationalDiseaseCount(dataMap
											.get(key)
											.getItOccupationalDiseaseCount());
								}
								if (dataMap.get(key).getItMaternityCount() != null) {
									obj.setItMaternityCount(dataMap.get(key)
											.getItMaternityCount());
								}
								if (dataMap.get(key).getItOtherCount() != null) {
									obj.setItOtherCount(dataMap.get(key)
											.getItOtherCount());
								}
							}
						});
	}

	private static Map<Integer, ActivitySummaryObject> getSummaryEmployee(
			Integer domainId, String domainName, Date startDate, Date endDate,
			Boolean starts, Boolean ends) {

		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId,
					AonServletUtils.getLoggedUser());
			Result<Record6<String, String, String, java.sql.Date, java.sql.Date, Integer>> result = ctx
					.getDslContext()
					.select(PERSON.NAME, PERSON.FIRST_SURNAME,
							PERSON.SECOND_SURNAME, CONTRACT.START_DATE,
							CONTRACT.END_DATE, CONTRACT.ID)
					.from(CONTRACT.leftOuterJoin(PERSON)
							.on(CONTRACT.PERSON.eq(PERSON.REGISTRY))
							.leftOuterJoin(REGISTRY)
							.on(REGISTRY.ID.eq(PERSON.REGISTRY)))
					.where(CONTRACT.DOMAIN.eq(domainId))
					.and(getStartCondition(startDate, endDate, starts))
					.and(getEndCondition(startDate, endDate, ends))
					.groupBy(CONTRACT.ID)
					.orderBy(PERSON.FIRST_SURNAME.asc(),
							PERSON.SECOND_SURNAME.asc(), PERSON.NAME.asc(),
							CONTRACT.START_DATE.desc()).fetch();

			Map<Integer, ActivitySummaryObject> map = new HashMap<>();
			result.stream()
					.forEach(
							record -> {
								ActivitySummaryObject obj = new ActivitySummaryObject();
								obj.setName(record.value1());
								obj.setFirstSurname(record.value2());
								obj.setSecondSurname(record.value3());
								obj.setStartDate(record.value4() != null ? new Date(
										record.value4().getTime()) : null);
								obj.setEndDate(record.value5() != null ? new Date(
										record.value5().getTime()) : null);
								obj.setId(record.value6());
								map.put(record.value6(), obj);
							});
			return map;

		} finally {
			if (ctx != null)
				ctx.close();
		}

	}

	private static Map<Integer, ActivitySummaryObject> getSummaryEnterprise(
			Integer[] childDomainIds, Integer domainId, String domainName,
			Date startDate, Date endDate, Boolean starts, Boolean ends) {

		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId,
					AonServletUtils.getLoggedUser());
			Result<Record4<Integer, String, BigDecimal, BigDecimal>> result = ctx
					.getDslContext()
					.select(DOMAIN.ID,
							DOMAIN.DESCRIPTION,
							DSL.sum(DSL
									.field(CONTRACT.START_DATE.between(
											new java.sql.Date(startDate
													.getTime()),
											new java.sql.Date(endDate.getTime())))
									.coerce(Integer.class)),
							DSL.sum(DSL
									.field(CONTRACT.END_DATE.between(
											new java.sql.Date(startDate
													.getTime()),
											new java.sql.Date(endDate.getTime())))
									.coerce(Integer.class)))
					.from(DOMAIN.leftOuterJoin(CONTRACT).on(
							CONTRACT.DOMAIN.eq(DOMAIN.ID)))
					.where(DOMAIN.ID.in(childDomainIds))
					.and(DOMAIN.ACTIVE.eq((byte) 1))
					.and(getStartCondition(startDate, endDate, starts))
					.and(getEndCondition(startDate, endDate, ends))
					.groupBy(DOMAIN.ID).orderBy(DOMAIN.DESCRIPTION.asc())
					.fetch();

			Map<Integer, ActivitySummaryObject> map = new HashMap<>();
			result.stream()
					.forEach(
							record -> {
								ActivitySummaryObject obj = new ActivitySummaryObject();
								obj.setId(record.value1());
								obj.setName(record.value2());
								obj.setStartCount(record.value3() != null ? record
										.value3().intValue() : 0);
								obj.setEndCount(record.value4() != null ? record
										.value4().intValue() : 0);
								map.put(record.value1(), obj);
							});
			return map;

		} finally {
			if (ctx != null)
				ctx.close();
		}

	}

	private static Map<Integer, ActivitySummaryObject> getSummaryEmployeeSalary(
			Integer domainId, String domainName, Date startDate, Date endDate,
			boolean salary, boolean salaryExtra, boolean salarySettle,
			boolean salaryOther) {

		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId,
					AonServletUtils.getLoggedUser());
			Result<Record5<Integer, BigDecimal, BigDecimal, BigDecimal, BigDecimal>> result = ctx
					.getDslContext()
					.select(CONTRACT.ID,
							DSL.sum(DSL.field(
									SALARY.TYPE.eq((byte) SalaryType.SALARY
											.ordinal())).coerce(Integer.class)),
							DSL.sum(DSL.field(
									SALARY.TYPE.eq((byte) SalaryType.EXTRA
											.ordinal())).coerce(Integer.class)),
							DSL.sum(DSL.field(
									SALARY.TYPE.eq((byte) SalaryType.SETTLE
											.ordinal())).coerce(Integer.class)),
							DSL.sum(DSL.field(
									SALARY.TYPE.gt((byte) SalaryType.SETTLE
											.ordinal())).coerce(Integer.class)))
					.from(CONTRACT.leftOuterJoin(SALARY).on(
							CONTRACT.ID.eq(SALARY.CONTRACT)))
					.where(CONTRACT.START_DATE.lt(
							new java.sql.Date(endDate.getTime())).or(
							CONTRACT.START_DATE.gt(new java.sql.Date(endDate
									.getTime()))))
					.and(SALARY.ISSUE_DATE.between(
							new java.sql.Date(startDate.getTime()),
							new java.sql.Date(endDate.getTime())))
					.and(CONTRACT.DOMAIN.eq(domainId))
					.and(getSalaryCondition(salary, salaryExtra, salarySettle,
							salaryOther)).groupBy(CONTRACT.ID)
					.orderBy(CONTRACT.ID.desc(), CONTRACT.START_DATE.desc())
					.fetch();

			Map<Integer, ActivitySummaryObject> map = new HashMap<>();
			result.stream()
					.forEach(
							record -> {
								ActivitySummaryObject obj = new ActivitySummaryObject();
								obj.setSalaryCount(record.value2() != null ? record
										.value2().intValue() : 0);
								obj.setSalaryExtraCount(record.value3() != null ? record
										.value3().intValue() : 0);
								obj.setSalarySettleCount(record.value4() != null ? record
										.value4().intValue() : 0);
								obj.setSalaryOtherCount(record.value5() != null ? record
										.value5().intValue() : 0);
								map.put(record.value1(), obj);
							});
			return map;

		} finally {
			if (ctx != null)
				ctx.close();
		}

	}

	private static Map<Integer, ActivitySummaryObject> getSummaryEnterpriseSalary(
			Integer[] childDomainIds, Integer domainId, String domainName,
			Date startDate, Date endDate, boolean salary, boolean salaryExtra,
			boolean salarySettle, boolean salaryOther) {

		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId,
					AonServletUtils.getLoggedUser());
			Result<Record5<Integer, BigDecimal, BigDecimal, BigDecimal, BigDecimal>> result = ctx
					.getDslContext()
					.select(DOMAIN.ID,
							DSL.sum(DSL.field(
									SALARY.TYPE.eq((byte) SalaryType.SALARY
											.ordinal())).coerce(Integer.class)),
							DSL.sum(DSL.field(
									SALARY.TYPE.eq((byte) SalaryType.EXTRA
											.ordinal())).coerce(Integer.class)),
							DSL.sum(DSL.field(
									SALARY.TYPE.eq((byte) SalaryType.SETTLE
											.ordinal())).coerce(Integer.class)),
							DSL.sum(DSL.field(
									SALARY.TYPE.gt((byte) SalaryType.SETTLE
											.ordinal())).coerce(Integer.class)))
					.from(DOMAIN.leftOuterJoin(SALARY).on(
							SALARY.DOMAIN.eq(DOMAIN.ID)))
					.where(SALARY.ISSUE_DATE.between(new java.sql.Date(
							startDate.getTime()),
							new java.sql.Date(endDate.getTime())))
					.and(DOMAIN.ID.in(childDomainIds))
					.and(DOMAIN.ACTIVE.eq((byte) 1))
					.and(getSalaryCondition(salary, salaryExtra, salarySettle,
							salaryOther)).groupBy(DOMAIN.ID)
					.orderBy(DOMAIN.ID.asc()).fetch();

			Map<Integer, ActivitySummaryObject> map = new HashMap<>();
			result.stream()
					.forEach(
							record -> {
								ActivitySummaryObject obj = new ActivitySummaryObject();
								obj.setSalaryCount(record.value2() != null ? record
										.value2().intValue() : 0);
								obj.setSalaryExtraCount(record.value3() != null ? record
										.value3().intValue() : 0);
								obj.setSalarySettleCount(record.value4() != null ? record
										.value4().intValue() : 0);
								obj.setSalaryOtherCount(record.value5() != null ? record
										.value5().intValue() : 0);
								map.put(record.value1(), obj);
							});
			return map;

		} finally {
			if (ctx != null)
				ctx.close();
		}

	}

	private static Map<Integer, ActivitySummaryObject> getSummaryEmployeeIT(
			Integer domainId, String domainName, Date startDate, Date endDate,
			boolean itCommonDisease, boolean itOccupationalDisease,
			boolean itMaternity, boolean itOther) {

		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId,
					AonServletUtils.getLoggedUser());
			Result<Record5<Integer, BigDecimal, BigDecimal, BigDecimal, BigDecimal>> result = ctx
					.getDslContext()
					.select(CONTRACT.ID,
							DSL.sum(DSL.field(
									CONTRACT_LEAVE.TYPE
											.in((byte) LeaveType.COMMON_DISEASE
													.ordinal(),
													(byte) LeaveType.COMMON_DISEASE_AT_LACK
													.ordinal())).coerce(
									Integer.class)),
							DSL.sum(DSL
									.field(CONTRACT_LEAVE.TYPE
											.eq((byte) LeaveType.OCCUPATIONAL_DISEASE
													.ordinal())).coerce(
											Integer.class)),
							DSL.sum(DSL.field(
									CONTRACT_LEAVE.TYPE.in(
											(byte) LeaveType.MATERNITY
													.ordinal(),
											(byte) LeaveType.PATERNITY
													.ordinal())).coerce(
									Integer.class)),
							DSL.sum(DSL
									.field(CONTRACT_LEAVE.TYPE.notIn(
											(byte) LeaveType.COMMON_DISEASE
													.ordinal(),
											(byte) LeaveType.COMMON_DISEASE_AT_LACK
												.ordinal(),
											(byte) LeaveType.OCCUPATIONAL_DISEASE
													.ordinal(),
											(byte) LeaveType.MATERNITY
													.ordinal(),
											(byte) LeaveType.PATERNITY
													.ordinal())).coerce(
											Integer.class)))
					.from(CONTRACT.leftOuterJoin(CONTRACT_LEAVE).on(
							CONTRACT_LEAVE.CONTRACT.eq(CONTRACT.ID)))
					.where(CONTRACT_LEAVE.START_DATE.le(new java.sql.Date(
							endDate.getTime())))
					.and((CONTRACT_LEAVE.END_DATE.isNull()
							.or(CONTRACT_LEAVE.END_DATE.ge(new java.sql.Date(
									startDate.getTime())))))
					.and(CONTRACT.DOMAIN.eq(domainId))
					.and(getItCondition(itCommonDisease, itOccupationalDisease,
							itMaternity, itOther)).groupBy(CONTRACT.ID)
					.orderBy(CONTRACT.ID.desc(), CONTRACT.START_DATE.desc())
					.fetch();

			Map<Integer, ActivitySummaryObject> map = new HashMap<>();
			result.stream()
					.forEach(
							record -> {
								ActivitySummaryObject obj = new ActivitySummaryObject();
								obj.setItCommonDiseaseCount(record.value2() != null ? record
										.value2().intValue() : 0);
								obj.setItOccupationalDiseaseCount(record
										.value3() != null ? record.value3()
										.intValue() : 0);
								obj.setItMaternityCount(record.value4() != null ? record
										.value4().intValue() : 0);
								obj.setItOtherCount(record.value5() != null ? record
										.value5().intValue() : 0);
								map.put(record.value1(), obj);
							});
			return map;

		} finally {
			if (ctx != null)
				ctx.close();
		}

	}

	private static Map<Integer, ActivitySummaryObject> getSummaryEnterpriseIT(
			Integer[] childDomainIds, Integer domainId, String domainName,
			Date startDate, Date endDate, boolean itCommonDisease,
			boolean itOccupationalDisease, boolean itMaternity, boolean itOther) {

		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId,
					AonServletUtils.getLoggedUser());
			Result<Record5<Integer, BigDecimal, BigDecimal, BigDecimal, BigDecimal>> result = ctx
					.getDslContext()
					.select(DOMAIN.ID,
							DSL.sum(DSL.field(
									CONTRACT_LEAVE.TYPE
											.in((byte) LeaveType.COMMON_DISEASE
													.ordinal(),
													(byte) LeaveType.COMMON_DISEASE_AT_LACK
													.ordinal())).coerce(
									Integer.class)),
							DSL.sum(DSL
									.field(CONTRACT_LEAVE.TYPE
											.eq((byte) LeaveType.OCCUPATIONAL_DISEASE
													.ordinal())).coerce(
											Integer.class)),
							DSL.sum(DSL.field(
									CONTRACT_LEAVE.TYPE.in(
											(byte) LeaveType.MATERNITY
													.ordinal(),
											(byte) LeaveType.PATERNITY
													.ordinal())).coerce(
									Integer.class)),
							DSL.sum(DSL
									.field(CONTRACT_LEAVE.TYPE.notIn(
											(byte) LeaveType.COMMON_DISEASE
													.ordinal(),
											(byte) LeaveType.COMMON_DISEASE_AT_LACK
													.ordinal(),
											(byte) LeaveType.OCCUPATIONAL_DISEASE
													.ordinal(),
											(byte) LeaveType.MATERNITY
													.ordinal(),
											(byte) LeaveType.PATERNITY
													.ordinal())).coerce(
											Integer.class)))
					.from(DOMAIN.leftOuterJoin(CONTRACT_LEAVE).on(
							CONTRACT_LEAVE.DOMAIN.eq(DOMAIN.ID)))
					.where(CONTRACT_LEAVE.START_DATE.le(new java.sql.Date(
							endDate.getTime())))
					.and((CONTRACT_LEAVE.END_DATE.isNull()
							.or(CONTRACT_LEAVE.END_DATE.ge(new java.sql.Date(
									startDate.getTime())))))
					.and(DOMAIN.ID.in(childDomainIds))
					.and(DOMAIN.ACTIVE.eq((byte) 1))
					.and(getItCondition(itCommonDisease, itOccupationalDisease,
							itMaternity, itOther)).groupBy(DOMAIN.ID)
					.orderBy(DOMAIN.ID.asc()).fetch();

			Map<Integer, ActivitySummaryObject> map = new HashMap<>();
			result.stream()
					.forEach(
							record -> {
								ActivitySummaryObject obj = new ActivitySummaryObject();
								obj.setItCommonDiseaseCount(record.value2() != null ? record
										.value2().intValue() : 0);
								obj.setItOccupationalDiseaseCount(record
										.value3() != null ? record.value3()
										.intValue() : 0);
								obj.setItMaternityCount(record.value4() != null ? record
										.value4().intValue() : 0);
								obj.setItOtherCount(record.value5() != null ? record
										.value5().intValue() : 0);
								map.put(record.value1(), obj);
							});
			return map;

		} finally {
			if (ctx != null)
				ctx.close();
		}

	}

	/*
	 * CONTRACT CONDITIONS
	 */
	private static Condition getStartCondition(Date startDate, Date endDate,
			boolean starts) {
		return starts ? CONTRACT.START_DATE.ge(
				new java.sql.Date(startDate.getTime())).and(
				CONTRACT.START_DATE.le(new java.sql.Date(endDate.getTime())))
				: CONTRACT.START_DATE.le(new java.sql.Date(endDate.getTime()));
	}

	private static Condition getEndCondition(Date startDate, Date endDate,
			boolean ends) {
		return ends ? CONTRACT.END_DATE.isNotNull().and(
				CONTRACT.END_DATE.ge(new java.sql.Date(startDate.getTime()))
						.and(CONTRACT.END_DATE.le(new java.sql.Date(endDate
								.getTime())))) : CONTRACT.END_DATE.ge(
				new java.sql.Date(startDate.getTime())).or(
				CONTRACT.END_DATE.isNull());
	}

	/*
	 * SALARY CONDITIONS
	 */
	private static Condition getSalaryCondition(boolean salary, boolean extra,
			boolean settle, boolean other) {
		byte salaryType = (byte) SalaryType.SALARY.ordinal();
		byte extraType = (byte) SalaryType.EXTRA.ordinal();
		byte settleType = (byte) SalaryType.SETTLE.ordinal();

		Condition cond = null;
		cond = salary ? SALARY.TYPE.eq(salaryType) : SALARY.TYPE.ne(salaryType);
		cond = extra ? cond.or(SALARY.TYPE.eq(extraType)) : cond
				.and(SALARY.TYPE.ne(extraType));
		cond = settle ? cond.or(SALARY.TYPE.eq(settleType)) : cond
				.and(SALARY.TYPE.ne(settleType));
		cond = other ? cond.or(SALARY.TYPE.gt(settleType)) : cond
				.and(SALARY.TYPE.le(settleType));
		return cond;
	}

	/*
	 * IT CONDITIONS
	 */
	private static Condition getItCondition(boolean itCommonDisease,
			boolean itOccupationalDisease, boolean itMaternity, boolean itOther) {
		byte itCommonDiseaseType = (byte) LeaveType.COMMON_DISEASE.ordinal();
		byte itCommonDiseaseAtLackType = (byte) LeaveType.COMMON_DISEASE_AT_LACK.ordinal();
		byte itOccupationalDiseaseType = (byte) LeaveType.OCCUPATIONAL_DISEASE
				.ordinal();
		byte itMaternityType = (byte) LeaveType.MATERNITY.ordinal();
		byte itPaternityType = (byte) LeaveType.PATERNITY.ordinal();

		Condition cond = null;
		cond = itCommonDisease ? CONTRACT_LEAVE.TYPE.in(itCommonDiseaseType, itCommonDiseaseAtLackType)
				: CONTRACT_LEAVE.TYPE.notIn(itCommonDiseaseType, itCommonDiseaseAtLackType);
		cond = itOccupationalDisease ? cond.or(CONTRACT_LEAVE.TYPE
				.eq(itOccupationalDiseaseType)) : cond.and(CONTRACT_LEAVE.TYPE
				.ne(itOccupationalDiseaseType));
		cond = itMaternity ? cond.or(CONTRACT_LEAVE.TYPE.eq(itMaternityType))
				.or(CONTRACT_LEAVE.TYPE.eq(itPaternityType)) : cond.and(
				CONTRACT_LEAVE.TYPE.ne(itMaternityType)).and(
				CONTRACT_LEAVE.TYPE.ne(itPaternityType));
		cond = itOther ? cond.or(CONTRACT_LEAVE.TYPE.gt(itPaternityType))
				: cond.and(CONTRACT_LEAVE.TYPE.le(itPaternityType));
		return cond;
	}

}
